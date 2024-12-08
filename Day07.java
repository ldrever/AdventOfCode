
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day07 {

	private static String freshEval(boolean debug, boolean isStart, boolean allowConcat, ArrayList<Long> sequence, long subtotal, long target) {

		/*
			We are presented with a sequence of numbers, and
			we want to determine whether that sequence can take us from our subtotal to
			our target, via the operations of addition, multiplication, and concatenation.

			Return an empty string if it CANNOT do so; if it can, return a string that
			shows the exact calculation by which this may be done.

			Key insight is that it recursively calls itself on shorter sequences, and because
			all operations are non-decreasing, it can "prune the tree" ie stop recursion
			when it encounters a partial path which is already in excess of the target

			Because operator precedence works strictly left to right, we can work this way:

			1) if the remaining sequence is empty, just evaluate whether subtotal == target,
			   thus bottoming out the recursion:

		*/
		if(sequence.size() == 0) {
			if(subtotal == target) return ".";
			else return "";
		}

		/*
			2) if there IS a first item in the sequence, work out how it augments the subtotal
			   so far, in all three ways:

		*/

		long head = getHead(sequence, 1);
		ArrayList<Long> tail = getTail(sequence, 1);

		long plusTotal = subtotal + head;
		long multTotal = subtotal * head;

		String str = "";
		str += subtotal;
		str += head;
		long concTotal = Long.parseLong(str);

		/*
			3) recurse, being sure to stop further calculations as soon as one
			   successful path is found - we're looking for ANY solution, not ALL solutions:

		*/

		String output = "";

		if(isStart) {

			/*
				4) We need to handle the very first number differently - if we say that
				   the notional subtotal prior to the first number is zero,
				   then we should only consider addition, and suppress showing that
				   first plus sign

			*/
			isStart = false;

			if(plusTotal <= target) {
				output = freshEval(debug, isStart, allowConcat, tail, plusTotal, target);
				if(output.length() > 0) return + head + output;
			}

		} else {

			isStart = false;

			if(plusTotal <= target) {
				output = freshEval(debug, isStart, allowConcat, tail, plusTotal, target);
				if(output.length() > 0) return " + " + head + output;
			}

			if(multTotal <= target) {
				output = freshEval(debug, isStart, allowConcat, tail, multTotal, target);
				if(output.length() > 0) return " * " + head + output;
			}

			if(allowConcat) {
				if(concTotal <= target) {
					output = freshEval(debug, isStart, allowConcat, tail, concTotal, target);
					if(output.length() > 0) return " | " + head + output;
				}
			}

		}

		return ""; // reached when the next value overshoots the target in all ways

	} // freshEval



	public static long getHead(ArrayList<Long> input, int headLength) {

		String str = "";

		for(int i = 0; i < headLength; i++) {
			str += input.get(i);
		}

		return Long.parseLong(str);


	} // getHead



	public static ArrayList<Long> getTail(ArrayList<Long> input, int headLength) {

		ArrayList<Long> output = new ArrayList<Long>();

		for(int i = headLength; i < input.size(); i++) {
			output.add(input.get(i));
		}
		//System.out.println("tail: " + output.toString());

		return output;

	} // getTail



	public static long answer(boolean debug, boolean allowConcat, boolean requestConf, ArrayList<Long> targets, ArrayList<ArrayList<Long>> sequences) {

		Scanner sc = new Scanner(System.in);
		long total = 0L;

		for(int i = 0; i < targets.size(); i++) {

			long target = targets.get(i);
			ArrayList<Long> sequence = sequences.get(i);

			boolean isStart = true;

			if (debug) System.out.print(target);
			if (debug) System.out.println(" from "  + sequence.toString() + " = ");

			String hit = freshEval(debug, isStart, allowConcat, sequence, 0, target);

			if(hit.length() > 0) {
				total += target;
				if (debug) System.out.println("ANSWER:" + hit);
			} else {
				if (debug) System.out.println("NOT POSSIBLE");
			}
			if (requestConf) {
				System.out.print("Continue?");
				String input = sc.next();
				if(input.equalsIgnoreCase("N")) break;
			}

		} // main loop
		sc.close();

		return total;

	} // answer



	public static void processFile(ArrayList<Long> targets, ArrayList<ArrayList<Long>> sequences) {

		try{
				Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\Day07input.dat"));

				while (diskScanner.hasNext()) {

					String line = diskScanner.nextLine();
					String[] colonSep = line.split(":");
					String[] spaceSep = colonSep[1].trim().split(" ");

					long target = Long.parseLong(colonSep[0]);
					targets.add(target);

					ArrayList<Long> sequence = new ArrayList<Long>();
					for(int i = 0; i < spaceSep.length; i++) sequence.add(Long.parseLong(spaceSep[i]));
					sequences.add(sequence);
				} // while

				diskScanner.close();

		} catch(Exception e) {
			System.out.println("problems reading file in");
		} // catch

	} // processFile



	public static void main(String[] args) {

		ArrayList<Long> targets = new ArrayList<Long>();
		ArrayList<ArrayList<Long>> sequences = new ArrayList<ArrayList<Long>>();

		boolean allowConcat;
		boolean debug = false;
		boolean requestConf = false;

		processFile(targets, sequences);

		// part 1
		allowConcat = false;
		System.out.print("Answer to part 1: ");
		System.out.println(answer(debug, allowConcat, requestConf, targets, sequences));

		// part 2
		System.out.print("Answer to part 2: ");
		allowConcat = true; // for part 2
		System.out.println(answer(debug, allowConcat, requestConf, targets, sequences));

	} // main


} // class
