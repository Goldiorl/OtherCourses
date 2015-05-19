import java.util.ArrayList;
import java.util.Arrays;
import java.io.PrintWriter;

import javax.swing.text.html.HTMLDocument.Iterator;


public class Main {
	public static void main(String[] args){

		int[] ns = {20};
		double[] rs={0,20,30,40,50,60,70,80,90};//9 r values
		
		for(int N : ns){
			double iterationmean[]=new double[9];
			double iterationstdDeviation[]=new double[9];
			long time[]=new long[9];
			int rcounter=0;
			
			
			for(double rtimeshundreds:rs){
				
	        long startTime = System.currentTimeMillis();
	        double stat[]=new double[500];
	        for(int ii = 0; ii < 500; ++ii){
	        	int count1 =1;
				Point2D[] points = new Point2D[N];
				ArrayList<Point2D> list = new ArrayList<Point2D>();
		        for(int i = 0; i < N; i++){
		        	
		        do{
		        	double t = 2 * Math.PI * Math.random();
		    		double r=(Math.floor(Math.random() * (100 - rtimeshundreds+ 1))+rtimeshundreds)/100;
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
		            count1++;
		        }
		        stat[ii]=count1;
	        }
	        long endTime  = System.currentTimeMillis();
	        long totalTime = endTime - startTime;
	        Statistics statClass=new Statistics(stat);
	        double mean=statClass.getMean();
	        double stdDev=statClass.getStdDev();
	        System.out.println("n = " + N + " " + " r value ="+ rtimeshundreds/100+" count=" + mean + " " + " time=" + totalTime+ " Variance= "+stdDev+" in 500 times");
	        iterationmean[rcounter]=mean;
	        iterationstdDeviation[rcounter]=stdDev;
	        time[rcounter++]=totalTime;
			}//r
			
			try
			{
			    PrintWriter pr = new PrintWriter("3Awith"+N+"points");    
			
			        pr.println("Mean");
			        for(int k = 0; k < 9; ++k)
			            pr.print(iterationmean[k]+" ");
			        pr.println();
			        pr.println("standard deviation");
			        for(int k = 0; k < 9; ++k)
			            pr.print(iterationstdDeviation[k]+" ");
			        pr.println();
			        pr.println("time");
			        for(int k = 0; k < 9; ++k)
			            pr.print(time[k]+" ");
			
			    pr.close();
			}
			catch (Exception e)
			{
			    e.printStackTrace();
			    System.out.println("No such file exists.");
			}
			
			
		}// N
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
