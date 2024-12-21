import java.util.*;
import java.io.*;

class Day09 {

	private static final int MAX_DIGIT = 9;
	private static final int BLANK = -1;
	private static final int MISSING = -1;



	private static long checksum(ArrayList<Integer> longForm) {

		long checksum = 0L;
		for(int position = 0; position < longForm.size(); position++) {
			int id = longForm.get(position);
			if(id != BLANK) checksum += (long) position * (long) id;
		}

		return checksum;

	} // checksum method



	private static int findRun(
		  boolean debug
		, ArrayList<Integer> arrayList
		, int startPosition
		, int targetValue
		, int runLength
	) {
		/*
			Returns the first index of arrayList that:
			- is at least startPosition
			- begins a run of entries all with the value targetValue...
			- ... which is at least runLength entries in length

			If no such index exists, then it returns MISSING. (e.g.
			- arrayList's size is zero
			- startPosition is larger than arrayList's last index
			- targetValue isn't present from startPosition onwards
			- such a runLength is never found
			)

		*/

		if(runLength <= 0) return MISSING;
		if(startPosition < 0) startPosition = 0;

		int size = arrayList.size();

		while(startPosition + runLength <= size) {

			int finishPosition = startPosition + runLength - 1;

			while(arrayList.get(finishPosition) == targetValue) {
				// start by jumping to what could be the END of the run, then
				// work back to the starting position
				if(startPosition == finishPosition) {
					if(debug) System.out.println(runLength + "-length run of " + targetValue + "s detected, starting at position " + startPosition);
					return startPosition;
				}

				finishPosition--;
			}

			// reaching here means we need to resume the search, one
			// position AFTER the failing position
			startPosition = finishPosition + 1;
		}

		return -1;

	} // findRun method



	public static void reviseGapStarts(
		  boolean debug
		, ArrayList<Integer> longForm
		, ArrayList<Integer> gapStarts
		, int startingPosition
		) {

		if(debug) System.out.println("Previous gap-list: " + gapStarts.toString());

		/*
			Recall that gapStarts.get(N) tells us the position within longForm
			where we FIRST encounter a "word" of N+ consecutive blanks. Notice
			that as N increases, the values returned can increase or stay the
			same, but can never decrease - you can't have a run of e.g. 3
			blanks without automatically including a run of 2 blanks starting
			at the same spot, for instance.

		*/

		// we COULD just always revise the gap record from 1 onwards, but why
		// not make use of the fact that if an N-length gap was already
		// detected at a location BEFORE any changes, then we can retain that
		// knowledge, and start the process only once we're at a size where the
		// previous first safe string might now have been modified:
		int firstLength = 1;
		for(; firstLength <= MAX_DIGIT; firstLength++) {

			int oldStart = gapStarts.get(firstLength);
			int oldFinish = oldStart + firstLength - 1;

			if(oldFinish >= startingPosition) break;

		} // firstLength for-loop


		// just as we can skip early lengths by trusting that they occur
		// too early on to be affected by some change, we can also skip
		// later lengths, on the grounds that if a start point didn't
		// exist for them before, then it won't do now either

		int lastLength = MAX_DIGIT;
		for(; lastLength >= firstLength; lastLength--) {

			if(gapStarts.get(lastLength) != MISSING) break;

		} // lastLength for-loop


		for(int len = firstLength; len <= lastLength; len++) {

			int foundAt = findRun(debug, longForm, startingPosition, BLANK, len);
			if(debug) System.out.println("FOUND " + len + " blanks starting at " + foundAt);

			if(foundAt == MISSING) { // will apply to all longer lengths too

				for(int badLen = len; badLen <= lastLength; badLen++) {
					gapStarts.set(badLen, MISSING);
				}
				break;
			}

			// reaching here means we found a gap-word of this length
			gapStarts.set(len, foundAt); // update the list
			startingPosition = foundAt; // new minimum for the next length up

		} // len for-loop

		if(debug) System.out.println("Revised gap-list: " + gapStarts.toString());

	} // reviseGapStarts method



	public static void swap(
		  boolean debug
		, int wordStart
		, int wordLength
		, int wordContent
		, ArrayList<Integer> longForm
		, ArrayList<Integer> gapStarts
		) {
			// System.out.println("Pre-swap: " + longForm.toString());
			if (debug) System.out.println("Swapping a run of " + wordLength + " " + wordContent + "s");
			/// only move words of data, not of blanks
			if(wordContent == BLANK) return;
			// we know the word's length, so the first step is to refer to the
			// list and identify a place for it
			int newHome = gapStarts.get(wordLength);

			// if (debug) System.out.println("New home will be at " + newHome);

			// situations where no swap should happen
			if(newHome == MISSING) return;
			if(newHome >= wordStart) return;

			// do the move
			for(int letter = 0; letter < wordLength; letter++) {
				longForm.set(newHome + letter, wordContent);
				longForm.set(wordStart + letter, BLANK);
			}

			// System.out.println("Successful swap; now revising gap data");
			// because it will have invalidated cached gap data
			reviseGapStarts(debug, longForm, gapStarts, newHome);

			if (debug) System.out.println("Post-swap: " + longForm.toString());
			if (debug) System.out.println("Post-swap gaps: " + gapStarts.toString());
		} // swap method




