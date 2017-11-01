# TetrisAI

This is seminary work project written in Java. It represents basic Tetris simulation.

![](https://i.imgur.com/jjAlkL3.png)

## Getting Started

Here are explanation and instructions how to play a game.

To play as human, run Frame class (use arrow keys).  
To see AI Bot in action, run TetrisBot class.  
To test multiple games at once, run TetrisTester class with appropriate settings.

### Frame

This extends JFrame and it is instantiated to draw a state.
The main function allows you to play a game manually using the arrow keys (UP, RIGHT, BOTTOM, LEFT).

### Label

Drawing library.

### State

This simulates a whole game. It keeps track of the state and allows you to make moves.
Moves are defined by two numbers: the **SLOT**, the leftmost column of the piece and the **ORIENT**, 
the orientation of the piece.
NextPiece (accessed by getNextPiece) contains the ID (0-6) of the piece you are about to play.
It also keeps track of the number of lines cleared - accessed by getRowsCleared().

```
draw() - draws the board
drawNext() - draws the next piece above the board
clearNext() - clears the drawing of the next piece so it can be drawn in a different slot / orientation
```
### TetrisBot

Here is implemented AI Bot player.
The main function plays a game automatically (with visualization).  
You can set speed in main function.

### TetrisTester

Class that tests one or multiple instances of the game.

```
TEST - number of tests (don't overstate)
DRAW - true or false, if true, be careful with number of tests
```

Languages Used:
* Java

Tools Used:
* Swing (for GUI)
* Eclipse (IDE)