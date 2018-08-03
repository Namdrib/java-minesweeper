package minesweeper;

import java.awt.Point;

/**
 * 
 * Used to monitor the state of a single Cell in Minesweeper Each Cell's value should be related to
 * its Minesweeper state (whether it is a mine, a number or empty. It should also have state of
 * whether it has been opened, and whether it has a flag (or question mark) on it. A Cell's number
 * value should be determined by how many neighbouring mines it has (or whether it is a mine).
 * <p>
 * The Cell starts off closed and is not flagged nor marked.
 * 
 * @author Namdrib
 *
 */
public interface Cell {
  public enum CellState {
    OPEN0, OPEN1, OPEN2, OPEN3, OPEN4, OPEN5, OPEN6, OPEN7, OPEN8, // normal numbers. 0 is pressed
                                                                   // in/safe
    FLAG0, // normal
    FLAG1, // flagged
    FLAG2, // marked
    MINE1, // normal mine
    MINE2, // clicked-on mine
    MINE3, // incorrectly-flagged mine
  }

  /**
   * @param cl a listener for events
   */
  public void addListener(CellListener cl);

  /**
   * @param cl a listener for events
   */
  public void deleteListener(CellListener cl);

  /**
   * Set this Cell's number (-1 for mine, a number 0-8 otherwise)
   * 
   * @param number input number to set this.number
   */
  public void setNumber(int number);

  /**
   * 
   * Return a code signifying its number based on its closeness to mines. The return codes are as
   * follows:
   * <ul>
   * <li><code>-1</code>: The Cell contains a mine
   * <li><code>0</code>: The Cell has no adjacent mines
   * <li><code>1-8</code>: The Cell has 1-8 adjacent mines
   * </ul>
   * 
   * @return the return code
   */
  public int getNumber();

  /**
   * A shorthand way of checking whether the Cell's number is -1
   * 
   * @return <code>true</code> if the number is <code>-1</code>, <code>false</code> otherwise
   */
  public boolean isMine();

  /**
   * 
   * @param point input point to set this.point
   */
  public void setPoint(Point point);

  /**
   * 
   * @return a copy of the Cell's Point
   */
  public Point getPoint();

  /**
   * 
   * @return the Game in which the Cell resides
   */
  public Game getGame();

  /**
   * 
   * @return <code>true</code> if the Cell is open/revealed, <code>false</code> otherwise
   */
  public boolean isOpen();

  /**
   * Return a code signifying whether the Cell is flagged or marked. The return codes are as
   * follows:
   * <ul>
   * <li><code>0</code>: The Cell is neither flagged nor marked
   * <li><code>1</code>: The Cell is flagged
   * <li><code>2</code>: The Cell is marked
   * </ul>
   * 
   * @return the return code
   * 
   */
  public int getFlagState();

  /**
   * 
   * @return the Cell's CellState enum value
   */
  public CellState getCellState();

  public void setMarking(boolean marking);

  /**
   * Toggle the Cell's flag state. The order is <code>0 -> 1 -> [2 ->] 0</code>. Marking the Cell is
   * only available if marking is enabled.
   * <p>
   * Toggling the flag state is only available when the Cell is NOT open
   */
  public void toggleFlag();

  /**
   * Set the Cell's flag state to a flag
   */
  public void setFlag();

  /**
   * If it is not already open, "open" the Cell by revealing what is underneath. If it is empty
   * (i.e. not a mine and does not have a number), <code>open()</code> all of its neighbours
   * (including diagonals). Opening a Cell that has a mine results in the game being lost.
   * 
   * @param direct whether this is opened by a direct click
   */
  public void open(boolean direct);

  /**
   * Prints the Cell as a single-character String.
   * <p>
   * If the Cell has been opened, print:
   * <ul>
   * <li>"M": if the Cell is a mine that was just opened
   * <li>"m": if the Cell is a mine indirectly opened
   * <li>"X": if the Cell is an incorrectly flagged mine (that was opened)
   * <li>its number: if the Cell has no mines. If the Cell is opened and blank, print a zero.
   * </ul>
   * <p>
   * If the Cell has NOT been opened, print:
   * <ul>
   * <li>" ": if the Cell is not flagged or marked
   * <li>"F": if the Cell is flagged
   * <li>"?": if the Cell is marked
   * </ul>
   * 
   * @return a String representing the Cell as described above
   */
  public String toString();
}
