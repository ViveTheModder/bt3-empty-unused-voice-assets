# bt3-empty-unused-voice-assets
Meant to be used alongside [bt3-gsc-voice-detector](https://github.com/ViveTheModder/bt3-gsc-voice-detector) and then [bt3-empty-voice-duplicator](https://github.com/ViveTheModder/bt3-empty-voice-duplicator). 

It replaces unused subtitles and lip-syncing files with empty ones (64 bytes) to decrease the overall file size of each TXT/LPS PAK file.

# Usage/Preview
![image](https://github.com/user-attachments/assets/72791795-e3c6-4ad8-95fc-9e4bdd81c61a)

![image](https://github.com/user-attachments/assets/57c49f02-822d-48eb-ae74-0e980aa81dbe)

Here, the program has overwritten 144 files (48 scenarios * 3 files per each) in about 2 & a half minutes.

Keep in mind, my computer currently has 4 GB of RAM. Other computers are bound to run it way faster.
# Results
TOP -> Modified, BOTTOM -> Original. Refer to byte 9856 to get a better idea of the changes.

![image](https://github.com/user-attachments/assets/232a2d7d-d313-4920-8905-f96fbe151726)

TOP -> Modified, BOTTOM -> Original. 

The bytes in red are the PAK file's index, essentially a list of offsets for each text file it contains.

![image](https://github.com/user-attachments/assets/4f862d80-fbb7-4645-9132-5b6b4089a038)

When searching for empty LPS files, the amount actually matches with what is specified in the CSV file.

![image](https://github.com/user-attachments/assets/80c620be-89e8-414f-a0e0-18b6b3ecc0f8)

![image](https://github.com/user-attachments/assets/3f193577-5434-47e2-8e6f-aabfc3684ca8)

TOP -> Original, BOTTOM -> Modified. It's not much, but it is honest work. 

Saved bytes nonetheless. Around 98304, which is 96 Kilibytes.

![image](https://github.com/user-attachments/assets/2cc015ef-fde2-4b61-8378-fd30bc2c926e)
