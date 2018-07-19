package minesweeper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
	Point				dims;
	List<List<Cell>>	cells;
	int					secondsPassed;
	boolean				started;

	public GameImpl()
	{
		listeners = new HashSet<>();
		difficulty = GameDifficulty.BEGINNER;
		createBoard(10, 10, 9);
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

		switch (difficulty)
		{
			case BEGINNER:
				createBoard(9, 9, 10);
				break;
			case INTERMEDIATE:
				createBoard(16, 16, 40);
				break;
			case EXPERT:
				createBoard(30, 16, 99);
				break;
			case CUSTOM:
				createBoard(width, height, numMines);
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
		// Clamp the dimensions and mine numbers
		dims = new Point(Math.max(9, Math.min(30, width)),
				Math.max(9, Math.min(24, height)));
		int maxMines = (int) ((dims.getX() - 1) * (dims.getY() - 1));
		this.numMines = Math.max(10, Math.min(maxMines, numMines));

		// Initialise with numMines mines in no particular order, then shuffle
		List<Cell> first = new ArrayList<>();
		int counter = 0;
		for (int i = 0; i < dims.getY(); i++)
		{
			for (int j = 0; j < dims.getX(); j++)
			{
				first.add(new CellImpl(this, new Point(i, j),
						counter < this.numMines));
				counter++;
			}
		}
		Collections.shuffle(first);

		// Convert the 1D List into a 2D List and re-allocate Points
		cells = new ArrayList<>();
		for (int i = 0; i < dims.getY(); i++)
		{
			ArrayList<Cell> oneRow = new ArrayList<>();
			for (int j = 0; j < dims.getX(); j++)
			{
				Cell temp = first.get((int) (i * dims.getX() + j));
				temp.setPoint(new Point(j, i));
				oneRow.add(temp);
			}
			cells.add(oneRow);
		}

		// Update each Cell's number according to their neighbouring mines
		cells.stream().forEach(row -> row.stream()
				.filter(cell -> !cell.isMine()).forEach(cell -> {
					int numNeighbouringMines = (int) getNeighboursOf(cell)
							.stream().filter(neighbour -> neighbour.isMine())
							.count();
					cell.setNumber(numNeighbouringMines);
				}));
	}

	@Override
	public List<List<Cell>> getCells()
	{
		return cells;
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

	@Override
	public Set<Cell> getNeighboursOf(Cell cell)
	{
		Set<Cell> neighbours = new HashSet<>();
		Point p = cell.getPoint();

		// Search each adjacent Cell including diagonals
		// Do not include self or out-of-bounds co-ordinates
		for (int n = -1; n < 2; n++)
		{
			for (int m = -1; m < 2; m++)
			{
				int newY = (int) p.getY() + n;
				int newX = (int) p.getX() + m;
				if ((n == 0 && m == 0) || newY < 0 || newX < 0
						|| newY >= dims.getY() || newX >= dims.getX())
				{
					continue;
				}
				neighbours.add(cells.get(newY).get(newX));
			}
		}

		return neighbours;
	}

	@Override
	public Point getDimensions()
	{
		return new Point(dims);
	}

	@Override
	public int getNumMines()
	{
		return numMines;
	}

	@Override
	public int getRemainingMines()
	{
		// For each row, count the number of flagged Cells. Sum these up
		int numPlacedFlags = cells.stream().mapToInt(b -> (int) b.stream()
				.filter(c -> c.getFlagState() == 1).count()).sum();
		return numMines - numPlacedFlags;
	}

	@Override
	public String toString()
	{
		String out = new String(String.valueOf(getRemainingMines()) + ", "
				+ String.valueOf(secondsPassed) + System.lineSeparator());

		// Surround the cells with an outline to more easily see the bounds
		String horizontalBorder = '+'
				+ new String(new char[(int) dims.getX() + 2]).replace('\0', '-')
				+ '+';
		out += horizontalBorder + System.lineSeparator();
		for (List<Cell> row : cells)
		{
			out += "| ";
			for (Cell cell : row)
			{
				out += String.valueOf(cell);
			}
			out += " |" + System.lineSeparator();
		}
		out += horizontalBorder;
		return out;
	}
}
