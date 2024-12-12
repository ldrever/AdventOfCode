import java.util.*;
import java.io.*;

public class StoneSet{

	private int size;
	private ArrayList<Long> coefficients;
	private ArrayList<Long> seeds;
	private ArrayList<Integer> powers;

	public int getSize() {return this.size;}
	public ArrayList<Long> getCoefficients() {return this.coefficients;}
	public ArrayList<Long> getSeeds() {return this.seeds;}
	public ArrayList<Integer> getPowers() {return this.powers;}

	public long getCoefficient(int i) {return this.coefficients.get(i);}
	public long getSeed(int i) {return this.seeds.get(i);}
	public int getPower(int i) {return this.powers.get(i);}



	public StoneSet clone() {
		try {
			ArrayList<Long> coefficients = this.getCoefficients();
			ArrayList<Long> seeds = this.getSeeds();
			ArrayList<Integer> powers = this.getPowers();

			return new StoneSet(coefficients, seeds, powers);
		} catch (Exception e) {
			return null;
		}
	}


	public StoneSet(ArrayList<Long> coefficients, ArrayList<Long> seeds, ArrayList<Integer> powers) throws Exception {
		if(coefficients.size() != seeds.size() || seeds.size() != powers.size()) throw new Exception();
		this.size = coefficients.size();

		this.coefficients = new ArrayList<Long>();
		this.seeds = new ArrayList<Long>();
		this.powers = new ArrayList<Integer>();


		for(int i = 0; i < this.size; i++) {
			this.coefficients.add(coefficients.get(i));
			this.seeds.add(seeds.get(i));
			this.powers.add(powers.get(i));
		}
	} // full constructor


	public StoneSet(long coefficient, long seed, int power) {
		this.coefficients = new ArrayList<Long>();
		this.seeds = new ArrayList<Long>();
		this.powers = new ArrayList<Integer>();

		this.coefficients.add(coefficient);
		this.seeds.add(seed);
		this.powers.add(power);
		this.size = 1;
	} // full details but single-entry constructor



	public StoneSet(ArrayList<Long> seeds, int power) {
		this.size = seeds.size();

		this.coefficients = new ArrayList<Long>();
		this.seeds = new ArrayList<Long>();
		this.powers = new ArrayList<Integer>();

		for(int i = 0; i < this.size; i++) {
			this.coefficients.add(1L);
			this.seeds.add(seeds.get(i));
			this.powers.add(power);
		}

	} // multiple entries but no coefficients and single choice of power



	public StoneSet(long seed) {
		this(1L, seed, 0);
	} // single entry, with default coefficient and power








	public String toString() {

		String result = "";

		for(int i = 0; i < size; i++) {
			if(coefficients.get(i) > 1) result += coefficients.get(i) + " * ";
			result += seeds.get(i);
			result += "[";
			result += powers.get(i) + "]; ";

		} // i loop

		return result;

	} // toString method





	public static long[] split(long input) {
		// assumes a positive input with an even number of digits
		String str = "";
		str += input;
		int halfSize = str.length() / 2;
		long[] result = new long[2];
		result[0] = Long.parseLong(str.substring(0, halfSize));
		result[1] = Long.parseLong(str.substring(halfSize, 2 * halfSize));

		return result;

	} // split method



	public static boolean hasEvenDigitCount(long input) {
		// assumes that input is not negative

		boolean result = false;
		long tenPower = 1;

		// 10^19 is the first power of 10 we CAN'T represent
		for (int exp = 1; exp < 19; exp++) {
			tenPower *= 10;
			if(input >= tenPower)
				result = !result; // main logic - result flips with each x10
			else
				break;

		} // for loop

		return result;

	} // hasEvenDigitCount method



	public void process(boolean debug, boolean allowNegativePowers) {

		// don't see this as fetching the actual sequence-length answer -
		// see it as transforming an expression into one whose powers are
		// a degree lower

		for(int i = this.size - 1; i >= 0; i--) {

			long seed = this.seeds.get(i);
			int power = this.powers.get(i);
			long coefficient = this.coefficients.get(i);

			if(power <= 0 && !allowNegativePowers) continue;

			if(seed == 0L) {
				this.seeds.set(i, 1L);
				this.powers.set(i, power - 1);

			} else if(hasEvenDigitCount(seed)) {
				long[] replacements = split(seed);

				this.seeds.set(i, replacements[0]);
				this.powers.set(i, power - 1);

				this.seeds.add(i + 1, replacements[1]);
				this.powers.add(i + 1, power - 1);
				this.coefficients.add(i + 1, coefficient);

				this.size++;

			} else {
				this.seeds.set(i, seed * 2024);
				this.powers.set(i, power - 1);
			}

		} // i loop
		gather(debug);

	} // process method



