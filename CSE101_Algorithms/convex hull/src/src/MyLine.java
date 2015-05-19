import java.awt.Graphics;
import java.util.ArrayList;

public class MyLine {

	/**
	 * attributes
	 */
	MyPoint point1;
	MyPoint point2;
	float angle;
	float polarAngle;
	float distance;
	boolean angleInfinite;

	/**
	 * constructor
	 */
	public MyLine(MyPoint p1, MyPoint p2) 
	{
		point1 = p1;
		point2 = p2;
		if (p1.x != p2.x) 
		{
			angleInfinite = false;
			if (p1.y == p2.y)
				angle = 0;
			else
				angle = (float) (p1.y - p2.y) / (p1.x - p2.x);
		} 
		else
			angleInfinite = true;
		distance = (float) ((float) Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((float)(p2.getY() - p1.getY()), 2));
		polarAngle = (float)Math.atan((double) (p1.y - p2.y) / (double)(p1.x - p2.x));
		if ( polarAngle < 0 )
			polarAngle += Math.PI;
	}
	
	/**
	 * calculates if the given point is on the left or right of the line
	 */
	public boolean onLeft(MyPoint point) 
	{
		if (this.angleInfinite)   // the points are on the same x axis
		{
			// checking point is on the left, return true
			if (point.x < point1.x)
				return true;
			else 
			{
				if (point.x == point1.x) // points are on same x line, compare the y values 
				{
					if (( point.y > point1.y && point.y < point2.y) || (point.y > point2.y && point.y < point1.y))
						return true;
					else
						return false;
				} 
				else  // checking point is on the right, return false
					return false;
			}
		} 
		else  // calculate the side between current point and given points according to their angles
		{
			int tempX = (int) (((point.x + angle * (angle * point1.x - point1.y + point.y)) / (1 + angle * angle)) * 800);
			int tempY = (int) ((angle * (tempX / 800 - point1.x) + point1.y) * 800);

			if (angle>0) 
			{
				if (tempX > (point.x * 800))
					return true;
				else
					return false;
			} 
			else if(angle<0)
			{
				if ((point.x * 800) > tempX)
					return true;
				else
					return false;
			}
			else
			{
				if ((point.y * 800) > tempY)
					return true;
				else
					return false;
			}
		}
	}

	/**
	 * draws a line according to coordinates
	 * @param g
	 */
	public void draw(Graphics g) {
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
	}
}