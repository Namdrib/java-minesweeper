package minesweeper;

public class Driver
{

	public static void main(String[] args)
	{
		Game game = new GameImpl();
		game.getCells().get(5).get(5).open(true);
		game.getFinished();
		System.out.println(game);
	}

}
