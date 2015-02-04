import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.SortedMap;

import java.io.IOException;

public class DictionaryTest {
  @Test
  public void testAdd() {
    String word = "abc";
    Dictionary d = new Dictionary();
    d.add(word, 10);
    assertEquals(word, d.lookup("abc"));
  }

  @Test
  public void testLoad() {
    Dictionary d = new Dictionary();
    SortedMap<String, Charset> ac = Charset.availableCharsets();
    Charset encoding = ac.get("UTF-8");
    Path path = Paths.get("res/en.txt");
    try {
      d.loadFile(path, encoding);
    } catch (IOException e) {

    }
    assertEquals("cat", d.lookup("cat"));
  }
}