
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day05 {

	public static ArrayList<String> rules;
	public static ArrayList<String> sequences;



	public static void main(String[] args) {
		// approach will be to look at one rule, eliminate all sequences that don't comply, and then
		// move onto the next rule. should eliminate many sequence/rule compliance checks that way

		// rule-evaluation will be made easier by transforming each sequence into a hashmap, of which
		// positions its various values occupy

		processFile();

		ArrayList<HashMap<Integer, Integer>> hashmaps = new ArrayList<HashMap<Integer, Integer>>();
		ArrayList<HashMap<Integer, Integer>> wrongHashmaps = new ArrayList<HashMap<Integer, Integer>>();

		for (String sequence : sequences) hashmaps.add(processSequence(sequence));



		// now go through the process of looking at every rule in turn, and eliminating
		// sequences that do not comply:

		for(String rule : rules) {
			String[] array = rule.split("\\|");
			int first = Integer.parseInt(array[0]);
			int last = Integer.parseInt(array[1]);

			// System.out.println(first + " must come before " + last);

			for(int i = hashmaps.size() - 1; i >= 0; i--) {
				if(! doesComply(hashmaps.get(i), first, last)) {
					//System.out.println("Removing sequence " + i + " for non-compliance with rule " + rule);
					wrongHashmaps.add(hashmaps.get(i));
					hashmaps.remove(i);

				}

			} // sequence-hashmap loop

		} // rule loop

		// now only valid sequence-hashmaps are left; iterate and total up their middle values

		int total = 0;

		for(HashMap<Integer, Integer> hashmap : hashmaps) total += hashmap.get(-1);

		System.out.println("Answer to part 1: " + total);

	} // main



	public static boolean doesComply(HashMap<Integer, Integer> hashmap, int first, int last) {
		//public static void doesComply(HashMap<Integer, Integer> hashmap, int first, int last) {

		try {
			int firstPosition = hashmap.get(first);
			int lastPosition = hashmap.get(last);
			//System.out.println(first + " is at " + firstPosition + " and " + last + " is at " + lastPosition);
			return(firstPosition < lastPosition);
		} catch (NullPointerException e) {
			//System.out.println("one or both values not present");
			return true;
		}

	} // doesComply



	public static HashMap<Integer, Integer> processSequence(String sequence) {

		// starting with a comma-separated string of integers, return a hashmap
		// where the FIRST value of each pair is one OF those integers, and the
		// SECOND (lookup) value is its POSITION, starting from zero

		// because we'll need it later, we'll also put in a special -1 entry,
		// whose lookup value will be the middle member of the sequence - so
		// note that this time the actual number will be second, and -1 will be first

		String[] array = sequence.split(",");
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		for(int i = 0; i < array.length; i++) {

			int value = Integer.parseInt(array[i]);
			result.put(value, i);

			// store the middle value
			// we are placing absolute trust in the input data that every sequence has an odd-numbered length
			if(i * 2 == array.length - 1) result.put(-1, value);

		} // for

		return result;

	} // processSequence



	public static void processFile() {

		try{
				rules = new ArrayList<String>();
				sequences = new ArrayList<String>();
				boolean isRule = true;
				Scanner diskScanner = new Scanner(new File("Y:\\code\\java\\AdventOfCode\\Day05input.dat"));

				while (diskScanner.hasNext()) {
					String line = diskScanner.nextLine();

					// line is one of three things: RULE, EMPTY, SEQUENCE
					if (line.isEmpty()) {
						isRule = false;
					} else if(isRule) {
						rules.add(line);
					} else {
						sequences.add(line);
					}

				}

		diskScanner.close();

	} catch(Exception e) {
		System.out.println("problems reading file in");
	} // catch

	} // processFile

} // class
