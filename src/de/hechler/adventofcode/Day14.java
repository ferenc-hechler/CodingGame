package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day14 {

	final static String MASK_RX = "mask = ([X01]{36})";
	final static String MEM_RX = "mem\\[(\\d+)\\] = (\\d+)";
	
	public static void mainPart1() throws FileNotFoundException {
		Map<Long, Long> memory = new HashMap<>();
		try (Scanner scanner = new Scanner(new File("input/day14.txt"))) {
			long filterMask = 0;
			long overwriteValue = 0;
			long invFilterMask = ((1L << 36)-1) ^ filterMask;
			
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.matches(MASK_RX)) {
					String mask = line.replaceFirst(MASK_RX, "$1");
					filterMask = Long.parseLong(mask.replace('0', '1').replace('X', '0'), 2);
					overwriteValue = Long.parseLong(mask.replace('X', '0'), 2);
					invFilterMask = ((1L << 36)-1) ^ filterMask;
					System.out.println(mask);
					continue;
				}
				if (!line.matches(MEM_RX)) {
					throw new RuntimeException("line '"+line+"' does not match MEM-RX");
				}
				long addr = Long.parseLong(line.replaceFirst(MEM_RX, "$1"));
				long value = Long.parseLong(line.replaceFirst(MEM_RX, "$2"));
				long effValue = (value & invFilterMask) | overwriteValue;
				System.out.println(addr+": "+effValue);
				memory.put(addr, effValue);
			}
			long sum = 0;
			for (Long value:memory.values()) {
				sum += value;
			}
			System.out.println("SUM: "+sum);
		}
	}


	public static void mainPart2() throws FileNotFoundException {
		Map<Long, Long> memory = new HashMap<>();
		try (Scanner scanner = new Scanner(new File("input/day14.txt"))) {
			List<Integer> bitPos = new ArrayList<>();
			String mask = ""; 
			long filterMask = 0;
			long overwriteValue = 0;
			long invFilterMask = ((1L << 36)-1) ^ filterMask;
			
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.matches(MASK_RX)) {
					mask = line.replaceFirst(MASK_RX, "$1");
					filterMask = Long.parseLong(mask.replace('X', '1'), 2);
					invFilterMask = ((1L << 36)-1) ^ filterMask;
					overwriteValue = Long.parseLong(mask.replace('X', '0'), 2);
					bitPos.clear();
					int pos = mask.indexOf('X');
					while (pos >= 0) {
						System.out.print((mask.length()-1-pos)+",");
						bitPos.add(mask.length()-1-pos);
						pos = mask.indexOf('X', pos+1);
					}
					System.out.println();
					continue;
				}
				
				if (!line.matches(MEM_RX)) {
					throw new RuntimeException("line '"+line+"' does not match MEM-RX");
				}
				long addr = Long.parseLong(line.replaceFirst(MEM_RX, "$1"));
				long value = Long.parseLong(line.replaceFirst(MEM_RX, "$2"));
				long effAddr = (addr & invFilterMask) | overwriteValue;
				fillMem(memory, effAddr, value, bitPos);
			}
			long sum = 0;
			for (Long value:memory.values()) {
				sum += value;
			}
			System.out.println("SUM: "+sum);
		}
	}

	private static void fillMem(Map<Long, Long> memory, long addr, long value, List<Integer> bitPos) {
		if (bitPos.isEmpty()) {
			memory.put(addr, value);
			System.out.println("  " + addr+": "+value);
			return;
		}
		//long oldAddr = addr;
		List<Integer> recBitPos = new ArrayList<>(bitPos);
		Integer bit = recBitPos.remove(0);
		fillMem(memory, addr, value, recBitPos);
		addr = addr | (1L << bit);
		fillMem(memory, addr, value, recBitPos);
	}


	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
