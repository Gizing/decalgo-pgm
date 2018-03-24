package multipleAttributes;

import java.util.HashSet;
import java.util.ArrayList;

public class Main
{

	public static void main(String[] args)
	{
//		//以下为测试输入
//		VertexNode[] vn=new VertexNode[4];
//		VertexNode temp1=new VertexNode();
//		VertexNode temp2=new VertexNode();
//		VertexNode temp3=new VertexNode();
//		VertexNode temp4=new VertexNode();
////		VertexNode temp5=new VertexNode();
////		VertexNode temp6=new VertexNode();
//		
//		temp1.setAttributeName("a");
//		vn[0]=temp1;
//		temp2.setAttributeName("b");
//		vn[1]=temp2;
//		temp3.setAttributeName("c");
//		vn[2]=temp3;
//		temp4.setAttributeName("d");
//		vn[3]=temp4;
////		temp5.setAttributeName("e");
////		vn[4]=temp5;
////		temp6.setAttributeName("f");
////		vn[5]=temp6;
//
//		Graph g=new Graph(vn.length,vn);
//		
//		ArrayList<String> arraylist=new ArrayList<String>();
//		arraylist.add("a");
//		arraylist.add("b");
////		arraylist.add("f");
////		arraylist.add("c");
//		ArrayList<String> arraylist2=new ArrayList<String>();
//		arraylist2.add("b");
//		arraylist2.add("c");
//		ArrayList<String> arraylist3=new ArrayList<String>();
//		arraylist3.add("c");
//		arraylist3.add("d");
//		//arraylist3.add("c");
//		ArrayList<String> arraylist4=new ArrayList<String>();
////		arraylist4.add("d");
////		arraylist4.add("e");
////		ArrayList<String> arraylist5=new ArrayList<String>();
////		arraylist5.add("e");
////		arraylist5.add("f");
////		ArrayList<String> arraylist6=new ArrayList<String>();
////		arraylist6.add("f");
////		arraylist6.add("a");
//		g.addEdges(arraylist);
//		g.addEdges(arraylist2);
//		g.addEdges(arraylist3);
//		g.addEdges(arraylist4);
////		g.addEdges(arraylist5);
////		g.addEdges(arraylist6);
//		
//		
//		g.print();
//		System.out.println("is graph chordal:"+g.isGraphChordal());
//		g.convertToChordal();
//		g.print();
//		System.out.println("is graph chordal:"+g.isGraphChordal());
//		g.findMaxClique();
//		Calculator ccl = new Calculator(g.getMaxCliques(),10,vn);
//		Constraint[] csa = new Constraint[2];
//		csa[0]=new Constraint(0,"a+b","<",2,3,"A");
//		HashSet<Integer> attNo1 = new HashSet<Integer>();
//		attNo1.add(1);
//		attNo1.add(2);
//		csa[0].setAttributeNo(attNo1);
//		
//		csa[1]=new Constraint(1,"b+c","<",1,4,"A");
//		HashSet<Integer> attNo2 = new HashSet<Integer>();
//		attNo2.add(2);
//		attNo2.add(3);
//		csa[1].setAttributeNo(attNo2);
//		ccl.setConstraints(csa);
//		ccl.solveMarginalDistributions();
		
		

		
		
		
		
		
		
//		VertexNode[] vn=new VertexNode[5];
//		VertexNode temp1=new VertexNode();
//		VertexNode temp2=new VertexNode();
//		VertexNode temp3=new VertexNode();
//		VertexNode temp4=new VertexNode();
//		VertexNode temp5=new VertexNode();
//		temp1.setAttributeName("a");
//		vn[0]=temp1;
//		temp2.setAttributeName("b");
//		vn[1]=temp2;
//		temp3.setAttributeName("c");
//		vn[2]=temp3;
//		temp4.setAttributeName("d");
//		vn[3]=temp4;
//		temp5.setAttributeName("e");
//		vn[4]=temp5;
//		
//		Constraint[] constraints = new Constraint[2];
//		constraints[0]=new Constraint(0,"a+c","<",2,3,"A");
//		HashSet<Integer> attNo1 = new HashSet<Integer>();
//		attNo1.add(1);
//		attNo1.add(3);
//		constraints[0].setAttributeNo(attNo1);
//		constraints[1]=new Constraint(1,"b+c+d","<",3,4,"A");
//		HashSet<Integer> attNo2 = new HashSet<Integer>();
//		attNo2.add(2);
//		attNo2.add(3);
//		constraints[1].setAttributeNo(attNo2);
		
		
		
		
		InputProcessor ip = new InputProcessor("g:/schema.txt","g:/constraints.txt");
		
		MultipleAttributes ma = new MultipleAttributes(ip.getVertexNode(),ip.getTableSize(),ip.getConstraints(),ip.getTableName());
		

		System.out.println();
	}

}
