package minesweeper.gameio;

/**
 * 
 * Exception for Game formats. Primary used in GameIO.
 * 
 * Usage situations might include:
 * <ul>
 * <li>inappropriate size
 * <li>number of mines
 * <li>mines out of bounds
 * </ul>
 * 
 * @author Namdrib
 *
 */
public class GameFormatException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 9102959461959126094L;

  int lineNr;

  public GameFormatException(int lineNr, String msg) {
    super(msg);
    this.lineNr = lineNr;
  }

  @Override
  public String toString() {
    return lineNr + " - " + super.toString();
  }
}