	private static void evolve2(boolean debug, ArrayList<Integer> longForm) {

		int nextIDToSwap = longForm.get(longForm.size()  - 1);


		if (debug) System.out.println("at start: " + longForm.toString());

		// we'll maintain an ArrayList whose Nth entry gives the start
		// position of the first contiguous group of N blanks.

		// initialize it with impossible values
		ArrayList<Integer> gapStarts = new ArrayList<Integer>();
		for(int wordLength = 0; wordLength <= MAX_DIGIT; wordLength++) {
			gapStarts.add(longForm.size());
		}

		// now update it from real data
		reviseGapStarts(debug, longForm, gapStarts, 0);

		if(debug) System.out.println("Gaps detected at: " + gapStarts.toString());

		// now for the real work
		int oldVal = BLANK;
		int wordLength = 0;

		for(int pos = longForm.size() - 1; pos >= 0; pos--) {
			int val = longForm.get(pos);

			if(val != oldVal) {
				if (debug) System.out.println("Position " + pos + " holds " + val + " while " + (pos + 1) + " starts a " + wordLength + " run of " + oldVal + "s");
				//if (debug) System.out.println("Current : " + longForm.toString());
				if(oldVal == nextIDToSwap) {
					swap(debug, pos + 1, wordLength, oldVal, longForm, gapStarts);
					nextIDToSwap--;
				}

				// ah but what if something gets swapped to its own immediate left? val becomes out of date...
				val = longForm.get(pos);

				wordLength = 0;
			}

			wordLength++;
			oldVal = val;

		} // pos for-loop


	} // evolve2 method




	private static void evolve(boolean debug, ArrayList<Integer> longForm) {

		int leftSwappee = 0;
		int rightSwappee = longForm.size() - 1;

		int iterations = 0;

		do {
			// move inwards until the FIRST swapworthy pair is found:
			while(longForm.get(leftSwappee) != BLANK && leftSwappee < rightSwappee) leftSwappee++;
			while(longForm.get(rightSwappee) == BLANK && leftSwappee < rightSwappee) rightSwappee--;

			if (debug) System.out.println(leftSwappee + " vs " + rightSwappee + " out of " + longForm.size());

			if (debug) {
				Scanner sc = new Scanner(System.in);
				System.out.println("After " + iterations + " iterations, the state is: " + longForm.toString());
				System.out.println("Continue?");
				String input = sc.next();
				if(input.equalsIgnoreCase("N")) break;

			}

			// having reached here, either leftSwappee == rightSwappee, OR we have a swappable pair
			if(leftSwappee == rightSwappee) {
				if (debug) System.out.println("No further iterations possible.");
				break;
			}

			int movingValue = longForm.get(rightSwappee);
			longForm.remove(leftSwappee);
			longForm.add(leftSwappee, movingValue);
			if (debug) System.out.println("blank removed from position " + leftSwappee + " and replaced with " + movingValue);

			longForm.remove(rightSwappee);
			longForm.add(rightSwappee, BLANK);

			iterations++;

		} // main while loop
		while(true);

	} // evolve method


	/*
		Let's try ArrayList<Integer> as a data structure, where the integer
		stored at position zero will be the file ID in disk sector zero, and
		so on. We can use -1 to represent empty blocks. Call this the "long
		form" representation, as opposed to "short form", which is how the
		input serves it up. Note that short form is an ARRAY of DIGITS, but
		long form is an ARRAYLIST of INTEGERS, a typical value being 10,000.

	*/


	private static ArrayList<Integer> expand(char[] shortForm) {

		ArrayList<Integer> longForm = new ArrayList<Integer>();

		int id = 0;
		int write = 0;

		for(int index = 0; index < shortForm.length; index++) {

			if(index % 2 == 0) {
				write = id;
				id++;
			}
			else {
				write = BLANK;
			}

			byte duration = charToDigit(shortForm[index]);

			for(byte i = 0; i < duration; i++)longForm.add(write);

		} // shortForm loop

		return longForm;

	} // expand method



	private static byte charToDigit(char c) {

		return (byte) (c - 48);

	} // charToByte method



	private static char[] fileToArray(String path) {

		char[] c = null;

		try {
			File fi = new File(path);
			Scanner sc = new Scanner(fi);
			String str = sc.nextLine();

			c = str.toCharArray();

			sc.close();

		} catch (Exception e) {
			System.out.println("Error processing file.");
		}


		return c;

	} // fileToArray method



	public static long answer(int part, boolean debug) throws Exception {

//	public static void main(String[] args) {

		//boolean debug = false;
		String path = "Y:\\code\\java\\AdventOfCode\\Day09input.dat";
		//String path = "Y:\\code\\java\\AdventOfCode\\Day09LD.dat";
		char[] shortForm = fileToArray(path);
		if (debug) System.out.println("File successfully transformed into " + shortForm.length + "-element array");
		ArrayList<Integer> longForm = expand(shortForm);
		if (debug) System.out.println("Array successfully transformed into " + longForm.size() + "-element disk map");
		if(debug) {
			for(int i = 0; i < longForm.size(); i++) {
				System.out.print(longForm.get(i) == BLANK ? "-" : longForm.get(i));

			}
			System.out.println();
		}
		//evolve2(debug, longForm);
		//evolve(debug, longForm);

		//System.out.println("Checksum: " + checksum(longForm));

		if(part == 1)
			evolve(debug, longForm);
		else
			evolve2(debug, longForm);

		return checksum(longForm);

	} // main method


} // class Day09