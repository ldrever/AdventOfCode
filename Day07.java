
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day07 {

	private static String freshEval(boolean debug, boolean isStart, boolean allowConcat, ArrayList<Long> sequence, long subtotal, long target) {

		/*
			Starting afresh from first principles. We are presented with a sequence, and
			we want to determine whether that sequence can take us from our subtotal to
			our target, via the operations of addition, multiplication, and concatenation.

			Return an empty string if it CANNOT do so; if it can, return a string that
			shows the exact calculation by which this can be done.

			Because operator precedence works strictly left to right, we can work this way:

			1) if the remaining sequence is empty, just evaluate whether subtotal == target:


		*/
		if(sequence.size() == 0) {

			if(subtotal == target)
				return ".";
			else
				return "";

		}

		/*
			2) if there IS a first item in the sequence, work out how it augments the subtotal,
			   in all three ways:

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
			3) recurse, being sure to not bother with further calculations as soon as one
			   successful path is found:

		*/

		String output = "";

		if(isStart) {

			/*
				4) Do however need to handle the first number differently - if we say that
				   the notional pre-existing subtotal prior to the first number is zero,
				   then really we should only consider addition, and then NOT show that
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
				if(output.length() > 0) return "+" + head + output;
			}

			if(multTotal <= target) {
				output = freshEval(debug, isStart, allowConcat, tail, multTotal, target);
				if(output.length() > 0) return "*" + head + output;
			}

			if(allowConcat) {
				if(concTotal <= target) {
					output = freshEval(debug, isStart, allowConcat, tail, concTotal, target);
					if(output.length() > 0) return "|" + head + output;
				}
			}

		}

		return "";

	} // freshEval






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



		// bottom out when the vector size reaches zero
		if(nums.size() == 0) {
			return subtotal == target ? "." : "";
		} else {
			if(debug) System.out.println("With " + subtotal + " out of " + target + ", use " + nums.toString() + ";");
		}


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
			if(temp.length() > 0) return (isStart ? "" : " + ") + nextNum + temp;

			// avoid starting with zero and multiplying that by the first number:
			if(!isStart) {
				if(multTotal <= target) temp = vectorEval(debug, false, allowConcat, tail, multTotal, target);
				if(temp.length() > 0) return " * " + nextNum + temp;
			}


		}	 // loop concatCount - the break clause takes us here
		return "";

	} // vectorEval



	private static String leftConcatEval(boolean debug, boolean isStart, boolean allowConcat, ArrayList<Long> nums, long subtotal, long target) {
		// evaluate the sequence "nums" on whether it's able to get us from the initial
		// subtotal to the desired target

		// return "" when it isn't able to, and the calculation-string when it is

		// bottom out when the remaining vector size reaches zero
		if(nums.size() == 0) {
			return subtotal == target ? "." : "";
		} else {
			if(debug) System.out.println("With " + subtotal + " out of " + target + ", use " + nums.toString() + ";");
		}

		// now probe how much of the remaining vector we can concatenate onto the subtotal
		int maxConcat = allowConcat ? nums.size() : 0;

		for(int concatCount = 0; concatCount <= maxConcat; concatCount++) {
			if(debug) System.out.println("concatenating " + concatCount + " values onto the subtotal");

			long newSubtotal = concatFromSequence(subtotal, nums, concatCount);
			if (newSubtotal > target) break; // the first time that concatenation sends us past the target, we know that even bigger ones will do too
			ArrayList<Long> notConcatenated = getTail(nums, concatCount);

			String temp = "";

			if(notConcatenated.size() == 0) {

				temp = leftConcatEval(debug, false, allowConcat, notConcatenated, newSubtotal, target);
				if(temp.length() > 0) return newSubtotal + temp; // LDFIXME can't be right

			}

				long nextNum = getHead(notConcatenated, 1);
				ArrayList<Long> tail = getTail(notConcatenated, 1);

				long plusTotal = newSubtotal + nextNum;
				long multTotal = newSubtotal * nextNum;


			if(debug) {
				if(isStart)
					System.out.println("so consider " + plusTotal);
				else
					System.out.println("so consider " + plusTotal + " and " + multTotal);
			}


			// recurse, as lazily as possible


			if(plusTotal <= target) temp = leftConcatEval(debug, false, allowConcat, tail, plusTotal, target);
			if(temp.length() > 0) return (isStart ? "" : " + ") + nextNum + temp;

			// avoid starting with zero and multiplying that by the first number:
			if(!isStart) {
				if(multTotal <= target) temp = leftConcatEval(debug, false, allowConcat, tail, multTotal, target);
				if(temp.length() > 0) return " * " + nextNum + temp;
			}

		} // for concatCount
		return "";

	} // leftConcatEval


	public static long concatFromSequence(long startingValue, ArrayList<Long> sequence, int headLength) {

		String str = "";
		str += startingValue;
		for(int i = 0; i < headLength; i++) str += sequence.get(i);
		return Long.parseLong(str);

	} // concatFromSequence


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


					boolean debug = false;
					boolean isStart = true;
					boolean allowConcat = true;
					boolean requestConf = false;

					System.out.print(target);
					System.out.println(" from "  + sequence.toString() + " = ");

					//String hit = leftConcatEval(debug, isStart, allowConcat, sequence, 0, target);

					String hit = freshEval(debug, isStart, allowConcat, sequence, 0, target);

					if(hit.length()>0) {
						total += target;
						System.out.println("ANSWER:          " + hit);
						//for(int blank = 0; blank < 5; blank++)System.out.println();
					} else {
						System.out.println("NOT POSSIBLE");
					}
					if (requestConf) {
						System.out.print("Continue?");
						String input = sc.next();
						if(input.equalsIgnoreCase("N")) break;
					}

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
