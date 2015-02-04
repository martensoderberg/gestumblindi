import java.util.List;
import java.util.ArrayList;

/*
 * A Radix tree implementation, with every edge representing just
 * a single character.
 */
public class Dictionary {
  private Node root;

  public Dictionary() {
    root = new Node(null);
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