import java.util.*;
import java.io.*;

public class Day12 {

	public synchronized static void main(String[] chars) {

		boolean debug = false;
		String filePath = "Y:\\code\\java\\AdventOfCode\\Day12input.dat";
		ArrayList<Area> areaList = new ArrayList<Area>();

		LetterGrid lg;
		try {
			lg = new LetterGrid(filePath);
		} catch (Exception e) {
			System.out.println("Error processing path into LetterGrid");
			return;
		}

		lg.floodFill(debug, areaList);

		int part1Score = 0;
		int part2Score = 0;

		for(Area area : areaList) {
			area.getLoops(debug);
			area.processLoops(debug);
			System.out.println("Area " + area.getAllegiance() + " including " + area.homeString() + ": " + area.getVitalStatistics(debug));
			area.printPaths(66);
			System.out.println();

			int part1Current = area.edgeScore();
			int part2Current = area.sideScore(debug);
			part1Score += part1Current;
			part2Score += part2Current;

		}
		System.out.println();
		System.out.println("Final summation: " + part1Score + " for part 1; " + part2Score + " for part 2");

	} // main method

} // Day12 class