package tetris;

import java.util.ArrayList;
import java.util.List;


public class TetrisTester implements Runnable {
	
	// Broj testiranja igre
	// Prikazace instancu od svake ako ih ima vise
	public static final int TESTS = 2;
	
	// True - prikazuje simulaciju
	// False - igra u pozadini i ispisuje rezultat u konzoli
	// Nije pametno cekirati true za vise od 3 testova
	public static final boolean DRAW = true;
	
	public static int min = TetrisBot.MAX;
	public static int max = TetrisBot.MIN;
	public static double average = 0.0;
	
	public static Object maxLock = new Object();
	public static Object minLock = new Object();
	public static Object averageLock = new Object();
	
	public static void main(String[] args) {
		
		List<Thread> threadList = new ArrayList<Thread>();
		
		// Inicijalizacija nove instance za svaki od testova
		for (int i = 0; i < TESTS; i++) {
			Thread t = new Thread(new TetrisTester());
			t.start();
			threadList.add(t);
		}
		
		// Ceka da se izvrse sve instance pa tek onda ispisuje krajnji rezultat
		try {
			for (Thread t: threadList)
				t.join();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.err.println("MIN: " + min + " MAX: " + max + " PROSEK: " + average / TESTS);
	}
	
	public void run() {
		State s = new State();
		
		if (DRAW)
			new Frame(s);
		
		TetrisBot bot = new TetrisBot();
		
		while (!s.hasLost()) { 
			s.makeMove(bot.pickMove(s, s.legalMoves()));
			
			if (DRAW) {
				s.draw();
				s.drawNext(4, 0);
			}
			
			// Podesavanje brzine
			try {
				Thread.sleep(10);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int rowsCleared = s.getRowsCleared();
		
		synchronized(maxLock) {
			if (rowsCleared > max)
				max = rowsCleared;
			
			maxLock.notify();
		}

		synchronized(minLock) {
			if (rowsCleared < min)
				min = rowsCleared;
			
			minLock.notify();
		}

		synchronized(averageLock) {
			average += rowsCleared;
			averageLock.notify();
		}
		
		System.out.println("Broj ociscenih redova: " + rowsCleared);
	}
}