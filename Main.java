import java.util.SortedMap;
import java.util.List;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* This program handles all that pesky I/O */
public class Main {
  // Main program!
  // The first thing we do is to determine how long words to look for!
  // This is done by parsing the three program arguments:

  // args[0]:
  // The first argument is always necessary, and is the word we will
  // try to build new words from. This will usually be 
  // "optimizationmatters", but who knows, right?

  // args[1]:
  // The second argument is the maximum length of words we will try to
  // find. For example, if args[1] == 3, we will only look for words of
  // length 3 or less.
  private static int DEFAULT_MAX_LENGTH = 20;

  // args[2]:
  // The third argument is the minimum length of words we will try to
  // find. For example, if args[2] == 3, we will only look for words of
  // length 3 or more.
  private static int DEFAULT_MIN_LENGTH = 1;
  public static void main(String... args) {
    // This is all parsing...
    if (args.length == 0 || args.length > 3) {
      printUsageHint();
      return;
    }

    String word;
    int max;
    int min;

    try {
      word = args[0];
      word = word.toLowerCase();

      if (args.length > 1) {
        max = Integer.parseInt(args[1]);
      } else {
        max = DEFAULT_MAX_LENGTH;
      }

      if (args.length > 2) {
        min = Integer.parseInt(args[2]);
      } else {
        min = DEFAULT_MIN_LENGTH;
      }
    } catch (NumberFormatException nfe) {
      printUsageHint();
      return;
    }

    if (min > max) {
      printUsageHint();
      System.out.println("WARNING: the max value needs to be bigger than the min value!");
      return;
    }

    // Now we load the dictionary from a file and add all of that to our
    // nifty radix tree implementation.
    long fileStartTime = System.nanoTime();
    Dictionary d = new Dictionary();
    try {
      SortedMap<String, Charset> ac = Charset.availableCharsets();
      Charset encoding = ac.get("UTF-8");
      Path path = Paths.get("res/en.txt");
      loadFile(d, path, encoding);
    } catch (IOException e) {
      // TODO improve error message
      System.err.println("ERROR: Could not load dictionary file!");
      return;
    }
    long fileStopTime = System.nanoTime();
    long fileReadDuration = fileStopTime - fileStartTime;


    // I/O stuff is dealt with! Now we just search.
    long queryStartTime = System.nanoTime();
    List<String> results = d.findAnagrams(word, max, min);
    long queryStopTime  = System.nanoTime();
    long queryDuration  = queryStopTime - queryStartTime;
    for (String s : results) {
      System.out.println(s);
    }
    System.out.println("");
    System.out.println("Done!");
    System.out.println("Reading   the file  took " + fileReadDuration + "\tnanoseconds");
    System.out.println("Executing the query took " + queryDuration + "\tnanoseconds");
  }

  public static void printUsageHint() {
    System.out.println("Usage:   java Main <input word> [max length (default: " + DEFAULT_MAX_LENGTH + ")] [min length (default: " + DEFAULT_MIN_LENGTH + ")]");
    System.out.println("Example: java Main \"optimizationmatters\" 3  3 ");
    System.out.println("Example: java Main \"optimizationmatters\" 19 1 ");
    System.out.println("Example: java Main \"optimizationmatters\" 3 ");
  }

  // loadFile takes in a Dictionary, a file and an encoding, reads the
  // file and stores every word in the given dictionary.
  // Assumption: the input file has exactly one word per line
  public static void loadFile(Dictionary dict, Path file, Charset encoding) throws IOException {
    List<String> lines = Files.readAllLines(file, encoding);
    for (String word : lines) {
      dict.add(word);
    }
  }
}