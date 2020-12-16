package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day16 {

	final static String RULE_RX = "([a-z ]+): (\\d+)-(\\d+) or (\\d+)-(\\d+)";
	final static String TICKET_RX = "(\\d+)(,\\d+)*";

	static class Rule {
		String name;
		int min1;
		int max1;
		int min2;
		int max2;
		boolean check(int n) {
			return ((n>=min1) && (n<=max1)) || ((n>=min2) && (n<=max2)); 
		}
		@Override
		public String toString() {
			return "Rule [name=" + name + ", min1=" + min1 + ", max1=" + max1 + ", min2=" + min2 + ", max2=" + max2
					+ "]";
		}
	}

	static Map<String, Rule> rules = new LinkedHashMap<>();

	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day16.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.isEmpty()) {
					break;
				}
				
				if (!line.matches(RULE_RX)) {
					throw new RuntimeException("invalid rule '"+line+"'");
				}
				Rule rule = new Rule();
				rule.name = line.replaceFirst(RULE_RX, "$1");
				rule.min1 = Integer.parseInt(line.replaceFirst(RULE_RX, "$2"));
				rule.max1 = Integer.parseInt(line.replaceFirst(RULE_RX, "$3"));
				rule.min2 = Integer.parseInt(line.replaceFirst(RULE_RX, "$4"));
				rule.max2 = Integer.parseInt(line.replaceFirst(RULE_RX, "$5"));
				rules.put(rule.name, rule);
				System.out.println(rule);
			}
			if (!scanner.nextLine().equals("your ticket:")) {
				throw new RuntimeException("missing your ticket:");
			}
			int[] myTicket = scanTicket(scanner.nextLine());
			show(myTicket);
			if (!scanner.nextLine().isEmpty()) {
				throw new RuntimeException("missing seperator");
			}
			if (!scanner.nextLine().equals("nearby tickets:")) {
				throw new RuntimeException("missing nearby tickets:");
			}
			int sumError = 0;
			while (scanner.hasNext()) {
				int[] nearbyTicket = scanTicket(scanner.nextLine());
				show(nearbyTicket);
				for (int n:nearbyTicket) {
					if (matchingRule(n) == null) {
						System.out.println("  "+n);
						sumError += n;
					}
				}
				
			}
			System.out.println(sumError);
		}
	}


	private static Rule matchingRule(int n) {
		for (Rule rule:rules.values()) {
			if (rule.check(n)) {
				return rule;
			}
		}
		return null;
	}


	private static void show(int[] intArr) {
		for (int i=0; i<intArr.length; i++) {
			System.out.print(intArr[i]+",");
		}
		System.out.println();
	}


	private static int[] scanTicket(String line) {
		if (!line.matches(TICKET_RX)) {
			throw new RuntimeException("invalid ticket format.");
		}
		String[] nums = line.split(",");
		int[] result = new int[nums.length];
		for (int i=0; i<nums.length; i++) {
			result[i] = Integer.parseInt(nums[i]);
		}
		return result;
	}


	public static void mainPart2() throws FileNotFoundException {
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
