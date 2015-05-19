import java.util.ArrayList;
import java.util.Arrays;
import java.io.PrintWriter;

import javax.swing.text.html.HTMLDocument.Iterator;


public class Main {
	public static void main(String[] args){

		int[] ns = {20,50,100,200,500,1000,2000,3000};
		for(int N : ns){
	        double count = 1;
	        
	        long startTime = System.currentTimeMillis();
	        for(int ii = 0; ii < 1; ++ii){
	        	int count1 =1;
				Point2D[] points = new Point2D[N];
				ArrayList<Point2D> list = new ArrayList<Point2D>();
		        for(int i = 0; i < N; i++){
		        	
		        do{
		        	double t = 2 * Math.PI * Math.random();
		    		double r1 = Math.random();
		    		double r2=Math.random();
		    		double r=((r1+r2)>1)? (1-r1-r2):(r1+r2);
		    		points[i] = new Point2D(r*Math.cos(t), r*Math.sin(t));
		        }while(list.contains(points[i]));
		    		list.add(points[i]);
		        }
		        
		        while(points.length >= 3){
		        	GrahamScan gra = new GrahamScan(points);
		            for(Point2D point : gra.hull()){
		            	
		            	list.remove(point);
		            }
		            points = list.toArray(new Point2D[0]);
		            count ++;
		            count1++;
		        }
	        }
	        long endTime  = System.currentTimeMillis();
	        long totalTime = endTime - startTime;
	        System.out.println("n = " + N + " " + "count " + count + " " + "time " + totalTime);
		}
	}
}


class Statistics 
{
  double[] data;
  double size;    

  public Statistics(double[] data) 
  {
      this.data = data;
      size = data.length;
  }   

  double getMean()
  {
      double sum = 0.0;
      for(double a : data)
          sum += a;
          return sum/size;
  }

      double getVariance()
      {
          double mean = getMean();
          double temp = 0;
          for(double a :data)
              temp += (mean-a)*(mean-a);
              return temp/size;
      }

      double getStdDev()
      {
          return Math.sqrt(getVariance());
      }

      public double median() 
      {
             double[] b = new double[data.length];
             System.arraycopy(data, 0, b, 0, b.length);
             Arrays.sort(b);

             if (data.length % 2 == 0) 
             {
                return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
             } 
             else 
             {
                return b[b.length / 2];
             }
      }
}
