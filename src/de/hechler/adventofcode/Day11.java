package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day11 {

	private static int[][] seats;
	
	private static int width;
	private static int height;
	
	public static void mainPart1() throws FileNotFoundException {
		List<int[]> seatLines = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day11.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				int[] seatLine = new int[line.length()];
				for (int i=0; i<line.length(); i++) {
					seatLine[i] = (line.charAt(i) == '.')?0:1;
				}
				seatLines.add(seatLine);
			}
			seats = (int[][]) seatLines.toArray(new int[seatLines.size()][]);
		}
		height = seats.length;
		width = seats[0].length;
		showSeats();

		boolean changed = true;
		while (changed) {
			changed = fillSeats();
			if (changed) {
				System.out.println();
				showSeats();
			}
		}
		
		
	}

	public static void mainPart2() throws FileNotFoundException {
		List<int[]> seatLines = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day11.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				int[] seatLine = new int[line.length()];
				for (int i=0; i<line.length(); i++) {
					seatLine[i] = (line.charAt(i) == '.')?0:1;
				}
				seatLines.add(seatLine);
			}
			seats = (int[][]) seatLines.toArray(new int[seatLines.size()][]);
		}
		height = seats.length;
		width = seats[0].length;
		showSeats();

		boolean changed = true;
		while (changed) {
			changed = fillVisibleSeats();
			if (changed) {
				System.out.println();
				showSeats();
			}
		}
		
		
	}


	private static boolean fillSeats() {
		int[][] newSeats = new int[height][width];
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (seats[y][x] == 0) {
					continue;
				}
				boolean isOccupied = seats[y][x] == 2;
				int numberAdjacent = getAdjacentOccupied(x, y);
				if (isOccupied) {
					if (numberAdjacent >= 4) {
						newSeats[y][x] = 1;
					}
					else {
						newSeats[y][x] = 2;
					}
				}
				else {   // empty
					if (numberAdjacent==0) {
						newSeats[y][x] = 2;
					}
					else {
						newSeats[y][x] = 1;
					}
 				}
			}
			
		}
		int count = 0;
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (seats[y][x] != newSeats[y][x]) {
					seats = newSeats;
					return true;
				}
				if (seats[y][x] == 2) {
					count += 1;
				}
			}
		}
		System.out.println("RESULT: " + count);
		return false;
	}


	private static int getAdjacentOccupied(int x, int y) {
		int result = 0;
		result += checkOccupied(x-1, y-1);
		result += checkOccupied(x  , y-1);
		result += checkOccupied(x+1, y-1);
		result += checkOccupied(x-1, y);
		result += checkOccupied(x+1, y);
		result += checkOccupied(x-1, y+1);
		result += checkOccupied(x  , y+1);
		result += checkOccupied(x+1, y+1);
		return result;
	}


	private static int checkOccupied(int x, int y) {
		if ((x<0) || (y<0) || (x>=width) || (y>=height)) {
			return 0;
		}
		if (seats[y][x] == 2) {
			return 1;
		}
		return 0;
	}

	private static boolean fillVisibleSeats() {
		int[][] newSeats = new int[height][width];
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (seats[y][x] == 0) {
					continue;
				}
				boolean isOccupied = seats[y][x] == 2;
				int numberVisible = getVisibleSeatsOccupied(x, y);
				if (isOccupied) {
					if (numberVisible >= 5) {
						newSeats[y][x] = 1;
					}
					else {
						newSeats[y][x] = 2;
					}
				}
				else {   // empty
					if (numberVisible==0) {
						newSeats[y][x] = 2;
					}
					else {
						newSeats[y][x] = 1;
					}
 				}
			}
			
		}
		int count = 0;
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (seats[y][x] != newSeats[y][x]) {
					seats = newSeats;
					return true;
				}
				if (seats[y][x] == 2) {
					count += 1;
				}
			}
		}
		System.out.println("RESULT: " + count);
		return false;
	}


	private static int getVisibleSeatsOccupied(int x, int y) {
		int result = 0;
		result += checkVisibleOccupied(x, y, -1, -1);
		result += checkVisibleOccupied(x, y,  0, -1);
		result += checkVisibleOccupied(x, y, +1, -1);
		result += checkVisibleOccupied(x, y, -1,  0);
		result += checkVisibleOccupied(x, y, +1,  0);
		result += checkVisibleOccupied(x, y, -1, +1);
		result += checkVisibleOccupied(x, y,  0, +1);
		result += checkVisibleOccupied(x, y, +1, +1);
		return result;
	}



	private static int checkVisibleOccupied(int startX, int startY, int dx, int dy) {
		int x = startX+dx;
		int y = startY+dy;
		while ((x>=0) && (x<width) && (y>=0) && (y<height)) {
			if (seats[y][x] == 2) {
				return 1;
			}
			if (seats[y][x] == 1) {
				return 0;
			}
			x += dx;
			y += dy;
		}
		return 0;
	}



	final static String[] symbol = {".", "L", "#"}; 
	
	private static void showSeats() {
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				System.out.print(symbol[seats[y][x]]);
			}
			System.out.println();
		}
	}

	
	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
