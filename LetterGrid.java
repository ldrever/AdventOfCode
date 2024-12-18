import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LetterGrid {

	private volatile int height, width;
	private int robotCol, robotRow;
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

	public LetterGrid(boolean[][] inputBool, char ifTrue, char ifFalse) {

		this.height = inputBool.length;
		this.width = inputBool[0].length;

		this.grid = new char[this.height][this.width];

		for(int i = 0; i < this.height; i++) {
			for(int j = 0; j < this.width; j++) {
				this.grid[i][j] = inputBool[i][j] ? ifTrue : ifFalse;
			} // horizontal loop
		} // vertical loop

	} // constructor


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
		this.floodFill(debug, areaList, 0, 0);

	}


	public synchronized void floodFill(boolean debug, ArrayList<Area> areaList, int beginRow, int beginCol) {

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
		frontierRows.add(beginRow);
		frontierCols.add(beginCol);
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



	public long sumGPS(char box) {
		long score = 0;

		for(int i = 0; i < this.height; i++) {
			for(int j = 0; j < this.width; j++) {
				if(box == this.getCell(i, j)) {
					score += i * 100 + j;
				}
			}
		}

		return score;

	} // sumGPS method



	public void findRobot(boolean debug) {

		for(int row = 0; row < this.height; row++) {
			for(int column = 0; column < this.width; column++) {

				char cellChar = this.getCell(row,column);
				if(cellChar == '@') {
					this.robotCol = column;
					this.robotRow = row;

					if (debug) System.out.println("New map. robot starts at (" + row + ", " + column + ")");

					return;
				}
			}
		}
	} // findRobot method



	public void evolve(char next, boolean isPart1, boolean debug) {
		int dy = 0;
		int dx = 0;

		switch(next) {
			case 'v':
				dy++;
			break;

			case '^':
				dy--;
			break;

			case '<':
				dx--;
			break;

			case '>':
				dx++;
			break;
		} // switch char

		if(isPart1) {
			this.successfulPush(this.robotRow, this.robotCol, dy, dx);

		} else {
			if(dy == 0)
				this.successfulPush(this.robotRow, this.robotCol, dy, dx);
			else
				this.verticalPush(this.robotRow, this.robotCol, dy, dx, debug);
		}

		if(debug) this.displayArray();


	} // evolve method


	public void swapCells(int y1, int x1, int y2, int x2) {

		// obvious part
		char c1 = this.getCell(y1, x1);
		char c2 = this.getCell(y2, x2);

		this.setCell(y1, x1, c2);
		this.setCell(y2, x2, c1);

		// update the robot	position if applicable
		if(y1 == this.robotRow && x1 == this.robotCol) {
			this.robotRow = y2;
			this.robotCol = x2;
		} else if(y2 == this.robotRow && x2 == this.robotCol) {
			this.robotRow = y1;
			this.robotCol = x1;
		}


	} // swapCells method



	public HashSet<String> getPushSet(int y, int x, int dy, boolean debug) {

		if(this.getCell(y, x) != '[') y = 1/0;
	/*
		Note: unlike other methods, this is invoked on the location of
		a box, NOT the robot.

		Note: only ever invoke this on the location of a '[' character!

		When about to push a box in the vertical direction, first run this to
		determine the full set of boxes that move. If the HashSet it returns
		is empty, then the box can't be pushed.

		The key is that it recursively calls itself on one or both boxes that
		lie in the way of THIS box. ("Goal-position left" and "goal-position
		right".)

		Algorithm:
		- if EITHER goal-position is a wall, return the empty set
		- if BOTH goal-positions are free, then return the set of JUST THIS BLOCK
		- if a SINGLE block stands in the way of BOTH goal-positions, then
		  find the pushset for that block
		  	- return an empty one if it is empty
		  	- otherwise return it, with this block added on
		- otherwise, iterate over one or both dependency-blocks:
			- as soon as one returns the empty set, then also return the empty set
			- if that never happens, then return the pushsets obtained from the
			  dependency-blocks, PLUS this block

		Note:
		- this must only ever be invoked on the coordinates of the LEFT half of
		  a box.

	*/

		HashSet<String> pushSet = new HashSet<String>();
		HashSet<String> result = new HashSet<String>();

		// ie NOT empty
		HashSet<String> dependencySet = null;
		HashSet<String> leftSet = null;
		HashSet<String> rightSet = null;

		char wall = '#';
		char free = '.';
		char boxLeft = '[';
		char boxRight = ']';

		char leftGoal = this.getCell(y + dy, x);
		char rightGoal = this.getCell(y + dy, x + 1);

		if(leftGoal == wall || rightGoal == wall)	{
			// intentionally leave result empty

		} else if(leftGoal == free && rightGoal == free) {
			// no dependencies, but movable block? return itself
			result.add(this.paddedCoords(y, x)); // we can ONLY invoke this method on the left-hand side of any block

		} else if(leftGoal == boxLeft) {
			// directly in-line
			dependencySet = getPushSet(y + dy, x, dy, debug);
			if(dependencySet.size() == 0) {
				// cascade the empty pushset down
			} else {
				// return whatever the dependency had, plus this one
				for(String str : dependencySet) result.add(str);
				result.add(this.paddedCoords(y, x));
			}

		} else if(leftGoal == boxRight && rightGoal == free) {
			// staggered to the left
			dependencySet = getPushSet(y + dy, x - 1, dy, debug);
			if(dependencySet.size() == 0) {
				// cascade the empty pushset down
			} else {
				// return whatever the dependency had, plus this one
				for(String str : dependencySet) result.add(str);
				result.add(this.paddedCoords(y, x));
			}

		} else if(rightGoal == boxLeft && leftGoal == free) {
			// staggered to the right
			dependencySet = getPushSet(y + dy, x + 1, dy, debug);
			if(dependencySet.size() == 0) {
				// cascade the empty pushset down
			} else {
				// return whatever the dependency had, plus this one
				for(String str : dependencySet) result.add(str);
				result.add(this.paddedCoords(y, x));
			}

		} else if(leftGoal == boxRight && rightGoal == boxLeft) {
			// double dependency
			leftSet = getPushSet(y + dy, x - 1, dy, debug);
			rightSet = getPushSet(y + dy, x + 1, dy, debug);

			if(leftSet.size() == 0 || rightSet.size() == 0) {
				// cascade the empty pushset down
			} else {
				for(String str : leftSet) result.add(str);
				for(String str : rightSet) result.add(str);
				result.add(this.paddedCoords(y, x));
			}

		}

		return result;

	} // getPushSet method



	public String paddedCoords(int y, int x) {
		// motivation here is that we sort coordinates as strings, hence
		// don't want (10,2) appearing before (2,2) etc.

		int max = Math.max(this.height, this.width);
		String longEnough = "" + max;
		int digits = longEnough.length();

		String strFormat = "%0" + digits + "d";
		String strY = String.format(strFormat, y);
		String strX = String.format(strFormat, x);
		return "(" + strY + "," + strX + ")";

	} // paddedCoords method


	public static void displayOrderedPushSet(HashSet<String> pushSet, int dy) {
		if(pushSet.size() == 0) return;

		List<String> list = new ArrayList<String>(pushSet);
		Collections.sort(list);
		if(dy > 0) Collections.reverse(list);
		System.out.print("Pushset here: " );
		System.out.println(list.toString());

	} // displayOrderedPushSet method


	public void executePushes(HashSet<String> pushSet, int dy) {
		// key insight here is that if we can just SORT the pushset first,
		// such that values whose y-coordinate are the MOST extreme in the
		// direction of dy, then we can just move them all by invoking a
		// swap-with-free-space trick...

		List<String> list = new ArrayList<String>(pushSet);
		Collections.sort(list);
		if(dy > 0) Collections.reverse(list);

		// System.out.println(list.toString());

		for(String str : list) {

			String noBrackets = str.substring(1, str.length() - 1);
			String [] ra = noBrackets.split(",");
			int y = Integer.parseInt(ra[0]);
			int x = Integer.parseInt(ra[1]);

			swapCells(y, x, y + dy, x); // move the [-character
			swapCells(y, x + 1, y + dy, x + 1); // move the ]-character

		} // str in list loop

	} // executePushes method


	public boolean verticalPush(int y, int x, int dy, int dx, boolean debug) {
		// this is always being executed on (y,x) being the robot's coords...
		HashSet<String> pushSet = new HashSet<String>();
		// find out what's immediately in front of him
		switch (this.getCell(y + dy, x + dx)) {

			case '#':
				return false;

			case '.':
				swapCells(y, x, y + dy, x + dx);
				return true;

			case '[':
				pushSet = getPushSet(y + dy, x + dx, dy, debug);
				break;

			case ']':
				pushSet = getPushSet(y + dy, x + dx - 1, dy, debug);
				if(debug) displayOrderedPushSet(pushSet, dy);
				break;

		} // switch

		if(pushSet.size() == 0) return false;
		executePushes(pushSet, dy);
		swapCells(y, x, y + dy, x + dx);
		return true;
	} // verticalPush method



	public boolean successfulPush(int y, int x, int dy, int dx) {
		// say that we WANT to move the object in the cell at (y, x) into the
		// cell at (y + dy, x + dx); these will always be horizontal or
		// vertical neighbours

		switch (this.getCell(y + dy, x + dx)) {
			case '#':
				return false;
			case ']': // intentionally drop through to O
			case '[': // likewise
			case 'O':
				// recurse
				if(!this.successfulPush(y + dy, x + dx, dy, dx)) return false;
				// intentionally no break

			case '.':
				this.swapCells(y, x, y + dy, x + dx);
				return true;

		} // switch
		return false; // should be impossible
	} // attemptPush method



	public void displayArray() {

		for(int i = 0; i < this.height; i++) {
			for(int j = 0; j < this.width; j++)
				System.out.print(this.grid[i][j]);

			System.out.println();
		}

	} // displayArray method

}
