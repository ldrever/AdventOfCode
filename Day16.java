import java.util.*;
import java.io.*;

class Day16 {

	public static void main(String[] args) {
		//ArrayList<Node> previousBoundary = new ArrayList<Node>();
		boolean debug = false;
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

		Boundary currentBoundary = new Boundary(debug, origin, turnScore);
		System.out.println(currentBoundary.toString());

		int turnCount = 0;
		//for(int i = 1; i < 8; i++) {
		do {
			turnCount++;

			currentBoundary = currentBoundary.getNextBoundary(debug, turnScore, (turnScore / 2) + turnCount * turnScore);

			if (currentBoundary.isEndCell()) {
				System.out.println("detected final score of " + currentBoundary.getThreshold());
				break;
			} else {
				System.out.println(currentBoundary.toString());
			}

		} while(turnCount < 1_000_000);


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