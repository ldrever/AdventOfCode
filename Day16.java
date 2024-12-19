import java.util.*;
import java.io.*;

class Day16 {

	public static boolean extend(ArrayList<Node> nodes, boolean debug) {
		boolean result = false;

		for(int i = nodes.size() - 1; i >= 0; i--) {

			Node node = nodes.get(i);

			if(node.hasBeenProcessed()) {

			} else {
				if(node.isEndCell()) {
					System.out.println(node.traceRoute());
					System.out.println(node.getTurnCount() + " turns needed.");
					System.out.println();

					Node currentNode = node;
					while(currentNode.isChildless()) {
						if(debug) System.out.print(currentNode.getCoords() + " is childless...");
						nodes.remove(currentNode);
						if(debug) System.out.print("successfully removed...");
						Node parent = currentNode.getParent();
						parent.removeChild(currentNode);
						if(debug) System.out.println("along with the parent's link to it.");
						currentNode = parent;

					} // isChildless while-loop

					// now at a state where the current node ISN'T childless,
					// in spite of having had a child removed.


				}

				//} else {
					if(debug) System.out.println("Found that node " + node.getCoords() + " has not been processed. Attempting to process it now...");
					// now that we've found the newest unprocessed, here's what
					// we do - just add its children to the list, and update its
					// processed state

					// remember that it goes ON the list WITHOUT knowing its
					// children...
					ArrayList<Node> children = node.findChildren(debug);
					for(Node child : children) nodes.add(child);
					node.setProcessedState(true);
					result = true;
					break;
				//}
			}


		} // i for-loop
		return result;

	} // processLatest method


	public static void main(String[] args) {

		boolean debug = false;
		boolean isPart1 = true;

		String filePath = debug ? "Y:\\code\\java\\AdventOfCode\\Day16small.dat" : "Y:\\code\\java\\AdventOfCode\\Day16small.dat"; // FIXME second needs small -> input
		LetterGrid lg = null;

		try {
			lg = new LetterGrid(filePath);
		}
		catch (Exception e) {System.out.println("file processing error");}


		lg.findStart(true);
		lg.findEnd(true);

		ArrayList<Node> nodes = new ArrayList<Node>();
		int arrivalDy = 0;
		int arrivalDx = 1; // start facing East
		Node origin = new Node(lg.getStartRow(), lg.getStartCol(), null, lg, arrivalDy, arrivalDx);
		nodes.add(origin);

		int safetyCounter = 0;

		while(safetyCounter < 20000) {
			safetyCounter++;
			boolean result = extend(nodes, debug);
			if(!result) break;
		}



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