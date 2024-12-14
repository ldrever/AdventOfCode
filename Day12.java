import java.util.*;
import java.io.*;

public class Day12 {



	public static void main(String[] chars) {
		boolean debug = false;
		String path = "Y:\\code\\java\\AdventOfCode\\Day12small.dat";
		LetterGrid lg;
		ArrayList<Area> areaList = new ArrayList<Area>();
		try {
			// lg = new LetterGrid(debug, new LetterGrid(path), '-'); //surround-by-ocean approach
			lg = new LetterGrid(path);
		} catch (Exception e) {
			System.out.println("Error processing path into LetterGrid");
			return;
		}
		lg.floodFill(debug, areaList);

		int part1Score = 0;
		int part2Score = 0;

		for(Area area : areaList) {
			System.out.print("area " + area.getAllegiance() + " including " + area.homeString());
			//area.displayPaths();
			//System.out.println();

			area.getLoops(debug);
			//area.processLoops(debug);

			//System.out.print("after looping - area : " + area.getAllegiance() + " paths: ");
			//area.displayPaths();
			//System.out.println();
			part1Score += area.edgeScore();
			part2Score += area.sideScore(debug);




			System.out.println("Areal summation: " + part1Score + " for part 1; " + part2Score + " for part 2");

		}
		System.out.println("Final summation: " + part1Score + " for part 1; " + part2Score + " for part 2");
		// FIXME ok so check that we have areas with all paths; then proceed PER TEB to tie those paths together...

	} // main method



} // Day12 class