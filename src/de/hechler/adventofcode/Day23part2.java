package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day23part2 {

	private final static int CIRCLE_SIZE = 1000000;

	public static class Cup {
		public int value;
		public Cup previous;
		public Cup next;
		public Cup(int value) {
			this(value, null, null);
		}
		public Cup(int value, Cup previous, Cup next) {
			this.value = value;
			this.previous = previous;
			this.next = next;
		}
		public Cup pickNext3() {
			Cup firstPick = this.next;
			Cup lastPick= firstPick.next.next;
			Cup nextCircle = lastPick.next;
			this.next = nextCircle;
			nextCircle.previous = this;
			firstPick.previous = lastPick;
			lastPick.next = firstPick;
			return firstPick;
		}
		public void insert(Cup firstPick) {
			Cup lastPick = firstPick.previous;
			Cup nextCircle = this.next;
			this.next = firstPick;
			firstPick.previous = this;
			lastPick.next = nextCircle;
			nextCircle.previous = lastPick;
		}
		@Override
		public String toString() {
			return previous.value+"-("+value+")-"+next.value;
		}
	}
	
	private static Cup[] cups = new Cup[CIRCLE_SIZE+1];
	private static Cup currentCup;
	
	public static void mainPart2() throws FileNotFoundException {
		for (int i=0; i<=CIRCLE_SIZE; i++) {
			cups[i]=new Cup(i);
		}
		for (int i=1; i<=CIRCLE_SIZE; i++) {
			cups[i].previous = cups[i-1];
			cups[i-1].next = cups[i];
		}
		cups[1].previous = cups[CIRCLE_SIZE];
		cups[CIRCLE_SIZE].next= cups[1];

		try (Scanner scanner = new Scanner(new File("input/day23.txt"))) {
			String cupNums = scanner.next();
		    Cup firstCup = null;
		    Cup lastCup = null;
			for (char c:cupNums.toCharArray()) {
				int value = (c-'0');
				Cup nextCup = cups[value];
				if (firstCup == null) {
					firstCup = nextCup;
				}
				else {
					nextCup.previous = lastCup;
					lastCup.next = nextCup;
				}
				lastCup = nextCup;
			}
//			lastCup.next = firstCup;
//			firstCup.previous = lastCup;
			lastCup.next = cups[10];
			cups[10].previous = lastCup;
			cups[CIRCLE_SIZE].next = firstCup;
			firstCup.previous = cups[CIRCLE_SIZE]; 
			currentCup = firstCup;
		}
//		showOrderedCups();
		checkLinks(currentCup, 20);
		
		for (int i=1; i<=10000000; i++) {
			if (i%100000 == 0) {
				System.out.println(i+": "+(100.0*i/10000000.0));
			}
//			System.out.print(i+": ");
//			showOrderedCups();
//			showCups();
			crabMove();
		}
		showPart();
		Cup cupOne = findCup(1);
		long value1 = cupOne.next.value; 
		long value2 = cupOne.next.next.value; 
		System.out.println(value1+" x "+value2+" = "+(value1*value2));
	}

	private static void checkLinks(Cup startCup, int size) {
		Cup cup = startCup;
		System.out.println("NEXT");
		for (int i=1; i<size; i++) {
			System.out.print(cup.value+" ");
			cup = cup.next;
		}
		System.out.println();
		System.out.println("PREVIOUS");
		for (int i=1; i<2*size; i++) {
			System.out.print(cup.value+" ");
			cup = cup.previous;
		}
		System.out.println();
		System.out.println("NEXT");
		for (int i=1; i<2*size; i++) {
			System.out.print(cup.value+" ");
			cup = cup.next;
		}
		System.out.println();
	}

	private static Cup findCup(int value) {
		return cups[value];
	}

	private static void crabMove() {
		Cup pick = currentCup.pickNext3();
		Cup destination = findDestination(pick.value, pick.next.value, pick.next.next.value);
		destination.insert(pick);
		currentCup = currentCup.next;
	}

//	private static void wrapcopy(int src, int dest, int size) {
//		for (int i=0; i<size; i++) {
//			cups[(dest+i+CIRCLE_SIZE)%CIRCLE_SIZE] = cups[(src+i+CIRCLE_SIZE)%CIRCLE_SIZE]; 
//		}
//	}
//
//	private static void getPick(int pos) {
//		pickedFrom = pos % CIRCLE_SIZE;
//		pickedMiddle = (pos+1) % CIRCLE_SIZE;
//		pickedTo = (pos+2) % CIRCLE_SIZE;
//		valueP0 = cups[pickedFrom];
//		valueP1 = cups[pickedMiddle];
//		valueP2 = cups[pickedTo];
//	}
//
//	private static void fillPick(int pos) {
//		cups[pos%CIRCLE_SIZE] = valueP0;
//		cups[(pos+1)%CIRCLE_SIZE] = valueP1;
//		cups[(pos+2)%CIRCLE_SIZE] = valueP2;
//	}

	private static Cup findDestination(int notValue1, int notValue2, int notValue3) {
		int search = currentCup.value-1;
		if (search <= 0) {
			search = CIRCLE_SIZE;
		}
		while ((search == notValue1) || (search == notValue2) || (search == notValue3)) {
			search -= 1;
			if (search <= 0) {
				search = CIRCLE_SIZE;
			}
		}
		return findCup(search);
	}

	
	private static void showOrderedCups() {
		Cup cup = currentCup;
		for (int i=0; i<CIRCLE_SIZE; i++) {
			if (i == 0) {
				System.out.print("(");
			}
			System.out.print(Integer.toString(cup.value));
			if (i == 0) {
				System.out.print(")");
			}
			System.out.print(" ");
			cup = cup.next;
		}
		System.out.println();
	}
//	private static void showCups() {
//		for (int i=0; i<CIRCLE_SIZE; i++) {
//			if (i == currentCup) {
//				System.out.print("(");
//			}
//			System.out.print(Integer.toString(cups[i]));
//			if (i == currentCup) {
//				System.out.print(")");
//			}
//			System.out.print(" ");
//		}
//		System.out.println();
//	}
	
	private static void showPart() {
		Cup cup1 = findCup(1);
		Cup cup = cup1.previous.previous;
		for (int i=0; i<5; i++) {
			System.out.print(cup.value+" ");
			cup = cup.next;
		}
		System.out.println();
	}


//	public static void mainPart2() throws FileNotFoundException {
////		try (Scanner scanner = new Scanner(new File("input/day23.txt"))) {
////			cups = new ArrayList<>();
////			String cupNums = scanner.next();
////			for (char c:cupNums.toCharArray()) {
////				cups.add(c-'0');
////			}
////		}
////		System.out.println(cups);
////		for (int i=10; i<=1000000; i++) {
////			cups.add(i);
////		}
////		currentCup = 0;
////		for (int i=1; i<=10000000; i++) {
////			if (i%100000 == 0) {
////				System.out.println(i+": "+(100.0*i/10000000.0));
////			}
////			//System.out.print(i+": ");
////			//showCups();
////			crabMove();
////		}
////		showPart();
//	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("DOUBLELINKEDLIST");
		mainPart2();
	}

	
}
