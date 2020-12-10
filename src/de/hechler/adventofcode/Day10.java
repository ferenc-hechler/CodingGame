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
public class Day10 {

	public static void mainPart1() throws FileNotFoundException {
		int maxValue = 0;
		List<Integer> values = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day10.txt"))) {
			while (scanner.hasNext()) {
				int value = scanner.nextInt();
				values.add(value);
				maxValue = Math.max(maxValue, value);
			}
		}
		values.add(maxValue+3);
		Collections.sort(values);
		int lastValue = 0;
		int[] diffs = new int[4];
		for (int value:values) {
			diffs[value-lastValue]+=1;
			lastValue = value;
		}
		System.out.println(diffs[1]*diffs[3]);
	}

	static int[] vals;
	
	public static void mainPart2() throws FileNotFoundException {
		int maxValue = 0;
		List<Integer> values = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day10.txt"))) {
			while (scanner.hasNext()) {
				int value = scanner.nextInt();
				values.add(value);
				maxValue = Math.max(maxValue, value);
			}
		}
		values.add(maxValue+3);
		values.add(0);
		Collections.sort(values);
		vals = new int[values.size()];
		for (int i=0; i<values.size(); i++) {
			vals[i] = values.get(i);
		}
		int maxIdx = vals.length-1;

		long possibilities = 1;
		int lastStartIdx = 0;
		while (lastStartIdx < maxIdx) {
			int endIdx = maxIdx;
			for (int i=lastStartIdx+1; i<maxIdx; i++) {
				if (vals[i]-vals[i-1] == 3) {
					endIdx = i;
					break;
				}
			}
			long segmentPossibilities = countPossibilities(lastStartIdx, vals[lastStartIdx], endIdx);
			possibilities *= segmentPossibilities;
			lastStartIdx = endIdx;
		}
		System.out.println(possibilities);
				
		possibilities = countPossibilities(0, 0, maxIdx);
		System.out.println(possibilities);

	}

	
	private static long countPossibilities(int startIdx, int lastValue, int maxIdx) {
		long possibilities = 0;
		for (int i=startIdx+1; i<=startIdx+3; i++) {
			if (i>maxIdx) {
				break;
			}
			int v = vals[i];
			if (v > lastValue+3) {
				break;
			}
			possibilities += countPossibilities(i, v, maxIdx);
		}
		if (possibilities == 0) {
			possibilities += 1;
		}
			
		return possibilities;
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
