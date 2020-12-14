package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day13c {

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
			
			long[] nums = new long[busnums.length];
			for (int i=0; i<nums.length; i++) {
				if (busnums[i].equals("x")) {
					nums[i] = 0;
				}
				else {
					long l = Long.parseLong(busnums[i]);
					nums[i] = l;
				}
			}

			System.out.println("#"+nums.length);
			
			long zyklus = nums[0];
			long startSearch = 0;
			for (long i=1; i<nums.length; i++) {
				long v = nums[(int) i]; 
				if (v==0) {
					continue;
				}
				long rest = v-i;
				while (rest < 0) {
					rest += v;
				}
				System.out.println(i+": "+startSearch+" (zyklus="+zyklus+", v="+v+", rest="+rest+")");
				while (true) {
					if (startSearch % v == rest) {
						break;
					}
					startSearch += zyklus;
				}
				zyklus = kgv(zyklus, v);
			}
			System.out.println(startSearch);
		}
	}


	private static long kgv(long num1, long num2) {
		List<Long> primes1 = primes(num1);
		List<Long> primes2 = primes(num2);
		
		long result = 1;
		for (Long prime1:primes1) {
			result = result * prime1;
			if (primes2.contains(prime1)) {
				removeOnce(primes2, prime1);
			}
		}
		for (Long prime2:primes2) {
			result = result * prime2;
		}
		return result;
	}

	private static void removeOnce(List<Long> nums, Long num) {
		Iterator<Long> iter = nums.iterator();
		while (iter.hasNext()) {
			if (num == iter.next()) {
				iter.remove();
				return;
			}
		}
	}

	private static List<Long> primes(long n) {
		List<Long> result = new ArrayList<>();
		long num = n;
		for (long i=2; i<=n; i++) {
			while (num%i==0) {
				System.out.println(i+"*");
				result.add(i);
				num = num/i;
			}
			if (num == 1) {
				return result;
			}
		}
		return null;
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
