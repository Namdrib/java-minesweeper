package minesweeper.gameio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import minesweeper.cell.Cell;
import minesweeper.game.Game;

public class GameReaderWriter implements GameIO {

  @SuppressWarnings("null")
  @Override
  public void readMines(Reader r, Game g) throws IOException, GameFormatException {
    // TODO Auto-generated method stub
    try (BufferedReader br = new BufferedReader(r)) {
      String line;
      int x = -1, y = -1, n = -1;
      List<List<Boolean>> mines = null;
      for (int lineNumber = 0; (line = br.readLine()) != null; lineNumber++) {
        // Ignore empty lines
        if (line.trim().isEmpty()) {
          continue;
        }

        // Ignore comments
        if (line.charAt(0) == '#') {
          continue;
        }

        // Read x/y
        if (x == -1 && y == -1) {
          String[] coords = line.split(" ");
          try {
            x = Integer.parseInt(coords[0]);
            if (x < 9 || x > 30) {
              throw new GameFormatException(lineNumber,
                  "GameReaderWriter.readMines(): x: not in bounds [9, 30]. Found " + x);
            }
          } catch (NumberFormatException ex) {
            throw new GameFormatException(lineNumber,
                "GameReaderWriter.readMines(): x: not integral. Found " + coords[0]);
          }
          try {
            y = Integer.parseInt(coords[1]);
            if (y < 9 || y > 24) {
              throw new GameFormatException(lineNumber,
                  "GameReaderWriter.readMines(): y: not in bounds [9, 24]. Found " + y);
            }
          } catch (NumberFormatException ex) {
            throw new GameFormatException(lineNumber,
                "GameReaderWriter.readMines(): y: not integral. Found " + coords[1]);
          }

          // Initialise mines
          mines = new ArrayList<>();
          for (int i = 0; i < y; i++) {
            List<Boolean> row = new ArrayList<>();
            for (int j = 0; j < x; j++) {
              row.add(false);
            }
            mines.add(row);
          }
          continue;
        }

        // Read n
        if (n == -1) {
          try {
            n = Integer.parseInt(line);
            if (n < 10 || n > (x - 1) * (y - 1)) {
              throw new GameFormatException(lineNumber,
                  "GameReaderWriter.readMines(): n: not in bounds [10, " + (x - 1) * (y - 1)
                      + "]. Found " + n);

            }
          } catch (NumberFormatException ex) {
            throw new GameFormatException(lineNumber,
                "GameReaderWriter.readMines(): n: not integral. Found " + line);
          }
          continue;
        }

        if (x == -1 || y == -1 || n == -1) {
          throw new GameFormatException(lineNumber,
              "GameReaderWriter.readMines(): Must read x, y and n before mine locations");
        }

        String[] coords = line.split(" ");
        System.out.println("Got " + Arrays.toString(coords) + " on line " + lineNumber);
        int xCoord, yCoord;
        try {
          xCoord = Integer.parseInt(coords[0]);
        } catch (NumberFormatException ex) {
          throw new GameFormatException(lineNumber,
              "GameReaderWriter.readMines(): xi: not integral. Found " + coords[0]);
        }
        try {
          yCoord = Integer.parseInt(coords[1]);
        } catch (NumberFormatException ex) {
          throw new GameFormatException(lineNumber,
              "GameReaderWriter.readMines(): yi: not integral. Found " + coords[0]);
        }
        mines.get(yCoord).set(xCoord, true);
      }
      g.createBoard(mines);
    }
  }

  @Override
  public void readGame(Reader r, Game g) throws IOException, GameFormatException {
    // TODO Auto-generated method stub

  }

  @Override
  public void writeMines(Writer w, Game g) throws IOException {
    List<List<Cell>> cells = g.getCells();
    int x = cells.get(0).size();
    int y = cells.size();
    w.write("# x y\n" + x + " " + y + "\n");

    w.write("# n\n" + g.getNumMines());

    w.write("# mine co-ords\n");
    for (List<Cell> row : cells) {
      for (Cell cell : row) {
        if (cell.isMine()) {
          int xCoord = (int) cell.getPoint().getX();
          int yCoord = (int) cell.getPoint().getY();
          w.write(xCoord + " " + yCoord);
        }
      }
    }
  }

  @Override
  public void writeGame(Writer w, Game g) throws IOException {
    // TODO Auto-generated method stub
  }
}
