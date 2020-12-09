package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day09 {

	private final static int PREAMBLE_SIZE = 25;
	
	public static void mainPart1() throws FileNotFoundException {
		List<Long> lastValues = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day09.txt"))) {
			for (int i=0; i<PREAMBLE_SIZE; i++) {
				lastValues.add(scanner.nextLong());
			}
			while (scanner.hasNext()) {
				long value = scanner.nextLong();
				boolean found = false;
				for (int i=0; i<PREAMBLE_SIZE-1; i++) {
					for (int j=i+1; j<PREAMBLE_SIZE; j++) {
						if (lastValues.get(i)+lastValues.get(j)==value) {
							found = true;
						}
					}
				}
				if (!found) {
					System.out.println(value);
				}
				lastValues.remove(0);
				lastValues.add(value);
			}
		}
	}

	public static void mainPart2() throws FileNotFoundException {
		long magicnumber = 1639024365;
		List<Long> lastValues = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day09.txt"))) {
			while (scanner.hasNext()) {
				lastValues.add(scanner.nextLong());
			}
			for (int blockSize = 2; blockSize<lastValues.size(); blockSize++) {
				for (int n=0; n<lastValues.size()-blockSize; n++) {
					long sum = 0;
					for (int i=n; i<n+blockSize; i++) {
						sum += lastValues.get(i);
					}
					if (sum == magicnumber) {
						System.out.println(n+" -> "+blockSize);
						long min = lastValues.get(n);
						long max = min;
						for (int i=n; i<n+blockSize; i++) {
							long v = lastValues.get(i);
							min = Math.min(min, v);
							max = Math.max(max, v);
							System.out.println(v);
						}
						System.out.println("min: "+min+", max: "+max+", minmax: "+(min+max));
					}
				}
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
