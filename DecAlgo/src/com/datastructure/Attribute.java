package com.datastructure;

/**
 * ���ݱ��е�������Ϣ
 * 
 * @author Gizing
 */
public class Attribute
{

	/** ���������������� */
	private String attrName = null;

	/** ������������ */
	private String attrType = null;

	/** ���Ե����ɺ��� */
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
