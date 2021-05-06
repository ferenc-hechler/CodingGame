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
public class Day23e {

	private final static int CIRCLE_SIZE = 1000000;
	
	private static int[] cups = new int[CIRCLE_SIZE];
	private static int currentCup;
	private static int pickedMiddle;
	private static int pickedFrom;
	private static int pickedTo;
	private static int valueP0;
	private static int valueP1;
	private static int valueP2;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day23small.txt"))) {
			String cupNums = scanner.next();
		    currentCup = 0;
			for (char c:cupNums.toCharArray()) {
				cups[currentCup] = c-'0';
				currentCup++;
			}
		}
		System.out.println(cups);
		for (int i=10; i<=CIRCLE_SIZE; i++) {
			cups[i-1]=i;
		}
		
		currentCup = 0;
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
		try {
			System.out.println("FINISHED");
			Thread.sleep(24*3600*1000);
		} catch (InterruptedException e) {} 
	}

	private static int findIndex(int value) {
		for (int i=0; i<CIRCLE_SIZE; i++) {
			if (cups[i] == value) {
				return i;
			}
		}
		return -1;
	}

	private static void crabMove() {
		getPick(currentCup+1);
		int pos = findDestination();
		pos = (pos+1)%CIRCLE_SIZE;
		if (pickedTo > pickedFrom) {
			// no wrap at end of circle in picks
			if (pos < pickedFrom) {
				int blockSize = pickedFrom-pos;
				// shift blocksize values from pos -> pos+3
				System.arraycopy(cups, pos, cups, pos+3, blockSize);
				fillPick(pos);
				currentCup+=3;
			}
			else {
				int blockSize = pos-1 - pickedTo;
				// shift blocksize values from pickedFrom+3 -> pickedFrom
				System.arraycopy(cups, pickedFrom+3, cups, pickedFrom, blockSize);
				fillPick(pos-3);
			}
		}
		else {
			// picks wrap at end of circle
			int blockSize = pickedFrom-pos;
			if (blockSize>=2) {
				// wrapcopy 2 values from pickedFrom-2 to pickedTo-2
				wrapcopy(pickedFrom-2, pickedTo-1, 2);
				// shift blocksize-2 value from pos to pos+3
				System.arraycopy(cups, pos, cups, pos+3, blockSize-2);
				fillPick(pos);
				currentCup+=3;
			}
			else {
				wrapcopy(pos, pos+3, blockSize);
				fillPick(pos);
				currentCup+=3;
			}
					
		}
		currentCup = (currentCup +1) % CIRCLE_SIZE;
	}

	private static void wrapcopy(int src, int dest, int size) {
		for (int i=0; i<size; i++) {
			cups[(dest+i+CIRCLE_SIZE)%CIRCLE_SIZE] = cups[(src+i+CIRCLE_SIZE)%CIRCLE_SIZE]; 
		}
	}

	private static void getPick(int pos) {
		pickedFrom = pos % CIRCLE_SIZE;
		pickedMiddle = (pos+1) % CIRCLE_SIZE;
		pickedTo = (pos+2) % CIRCLE_SIZE;
		valueP0 = cups[pickedFrom];
		valueP1 = cups[pickedMiddle];
		valueP2 = cups[pickedTo];
	}

	private static void fillPick(int pos) {
		cups[pos%CIRCLE_SIZE] = valueP0;
		cups[(pos+1)%CIRCLE_SIZE] = valueP1;
		cups[(pos+2)%CIRCLE_SIZE] = valueP2;
	}

	private static int findDestination() {
		int search = cups[currentCup]-1;
		if (search <= 0) {
			search = CIRCLE_SIZE;
		}
		while ((search == valueP0) || (search == valueP1) || (search == valueP2)) {
			search -= 1;
			if (search <= 0) {
				search = CIRCLE_SIZE;
			}
		}
		return findIndex(search);
	}

	
	private static void showOrderedCups() {
		for (int i=0; i<CIRCLE_SIZE; i++) {
			if (i == 0) {
				System.out.print("(");
			}
			System.out.print(Integer.toString(cups[(i+currentCup)%CIRCLE_SIZE]));
			if (i == 0) {
				System.out.print(")");
			}
			System.out.print(" ");
		}
		System.out.println();
	}
	private static void showCups() {
		for (int i=0; i<CIRCLE_SIZE; i++) {
			if (i == currentCup) {
				System.out.print("(");
			}
			System.out.print(Integer.toString(cups[i]));
			if (i == currentCup) {
				System.out.print(")");
			}
			System.out.print(" ");
		}
		System.out.println();
	}
	
	private static void showPart() {
		int pos1 = findIndex(1);
		System.out.print((pos1-1)+": ");
		for (int i=pos1-2; i<=pos1+2; i++) {
			System.out.print(cups[(i+CIRCLE_SIZE)%CIRCLE_SIZE]+" ");
			
		}
		System.out.println();
	}


	public static void mainPart2() throws FileNotFoundException {
//		try (Scanner scanner = new Scanner(new File("input/day23.txt"))) {
//			cups = new ArrayList<>();
//			String cupNums = scanner.next();
//			for (char c:cupNums.toCharArray()) {
//				cups.add(c-'0');
//			}
//		}
//		System.out.println(cups);
//		for (int i=10; i<=1000000; i++) {
//			cups.add(i);
//		}
//		currentCup = 0;
//		for (int i=1; i<=10000000; i++) {
//			if (i%100000 == 0) {
//				System.out.println(i+": "+(100.0*i/10000000.0));
//			}
//			//System.out.print(i+": ");
//			//showCups();
//			crabMove();
//		}
//		showPart();
	}


	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("ARRAYS");
		mainPart1();
	}

	
}
