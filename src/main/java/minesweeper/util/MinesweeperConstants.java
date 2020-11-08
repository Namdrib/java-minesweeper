package minesweeper.util;

/**
 * Used to store constants specifically related to Minesweeper
 * 
 * @author Namdrib
 *
 */
public final class MinesweeperConstants {

  // Prevents instantiation
  private MinesweeperConstants() {}

  public static final int MIN_DIM_X = 9;
  public static final int MIN_DIM_Y = 9;
  public static final int MIN_MINES = 10;
  
  public static final int MAX_DIM_X = 30;
  public static final int MAX_DIM_Y = 24;

  public static final int BEGINNER_X = 9;
  public static final int BEGINNER_Y = 9;
  public static final int BEGINNER_MINES = 10;

  public static final int INTERMEDIATE_X = 16;
  public static final int INTERMEDIATE_Y = 16;
  public static final int INTERMEDIATE_MINES = 40;

  public static final int EXPERT_X = 30;
  public static final int EXPERT_Y = 16;
  public static final int EXPERT_MINES = 99;

  public static final int MAX_SECONDS = 999;
}
