package minesweeper;

public interface GameListener
{
	public void tileChanged();

	public void timeChanged();

	public void otherChanged();
}
