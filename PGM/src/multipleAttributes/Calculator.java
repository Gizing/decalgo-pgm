package multipleAttributes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Step4
 * @author Gizing
 *
 */
public class Calculator
{
	private ArrayList<HashSet<Integer>> maxCliques=null;
	private int tableSize;
	private VertexNode[] attributes = null;
	
	private StringBuilder variable = null;//�洢���̱�������formulaOneʱ�Ϳɵõ�
	private StringBuilder formulaOne = null;
	private StringBuilder varilargezero = null;//ÿ��ȡֵ����ĸ��ʶ�������ڵ���0
	
	private StringBuilder formulaTwo = null;
	private Constraint[] constraints = null;
	CalculateLink cl = null;
	
	private StringBuilder formulaThree = null;
	
	private StringBuilder Equations = null;
	private HashMap<String,Double> equationsResult = null;
	
	public Calculator(ArrayList<HashSet<Integer>> maxCliques,int tableSize,VertexNode[] attributes)
	{
		this.maxCliques=maxCliques;
		this.tableSize=tableSize;
		this.attributes=attributes;
		
		formulaOne = new StringBuilder();
		variable = new StringBuilder("{");
		varilargezero = new StringBuilder();
		formulaTwo = new StringBuilder();
		formulaThree = new StringBuilder();
		
		cl = new CalculateLink();
		
		Equations = new StringBuilder("N[FindInstance[{");
	}
	
	public void setConstraints(Constraint[] constraints)
	{
		this.constraints=constraints;
	}
	
	String[] getAttributesName(HashSet<Integer> temMaxClique)
	{
		int length = temMaxClique.size();
		String[] attributesName = new String[length];
		Iterator<Integer> it = temMaxClique.iterator();
		for(int i=0;i<length;i++)
		{
			attributesName[i]=attributes[it.next()-1].getAttributeName();
		}
		return attributesName;
	}
	
	
	
	
	/*
	 * Step4��������
	 */
	public void solveMarginalDistributions()
	{
		generateFormulaOne();
		generateFormulaTwo();
		generateFormulaThree();
		
		//��װ�ܵķ�����
		Equations.append(formulaOne);
		Equations.append(',');
		Equations.append(varilargezero);
		Equations.append(',');
		Equations.append(formulaTwo);
		if(formulaThree.length()!=0)
		{
			Equations.append(',');
			Equations.append(formulaThree);
		}
		Equations.append("},");
		Equations.append(variable);
		Equations.append("]]");
		
		equationsResult = cl.solveEquationsResult(Equations.toString());
		
		cl.closeLink();
	}
	
	
	
	
	
	
	/*
	 * Step4.1���ݹ�ʽ1���ɶ�Ԫһ�η���
	 */
	public void generateFormulaOne()
	{
		
		StringBuilder attPos = null;//��������±�
		//StringBuilder attDom = null;//���������ȡֵ
		DecimalFormat df = null;
		StringBuilder formatExample = null;
		Iterator<Integer> it = null;
		
		//���ÿһ��������
		for(int i=0,length = maxCliques.size();i< length;i++)
		{
			attPos = new StringBuilder("p");
			//attDom = new StringBuilder();
			formatExample = new StringBuilder();
			
			it=maxCliques.get(i).iterator();
			while(it.hasNext())
			{
				attPos.append(it.next().toString());
			}
			
			//����ÿ�����������ɷ���1
			int domain = (int)Math.pow(10,maxCliques.get(i).size());
			for(int j=0;j<maxCliques.get(i).size();j++)
			{
				formatExample.append("0");
			}
			df = new DecimalFormat(formatExample.toString());
			for(int j = 0;j < domain;j++)
			{
				//���̱�����װ
				variable.append(attPos);
				variable.append(df.format(j));
				
				//���Ϲ��ɱ�����ڵ���0��Լ��
				varilargezero.append(attPos);
				varilargezero.append(df.format(j));
				varilargezero.append(">=0");
				
				//������װ
				formulaOne.append(attPos);
				formulaOne.append(df.format(j));
				if(j+1==domain)
				{
					//һ�����̽���
					formulaOne.append("==1");
					
					
					//����������һ�����̾ͼӸ�����
					if(i+1!=length)
					{
						variable.append(',');
						varilargezero.append(',');
						formulaOne.append(',');
					}
					else
					{
						variable.append('}');
					}
					break;
				}
				else
				{
					variable.append(',');
					varilargezero.append(',');
					formulaOne.append('+');
				}
			}
			
			
		}
	}
	
	
	
	
	/*
	 * Step4.2���ݹ�ʽ2���ɶ�Ԫһ�η���
	 */
	public void generateFormulaTwo()
	{
		ArrayList<Integer> result = null;
		for(int i=0;i<constraints.length;i++)
		{
			result = isSubSet(constraints[i]);
			if(result.size()==0)
			{
				continue;
			}
			makeEquations2(constraints[i],result);
			
			//��û������
			if(i+1!=constraints.length)
			{
				formulaTwo.append(',');
			}
		}
		
	}
	
