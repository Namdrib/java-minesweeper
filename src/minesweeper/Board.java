package minesweeper;

import java.awt.Point;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Namdrib
 * 
 *         Used to represent the Minesweeper field.
 * 
 */
public interface Board
{
	/**
	 * 
	 * Initialise the board to the specified size and number of mines. Also
	 * populate the board with that number of mines and set all the Cell values.
	 * <p>
	 * Also sets each Cell's number based on neighbouring mines. For example, if
	 * the Cell contains a mine, it should be -1. If there are two adjacent
	 * mines, the number should be 2. If there are no adjacent mines, this
	 * number should be zero. Rules:
	 * <p>
	 * width and height each must range between 9 and 30 (i.e. smallest board is
	 * 9x9, largest is 30x30, anything in between is valid. Attempts to set the
	 * size to anything else results in the offending dimension being "clamped"
	 * to [9-30].
	 * <p>
	 * numMines must range between 10 and 667 (inclusive). The most mines in a
	 * 9x9 is 64. TODO : Figure out how this scales with the number of tiles
	 * 
	 * @param width
	 *            requested board width (Cells)
	 * @param height
	 *            requested board height (Cells)
	 * @param mines
	 *            requested board mine count (Cells with mines)
	 * 
	 */
	public void init(int width, int height, int numMines);

	/**
	 * 
	 * @param x
	 *            x co-ordinate of target Cell
	 * @param y
	 *            y co-ordinate of target Cell
	 * @return a Set of up to 8 Cells neighbouring the Cell specified by the
	 *         dimensions
	 * @throws IndexOutOfBoundsException
	 *             if provided x or y is out of bounds of the Board's cells
	 */
	public Set<Cell> getNeighboursOf(int x, int y)
			throws IndexOutOfBoundsException;

	/**
	 * 
	 * @return a point representing (x, y) where x and y are the number of cells
	 *         in each dimension
	 */
	public Point getDimensions();

	/**
	 * 
	 * @return the total number of mines
	 */
	public int getNumMines();

	/**
	 * This should be <code>numMines - numFlags</code>, regardless of whether
	 * the flags correspond to a mine. For example if the Board has 10 mines,
	 * and 6 flags have been used anywhere, then this returns 4. If the number
	 * is negative, too many flags have been placed.
	 * 
	 * @return the number of "unflagged" mines. This number can be negative.
	 */
	public int getRemainingMines();

	/**
	 * 
	 * @return a copy of the Board's cells
	 */
	public List<List<Cell>> getCells();

	/**
	 * "Opens" the Cell at position (x, y). This should call open() on the Cell.
	 * 
	 * @param x
	 *            the x co-ordinate to use
	 * @param y
	 *            the y co-ordinate to use
	 */
	public void open(int x, int y);

	/**
	 * 
	 * Return a 2D grid of the Board's cells. There should be each Cell's
	 * String, with one row on a single line. The rows should be separated by
	 * new line characters
	 * 
	 * @return a String representing the Board as described above
	 */
	public String toString();

}
