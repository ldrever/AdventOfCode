import java.util.*;
import java.io.*;

class Day16 {

		public static long answer(int part, boolean debug) throws Exception {
//	public static void main(String[] args) {

//		boolean debug = false;
		boolean isPart1 = true;

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
			boolean result = Node.extend(nodes, scores, debug);
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

	} // main method

} // Day16 class