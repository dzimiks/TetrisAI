package tetris;

import java.awt.Color;


public class State {
	
	public static final int COLS = 10;
	public static final int ROWS = 21;
	public static final int N_PIECES = 7;

	public boolean lost = false;
	public Label label;
	
	// Trenutni potez
	private int turn = 0;
	private int cleared = 0;
	
	// Svaki kvadrat u gridu - int znaci da je prazan, 
	// druge vrednosti se odnose na postavljeni potez
	private int[][] field = new int[ROWS][COLS];
	
	// Gornji row + 1 u svakoj koloni
	// 0 znaci da je prazan
	private int[] top = new int[COLS];
	
	// Broj sledece figure
	protected int nextPiece;
	
	// Svi legalni potezi - prvi indeks je tip figure, a onda ide niz
	protected static int[][][] legalMoves = new int[N_PIECES][][];
	
	// Indeksi za legalne poteze
	public static final int ORIENT = 0;
	public static final int SLOT = 1;
	
	// Moguce orijentacije za dati tip figure
	protected static int[] pOrients = {1, 2, 4, 4, 4, 2, 2};
	
	public State() {
		nextPiece = randomPiece();
	}
	
	// Sledeci nizovi definisu svaku figuru
	// Sirina figure [ID figure][orijentacija]
	protected static int[][] pWidth = {
			{2},
			{1, 4},
			{2, 3, 2, 3},
			{2, 3, 2, 3},
			{2, 3, 2, 3},
			{3, 2},
			{3, 2}
	};
	
	// Visina figure [ID figure][orijentacija]
	private static int[][] pHeight = {
			{2},
			{4, 1},
			{3, 2, 3, 2},
			{3, 2, 3, 2},
			{3, 2, 3, 2},
			{2, 3},
			{2, 3}
	};
	
	private static int[][][] pBottom = {
		{{0, 0}},
		{{0}, {0, 0, 0, 0}},
		{{0, 0}, {0, 1, 1}, {2, 0}, {0, 0, 0}},
		{{0, 0}, {0, 0, 0}, {0, 2}, {1, 1, 0}},
		{{0, 1}, {1, 0, 1}, {1, 0}, {0, 0, 0}},
		{{0, 0, 1}, {1, 0}},
		{{1, 0, 0}, {0, 1}}
	};
	
	private static int[][][] pTop = {
		{{2, 2}},
		{{4}, {1, 1, 1, 1}},
		{{3, 1}, {2, 2, 2}, {3, 3}, {1, 1, 2}},
		{{1, 3}, {2, 1, 1}, {3, 3}, {2, 2, 2}},
		{{3, 2}, {2, 2, 2}, {2, 3}, {1, 2, 1}},
		{{1, 2, 2}, {3, 2}},
		{{2, 2, 1}, {2, 3}}
	};
	
	// Svi legalni potezi
	{
		// Za svaki tip figure
		for (int i = 0; i < N_PIECES; i++) {
			// Broj figure legalnih poteza
			int n = 0;
			for (int j = 0; j < pOrients[i]; j++) {
				// Broj lokacija u ovoj orijentaciji
				n += COLS + 1 - pWidth[i][j];
			}
			
			legalMoves[i] = new int[n][2];
			
			// Za svaku orijentaciju
			n = 0;
			for (int j = 0; j < pOrients[i]; j++) {
				// Za svaki slot
				for (int k = 0; k < COLS + 1 - pWidth[i][j]; k++) {
					legalMoves[i][n][ORIENT] = j;
					legalMoves[i][n][SLOT] = k;
					n++;
				}
			}
		}
	}
	
	// Random broj 0-6
	private int randomPiece() {
		return (int)(Math.random() * N_PIECES);
	}
	
	public int[][] legalMoves() {
		return legalMoves[nextPiece];
	}
	
	//
	public void makeMove(int move) {
		makeMove(legalMoves[nextPiece][move]);
	}
	
	// Pravi potez na osnovu move indeksa
	// Pravi potez na osnovu niza orijentacije i slota
	public void makeMove(int[] move) {
		makeMove(move[ORIENT], move[SLOT]);
	}
	
