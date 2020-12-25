package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day25 {

	public static void mainPart1() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("input/day25.txt"))) {
			long pubKeyCard = Long.parseLong(scanner.next());
			long pubKeyDoor = Long.parseLong(scanner.next());
			System.out.println(pubKeyCard);
			System.out.println(pubKeyDoor);
			int cardLoop=0;
			int doorLoop=0;
			int loop = 0;
			long pubValue = 1;
			while ((cardLoop == 0) || (doorLoop == 0)) {
				loop += 1;
				pubValue = (pubValue * 7) % MODULO;
				if (pubValue == pubKeyCard) {
					cardLoop = loop;
				}
				if (pubValue == pubKeyDoor) {
					doorLoop = loop;
				}
			}
			System.out.println("loop card: "+cardLoop);
			System.out.println("loop door: "+doorLoop);
			long encKey1 = encryptLoop(pubKeyDoor, cardLoop);
			long encKey2 = encryptLoop(pubKeyDoor, cardLoop);
			System.out.println(encKey1+" == "+encKey2);
		}
	}

	private final static long MODULO = 20201227;
	
	public static long encryptLoop(long subject, int loops) {
		long value = 1;
		for (int i=0; i<loops; i++) {
			value = (value * subject) % MODULO;
		}
		return value;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		mainPart1();
	}

	
}
