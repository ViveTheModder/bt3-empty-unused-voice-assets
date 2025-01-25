package cmd;
//BT3 Empty Unused GSC Subtitles & LPS Files by ViveTheModder
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main 
{
	static final File IN = new File("./in/");
	static final int MAX_VOICES = 300;
	static File currCsv, currPak;
	static int[] voiceIndices = new int[MAX_VOICES];
	static int totalVoices;
	
	private static File getFolderFromFileChooser()
	{
		File folder = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select folder with CSV files...");
		while (true)
		{
			int result = chooser.showOpenDialog(chooser);
			if (result==0)
			{
				File tempFolderRef = chooser.getSelectedFile(); //actually gets the selected folder
				File[] tempFolderCSVs = tempFolderRef.listFiles((dir, name) -> 
				{
					return name.toLowerCase().endsWith(".csv");
				});
				if (!(tempFolderCSVs==null || tempFolderCSVs.length==0)) 
				{
					folder = tempFolderRef; break;
				}
				else JOptionPane.showMessageDialog(chooser, "This folder does NOT have CSV files! Try again!", "Invalid Folder", JOptionPane.ERROR_MESSAGE);
			}
			else System.exit(2);
		}
		return folder;
	}
	private static void overwrite(int vlid, String pakType) throws IOException
	{
		RandomAccessFile pak = new RandomAccessFile(currPak,"rw");
		pak.seek(4);
		int[] positions = new int[MAX_VOICES+1];
		int[] sizes = new int[MAX_VOICES];
		int tempPos=0;
		
		for (int i=1; i<=MAX_VOICES; i++) //initialize positions and sizes
		{
			if (tempPos==0) positions[i-1] = LittleEndian.getInt(pak.readInt()); //current position
			else positions[i-1] = tempPos;
			positions[i] = LittleEndian.getInt(pak.readInt()); //next position
			tempPos = positions[i]; //temporary position which exists to prevent reading the same positions twice
			sizes[i-1] = positions[i]-positions[i-1];
		}

		int difference = sizes[vlid]-64;
		//fix index
		pak.seek((vlid+2)*4);
		for (int i=vlid+1; i<=MAX_VOICES; i++) pak.writeInt(LittleEndian.getInt(positions[i]-difference));

		pak.seek(positions[vlid+1]);
		int fullFileSize = (int)pak.length();
		int restOfFileSize = fullFileSize - positions[vlid+1];
		byte[] restOfFile = new byte[restOfFileSize];
		pak.read(restOfFile); //copy the rest of the file contents before overwriting
		pak.seek(positions[vlid]);
		byte[] empty = new byte[64];
		if (pakType.startsWith("TXT")) //change header to 0xFFFE
		{
			empty[0]=(byte)255; empty[1]=(byte)254;
		}
		else if (pakType.startsWith("LPS")) //change header to 0x4C495053
		{
			byte[] header = {(byte)76,(byte)73,(byte)80,(byte)83};
			System.arraycopy(header, 0, empty, 0, 4);
		}
		//actual overwriting process
		pak.write(empty);
		pak.write(restOfFile);
		pak.setLength(fullFileSize-difference);
		pak.close();
	}
	private static void setUnusedVoiceLinesFromCsv() throws IOException
	{
		voiceIndices = new int[MAX_VOICES];
		totalVoices=0;
		Scanner sc = new Scanner(currCsv);
		if (sc.hasNextLine()) sc.nextLine(); //skip header
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			if (inputArr.length==2)
			{
				voiceIndices[totalVoices]=Integer.parseInt(inputArr[1]);
				totalVoices++;
			}
		}
		sc.close();
	}
	public static void replaceUnusedAssets() throws IOException
	{
		File csvFolder = getFolderFromFileChooser();
		File[] csvArray = csvFolder.listFiles();
		File[] pakArray = IN.listFiles((dir, name) -> 
		{
			return name.toLowerCase().endsWith(".pak");
		});
		
		double start = System.currentTimeMillis();
		for (int i=0; i<csvArray.length; i++)
		{
			currCsv = csvArray[i];
			int gscIndexFromCSV = Integer.parseInt(currCsv.getName().split("-")[2]);
			setUnusedVoiceLinesFromCsv();
			if (pakArray.length==0) continue;
			for (int j=0; j<pakArray.length; j++)
			{
				currPak = pakArray[j];
				String[] pakFileNameArray = currPak.getName().split("-");
				String pakType = pakFileNameArray[0]+"-"+pakFileNameArray[1];
				int gscIndexFromPak = Integer.parseInt(pakFileNameArray[3].replace(".pak", ""));
				if (gscIndexFromPak==gscIndexFromCSV)
				{
					for (int k=0; k<totalVoices; k++) 
					{
						System.out.println("Overwriting scenario "+gscIndexFromCSV+"'s voice line "+voiceIndices[k]+"'s "+pakType+" assets...");
						overwrite(voiceIndices[k],pakType);
					}
				}
			}
		}
		double end = System.currentTimeMillis();
		System.out.println("\nTime elapsed: "+(end-start)/1000+" s");
	}
	public static void main(String[] args)
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			replaceUnusedAssets();
		} 
		catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
	}
}