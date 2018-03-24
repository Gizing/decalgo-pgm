package multipleAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * ����Ϊ����������������в����ķ�װ�����캯��������ȫ������
 * @author Gizing
 *
 */
public class MultipleAttributes
{
	
	public MultipleAttributes(VertexNode[] vn,int tableSize,Constraint[] constraints,String tableName)
	{
		
		
		Graph graph = new Graph(vn.length,vn);
		
		//�����Լ�������ԣ�����ӽ�ͼ����������Ʒ���
		identifyConstraintAttributeNo(vn,constraints);
		functionNameIsShit(vn,constraints,graph);
		
		graph.print();
		//��ͼתΪ��ͼ
		graph.convertToChordal();
		graph.print();
		
		//������м�����
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
		
		//�����м����ŵı�Ե�ֲ�
		Calculator calculator = new Calculator(graph.getMaxCliques(),tableSize,vn);
		calculator.setConstraints(constraints);
		calculator.solveMarginalDistributions();
		
		//���ݱ�Ե�ֲ�sample�������ݿ�ʵ��
		Sample sample = new Sample(vn,graph.getMaxCliques(),calculator.getEquationsResult(),tableSize,tableName);
		sample.doSample();
	}
	
	/**
	 * �����Լ�������ԣ�����ӽ�ͼ
	 * @param vn
	 * @param constraints
	 * @param graph
	 */
	void functionNameIsShit(VertexNode[] vn,Constraint[] constraints,Graph graph)
	{
		//��schema�����Լ�
		HashSet<String> schemaAtt = new HashSet<String>();
		for(VertexNode temp:vn)
		{
			schemaAtt.add(temp.getAttributeName());
		}
		
		HashSet<String> tempAttSet = null;
		ArrayList<String> edges = null;
		for(Constraint temp:constraints)
		{
			//��schema���Լ���ÿ��constraint���Լ��Ľ���
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
	 * ������constraint��attributeNo���趨��
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
