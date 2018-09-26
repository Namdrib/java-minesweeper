package minesweeper.gameio;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import minesweeper.game.Game;
import minesweeper.gameio.GameFormatException;

/**
 * Interface used to handle reading/writing Games
 * 
 * Provides methods for reading and writing the start of a game (i.e where the mines are), and the
 * middle of a game (i.e. where the mines are, and which cells have been opened)
 * 
 * See the associated README file for a description of the representation of a Game
 * 
 * @author Namdrib
 *
 */
public interface GameIO {

  // Read the description of a map from the
  // Reader r, and transfers it to Map, m.
  public void readMines(Reader r, Game g) throws IOException, GameFormatException;

  // Read the description of a map from the
  // Reader r, and transfers it to Map, m.
  public void readGame(Reader r, Game g) throws IOException, GameFormatException;

  // Write a representation of the Map, m, to the Writer w.
  public void writeMines(Writer w, Game g) throws IOException;

  // Write a representation of the Map, m, to the Writer w.
  public void writeGame(Writer w, Game g) throws IOException;
}
