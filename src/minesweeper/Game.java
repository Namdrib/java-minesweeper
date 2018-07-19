package minesweeper;

/**
 * 
 * @author Namdrib
 *
 *         Should have a Board object Also keeps track of timing information
 *         associated with the game
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
	 *            how many cells wide the board will be
	 * @param height
	 *            how many cells high the board will be
	 * @param numMines
	 *            how many mines in the board
	 * 
	 * @see Board.init()
	 * 
	 */
	public void createBoard(int width, int height, int numMines);

	/**
	 * Returns a copy so the board state can be observed but not changed
	 * externally
	 * 
	 * @return a copy of the board
	 */
	public Board getBoard();

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
	 * <li><code>1</code>: The game has been won (all non-mine Cells in the
	 * Board opened)
	 * <li><code>2</code>: A mine has been exploded
	 * </ul>
	 * 
	 * @return the return code
	 * 
	 */
	public int isFinished();
}
