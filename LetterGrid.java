import java.util.Scanner;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LetterGrid {

	private int height, width;
	private char[][] grid; // LIKE A MATRIX - FIRST COUNTER VERTICAL, SECOND COUNTER HORIZONTAL

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public char[][] getGrid() {
		return this.grid;
	}

	public char getCell(int row, int col) {
		return this.grid[row][col];
	}

	public void setCell(int row, int col, char c) {
		this.grid[row][col] = c;
	}

	public LetterGrid(String path) throws IOException {

		ArrayList<char[]> inputLines = new ArrayList<char[]>();
		Scanner diskScanner = new Scanner(new File(path));

		int width = 0;
		int height = 0;

		while (diskScanner.hasNext()) {
			String text = diskScanner.nextLine();

			inputLines.add(text.toCharArray());
			height++;
			if (width < text.length()) width = text.length();

		}

		diskScanner.close();

		this.grid = new char[height][];

		for(int i = 0; i < height; i++) {
			this.grid[i] = inputLines.get(i);
		}

		this.height = height;
		this.width = width;

	} // constructor

	public void wipe(char c) {
		for(int row = 0; row < this.getHeight(); row++) {
			for(int column = 0; column < this.getWidth(); column++) {
				this.setCell(row, column, c);
			}
		}

	} // wipe method

}
