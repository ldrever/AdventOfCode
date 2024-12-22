import java.util.*;
import java.io.*;

class Day15 {

	public static String getControls(String filePath) throws Exception {

		Scanner diskScanner = new Scanner(new File(filePath));
		String text = "";
		while (diskScanner.hasNext())
			text += diskScanner.nextLine();

		diskScanner.close();
		return text;
	}


		public static long answer(int part, boolean debug) throws Exception {
//	public static void main(String[] args) {
//		boolean debug = false;
		boolean isPart1 = (part == 1);
		String filePath = isPart1 ?  "Y:\\code\\java\\AdventOfCode\\Day15input.dat": "Y:\\code\\java\\AdventOfCode\\Day15large.dat";
		String controls = "";
		LetterGrid lg = null;

		try {
			controls = getControls("Y:\\code\\java\\AdventOfCode\\Day15control.dat");
			lg = new LetterGrid(filePath);
		}
		catch (Exception e) {System.out.println("file processing error");}

		lg.findRobot(debug);
		char[] ra = controls.toCharArray();

		//Scanner sc = new Scanner(System.in);

		for(int i = 0; i < ra.length; i++) {

			lg.evolve(ra[i], isPart1, debug);

		}
		//sc.close();

		char box = isPart1 ? 'O' : '[';
		return (lg.sumGPS(box));

	} // main method

} // Day15 class