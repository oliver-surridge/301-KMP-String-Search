# 301-KMP-String-Search

Oliver Surridge
1607940

This project implements the solo solution for the KMP String Search assignment
Usage: java KMPsearch 'target string' 'filename.txt' > 'output file'

If only the target string is provided, the KMP skip array for that target will be created and displayed in the format outlined in the brief.
If the file to search through is also provided, as per the solo requirements, the program searches each line of the file, one line at a time, for the substring target and each line that has at least one occurrence of that substring is printed out just once, preceded by the index into that line indicating where the target first occurs.

AI was used to help me implement the buildLPS method as well as coach me through the processes of the search of each line of the input file. It was also used to create testing code along the way which helped me visualise what was going on, e.g. visualising the longestPS array as well as getChars and charToRow
