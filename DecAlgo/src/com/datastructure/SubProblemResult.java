package com.datastructure;

public class SubProblemResult
{
	//��������p1,p2....���������е�c
	private double adjustParameter;
	
	//��������c����
	private double cNo;
	
	public SubProblemResult(double adjustParameter,double cNo)
	{
		this.adjustParameter = adjustParameter;
		this.cNo = cNo;
	}
	
	public double getAdjustParameter()
	{
		return adjustParameter;
	}


	public void setAdjustParameter(double adjustParameter)
	{
		this.adjustParameter = adjustParameter;
	}


	public double getcNo()
	{
		return cNo;
	}


	public void setcNo(double cNo)
	{
		this.cNo = cNo;
	}


	@Override
	public String toString()
	{
		return "SubProblemResult [adjustParameter=" + adjustParameter + ", cNo=" + cNo + "]";
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
