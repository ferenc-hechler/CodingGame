package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day23b {

	private static int currentCup;
	private static List<Integer> cups;
	private static List<Integer> picked;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day23.txt"))) {
			cups = new ArrayList<>();
			String cupNums = scanner.next();
			for (char c:cupNums.toCharArray()) {
				cups.add(c-'0');
			}
		}
		currentCup = 0;
		for (int i=1; i<=100; i++) {
			//System.out.print(i+": ");
			//showCups();
			crabMoveSmall();
		}
		showCups();
		int pos1 = cups.indexOf(1);
		for (int i=1; i<cups.size(); i++) {
			System.out.print(cups.get((pos1+i)%cups.size()));
		}
	}

	private static void crabMoveSmall() {
		picked = pickCups(cups, 3);
		int pos = findDestinationSmall();
		if (pos < currentCup) {
			currentCup += picked.size();
		}
		cups.addAll(pos+1, picked);
		currentCup = (currentCup +1) %cups.size();
	}

	private static void crabMoveMillion() {
		picked = pickCups(cups, 3);
		int pos = findDestinationMillion();
		if (pos < currentCup) {
			currentCup += picked.size();
		}
		cups.addAll(pos+1, picked);
		currentCup = (currentCup +1) %cups.size();
	}

	private static int findDestinationSmall() {
		int search = cups.get(currentCup)-1;
		while (!cups.contains(search)) {
			search -= 1;
			if (search < 0) {
				search = 9;
			}
		}
		return cups.indexOf(search);
	}

	private static int findDestinationMillion() {
		int search = cups.get(currentCup)-1;
		while (!cups.contains(search)) {
			search -= 1;
			if (search < 0) {
				search = 1000000;
			}
		}
		return cups.indexOf(search);
	}

	private static void showCups() {
		for (int i=0; i<cups.size(); i++) {
			if (i == currentCup) {
				System.out.print("(");
			}
			System.out.print(cups.get(i).toString());
			if (i == currentCup) {
				System.out.print(")");
			}
			System.out.print(" ");
		}
		System.out.println();
	}
	
	private static void showPart() {
		int pos1 = cups.indexOf(1);
		System.out.print((pos1-1)+": ");
		for (int i=pos1-2; i<=pos1+2; i++) {
			System.out.print(cups.get(i%cups.size())+" ");
			
		}
		System.out.println();
	}

	private static List<Integer> pickCups(List<Integer> circle, int count) {
		int from = currentCup + 1;
		List<Integer> result = new ArrayList<>();
		for (int i=0; i<3; i++) {
			from = from % circle.size();
			if (from < currentCup) {
				currentCup--;
			}
			result.add(circle.remove(from));
		}
		return result;
	}

	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day23.txt"))) {
			cups = new ArrayList<>();
			String cupNums = scanner.next();
			for (char c:cupNums.toCharArray()) {
				cups.add(c-'0');
			}
		}
		System.out.println(cups);
		for (int i=10; i<=1000000; i++) {
			cups.add(i);
		}
		currentCup = 0;
		for (int i=1; i<=10000000; i++) {
			if (i%100000 == 0) {
				System.out.println(i+": "+(100.0*i/10000000.0));
			}
			//System.out.print(i+": ");
			//showCups();
			crabMoveMillion();
		}
		showPart();
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
