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
	}
	
	public static void mainPart1() throws FileNotFoundException {
		Set<HexPos> turnedHexPosSet = new LinkedHashSet<>();
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



	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
