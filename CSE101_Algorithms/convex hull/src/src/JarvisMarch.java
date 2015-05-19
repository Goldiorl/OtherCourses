import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Vector;

public class JarvisMarch {

	Vector<MyLine> checkedLines = new Vector<MyLine>();
	ArrayList<MyPoint> hullPoints = new ArrayList<MyPoint>();
	ArrayList<MyLine> hullLines = new ArrayList<MyLine>();
	ArrayList<MyPoint> convexHull = new ArrayList<MyPoint>();
	MyPoint currentPoint;

	/**
	 * Constructor for console application
	 * @param pointList
	 */
	public JarvisMarch(ArrayList<MyPoint> pointList) {

		int i,j;
		if (pointList.size() > 0) {
			hullPoints.removeAll(hullPoints);
			hullPoints = (ArrayList<MyPoint>) pointList.clone();
			for (j = 0, i = 1; i < pointList.size(); i++)
				if (hullPoints.get(i).getX() < hullPoints.get(j).getX() || ((hullPoints.get(i).getX() == hullPoints.get(j).getX()) && (hullPoints.get(i).getY() < hullPoints.get(j).getY())))
					j = i;
			hullPoints.add(hullPoints.get(j));
			hullLines.remove(hullLines);
			convexHull.remove(convexHull);

			for (int t = 0; t < pointList.size(); t++) {
				// swap points
				currentPoint = hullPoints.get(j);
				hullPoints.set(j, (MyPoint) hullPoints.get(t));
				hullPoints.set(t, currentPoint);
				hullLines.add(new MyLine((hullPoints.get(t)), (hullPoints.get(t))));
				convexHull.add(new MyPoint(hullPoints.get(t).getName(),	(int)hullPoints.get(t).getX(), (int)hullPoints.get(t).getY()));

				j = t+1;
				for (i = t+2; i <= pointList.size(); i++) {
					boolean control = (hullPoints.get(i)).compare(hullPoints.get(t),hullPoints.get(j));
					if (control)
						j = i;
				}
				if (j == pointList.size()) 
					return;
			}
		}
	}

	/**
	 * Constructor for GUI application
	 * @param g
	 * @param pointList
	 */
	public JarvisMarch(Graphics2D g, ArrayList<MyPoint> pointList) 
	{
		int i,j;
		if (pointList.size() > 0) {
			hullPoints.removeAll(hullPoints);
			hullPoints = (ArrayList<MyPoint>) pointList.clone();
			for (j = 0, i = 1; i < pointList.size(); i++)
				if (hullPoints.get(i).getX() < hullPoints.get(j).getX() || ((hullPoints.get(i).getX() == hullPoints.get(j).getX()) && (hullPoints.get(i).getY() < hullPoints.get(j).getY())))
					j = i;
			hullPoints.add(hullPoints.get(j));
			hullLines.remove(hullLines);
			convexHull.remove(convexHull);

			for (int t = 0; t < pointList.size(); t++) {
				currentPoint = hullPoints.get(j);
				hullPoints.set(j, (MyPoint) hullPoints.get(t));
				hullPoints.set(t, currentPoint);
				hullLines.add(new MyLine((hullPoints.get(t)),(hullPoints.get(t))));
				convexHull.add(new MyPoint(hullPoints.get(t).getName(),	(int)hullPoints.get(t).getX(), (int)hullPoints.get(t).getY()));

				j = t+1;
				for (i = t + 2; i <= pointList.size(); i++) {
					boolean control = (hullPoints.get(i)).compare(hullPoints.get(t),hullPoints.get(j));
					hullPoints.get(i).drawCurrent(g);
					if (control)
						j = i;
					if(i==pointList.size())	{	
						g.setColor(Color.blue);
						for (int k = 0; k < convexHull.size()-1; k++)
							new MyLine(convexHull.get(k),convexHull.get(k+1)).draw(g);
						for (int k = 0; k < checkedLines.size(); k++) 
							checkedLines.elementAt(k).draw(g);
						for (int k = 0; k < convexHull.size(); k++) 
							convexHull.get(k).draw(g,10);
						try {
							Thread.sleep(200);
						} 
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(200);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					checkedLines.removeAllElements();
					checkedLines.addElement(new MyLine(hullPoints.get(t),hullPoints.get(j)));
					g.setColor(Color.gray);
					g.fillRect(0, 0, 800, 800);
					g.setColor(Color.black);
					for (int k = 0; k < pointList.size(); k++)
						pointList.get(k).draw(g, 8);
					g.setColor(Color.red);
					for (int k = 0; k < checkedLines.size(); k++)
						checkedLines.elementAt(k).draw(g);
					g.setColor(Color.blue);
					for (int k = 0; k < convexHull.size()-1; k++)
						new MyLine(convexHull.get(k),convexHull.get(k+1)).draw(g);
					for (int k = 0; k < convexHull.size(); k++) 
						convexHull.get(k).draw(g,10);
					currentPoint.drawCurrent(g);
				}
				if (j == pointList.size()) {
					g.setColor(Color.blue);
					new MyLine(convexHull.get(0),convexHull.get(convexHull.size()-1)).draw(g);
					currentPoint.draw(g,10);
					return;
				}
			}
		}
	}
}
