package minesweeper;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Namdrib
 *
 */
public class GameImpl implements Game
{
	/**
	 * Used to switch upon when alerting listeners
	 * 
	 * @author Namdrib
	 *
	 */
	public enum GameChangeType
	{
		TILE, // tile change (something to do with the board)
		TIME, // timing change
		OTHER, // other changes
	}

	Set<GameListener>	listeners;
	GameDifficulty		difficulty;
	int					width, height, numMines;
	Board				board;
	int					secondsPassed;
	boolean				started;

	public GameImpl()
	{
		listeners = new HashSet<>();
		difficulty = GameDifficulty.BEGINNER;
		board = new BoardImpl(10, 10, 9);
		secondsPassed = 0;
		started = false;
	}

	public GameImpl(GameDifficulty difficulty, int width, int height,
			int numMines)
	{
		this();
		this.difficulty = difficulty;
		this.width = width;
		this.height = height;
		this.numMines = numMines;
		if (this.difficulty == null)
		{
			this.difficulty = GameDifficulty.BEGINNER;
		}
		//		System.out.println("New game at " + difficulty.toString());
		switch (difficulty)
		{
			case BEGINNER:
				board = new BoardImpl(9, 9, 10);
				break;
			case INTERMEDIATE:
				board = new BoardImpl(16, 16, 40);
				break;
			case EXPERT:
				board = new BoardImpl(30, 16, 99);
				break;
			case CUSTOM:
				board = new BoardImpl(width, height, numMines);
				break;
			default:
				break;
		}
	}

	public void addListener(GameListener gl)
	{
		listeners.add(gl);
	}

	public void removeListener(GameListener gl)
	{
		listeners.remove(gl);
	}

	/**
	 * Alerts all listeners by looping through and calling
	 * <code>cellChanged()</code>
	 */
	public void alertListeners(GameChangeType type)
	{
		switch (type)
		{
			case TILE:
				listeners.stream().forEach(e -> e.tileChanged());
				break;
			case TIME:
				listeners.stream().forEach(e -> e.timeChanged());
				break;
			case OTHER:
				listeners.stream().forEach(e -> e.otherChanged());
				break;
			default:
				return;
		}
	}

	@Override
	public void createBoard(int width, int height, int numMines)
	{
		board.init(width, height, numMines);
	}

	@Override
	public Board getBoard()
	{
		//		return new BoardImpl(board);
		return board;
	}

	@Override
	public GameDifficulty getDifficulty()
	{
		return difficulty;
	}

	@Override
	public void tick()
	{
		if (started)
		{
			if (secondsPassed < 999)
			{
				secondsPassed++;
				alertListeners(GameChangeType.TIME);
			}
		}
	}

	@Override
	public int getSecondsPassed()
	{
		return secondsPassed;
	};

	@Override
	public int isFinished()
	{
		// TODO Auto-generated method stub (GameImpl.isFinished())

		return 0;
	}

}
