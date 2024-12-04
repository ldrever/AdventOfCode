public class MatrixDirection {
	public String name;
	public int top, bot, left, right;
	public int yIncrement, xIncrement;

	public MatrixDirection(String inputName, int inputTop, int inputBot, int inputLeft, int inputRight, int inputY, int inputX) {
		this.name = inputName;
		this.top = inputTop;
		this.bot = inputBot;
		this.left = inputLeft;
		this.right = inputRight;
		this.yIncrement = inputY;
		this.xIncrement = inputX;
	}

}
