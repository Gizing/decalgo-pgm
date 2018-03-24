package multipleAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.ArrayList;


//����ͼ(�ڽӾ��󹹳ɣ�������1��ʼ)
public class Graph
{
	private VertexNode[] verticesList = null;
	private int[][] edge = null;
	private int numVertices = 0;
	
	private int v[]=null;
	private int setc[]=null;
	private int vis[]=null;//�Ƿ���ʹ�,1������ʹ��������ж��Ƿ�����ͼ

	
	private int[] visited;// �ڵ�״̬,ֵΪ0����δ���ʵ�,���������л�ʱ
	private ArrayList<Integer> singleTrace = new ArrayList<Integer>();// �ӳ����ڵ㵽��ǰ�ڵ�Ĺ켣
	private boolean hasLoop;
	private ArrayList<ArrayList<Integer>> loops=null;
	private ArrayList<HashSet<Integer>> loopSet=null;
	
	private ArrayList<HashSet<Integer>> maxCliques=null;//���м����ŵĶ���
	
//	private HashSet<Integer> currentClique=null;
//	private int maxNumOfClique;
//	private int numOfClique;
//	private HashSet<Integer> visitedSet=null;
	
	
	//���캯��
	public Graph(int size, VertexNode[] attributes)
	{
		this.numVertices = size;
		this.verticesList = attributes;
		edge = new int[numVertices+1][numVertices+1];
		// verticesList = new VertexNode[numVertices];

		// �����ʼ��
		for (int i = 1; i <= numVertices; i++)
		{
			for (int j = 1; j <= numVertices; j++)
			{
				// 0�����޹�����1�����й���
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
	 * Step2.1�ж��Ƿ�����ͼ�����������MCS�㷨
	 */
	// �����������������
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
	
	//�����������
	void setEdge(int v1,int v2)
	{
		edge[v1][v2] = edge[v2][v1]=1;
	}

	// ��������vertex��ͼ�е�λ��
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

	// ȡ����i����������null
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

	// �ж�����֮���Ƿ��й������й�������true
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
	
	//����ڽӾ������������ڵ�Ԫ����
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
	
	//������������(������㷨)
	void MCS()
	{
		//��ʼ��
		for (int i = 0; i <= this.numVertices; i++)
		{
			v[i] = -1;
			vis[i] = 0;
		}
		v[this.numVertices] = 1;
		vis[1] = 1;

		//��n��1���θ�����
		for (int i = this.numVertices - 1; i >= 1; i--)
		{
			int id = -1, nmax = -1;
			for (int j = 2; j <= this.numVertices; j++)
			{
				//δ���ʹ�
				if (vis[j]==0)
				{
					//num�����ѱ�ŵĵ����ڵ�����
					int num = 0;
					for (int k = this.numVertices; k > i; k--)
					{
						if (edge[j][v[k]]==1)
							num++;
					}
					//ѡ�����ı��
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
	
	//�ж��Ƿ��ͼΪ��ͼ
	public boolean isGraphChordal()
	{
		//�����������
		MCS();

		//�ж��Ƿ�Ϊ������������
		for (int i = 1; i <= this.numVertices; i++)
		{
			//��Vi���ڵ�ĸ���
			int counter = 0;

			//�����Vi���ڵĵĵ�
			for (int j = i + 1; j <= this.numVertices; j++)
			{
				if (edge[v[i]][v[j]]==1)
					setc[counter++] = v[j];
			}

			//�ж�Vj1�Ƿ���Vj2.....Vjk���ڣ�����������������������
			for (int j = 1; j < counter; j++)
			{
				if (edge[setc[0]][setc[j]]==0)
					return false;
			}
		}
		return true;
	}
	
	
	
	/*
	 * Step2.2.1���ͼ�����л�
	 */
	// �������е㣬������еĻ�
	public void findLoops()
	{
		for (int i = 1; i <= this.numVertices; i++)
		{
			cleanVisited();
			singleLoop(i);
		}
	}

	// �ж�ĳ�����Ƿ����ڻ�����.�����򷵻�true
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

	// ���visited״̬
	void cleanVisited()
	{
		Arrays.fill(visited, 0);
	}

	// ��ĳ�������������еĻ�,�ݹ�DFS
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
					// ��¼һ����
					tempVec.add(singleTrace.get(j));
					tempSet.add(singleTrace.get(j));
					// System.out.print(singleTrace.get(j) + " ");
					j++;
				}

				// �жϴ˻��Ƿ��Ѵ��ڣ���������뻷��
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
	 * Step2.2.2�жϻ��Ƿ�����
	 */
	//����û���ҵĻ���loops�еı��
	ArrayList<Integer> isLoopChordal()
	{
		LoopSetComparator loopSetComparator=new LoopSetComparator();
		Collections.sort(this.loopSet, loopSetComparator);
		ArrayList<Integer> unchordalVec=new ArrayList<Integer>();
		LoopComparator loopComparator = new LoopComparator();
		Collections.sort(this.loops,loopComparator);
		boolean isChordal=false;
		
		
		//�Ӻ���ǰ��ÿ��������������3����set�Ƿ����Ӽ����������ң�û����û����
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
					//�˻�����
					isChordal=true;
					break;
				}
			}
			if(!isChordal)
			{
				//�����ҵĻ��ı�ż�¼����
				unchordalVec.add(i);
			}
		}
		return unchordalVec;
	}
	
	
	
	/*
	 * Step2.2.3��û���ҵĻ�������
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
	
	//����Step2������
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
//	 * Step3�ҳ���ͼ�е����������
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
//	 * ������������������
//	 * @param nodeID
//	 */
//	void DFS(int nodeID)
//	{
//		if(isAvailable(nodeID))
//		{
//			//���˵����������
//			currentClique.add(nodeID);
//			numOfClique++;
//			
//			
//			HashSet<Integer> result = new HashSet<Integer>();
//			result.addAll(visitedSet);
//			result.removeAll(currentClique);
//			
//			//��Ҷ�ӽڵ�
//			if(result.size()==0)
//			{
//				//����õ��ű���
//				maxNumOfClique=numVertices;
//				//maxCliques.add(new HashSet<Integer>(currentClique));
//				addToMaxCliques();
//				return;
//			}
//			
//			
//			//����������һ��
//			Iterator<Integer> it = result.iterator();
//			while(it.hasNext())
//			{
//				DFS(it.next());
//			}
//			
//			if(numOfClique>=maxNumOfClique)
//			{
//				//���������
//				maxNumOfClique=numOfClique;
//				//maxCliques.add(new HashSet<Integer>(currentClique));
//				addToMaxCliques();
//			}
//			
//			//�Ƴ��˵�
//			numOfClique--;
//			currentClique.remove(nodeID);
//		}
//		return;
//	}
//	
//	/**
//	 * �жϴ˶������������Ƿ���ϣ�һ��δ�����ĵ㼴����false
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
//	 * ��ʼ�����ʼ�
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
//	 * ������ż�������ż�maxCliques
//	 */
//	void addToMaxCliques()
//	{
//		//1.�жϴ�ʱ��currentClique�Ƿ��Ѵ�����maxCliques��
//		for(int i=0;i<maxCliques.size();i++)
//		{
//			if(maxCliques.get(i).equals(currentClique))
//			{
//				return;
//			}
//		}
//		
//		//2.����û�������
//		maxCliques.add(new HashSet<Integer>(currentClique));
//	}
	
	public ArrayList<HashSet<Integer>> getMaxCliques()
	{
		return maxCliques;
	}


	/**
	 * ����Mathematica������еļ�����
	 */
	public void findMaximalCliques()
	{
		maxCliques = new ArrayList<HashSet<Integer>>();
		StringBuilder input = new StringBuilder("g=Graph[{");
		
		//��װ��������
		for(int i=1;i<=numVertices;i++)
		{
			input.append(i);
			input.append(',');
		}
		input.deleteCharAt(input.length()-1);
		input.append("},{");
		
		//��װ�ߵ����
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
		
		//������
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






//���ڶ�loopSet���򣬸���set�Ĵ�С������
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

//����loops���򣬸���ÿ��loop�Ĵ�С������
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