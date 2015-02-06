import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.PriorityQueue;

/* This implementation stores a dictionary in a radix tree to quickly
 * find anagrams of given lengths.
 */
public class Dictionary {
  private Node root;
  private List<String> searchResults;

  public Dictionary() {
    root = new Node();
  }

  public List<String> findAnagrams(String word, int maxLength, int minLength) {
    searchResults = new ArrayList<String>();

    if (maxLength < minLength) {
      // Only do something if max >= min. Otherwise, just return empty.
      return searchResults;
    }

    int originalSize = word.length();

    char[] ca = word.toCharArray();
    Arrays.sort(ca);
    List<Character> cl = new ArrayList<Character>();
    for (char c : ca) {
      cl.add(new Character(c));
    }

    Queue<PartialAnagram> pq = new PriorityQueue<PartialAnagram>();
    PartialAnagram first = new PartialAnagram(cl, root);
    pq.add(first);

    while (!pq.isEmpty()) {
      PartialAnagram pa = pq.poll();
      Node n = pa.next;
      List<Character> remaining = pa.remaining;

      int stepsTaken = originalSize - remaining.size();
      if (stepsTaken >= minLength && n.word != null) {
        // 1) Have we reached a node with a result?
        searchResults.add(n.word);
      }

      if (stepsTaken >= maxLength) {
        // 2) If we shouldn't be stepping any longer, stop here.
        continue;
      }

      // 3:
      for (Character c : remaining) {
        Node next = n.edges.get(c);
        if (next != null) {
          // TODO: write this comment
          // Trim branches by only stepping into certain leaves
          if (next.furthestLeaf < (minLength - stepsTaken)) {
            // Skip this node
            continue;
          }

          if (next.closestLeaf > (maxLength - stepsTaken)) {
            // dito
            continue;
          }

          // Edge exists, step right this way!
          // (but first, work out what part of the word to pass on)
          List<Character> theRest = new ArrayList<Character>(remaining);
          // Remove the first occurence of c in theRest
          theRest.remove(c);
          PartialAnagram newPA = new PartialAnagram(theRest, next);
          pq.add(newPA);
        }
      }
    }
    
    return searchResults;
  }

  private class PartialAnagram implements Comparable<PartialAnagram> {
    private List<Character> remaining;
    private Node            next;
    
    private PartialAnagram(List<Character> remaining, Node next) {
      this.remaining = remaining;
      this.next      = next;
    }

    public int compareTo(PartialAnagram other) {
      if (other == null) {
        return 1;
      }

      return (other.remaining.size() - this.remaining.size());
    }
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

  /* Recursive add function.
   * Performs some nifty branch depth calculations in order to note the
   * depth of every branch in existence.
   *
   * 1) Step through the tree, creating nodes as necessary
   * 2) Once we've run out of characters in our word, we must be done
   * 3) Store the whole word in the last node and return 0
   * 4) Note the depth in every node we pass
   */
  private int addHelper(String word, String wholeWord, Node n) {
    if (word.length() == 0) {
      // 2 & 3:
      // We have entered the whole word into the tree.
      // This last word should therefore store the full word.
      // This way, we know that this node represents a full word.
      n.word  = wholeWord;
      return 0;
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
    int depth = 1 + addHelper(tail, wholeWord, next);

    // 4:
    if (next.closestLeaf > depth) {
      next.closestLeaf = depth;
    }

    if (next.furthestLeaf < depth) {
      next.furthestLeaf = depth;
    }

    return depth;
  }

  // Internal representation of a Node.
  // We represent edges as a map of Characters -> Nodes
  // This means that the complexity of our data structure is effectively
  // bounded by the size of the alphabet we use to write our words!
  private class Node {
    private Map<Character, Node>  edges;
    private String                word;
    private int                   closestLeaf;
    private int                   furthestLeaf;
    
    private Node () {
      // Note: Character.hashCode() == Character.hashValue()
      this.edges = new HashMap<Character, Node>();

      // By default, a node represents no word.
      // While "banana" is a word, "bana" is not a word, and thus the
      // node given by following "b" -> "a" -> "n" -> "a" should not give
      // a word result.
      this.word  = null;

      // In order to save us some execution cycles, we can keep track
      // of how deep the branch that starts at this node is!
      // For example, if we only want to find anagrams of length > 10,
      // it makes no sense to go down a branch with depth < 10.
      this.closestLeaf  = Integer.MAX_VALUE;
      this.furthestLeaf = 0;
    }
  }
}