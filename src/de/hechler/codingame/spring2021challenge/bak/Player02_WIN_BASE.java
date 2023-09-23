package de.hechler.codingame.spring2021challenge.bak;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player02_WIN_BASE {


	public static LogLevel LOG_LEVEL = LogLevel.DEBUG;
	public static long MY_RANDOM_SEED = 0L;
	
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	private static String PLAYBACK_FILE = null;
	private static boolean RECORD_INPUT = false;
    private static boolean CLEAR_RECORDS = false;


	public void run(PlaybackScanner in) {
        int numberOfCells = in.nextInt(); // 37
        d("#CELLS:", numberOfCells);
        HexField.init();
        for (int i = 0; i < numberOfCells; i++) {
            int index = in.nextInt(); // 0 is the center cell, the next cells spiral outwards
            int richness = in.nextInt(); // 0 if the cell is unusable, 1-3 for usable cells
            int neigh0 = in.nextInt(); // the index of the neighbouring cell for each direction
            int neigh1 = in.nextInt();
            int neigh2 = in.nextInt();
            int neigh3 = in.nextInt();
            int neigh4 = in.nextInt();
            int neigh5 = in.nextInt();
            d("  #", index, " rich:", richness, " neigh:(",neigh0,",",neigh1,",",neigh2,",",neigh3,",",neigh4,",",neigh5,")");
            HexField.get(index).setRichness(richness);
            HexField.get(index).setNeighbours(neigh0, neigh1, neigh2, neigh3, neigh4, neigh5);
        }

        World world = new World();
        // game loop
        while (true) {
            world.clear();
            int day = in.nextInt(); // the game lasts 24 days: 0-23
            int nutrients = in.nextInt(); // the base score you gain from the next COMPLETE action
            int sun = in.nextInt(); // your sun points
            int score = in.nextInt(); // your current score
            int oppSun = in.nextInt(); // opponent's sun points
            int oppScore = in.nextInt(); // opponent's score
            boolean oppIsWaiting = in.nextInt() != 0; // whether your opponent is asleep until the next day
            d("DAY:", day, " nut:", nutrients, " sun:", sun, " score:", score, " (osun:", oppSun, " oscore:", oppScore, (oppIsWaiting?" WAIT":""),")");
            world.setDayData(day, nutrients, sun, score, oppSun, oppScore, oppIsWaiting);
            int numberOfTrees = in.nextInt(); // the current amount of trees
            d("#TREES:", numberOfTrees);
            for (int i = 0; i < numberOfTrees; i++) {
                int cellIndex = in.nextInt(); // location of this tree
                int size = in.nextInt(); // size of this tree: 0-3
                boolean isMine = in.nextInt() != 0; // 1 if this is your tree
                boolean isDormant = in.nextInt() != 0; // 1 if this tree is dormant
                d("  #", cellIndex, " size:", size, (isMine?" X":" O"), (isDormant?" dorm":""));
                HexField.get(cellIndex).addTree(isMine, size, isDormant);
            }
            int numberOfPossibleMoves = in.nextInt();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            d("#MOVES:", numberOfPossibleMoves);
            for (int i = 0; i < numberOfPossibleMoves; i++) {
                String possibleMove = in.nextLine();
                d("  ", possibleMove);
            }
            i("\n"+world.showWorld());

            in.outputRecording();
            if (CLEAR_RECORDS) {
                in.clear();
            }
            
            // GROW cellIdx | SEED sourceIdx targetIdx | COMPLETE cellIdx | WAIT <message>
            String move = world.nextMove();
            System.out.println(move);
        }
    }

	
	public static class HexPos {
		public int row;
		public int col;
		public HexPos(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
	
	public enum HexDirection {
		EAST(0),
		NORTHEAST(1),
		NORTHWEST(2),
		WEST(3),
		SOUTHWEST(4),
		SOUTHEAST(5);
		private int dirIdx;
		HexDirection(int dirIdx) {
			this.dirIdx=dirIdx;
		}
		public int idx() {
			return dirIdx;
		}
		public HexDirection nextClockDir() {
			switch (this) {
			case EAST:
				return SOUTHEAST;
			case SOUTHEAST:
				return SOUTHWEST;
			case SOUTHWEST:
				return WEST;
			case WEST:
				return NORTHWEST;
			case NORTHWEST:
				return NORTHEAST;
			case NORTHEAST:
				return EAST;
			default:
				return null;
			}
		}
		public HexDirection nextCounterClockDir() {
			switch (this) {
			case SOUTHEAST:
				return EAST;
			case SOUTHWEST:
				return SOUTHEAST;
			case WEST:
				return SOUTHWEST;
			case NORTHWEST:
				return WEST;
			case NORTHEAST:
				return NORTHWEST;
			case EAST:
				return NORTHEAST;
			default:
				return null;
			}
		}
	}
	
	public static class HexField {
		
		public static HexField[] id2hexfields;
		public static void init() {
			if (id2hexfields != null) {
				return;
			}
			id2hexfields = new HexField[37]; 
			for (int i=0; i<37; i++) {
				id2hexfields[i] = new HexField(i);
			}
		}
		public int id;
		public HexField[] neighbours;
		public int richness;
		public int owner;
		public int treeSize;
		public boolean dormant;
		private HexField(int id) {
			this.id = id;
		}
		public static HexField get(int id) {
			if (id == -1) {
				return null;
			}
			return id2hexfields[id];
		}
		public void setRichness(int richness) {
			this.richness = richness;
		}
		public void setNeighbours(int idE, int idNE, int idNW, int idW, int idSW, int idSE) {
			neighbours = new HexField[] { 
					HexField.get(idE), HexField.get(idNE), HexField.get(idNW), 
					HexField.get(idW), HexField.get(idSW), HexField.get(idSE) 
				};
		}
		public void addTree(boolean mine, int size, boolean dormant) {
			this.owner = mine ? 1 : -1;
			this.treeSize = size;
			this.dormant = dormant;
		}
		public boolean hasNeighbour(HexDirection dir) {
			return neighbours[dir.idx()] != null;
		}
		public HexField getNeighbour(HexDirection dir) {
			return neighbours[dir.idx()];
		}
		public String display() {
			if (owner == 0) {
				return "<>";
			}
			if (owner == 1) {
				return "T"+treeSize;
			}
			return "t"+treeSize;
		}
		public void clear() {
			owner = 0;
			treeSize = 0;
			dormant = false;
		}
		public boolean isMine() {
			return owner == 1;
		}
	}
	
	public static class World {
		private int day;
		private int nutrients;
		private int sun;
		private int score;
		private int oppSun;
		private int oppScore;
		private boolean oppIsWaiting;
		private HexField[] poles;
		public World() {
			poles = new HexField[6];
			poles[HexDirection.EAST.idx()] = HexField.get(19);
			poles[HexDirection.NORTHEAST.idx()] = HexField.get(22);
			poles[HexDirection.NORTHWEST.idx()] = HexField.get(25);
			poles[HexDirection.WEST.idx()] = HexField.get(28);
			poles[HexDirection.SOUTHWEST.idx()] = HexField.get(31);
			poles[HexDirection.SOUTHEAST.idx()] = HexField.get(34);
		}
		
		public void clear() {
			for (int id=0; id<37; id++) {
				HexField.get(id).clear();
			}
		}

		public void setDayData(int day, int nutrients, int sun, int score, int oppSun, int oppScore, boolean oppIsWaiting) {
			this.day = day;
			this.nutrients = nutrients;
			this.sun = sun;
			this.score = score;
			this.oppSun = oppSun;
			this.oppScore = oppScore;
			this.oppIsWaiting = oppIsWaiting;
		}

		public String showWorld() {
			StringBuilder result = new StringBuilder();
			HexField currentHF = poles[HexDirection.NORTHWEST.idx()];
			result.append("      ").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHWEST);
			result.append("    ").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHWEST);
			result.append("  ").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHWEST);
			result.append("").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHEAST);
			result.append("  ").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHEAST);
			result.append("    ").append(generateRow(currentHF)).append("\n");
			currentHF = currentHF.getNeighbour(HexDirection.SOUTHEAST);
			result.append("      ").append(generateRow(currentHF)).append("\n");
			return result.toString();
		}

		private String generateRow(HexField hf) {
			StringBuilder result = new StringBuilder();
			while (hf != null) {
				result.append(hf.display()).append(" ");
				hf = hf.getNeighbour(HexDirection.EAST);
			}			
			return result.toString();
		}

		public String nextMove() {
			if (sun < 4) {
				return "WAIT";
			}
			int bestMove = -1;
			int bestScore = -1;
			for (int id=0; id<37; id++) {
				if (HexField.get(id).isMine()) {
					int score = HexField.get(id).richness;
					if (score > bestScore) {
						bestMove = id;
						bestScore = score;
					}
				}
			}
			if (bestMove != -1) {
				return "COMPLETE "+bestMove;
			}
			return "WAIT";
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
      	  	LOG_LEVEL = LogLevel.FATAL;  
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
    	
		public boolean hasNextLine() {
			return scanner.hasNextLine();
		}

		public String nextLine() {
			String result = scanner.nextLine();
			if (RECORD_INPUT) {
				record.append(result);
				recordLinebreak();
			}
			return result;
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
    
	public static class MyRandom {
		private static MyRandom instance;
		public static MyRandom getInstance() {
			if (instance == null) {
				instance = new MyRandom();
			}
			return instance;
		}
		private static Random rand;
		private MyRandom() {
			if (MY_RANDOM_SEED == 0) {
				MY_RANDOM_SEED = System.currentTimeMillis();
				logAlways("created new MY_RANDOM_SEED = "+MY_RANDOM_SEED+"L");
			} else {
				logAlways("using given MY_RANDOM_SEED = "+MY_RANDOM_SEED+"L");
			}
			rand = new Random(MY_RANDOM_SEED);
		}
		/** @return value from 0..(bound.1) */
		private int nextInt(int bound) { return rand.nextInt(bound); }
		/** Fisher-Yates shuffle algorithm @see https://medium.com/@joshfoster_14132/best-javascript-shuffle-algorithm-c2c8057a3bc1 */
		public <T> void shuffleArray(List<T> array) {
			for (int currentIndex = array.size()-1; currentIndex>=0; currentIndex--) {
				// pick a random element (index)
				int randomIndex = nextInt(currentIndex+1);
				// swap last element with random element.
				T temporayValue = array.get(currentIndex);
				array.set(currentIndex, array.get(randomIndex));
				array.set(randomIndex, temporayValue);
			}
		}
	};
	
	
	
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
 
	public static void main(String args[]) {
	  parseArgs(args);
	  try (PlaybackScanner in = new PlaybackScanner()) {
		  Player02_WIN_BASE player = new Player02_WIN_BASE();
		  player.run(in);
	  }
	}
    
    
}