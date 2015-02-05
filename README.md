#### Gestumblindi
Jeppesen optimization challenge, implemented in Java using a 
**radix tree** 
data structure

The Main class will use the Dictionary to:

1. Build a tree from all words in res/en.txt
2. Find all anagrams that can be made from an input word, using the dictionary built in step 1.
  
The program is not case sensitive, and will treat "thisword" and "THISWORD" as the same word.

Compile: `javac Main.java`

Run:     `java  Main "YourWord" [max anagram length] [min anagram length]`
