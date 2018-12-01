package minesweeper;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javafx.embed.swing.JFXPanel;
import minesweeper.game.Game;
import minesweeper.game.Game.GameDifficulty;
import minesweeper.game.GameImpl;
import minesweeper.gameio.GameIO;
import minesweeper.gameio.GameReaderWriter;
import minesweeper.gamelistener.GamePanel;
import minesweeper.persistence.Persistence;
import minesweeper.persistence.SqlitePersistence;
import minesweeper.util.Global;

/**
 * 
 * Main window for Minesweeper. Handles menu logic and houses the Game and GamePanel.
 * <p>
 * Game settings are changed through this class (e.g. difficulty, sound)
 * 
 * @author Namdrib
 *
 */
public class Minesweeper {
  /**
   * Used for timing information.
   * 
   * @author Namdrib
   * @see Minesweeper.Minesweeper(Game game)
   */
  private static class Ticker extends TimerTask {
    Game game;

    public Ticker() {
      ;
    }

    /**
     * 
     * @param game a new Game object to connect to. Used when resetting the timer when a new Game is
     *        created. Otherwise it persists ticking the old Game object.
     */
    public void connect(Game game) {
      Ticker.this.game = game;
    }

    public void run() {
      game.tick();
    }
  }

  GameIO gio;

  String version = "0.5.0";

  // UI elements
  JFrame frame;
  JCheckBoxMenuItem markingMenuItem;
  JCheckBoxMenuItem colourMenuItem;
  JCheckBoxMenuItem soundMenuItem;

  // Game stuff
  Game game;
  GamePanel gamePanel;

  // Timing stuff
  Timer timer;
  Ticker ticker;

  // Persistence stuff
  public Persistence persistence;
  String dbPath;

