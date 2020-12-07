package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day07 {

	private final static String RX_LINE = "^([a-z]+ [a-z]+) bags contain (([0-9]+ [a-z]+) ([a-z]+) bags?(, ([0-9]+ [a-z]+) ([a-z]+) bags?)*).$";
	private final static String RX_LINE2 = "^([a-z]+ [a-z]+) bags contain no other bags.$";
	private final static String RX_CONSTRAINT = "^([0-9]+) ([a-z]+ [a-z]+) bags?$";

	private static class Constraint {
		public String color;
		public int num;
		public Constraint(String color, int num) {
			this.color = color;
			this.num = num;
		}
		@Override
			public String toString() {
				return color+"#"+num;
			}
	}

	
	private static class Bag {
		public String color;
		public Map<String, Constraint> constraints;
		public Bag(String color) {
			this.color = color;
			this.constraints = new LinkedHashMap<>();
		}
		@Override
		public String toString() {
			return "B["+color+"->"+constraints+"]";
		}
		public void addConstraint(String cColor, int cNum) {
			Constraint oldConstraint = constraints.put(cColor, new Constraint(cColor, cNum));
			if (oldConstraint != null) {
				throw new RuntimeException("duplicate constraint "+oldConstraint);
			}
		}
	}

	private static class FilledBag {
		public int count;
		public Bag bag;
		public List<FilledBag> children;
		public FilledBag(int count, Bag bag) {
			this.count = count;
			this.bag = bag;
			this.children = new ArrayList<>();
		}
		@Override
		public String toString() {
			return "FB["+bag+"->"+children+"]";
		}
		public void fillBag(Map<String, Bag> templates) {
			for (Constraint constraint:bag.constraints.values()) {
				FilledBag child = new FilledBag(constraint.num, templates.get(constraint.color));
				child.fillBag(templates);
				children.add(child);
			}
		}
		public boolean findRecursive(String searchColor) {
			if (bag.color.equals(searchColor)) {
				return true;
			}
			for (FilledBag child:children) {
				if (child.findRecursive(searchColor)) {
					return true;
				}
			}
			return false;
		}
		public int countBags() {
			int result = 1;
			for (FilledBag child:children) {
				result += child.countBags();
			}
			return count*result;
		}
	}


	private Map<String, Bag> coloredBagTemplates;
	private List<FilledBag> filledBags;
	
	public void readData() {
		try (Scanner scanner = new Scanner(new File("input/day07.txt"))) {
			coloredBagTemplates = new HashMap<>();		
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.isEmpty())
					continue;
				if (line.matches(RX_LINE)) {
					System.out.println(line);
					String bagColor = line.replaceFirst(RX_LINE, "$1");
					Bag newBag = new Bag(bagColor);
					Bag oldBag = coloredBagTemplates.put(bagColor, newBag);
					if (oldBag != null) {
						throw new RuntimeException("duplicate association for "+bagColor);
					}
					String[] constraints = line.replaceFirst(RX_LINE, "$2").split("\\s*,\\s*"); 
					for (String contraint:constraints) {
						if (!contraint.matches(RX_CONSTRAINT)) {
							throw new RuntimeException("unmatched constraint "+contraint);
						}
						int num = Integer.parseInt(contraint.replaceFirst(RX_CONSTRAINT, "$1"));
						String color = contraint.replaceFirst(RX_CONSTRAINT, "$2");
						newBag.addConstraint(color, num);
					}
				}
				else if (line.matches(RX_LINE2)) {
						System.out.println(line);
						String bagColor = line.replaceFirst(RX_LINE2, "$1");
						Bag newBag = new Bag(bagColor);
						Bag oldBag = coloredBagTemplates.put(bagColor, newBag);
						if (oldBag != null) {
							throw new RuntimeException("duplicate association for "+bagColor);
						}
				}
				else {
					throw new RuntimeException("unmatched line "+line);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		String bags = coloredBagTemplates.toString();			
		System.out.println("-----");					
		System.out.println(bags.substring(0,1024));			
		System.out.println("-----");					
	}

	private void createFilledBags() {
		filledBags = new ArrayList<>();
		for (Bag bag:coloredBagTemplates.values()) {
			FilledBag fb = new FilledBag(1, bag);
			fb.fillBag(coloredBagTemplates);
			filledBags.add(fb);
		}
	}

	private int searchNumberShinyGolden() {
		String searchColor = "shiny gold";
		int result = 0;
		for (FilledBag fb:filledBags) {
			if (fb.bag.color.equals(searchColor)) {
				continue;
			}
			if (fb.bag.color.equals("drab teal")) {
				System.out.println(fb.bag);
			}
			if (fb.findRecursive(searchColor)) {
				result += 1;
			}
		}
		return result;
	}

	private int countBagsInShinyGolden() {
		String searchColor = "shiny gold";
		int result = 0;
		for (FilledBag fb:filledBags) {
			if (fb.bag.color.equals(searchColor)) {
				return fb.countBags();
			}
		}
		return -1;
	}

	public static void mainPart1(String[] args) throws FileNotFoundException {
		Day07 app = new Day07();
		app.readData();
		app.createFilledBags();
		int countShinyGolden = app.searchNumberShinyGolden();
		System.out.println("part1: "+countShinyGolden);
		int countInShiyGold = app.countBagsInShinyGolden();
		System.out.println("part2: "+(countInShiyGold-1));
	}



	public static void main(String[] args) throws FileNotFoundException {
		mainPart1(args);
	}

	
}
