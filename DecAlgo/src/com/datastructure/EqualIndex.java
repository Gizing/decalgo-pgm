package com.datastructure;

/**
 * �������ϵ��ظ�����Ϣ
 * 
 * @author Gizing
 */
public class EqualIndex
{

	/** �������ϵ��ظ��� */
	private int index;

	/** �ظ����� */
	private int num;
	
	//��Ӹ��ظ�����Ϣ�ĵȺ�Լ�����
	private int constraintId;

	public EqualIndex()
	{
		
	}
	
	public EqualIndex(int index, int num, int constraintId)
	{
		super();
		this.index = index;
		this.num = num;
		this.constraintId = constraintId;
	}
	
	public EqualIndex(EqualIndex ei)
	{
		this.index = ei.getIndex();
		this.num = ei.getNum();
		this.constraintId = ei.getConstraintId();
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public void setNum(int num)
	{
		this.num = num;
	}
	
	public int getIndex()
	{
		return index;
	}

	public int getNum()
	{
		return num;
	}
	
	public int getConstraintId()
	{
		return constraintId;
	}

	public void setConstraintId(int constraintId)
	{
		this.constraintId = constraintId;
	}

	@Override
	public String toString()
	{
		return "[index=" + index + ", num=" + num + ", constraintId=" + constraintId + "]";
	}
}
