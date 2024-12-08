import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Point {

	private LetterGrid parentMap;
	private int row;
	private int column;
	private char value;



	public Point(LetterGrid parentMap, int row, int column) throws PointOutOfBoundsException{
		this.parentMap = parentMap;
		this.row = row;
		this.column = column;

		if(this.row < 0 || this.column < 0) throw new PointOutOfBoundsException("negative co-ordinates not possible");
		if(this.row >= parentMap.getHeight() || this.column >= parentMap.getWidth()) throw new PointOutOfBoundsException("map is too small");

		this.value = parentMap.getCell(row, column);
	} // constructor



	public int getRow() {
		return this.row;
	}



	public int getColumn() {
		return this.column;
	}



	public String getCoords() {
		return "(" + this.row + ", " + this.column + ")";
	}



	public Point getVectorMultiple(Point directionPoint, int multiple) throws PointOutOfBoundsException {

		int rowComponent = directionPoint.getRow() - this.getRow();
		int columnComponent = directionPoint.getColumn() - this.getColumn();

		int factor = hcf(Math.abs(rowComponent), Math.abs(columnComponent));
		rowComponent /= factor;
		columnComponent /= factor;

		return new Point(parentMap, this.row + multiple * rowComponent, this.column + multiple * columnComponent);

	} // getReflection method



	public static int hcf(int a, int b) {
		// highest common factor - implments euclid's algorithm
		// assumes that both inputs are positive and that at least one is non-zero

		if(a > b) return hcf(b, a); // guarantees that b is not smaller than a
		if(a == 0) return b;
		int mod = b % a;
		if(mod == 0) return a;
		return hcf(mod, a);

	} // hcf method

}
