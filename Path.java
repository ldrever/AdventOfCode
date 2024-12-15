import java.util.*;
import java.io.*;

public class Path {

	/*
		A PATH is a series of VERTICES on a 2D grid, such that between
		every neighbouring pair, there is a single STEP, horizontally or
		vertically, that threads between two CELLS on that grid. (If
		the last vertex is the same as the first one, then the path is
		presumed to be a loop.)

		Whether the path has one or many steps, and whether or not
		it loops, the convention is that walking a path starting
		from its zero'th point will always keep the relevant
		region on the right-hand-side. I.e., every step of the way,
		a cell will exist on the right hand edge of the path, and
		these cells will always contain the same character.

		Some implications of this are:
		- any region's outermost border will always run clockwise
		- paths that run along the edge of the grid will run clockwise
		- if a region has enclaves that are not part of it, those
		  boundaries will run anti-clockwise

	*/



	// data
	private volatile ArrayList<Integer> rows;
	private volatile ArrayList<Integer> cols;
	private volatile String initialDirection;
	private volatile LetterGrid parentGrid;



	// getters
	public synchronized ArrayList<Integer> getRows() {return this.rows;}
	public synchronized ArrayList<Integer> getCols() {return this.cols;}
	public synchronized int getStartRow() {return this.rows.get(0);}
	public synchronized int getStartCol() {return this.cols.get(0);}
	public synchronized int getEndRow() {return this.rows.get(this.rows.size() - 1);}
	public synchronized int getEndCol() {return this.cols.get(this.cols.size() - 1);}
	public synchronized String getInitialDirection() {return this.initialDirection;}
	public synchronized LetterGrid getParentGrid() {return this.parentGrid;}



	// constructors

	// basic constructor used by the flood fill algorithm, based on knowing a
	// boundary-touching cell, and the direction in which the boundary is
	// crossed from there
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

		this.addPoint(arrowStartRow, arrowStartCol);
		this.addPoint(arrowEndRow, arrowEndCol);

	}

	// special constructor to return the N'th step of the input
	public Path (Path input, int step) {

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

	}

	// special constructor returning a one-step path that WOULD
	// flow inwards to the parameter path, in the same direction as its start
	public Path(Path forwardsPath) {
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

		this.addPoint(startRow, startCol);
		this.addPoint(endRow, endCol);

	}



	// read-only methods
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

	public synchronized char getRightChar() {
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

	public synchronized byte joinType(Path p2) {
	/*

		It's worth noting that we can't just naively say that whenever a
		path self-intersects, that's it, and we're back to the beginning
		of the loop. Consider the "-" region here:

			--V
			-W-
			---

		Yes there's one outer loop around the region, but it also self-
		intersects.

		The way to handle this starts by observing that, given our right-
		handedness convention, a directed path that goes straight or
		turns right can be trusted NOT to cause any difficulties like
		this, because the same square on the grid is to the right of both
		steps. A left-hand turn can cause difficulties, which are dealt
		with here.

		Hence this method returns:
		-2 for an invalid left turn
		-1 if the two path ends don't touch
		0 if it's a collinear (straight-on) join
		+1 for a valid right turn
		+2 for a valid left turn

		Note that right turns can't be invalid,

	*/

		// nose/tail check
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

	public synchronized int countCorners() {
		ArrayList<Path> steps = new ArrayList<Path>();

		// remember that 0 and this.rows.size() - 1 are the SAME POINT
		for(int i = 0; i < this.rows.size() - 1; i++) {
			Path step = new Path(this, i);
			steps.add(step);
		}

		int stepCount = steps.size();
		int cornerCount = 0;

		// every step needs to be both p1 and p2 as we go round the circle;
		// this conveniently sets up the initial state, AND gets round the
		// looping-back-round situation
		Path p1 = steps.get(stepCount - 1);

		for(int i = 0; i < stepCount; i++) {
			Path p2 = steps.get(i);
			byte type = p1.joinType(p2);
			p1 = p2;
			if(type > 0) cornerCount++;

		}
		return cornerCount;
	}

	public synchronized boolean isLoop() {
		// this just says, if we consider one path as having the roles
		// of both first and second paths, do those two paths form
		// a valid head/tail join?
		return (this.joinType(this) >= 0);
	}



	// writing methods
	public synchronized void addPoint(int row, int col) {
		// trusts that this point DEFINITELY joins on to the end
		this.rows.add(row);
		this.cols.add(col);

	} // addPoint method

	public synchronized String attemptJoin(boolean debug, boolean allowLeftTurns, Path p2) {

		String result = "OTHER JOIN";
		byte joinType = this.joinType(p2);
		if(joinType < 0) return "NO JOIN";
		boolean permissionToExecute = true;
		if(joinType == 2) { // valid left turn
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

		} // permissionToExecute check
		return result;

	} // attemptJoin method



} // path class
