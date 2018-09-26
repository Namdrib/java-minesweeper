package minesweeper.util;

/**
 * Class providing static utility functions
 * 
 * @author Namdrib
 *
 */
public class Util {
  /**
   * 
   * Clamps an input value between a provided lower and upper bound
   * 
   * @param in the input value to clamp
   * @param lo the lower bound
   * @param hi the upper bound
   * @return the result of clamping <code>in</code> between <code>lo</code> and <code>hi</code>
   */
  public static <T extends Comparable<? super T>> T clamp(T in, T lo, T hi) {
    return ((lo.compareTo(in) > 0) ? lo : (hi.compareTo(in) < 0) ? hi : in);
  }
}
