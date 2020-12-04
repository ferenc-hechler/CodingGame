package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day04 {

	private final static String[] KEYS = {
		    "byr",
		    "iyr",
		    "eyr",
		    "hgt",
		    "hcl",
		    "ecl",
		    "pid",
		    "cid"
	};

	
	private static String RX_LINE = "^([0-9]+)-([0-9]+) (.): ([a-z]+)$";
	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		Set<String> keys = new HashSet<>(Arrays.asList(KEYS));
		int countValid = 0;
		try (Scanner scanner = new Scanner(new File("input/day04.txt"))) {
			String pass = "";
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.isEmpty()) {
					String[] passFields = pass.trim().split("\\s+");
					Set<String> foundKeys = new HashSet<>(keys);
					boolean valid = true;
					for (int i=0; i<passFields.length; i++) {
						String[] w2 = passFields[i].split(":");
						String word = w2[0];
						if (!keys.contains(word)) {
							valid = false;
							break;
						}
						if (!foundKeys.contains(word)) {
							valid = false;
							break;
						}
						foundKeys.remove(word);
						
					}
					if (valid) {
						valid = (foundKeys.isEmpty()) || (foundKeys.toString().contentEquals("[cid]"));
					}
					if (valid) {
						System.out.println(pass);
						countValid+=1;
					}
					else {
						System.err.println(pass);
					}
					pass = "";
					continue;
				}
				pass = pass + " "+line;
			}
		}
		System.out.println(countValid);
	}
	
	public static void mainPart2(String[] args) throws FileNotFoundException {
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		mainPart1(args);
	}
	
}
