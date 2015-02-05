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

  /* Wrapper method for the real magic. Makes sure that input is
   * okay, prepares the result set and starts off the recursive call
   */
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

  /* This is where the magic happens.
   * For every recursion ('step' here):
   * 1) Have we reached a node holding a real word?
   *  - if so, add it to the results
   *
   * 2) Have we stepped maxSteps times yet?
   *  - if so, stop stepping
   *
   * 3) For every character in our incoming word:
   *  - If we have an edge with this character's name on it:
   *    - step across that edge to a new node, with word = (word - char)
   *
   * 4) No more matching characters? Return.
   */
  private void findAnagramsHelper(String word, int stepsTaken, int maxSteps, int minSteps, Node n) {
    if (stepsTaken >= minSteps && n.word != null) {
      // 1) Have we reached a node with a result?
      searchResults.add(n.word);
    }

    if (stepsTaken >= maxSteps) {
      // 2) If we shouldn't be stepping any longer, stop here.
      return;
    }

    // 3:
    for (int i = 0; i < word.length(); i++) {
      // For every character in our incoming word:
      char   nextStep = word.charAt(i);
      Node   next     = n.edges.get(new Character(nextStep));
      if (next != null) {
        // Edge exists, step right this way!
        // (but first, work out what part of the word to pass on)
        String theRest  = removeCharAt(word, i);
        findAnagramsHelper(theRest, stepsTaken + 1, maxSteps, minSteps, next);
      }
    }

    // 4:
    return;
  }

  // This helper function works as a complement to String.charAt,
  // by removing the char at a certain index.
  public static String removeCharAt(String s, int i) {
    StringBuffer buf = new StringBuffer(s.length() - 1);
    buf.append(s.substring(0, i)).append(s.substring(i+1));
    return buf.toString();
  }

  // Given a certain input, does it exist in our data structure?
  public boolean exists(String word) {
    return existsHelper(word, root);
  }

  // Recursive helper function for exists.
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

  // Wrapper function for the 'real' add implementation.
  public void add(String word) {
    word = word.toLowerCase();
    addHelper(word, word, root);
  }

  /* Add is pretty straightforward:
   * 1) Step through the tree, creating nodes as necessary
   * 2) Once we've run out of characters in our word, we must be done
   * 3) Store the whole word in the last node
   */
  private void addHelper(String word, String wholeWord, Node n) {
    if (word.length() == 0) {
      // 2 & 3:
      // We have entered the whole word into the tree.
      // This last word should therefore store the full word.
      // This way, we know that this node represents a full word.
      n.word  = wholeWord;
      return;
    }

    // 1:
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

  // Internal representation of a Node.
  // We represent edges as a map of Characters -> Nodes
  // This means that the complexity of our data structure is effectively
  // bounded by the size of the alphabet we use to write our words!
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