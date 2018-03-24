
package multipleTables;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.sqltest.SQLLink;

import multipleAttributes.Constraint;
import multipleAttributes.MultipleAttributes;
import multipleAttributes.VertexNode;


/**
 * 此类为多表所有操作的封装
 * @author Gizing
 *
 */
public class MultipleTable
{
	private SQLLink sqlLink = null;
	
	public MultipleTable(Constraint[] constraints,ArrayList<TreeNode> nodes)
	{
		sqlLink = new SQLLink();
		
		Tree tree = new Tree(nodes);
		convertToSingleTable(constraints,tree);
		
		step2(nodes,constraints);
		
		step3(tree);
		
		step4(nodes);
		
		sqlLink.close();
	}
	
	/**
	 * Step4 生成新的R
	 * @param nodes
	 */
	void step4(ArrayList<TreeNode> nodes)
	{
		String tableName = null;
		StringBuilder Relation = null;
		String[] parts = null;
		String primaryKey = null;
		ArrayList<ForeignKeyInfo> foreignKeyInfo = null;
		StringBuilder sql = null;
		//SQLLink sqlLink = new SQLLink();
		
		//主键
		for(TreeNode node:nodes)
		{
			tableName = node.getTableName();
			foreignKeyInfo = new ArrayList<ForeignKeyInfo>();
			Relation = new StringBuilder(node.getSchemaRelation());
			Relation.delete(0, 2);
			Relation.deleteCharAt(Relation.length()-1);
			parts = Relation.toString().split(",");
			for(int i=2;i<parts.length;i++)
			{
				//PK
				if(parts[i].contains(" PK"))
				{
					primaryKey = parts[i].split(" ")[0];
				}
				
				//FK
				if(parts[i].contains(" FK;"))
				{
					ForeignKeyInfo single = new ForeignKeyInfo();
					String[] info = parts[i].split(";");
					single.setAttribute(info[0].split(" ")[0]);
					single.setReferenceTable(info[1]);
					single.setReferenceAttribute(info[2]);
					foreignKeyInfo.add(single);
				}
			}
			
			//增加主键列且自增
			sql = new StringBuilder("alter table ");
			sql.append(tableName);
			sql.append(" add ");
			sql.append(primaryKey);
			sql.append(" int primary key auto_increment;");
			sqlLink.executeUpdate(sql.toString());
			
			//增加外键列
			if(foreignKeyInfo.size()==0)
			{
				//此表没有外键则跳过
				continue;
			}
			sql = new StringBuilder("alter table ");
			sql.append(tableName);
			sql.append(' ');
			for(ForeignKeyInfo fki:foreignKeyInfo)
			{
				sql.append("add ");
				sql.append(fki.getAttribute());
				sql.append(" int,");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(';');
			sqlLink.executeUpdate(sql.toString());
		}
		
		//外键
		for(TreeNode node:nodes)
		{
			tableName = node.getTableName();
			foreignKeyInfo = new ArrayList<ForeignKeyInfo>();
			Relation = new StringBuilder(node.getSchemaRelation());
			Relation.delete(0, 2);
			Relation.deleteCharAt(Relation.length()-1);
			parts = Relation.toString().split(",");
			for(int i=2;i<parts.length;i++)
			{
				//FK
				if(parts[i].contains(" FK;"))
				{
					ForeignKeyInfo single = new ForeignKeyInfo();
					String[] info = parts[i].split(";");
					single.setAttribute(info[0].split(" ")[0]);
					single.setReferenceTable(info[1]);
					single.setReferenceAttribute(info[2]);
					foreignKeyInfo.add(single);
				}
			}
			
			//增加外键约束
			if(foreignKeyInfo.size()==0)
			{
				//此表没有外键跳过
				continue;
			}
			sql = new StringBuilder("alter table ");
			sql.append(tableName);
			sql.append(' ');
			for(ForeignKeyInfo fki:foreignKeyInfo)
			{
				sql.append("add foreign key(");
				sql.append(fki.getAttribute());
				sql.append(") references ");
				sql.append(fki.getReferenceTable());
				sql.append('(');
				sql.append(fki.getReferenceAttribute());
				sql.append("),");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(';');
			sqlLink.executeUpdate(sql.toString());
			
			//给当前表的所有外键赋值
			assignForeignKey(foreignKeyInfo,node,nodes);
			
			
			//将重复列删除
			deleteDuplicateColumn(foreignKeyInfo,node,nodes);
		}
		
		
	}
	
	
	/**
	 * 将重复列删除
	 * @param foreignKeyInfo
	 * @param node
	 * @param nodes
	 */
	void deleteDuplicateColumn(ArrayList<ForeignKeyInfo> foreignKeyInfo,TreeNode node,ArrayList<TreeNode> nodes)
	{
		String referenceTable = null;
		HashSet<String> interAtt = new HashSet<String>();
		StringBuilder sql = null;
		
		for(ForeignKeyInfo fki:foreignKeyInfo)
		{
			referenceTable = fki.getReferenceTable();
			interAtt.clear();
			interAtt.addAll(node.getAttributesView());
			for(TreeNode tn:nodes)
			{
				if(referenceTable.equals(tn.getTableName()))
				{
					interAtt.retainAll(tn.getAttributesView());
					break;
				}
			}
			
			sql = new StringBuilder("alter table ");
			sql.append(node.getTableName());
			sql.append(' ');
			for(String str:interAtt)
			{
				sql.append("drop ");
				sql.append(str);
				sql.append(',');
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(';');
			
			sqlLink.executeUpdate(sql.toString());
		}
	}
	
	
	/**
	 * 给当前表的所有外键赋值
	 * @param foreignKeyInfo 当前表所有的外键信息
	 * @param node 当前表
	 * @param nodes 所有表的数组
	 */
	void assignForeignKey(ArrayList<ForeignKeyInfo> foreignKeyInfo,TreeNode node,ArrayList<TreeNode> nodes)
	{
		StringBuilder sql = null;
		String tableName = node.getTableName();
		String referenceTable = null;
		HashSet<String> interAtt = new HashSet<String>();
		
		for(ForeignKeyInfo fki:foreignKeyInfo)
		{
			referenceTable = fki.getReferenceTable();
			sql = new StringBuilder("update ");
			sql.append(tableName);
			sql.append(" set ");
			sql.append(fki.getAttribute());
			sql.append("=(select ");
			sql.append(fki.getReferenceAttribute());
			sql.append(" from ");
			sql.append(referenceTable);
			sql.append(" where ");
			
			//求两个表的交集属性
			interAtt.clear();
			interAtt.addAll(node.getAttributesView());
			for(TreeNode tn:nodes)
			{
				if(referenceTable.equals(tn.getTableName()))
				{
					interAtt.retainAll(tn.getAttributesView());
					break;
				}
			}
			
			for(String str:interAtt)
			{
				sql.append(tableName);
				sql.append('.');
				sql.append(str);
				sql.append('=');
				sql.append(referenceTable);
				sql.append('.');
				sql.append(str);
				sql.append(" and ");
			}
			sql.delete(sql.length()-5, sql.length());
			sql.append(" order by rand() limit 1);");
			
			sqlLink.executeUpdate(sql.toString());
		}
	}
	
	
	/**
	 * Step3 更新所有View使得满足性质，采用广度优先
	 * @param tree 
	 */
	void step3(Tree tree)
	{
		LinkedList<ParentChildMap> queue = new LinkedList<ParentChildMap>();
		
		//将根节点的孩子添加入queue
		for(TreeNode t:tree.getRoot().getChildNodes())
		{
			queue.add(new ParentChildMap(tree.getRoot(),t));
		}
		
		TreeNode node = null;
		TreeNode parentNode = null;
		ParentChildMap map = null;
		
		while(!queue.isEmpty())
		{
			//取出当前节点的子节点链表，加入queue
			map = queue.getFirst();
			node = map.getNode();
			parentNode = map.getParentNode();
			if(node.getChildNodes().size()!=0)
			{
				for(TreeNode t:node.getChildNodes())
				{
					queue.add(new ParentChildMap(node,t));
				}
			}
			
			//针对每个节点更新View
			updateEachView(parentNode,node);
			
			//从queue中删除此节点
			queue.removeFirst();
		}
		
	}
	
	/**
	 * 针对每个节点更新View
	 * @param fatherNode 当前节点的父节点
	 * @param node	当前节点
	 */
	void updateEachView(TreeNode parentNode,TreeNode node)
	{
//		MultipleAttributesResult parentInstance = parentNode.getDatabaseInstance();
//		MultipleAttributesResult instance = node.getDatabaseInstance();
		String nodeTableName = node.getTableName();
		String parentNodeTableName = parentNode.getTableName();
		//int index = node.getSchemaView().indexOf('[');
		//String attributesStr = node.getSchemaView().substring(index, node.getSchemaView().length()-1);
		
		//求两表的交集属性
		HashSet<String> interAtt = new HashSet<String>();
		interAtt.addAll(parentNode.getAttributesView());
		interAtt.retainAll(node.getAttributesView());
		
		//构建SQL语句
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(nodeTableName);
		sql.append('(');
		//sql.append(attributesStr);
		for(String singleAtt:interAtt)
		{
			sql.append(singleAtt);
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") select distinct ");
		//sql.append(attributesStr);
		for(String singleAtt:interAtt)
		{
			sql.append(singleAtt);
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" from ");
		sql.append(parentNodeTableName);
		sql.append(" where not exists (select * from ");
		sql.append(nodeTableName);
		sql.append(" where (");
		//String[] att = attributesStr.split(",");
		for(String singleAtt:interAtt)
		{
			sql.append(nodeTableName);
			sql.append('.');
			sql.append(singleAtt);
			sql.append('=');
			sql.append(parentNodeTableName);
			sql.append('.');
			sql.append(singleAtt);
			sql.append(" and ");
		}
		sql.delete(sql.length()-5, sql.length());
		sql.append(")");
		for(String singleAtt:interAtt)
		{
			sql.append(" or ");
			sql.append(parentNodeTableName);
			sql.append('.');
			sql.append(singleAtt);
			sql.append(" is null");
		}
		sql.append(");");
		
		sqlLink.executeUpdate(sql.toString());
		
	}
	
	/**
	 * Step2 针对每个View调用单表多属性
	 * @param nodes 有生成VertexNode tableSize所需的信息
	 * @param constraints 所有约束
	 */
	void step2(ArrayList<TreeNode> nodes,Constraint[] constraints)
	{
		String[] parts = null;
		VertexNode[] vn = null;
		//ArrayList<Constraint> relatedConstraints = null;
		MultipleAttributes ma = null;
		
		for(TreeNode node:nodes)
		{
			//生成每个View所需的VertexNode[]
			String temp = node.getSchemaView();
			int beginIndex = temp.indexOf('[');
			temp = temp.substring(beginIndex+1, temp.length()-1);
			parts = temp.split(",");
			vn = new VertexNode[parts.length];
			for(int i=0;i<vn.length;i++)
			{
				vn[i] = new VertexNode();
				vn[i].setAttributeName(parts[i]);
			}
			
			//每个View的表大小
			int tableSize = node.getTableSize();
			
			//涉及此View的相关约束
			ArrayList<Constraint> corCon = new ArrayList<Constraint>();
			for(int i=0;i<constraints.length;i++)
			{
				String view = constraints[i].getTableName();
				if(node.getTableName().equals(view.substring(0, view.length()-5)))
				{
					//与当前输入表的相关约束
					corCon.add(constraints[i]);
				}
			}
			Constraint[] correlateConstraints = new Constraint[corCon.size()];
			corCon.toArray(correlateConstraints);
			
			
			ma = new MultipleAttributes(vn,tableSize,correlateConstraints,node.getTableName());
		}
	}
	
	
	/**
	 * 将涉及多表的constraint变为单表
	 * @param constraints
	 * @param tree
	 */
	void convertToSingleTable(Constraint[] constraints,Tree tree)
	{
		String[] names = null;
		HashSet<String> compareSet = null;
		//ArrayList<TreeNode> nodes = null;
		for(int i=0;i<constraints.length;i++)
		{
			names = constraints[i].getTableName().split(" ");
//			if(1 == names.length)
//			{
//				continue;
//			}
			
			compareSet = new HashSet<String>();
			for(int j=0;j<names.length;j++)
			{
				compareSet.add(names[j]);
			}
			
			//nodes = tree.getNodes();
			String tableNameView = tree.getRelationViewMap().get(compareSet);
			if(tableNameView != null)
			{
				//将多个Ri替换为Vi
				constraints[i].setTableName(tableNameView);
			}
		}

	}
	
	
}
