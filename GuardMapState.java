import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GuardMapState {

	private boolean debug;

	// either NEW, IN-PROGRESS, ESCAPE or LOOP:
	private String status;

	// current state of the map
	private LetterGrid map;

	// where she is and what direction she's moving:
	private int guardRow;
	private int guardColumn;
	private int southSpeed;
	private int eastSpeed;

	// how many distinct squares has she been on:
	private int groundBreakings;

	// a record OF those distinct squares, but EXCLUDING the start-point
	// (separate objects for the row and column dimensions - be careful
	// to look only at correctly-matching pairings)
	private ArrayList<Integer> potentialBlockerRows;
	private ArrayList<Integer> potentialBlockerColumns;



	public GuardMapState(boolean debug, String path) throws IOException {

		this.debug = debug;
		this.status = "NEW";

		// implicit assumptions such as the presence of precisely
		// one '^' origin character, and no others besides '.' and '#'
		this.map = new LetterGrid(path);
		this.findGuard(); // intialise position

		// initialise direction - going northwards at start
		this.southSpeed = -1;
		this.eastSpeed = 0;

		// every other cell on the path gets the following treatment when
		// it is first entered; that doesn't apply to the starting cell,
		// so must be done specially.
		boolean dummy = cellUpdate();
		this.groundBreakings = 1;

		this.potentialBlockerRows = new ArrayList<Integer>();
		this.potentialBlockerColumns = new ArrayList<Integer>();

	} // constructor



	public GuardMapState(boolean debug, String path, int blockedRow, int blockedColumn) throws IOException {

		this(debug, path);
		if (debug) System.out.println("Creating a new map with a blocker at (" + blockedRow + ", " + blockedColumn + ")");
		this.map.setCell(blockedRow, blockedColumn, '#');

	} // constructor with blocking


	public void findGuard() {

		for(int row = 0; row < map.getHeight(); row++) {
			for(int column = 0; column < map.getWidth(); column++) {

				char cellChar = map.getCell(row,column);
				if(cellChar == '^') {
					this.guardColumn = column;
					this.guardRow = row;

					// can now eliminate the '^' in favour of standard cell coding
					this.map.setCell(row, column, '.');
					if (debug) System.out.println("New map. Guard starts at (" + row + ", " + column + ")");

					return;
				}
			}
		}
	} // findGuard



	public String getStatus() {
		return this.status;
	}



	public int getGroundBreakings() {
		return this.groundBreakings;
	}



	public ArrayList<Integer> getPotentialBlockerRows() {
		return this.potentialBlockerRows;
	}



	public ArrayList<Integer> getPotentialBlockerColumns() {
		return this.potentialBlockerColumns;
	}



	public char whatsInFrontOfGuard() {

		int nextRow = this.guardRow + this.southSpeed;
		int nextColumn = this.guardColumn + this.eastSpeed;

		if(nextRow == -1 || nextColumn == -1) return 'O'; // represents the outside
		if(nextRow == map.getHeight() || nextColumn == map.getWidth()) return 'O';

		return map.getCell(nextRow, nextColumn);

	} // whatsInFrontOfGuard



	public void rotateClockwise() {

		if(this.eastSpeed == 0) {

			this.eastSpeed = - this.southSpeed;
			this.southSpeed = 0;

		} else {

			this.southSpeed = this.eastSpeed;
			this.eastSpeed = 0;
		}

	} // rotate



	public boolean evolve(boolean updateBlockingCandidates) {
		this.status = "IN-PROGRESS";

		// evolve just means progress the state inexorably onwards from the current state
		// returns true only if the status is still IN-PROGRESS, and further evolution is
		// possible; returns false once a state of ESCAPE or LOOP obtains

		if (debug) System.out.print("at position (" + guardRow + ", " + guardColumn + ")");

		// start by looking at the next square:
		char nextChar = this.whatsInFrontOfGuard();
		if (debug) System.out.println("; in front of me is: " + nextChar);

		// update the position, applying a temporary default assumption that we're carrying on
		// in the same direction:
		this.guardRow += this.southSpeed;
		this.guardColumn += this.eastSpeed;

		// main logic
		switch (nextChar) {

			case 'O': // we've found the outside; no need to break
				this.status = "ESCAPE";
				return false;

			case '#': // blocker - means must undo the position-update
				this.guardColumn -= this.eastSpeed;
				this.guardRow -= this.southSpeed;

				// and then rotate
				this.rotateClockwise();

				break;

			case '.':
				// these treatments needed for a ground-breaking cell...
				if(updateBlockingCandidates) {
					potentialBlockerRows.add(this.guardRow);
					potentialBlockerColumns.add(this.guardColumn);
				}
				this.groundBreakings++;

				// ... before proceeding to treat it in the normal manner for
				// coded cells - hence intentionally absent break clause

			default:
				if(this.cellUpdate()) {
					this.status = "LOOP";
					return false;
				}

		} // switch

		return true;

	} // evolve



	/*
		capture all directions
		of travel that a cell has been subject to. It seems reasonably clear that a loop
		situation happens, if and only if a cell exists that has been traversed in the same
		direction, more than once.

		relies on bit-level stuff

		Let's say that the baseline or zero number represents '.' and then add 1 for north, 2 for
		east, 4 for south, 8 for west. Thus there are 16 combinations (0 to 15).

	*/

	boolean cellUpdate() {
		// return true if that cell's been traversed before, IN THIS SAME DIRECTION.
		// side effect being to update that cell based on the direction of motion

		int magicValue = 0;

		if(this.southSpeed == -1) magicValue = 1;
		if(this.eastSpeed == 1) magicValue = 2;
		if(this.southSpeed == 1) magicValue = 4;
		if(this.eastSpeed == -1) magicValue = 8;

		// value from 0-15 representing the history of directions this cell has seen
		int cellHistory = (int) this.map.getCell(this.guardRow, this.guardColumn) - (int)'.'; // equate dot with zero

		int cellFuture = cellHistory | magicValue;

		int newCellValue = cellFuture + (int)'.';

		this.map.setCell(this.guardRow, this.guardColumn, (char) newCellValue);

		return (cellFuture == cellHistory);

	} // cellUpdate

} // class