import java.util.*;
import java.io.*;

class Day14 {



	public static void populateFromFile(
		  String filePath

		// void method but it updates all 4 of these:
		, ArrayList<Integer> initialPositionX
		, ArrayList<Integer> initialPositionY
		, ArrayList<Integer> velocityX
		, ArrayList<Integer> velocityY
		) {

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

				initialPositionX.add(Integer.parseInt(p[0]));
				initialPositionY.add(Integer.parseInt(p[1]));
				velocityX.add(Integer.parseInt(v[0]));
				velocityY.add(Integer.parseInt(v[1]));
			} // file-read while loop

			sc.close();
			System.out.println("Success processing file");

		} catch (Exception e) {
			System.out.println("Error processing file");
		}

	} // populateFromFile method



	public static void evolve(
		  int height
		, int width
		, int time
		, ArrayList<Integer> velocityX
		, ArrayList<Integer> velocityY

		// void method but it updates these:
		, ArrayList<Integer> positionX
		, ArrayList<Integer> positionY
		) {

		int robotCount = positionX.size();
		for(int robot = 0; robot < robotCount; robot++) {

			int px = positionX.get(robot);
			int py = positionY.get(robot);
			int vx = velocityX.get(robot);
			int vy = velocityY.get(robot);

			int tx = px + time * vx;
			tx %= width;
			tx += width;
			tx %= width;
			int ty = py + time * vy;
			ty %= height;
			ty += height;
			ty %= height;

			positionX.set(robot, tx);
			positionY.set(robot, ty);

		} // robot for-loop

	} // evolve method



	public static ArrayList<String> getQuadrants(
		  int height
		, int width
		, ArrayList<Integer> positionX
		, ArrayList<Integer> positionY
		) {

		int dmzColumn = width / 2; // e.g. for a width of 5, being 01234, this correctly returns 2 as the middle
		int dmzRow = height / 2;

		ArrayList<String> quadrants = new ArrayList<String>();

		int robotCount = positionX.size();
		for(int robot = 0; robot < robotCount; robot++) {

			String result = "";

			int tx = positionX.get(robot);
			int ty = positionY.get(robot);

			if(tx == dmzColumn || ty == dmzRow) {
				result += "DMZ";
			} else {
				result += ty < dmzRow ? "TOP" : "BOTTOM";
				result += " ";
				result += tx < dmzColumn ? "LEFT" : "RIGHT";
			}
			quadrants.add(result);

		} // robot for-loop

		return quadrants;

	} // getQuadrants method



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
	} // displayArray method



	public static void main(String[] args) {

		String path = "Y:\\code\\java\\AdventOfCode\\Day14input.dat";
		ArrayList<Integer> positionX = new ArrayList<Integer>();
		ArrayList<Integer> positionY = new ArrayList<Integer>();
		ArrayList<Integer> velocityX = new ArrayList<Integer>();
		ArrayList<Integer> velocityY = new ArrayList<Integer>();
		populateFromFile(path, positionX, positionY, velocityX, velocityY);

		int width = 101;
		int height = 103;
		int time = 100;

		evolve(height, width, time, velocityX, velocityY, positionX, positionY);
		ArrayList<String> quadrants = getQuadrants(height, width, positionX, positionY);
		System.out.println("Part 1 answer: " + safetyFactor(quadrants));

		//displayArray();

	} // main method



} // Day13 class