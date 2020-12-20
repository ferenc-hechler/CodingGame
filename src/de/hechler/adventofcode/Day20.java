package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day20 {

	private final static String HEADER_RX = "^Tile (\\d+):$";
	private final static String IMAGE_LINE_RX = "^([#.]{10})$";

	private final static boolean[] MATCH_FOUR = {
//			0000	0001	0010	0011	0100	0101	0110	0111	1000	1001	1010	1011	1100	1101	1110	1111
			false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	false,	true
	};
	
	private final static boolean[] MATCH_THREE = {
//			0000	0001	0010	0011	0100	0101	0110	0111	1000	1001	1010	1011	1100	1101	1110	1111
			false,	false,	false,	false,	false,	false,	false,	true,	false,	false,	false,	true,	false,	true,	true,	true
	};
	
	private final static boolean[] MATCH_TWO = {
//			0000	0001	0010	0011	0100	0101	0110	0111	1000	1001	1010	1011	1100	1101	1110	1111
			false,	false,	false,	true,	false,	false,	true,	true,	false,	true,	false,	true,	true,	true,	true,	true
	};
	
	static class Tile {
		int id;
		int filled;
		int[][] image;
		int[] borders;
		int[] bordersFlipped;
		boolean[] bordersMatched;
		boolean[] bordersFlippedMatched;
		int bm;
		int bfm;
		int maxBMMatch;
		int maxBFMMatch;
		public Tile(int id) {
			this.id = id;
			this.image = new int[10][10];
			this.filled = 0;
		}
		public void addLine(String imageLine) {
			for (int i=0; i<10; i++) {
				image[filled][i] = (imageLine.charAt(i)=='#') ? 1 : 0;
			}
			filled += 1;
		}
		public void calcBorders() {
			if (filled != 10) {
				throw new RuntimeException("tile not filled completely: "+filled);
			}
			borders = new int[4];
			bordersFlipped = new int[4];
			for (int i=0; i<10; i++) {
				borders[0] = (borders[0] << 1) + image[0][i]; 
				borders[1] = (borders[1] << 1) + image[i][9]; 
				borders[2] = (borders[2] << 1) + image[9][9-i]; 
				borders[3] = (borders[3] << 1) + image[9-i][0]; 
				// flip image horizontally (x -> 9-x)
				bordersFlipped[0] = (bordersFlipped[0] << 1) + image[0][9-i]; 
				bordersFlipped[1] = (bordersFlipped[1] << 1) + image[i][0]; 
				bordersFlipped[2] = (bordersFlipped[2] << 1) + image[9][i]; 
				bordersFlipped[3] = (bordersFlipped[3] << 1) + image[9-i][9]; 
			}
		}
		public Set<Integer> getPossibleBorders() {
			Set<Integer> result = new LinkedHashSet<Integer>();
			result.add(borders[0]);
			result.add(borders[1]);
			result.add(borders[2]);
			result.add(borders[3]);
			result.add(bordersFlipped[0]);
			result.add(bordersFlipped[1]);
			result.add(bordersFlipped[2]);
			result.add(bordersFlipped[3]);
			return result;
		}
		public void checkPossibleBorders(Map<Integer, Set<Tile>> border2Tiles) {
			bm = 0;
			bfm = 0;
			bordersMatched = new boolean[4];
			bordersFlippedMatched = new boolean[4];
			for (int i=0; i<4; i++) {
				bordersMatched[i] = checkBorder(borders[i], border2Tiles);
				bordersFlippedMatched[i] = checkBorder(borders[i], border2Tiles);
				bm = (bm<<1) + (bordersMatched[i] ? 1 : 0);
				bfm = (bfm<<1) + (bordersFlippedMatched[i] ? 1 : 0);
			}
			maxBMMatch = getMaxMatch(bm);
			maxBFMMatch = getMaxMatch(bfm);
		}
		private int getMaxMatch(int mask) {
			if (MATCH_FOUR[mask]) {
				return 4;
			}
			if (MATCH_THREE[mask]) {
				return 3;
			}
			if (MATCH_TWO[mask]) {
				return 2;
			}
			return 0;
		}
		public int getMaxTileMatch() {
			return Math.max(maxBMMatch, maxBFMMatch);
		}
		private boolean checkBorder(int border, Map<Integer, Set<Tile>> border2Tiles) {
			return border2Tiles.get(border).size() > 1;
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tile other = (Tile) obj;
			if (id != other.id)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "T["+id+":("+borders[0]+">"+borders[1]+">"+borders[2]+">"+borders[3]+")="+maxBMMatch+"|("+bordersFlipped[0]+"<"+bordersFlipped[1]+"<"+bordersFlipped[2]+"<"+bordersFlipped[3]+")="+maxBFMMatch+"]";
		}
	}
	
	static List<Tile> tiles;
	static Map<Integer, Set<Tile>> possibleBorders2Tiles;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day20.txt"))) {
			tiles = new ArrayList<>();
			possibleBorders2Tiles = new LinkedHashMap<Integer, Set<Tile>>();
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				System.out.println(line);
				if (line.isEmpty()) {
					continue;
				}
				if (!line.matches(HEADER_RX)) {
					throw new RuntimeException("line does not match HEADER_RX '"+line+"'");
				}
				int tileID = Integer.parseInt(line.replaceFirst(HEADER_RX, "$1"));
				Tile tile = new Tile(tileID);
				for (int i=0; i<10; i++) {
					line = scanner.nextLine().trim();
					System.out.println(line);
					if (!line.matches(IMAGE_LINE_RX)) {
						throw new RuntimeException("line does not match IMAGE_LINE_RX '"+line+"'");
					}
					tile.addLine(line);
				}
				tile.calcBorders();
				System.out.println(tile);
				tiles.add(tile);
				Set<Integer> possibleBorders = tile.getPossibleBorders();
				for (int possibleBorder:possibleBorders) {
					Set<Tile> matchingTiles = possibleBorders2Tiles.get(possibleBorder);
					if (matchingTiles == null) {
						matchingTiles = new LinkedHashSet<>();
						possibleBorders2Tiles.put(possibleBorder, matchingTiles);
					}
					matchingTiles.add(tile);
				}
			}
			System.out.println();
			for (Tile tile:tiles) {
				tile.checkPossibleBorders(possibleBorders2Tiles);
			}
			System.out.println("---- 4 -----");
			int count = 0;
			for (Tile tile:tiles) {
				if (tile.getMaxTileMatch() == 4) {
					System.out.println(tile);
					count++;
				}
			}
			System.out.println("count: "+count);
			System.out.println("---- 3 -----");
			count = 0;
			for (Tile tile:tiles) {
				if (tile.getMaxTileMatch() == 3) {
					System.out.println(tile);
					count++;
				}
			}
			System.out.println("count: "+count);
			System.out.println("---- 2 -----");
			count = 0;
			long productCorners = 1;
			for (Tile tile:tiles) {
				if (tile.getMaxTileMatch() == 2) {
					System.out.println(tile);
					productCorners *= tile.id;
					count++;
				}
			}
			System.out.println("count: "+count);
			System.out.println("PRODUCTCORNERS = "+productCorners);
			System.out.println("---- 0 -----");
			for (Tile tile:tiles) {
				if (tile.getMaxTileMatch() == 0) {
					System.out.println(tile);
				}
			}
		}
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
