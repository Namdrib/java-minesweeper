package minesweeper.gamelistener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collection;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import minesweeper.Minesweeper;
import minesweeper.celllistener.CellIcon;
import minesweeper.game.Game;
import minesweeper.util.Global;
import minesweeper.util.ThickBevelBorder;

/**
 * 
 * @author Namdrib
 *
 */
public class GamePanel extends JPanel implements GameListener {
  /**
   * 
   */
  private static final long serialVersionUID = 9163587486511740720L;
  Minesweeper minesweeper;
  Game game;
  GamePanelMouseListener l;

  JPanel numMinePanel;
  JLabel[] numMineLabels;
  ImageIcon[] numMineIcons;

  Face face;

  JPanel timerPanel;
  JLabel[] timerLabels;
  ImageIcon[] timerIcons;

  private class Face extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = 5160499669072181398L;

    public Face() {
      ImageIcon faceIcon = new ImageIcon(Global.IMAGE_PATH + "face-normal.png");
      this.setIcon(faceIcon);
      this.addMouseListener(new MouseListener() {
        boolean pressed = false;
        boolean wasPressed = false;

        private void pressFaceIcon() {
          Face.this.setIcon(new ImageIcon(Global.IMAGE_PATH + "face-pressed.png"));
        }

        @Override
        public void mouseClicked(MouseEvent me) {
          ;
        }

        @Override
        public void mousePressed(MouseEvent me) {
          if (me.getButton() == MouseEvent.BUTTON1) {
            pressed = true;
            wasPressed = true;
            pressFaceIcon();
          }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
          if (pressed) {
            System.out.println("Face pressed!");
            minesweeper.resetGame();
          }
          pressed = false;
          wasPressed = false;
          resetImageToFaceState();
        }

        @Override
        public void mouseEntered(MouseEvent me) {
          if (wasPressed) {
            pressFaceIcon();
            pressed = true;
          }
        }

        @Override
        public void mouseExited(MouseEvent me) {
          pressed = false;
          resetImageToFaceState();
        }
      });
    }

