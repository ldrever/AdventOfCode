import java.util.*;
import java.io.*;

class Day16 {

	public static void main(String[] args) throws Exception {
		//ArrayList<Node> previousBoundary = new ArrayList<Node>();
		boolean debug = true;
		boolean isPart1 = true;
		int turnScore = 1000;
		String filePath = debug ? "Y:\\code\\java\\AdventOfCode\\Day16small.dat" : "Y:\\code\\java\\AdventOfCode\\Day16input.dat";
		LetterGrid lg = null;
		try {
			lg = new LetterGrid(filePath);
		}
		catch (Exception e) {System.out.println("file processing error");}
		lg.findStart(debug);
		lg.findEnd(debug);
		int arrivalDy = 0;
		int arrivalDx = 1; // start facing East
		Node parent = null;
		Node origin = new Node(lg.getStartRow(), lg.getStartCol(), parent, lg, arrivalDy, arrivalDx);
		System.out.println("origin scores " + origin.getScore(turnScore));

		// small increments don't work with boundaries, so we need a three-boundary stack really:


		Boundary oldBoundary = new Boundary(debug, origin, turnScore);
		int oldThreshold = oldBoundary.getThreshold();

		Boundary baselineBoundary = oldBoundary;
		int baselineThreshold = oldThreshold;


		System.out.println(oldBoundary.toString());

		//int turnCount = 0;

		int upperBound = Integer.MAX_VALUE;
		int lowerBound = 0;


		/*
			Objective for this next part is going to be:

			-	Identify an upper bound for the cost of reaching the end-cell
				(because an earlier boundary could conceivably reach it before,
				and less cheaply than, a later boundary)

			-	Identify a lower bound for the cost of reaching the end-cell
				(simply by adding one to the last controlled boundary
				threshold - we know that THAT can't reach the end-cell).

			-	Identify the baseline boundary that we will use to track down
				the TRUE boundary - it must be sufficiently far below both
				the upper AND LOWER bounds.

		*/

		do {
			baselineThreshold = oldThreshold;
			baselineBoundary = oldBoundary;
			System.out.println(oldThreshold);
			int newThreshold = oldThreshold + 1001; // FIXME hardcode
			Boundary newBoundary = oldBoundary.getNextBoundary(debug, turnScore, newThreshold);

			if(newBoundary.getThreshold() == oldBoundary.getThreshold() ) {
				// means nothing could be found requiring THAT big a threshold
				System.out.println("No nodes findable costing " + newThreshold);
				upperBound = oldBoundary.getThreshold() + 1001;
				break;
			}

			if(newBoundary.hasEndCell(turnScore)) {
				// means the end-cell is findable by THAT boundary, but maybe by some lower ones too
				System.out.println("End-cell detected!");
				upperBound = newBoundary.getThreshold();
				break;
			}

			System.out.println("We know that the boundary with threshold " + newBoundary.getThreshold() + " does NOT include the end-cell");

			lowerBound = newBoundary.getThreshold() + 1;

			oldBoundary = newBoundary;
			oldThreshold = newThreshold;

		} while(true);

/*
			if (newBoundary.isEndCell()) {
			//if(newBoundary.hasEndCell(turnScore)) {
				System.out.println("STOP! We know that " + newBoundary.getThreshold() + " can reach THE END-CELL!!!!!");

				System.out.println("detected LOWER BOUND of " + oldThreshold); // oldThreshold being the last such whose boundary didn't have the end-cell
				upperBound = newBoundary.getThreshold();
				System.out.println("detected UPPER BOUND of " + upperBound);
				break;

			} else {
				System.out.println("We know that the boundary with threshold " + newBoundary.getThreshold() + " does NOT include the end-cell");

				baselineBoundary = oldBoundary;
				oldBoundary = newBoundary;

				baselineThreshold = oldThreshold;
				oldThreshold = newThreshold;

				//System.out.println(oldBoundary.toString());
			}

		} //while(turnCount < 1_000_000);
			while(true);
*/

	int winningScore = upperBound;

	//System.out.println("Key boundary is that at " + baselineBoundary.toString());

	for(int threshold = upperBound; threshold >= lowerBound; threshold--) {

		Boundary b = baselineBoundary.getNextBoundary(debug, turnScore, threshold);

		if(b.hasEndCell(turnScore)) {
			int newScore = b.getThreshold();
			if(newScore < winningScore) winningScore = newScore;
		}

	}

			System.out.println("how hopefully answer is " + winningScore);

	} // main method // FIXME - shouldn't exist!

		// Everything above has been for the purpose of implementing a depth-first
		// search. Let's now do things differently, where we have series of waves,
		// each one a little bit further out from the starting square.
		/*
			public ArrayList<Node> propagate(ArrayList<Node> noGoZone, int maxCost, int turnCost) {
				// starting wherever we are, enumerate every reachable node that
				// satisfies both:
				// A/ the FROM-START cost of reaching it doesn't exceed maxCost
				// B/ it's not one of the no-go-zone nodes





				ArrayList<Node> results = new ArrayList<Node>();



				return results;
			} // propagate method

		} // Node class
*/



		public static long answer(int part, boolean debug) throws Exception {
//	public static void main(String[] args) {

//		boolean debug = false;
		boolean isPart1 = true;
		int turnScore = 1000;

		String filePath = debug ? "Y:\\code\\java\\AdventOfCode\\Day16small.dat" : "Y:\\code\\java\\AdventOfCode\\Day16small.dat"; // FIXME second needs small -> input
		LetterGrid lg = null;

		try {
			lg = new LetterGrid(filePath);
		}
		catch (Exception e) {System.out.println("file processing error");}


		lg.findStart(debug);
		lg.findEnd(debug);

		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Integer> scores = new ArrayList<Integer>();

		int arrivalDy = 0;
		int arrivalDx = 1; // start facing East
		Node origin = new Node(lg.getStartRow(), lg.getStartCol(), null, lg, arrivalDy, arrivalDx);
		nodes.add(origin);

		int safetyCounter = 0;

		while(safetyCounter < 10_000_000) {
			safetyCounter++;
			boolean result = Node.extend(nodes, scores, debug, turnScore);
			if(!result) break;
		}

		int bestScore = Integer.MAX_VALUE;

		for(int score : scores) {
			if(score < bestScore) {
				bestScore = score;
			}
		}

		//System.out.println("BEST POSSIBLE SCORE HERE: " + bestScore);
		return bestScore;


		/*
		Scanner sc = new Scanner(System.in);

		for(int i = 0; i < ra.length; i++) {

			lg.evolve(ra[i], isPart1, debug);

		}
		sc.close();

		char box = isPart1 ? 'O' : '[';
		System.out.println(lg.sumGPS(box));
*/

	} // answer method

} // Day16 class