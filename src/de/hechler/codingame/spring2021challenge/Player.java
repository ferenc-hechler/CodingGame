package de.hechler.codingame.spring2021challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import de.hechler.codingame.spring2021challenge.Player.World.SunResult;

public class Player {

	int FINAL_DAY = 23;
	
	private final static int[] MAX_NUM_TREES_START = {4, 4, 4, 4}; 
	private final static int[] MAX_NUM_TREES_START_DAY18 = {0, 3, 3, 4}; 
	private final static int[] MAX_NUM_TREES_START_DAY20 = {0, 2, 2, 4}; 
	private final static int[] MAX_NUM_TREES_START_DAY22 = {0, 0, 0, 2}; 
	private final static int[] MAX_NUM_TREES_START_DAY23 = {0, 0, 0, 0}; 
	
	public static LogLevel LOG_LEVEL = LogLevel.DEBUG;
	public static long MY_RANDOM_SEED = 0L;
	
	public enum LogLevel {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	private static String PLAYBACK_FILE = null;
	private static boolean RECORD_INPUT = false;
    private static boolean CLEAR_RECORDS = false;

    static class SeedAction {
    	public int seedCellId;
    	public int treeCellId;
		public SeedAction(int treeCellId, int seedCellId) {
			this.treeCellId = treeCellId;
			this.seedCellId = seedCellId;
		}
		public String toCommand() {
			return "SEED "+treeCellId+" "+seedCellId;
		}
    	
    }
    
	public void run(PlaybackScanner in) {
		// DbgLog.initErr(System.err);
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
                if (possibleMove.startsWith("SEED")) {
                	String[] cmd_tree_seed = possibleMove.split(" ");
                	int treeCellId = Integer.parseInt(cmd_tree_seed[1]);
                	int seedCellId = Integer.parseInt(cmd_tree_seed[2]);
                	world.addPossibleSeedAction(treeCellId, seedCellId);
                }
            }
            i("\n"+world.showWorld());
            SunResult sunCalc = world.calcSun();
            d("SUN: calc=", sunCalc.toString(), "nextSun=", sun, "/", oppSun);
            
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
		
		private final static HexDirection[] COUNTERCLOCKROT = { EAST, NORTHEAST, NORTHWEST, WEST, SOUTHWEST, SOUTHEAST };     
		public static HexDirection fromIdx(int idx) { return COUNTERCLOCKROT[idx]; }
		
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
		public HexDirection invert() {
			switch (this) {
			case EAST:
				return WEST;
			case SOUTHEAST:
				return NORTHWEST;
			case SOUTHWEST:
				return NORTHEAST;
			case WEST:
				return EAST;
			case NORTHWEST:
				return SOUTHEAST;
			case NORTHEAST:
				return SOUTHWEST;
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
				return "+"+treeSize;
			}
			return "-"+treeSize;
		}
		public void clear() {
			owner = 0;
			treeSize = 0;
			dormant = false;
		}
		public boolean isMine() {
			return owner == 1;
		}
		public boolean isOpponent() {
			return owner == -1;
		}
	}
	
	public static class World {
		private int day;
		private int nutrients;
		private int sun;
		private int lastSun;
		private int score;
		private int oppSun;
		private int lastOppSun;
		private int oppScore;
		private boolean oppIsWaiting;
		private HexField[] poles;
		private List<SeedAction> possibleSeedActions;
		private int[] MAX_NUM_TREES; 
		private HexDirection sunDir;
		public World() {
			poles = new HexField[6];
			poles[HexDirection.EAST.idx()] = HexField.get(19);
			poles[HexDirection.NORTHEAST.idx()] = HexField.get(22);
			poles[HexDirection.NORTHWEST.idx()] = HexField.get(25);
			poles[HexDirection.WEST.idx()] = HexField.get(28);
			poles[HexDirection.SOUTHWEST.idx()] = HexField.get(31);
			poles[HexDirection.SOUTHEAST.idx()] = HexField.get(34);
			possibleSeedActions = new ArrayList<>();
			lastSun = 0;
			lastOppSun = 0;
		}
		
		public void clear() {
			for (int id=0; id<37; id++) {
				HexField.get(id).clear();
			}
			possibleSeedActions.clear();
			lastSun = sun;
			lastOppSun = oppSun;
		}

