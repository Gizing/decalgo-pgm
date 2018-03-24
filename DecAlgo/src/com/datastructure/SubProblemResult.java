package com.datastructure;

public class SubProblemResult
{
	//调整参数p1,p2....，子问题中的c
	private double adjustParameter;
	
	//子问题中c的序
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
