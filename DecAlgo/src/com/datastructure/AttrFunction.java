package com.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 属性数据生成函数
 * 
 * @author Gizing
 */
public class AttrFunction
{

	/** y = c0 *x + c1 */
	private float c0;
	private float c1;

	/** 定义域的起点 */
	private float s;

	/** 定义域的步距 */
	private float step;

	/** 需要生成的数据量，继承于该属性所属数据表的大小 */
	private int num;

	/** 定义域上的重复点信息列表 */
	private List<EqualIndex> indexs = null;
	
	//临时的重复点信息
	private List<EqualIndex> tempIndexs = null;
	
	//不等号对应的index点集合
	private HashSet<Integer> nonEquIndexSet = null;
	
	private HashMap<Integer,Constraint> constraintsMap = null;
	
	//对应的属性全名如A.a1
	private String attributeFullName = null;

	

	public AttrFunction(float c0, float c1, float s, float step, int num, List<EqualIndex> indexs,
			HashMap<Integer,Constraint> constraintsMap,String attributeFullName)
	{
		super();
		this.c0 = c0;
		this.c1 = c1;
		this.s = s;
		this.step = step;
		this.num = num;
		this.indexs = indexs;
		
		tempIndexs = new ArrayList<EqualIndex>();
		nonEquIndexSet = new HashSet<Integer>();
		
		this.lastValueOfX = s;
		//this.lastEqualIndexNo = -1;
		this.lastEqualIndexNo = 0;
		
		this.constraintsMap = constraintsMap;
		this.attributeFullName = attributeFullName;
	}
	
	/**
	 * 深复制一份AttrFunction
	 * @param attr
	 */
	public AttrFunction(AttrFunction attr)
	{
		this.c0 = attr.getC0();
		this.c1 = attr.getC1();
		this.s = attr.getS();
		this.step = attr.getStep();
		this.num = attr.getNum();
		this.indexs = new ArrayList<EqualIndex>(attr.getIndexs());
		this.tempIndexs = new ArrayList<EqualIndex>(attr.getTempIndexs());
		this.nonEquIndexSet = new HashSet<Integer>(attr.getNonEquIndexSet());
		this.lastValueOfX = s;
		//this.lastEqualIndexNo = -1;
		this.lastEqualIndexNo = 0;
		this.constraintsMap = new HashMap<Integer,Constraint>(attr.getConstraintsMap());
		this.attributeFullName = new String(attr.getAttributeFullName());
	}
	

	//上次的x值
	private double lastValueOfX;
	//上次所在等值段的编号
	private int lastEqualIndexNo;
	/**
	 * 输入为一个属性定义域的某个序i，返回该序的y值
	 * @param i 从0开始计数
	 * @return
	 */
	public Object getGeneData(int i)
	{
		if(i==0)
			return c0*lastValueOfX+c1;
		
		boolean flag = false;
		//for(int j=0;j<indexs.size();j++)
		for(int j=lastEqualIndexNo;j<indexs.size();j++)
		{
			EqualIndex ei = indexs.get(j);
			
			//test
			if(ei.getNum()==1)
			{
				//针对只有等值段只有1的特殊处理
				Constraint con = constraintsMap.get(ei.getConstraintId());
				if(!con.getIsFilled())
				{
					if(con.getInvolvedAttributes().size()==1)
					{
						// 求单表单属性等号约束的属性value
						con.putAttributeValue(attributeFullName, c0 * lastValueOfX + c1);
						con.setIsFilled(true);
					}else
					{
						con.putAttributeValue(attributeFullName, c0 * lastValueOfX + c1);
					}
				}
			}
			
			Section sec = new Section(ei.getIndex(),ei.getIndex()+ei.getNum());
			if(sec.contains(i+1) && j-lastEqualIndexNo>=0)
			{
				//进入某一等值段
				flag = true;
				lastEqualIndexNo = j;
				
				Constraint con = constraintsMap.get(ei.getConstraintId());
				if(!con.getIsFilled())
				{
					if(con.getInvolvedAttributes().size()==1)
					{
						// 求单表单属性等号约束的属性value
						//con.setAdjustParameter(c0 * lastValueOfX + c1);
						//con.setIsFilled(true);
						con.putAttributeValue(attributeFullName, c0 * lastValueOfX + c1);
						con.setIsFilled(true);
					}else
					{
						con.putAttributeValue(attributeFullName, c0 * lastValueOfX + c1);
					}
				}

				break;
			}
		}
		if(!flag)
		{
			lastValueOfX+=step;
		}
		
		return c0*lastValueOfX+c1;
	}
	
	
	
