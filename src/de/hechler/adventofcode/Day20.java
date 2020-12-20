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
			int bit = 1;
			for (int i=0; i<4; i++) {
				bordersMatched[i] = checkBorder(borders[i], border2Tiles);
				bordersFlippedMatched[i] = checkBorder(borders[i], border2Tiles);
				bm = bm | (bordersMatched[i] ? bit : 0);
				bfm = bfm | (bordersFlippedMatched[i] ? bit : 0);
				bit = bit << 1;
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

	static List<Tile> cornerTiles = new ArrayList<>();
	static List<Tile> edgeTiles = new ArrayList<>();
	static List<Tile> innerTiles = new ArrayList<>();


	static int[][] fullImage;
	
	public static void mainPart2() throws FileNotFoundException {
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
				if (possibleBorders.size() != 8) {
					throw new RuntimeException("non unique borders!" + tile);
				}
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
					innerTiles.add(tile);
					System.out.println(tile);
					count++;
				}
			}
			System.out.println("count: "+count);
			System.out.println("---- 3 -----");
			count = 0;
			for (Tile tile:tiles) {
				if (tile.getMaxTileMatch() == 3) {
					edgeTiles.add(tile);
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
					cornerTiles.add(tile);
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
		
		World world = new World(cornerTiles, edgeTiles, innerTiles);
		world.placeAnyCornerTileAsFirst();
		while (!world.isFinished()) {
			world.placeNextTile();
		}
		System.out.println();
		System.out.println("WORLDIMAGE:");

		for (int y=0; y<120; y++) {
			if ((y%10) == 0) {
				System.out.println();
			}
			for (int x=0; x<120; x++) {
				if ((x%10) == 0) {
					System.out.print(" ");
				}
				if (world.get(x,y) == 1) {
					System.out.print("#");
				}
				else {
					System.out.print(".");
				}
			}
			System.out.println();
		}

		fullImage = new int[12*8][12*8];
		
		for (int ty=0; ty<12; ty++) {
			for (int tx=0; tx<12; tx++) {
				for (int oy=0; oy<8; oy++) {
					for (int ox=0; ox<8; ox++) {
						fullImage[8*ty+oy][8*tx+ox] = world.get(10*tx+ox+1, 10*ty+oy+1);
					}
				}
			}
		}
		
		System.out.println();
		System.out.println("FULLIMAGE:");

		for (int y=0; y<96; y++) {
			if ((y%8) == 0) {
				System.out.println();
				System.out.println();
				System.out.println();
			}
			for (int x=0; x<96; x++) {
				if (x==0) {
					System.out.print("  ");
				}
				else if ((x%8) == 0) {
					System.out.print("   ");
				}
				if (fullImage[y][x] == 1) {
					System.out.print("#");
				}
				else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
		
		searchMonster(fullImage);
		
		int countHash = showFullImage();
		System.out.println("count #: "+countHash);
		
	}


	private static int showFullImage() {
		System.out.println();
		System.out.println("MONSTERS:");

		int result = 0;
		for (int y=0; y<96; y++) {
			for (int x=0; x<96; x++) {
				if (fullImage[y][x] > 1) {
					System.out.print("O");
				}
				else if (fullImage[y][x] == 1) {
					System.out.print("#");
					result++;
				}
				else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
		return result;
	}

	static class RTile {
		Tile tile;
		boolean flipped;
		int rot;
		public RTile(Tile tile, boolean flipped, int rot) {
			this.tile = tile;
			this.flipped = flipped;
			this.rot = rot;
		}
		public int getBorder(int dir) {
			int rDir = (dir + rot) % 4;
			if (flipped) {
				return tile.bordersFlipped[rDir];
			}
			return tile.borders[rDir];
		}
		public int get(int x, int y) {
			int rx = x;
			int ry = y;
			if (rot == 1) {
				rx = 9-y;
				ry = x;
			}
			if (rot == 2) {
				rx = 9-x;
				ry = 9-y;
			}
			if (rot == 3) {
				rx = y;
				ry = 9-x;
			}
			if (flipped) {
				rx = 9-rx;
			}
			return tile.image[ry][rx];
		}
		@Override
		public String toString() {
			return "RT["+tile.id+"("+getBorder(0)+">"+getBorder(1)+">"+getBorder(2)+">"+getBorder(3)+")|("+flip(getBorder(0))+"<"+flip(getBorder(1))+"<"+flip(getBorder(2))+"<"+flip(getBorder(3))+")]";
		}
	}
	
	static class World {
		List<Tile> corners;
		List<Tile> edges;
		List<Tile> inner;
		RTile[][] rTiles;
		
		int nextX;
		int nextY;
		
		public World(List<Tile> corners, List<Tile> edges, List<Tile> inner) {
			this.corners = new ArrayList<>(corners);
			this.edges = new ArrayList<>(edges);
			this.inner = new ArrayList<>(inner);
			rTiles = new RTile[12][12];
		}
	
		public int get(int x, int y) {
			int tx = x/10;
			int ty = y/10;
			int ox = x%10;
			int oy = y%10;
			return rTiles[ty][tx].get(ox, oy);
		}

		public void placeAnyCornerTileAsFirst() {
			Tile first = corners.remove(0);
			System.out.println(first.bm);
			int rot;
			if (first.bm == 0b0011) {
				rot = 3;
			}
			else if (first.bm == 0b0110) {
				rot = 0;
			}
			else if (first.bm == 0b1100) {
				rot = 1;
			}
			else if (first.bm == 0b1001) {
				rot = 2;
			}
			else {
				throw new RuntimeException("invalid corner tile "+first);
			}
			rTiles[0][0] = new RTile(first, false, rot);
			nextY = 0;
			nextX = 1;
		}
		
		public void placeNextTile() {
			Tile tile;
			if (nextIsInner()) {
				tile = selectLeftMatchingTile(inner);
			}
			else if (nextIsCorner()) {
				if (nextX == 0) {
					tile = selectTopMatchingTile(corners);
				}
				else {
					tile = selectLeftMatchingTile(corners);
				}
			}
			else {
				if (nextX == 0) {
					tile = selectTopMatchingTile(edges);
				}
				else {
					tile = selectLeftMatchingTile(edges);
				}
			}
			for (int rot = 0; rot<4; rot++) {
				RTile rt = new RTile(tile, false, rot);
				if (checkNextMatch(rt)) {
					rTiles[nextY][nextX] = rt;
					break;
				}
				rt = new RTile(tile, true, rot);
				if (checkNextMatch(rt)) {
					rTiles[nextY][nextX] = rt;
					break;
				}
			}
			if (rTiles[nextY][nextX] == null) {
				throw new RuntimeException("could not find matching tile for x="+nextX+", y="+nextY+" tried "+tile);
			}
			nextX += 1;
			if (nextX > 11) {
				nextX = 0;
				nextY += 1;
			}
		}

		private Tile selectLeftMatchingTile(List<Tile> selection) {
			RTile left = rTiles[nextY][nextX-1];
			int border = left.getBorder(1);
			int flippedBorder = flip(border);
			Set<Tile> matchingTiles = possibleBorders2Tiles.get(flippedBorder);
			for (Tile matchingTile:matchingTiles) {
				if (matchingTile != left.tile) {
					if (!selection.remove(matchingTile)) {
						throw new RuntimeException("wrong matching tile found for x="+nextX+", y="+nextY);
					}
					return matchingTile;
				}
			}
			throw new RuntimeException("no matching tile found for x="+nextX+", y="+nextY);
		}
		
		private Tile selectTopMatchingTile(List<Tile> selection) {
			RTile top = rTiles[nextY-1][nextX];
			int border = top.getBorder(2);
			int flippedBorder = flip(border);
			Set<Tile> matchingTiles = possibleBorders2Tiles.get(flippedBorder);
			for (Tile matchingTile:matchingTiles) {
				if (matchingTile != top.tile) {
					if (!selection.remove(matchingTile)) {
						throw new RuntimeException("wrong matching tile found for x="+nextX+", y="+nextY);
					}
					return matchingTile;
				}
			}
			throw new RuntimeException("no matching tile found for x="+nextX+", y="+nextY);
		}

		private boolean checkNextMatch(RTile rt) {
			if (nextX > 0) {
				if (rTiles[nextY][nextX-1].getBorder(1) != flip(rt.getBorder(3))) {
					return false;
				}
			}
			if (nextY > 0) {
				if (rTiles[nextY-1][nextX].getBorder(2) != flip(rt.getBorder(0))) {
					return false;
				}
			}
			return true;
		}

		public boolean isFinished() {
			return (nextY==12);
		}
		public boolean nextIsCorner() {
			return ((nextX==0) || (nextX==11)) && ((nextY==0) || (nextY==11)); 
 		}
		public boolean nextIsEdge() {
			return !nextIsCorner() && (((nextX==0) || (nextX==11)) || ((nextY==0) || (nextY==11))); 
 		}
		public boolean nextIsInner() {
			return (nextX>0) && (nextX<11) && (nextY>0) && (nextY<11); 
 		}

	}

	
	public static int flip(int border) {
		int result = 0;
		int shift = border;
		for (int i=0; i<10; i++) {
			result = (result<<1) + (shift &0x01);
			shift = shift>>1;
		}
		return result;
	}

	static class Pos {
		int x;
		int y;
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
	}
	
	static class Monster {
		int maxX = 19;
		int maxY = 2;
		List<Pos> monsterPos;
		public Monster() {
			monsterPos = new ArrayList<>();
		}
		public void addPos(Pos pos) {
			monsterPos.add(pos);
		}
		public void mark(Transformer transImg) {
			for (int y=0; y<96-maxY; y++) {
				for (int x=0; x<96-maxX; x++) {
					if (checkAllNonZero(transImg, x, y)) {
						incAll(transImg, x, y);
						//showFullImage();
					}
				}
			}
		}
		private boolean checkAllNonZero(Transformer transImg, int x, int y) {
			for (Pos pos:monsterPos) {
				if (transImg.get(x+pos.x, y+pos.y) == 0) {
					return false;
				}
			}
			return true;
		}
		private void incAll(Transformer transImg, int x, int y) {
			for (Pos pos:monsterPos) {
				transImg.inc(x+pos.x, y+pos.y);
			}
		}

	}
	
	private static Monster createMonster() {
		String[] monster = {
				"                  # ", 
				"#    ##    ##    ###", 
				" #  #  #  #  #  #   "
		};
		Monster result = new Monster();
		for (int y=0; y<3; y++) {
			for (int x=0; x<20; x++) {
				if (monster[y].charAt(x) == '#') {
					result.addPos(new Pos(x,y));
				}
			}
		}
		return result;
	}
	
	
	static class Transformer {
		int[][] baseImg;
		boolean flip;
		int rot;
		Transformer(int[][] img) {
			this.baseImg = img;
			this.flip = false;
			this.rot = 0;
		}
		public void setFlip(boolean flip) {
			this.flip = flip;
		}
		public void setRot(int rot) {
			this.rot = rot;
		}
		public int get(int x, int y) {
			return baseImg[transY(x,y)][transX(x,y)];
		}
		public void inc(int x, int y) {
			baseImg[transY(x,y)][transX(x,y)]++;
		}
		public int transX(int x, int y) {
			int result = -1;
			if (rot == 0) {
				result = x;
			}
			else if (rot == 1) {
				result = 95-y;
			}
			else if (rot == 2) {
				result = 95-x;
			}
			else if (rot == 3) {
				result = y;
			}
			if (flip) {
				result = 95-result;
			}
			return result;
		}
		public int transY(int x, int y) {
			int result = -1;
			if (rot == 0) {
				result = y;
			}
			else if (rot == 1) {
				result = x;
			}
			else if (rot == 2) {
				result = 95-y;
			}
			else if (rot == 3) {
				result = 95-x;
			}
			return result;
		}
	}
	
	private static void searchMonster(int[][] img) {
		Transformer transImg = new Transformer(img);
		Monster monster = createMonster();
		transImg.setFlip(false);
		transImg.setRot(0);
		monster.mark(transImg);
		transImg.setRot(1);
		monster.mark(transImg);
		transImg.setRot(2);
		monster.mark(transImg);
		transImg.setRot(3);
		monster.mark(transImg);
		transImg.setFlip(true);
		transImg.setRot(0);
		monster.mark(transImg);
		transImg.setRot(1);
		monster.mark(transImg);
		transImg.setRot(2);
		monster.mark(transImg);
		transImg.setRot(3);
		monster.mark(transImg);
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
