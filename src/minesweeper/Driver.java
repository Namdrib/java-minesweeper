package minesweeper;

public class Driver
{

	public static void main(String[] args)
	{
		Game game = new GameImpl();
		game.getCells().get(5).get(5).open(true);
		game.isFinished();
		System.out.println(game);
	}

}
