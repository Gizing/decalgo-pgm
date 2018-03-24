package com.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据表Schema信息
 * 
 * @author Gizing
 */
public class Table
{

	/** 表名 */
	private String tableName = null;

	/** 表行数 */
	private int size;

	/** 属性信息列表 */
	private List<Attribute> attributes = null;

	/**
	 * 构造函数，根据传入参数初始化数据表
	 * 
	 * @param tableName
	 *            表名
	 * @param size
	 *            表行数
	 * @param attributesInfo
	 *            属性信息数组
	 */
	public Table(String tableName, int size, String[] attributesInfo)
	{
		super();
		this.tableName = tableName;
		this.size = size;
		attributes = new ArrayList<Attribute>();
		String[] arr = null;
		for (int i = 0; i < attributesInfo.length; i++)
		{
			arr = attributesInfo[i].trim().split("[ \t]+");
			attributes.add(new Attribute(arr[0].trim(), arr[1].trim()));
		}
	}
	
	/**
	 * 根据输入的属性名字，返回相应的Attribute
	 * @param attributeName 属性名字不包含表名
	 * @return
	 */
	public Attribute indexOf(String attributeName)
	{
		for(Attribute a : attributes)
		{
			if(attributeName.equals(a.getAttrName()))
				return a;
		}
		//名字正确则不可能返回null
		return null;
	}

	public String getTableName()
	{
		return tableName;
	}

	public int getSize()
	{
		return size;
	}

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	public String toString()
	{
		return "Table [tableName=" + tableName + ", size=" + size + ", attributes=\n" + attributes + "]\n";
	}
}
