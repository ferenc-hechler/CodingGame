package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;

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
	
	public static enum FIELD {
	    byr(null, null, 1920, 2002),
	    iyr(null, null, 2010, 2020),
	    eyr(null, null, 2020, 2030),
	    hgt(null, s -> chkHGT(s), 0, -1),
	    hcl("#[0-9a-f]{6}", null, 0, -1),
	    ecl("(amb)|(blu)|(brn)|(gry)|(grn)|(hzl)|(oth)", null, 0, -1),
	    pid("[0-9]{9}", null, 0, -1),
	    cid(null, null, 0, -1);
		
		String regex;
		Predicate<String> chk;
		int min;
		int max;
		FIELD(String regex, Predicate<String> chk, int min, int max) {
			this.regex = regex;
			this.chk = chk;
			this.min = min;
			this.max = max;
		}
		static boolean chkHGT(String s) {
			if (s.endsWith("cm")) {
				return chkNumRange(s.substring(0, s.length()-2), 150, 193);
			}
			else if (s.endsWith("in")) {
				return chkNumRange(s.substring(0, s.length()-2), 59, 76);
			}
			return false;
		}
		public boolean validate(String s) {
			if ((regex != null) && !s.matches(regex)) {
				return false;
			}
			if ((chk != null) && !chk.test(s)) {
				return false;
			}
			return chkNumRange(s, min, max);
		}
		private static boolean chkNumRange(String s, int mini, int maxi) {
			if (mini <= maxi) {
				int i; 
				try {
					i = Integer.parseInt(s);
				}
				catch (Exception ignore) {
					return false;
				}
				if ((i < mini) || (i > maxi)) {
					return false;
				}
			}
			return true;
		}
	}


	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		Set<String> keys = new HashSet<>(Arrays.asList(KEYS));
		int countValid = 0;
		try (Scanner scanner = new Scanner(new File("input/day04-orig.txt"))) {
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
						FIELD field = FIELD.valueOf(word);
						if (field == null) {
							valid = false;
						}
						valid = field.validate(w2[1]);
						if (!valid) {
							break;
						}
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
