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

		int score = 0;
		for(Area area : areaList) score += area.edgeScore();

		System.out.println("Areal summation: " + score);

		// FIXME ok so check that we have areas with all paths; then proceed PER TEB to tie those paths together...

	} // main method



} // Day12 class