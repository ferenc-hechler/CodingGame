package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day06 {



	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		int countSumLetters = 0;
		try (Scanner scanner = new Scanner(new File("input/day06.txt"))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String group = line;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (line.trim().isEmpty()) {
						break;
					}
					group = group + line;
				}
				if (group.trim().isEmpty()) {
					continue;
				}
				Set<Character> letters = new HashSet<>();
				for (int i=0; i<group.length(); i++) {
					letters.add(group.charAt(i));
				}
				countSumLetters += letters.size();
			}
		}
		System.out.println(countSumLetters);
	}
	
	public static void mainPart2(String[] args) throws FileNotFoundException {
		int countSumLetters = 0;
		try (Scanner scanner = new Scanner(new File("input/day06.txt"))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.trim().isEmpty()) {
					continue;
				}
				Set<Character> letters = new HashSet<>();
				for (int i=0; i<line.length(); i++) {
					letters.add(line.charAt(i));
				}
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if (line.trim().isEmpty()) {
						break;
					}
					Set<Character> personLetters = new HashSet<>();
					for (int i=0; i<line.length(); i++) {
						personLetters.add(line.charAt(i));
					}
					letters.retainAll(personLetters);
				}
				countSumLetters += letters.size();
			}
		}
		System.out.println(countSumLetters);
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2(args);
	}
	
}
