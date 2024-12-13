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
		int innerloop = 0;
		while(boundary.size() > 0)
		{
			innerloop++;
			int outerloop = 0;
			do { // inner do-loop, because we can't trust that the paths come to us in the right order to append
				 // so have to iterate indefinitely, trusting that eventually the first path will be a loop
				outerloop++;
				Path p0 = this.boundary.get(0);
				boolean connected = false;

				if(debug) System.out.print("Next pass in making forward joins to path ");
				if(debug) p0.display();

				if( outerloop + innerloop > 30000) debug = true;
				if(debug) {
					Scanner sc = new Scanner(System.in);
					System.out.println("go?");
					String input = sc.next();
					if(input.equalsIgnoreCase("N")) return;
				}


				ArrayList<Path> lefties = new ArrayList<Path>();
				boolean allowLeftTurns = false;

				for(int i = this.boundary.size() - 1; i > 0; i--) {

					Path p1 = this.boundary.get(i);
					//if(debug) System.out.print("Trying out path ");
					//if(debug) p1.display();

					String result = p0.attemptJoin(debug, allowLeftTurns, p1);

					if(result.equals("OTHER JOIN")) {
						boundary.remove(i);
						if(debug) System.out.println("Path " + i + " was successfully joined on.");
						connected = true;
						break;
					}

					if(result.equals("LEFT JOIN")) {
						lefties.add(p1);
						boundary.remove(i);
						if(debug) System.out.println("Path " + i + " (a left join) deferred.");

					}
				} // for i loop



				if(!connected) {
					allowLeftTurns = true;
					for(int j = lefties.size() - 1; j >= 0; j--) {
						Path p1 = lefties.get(j);

						String result = p0.attemptJoin(debug, allowLeftTurns, p1);
						if(result.equals("LEFT JOIN")) {
							lefties.remove(j);
							if(debug) System.out.println("Left-turning path " + j + " was successfully joined on.");
						}

					}
				}

				// trusting that SOMETHING has been added on, now need to empty lefties back into boundary
				// ready for the next joinment
				for(int k = 0; k < lefties.size(); k++) {
					boundary.add(lefties.get(k));
					lefties.remove(k);
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
