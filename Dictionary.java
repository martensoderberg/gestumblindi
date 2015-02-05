import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/* This implementation stores a dictionary in a radix tree to quickly
 * find anagrams of given lengths.
 */
public class Dictionary {
  private Node root;
  private Set<String> searchResults;

  public Dictionary() {
    root = new Node();
  }

  public Set<String> findAnagrams(String word, int maxLength, int minLength) {
    searchResults = new HashSet<String>();
    if (maxLength >= minLength) {
      // Only do something if max >= min. Otherwise, just return empty.
      // (it is pointless to do anything if max < min)
      word = word.toLowerCase();
      findAnagramsHelper(word, 0, maxLength, minLength, root);
    }
    return searchResults;
  }

  private void findAnagramsHelper(String word, int stepsTaken, int maxSteps, int minSteps, Node n) {
    if (stepsTaken >= minSteps && n.word != null) {
      // If we have taken enough steps, and this is a real word,
      // add it to the results!
      searchResults.add(n.word);
    }

    if (stepsTaken >= maxSteps) {
      // If we shouldn't be stepping any longer, stop here.
      return;
    }

    for (int i = 0; i < word.length(); i++) {
      char   nextStep = word.charAt(i);
      String theRest  = removeCharAt(word, i);
      Node   next     = n.edges.get(new Character(nextStep));
      if (next != null) {
        // For all possible edges, keep stepping!
        findAnagramsHelper(theRest, stepsTaken+1, maxSteps, minSteps, next);
      }
    }
  }

  public static String removeCharAt(String s, int i) {
    StringBuffer buf = new StringBuffer(s.length() - 1);
    buf.append(s.substring(0, i)).append(s.substring(i+1));
    return buf.toString();
  }

  // Given a certain input, does it exist in our data structure?
  public boolean exists(String word) {
    return existsHelper(word, root);
  }

  // Recursive helper function for lookup.
  private boolean existsHelper(String word, Node n) {
    if (word.length() == 0) {
      return (n.word != null);
    }

    char   head = word.charAt(0);
    // (try to) retrieve the next edge
    Node   next = n.edges.get(new Character(head));
    if (next != null) {
      // We found such an edge, let's continue
      String tail = word.substring(1);
      return existsHelper(tail, next);
    } else {
      // No such edge, the word does not exist, we cannot go further
      return false;
    }
  }

  public void add(String word) {
    word = word.toLowerCase();
    addHelper(word, word, root);
  }

  private void addHelper(String word, String wholeWord, Node n) {
    if (word.length() == 0) {
      // We have entered the whole word into the tree.
      // This last word should therefore store the full word.
      // This way, we know that this node represents a full word.
      n.word  = wholeWord;
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
    addHelper(tail, wholeWord, next);
  }

  private class Node {
    private Map<Character, Node>  edges;
    private String                word;
    
    private Node () {
      // Note: Character.hashCode() == Character.hashValue()
      this.edges = new HashMap<Character, Node>();

      // By default, a node represents no word.
      // While "banana" is a word, "bana" is not a word, and thus the
      // node given by following "b" -> "a" -> "n" -> "a" should not give
      // a word result.
      this.word  = null;
    }
  }
}