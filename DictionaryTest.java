import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class DictionaryTest {
  @Test
  public void testAdd() {
    String word = "abc";
    Dictionary rt = new Dictionary();
    rt.add(word, 10);
    assertEquals(rt.lookup("abc"), word);
  }
}