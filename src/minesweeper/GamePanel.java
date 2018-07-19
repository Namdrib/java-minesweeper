package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 
 * @author Namdrib
 *
 */
public class GamePanel extends JPanel implements GameListener
{
	Minesweeper				minesweeper;
	Game					game;
	GamePanelMouseListener	l;

	JPanel					numMinePanel;
	JLabel[]				numMineLabels;
	ImageIcon[]				numMineIcons;

	JPanel					timerPanel;
	JLabel[]				timerLabels;
	ImageIcon[]				timerIcons;

	// TODO big boi
	private class GamePanelMouseListener extends MouseInputAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			System.out.println("GamePanel: mouse pressed");
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			System.out.println("GamePanel: mouse clicked");
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			System.out.println("GamePanel: mouse released");
		}
	}

	public GamePanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		l = new GamePanelMouseListener();
		addMouseListener(l);
		addMouseMotionListener(l);

		numMineLabels = new JLabel[3];
		timerLabels = new JLabel[3];
		for (int i=0; i<3; i++)
		{
			numMineLabels[i] = new JLabel();
			timerLabels[i] = new JLabel();
		}
	}

	// TODO : Create custom bevel border to allow thicknesses
	public GamePanel(Game game, Minesweeper minesweeper)
	{
		this();
		this.game = game;
		this.minesweeper = minesweeper;

		//-------------- testing HUD
		JPanel hud = new JPanel(new BorderLayout());
		hud.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.LINE_START);

		// add remaining mines (LINE_START)
		numMinePanel = new JPanel();
		numMinePanel
				.setLayout(new BoxLayout(numMinePanel, BoxLayout.LINE_AXIS));
		for (int i=0; i<numMineLabels.length; i++)
		{
			numMineLabels[i] = new JLabel(new ImageIcon(Global.IMAGE_PATH + "num_0.png"));
			numMinePanel.add(numMineLabels[i]);
		}
		numMinePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.WHITE, Color.DARK_GRAY));
		hud.add(numMinePanel, BorderLayout.LINE_START);

		// add face (CENTER) TODO : turn into JLabel?
		//		JButton face = new JButton(".",
		//				new ImageIcon(Global.IMAGE_PATH + "face-normal.png"));
		//		face.setMnemonic(MouseEvent.BUTTON1);
		//		face.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e)
		//			{
		//				System.out.println("Face pressed!");
		//				minesweeper.resetGame();
		//			}
		//		});
		//		face.addMouseListener(new MouseListener() {
		//
		//			@Override
		//			public void mouseClicked(MouseEvent arg0)
		//			{
		//				// TODO Auto-generated method stub
		//				;
		//			}
		//
		//			@Override
		//			public void mouseEntered(MouseEvent arg0)
		//			{
		//				// TODO Auto-generated method stub
		//
		//			}
		//
		//			@Override
		//			public void mouseExited(MouseEvent arg0)
		//			{
		//				// TODO Auto-generated method stub
		//
		//			}
		//
		//			@Override
		//			public void mousePressed(MouseEvent arg0)
		//			{
		//				// TODO Auto-generated method stub
		//
		//			}
		//
		//			@Override
		//			public void mouseReleased(MouseEvent arg0)
		//			{
		//				// TODO Auto-generated method stub
		//
		//			}
		//		});

		JLabel face = new JLabel(
				new ImageIcon(Global.IMAGE_PATH + "face-normal.png"));
		hud.add(face, BorderLayout.CENTER);

		// add timer (LINE_END)
		timerPanel = new JPanel();
		timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.LINE_AXIS));
		for (int i=0; i<timerLabels.length; i++)
		{
			timerLabels[i] = new JLabel(new ImageIcon(Global.IMAGE_PATH + "num_0.png"));
			timerPanel.add(timerLabels[i]);
		}
		timerPanel.setBorder(BorderFactory.createBevelBorder(
				BevelBorder.LOWERED, Color.WHITE, Color.DARK_GRAY));
		hud.add(timerPanel, BorderLayout.LINE_END);

		hud.setBackground(new Color(192, 192, 192));
		hud.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.WHITE, Color.DARK_GRAY));

		this.add(hud);
		//-------------- testing HUD

		this.add(Box.createRigidArea(new Dimension(0, 5)));

		//-------------- testing cellField
		Point p = game.getDimensions();
		int xButtons = (int) p.getX();
		int yButtons = (int) p.getY();

		JPanel cellField = new JPanel(new GridLayout(0, xButtons, 0, 0));
		for (int i = 0; i < yButtons; i++)
		{
			for (int j = 0; j < xButtons; j++)
			{
				JLabel a = new CellIcon(game.getCells().get(i).get(j));
				cellField.add(a);
			}
		}
		cellField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.WHITE, Color.DARK_GRAY));
		//		this.add(cellField, BorderLayout.CENTER);
		this.add(cellField);

		//-------------- testing cellField
		this.setBorder(new LineBorder(new Color(192, 192, 192), 6));
	}

	@Override
	public void tileChanged()
	{
		// TODO Auto-generated method stub (GamePanel.tileChanged())
		System.out.println(game.getRemainingMines() + " mines left");
		// TODO if game is finished, do a thing?
	}

	// Path must be relative to the project folder,
	// not from this particular file
	private static void playSound(String s)
	{
		try
		{
			Media hit = new Media(new File(s).toURI().toString());
			MediaPlayer mediaPlayer = new MediaPlayer(hit);
			mediaPlayer.play();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void timeChanged()
	{
		String timeStr = String.format("%03d", game.getSecondsPassed());
		System.out.println("Time changed to " + timeStr);
		if (minesweeper.enableSound)
		{
			playSound(Global.SOUND_PATH + "tick.mp3");
		}
		for (int i=0; i<timeStr.length(); i++)
		{
			String img = Global.IMAGE_PATH + "num_" + timeStr.charAt(i) + ".png";
			System.out.println(img);
			timerLabels[i].setIcon(new ImageIcon(img));
			System.out.println("Changed " + i + " to " + timerLabels[i].getIcon());
			timerLabels[i].validate();
			timerLabels[i].repaint();
		}
		validate();
		repaint();
	}

	@Override
	public void otherChanged()
	{
		// TODO Auto-generated method stub (GamePanel.otherChanged())
	}

	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(192, 192, 192));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
}
