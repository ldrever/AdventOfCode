import java.util.*;
import java.io.*;

public class Day11 {



	public static StoneSet loadFromFile(boolean debug, String path, int iterationsDesired) {
		try {
			File fi = new File(path);
			Scanner sc = new Scanner(fi);
			String str = sc.nextLine();
			sc.close();
			String[] ra = str.split(" ");

			ArrayList<Long> sequence = new ArrayList<Long>();

			for(int i = 0; i < ra.length; i++) {
				long l = Long.parseLong(ra[i]);
				sequence.add(l);
			}
			return new StoneSet(sequence, iterationsDesired);

		}
		catch (Exception e) {
			System.out.println("Could not generate a StoneSet from file at path: " + path);
			return null;
		}
	} // loadFromFile method



	public static void populateLibrary(
		  boolean debug
		, ArrayList<StoneSet> befores
		, ArrayList<StoneSet> afters
		) throws Exception {

		if(befores.size() > 0 || afters.size() > 0) throw new Exception();

		// having been pointed towards these two empty ArraySets, populate the
		// first with single-element StoneSets, and the second with longer
		// StoneSets that have negative powers. we will eventually apply these
		// to the input, replacing a few high-powered terms with more low-
		// powered ones

		int max = 100; // determined empirically to work well
		int worstCaseStepCount = 0;
		long coefficient = 1L;
		boolean allowNegatives = true;

		for(int i = 0; i < max; i++) {
			StoneSet initialSet = new StoneSet(coefficient, i, 0);
			befores.add(initialSet.clone());

			if(debug) {
				System.out.println();
				System.out.println("Now considering StoneSet " + initialSet.toString());
				System.out.println("================================================");
			} else {
				System.out.print(initialSet.toString() + " -> ");
			}

			for(int step = 1;; step++) {
				initialSet.process(debug, allowNegatives);
				if(debug) System.out.println("After " + step + " iterations: " + initialSet.toString());
				if (initialSet.getSize() > 1 && initialSet.isBelowMax(max)) {
					afters.add(initialSet);
					if(debug) System.out.println("there we go");
					if (step > worstCaseStepCount) worstCaseStepCount = step;
					break;
				} // if

			} // step loop
			System.out.println(initialSet.toString());
		} // number loop

		System.out.println("All done in max " + worstCaseStepCount + " steps");

	} // populateLibrary method



	public static long answer(int part, boolean debug) throws Exception {

//	public static void main(String[] args) {

//		boolean debug = false;
		boolean allowNegativePowers = false;
		int iterationsDesired = 75;
		String path = "Y:\\code\\java\\AdventOfCode\\Day11input.dat";
		StoneSet ss = loadFromFile(debug, path, iterationsDesired);

		for(int i = 1; i <= iterationsDesired; i++) {
			//System.out.print("Size after " + i + " steps: ");
			ss.process(debug, allowNegativePowers);
			try {
				//System.out.println(ss.evaluate());
				if(i == 25 && part == 1) return ss.evaluate();
				if(i == 75 && part == 2) return ss.evaluate();
			} catch (Exception e) {
				System.out.println("Could not evaluate");
			}
		}

		return -1;

	/*

		StoneSet s = new StoneSet(1, 0, 1);
		StoneSet t = s.clone();
		for(int i = 0; i < 4; i++) {

			//System.out.println(s.toString());
			s.process(false, true);
		}
		System.out.println(t.toString());
		System.out.println(s.toString());


		boolean allowNegatives = true;
		int desiredSteps = 30;
		long max = 100L;


		ArrayList<StoneSet> befores = new ArrayList<StoneSet>();
		ArrayList<StoneSet> afters = new ArrayList<StoneSet>();

		try {
			populateLibrary(debug, befores, afters);
		} catch (Exception e) {
			System.out.println("Error populating library");
		}

*/

	} // main method
} // Day11 class