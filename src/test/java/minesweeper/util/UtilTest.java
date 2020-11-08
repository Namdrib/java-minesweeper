package minesweeper.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Namdrib
 *
 */
public class UtilTest {

  @Test
  public void testClampInts() {
    assertEquals(0, (int) Util.clamp(-1, 0, 2));
    assertEquals(0, (int) Util.clamp(0, 0, 2));
    assertEquals(1, (int) Util.clamp(1, 0, 2));
    assertEquals(2, (int) Util.clamp(2, 0, 2));
    assertEquals(2, (int) Util.clamp(3, 0, 2));
    assertEquals(2, (int) Util.clamp(999, 0, 2));
  }

  @Test
  public void testClampDoubles() {
    assertEquals(0.0, (double) Util.clamp(-1.0, 0.0, 2.0), 0.0);
    assertEquals(0.0, (double) Util.clamp(0.0, 0.0, 2.0), 0.0);
    assertEquals(1.0, (double) Util.clamp(1.0, 0.0, 2.0), 0.0);
    assertEquals(2.0, (double) Util.clamp(2.0, 0.0, 2.0), 0.0);
    assertEquals(2.0, (double) Util.clamp(3.0, 0.0, 2.0), 0.0);
    assertEquals(2.0, (double) Util.clamp(999.0, 0.0, 2.0), 0.0);
  }

  @Test
  public void testClampChars() {
    assertEquals('b', (char) Util.clamp('a', 'b', 'd'));
    assertEquals('b', (char) Util.clamp('b', 'b', 'd'));
    assertEquals('c', (char) Util.clamp('c', 'b', 'd'));
    assertEquals('d', (char) Util.clamp('d', 'b', 'd'));
    assertEquals('d', (char) Util.clamp('e', 'b', 'd'));
  }
}
