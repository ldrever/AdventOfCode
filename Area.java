import java.util.*;
import java.io.*;

public class Area {

	int homeCellRow;
	int homeCellCol;
	char allegiance;
	int blockCount;
	int edgeSegmentCount;
	int sideCount;

	ArrayList<Path> boundary;

	public void setBlocks(int blocks) {
		this.blockCount = blocks;
	}

	public void setEdgeSegments(int edgeSegments) {
		this.edgeSegmentCount = edgeSegments;
	}

	public void setSideCount(int sideCount) {
		this.sideCount = sideCount;
	}

	public int edgeScore() {
		return this.blockCount * this.edgeSegmentCount;
	}


	public Area (int homeCellRow, int homeCellCol, char allegiance) {
		this.boundary = new ArrayList<Path>();

		this.homeCellRow = homeCellRow;
		this.homeCellCol = homeCellCol;
		this.allegiance = allegiance;
	}

	public void addPath(Path path) {
		this.boundary.add(path);
	}

} // area class
