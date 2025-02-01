import java.util.*;
import java.io.*;

public class NodeCollection {

	private ArrayList<Node> nodes;

	public ArrayList<Node> getNodes() {return this.nodes;}



	public NodeCollection(ArrayList<Node> nodes) {
		this.nodes = new ArrayList<Node>();
		for(Node node : nodes) this.nodes.add(node);

	}



	public void remove(Node n) {
		this.nodes.remove(n);
	}



	public void extend(NodeCollection other) {

		ArrayList<Node> otherNodes = other.getNodes();

		for(Node n : otherNodes) {
			this.nodes.add(n);
		}

	}



	public NodeCollection clone() {

		ArrayList<Node> nodes = this.getNodes();
		return new NodeCollection(nodes);

	}



	public NodeCollection getFinalOnes() {

		ArrayList<Node> allNodes = this.nodes;
		ArrayList<Node> output = new ArrayList<>();

		for(Node n : allNodes) {
			if(!n.getFertility()) {
				output.add(n);

				// SET THEM BACK TO DEFAULTLY FERTILE!
				n.setFertility(true);

				//System.out.println("added node " + n.getCoords());
			}
		}

		return new NodeCollection(output);
	}



	public NodeCollection getMatchingOnes(NodeCollection matches) {
		//System.out.println("Attempting to return the subset of " + this.toString());
		//System.out.println("that match members of " + matches.toString());

		ArrayList<Node> theseNodes = this.getNodes();
		ArrayList<Node> matchNodes = matches.getNodes();
		ArrayList<Node> output = new ArrayList<>();

		for(Node n1 : theseNodes) {
			int iRow = n1.getRow();
			int iColumn = n1.getColumn();

			for(Node n2 : matchNodes) {
				int jRow = n2.getRow();
				int jColumn = n2.getColumn();

				if(iRow == jRow && iColumn == jColumn) {
					output.add(n1);
					break;
				}

			}

		}

		return new NodeCollection(output);

	}



	public void winnowWith(NodeCollection other, int turnScore) {

		ArrayList<Node> theseNodes = this.getNodes();
		ArrayList<Node> otherNodes = other.getNodes();

		for(int i = 0; i < theseNodes.size(); i++) {
			Node n1 = theseNodes.get(i);
			int iRow = n1.getRow();
			int iColumn = n1.getColumn();
			int iDx = n1.getArrivalDx();
			int iDy = n1.getArrivalDy();

			for(int j = 0; j < otherNodes.size(); j++) {
				Node n2 = otherNodes.get(j);
				int jRow = n2.getRow();
				int jColumn = n2.getColumn();
				int jDx = n2.getArrivalDx();
				int jDy = n2.getArrivalDy();

				if(iRow == jRow && iColumn == jColumn & iDx == jDx && iDy == jDy) {
					//if(debug) System.out.println("dupe detuct at (" + iRow + ", " + iColumn + ")");

					if(n1.isCheaperThan(n2, turnScore)) {
						other.remove(n2);
						j--;

					} else {
						this.remove(n1);
						i--;
						break; // out of the j-loop
					}

				} // dupe-found if()

			} // j-loop

		} // i-loop

	}



	public void winnow(boolean debug, int turnScore) {

		// dedupe, keeping only the cheapest ways of reaching a given square
		for(int i = 0; i < this.nodes.size() - 1; i++) {

			Node n1 = this.nodes.get(i);
			int iRow = n1.getRow();
			int iColumn = n1.getColumn();
			int iDx = n1.getArrivalDx();
			int iDy = n1.getArrivalDy();

			for(int j = i + 1; j < this.nodes.size(); j++) {

				Node n2 = this.nodes.get(j);
				int jRow = n2.getRow();
				int jColumn = n2.getColumn();
				int jDx = n2.getArrivalDx();
				int jDy = n2.getArrivalDy();

				if(iRow == jRow && iColumn == jColumn & iDx == jDx && iDy == jDy) {
					if(debug) System.out.println("dupe detuct at (" + iRow + ", " + iColumn + ")");

					if(n1.isCheaperThan(n2, turnScore)) {
						this.nodes.remove(n2);
						j--;

					} else {
						this.nodes.remove(n1);
						i--;
						break; // out of the j-loop
					}

				} // dupe-found if()

			} // j-loop

		}// i-loop

	} // winnow method



	public boolean hasNodes() {

		return (this.nodes.size() > 0);

	}



	public void setFertility(boolean fertility) {
		ArrayList<Node> nodes = this.getNodes();
		for(Node n : nodes) n.setFertility(fertility);
	}



	public NodeCollection getParents() {

		ArrayList<Node> nodes = this.getNodes();
		ArrayList<Node> parents = new ArrayList<>();

		for(Node n : nodes) parents.add(n.getParent());

		return new NodeCollection(parents);
	}



	public int minScore(int turnScore) {
		int output = Integer.MAX_VALUE;

		for(Node n : this.getNodes()) {
			int nodeScore = n.getScore(turnScore);
			if(nodeScore < output) output = nodeScore;
		}

		return output;
	}



	public int maxScore(int turnScore) {
		int output = Integer.MIN_VALUE;

		for(Node n : this.getNodes()) {
			int nodeScore = n.getScore(turnScore);
			if(nodeScore > output) output = nodeScore;
		}

		return output;
	}



	public NodeCollection getGoodNeighbours(
	  boolean debug
	, int turnScore
	, int threshold
	, NodeCollection badNeighbours
	, boolean allowAncestorsAsChildren
	) {
		// find nodes that neighbour this collection, and which aren't PART of
		// this collection; nor are they part of a known set of bad neighbours

		ArrayList<Node> noGoZone = new ArrayList<>();
		for(Node n : this.getNodes()) noGoZone.add(n);
		for(Node n : badNeighbours.getNodes()) noGoZone.add(n);

		ArrayList<Node> goodNeighbours = new ArrayList<>();

		for(Node n : this.getNodes()) {

			if(n.getFertility()) {
				ArrayList<Node> nodeChildren = n.findChildren(debug, noGoZone, allowAncestorsAsChildren);

				for(Node child : nodeChildren) {

					// when a node's score hits the threshold, stop
					// spawning further nodes from it:
					if(child.getScore(turnScore) >= threshold) {
						child.setFertility(false);
						//System.out.print(child.getCoords() + " scores ");
						//System.out.print(child.getScore(turnScore));
						//System.out.println(" hence is marked infertile");
					}

					goodNeighbours.add(child);

				}

			} // fertility-if

		}

		NodeCollection output = new NodeCollection(goodNeighbours);
		return output;

	}



	public String toString() {
		String output = "";

		for (Node n : this.getNodes()) {

			output += n.getCoords() + " ";

		}

		return output;

	}



} // NodeCollection class