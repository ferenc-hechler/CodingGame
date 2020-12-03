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
public class Day03 {

	private final static String RX_LINE = "^[0-9]+-[0-9]+$";
	
	public static void mainPart1(String[] args) throws FileNotFoundException {
		List<String> forest = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("input/day03.txt"))) {
			while (scanner.hasNext()) {
				String line = scanner.next();
				if (line.isEmpty())
					break;
				forest.add(line);
			}
			System.out.println(forest);
		}
		long cnt1 = countTrees(forest, 1, 1);
		long cnt2 = countTrees(forest, 3, 1);
		long cnt3 = countTrees(forest, 5, 1);
		long cnt4 = countTrees(forest, 7, 1);
		long cnt5 = countTrees(forest, 1, 2);
		System.out.println(cnt1*cnt2*cnt3*cnt4*cnt5);
	}




	private static int countTrees(List<String> forest, int stepX, int stepY) {
		int cnt = 0;
		int posX = 0;
		int maxX = forest.get(0).length();
		int maxY = forest.size();
		if (forest.get(0).charAt(posX) == '#') {
			cnt += 1;
		}
		for (int posY=0; posY<maxY; posY += stepY) {
			if (forest.get(posY).charAt(posX) == '#') {
				cnt += 1;
			}
			posX = (posX + stepX) % maxX;
		}
		return cnt;
	}

	


	public static void main(String[] args) throws FileNotFoundException {
		mainPart1(args);
	}

	
}
