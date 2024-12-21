
import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 {

	static int substringCounter(String needle, String haystack) {

		Pattern pattern = Pattern.compile(needle);
		Matcher matcher = pattern.matcher(haystack);

		int position = 0;
		int count = 0;

		while(matcher.find(position)) {
			count++;
			position = matcher.end();
		}

		return count;

	}


	public static long answer(int part, boolean debug) throws Exception {
//	public static void main(String args[]) throws IOException {
		// System.out.println(part1());
		if(part == 1)
			return (long) part1();
		else
			return (long) part2();
	}


	public static int part1() throws IOException {

		LetterGrid lg = new LetterGrid("Y:\\code\\java\\AdventOfCode\\Day04input.dat");

		int height = lg.getHeight();
		int width = lg.getWidth();
		char[][] array = lg.getGrid();

		//System.out.println("Height: " + height + "; Width: " + width);

		// NOW ASSUME A SQUARE GRID
		final int noMarginLimit = height - 1;
		final int seqLength = 4;
		final int margin = seqLength - 1;
		final int marginLimit = noMarginLimit - margin;

		// set up directions

		ArrayList<MatrixDirection> directions = new ArrayList<MatrixDirection>();

		directions.add(new MatrixDirection("east",0,noMarginLimit,0,marginLimit,0,1));
		directions.add(new MatrixDirection("south",0,marginLimit,0,noMarginLimit,1,0));
		directions.add(new MatrixDirection("southeast",0,marginLimit,0,marginLimit,1,1));
		directions.add(new MatrixDirection("northeast",margin,noMarginLimit,0,marginLimit,-1,1));

		int count = 0;

		for(MatrixDirection direction : directions) {

			// System.out.println("now working " + direction.name);

			for(int y = direction.top; y <= direction.bot; y++) {
				for(int x = direction.left; x <= direction.right; x++) {

					char[] subArray = new char[seqLength];

					for(int i = 0; i < seqLength; i++) {
						subArray[i] = array[y + i * direction.yIncrement][x + i * direction.xIncrement];

					}
					String text = String.valueOf(subArray);
					if(text.equalsIgnoreCase("XMAS") || text.equalsIgnoreCase("SAMX")) count++;

				} // x
			} // y
		} // direction

		return count;

	} // method


		public static int part2() throws IOException {

			LetterGrid lg = new LetterGrid("Y:\\code\\java\\AdventOfCode\\Day04input.dat");

			int height = lg.getHeight();
			int width = lg.getWidth();
			char[][] array = lg.getGrid();

			// NOW ASSUME A SQUARE GRID
			final int noMarginLimit = height - 1;
			final int seqLength = 3;
			final int margin = seqLength - 1;
			final int marginLimit = noMarginLimit - margin;

			// set up directions

			ArrayList<MatrixDirection> directions = new ArrayList<MatrixDirection>();

			directions.add(new MatrixDirection("east",0,noMarginLimit,0,marginLimit,0,1));
			directions.add(new MatrixDirection("south",0,marginLimit,0,noMarginLimit,1,0));
			directions.add(new MatrixDirection("southeast",0,marginLimit,0,marginLimit,1,1));
			directions.add(new MatrixDirection("northeast",margin,noMarginLimit,0,marginLimit,-1,1));

			int count = 0;
			ArrayList<WordLocation> discoveries = new ArrayList<WordLocation>();

			for(MatrixDirection direction : directions) {

				//System.out.println("now working " + direction.name);

				for(int y = direction.top; y <= direction.bot; y++) {
					for(int x = direction.left; x <= direction.right; x++) {

						char[] subArray = new char[seqLength];

						for(int i = 0; i < seqLength; i++) {
							subArray[i] = array[y + i * direction.yIncrement][x + i * direction.xIncrement];

						}
						String text = String.valueOf(subArray);
						if(text.equalsIgnoreCase("MAS") || text.equalsIgnoreCase("SAM")) {


							WordLocation wl = new WordLocation(x + direction.xIncrement, y + direction.yIncrement, direction);
							discoveries.add(wl);

							// System.out.println(text + " discovered running " + direction.name + " from position (" + y +", " + x + ")");

						}

					} // x
				} // y
			} // direction

			// System.out.println(discoveries.size());
			int results = 0;

			for(int i = 0; i < discoveries.size(); i++) {
				WordLocation w1 = discoveries.get(i);
				for(int j = i+1; j < discoveries.size(); j++) {
					WordLocation w2 = discoveries.get(j);
					MatrixDirection d1 = w1.getDirection();
					MatrixDirection d2 = w2.getDirection();

					//if(d1.xIncrement + d1.yIncrement + d2.xIncrement + d2.yIncrement == 2) { // clever way of checking we have two orthogonal directions
					//if(!d1.name.equalsIgnoreCase(d2.name)) {
					if(d1.name.equalsIgnoreCase("northeast") && d2.name.equalsIgnoreCase("southeast") || d1.name.equalsIgnoreCase("southeast") && d2.name.equalsIgnoreCase("northeast")) {
						if(w1.getXCentre() == w2.getXCentre() && w1.getYCentre() == w2.getYCentre()) {
							results++;
						} // location check


					} // direction check


				} // j
			} // i

			return (results);
	} // method

} // class