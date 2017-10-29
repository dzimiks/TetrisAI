package tetris;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


public class Frame extends JFrame implements KeyListener {
	
	private static final long serialVersionUID = 1L;
	public static final int MANUAL = 0;
	public static final int NONE = 1;
	
	public Label label = new Label(300, 700);
	public State s;
	public int orient, slot;
	public int mode = MANUAL;

	public Frame() {
		s.label = label;
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
		setTitle("RAF TetrisAI");
		setContentPane(label.draw);
		pack();
		label.BORDER = 0.05;
		label.setXscale(0, State.COLS);
		label.setYscale(0, State.ROWS + 5);
		this.addKeyListener(this);  
		setVisible(true);
	}
	
	public Frame(State s) {
		this.s = s;
		s.label = label;
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
		setTitle("RAF TetrisAI");
		setContentPane(label.draw);
		pack();
		label.BORDER = 0.05;
		label.setXscale(0, State.COLS);
		label.setYscale(0, State.ROWS + 5);
		
		try {
            this.setIconImage(ImageIO.read(new File("images/raf-logo.png")));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
		
		setMinimumSize(new Dimension(300, 700));
		this.addKeyListener(this);  
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// Zamenjuje stanje koje je vezano za Frame
	public void bindState(State s) {
		if(s != null)	
			s.label = null;
		
		this.s = s;
		s.label = label;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(mode) {
			case(MANUAL): {
				switch(e.getKeyCode()) {
					case(KeyEvent.VK_RIGHT): {
						if (slot < State.COLS - State.pWidth[s.nextPiece][orient])	
							slot++;
						
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_LEFT): {
						if (slot > 0)
							slot--;
						
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_UP): {
						orient++;
						
						if (orient % State.pOrients[s.nextPiece] == 0)	
							orient = 0;
						
						if (slot > State.COLS - State.pWidth[s.nextPiece][orient])
							slot = State.COLS - State.pWidth[s.nextPiece][orient];
						
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					case(KeyEvent.VK_DOWN): {
						if (!s.makeMove(orient, slot))	
							mode = NONE;
						
						if (orient >= State.pOrients[s.nextPiece])
							orient = 0;
						
						if (slot > State.COLS - State.pWidth[s.nextPiece][orient])
							slot = State.COLS - State.pWidth[s.nextPiece][orient];
						
						s.draw();
						
						if (mode == NONE) {
							label.text(State.COLS / 2.0, State.ROWS / 2.0, "Izgubio si!");
							System.out.println("Uspesno je ocisceno " + s.getRowsCleared() + " redova!");
						}
						
						s.clearNext();
						s.drawNext(slot, orient);
						break;
					}
					default:
						break;
				}
			}
			case(NONE):	
				break;
			default:
				System.out.println("Unknown mode!");
				break;
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {

	}
	
    @SuppressWarnings("unused")
	public static void main(String[] args) {
		State s = new State();
		Frame t = new Frame(s);
		s.draw();
		s.drawNext(0, 0);
	}
}