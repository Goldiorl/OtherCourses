package cse;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;




public class Graph {
	public Stack stackX;
	public int graphcccounter;
	public int MAX_VERTS = 2000; // vertices number
	public int MAX_EDGES=1000000;
	public Vertex vertexList[];
	public boolean is = false;// is directed?
	public int nVerts = 0;
	public Vertex dfs[];
	public Edge[] edgeVector=null;
	public int edgeCounter=0;
	
	//Constructor
	
	public Graph(){
		vertexList = new Vertex[MAX_VERTS];
		edgeVector=new Edge[MAX_EDGES];
		dfs = new Vertex[MAX_VERTS];
	}
	public Graph(int n){
		vertexList = new Vertex[n];
		edgeVector=new Edge[MAX_EDGES];
		dfs = new Vertex[n];
		
	}
	public Graph(int n, boolean is){
		this.is = is;
		vertexList = new Vertex[n];
		edgeVector=new Edge[MAX_EDGES];
		dfs = new Vertex[n];
	}
	
	
	
	//Some getter, setter,displayer
	
	public Vertex[] getVertexList() {
		return vertexList;
	}
	public Vertex[] getDfs() {
		return dfs;
	}
	
	public void displayGraph(){
		ArrayList<Vertex> next = null;
		for (int i = 0; i < vertexList.length; i++) {
			printVertx(vertexList[i]);
		}
	}
	public void printVertx(Vertex vertex){
		ArrayList<Vertex> next = vertex.getAdj();
		if(next == null){ System.out.println(vertex.toString()+" No adjacent vertex");}
		else{
			System.out.print(vertex.toString()+" Has adjacent vertex(s):");
			for (int i = 0; i < next.size(); i++) {
				System.out.print("Vertex "+next.get(i).label+", ");
			}
			System.out.println();
		}
	}
	
	
	
	//vertices operations ------------Global-----------
	
	//add vertices
	public void addVertex(Vertex vertex){
		vertex.setIndex(nVerts);
		vertexList[nVerts] = vertex;
		nVerts++;
	}
	
	//add Edge
	public void addEdge(int start, int end,int weight){
		vertexList[start].addAdj(vertexList[end]);
		if (!is) {vertexList[end].addAdj(vertexList[start]);}
		Edge edge=new Edge(start,end,weight);
		edgeVector[edgeCounter++]=edge;
	}	
	
	//return number of vertices
	public int getVertsCount(){
		return vertexList.length;
	}
	
	
	//return next unvisited adjacent vertex of a certain vertex
	public Vertex getAdjVertex(Vertex vertex){	
		ArrayList<Vertex> adjVertexs = vertex.getAdj();
		if(adjVertexs!=null)
		for (int i = 0; i < adjVertexs.size(); i++) {
			if(!adjVertexs.get(i).isVisted){
				return adjVertexs.get(i);
			}
		}
		return null;
	}	
	
	
	//----------iterator -------
	
	public Iterator dfsIterator(){
		dfs();
		return new DfsIterator();
	}
	
	private abstract class GraphIterator implements Iterator{
		
		int count = 0;
		public GraphIterator(){
		}
		public boolean hasNext() {
			return count != getVertsCount();
		}
		public Object next() {		
			// TODO Auto-generated method stub
			return null;
		}
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	//深度优先迭代
	private class DfsIterator extends GraphIterator{
		public DfsIterator(){
			super();
		}
	
		public Vertex next() {		
			return dfs[count++];
		}
	}	
	
	//-------dfs-----------
		//--opeartions
		boolean allVisited(Vertex vertexlist[]){
			boolean flag =true;
			for (Vertex v: vertexlist){
				if(!v.isVisted) flag=false;
			}
			return flag;
		}
		
		Vertex getNextUnvisited(Vertex vertexlist[]){
			Vertex retv = null;
			if(!allVisited(vertexlist)){
				for (Vertex v :vertexlist){
					if(!v.isVisted) {retv=v;break;}
				}
			}
			
			return retv;
		}
		
		int explore(Vertex vertex, int dfsIndex, int cc){
			stackX = new Stack();
			vertex.isVisted = true;
			vertex.cc=cc;
			dfs[dfsIndex]=vertex;
			stackX.push(vertex);
			while(!stackX.isEmpty()){
				vertex = getAdjVertex((Vertex)stackX.peek());
				if(vertex == null){
					stackX.pop();
				}else{
					vertex.isVisted = true;
					//System.out.println("This vertices is :"+vertex.index+"and its cc is :"+cc);
					vertex.cc=cc;
					dfs[++dfsIndex]=vertex;
					stackX.push(vertex);
				}
			}
			return dfsIndex;
		}
		
		public void dfs(){
			Vertex vertex;
			int dfsIndex = 0;
			int cc=0;
			while(!allVisited(vertexList)){
				vertex=getNextUnvisited(vertexList);
				//System.out.println("The cc is going to be:"+cc);
				dfsIndex=explore(vertex, dfsIndex,cc++);
				//System.out.println("The cc has been updated to be:"+cc);
			}

		}
		
