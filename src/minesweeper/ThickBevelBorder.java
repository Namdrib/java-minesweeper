package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

/**
 * A custom Border class to emulate a BevelBorder, but with adjustable
 * thickness. The class has three colours:
 * <ul>
 * <li>top-left border line
 * <li>bottom-right border line
 * <li>intersecting lines
 * </ul>
 * 
 * It also has a thickness, described by pixels.
 * 
 * @see BevelBorder
 * @author Namdrib
 *
 */
public class ThickBevelBorder extends AbstractBorder
{
	Color	topLeftColour, bottomRightColour, diagColour;
	int		borderThickness;

	public ThickBevelBorder(Color topLeftColour, Color bottomRightColour,
			int borderThickness)
	{
		setTopLeftColour(topLeftColour);
		setBottomRightColour(bottomRightColour);
		setBorderThickness(borderThickness);
	}

	/**
	 * @return the topLeftColour
	 */
	public Color getTopLeftColour()
	{
		return topLeftColour;
	}

	/**
	 * @param topLeftColour
	 *            the topLeftColour to set
	 */
	public void setTopLeftColour(Color topLeftColour)
	{
		this.topLeftColour = topLeftColour;
	}

	/**
	 * @return the bottomRightColour
	 */
	public Color getBottomRightColour()
	{
		return bottomRightColour;
	}

	/**
	 * @param bottomRightColour
	 *            the bottomRightColour to set
	 */
	public void setBottomRightColour(Color bottomRightColour)
	{
		this.bottomRightColour = bottomRightColour;
	}

	/**
	 * @return the borderThickness
	 */
	public int getBorderThickness()
	{
		return borderThickness;
	}

	/**
	 * @param borderThickness
	 *            the borderThickness to set
	 */
	public void setBorderThickness(int borderThickness)
	{
		this.borderThickness = borderThickness;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height)
	{
		super.paintBorder(c, g, x, y, width, height);

		int h = height;
		int w = width;
		int bw = getBorderThickness();
		Graphics2D g2 = (Graphics2D) g.create();

		// Start in the top-left corner, work clockwise around back to start
		Polygon topLeftPolygon = createPolygon(
				new Point(0, 0),
				new Point(w - 3, 0),
				new Point(w - bw - 2, bw - 1),
				new Point(bw - 1, bw - 1),
				new Point(bw - 1, h - bw - 2),
				new Point(0, h - 3),
				new Point(0, 0));
		g2.setColor(getTopLeftColour());
		g2.fill(topLeftPolygon);
		g2.draw(topLeftPolygon);

		// Start in the bottom-right corner, work clockwise around back to start
		Polygon bottomRightPolygon = createPolygon(
				new Point(w - 2, h - 2), 
				new Point(1, h - 2),
				new Point(bw, h - bw - 1),
				new Point(w - bw - 1, h - bw - 1),
				new Point(w - bw - 1, bw),
				new Point(w - 2, 1),
				new Point(w - 2, h - 2));
		g2.setColor(getBottomRightColour());
		g2.fill(bottomRightPolygon);
		g2.draw(bottomRightPolygon);

		// TODO : remove me
		// put points into the corners
		g2.setColor(Color.MAGENTA);
		g2.fillRect(0, 0, 1, 1);
		g2.fillRect(0, height - 2, 1, 1);
		g2.fillRect(width - 2, 0, 1, 1);
		g2.fillRect(width - 2, height - 2, 1, 1);

		g2.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c)
	{
		int a = getBorderThickness();
		return new Insets(a, a, a + 1, a + 1);
	}

	private Polygon createPolygon(Point... points)
	{
		Polygon polygon = new Polygon();
		for (Point point : points)
		{
			polygon.addPoint(point.x, point.y);
		}
		return polygon;
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		for (int i = 1; i < 8; i++)
		{
			JPanel p = new JPanel() {
				@Override
				public Dimension getPreferredSize()
				{
					return new Dimension(200, 60);
				}
			};
			p.setBorder(new ThickBevelBorder(Color.RED, Color.GREEN, i));
			panel.add(p);
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
		}

		frame.add(panel);

		frame.pack();
		frame.setVisible(true);
	}
}
