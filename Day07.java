
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day07 {



	private static boolean part1eval(boolean debug, ArrayList<Long> nums, int numPos, long subtotal, long target) {

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

		long nextNum = nums.get(numPos);
		long plusTotal = subtotal + nextNum;
		long multTotal = subtotal * nextNum;

		if(debug) {
			System.out.print("Now evaluating " + nums.toString() + " at position " + (numPos+1) + " of " + nums.size());
			System.out.print(" (" + nextNum + "); subtotal so far is " + subtotal);
			System.out.println(" so consider " + plusTotal + " and " + multTotal);
		}

		if(numPos + 1 == nums.size()) return (plusTotal == target || multTotal == target); // leaf node case

		// now recurse, but be as lazy as possible - the moment we encounter matching, RETURN that fact
		boolean match = false;
		if(plusTotal <= target) match = part1eval(debug, nums, numPos + 1, plusTotal, target);
		if(match) return true;
		if(multTotal <= target) match = part1eval(debug, nums, numPos + 1, multTotal, target);
		return match;

	} // evaluate



	private static boolean vectorEval(boolean debug, boolean isStart, boolean allowConcat, ArrayList<Long> nums, long subtotal, long target) {

		if(debug) System.out.println("With " + subtotal + " out of " + target + ", use " + nums.toString() + ";");

		// bottom out when the vector size reaches zero
		if(nums.size() == 0) return (subtotal == target);

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
			boolean match = false;

			if(plusTotal <= target) match = vectorEval(debug, false, allowConcat, tail, plusTotal, target);
			if(match) return true;

			// avoid starting with zero and multiplying that by the first number:
			if(!isStart) {
				if(multTotal <= target) match = vectorEval(debug, false, allowConcat, tail, multTotal, target);
				if(match) return true;
			}


		}	 // loop concatCount - the break clause takes us here
		return false;

	} // nonConcatEval

	/*
		Rework the above so that it passes shorter sequences when recursing, as opposed to a position-counter...
	*/

	//

	/*
		How about this. At each step of the process, we're faced with a whole new sequence. And we start by
		concatenating together next N values. We stop once we hit something that is too big when added or
		multiplied on to the subtotal. Then separately answer the various concatenation-level sequences.

		So. Start with a subtotal (should be fine to start with zero, and treat the first number just like any
		other) and a target. Break the N-member sequence that lies ahead into 1 and (N-1), 2 and (N-2), and so
		on, until that first concatenation-quantity already yields something that's past the target, which we
		then exclude.


	private static boolean part2eval(boolean debug, ArrayList<Long> nums, long subtotal, long target) {

		ArrayList<Long> nextHeads = new ArrayList<Long>();
		ArrayList<ArrayList<Long>> nextTails = new ArrayList<ArrayList<Long>>();

		for(int concatCount = 1; concatCount <= nums.size(); concatCount++) {

			long head = getHead(nums, concatCount);
			ArrayList<Long> tail = getTail(nums, concatCount);

			if isTooBig(subtotal, head, target) {// FIXME to write, carefully!
				break;
			} else {
				nextHeads.add(head);
				nextTails.add(tail);
			}

		} // for concatCount

		// OK. This leaves us with N new sequences to evaluate.
		// How about having a function that does NON-CONCAT evaluation? Which we kind of have already...


	} // part2eval

	*/
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
					boolean hit = vectorEval(false, true, false, sequence, 0, target);
					if(hit) total += target;

					//System.out.print("Continue?");
					//String input = sc.next();
					//if(input.equalsIgnoreCase("N")) break;

				}

				diskScanner.close();

				System.out.println("Result: " + total);

		} catch(Exception e) {
			System.out.println("problems reading file in"); //ldfixme covers algo blems too atm...
		} // catch

} // processFile


	public static void main(String[] args) {
/*
		ArrayList<Long> testAL = new ArrayList<Long>();
		testAL.add(5L);
		testAL.add(6L);
		testAL.add(7L);
		testAL.add(8L);

		System.out.println(getHead(testAL, 1));
		System.out.println(getTail(testAL, 1));

*/

		processFile();

	} // main




	/*

	public static void main(String[] args) {

		String path = "Y:\\code\\java\\AdventOfCode\\Day07input.dat";

		try {
			GuardMapState map = new GuardMapState(false, path);

			for(int iteration = 0; iteration < 100000; iteration++) {
				boolean x = map.evolve(true);

				if(!x) break;
			}

			int groundBreakings = map.getGroundBreakings();
			System.out.println("X-count: " + groundBreakings);

			ArrayList<Integer> candidateRows = map.getPotentialBlockerRows();
			ArrayList<Integer> candidateColumns = map.getPotentialBlockerColumns();




			// Scanner sc = new Scanner(System.in);

			int loopCount = 0;

			// we can trust the ground-breakings number to be one less than the array size
			for(int blockerPosition = 0; blockerPosition < groundBreakings - 1; blockerPosition++) {
				int blockedRow = candidateRows.get(blockerPosition);
				int blockedColumn = candidateColumns.get(blockerPosition);
				//System.out.print("Result of placing a blocker at (" + blockedRow + ", " + blockedColumn + "): ");

				GuardMapState blockedMap = new GuardMapState(false, path, blockedRow, blockedColumn);

				// guess change this and the other to while... and explain the true/false you're passing FIXME

				for(int iteration = 0; iteration < 100000; iteration++) {
					boolean x = blockedMap.evolve(false);
					if(!x) break;
				}

				if(blockedMap.getStatus().equals("LOOP")) loopCount++;

				// System.out.println(blockedMap.getStatus());


			} // for over possible blockers

			System.out.println("Blockers that resulted in loops: " + loopCount);


		} catch (IOException e) { // FIXME wrong location?
			System.out.println("Problems loading file");
		}

	} // main


*/


} // class
