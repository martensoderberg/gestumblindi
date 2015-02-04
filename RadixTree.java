import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/* A Radix Tree for words or something */

public class RadixTree {
  private Node root;

  public String lookup(String word) {
    return lookupHelper(word, root);
  }

  private String lookupHelper(String word, Node n) {
    if (word.equals("")) {
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

  public void add(String s) {

  }

  private void addHelper(String s, Node n) {

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
    
    public Node (List<Edge> edges) {
      this.edges = edges;
      if (edges == null) {
        edges = new ArrayList<Edge>();
      }
    }

    public List<Edge> getEdges() { return edges; }
  }
}