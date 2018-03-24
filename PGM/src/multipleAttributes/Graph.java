package multipleAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.ArrayList;


//无向图(邻接矩阵构成，行列以1开始)
public class Graph
{
	private VertexNode[] verticesList = null;
	private int[][] edge = null;
	private int numVertices = 0;
	
	private int v[]=null;
	private int setc[]=null;
	private int vis[]=null;//是否访问过,1代表访问过，用于判断是否是弦图

	
	private int[] visited;// 节点状态,值为0的是未访问的,用于求所有环时
	private ArrayList<Integer> singleTrace = new ArrayList<Integer>();// 从出发节点到当前节点的轨迹
	private boolean hasLoop;
	private ArrayList<ArrayList<Integer>> loops=null;
	private ArrayList<HashSet<Integer>> loopSet=null;
	
	private ArrayList<HashSet<Integer>> maxCliques=null;//所有极大团的队列
	
//	private HashSet<Integer> currentClique=null;
//	private int maxNumOfClique;
//	private int numOfClique;
//	private HashSet<Integer> visitedSet=null;
	
	
	//构造函数
	public Graph(int size, VertexNode[] attributes)
	{
		this.numVertices = size;
		this.verticesList = attributes;
		edge = new int[numVertices+1][numVertices+1];
		// verticesList = new VertexNode[numVertices];

		// 矩阵初始化
		for (int i = 1; i <= numVertices; i++)
		{
			for (int j = 1; j <= numVertices; j++)
			{
				// 0代表无关联，1代表有关联
				edge[i][j] = 0;
			}
		}
		
		v=new int[this.numVertices+1];
		setc=new int[this.numVertices+1];
		vis=new int[this.numVertices+1];
		
		visited = new int[numVertices+1];
		Arrays.fill(visited, 0);
		this.hasLoop=false;
		this.loops=new ArrayList<ArrayList<Integer>>();
		this.loopSet=new ArrayList<HashSet<Integer>>();
	}

	/*
	 * Step1 
	 * Step2.1判断是否是弦图，利用最大势MCS算法
	 */
	// 给定属性名将其关联
	public void addEdges(ArrayList<String> constraintAttributes)
	{
		VertexNode temp=new VertexNode();
		int tempi=0,tempj=0;
		for (int i = 0; i < constraintAttributes.size() - 1; i++)
		{
			for (int j = i + 1; j < constraintAttributes.size(); j++)
			{
				temp.setAttributeName(constraintAttributes.get(i));
				tempi=this.getVertexPos(temp);
				temp.setAttributeName(constraintAttributes.get(j));
				tempj=this.getVertexPos(temp);
				
				if(!this.isConnected(tempi, tempj))
				{
					this.setEdge(tempi, tempj);
				}
			}
		}
	}
	
	//设置两点关联
	void setEdge(int v1,int v2)
	{
		edge[v1][v2] = edge[v2][v1]=1;
	}

	// 给出顶点vertex在图中的位置
	public int getVertexPos(VertexNode vertex)
	{

		for (int i = 0; i < numVertices; i++)
		{
			if (verticesList[i].getAttributeName().equals(vertex.getAttributeName()))
			{
				return i+1;
			}
		}
		return -1;
	}

	// 取顶点i，不合理返回null
	public VertexNode getValue(int i)
	{
		if (i >= 0 && i <= numVertices)
		{
			return verticesList[i-1];
		} else
		{
			return null;
		}

	}

	// 判断两点之间是否有关联，有关联返回true
	boolean isConnected(int v1, int v2)
	{
		if (edge[v1][v2] == 1 && edge[v2][v1] == 1)
		{
			return true;
		} else
		{
			return false;
		}

	}
	
