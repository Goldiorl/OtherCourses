import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

public class GrahamScan {

	Vector<MyLine> checkedLines = new Vector<MyLine>();
	ArrayList<MyPoint> hullPoints = new ArrayList<MyPoint>();
	ArrayList<MyLine> hullLines = new ArrayList<MyLine>();
	Stack<MyPoint> convexHull = new Stack<MyPoint>();
	MyPoint currentPoint;

	/**
	 * Constructor for console application
	 * @param pointList
	 */
	public GrahamScan(ArrayList<MyPoint> pointList) {

		// find the point with the minimum Y coordinate
		int minY = 0;
		for (int i = 0; i < pointList.size(); i++) {
			if (pointList.get(i).getY() < pointList.get(minY).getY())
				minY = i;
			else if (pointList.get(i).getY() == pointList.get(minY).getY())
				if (pointList.get(i).getX() < pointList.get(minY).getX())
					minY = i;
		}
		
		// create lines between minimum y point and the other ones
		for (int i = 0; i < pointList.size(); i++) {
			if (i == minY)
				continue;
			MyLine temp = new MyLine(pointList.get(minY), pointList.get(i));
			hullLines.add(temp);
		}
		
		// Sort the lines according to their angle and distances
		Sort(hullLines);
		
		// push the sorted array list to the stack and control three point each time
		// if they generates a convex hull border, push them to convex hull stack
		convexHull.push(pointList.get(minY));
		convexHull.push(hullLines.get(0).point2);

		MyPoint first, second, third;
		for (int i = 1; i < hullLines.size(); i++) {
			second = convexHull.pop();
			first = convexHull.pop();
			
			third = hullLines.get(i).point2;
			int direction = crossProduct(first, second, third);
			convexHull.push(first);
			convexHull.push(second);
			if (direction > 0) {   // the three points constitute a left turn
				convexHull.push(third);
			} else if (direction < 0) { // the three points constitute a right turn
				while (direction <= 0 && convexHull.size() > 2) {
					convexHull.pop();
					second = convexHull.pop();
					first = convexHull.pop();
					direction = crossProduct(first, second, third);
					convexHull.push(first);
					convexHull.push(second);
				}
				convexHull.push(third);
			} else // they are collinear
			{  
				convexHull.pop();
				convexHull.push(third);
			}
		}
		hullPoints.clear();
		for (int i = 0; i < convexHull.size(); i++) {
			hullPoints.add(convexHull.elementAt(i));
		}
	}
	
