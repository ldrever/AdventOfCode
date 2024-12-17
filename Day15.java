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


	public static void main(String[] args) {
		boolean isPart1 = true;
		String controls = "";
		LetterGrid lg = null;

		try {
			controls = getControls("Y:\\code\\java\\AdventOfCode\\Day15control.dat");
			lg = new LetterGrid("Y:\\code\\java\\AdventOfCode\\Day15input.dat");
		}
		catch (Exception e) {System.out.println("file processing error");}

		lg.findRobot(true);
		char[] ra = controls.toCharArray();

		for(int i = 0; i < ra.length; i++) {
			lg.evolve(ra[i]);
			//lg.displayArray();
		}

		char box = isPart1 ? 'O' : '[';
		System.out.println(lg.sumGPS(box));


	} // main method

} // Day13 class