	//�ж�ĳ��Լ���Ƿ�ĳ�������ŵ��Ӽ������ؼ����ŵı��ArrayList,���ɹ�ArrayList��СΪ0
	ArrayList<Integer> isSubSet(Constraint conInput)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i=0,length=maxCliques.size();i<length;i++)
		{
			if(maxCliques.get(i).containsAll(conInput.attributeNo))
			{
				result.add(i);
			}
		}
		return result;
	}
	
	//����Լ���ͼ����ű�����ɷ���2
	void makeEquations2(Constraint conInput,ArrayList<Integer> maxCliquesNo)
	{
		
		String[] attributesName = null;
		int[] values = null;
		StringBuilder attPos = null;//��������±�
		//StringBuilder attDom = null;//���������ȡֵ
		HashSet<Integer> temMaxClique = null;
		Iterator<Integer> it = null;
		DecimalFormat df = null;
		StringBuilder formatExample = null;
		//CalculateLink cl = new CalculateLink();
		
		//һ��Լ�������ڶ����������
		for(int i=0,length=maxCliquesNo.size();i<length;i++)
		{
			temMaxClique = maxCliques.get(maxCliquesNo.get(i));
			attPos = new StringBuilder("p");
			//attDom = new StringBuilder();
			formatExample = new StringBuilder();
			it=temMaxClique.iterator();
			while(it.hasNext())
			{
				attPos.append(it.next().toString());
			}
			
			int temMaxCliLen = temMaxClique.size();
			int domain = (int)Math.pow(10,temMaxCliLen);
			for(int j=0;j<temMaxCliLen;j++)
			{
				formatExample.append("0");
			}
			df = new DecimalFormat(formatExample.toString());
			
			
			for(int j=0;j<domain;j++)
			{
				//��װ�����������ȡֵ����
				attributesName = getAttributesName(temMaxClique);
				values = new int[temMaxCliLen];
				int temj=j;
				for(int k=0;k<temMaxCliLen;k++)
				{
					values[k]=temj/(int)Math.pow(10,temMaxCliLen-1-k);
					temj-=values[k]*(int)Math.pow(10,temMaxCliLen-1-k);
				}
				
				if(cl.isFulfill(conInput.expr.toString(), attributesName, values))
				{
					//��ȡֵ����Լ��
					formulaTwo.append(attPos);
					formulaTwo.append(df.format(j));
					formulaTwo.append('+');
				}
				if(j+1==domain)
				{
					formulaTwo.deleteCharAt(formulaTwo.length()-1);
					//formulaTwo = formulaTwo.substring(0, formulaTwo.length()-1);
					formulaTwo.append("==");
					formulaTwo.append(conInput.num);
					formulaTwo.append('/');
					formulaTwo.append(tableSize);
				}
			}
			
			if(i+1!=length)
			{
				formulaTwo.append(',');
			}
			
		}
	}
	
	
	/*
	 * Step4.3���ݹ�ʽ3���ɶ�Ԫһ�η���
	 */
	public void generateFormulaThree()
	{
		int length = maxCliques.size();
		HashSet<Integer> result = new HashSet<Integer>();
		for(int i=0;i<length;i++)
		{
			for(int j=i+1;j<length;j++)
			{
				//�ж�i��j��ָ�ļ����ű���Ƿ��н���
				result.clear();
				result.addAll(maxCliques.get(i));
				result.retainAll(maxCliques.get(j));
				if(result.isEmpty())
				{
					continue;
				}
				
				//����������н���
				makeEquations3(result,i,j);
			}
		}
		if(formulaThree.length()!=0)
		{
			formulaThree.deleteCharAt(formulaThree.length()-1);
		}
	}
	
	//���ɷ���3
	void makeEquations3(HashSet<Integer> intersection,int maxCliNo1,int maxCliNo2)
	{
		//���������������Ա��
		StringBuilder attPosLeft = new StringBuilder("p");//��Ź�ʽ��������±�
		StringBuilder attPosRight = new StringBuilder("p");//��Ź�ʽ�ұ������±�
		Iterator<Integer> it=maxCliques.get(maxCliNo1).iterator();
		while(it.hasNext())
		{
			attPosLeft.append(it.next().toString());
		}
		it=maxCliques.get(maxCliNo2).iterator();
		while(it.hasNext())
		{
			attPosRight.append(it.next().toString());
		}
		
		//
		int domain = (int)Math.pow(10,intersection.size());
		StringBuilder formatExample = new StringBuilder();
		for(int j=0;j<intersection.size();j++)
		{
			formatExample.append("0");
		}
		DecimalFormat df = new DecimalFormat(formatExample.toString());
		StringBuilder tempResult = null;
		for(int i=0;i<domain;i++)
		{
			tempResult = new StringBuilder();
			
			//1.��߱��������򣬷��Ͻ�����ȡֵ���뷽��
			isFulfill(tempResult,attPosLeft,intersection,df.format(i));
			tempResult.append("==");
			
			//2.�ұ߱��������򣬷��Ͻ�����ȡֵ���뷽��
			isFulfill(tempResult,attPosRight,intersection,df.format(i));
			
			formulaThree.append(tempResult);
			formulaThree.append(',');
		}
	}
	
	//�ж�ĳ��ȡֵ�Ƿ���Ͻ�����ȡֵ��������װ����
	void isFulfill(StringBuilder output,StringBuilder attPos,HashSet<Integer> intersection,String intersecStr)
	{
		int domain = (int)Math.pow(10,attPos.length()-1);
		StringBuilder formatExample = new StringBuilder();
		for(int j=0;j<attPos.length()-1;j++)
		{
			formatExample.append("0");
		}
		DecimalFormat df = new DecimalFormat(formatExample.toString());
		Iterator<Integer> it = null;
		
		for(int i=0;i<domain;i++)
		{
			boolean flag = true;
			it = intersection.iterator();
			String domainStr = df.format(i);
			for(int j=0;j<intersection.size();j++)
			{
				int temIndex = attPos.indexOf(it.next().toString());
				if(domainStr.charAt(temIndex-1)!=intersecStr.charAt(j))
				{
					//��ʱȡֵ�뽻������
					flag = false;
				}
			}
			
			if(flag)
			{
				//ȡֵ���Ͻ���
				output.append(attPos);
				output.append(domainStr);
				output.append('+');
			}
		}
		
		output.deleteCharAt(output.length()-1);
	}
	
	
	
	public HashMap<String,Double> getEquationsResult()
	{
		return equationsResult;
	}
}
