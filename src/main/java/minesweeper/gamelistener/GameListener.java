package minesweeper.gamelistener;

public interface GameListener {
  public void gameLose();

  public void gameWin();

  public void gameTick();

  public void flagChanged();
}
