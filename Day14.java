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
			//System.out.println("Success processing file");

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



	public static int[][] divisions(
		  int globalHeight
		, int globalWidth
		, int boxHeight
		, int boxWidth
		, ArrayList<Integer> positionX
		, ArrayList<Integer> positionY
		) {

		int yDivision = 1 + boxHeight;
		int xDivision = 1 + boxWidth;

		// ensure we don't count any zero-dimension boxes in the case of
		// an exterior border
		if(globalHeight % yDivision == 0) globalHeight--;
		if(globalWidth % xDivision == 0) globalWidth--;

		// having done the above, we know that the number of BORDERS will
		// be e.g. globalHeight / yDivision using integer division... hence
		// add 1 to that to store results:
		int rowCount = 1 + globalHeight / yDivision;
		int colCount = 1 + globalWidth / xDivision;

		int[][] result = new int[rowCount][colCount];

		int robotCount = positionX.size();
		for(int robot = 0; robot < robotCount; robot++) {

			// better here to say that the top and left are indexed as
			// 1 and not 0; can then say that zero modulus positions
			// are the borders
			int xp = 1 + positionX.get(robot);
			int yp = 1 + positionY.get(robot);

			if(xp % xDivision == 0 || yp % yDivision == 0) continue; // don't count points on borders

			result[yp/yDivision][xp/xDivision]++;

		} // loop over the robots

		return result;
	}



	public static long latticeProduct(int[][] breakdown) {

	/*
		Attempts to divide the grid up into boxes of the specified dimensions.
		There will be a row or column of border between boxes. At the right
		or bottom of the grid, the possibilities are either:
		- the box dimension requested is larger than the grid, and no borders used
		- the box dimension requested is equal to the grid, and no borders used
		- a row or column of border running along the outside
		- a "perfect" subdivision, in the case that the global dimension is
		  one less than a multiple of the border dimension
		- the final box is curtailed

	*/

		int rows = breakdown.length;
		int cols = breakdown[0].length;

		long result = 1L;

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				result *= breakdown[i][j];
			}
		}

		return result;
	}



	public static void displayArray(ArrayList<Integer> x, ArrayList<Integer> y, int height, int width, int time) {

		boolean[][] results = new boolean[height][width];

		for(int index = 0; index < x.size(); index++)
			results[y.get(index)][x.get(index)] = true;

		System.out.println("NOW SHOWING RESULTS FOR TIME: " + time );
		System.out.println("====================================================");

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++)
				System.out.print(results[i][j] ? "X" : " ");

			System.out.println();
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Continue?");
		String input = sc.next();

	} // displayArray method



	public static LetterGrid getGrid(ArrayList<Integer> x, ArrayList<Integer> y, int height, int width) {

		boolean[][] results = new boolean[height][width];

		for(int index = 0; index < x.size(); index++)
			results[y.get(index)][x.get(index)] = true;
/*
		System.out.println("NOW SHOWING RESULTS FOR TIME: " + time );
		System.out.println("====================================================");

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++)
				System.out.print(results[i][j] ? "X" : " ");

			System.out.println();
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("Continue?");
		String input = sc.next();
*/
		return new LetterGrid(results, 'X', ' ');
	} // displayArray method




		public static long answer(int part, boolean debug) throws Exception {
//	public static void main(String[] args) {

		boolean part1 = (part == 1);
		int width = 101;
		int height = 103;

		String path = "Y:\\code\\java\\AdventOfCode\\Day14input.dat";
		ArrayList<Integer> positionX = new ArrayList<Integer>();
		ArrayList<Integer> positionY = new ArrayList<Integer>();
		ArrayList<Integer> velocityX = new ArrayList<Integer>();
		ArrayList<Integer> velocityY = new ArrayList<Integer>();
		populateFromFile(path, positionX, positionY, velocityX, velocityY);

		if(part1) {
			evolve(height, width, 100, velocityX, velocityY, positionX, positionY);
			int[][] segregation = divisions (height, width, height / 2, width / 2, positionX, positionY);
			//System.out.println("Part 1 answer: " + latticeProduct(segregation));
			return latticeProduct(segregation);
		} else {

			// every robot's x-periodicity has to be a fraction of width; likewise y and height
			// thus there can only ever be height * width distinct overall states before it repeats
			int maxTime = height * width;

			for(int time = 0; time < maxTime; time++) {

				// let's hope that the xmas tree pic will entail blank space in other areas -
				// checking for a 6x6 lattice with at least one of its blocks empty:

				// crude but cheap filter saying that we want a significant empty area
				int[][] segregation = divisions (height, width, height / 6, width / 6, positionX, positionY);
				if(0 == latticeProduct(segregation)) { //displayArray(positionX, positionY, height, width, time); // manual approach

					// fine but costly filter saying we want several connected regions each with a significant number of blocks
					LetterGrid lg = getGrid(positionX, positionY, height, width);
					if(lg.hasBigAreas(20, 4)) return time;
				}
				evolve(height, width, 1, velocityX, velocityY, positionX, positionY);
			} // single time-step loop

		} // part1 / part2 if

		return -1;
	} // answer method

} // Day13 class