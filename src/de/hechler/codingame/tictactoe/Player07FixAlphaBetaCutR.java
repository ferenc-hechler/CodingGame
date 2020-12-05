package de.hechler.codingame.tictactoe;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player07FixAlphaBetaCutR {

	public final static int KI_SEARCH_DEPTH = 4;
	
	public static LogLevel LOG_LEVEL = LogLevel.DEBUG;
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	
	public final static boolean showValidActions = false;
	
	
	private static String PLAYBACK_FILE = null;
	private static boolean RECORD_INPUT = false;
    private static boolean CLEAR_RECORDS = false;

	
    public static interface GameInterface {
    	public List<? extends GameInterface> getAllValidMoves(int player);
    	public int getMeasure();
    	public void setRecursiveMeasure(int recursiveMeasure);
    }

    public final static int UNKNOWN_MEASURE = Integer.MIN_VALUE;
    public final static int SKIPPED_MEASURE = Integer.MIN_VALUE+1;
    
    public static abstract class AbstractCachedMeasureGame implements GameInterface {
    	public int cachedMeasure = UNKNOWN_MEASURE; 
    	public int getMeasure() {
    		if (cachedMeasure == UNKNOWN_MEASURE) {
    			cachedMeasure = calcMeasure();
    		}
    		return cachedMeasure;
    	}
    	public void setRecursiveMeasure(int recursiveMeasure) {
    		cachedMeasure = recursiveMeasure;
    	}
    	public abstract int calcMeasure();
    }

    
    public static class AlphaBetaSearch {
    	public int maxSearchDepth;
    	public AlphaBetaSearch(int maxSearchDepth) {
    		this.maxSearchDepth = maxSearchDepth;
    	}

    	public GameInterface selectBestMove(int player, GameInterface game) {
    		return selectBestMove(player, game, maxSearchDepth, null);
    	}

    	public GameInterface selectBestMove(int player, GameInterface game, int searchDepth, GameInterface bestEnemyParentGame) {
    		int cutM = (bestEnemyParentGame == null) ? UNKNOWN_MEASURE : bestEnemyParentGame.getMeasure();
    		int bestM = UNKNOWN_MEASURE;
    		GameInterface bestMove = null;
        	List<? extends GameInterface> ownMoves = game.getAllValidMoves(player);
    		for (GameInterface ownMove:ownMoves) {
    			int measure;
    			if (searchDepth == 0) {
    				measure = ownMove.getMeasure();
    			}
    			else {
    				GameInterface bestEnemyMove = selectBestMove(-player, ownMove, searchDepth-1, bestMove);
    				if (bestEnemyMove == null) {
    					measure = ownMove.getMeasure();
    				}
    				else {
    					measure = bestEnemyMove.getMeasure();
    					ownMove.setRecursiveMeasure(measure);
    				}
   				}
    			if (player == 1) {          
    				// MAX
	    			if ((cutM != UNKNOWN_MEASURE) && (measure >= cutM)) {
	    				ownMove.setRecursiveMeasure(cutM-player);
	    				return ownMove;
	    			}
	    			if ((bestM == UNKNOWN_MEASURE) || (measure > bestM)) {
	    				bestMove = ownMove;
	    				bestM = measure;
	    			}
    			}
    			else {                      
    				// MIN
	    			if ((cutM != UNKNOWN_MEASURE) && (measure <= cutM)) {
	    				ownMove.setRecursiveMeasure(cutM-player);
	    				return ownMove;
	    			}
	    			if ((bestM == UNKNOWN_MEASURE) || (measure < bestM)) {
	    				bestMove = ownMove;
	    				bestM = measure;
	    			}
    			}
    		}
    		return bestMove;
    	}
    }

    
    
	public static class World extends AbstractCachedMeasureGame {
		public TicTacToe master; 
		public TicTacToe[] ticTacToes;
		public int lastRow;
		public int lastCol;

		public World() {
			master = new TicTacToe();
			ticTacToes = new TicTacToe[9];
			for (int i=0; i<9; i++) {
				ticTacToes[i] = new TicTacToe();
			}
		}

		public World(World w) {
			master = w.master;
			ticTacToes = new TicTacToe[9];
			for (int i=0; i<9; i++) {
				ticTacToes[i] = w.ticTacToes[i];
			}
		}

		public World createMove(int bigRow, int bigCol, int player) {
			World result = new World(this);
			result.doMove(bigRow, bigCol, player);
			return result;
		}

		private void doMove(int bigRow, int bigCol, int player) {
			lastRow = bigRow;
			lastCol = bigCol;
			int mRow = bigRow/3;
			int mCol = bigCol/3;
			int idx = 3*mRow+mCol;
			int subRow = bigRow%3;
			int subCol = bigCol%3; 
			TicTacToe ttt = ticTacToes[idx].createMove(subRow, subCol, player);
			ticTacToes[idx] = ttt;
			int m = ttt.getMeasure();
			if (Math.abs(m) == THREE_OF_THREE) {  // WIN
				master = master.createMove(mRow, mCol, m/THREE_OF_THREE);
			}
			if (m == 0) {
				if (!ttt.hasFree()) { // DRAW
					master = master.createMove(mRow, mCol, 2);
				}
			}
		}


		@Override
		public int calcMeasure() {
			int result = 0;
			result += 10*master.getMeasure();
			for (int i=0; i<9; i++) {
				TicTacToe ttt = ticTacToes[i];
				ttt.getMeasure();
			}
			return result;
		}

		@Override
		public List<World> getAllValidMoves(int player) {
			int mRow = lastRow%3;
			int mCol = lastCol%3;
			if (master.isFree(mRow, mCol)) {
				return getNextMoves(player, mRow, mCol);
			}
			List<World> result = new ArrayList<>();
			for (int r=0; r<3; r++) {
				for (int c=0; c<3; c++) {
					if (master.isFree(r, c)) {
						result.addAll(getNextMoves(player, r, c));
					}
				}
			}
			return result;
		}

		private List<World> getNextMoves(int player, int mRow, int mCol) {
			List<World> result = new ArrayList<>(); 
			int bigRow = mRow*3;
			int bigCol = mCol*3;
			TicTacToe ttt = ticTacToes[3*mRow+mCol];
			for (int r=0; r<3; r++) {
				for (int c=0; c<3; c++) {
					if (ttt.isFree(r, c)) {
						result.add(createMove(bigRow+r, bigCol+c, player));
					}
				}
			}
			return result;
		}

		@Override
		public String toString() {
			return "W["+master+":last=("+lastRow+","+lastCol+")]";
		}
		
	}

	private final static char[] symbol = {'O', ':', 'X', '-'};
	
	private static int[][] threes = {
			{0, 1, 2},
			{3, 4, 5},
			{6, 7, 8},
			{0, 3, 6},
			{1, 4, 7},
			{2, 5, 8},
			{0, 4, 8},
			{2, 4, 6}
	};
	
	private final static int ONE_OF_THREE   =    1;
	private final static int TWO_OF_THREE   =   16;
	private final static int THREE_OF_THREE = 1024;
	private final static int[] MESAURE_IDX_OF_THREE = {0, ONE_OF_THREE, TWO_OF_THREE, THREE_OF_THREE};

	

	public static class TicTacToe extends AbstractCachedMeasureGame {
		public int[] field;
		public int lastRow;
		public int lastCol;
		public TicTacToe() {
			this(new int[9]);
		}
		public TicTacToe(int[] field) {
			this.field = new int[field.length];
			System.arraycopy(field, 0, this.field, 0, field.length);
		}
		public int get(int row, int col) {
			return field[row*3+col];
		}
		public boolean isFree(int row, int col) {
			return hasValue(row, col, 0);
		}
		public boolean hasFree() {
			for (int i:field) {
				if (i==0) {
					return true;
				}
			}
			return false;
		}
		public boolean hasValue(int row, int col, int value) {
			return field[row*3+col] == value;
		}
		private void setValue(int row, int col, int value) {
			field[row*3+col] = value;
		}
		public TicTacToe createMove(int row, int col, int player) {
			TicTacToe result = new TicTacToe(field);
			result.setValue(row, col, player);
			return result;
		}
		public List<TicTacToe> getAllValidMoves(int player) {
			List<TicTacToe> result = new ArrayList<>();
			for (int row=0; row<3; row++) {
				for (int col=0; col<3; col++) {
					if (isFree(row, col)) {
						result.add(createMove(row, col, player));
					}
				}
			}
			return result;
		}
		
		public int calcMeasure() {
			int sumMeasure = 0;
			for (int[] three:threes) {
				int m = measureThree(three);
				if (Math.abs(m) == THREE_OF_THREE) {
					return m;
				}
				sumMeasure += m;
			}
			return sumMeasure;
		}
		
		private int measureThree(int[] three) {
			int player = 0;
			int cnt = 0;
			for (int idx:three) {
				if (field[idx] == 0) {
					continue;
				}
				if (player == 0) {
					player = field[idx];
					cnt = 1;
				}
				else if (field[idx] == player) {
					cnt += 1;
				}
				else {
					return 0;
				}
			}
			return player*MESAURE_IDX_OF_THREE[cnt];
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(symbol[get(0,0)+1]).append(symbol[get(0,1)+1]).append(symbol[get(0,2)+1]).append("|");
			sb.append(symbol[get(1,0)+1]).append(symbol[get(1,1)+1]).append(symbol[get(1,2)+1]).append("|");
			sb.append(symbol[get(2,0)+1]).append(symbol[get(2,1)+1]).append(symbol[get(2,2)+1]).append(")");
			return sb.toString();
		}
	}
	

    public static boolean debug = false;

    public static void main(String args[]) {
      AlphaBetaSearch abs = new AlphaBetaSearch(KI_SEARCH_DEPTH);
      parseArgs(args);
      try (PlaybackScanner in = new PlaybackScanner()) {

        World world = new World();
        // game loop
		int round = 0;
        while (true) {
			round++;
            d("A: "+world);
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            in.recordLinebreak();
            i("opponent: (", opponentRow, ",", opponentCol, ")");
            if (opponentRow != -1) {
            	world = world.createMove(opponentRow, opponentCol, -1);
                d("B: "+world);
            }
            int validActionCount = in.nextInt();
            in.recordLinebreak();
            _i(showValidActions, "actions: ", validActionCount);
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                in.recordAdditional(" ");
                _i(showValidActions, "   (", row, ",", col, ")");
            }
            in.recordLinebreak();
			round++;

			if (round >= 17) {
				debug = true;
			}
			
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            
            if (opponentRow == -1) {
            	world = world.createMove(4, 4, 1);
                d("C: "+world);
            }
            else {
            	world = (World) abs.selectBestMove(1, world);
                d("D: "+world);
            }
            in.outputRecording();
            if (CLEAR_RECORDS) {
                in.clear();
            }
            System.out.println(world.lastRow+" "+world.lastCol);
        }
      }
    }
    
    
    
    private static void parseArgs(String[] args) {
    	for (String arg:args) {
    		if (arg.equals("-r")) {
    			RECORD_INPUT = true;
    		}
    		else if (arg.startsWith("-i")) {
    			PLAYBACK_FILE = arg.substring(2);
    		}
    		else {
				usage(arg);
			}
    	}
        if (RECORD_INPUT) {
        	i("testbefore");
      	  	LOG_LEVEL = LogLevel.FATAL;  
      	  	i("testafter");
        }
	}



	private static void usage(String errMsg) {
		System.err.println("unknown option '"+errMsg+"'");
		System.err.println("usage: player [-r] | [-i<filename>]");
		System.err.println("  -r           : record input");
		System.err.println("  -i<filename> : use input recorded in file <filename>");
		throw new RuntimeException("this could be System.exit(1)");
	}



	public static class PlaybackScanner implements AutoCloseable {
    	
    	private Scanner scanner;
    	StringBuilder record;
    	
    	public PlaybackScanner() {
    		if (PLAYBACK_FILE == null) {
    			scanner = new Scanner(System.in);
    		}
    		else {
    			try {
					scanner = new Scanner(new File(PLAYBACK_FILE));
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e.toString(), e);
				}
    		}
			if (RECORD_INPUT) {
				record = new StringBuilder();
			}
    	}
    	
		public int nextInt() {
			int result = scanner.nextInt();
			if (RECORD_INPUT) {
				record.append(Integer.toString(result)).append(" ");
			}
			return result;
		}

		public void recordLinebreak() {
			if (RECORD_INPUT) {
				record.append("\n");
			}
		}
		
		public void recordAdditional(String txt) {
			if (RECORD_INPUT) {
				record.append(txt);
			}
		}
		
		public void clear() {
			if (RECORD_INPUT) {
				record.setLength(0);
			}
		}

		public String getRecording() {
			if (RECORD_INPUT) {
				return record.toString();
			}
			return "";
		}

		public void outputRecording() {
			if (RECORD_INPUT) {
				System.err.println(getRecording());
			}
		}
		
		@Override
		public void close() {
			if (scanner != null) {
				scanner.close();
				scanner = null;
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