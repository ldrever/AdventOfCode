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



	public ArrayList<Integer> getRows() {
		return this.rows;
	}

	public ArrayList<Integer> getCols() {
		return this.cols;
	}


	public void display() {
		for(int i = 0; i < rows.size(); i++) {
			System.out.print((i == 0 ? "" : "->") + "(" + rows.get(i) + "," + cols.get(i) + ")");

		}
		System.out.print(";");

	}



	public boolean isLoop() {
		// note that there is never an act of "tying the knot"; a path is
		// assumed to be a loop if it has the same point for a head and tail

		if(this.rows.get(0) == this.rows.get(this.rows.size() - 1)) {
			if(this.cols.get(0) == this.cols.get(this.cols.size() - 1)) {
				return true;
			}
		}
		return false;

	} // isLoop method


	public boolean attemptForwardsJoin(boolean debug, Path p2) {

		boolean result = false;
		if(debug) System.out.print("Checking whether path ");
		if(debug) p2.display();
		if(debug) System.out.println(" can follow on from path ");
		if(debug) this.display();
		if(debug) System.out.println(" Result:");

		ArrayList<Integer> p2Rows = p2.getRows();
		ArrayList<Integer> p2Cols = p2.getCols();

		if(this.rows.get(this.rows.size() - 1) == p2Rows.get(0)) {
			if(this.cols.get(this.cols.size() - 1) == p2Cols.get(0)) {
				for(int i = 1; i < p2Rows.size(); i++) { // start from 1 not 0, since 0 is a repeat of this's end-point
					this.rows.add(p2Rows.get(i));
					this.cols.add(p2Cols.get(i));

				}
				if(debug) System.out.println(" success");
				return true;
			}

		}
		if(debug) System.out.println(" failure");
		return false;

	} // attemptForwardJoin method



	public Path (int regionRow, int regionCol, int nonRegionRowOffset, int nonRegionColOffset) {

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
			arrowEndRow++;
		}

		if(nonRegionRowOffset == 0 && nonRegionColOffset == -1) {
			// external territory to the WEST
			// means nudge arrow-start SOUTH
			arrowStartRow++;

			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it NORTH
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
			arrowEndCol--;
		}

		if(nonRegionRowOffset == -1 && nonRegionColOffset == 0) {
			// external territory to the NORTH
			// means nudge arrow-start NOT AT ALL

			arrowEndRow = arrowStartRow;	// left in for clarity
			arrowEndCol = arrowStartCol;	// left in for clarity

			// and point it EAST
			arrowEndCol++;
		}

		this.rows.add(arrowStartRow);
		this.rows.add(arrowEndRow);
		this.cols.add(arrowStartCol);
		this.cols.add(arrowEndCol);

	}



} // path class
