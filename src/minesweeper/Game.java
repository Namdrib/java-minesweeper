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
 *         <p>
 *         TODO : This information is used to set high scores (when implemented)
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
	 * 
	 * @param width
	 *            how many Cells wide the game will be
	 * @param height
	 *            how many Cells high the game will be
	 * @param numMines
	 *            how many Cells are mines
	 * 
	 * @see Board.init()
	 * 
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
	 * Return a code signifying whether the game is finished. The return codes
	 * are as follows:
	 * <ul>
	 * <li><code>0</code>: The game is ongoing
	 * <li><code>1</code>: The game has been won (all non-mine Cells opened)
	 * <li><code>2</code>: A mine has been exploded
	 * </ul>
	 * 
	 * @return the return code
	 * 
	 */
	public int isFinished();
}
