import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


public class FileOperation {
	
	// array list for keeping the resulted of the convex hull points
	ArrayList<MyPoint> convexHull = new ArrayList<MyPoint>();
	
	void doOperation(char algType, String outputFile,ArrayList<MyPoint> pointList)
	{
		switch(algType)
		{
		case 'b':
			convexHull = new BruteForce(pointList).hullPoints; 
			break;
		case 'g':
			convexHull = new GrahamScan(pointList).hullPoints;
			break;
		case 'j':
			convexHull = new JarvisMarch(pointList).convexHull;
			break;
		default:
			System.out.println("Wrong type of algorithm!");	
			return;
		}	
		writeResult(outputFile);
	}
	
	/**
	 * method for writing the results to output file
	 * @param outFile
	 */
	void writeResult(String outputFile)
	{
		try {
			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter output = new BufferedWriter(fstream);
			for (int k = 0; k < convexHull.size(); k++) {
				output.write(convexHull.get(k).getName());
				output.newLine();
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
