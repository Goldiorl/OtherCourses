import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Vector;

public class BruteForce {

	Vector<MyLine> checkedLines = new Vector<MyLine>();
	ArrayList<MyPoint> hullPoints = new ArrayList<MyPoint>();
	ArrayList<MyLine> hullLines = new ArrayList<MyLine>();
	MyPoint currentPoint;
	MyLine temp;

	/**
	 * Constructor for console application
	 * @param pointList
	 */
	public BruteForce(ArrayList<MyPoint> pointList) {
		int x, y, z;
		boolean leftMost, rightMost;
		for (x = 0; x < pointList.size(); x++) {
			for (y = x+1; y < pointList.size(); y++) {
				leftMost = true;
				rightMost = true;
				temp = new MyLine(pointList.get(x), pointList.get(y));

				for (z = 0; z < pointList.size(); z++) {
					if (z != x && z != y) {
						if (temp.onLeft(pointList.get(z)))
							leftMost = false;
						else
							rightMost = false;
					}
				}
				if (leftMost || rightMost) {
					if (!hullPoints.contains(pointList.get(x)))
						hullPoints.add(pointList.get(x));
					else if (!hullPoints.contains(pointList.get(y)))
						hullPoints.add(pointList.get(y));
				}
			}
		}		
	}

	/**
	 * Constructor for GUI application
	 * @param g
	 * @param pointList
	 */
	public BruteForce(Graphics2D g, ArrayList<MyPoint> pointList) {
		int x, y, z;
		boolean left, right;
		for (x = 0; x < pointList.size(); x++) {
			for (y = x+1; y < pointList.size(); y++) {
				left = true;
				right = true;
				temp = new MyLine(pointList.get(x),pointList.get(y));

				for (z = 0; z < pointList.size(); z++) 
				{
					currentPoint = pointList.get(z);
					checkedLines.removeAllElements();
					checkedLines.addElement(new MyLine(pointList.get(x),pointList.get(y)));
					if ((z != x) && (z != y)) 
					{
						if (temp.onLeft(currentPoint))
							left = false;
						else
							right = false;
					}
					try {
						Thread.sleep(50);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					g.setColor(Color.gray);
					g.fillRect(0, 0, 800, 800);
					g.setColor(Color.black);
					for (int i = 0; i < pointList.size(); i++)
						pointList.get(i).draw(g, 8);
					g.setColor(Color.blue);
					for (int j = 0; j < hullLines.size(); j++)
						hullLines.get(j).draw(g);
					currentPoint.drawCurrent(g);
					g.setColor(Color.red);
					for (int k = 0; k < checkedLines.size(); k++) {
						checkedLines.elementAt(k).draw(g);
					}
				}
				if (left || right) {
					if (!hullPoints.contains(pointList.get(x)))
						hullPoints.add(pointList.get(x));
					else if (!hullPoints.contains(pointList.get(y)))
						hullPoints.add(pointList.get(y));
					hullLines.add(new MyLine(pointList.get(x),pointList.get(y)));
				}
			}
		}
		g.setColor(Color.blue);
		for (int i = 0; i < hullPoints.size(); i++)
			hullPoints.get(i).draw(g, 9);
		for (int j = 0; j < hullLines.size(); j++)
			hullLines.get(j).draw(g);
	}
}
