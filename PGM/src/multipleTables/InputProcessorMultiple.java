package multipleTables;

import java.util.ArrayList;

import multipleAttributes.InputProcessor;
import multipleAttributes.MyFileReader;
import multipleAttributes.VertexNode;

public class InputProcessorMultiple extends InputProcessor 
{
	private ArrayList<TreeNode> nodes = null;
	
	public InputProcessorMultiple(){}
	
	public InputProcessorMultiple(String schemaPath,String constraintsPath)
	{
		nodes = new ArrayList<TreeNode>();
		
		MyFileReader mfr = new MyFileReader();

		// 读schema并处理
		mfr.setFilePath(schemaPath);
		mfr.read();
		schema = mfr.getReadString();
		// System.out.println(schema);
		processSchema();

		// 读constraint并处理
		mfr.setFilePath(constraintsPath);
		mfr.read();
		constraintString = mfr.getReadString();
		processConstraints();
	}
	
	/**
	 * 处理多表时的schema
	 */
	protected void processSchema()
	{
		String[] schemaParts = schema.toString().split(";;");
		String[] temp = null;
		for(String singleSchema:schemaParts)
		{
			temp = singleSchema.toString().split(",");
			//tableName = temp[0].substring(2);
			tableSize = Integer.parseInt(temp[1]);
			vertexNode = new VertexNode[temp.length-2];
			VertexNode curVertexNode = null;
			for(int i=2;i<temp.length;i++)
			{
				curVertexNode = new VertexNode();
				curVertexNode.setAttributeName(temp[i].split(" ")[0]);
				vertexNode[i-2] = curVertexNode;
			}
			
			nodes.add(new TreeNode(singleSchema,tableSize));
			
		}
	}
	
	
	public ArrayList<TreeNode> getNodes()
	{
		return nodes;
	}
}
