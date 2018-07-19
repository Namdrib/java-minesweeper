package minesweeper;

public interface GameListener
{
	public void gameLose();

	public void gameWin();
	
	public void gameTick();
}
