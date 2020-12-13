package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day12 {
    //                  E   S   W   N
    //                  0   1   2   3
	static int[] dx = { 1,  0, -1,  0};
	static int[] dy = { 0, -1,  0,  1};

	static int headDir = 0;

	static int wpX = 10;
	static int wpY = 1;
	
	static int posX = 0;
	static int posY = 0;

	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day12.txt"))) {
			while (scanner.hasNext()) {
				String word = scanner.next();
				char cmd = word.charAt(0);
				int num = Integer.parseInt(word.substring(1));
				switch (cmd) {
				case 'E': {
					go(0, num);
					break;
				}
				case 'S': {
					go(1, num);
					break;
				}
				case 'W': {
					go(2, num);
					break;
				}
				case 'N': {
					go(3, num);
					break;
				}
				case 'F': {
					go(headDir, num);
					break;
				}
				case 'L': {
					rotL(num);
					break;
				}
				case 'R': {
					rotR(num);
					break;
				}
				}
			}
		}
		System.out.println(Math.abs(posX)+Math.abs(posY));
	}

	private static void rotL(int num) {
		rotR(360-num);
	}

	private static void rotR(int num) {
		while (num < 0) {
			num += 360;
		}
		while (num >= 360) {
			num -= 360;
		}
		int r = num / 90;
		headDir = (headDir+r)%4;
	}

	private static void go(int dir, int num) {
		while (dir < 0) {
			dir = dir + 4;
		}
		while (dir >= 4) {
			dir = dir - 4;
		}
		posX += dx[dir]*num;
		posY += dy[dir]*num;
	}

	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day12.txt"))) {
			while (scanner.hasNext()) {
				String word = scanner.next();
				char cmd = word.charAt(0);
				int num = Integer.parseInt(word.substring(1));
				switch (cmd) {
				case 'E': {
					moveWP(0, num);
					break;
				}
				case 'S': {
					moveWP(1, num);
					break;
				}
				case 'W': {
					moveWP(2, num);
					break;
				}
				case 'N': {
					moveWP(3, num);
					break;
				}
				case 'F': {
					forward(num);
					break;
				}
				case 'L': {
					rotWPL(num);
					break;
				}
				case 'R': {
					rotWPR(num);
					break;
				}
				}
			}
		}
		System.out.println(Math.abs(posX)+Math.abs(posY));
	}

	private static void forward(int num) {
		posX += num* wpX;
		posY += num* wpY;
	}

	private static void moveWP(int dir, int num) {
		while (dir < 0) {
			dir = dir + 4;
		}
		while (dir >= 4) {
			dir = dir - 4;
		}
		wpX += dx[dir]*num;
		wpY += dy[dir]*num;
	}

	private static void rotWPL(int num) {
		rotWPR(360-num);
	}

	private static void rotWPR(int num) {
		while (num < 0) {
			num += 360;
		}
		while (num >= 360) {
			num -= 360;
		}
		int r = num / 90;
		for (int i=0; i<r; i++) {
			int oldWPX = wpX;
			int oldWPY = wpY;
			wpX = oldWPY;
			wpY = -oldWPX;
		}
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
