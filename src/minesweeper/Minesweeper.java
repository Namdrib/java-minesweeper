package minesweeper;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;
import minesweeper.Game.GameDifficulty;

/**
 * 
 * Main window for Minesweeper. Handles menu logic and houses the Game and
 * GamePanel.
 * <p>
 * Game settings are changed through this class (e.g. difficulty, sound)
 * 
 * @author Namdrib
 *
 */
public class Minesweeper
{
	/**
	 * Used for timing information.
	 * 
	 * @author Namdrib
	 * @see Minesweeper.Minesweeper(Game game)
	 */
	private static class Ticker extends TimerTask
	{
		Game game;

		public Ticker()
		{
			;
		}

		/**
		 * 
		 * @param game
		 *            a new Game object to connect to. Used when resetting the
		 *            timer when a new Game is created. Otherwise it persists
		 *            ticking the old Game object.
		 */
		public void connect(Game game)
		{
			Ticker.this.game = game;
		}

		public void run()
		{
			game.tick();
		}
	}

	String					version	= "0.5.0";

	// UI elements
	JFrame					frame;
	JMenuBar				menuBar;
	JMenu					menu;
	JMenuItem				menuItem;
	JRadioButtonMenuItem	rbMenuItem;
	JCheckBoxMenuItem		markingMenuItem;
	JCheckBoxMenuItem		colourMenuItem;
	JCheckBoxMenuItem		soundMenuItem;

	// Game stuff
	Game					game;
	GamePanel				gamePanel;
	GameDifficulty			difficulty;				// base on menus
	int						width, height, numMines;

	// Timing stuff
	Timer					timer;
	Ticker					ticker;

	boolean					enableMarking;
	boolean					enableColour;
	boolean					enableSound;

	/**
	 * 
	 * Constructor for Minesweeper class. Connects entities together (Game to
	 * GameListener and vice versa, and Game to Ticker). Creates a Timer object
	 * for the timing information.
	 * 
	 * @param game
	 *            an initialised game object
	 * 
	 * @see Ticker
	 */
	public Minesweeper()
	{
		// Ticker that ticks every second
		timer = new Timer();
		ticker = new Ticker();

		frame = new JFrame("Minesweeper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); // Centre the window
		frame.setIconImage(
				new ImageIcon(Global.IMAGE_PATH + "logo-full-small.png")
						.getImage());
		addMenuThings();

		difficulty = GameDifficulty.BEGINNER;
		resetGame();
		resetGame(); // quick fix for the UI resizing. force resize (shrink) upon start.
		timer.scheduleAtFixedRate(ticker, 0, 1000);
	}

	private JMenuItem createJMenuItem(String name, int mnemonic, String desc)
	{
		JMenuItem out = new JMenuItem(name, mnemonic);
		out.getAccessibleContext().setAccessibleDescription(desc);
		return out;
	}

	private JMenuItem createJMenuItem(String name, int mnemonic, String desc,
			KeyStroke ks)
	{
		JMenuItem out = createJMenuItem(name, mnemonic, desc);
		out.setAccelerator(ks);
		return out;
	}

	private void addMenuThings()
	{
		// Set up the menu things
		menuBar = new JMenuBar();
		menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_G);
		menu.getAccessibleContext().setAccessibleDescription("Game");
		menuBar.add(menu);

		// File -> New : Starts a new game
		menuItem = createJMenuItem("New", KeyEvent.VK_N,
				"Start anew. Discards current map",
				KeyStroke.getKeyStroke("F2"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("New game!");
				resetGame();
			}
		});
		menu.add(menuItem);

		menu.addSeparator();
		ButtonGroup difficultyRadioGroup = new ButtonGroup();