		public void setDayData(int day, int nutrients, int sun, int score, int oppSun, int oppScore, boolean oppIsWaiting) {
			d("DAY = ", day);
			this.day = day;
			this.nutrients = nutrients;
			this.sun = sun;
			this.score = score;
			this.oppSun = oppSun;
			this.oppScore = oppScore;
			this.oppIsWaiting = oppIsWaiting;

			
			this.sunDir = HexDirection.fromIdx(day%6);
			i("DIRECTION = ", sunDir);
			if (day < 18) {
				MAX_NUM_TREES = MAX_NUM_TREES_START;
			} 
			else if (day < 20) {
				MAX_NUM_TREES = MAX_NUM_TREES_START_DAY18;
			}
			else if (day < 22) {
				MAX_NUM_TREES = MAX_NUM_TREES_START_DAY20;
			}
			else if (day < 23) {
				MAX_NUM_TREES = MAX_NUM_TREES_START_DAY22;
			}
			else {
				MAX_NUM_TREES = MAX_NUM_TREES_START_DAY23;
			}
		}
		
		public void addPossibleSeedAction(int treeCellId, int seedCellId) {
			possibleSeedActions.add(new SeedAction(treeCellId, seedCellId));
		}
		
		public static class SunResult {
			public int sun;
			public int oppSun;
			public SunResult(int sun, int oppSun) {
				this.sun = sun;
				this.oppSun = oppSun;
			}
			public void addSun(int addSun) {
				sun += addSun;
			}
			public void addOppSun(int addOppSun) {
				oppSun += addOppSun;
			}
			public String toString() {
				return sun + "/" +oppSun;
			}
		}
		
		public SunResult calcSun() {
			SunResult result = new SunResult(lastSun, lastOppSun);
			HexDirection poleDir = sunDir.invert();
			HexField startField = poles[poleDir.idx()];
			calcSunForRow(result, startField, sunDir);
			HexDirection leftDir = sunDir.nextCounterClockDir();
			for (HexField leftField = startField.getNeighbour(leftDir); leftField != null; leftField = leftField.getNeighbour(leftDir)) {
				calcSunForRow(result, leftField, sunDir);
			};
			HexDirection rightDir = sunDir.nextClockDir();
			for (HexField rightField = startField.getNeighbour(rightDir); rightField != null; rightField = rightField.getNeighbour(rightDir)) {
				calcSunForRow(result, rightField, sunDir);
			};
			return result;
		}

		private void calcSunForRow(SunResult result, HexField hfStart, HexDirection dir) {
			int shadowTree1Size = 0;
			int shadowTree2Size = 0;
			int shadowTree3Size = 0;
			HexField hf = hfStart;
			while (hf != null) {
				int treeSize = hf.treeSize;
				if (treeSize > 0) {
					boolean spooky = (shadowTree3Size==3);
					spooky = spooky || ((shadowTree2Size>=2) && (shadowTree2Size>=treeSize));
					spooky = spooky || ((shadowTree1Size>=1) && (shadowTree1Size>=treeSize));
					if (!spooky) {
						if (hf.isMine()) {
							result.addSun(treeSize);
						}
						else {
							result.addOppSun(treeSize);
						}
					}
				}				
				shadowTree3Size = shadowTree2Size;
				shadowTree2Size = shadowTree1Size;
				shadowTree1Size = treeSize;
				hf = hf.getNeighbour(dir);
			}
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
				result.append(hf.display()).append("  ");
				hf = hf.getNeighbour(HexDirection.EAST);
			}			
			return result.toString();
		}

		public String nextMove() {
			for (int minRichness = 3; minRichness>0; minRichness--) {
				String result = findSeedsToSet(minRichness);
				if (result != null) {
					return result;
				}
				result = findTreeToComplete(minRichness);
				if (result != null) {
					return result;
				}
				result = findTreeToGrow(minRichness);
				if (result != null) {
					return result;
				}
			}
			return "WAIT";
		}
		
		public String findSeedsToSet(int minRichness) {
			if (possibleSeedActions.isEmpty()) {
				return null;
			}
			int numSeeds= countMineTreesWithSize(0);
			if (numSeeds >= MAX_NUM_TREES[0]) {
				return null;
			}
			if (sun < numSeeds) {
				return "WAIT";
			}
			int bestScore = -1;
			SeedAction bestSeedAction = null;
			for (SeedAction posSeedAction:possibleSeedActions) {
				int richness = HexField.get(posSeedAction.seedCellId).richness;
				if (richness >= minRichness) {
					int score = richness;
					if (score > bestScore) {
						bestScore = score;
						bestSeedAction = posSeedAction;
					}
				}
			}
			if (bestSeedAction != null) {
				return bestSeedAction.toCommand();
			}
			return null;
		}

		public String findTreeToComplete(int minRichness) {
			if ((day<20) && (nutrients == 20)) {
				return null;
			}
			int numTree3 = countMineTreesWithSize(3);
			if (numTree3 < MAX_NUM_TREES[3]) {
				return null;
			}
			int completeTree = findMineRichestTreeWithSizeAndMinRich(3, minRichness);
			if (completeTree == -1) {
				return null;
			}
			if (sun < 4) {
				return "WAIT";
			}
			return "COMPLETE "+completeTree;
		}

