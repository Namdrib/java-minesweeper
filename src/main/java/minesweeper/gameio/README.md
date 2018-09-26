# GameIO

This interface is used to serialise and deserialise a Game into a human-readable representation. This enables pre-determined games to be loaded, and for games to be saved.

All game files may have lines starting with `#` denoting a comment. These lines are to be ignored when parsing the files.

## \*Mines
The "\*Mines()" set of functions handles reading/writing a file containing a description of:

- The game dimensions (x, y)
- The number of mines (n)
- The location of each mine (the next n lines)

### The game dimensions
The first line must be two space-separated numbers. The first number represents how many cells running across the game (`x`). The second represents how many cells running down (`y`).

If either dimension is too large or too small, there should be a GameFormatException thrown with the number of the file being read, and a message signifying which dimension caused the error, and its value.

### The number of mines
The next line describes how many mines there are in the game (`n`). Again, a GameFormatException should be thrown if this is an inappropriate value. Specifically, the minimum value is 10, and the maximum value is `(x-1)*(y-1)`.

### The location of each mine
The next n lines should be space-separated pairs of numbers (`xi yi`), each describing the x- and y-co-ordinates of a mine. If there are too few mine locations provided, or if any of them are out of bounds, or a location is duplicated, a GameFormatException should be thrown.

The x- and y-co-ordinates should follow nested array co-ordinates. i.e. `0 0` is a mine in the top-left corner, and `x-1 y-1` is a mine in the bottom-right corner.

### Example input file
```
10 10
10
0 0
1 1
2 2
3 3
4 4
5 5
6 6
7 7
8 8
9 9
```
This describes a game whose mines follow a straight diagonal from the top-left to bottom-right.

## \*Game
The "\*Games()" set of functions handles reading/writing a a file describing the complete game state. Like the "\*Mines()" set of functions, it must contain:

- The game dimensions (x, y)
- The total number of mines (n)

Instead of having a line-by-line description of each mine's location, it is followed by a line-by-line description of each row in the game. It also contains metadata such as number of seconds elapsed.

TODO : Example input file