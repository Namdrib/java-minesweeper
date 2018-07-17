package minesweeper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BoardImpl implements Board
{
	Point				dims;
	int					numMines;
	List<List<Cell>>	cells;

	public BoardImpl(int width, int height, int numMines)
	{
		init(width, height, numMines);
	}

	public BoardImpl(Board board)
	{
		this.dims = new Point(board.getDimensions());
		this.numMines = board.getNumMines();
		this.cells = board.getCells();
	}

	// I want to distribute numMines across the board randomly.
	// But then also need to make sure they have the correct co-ordinates
	// and neighbour numbers. Therefore, split this into a four-stage process:
	// 1. Create a 1D List and make the first numMines entries with mines
	// 2. Shuffle this List to randomise mine position
	// 3. Migrate the elements into a 2D List
	// 4. Assign numbers for each element in the 2D List based on neighbours
	@Override
	public void init(int width, int height, int numMines)
	{
		// Clamp the dimensions and mine numbers
		dims = new Point(Math.max(9, Math.min(30, width)),
				Math.max(9, Math.min(30, height)));
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
		for (int i = 0; i < dims.getY(); i++)
		{
			for (int j = 0; j < dims.getX(); j++)
			{
				// Don't need to update a mine's number
				if (cells.get(i).get(j).isMine())
				{
					continue;
				}

				int numNeighbouringMines = (int) getNeighboursOf(j, i).stream()
						.filter(c -> c.isMine()).count();

				cells.get(i).get(j).setNumber(numNeighbouringMines);
			}
		}

		// Same as above
		cells.stream().forEach(row -> row.stream()
				.filter(cell -> !cell.isMine()).forEach(cell -> {
					Point p = cell.getPoint();
					int numNeighbouringMines = (int) getNeighboursOf(
							(int) p.getX(), (int) p.getY()).stream()
									.filter(neighbour -> neighbour.isMine())
									.count();
					cell.setNumber(numNeighbouringMines);
				}));
	}

	@Override
	public Set<Cell> getNeighboursOf(int xIn, int yIn)
			throws IndexOutOfBoundsException
	{
		if (yIn < 0 || yIn >= dims.getY() || xIn < 0 || xIn >= dims.getX())
		{
			throw new IndexOutOfBoundsException(
					"Bad bounds: x=" + xIn + ", y=" + yIn);
		}
		Set<Cell> neighbours = new HashSet<>();

		// Search each adjacent Cell including diagonals
		// Do not include self or out-of-bounds co-ordinates
		for (int n = -1; n < 2; n++)
		{
			for (int m = -1; m < 2; m++)
			{
				int newY = yIn + n;
				int newX = xIn + m;
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
	public List<List<Cell>> getCells()
	{
		return new ArrayList<List<Cell>>(cells);
	}

	@Override
	public void open(int x, int y)
	{
		cells.get(y).get(x).open(true);
		System.out.println(this.toString());
	}

	@Override
	public String toString()
	{
		// Surround the board with an outline to more easily see the
		// Board's bounds
		String horizontalBorder = '+'
				+ new String(new char[(int) dims.getX() + 2]).replace('\0', '-')
				+ '+';
		String out = new String(horizontalBorder) + System.lineSeparator();
		for (List<Cell> row : cells)
		{
			out += "| ";
			for (Cell c : row)
			{
				out += String.valueOf(c);
			}
			out += " |" + System.lineSeparator();
		}
		out += horizontalBorder;
		return out;
	}
}
