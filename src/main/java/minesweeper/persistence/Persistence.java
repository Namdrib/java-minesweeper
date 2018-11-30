package minesweeper.persistence;

/**
 * An interface to provide saving and loading functionality of game settings and times
 * 
 * <code>Times</code> should contain all game difficulties (with the exception of
 * Game.GameDifficulty.CUSTOM) and their corresponding best times and text labels.
 * 
 * <code>Settings</code> should contain booleans for the toggle-settings marking, colour and sound,
 * as well as the most recent difficulty. This allows these settings to be extended beyond a single
 * play session. The recent difficulty should also include a count for the x, y and number of mines
 * to remember custom game settings.
 * 
 * The other classes should interact with this interface by setting the values for the
 * settings/times, etc.
 * 
 * @author Namdrib
 *
 */
public interface Persistence {
  /**
   * Resets all the best times to 999 seconds by "Anonymous"
   */
  public void resetTimes();

  /**
   * Create settings and times tables, reset them to default values
   * 
   * The tables should be called "SETTINGS" and "TIMES", respectively
   * 
   * The settings default values should have:
   * <ul>
   * <li>Beginner difficulty, with 9, 9 and 10 for x, y and num mines, respectively
   * <li>Marks as true
   * <li>Colour as true
   * <li>Sound as true
   * </ul>
   * 
   * Since resetTimes() exists, that can be called from within this function This is because
   * resetTimes may be used multiple times without resetting the settings
   * 
   * The times default values should have an entry for each of Game.GameDifficulty.{BEGINNER,
   * INTERMEDIATE, EXPERT}, each with time as 999 and name as "Anonymous"
   * 
   * 
   * @return true iff the database was successfully initialised, false otherwise
   */
  public boolean initialiseDatabase();

  /**
   * Write settings and times to their respective tables in the database file
   * 
   * @return true iff the database was successfully saved, false otherwise
   */
  public boolean saveDB();

  /**
   * Read settings and times from the database file
   * 
   * @return true iff the database was successfully read, false otherwise
   */
  public boolean loadDB();
}
