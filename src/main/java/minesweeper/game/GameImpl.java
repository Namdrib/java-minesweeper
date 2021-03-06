package minesweeper.game;

import static minesweeper.util.MinesweeperConstants.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import minesweeper.cell.Cell;
import minesweeper.cell.Cell.CellState;
import minesweeper.cell.CellImpl;
import minesweeper.gamelistener.GameListener;
import minesweeper.util.Util;

/**
 * 
 * @author Namdrib
 *
 */
public class GameImpl implements Game {
  /**
   * Used to switch upon when alerting listeners
   * 
   * @author Namdrib
   *
   */
  public enum GameChangeType {
    LOSE, // tile change (something to do with the board)
    WIN, // other changes
    TICK, // timing change
    FLAG, // Cell flag changes
  }

  Set<GameListener> listeners;
  GameDifficulty difficulty;
  int numMines;
  Point dims;
  List<List<Cell>> cells;
  int secondsPassed;
  boolean started;
  int finished;

  public GameImpl() {
    listeners = new HashSet<>();
    difficulty = GameDifficulty.BEGINNER;
    createBoard(BEGINNER_X, BEGINNER_Y, BEGINNER_MINES);
    secondsPassed = 0;
    started = false;
    finished = 0;
  }

  public GameImpl(GameDifficulty difficulty, int width, int height, int numMines) {
    this();
    this.difficulty = difficulty;
    this.numMines = numMines;

    switch (difficulty) {
      case BEGINNER:
        createBoard(BEGINNER_X, BEGINNER_Y, BEGINNER_MINES);
        break;
      case INTERMEDIATE:
        createBoard(INTERMEDIATE_X, INTERMEDIATE_Y, INTERMEDIATE_MINES);
        break;
      case EXPERT:
        createBoard(EXPERT_X, EXPERT_Y, EXPERT_MINES);
        break;
      case CUSTOM:
        createBoard(width, height, numMines);
        break;
      default:
        break;
    }
    flagChanged();
  }

  public void addListener(GameListener gl) {
    listeners.add(gl);
  }

  public void removeListener(GameListener gl) {
    listeners.remove(gl);
  }

  /**
   * Alerts all listeners by looping through and calling <code>cellChanged()</code>
   */
  public void alertListeners(GameChangeType type) {
    switch (type) {
      case LOSE:
        listeners.stream().forEach(e -> e.gameLose());
        break;
      case WIN:
        listeners.stream().forEach(e -> e.gameWin());
        break;
      case TICK:
        listeners.stream().forEach(e -> e.gameTick());
        break;
      case FLAG:
        listeners.stream().forEach(e -> e.flagChanged());
        break;
      default:
        return;
    }
  }

  // I want to distribute numMines across the board randomly.
  // But then also need to make sure they have the correct co-ordinates
  // and neighbour numbers. Therefore, split this into a three-stage process:
  // 1. Create a blank 2D List of Cells
  // 2. Randomly assign mines to <code>numMines</code> of these Cells
  // 3. Assign numbers for each element in the 2D List based on neighbours
  @Override
  public void createBoard(int width, int height, int numMines) {
    // Clamp the dimensions and mine numbers
    dims = new Point(Util.clamp(width, MIN_DIM_X, MAX_DIM_X),
        Util.clamp(height, MIN_DIM_Y, MAX_DIM_Y));
    int maxMines = (int) ((dims.getX() - 1) * (dims.getY() - 1));
    this.numMines = Util.clamp(numMines, MIN_MINES, maxMines);

    // Step 1
    cells = new ArrayList<>();
    for (int i = 0; i < dims.getY(); i++) {
      List<Cell> oneRow = new ArrayList<>();
      for (int j = 0; j < dims.getX(); j++) {
        oneRow.add(new CellImpl(this, new Point(j, i), false));
      }
      cells.add(oneRow);
    }

    // Step 2
    int minesPlaced = 0;
    while (minesPlaced < this.numMines) {
      int randX = ThreadLocalRandom.current().nextInt((int) dims.getX());
      int randY = ThreadLocalRandom.current().nextInt((int) dims.getY());

      if (!cells.get(randY).get(randX).isMine()) {
        cells.get(randY).get(randX).setNumber(-1);
        minesPlaced++;
      }
    }

    // Step 3
    updateCellNumbers();
  }

  @Override
  public void createBoard(List<List<Boolean>> mines) {
    // Clamp the dimensions and mine numbers
    dims = new Point(
        Util.clamp(mines.get(0).size(), MIN_DIM_X, MAX_DIM_X),
        Util.clamp(mines.size(), MIN_DIM_Y, MAX_DIM_Y));
    int maxMines = (int) ((dims.getX() - 1) * (dims.getY() - 1));
    this.numMines = Util.clamp(numMines, MIN_MINES, maxMines);

    // Create Cells based on mines
    cells = new ArrayList<>();
    for (int i = 0; i < dims.getY(); i++) {
      ArrayList<Cell> oneRow = new ArrayList<>();
      for (int j = 0; j < dims.getX(); j++) {
        oneRow.add(new CellImpl(this, new Point(j, i), mines.get(i).get(j)));
      }
      cells.add(oneRow);
    }

    updateCellNumbers();
  }

