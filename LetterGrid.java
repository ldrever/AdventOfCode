import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LetterGrid {

	private volatile int height, width;
	private volatile char[][] grid; // LIKE A MATRIX - FIRST COUNTER VERTICAL, SECOND COUNTER HORIZONTAL

	public synchronized int getHeight() {
		return this.height;
	}

	public synchronized int getWidth() {
		return this.width;
	}

	public synchronized char[][] getGrid() {
		return this.grid;
	}

	public synchronized char getCell(int row, int col) {
		return this.grid[row][col];
	}

	public synchronized void setCell(int row, int col, char c) {
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

	public synchronized void wipe(char c) {
		for(int row = 0; row < this.getHeight(); row++) {
			for(int column = 0; column < this.getWidth(); column++) {
				this.setCell(row, column, c);
			}
		}

	} // wipe method


	public synchronized void floodFill(boolean debug, ArrayList<Area> areaList) {

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

	...

	OK - the one-region version is working perfectly. Now to extend it
	so that it does the entire map. We will need to ensure:
	- already-processed regions do not get re-looked at, even though
	  they DO need to be considered for the purpose of edge-finding
	  and edge-counting
	- when finishing a region, the "borderland" from its point of view
	  needs to become the to-do list; this will eventually cover the
	  whole map. The subsequent region though needs to remove its OWN
	  territory from the borderland register (while extending it with its
	  own borders with THIRD territories)

	How about if we were to maintain three registers? Done, Current and
	Future (aka borderland)?

	- Start with the only cell in any register being (0,0) in Future.
	- Start in a state of betweenRegions = true
	- As with any moment in such a state, find the NEXT cell in
	  Future (not possible? then we're done), flip that flag, and
	  proceed to move that cell into the Current register
	- Now that we're processing and NOT betweenRegions, do the standard
	  process below, with the main difference being that anything from
	  the Done register should NOT get flagged as Borderlands
	- When this per-region process finishes, re-flag all Current cells
	  as Done, and re-set betweenRegions = true

... presumably want to modify from the innermost loop outwards...
*/

		ArrayList<Integer> friendlyRows = new ArrayList<Integer>();
		ArrayList<Integer> friendlyCols = new ArrayList<Integer>();
		int friendlyCount = 0;

		ArrayList<Integer> foreignRows = new ArrayList<Integer>();
		ArrayList<Integer> foreignCols = new ArrayList<Integer>();
		int foreignCount = 0;

		ArrayList<Integer> frontierRows = new ArrayList<Integer>();
		ArrayList<Integer> frontierCols = new ArrayList<Integer>();
		frontierRows.add(0);
		frontierCols.add(0);
		int frontierCount = 1;

		// outer loop: are there any frontier cells left to process?
		while(frontierCount > 0)
		{

			//  change first frontier cell to a friendly
			int startRow = frontierRows.get(0);
			int startCol = frontierCols.get(0);

			friendlyRows.add(startRow);
			friendlyCols.add(startCol);
			friendlyCount++;

			frontierRows.remove(0);
			frontierCols.remove(0);
			frontierCount--;

			char friendlyChar = this.getCell(startRow, startCol);
			Area friendlyArea = new Area(startRow, startCol, friendlyChar);
			areaList.add(friendlyArea);
			int areaIndex = areaList.size() - 1;
			int edgeCount = 0;

			// inner loop: are there any friendly cells left to process?
			// unlike frontier cells, we don't eliminate them as we process
			// them, so here, we just count up from zero, trusting that new
			// ones are added to the end, and will eventually get processed
			for(int cell = 0; cell < friendlyCount; cell++) {
				int cellRow = friendlyRows.get(cell);
				int cellCol = friendlyCols.get(cell);

				// handle all four of the current friendly cell's NEIGHBOURS:
				for(int dy = -1; dy <= 1; dy++) {

					nextNeighbour:
					for(int dx = -1; dx <= 1; dx++) {
						if(Math.abs(dx) + Math.abs(dy) != 1) continue nextNeighbour; // move in precisely one direction

						try {
							int newRow = cellRow + dy;
							int newCol = cellCol + dx;
							char newChar = this.getCell(newRow, newCol);



							// scenario A - registered friend: nothing to do
							for(int check = 0; check < friendlyCount; check++) {
								if(friendlyRows.get(check) == newRow && friendlyCols.get(check) == newCol) {
									continue nextNeighbour;
								}
							} // friend check loop



							// scenario B - registered foreigner: just note the edge and move on
							for(int check = 0; check < foreignCount; check++) {
								if(foreignRows.get(check) == newRow && foreignCols.get(check) == newCol) {
									edgeCount++;
									friendlyArea.addPath(new Path(cellRow, cellCol, dy, dx, this));

									continue nextNeighbour;
								}

							} // foreign check loop



							// scenario C - registered frontier: evaluate it for friendliness,
							// and either transfer it to that register if so, OR note the edge
							for(int check = 0; check < frontierCount; check++) {
								if(frontierRows.get(check) == newRow && frontierCols.get(check) == newCol) {

									if(newChar == friendlyChar) {

										friendlyRows.add(newRow);
										friendlyCols.add(newCol);
										friendlyCount++;

										frontierRows.remove(check);
										frontierCols.remove(check);
										frontierCount--;

									} else {
										edgeCount++;
										friendlyArea.addPath(new Path(cellRow, cellCol, dy, dx, this));
									}

									continue nextNeighbour;
								}

							} // frontier check loop


							// if we've reached here -
							// scenario D - new cell not in any register: evaluate it, and
							// add as friend or frontier accordingly

							if(newChar == friendlyChar) {
								friendlyRows.add(newRow);
								friendlyCols.add(newCol);
								friendlyCount++;
							} else {
								frontierRows.add(newRow);
								frontierCols.add(newCol);
								frontierCount++;

								edgeCount++;
								friendlyArea.addPath(new Path(cellRow, cellCol, dy, dx, this));
							}

						} catch (Exception e) {
							// scenario E - edge of the map
							edgeCount++;
							friendlyArea.addPath(new Path(cellRow, cellCol, dy, dx, this));

						} // try-catch

					} // dx loop
				} // dy loop
			} // inner friendly cell loop

			// NICE TO HAVE only do this when they touch the border...


			// summarize region
			friendlyArea.setBlocks(friendlyCount);
			friendlyArea.setEdgeSegments(edgeCount);

			if(debug) {
				System.out.println("The '" + friendlyChar + "' region including (" + startRow + "," + startCol + ") has " + friendlyCount + " cells and " + edgeCount + " edges.");
				System.out.println("HENCE *ITS* SCORE IS " + friendlyArea.edgeScore());
				System.out.println("(There are now " + frontierCount + " frontier cells.)");
				System.out.println();
			}

			// bulk-move all friendlies to foreign
			// NICE TO HAVE - faster if non-interior cells skup...
			for(int i = 0; i < friendlyCount; i++) {
				foreignRows.add(friendlyRows.get(i));
				foreignCols.add(friendlyCols.get(i));
				foreignCount++;
			} // bulk-move loop

			friendlyRows.clear();
			friendlyCols.clear();
			friendlyCount = 0;

		} // while(frontierCount > 0)

	} // floodFill method



}
