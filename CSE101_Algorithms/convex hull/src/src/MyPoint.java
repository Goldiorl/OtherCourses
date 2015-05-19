import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class MyPoint extends Point {

	/**
	 * attribute
	 */
	private String name;
	
	/**
	 * Get-Set methods
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * constructor
	 * @param name
	 * @param x
	 * @param y
	 */
	public MyPoint(String name, int x, int y) {
		super(x, y);
		this.name = name;
	}

	/**
	 * compares the current point with given two points according two their turning direction and the lengths
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean compare(MyPoint p1, MyPoint p2) {

		float turning = (p2.x - p1.x) * (y - p1.y) - (x - p1.x) * (p2.y - p1.y);

		if (turning > 0)  // direction is left
			return false;
		else if (turning < 0)  // direction is left
			return true;
		else if (((p2.x - p1.x) * (x - p1.x) < 0) || ((p2.y - p1.y) * (y - p1.y) < 0)) //current point is behind the line which given two points creates
			return false;
		else if (Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)) < Math.sqrt((x - p1.x) * (x - p1.x) + (y - p1.y) * (y - p1.y))) //current point is in front of the line which given two points creates
			return true;
		return false;
	}

	/**
	 * draw a point
	 */
	public void draw(Graphics g, int size) {
		g.fillOval(x - 4, y - 4, size, size);
		g.drawString(this.name, x + 4, y + 4);
	}

	/**
	 * draw the current point in red
	 */
	public void drawCurrent(Graphics g) {
		g.setColor(Color.red);
		g.fillOval(x - 5, y - 5, 10, 10);
		g.drawString(this.name, x + 4, y + 4);
	}

}
