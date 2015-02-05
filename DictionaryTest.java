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
    d.add(word);
    assertTrue("Words are not correctly added!", 
                d.exists("abc"));
  }

  @Test
  public void testLoad() {
    // TODO: Handle the case of when UTF-8 is not available...
    // ...or how likely is that, really?
    SortedMap<String, Charset> ac = Charset.availableCharsets();
    Charset encoding = ac.get("UTF-8");
    Path path = Paths.get("res/en.txt");
    
    try {
      Dictionary d = new Dictionary();
      d.loadFile(path, encoding);
      assertTrue("the word \"cat\" was not found in the dictionary",
                 d.exists("cat"));
    } catch (IOException e) {
      // TODO: Handle this...
    }
  }
}