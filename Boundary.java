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



	public boolean hasEndCell(int turnScore)
	{

		int endScore = Integer.MAX_VALUE;

		for(Node n : this.outside.getNodes()) {
			if(n.isEndCell()) {
				int newEndScore = n.getScore(turnScore);
				if(newEndScore < endScore) endScore = newEndScore;
			}
		}

		return (endScore < Integer.MAX_VALUE);

	}



	public int scoreUpperBound(int turnScore) {

		int endScore = Integer.MAX_VALUE;

		for(Node n : this.outside.getNodes()) {
			if(n.isEndCell()) {

				int newEndScore = n.getScore(turnScore);
				if(newEndScore < endScore) endScore = newEndScore;

			}
		}

		return endScore;

	}





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





	public Boundary getNextBoundary(boolean debug, int turnScore, int nextThreshold) throws Exception {


		/*

			Algorithm for finding a valid higher-threshold boundary, starting
			off from an old, valid, lower-threshold one:

			Initialization step
			===================
			*	Initialize HISTORY as the old boundary's
				inside and LATEST as its outside.

			Main loop
			=========
			*	Detect NEW as being all children of LATEST that are not
				members of HISTORY or LATEST.

			*	Ensure that NEW contains at most one way of entering a given
				cell from a given direction - this being the cheapest of all
				the possibilities.

			*	Ensure likewise that if there is a node that enters some cell
				in some direction in NEW, and there's another node doing the
				same in HISTORY, that only the cheapest of the two is kept.

			*	Extend the definition of HISTORY to include what had been
				LATEST, then loop back and re-detect NEW.








			1/	Identify a "layer" of child nodes of the lower-threshold
				boundary's outside nodes. Ensure that none belong to the
				lower boundary's outside or inside.

			2/	Dedupe them so that there is a maximum of one node per cell,
				and that the CHEAPEST such node is the one kept.


		*/
		this.outside.setFertility(true);
		this.inside.setFertility(true);


		NodeCollection latestLayer = this.outside.clone();
		NodeCollection history = this.inside.clone();

		do {
			// now in the standard state whereby:
			// history is populated, and assumed to be CHEAP-UNIQUE
			// latest is populated, also assumed CHEAP-UNIQUE, and DISJOINT FROM HISTORY

			// so we find the new layer...




			/*
				Here's how this works. Let's suppose we already have a valid
				boundary - i.e., outside nodes that CAN'T be reached unless a
				threshold is attained or exceeded, and inside nodes that are
				their parents, and which score LESS than that threshold.

				(Let's also stipulate that both these sets are non-empty.)

				Starting from such a boundary, let's say that "layer zero" is
				its outside. Then repeatedly do this:

				-	find all children of those layer N nodes that haven't
					already scored X; call these layer N+1

				Stop once an empty "layer" is found.

				Key things however:

				-	An assumption is made that the old boundary can have no
					nodes in common with the new one. In order to work with
					that, we need to insist on the next boundary being a
					turncost plus a stepcost higher than the previous one.

				-	Under what conditions might it be possible that NO such
					nodes can be found? Well, I think then, either return
					the same input boundary, or construct a valid boundary
					with the end-cell in its outside...


			*/
			NodeCollection newLayer = latestLayer.spawnFromSubThresholdNodes(debug, turnScore, nextThreshold, history);




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

/*
		int endScore = Integer.MAX_VALUE;

		for(Node n : history.getNodes()) {
			if(n.isEndCell()) {

				int newEndScore = n.getScore(turnScore);
				if(newEndScore < endScore) endScore = newEndScore;

			}
		}

		if(endScore < Integer.MAX_VALUE) {

			// find any node at the end-cell with the correct score



			return new Boundary(endScore);
		}

*/

		// can now end via return all childless ones...
		NodeCollection newOutside = history.getFinalOnes();


		ArrayList<Node> results = history.getFinalOnes().getNodes();

		if(results.size() == 0) {
			// so we failed to hit the threshold - send THIS signal:
			return this;

		} else {
			NodeCollection newInside = newOutside.getParents();
			return new Boundary(newInside, newOutside, nextThreshold);

		}



		//System.out.println("only these should be over the threshold of " + nextThreshold + ": " + newOutside.toString());

/*
		NodeCollection hinterland = newOutside.spawnFromSubThresholdNodes(debug, turnScore, Integer.MAX_VALUE, history, allowAncestorsAsChildren);
		//System.out.println("hinterland: " + hinterland.toString());

		// blem here bieng that inside'll have higher scores than outside...




		// blem becuase findChildren won't pick up parents... couldn't we just GET their parents? or would some be missing FIXME
		allowAncestorsAsChildren = true;
		NodeCollection reversedInside = newOutside.spawnFromSubThresholdNodes(debug, turnScore, Integer.MAX_VALUE, hinterland, allowAncestorsAsChildren);
		//System.out.println("reversed inside: " + reversedInside.toString());

		// ideally want to find everything in history that matches something in newInside...
		NodeCollection newInside = history.getMatchingOnes(reversedInside);

*/



		// reassuring bit of verification
		// System.out.print("RESULTS OF SCORE-CHECKS...");

		//if(newOutside.minScore(turnScore) < nextThreshold) throw new Exception("outside/inside threshold mismatch");
		//if(newInside.maxScore(turnScore) >= nextThreshold) throw new Exception("outside/inside threshold mismatch");



	}



	public String toString() {
		String output = "inside the threshold of " + this.threshold + ": ";
		output += this.inside.toString() + "... ";
		output += "while OUTSIDE is: ";
		output += this.outside.toString();

		return output;

	}

} // Boundary class