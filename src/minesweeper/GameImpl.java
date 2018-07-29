package minesweeper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		LOSE, // tile change (something to do with the board)
		WIN, // other changes
		TICK, // timing change
		FLAG, // Cell flag changes
	}

	Set<GameListener>	listeners;
	GameDifficulty		difficulty;
	int					numMines;
	Point				dims;
	List<List<Cell>>	cells;
	int					secondsPassed;
	boolean				started;
	int					finished;

	public GameImpl()
	{
		listeners = new HashSet<>();
		difficulty = GameDifficulty.BEGINNER;
		createBoard(10, 10, 9);
		secondsPassed = 0;
		started = false;
		finished = 0;
	}

	public GameImpl(GameDifficulty difficulty, int width, int height,
			int numMines)
	{
		this();
		this.difficulty = difficulty;
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
		flagChanged();
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
			case LOSE:
				listeners.stream().forEach(e -> e.gameLose());
				break;
			case WIN:
				listeners.stream().forEach(e -> e.gameWin());
				break;
			case TICK:
				listeners.stream().forEach(e -> e.gameTick());
				break;
			case FLAG:
				listeners.stream().forEach(e -> e.flagChanged());
				break;
			default:
				return;
		}
	}

	// I want to distribute numMines across the board randomly.
	// But then also need to make sure they have the correct co-ordinates
	// and neighbour numbers. Therefore, split this into a four-stage process:
	// 1. Create a 1D List and make the first numMines entries with mines
	// 2. Shuffle this List to randomise mine position
	// 3. Migrate the elements into a 2D List
	// 4. Assign numbers for each element in the 2D List based on neighbours
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
		if (started && finished == 0)
		{
			if (secondsPassed < 999)
			{
				secondsPassed++;
				alertListeners(GameChangeType.TICK);
			}
		}
	}

	@Override
	public int getSecondsPassed()
	{
		return secondsPassed;
	};

	@Override
	public void setFinished()
	{
		// Already finished, no chance of changing
		if (finished > 0)
		{
			return;
		}

		// A mine has been opened (lose game)
		int openMines = (int) cells.stream().flatMap(Collection::stream)
				.filter(c -> c.isOpen() && c.isMine()).count();
		if (openMines > 1)
		{
			finished = 2;
			alertListeners(GameChangeType.LOSE);
			return;
		}

		// The board has been revealed (win game)
		int openCells = (int) cells.stream().flatMap(Collection::stream)
				.filter(c -> c.isOpen()).count();
		if (openCells == (dims.getX() * dims.getY() - numMines))
		{
			finished = 1;
			alertListeners(GameChangeType.WIN);
			return;
		}

		// Nothing so far, not finished
		finished = 0;
	}

	@Override
	public int getFinished()
	{
		return finished;
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
	public void relocateMine(Cell cell)
	{
		System.out.println("Safe click");

		// Relocate the mine
		relocation: for (int i = 0; i < cells.size(); i++)
		{
			for (int j = 0; j < cells.get(0).size(); j++)
			{
				Cell temp = cells.get(i).get(j);

				if (!temp.isMine())
				{
					// Make temp a mine, update its neighbours counts
					temp.setNumber(-1);
					getNeighboursOf(temp).stream().filter(c -> !c.isMine())
							.forEach(c -> c.setNumber(c.getNumber() + 1));
					break relocation;
				}
			}
		}

		// Update cell number
		cell.setNumber((int) getNeighboursOf(cell).stream()
				.filter(c -> c.isMine()).count());
		// Update neighbours of cell
		getNeighboursOf(cell).stream().filter(c -> !c.isMine())
				.forEach(c -> c.setNumber(c.getNumber() - 1));
	}

	@Override
	public void flagChanged()
	{
		alertListeners(GameChangeType.FLAG);
	}

	@Override
	public boolean isStarted()
	{
		return started;
	}

	@Override
	public void start()
	{
		started = true;
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
