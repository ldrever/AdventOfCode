import java.util.*;
import java.io.*;

public class Day12 {



	public static void main(String[] chars) {
		boolean debug = false;
		String path = "Y:\\code\\java\\AdventOfCode\\Day12input.dat";
		LetterGrid lg;
		try {
			lg = new LetterGrid(path);
		} catch (Exception e) {
			System.out.println("Error processing path into LetterGrid");
			return;
		}
		lg.floodFill(debug, 32, 0);

	} // main method



} // Day12 class