  /**
   * Update every cell's number according to their neighbouring mines
   * 
   * e.g. if a given cell has two mines around it, its number is two
   */
  void updateCellNumbers() {
    cells.stream().forEach(row -> row.stream().filter(cell -> !cell.isMine()).forEach(cell -> {
      int numNeighbouringMines =
          (int) getNeighboursOf(cell).stream().filter(neighbour -> neighbour.isMine()).count();
      cell.setNumber(numNeighbouringMines);
    }));
  }

  @Override
  public List<List<Cell>> getCells() {
    return cells;
  }

  @Override
  public GameDifficulty getDifficulty() {
    return difficulty;
  }

  @Override
  public void tick() {
    if (started && finished == 0) {
      if (secondsPassed < MAX_SECONDS) {
        secondsPassed++;
        alertListeners(GameChangeType.TICK);
      }
    }
  }

  @Override
  public int getSecondsPassed() {
    return secondsPassed;
  };

  @Override
  public void setFinished() {
    // Already finished, no chance of changing
    if (finished > 0) {
      return;
    }

    // A mine has been opened (lose game)
    int openMines = (int) cells.stream().flatMap(Collection::stream)
        .filter(c -> c.isOpen() && c.isMine()).count();
    if (openMines > 0) {
      finished = 2;
      alertListeners(GameChangeType.LOSE);
      return;
    }

    // The board has been revealed (win game)
    int openCells = (int) cells.stream().flatMap(Collection::stream)
        .filter(c -> c.isOpen() && !c.isMine() && c.getCellState() != CellState.MINE3).count();
    if (openCells == (dims.getX() * dims.getY() - numMines)) {
      finished = 1;
      alertListeners(GameChangeType.WIN);
      return;
    }

    // Nothing so far, not finished
    finished = 0;
  }

  @Override
  public int getFinished() {
    return finished;
  }

  @Override
  public Set<Cell> getNeighboursOf(Cell cell) {
    Set<Cell> neighbours = new HashSet<>();
    Point p = cell.getPoint();

    // Search each adjacent Cell including diagonals
    // Do not include self or out-of-bounds co-ordinates
    for (int n = -1; n < 2; n++) {
      for (int m = -1; m < 2; m++) {
        int newY = (int) p.getY() + n;
        int newX = (int) p.getX() + m;
        if ((n == 0 && m == 0) || newY < 0 || newX < 0 || newY >= dims.getY()
            || newX >= dims.getX()) {
          continue;
        }
        neighbours.add(cells.get(newY).get(newX));
      }
    }

    return neighbours;
  }

  @Override
  public Point getDimensions() {
    return new Point(dims);
  }

  @Override
  public int getNumMines() {
    return numMines;
  }

  @Override
  public int getRemainingMines() {
    // For each row, count the number of flagged Cells. Sum these up
    int numPlacedFlags = cells.stream()
        .mapToInt(b -> (int) b.stream().filter(c -> c.getFlagState() == 1).count()).sum();
    return numMines - numPlacedFlags;
  }

  @Override
  public void relocateMine(Cell cell) {
    System.out.println("Safe click");

    // Relocate the mine
    relocation: for (int i = 0; i < cells.size(); i++) {
      for (int j = 0; j < cells.get(0).size(); j++) {
        Cell temp = cells.get(i).get(j);

        if (!temp.isMine()) {
          // Make temp a mine, update its neighbours counts
          temp.setNumber(-1);
          getNeighboursOf(temp).stream().filter(c -> !c.isMine())
              .forEach(c -> c.setNumber(c.getNumber() + 1));
          break relocation;
        }
      }
    }

    // Update cell number
    cell.setNumber((int) getNeighboursOf(cell).stream().filter(c -> c.isMine()).count());
    // Update neighbours of cell
    getNeighboursOf(cell).stream().filter(c -> !c.isMine())
        .forEach(c -> c.setNumber(c.getNumber() - 1));
  }

  @Override
  public void flagChanged() {
    alertListeners(GameChangeType.FLAG);
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public void start() {
    started = true;
  }

  @Override
  public String toString() {
    String out = new String(String.valueOf(getRemainingMines()) + ", "
        + String.valueOf(secondsPassed) + System.lineSeparator());

    // Surround the cells with an outline to more easily see the bounds
    String horizontalBorder =
        '+' + new String(new char[(int) dims.getX() + 2]).replace('\0', '-') + '+';
    out += horizontalBorder + System.lineSeparator();
    for (List<Cell> row : cells) {
      out += "| ";
      for (Cell cell : row) {
        out += String.valueOf(cell);
      }
      out += " |" + System.lineSeparator();
    }
    out += horizontalBorder;
    return out;
  }
}
