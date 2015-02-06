import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.List;

/* Some test cases for the Dictionary class.
 * Note that these extensively use the "exists" method, this is in order
 * to ensure that the add method works as expected (and that the line of
 * thinking when it comes to the general recursion isn't entirely crazy)
 */
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
  public void testFindAnagrams() {
    Dictionary d    = new Dictionary();

    // Note the ' at the end of the alphabet string.
    // (Special characters should be no problem)
    String alphabet = "abcdefghijklmnopqrstuvwxyz'";
    String[] words  =
    {  "what"  ,
       "is"    ,
       "love"  ,
       "baby"  ,
       "don't" ,
       "hurt"  ,
       "me"    ,
       "no"    ,
       "mooore"
     };

    for (String word : words) {
      d.add(word);
    }

    // Find all words, 20 characters or less, that exist in the
    // dictionary and can be formed with the letters of the alphabet
    // (using each character at most once).
    List<String> result = d.findAnagrams(alphabet, 20, 0);

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

  // If maxLength < minLength, we should always just get an empty set
  // (and this should be done rather quickly)
  @Test
  public void testFindAnagrams_invalidInput() {
    Dictionary d = new Dictionary();
    String word = "cheese";
    int max = 5;
    int min = 10;
    d.add("cheese");
    List<String> result = d.findAnagrams(word, max, min);
    assertTrue("max < min not handled correctly", result.isEmpty());
  }

  // removeCharAt is supposed to be a complement to charAt, removing the
  // char that would've been given by charAt from the string.
  @Test
  public void testRemoveCharAt() {
    String str = "abcdefghijklmnopqrstuvwxyz";
    for (int i = 0; i < str.length(); i++) {
      char   c        = str.charAt(i);
      String cRemoved = Dictionary.removeCharAt(str, i);
      assertFalse(cRemoved + " contains the char " + c + " even though it shouldn't", cRemoved.contains("" + c));
    }
  }
}