package minesweeper;

public class Driver
{

	public static void main(String[] args)
	{
		BoardImpl b = new BoardImpl(10, 10, 9);
		System.out.println(b);

		b.open(0, 0);
		b.open(1, 0);
		b.open(5, 5);
		System.out.println(b);
		
		System.out.println(b.getRemainingMines());
	}

}
