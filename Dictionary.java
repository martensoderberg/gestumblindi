import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * A Radix tree implementation, with every edge representing just
 * a single character.
 */
public class Dictionary {
  private Node root;
  private Set<String> searchResults;

  public Dictionary() {
    root = new Node();
  }

  public Set<String> search(String word, int maxLength, int minLength) {
    searchResults = new HashSet<String>();
    word = word.toLowerCase();
    searchHelper(word, 0, maxLength, minLength, root);
    return searchResults;
  }

  private void searchHelper(String word, int stepsTaken, int maxSteps, int minSteps, Node n) {
    if (stepsTaken >= maxSteps) {
      return;
    }

    for (int i = 0; i < word.length(); i++) {
      char   nextStep = word.charAt(i);
      String theRest  = removeCharAt(word, i);
      Node   next     = n.edges.get(new Character(nextStep));
      if (next != null) {
        if (stepsTaken >= minSteps && next.value != -1) {
          // If we have taken enough steps, and this is a real word, 
          // add it to the results!
          searchResults.add(next.word);
        }
        searchHelper(theRest, stepsTaken+1, maxSteps, minSteps, next);
      }
    }
  }

  public static String removeCharAt(String s, int i) {
    StringBuffer buf = new StringBuffer(s.length() - 1);
    buf.append(s.substring(0, i)).append(s.substring(i+1));
    return buf.toString();
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
    // (try to) retrieve the next edge
    Node   next = n.edges.get(new Character(head));
    if (next != null) {
      // We found such an edge, let's continue
      String tail = word.substring(1);
      return head + lookupHelper(tail, next);
    } else {
      // No such edge, the word does not exist, we cannot go further
      return "";
    }
  }

  public void add(String word, int value) {
    word = word.toLowerCase();
    addHelper(word, value, word, root);
  }

  private void addHelper(String word, int value, String wholeWord, Node n) {
    if (word.length() == 0) {
      // We have entered the whole word into the tree.
      // This last node should therefore have the value of the word.
      n.word  = wholeWord;
      n.value = value;
      return;
    }

    String tail = word.substring(1);
    char   head = word.charAt(0);
    Node   next = n.edges.get(new Character(head));
    if (next == null) {
      // No match found. Create a new Edge with this char!
      next = new Node();
      Character c  = new Character(head);
      n.edges.put(c, next);
    }
    addHelper(tail, value, wholeWord, next);
  }

  private class Node {
    private Map<Character, Node>  edges;
    private int                   value;
    private String                word;
    
    private Node () {
      // Note: Character.hashCode() == Character.hashValue()
      this.edges = new HashMap<Character, Node>();

      // By default, a node represents no word.
      // While "banana" is a word, "bana" is not a word, and thus the
      // node given by following "b" -> "a" -> "n" -> "a" should not give
      // a word result.
      this.value = -1;
      this.word  = null;
    }
  }
}