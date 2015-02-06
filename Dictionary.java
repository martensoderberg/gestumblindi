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

  /* Where the magic happens.
   * We perform a breadth-first search through the radix tree, at every
   * node taking every step that is possible, while keeping in mind which
   * characters we have 'used up' in getting there. Or rather, we keep in
   * mind which characters we can still use.
   *
   * Uses the internal PartialAnagram class to perform the search.
   */
  public List<String> findAnagrams(String word, int maxLength, int minLength) {
    searchResults = new ArrayList<String>();

    if (maxLength < minLength) {
      // Only do something if max >= min. Otherwise, just return empty.
      return searchResults;
    }

    int originalSize = word.length();

    // Convert the word into a list of characters.
    char[] chararray = word.toCharArray();
    List<Character> charlist = new ArrayList<Character>();
    for (char c : chararray) {
      charlist.add(new Character(c));
    }

    // Set up the queue
    Queue<PartialAnagram> pq = new PriorityQueue<PartialAnagram>();
    PartialAnagram first = new PartialAnagram("", charlist, root);
    pq.add(first);

    // Set up is done, let the searching commence
    while (!pq.isEmpty()) {
      PartialAnagram pa = pq.poll();
      Node n = pa.next;
      List<Character> remaining = pa.remaining;

      int stepsTaken = originalSize - remaining.size();
      if (stepsTaken >= minLength && n.word != null) {
        // Have we reached a node with a result?
        searchResults.add(n.word);
      }

      if (stepsTaken >= maxLength) {
        // If we shouldn't be stepping any longer, stop here.
        continue;
      }

      // This is where we step to new nodes!
      // pick c from _unique_ elements in remaining
      Set<Character> uniqueRemaining = new HashSet<Character>(remaining);
      for (Character c : uniqueRemaining) {
        Node next = n.edges.get(c);
        if (next != null) {
          // We only wish to step into a branch if we have any hope
          // of finding what we seek in there.
          // (For example, this means that if we get a query for all
          // anagrams of length > 50, we won't be looking through any
          // entries as no branch will be that deep)
          if ((next.furthestLeaf < (minLength - stepsTaken)) ||
              (next.closestLeaf  > (maxLength - stepsTaken))) {
            // Skip this node
            continue;
          }

          // Edge exists, step right this way!
          // (but first, work out what part of the word to pass on)
          List<Character> newRemaining = new ArrayList<Character>(remaining);
          // Remove the first occurence of c in theRest
          newRemaining.remove(c);
          String newPartialWord = pa.partialWord + c;
          PartialAnagram newPA =
            new PartialAnagram(newPartialWord, newRemaining, next);
          pq.add(newPA);
        }
      }
    } // end of while loop
    
    // No more entries in our queue - we are done.
    return searchResults;
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

  // Internal representation of a partial anagram - a stepping stone on
  // the way to actually finding anagrams.
  // These are stored in the PriorityQueue used in findAnagrams, and as
  // such, they are compared to each other!
  // Since we would like our output to be sorted, we make that happen
  // here.
  private class PartialAnagram implements Comparable<PartialAnagram> {
    private String          partialWord;
    private List<Character> remaining;
    private Node            next;

    private PartialAnagram(String partialWord,
                           List<Character> remaining,
                           Node next) {
      this.partialWord = partialWord;
      this.remaining   = remaining;
      this.next        = next;
    }

    // Sorting order:
    // Primarily, we wish to evaluate PAs with more remaining nodes
    // Secondarily, we wish to evaluate PAs in alphabetical order
    public int compareTo(PartialAnagram other) {
      if (other == null) {
        return 1;
      }

      if        (other.remaining.size() > this.remaining.size()) {
        return 1;
      } else if (other.remaining.size() < this.remaining.size()) {
        return -1;
      } else { // They are equally long -- compare alphabetically
        return this.partialWord.compareTo(other.partialWord);
      }
    }
  }
}