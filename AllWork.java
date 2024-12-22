import java.lang.reflect.Method;
import java.util.*;
import java.io.*;

public class AllWork {

	/*

	Get used to pasting the two lines below:
		public static long answer(int part, boolean debug) throws Exception {
//


	*/

	public static void main(String[] args) throws IOException {

		long[][] ra = {{    2285373L,       21142653L}
					,{          472L,            520L}
					,{    182780583L,       90772405L}
					,{         2551L,           1985L}
					,{		   5166L,           4679L}
					,{		   4939L,           1434L}
					,{1708857123053L,189207836795655L}
					,{			394L,           1277L}
					,{6288599492129L,  6321896265143L}
					,{          510L,           1058L}
					,{       233050L,276661131175807L}
					,{      1437300L,         849332L}
					,{        29598L, 93217456941970L} // FIXME want to do "part 3" of day 13 also
					,{    228690000L,           7093L}
					,{      1495147L,        1524905L}
					,{11048L,11048L} // FIXME day 16: this is actually one of the baby sample maps; checking my DFS implementation doesn't break
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
					result = (Long) answerMethod.invoke(null, part, false);
				} catch (Exception e) {System.out.println("problems RUNNING a method");}

				System.out.println(result);
				if(result == rightAnswer)
					System.out.println("CORRECT!");
				else
					System.out.println("THIS IS NOT CORRECT!!!");

			} // for-part loop



		} // for-problem loop

	} // main method

} // AllWork class