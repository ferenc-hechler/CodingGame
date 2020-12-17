package de.hechler.adventofcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2020/day/1
 *
 */
public class Day17 {

	static class Cube {
		int x;
		int y;
		int z;
		int w;
		public Cube(int x, int y, int z) {
			this(x,y,z,0);
		}
		public Cube(int x, int y, int z, int w) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
		boolean isNeighbour(int cx, int cy, int cz) {
			return isNeighbour(cx, cy, cz, 0); 
		}
		boolean isNeighbour(int cx, int cy, int cz, int cw) {
			return (x>=cx-1) && (x<=cx+1)&&(y>=cy-1) && (y<=cy+1)&&(z>=cz-1) && (z<=cz+1)&&(w>=cw-1) && (w<=cw+1); 
		}
		@Override
		public String toString() {
			return "(" + x+ "," + y+ "," + z + "," + w + ")";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			result = prime * result + w;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Cube other = (Cube) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}
	}

	static Set<Cube> currentCubes;
	static Set<Cube> nextCubes;

	public static void mainPart1() throws FileNotFoundException {
		nextCubes = new LinkedHashSet<>();
		try (Scanner scanner = new Scanner(new File("input/day17.txt"))) {
			{
				int z = 0;
				int y = 0;
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (!line.matches("^[.#]+$")) {
						throw new RuntimeException("invalid line '"+line+"'");
					}
					for (int x=0; x<line.length(); x++) {
						if (line.charAt(x) == '#') {
							nextCubes.add(new Cube(x, y, z));
						}
					}
					y++;
				}
			}
			for (int tick=0; tick<6; tick++) {
//				System.out.println("TICK: "+tick);
//				System.out.println(nextCubes);
				Cube minCube = new Cube(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
				Cube maxCube = new Cube(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
				findMinMax(nextCubes, minCube, maxCube);
//				show(nextCubes, minCube, maxCube);
				currentCubes = nextCubes;
				nextCubes = new LinkedHashSet<>();
				for (int z = minCube.z-1; z<=maxCube.z+1; z++) {
					for (int y = minCube.y-1; y<=maxCube.y+1; y++) {
						for (int x = minCube.x-1; x<=maxCube.x+1; x++) {
							Cube cube = new Cube(x,y,z);
							Set<Cube> neighbourCubes = findNeighbourCubes(currentCubes, cube);
							boolean active = neighbourCubes.remove(cube);
							if (neighbourCubes.size() == 3) {
								nextCubes.add(cube);
							}
							else if (active && neighbourCubes.size() == 2) {
								nextCubes.add(cube);
							}
						}
					}
				}
			}
			Cube minCube = new Cube(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
			Cube maxCube = new Cube(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
			findMinMax(nextCubes, minCube, maxCube);
			show(nextCubes, minCube, maxCube);
			System.out.println(nextCubes);
			System.out.println(nextCubes.size());
		}
	}

	public static void mainPart2() throws FileNotFoundException {
		nextCubes = new LinkedHashSet<>();
		try (Scanner scanner = new Scanner(new File("input/day17.txt"))) {
			{
				int w = 0;
				int z = 0;
				int y = 0;
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (!line.matches("^[.#]+$")) {
						throw new RuntimeException("invalid line '"+line+"'");
					}
					for (int x=0; x<line.length(); x++) {
						if (line.charAt(x) == '#') {
							nextCubes.add(new Cube(x, y, z, w));
						}
					}
					y++;
				}
			}
			for (int tick=0; tick<6; tick++) {
//				System.out.println("TICK: "+tick);
//				System.out.println(nextCubes);
				Cube minCube = new Cube(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
				Cube maxCube = new Cube(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
				findMinMax(nextCubes, minCube, maxCube);
//				show(nextCubes, minCube, maxCube);
				currentCubes = nextCubes;
				nextCubes = new LinkedHashSet<>();
				for (int w = minCube.w-1; w<=maxCube.w+1; w++) {
					for (int z = minCube.z-1; z<=maxCube.z+1; z++) {
						for (int y = minCube.y-1; y<=maxCube.y+1; y++) {
							for (int x = minCube.x-1; x<=maxCube.x+1; x++) {
								Cube cube = new Cube(x,y,z,w);
								Set<Cube> neighbourCubes = findNeighbourCubes(currentCubes, cube);
								boolean active = neighbourCubes.remove(cube);
								if (neighbourCubes.size() == 3) {
									nextCubes.add(cube);
								}
								else if (active && neighbourCubes.size() == 2) {
									nextCubes.add(cube);
								}
							}
						}
					}
				}
			}
			Cube minCube = new Cube(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
			Cube maxCube = new Cube(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE);
			findMinMax(nextCubes, minCube, maxCube);
			show(nextCubes, minCube, maxCube);
			System.out.println(nextCubes);
			System.out.println(nextCubes.size());
		}
	}
	
	private static void show(Set<Cube> cubes, Cube minCube, Cube maxCube) {
		for (int z = minCube.z; z<=maxCube.z; z++) {
			System.out.println("Z="+z);
			for (int y = minCube.y; y<=maxCube.y; y++) {
				for (int x = minCube.x; x<=maxCube.x; x++) {
					if (cubes.contains(new Cube(x,y,z))) {
						System.out.print("#");
					}
					else {
						System.out.print(".");
					}
						
					
				}
				System.out.println();
			}
		}
	}

	private static void show(Set<Cube> nextCubes2) {
		// TODO Auto-generated method stub
		
	}

	private static Set<Cube> findNeighbourCubes(Set<Cube> cubes, Cube centerCube) {
		Set<Cube> result = new LinkedHashSet<>();
		for (Cube cube:cubes) {
			if (cube.isNeighbour(centerCube.x, centerCube.y, centerCube.z, centerCube.w)) {
				result.add(cube);
			}
		}
		return result;
	}

	private static void findMinMax(Set<Cube> cubes, Cube minCube, Cube maxCube) {
		for (Cube cube:cubes) {
			minCube.x = Math.min(minCube.x, cube.x);
			minCube.y = Math.min(minCube.y, cube.y);
			minCube.z = Math.min(minCube.z, cube.z);
			minCube.w = Math.min(minCube.w, cube.w);
			maxCube.x = Math.max(maxCube.x, cube.x);
			maxCube.y = Math.max(maxCube.y, cube.y);
			maxCube.z = Math.max(maxCube.z, cube.z);
			maxCube.w = Math.max(maxCube.w, cube.w);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		mainPart2();
	}

	
}
