package com.datastructure;

/**
 * 数据表中的属性信息
 * 
 * @author Gizing
 */
public class Attribute
{

	/** 属性名，不带表名 */
	private String attrName = null;

	/** 属性数据类型 */
	private String attrType = null;

	/** 属性的生成函数 */
	private AttrFunction attrFunction = null;

	public Attribute(String attrName, String attrType)
	{
		super();
		this.attrName = attrName;
		this.attrType = attrType;
	}

	public void setAttrFunction(AttrFunction attrFunction)
	{
		this.attrFunction = attrFunction;
	}

	public String getAttrName()
	{
		return attrName;
	}

	public String getAttrType()
	{
		return attrType;
	}

	public AttrFunction getAttrFunction()
	{
		return attrFunction;
	}

	public String toString()
	{
		return "[attrName=" + attrName + ", attrType=" + attrType + ", attrFunction=" + attrFunction + "]";
	}
}
