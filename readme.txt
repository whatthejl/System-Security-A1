Name         : Yong Jia Liang
UOWID        : 7083609
Assignment 1 : Rainbow Table

Completed on Notepad++, compiled in terminal.
Instructions : 
	1. From terminal, javac 7083609_YongJiaLiang_A1.java
	2. Running the program, java Rainbow Wordlist.txt

Reduction Method :
	1. Convert hash to BigInteger
	2. Convert length of Wordlist to BigInteger
	3. hash.mod(length)
	4. return mod value
This reduction method does not deal with collisions.