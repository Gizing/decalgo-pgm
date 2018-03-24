package multipleTables;

import java.util.ArrayList;

import multipleAttributes.Constraint;

public class Main
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
//		ArrayList<TreeNode> test = new ArrayList<TreeNode>();
//		TreeNode node1 = new TreeNode("T[test1,4,K1 PK,email,shit,id,num,fuck,FK1 FK;test2;K2,FK2 FK;test3;K3]",10);
//		TreeNode node2 = new TreeNode("T[test2,5,K2 PK,id,num]",10);
//		TreeNode node3 = new TreeNode("T[test3,3,K3 PK,email]",10);
		//TreeNode node4 = new TreeNode("T[R4,7,K4 PK,D]",10);
		//test.add(node4);
//		test.add(node3);
//		test.add(node2);
//		test.add(node1);
		//Tree tree = new Tree(test);
//		Constraint[] constraints = new Constraint[3];
//		constraints[0] = new Constraint(0,"a+b","<",2,3,"R3 R4");
//		constraints[1] = new Constraint(1,"a+b","<",2,3,"R1 R2 R3");
//		constraints[2] = new Constraint(2,"a+b","<",2,3,"R2 R3");
		
		InputProcessorMultiple ipm = new InputProcessorMultiple("g:/schemaMultiple.txt","g:/constraintsMultiple.txt");
		
		MultipleTable mt = new MultipleTable(ipm.getConstraints(),ipm.getNodes());
		//mt.convertToSingleTable();
		
		
		System.out.println("hello world");
	}

}
