package de.hechler.codingame.othello;
import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player00Expert {

//	........
//	........
//	........
//	...10...
//	...100..
//	...1....
//	........
//	........
//	d6;        (EXPERT only)
//	5
//	c6
//	c4
//	c7
//	c5
//	c3	

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int id = in.nextInt(); // id of your player.
        int boardSize = in.nextInt();

        // game loop
        boolean expert = false;
        while (true) {
            for (int i = 0; i < boardSize; i++) {
                String line = in.next(); // rows from top to bottom (viewer perspective).
            	System.err.println(line);
            }
            if (expert) {
            	String enemyMoves = in.nextLine();
            	System.err.println(enemyMoves);
            }
            int actionCount = in.nextInt(); // number of legal actions for this turn.
            String action = null;
            for (int i = 0; i < actionCount; i++) {
                action = in.next(); // the action
            	System.err.println(action);
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("EXPERT "+action); // a-h1-8
            expert = true;
        }
    }	
	
}
