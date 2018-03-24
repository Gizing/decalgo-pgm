package multipleAttributes;

import java.util.HashSet;

public class Constraint
{
	int id;
	String expr1 = null;
	String type = null;
	int expr2;
	int num;
	StringBuilder expr = null;
	
	/**
	 * 此约束涉及到的表的名字，格式:table_name1 table_name2
	 */
	String tableName = null;
	
	HashSet<Integer> attributeNo = null;//此约束相关属性在VertexNode数组中的编号（从1开始计数,如VertexNode[3]就写4）
	HashSet<String> attributesName = null;//此约束涉及到的属性名
	
	public Constraint(int id,String expr1,String type,int expr2,int num,String tableName)
	{
		this.id=id;
		this.expr1=expr1;
		this.type=type;
		this.expr2=expr2;
		this.num=num;
		//this.attributesName=attributesName;
		expr = new StringBuilder();
		expr.append(expr1);
		expr.append(type);
		expr.append(expr2);
		
		this.tableName = tableName;
	}
	
	/**
	 * 
	 * @param input 包含所有信息
	 */
	public Constraint(String input)
	{
		input = input.substring(2, input.length()-1);
		String[] parts = input.split(",");
		id = Integer.parseInt(parts[0]);
		//expr = new StringBuilder(parts[1]);
		expr = new StringBuilder(parts[1].replace(";", "&&"));
		num = Integer.parseInt(parts[2]);
		tableName = parts[3];
		
		divideForAttribute();
	}
	
	/**
	 * 求出输入的每个约束的属性集
	 * @param constraints 约束集
	 */
	void divideForAttribute()
	{

		String[] parts = expr.toString().split("\\p{Punct}");
		attributesName = new HashSet<String>();

		// 只要变量部分，数字不要
		for (String piece : parts)
		{
			if (!piece.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$"))
			{
				attributesName.add(piece);
			}
		}
	}
	
	
	
	public void setAttributeNo(HashSet<Integer> attributeNo)
	{
		this.attributeNo = attributeNo;
	}
	
	public String getTableName()
	{
		return tableName;
	}
	public void setTableName(String tableName)
	{
		this.tableName = new String(tableName);
	}
	
	public Constraint()
	{
		
	}
	
	public String getExpr()
	{
		return expr.toString();
	}
	
	public void setAttributesName(HashSet<String> attributesName)
	{
		this.attributesName = attributesName;
	}
	public HashSet<String> getAttributesName()
	{
		return attributesName;
	}
}
