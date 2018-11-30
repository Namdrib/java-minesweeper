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

  private static String dbPath = "";
  private Statement stmt = null;
  private PreparedStatement pstmt = null;
  private ResultSet rs = null;

  // Settings
  GameDifficulty lastDifficulty;
  boolean marks, colour, sound;
  int x, y, mines;

  // Times
  Map<GameDifficulty, Integer> bestTimes;
  Map<GameDifficulty, String> bestNames;

  // Some constants
  final static int defaultTime = 999;
  final static String defaultName = "Anonymous";
  final static String settingsTable = "SETTINGS";
  final static String timesTable = "TIMES";

  SqlitePersistence() {
    marks = true;
    colour = true;
    sound = true;

    x = y = 9;
    mines = 10;

    bestTimes = new HashMap<>();
    bestNames = new HashMap<>();
  }

  @Override
  public void resetTimes() {
    for (Game.GameDifficulty gd : Game.GameDifficulty.values()) {
      if (gd.equals(Game.GameDifficulty.CUSTOM))
        continue;

      bestTimes.put(gd, defaultTime);
      bestNames.put(gd, defaultName);
    }

    saveDB();
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

      pstmt.setString(1, Game.GameDifficulty.BEGINNER.toString());
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
        marks = (rs.getInt(5) > 0) ? true : false;
        colour = (rs.getInt(6) > 0) ? true : false;
        sound = (rs.getInt(7) > 0) ? true : false;
      }

      // Read times
      rs = stmt.executeQuery("SELECT DIFFICULTY, TIME, NAME FROM TIMES");
      bestTimes = new HashMap<>();
      bestNames = new HashMap<>();

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

  public static void main(String[] args) throws ClassNotFoundException {
    Class.forName("org.sqlite.JDBC");
    String path = "a.db";
    dbPath = "jdbc:sqlite:" + path;

    SqlitePersistence p = new SqlitePersistence();
    p.resetTimes();
    p.initDB();
    p.saveDB();
    p.loadDB();
  }
}
