package de.hechler.codingame.tictactoe;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player03DepthTwoLook {

	public static LogLevel LOG_LEVEL = LogLevel.DEBUG;
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	
	public final static boolean showValidActions = false;
	
	
	public static class World {
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
			int m = ttt.calcStaticMeasure();
			if (Math.abs(m) == THREE_OF_THREE) {
				master = master.createMove(mRow, mCol, player);
			}
		}

		public World selectBestMove(int player) {
			List<World> ownMoves = getNextMoves(player);
			if (ownMoves.isEmpty()) {
				return null;
			}
			World bestMove = null;
			int bestMesaure = 0;
			for (World ownMove:ownMoves) {
				int m = player*ownMove.calcStaticMeasure();
				if (m == THREE_OF_THREE) {
					return ownMove;
				}
				World enemyMove = ownMove.selectBestEnemyMove(-player);
				if (enemyMove != null) {
					m = -player*enemyMove.calcStaticMeasure();
				}
				if ((bestMove == null) || (m > bestMesaure)) {
					bestMesaure = m;
					bestMove = ownMove;
				}
			}
			return bestMove;
		}

		public World selectBestEnemyMove(int player) {
			List<World> ownMoves = getNextMoves(player);
			if (ownMoves.isEmpty()) {
				return null;
			}
			World bestMove = null;
			int bestMesaure = 0;
			for (World ownMove:ownMoves) {
				int m = player*ownMove.calcStaticMeasure();
				if (m == THREE_OF_THREE) {
					return ownMove;
				}
				if ((bestMove == null) || (m > bestMesaure)) {
					bestMesaure = m;
					bestMove = ownMove;
				}
			}
			return bestMove;
		}

		private int calcStaticMeasure() {
			int result = 0;
			for (int i=0; i<9; i++) {
				int m = ticTacToes[i].calcStaticMeasure();
				if (Math.abs(m) == THREE_OF_THREE) {
					if (master.isFree(i/3, i%3)) {
						master = master.createMove(i/3, i%3, m/THREE_OF_THREE);
					}
				}
				if ((m == 0) && !ticTacToes[i].hasFree()) {
					if (master.isFree(i/3, i%3)) {
						master = master.createMove(i/3, i%3, 2);
					}
				}
				result += m;
			}
			result = result + master.calcStaticMeasure()*10;
			return result;
		}

		private List<World> getNextMoves(int player) {
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

	private final static char[] symbol = {'X', ':', 'O', '-'};
	
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
	private final static int TWO_OF_THREE   =   10;
	private final static int THREE_OF_THREE = 1000;
	private final static int[] MESAURE_IDX_OF_THREE = {0, ONE_OF_THREE, TWO_OF_THREE, THREE_OF_THREE};

	

	public static class TicTacToe {
		public int[] field;
		public int lastRow;
		public int lastCol;
		public TicTacToe() {
			this(new int[9]);
		}
		public TicTacToe selectBestMove(int player) {
			List<TicTacToe> ownMoves = getNextMoves(player);
			if (ownMoves.size() == 0) {
				return null;
			}
			TicTacToe bestMove = null;
			int bestMeasure = 0;
			for (TicTacToe ownMove:ownMoves) {
				int m = player*ownMove.calcStaticMeasure();
				if (m == THREE_OF_THREE) {
					return ownMove;
				}
				if ((bestMove == null) || (m > bestMeasure)) {
					bestMove = ownMove;
					bestMeasure = m;
				}
			}
			return bestMove;
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
		public List<TicTacToe> getNextMoves(int player) {
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
		
		public int calcStaticMeasure() {
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
			return MESAURE_IDX_OF_THREE[cnt];
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
	
	
    public static void main(String args[]) {
      try (Scanner in = new Scanner(System.in)) {

        World world = new World();
        // game loop
        while (true) {
            d("A: "+world);
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            i("opponent: (", opponentRow, ",", opponentCol, ")");
            if (opponentRow != -1) {
            	world = world.createMove(opponentRow, opponentCol, -1);
                d("B: "+world);
            }
            int validActionCount = in.nextInt();
            _i(showValidActions, "actions: ", validActionCount);
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                _i(showValidActions, "   (", row, ",", col, ")");
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            
            if (opponentRow == -1) {
            	world = world.createMove(1, 1, 1);
                d("C: "+world);
            }
            else {
            	world = world.selectBestMove(1);
                d("D: "+world);
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