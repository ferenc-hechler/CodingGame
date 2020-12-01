package de.hechler.codingame.tictactoe;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player01SelectBest3x3 {

	public static LogLevel LOG_LEVEL = LogLevel.INFO;
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	
	public static class World {
		public int player;
		public int[] field;
		public int lastRow;
		public int lastCol;
		public int measure;
		public World(int player) {
			this(player, new int[9]);
		}
		public World(int player, int[] field) {
			this.player = player;
			this.field = new int[field.length];
			this.lastRow = -1;
			this.lastCol = -1;
			this.measure = 0;
			System.arraycopy(field, 0, this.field, 0, field.length);
		}
		public int get(int row, int col) {
			return field[row*3+col];
		}
		public boolean isFree(int row, int col) {
			return hasValue(row, col, 0);
		}
		public boolean hasValue(int row, int col, int value) {
			return field[row*3+col] == value;
		}
		public void setValue(int row, int col, int value) {
			field[row*3+col] = value;
			lastRow = row;
			lastCol = col;
		}
		public World createMove(int row, int col, int value) {
			World result = new World(-player, field);
			result.setValue(row, col, player);
			return result;
		}
		public List<World> getNextMoves() {
			List<World> result = new ArrayList<>();
			for (int row=0; row<3; row++) {
				for (int col=0; col<3; col++) {
					if (isFree(row, col)) {
						result.add(createMove(row, col, player));
					}
				}
			}
			return result;
		}
		public boolean checkWin() {
			int checkPlayer = get(lastRow, lastCol);
			boolean rowWin = true;
			boolean colWin = true;
			for (int i=0; i<3; i++) {
				rowWin = rowWin && hasValue(lastRow, i, checkPlayer);
				colWin = colWin && hasValue(i, lastCol, checkPlayer);
			}
			if (rowWin || colWin) {
				return true;
			}
			if ((lastRow == 1) && (lastCol == 1)) {
				boolean slashWin = true;
				boolean backSlashWin = true;
				for (int i=0; i<3; i++) {
					slashWin = slashWin && hasValue(i, i, checkPlayer);
					backSlashWin = backSlashWin && hasValue(i, 2-i, checkPlayer);
				}
				if (slashWin || backSlashWin) {
					return true;
				}
			}
			if ((lastRow != 1) && (lastCol != 1)) {
				if (lastRow == lastCol) {
					boolean slashWin = true;
					for (int i=0; i<3; i++) {
						slashWin = slashWin && hasValue(i, i, checkPlayer);
					}
					if (slashWin) {
						return true;
					}
				}
				else {
					boolean backSlashWin = true;
					for (int i=0; i<3; i++) {
						backSlashWin = backSlashWin && hasValue(i, 2-i, checkPlayer);
					}
					if (backSlashWin) {
						return true;
					}
				}
			}
			return false;
		}
		
		public World selectBestMove() {
			d("SBM: "+this);
			List<World> ownMoves = getNextMoves();
			d("  own: "+ownMoves);
			if (ownMoves.isEmpty()) {
				return null;
			}
			for (World ownMove:ownMoves) {
				if (ownMove.checkWin()) {
					ownMove.measure = 1000;
					return ownMove;
				}
			}
			int bestOwnMoveMeasure = Integer.MIN_VALUE;
			World bestOwnMove = null;
			for (World ownMove:ownMoves) {
				World opponentMove = ownMove.selectBestMove();
				if (opponentMove == null) {
					ownMove.measure = 0;
				}
				else {
					ownMove.measure = -opponentMove.measure;
				}
				if (ownMove.measure > bestOwnMoveMeasure) {
					bestOwnMoveMeasure = ownMove.measure;
					bestOwnMove = ownMove;
				}
			}
			d("  measures: "+ownMoves);
			return bestOwnMove;
		}

		@Override
			public String toString() {
				return "("+player+"|"+lastRow+","+lastCol+":"+measure+")";
			}
	}
	
	
    public static void main(String args[]) {
      try (Scanner in = new Scanner(System.in)) {

        World world = new World(1); 
        // game loop
        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            i("opponent: (", opponentRow, ",", opponentCol, ")");
            if (opponentRow != -1) {
            	world = world.createMove(opponentRow, opponentCol, -1);
            }
            int validActionCount = in.nextInt();
            i("actions: ", validActionCount);
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                i("   (", row, ",", col, ")");
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            
            if (opponentRow == -1) {
            	world = world.createMove(1, 1, 1);
            }
            else {
            	world = world.selectBestMove();
            }
            System.out.println(world.lastRow+" "+world.lastCol);
        }
      }
    }
    
    
    
    public static void _d(boolean show, Object... objs) { if (show) log(LogLevel.DEBUG, objs); }
    public static void _i(boolean show, Object... objs) { if (show) log(LogLevel.INFO, objs); }
    public static void _e(boolean show, Object... objs) { if (show) log(LogLevel.ERROR, objs); }
    public static void d(Object... objs) { log(LogLevel.DEBUG, objs); }
    public static void i(Object... objs) { log(LogLevel.INFO, objs); }
    public static void e(Object... objs) { log(LogLevel.ERROR, objs); }
    public static void log(LogLevel lvl, Object... objs) {
    	if (lvl.ordinal() >= LOG_LEVEL.ordinal()) {
    		System.err.print("["+lvl+"] ");
    		logAlways(objs);
    	}
    }
    public static void logAlways(Object... objs) {
    	if (objs.length == 0) {
    		return;
    	}
    	StringBuilder result = new StringBuilder();
    	for (Object obj:objs) {
    		if (obj == null) {
    			obj = "<null>";
    		}
    		result.append(obj.toString());
    	}
    	System.err.println(result.toString());
    }
 
}