    /**
     * Set the face icon depending on the current game state
     * <ul>
     * <li>Lose: face-lose
     * <li>Win: face-win
     * <li>Normal: face-normal
     * <ul>
     */
    public void resetImageToFaceState() {
      String iconPath = Global.IMAGE_PATH;
      switch (game.getFinished()) {
        case 0:
          iconPath += "face-normal.png";
          break;

        case 1:
          iconPath += "face-win.png";
          break;

        case 2:
          iconPath += "face-lose.png";
          break;

        default:
          iconPath += "face-normal.png";
          break;
      }
      face.setIcon(new ImageIcon(iconPath));
    }
  }

  // TODO big boi
  private class GamePanelMouseListener extends MouseInputAdapter {
    boolean pressed = false;
    boolean wasPressed = false;

    @Override
    public void mousePressed(MouseEvent me) {
      if (me.getButton() == MouseEvent.BUTTON1) {
        System.out.println("GamePanel: mouse pressed");
        if (game.getFinished() > 0) {
          return;
        }
        pressed = true;
        wasPressed = true;
        face.setIcon(new ImageIcon(Global.IMAGE_PATH + "face-click.png"));
      }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
      System.out.println("GamePanel: mouse clicked");
    }

    @Override
    public void mouseReleased(MouseEvent me) {
      System.out.println("GamePanel: mouse released");
      if (pressed) {
        System.out.println("Face pressed!");
      }
      pressed = false;
      wasPressed = false;
    }
  }

  public GamePanel() {
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    l = new GamePanelMouseListener();
    addMouseListener(l);
    addMouseMotionListener(l);

    numMineLabels = new JLabel[3];
    timerLabels = new JLabel[3];
    for (int i = 0; i < 3; i++) {
      numMineLabels[i] = new JLabel();
      timerLabels[i] = new JLabel();
    }
  }

  public GamePanel(Game game, Minesweeper minesweeper) {
    this();
    this.game = game;
    this.minesweeper = minesweeper;

    // Create HUD
    // add remaining mines (LINE_START)
    numMinePanel = new JPanel();
    numMinePanel.setLayout(new BoxLayout(numMinePanel, BoxLayout.LINE_AXIS));
    for (int i = 0; i < numMineLabels.length; i++) {
      numMineLabels[i] = new JLabel(new ImageIcon(Global.IMAGE_PATH + "hud0.png"));
      numMinePanel.add(numMineLabels[i]);
    }
    numMinePanel.setBorder(new ThickBevelBorder(Color.GRAY, Color.WHITE, 1));

    // add face (CENTER)
    face = new Face();

    // add timer (LINE_END)
    timerPanel = new JPanel();
    timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.LINE_AXIS));
    for (int i = 0; i < timerLabels.length; i++) {
      timerLabels[i] = new JLabel(new ImageIcon(Global.IMAGE_PATH + "hud0.png"));
      timerPanel.add(timerLabels[i]);
    }
    timerPanel.setBorder(new ThickBevelBorder(Color.GRAY, Color.WHITE, 1));

    // Add everything to the HUD
    JPanel hud = new JPanel();
    hud.setLayout(new BoxLayout(hud, BoxLayout.LINE_AXIS));
    hud.add(numMinePanel);
    hud.add(Box.createHorizontalGlue());
    hud.add(face);
    hud.add(Box.createHorizontalGlue());
    hud.add(timerPanel);
    hud.setBackground(new Color(192, 192, 192));

    // Add a thing around the hud so there's some space
    JPanel hudContainer = new JPanel();
    hudContainer.setBackground(Color.LIGHT_GRAY);
    hudContainer.setLayout(new BorderLayout());
    hudContainer.add(Box.createRigidArea(new Dimension(0, 4)), BorderLayout.NORTH);
    hudContainer.add(Box.createRigidArea(new Dimension(0, 4)), BorderLayout.SOUTH);
    hudContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    hudContainer.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    hudContainer.add(hud, BorderLayout.CENTER);
    hudContainer.setBorder(new ThickBevelBorder(Color.GRAY, Color.WHITE, 2));
    this.add(hudContainer);

    this.add(Box.createRigidArea(new Dimension(0, 5)));

    // Create cell field
    Point p = game.getDimensions();
    int xButtons = (int) p.getX();
    int yButtons = (int) p.getY();

    JPanel cellField = new JPanel(new GridLayout(0, xButtons, 0, 0));
    for (int i = 0; i < yButtons; i++) {
      for (int j = 0; j < xButtons; j++) {
        JLabel a = new CellIcon(game.getCells().get(i).get(j));
        cellField.add(a);
      }
    }
    cellField.setBorder(new ThickBevelBorder(Color.GRAY, Color.WHITE, 3));
    this.add(cellField);
    flagChanged();

    this.setBorder(new LineBorder(new Color(192, 192, 192), 6));
  }

  // Path must be relative to the project folder,
  // not from this particular file
  private static void playSound(String s) {
    try {
      Media hit = new Media(new File(s).toURI().toString());
      MediaPlayer mediaPlayer = new MediaPlayer(hit);
      mediaPlayer.play();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void gameLose() {
    if (minesweeper.enableSound) {
      playSound(Global.SOUND_PATH + "lose.mp3");
    }
    face.setIcon(new ImageIcon(Global.IMAGE_PATH + "face-lose.png"));
  }

  @Override
  public void gameWin() {
    if (minesweeper.enableSound) {
      playSound(Global.SOUND_PATH + "win.mp3");
    }
    face.setIcon(new ImageIcon(Global.IMAGE_PATH + "face-win.png"));

    // Flag all non-flagged mines
    game.getCells().stream().flatMap(Collection::stream)
        .filter(c -> c.isMine() && (c.getFlagState() != 1)).forEach(c -> c.setFlag());

    minesweeper.checkBestTime();
  }

  @Override
  public void gameTick() {
    String s = String.format("%03d", game.getSecondsPassed());
    if (minesweeper.enableSound) {
      playSound(Global.SOUND_PATH + "tick.mp3");
    }
    for (int i = 0; i < s.length(); i++) {
      String img = Global.IMAGE_PATH + "hud" + s.charAt(i) + ".png";
      timerLabels[i].setIcon(new ImageIcon(img));
    }
  }

  @Override
  public void flagChanged() {
    String s = String.format("%03d", game.getRemainingMines());
    for (int i = 0; i < s.length(); i++) {
      String img = Global.IMAGE_PATH + "hud" + s.charAt(i) + ".png";
      numMineLabels[i].setIcon(new ImageIcon(img));
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    g.setColor(new Color(192, 192, 192));
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
  }
}
