
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day07 {

		/*
			We seek to answer the question, "given a sequence of numbers, can we reach a target number by
			putting either + or * between every number in the sequence?" Note that evaluation is always
			left to write, not per the usual precedence.

			The possibilities might seem to involve checking all 2^N possibilities, where there are N spaces
			between numbers. But observe that + and * can never make the subtotal any smaller, assuming that
			every number in the sequence is positive. So as soon as a subtotal goes beyond the target, we don't
			have to worry about evaluating that path any more.

			So the logic being applied here is as follows:
			- we are given a subtotal so far, and the next position (and hence number) in the sequence
			- start by evaluating both the summation and the multiplication that result from increasing the subtotal by the next number
			- IN THE CASE OF THE FINAL NUMBER (aka leaf node), we can return true if EITHER of those match the target
			- for a non-final number, start by returning false if BOTH addition and multiplication are in excess
			- otherwise, recursively return the result of looking at the NEXT position and number, for one or both increased
			  (but non-excessive) subtotals
			- note that as soon as a TRUE evaluation is encountered, then that's it, no need to calculate any further

		*/


	private static String vectorEval(boolean debug, boolean isStart, boolean allowConcat, ArrayList<Long> nums, long subtotal, long target) {
		// return "" when it doesn't work, and the calculation-string when it does

		if(debug) System.out.println("With " + subtotal + " out of " + target + ", use " + nums.toString() + ";");

		// bottom out when the vector size reaches zero
		if(nums.size() == 0) return subtotal == target ? "." : "";

		// now free to presume an available next number
		int maxConcat = allowConcat ? nums.size() : 1;
		for(int concatCount = 1; concatCount <= maxConcat; concatCount++) {

			long nextNum = getHead(nums, concatCount);

			// long nextNum = nums.get(0);

			ArrayList<Long> tail = getTail(nums, concatCount);

			if(concatCount > 1) {
				if(debug) System.out.print("USING CONCATENATION: " + nextNum + " vs " + tail.toString() + " ");
			}

			long plusTotal = subtotal + nextNum;
			long multTotal = subtotal * nextNum;


			//exit the loop and return false, since wider concatenations will be even further over the target
			if(isStart) {
					if(plusTotal > target) break;
			} else {
					if(plusTotal > target && multTotal > target) break; // wondering whether the mult check can be done away with, being always greater than plus..?
			}

			if(debug) {
				if(isStart)
					System.out.println("so consider " + plusTotal);
				else
					System.out.println("so consider " + plusTotal + " and " + multTotal);
			}


			// recurse, as lazily as possible
			String temp = "";

			if(plusTotal <= target) temp = vectorEval(debug, false, allowConcat, tail, plusTotal, target);
			if(temp.length() > 0) return " + " + nextNum + temp;

			// avoid starting with zero and multiplying that by the first number:
			if(!isStart) {
				if(multTotal <= target) temp = vectorEval(debug, false, allowConcat, tail, multTotal, target);
				if(temp.length() > 0) return " * " + nextNum + temp;
			}


		}	 // loop concatCount - the break clause takes us here
		return "";

	} // nonConcatEval


	public static long getHead(ArrayList<Long> input, int concatCount) {

		String str = "";

		for(int i = 0; i < concatCount; i++) {
			str += input.get(i);
		}

		return Long.parseLong(str);


	} // getHead



	public static ArrayList<Long> getTail(ArrayList<Long> input, int concatCount) {

		ArrayList<Long> output = new ArrayList<Long>();

		for(int i = concatCount; i < input.size(); i++) {
			output.add(input.get(i));
		}
		//System.out.println("tail: " + output.toString());

		return output;

	} // getTail



	public static void processFile() {

		try{
				//ArrayList<String> lines = new ArrayList<String>();
				//ArrayList<Long> targets = new ArrayList<Long>();
				//ArrayList<ArrayList<Long>> sequences = new ArrayList<ArrayList<Long>>();

				Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\Day07input.dat"));
				Scanner sc = new Scanner(System.in);
				Long total = 0L;

				while (diskScanner.hasNext()) {
					String line = diskScanner.nextLine();
					String[] colonSep = line.split(":");
					String[] spaceSep = colonSep[1].trim().split(" ");
					// System.out.println("values <" + spaceSep[0] + "> through <" + spaceSep[spaceSep.length - 1] + ">");

					ArrayList<Long> sequence = new ArrayList<Long>();
					for(int i = 0; i < spaceSep.length; i++) sequence.add(Long.parseLong(spaceSep[i]));
					//sequences.add(sequence);
					long target = Long.parseLong(colonSep[0]);
					//targets.add(target);
					//boolean hit = part1eval(false, sequence, 1, sequence.get(0), target);
					String hit = vectorEval(false, true, false, sequence, 0, target);

					if(hit.length()>0) {

					total += target;
					System.out.print(target + " = ");
					System.out.println(hit);
					}
/*
					System.out.print("Continue?");
					String input = sc.next();
					if(input.equalsIgnoreCase("N")) break;
*/
				}

				diskScanner.close();

				System.out.println("Result: " + total);

		} catch(Exception e) {
			System.out.println("problems reading file in"); //ldfixme covers algo blems too atm...
		} // catch

} // processFile


	public static void main(String[] args) {

		processFile();

	} // main


} // class
