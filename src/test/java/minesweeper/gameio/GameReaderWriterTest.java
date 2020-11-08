package minesweeper.gameio;

import static org.junit.Assert.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.junit.Before;
import org.junit.Test;
import minesweeper.game.Game;
import minesweeper.game.GameImpl;
import minesweeper.util.Global;

public class GameReaderWriterTest {

  Game g;
  Reader r;
  GameReaderWriter grw;

  @Before
  public void setUp() throws Exception {
    g = new GameImpl();
    grw = new GameReaderWriter();
  }

  @Test(expected = Test.None.class)
  public void testSampleMinesGame() throws IOException, GameFormatException {
    r = new FileReader(Global.GAMES_PATH + "sampleMines.game");
    grw.readMines(r, g);
  }

  @Test(expected = Test.None.class)
  public void testSampleMinesBad() throws IOException, GameFormatException {
    String[] bad = {"BadN", "BadNRange", "BadX", "BadXRange", "BadY", "BadYRange"};
    for (String s : bad) {
      r = new FileReader(Global.GAMES_PATH + "sampleMines" + s + ".game");
      try {
        grw.readMines(r, g);
      } catch (GameFormatException ex) {
        System.err.println(ex.toString());
        continue;
      }
      fail();
    }
  }
}
