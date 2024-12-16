import java.util.*;
import java.io.*;

class Day14 {

	public static ArrayList<String> processFile(String filePath, int height, int width, int time) {
		ArrayList<String> quadrants = new ArrayList<String>();

		try {
			File fi = new File(filePath);
			Scanner sc = new Scanner(fi);

			while(sc.hasNext()) {
				String inputLine = sc.nextLine();

				// p then v strings
				String[] spaceSplit = inputLine.split(" ");

				// strip off "p=" and "v="
				String position = spaceSplit[0].trim().substring(2, spaceSplit[0].length());
				String velocity = spaceSplit[1].trim().substring(2, spaceSplit[1].length());

				String[] p = position.split(",");
				String[] v = velocity.split(",");

				int px = Integer.parseInt(p[0]);
				int py = Integer.parseInt(p[1]);
				int vx = Integer.parseInt(v[0]);
				int vy = Integer.parseInt(v[1]);

				String quad = getQuadrant(width, height, px, py, vx, vy, time);
				//System.out.println(quad);
				quadrants.add(quad);


			}

			sc.close();
			System.out.println("Success processing file");
			return quadrants;

		} catch (Exception e) {
			System.out.println("Error processing file");
			return null;
		}

	} // processFile method



	public static String getQuadrant(int width, int height, int px, int py, int vx, int vy, int time) {
		int dmzColumn = width / 2; // e.g. for a width of 5, being 01234, this correctly returns 2 as the middle
		int dmzRow = height / 2;

		String result = "";

		int tx = px + time * vx;
		tx %= width;
		tx += width;
		tx %= width;

		int ty = py + time * vy;
		ty %= height;
		ty += height;
		ty %= height;

		if(tx == dmzColumn || ty == dmzRow) return "DMZ";
		result += ty < dmzRow ? "TOP" : "BOTTOM";
		result += " ";
		result += tx < dmzColumn ? "LEFT" : "RIGHT";

		return result;

	} // getQuadrant method


	public static int safetyFactor(ArrayList<String> quadrants) {

		int topLeft = 0, topRight = 0, bottomLeft = 0, bottomRight = 0;

		for(String str : quadrants) {

			switch(str) {

				case "DMZ":break;
				case "TOP LEFT":topLeft++;break;
				case "TOP RIGHT":topRight++;break;
				case "BOTTOM LEFT":bottomLeft++;break;
				case "BOTTOM RIGHT":bottomRight++;break;

			}

		}

		return topLeft * topRight * bottomLeft * bottomRight;

	} // safetyFactor method

	public static void main(String[] args) {

		int width = 101;
		int height = 103;
		int time = 100;
		ArrayList<String> quadrants = processFile("Y:\\code\\java\\AdventOfCode\\Day14input.dat", height, width, time);

		System.out.println("Part 1 answer: " + safetyFactor(quadrants));

		displayArray();

	} // main method


	// public static void displayAtTime(int[][] positions, int[][] velocities, int time) {

	public static void displayArray(ArrayList<Integer> x, ArrayList<Integer> y) {

		int height = 3;
		int width = 12;

		int[][] results = new int[height][width];

		for(int index = 0; index < x.size(); index++) {
			results[y.get(index)][x.get(index)] = 1;

		}

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				System.out.print(results[i][j]);
			}
			System.out.println();
		}
	}


} // Day13 class