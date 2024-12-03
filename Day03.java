import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 {

	private static int getVersion() {
	    String version = System.getProperty("java.version");
	    if(version.startsWith("1.")) {
	        version = version.substring(2, 3);
	    } else {
	        int dot = version.indexOf(".");
	        if(dot != -1) { version = version.substring(0, dot); }
	    } return Integer.parseInt(version);
	}



	public static void dissect(String input, int firstToInclude, int firstToExclude, int trailings) {
		// just a debugging function, so that you can quickly verify that you're dealing
		// with the part of a string that you think you are

		int len = input.length();

		// VALIDATE the request, without reference to the specifics of the input string (i.e. ensure it's not inherently impossible)
		if(trailings < 0) {
			System.out.println("Negative trailing characters are not allowed.");
			return;
		}

		if(firstToExclude < firstToInclude) {
			System.out.println("firstToExclude cannot be lower than firstToInclude");
			return;
		}

		// REPEAT BACK the request
		System.out.println();
		System.out.print("Your string has length " + len);
		System.out.println( (len > 0 ? " (characters 0 through " + (len - 1) +")" : " (no characters)"));

		if (trailings > 0) 						System.out.println("Requested: the " + trailings + " pre-trailing characters " + (firstToInclude - trailings) + " through " + (firstToInclude - 1));
		if (firstToExclude > firstToInclude) 	System.out.println("Requested: the " + (firstToExclude - firstToInclude) + " main characters " + (firstToInclude) + " through " + (firstToExclude - 1));
		if (trailings > 0)						System.out.println("Requested: the " + trailings + " post-trailing characters " + (firstToExclude) + " through " + (firstToExclude + trailings - 1));



		// ADAPT the request based on the string's actual properties...

		// 1: deal with any requests that miss the string altogether
		if(firstToInclude > len) firstToInclude = len;
		if(firstToExclude < 0) firstToExclude = 0;

		// 2: deal with any requests that want characters outside the bounds
		if(firstToInclude < 0) firstToInclude = 0;
		if(firstToExclude > len) firstToExclude = len;

		// 3: work out available trailings
		int firstLeftTrailer = firstToInclude - trailings;
		int firstRightNonTrailer = firstToExclude + trailings;

		if(firstLeftTrailer < 0) firstLeftTrailer = 0;
		if(firstRightNonTrailer > len) firstRightNonTrailer = len;

		// display what's available
		System.out.println();
		System.out.println("Available: the " + (firstToInclude - firstLeftTrailer) + " pre-trailing characters: " + input.substring(firstLeftTrailer, firstToInclude));
		System.out.println("Available: the " + (firstToExclude - firstToInclude) + " included characters: " + input.substring(firstToInclude, firstToExclude));
		System.out.println("Available: the " + (firstRightNonTrailer - firstToExclude) + " post-trailing characters: " + input.substring(firstToExclude, firstRightNonTrailer));
		System.out.println();
	}

	public static int eval(String input) {
		// we trust that the input is already a match for "mul\\([0-9]{1,3}\\,[0-9]{1,3}\\)"
		// eg "mul(999,1)"
		// WE ASSUME NO NEGATIVE VALUES ARE SENT

		String needle = "[0-9]{1,3}";

		Pattern pattern = Pattern.compile(needle, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		int position = 0;
		int product = 1;
		while(matcher.find(position)) {
			int start = matcher.start();
			int end = matcher.end();
			String digitGroup = input.substring(start, end);
			int factor = Integer.parseInt(digitGroup);
			product *= factor;
			position = end;
		}
		return product;
	}


	public static void main(String args[]) throws IOException {
		Path path = Path.of("Y:\\code\\java\\AdventOfCode\\day03input.dat");
		String content = Files.readString(path);

		String needle = "(mul\\([0-9]{1,3}\\,[0-9]{1,3}\\)|do)"; // negative values out of scope

		Pattern pattern = Pattern.compile(needle, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);

		String state = "valid";
		int position = 0;
		int total = 0;
		ArrayList<String> results = new ArrayList<String>();

		while(matcher.find(position)) {
			int start = matcher.start();
			int end = matcher.end();
			//dissect(content, start, end, 3);
			String result = content.substring(start, end);

			if (result.equalsIgnoreCase("do")) {

				if(content.substring(start, end + 3).equalsIgnoreCase("don't")) {
					System.out.println("Entering INVALID mode following encounter of 'don't' at characters " + start + " through " + (end + 1));
					state = "invalid";
				} else {
					System.out.println("Entering VALID mode following encounter of 'do' at characters " + start + " through " + (end - 1));
					state = "valid";
				}

			}	else {
					if(state.equalsIgnoreCase("valid")) {
						results.add(result);
						total += eval(result);
						System.out.println("Valid: " + result);
					} else if(state.equalsIgnoreCase("invalid")) {
						System.out.println("Invalid: " + result);
					}
			}

			position = end;
		}

		System.out.println("Total: " + total);

	}

}