package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day24 {

	public static class HexPos {
		int row;
		int col;
		public HexPos() {
			this(0,0);
		}
		public HexPos(int row, int col) {
			super();
			this.row = row;
			this.col = col;
		}
		public HexPos(HexPos hex) {
			this(hex.row, hex.col);
		}
		public void go(String dir) {
			switch (dir) {
			case "e":
				col++;
				break;
			case "w":
				col--;
				break;
			case "ne":
				row--;
				break;
			case "sw":
				row++;
				break;
			case "nw":
				row--;
				col--;
				break;
			case "se":
				row++;
				col++;
				break;
			}
		}
		public void min(HexPos hex) {
			row = Math.min(row, hex.row);
			col = Math.min(col, hex.col);
		}
		public void max(HexPos hex) {
			row = Math.max(row, hex.row);
			col = Math.max(col, hex.col);
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + row;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HexPos other = (HexPos) obj;
			if (col != other.col)
				return false;
			if (row != other.row)
				return false;
			return true;
		}
		@Override
			public String toString() {
				return "H<"+row+","+col+">";
			}
	}

	static Set<HexPos> turnedHexPosSet = new LinkedHashSet<>();
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day24.txt"))) {
			while (scanner.hasNext()) {
				String way = scanner.next();
				HexPos hexPos = goHexPos(way);
				if (turnedHexPosSet.contains(hexPos)) {
					turnedHexPosSet.remove(hexPos);
				}
				else {
					turnedHexPosSet.add(hexPos);
				}
			}
			System.out.println(turnedHexPosSet);
			System.out.println("#black: "+turnedHexPosSet.size());
		}
	}
	
	private static HexPos goHexPos(String way) {
		HexPos result = new HexPos();
		int pos = 0;
		while (pos<way.length()) {
			char c = way.charAt(pos);
			if ((c == 'n') || (c == 's')) {
				result.go(way.substring(pos, pos+2));
				pos += 2;
			}
			else {
				result.go(way.substring(pos, pos+1));
				pos += 1;
			}
		}
		return result;
	}


	static HexPos minHex;
	static HexPos maxHex;
	
	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day24.txt"))) {
			while (scanner.hasNext()) {
				String way = scanner.next();
				HexPos hexPos = goHexPos(way);
				if (turnedHexPosSet.contains(hexPos)) {
					turnedHexPosSet.remove(hexPos);
				}
				else {
					turnedHexPosSet.add(hexPos);
				}
			}
			System.out.println(turnedHexPosSet);
			System.out.println("#black: "+turnedHexPosSet.size());
		}
		for (int i=0; i<=100; i++) {
			System.out.println(i+": "+turnedHexPosSet.size());
			Set<HexPos> nextTurnedHexPosSet = new LinkedHashSet<>();
			getMinMax();
			for (int row=minHex.row-1; row<=maxHex.row+1; row++) {
				for (int col=minHex.col-1; col<=maxHex.col+1; col++) {
					HexPos check = new HexPos(row, col);
					int count = countNeigbours(check);
					if (turnedHexPosSet.contains(check)) {
						// black: keep if 1 or 2 neighbours
						if ((count == 1) || (count == 2)) {
							nextTurnedHexPosSet.add(check);
						}
					}
					else {
						// white: flip if 2 neighbours
						if (count == 2) {
							nextTurnedHexPosSet.add(check);
						}
					}
				}
			}
			turnedHexPosSet = nextTurnedHexPosSet;
		}
	}
	
	

	private static int countNeigbours(HexPos check) {
		int result = 0;
		check.go("e");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("sw");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("w");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("nw");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("ne");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("e");
		result += turnedHexPosSet.contains(check) ? 1 : 0; 
		check.go("sw");
		return result;
	}

	private static void getMinMax() {
		minHex = new HexPos(turnedHexPosSet.iterator().next());
		maxHex = new HexPos(minHex);
		for (HexPos hex:turnedHexPosSet) {
			minHex.min(hex);
			maxHex.max(hex);
		}
	}
	
	

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
