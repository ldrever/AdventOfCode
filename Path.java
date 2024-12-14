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
	private ArrayList<Integer> rows;
	private ArrayList<Integer> cols;
	private String initialDirection;
	private LetterGrid parentGrid;

	public LetterGrid getParentGrid() {
		return this.parentGrid;
	}

	public String getInitialDirection() {
		return this.initialDirection;
	}



	public Path(Path forwardsPath) {
	// special constructor returning a one-element path that WOULD
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

		rows.add(startRow);
		cols.add(startCol);
		rows.add(endRow);
		cols.add(endCol);

	}

	public ArrayList<Integer> getRows() {
		return this.rows;
	}

	public ArrayList<Integer> getCols() {
		return this.cols;
	}



	public String toString() {
		String result = "";
		for(int i = 0; i < rows.size(); i++) {
			result += ((i == 0 ? "" : "->") + "(" + rows.get(i) + "," + cols.get(i) + ")");

		}
		return result;
	}



	public int countCorners() {

		int result = 0;

		for(int currentIndex = 0; currentIndex < this.rows.size(); currentIndex++) {

			// pre-adding the size prevents the modulus operator from returning a negative
			int prevIndex = (this.rows.size() + currentIndex - 1) % this.rows.size();
			int nextIndex = (this.rows.size() + currentIndex + 1) % this.rows.size();

			boolean prevHoriz = (this.rows.get(prevIndex) == this.rows.get(currentIndex));
			boolean nextHoriz = (this.rows.get(currentIndex) == this.rows.get(nextIndex));

			// XOR the horizontality going in and out of a given vertex - it's
			// a corner if precisely one of the edges is horizontal
			if(prevHoriz ^ nextHoriz) result++;

		} // currentIndex loop

		return result;

	} // countCorners method



	public boolean isLoop() {
		// check whether the start is a valid join to the end
		return (this.joinType(this) >= 0);



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

	} // isLoop method


	public void addPoint(int row, int col) {

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



	public int getEndRow() {return this.rows.get(this.rows.size() - 1);}
	public int getEndCol() {return this.cols.get(this.cols.size() - 1);}

	public int getStartRow() {return this.rows.get(0);}
	public int getStartCol() {return this.cols.get(0);}



	public byte joinType(Path p2) {
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
		int oldDy = this.rows.get(this.rows.size() - 1) - this.rows.get(this.rows.size() - 2);
		int oldDx = this.cols.get(this.cols.size() - 1) - this.cols.get(this.cols.size() - 2);
		int oldClockDirection = 0;
		if(oldDy == -1) oldClockDirection = 12;
		if(oldDx == 1) oldClockDirection = 3;
		if(oldDy == 1) oldClockDirection = 6;
		if(oldDx == -1) oldClockDirection = 9;

		ArrayList<Integer> p2Rows = p2.getRows();
		ArrayList<Integer> p2Cols = p2.getCols();
		int newDy = p2Rows.get(1) - p2Rows.get(0);
		int newDx = p2Cols.get(1) - p2Cols.get(0);
		int newClockDirection = 0;
		if(newDy == -1) newClockDirection = 12;
		if(newDx == 1) newClockDirection = 3;
		if(newDy == 1) newClockDirection = 6;
		if(newDx == -1) newClockDirection = 9;

		int turn = newClockDirection -= oldClockDirection;
		turn += 12; // now it's positive
		turn %= 12; // now it's 0 - 11

		switch(newClockDirection) {

			case 0:return 0;
			case 3:return 1;
			// case 6, being a 180-degree turn, is already made impossible by
			// the start/end check

		}


		// if it's a LEFT turn though, we need to verify that the "elbow"
		// character matches the character on both paths' right-hand-sides

		// given that we have just made a left turn, the elbow character will
		// be the right character of an imaginary arrow lined up behind the
		// new arrow

		Path imaginary = new Path(p2);
		char elbow = imaginary.getRightChar();
		if(this.getRightChar() == elbow && p2.getRightChar() == elbow)
			return 2;
		else
			return -2;

	} // joinType method



	public char getRightChar() {
		// what character lies on this path's right bank?

		// knowing that a path's origin vertex shares the same coordinates
		// as the square below and right of that vertex, start by saying
		int charRow = this.rows.get(0);
		int charCol = this.cols.get(0);

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
	} // getRightChar method


	public String attemptJoin(boolean debug, boolean allowLeftTurns, Path p2) {

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
				this.addPoint(p2Rows.get(i), p2Cols.get(i));
			}
			if(debug) System.out.println(" success");

		} // permissionToExecute check
		return result;

	} // attemptJoin method




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

		this.rows.add(arrowStartRow);
		this.rows.add(arrowEndRow);
		this.cols.add(arrowStartCol);
		this.cols.add(arrowEndCol);

	}



} // path class
