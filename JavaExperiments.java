import java.util.*;
import java.io.*;

class JavaExperiments {


	public static void main(String[] args) {

		HashSet<String> hs = new HashSet<String>();

		hs.add("(0,5)");
		hs.add("(0,5)");
		hs.add("(1,2)");
		hs.add("(2,1)");
		hs.add("(10,11)");

		System.out.println(hs.toString());

		List<String> list = new ArrayList<String>(hs);
		Collections.sort(list);
		System.out.println(list.toString());


		System.out.println(LetterGrid.paddedCoords(0, 6, 121000, 50));


	} // main method

} // Day13 class