package minesweeper.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import minesweeper.game.Game;
import minesweeper.game.Game.GameDifficulty;

public class SqlitePersistence implements Persistence {

  private String dbPath = null;
  private Statement stmt = null;
  private PreparedStatement pstmt = null;
  private ResultSet rs = null;

  // Settings
  boolean marks, colour, sound;
  GameDifficulty lastDifficulty;
  int x, y, mines;

  // Times
  Map<GameDifficulty, Integer> bestTimes;
  Map<GameDifficulty, String> bestNames;

  // Some constants
  final static int defaultTime = 999;
  final static String defaultName = "Anonymous";
  final static String settingsTable = "SETTINGS";
  final static String timesTable = "TIMES";

  public SqlitePersistence() {
    this("a.db");
  }

  public SqlitePersistence(String dbPath) {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    this.dbPath = dbPath;

    marks = true;
    colour = true;
    sound = true;

    // Set as though last were beginner
    lastDifficulty = GameDifficulty.BEGINNER;
    x = y = 9;
    mines = 10;

    bestTimes = new HashMap<>();
    bestNames = new HashMap<>();
    resetTimes();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("----- PRINTING PERSISTENCE -----\n");
    sb.append("marks: " + marks + ", colour: " + colour + ", sound: " + sound + "\n");
    sb.append("Diff: " + lastDifficulty.toString() + ": " + x + ", " + y + ", " + mines + "\n");
    sb.append("Times: " + bestTimes.toString() + "\n");
    sb.append("Names: " + bestNames.toString() + "\n");
    sb.append("----- DONE -----\n");

    return sb.toString();
  }

  @Override
  public void resetTimes() {
    for (Game.GameDifficulty gd : Game.GameDifficulty.values()) {
      if (gd.equals(Game.GameDifficulty.CUSTOM))
        continue;

      bestTimes.put(gd, defaultTime);
      bestNames.put(gd, defaultName);
    }
  }

  @Override
  public boolean initDB() {
    String dbURL = dbPath;

    try (Connection connection = DriverManager.getConnection(dbURL)) {
      // Create settings table
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE IF EXISTS SETTINGS");
      stmt.executeUpdate(
          "CREATE TABLE SETTINGS (DIFFICULTY TEXT, X INTEGER, Y INTEGER, MINES INTEGER, MARKS INTEGER, COLOUR INTEGER, SOUND INTEGER)");

      // Create times table
      stmt.executeUpdate("DROP TABLE IF EXISTS TIMES");
      stmt.executeUpdate("CREATE TABLE TIMES (DIFFICULTY TEXT, TIME INTEGER, NAME TEXT)");

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean saveDB() {
    String dbURL = dbPath;
    String template;

    try (Connection connection = DriverManager.getConnection(dbURL)) {

      // Write settings
      template = "DELETE FROM SETTINGS";
      pstmt = connection.prepareStatement(template);
      pstmt.executeUpdate();

      // Write in values
      template =
          "INSERT INTO SETTINGS (DIFFICULTY, X, Y, MINES, MARKS, COLOUR, SOUND) values (?,?,?,?,?,?,?)";
      pstmt = connection.prepareStatement(template);

      pstmt.setString(1, lastDifficulty.toString());
      pstmt.setInt(2, x);
      pstmt.setInt(3, y);
      pstmt.setInt(4, mines);
      pstmt.setBoolean(5, marks);
      pstmt.setBoolean(6, colour);
      pstmt.setBoolean(7, sound);
      pstmt.executeUpdate();

      // Write times
      // Clear existing values
      template = "DELETE FROM TIMES";
      pstmt = connection.prepareStatement(template);
      pstmt.executeUpdate();

      // Write in values
      template = "INSERT INTO TIMES (DIFFICULTY, TIME, NAME) values (?,?,?)";
      pstmt = connection.prepareStatement(template);

      // each best time saved
      for (Game.GameDifficulty gd : Game.GameDifficulty.values()) {
        if (gd.equals(Game.GameDifficulty.CUSTOM))
          continue;

        final String difficulty = gd.toString();
        pstmt.setString(1, difficulty);
        pstmt.setInt(2, bestTimes.get(gd));
        pstmt.setString(3, bestNames.get(gd));
        pstmt.executeUpdate();
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean loadDB() {
    String dbURL = dbPath;

    try (Connection connection = DriverManager.getConnection(dbURL)) {
      stmt = connection.createStatement();

      // Read settings
      rs = stmt.executeQuery("SELECT DIFFICULTY, X, Y, MINES, MARKS, COLOUR, SOUND FROM SETTINGS");
      while (rs.next()) {
        lastDifficulty = GameDifficulty.valueOf(rs.getString(1));
        x = rs.getInt(2);
        y = rs.getInt(3);

        mines = rs.getInt(4);
        marks = rs.getBoolean(5);
        colour = rs.getBoolean(6);
        sound = rs.getBoolean(7);
      }

      // Read times
      rs = stmt.executeQuery("SELECT DIFFICULTY, TIME, NAME FROM TIMES");

      while (rs.next()) {
        GameDifficulty difficulty = GameDifficulty.valueOf(rs.getString(1));
        bestTimes.put(difficulty, rs.getInt(2));
        bestNames.put(difficulty, rs.getString(3));
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }


  // GETTERS AND SETTERS

  @Override
  public void setLastDifficulty(GameDifficulty lastDifficulty, int x, int y, int mines) {
    this.lastDifficulty = lastDifficulty;
    this.x = x;
    this.y = y;
    this.mines = mines;
  }

  @Override
  public GameDifficulty getLastDifficulty() {
    return lastDifficulty;
  }

  @Override
  public int getLastX() {
    return x;
  }

  @Override
  public int getLastY() {
    return y;
  }

  @Override
  public int getLastMines() {
    return mines;
  }

  @Override
  public void setMarks(boolean marks) {
    this.marks = marks;
  }

  @Override
  public boolean getMarks() {
    return marks;
  }

  @Override
  public void setColour(boolean colour) {
    this.colour = colour;
  }

  @Override
  public boolean getColour() {
    return colour;
  }

  @Override
  public void setSound(boolean sound) {
    this.sound = sound;
  }

  @Override
  public boolean getSound() {
    return sound;
  }

  @Override
  public void setBestTime(GameDifficulty difficulty, int time, String name) {
    if (time < bestTimes.get(difficulty)) {
      bestTimes.put(difficulty, time);
      bestNames.put(difficulty, name);
    }
  }

  @Override
  public Map<GameDifficulty, Integer> getBestTimes() {
    return new HashMap<>(bestTimes);
  }

  @Override
  public Map<GameDifficulty, String> getBestNames() {
    return new HashMap<>(bestNames);
  }

  public static void main(String[] args) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String path = "a.db";

    SqlitePersistence p = new SqlitePersistence("jdbc:sqlite:" + path);
    p.resetTimes();
    p.initDB();
    p.saveDB();
    p.loadDB();
    p.saveDB();
  }
}
