package minesweeper;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Namdrib
 *
 */
public class CellImpl implements Cell
{
	/**
	 * cellState represents the external state of the Cell (what is displayed)
	 * Other information (flagState, number, etc.) represent the internal state
	 */
	CellState			cellState;
	int					flagState;
	int					number;
	boolean				isOpen;
	boolean				enableMarking;	// TODO : replace with a Game option?

	// Not sure if I need this:
	// Keeping track of the game and its position within the game
	Game				game;
	Point				point;
	Set<CellListener>	listeners;

	// Set default state
	public CellImpl()
	{
		cellState = CellState.FLAG0;
		flagState = 0;
		number = 0;
		isOpen = false;
		enableMarking = true;
		listeners = new HashSet<>();
	}

	/**
	 * 
	 * @param game
	 *            a reference to the Game in which the Cell resides
	 * @param point
	 *            representing the Cell's location within the Game's grid of
	 *            Cells
	 * @param mine
	 *            whether this Cell has a mine
	 */
	public CellImpl(Game game, Point point, boolean mine)
	{
		this();
		this.game = game;
		this.point = point;
		if (mine)
		{
			number = -1;
		}
	}

	@Override
	public void addListener(CellListener cl)
	{
		listeners.add(cl);
	}

	@Override
	public void deleteListener(CellListener cl)
	{
		listeners.remove(cl);
	}

	/**
	 * Alerts all listeners by looping through and calling
	 * <code>cellChanged()</code>
	 */
	public void alertListeners()
	{
		listeners.stream().forEach(e -> e.cellChanged());
	}

	@Override
	public void setNumber(int number)
	{
		this.number = number;
		alertListeners();
	}

	@Override
	public int getNumber()
	{
		return number;
	}

	@Override
	public boolean isMine()
	{
		return number == -1;
	}

	@Override
	public void setPoint(Point point)
	{
		this.point = point;
	}

	@Override
	public Point getPoint()
	{
		return new Point(point);
	}

	@Override
	public Game getGame()
	{
		return game;
	}

	@Override
	public boolean isOpen()
	{
		return isOpen;
	}

	@Override
	public int getFlagState()
	{
		return flagState;
	}

	@Override
	public CellState getCellState()
	{
		return cellState;
	}

	@Override
	public void setMarking(boolean marking)
	{
		enableMarking = marking;
	}

	@Override
	public void toggleFlag()
	{
		if (!isOpen)
		{
			System.out.println("Toggle flag");
			switch (flagState)
			{
				case 0:
					flagState = 1;
					cellState = CellState.FLAG1;
					break;
				case 1:
					if (enableMarking)
					{
						flagState = 2;
						cellState = CellState.FLAG2;
					}
					else
					{
						flagState = 0;
						cellState = CellState.FLAG0;
					}
					break;
				case 2:
					flagState = 0;
					cellState = CellState.FLAG0;
					break;
			}
			alertListeners();
		}
	}

	@Override
	public void open(boolean direct)
	{
		// Do nothing if already open.
		// Acts as base case for dfs recursion.
		if (isOpen)
		{
			System.err.println("CellImpl.open(): early return (open)");
			return;
		}
		// Cannot directly open if the Cell is flagged or marked
		// however, can directly open (e.g. via middle click or end-of-game reveal)
		if (flagState == 1)
		{
			if (!direct && !isMine())
			{
				flagState = 0;
				cellState = CellState.MINE3;
				isOpen = true;
				alertListeners();
			}
			else
			{
				System.err.println("CellImpl.open(): early return (flagged)");
			}
			return;
		}

		isOpen = true;

		// A mine has been opened.
		// TODO : "safeguarding" the first click?
		// TODO : extend to make a safe "lake"
		if (isMine())
		{
			// TODO : report to Game that a mine has been opened
			System.err.println("CellImpl.open(): Landed on a mine at " + point);
			cellState = ((direct) ? CellState.MINE2 : CellState.MINE1);

			// Open all other mines
			game.getCells().stream().forEach(r -> r.stream().filter(
					c -> !c.isOpen() && (c.isMine() || c.getFlagState() != 0))
					.forEach(c -> c.open(false)));
			alertListeners();
			return;
		}
		else
		{
			CellState[] numToState = { CellState.OPEN0, CellState.OPEN1,
					CellState.OPEN2, CellState.OPEN3, CellState.OPEN4,
					CellState.OPEN5, CellState.OPEN6, CellState.OPEN7,
					CellState.OPEN8 };
			cellState = numToState[number];
			if (number == 0)
			{
				// alertListeners();
				System.err.println("CellImpl.open(): opening " + point);
				// perform dfs on all unopened neighbours
				game.getNeighboursOf(this).stream().filter(c -> !c.isOpen())
						.forEach(c -> c.open(false));
			}
		}
		alertListeners();
	}

	@Override
	public String toString()
	{
		if (isOpen)
		{
			// TODO : tell whether it's the clicked mine or incorrectly flagged
			// if it was incorrectly flagged, have "x"
			// if it was a mine clicked on, have "*"
			if (isMine())
			{
				return ".";
			}
			else
			{
				return String.valueOf(number);
			}
		}
		else
		{
			switch (flagState)
			{
				case 0:
					return " ";
				case 1:
					return "F";
				case 2:
					return "?";
				default:
					return "*";
			}
		}
	}

}
