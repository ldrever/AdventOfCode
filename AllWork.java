import java.lang.reflect.Method;
import java.util.*;
import java.io.*;

public class AllWork {

	public static void main(String[] args) throws IOException {

		long[][] ra = {{2285373L,21142653L}
					,{472,520}
		/*			,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
					,{,}
		*/			};



		System.out.println("Verifying tests pass...");

		for(int i = 0; i < ra.length; i++) {
			int problem = i + 1;


			String nextClassString = "Day" + String.format("%02d", problem);
			Class nextClass = null;

			try {
				nextClass = Class.forName(nextClassString);

			} catch (Exception e) {System.out.println("problems getting a class");}

			for(int j = 0; j < 2; j++) {
				int part = j + 1;
				long rightAnswer = ra[i][j];
				System.out.print("Day " + problem + " part " + part +" should be " + rightAnswer + "; it is in fact...");
				boolean debug = false;
				Method answerMethod = null;
				try {
					answerMethod = nextClass.getDeclaredMethod("answer", int.class, boolean.class);
				} catch (Exception e) {System.out.println("problems getting a method");}
				Long result = null;
				try {
					result = (Long) answerMethod.invoke(null, 1, false);
				} catch (Exception e) {System.out.println("problems RUNNING a method");}

				System.out.println(result);

			} // for-part loop



		} // for-problem loop

	} // main method

} // AllWork class