package com.datastructure;

import java.util.HashSet;

/**
 * 用于整数等值段的区间，左闭右开[start,end)
 * @author Gizing
 *
 */
public class Section
{
	private int start;
	private int end;
	
	public Section()
	{
		
	}
	public Section(int start,int end)
	{
		this.start = start;
		this.end = end;
	}
	
	/**
	 * 判断两个区间是否有交集
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean isIntersected(int start,int end)
	{
		if(end <= this.start || start >= this.end)
			return false;
		else	
			return true;
		
	}
	
	/**
	 * 判断某个数是否在区间内
	 * @param input
	 * @return
	 */
	public boolean contains(int input)
	{
		if(input>start && input<end)
			return true;
		else
			return false;
		
//		if(input>=start && input<end)
//			return true;
//		else
//			return false;
	}
	
	/**
	 * 求两个区间的交集长度
	 * @param start
	 * @param end
	 * @return
	 */
	public int intersectLength(int start,int end)
	{
		if(end <= this.start || start >= this.end)
			return 0;
		HashSet<Integer> intersectSet = new HashSet<Integer>();
		for(int i = this.start; i < this.end; i++)
			intersectSet.add(i);
		HashSet<Integer> retainSet = new HashSet<Integer>();
		for(int i=start;i<end;i++)
			retainSet.add(i);
		intersectSet.retainAll(retainSet);
		return intersectSet.size();
	}
	
	/**
	 * 将此区间与输入的区间取交集，前提是必然有交集
	 * @param sec
	 */
	public void modifySection(Section sec)
	{
		if (start > sec.getStart())
		{
			start = sec.getEnd();
			
		} else
		{
			
		}
	}

	public int getStart()
	{
		return start;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public int getEnd()
	{
		return end;
	}

	public void setEnd(int end)
	{
		this.end = end;
	}
	
	public static void main(String[] args)
	{
		
	}
}