  /**
   * 
   * Constructor for Minesweeper class. Connects entities together (Game to GameListener and vice
   * versa, and Game to Ticker). Creates a Timer object for the timing information.
   * 
   * @param game an initialised game object
   * 
   * @see Ticker
   */
  public Minesweeper(GameIO gio) {

    // Set up persistence stuff
    String path = "a.sqlite";
    dbPath = "jdbc:sqlite:" + path;

    persistence = new SqlitePersistence(dbPath);

    // Initialise the db if the db file doesn't exist
    // create and set default values
    File f = new File(path);
    if (!f.isFile()) {
      persistence.initDB();
      System.out.println("Init db to " + persistence);
    }

    if (!persistence.loadDB()) {
      System.out.println("Couldn't load db. Loading default values.");
      persistence.resetTimes();
      persistence.saveDB();
    }

    System.out.println("Finished loading: " + persistence);

    this.gio = gio;

    // Ticker that ticks every second
    timer = new Timer();
    ticker = new Ticker();

    frame = new JFrame("Minesweeper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null); // Centre the window
    frame.setIconImage(new ImageIcon(Global.IMAGE_PATH + "logo-full-small.png").getImage());
    addMenuThings();

    resetGame();
    resetGame(); // quick fix for the UI resizing. force resize (shrink) upon start.
    timer.scheduleAtFixedRate(ticker, 0, 1000);
  }

  private JMenuItem createJMenuItem(String name, int mnemonic, String desc) {
    JMenuItem out = new JMenuItem(name, mnemonic);
    out.getAccessibleContext().setAccessibleDescription(desc);
    return out;
  }

  private JMenuItem createJMenuItem(String name, int mnemonic, String desc, KeyStroke ks) {
    JMenuItem out = createJMenuItem(name, mnemonic, desc);
    out.setAccelerator(ks);
    return out;
  }

  private void addMenuThings() {
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;

    // Set up the menu things
    menuBar = new JMenuBar();
    menu = new JMenu("Game");
    menu.setMnemonic(KeyEvent.VK_G);
    menu.getAccessibleContext().setAccessibleDescription("Game");
    menuBar.add(menu);

    // File -> New : Starts a new game
    menuItem =
        createJMenuItem("New", KeyEvent.VK_N, "Start a new game", KeyStroke.getKeyStroke("F2"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        resetGame();
      }
    });
    menu.add(menuItem);

    menu.addSeparator();
    menuItem = createJMenuItem("Open...", KeyEvent.VK_O, "Open a game from file",
        KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        open();
      }
    });
    menu.add(menuItem);

    // File -> Save as...
    menuItem = createJMenuItem("Save as...", KeyEvent.VK_S, "Save current game to file",
        KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAs();
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
      public void actionPerformed(ActionEvent ae) {
        persistence.setLastDifficulty(GameDifficulty.BEGINNER, 9, 9, 10);
        resetGame();
      }
    });
    difficultyRadioGroup.add(rbMenuItem);
    menu.add(rbMenuItem);

    // File -> Intermediate : Starts a new intermediate game
    rbMenuItem = new JRadioButtonMenuItem("Intermediate");
    rbMenuItem.setMnemonic(KeyEvent.VK_I);
    rbMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        persistence.setLastDifficulty(GameDifficulty.INTERMEDIATE, 16, 16, 40);
        resetGame();
      }
    });
    difficultyRadioGroup.add(rbMenuItem);
    menu.add(rbMenuItem);

    // File -> Expert : Starts a new expert game
    rbMenuItem = new JRadioButtonMenuItem("Expert");
    rbMenuItem.setMnemonic(KeyEvent.VK_E);
    rbMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        persistence.setLastDifficulty(GameDifficulty.EXPERT, 30, 16, 99);
        resetGame();
      }
    });
    difficultyRadioGroup.add(rbMenuItem);
    menu.add(rbMenuItem);

    // File -> Custom... : Bring up options for a new custom game
    rbMenuItem = new JRadioButtonMenuItem("Custom...");
    rbMenuItem.setMnemonic(KeyEvent.VK_C);
    rbMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        // User dialogue for: "Height", "Width" and "Mines"
        // Existing values
        Point p = game.getDimensions();
        int newHeight = (int) p.getY();
        int newWidth = (int) p.getX();
        int newMines = game.getNumMines();

        JTextField heightField = new JTextField(String.valueOf(newHeight));
        JTextField widthField = new JTextField(String.valueOf(newWidth));
        JTextField minesField = new JTextField(String.valueOf(newMines));

        Object[] prompts = {"Height:", heightField, "Width:", widthField, "Mines:", minesField};
        int result = JOptionPane.showConfirmDialog(null, prompts, "Custom Field",
            JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
          try {
            newHeight = Integer.parseInt(heightField.getText());
            newWidth = Integer.parseInt(widthField.getText());
            newMines = Integer.parseInt(minesField.getText());
          } catch (NumberFormatException ex) {
            return;
          }

          persistence.setLastDifficulty(GameDifficulty.CUSTOM, newHeight, newWidth, newMines);
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
      public void actionPerformed(ActionEvent ae) {
        game.getCells().stream()
            .forEach(r -> r.stream().forEach(c -> c.setMarking(markingMenuItem.isSelected())));
        persistence.setMarks(markingMenuItem.isSelected());
      }
    });
    markingMenuItem.setSelected(persistence.getMarks());
    menu.add(markingMenuItem);

    // File -> Colour : Toggle colour
    colourMenuItem = new JCheckBoxMenuItem("Colour");
    colourMenuItem.setMnemonic(KeyEvent.VK_L);
    colourMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        // TODO : colour
        persistence.setColour(colourMenuItem.isSelected());
      }
    });
    colourMenuItem.setSelected(persistence.getColour());
    menu.add(colourMenuItem);

    // File -> Sound : Toggle sound
    soundMenuItem = new JCheckBoxMenuItem("Sound");
    soundMenuItem.setMnemonic(KeyEvent.VK_S);
    soundMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        persistence.setSound(soundMenuItem.isSelected());
      }
    });
    soundMenuItem.setSelected(persistence.getColour());
    menu.add(soundMenuItem);

    menu.addSeparator();

    // File -> Best Times... : Shows best times
    menuItem = createJMenuItem("Best Times...", KeyEvent.VK_T, "Shows best times");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        showBestTimes();
      }
    });
    menu.add(menuItem);

    menu.addSeparator();

    // File -> Exit : Close the window
    menuItem = createJMenuItem("Exit", KeyEvent.VK_X, "Close the window");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
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
    menuItem = createJMenuItem("About Minesweeper...", KeyEvent.VK_A, "Show information");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        System.out.println("About Minesweeper...");
        showAbout();
      }
    });
    menu.add(menuItem);
    frame.setJMenuBar(menuBar);
  }

  /**
   * Start a new game (and reload associated GUI elements) with the currently-saved settings
   * (difficulty, width, height, numMines)
   * <p>
   * Handles all the frame sizing
   */
  public void resetGame() {
    System.out.println("RESET!");
    try {
      frame.remove(gamePanel);
    } catch (NullPointerException ex) {
      System.out.println("Can't remove gamePanel");
    }

    game = new GameImpl(persistence.getLastDifficulty(), persistence.getLastX(),
        persistence.getLastY(), persistence.getLastMines());
    gamePanel = new GamePanel(game, this);
    game.addListener(gamePanel);

    ticker.connect(game);
    frame.add(gamePanel, BorderLayout.CENTER);
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
  }

  // File -> Open... (Ctrl-O)
  private void open() {
    JFileChooser fc = new JFileChooser(Global.GAMES_PATH);
    int ret = fc.showOpenDialog(null);
    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      System.out.println("Want to read \"" + file + "\"");
      // Do a thing with file
      try (Reader r = new FileReader(file)) {
        gio.readMines(r, game);
        System.out.println("Read \"" + file + "\" successful!");
      } catch (Exception ex) {
        ;
      } finally {
        frame.validate();
        frame.repaint();
      }
    }
  }

  // File -> Save As... (Ctrl-S)
  private void saveAs() {
    JFileChooser fc = new JFileChooser(Global.GAMES_PATH);
    int ret = fc.showSaveDialog(null);
    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      System.out.println("Want to save to \"" + file + "\"");
      try (Writer w = new FileWriter(file)) {
        gio.writeMines(w, game);
      } catch (Exception ex) {
        ;
      } finally {
        frame.validate();
        frame.repaint();
      }
    }
  }

  /**
   * The text is to be used in a JLabel's text field, so to display new lines, requires use of HTML
   * tags with <code><br></code> to emulate new lines.
   * 
   * @return a HTML string representing the best scores as above.
   */
  private String getBestTimesText() {
    Map<GameDifficulty, Integer> bestTimes = persistence.getBestTimes();
    Map<GameDifficulty, String> bestNames = persistence.getBestNames();

    StringBuilder sb = new StringBuilder();
    sb.append("<html><table>");
    for (GameDifficulty gd : GameDifficulty.values()) {
      if (gd != GameDifficulty.CUSTOM) {
        String difficultyStr =
            gd.toString().substring(0, 1).toUpperCase() + gd.toString().substring(1).toLowerCase();
        sb.append("<tr><td>" + difficultyStr + ":</td>");
        sb.append("<td>" + bestTimes.get(gd) + " seconds</td>");
        sb.append("<td>" + bestNames.get(gd) + "</td></tr>");
      }
    }
    sb.append("</table></html>");
    return sb.toString();
  }

  /**
   * Show the best times. Display two buttons: "Reset Scores" and "OK". Reset Scores should return
   * each best time/name combination to the default and update the display.
   */
  private void showBestTimes() {
    JLabel text = new JLabel(getBestTimesText(), JLabel.CENTER);
    JButton resetButton = new JButton("Reset Scores");
    resetButton.setEnabled(true);
    resetButton.addActionListener(new ActionListener() {
      // Setting the JLabel's text repaints it
      @Override
      public void actionPerformed(ActionEvent ae) {
        persistence.resetTimes();
        text.setText(getBestTimesText());
      }
    });

    Object[] options = {resetButton, "OK"};
    JOptionPane.showOptionDialog(frame, text, "Fastest Mine Sweepers", JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
  }

  /**
   * Check whether the time taken and difficulty of the current game is the fastest for its
   * difficulty. If so, prompt for the user to enter a name and record the score. Then display the
   * best time.
   * 
   * @param timeTaken
   */
  public void checkBestTime() {
    // Only proceed if the game is won
    if (game.getFinished() != 1) {
      System.out.println("CheckBestTime: not finished");
      return;
    }

    GameDifficulty difficulty = game.getDifficulty();
    int time = game.getSecondsPassed();
    if (difficulty == GameDifficulty.CUSTOM || time >= persistence.getBestTimes().get(difficulty)) {
      System.out.println("CheckBestTime: diff or time");
      return;
    }

    // Create prompt for time. Just ask for name
    // TODO : Have so only 1 prompt button "OK"
    String prompt = "You have the fastest time for " + difficulty.toString().toLowerCase()
        + " level.\nPlease enter your name.\n\n";
    String s = (String) JOptionPane.showInputDialog(frame, prompt, null, JOptionPane.PLAIN_MESSAGE,
        null, null, persistence.getBestNames().get(difficulty));
    if (s != null) {
      persistence.setBestTime(difficulty, time, s);
    }
    showBestTimes();
  }

  private void showAbout() {
    String out = "Java port of Windows XP Minesweeper by Namdrib (version " + version + ")"
        + System.lineSeparator()
        + "See https://github.com/Namdrib/java-minesweeper for more information";
    JOptionPane.showMessageDialog(null, out, "About Minesweeper", JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");

    // This wrapper prevents an exception from unknown source
    // https://stackoverflow.com/questions/37832170/java-exception-in-thread-awt-eventqueue-0-java-lang-classcastexception#37832926
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {

        // Set the look and feel of the Swing UI
        try {
          // Set to System L&F
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
          // handle exception
        } catch (ClassNotFoundException e) {
          // handle exception
        } catch (InstantiationException e) {
          // handle exception
        } catch (IllegalAccessException e) {
          // handle exception
        }

        // this makes sounds play by initialising a thing
        // https://stackoverflow.com/questions/14025718/javafx-toolkit-not-initialized-when-trying-to-play-an-mp3-file-through-mediap
        @SuppressWarnings("unused")
        final JFXPanel fxPanel = new JFXPanel();
        @SuppressWarnings("unused")
        Minesweeper minesweeper = new Minesweeper(new GameReaderWriter());
      }
    });
  }
}
