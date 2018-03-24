package com.datastructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import mathematica.Macro;
import mathematica.Mathematica;

/**
 * 基数约束
 * 
 * @author Gizing
 */
public class Constraint
{

	/** 编号 */
	private int id;

	/** 表达式信息数组 */
	private String[] expressions = null;

	/** 表达式类型信息数组 */
	private String[] expTypes = null;

	/** 中间结果集大小 */
	private int size;
	
	/** 此约束涉及属性的集合，格式如A.a1*/
	private HashSet<String> involvedAttributes = null;
	
	//等号前提下的类别枚举，
	//多表多属性等号约束multipletables 0，单表单属性等号约束singleattribute 1，单表多属性等号约束multipleattributes 2
	//为不等号时值为-1
	//private enum equalityType {multipletables,singleattribute,multipleattributes};
	private int equalityType;
	
	//多表多属性不等号约束0，单表单属性不等号约束1，单表多属性不等号约束2，为等号时值为-1
	private int nonEqualityType;
	
	//相关属性全名对应的value，用于计算调整参数
	private HashMap<String,Double> attributeValue = null;
	
	//此约束对应的调整参数和实际count
	private double adjustParameter;
	private int count;
	
	//true为已设置完value
	private boolean isFilled;
	
	//存单表类约束的非等值index点
	private int nonEquIndex;
	
	//上一层传来的有效定义域,每个相关属性对应一个有效的定义域(格式为:A.a1->[0,5])，Section有可能为null
	private HashMap<String,Section> validRange = null;


	public Constraint(int id, String[] expressions, String[] expTypes, int size)
	{
		super();
		this.id = id;
		this.expressions = expressions;
		this.expTypes = expTypes;
		this.size = size;
		solveInvolvedAttributes();
		if(expTypes[0].equals("="))
			setEqualityType();
		else
			setNonEqualityType();
		isFilled = false;
		
		attributeValue = new HashMap<String,Double>();
		
		validRange = new HashMap<String,Section>();
	}
	
	/**
	 * 设置不等号类别
	 */
	private void setNonEqualityType()
	{
		equalityType = -1;
		//单表单属性
		if(involvedAttributes.size()==1)
		{
			nonEqualityType = 1;
			return;
		}
		
		HashSet<String> tableSet = new HashSet<String>();
		Iterator<String> iter = involvedAttributes.iterator();
		while(iter.hasNext())
		{
			tableSet.add(iter.next().split("\\.")[0]);
		}
		if(tableSet.size()==1)
		{
			//单表多属性
			nonEqualityType = 2;
		}
		else
		{
			//多表多属性
			nonEqualityType = 0;
		}
	}
	
	/**
	 * 设置等号类别，如为不等号则值为-1
	 */
	private void setEqualityType()
	{
		nonEqualityType = -1;
		//单表单属性
		if(involvedAttributes.size()==1)
		{
			equalityType = 1;
			return;
		}
		
		HashSet<String> tableSet = new HashSet<String>();
		Iterator<String> iter = involvedAttributes.iterator();
		while(iter.hasNext())
		{
			String temp = iter.next();
			tableSet.add(temp.split("\\.")[0]);
		}
		if(tableSet.size()==1)
		{
			//单表多属性
			equalityType = 2;
		}
		else
		{
			//多表多属性
			equalityType = 0;
		}
	}
	
	/**
	 * 求此约束涉及属性的集合，格式如：A.a1
	 */
	private void solveInvolvedAttributes()
	{
		involvedAttributes = new HashSet<String>();
		for(String oneExpr:expressions)
		{
			//从表达式中提取出属性和数字
			String[] parts = oneExpr.split("[^.a-zA-Z0-9]");
			for(String piece:parts)
			{
				//不是数字则加入集合
				if(!piece.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$"))
				{
					involvedAttributes.add(piece.trim());
				}
			}
		}
	}
	
	/**
	 * 将属性全名和对应的值放入map中
	 * @param attributeName
	 * @param value
	 */
	public void putAttributeValue(String attributeName,double value)
	{
		if(!attributeValue.containsKey(attributeName))
			attributeValue.put(attributeName, value);
	}
	
	/**
	 * 计算此等号约束的调整参数，所有属性对应的value已求出
	 */
	public void calEquAdjustParameter()
	{
		//当数值很大时将java存储的值转为非科学记数法
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		//'.'是mathematica的关键字
		String expression = new String(expressions[0]);
		expression = expression.replaceAll("\\.", "");
		StringBuilder input = new StringBuilder(expression);
		input.append("/.{");
		for(String attr : involvedAttributes)
		{
			String temp = new String(attr);
			temp = temp.replaceAll("\\.", "");
			input.append(temp);
			input.append("->");
			
			//有可能不存在attr对应的double，则返回的为null
			//System.out.println(attr + "," + attributeValue.get(attr));
			if(attributeValue.get(attr) == null)
			{
				input.append(nf.format(1));
			}
			else
				input.append(nf.format(attributeValue.get(attr)));
			input.append(',');
		}
		input.deleteCharAt(input.length()-1);
		input.append('}');
		
		//System.out.println(input.toString());
		
		Mathematica mm = new Mathematica();
		//adjustParameter = Double.parseDouble(mm.getEquAdjustParameter(input.toString()));
		double startTime = System.currentTimeMillis();
		adjustParameter = mm.evaluate(input.toString());
		Macro.calculateTime+=System.currentTimeMillis()-startTime;
		isFilled = true;
		mm.close();
	}
	
	public int getEqualityType()
	{
		return equalityType;
	}

	public int getId()
	{
		return id;
	}

	public String[] getExpressions()
	{
		return expressions;
	}

	public String[] getExpTypes()
	{
		return expTypes;
	}

	public int getSize()
	{
		return size;
	}
	
	public HashSet<String> getInvolvedAttributes()
	{
		return involvedAttributes;
	}
	
	public int getNonEqualityType()
	{
		return nonEqualityType;
	}
	
	public void setAdjustParameter(double adjustParameter)
	{
		this.adjustParameter = adjustParameter;
	}
	public double getAdjustParameter()
	{
		return adjustParameter;
	}
	
	public void setIsFilled(boolean isFilled)
	{
		this.isFilled = isFilled;
	}
	public boolean getIsFilled()
	{
		return isFilled;
	}
	
	public HashMap<String,Double> getAttributeValue()
	{
		return attributeValue;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	public int getCount()
	{
		return count;
	}
	
	public void setNonEquIndex(int nonEquIndex)
	{
		this.nonEquIndex = nonEquIndex;
	}
	public int getNonEquIndex()
	{
		return nonEquIndex;
	}
	
	public HashMap<String,Section> getValidRange()
	{
		return this.validRange;
	}
	public void putRange(String attributeName,Section sec)
	{
		validRange.put(attributeName, sec);
	}
	
	@Override
	public String toString()
	{
		return "Constraint [id=" + id + ", expressions=" + Arrays.toString(expressions) + ", expTypes="
				+ Arrays.toString(expTypes) + ", size=" + size + ", involvedAttributes=" + involvedAttributes
				+ ", equalityType=" + equalityType + ", nonEqualityType=" + nonEqualityType + ", attributeValue="
				+ attributeValue + ", adjustParameter=" + adjustParameter + ", isFilled=" + isFilled + "]";
	}


}