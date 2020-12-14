package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day13b {

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
		long z1temp = num1;
		long z2temp = num2;
         
        while (z1temp != z2temp) {
            if (z1temp < z2temp) {
                z1temp += num1;
            } else {
                z2temp+= num2;
            }
        }
        return z1temp;
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
