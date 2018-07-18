package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
	static Minesweeper		minesweeper;
	Game					game;
	GamePanelMouseListener	l;

	ImageIcon[]				timerIcons;
	ImageIcon[]				numMineIcons;

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
//		this.setLayout(new BorderLayout());
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		l = new GamePanelMouseListener();
		addMouseListener(l);
		addMouseMotionListener(l);
	}

	public GamePanel(Game game, Minesweeper minesweeper)
	{
		this();
		this.game = game;
		this.minesweeper = minesweeper;

		//-------------- testing HUD
		JPanel hud = new JPanel(new BorderLayout());
		
		// add remaining mines (LINE_START)
		
		// add face (CENTER) TODO : turn into JLabel?
		JButton face = new JButton("Face, yo",
				new ImageIcon(Global.IMAGE_PATH + "face-normal.png"));
		face.setMnemonic(MouseEvent.BUTTON1);
		face.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Face pressed!");
				minesweeper.resetGame();
			}
		});
		face.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				;
			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
		});

		hud.add(face, BorderLayout.CENTER);

		// add timer (LINE_END)

//		this.add(hud);
		//-------------- testing HUD

		// maybe add a separator?
//		this.add(Box.createRigidArea(new Dimension(0, 5)));

		//-------------- testing cellField
		Point p = game.getBoard().getDimensions();
		int xButtons = (int) p.getX();
		int yButtons = (int) p.getY();

//		JButton a = new JButton(imageIcon);
		JPanel cellField = new JPanel(new GridLayout(0, xButtons, 0, 0));
//		this.setLayout(new GridLayout(0, xButtons, 0, 0));
		for (int i = 0; i < yButtons; i++)
		{
			for (int j = 0; j < xButtons; j++)
			{
				JLabel a = new CellIcon(
						game.getBoard().getCells().get(i).get(j));
//				ImageIcon imageIcon = new ImageIcon(Global.IMAGE_PATH + "flag1.png");
//				a.setIcon(imageIcon);
				cellField.add(a);
			}
		}
		cellField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.WHITE, Color.DARK_GRAY));
//		this.add(cellField, BorderLayout.CENTER);
		this.add(cellField);
		System.out.println("GamePanel: cellField size: " + cellField.getVisibleRect());

		//-------------- testing cellField
		this.setBorder(new LineBorder(new Color(192, 192, 192), 6));
	}

	@Override
	public void tileChanged()
	{
		// TODO Auto-generated method stub (GamePanel.tileChanged())
		System.out.println(game.getBoard().getRemainingMines() + " mines left");
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
		System.out.println("Time changed to " + game.getSecondsPassed());
		if (minesweeper.enableSound)
		{
			playSound(Global.SOUND_PATH + "tick.mp3");			
		}
	}

	@Override
	public void otherChanged()
	{
		// TODO Auto-generated method stub (GamePanel.otherChanged())
	}

}
