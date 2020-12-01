package de.hechler.adventofcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day01 {

	public static void mainPart1(String[] args) {
		List<Integer> values = new ArrayList<>();
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				int value = scanner.nextInt();
				for (int otherValue:values) {
					if (value + otherValue == 2020) {
						System.out.println(otherValue+" * " + value+" = "+ value*otherValue);
						return;
					}
				}
				values.add(value);
			}
		}
	}

	public static void mainPart2(String[] args) {
		try {
			List<Long> values = new ArrayList<>();
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					long value = scanner.nextInt();
					for (int i=0; i<values.size()-1; i++) {
						for (int j=i+1; j<values.size(); j++) {
							if (values.get(i) + values.get(j) + value == 2020) {
								System.out.println(values.get(i)+" * " + values.get(j)+" * " + value+" = "+ values.get(i)*values.get(j)*value);
								return;
							}
						}
					}
					values.add(value);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	
}
