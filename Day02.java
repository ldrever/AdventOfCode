
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Day02 {

	public static boolean isSafe(ArrayList<Integer> levels) {
		if(levels.size() < 2) {
			return false;
		}

		int minDiff = Integer.MAX_VALUE;
		int maxDiff = Integer.MIN_VALUE;

		for(int i = 1; i < levels.size(); i++) {
			int diff = levels.get(i) - levels.get(i-1);

			if(diff < minDiff) {
				minDiff = diff;
			}

			if(diff > maxDiff) {
				maxDiff = diff;
			}

		}

		if (Math.signum(minDiff) != Math.signum(maxDiff)) {
			return false;
		}

		if(maxDiff > 3 || minDiff < -3 || maxDiff == 0) {
			return false;
		}

		return true;
	}


	public static void main(String args[]) throws IOException {

		ArrayList<String> inputLines = new ArrayList<>();
		Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\day02input.dat"));

		while (diskScanner.hasNext()) {
			inputLines.add(diskScanner.nextLine());
		}

		diskScanner.close();

		int safeCount = 0;

		for (String inputLine : inputLines) {

			String[] levelStrings = inputLine.split(" ");
			ArrayList<Integer> levels = new ArrayList<Integer>();

			for(String singleString : levelStrings) {
				levels.add(Integer.parseInt(singleString));
			}

			boolean isSafe = isSafe(levels);

			int i = 0;
			while ((!isSafe) && i < levels.size()) {
				int value = levels.get(i);
				levels.remove(i);
				isSafe = isSafe(levels);
				levels.add(i, value);
				i++;
			}

			safeCount += isSafe ? 1 : 0;

		}

		System.out.println(safeCount + " safe ones detected.");

	}

}