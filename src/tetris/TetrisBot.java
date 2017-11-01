package tetris;


public class TetrisBot {
	
	public static final int MAX = Integer.MAX_VALUE;
	public static final int MIN = Integer.MIN_VALUE;
		
	
	public static void main(String[] args) {
		
		State s = new State();
		new Frame(s);
		TetrisBot p = new TetrisBot();
		
		while (!s.hasLost()) {
			s.makeMove(p.pickMove(s, s.legalMoves()));
			s.draw();
			s.drawNext(4, 0); // Generise figuru na sredini
			
			// Ovde podesiti brzinu igre
//			try {
//				Thread.sleep(1000);
//			} 
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		System.out.println("Uspesno je ocisceno " + s.getRowsCleared() + " redova!");
	}
	
	public int pickMove(State s, int[][] legalMoves) {
		double maxHeuristic = TetrisBot.MIN;
		int maxHeuristicIndex = 0;

		// Gleda sve moguce legalne poteze
		for (int i = 0; i < legalMoves.length; i++) {
			// Vrednost heuristike datog poteza
			Heuristic h = new Heuristic(s, legalMoves[i]);
			double totalHeuristic = h.getTotalHeuristicValue();
			
			// Trazi se najbolji potez
			if (Double.compare(totalHeuristic, maxHeuristic) > 0) {
				maxHeuristic = totalHeuristic;
				maxHeuristicIndex = i;
			}
		}
		
		// Za svaki slucaj - pazi se na IndexOutOfBoundsException
		return (maxHeuristicIndex > (-1)) ? maxHeuristicIndex : 0;
	}
	
	// Glavna klasa koja definise sve akcije
	// Eksperimentise se sa razlicitim tezinama i heuristikama
	private class Heuristic {

		// Trenutno stanje
		private State s;
		
		// Kako ce sledece stanje izgledati nakon odigranog dozvoljenog poteza
		private StateCopy sc;
		
		// Odredjene tezine za razlicite heuristike - uvek moze bolje
		private static final double ROWS_COMPLETED_WEIGHT     = 388.43;
		private static final double DIF_MAX_HEIGHT_WEIGHT     = -4.82;
		private static final double DIF_HOLES_WEIGHT          = -111.74;
		private static final double DIF_AVERAGE_HEIGHT_WEIGHT = -379.08;
		private static final double DIF_ABS_HEIGHT_WEIGHT     = -20.79;
		private static final double DIF_IN_HEIGHT_WEIGHT	  = -3.25;
		private static final double DIF_IN_TROUGHS_WEIGHT	  = -50;
		private boolean hasLost = false;
		
		public Heuristic(State s, int[] m) {
			this.s = s;
			this.sc = new StateCopy(s);
			this.sc.makeMove(m);
		}
		
		public double getTotalHeuristicValue() {
			
			if (hasLost)
				return TetrisBot.MIN / 2; // Ovaj potez svakako ne zelimo da se desi
			
			double rowsCompleted = getRowsCompleted(sc);
			double differenceInMaxHeight = getMaxHeight(sc) - getMaxHeight(s);
			double differenceInHolesInBoard = getHoles(sc) - getHoles(s);
			double differenceInAbsHeightDifference = getAbsHeightDifference(sc) - getAbsHeightDifference(s);
			double differenceInAverageHeight = getAverageHeight(sc) - getAverageHeight(s);
			double differenceBetweenMaxAndMin = getMaxHeight(sc) - getMinHeight(sc);
			double differenceBetweenDeepTroughs = getDeepTroughs(sc) - getDeepTroughs(s);

			double totalHeuristic = (rowsCompleted * ROWS_COMPLETED_WEIGHT) +
									(differenceInMaxHeight * DIF_MAX_HEIGHT_WEIGHT) +
									(differenceInHolesInBoard * DIF_HOLES_WEIGHT) +
									(differenceInAverageHeight * DIF_AVERAGE_HEIGHT_WEIGHT) +
									(differenceInAbsHeightDifference * DIF_ABS_HEIGHT_WEIGHT) +
									(differenceBetweenMaxAndMin * DIF_IN_HEIGHT_WEIGHT) +
									(differenceBetweenDeepTroughs * DIF_IN_TROUGHS_WEIGHT);

			return totalHeuristic;
		}
			
		public int getHoles(Object s) {
			int field[][], top[];
			int cols = State.COLS;
			int holes = 0;
			
			try {
				field = State.class.cast(s).getField();
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e1) {
				try {
					field = StateCopy.class.cast(s).getField();
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}
			
			for (int i = 0; i < cols; i++) {
				for (int j = top[i] - 2; j > 0; j--) {
					try {
						if (field[j][i] == 0 && field[j + 1][i] != 0)
							holes++;
					}
					catch (IndexOutOfBoundsException e) {
						// Znaci da smo dosli do vrha 
					}
				}
			}
			
			return holes;
		}

		// Trazi vertikalne praznine dubine 2
		public int getDeepTroughs(Object s) {
			int troughs = 0;
			int field[][], top[];
			
			try {
				field = State.class.cast(s).getField();
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e) {
				try {
					field = StateCopy.class.cast(s).getField();
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}

			for (int col = 0; col < top.length; col++) {
				int row = top[col];
				
				try {
					if (col == 0) {
						// Specijalni slucaj za prvu kolonu
						boolean isTroff = field[row + 1][col + 1] != 0 && field[row + 2][col + 1] != 0;
						if (isTroff)
							troughs++;
					}
					else if (col == top.length - 1) {
						// Specijalni slucaj za poslednju kolonu
						boolean isTroff = field[row + 1][col - 1] != 0 && field[row + 2][col - 1] != 0; 
						if (isTroff)
							troughs++;
					}
					else {
						boolean isTroff = field[row + 1][col + 1] != 0 && field[row + 1][col - 1] != 0 && 
										  field[row + 2][col + 1] != 0 && field[row + 2][col - 1] != 0;
						if (isTroff)
							troughs++;
					}
				}
				catch (IndexOutOfBoundsException e) {

				}
			}
			
			return troughs;
		}
	
		public double getAbsHeightDifference(Object s) {
			int top[];
			int sum = 0;
			
			try {
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e1) {
				try {
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}

			try {
				for (int i = 0; i < top.length; i++) {
					sum += Math.abs(top[i] - top[i + 1]);
				}
			}
			catch (IndexOutOfBoundsException e) {
				// Znaci da je prosao kroz sve redove
			}
				
			return sum;
		}
		
		public double getMinHeight(Object s) {
			int top[];
			int min= TetrisBot.MAX;
			
			try {
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e1) {
				try {
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}

			
			for (int i : top) 
				if (i < min)
					min = i;
			
			return min;
		}
		
		public double getMaxHeight(Object s) {
			int top[];
			int max = -1;
			
			try {
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e1) {
				try{
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}
			
			for (int i : top) 
				if (i > max)
					max = i;
			
			return max;
		}
				
		public double getAverageHeight(Object s) {
			int top[];
			double sum = 0;
			
			try {
				top = State.class.cast(s).getTop();
			}
			catch (ClassCastException e1) {
				try {
					top = StateCopy.class.cast(s).getTop();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}

			for (int i : top)
				sum += i;

			return sum / (double)top.length;
		}
			
		public double getRowsCompleted(Object s) {
			int rowsCleared;
			
			try {
				rowsCleared = State.class.cast(s).getRowsCleared();
			}
			catch (ClassCastException e1) {
				try {
					rowsCleared = StateCopy.class.cast(s).getRowsCleared();
				}
				catch (ClassCastException e2) {
					System.err.println("Greska pri kastovanju objekta!");
					return -1000;
				}
			}
			
			return rowsCleared;
		}
		
		// Kopija klase State koja kopira trenutno stanje i simulira klasu State
		// tako da mogu da se testiraju naredni potezi
		private class StateCopy {
			
			public final int COLS = 10;
			public final int ROWS = 21;
			public final int N_PIECES = 7;
			public final int ORIENT = 0;
			public final int SLOT = 1;
			
			private int turn = 0;
			private int cleared = 0;
			protected int nextPiece;
			
			private int[][] field = new int[ROWS][COLS];
			private int[] top = new int[COLS];
			
			protected int[][][] legalMoves = new int[N_PIECES][][];
			protected int[] pOrients = {1, 2, 4, 4, 4, 2, 2};
			
			protected int[][] pWidth = {
					{2},
					{1, 4},
					{2, 3, 2, 3},
					{2, 3, 2, 3},
					{2, 3, 2, 3},
					{3, 2},
					{3, 2}
			};
	
			private int[][] pHeight = {
					{2},
					{4, 1},
					{3, 2, 3, 2},
					{3, 2, 3, 2},
					{3, 2, 3, 2},
					{2, 3},
					{2, 3}
			};
			
			private int[][][] pBottom = {
				{{0, 0}},
				{{0}, {0, 0, 0, 0}},
				{{0, 0}, {0, 1, 1}, {2, 0}, {0, 0, 0}},
				{{0, 0}, {0, 0, 0}, {0, 2}, {1, 1, 0}},
				{{0, 1}, {1, 0, 1}, {1, 0}, {0, 0, 0}},
				{{0, 0, 1}, {1, 0}},
				{{1, 0, 0}, {0, 1}}
			};
			
			private int[][][] pTop = {
				{{2, 2}},
				{{4}, {1, 1, 1, 1}},
				{{3, 1}, {2, 2, 2}, {3, 3}, {1, 1, 2}},
				{{1, 3}, {2, 1, 1}, {3, 3}, {2, 2, 2}},
				{{3, 2}, {2, 2, 2}, {2, 3}, {1, 2, 1}},
				{{1, 2, 2}, {3, 2}},
				{{2, 2, 1}, {2, 3}}
			};
			
			public StateCopy(State s) {
				for (int i = 0; i < N_PIECES; i++) {
					int n = 0;
					
					for (int j = 0; j < pOrients[i]; j++) 
						n += COLS + 1 - pWidth[i][j];

					legalMoves[i] = new int[n][2];
					n = 0;
					
					for (int j = 0; j < pOrients[i]; j++) {
						for (int k = 0; k < COLS + 1 - pWidth[i][j]; k++) {
							legalMoves[i][n][ORIENT] = j;
							legalMoves[i][n][SLOT] = k;
							n++;
						}
					}
				}
				
				nextPiece = s.getNextPiece();
				int currentLegalMoves[][] = s.legalMoves();
				
				for (int i = 0; i < this.legalMoves[nextPiece].length; i++)
					for (int j = 0; j < this.legalMoves[nextPiece][i].length; j++)
						this.legalMoves[nextPiece][i][j] = currentLegalMoves[i][j];
				
				int currentField[][] = s.getField();
				
				for (int i = 0; i < ROWS; i++)
					for (int j = 0; j < COLS; j++)
						this.field[i][j] = currentField[i][j];
				
				int currentTop[] = s.getTop();
				
				for (int i = 0; i < currentTop.length; i++)
					this.top[i] = currentTop[i];
			}
	
			public void makeMove(int[] move) {
				makeMove(move[ORIENT], move[SLOT]);
			}
			
			public void makeMove(int orient, int slot) {
				turn++;
				int height = top[slot] - pBottom[nextPiece][orient][0];
				
				for (int c = 1; c < pWidth[nextPiece][orient]; c++) 
					height = Math.max(height, top[slot + c] - pBottom[nextPiece][orient][c]);
				
				if (height + pHeight[nextPiece][orient] >= ROWS) {
					hasLost = true;
					return;
				}
				
				for (int i = 0; i < pWidth[nextPiece][orient]; i++) {
					for (int h = height + pBottom[nextPiece][orient][i]; h < height + pTop[nextPiece][orient][i]; h++) {
						field[h][i + slot] = turn;
					}
				}
				
				for (int c = 0; c < pWidth[nextPiece][orient]; c++) 
					top[slot + c] = height + pTop[nextPiece][orient][c];
				
				int rowsCleared = 0;
				
				for (int r = height + pHeight[nextPiece][orient] - 1; r >= height; r--) {
					boolean full = true;
					
					for (int c = 0; c < COLS; c++) {
						if (field[r][c] == 0) {
							full = false;
							break;
						}
					}
	
					if (full) {
						rowsCleared++;
						cleared++;
						
						for (int c = 0; c < COLS; c++) {
							for (int i = r; i < top[c]; i++)
								field[i][c] = field[i + 1][c];
							
							top[c]--;
							
							while (top[c] >= 1 && field[top[c] -1 ][c] == 0)	
								top[c]--;
						}
					}
				}
			}	
			
			public int[][] getField() {
				return field;
			}
			
			public int[] getTop() {
				return top;
			}
	
			public int getRowsCleared() {
				return cleared;
			}
		}	
	}
}