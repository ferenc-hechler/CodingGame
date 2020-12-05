package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://hack.ainfosec.com/retired-challenges/
 *
 */
public class ais {

	private static String RX_LINE = "^([FB]{7})([LR]{3})$";
	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		int max = 0;
		try (Scanner scanner = new Scanner(new File("input/day05.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.matches(RX_LINE)) {
					throw new RuntimeException("invalid format: '"+line+"'");
				}
				String bin = line.replace('F', '0').replace('B', '1').replace('L', '0').replace('R', '1');
				int n = Integer.parseInt(bin, 2);
				System.out.println(n);
				if (n > max) {
					max = n;
				}
			}
		}
		System.out.println(max);
	}
	
	public static void mainPart2(String[] args) throws FileNotFoundException {
		boolean[] seat = new boolean[1024];
		try (Scanner scanner = new Scanner(new File("input/day05.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.matches(RX_LINE)) {
					throw new RuntimeException("invalid format: '"+line+"'");
				}
				String bin = line.replace('F', '0').replace('B', '1').replace('L', '0').replace('R', '1');
				int n = Integer.parseInt(bin, 2);
				seat[n] = true;
			}
			for (int i=1; i<1023; i++) {
				if (seat[i-1] && seat[i+1] && !seat[i]) {
					System.out.println(i);
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		mainPart2(args);
	}
	
}
