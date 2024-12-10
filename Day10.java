import java.util.*;
import java.io.*;

public class Day10 {



	public static int propagate(boolean debug, LetterGrid elevations) {

/*
			Data structure: identify every peak, then associate every grid
			position with a HashSet of peaks that one can ascend to, from
			that position.

			Set this up by processing every 9|8 border, then every 8|7 border,
			and so on until every 0 has an associated HashSet. Then just add
			up the HashSet sizes of all 0 positions.

*/
	final char TOP = '9', BOTTOM = '0';


	// let's first set up those hashsets:
	int rows = elevations.getHeight();
	int columns = elevations.getWidth();
	Object[][] reachables = new Object[rows][columns];

	for(int row = 0; row < rows; row++) {
		for(int column = 0; column < columns; column++) {
			HashSet<String> hs = new HashSet<String>();
			if(elevations.getCell(row, column) == TOP) {
				String peakText = "(" + row + "," + column + ")";
				hs.add(peakText);
				// System.out.println("Logged new peak at " + peakText);
			}
			reachables[row][column] = hs;
		} // column loop
	} // row loop


	for(char elevation = TOP; elevation >= BOTTOM; elevation--) {
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				if(elevations.getCell(row, column) == elevation) {
					// if (debug) System.out.println("cell(" + row + ", " + column + ") is at elevation " + elevation);

						@SuppressWarnings("unchecked")
						HashSet<String> upper = (HashSet<String>) reachables[row][column];
						if(debug) System.out.println("Cell (" + row + "," + column + ") logs " + upper.toString());

						for(int dy = -1; dy <= 1; dy++) {
							for(int dx = -1; dx <= 1; dx++) {
								if(Math.abs(dx) + Math.abs(dy) == 1) { // move in precisely one direction
									try {
										char neighbour = elevations.getCell(row + dy, column + dx);

										if(neighbour + 1 == elevation) {
											if(debug) System.out.print("Successfully found gradient from " + elevation + " to " + neighbour);
											if(debug) System.out.println(" between cells (" + row + ", " + column + ") and (" + (row + dy) + ", " + (column + dx) + ")");

											@SuppressWarnings("unchecked")
											HashSet<String> lower = (HashSet<String>) reachables[row + dy][column + dx];
											lower.addAll(upper);


										} // small-height-change check

									} catch (Exception e) {;}// easiest way to avoid problems at the edges
								}
							} // inspect horizontal neighbours
						} // inspect vertical neighbours

				} // elevation check

			} // column loop
		} // row loop

	} // elevation loop

	// score-totalling
		int score = 0;

		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {

				if(elevations.getCell(row, column) == BOTTOM) {

					@SuppressWarnings("unchecked")
					HashSet<String> hs = (HashSet<String>) reachables[row][column];
					score += hs.size();
					System.out.println("Valley at (" + row + "," + column + ") can reach the " + hs.size() + " peaks " + hs.toString());
					// System.out.println("Logged new peak at " + peakText);
				}

			} // column loop
		} // row loop

	return score;
	} // propagage method







	public static LetterGrid processFile(boolean debug, String path) {

		try {
			return new LetterGrid(path);
		} catch (Exception e) {
			System.out.println("problem processing file");
			return null;
		}
	} // processFile method



	public static void main(String[] args) {

		boolean debug = false;
		String path = "Y:\\code\\java\\AdventOfCode\\Day10input.dat";
		LetterGrid elevations = processFile(debug, path);
		LetterGrid scores = processFile(debug, path);
		System.out.println(elevations.getHeight());
		//System.out.println(scores.getHeight());
		//scores.wipe('0');

		//if(debug) System.out.println(elevations.getCell(2,2) + " vs " + scores.getCell(2, 2));

		HashSet<String> hs = new HashSet<String>();
		HashSet<String> hb = new HashSet<String>();
/*
		hs.add("a");hs.add("b");hs.add("c");hs.add("d");hs.add("e");

		hb.add("A");hb.add("b");hb.add("c");hb.add("Tony");

		hs.addAll(hb);

		for(String s : hs) System.out.println(s);
*/

		int part1score = propagate(debug, elevations);
		System.out.println("Therefore score is " + part1score);

	} // main method
} // Day10 class