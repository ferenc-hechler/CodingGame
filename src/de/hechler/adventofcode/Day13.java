package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day13 {

	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day13.txt"))) {
			long value = scanner.nextLong();
			String line = scanner.next();
			String[] busnums = line.split("[,]");

			long bestWait = Integer.MAX_VALUE;
			long bestN = 0;
			for (String busnum:busnums) {
				if (busnum.equals("x") || busnum.isEmpty()) {
					continue;
				}
				long n = Long.parseLong(busnum);
				long wait = n - (value % n);
				if (wait < bestWait) {
					bestWait = wait;
					bestN = n;
				}
			}
			System.out.println(bestWait+"*"+bestN+"="+(bestN*bestWait));
		}
	}

	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day13.txt"))) {
			scanner.nextLong();
			String line = scanner.next();
			String[] busnums = line.split("[,]");
			
			long max = 0;
			long maxIdx = 0;
			long[] nums = new long[busnums.length];
			for (int i=0; i<nums.length; i++) {
				if (busnums[i].equals("x")) {
					nums[i] = 0;
				}
				else {
					long l = Long.parseLong(busnums[i]);
					nums[i] = l;
					if (l > max) {
						max = l;
						maxIdx = i;
					}
				}
			}
			
			long step = max;
			long offset = max - maxIdx; 
			long l = offset;
			while (true) {
				boolean found = true;
				for (long i=0; i<nums.length; i++) {
					long v = nums[(int) i]; 
					if (v == 0) {
						continue;
					}
					if (i==0) {
						if (l%v != 0) {
							found = false;
							break;
						}
						continue;
					}
					if ((v-l%v) != i) {
						found = false;
						break;
					}
				}
				if (found) {
					System.out.println(l);
					return;
				}
				l += step;
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
