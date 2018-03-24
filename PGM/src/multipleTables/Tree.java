package multipleTables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


//���ӱ�ʾ��
public class Tree
{
	private ArrayList<TreeNode> nodes = null;
	private TreeNode root = null;
	private HashMap<HashSet<String>,String> relationViewMap = null;
	
	public Tree(ArrayList<TreeNode> input)
	{
		nodes = input;
		String tempRelation = null;
		StringBuilder tempView = null;
		String[] temp = null;
		ArrayList<TreeNode> tempChild = null;
		
		HashSet<String> tableNameSet = null;
		relationViewMap = new HashMap<HashSet<String>,String>();
		
		for(int i=0,length=nodes.size();i<length;i++)
		{
			tempRelation = nodes.get(i).getSchemaRelation();
			tempRelation = tempRelation.substring(2, tempRelation.length()-1);
			temp = tempRelation.split(",");
			nodes.get(i).setTableName(temp[0]);
			
			//����
			for(int j=2;j<temp.length;j++)
			{
				if(temp[j].contains(" FK;"))
				{
					//ȡ��FK���ο��ı���
					String[] tempshit = temp[j].split(";");
					
					nodes.get(i).addChildNode(getChild(tempshit[1]));
				}
			}
			
			
			tableNameSet = new HashSet<String>();
			tableNameSet.add(temp[0]);
			
			
			//����Vi����Ӧ��Vi����
			HashSet<String> attributesView = new HashSet<String>();
			tempView = new StringBuilder(temp[0]);
			tempView.append("_View[");
			tempChild = nodes.get(i).getChildNodes();
			
			makeView(nodes.get(i),tempView,attributesView);
			for(int j=0;j<tempChild.size();j++)
			{
				makeView(tempChild.get(j),tempView,attributesView);
				tableNameSet.add(tempChild.get(j).getTableName());
			}
			tempView.deleteCharAt(tempView.length()-1);
			tempView.append(']');
			nodes.get(i).setSchemaView(tempView.toString());
			nodes.get(i).setAttributesView(attributesView);
			
			relationViewMap.put(tableNameSet, temp[0]+"_View");
		}
		
		findRoot();
	}
	
	//�ҳ����ڵ�
	void findRoot()
	{
		HashSet<String> tableSet = new HashSet<String>();
		for(int i=0,length=nodes.size();i<length;i++)
		{
			tableSet.add(nodes.get(i).getTableName());
		}
		
		String Relation = null;
		String[] parts = null;
		String[] temp = null;
		for(int i=0,length=nodes.size();i<length;i++)
		{
			Relation = nodes.get(i).getSchemaRelation();
			Relation = Relation.substring(2, Relation.length()-1);
			parts = Relation.split(",");
			
			for(int j=2;j<parts.length;j++)
			{
				if(parts[j].contains(" FK;"))
				{
					temp = parts[j].split(";");
					if(tableSet.contains(temp[1]))
					{
						tableSet.remove(temp[1]);
					}
				}
			}
		}
		
		//System.out.println(tableSet.size());
		Iterator<String> it = tableSet.iterator();
		String rootTableName = it.next();
		for(int i=0,length=nodes.size();i<length;i++)
		{
			if(rootTableName.equals(nodes.get(i).getTableName()))
			{
				root = nodes.get(i);
				break;
			}
		}
	}
	
	
	//���������������Ӧ�ڵ�,���򷵻�null
	TreeNode getChild(String tableName)
	{
		String[] temp = null;
		for(int i=0,length=nodes.size();i<length;i++)
		{
			temp = nodes.get(i).getSchemaRelation().split(",");
			if(temp[0].contains(tableName))
			{
				return nodes.get(i);
			}
		}
		return null;
	}
	
	
	/**
	 * ����ĳ���ڵ㣬�����е�value������ȡ����������Vi��StringBuilder��ʽ
	 * @param input ĳ���ڵ�
	 * @param tempView ����ֵVi
	 * @param attributesView ���ش�input����ȡ��Vi��Ӧ����
	 */
	void makeView(TreeNode input,StringBuilder tempView,HashSet<String> attributesView)
	{
		StringBuilder Relation = new StringBuilder(input.getSchemaRelation());
		Relation.delete(0, 2);
		Relation.deleteCharAt(Relation.length()-1);
		String[] parts = Relation.toString().split(",");
		
		for(int i=2;i<parts.length;i++)
		{
			if((!parts[i].contains(" PK")) && (!parts[i].contains(" FK;")))
			{
				tempView.append(parts[i]);
				tempView.append(',');
				
				attributesView.add(parts[i]);
			}
		}
	}
	
	public ArrayList<TreeNode> getNodes()
	{
		return nodes;
	}
	
	public TreeNode getRoot()
	{
		return root;
	}
	
	public HashMap<HashSet<String>,String> getRelationViewMap()
	{
		return relationViewMap;
	}
	
}


class TreeNode
{
	private String tableName = null;
	private String schemaRelation = null;//Ri
	private String schemaView = null;	   //Vi
	private ArrayList<TreeNode> childNodes = null;//�ӽڵ�����
	private int tableSize;
	private HashSet<String> attributesView = null;//View�����Լ��ϣ��ڶ���һ�������ɺ���
	
	/**
	 * ÿ��view��Ӧ�����ݿ�ʵ��
	 */
	//private MultipleAttributesResult databaseInstance= null;
	
	public TreeNode(String schemaRelation,int tableSize)
	{
		this.schemaRelation = schemaRelation;
		childNodes = new ArrayList<TreeNode>();
		this.tableSize = tableSize;
		
		int index = schemaRelation.indexOf(',');
		tableName = schemaRelation.substring(2, index);
	}
	
	
	public HashSet<String> getAttributesView()
	{
		return attributesView;
	}
	
	public void setAttributesView(HashSet<String> attributesView)
	{
		this.attributesView = attributesView;
	}
	
	public String getTableName()
	{
		return tableName;
	}
	
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	
	public ArrayList<TreeNode> getChildNodes()
	{
		return childNodes;
	}
	
	public void addChildNode(TreeNode input)
	{
		childNodes.add(input);
	}
	
	public String getSchemaRelation()
	{
		return schemaRelation;
	}
	
	public String getSchemaView()
	{
		return schemaView.toString();
	}
	
	public void setSchemaView(String schemaView)
	{
		this.schemaView = schemaView;
	}
	
	public int getTableSize()
	{
		return tableSize;
	}
	

}

class ParentChildMap
{
	
	private TreeNode parentNode = null;
	private TreeNode node = null;
	public ParentChildMap(TreeNode parentNode,TreeNode node)
	{
		this.parentNode = parentNode;
		this.node = node;
	}
	
	public TreeNode getParentNode()
	{
		return parentNode;
	}
	public void setParentNode(TreeNode parentNode)
	{
		this.parentNode = parentNode;
	}
	public TreeNode getNode()
	{
		return node;
	}
	public void setNode(TreeNode node)
	{
		this.node = node;
	}
}
