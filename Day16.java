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

					Node currentNode = node;
					while(currentNode.isChildless()) {
						nodes.remove(currentNode);
						Node parent = currentNode.getParent();
						parent.removeChild(currentNode);
						currentNode = parent;

					} // isChildless while-loop

					nodes.remove(i);


				} else {
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
				}
			}


		} // i for-loop
		return result;

	} // processLatest method


	public static void main(String[] args) {

		boolean debug = true;
		boolean isPart1 = true;

		String filePath = debug ? "Y:\\code\\java\\AdventOfCode\\Day16small.dat" : "Y:\\code\\java\\AdventOfCode\\Day16input.dat";
		LetterGrid lg = null;

		try {
			lg = new LetterGrid(filePath);
		}
		catch (Exception e) {System.out.println("file processing error");}


		lg.findStart(debug);
		lg.findEnd(debug);

		ArrayList<Node> nodes = new ArrayList<Node>();
		Node origin = new Node(lg.getStartRow(), lg.getStartCol(), null, lg);
		nodes.add(origin);

		int safetyCounter = 0;

		while(safetyCounter < 200) {
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