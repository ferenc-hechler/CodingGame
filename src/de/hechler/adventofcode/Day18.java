package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day18 {


	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day18.txt"))) {
			long totalSum = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				long sum = evaluate(line);
				System.out.println(line + " = "+sum);
				totalSum += sum;
			}
			System.out.println("TOTAL: "+totalSum);
		}
	}

	static String NUMBER_RX = "^(\\d+)$"; 
	static String ADD_RX = "^(.*) [+] (\\d+)$"; 
	static String MUL_RX = "^(.*) [*] (\\d+)$"; 
	
	private static long evaluate(String line) {
		line = line.trim();
		if (line.startsWith("(")) {
			int closingBracketPos = findClosingBracket(line, 1);
			String leftTerm = line.substring(1, closingBracketPos).trim();
			String rightFragment = line.substring(closingBracketPos+1).trim();
			long leftNum = evaluate (leftTerm);
			String newLine = leftNum + " " + rightFragment;
			return evaluate(newLine);
		}
		if (line.endsWith(")")) {
			int openingBracketPos = findOpeningBracket(line, line.length()-2);
			String rightTerm = line.substring(openingBracketPos+1, line.length()-1).trim();
			String leftFragment = line.substring(0, openingBracketPos).trim();
			long rightNum = evaluate (rightTerm);
			String newLine = leftFragment + " " + rightNum;
			return evaluate(newLine);
		}
		if (line.matches(NUMBER_RX)) {
			return Long.parseLong(line);
		}
		if (line.matches(ADD_RX)) {
			long num = Long.valueOf(line.replaceFirst(ADD_RX, "$2"));
			String term = line.replaceFirst(ADD_RX, "$1").trim();
			return evaluate(term) + num;
		}
		if (line.matches(MUL_RX)) {
			long num = Long.valueOf(line.replaceFirst(MUL_RX, "$2"));
			String term = line.replaceFirst(MUL_RX, "$1").trim();
			return evaluate(term) * num;
		}
		throw new RuntimeException("invalid term '"+line+"'");
	}

	private static int findOpeningBracket(String line, int startPos) {
		int countClose = 1;
		int pos = startPos;
		while (countClose > 0) {
			int posOpen = line.lastIndexOf('(', pos);
			int posClose = line.lastIndexOf(')', pos);
			if ((posClose != -1) && ((posOpen==-1) || posClose > posOpen)) {
				countClose++;
				pos = posClose - 1;
				continue;
			}
			if (posOpen != -1) {
				countClose--;
				pos = posOpen - 1;
				continue;
			}
			throw new RuntimeException("could not find closing bracket to '"+line+"' from "+startPos);
		}
		return pos+1;
	}

	private static int findClosingBracket(String line, int startPos) {
		int countOpen = 1;
		int pos = startPos;
		while (countOpen > 0) {
			int posOpen = line.indexOf('(', pos);
			int posClose = line.indexOf(')', pos);
			if ((posClose != -1) && ((posOpen==-1) || posClose < posOpen)) {
				countOpen--;
				pos = posClose + 1;
				continue;
			}
			if (posOpen != -1) {
				countOpen++;
				pos = posOpen + 1;
				continue;
			}
			throw new RuntimeException("could not find opening bracket to '"+line+"' from "+startPos);
		}
		return pos-1;
	}

	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day18.txt"))) {
			long totalSum = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				long sum = evaluate2(line);
				System.out.println(line + " = "+sum);
				totalSum += sum;
			}
			System.out.println("TOTAL: "+totalSum);
		}
	}
	

	static String FINAL_ADD_RX = "^(\\d+) [+] (\\d+)$"; 

	private static long evaluate2(String line) {
		line = line.trim();
		int posOpenBracket = line.indexOf('(');
		if (posOpenBracket != -1) {
			int posClosingBracket = findClosingBracket(line, posOpenBracket+1);
			String leftFragment = line.substring(0, posOpenBracket).trim();
			String rightFragment = line.substring(posClosingBracket+1).trim();
			long middleValue = evaluate2(line.substring(posOpenBracket+1, posClosingBracket));
			String newLine = leftFragment+ " " + middleValue + " " +rightFragment;
			return evaluate2(newLine);
		}
		if (line.matches(FINAL_ADD_RX)) {
			long leftNum = Long.parseLong(line.replaceFirst(FINAL_ADD_RX, "$1"));
			long rightNum = Long.parseLong(line.replaceFirst(FINAL_ADD_RX, "$2"));
			return leftNum+rightNum;
		}
		int posAdd = line.indexOf('+');
		if (posAdd != -1) {
			int posOpBefore = line.lastIndexOf('*', posAdd-1);
			int posMulAfter =  line.indexOf('*', posAdd+1);
			int posAddAfter =  line.indexOf('+', posAdd+1);
			int posOpAfter = Math.min(posMulAfter, posAddAfter);
			if (posMulAfter == -1) {
				posOpAfter = posAddAfter;
			}
			if (posAddAfter == -1) {
				posOpAfter = posMulAfter;
			}
			if (posOpAfter == -1) {
				posOpAfter = line.length();
			}
			String leftFragment = line.substring(0, posOpBefore+1).trim();
			String rightFragment = line.substring(posOpAfter).trim();
			long middleValue = evaluate2(line.substring(posOpBefore+1, posOpAfter));
			String newLine = leftFragment+ " " + middleValue + " " +rightFragment;
			return evaluate2(newLine);
		}
		String[] values = line.trim().split("[*]");
		long result = 1;
		for (String value:values) {
			long v = Long.parseLong(value.trim());
			result = result * v;
		}
		return result;
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
