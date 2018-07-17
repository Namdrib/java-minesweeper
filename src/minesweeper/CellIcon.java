package minesweeper;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
					System.out.println("CellIcon: left mouse pressed");
					leftDown = false;
					cell.open(true);
					break;
				case MouseEvent.BUTTON2:
					System.out.println("CellIcon: middle mouse pressed");
					middleDown = false;
					// if enough mines, open each neighbour
					break;
				case MouseEvent.BUTTON3:
					System.out.println("CellIcon: right mouse pressed");
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

			if (e.getModifiers() == MouseEvent.BUTTON1_MASK
					|| e.getModifiers() == MouseEvent.BUTTON2_MASK)
			{
				System.out.println("Entered with left click");
				CellIcon.this
						.setIcon(new ImageIcon(Global.IMAGE_PATH + "_0.png"));
				validateAndRepaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			if (CellIcon.this.cell.isOpen() || CellIcon.this.cell.getFlagState() == 1)
			{
				return;
			}

			if (!(e.getModifiers() == MouseEvent.BUTTON1_MASK)
					|| !(e.getModifiers() == MouseEvent.BUTTON2_MASK))
			{
				CellIcon.this.setIcon(new ImageIcon(Global.IMAGE_PATH + CellIcon.this.cell.getCellState().toString() + ".png"));
				System.out.println(
						"CellIcon: Exited: " + leftDown + " | " + middleDown);
				validateAndRepaint();
			}
		}
	}

	Cell		cell;
	String		imageName;
	int			xSize;
	int			ySize;

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

	@Override
	public void cellChanged()
	{
		System.err.println("Cell changed");
		imageName = Global.IMAGE_PATH + cell.getCellState().toString().toLowerCase() + ".png";

		System.out.println("Cell " + cell.getPoint() + " changed to " + imageName);
		this.setIcon(new ImageIcon(imageName));
		validateAndRepaint();
	}

	// paintComponent() absolutely breaks repainting.
	// It results in each button looking like the top-left corner
	// of the entire Minesweeper window (Game, Help menus)
}
