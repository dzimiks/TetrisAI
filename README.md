# TetrisAI

This is seminary work project written in Java. It represents basic Tetris simulation.

## Getting Started

Here are explanation and instructions how to play a game.

### Frame

This extends JFrame and it is instantiated to draw a state.
The main function allows you to play a game manually using the arrow keys (UP, RIGHT, BOTTOM, LEFT).

### Label

Drawing library.

### State

This simulates a whole game. It keeps track of the state and allows you to make moves.

Moves are defined by two numbers: the **SLOT**, the leftmost column of the piece and the **ORIENT**, 
the orientation of the piece.

It also keeps track of the number of lines cleared - accessed by getRowsCleared().

```
draw() - draws the board
drawNext() - draws the next piece above the board
clearNext() - clears the drawing of the next piece so it can be drawn in a different slot / orientation
```

Languages Used:
* Java

Tools Used:
* Swing (for GUI)
* Eclipse (IDE)