		public int getCC(){
			int ccmax=0;
			for (Vertex i:vertexList)
				if(i.cc > ccmax) ccmax=i.cc;
			
			return ccmax+1;
		}
		
	
	public static void main(String args[]){

		//-------Run code-------
		Graph retgraph=null;
		Vertex retvertex=null;
		double data[][] = new double[4][50];
		double data_variance[][]=new double[4][50];
		int nvalues[]={20,100,500,1000};
		double parray[]=new double[50];
		int trial=500;
		int nVerts=20;
		int ccnumindex=0;
		int i=nVerts;
		
		for(int nvalueindex=0;nvalueindex<3;nvalueindex++){
			System.out.println("Current Nvertex"+nvalues[nvalueindex]);
			i=nvalues[nvalueindex];
			int pindex=0;
			long time[]=new long[50];
			for(double prob=0;prob<1;prob+=0.02){
				long startTime=System.currentTimeMillis();
				double variance[]=new double[500];
				int ccaccum=0;
				for(int times=0;times<500;times++){
					//create graph
					int ccnumforgraph;
					retgraph=new Graph(i,false);
					for(int j=0;j<i;j++){
						retvertex=new Vertex((char) ((j+1)%128));
						retgraph.addVertex(retvertex);
					}
					//add edges
					for (int m=0;m<i;m++){
						for (int n=m+1;n<i;n++){
							//-------TODO-----don't forget to multiply the cost with 100;
							int weight = 1;
							if(Math.random()<=prob)
							retgraph.addEdge(m, n, weight);
						}
					}
				
					//run dfs to get CC
					retgraph.dfs();
					ccnumforgraph=retgraph.getCC();
	
					
					ccaccum+=ccnumforgraph;
					variance[times]=ccnumforgraph;
					//System.out.print("Connected component is:"+ccnumforgraph+" ");
		
				}//times
				
				Statistics stat=new Statistics(variance);
				double result=((double)ccaccum)/((double)trial);
				data[nvalueindex][pindex]=result;
				System.out.println(stat.getMean());
				data_variance[nvalueindex][pindex]=stat.getStdDev();
				

				long endTime=System.currentTimeMillis();
				long totalTime=endTime-startTime;
				time[pindex]=totalTime;
				pindex++;
			}//prob
			try
	        {
	            PrintWriter pr = new PrintWriter(nvalueindex+"file");    

	                pr.println("Mean");
	                for(int k = 0; k < 50; ++k)
	                    pr.print(data[nvalueindex][k]+" ");
	                pr.println();
	                pr.println("deviation");
	                for(int k = 0; k < 50; ++k)
	                    pr.print(data_variance[nvalueindex][k]+" ");
	                pr.println();
	                pr.println("time");
	                for(int k = 0; k < 50; ++k)
	                    pr.print(time[k]+" ");

	            pr.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            System.out.println("No such file exists.");
	        }
		}//nvalues

	}
	


}


//Vertex classes
class Vertex{
	public char label; 
	public boolean isVisted;
	public int index;
	public int cc;
	private ArrayList<Vertex> next = null;
	public Vertex(char lab)  // constructor
	{	
		label = lab;
		isVisted = false;
	}
	//add adjacent nodes
	public void addAdj(Vertex ver){
		if(next == null) next = new ArrayList<Vertex>();
		next.add(ver);
	}
	
	public ArrayList<Vertex> getAdj(){
		return next;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public String toString(){
		return "Vertex "+label+",Index number："+index+".";
	}
}


class Edge implements Comparable
{
	public int start;
	public  int end;
	public int weight;
	public int cc;
	
	public Edge(int start,int end,int weight)
	{
		this.start=start;
		this.end=end;
		this.weight=weight;
	}
	
	public int compareTo(Object e)
	{
		return weight-((Edge)e).weight;
	}
	
	public String toString()
	{
		return "("+start+","+end+")";
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


class Kruscal{
	public Edge edge[]=null;
	public Edge mst[]=null;
	public int hasRun=0;
	int treecounter=0;
	public Kruscal(Edge edge[]){
		this.edge=edge;
		mst=new Edge[edge.length];
	}
	
	public void findMST(){
		int N=2000000;
		
		UF uf=new UF(N);
		
		for(Edge e:edge){
			if(!uf.connected(e.start, e.end)) {
				uf.union(e.start,e.end);
				mst[treecounter++]=e;
			}
		}
		hasRun=1;
	}
	
	public void printMST(){
		System.out.println("hasRun value:"+hasRun);
		for(Edge e:mst){
			if(e!=null)
			System.out.println("This edge is from"+e.start+" to "+e.end+" with weight:"+e.weight);
		}
	}
	public int returnCost(){
		int cost=0;
		for(Edge e:mst) {
			if(e==null) break;
			cost+=e.weight;
		}
		return cost;
	}
	public double returnAverageCost(){
		if(hasRun!=1) return -1;
		int cost=0;
		for(Edge e:mst) {
			if(e==null) break;
			cost+=e.weight;
		}
		return ((double)cost/treecounter);
	}
	public int returnTreeSize(){
		return ((hasRun==1)? treecounter: -1);
	}
}