		public String findTreeToGrow(int minRichness) {
			int numTree3 = countMineTreesWithSize(3);
			if (numTree3 < MAX_NUM_TREES[3]) {
				int growTree = findMineRichestTreeWithSizeAndMinRich(2, minRichness);
				if (growTree != -1) {
					if (sun < 7 + numTree3) {
						return "WAIT";
					}
					return "GROW "+growTree; 
				}
			}
			int numTree2 = countMineTreesWithSize(2);
			if (numTree2 < MAX_NUM_TREES[2]) {
				int growTree = findMineRichestTreeWithSizeAndMinRich(1, minRichness);
				if (growTree != -1) {
					if (sun < 3 + numTree2) {
						return "WAIT";
					}
					return "GROW "+growTree; 
				}
			}
			int numTree1 = countMineTreesWithSize(1);
			if (numTree1 < MAX_NUM_TREES[1]) {
				int growTree = findMineRichestTreeWithSizeAndMinRich(0, minRichness);
				if (growTree != -1) {
					if (sun < 1 + numTree1) {
						return "WAIT";
					}
					return "GROW "+growTree; 
				}
			}
			return null;
		}

		public int countMineTreesWithSize(int size) {
			int result = 0;
			for (int id=0; id<37; id++) {
				if (HexField.get(id).isMine() && (HexField.get(id).treeSize == size)) {
					result += 1;
				};
			}
			return result;
		}

		public int findMineRichestTreeWithSize(int size) {
			return findMineRichestTreeWithSizeAndMinRich(size, 0);
		}
		
		public int findMineRichestTreeWithSizeAndMinRich(int size, int minRichness) {
			int bestMove = -1;
			int bestScore = -1;
			for (int id=0; id<37; id++) {
				HexField hf = HexField.get(id);
				if (hf.isMine() && (!hf.dormant) && (hf.treeSize==size)) {
					if (hf.richness >= minRichness) {
						int score = hf.richness;
						if (score > bestScore) {
							bestMove = id;
							bestScore = score;
						}
					}
				}
			}
			if (bestMove != -1) {
				return bestMove;
			}
			return -1;
		}

	}

	
    private static void parseArgs(String[] args) {
    	if (args == null) {
    		args = new String[0];
    	}
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
    	
    	public PlaybackScanner(InputStream in) {
    		if (PLAYBACK_FILE == null) {
    			scanner = new Scanner(in);
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
				DbgLog.logAlways("created new MY_RANDOM_SEED = "+MY_RANDOM_SEED+"L");
			} else {
				DbgLog.logAlways("using given MY_RANDOM_SEED = "+MY_RANDOM_SEED+"L");
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
	
	public static class DbgLog {
		public static PrintStream sysdbgGui = null;
		public static void initDbgLogGui(PrintStream sdg) {sysdbgGui = sdg; };
		public static PrintStream sysdbgDirect = null;
		public static void initDbgLogDirect(PrintStream sdd) {
			if (sdd != sysdbgGui) {
				sysdbgDirect = sdd; 
			}
		};
	    public static void log(LogLevel lvl, Object... objs) {
	    	if (lvl.ordinal() >= LOG_LEVEL.ordinal()) {
	    		sysdbgGui.print("["+lvl+"] ");
	    		if (sysdbgDirect != null) {
	    			sysdbgDirect.print("["+lvl+"] ");
	    		}
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
	    	// System.err.println(result.toString());
	    	sysdbgGui.println(result.toString());
    		if (sysdbgDirect != null) {
    			sysdbgDirect.println(result.toString());
    		}
	    }
	}
	
    public static void _d(boolean show, Object... objs) { if (show) DbgLog.log(LogLevel.DEBUG, objs); }
    public static void _i(boolean show, Object... objs) { if (show) DbgLog.log(LogLevel.INFO, objs); }
    public static void _e(boolean show, Object... objs) { if (show) DbgLog.log(LogLevel.ERROR, objs); }
    public static void d(Object... objs) { DbgLog.log(LogLevel.DEBUG, objs); }
    public static void i(Object... objs) { DbgLog.log(LogLevel.INFO, objs); }
    public static void e(Object... objs) { DbgLog.log(LogLevel.ERROR, objs); }
 
	private final static PrintStream origSysErr = System.err;

	public static void main(String args[]) {
		DbgLog.initDbgLogGui(System.err);
		DbgLog.initDbgLogDirect(origSysErr);
		parseArgs(args);
		try (PlaybackScanner in = new PlaybackScanner(System.in)) {
			Player player = new Player();
			player.run(in);
		}
	}    
    
}