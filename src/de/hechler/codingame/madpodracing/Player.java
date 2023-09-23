package de.hechler.codingame.madpodracing;
import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {


    int currentPos;
    List<Integer> xPos = new ArrayList<>(10);
    List<Integer> yPos = new ArrayList<>(10);
    List<Double> nextAngel = new ArrayList<>(10);


    static boolean initialized = false;
    static boolean boosted = false;
    static int lastX;
    static int lastY;
    
    static String overwrite = null;

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            final int x = in.nextInt();
            final int y = in.nextInt();
            final int nextCheckpointX = in.nextInt(); // x position of the next check point
            final int nextCheckpointY = in.nextInt(); // y position of the next check point
            final int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            final int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            final int opponentX = in.nextInt();
            final int opponentY = in.nextInt();

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            if (!initialized) {
                initialized = true;
                lastX = x;
                lastY = y;
            }
            double dx = x - lastX;
            double dy = y - lastY;
            double v = Math.sqrt(dx*dx+dy*dy);
            lastX = x;
            lastY = y;


            // You have to output the target position
            // followed by the power (0 <= thrust <= 100)
            // i.e.: "x y thrust"
            final double wrongDir = Math.abs(nextCheckpointAngle)/180.0;
            int startBreak = (int) (v*2.75);
            System.err.println("startBreak: "+startBreak);
            double speed = 100.0;
            if (nextCheckpointDist < startBreak) {
                speed = 0.0;
            }
            if (wrongDir > 0.25) {
                //speed = speed * (1.0-wrongDir);
                speed = 5;
            }
            String speedStr = Integer.toString((int)speed);
            // if ((!boosted) && (nextCheckpointDist > 6000) && (wrongDir < 0.05)) {
            if (!boosted) {
                speedStr = " BOOST";
                boosted = true;
            }
            String cmd = nextCheckpointX + " " + nextCheckpointY + " "+speedStr;
            if (overwrite != null) {
                cmd = overwrite;
            }

            System.err.println("dist: "+nextCheckpointDist);
            System.err.println("angle: "+nextCheckpointAngle);
            System.err.println("v: "+v);
            System.err.println("wrongDir: "+wrongDir);

            System.out.println(cmd);
        }
    }
}