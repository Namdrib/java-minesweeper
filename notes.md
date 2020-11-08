# Notes
Personal notes to keep track of stuff/thoughts as I go along.

## Development

### Design
- Minesweeper: Has main, handles the JFrame. Creates timer info (the MapEditor)
	- Uses info from the game to set GUI stuff (e.g. timer, mines left, board)
- Game: contains the board, how many mines, the timer
- Board: the board
- Cell: Board have a lot of these. handles states, etc.

#### Menus
- Game
	- (N)ew (F2)
	- ---
	- (B)eginner (10 / 9x9)
	- (I)ntermediate (40 / 16x16)
	- (E)xpert (99 / 16x30)
	- (C)ustom (Dialogue asking for height, width, mines) [OK/Cancel]
		- Takes default values of the current game
		- Smallest board is 9*9 (smallest of any dim is 9)
		- Largest board id 30*30 (largest of any dim is 9)
		- e.g. input 5x35 -> 9x30
		- Fewest mines is 10
		- Most mines is 667
		- Most mines on 9x9 is 64
		- Fewest mines on 30*30 is 10
	- ---
	- (M)arks (?)
	- Co(l)our
	- (S)ound
		- 3 sound I know of - timer ticking (per second), uncovering a mine and winning
	- ---
	- Bes(t) Times
	- ---
	- E(x)it
- Help
	- About

### Mouse actions:
#### On the board
IF THE GAME IS PLAYING
- left down -> "press" (do the "push down" animation) current cell and change face to surprised
- left up -> open the current cell and change face to normal
- right down -> toggle flag state on current cell
	- if the marks options has been changed AFTER the game started, the next right click should eitiher:
		- mark -> blank
		- flag -> blank
		- blank -> mark
- right up -> nothing
- middle down -> "press" all neighbours and change face to surprised
- middle up -> if num flags around the cell == the cell's number, open every other neighbour and change face to normal
- left + right -> same as middle

Otherwise, do nothing

### On the face
- left down -> "press"
- left leave -> unpress
- left up -> nothing
- left FULL -> reset game, change sprite if necessary
- right anything -> nothing
- middle anything -> nothing

### Timing:
- Starts on the first LEFT click on a cell, keeps ticking until either:
	- timer hits 999 (stops ticking entirely, no sounds, no increase)
	- game ends (either mine uncovered, game wins or new game) 

## Usage
