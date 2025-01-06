
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BestScrabbleWord{



	public static int getScore(String input) {
		char[] breakdown = input.toCharArray();
		int output = 0;

		for (char letter : breakdown) {
			int letterScore = 0;

			switch (letter) {
				case 'Q': letterScore = 10;	break;
				case 'Z': letterScore = 10;	break;

				case 'J': letterScore = 8;	break;
				case 'X': letterScore = 8;	break;

				case 'K': letterScore = 5;	break;

				case 'F': letterScore = 4;	break;
				case 'H': letterScore = 4;	break;
				case 'V': letterScore = 4;	break;
				case 'W': letterScore = 4;	break;
				case 'Y': letterScore = 4;	break;

				case 'B': letterScore = 3;	break;
				case 'C': letterScore = 3;	break;
				case 'M': letterScore = 3;	break;
				case 'P': letterScore = 3;	break;

				case 'D': letterScore = 2;	break;
				case 'G': letterScore = 2;	break;

				case 'A': letterScore = 1;	break;
				case 'E': letterScore = 1;	break;
				case 'I': letterScore = 1;	break;
				case 'L': letterScore = 1;	break;
				case 'N': letterScore = 1;	break;
				case 'O': letterScore = 1;	break;
				case 'R': letterScore = 1;	break;
				case 'S': letterScore = 1;	break;
				case 'T': letterScore = 1;	break;
				case 'U': letterScore = 1;	break;

			}

			output += letterScore;
		}

		return output;
	} // getScore method



	public static void main(String args[]) throws IOException {

		ArrayList<String> inputLines = new ArrayList<>();
		Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\ScrabbleInput.dat"));

		while (diskScanner.hasNext()) {
			inputLines.add(diskScanner.nextLine().trim().toUpperCase());
		}

		diskScanner.close();

		int minScore = Integer.MAX_VALUE;
		int maxScore = Integer.MIN_VALUE;
		String minWord = "";
		String maxWord = "";

		for (String inputLine : inputLines) {

			int lineScore = getScore(inputLine);

			if(lineScore < minScore) {
				minScore = lineScore;
				minWord = inputLine;
			}

			if(lineScore > maxScore) {
				maxScore = lineScore;
				maxWord = inputLine;
			}

		}

		System.out.println("MAX: " + maxWord + " (" + maxScore + ")");
		System.out.println("MIN: " + minWord + " (" + minScore + ")");

	}

}