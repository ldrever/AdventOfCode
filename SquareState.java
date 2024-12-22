import java.util.*;
import java.io.*;

class SquareState {

	private LetterGrid parentGrid;
	private int minimumExitCost;

	// position:
	private int row;
	private int column;

	// direction - model it as the way they are facing, in this cell:
	private int dy;
	private int dx;

	public SquareState(LetterGrid parentGrid; int minimumExitCost, int row, int column, int dy, int dx) {
		this.parentGrid = parentGrid;
		this.row = row;
		this.column = column;
		this.dy = dy;
		this.dx = dx;
	} // constructor


	public ArrayList<SquareState> whoCanReachMe(int maxCost, int turnCost) { // FIXME needs? , ArrayList<SquareState> disallowedZone

		// for now let's just focus on who can reach me

		//


	} // whoCanReachMe method


} // SquareState class