import java.util.*;
import java.io.*;

public class Path {

	// With regard to some connected sub-region of a 2D grid, let's say that
	// a PATH is a connected subset of that region's boundary, such that the
	// region lies on the right-hand side of the path as it is traversed.

	// represent it as a sequence of coordinates, in such a way that a cell
	// on the grid shares the same coordinate representation as the vertex
	// to its top left

	// the number of path segments is one less than the size of these:
	private volatile ArrayList<Integer> rows;
	private volatile ArrayList<Integer> cols;
	private volatile String initialDirection;
	private volatile LetterGrid parentGrid;

	public synchronized LetterGrid getParentGrid() {
		return this.parentGrid;
	}

	public synchronized String getInitialDirection() {
		return this.initialDirection;
	}


	public synchronized ArrayList<Integer> getRows() {
		return this.rows;
	}

	public synchronized ArrayList<Integer> getCols() {
		return this.cols;
	}



	public synchronized String toString() {
		String result = "";
		for(int i = 0; i < this.rows.size(); i++) {
			result += ((i == 0 ? "" : "->") + "(" + this.rows.get(i) + "," + this.cols.get(i) + ")");

		}
		return result;
	}


	public synchronized String toAbbreviatedString(int maxLength) {

		String fullString = this.toString();
		if(fullString.length() <= maxLength) return fullString;

		String dots = "...";
		String results = fullString.substring(0, (maxLength-dots.length())/2);
		results += dots;
		results += fullString.substring(fullString.length() - results.length(), fullString.length());
		return results;
	}




	public Path(Path forwardsPath) {
	// special constructor returning a one-step path that WOULD
	// flow inwards to the parameter path, in the same direction as its start
		this.parentGrid = forwardsPath.getParentGrid();
		this.initialDirection = forwardsPath.getInitialDirection();

		int endRow = forwardsPath.getRows().get(0);
		int endCol = forwardsPath.getCols().get(0);

		int startRow = endRow;
		int startCol = endCol;

		switch (this.initialDirection) {
			case "NORTH":
			startRow++;
			break;

			case "SOUTH":
			startRow--;
			break;

			case "WEST":
			startCol++;
			break;

			case "EAST":
			startCol--;
			break;
		}

		this.rows = new ArrayList<Integer>();
		this.cols = new ArrayList<Integer>();

		this.addPoint(startRow, startCol, "startpoint of imaginary vector");
		this.addPoint(endRow, endCol, "endpoint of imaginary vector");

		//System.out.println("Imaginary path used: " + this.toString());

	}

	public synchronized int countCorners2() {
		ArrayList<Path> steps = new ArrayList<Path>();

		// remember that 0 and this.rows.size() - 1 are the same point
		// a path with N entries in rows/cols means N vertices, and N-1
		// individual steps
		System.out.println("Starting corner-count on " + this.toString());
		for(int i = 0; i < this.rows.size() - 1; i++) {
			System.out.print("The " + i + "th step is ");
			Path step = new Path(this, i);
			System.out.println(step.toString());
			steps.add(step);
		}

		int stepCount = steps.size();
		System.out.println(stepCount + "steps detuct");
		Path p1 = steps.get(stepCount - 1);

		int count = 0;

		for(int i = 0; i < stepCount; i++) {

			Path p2 = steps.get(i);
			System.out.println("corner-detection between " + p1.toString() + " and " + p2.toString());
			byte type = p1.joinType(p2);
			p1 = p2;
			if(type > 0) count++;

		}
		return count;

	}


	public synchronized int countCorners(boolean debug) {

		int result = 0;

		System.out.print("countCorners has started at 0...");
		System.out.println(this.toString());


		ArrayList<Integer> myRows =  this.rows;


		ArrayList<Integer> myCols = this.cols;

			int size = myRows.size();



			// zeroth point is a repeat of the last point, so ignore it, and
			// ensure that size is adjusted down by when when working out
			// modulus results

			for(int currentIndex = 1; currentIndex < size; currentIndex++) {
				System.out.println(result + "at index " + currentIndex);
				// pre-adding the size prevents the modulus operator from returning a negative
				int prevIndex = ((size - 1) + currentIndex - 1) % (size - 1);
				int nextIndex = ((size - 1) + currentIndex + 1) % (size - 1);

				String showme = "Evaluating cornerity of (" + myRows.get(prevIndex) + "," + myCols.get(prevIndex) + ")->("+myRows.get(currentIndex)+","+myCols.get(currentIndex)+") vs "
														+ "(" + myRows.get(currentIndex)+","+myCols.get(currentIndex) + ")->("+myRows.get(nextIndex)+","+myCols.get(nextIndex)+") vs ";


				System.out.println(showme);

				int horizCount = 0;
				if (myRows.get(prevIndex) == myRows.get(currentIndex)) horizCount++;
				if (myRows.get(currentIndex) == myRows.get(nextIndex)) horizCount++;

				// XOR the horizontality going in and out of a given vertex - it's
				// a corner if precisely one of the edges is horizontal
				if(horizCount == 1) {

					result++;


				}

			} // currentIndex loop



		System.out.println("and finished at " + result);
		return result;

	} // countCorners method



