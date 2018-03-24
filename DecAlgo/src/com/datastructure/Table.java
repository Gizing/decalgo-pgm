package com.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * ���ݱ�Schema��Ϣ
 * 
 * @author Gizing
 */
public class Table
{

	/** ���� */
	private String tableName = null;

	/** ������ */
	private int size;

	/** ������Ϣ�б� */
	private List<Attribute> attributes = null;

	/**
	 * ���캯�������ݴ��������ʼ�����ݱ�
	 * 
	 * @param tableName
	 *            ����
	 * @param size
	 *            ������
	 * @param attributesInfo
	 *            ������Ϣ����
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
	 * ����������������֣�������Ӧ��Attribute
	 * @param attributeName �������ֲ���������
	 * @return
	 */
	public Attribute indexOf(String attributeName)
	{
		for(Attribute a : attributes)
		{
			if(attributeName.equals(a.getAttrName()))
				return a;
		}
		//������ȷ�򲻿��ܷ���null
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
