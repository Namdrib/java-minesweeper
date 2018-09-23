package minesweeper.celllistener;

import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;
import minesweeper.cell.Cell;
import minesweeper.util.Global;

public class CellIcon extends JLabel implements CellListener {
  /**
   * 
   */
  private static final long serialVersionUID = -8336551768573752434L;

  // TODO : Big boi CellIconMouseListener
  // Use Board.getRemainingMines() for middle click
  private class CellIconMouseListener extends MouseInputAdapter {
    boolean leftDown = false;
    boolean rightDown = false;
    boolean chording = false;
    final int doubleClick = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;

    private void pressCellIcon() {
      CellIcon.this.setIcon(new ImageIcon(Global.IMAGE_PATH + "open0.png"));
    }

    /**
     * A chord action is to open all non-mine squares around a CellIcon's Cell. The opening action
     * should be indirect
     */
    private void chord() {
      if (CellIcon.this.cell.isOpen()) {
        Set<Cell> neighbours = CellIcon.this.cell.getGame().getNeighboursOf(CellIcon.this.cell);
        int neighbouringFlags =
            (int) neighbours.stream().filter(c -> c.getFlagState() == 1).count();
        if (neighbouringFlags == CellIcon.this.cell.getNumber()) {
          neighbours.stream().forEach(c -> c.open(false));
        }
      }
    }

    @Override
    public void mousePressed(MouseEvent me) {
      if (CellIcon.this.cell.getGame().getFinished() > 0) {
        return;
      }
      if (cell.isOpen()) {
        if (me.getModifiersEx() == doubleClick) {
          System.out.println("WE CHORDING!");
          chording = true;
        }
        if (cell.getNumber() > 0) {
          return;
        }
      }

      int btn = me.getButton();
      switch (btn) {
        case MouseEvent.BUTTON1:
          // "push" the Cell down but don't do anything yet
          System.out.println("CellIcon: left pressed " + me.getModifiersEx());
          if (CellIcon.this.cell.getFlagState() == 1) {
            break;
          }
          leftDown = true;
          pressCellIcon();
          break;

        case MouseEvent.BUTTON2:
          System.out.println("CellIcon: middle pressed " + me.getModifiers());
          break;

        case MouseEvent.BUTTON3:
          System.out.println("CellIcon: right pressed " + me.getModifiersEx());
          if (leftDown) {
            chording = true;
          } else {
            cell.toggleFlag();
          }
          rightDown = true;
          break;

        default:
          break;
      }
      validateAndRepaint();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
      if (CellIcon.this.cell.getGame().getFinished() > 0) {
        return;
      }
      System.out.println("CellIcon: mouse clicked");
      validateAndRepaint();
    }

    /**
     * When releasing from a chord, unpress both left and right
     */
    @Override
    public void mouseReleased(MouseEvent me) {
      if (CellIcon.this.cell.getGame().getFinished() > 0) {
        return;
      }
      if (leftDown && (me.getModifiersEx() & MouseEvent.BUTTON1_MASK) != 0) {
        System.out.println("\taasdf");
      }

      int btn = me.getButton();
      switch (btn) {
        case MouseEvent.BUTTON1:
          // "push" the Cell down but don't do anything yet
          System.out.println("CellIcon: release left " + leftDown);
          if (chording) {
            chord();
            chording = false;
            leftDown = false;
            rightDown = false;
          } else if (leftDown) {
            System.out.println("Release left down");
            cell.open(true);
          }
          leftDown = false;
          break;
        case MouseEvent.BUTTON2:
          System.out.println("CellIcon: release middle");
          chord();
          break;
        case MouseEvent.BUTTON3:
          System.out.println("CellIcon: release right");
          if (chording) {
            chord();
            chording = false;
            leftDown = false;
            rightDown = false;
          }
          rightDown = false;
          break;
        default:
          break;
      }
      validateAndRepaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
      if (CellIcon.this.cell.getGame().getFinished() > 0) {
        return;
      }
      System.out.println("Mouse entered " + cell.getPoint());
      if (cell.isOpen() || cell.getFlagState() == 1) {
        return;
      }

      if ((me.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
        System.out.println("CellIcon: enter left " + cell.getPoint());
        leftDown = true;
        pressCellIcon();
        validateAndRepaint();
      }
      if ((me.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
        System.out.println("CellIcon: enter middle");
        pressCellIcon();
        validateAndRepaint();
      }
      if ((me.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
        System.out.println("CellIcon: enter right");
        rightDown = true;
      }
    }

    @Override
    public void mouseExited(MouseEvent me) {
      if (CellIcon.this.cell.getGame().getFinished() > 0) {
        return;
      }
      leftDown = false;
      rightDown = false;
      chording = false;
      if (CellIcon.this.cell.isOpen() || CellIcon.this.cell.getFlagState() == 1) {
        return;
      }

      if (!((me.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
          || !((me.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0)) {
        CellIcon.this.resetImageToCellState();
        System.out.println("CellIcon: Exited: " + leftDown + " | " + rightDown);
        validateAndRepaint();
      }
    }
  }

  Cell cell;
  String imageName;
  int xSize;
  int ySize;

  public CellIcon() {
    CellIconMouseListener listener = new CellIconMouseListener();
    addMouseListener(listener);
    addMouseMotionListener(listener);

    imageName = Global.IMAGE_PATH + "flag0.png";
    this.setIcon(new ImageIcon(imageName));

    validateAndRepaint();
  }

  public CellIcon(Cell cell) {
    this();
    this.cell = cell;
    cell.addListener(this);
    validateAndRepaint();
  }

  private void validateAndRepaint() {
    validate();
    repaint();
  }

  private void resetImageToCellState() {
    this.setIcon(
        new ImageIcon(Global.IMAGE_PATH + cell.getCellState().toString().toLowerCase() + ".png"));
  }

  @Override
  public void cellChanged() {
    // System.err.print("Cell changed: ");
    resetImageToCellState();

    // System.err.println(cell.getPoint() + " to " + getIcon().toString());

    cell.getGame().setFinished();

    validateAndRepaint();
  }

  // paintComponent() absolutely breaks repainting.
  // It results in each button looking like the top-left corner
  // of the entire Minesweeper window (Game, Help menus)
}
