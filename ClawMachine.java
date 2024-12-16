import java.util.*;
import java.io.*;

public class ClawMachine {

	// data
	long ax;
	long ay;
	long bx;
	long by;
	long prizex;
	long prizey;

	// constructor
	public ClawMachine(long aX, long aY, long bX, long bY, long prizeX, long prizeY) {
		this.ax = aX;
		this.ay = aY;
		this.bx = bX;
		this.by = bY;
		this.prizex = prizeX;
		this.prizey = prizeY;
	}

	public String toString() {
		String result = "a = (" + ax + "," + ay + "); ";
		result += "b = ( " + bx + "," + by + "); ";
		result += "z = ( " + prizex + "," + prizey + "); ";

		return result;
	}

	public long solve () {
		MachineVector a = new MachineVector(ax, ay);
		MachineVector b = new MachineVector(bx, by);
		MachineVector z = new MachineVector(prizex, prizey);

		System.out.println("Working on ( " + a.toString() + " " + b.toString() + ")(x,y) = " + z.toString());

		try {
			MachineVector solution = MachineVector.solve(a, b, z);
			System.out.println(solution.toString() + " resolves this one.");
			long value = solution.dotProduct(new MachineVector(3L, 1L));
			System.out.println("costing " + value);
			return value;

		} catch (Exception e) {
			System.out.println("Cannot currently handle this situation. (" + e.getMessage() + ")");
			return 0L;
		}

	} // solve method
}