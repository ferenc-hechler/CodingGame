package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import de.hechler.adventofcode.Day21.Food;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day22 {

	private final static String PLAYER_RX = "^Player ([12]):$";

	private static List<Integer>[] deck;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day22.txt"))) {
			if (!scanner.nextLine().matches(PLAYER_RX)) {
				throw new RuntimeException("Missing Player header");
			}
			deck = new ArrayList[2];
			deck[0] = new ArrayList<>();
			deck[1] = new ArrayList<>();
			int player = 0;
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.matches(PLAYER_RX)) {
					player += 1;
					continue;
				}
				int card = Integer.parseInt(line);
				deck[player].add(card);
			}
			System.out.println("Player 1");
			System.out.println(deck[0]);
			System.out.println("Player 2");
			System.out.println(deck[1]);
			int cnt = 0;
			while ((deck[0].size() > 0) && (deck[1].size() > 0)) {
				int c0 = deck[0].remove(0);
				int c1 = deck[1].remove(0);
				if (c0 > c1) {
					deck[0].add(c0);
					deck[0].add(c1);
				}
				else {
					deck[1].add(c1);
					deck[1].add(c0);
				}
			}
			System.out.println("Runden: "+cnt);
			System.out.println("Player 1");
			System.out.println(deck[0]);
			System.out.println("Player 2");
			System.out.println(deck[1]);
			List<Integer> win = deck[0].size()>0 ? deck[0] : deck[1];
			long mul = win.size();
			long result = 0;
			for (int card:win) {
				result += mul*card;
				mul--;
			}
			System.out.println(result);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
