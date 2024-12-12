import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LetterGrid {

	private int height, width;
	private char[][] grid; // LIKE A MATRIX - FIRST COUNTER VERTICAL, SECOND COUNTER HORIZONTAL

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public char[][] getGrid() {
		return this.grid;
	}

	public char getCell(int row, int col) {
		return this.grid[row][col];
	}

	public void setCell(int row, int col, char c) {
		this.grid[row][col] = c;
	}

	public LetterGrid(String path) throws IOException {

		ArrayList<char[]> inputLines = new ArrayList<char[]>();
		Scanner diskScanner = new Scanner(new File(path));

		int width = 0;
		int height = 0;

		while (diskScanner.hasNext()) {
			String text = diskScanner.nextLine();

			inputLines.add(text.toCharArray());
			height++;
			if (width < text.length()) width = text.length();

		}

		diskScanner.close();

		this.grid = new char[height][];

		for(int i = 0; i < height; i++) {
			this.grid[i] = inputLines.get(i);
		}

		this.height = height;
		this.width = width;

	} // constructor

	public void wipe(char c) {
		for(int row = 0; row < this.getHeight(); row++) {
			for(int column = 0; column < this.getWidth(); column++) {
				this.setCell(row, column, c);
			}
		}

	} // wipe method


	public void floodFill(boolean debug, int startRow, int startCol) {

/*
	Start with a seed cell, and iteratively apply the principle that
	EACH OF ITS FOUR EDGES EITHER MEANS A FENCE, OR A CO-REGIONAL CELL TO
	APPLY THIS PROCESS TO.

	Bear in mind that most co-regional neighbours will be connected to
	the region along more than one edge.

	Because of the possibility of "holes", let's NOT do the "walk the
	entire perimeter while keeping your left hand on the wall" approach,
	but instead, track the cells in historic order of discovery, and
	insist on probing out all four edges of the oldest discoveries
	first. (Hopefully this is the standard flood-fill algorithm.)

*/
		char ourChar;
		try {
			ourChar = this.getCell(startRow, startCol);
		} catch (Exception e) {
			System.out.println("No such cell exists; no action taken.");
			return;
		}


		ArrayList<Integer> borderlandRows = new ArrayList<Integer>();
		ArrayList<Integer> borderlandCols = new ArrayList<Integer>();
		int borderlandCount = 0;

		ArrayList<Integer> ourRows = new ArrayList<Integer>();
		ArrayList<Integer> ourCols = new ArrayList<Integer>();
		ourRows.add(startRow);
		ourCols.add(startCol);
		int ourCount = 1;

		int edgeCount = 0;

		for(int cell = 0; cell < ourCount; cell++) {
			for(int dy = -1; dy <= 1; dy++) {
				for(int dx = -1; dx <= 1; dx++) {
					if(Math.abs(dx) + Math.abs(dy) != 1) continue; // move in precisely one direction
					try {
						int newRow = ourRows.get(cell) + dy;
						int newCol = ourCols.get(cell) + dx;
						char newChar = this.getCell(newRow, newCol);

						if(newChar == ourChar) {
							boolean alreadyNoted = false;
							// scenario 1: friendly neighbour, already known to be part of our region
							for(int check = 0; check < ourCount; check++) {
								if(ourRows.get(check) == newRow && ourCols.get(check) == newCol) {
									alreadyNoted = true;
									break;
								}
							} // check loop

							// scenario 2: friendly neighbour, encountered for the first time
							if(!alreadyNoted) {
								ourRows.add(newRow);
								ourCols.add(newCol);
								ourCount++;
								if(debug) System.out.println("Logged " + ourCount + "th friendly neighbour at (" + newRow + "," + newCol + ")");
							}

						} else {
							edgeCount++;

							// do to the borderland what we already did for our lands
							// FIXME pull out to a function, that both use? boolean on whether to up its count?

							boolean alreadyNoted = false;
							// scenario 3: cross-border neighbour, already known
							for(int check = 0; check < borderlandCount; check++) {
								if(borderlandRows.get(check) == newRow && borderlandCols.get(check) == newCol) {
									alreadyNoted = true;
									break;
								}
							} // check loop

							// scenario 4: cross-border neighbour, encountered for the first time
							if(!alreadyNoted) {
								borderlandRows.add(newRow);
								borderlandCols.add(newCol);
								borderlandCount++;
							}

						}
					} catch (Exception e) {
						// scenario 5: edge of the map
						edgeCount++;
					} // try-catch
				} // dx loop
			} // dy loop
		} // cell loop

		System.out.println("The '" + ourChar + "' region including (" + startRow + "," + startCol + ") has " + ourCount + " cells and " + edgeCount + " edges.");
		System.out.println("(" + borderlandCount + " bordering cells.)");
		System.out.println();
		System.out.println("HENCE ITS SCORE IS " + edgeCount * ourCount);

// maybe a shouldIModify flag? or just return an altered map?

// might want to return an AL of its "borderland"

	} // floodFill method
}