	public synchronized boolean isLoop() {
		// check whether the start is a valid join to the end

			return (this.joinType(this) >= 0);
		}


		// note that there is never an act of "tying the knot"; a path is
		// assumed to be a loop if it has the same point for a head and tail
/*
		if(this.rows.get(0) == this.rows.get(this.rows.size() - 1)) {
			if(this.cols.get(0) == this.cols.get(this.cols.size() - 1)) {
				return true;
			}
		}
		return false;
*/

/*
				--V
				-W-
				---

	Observe how the loop around the "-" region touches itself at one point -
	this causes a bug; the code above will erroneously identify loops
	prematurely.

	How to fix? Perhaps we could start by COUNTING those points that appear
	more than once. No, best just to use history and track loopage as a
	variable...

*/



	public synchronized void addPoint(int row, int col, String message) {

int i = 0;

		if (this.rows.size() != 0) {
			if (this.rows.get(this.rows.size() - 1) == row) {
				if (this.cols.get(this.cols.size() - 1) == col) {
					Scanner sc = new Scanner(System.in);
					System.out.println(message);
					System.out.println("continue?");
					String input = sc.next();
					if(input.equalsIgnoreCase("N")) i = 1 / 0;
				}
			}
		}
		// trusts that this point DEFINITELY joins on to the end
		this.rows.add(row);
		this.cols.add(col);


		/*
			Not enough. A true check needs the following:
			- start = end, of course
			- if they're going in the same direction, then that's fine
			- if it's a right turn, that's fine
			- if it's a left turn, then the "elbow square" must match
			both squares that the lines run past

		*/

	} // addPoint method



	public synchronized int getEndRow() {return this.rows.get(this.rows.size() - 1);}
	public synchronized int getEndCol() {return this.cols.get(this.cols.size() - 1);}

	public synchronized int getStartRow() {return this.rows.get(0);}
	public synchronized int getStartCol() {return this.cols.get(0);}



	public synchronized byte joinType(Path p2) {
		/*
			Returns:
			-2 for an invalid left turn
			-1 if they don't touch
			0 if it's a collinear join
			+1 for a valid right turn
			+2 for a valid left turn

			Note that right turns can't be invalid, because the same square
			on the grid is to the right of both arrows.

		*/

		// start/end check
		if (this.getEndRow() != p2.getStartRow()) return -1;
		if (this.getEndCol() != p2.getStartCol()) return -1;

		// if they DO touch, work out the direction of turn - helps to picture a clock face
		ArrayList<Integer> p2Rows = p2.getRows();
		ArrayList<Integer> p2Cols = p2.getCols();

		int nextRow = p2Rows.get(1);
		int nextCol = p2Cols.get(1);

		int preRow = this.rows.get(this.rows.size() - 2);
		int preCol = this.cols.get(this.cols.size() - 2);

		int thisRow = this.rows.get(this.rows.size() - 1);
		int thisCol = this.cols.get(this.cols.size() - 1);

		boolean debug = false; // (thisRow == 0 && thisCol == 0);

		int newDx = nextCol - thisCol;
		int newDy = nextRow - thisRow;

		int oldDx = thisCol - preCol;
		int oldDy = thisRow - preRow;

		int oldClockDirection = 0;
		if(oldDy == -1) oldClockDirection = 12;
		if(oldDx == 1) oldClockDirection = 3;
		if(oldDy == 1) oldClockDirection = 6;
		if(oldDx == -1) oldClockDirection = 9;

		int newClockDirection = 0;
		if(newDy == -1) newClockDirection = 12;
		if(newDx == 1) newClockDirection = 3;
		if(newDy == 1) newClockDirection = 6;
		if(newDx == -1) newClockDirection = 9;


		int turn = newClockDirection - oldClockDirection;
		turn += 12; // now it's positive
		turn %= 12; // now it's 0 - 11


		switch(turn) {

			case 0:return 0;
			case 3:return 1;
			// case 6, being a 180-degree turn, is already made impossible by
			// the start/end check

		}
		if (debug) System.out.println("Pre and post clockface directions here are " + oldClockDirection + " and " + newClockDirection);


		// if it's a LEFT turn though, we need to verify that the "elbow"
		// character matches the character on both paths' right-hand-sides

		// given that we have just made a left turn, the elbow character will
		// be the right character of an imaginary arrow lined up behind the
		// new arrow
		if (debug) System.out.println("PROCESSING left turn detected from (" + preRow + "," + preCol + ") to (" + thisRow + "," + thisCol + ") to (" + nextRow + "," + nextCol + ")");

		Path imaginary = new Path(p2);
		char elbow = imaginary.getRightChar();
		if(this.getRightChar() == elbow && p2.getRightChar() == elbow)
			return 2;
		else
			return -2;

	} // joinType method