		// File -> Beginner : Starts a new beginner game
		rbMenuItem = new JRadioButtonMenuItem("Beginner");
		rbMenuItem.setMnemonic(KeyEvent.VK_B);
		rbMenuItem.setSelected(true);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Beginner difficulty");
				difficulty = GameDifficulty.BEGINNER;
				resetGame();
			}
		});
		difficultyRadioGroup.add(rbMenuItem);
		menu.add(rbMenuItem);

		// File -> Intermediate : Starts a new intermediate game
		rbMenuItem = new JRadioButtonMenuItem("Intermediate");
		rbMenuItem.setMnemonic(KeyEvent.VK_I);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Intermediate difficulty");
				difficulty = GameDifficulty.INTERMEDIATE;
				resetGame();
			}
		});
		difficultyRadioGroup.add(rbMenuItem);
		menu.add(rbMenuItem);

		// File -> Expert : Starts a new expert game
		rbMenuItem = new JRadioButtonMenuItem("Expert");
		rbMenuItem.setMnemonic(KeyEvent.VK_E);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Expert difficulty");
				difficulty = GameDifficulty.EXPERT;
				resetGame();
			}
		});
		difficultyRadioGroup.add(rbMenuItem);
		menu.add(rbMenuItem);

		// File -> Custom... : Bring up options for a new custom game
		rbMenuItem = new JRadioButtonMenuItem("Custom...");
		rbMenuItem.setMnemonic(KeyEvent.VK_C);
		rbMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Custom difficulty");

				// User dialogue for: "Height", "Width" and "Mines"
				// Existing values
				Point p = game.getDimensions();
				int newHeight = (int) p.getY();
				int newWidth = (int) p.getX();
				int newMines = game.getNumMines();

				JTextField heightField = new JTextField(
						String.valueOf(newHeight));
				JTextField widthField = new JTextField(
						String.valueOf(newWidth));
				JTextField minesField = new JTextField(
						String.valueOf(newMines));

				Object[] prompts = { "Height:", heightField, "Width:",
						widthField, "Mines:", minesField };
				int result = JOptionPane.showConfirmDialog(null, prompts,
						"Custom Field", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
				{
					try
					{
						newHeight = Integer.parseInt(heightField.getText());
						newWidth = Integer.parseInt(widthField.getText());
						newMines = Integer.parseInt(minesField.getText());
					}
					catch (NumberFormatException ex)
					{
						return;
					}

					difficulty = GameDifficulty.CUSTOM;
					height = newHeight;
					width = newWidth;
					numMines = newMines;
					resetGame();
				}
			}
		});
		difficultyRadioGroup.add(rbMenuItem);
		menu.add(rbMenuItem);

		menu.addSeparator();

		// File -> Marks (?) : Toggle use of marks (?)
		markingMenuItem = new JCheckBoxMenuItem("Marks (?)");
		markingMenuItem.setMnemonic(KeyEvent.VK_M);
		markingMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				game.getCells().stream().forEach(r -> r.stream().forEach(
						c -> c.setMarking(markingMenuItem.isSelected())));
				System.out.println("Toggle marks: " + enableMarking);
			}
		});
		markingMenuItem.setSelected(true);
		menu.add(markingMenuItem);

		// File -> Colour : Toggle colour
		colourMenuItem = new JCheckBoxMenuItem("Colour");
		colourMenuItem.setMnemonic(KeyEvent.VK_L);
		colourMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// TODO
				enableColour = colourMenuItem.isSelected();
				System.out.println("Toggle colour: " + enableColour);
			}
		});
		colourMenuItem.setSelected(true);
		menu.add(colourMenuItem);

		// File -> Sound : Toggle sound
		soundMenuItem = new JCheckBoxMenuItem("Sound");
		soundMenuItem.setMnemonic(KeyEvent.VK_S);
		soundMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// TODO
				enableSound = soundMenuItem.isSelected();
				System.out.println("Toggle sound: " + enableSound);
			}
		});
		soundMenuItem.setSelected(false);
		menu.add(soundMenuItem);

		menu.addSeparator();

		// File -> Best Times... : Shows high scores / best times
		menuItem = createJMenuItem("Best Times...", KeyEvent.VK_T,
				"Shows high scores / best times");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// TODO
				System.out.println("Best Times...");
				showBestTimes();
			}
		});
		menu.add(menuItem);

		menu.addSeparator();

		// File -> Exit : Close the window
		menuItem = createJMenuItem("Exit", KeyEvent.VK_X, "Close the window");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Exit");
				System.exit(0);
			}
		});
		menu.add(menuItem);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help");
		menuBar.add(menu);

		// Help -> About Minesweeper... : Show information
		menuItem = createJMenuItem("About Minesweeper...", KeyEvent.VK_A,
				"Show information");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// TODO
				System.out.println("About Minesweeper...");
				showAbout();
			}
		});
		menu.add(menuItem);
		frame.setJMenuBar(menuBar);
	}

	/**
	 * Start a new game (and reload associated GUI elements) with the
	 * currently-saved settings (difficulty, width, height, numMines)
	 * <p>
	 * Handles all the frame sizing
	 */
	public void resetGame()
	{
		System.out.println("RESET!");
		try
		{
			frame.remove(gamePanel);
		}
		catch (NullPointerException ex)
		{
			System.out.println("Can't remove gamePanel");
		}

		game = new GameImpl(difficulty, width, height, numMines);
		gamePanel = new GamePanel(game, this);
		game.addListener(gamePanel);

		ticker.connect(game);
		frame.add(gamePanel, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	// TODO : Write best times to a file? Or keep locally
	private void showBestTimes()
	{
		;
	}

	private void showAbout()
	{
		String out = "Java port of Windows XP Minesweeper by Namdrib (version "
				+ version + ")" + System.lineSeparator()
				+ "See https://github.com/Namdrib/java-minesweeper for more information";
		JOptionPane.showMessageDialog(null, out, "About Minesweeper",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args)
	{
		// This wrapper prevents an exception from unknown source
		// https://stackoverflow.com/questions/37832170/java-exception-in-thread-awt-eventqueue-0-java-lang-classcastexception#37832926
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				// this makes sounds play by initialising a thing
				// https://stackoverflow.com/questions/14025718/javafx-toolkit-not-initialized-when-trying-to-play-an-mp3-file-through-mediap
				@SuppressWarnings("unused") final JFXPanel fxPanel = new JFXPanel();
				@SuppressWarnings("unused") Minesweeper minesweeper = new Minesweeper();
			}
		});
	}
}
