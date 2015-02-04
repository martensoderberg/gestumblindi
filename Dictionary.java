import java.util.List;
import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.file.Files;

import java.io.IOException;

/*
 * A Radix tree implementation, with every edge representing just
 * a single character.
 */
public class Dictionary {
  private Node root;

  public Dictionary() {
    root = new Node(null);
  }

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
  private static int DEFAULT_MAX_LENGTH = 3;

  // args[2]:
  // The third argument is the minimum length of words we will try to
  // find. For example, if args[2] == 3, we will only look for words of
  // length 3 or more.
  private static int DEFAULT_MIN_LENGTH = 1;
  public static void main(String... args) {
    // This is all parsing...
    if (args.length == 0 || args.length > 3) {
      printUsageHint();
    }

    String word;
    int max;
    int min;

    try {
      word = args[0];

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
    }

    // Parsing over, calculations ensue!
    // This will basically be done by a breadth-first search through the
    // radix tree
  }

  public static void printUsageHint() {
    System.out.println("Usage:   java Dictionary <input word> [max length (default: " + DEFAULT_MAX_LENGTH + ")] [min length (default: " + DEFAULT_MIN_LENGTH + ")]");
    System.out.println("Example: java Dictionary \"optimizationmatters\" 3  3 ");
    System.out.println("Example: java Dictionary \"optimizationmatters\" 19 1 ");
    System.out.println("Example: java Dictionary \"optimizationmatters\" 3 ");
  }

  public void loadFile(Path file, Charset encoding) throws IOException {
    List<String> lines = Files.readAllLines(file, encoding);
    for (String line : lines) {
      String[] parts = line.split(" ");
      String word = parts[0];
      String val  = parts[1];
      int value   = Integer.parseInt(val);
      add(word, value);
    }
  }

  // Given a certain input, find the best matching word.
  public String lookup(String word) {
    return lookupHelper(word, root);
  }

  // Recursive helper function for lookup.
  private String lookupHelper(String word, Node n) {
    if (word.length() == 0) {
      return "";
    }

    char   head = word.charAt(0);
    String tail = word.length() > 0 ? word.substring(1) : "" ;
    for (Edge e : n.edges) {
      if (e.correspondsTo(head)) {
        return head + lookupHelper(tail, e.next);
      }
    }
    return "";
  }

  public void add(String word, int value) {
    addHelper(word, value, root);
  }

  private void addHelper(String word, int value, Node n) {
    if (word.length() == 0) {
      // We have entered the whole word into the tree.
      // This last node should therefore have the value of the word.
      n.value = value;
      return;
    }

    char   head = word.charAt(0);
    String tail = word.length() > 0 ? word.substring(1) : "" ;
    for (Edge e : n.edges) {
      if (e.correspondsTo(head)) {
        // We found a match, add the tail to that match
        addHelper(tail, value, e.next);
        return;
      }
    }
    // No match found. Create a new Edge with this char!
    Node newNext = new Node(null);
    Edge newEdge = new Edge(head, newNext);
    n.edges.add(newEdge);
    addHelper(tail, value, newNext);
  }

  private class Edge {
    private char c;
    private Node next;
    
    private Edge(char c, Node next) {
      this.c     = c;
      this.next  = next;
    }

    private boolean correspondsTo(char c) {
      return (this.c == c);
    }
  }

  private class Node {
    private List<Edge> edges;
    private int        value;
    
    private Node (List<Edge> edges) {
      if (edges == null) {
        this.edges = new ArrayList<Edge>();
      } else {
        this.edges = edges;
      }

      this.value = -1;
    }
  }
}