	//输出邻接矩阵边情况，用于单元测试
	public void print()
	{
		for(int i=1;i<=this.numVertices;i++)
		{
			for(int j=1;j<=this.numVertices;j++)
			{
				System.out.print(edge[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	//
	public void setMaxClique(ArrayList<HashSet<Integer>> maxCliques)
	{
		this.maxCliques = maxCliques;
	}
	
	//构建消除序列(最大势算法)
	void MCS()
	{
		//初始化
		for (int i = 0; i <= this.numVertices; i++)
		{
			v[i] = -1;
			vis[i] = 0;
		}
		v[this.numVertices] = 1;
		vis[1] = 1;

		//从n到1依次给点标号
		for (int i = this.numVertices - 1; i >= 1; i--)
		{
			int id = -1, nmax = -1;
			for (int j = 2; j <= this.numVertices; j++)
			{
				//未访问过
				if (vis[j]==0)
				{
					//num是与已标号的点相邻的数量
					int num = 0;
					for (int k = this.numVertices; k > i; k--)
					{
						if (edge[j][v[k]]==1)
							num++;
					}
					//选择最大的标号
					if (num > nmax)
					{
						nmax = num;
						id = j;
					}
				}
			}
			v[i] = id;
			vis[id] = 1;
		}
	}
	
	//判断是否此图为弦图
	public boolean isGraphChordal()
	{
		//求出消除序列
		MCS();

		//判断是否为完美消除序列
		for (int i = 1; i <= this.numVertices; i++)
		{
			//存Vi相邻点的个数
			int counter = 0;

			//求出于Vi相邻的的点
			for (int j = i + 1; j <= this.numVertices; j++)
			{
				if (edge[v[i]][v[j]]==1)
					setc[counter++] = v[j];
			}

			//判断Vj1是否与Vj2.....Vjk相邻，不相邻则不是完美消除序列
			for (int j = 1; j < counter; j++)
			{
				if (edge[setc[0]][setc[j]]==0)
					return false;
			}
		}
		return true;
	}
	
	
	
	/*
	 * Step2.2.1求出图中所有环
	 */
	// 遍历所有点，求得所有的环
	public void findLoops()
	{
		for (int i = 1; i <= this.numVertices; i++)
		{
			cleanVisited();
			singleLoop(i);
		}
	}

	// 判断某个环是否已在环库中.存在则返回true
	boolean isLoopExist(HashSet<Integer> input)
	{
		for (int i = 0; i < loopSet.size(); i++)
		{
			if (loopSet.get(i).equals(input))
			{
				return true;
			}
		}

		return false;
	}

	// 清除visited状态
	void cleanVisited()
	{
		Arrays.fill(visited, 0);
	}

	// 以某个点出发求得所有的环,递归DFS
	void singleLoop(int v)
	{
		if (visited[v] == 1)
		{
			int j;
			if ((j = singleTrace.indexOf(v)) != -1 && singleTrace.size() - j > 2)
			{
				hasLoop = true;
				// System.out.print("Cycle:");
				ArrayList<Integer> tempVec = new ArrayList<Integer>();
				HashSet<Integer> tempSet = new HashSet<Integer>();
				while (j < singleTrace.size())
				{
					// 记录一条环
					tempVec.add(singleTrace.get(j));
					tempSet.add(singleTrace.get(j));
					// System.out.print(singleTrace.get(j) + " ");
					j++;
				}

				// 判断此环是否已存在，否则添加入环库
				if (!isLoopExist(tempSet))
				{
					loopSet.add(tempSet);
					loops.add(tempVec);
				}

				// System.out.print("\n");
				return;
			}
			return;
		}
		visited[v] = 1;
		singleTrace.add(v);

		for (int i = 1; i <= this.numVertices; i++)
		{
			if (edge[v][i] == 1)
			{
				singleLoop(i);
			}
		}
		singleTrace.remove(singleTrace.size() - 1);
	}

	boolean getHasLoop()
	{
		return hasLoop;
	}
	
	
	
	/*
	 * Step2.2.2判断环是否有弦
	 */
	//返回没有弦的环在loops中的编号
	ArrayList<Integer> isLoopChordal()
	{
		LoopSetComparator loopSetComparator=new LoopSetComparator();
		Collections.sort(this.loopSet, loopSetComparator);
		ArrayList<Integer> unchordalVec=new ArrayList<Integer>();
		LoopComparator loopComparator = new LoopComparator();
		Collections.sort(this.loops,loopComparator);
		boolean isChordal=false;
		
		
		//从后往前求每个环（边数大于3）的set是否有子集，有则有弦，没有则没有弦
		for(int i=this.loopSet.size()-1;i>=0;i--)
		{
			isChordal=false;
			if(this.loopSet.get(i).size()<4)
			{
				break;
			}
			for(int j=0;j<i;j++)
			{
				if(this.loopSet.get(i).containsAll(this.loopSet.get(j)))
				{
					//此环有弦
					isChordal=true;
					break;
				}
			}
			if(!isChordal)
			{
				//将无弦的环的编号记录下来
				unchordalVec.add(i);
			}
		}
		return unchordalVec;
	}
	
	
	
	/*
	 * Step2.2.3给没有弦的环加上弦
	 */
	void addChordToLoop(ArrayList<Integer> input)
	{
		int id;
		ArrayList<Integer> temp=null;
		for(int i=0;i<input.size();i++)
		{
			id=input.get(i);
			temp=this.loops.get(id);
			this.setEdge(temp.get(1), temp.get(temp.size()-1));
		}
	}
	
	//整个Step2的整合
	public void convertToChordal()
	{
		ArrayList<Integer> temp=null;
		while(!this.isGraphChordal())
		{
			this.findLoops();
			temp=this.isLoopChordal();
			this.addChordToLoop(temp);
		}
	}
	
	
	
//	/**
//	 * Step3找出弦图中的所有最大团
//	 */
//	public void findMaxClique()
//	{
//		maxNumOfClique=0;
//		numOfClique=0;
//		visitedSet=new HashSet<Integer>();
//		initVisitedSet();
//		currentClique=new HashSet<Integer>();
//		maxCliques=new ArrayList<HashSet<Integer>>();
//		
//		for(int i=1;i<=this.numVertices;i++)
//		{
//			//this.initVisitedSet();
//			currentClique.clear();
//			numOfClique=0;
//			DFS(i);
//			
//		}
//	}
//	
//	/**
//	 * 深度优先搜索求最大团
//	 * @param nodeID
//	 */
//	void DFS(int nodeID)
//	{
//		if(isAvailable(nodeID))
//		{
//			//将此点加入现有团
//			currentClique.add(nodeID);
//			numOfClique++;
//			
//			
//			HashSet<Integer> result = new HashSet<Integer>();
//			result.addAll(visitedSet);
//			result.removeAll(currentClique);
//			
//			//到叶子节点
//			if(result.size()==0)
//			{
//				//将求得的团保存
//				maxNumOfClique=numVertices;
//				//maxCliques.add(new HashSet<Integer>(currentClique));
//				addToMaxCliques();
//				return;
//			}
//			
//			
//			//迭代进入下一点
//			Iterator<Integer> it = result.iterator();
//			while(it.hasNext())
//			{
//				DFS(it.next());
//			}
//			
//			if(numOfClique>=maxNumOfClique)
//			{
//				//更新最大团
//				maxNumOfClique=numOfClique;
//				//maxCliques.add(new HashSet<Integer>(currentClique));
//				addToMaxCliques();
//			}
//			
//			//移除此点
//			numOfClique--;
//			currentClique.remove(nodeID);
//		}
//		return;
//	}
//	
//	/**
//	 * 判断此顶点与现有团是否符合，一有未关联的点即返回false
//	 * @param nodeID
//	 * @return
//	 */
//	boolean isAvailable(int nodeID)
//	{
//		
//		for(Iterator<Integer> it=this.currentClique.iterator();it.hasNext();)
//		{
//			if(this.edge[nodeID][it.next()]==0)
//			{
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * 初始化访问集
//	 */
//	void initVisitedSet()
//	{
//		this.visitedSet.clear();
//		for(int i=1;i<=this.numVertices;i++)
//		{
//			this.visitedSet.add(i);
//		}
//	}
//	
//	/**
//	 * 将最大团加入最大团集maxCliques
//	 */
//	void addToMaxCliques()
//	{
//		//1.判断此时的currentClique是否已存在在maxCliques中
//		for(int i=0;i<maxCliques.size();i++)
//		{
//			if(maxCliques.get(i).equals(currentClique))
//			{
//				return;
//			}
//		}
//		
//		//2.集中没有则加入
//		maxCliques.add(new HashSet<Integer>(currentClique));
//	}
	
	public ArrayList<HashSet<Integer>> getMaxCliques()
	{
		return maxCliques;
	}


	/**
	 * 利用Mathematica求出所有的极大团
	 */
	public void findMaximalCliques()
	{
		maxCliques = new ArrayList<HashSet<Integer>>();
		StringBuilder input = new StringBuilder("g=Graph[{");
		
		//组装顶点的语句
		for(int i=1;i<=numVertices;i++)
		{
			input.append(i);
			input.append(',');
		}
		input.deleteCharAt(input.length()-1);
		input.append("},{");
		
		//组装边的语句
		for(int i=1;i<=numVertices;i++)
		{
			for(int j=i+1;j<=numVertices;j++)
			{
				if(edge[i][j]==1)
				{
					input.append(i);
					input.append("<->");
					input.append(j);
					input.append(',');
				}
			}
		}
		input.deleteCharAt(input.length()-1);
		
		input.append("}];FindClique[g,Infinity,All]");
		CalculateLink ccl = new CalculateLink();
		StringBuilder result = new StringBuilder(ccl.findCliques(input.toString()));
		ccl.closeLink();
		
		//处理结果
		result.delete(0, 2);
		result.delete(result.length()-2, result.length());
		String[] cliqueString = result.toString().split("\\}, \\{");
		String[] vertexs = null;
		HashSet<Integer> eachClique = null;
		for(String each:cliqueString)
		{
			//each = each.substring(1, each.length()-1);
			vertexs = each.split(", ");
			eachClique = new HashSet<Integer>();
			for(String single:vertexs)
			{
				eachClique.add(Integer.parseInt(single));
			}
			maxCliques.add(eachClique);
		}
	}
	
}






//用于对loopSet排序，根据set的大小升序排
class LoopSetComparator implements Comparator<Object>
{

	@Override
	public int compare(Object o1, Object o2)
	{
		@SuppressWarnings("unchecked")
		HashSet<Integer> temp1=(HashSet<Integer>)o1;
		@SuppressWarnings("unchecked")
		HashSet<Integer> temp2=(HashSet<Integer>)o2;
		
		if(temp1.size()>temp2.size())
		{
			return 1;
		}
		else if(temp1.size()==temp2.size())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
}

//用于loops排序，根据每个loop的大小升序排
class LoopComparator implements Comparator<Object>
{

	@Override
	public int compare(Object o1, Object o2)
	{
		@SuppressWarnings("unchecked")
		ArrayList<Integer> temp1=(ArrayList<Integer>)o1;
		@SuppressWarnings("unchecked")
		ArrayList<Integer> temp2=(ArrayList<Integer>)o2;
		
		if(temp1.size()>temp2.size())
		{
			return 1;
		}
		else if(temp1.size()==temp2.size())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
}