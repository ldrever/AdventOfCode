import java.util.*;
import java.io.*;

class Day13 {

	public static long numberize(String input) {
		// turns eg " Y=513" or "X-32" into an integer eg 513 or -32

		String tempString = input.trim();

		// ALWAYS strip off the first character
		tempString = tempString.substring(1, tempString.length());

		// strip off any "=" or "+" that appears after that
		String first = tempString.substring(0, 1);
		if(first.equals("=") || first.equals("+")) {
			tempString = tempString.substring(1, tempString.length());
		}

		return Long.parseLong(tempString);

	} // numberize method



	public static void processFile(String filePath, ArrayList<ClawMachine> machines, boolean part2) {
		try {
			File fi = new File(filePath);
			Scanner sc = new Scanner(fi);
			int lineNumber = -1;
			long ax = 0, ay = 0, bx = 0, by = 0, prizex = 0, prizey = 0;
			while(sc.hasNext()) {
				lineNumber++;
				String inputLine = sc.nextLine();
				String[] keyValue = null;
				String[] coords = null;
				try {
					keyValue = inputLine.split(":");
					coords = keyValue[1].split(",");
				} catch (Exception e) {
					// empty line situation; nothing to do
				}


				switch (lineNumber % 4) {

					case 0:
						ax = numberize(coords[0]);
						ay = numberize(coords[1]);
					//	System.out.print("Line " + lineNumber + ": ");
					//	System.out.println("Machine " + (lineNumber / 4) + " has A-function of (" + ax + "," + ay + ")");
					break;

					case 1:
						bx = numberize(coords[0]);
						by = numberize(coords[1]);
					//	System.out.print("Line " + lineNumber + ": ");
					//	System.out.println("Machine " + (lineNumber / 4) + " has B-function of (" + bx + "," + by + ")");
					break;

					case 2:
						prizex = numberize(coords[0]);
						prizey = numberize(coords[1]);

						if(part2) {
							prizex += 10000000000000L;
							prizey += 10000000000000L;
						}
					//	System.out.print("Line " + lineNumber + ": ");
					//	System.out.println("Machine " + (lineNumber / 4) + " has prize at (" + prizex + "," + prizey + ")");
						machines.add(new ClawMachine(ax, ay, bx, by, prizex, prizey));
					break;

					case 3:
						// for the empty line between groups

					break;

				}

			} // line-based while loop

			sc.close();
			System.out.println("Success processing file");
		} catch (Exception e) {
			System.out.println("Error processing file");
		}

	} // processFile method



	public static void main(String[] args) {

		ArrayList<ClawMachine> machines = new ArrayList<ClawMachine>();
		boolean part2 = false;
		processFile("Y:\\code\\java\\AdventOfCode\\Day13small.dat", machines, part2);

		long total = 0;
		long currentlyAt = 1;
		long size = machines.size();

		for(ClawMachine m : machines) {
			System.out.println();
			System.out.println("PROCESSING MACHINE " + currentlyAt + " OF " + size);
			System.out.println(m.toString());
			total += m.solve();

		}

		System.out.println("Total: " + total);



	} // main method

} // Day13 class