
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day05 {

	public static ArrayList<String> ruleStrings;
	public static ArrayList<String> sequenceStrings;



	public static void main(String[] args) {

		ArrayList<NumberSequence> goodies = new ArrayList<NumberSequence>();
		ArrayList<NumberSequence> baddies = new ArrayList<NumberSequence>();

		processFile();

		for (String sequenceString : sequenceStrings) goodies.add(new NumberSequence(sequenceString));
		for(String rule : ruleStrings) purify(rule, goodies, baddies);

		System.out.println("Answer to part 1: " + part1(goodies));
		System.out.println("Answer to part 2: " + part2(baddies));

	} // main



	public static int part2(ArrayList<NumberSequence> sequences) {

		/*
			To understand how this works, imagine a collection of boxes, each
			containing zero or more NumberSequences.

			The first box is the one that is passed in as a parameter. The next
			box is formed by running the "purify" function on the first box;
			this results in only CLEAN sequences being left there, and all others
			moved to that next box, while also being given the swapping treatment.

			keep running this process until there are
			no unclean sequences to put into a new box.

			Note that at every point, the population of sequences remains
			constant, although it's being gradually spread amongst multiple boxes.

		*/

		ArrayList<ArrayList<NumberSequence>> boxOfBoxes = new ArrayList<ArrayList<NumberSequence>>();
		ArrayList<NumberSequence> currentBox = sequences;
		int safetyCounter = 0;

		do {
			ArrayList<NumberSequence> nextBox = new ArrayList<NumberSequence>();

		// now look at every rule in turn, and for
		// sequenceStrings that do not comply, re-classify as good to bad:
			for(String rule : ruleStrings) {

				purify(rule, currentBox, nextBox);

				//System.out.println("Purification via rule " + rule + " has left " + currentBox.size() + " sequences in box " + safetyCounter + "; " );
				if (currentBox.size() == 0) break;
			} // rule loop
			boxOfBoxes.add(currentBox);
			// System.out.println(currentBox.size() + " sequences in box " + safetyCounter);
			currentBox = nextBox;
			safetyCounter++;

		} while (safetyCounter < 10000 && currentBox.size() > 0);

		int middleSum = 0;
		for(ArrayList<NumberSequence> box : boxOfBoxes) {
			//System.out.println("next box");
			for(NumberSequence sequence : box) {
			//	System.out.println(sequence.getValues().toString());
				middleSum += sequence.getMiddle();

			}  // loop through the sequences in the box

		} // loop through the boxes in boxOfBoxes

		return middleSum;

	} // part2



	public static int part1(ArrayList<NumberSequence> sequences) {
		// approach will be to look at one rule, eliminate all sequenceStrings that don't comply, and then
		// move onto the next rule. should eliminate many sequenceString/rule compliance checks that way

		// rule-evaluation will be made easier by transforming each sequenceString into a hashmap, of which
		// positions its various values occupy

		int middleSum = 0;
		for(NumberSequence sequence : sequences) middleSum += sequence.getMiddle();
		return middleSum;

	} // part1



	public static void purify(String rule, ArrayList<NumberSequence> mainList, ArrayList<NumberSequence> dirtyList) {
		// with respect to the rule provided, this will test every NumberSequence in mainList, and
		// LEAVE BEHIND THERE only those ones that comply with the rule.

		// the ones that don't comply will be moved across into dirtyList (relying on pass-by-reference!)
		// which will ALSO give them a swap-treatment, so that in future they shouldn't fail that particular rule

		String[] array = rule.split("\\|");
		int first = Integer.parseInt(array[0]);
		int last = Integer.parseInt(array[1]);

		for(int i = mainList.size() - 1; i >= 0; i--) {
			NumberSequence sequence = mainList.get(i);
			if(! sequence.doesComply(first, last)) {
				//System.out.print("Removing sequence " + sequence.getValues().toString() + " for non-compliance with rule " + rule);
				sequence.swap(first, last);
				//System.out.println(" (now in dirty pile, modified to " + sequence.getValues().toString() + ")");
				dirtyList.add(sequence);
				mainList.remove(i);

			}

		} // for i


	} // purify



	public static void processFile() {

		try{
				ruleStrings = new ArrayList<String>();
				sequenceStrings = new ArrayList<String>();
				boolean isRule = true;
				Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\Day05input.dat"));

				while (diskScanner.hasNext()) {
					String line = diskScanner.nextLine();

					// line is one of three things: RULE, EMPTY, SEQUENCE
					if (line.isEmpty()) {
						isRule = false;
					} else if(isRule) {
						ruleStrings.add(line);
					} else {
						sequenceStrings.add(line);
					}

				}

				diskScanner.close();

		} catch(Exception e) {
			System.out.println("problems reading file in");
		} // catch

	} // processFile

} // class
