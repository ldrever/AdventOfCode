
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

public class Day06 {

	public static void main(String[] args) {

		String path = "Y:\\code\\java\\AdventOfCode\\Day06input.dat";

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


			/*
				OK. We now have all possible CANDIDATE positions in which to put our one blocker...
				now for the process to evaluate each. We can't continue using the same map; it's been
				heavily modified - we have to go back to the file...



			*/

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
				/*
				System.out.print("Continue?");
				String input = sc.next();
				if(input.equalsIgnoreCase("N")) break;

				// FIXME have hand-continuation here, in case brute-force is too much...
				*/

			} // for over possible blockers

			System.out.println("Blockers that resulted in loops: " + loopCount);


		} catch (IOException e) { // FIXME wrong location?
			System.out.println("Problems loading file");
		}

	} // main

} // class
