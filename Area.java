import java.util.*;
import java.io.*;

public class Area {

	int homeCellRow;
	int homeCellCol;
	char allegiance;
	int blockCount;
	int edgeSegmentCount;
	int sideCount;

	ArrayList<Path> boundary;

	public String homeString() {
		String result = "";
		result += "(" + this.homeCellRow + "," + this.homeCellCol + ")";
		return result;
	}

	public char getAllegiance() {
		return this.allegiance;
	}

	public void setBlocks(int blocks) {
		this.blockCount = blocks;
	}

	public void setEdgeSegments(int edgeSegments) {
		this.edgeSegmentCount = edgeSegments;
	}

	public void setSideCount(int sideCount) {
		this.sideCount = sideCount;
	}

	public int edgeScore() {

		return this.blockCount * this.edgeSegmentCount;
	}

	public int sideScore(boolean debug) {
		if(debug) System.out.println(this.blockCount + " blocks and " + this.sideCount + " sides.");
		return this.blockCount * this.sideCount;
	}


	public Area (int homeCellRow, int homeCellCol, char allegiance) {
		this.boundary = new ArrayList<Path>();

		this.homeCellRow = homeCellRow;
		this.homeCellCol = homeCellCol;
		this.allegiance = allegiance;
	}

	public void addPath(Path path) {
		this.boundary.add(path);
	}

	public void displayPaths() {
		for(Path path : boundary) {
			path.display();
		}
	} // displayPath method


	public void getLoops(boolean debug) {

		// work through its boundary set until that is empty, and the following contains one or more loop-paths:
		ArrayList<Path> loopSet = new ArrayList<Path>();

		// outermost while-loop, because we might have SOME loops, but do we have ALL loops?
		while(boundary.size() > 0)
		{
			do { // inner do-loop, because we can't trust that the paths come to us in the right order to append
				 // so have to iterate indefinitely, trusting that eventually the first path will be a loop

				Path p0 = this.boundary.get(0);
				if(debug) System.out.print("Next pass in making forward joins to path ");
				if(debug) p0.display();

				for(int i = this.boundary.size() - 1; i > 0; i--) {

					Path p1 = this.boundary.get(i);
					//if(debug) System.out.print("Trying out path ");
					//if(debug) p1.display();

					if(p0.attemptForwardsJoin(debug, p1)) {

						boundary.remove(i);
						if(debug) System.out.println("Path " + i + " was successfully joined on.");

					}

				}

			} // inner do loop

			while(!this.boundary.get(0).isLoop());
			loopSet.add(this.boundary.get(0));
			boundary.remove(0);

		} // outer while-loop

	// boundary will be empty at this point
	this.boundary = loopSet;

	} // getLoops method



	public void processLoops(boolean debug) {
		this.sideCount = 0;
		if(debug) System.out.println("Boundary includes " + boundary.size() + " paths.");
		// trusting that the boundary is already a small number of loops at this point
		for(Path path : boundary) {
			this.sideCount += path.countCorners();
			if(debug) System.out.print("Counted " + path.countCorners() + " corners in path ");
			if(debug) path.display();
			System.out.println();


		}


	} // processLoops method



} // area class