	/**
	 * Constructor for GUI application
	 * @param g
	 * @param pointList
	 */
	public GrahamScan(Graphics2D g, ArrayList<MyPoint> pointList) {

		// draw the background and set the points
		g.setColor(Color.gray);
		g.fillRect(0, 0, 800, 800);
		g.setColor(Color.black);
		for (int i = 0; i < pointList.size(); i++)
			pointList.get(i).draw(g, 8);
		
		// find the point with the minimum Y coordinate
		int minY = 0;
		for (int i = 0; i < pointList.size(); i++) {
			if (pointList.get(i).getY() < pointList.get(minY).getY())
				minY = i;
			else if (pointList.get(i).getY() == pointList.get(minY).getY())
				if (pointList.get(i).getX() < pointList.get(minY).getX())
					minY = i;
		}
		
		// create lines between minimum y point and the other ones
		for (int i = 0; i < pointList.size(); i++) {
			if (i == minY)
				continue;
			MyLine temp = new MyLine(pointList.get(minY), pointList.get(i));
			hullLines.add(temp);
		}
		
		// Sort the lines according to their angle and distances
		Sort(hullLines);
		
		// push the sorted array list to the stack and control three point each time
		// if they generates a convex hull border, push them to convex hull stack	
		convexHull.push(pointList.get(minY));
		convexHull.push(hullLines.get(0).point2);

		MyPoint first, second, third;
		for (int j = 1; j < hullLines.size(); j++)
		{			
			second = convexHull.pop();
			first = convexHull.pop();			
			third = hullLines.get(j).point2;
			
			try{
				Thread.sleep(500);
			}
			catch(Exception e){
				e.getLocalizedMessage();
			}
			// draw each compared points in red
			g.setColor(Color.red);
			first.drawCurrent(g);
			second.drawCurrent(g);
			third.drawCurrent(g);
			g.setColor(Color.green);
			new MyLine(second,third).draw(g);
			new MyLine(first,second).draw(g);
			
			// calculate the direction according to cross product of three points
			int direction = crossProduct(first, second, third);
			convexHull.push(first);
			convexHull.push(second);
			if (direction > 0) {  // the three points constitute a left turn
				g.setColor(Color.green);
				new MyLine(first,second).draw(g);
				try{
					Thread.sleep(500);
				}
				catch(Exception e){
					e.getMessage();
				}
				convexHull.push(third);
			}
			else if (direction < 0) { // the three points constitute a right turn
				while (direction <= 0 && convexHull.size() > 2) {
					convexHull.pop();
					g.setColor(Color.gray);
					g.fillRect(0, 0, 800, 800);
					g.setColor(Color.black);
					for (int i = 0; i < pointList.size(); i++)
						pointList.get(i).draw(g, 8);
					g.setColor(Color.blue);
					for (int i = 0; i < convexHull.size(); i++)
						convexHull.elementAt(i).draw(g, 8);
					for (int i = 0; i < convexHull.size()-1; i++)
						new MyLine(convexHull.elementAt(i),convexHull.elementAt(i+1)).draw(g);
				
					// take the two points at top and control the turning direction again
					second = convexHull.pop();
					first = convexHull.pop();
					direction = crossProduct(first, second, third);
					convexHull.push(first);
					convexHull.push(second);
					g.setColor(Color.red);
					new MyLine(first,second).draw(g);
					try{
						Thread.sleep(500);
					}
					catch(Exception e){
						e.getMessage();
					}
				}
				g.setColor(Color.red);
				new MyLine(second,third).draw(g);
				try{
					Thread.sleep(500);
				}
				catch(Exception e){
					e.getMessage();
				}
				convexHull.push(third);
			} 
			else {  // points are collinear
				
				g.setColor(Color.gray);
				g.fillRect(0, 0, 800, 800);
				g.setColor(Color.black);
				for (int i = 0; i < pointList.size(); i++)
					pointList.get(i).draw(g, 8);
				g.setColor(Color.blue);
				for (int i = 0; i < convexHull.size(); i++)
					convexHull.elementAt(i).draw(g, 8);
				for (int i = 0; i < convexHull.size()-1; i++)
					new MyLine(convexHull.elementAt(i),convexHull.elementAt(i+1)).draw(g);
				
				g.setColor(Color.red);
				new MyLine(first,third).draw(g);
				try{
					Thread.sleep(500);
				}
				catch(Exception e){
					e.getMessage();
				}
				convexHull.pop();
				convexHull.push(third);
			}
			
			g.setColor(Color.gray);
			g.fillRect(0, 0, 800, 800);
			g.setColor(Color.black);
			for (int i = 0; i < pointList.size(); i++)
				pointList.get(i).draw(g, 8);
			g.setColor(Color.blue);
			for (int i = 0; i < convexHull.size(); i++)
				convexHull.elementAt(i).draw(g, 8);
			for (int i = 0; i < convexHull.size()-1; i++)
				new MyLine(convexHull.elementAt(i),convexHull.elementAt(i+1)).draw(g);
						
		}
		g.setColor(Color.blue);
		new MyLine(convexHull.lastElement(),convexHull.firstElement()).draw(g);
	}
	
	public int crossProduct(MyPoint p1, MyPoint p2, MyPoint p3)
    {
		return (int) (((p2.getX() - p1.getX())*(p3.getY() - p1.getY())) - ((p2.getY() - p1.getY())*(p3.getX() - p1.getX())));
    }

	public void Sort(ArrayList<MyLine> hullLines2)
	{
		for (int i = (hullLines2.size() / 2) - 1; i >= 0; i--)
			heapSortPart(hullLines2, i, hullLines2.size());
		for (int i = hullLines2.size() - 1; i >= 1; i--) {
			swap(hullLines2,i,0);
			heapSortPart(hullLines2, 0, i);
		}
	}
	
	public void swap(ArrayList<MyLine> hullLines2,int i,int j)
	{
		MyLine line = hullLines2.get(j);
		hullLines2.set(j, hullLines2.get(i));
		hullLines2.set(i, line);
	}

	public void heapSortPart(ArrayList<MyLine> hullLines2, int start, int n) {

		while (true) {
			int t = start;
			int first = 2 * t + 1;
			int second = 2 * t + 2;
			if (first < n) {

				if (hullLines2.get(start).polarAngle < hullLines2.get(first).polarAngle)
					start = first;
				else if (hullLines2.get(start).polarAngle == hullLines2.get(first).polarAngle) {
					if (hullLines2.get(start).distance < hullLines2.get(first).distance)
						start = first;
				}
			}
			if (second < n) {
				if (hullLines2.get(start).polarAngle < hullLines2.get(second).polarAngle)
					start = second;
				else if (hullLines2.get(start).polarAngle == hullLines2.get(second).polarAngle) {
					if (hullLines2.get(start).distance < hullLines2.get(second).distance)
						start = second;
				}
			}
			if (start != t) {
				swap(hullLines2,start,t);
			} 
			else
				break;
		}
	}
}
