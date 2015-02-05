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

Note that the list of words used to build the dictionary (res/en.txt) is not very good. It contains a lot of entries that are not really words in the English language, such as 'p'. So it contains a lot of false words, which is why the output will sometimes seem absurd.
