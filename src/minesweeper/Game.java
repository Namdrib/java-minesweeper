package minesweeper;

import java.awt.Point;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Namdrib
 *
 *         Keeps track of the Cells and the timing information associated with
 *         the game
 *
 */
public interface Game
{
	/**
	 * Used to determine whether to write high scores. A custom game with the
	 * same dimensions and mines as a preset difficulty does not count as that
	 * difficulty. Only check high scores if the provided difficulty is one of
	 * BEGINNER, INTERMEDIATE or EXPERT
	 * 
	 * @author Namdrib
	 *
	 */
	public enum GameDifficulty
	{
		BEGINNER, // beginner difficulty (9x9, 10 mines)
		INTERMEDIATE, // intermediate difficulty (16x16, 40 mines)
		EXPERT, // expert difficulty (16x30, 99 mines)
		CUSTOM, // custom difficulty
	}

	/**
	 * @param gl
	 *            a listener for events
	 */
	public void addListener(GameListener gl);

	/**
	 * @param gl
	 *            a listener for events
	 */
	public void removeListener(GameListener gl);

	/**
	 * Initialise the board to the specified size and number of mines. Also
	 * populate the board with that number of mines and set all the Cell values.
	 * <p>
	 * Also sets each Cell's number based on neighbouring mines. For example, if
	 * the Cell contains a mine, it should be -1. If there are two adjacent
	 * mines, the number should be 2. If there are no adjacent mines, this
	 * number should be 0. Rules:
	 * <p>
	 * <ul>
	 * <li>width must be between 9 and 30 (inclusive)
	 * <li>height must be between 9 and 24 (inclusive)
	 * <li>numMines must be between 10 and (width-1)*(height-1) (inclusive)
	 * <li>Attempts to set the size to anything else results in the offending
	 * dimension being "clamped" to its appropriate range.
	 * </ul>
	 * <p>
	 * 
	 * @param width
	 *            requested board width (Cells)
	 * @param height
	 *            requested board height (Cells)
	 * @param numMines
	 *            requested board mine count (Cells with mines)
	 */
	public void createBoard(int width, int height, int numMines);

	/**
	 * 
	 * @param cell
	 *            target Cell
	 * @return a Set of up to 8 Cells neighbouring the Cell specified by the
	 *         input Cell
	 */
	public Set<Cell> getNeighboursOf(Cell cell);

	/**
	 * 
	 * @return a point representing (x, y) where x and y are the number of Cells
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
	 * Move the mine located at <code>cell</code> to the top-left Cell. If the
	 * top-left Cell already has a mine, relocate it to the next one to the
	 * right, and so on, until a free spot is found. If the first free Cell to
	 * be found is <code>cell</code>, skip it.
	 * <p>
	 * e.g. if <code>cell</code> is at position (1, 0), and there is a mine at
	 * (0, 0), then move the mine to (2, 0). If (2, 0) already has a mine, move
	 * it to (3, 0).
	 * 
	 * @param cell
	 *            whose mine needs a relocation
	 */
	public void relocateMine(Cell cell);

	/**
	 * Used to alert listeners that a flag has been changed. Should trigger
	 * recalculation of <code>getRemainingMines()</code>
	 */
	public void flagChanged();

	/**
	 * Returns a copy so the board state can be observed but not changed
	 * externally
	 * 
	 * @return a copy of the Cells
	 */
	public List<List<Cell>> getCells();

	/**
	 * 
	 * @return the game difficulty
	 */
	public GameDifficulty getDifficulty();

	/**
	 * Tick used for timing information. Increases a counter based on a counter.
	 */
	public void tick();

	/**
	 * @return the number of seconds passed since the game as started
	 */
	public int getSecondsPassed();

	/**
	 * Set <code>finished</code> to a code signifying the game state. The
	 * <code>finished</code> codes are as follows:
	 * <ul>
	 * <li><code>0</code>: The game is ongoing
	 * <li><code>1</code>: The game has been won (all non-mine Cells opened)
	 * <li><code>2</code>: A mine has been exploded
	 * </ul>
	 */
	public void setFinished();

	/**
	 * 
	 * @return a code representing whether the game is finished, as set by
	 *         <code>isFinishied()</code>
	 * 
	 * @see isFinished()
	 */
	public int getFinished();

	/**
	 * 
	 * @return <code>true</code> if the game has started, <code>false</code>
	 *         otherwise
	 */
	public boolean isStarted();

	/**
	 * Called to start the game
	 */
	public void start();
}