	public synchronized char getRightChar() {

			// what character lies on this path's right bank?

			// knowing that a path's origin vertex shares the same coordinates
			// as the square below and right of that vertex, start by saying
			int charRow = this.rows.get(0);
			int charCol = this.cols.get(0);
	//	try {

			// now adjust based on direction
			switch (this.initialDirection)
			{
				case "EAST":
				// do nothing
				break;

				case "WEST":
				// want the character north and west of that initial choice
				charRow--;
				charCol--;
				break;

				case "NORTH":
				// want the character north of that choice
				charRow--;
				break;

				case "SOUTH":
				// want the character west of that choice
				charCol--;
				break;
			}

			return parentGrid.getCell(charRow, charCol);
			/*
		} catch (Exception e) {
			System.out.println("Houston we have a problem");
			System.out.println("Attempted to return the character at (" + charRow + "," + ")");
			System.out.println("(Grid size: " + this.parentGrid.getHeight() + "x" + this.parentGrid.getWidth() + ")");
			return '!';
		}
		*/
	} // getRightChar method


	public synchronized String attemptJoin(boolean debug, boolean allowLeftTurns, Path p2) {

		String result = "OTHER JOIN";
		byte joinType = this.joinType(p2);
		if(joinType < 0) return "NO JOIN";
		boolean permissionToExecute = true;
		if(joinType == 2) { // valid left turn
			if(debug) System.out.println("***** LEFT TURN DETECTED *****");
			result = "LEFT JOIN";
			permissionToExecute = allowLeftTurns;
		}

		if(permissionToExecute) {

			ArrayList<Integer> p2Rows = p2.getRows();
			ArrayList<Integer> p2Cols = p2.getCols();

			// start from 1 not 0, since 0 is a repeat of this's end-point
			for(int i = 1; i < p2Rows.size(); i++) {
				this.addPoint(p2Rows.get(i), p2Cols.get(i), i + "th addment of attemptJoin");
			}
			if(debug) System.out.println(" success");

		} // permissionToExecute check
		return result;

	} // attemptJoin method


	public Path (Path input, int step) {
		// special constructor
		// return the 0+'th single-step sub-path of the input



		ArrayList<Integer> inputRows = input.getRows();
		ArrayList<Integer> inputCols = input.getCols();

		this.rows = new ArrayList<Integer>();
		this.cols = new ArrayList<Integer>();

		this.rows.add(inputRows.get(step));
		this.cols.add(inputCols.get(step));

		this.rows.add(inputRows.get(step + 1));
		this.cols.add(inputCols.get(step + 1));

		this.parentGrid = input.getParentGrid();

		int dy = this.rows.get(1) - this.rows.get(0);
		int dx = this.cols.get(1) - this.cols.get(0);

		if (dx == 1) this.initialDirection = "EAST";
		if (dx == -1) this.initialDirection = "WEST";
		if (dy == 1) this.initialDirection = "SOUTH";
		if (dy == -1) this.initialDirection = "NORTH";

		System.out.println("testing we got here at step " + step);
	}


	public Path (int regionRow, int regionCol, int nonRegionRowOffset, int nonRegionColOffset, LetterGrid parentGrid) {
		this.parentGrid = parentGrid;
		this.initialDirection = "";

		this.rows = new ArrayList<Integer>();
		this.cols = new ArrayList<Integer>();


		int	arrowStartRow = regionRow;
		int arrowStartCol = regionCol;

		int arrowEndRow = -1;
		int arrowEndCol = -1;

		if(nonRegionRowOffset == 0 && nonRegionColOffset == 1) {
			// external territory to the EAST
			// means nudge arrow-start EAST
			arrowStartCol++;


			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it SOUTH
			this.initialDirection = "SOUTH";
			arrowEndRow++;

		}

		if(nonRegionRowOffset == 0 && nonRegionColOffset == -1) {
			// external territory to the WEST
			// means nudge arrow-start SOUTH
			arrowStartRow++;

			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it NORTH
			this.initialDirection = "NORTH";
			arrowEndRow--;
		}

		if(nonRegionRowOffset == 1 && nonRegionColOffset == 0) {
			// external territory to the SOUTH
			// means nudge arrow-start SOUTH AND EAST
			arrowStartRow++;
			arrowStartCol++;

			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it WEST
			this.initialDirection = "WEST";
			arrowEndCol--;
		}

		if(nonRegionRowOffset == -1 && nonRegionColOffset == 0) {
			// external territory to the NORTH
			// means nudge arrow-start NOT AT ALL

			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it EAST
			this.initialDirection = "EAST";
			arrowEndCol++;
		}

		this.addPoint(arrowStartRow, arrowStartCol, "basic bitch flood fill startpoint");
		this.addPoint(arrowEndRow, arrowEndCol, "basic bitch flood fill endpoint");

	}



} // path class
