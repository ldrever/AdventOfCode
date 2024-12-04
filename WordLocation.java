public class WordLocation {
	private int xCentre, yCentre;
	private MatrixDirection direction;

	public int getXCentre() {
		return this.xCentre;
	}

	public int getYCentre() {
		return this.yCentre;
	}

	public MatrixDirection getDirection() {
		return this.direction;
	}

	public WordLocation(int xCentre, int yCentre, MatrixDirection direction) {
		this.xCentre = xCentre;
		this.yCentre = yCentre;
		this.direction = direction;
	}

}
