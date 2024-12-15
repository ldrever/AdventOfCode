import java.util.*;
import java.io.*;

public class Area {

	private volatile int homeCellRow;
	private volatile int homeCellCol;
	private volatile char allegiance;

	private volatile int blockCount;
	private volatile int edgeSegmentCount;
	private volatile int sideCount;

	private volatile ArrayList<Path> boundary;

	public synchronized String homeString() {
		String result = "";
		result += "(" + this.homeCellRow + "," + this.homeCellCol + ")";
		return result;
	}

	public synchronized String getVitalStatistics(boolean debug) {
		String result = this.blockCount + " blocks, ";
		result += this.sideCount + " sides and ";
		result += this.edgeSegmentCount + " edge segments, thus scoring ";
		result += this.edgeScore() + " (part 1) and ";
		result += this.sideScore(debug) + " (part 2)";
		return result;
	}

	public synchronized char getAllegiance() {
		return this.allegiance;
	}

	public synchronized void setBlocks(int blocks) {
		this.blockCount = blocks;
	}

	public synchronized void setEdgeSegments(int edgeSegments) {
		this.edgeSegmentCount = edgeSegments;
	}

	public synchronized void setSideCount(int sideCount) {
		this.sideCount = sideCount;
	}

	public synchronized int edgeScore() {
		return this.blockCount * this.edgeSegmentCount;
	}

	public synchronized int sideScore(boolean debug) {
		return this.blockCount * this.sideCount;
	}


	public Area (int homeCellRow, int homeCellCol, char allegiance) {
		this.boundary = new ArrayList<Path>();

		this.homeCellRow = homeCellRow;
		this.homeCellCol = homeCellCol;
		this.allegiance = allegiance;
	}

	public synchronized void addPath(Path path) {
		this.boundary.add(path);
	}



	public synchronized String toString() {
		String result = "";
		for(Path path : boundary) result += path.toString() + "; ";
		return result;
	} // toString method


	public synchronized void printPaths(int maxLength) {
		for(Path path : boundary) System.out.println("path: " + path.toAbbreviatedString(maxLength));
	}


	public synchronized void getLoops(boolean debug) {

		// work through its boundary set until that is empty, and the following contains one or more loop-paths:
		ArrayList<Path> loopSet = new ArrayList<Path>();

		// outermost while-loop, because we might have SOME loops, but do we have ALL loops?
		int outerloop = 0;
		while(boundary.size() > 0)
		{

			outerloop++;
			int innerloop = 0;
			do { // inner do-loop, because we can't trust that the arrows come to us in the right order to append
				 // so have to iterate indefinitely, trusting that eventually the first path will be a loop
				innerloop++;

				if(debug) {
					System.out.print("boundary zero: ");
					System.out.print(boundary.get(0).toString());
					System.out.println();
					System.out.println("rest of boundary: ");
					for(int b = 1; b < boundary.size(); b++) {
						System.out.print(boundary.get(b).toString());
					}
				}



				Path p0 = this.boundary.get(0);
				boolean connected = false;


				//if (this.allegiance == 'I') debug = true;
/*
*/
				//if( outerloop + innerloop > 44) debug = true;
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
					if(debug) System.out.print("Trying out path ");
					if(debug) System.out.print(p1.toString());
/*
					if(p1.toString().equals("(1,128)->(2,128)")) debug = true;
									if(debug) {
										Scanner sc = new Scanner(System.in);
										System.out.println("baddie found?");
										String input = sc.next();
										if(input.equalsIgnoreCase("N")) return;
					}
*/
					String result = p0.attemptJoin(debug, allowLeftTurns, p1);

					if(result.equals("OTHER JOIN")) {
						boundary.remove(i);
						if(debug) System.out.println("Path " + i + " was successfully joined on.");
						connected = true;
						break;
					}

					if(result.equals("LEFT JOIN")) {
						lefties.add(p1);
						if(debug) System.out.println("trying to remove item " + i + " of " + boundary.size());

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
				for(int k = lefties.size() - 1; k >= 0; k--) {
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
	System.out.println("it has " + this.boundary.size() + " bounding loops");

	} // getLoops method



	public synchronized void processLoops(boolean debug) {
		this.sideCount = 0;
		if(debug) System.out.println("Boundary includes " + boundary.size() + " paths.");
		// trusting that the boundary is already a small number of loops at this point
		for(Path path : boundary) {
			this.sideCount += path.countCorners2();
			if(debug) System.out.print("Counted " + path.countCorners2() + " corners in path ");
			if(debug) System.out.print(path.toString());
			//System.out.println();


		}


	} // processLoops method



} // area class
