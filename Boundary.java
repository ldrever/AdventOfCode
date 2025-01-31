import java.util.*;
import java.io.*;

public class Boundary {

	/*
		The idea here is to capture an INSIDE ring of nodes as well
		as an OUTSIDE ring of nodes.

		Every node on the outside must be a child of one on the
		inside; every node on the outside must score AT LEAST some
		particular threshold, and every node on the inside must
		score LESS THAN it.

	*/

	private NodeCollection inside;
	private NodeCollection outside;
	private int threshold;
	private boolean isEndCell;

	public int getThreshold() {return this.threshold;}


	/*
		One straightforward means of construction will be to take a
		single node, let that be the inside, and all its children
		will then form the outside.
	*/

	public Boundary(boolean debug, Node origin, int turnScore) {

		ArrayList<Node> insideHolder = new ArrayList<>();
		insideHolder.add(origin);
		this.inside = new NodeCollection(insideHolder);

		// System.out.println("ORIGIN INSIDE!");

		ArrayList<Node> outsideHolder = origin.findChildren(debug, null);
		this.outside = new NodeCollection(outsideHolder);

		this.threshold = origin.getScore(turnScore) + 1;
		this.isEndCell = false;
	}


	// private constructor
	private Boundary(NodeCollection inside, NodeCollection outside, int threshold) {
		this.inside = inside;
		this.outside = outside;
		this.threshold = threshold;
		this.isEndCell = false;
	}



	// special constructor for the end-state
	private Boundary(int threshold) {
		this.inside = null;
		this.outside = null;
		this.threshold = threshold;
		this.isEndCell = true;

	}



	public boolean isEndCell() {

		return this.isEndCell;

	}





	public Boundary getNextBoundary(boolean debug, int turnScore, int nextThreshold) {

		NodeCollection latestLayer = this.outside.clone();
		NodeCollection history = this.inside.clone();

		do {
			// now in the standard state whereby:
			// history is populated, and assumed to be CHEAP-UNIQUE
			// latest is populated, also assumed CHEAP-UNIQUE, and DISJOINT FROM HISTORY

			// so we find the new layer...
			NodeCollection newLayer = latestLayer.getGoodNeighbours(debug, turnScore, nextThreshold, history, false);




			// winnow it down...
			newLayer.winnow(debug, turnScore);



			// winnow it WRT the history...
			newLayer.winnowWith(history, turnScore);



			// now restore those standard-state assumptions:
			history.extend(latestLayer);

			//System.out.println("de-duped history: " + history.toString());
			//System.out.println("de-duped new layer: " + newLayer.toString());

			latestLayer = newLayer;

			// cheap-uniqueness of the history relies upon each new layer being
			// self-winnowed, then winnowed with the history, plus the fact it
			// starts off as a single point, and then relies on this to keep it ok

		} while (latestLayer.hasNodes());

		// here we check whether we've hit the end cell - note that
		// we can't do this any sooner, since it's possible that a
		// later "layer" will reach it more cheaply than an earlier one

		int endScore = Integer.MAX_VALUE;

		for(Node n : history.getNodes()) {
			if(n.isEndCell()) {

				int newEndScore = n.getScore(turnScore);
				if(newEndScore < endScore) endScore = newEndScore;

			}
		}

		if(endScore < Integer.MAX_VALUE) return new Boundary(endScore);

		// can now end via return all childless ones...
		NodeCollection newOutside = history.getFinalOnes();
		//System.out.println("only these should be over the threshold of " + nextThreshold + ": " + newOutside.toString());

		NodeCollection hinterland = newOutside.getGoodNeighbours(debug, turnScore, Integer.MAX_VALUE, history, false);
		//System.out.println("hinterland: " + hinterland.toString());

		// blem here bieng that inside'll have higher scores than outside...


		// blem becuase findChildren won't pick up parents... couldn't we just GET their parents? or would some be missing FIXME
		NodeCollection reversedInside = newOutside.getGoodNeighbours(debug, turnScore, Integer.MAX_VALUE, hinterland, true);
		//System.out.println("reversed inside: " + reversedInside.toString());

		// ideally want to find everything in history that matches something in newInside...
		NodeCollection newInside = history.getMatchingOnes(reversedInside);

		return new Boundary(newInside, newOutside, nextThreshold);

	}



	public String toString() {
		String output = "inside the threshold of " + this.threshold + ": ";
		output += this.inside.toString() + "... ";
		output += "while OUTSIDE is: ";
		output += this.outside.toString();

		return output;

	}

} // Boundary class