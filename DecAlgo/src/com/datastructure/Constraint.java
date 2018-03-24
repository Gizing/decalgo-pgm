package com.datastructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import mathematica.Macro;
import mathematica.Mathematica;

/**
 * ����Լ��
 * 
 * @author Gizing
 */
public class Constraint
{

	/** ��� */
	private int id;

	/** ���ʽ��Ϣ���� */
	private String[] expressions = null;

	/** ���ʽ������Ϣ���� */
	private String[] expTypes = null;

	/** �м�������С */
	private int size;
	
	/** ��Լ���漰���Եļ��ϣ���ʽ��A.a1*/
	private HashSet<String> involvedAttributes = null;
	
	//�Ⱥ�ǰ���µ����ö�٣�
	//�������ԵȺ�Լ��multipletables 0���������ԵȺ�Լ��singleattribute 1����������ԵȺ�Լ��multipleattributes 2
	//Ϊ���Ⱥ�ʱֵΪ-1
	//private enum equalityType {multipletables,singleattribute,multipleattributes};
	private int equalityType;
	
	//�������Բ��Ⱥ�Լ��0���������Բ��Ⱥ�Լ��1����������Բ��Ⱥ�Լ��2��Ϊ�Ⱥ�ʱֵΪ-1
	private int nonEqualityType;
	
	//�������ȫ����Ӧ��value�����ڼ����������
	private HashMap<String,Double> attributeValue = null;
	
	//��Լ����Ӧ�ĵ���������ʵ��count
	private double adjustParameter;
	private int count;
	
	//trueΪ��������value
	private boolean isFilled;
	
	//�浥����Լ���ķǵ�ֵindex��
	private int nonEquIndex;
	
	//��һ�㴫������Ч������,ÿ��������Զ�Ӧһ����Ч�Ķ�����(��ʽΪ:A.a1->[0,5])��Section�п���Ϊnull
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
	 * ���ò��Ⱥ����
	 */
	private void setNonEqualityType()
	{
		equalityType = -1;
		//��������
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
			//���������
			nonEqualityType = 2;
		}
		else
		{
			//��������
			nonEqualityType = 0;
		}
	}
	
	/**
	 * ���õȺ������Ϊ���Ⱥ���ֵΪ-1
	 */
	private void setEqualityType()
	{
		nonEqualityType = -1;
		//��������
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
			//���������
			equalityType = 2;
		}
		else
		{
			//��������
			equalityType = 0;
		}
	}
	
	/**
	 * ���Լ���漰���Եļ��ϣ���ʽ�磺A.a1
	 */
	private void solveInvolvedAttributes()
	{
		involvedAttributes = new HashSet<String>();
		for(String oneExpr:expressions)
		{
			//�ӱ��ʽ����ȡ�����Ժ�����
			String[] parts = oneExpr.split("[^.a-zA-Z0-9]");
			for(String piece:parts)
			{
				//������������뼯��
				if(!piece.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$"))
				{
					involvedAttributes.add(piece.trim());
				}
			}
		}
	}
	
	/**
	 * ������ȫ���Ͷ�Ӧ��ֵ����map��
	 * @param attributeName
	 * @param value
	 */
	public void putAttributeValue(String attributeName,double value)
	{
		if(!attributeValue.containsKey(attributeName))
			attributeValue.put(attributeName, value);
	}
	
	/**
	 * ����˵Ⱥ�Լ���ĵ����������������Զ�Ӧ��value�����
	 */
	public void calEquAdjustParameter()
	{
		//����ֵ�ܴ�ʱ��java�洢��ֵתΪ�ǿ�ѧ������
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		//'.'��mathematica�Ĺؼ���
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
			
			//�п��ܲ�����attr��Ӧ��double���򷵻ص�Ϊnull
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