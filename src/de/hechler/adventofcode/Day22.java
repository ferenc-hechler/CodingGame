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

	private static List<Integer>[] playerDeck;
	
	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day22.txt"))) {
			if (!scanner.nextLine().matches(PLAYER_RX)) {
				throw new RuntimeException("Missing Player header");
			}
			playerDeck = new ArrayList[2];
			playerDeck[0] = new ArrayList<>();
			playerDeck[1] = new ArrayList<>();
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
				playerDeck[player].add(card);
			}
			System.out.println("Player 1");
			System.out.println(playerDeck[0]);
			System.out.println("Player 2");
			System.out.println(playerDeck[1]);
			int cnt = 0;
			while ((playerDeck[0].size() > 0) && (playerDeck[1].size() > 0)) {
				int c0 = playerDeck[0].remove(0);
				int c1 = playerDeck[1].remove(0);
				if (c0 > c1) {
					playerDeck[0].add(c0);
					playerDeck[0].add(c1);
				}
				else {
					playerDeck[1].add(c1);
					playerDeck[1].add(c0);
				}
				cnt++;
			}
			System.out.println("Runden: "+cnt);
			System.out.println("Player 1");
			System.out.println(playerDeck[0]);
			System.out.println("Player 2");
			System.out.println(playerDeck[1]);
			List<Integer> win = playerDeck[0].size()>0 ? playerDeck[0] : playerDeck[1];
			long mul = win.size();
			long result = 0;
			for (int card:win) {
				result += mul*card;
				mul--;
			}
			System.out.println(result);
		}
	}

	static class World {
		List<Integer> player1;
		List<Integer> player2;
		List<String> player1History;
		List<String> player2History;
		boolean p1win;
		World() {
			this(Collections.emptyList(),Collections.emptyList());
		}
		World(List<Integer> player1, List<Integer> player2) {
			this.player1 = new ArrayList<>(player1);
			this.player2 = new ArrayList<>(player2);
			this.player1History = new ArrayList<>();
			this.player2History = new ArrayList<>();
			this.p1win = false;
		}
		public void addCard(int player, int card) {
			if (player == 1) {
				player1.add(card);
			}
			else {
				player2.add(card);
			}
		}
		public int getWinner() {
			if (p1win) {
				return 1;
			}
			if (player1.size() == 0) {
				return 2;
			}
			if (player2.size() == 0) {
				return 1;
			}
			return 0;
		}
		public List<Integer> getDeck(int player) {
			if (player == 1) {
				return player1;
			}
			return player2;
		}
		public void playRound() {
			String p1hist = player1.toString();
			String p2hist = player2.toString();
			if (player1History.contains(p1hist) || player2History.contains(p2hist)) {
				// instant win player 1 for identical decks in history
				p1win=true;
				return;
			}
			player1History.add(p1hist);
			player2History.add(p2hist);
			int c1 = player1.remove(0);
			int c2 = player2.remove(0);
			if ((c1<=player1.size()) && (c2<=player2.size())) {
				// play subgame
				int winner = playSubGame(c1, c2);
				if (winner == 1) {
					player1.add(c1);
					player1.add(c2);
				}
				else {
					player2.add(c2);
					player2.add(c1);
				}
				return;
			}
			if (c1>c2) {
				player1.add(c1);
				player1.add(c2);
			}
			else {
				player2.add(c2);
				player2.add(c1);
			}
		}
		private int playSubGame(int c1, int c2) {
			World subWorld = new World();
			for (int i=0; i<c1; i++) {
				subWorld.addCard(1, player1.get(i));
			}
			for (int i=0; i<c2; i++) {
				subWorld.addCard(2, player2.get(i));
			}
			while (subWorld.getWinner()==0) {
				subWorld.playRound();
			}
			return subWorld.getWinner();
		}
		@Override
			public String toString() {
				return "P1="+player1+"  P2="+player2;
			}
	}
	
	public static void mainPart2() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day22.txt"))) {
			if (!scanner.nextLine().matches(PLAYER_RX)) {
				throw new RuntimeException("Missing Player header");
			}
			World world = new World();
			int player = 1;
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
				world.addCard(player, card);
			}
			System.out.println(world);
			int cnt = 0;
			while (world.getWinner()==0) {
				world.playRound();
				cnt++;
			}
			System.out.println("Runden: "+cnt);
			System.out.println(world);
			List<Integer> win = world.getDeck(world.getWinner());
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
		mainPart2();
	}

	
}
