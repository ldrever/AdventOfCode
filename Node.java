import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Node {

	private int row;
	private int column;
	private Node parent;
	private ArrayList<Node> children;
	private LetterGrid parentGrid;
	private boolean isProcessed;
	private int arrivalDy;
	private int arrivalDx;
	private int turnCount;
	private int stepCount;

	// getters
	public int getRow() {return this.row;}
	public int getColumn() {return this.column;}
	public Node getParent() {return this.parent;}
	public ArrayList<Node> getChildren() {return this.children;}
	public LetterGrid getParentGrid() {return this.parentGrid;}
	public boolean hasBeenProcessed() {return this.isProcessed;}
	public int getTurnCount() {return this.turnCount;}
	public int getStepCount() {return this.stepCount;}

	public int getScore(int turnScore) {return this.stepCount + turnScore * this.turnCount;}

	// setters
	public void setProcessedState(boolean isProcessed) {this.isProcessed = isProcessed;}
	public void setTurnCount(int turnCount) {this.turnCount = turnCount;}
	public void setStepCount(int stepCount) {this.stepCount = stepCount;}

	// constructor
	public Node(int row, int column, Node parent, LetterGrid parentGrid, int arrivalDy, int arrivalDx) {
		this.row = row;
		this.column = column;
		this.parent = parent;
		this.children = new ArrayList<Node>();

		// FIXME score to do later
		// FIXME also need to do the turning/directionality stuff

		this.parentGrid = parentGrid;
		this.isProcessed = false;
		this.arrivalDy = arrivalDy;
		this.arrivalDx = arrivalDx;

	} // constructor

	// methods
	public boolean isEndCell() {
		return (this.row == parentGrid.getEndRow()
			&& this.column == parentGrid.getEndCol());

	}

	public String getCoords() {

		return "(" + this.row + "," + this.column + ")";

	}

	public String traceRoute() {
		String result = "";

		Node node = this;

		while (node != null) {

			result = node.getCoords() + result;

			node = node.getParent();
		}

		return result;

	} // traceRoute method

	public boolean didComeVia(int viaRow, int viaColumn) {

		boolean result = false;
		Node node = this;

		while (node != null) {

			if(node.getRow() == viaRow && node.getColumn() == viaColumn) {
				result = true;
				break;
			}

			node = node.getParent();
		}

		return result;
	} // didComeVia method




	public ArrayList<Node> getAncestors() { // NB also includes the current node as an ancestor of itself

		ArrayList<Node> results = new ArrayList<Node>();
		Node node = this;

		while(node != null) {
			results.add(node);
			node = node.getParent();
		} // ancestor-iteration loop

		return results;
	} // getAncestors method




	public Node spawnAt(int spawnRow, int spawnColumn, int arrivalDy, int arrivalDx) {

		Node newNode = new Node(spawnRow, spawnColumn, this, this.parentGrid, arrivalDy, arrivalDx);
		this.children.add(newNode);
		return newNode;

	} // spawnAt method



	public ArrayList<Node> findChildren(boolean debug) { // default no-go-zone is its OWN ANCESTORS
		ArrayList<Node> noGoZone = this.getAncestors();
		return this.findChildren(debug, noGoZone);
	}



	public ArrayList<Node> findChildren(boolean debug, ArrayList<Node> noGoZone) {
		ArrayList<Node> output = new ArrayList<Node>();

		for(int dy = -1; dy <= 1; dy++) {

			nextNeighbour:
			for(int dx = -1; dx <= 1; dx++) {

				if(Math.abs(dx) + Math.abs(dy) != 1) continue;

				int nextRow = this.row + dy;
				int nextColumn = this.column + dx;
				if(debug) System.out.print("Investigating (" + nextRow + "," + nextColumn + ")...");

				// ignore the next neighbour if it's a wall:
				if(parentGrid.getCell(nextRow, nextColumn) == '#') {
					if(debug) System.out.println("that's a wall.");
					continue nextNeighbour;
				}

				// ignore the next neighbour if it's in the no-go-zone:
				for(Node ancestor : noGoZone) {
					if(ancestor != null) {
						int ancRow = ancestor.getRow();
						int ancColumn = ancestor.getColumn();

						if(nextRow == ancRow && nextColumn == ancColumn) {
							continue nextNeighbour;
						}
					}
				}

				// every other neighbouring cell is fair game:
				if(debug) System.out.println("About to spawn at (" + nextRow + "," + nextColumn + ")");
				Node newNode = this.spawnAt(nextRow, nextColumn, dy, dx);

				int turnsSoFar = this.getTurnCount();
				boolean corner = (this.arrivalDy != dy || this.arrivalDx != dx);

				newNode.setTurnCount(corner ? turnsSoFar + 1 : turnsSoFar);
				newNode.setStepCount(this.getStepCount() + 1);
				output.add(newNode);
			}

		}
		if(debug) System.out.print("Newly discovered children are:");
		if(debug) for(Node child : output) System.out.print(child.getCoords());
		if(debug) System.out.println();

		return output;

	} // findChildren method



	public void removeChild(Node child) {

		this.children.remove(child);

	} // removeChild method



	public boolean isChildless() {
		return this.children.size() == 0;
	} // isChildless method



	public static boolean extend(ArrayList<Node> nodes, ArrayList<Integer> scores, boolean debug, int turnScore) {

		ArrayList<Node> reachableSet = new ArrayList<Node>();
		return extend(nodes, scores, debug, reachableSet, null, turnScore, null);

	} // version without reachableSet or target score

	public static boolean extend(
		  ArrayList<Node> nodes
		, ArrayList<Integer> scores
		, boolean debug
		, ArrayList<Node> reachableSet
		, Integer targetScore
		, int turnScore
		, ArrayList<Node> noGoZone
		) {
		/*
			Repeatedly applying this to a list of nodes will implement
			depth-first-search (DFS).

			- It starts with the most recently added node on the list.

			- If it's the end-cell, then capture the score for the trip.
			  (This does NOT typically end the process - it tries every
			  possible way of walking the maze, and the end-cell can
			  come up at the end of many different routes.)
			- Also if it's the end-cell, then PRUNE THE BRANCH - i.e.,
			  keep removing the nodes that led us to the end, UNTIL
			  one is found that allows for a different path forwards.

			- (The same applies if the target score is reached.)

			- If it's a typical non-end-cell, we have this concept of "is
			  it processed?". This just means "have we found its children
			  and added THOSE to the list as well?". So a cell gets marked
			  as "processed" once its children are added to the list -
			  themselves in the "unprocessed" state.

			Note that the same cell can appear multiple times in the DFS -
			just never as an ancestor to itself, i.e. more than once in the
			tracing of the route.

			Note that this very much modifies the arrays passed to it.
			(Yes it returns a boolean, but that is only to inform you
			as to whether the entire DFS has completed.)



		*/
		boolean result = false;

		for(int i = nodes.size() - 1; i >= 0; i--) {

			Node node = nodes.get(i);

			if(node.hasBeenProcessed()) {

			} else {

				boolean endState = node.isEndCell();
				int score = node.getScore(turnScore);

				if(targetScore != null)	endState = endState || (score >= targetScore);


				if(endState) {
					reachableSet.add(node);
					if(debug) System.out.println(node.getCoords() + " added to reachable set.");
					if(debug) System.out.print("Tracing... ");
					if(debug) System.out.println(node.traceRoute());
					if(debug) System.out.println(node.getTurnCount() + " turns needed.");

					if(debug) System.out.println(node.getStepCount() + " steps needed.");

					if(debug) System.out.println("Scoring " + score);
					scores.add(score);

					if(debug) System.out.println();

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
					// children (the unprocessed state)
					ArrayList<Node> children;
					if(noGoZone == null)
						children = node.findChildren(debug);
					else
						children = node.findChildren(debug, noGoZone);


					for(Node child : children) nodes.add(child);
					node.setProcessedState(true);
					result = true;
					break;
				//}
			}


		} // i for-loop
		return result;

	} // extend method





} // Node class
