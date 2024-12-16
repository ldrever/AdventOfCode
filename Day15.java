import java.util.*;
import java.io.*;

class Day15 {



	public static void main(String[] args) {
		LetterGrid lg = null;
		try {lg = new LetterGrid("Y:\\code\\java\\AdventOfCode\\Day15small.dat");}
		catch (Exception e) {System.out.println("file processing error");}

		System.out.println(lg.sumGPS());

	} // main method

} // Day13 class