	public void gather(boolean debug) { // collect terms with equal seed and power under a single larger coefficient

		int master = 0;

		// outer loop runs rightwards over every triple that's NOT the last one
		while (master < this.size - 1) {
			int masterPower = powers.get(master);
			long masterSeed = seeds.get(master);

			// inner loop runs leftwards, from the very last one, through the one immediately right of the master
			// (to avoid index confusion when removing entries)
			for(int slave = this.size - 1; slave > master; slave--) {
				if(this.powers.get(slave) == masterPower && this.seeds.get(slave) == masterSeed) {
					this.coefficients.set(master, this.coefficients.get(master) + this.coefficients.get(slave));

					this.coefficients.remove(slave);
					this.seeds.remove(slave);
					this.powers.remove(slave);

					this.size--;
				}
			} // slave loop

			master ++;
		} // master loop

	} // gather method



	public long getLowestSeed() throws Exception{
		if (this.size == 0) throw new Exception("no values defined");
		long result = Long.MAX_VALUE;
		for(int i = 0; i < this.size; i++) {
			long nextVal = this.seeds.get(i);
			if (nextVal < result) result = nextVal;
		}
		return result;
	} // getLowestSeed method

	public long getHighestSeed() throws Exception{
		if (this.size == 0) throw new Exception("no values defined");
		long result = Long.MIN_VALUE;
		for(int i = 0; i < this.size; i++) {
			long nextVal = this.seeds.get(i);
			if (nextVal > result) result = nextVal;
		}
		return result;
	} // getHighestSeed method

	public int getLowestPower() throws Exception{
		if (this.size == 0) throw new Exception("no values defined");
		int result = Integer.MAX_VALUE;
		for(int i = 0; i < this.size; i++) {
			int nextVal = this.powers.get(i);
			if (nextVal < result) result = nextVal;
		}
		return result;
	} // getLowestPower method

	public int getHighestPower() throws Exception{
		if (this.size == 0) throw new Exception("no values defined");
		int result = Integer.MIN_VALUE;
		for(int i = 0; i < this.size; i++) {
			int nextVal = this.powers.get(i);
			if (nextVal > result) result = nextVal;
		}
		return result;
	} // getHighestPower method



	public void replace(StoneSet before, StoneSet after) throws Exception {
		// when supplied with a SINGLE-ENTRY "before" StoneSet
		if (before.getSize() > 1) throw new Exception();
		long toReplace = before.getSeeds().get(0);

		// whose power must be zero
		if (before.getHighestPower() != 0) throw new Exception();

		// and an "after" StoneSet with all powers negative
		int newPower = after.getHighestPower();
		if (newPower >= 0) throw new Exception();

		// and the SAME
		if (after.getLowestPower() < newPower) throw new Exception();

		// find any corresponding subset that matches the before-set
		for(int i = this.size; i >= 0; i--) {

			if (this.seeds.get(i) == toReplace) {

				int oldPower = this.powers.get(i);

				if(oldPower + newPower < 0) continue;

				long oldCoefficient = this.coefficients.get(i);

				this.seeds.remove(i);
				this.powers.remove(i);
				this.coefficients.remove(i);
				this.size--;

				for(int j = 0; j < after.getSize(); j++) {
					this.seeds.add(after.getSeed(j));
					this.powers.add(oldPower + newPower);
					this.coefficients.add(oldCoefficient * after.getCoefficient(j));
					this.size++;
				} // j-loop over AFTER

			} // seed-match if

		} // i-loop over THIS

	} // replace method



	public boolean isBelowMax(long max) {

		for (long seed : this.seeds) {
			if(seed >= max) return false;
		} // seed loop
		return true;

	} // areAllSeedsSingleDigits method



	public long evaluate() throws Exception {
		// typically a check should be made that all powers are zero,
		// before invoking this

		long total = 0L;
		for(long coefficient : this.coefficients) total += coefficient;
		return total;

	} // evaluate method



} // StoneSet class