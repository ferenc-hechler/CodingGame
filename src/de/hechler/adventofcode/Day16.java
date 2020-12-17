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
			return name + "(" + min1 + "-" + max1 + ")/(" + min2 + "," + max2 + ")";
		}
	}

	static List<Rule> rules = new ArrayList<>();
	static Set<Rule>[] possibleRules;

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
				rules.add(rule);
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

	static int cnt;
	static String info;
	
	public static void mainPart2() throws FileNotFoundException {
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
				rules.add(rule);
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
			System.out.println();
			possibleRules = new Set[myTicket.length];
			for (int i=0; i<myTicket.length; i++) {
				possibleRules[i] = new LinkedHashSet<>(rules);
			}
			
			cnt = 0;
			while (scanner.hasNext()) {
				cnt++;
				int[] nearbyTicket = scanTicket(scanner.nextLine());
				//show(nearbyTicket);
				boolean hasError = false;
				for (int n:nearbyTicket) {
					if (matchingRule(n) == null) {
						hasError = true;
						// System.out.println("  ignored: "+n);
						continue;
					}
				}
				if (hasError) {
					continue;
				}
				for (int i=0; i<nearbyTicket.length; i++) {
					Set<Rule> matchingRules = matchingRules(nearbyTicket[i]);
					info = "matching rules for "+nearbyTicket[i]+" are "+matchingRules;
					reteinPR(i, matchingRules);
					removeUniqueRuleFromOthers(i);
				}
			}
		
			long result = 1;
			for (int i=0; i<myTicket.length; i++) {
				if (possibleRules[i].size() != 1) {
					throw new RuntimeException("unclear rule for index "+i+": matching="+possibleRules[i]);
				}
				Rule rule = possibleRules[i].iterator().next();
				if (rule.name.startsWith("departure")) {
						System.out.println("  "+i+": value="+myTicket[i]+", rule="+rule.name);
						result = result * (long)myTicket[i];
				}
			}
			System.out.println("RESULT: "+ result);
		}
	}

	private static void removeUniqueRuleFromOthers(int i) {
		if (possibleRules[i].size() == 1) {
			for (int j=0; j<possibleRules.length; j++) {
				if (i!=j) {
					info = "removing rule "+possibleRules[i];
					int oldSize = possibleRules[j].size();
					removePR(j, possibleRules[i]);
					int newSize = possibleRules[j].size();
					if ((newSize == 1) && (oldSize > newSize)) {
						removeUniqueRuleFromOthers(j);
					}
				}
			}
		}
	}

	static int TRACKING_INDEX = 1;
	
	private static void reteinPR(int fromPR, Set<Rule> retainRules) {
		String oldRules = pr2str(fromPR); 
		possibleRules[fromPR].retainAll(retainRules);
		if (fromPR == TRACKING_INDEX) {
			String newRules = pr2str(fromPR);
			if (!newRules.equals(oldRules)) {
				System.out.println("RETAINING: "+cnt+"  INFO="+info);
				System.out.println("  before: "+oldRules);
				System.out.println("  after : "+newRules);
			}
		}
	}

	private static void removePR(int fromPR, Set<Rule> removeSet) {
		String oldRules = pr2str(fromPR); 
		possibleRules[fromPR].removeAll(removeSet);
		if (fromPR == TRACKING_INDEX) {
			String newRules = pr2str(fromPR);
			if (!newRules.equals(oldRules)) {
				System.out.println("REMOVE: "+cnt+"  INFO="+info);
				System.out.println("  before: "+oldRules);
				System.out.println("  after : "+newRules);
			}
		}
	}


	private static String pr2str(int i) {
		StringBuilder result = new StringBuilder();
		String seperator = "";
		for (Rule rule:possibleRules[i]) {
			result.append(seperator).append(rule.name);
			seperator = ", ";
		}
		return result.toString();
	}

	private static Rule matchingRule(int n) {
		for (Rule rule:rules) {
			if (rule.check(n)) {
				return rule;
			}
		}
		return null;
	}

	private static Set<Rule> matchingRules(int n) {
		Set<Rule> result = new LinkedHashSet<>();
		for (Rule rule:rules) {
			if (rule.check(n)) {
				result.add(rule);
			}
		}
		return result;
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



	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