	/**
	 * 用于验证子问题三的一个生成方式
	 * @param i
	 * @return
	 */
	public Object verifyGeneration(int i)
	{
		double x = s+i*step;
		for(int j=0;j<indexs.size();j++)
		{
			EqualIndex ei = indexs.get(j);
			Section sec = new Section(ei.getIndex(),ei.getIndex()+ei.getNum());
			if(sec.contains(i+1) && (i+1)>ei.getIndex())
			{
				x=s+(ei.getIndex()-1)*step;
				
				Constraint con = constraintsMap.get(ei.getConstraintId());
				if(!con.getIsFilled())
				{
					if(con.getInvolvedAttributes().size()==1)
					{
						// 求单表单属性等号约束的属性value
						//con.setAdjustParameter(c0 * lastValueOfX + c1);
						//con.setIsFilled(true);
						con.putAttributeValue(attributeFullName, c0 * x + c1);
						con.setIsFilled(true);
					}else
					{
						con.putAttributeValue(attributeFullName, c0 * x + c1);
					}
				}
				
				break;
			}
				
		}
		return c0*x+c1;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 将指定的约束编号对应的临时重复点固化，并从临时重复点中删除相应的等值段
	 * @param constraintID
	 */
	public void addTempToIndexs(int constraintID)
	{
		List<EqualIndex> copyTempIndexs = new ArrayList<EqualIndex>(tempIndexs);
		for(EqualIndex ei : copyTempIndexs)
		{
			if(ei.getConstraintId()==constraintID)
			{
				indexs.add(ei);
				tempIndexs.remove(ei);
			}
		}
		
//		Iterator<EqualIndex> it = tempIndexs.iterator();
//		while(it.hasNext())
//		{
//			EqualIndex temp = it.next();
//			if(temp.getConstraintId()==constraintID)
//			{
//				indexs.add(new EqualIndex(temp));
//				it.remove();
//			}
//		}
	}
	
	/**
	 * 将输出的多个临时重复点信息固化
	 * @param eiList
	 */
	public void addTempToIndexs(List<EqualIndex> eiList)
	{
		indexs.addAll(eiList);
	}
	
	/**
	 * 将所有的临时重复点信息固化，并清空临时重复点信息
	 */
	public void addTempToIndexs()
	{
		indexs.addAll(new ArrayList<EqualIndex>(tempIndexs));
		tempIndexs.clear();
	}
	
	/**
	 * 将某个EqualIndex添加入临时重复点队列
	 * @param ei
	 */
	public void addTempIndexs(EqualIndex ei)
	{
		tempIndexs.add(ei);
	}
	
	/**
	 * 将不等号的index点添加入集合
	 * @param index
	 */
	public void addNonEquIndex(int index)
	{
		nonEquIndexSet.add(index);
	}
	
	/**
	 * 判断某个index点是否已存在
	 * @param index
	 * @return
	 */
	public boolean nonEquIndexSetContain(int index)
	{
		if(nonEquIndexSet.contains(index))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 清空tempIndexs列表
	 */
	public void clearTempIndexs()
	{
		tempIndexs.clear();
	}
	
	/**
	 * 对此AttrFunction的indexs进行排序，根据EqualIndex中index的大小，由小到大
	 */
	public void sort()
	{
		EqualIndexComparator eic = new EqualIndexComparator();
		Collections.sort(indexs, eic);
	}

	public float getC0()
	{
		return c0;
	}

	public float getC1()
	{
		return c1;
	}

	public float getS()
	{
		return s;
	}

	public float getStep()
	{
		return step;
	}
	public void setC0(float c0)
	{
		this.c0 = c0;
	}

	public void setC1(float c1)
	{
		this.c1 = c1;
	}

	public void setS(float s)
	{
		this.s = s;
	}

	public void setStep(float step)
	{
		this.step = step;
	}

	public int getNum()
	{
		return num;
	}
	public void setNum(int num)
	{
		this.num = num;
	}

	public List<EqualIndex> getIndexs()
	{
		return indexs;
	}
	
	public HashSet<Integer> getNonEquIndexSet()
	{
		return nonEquIndexSet;
	}

	public List<EqualIndex> getTempIndexs()
	{
		return tempIndexs;
	}
	
	public HashMap<Integer,Constraint> getConstraintsMap()
	{
		return constraintsMap;
	}
	
	public String getAttributeFullName()
	{
		return attributeFullName;
	}

	public void setAttributeFullName(String attributeFullName)
	{
		this.attributeFullName = attributeFullName;
	}
	
	@Override
	public String toString()
	{
		return "AttrFunction [c0=" + c0 + ", c1=" + c1 + ", s=" + s + ", step=" + step + ", num=" + num + ", indexs="
				+ indexs + ", tempIndexs=" + tempIndexs + ", nonEquIndexSet=" + nonEquIndexSet + ", lastValueOfX="
				+ lastValueOfX + ", lastEqualIndexNo=" + lastEqualIndexNo + "]";
	}

	public static void main(String[] args)
	{
//		AttrFunction attrFunction = new AttrFunction(2, 4, 12, 6, 1000,
//				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(23, 12,1))));
//		System.out.println(attrFunction);
	}
}



/**
 * 用于给AttrFunction中的EqualIndex数组进行排序
 * @author Gizing
 *
 */
class EqualIndexComparator implements Comparator<EqualIndex>
{

	@Override
	public int compare(EqualIndex o1, EqualIndex o2)
	{
		if (o1.getIndex() > o2.getIndex())
			return 1;
		else if (o1.getIndex() == o2.getIndex())
			return 0;
		else
			return -1;
	}
	
}