import java.io.*;
import java.util.*;

/**
 * Class which the operations are handled.
 * @author Rana
 */
public class Execution {
	
	public ArrayList<MyPoint> pointList = new ArrayList<MyPoint>();
	
	void start(String[] args)
	{
		FileOperation fo = new FileOperation();
		MenuOperation mo = new MenuOperation();
		
		if(args.length==2 && args[0].equals("-g")) // run with GUI
		{
			readInput(args[1]);
			mo.init(pointList);
		}
		else if(args.length==4 && args[0].equals("-c"))  // run on console
		{
			readInput(args[2]);
			fo.doOperation(args[1].charAt(1),args[3],pointList);
		}
		else
			System.err.println("Wrong argument format!");
	}
	void readInput(String fileName)
	{
		File file = new File(fileName);
		Scanner sc;
		String line;
		try {
			sc = new Scanner (file);
			// read lines
			while(sc.hasNextLine())
			{				
				line = sc.nextLine();
				StringTokenizer st = new StringTokenizer(line,",");  // tokenize line according to delimiter
				while(st.hasMoreTokens())
				{
					// create new point
					MyPoint point = new MyPoint(st.nextToken(),Integer.valueOf(st.nextToken()).intValue(),Integer.valueOf(st.nextToken()).intValue());
					pointList.add(point); // add point to array list
				}
			}
			sc.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