	// Vraca false ako je igrac izgubio
	public boolean makeMove(int orient, int slot) {
		turn++;
		
		// Visina ako je prva kolona napravila kontakt
		int height = top[slot] - pBottom[nextPiece][orient][0];
		
		// Za svaku figuru izvan prve u figuri
		for (int c = 1; c < pWidth[nextPiece][orient]; c++) {
			height = Math.max(height, top[slot + c] - pBottom[nextPiece][orient][c]);
		}
		
		// Vraca da li je igra zavrsena
		if (height + pHeight[nextPiece][orient] >= ROWS) {
			lost = true;
			return false;
		}
		
		// Za svaku kolonu u figuri popunjava odgovarajuce kvadrate
		for (int i = 0; i < pWidth[nextPiece][orient]; i++) {
			for (int h = height + pBottom[nextPiece][orient][i]; h < height + pTop[nextPiece][orient][i]; h++) {
				field[h][i + slot] = turn;
			}
		}
		
		// Podesava top
		for (int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot + c] = height + pTop[nextPiece][orient][c];
		}
		
		// Broj obrisanih redova
		int rowsCleared = 0;
		
		for (int r = height + pHeight[nextPiece][orient] - 1; r >= height; r--) {
			// Prolazi kroz sve kolone u redu
			boolean full = true;
			for (int c = 0; c < COLS; c++) {
				if (field[r][c] == 0) {
					full = false;
					break;
				}
			}
			
			// Ako je red bio pun - obrisi ga i spusti gornje kvadratice
			if (full) {
				rowsCleared++;
				cleared++;

				// Za svaku kolonu
				for (int c = 0; c < COLS; c++) {
					// Spusta sve popunjene kvadrate nadole
					for (int i = r; i < top[c]; i++) {
						field[i][c] = field[i + 1][c];
					}
					
					// Spusta top
					top[c]--;
					while (top[c] >= 1 && field[top[c] - 1][c] == 0)	
						top[c]--;
				}
			}
		}

		// Generise novu figuru
		nextPiece = randomPiece();
		return true;
	}
	
	public void draw() {
		label.clear();
		label.setPenRadius();

		// Iscrtava okvir
		label.line(0, 0, 0, ROWS + 5);
		label.line(COLS, 0, COLS, ROWS + 5);
		label.line(0, 0, COLS, 0);
		label.line(0, ROWS - 1, COLS, ROWS - 1);
		
		// Prikazuje kvadrate
		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < top[c]; r++) {
				if (field[r][c] != 0) {
					drawBrick(c, r);
				}
			}
		}
		
		for (int i = 0; i < COLS; i++) {
			label.setPenColor(Color.RED);
			label.line(i, top[i], i + 1, top[i]);
			label.setPenColor();
		}
		
		label.show();
	}
	
	public static final Color brickCol = Color.BLUE; 
	
	private void drawBrick(int c, int r) {
		label.filledRectangleLL(c, r, 1, 1, brickCol);
		label.rectangleLL(c, r, 1, 1);
	}
	
	public void drawNext(int slot, int orient) {
		for (int i = 0; i < pWidth[nextPiece][orient]; i++) {
			for (int j = pBottom[nextPiece][orient][i]; j < pTop[nextPiece][orient][i]; j++) {
				drawBrick(i + slot, ROWS + j + 1);
			}
		}
		label.show();
	}
	
	public void clearNext() {
		label.filledRectangleLL(0, ROWS + 0.9, COLS, 4.2, Label.DEFAULT_CLEAR_COLOR);
		label.line(0, 0, 0, ROWS + 5);
		label.line(COLS, 0, COLS, ROWS + 5);
	}
	
	public int[][] getField() {
		return field;
	}

	public int[] getTop() {
		return top;
	}

    public static int[] getpOrients() {
        return pOrients;
    }
    
    public static int[][] getpWidth() {
        return pWidth;
    }

    public static int[][] getpHeight() {
        return pHeight;
    }

    public static int[][][] getpBottom() {
        return pBottom;
    }

    public static int[][][] getpTop() {
        return pTop;
    }

	public int getNextPiece() {
		return nextPiece;
	}
	
	public boolean hasLost() {
		return lost;
	}
	
	public int getRowsCleared() {
		return cleared;
	}
	
	public int getTurnNumber() {
		return turn;
	}
}