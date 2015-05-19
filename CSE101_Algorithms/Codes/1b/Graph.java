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
	public Map<Pair<Integer,Integer>,Edge> searchEdgeMap=new HashMap<Pair<Integer,Integer>,Edge>();
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
		
		public int dfsMst(){
			//System.out.println("Vertices in this graph"+vertexList.length );
			MstVertex vertex=(MstVertex) vertexList[0];
			//System.out.println("Begin from"+vertex.index);
			int dfsIndex=0;
			vertex.isVisted=true;
			dfs[dfsIndex++]=vertex;
			ArrayList<Integer> retValueList=new ArrayList<Integer>();
			int retvalue=0;
			MstVertex nextVertex=(MstVertex) getAdjVertex(vertex);
			while(nextVertex!=null){
				//System.out.println("Indfs MST From "+vertex.index+" To "+nextVertex.index);
				retvalue=exploreMst(nextVertex,vertex.index,dfsIndex);
				retValueList.add(retvalue);
				nextVertex=(MstVertex) getAdjVertex(vertex);
				//if(nextVertex!=null) System.out.println("This "+vertex.index+" has next" +nextVertex.index);
			}
			Collections.sort(retValueList);
			int LargestSize=retValueList.size();
			int diameter=0;
			//System.out.println(" LargestSize" +LargestSize );
			if (LargestSize==0) return 0;
			else if (LargestSize==1) return retValueList.get(0);
			else if (LargestSize>1) return retValueList.get(LargestSize-1)+retValueList.get(LargestSize-2);
			return -1;
			
			
		}
		
		
		public int exploreMst(MstVertex vertex,int sourceindex,int dfsIndex){
			//System.out.println("I'm exploring"+vertex.toString());
			vertex.isVisted=true;
			dfs[dfsIndex++]=vertex;
			MstVertex nextVertex=null;
			if(getAdjVertex(vertex)!=null)
			nextVertex=(MstVertex) getAdjVertex(vertex);
			int returnedlength = 0;
			while(nextVertex!=null){
				//System.out.println("From "+vertex.index+" To "+nextVertex.index);
				returnedlength=exploreMst(nextVertex,vertex.index,dfsIndex);
				vertex.maxlength=Math.max(vertex.maxlength,returnedlength);
				nextVertex=(MstVertex) getAdjVertex(vertex);
			}

			vertex.maxlength+=1;

			return vertex.maxlength;
		}
		
		public void dfs(){
			Vertex vertex;
			int dfsIndex = 0;
			int cc=0;
			while(!allVisited(vertexList)){
				vertex=getNextUnvisited(vertexList);

				dfsIndex=explore(vertex, dfsIndex,cc++);

			}

		}
		
		public int getCC(){
			int ccmax=0;
			for (Vertex i:vertexList)
				if(i.cc > ccmax) ccmax=i.cc;
			
			return ccmax+1;
		}
		
	
	public static void main(String args[]){

		
		
		//-------Run code----------------for deliverable 1
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
				
				for(int nvalueindex=0;nvalueindex<1;nvalueindex++){
					System.out.println("Current Nvertex"+nvalues[nvalueindex]);
					i=nvalues[nvalueindex];
					int pindex=1;
					data[nvalueindex][0]=0; //P=0, averge cost or diameter =0;
					long time[]=new long[50];
					for(double prob=0.02;prob<1;prob+=0.02){
						System.out.println("current prob"+prob);
						long startTime=System.currentTimeMillis();
						double variance[]=new double[500];
						int ccaccum=0;
						double diameteraccum=0;
						double costaccum=0;
						for(int times=0;times<500;times++){
							//create graph
							int ccnumforgraph;
							int diameter=0;
							int cost = 0;
							retgraph=new Graph(i,false);
							for(int j=0;j<i;j++){
								retvertex=new Vertex((char) ((j+1)%128));
								retgraph.addVertex(retvertex);
							}
							//add edges
							for (int m=0;m<i;m++){
								for (int n=m+1;n<i;n++){
									//-------Ëæ»úÊý---TODO-----don't forget to multiply the cost with 100;
									int weight = (int) (Math.floor(Math.random() * (99 - 1 + 1)) + 1);
									//int weight=50;
									if(Math.random()<=prob)
									retgraph.addEdge(m, n, weight);
								}
							}
						
							//run dfs to get CC
							retgraph.dfs();
							ccnumforgraph=retgraph.getCC();
							//System.out.println("ccnumforgraph:"+ccnumforgraph);
							
							//To find MST
							//System.out.println("Debug, to MST");
							Edge edgeCCVector[][]=new Edge[ccnumforgraph][retgraph.MAX_EDGES];
								int edgecounters[]=new int[ccnumforgraph];
								for(Edge e:retgraph.edgeVector) {
									if(e==null) break;
									edgeCCVector[retgraph.vertexList[e.start].cc][edgecounters[retgraph.vertexList[e.start].cc]++]=e;
								}
							for (int index=0;index <ccnumforgraph;index++){
								
								//copy  edgeCCVector[index] to edgesToSort[]
								int edgecounterinvector=0;
								for (Edge e:edgeCCVector[index]) {
									if(e==null) break;
									edgecounterinvector++;
								}
								
								Edge edgesToSort[]=new Edge[edgecounterinvector];
								int filledgecounter=0;
								for (Edge e:edgeCCVector[index]) {
									if(e==null) break;
									edgesToSort[filledgecounter++]=e;
								}

								Arrays.sort(edgesToSort);
								Kruscal kruscal=new Kruscal(edgesToSort);
								kruscal.findMST();

								cost+=kruscal.returnCost();

							}//for ccnumforparagraph
							
							costaccum+=((double)cost)/((double)ccnumforgraph);

						}//times
						
						double result=(((double)costaccum)/((double)trial))/100;
						data[nvalueindex][pindex]=result;


						System.out.println("average cost is"+result);
						long endTime=System.currentTimeMillis();
						long totalTime=endTime-startTime;
						System.out.println("Time used for this prob's iteration is " +totalTime);
						time[pindex]=totalTime;
						pindex++;
					}//prob
					try
			        {
			            PrintWriter pr = new PrintWriter(nvalueindex+"file_averagecost");    

			                pr.println("Average");
			                for(int k = 0; k < 50; ++k)
			                    pr.print(data[nvalueindex][k]+" ");
			                pr.println();
			                //pr.println("deviation");
			                //for(int k = 0; k < 50; ++k)
			                //    pr.print(data_variance[nvalueindex][k]+" ");
			                //pr.println();
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
				
			
	}//main
	


}//class


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
		return "Vertex "+label+",Index number£º"+index+".";
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




class Pair<FIRST, SECOND> implements Comparable<Pair<FIRST, SECOND>> {

    public final FIRST first;
    public final SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public static <FIRST, SECOND> Pair<FIRST, SECOND> of(FIRST first,
            SECOND second) {
        return new Pair<FIRST, SECOND>(first, second);
    }

    @Override
    public int compareTo(Pair<FIRST, SECOND> o) {
        int cmp = compare(first, o.first);
        return cmp == 0 ? compare(second, o.second) : cmp;
    }

    // todo move this to a helper class.
    private static int compare(Object o1, Object o2) {
        return o1 == null ? o2 == null ? 0 : -1 : o2 == null ? +1
                : ((Comparable) o1).compareTo(o2);
    }

    @Override
    public int hashCode() {
        return 31 * hashcode(first) + hashcode(second);
    }

    // todo move this to a helper class.
    private static int hashcode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair))
            return false;
        if (this == obj)
            return true;
        return equal(first, ((Pair) obj).first)
                && equal(second, ((Pair) obj).second);
    }

    // todo move this to a helper class.
    private boolean equal(Object o1, Object o2) {
        return o1 == null ? o2 == null : (o1 == o2 || o1.equals(o2));
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
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

		for(Edge e:mst){
			if(e!=null)
			System.out.println("This edge is from"+e.start+" to "+e.end+" with weight:"+e.weight);
		}
	}
	public int returnCost(){
		if(hasRun!=1) return -1;
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
	
	public int getDiameter(){
		Graph mstGraph=new Graph((treecounter+1));
		Set<Integer> indexset=new HashSet<Integer>();
		Map<Integer,Integer> indexmap=new HashMap<Integer,Integer>();
		//add edges and vertices
		for (Edge e:mst){
			if(e==null) break;

			if(!indexmap.containsKey(e.start)) {
				MstVertex mstVertex=new MstVertex((char) (e.start %128));
				mstGraph.addVertex(mstVertex);

				indexmap.put(e.start,mstVertex.index);
			}
			
			if(!indexmap.containsKey(e.end)) {
				MstVertex mstVertex=new MstVertex((char) (e.end %128));
				mstGraph.addVertex(mstVertex);

				indexmap.put(e.end,mstVertex.index);
			}
			if(e!=null){
				//System.out.println("Adding edge"+e.toString());
				mstGraph.addEdge(indexmap.get(e.start),indexmap.get(e.end),e.weight);
			}
			mstGraph.searchEdgeMap.put((new Pair<Integer,Integer>(Math.min(indexmap.get(e.start),indexmap.get(e.end)),Math.max(indexmap.get(e.start), indexmap.get(e.end)))),e);

			
		}
		

		return mstGraph.dfsMst();
		
	}
}

class MstVertex extends Vertex{
	public int maxlength=0;
	public MstVertex(char lab) {
		super(lab);
	}
}


