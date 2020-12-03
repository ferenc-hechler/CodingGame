package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day02 {

	private static String RX_LINE = "^([0-9]+)-([0-9]+) (.): ([a-z]+)$";
	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		int countValid = 0;
		try (Scanner scanner = new Scanner(new File("input/day02.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.matches(RX_LINE)) {
					throw new RuntimeException("invalid format: '"+line+"'");
				}
				int min = Integer.parseInt(line.replaceFirst(RX_LINE, "$1"));
				int max = Integer.parseInt(line.replaceFirst(RX_LINE, "$2"));
				String letter = line.replaceFirst(RX_LINE, "$3");
				String pw = line.replaceFirst(RX_LINE, "$4");
				int cnt = pw.replaceAll("[^"+letter+"]", "").length();
				if ((cnt < min) || (cnt > max)) {
					System.err.println(line);
				}
				else {
					System.out.println(line);
					countValid += 1;
				}
			}
		}
		System.out.println(countValid);
	}
	
	public static void mainPart2(String[] args) throws FileNotFoundException {
		int countValid = 0;
		try (Scanner scanner = new Scanner(new File("input/day02.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.matches(RX_LINE)) {
					throw new RuntimeException("invalid format: '"+line+"'");
				}
				int pos1 = Integer.parseInt(line.replaceFirst(RX_LINE, "$1"));
				int pos2 = Integer.parseInt(line.replaceFirst(RX_LINE, "$2"));
				String letter = line.replaceFirst(RX_LINE, "$3");
				String pw = line.replaceFirst(RX_LINE, "$4");
				boolean valid = false;
				if ((pw.length()>=pos1) && pw.substring(pos1-1, pos1).equals(letter)) {
					valid = !valid;
				}
				if ((pw.length()>=pos2) && pw.substring(pos2-1, pos2).equals(letter)) {
					valid = !valid;
				}
				if (!valid) {
					System.err.println(line);
				}
				else {
					System.out.println(line);
					countValid += 1;
				}
			}
		}
		System.out.println(countValid);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		mainPart2(args);
	}
	
}
