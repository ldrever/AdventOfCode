import java.util.ArrayList;
import java.util.HashMap;

public class NumberSequence {



	private ArrayList<Integer> values;
	private HashMap<Integer, Integer> index;
	private int middle;



	public NumberSequence(String text) {

		String[] array = text.split(",");

		values = new ArrayList<Integer>();
		index = new HashMap<Integer, Integer>();

		for(int i = 0; i < array.length; i++) {
			int value = Integer.parseInt(array[i]);
			this.values.add(value);
			this.index.put(value, i);
			if(i * 2 == array.length - 1) this.middle = value; // picks out the middle value
		} // for i

	} // text constructor

	public int getMiddle() {
		return this.middle;
	}



	public ArrayList<Integer> getValues() {
		return this.values;

	}


	public void swap(int firstValue, int lastValue) {

		try {
			int firstPosition = this.index.get(firstValue);
			int lastPosition = this.index.get(lastValue);

			this.index.put(firstValue, lastPosition);
			this.index.put(lastValue, firstPosition);

			this.values.remove(firstPosition);
			this.values.add(firstPosition, lastValue);
			this.values.remove(lastPosition);
			this.values.add(lastPosition, firstValue);

			// now just have to re-jig that middle position...

			int middlePosition = (this.values.size() - 1) / 2;
			this.middle = values.get(middlePosition);

		} catch (NullPointerException e) {
 			// nothing to do if one of the values to be swapped wasn't actually present
		}


	} // swap


	public boolean doesComply(int firstValue, int lastValue) {

		try {
			int firstPosition = this.index.get(firstValue);
			int lastPosition = this.index.get(lastValue);
			return(firstPosition < lastPosition);
		} catch (NullPointerException e) { // absence of either number from the sequence means it complies with that rule
			return true;
		}

	} // doesComply


} // class