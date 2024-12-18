import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Node {

	private int row;
	private int column;
	private Node parent;
	private ArrayList<Node> children;
	private int score;
	private LetterGrid parentGrid;
	private boolean isProcessed;

	// getters
	public int getRow() {return this.row;}
	public int getColumn() {return this.column;}
	public Node getParent() {return this.parent;}
	public ArrayList<Node> getChildren() {return this.children;}
	public int getScore() {return this.score;}
	public LetterGrid getParentGrid() {return this.parentGrid;}
	public boolean hasBeenProcessed() {return this.isProcessed;}

	// setters
	public void setProcessedState(boolean isProcessed) {this.isProcessed = isProcessed;}

	// constructor
	public Node(int row, int column, Node parent, LetterGrid parentGrid) {
		this.row = row;
		this.column = column;
		this.parent = parent;
		this.children = new ArrayList<Node>();

		// FIXME score to do later
		// FIXME also need to do the turning/directionality stuff

		this.parentGrid = parentGrid;
		this.isProcessed = false;

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


	public Node spawnAt(int spawnRow, int spawnColumn) {

		Node newNode = new Node(spawnRow, spawnColumn, this, this.parentGrid);
		this.children.add(newNode);
		return newNode;

	} // spawnAt method


	public ArrayList<Node> findChildren(boolean debug) {

		ArrayList<Node> output = new ArrayList<Node>();

		for(int dy = -1; dy <= 1; dy++) {

			for(int dx = -1; dx <= 1; dx++) {

				if(Math.abs(dx) + Math.abs(dy) != 1) continue;

				int nextRow = this.row + dy;
				int nextColumn = this.column + dx;
				if(debug) System.out.print("Investigating" + nextRow + "," + nextColumn + ")...");

				if(parentGrid.getCell(nextRow, nextColumn) == '#') {
					// ignore walls
					System.out.println("that's a wall.");

				} else if(this.didComeVia(nextRow, nextColumn)) {
					// ignore ancestor nodes
					System.out.println("already visited.");

				} else {
					if(debug) System.out.println("About to spawn at (" + nextRow + "," + nextColumn + ")");
					Node newNode = this.spawnAt(nextRow, nextColumn);
					output.add(newNode);
				}

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



} // Node class
