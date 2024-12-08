
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Day08 {
	/*
		https://adventofcode.com/2024/day/8

		Algorithm for getting UNIQUE LOCATIONS THAT CONTAIN AN ANTINODE:

		1/ parse the map and record all antenna locations
		2/ segregate these by code
		3/ for any given code, find ALL PAIRS
		4/ for each pair, determine the antinode locations along the relevant vector
		5/ (remembering to ignore those lying off-map)

	*/



	private static HashMap<Character, ArrayList<Point>> getAntennae(boolean debug, LetterGrid map) {

		HashMap<Character, ArrayList<Point>> antennae = new HashMap<Character, ArrayList<Point>>();

		for(int row = 0; row < map.getHeight(); row++) {
			for(int column = 0; column < map.getWidth(); column++) {

				char c = map.getCell(row, column);

				if(c != '.') {
					ArrayList<Point> al = antennae.get(c);

					if (al == null) { // FIRST antenna of this frequency
						al = new ArrayList<Point>();
						antennae.put(c, al);
						if(debug) System.out.println("first instance of " + c + " found");
					}

					try { // ALL antennae of this frequency
						al.add(new Point(map, row, column));
						if(debug) System.out.println("instance of " + c + " at (" + row + ", " + column + ") successfully recorded");
					} catch (PointOutOfBoundsException e) {
						System.out.println("point out of bounds - this should never happen!");
					}

				} // antenna-if

			} // column-loop
		} // row-loop

		return antennae;

	} // getAntennae-method



	public static LetterGrid processFile() {

		String path = "Y:\\code\\java\\AdventOfCode\\Day08input.dat";

		try {
			return new LetterGrid(path);

		} catch (Exception e) {
			System.out.println("error loading map");
			return null;
		}

	} // processFile



	public static int countAntinodes(boolean debug, int problemPart, HashMap<Character, ArrayList<Point>> antennae) {

		HashSet<String> uniqueAntinodes = new HashSet<String>();

		for(char c : antennae.keySet()) {

			ArrayList<Point> locations = antennae.get(c);
			if(debug) System.out.println("Frequency " + c + " has " + locations.size() + " transmitters.");

			for(int firstAntenna = 0; firstAntenna < locations.size() ; firstAntenna++) {

				Point p1 = locations.get(firstAntenna);
				String c1 = p1.getCoords();

				for(int secondAntenna = 0; secondAntenna < locations.size(); secondAntenna++) {

					Point p2 = locations.get(secondAntenna);
					String c2 = p2.getCoords();

					if(!c1.equals(c2)) {

						if(debug) System.out.println("Directed "+ c + "-pair at " + p1.getCoords() + " and " + p2.getCoords() + ". ");

						int vectorMultiple = (problemPart == 1) ? 2 : 1; // second part of the problem means the first antinode can be ON the antenna

						do {

							try {
								Point antinode = p1.getVectorMultiple(p2, vectorMultiple);
								uniqueAntinodes.add(antinode.getCoords());
								if(debug) System.out.println("(Antinode noted at " + antinode.getCoords()+")");
								vectorMultiple++;

							} catch (PointOutOfBoundsException e) {
								if(debug) System.out.println("(Next antinode is off the map.)");
								break; // stop looping once we go off the map
							}

						} while(problemPart == 2); // second part of the problem also means loop beyond the first antinode

					} // not-same-point check

				} // secondAntenna-for

			} // firstAntenna-for

		} // antennae-keySet

		return uniqueAntinodes.size();

	} // countAntinodes method



	public static void main(String[] args) {

		boolean debug = false;
		LetterGrid map = processFile();
		if(debug) System.out.println(map.getHeight() + "*" + map.getWidth() + " map successfully loaded");
		HashMap<Character, ArrayList<Point>> antennae = getAntennae(debug, map);

		int problemPart = 1;
		System.out.println("Total unique antinodes for part " + problemPart + ": " + countAntinodes(debug, problemPart, antennae));

		problemPart = 2;
		System.out.println("Total unique antinodes for part " + problemPart + ": " + countAntinodes(debug, problemPart, antennae));

	} // main method



} // class
