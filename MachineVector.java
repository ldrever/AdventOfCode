import java.util.*;
import java.io.*;

public class MachineVector {

	// data
	long x;
	long y;

	// getters
	public long getX(){return this.x;}
	public long getY(){return this.y;}

	// constructor
	public MachineVector(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return ("(" + this.x + "," + this.y + ")");
	}

	public long dotProduct(MachineVector input) {
		long result = this.getX() * input.getX();
		result += this.getY() * input.getY();
		return result;
	}

	public long getDeterminant(MachineVector right) {
		MachineVector left = this;
		return left.getX() * right.getY() - left.getY() * right.getX();

	} // getDeterminant method

	public static MachineVector solve(MachineVector a, MachineVector b, MachineVector z) throws Exception {
		long determinant = a.getDeterminant(b);
		// FIXME
		//degenerateSolve(a, b, z);
		//if(determinant == 0L) return degenerateSolve(a, b, z);

		// invert 2D matrix
		long solutionX = b.getY() * z.getX() - b.getX() * z.getY();
		long solutionY = a.getX() * z.getY() - a.getY() * z.getX();
		if (solutionX % determinant != 0L || solutionY % determinant != 0L) throw new Exception("fractions required");
		solutionX /= determinant;
		solutionY /= determinant;
		if (solutionX < 0L || solutionY < 0L) throw new Exception("negatives required");
		return new MachineVector(solutionX, solutionY);

	} // solve method

	public static MachineVector degenerateSolve(MachineVector a, MachineVector b, MachineVector z) throws Exception {

		// so - get the highest common factor...
		long hcfA = hcf(a.getX(), a.getY());
		long hcfB = hcf(b.getX(), b.getY());
		long hcfZ = hcf(z.getX(), z.getY());

		long baseX = a.getX() / hcfA;
		long baseY = a.getY() / hcfA;

		if(z.getX() / hcfZ != baseX || z.getY() / hcfZ != baseY) throw new Exception("target not reachable on degenerate solution-line");
		/*

			at this point, every x-y coordinate pair is in the same proportional
			relation, so just think about the x-coordinate:

			m * hcfA * baseX + n * hcfB * baseX = hcfZ * baseX


			ignoring integrality for now, we can say that everything along the
			line from (m = 0, n = hcfZ / hcfB) to (m = hcfZ/hcfA, n = 0) is a solution

			which is to say, n = (hcfZ - m*hcfA) / hcfB
			as m runs from 0 to hcfZ/hcfA

			the real question being - what point on that line minimizes cost?
			if we say that cost = 3m + n then
			cost = (3 * hcfB * m + hcfZ - m*hcfA) / hcfB

			ie cost = ( m(3*hcfB - hcfA) + hcfZ ) / hcfB

			with the implication that:
			- if 3*hcfB == hcfA then all solutions on the line are the same price - just return this price in that case
			- if 3*hcfB > hcfA then cost rises linearly with increasing m, and hence we want the lowest-m possible solution
			- if 3*hcfB < hcfA then cost decreases linearly with increasing m, and we wnat the highest-m possible solution

			Going back to n = (hcfZ - m*hcfA) / hcfB

			we can see that this is integral only when (hcfZ - m*hcfA) is zero mod hcfB

			Hence z = am (mod b)
			m = z * a^(-1) (mod b)

			So let's seek out such...
		*/
		long m = 0;
		long abCoprimality = hcf(hcfA,hcfB);
		if(hcfZ % abCoprimality != 0) throw new Exception("target on degenerate solution-line not reachable with integral multiples");

		// divide through so that we can eventually take modular inverses
		hcfA /= abCoprimality;
		hcfB /= abCoprimality;
		hcfZ /= abCoprimality;


			m = modInverse(hcfA, hcfB);
			m *= hcfZ;
			m %= hcfB;

			// which is already good, if we're looking to minimize it... but if maximizing...
			if(3 * hcfB < hcfA) {
				// want it the same mod hcfB, but at the top end of the scale...

				// integer-division to find the top hcfB-multiple...
				long max = hcfZ / hcfA;

				long maxModB = max % hcfB;

				long toSubtract = hcfB + maxModB - m;
				toSubtract %= hcfB;

				m = max - toSubtract;
			}

			return new MachineVector(m, hcfZ - m * hcfZ);

	} // degenerateSolver method


	public static long hcf(long a, long b) {
		// highest common factor - implments euclid's algorithm
		// assumes that both inputs are positive and that at least one is non-zero

		if(a > b) return hcf(b, a); // guarantees that b is not smaller than a
		if(a == 0) return b;
		long mod = b % a;
		if(mod == 0) return a;
		return hcf(mod, a);

	} // hcf method


	public static long modInverse(long number, long modulus) {

		for(long i = 0L; i < modulus; i++) {
			if((i * number) % modulus == 1) return i;
		} // for-i loop
		return -1; // means something has gone, such as the inputs not being coprime
	} // modInverse method


}