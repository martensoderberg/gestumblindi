import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class RadixTreeTest {
  @Test
  public void testAdd() {
    String word = "abc";
    RadixTree rt = new RadixTree();
    rt.add(word);
    assertEquals(rt.lookup("abc"), word);
  }
}