package minesweeper;

import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import minesweeper.Cell.CellState;

public class CellIcon extends JLabel implements CellListener
{
	// TODO : Big boi CellIconMouseListener
	// Use Board.getRemainingMines() for middle click
	private class CellIconMouseListener extends MouseInputAdapter
	{
		boolean	leftDown	= false;
		boolean	middleDown	= false;
		boolean	rightDown	= false;

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (cell.isOpen())
			{
				return;
			}

//			if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0)
//			{
//				cell.toggleFlag();
//			}
			int btn = e.getButton();
			switch (btn)
			{
				case MouseEvent.BUTTON1:
					// "push" the cell down but don't do anything yet
					System.out.println("CellIcon: left mouse pressed");
					leftDown = true;
					CellIcon.this.setIcon(
							new ImageIcon(Global.IMAGE_PATH + "_0.png"));
					break;
				case MouseEvent.BUTTON2:
					System.out.println("CellIcon: middle mouse pressed");
					middleDown = true;
					break;
				case MouseEvent.BUTTON3:
					System.out.println("CellIcon: right mouse pressed");
					cell.toggleFlag();
					rightDown = true;
					break;
				default:
					break;
			}
			validateAndRepaint();
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			System.out.println("CellIcon: mouse clicked");
			validateAndRepaint();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			int btn = e.getButton();
			switch (btn)
			{
				case MouseEvent.BUTTON1:
					// "push" the cell down but don't do anything yet
					System.out.println("CellIcon: left mouse released, " + leftDown);
					if (leftDown)
					{
						System.out.println("Open it");
						cell.open(true);
					}
					leftDown = false;
					break;
				case MouseEvent.BUTTON2:
					System.out.println("CellIcon: middle mouse released");
					middleDown = false;
					// if enough mines, open each neighbour
					if (CellIcon.this.cell.isOpen())
					{
						Set<Cell> neighbours = CellIcon.this.cell.getBoard()
								.getNeighboursOf(CellIcon.this.cell);
						int neighbouringFlags = (int) neighbours.stream()
								.filter(c -> c.getFlagState() == 1).count();
						if (neighbouringFlags == CellIcon.this.cell.getNumber())
						{
							neighbours.stream().forEach(c -> c.open(false));
						}
					}
					break;
				case MouseEvent.BUTTON3:
					System.out.println("CellIcon: right mouse released");
					rightDown = false;
					break;
				default:
					break;
			}
			validateAndRepaint();
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			System.out.println("Mouse entered");
			if (cell.isOpen() || cell.getFlagState() == 1)
			{
				return;
			}

			if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
			{
				System.out.println("Entered with left click");
				leftDown = true;
				CellIcon.this
						.setIcon(new ImageIcon(Global.IMAGE_PATH + "_0.png"));
				validateAndRepaint();
			}
			if (e.getModifiers() == MouseEvent.BUTTON2_MASK)
			{
				System.out.println("Entered with middle click");
				middleDown = true;
				CellIcon.this
						.setIcon(new ImageIcon(Global.IMAGE_PATH + "_0.png"));
				validateAndRepaint();
			}
			if (e.getModifiers() == MouseEvent.BUTTON3_MASK)
			{
				System.out.println("Entered with right click");
				rightDown = true;
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			leftDown = false;
			middleDown = false;
			rightDown = false;
			if (CellIcon.this.cell.isOpen()
					|| CellIcon.this.cell.getFlagState() == 1)
			{
				return;
			}

			if (!(e.getModifiers() == MouseEvent.BUTTON1_MASK)
					|| !(e.getModifiers() == MouseEvent.BUTTON2_MASK))
			{
				CellIcon.this.resetImageToCellState();
				System.out.println(
						"CellIcon: Exited: " + leftDown + " | " + middleDown);
				validateAndRepaint();
			}
		}
	}

	Cell	cell;
	String	imageName;
	int		xSize;
	int		ySize;

	public CellIcon()
	{
		CellIconMouseListener l = new CellIconMouseListener();
		addMouseListener(l);
		addMouseMotionListener(l);

		imageName = Global.IMAGE_PATH + "flag0.png";
		this.setIcon(new ImageIcon(imageName));

		validateAndRepaint();
	}

	public CellIcon(Cell cell)
	{
		this();
		this.cell = cell;
		cell.addListener(this);
		validateAndRepaint();
	}

	private void validateAndRepaint()
	{
		validate();
		repaint();
	}

	private void resetImageToCellState()
	{
		this.setIcon(new ImageIcon(Global.IMAGE_PATH
				+ this.cell.getCellState().toString() + ".png"));
	}

	@Override
	public void cellChanged()
	{
		System.err.println("Cell changed");
		resetImageToCellState();
		System.out.println("cellChanged: " + cell.getPoint() + " to "
				+ this.getIcon().toString());
		validateAndRepaint();
	}

	// paintComponent() absolutely breaks repainting.
	// It results in each button looking like the top-left corner
	// of the entire Minesweeper window (Game, Help menus)
}
