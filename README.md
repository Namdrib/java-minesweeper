# java-minesweeper
A Java version of Microsoft XP Mineseweeper. The GUI is built using Java AWT and Swing, with the sound component using JavaFX. This may be ported to JavaFX in the future.

## Requirements
- Java 8 or higher
- JavaFX

## Running
- `git clone git@github.com:Namdrib/java-minesweeper.git` or `git clone https://github.com/Namdrib/java-minesweeper.git`

### Command line
- `cd java-minesweeper/src/minesweeper`
- `javac Minesweeper.java`
- `java Minesweeper`

### IDE
- Import folder as a project and compile/run

## How to play Minesweeper
### Playing a game
- Click on a tile to reveal it. Its number (1-8) represents how many mines are adjacent to the tile
	- If it doesn't have a number, there are no mines surrounding it, and it will automatically reveal its neighbours
	- If it contained a mine, the game is lost
- Right-clicking on a tile flags it - this is useful for marking the position of known mines
	- If enabled, right-clicking again on a flagged mine puts `?`, which may be useful for if the player is unsure whether there is a mine
- Left-and-right clicking at the same time on a numbered tile allows the user to perform a **chording** action
	- Chording only takes place if there are the same number of flags around the tile as its displayed number. For example, to chord a "1" tile, there must be exactly 1 flag around it
	- Chording opens every un-flagged neighbour of the target tile, which allows for rapid board expansion
	- If one of the flags around the neighbour is incorrectly placed, this results in the opening of a mine, resulting in the game being lost
	- The user may also middle-click the tile to achieve the same effect

### Changing the settings
There are several settings that may be changed from within the game
1. The difficulty
	- There are three preset difficulties, beginner, intermediate and expert
	- In addition to these, there is also a custom difficulty in which the user may select the board's dimensions and how many mines there are. Note that the x-dimension must be between 9 and 30, the y-dimension must be between 9 and 24, and the number of mines must be between 10 and `(x-1)*(y-1)`
	- High scores are maintained for the three preset difficulties
1. Marking: whether tiles may be marked with a `?`
	- Turning this off may improve performance for speedruns
1. Theme: which graphical theme to use (work in progress) 
	- At some point in the future, I may implement a theme switcher, where the program scans through its directory to see whether a valid theme is present
	- For a theme to be valid, it must have all the files in `/assets/img` but in another folder (e.g. `/assets/img/ex_theme`). Then in the game, the user may use the menu `File -> Themes -> ex_theme` to switch themes. Doing so will only affect the icons that are being displayed, not the colours of the main window, borders, etc.
1. Sound: whether sounds are to be played
