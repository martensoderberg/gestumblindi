import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.Set;

public class DictionaryTest {
  @Test
  public void testAdd() {
    Dictionary d    = new Dictionary();
    String     word = "abc";
    d.add(word);
    assertTrue("Words are not correctly added!", d.exists("abc"));
  }

  @Test
  public void testExists_FullWord() {
    Dictionary d    = new Dictionary();
    String     word = "banana";
    d.add(word);
    assertTrue("Word falsely said to not exist", d.exists("banana"));
  }

  @Test
  public void testExists_Prefix() {
    Dictionary d    = new Dictionary();
    String     word = "potato";
    d.add(word);
    assertFalse("Prefix falsely identified as word", d.exists("pot"));
  }

  @Test
  public void testExists_PrefixAfterAddingWord() {
    Dictionary d     = new Dictionary();
    String     word1 = "potato";
    String     word2 = "pot";
    d.add(word1);
    assertFalse("Prefix falsely identified as word", d.exists("pot"));
    d.add(word2);
    assertTrue("Word falsely said to not exist", d.exists("pot"));
  }

  @Test
  public void testSearch() {
    Dictionary d    = new Dictionary();

    // Note the ' at the end of the alphabet string:
    String alphabet = "abcdefghijklmnopqrstuvwxyz'";
    String[] words  = {"what", "is", "love", "baby", "don't", "hurt", "me", "no", "mooore"};
    for (String word : words) {
      d.add(word);
    }
    Set result = d.search(alphabet, 20, 0);
    assertTrue  (result.contains("what"));
    assertTrue  (result.contains("is"  ));
    assertTrue  (result.contains("love"));
    assertFalse (result.contains("baby")); // Input has only one 'b'
    assertTrue  (result.contains("don't")); // Alphabet does contain one '
    assertTrue  (result.contains("hurt"));
    assertTrue  (result.contains("me"  ));
    assertTrue  (result.contains("no"  ));
    assertFalse (result.contains("mooore")); // Input has only one 'o'

    result.remove("what");
    result.remove("is");
    result.remove("love");
    result.remove("don't");
    result.remove("hurt");
    result.remove("me");
    result.remove("no");
    assertTrue("The result set should be empty, all okay results should have been removed", result.isEmpty());
  }
}