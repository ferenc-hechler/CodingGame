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
public class Day15 {

	static class Info {
		long value;
		long cnt; 
		long lastIDX;
		public Info(long value, long cnt, long lastIDX) {
			this.value = value;
			this.cnt = cnt;
			this.lastIDX = lastIDX;
		}
		
	}
	
	public static void mainPart1() throws FileNotFoundException {
		Map<Long, Info> memory = new HashMap<>();
		try (Scanner scanner = new Scanner(new File("input/day15.txt"))) {
			String[] numbers = scanner.next().split("[,]");
			int idx = 0;
			long lastValue = 0;
			for (String number:numbers) {
				System.out.println(idx+": "+number);
				idx += 1;
				lastValue = Long.parseLong(number);
				Info info = memory.get(lastValue);
				memory.put(lastValue, new Info(lastValue, 1, idx));
			}
			long nextValue = 0;
			while (idx < 2020) {
				lastValue = nextValue;
				idx++;
				System.out.println(idx+": "+lastValue);
				Info info = memory.get(lastValue);
				if (info == null) {
					nextValue = 0;
					memory.put(lastValue, new Info(lastValue, 1, idx));
				}
				else {
					nextValue = idx - info.lastIDX;
					info.lastIDX = idx;
					info.cnt += 1;
				}
			}
		}
	}


	public static void mainPart2() throws FileNotFoundException {
		Map<Long, Info> memory = new HashMap<>();
		try (Scanner scanner = new Scanner(new File("input/day15.txt"))) {
			String[] numbers = scanner.next().split("[,]");
			int idx = 0;
			long lastValue = 0;
			for (String number:numbers) {
				System.out.println(idx+": "+number);
				idx += 1;
				lastValue = Long.parseLong(number);
				Info info = memory.get(lastValue);
				memory.put(lastValue, new Info(lastValue, 1, idx));
			}
			long nextValue = 0;
			while (idx < 30000000) {
				lastValue = nextValue;
				idx++;
				System.out.println(idx+": "+lastValue);
				Info info = memory.get(lastValue);
				if (info == null) {
					nextValue = 0;
					memory.put(lastValue, new Info(lastValue, 1, idx));
				}
				else {
					nextValue = idx - info.lastIDX;
					info.lastIDX = idx;
					info.cnt += 1;
				}
			}
		}
	}



	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
