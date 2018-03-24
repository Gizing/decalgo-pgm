package multipleAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 此类为整个单表多属性所有操作的封装，构造函数即能做全部操作
 * @author Gizing
 *
 */
public class MultipleAttributes
{
	
	public MultipleAttributes(VertexNode[] vn,int tableSize,Constraint[] constraints,String tableName)
	{
		
		
		Graph graph = new Graph(vn.length,vn);
		
		//求出有约束的属性，并添加进图，构建马尔科夫网
		identifyConstraintAttributeNo(vn,constraints);
		functionNameIsShit(vn,constraints,graph);
		
		graph.print();
		//将图转为弦图
		graph.convertToChordal();
		graph.print();
		
		//求出所有极大团
		graph.findMaximalCliques();
//		ArrayList<HashSet<Integer>> maxCliques = new ArrayList<HashSet<Integer>>();
//		HashSet<Integer> shit1 = new HashSet<Integer>();
//		shit1.add(1);
//		shit1.add(3);
//		maxCliques.add(shit1);
//		HashSet<Integer> shit2 = new HashSet<Integer>();
//		shit2.add(4);
//		shit2.add(8);
//		shit2.add(3);
//		maxCliques.add(shit2);
//		graph.setMaxClique(maxCliques);
		
		//求所有极大团的边缘分布
		Calculator calculator = new Calculator(graph.getMaxCliques(),tableSize,vn);
		calculator.setConstraints(constraints);
		calculator.solveMarginalDistributions();
		
		//根据边缘分布sample生成数据库实例
		Sample sample = new Sample(vn,graph.getMaxCliques(),calculator.getEquationsResult(),tableSize,tableName);
		sample.doSample();
	}
	
	/**
	 * 求出有约束的属性，并添加进图
	 * @param vn
	 * @param constraints
	 * @param graph
	 */
	void functionNameIsShit(VertexNode[] vn,Constraint[] constraints,Graph graph)
	{
		//求schema的属性集
		HashSet<String> schemaAtt = new HashSet<String>();
		for(VertexNode temp:vn)
		{
			schemaAtt.add(temp.getAttributeName());
		}
		
		HashSet<String> tempAttSet = null;
		ArrayList<String> edges = null;
		for(Constraint temp:constraints)
		{
			//求schema属性集和每个constraint属性集的交集
			tempAttSet = new HashSet<String>();
			tempAttSet.addAll(temp.getAttributesName());
			tempAttSet.retainAll(schemaAtt);
			if(schemaAtt.size() < 2)
			{
				continue;
			}
			
			edges = new ArrayList<String>(tempAttSet);
			graph.addEdges(edges);
		}
	}
	
	/**
	 * 将所有constraint的attributeNo都设定好
	 * @param vn
	 * @param constraints
	 */
	void identifyConstraintAttributeNo(VertexNode[] vn,Constraint[] constraints)
	{
		HashSet<Integer> attributeNo = null;
		Iterator<String> it = null;
		for(Constraint temp:constraints)
		{
			attributeNo = new HashSet<Integer>();
			it = temp.getAttributesName().iterator();
			while(it.hasNext())
			{
				String s = it.next();
				for(int i=0;i<vn.length;i++)
				{
					if(s.equals(vn[i].getAttributeName()))
					{
						attributeNo.add(i+1);
					}
				}
			}
			temp.setAttributeNo(attributeNo);
		}
	}

	
}
