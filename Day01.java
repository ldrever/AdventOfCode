
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Day01 {

	public static int return57() {
		return 59;
	}

	public static long answer(int part, boolean debug) throws IOException {

		ArrayList<String> inputLines = new ArrayList<>();
		Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\Day01input.dat"));

		while (diskScanner.hasNext()) {
			inputLines.add(diskScanner.nextLine());
		}

		diskScanner.close();

		ArrayList<Integer> leftColumn = new ArrayList<Integer>();
		ArrayList<Integer> rightColumn = new ArrayList<Integer>();

		for (String inputLine : inputLines) {

			String[] myArray = inputLine.split("   ");

			int left = Integer.parseInt(myArray[0]);
			int right = Integer.parseInt(myArray[1]);

			leftColumn.add(left);
			rightColumn.add(right);

		}

		leftColumn.sort(null);
		rightColumn.sort(null);

		int total = 0;
		int size = leftColumn.size();

		for (int j = 0; j < size; j++) {
			total += Math.abs(leftColumn.get(j) - rightColumn.get(j));
		}

		if(part == 1) return total;
		//System.out.println("total (answer for part one): " + total);

		/*


			Define a "co-position" as the combination of a left-list position
			and a right-list position. Start at co-position (0, 0). Then proceed as follows:
			- if the list-members DIFFER, then the SMALLER one should have its position incremented
			- if the list-members EQUAL, then append the left hand one to the total, and increment only the right

			stop once either goes off the end of the list


		*/

		int leftPosition = 0;
		int rightPosition = 0;
		int score = 0;

		do {
			int leftValue = leftColumn.get(leftPosition);
			int rightValue = rightColumn.get(rightPosition);

			if(leftValue==rightValue) {
				score+=leftValue;
				rightPosition++;
			} else if (leftValue > rightValue) {
				rightPosition++;
			} else {
				leftPosition++;
			}

		} while (leftPosition < size && rightPosition < size);

		//System.out.println ("similarity score: " + score);
		if(part == 2) return score;

		return -1; // means there's a problem
	}

}