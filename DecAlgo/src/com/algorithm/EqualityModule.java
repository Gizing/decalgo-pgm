package com.algorithm;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.datastructure.AttrFunction;
import com.datastructure.Attribute;
import com.datastructure.Constraint;
import com.datastructure.EqualIndex;
import com.datastructure.SubProblemResult;
import com.datastructure.Table;
import com.datastructure.Section;

import mathematica.Macro;
import mathematica.Mathematica;
import mathematica.SubProblem1;
import mathematica.SubProblem2;
import mathematica.SubProblem3;

/**
 * �Ⱥ�ģ�飬������
 * 1.���� ������������
 * 2.��Ӱ���ϵ
 * 3.�Ⱥ���Ϣע�붨����
 * 4.�����������
 * @author Gizing
 *
 */
public class EqualityModule
{
	private List<Table> tables = null;
	private HashSet<Integer> notDoIdSet = null;
	private HashMap<Integer,Constraint> constraintsMap = null;
	private HashMap<Integer,List<Integer>> dependencesMap = null;
	private ArrayList<HashSet<Integer>> layerInfo = null;
	private ArrayList<HashSet<String>> groupInfo = null;
	
	//Ӱ���ϵ,key��Լ����ţ�value��key��Ӱ���Լ���ı�ż���
	private HashMap<Integer,HashSet<Integer>> influenceMap = null;
	
	//һ�εȺŲ����в�����ʱ�ظ�����Ϣ������ȫ���������������ϣ����ڼ��㾫�Ȳ���Ĺ̻�
	private HashSet<String> haveTempIndexsAttrSet = null;
	
	//һ�μ��㾫�Ȳ�������Ĳ��ϸ�Ⱥż���
	private HashSet<Integer> unqualifiedSet = null;

	
	/**
	 * ���캯��
	 * @param tables ����Ϣ
	 * @param notDoIdSet �����봦��Լ����ż�
	 * @param constraintsMap Լ����Ϣ
	 * @param dependencesMap ������Ϣ
	 * @param layerInfo Լ�������Ϣ
	 * @param groupInfo ��������Ϣ
	 */
	public EqualityModule(List<Table> tables,HashSet<Integer> notDoIdSet,
			HashMap<Integer,Constraint> constraintsMap,
			HashMap<Integer,List<Integer>> dependencesMap,
			ArrayList<HashSet<Integer>> layerInfo,
			ArrayList<HashSet<String>> groupInfo)
	{
		this.tables = tables;
		this.notDoIdSet = notDoIdSet;
		this.constraintsMap = constraintsMap;
		this.dependencesMap = dependencesMap;
		this.layerInfo = layerInfo;
		this.groupInfo = groupInfo;
	}
	
	/**
	 * ����������Զ�Ӧ�ĺ����Ͷ����򣬽���������Ե�AttrFunction����
	 */
	public void generateFunction()
	{
		Random random = new Random();
		PrimeGenerator c1PrimeGenerator = new PrimeGenerator(random.nextInt(1000));
		PrimeGenerator sPrimeGenerator = new PrimeGenerator(random.nextInt(1000));
		List<Integer> primes = PrimeGenerator.getPrimes(100);
		for(Table table : tables) 
		{
			List<Attribute> attributes = table.getAttributes();
			for(Attribute attribute : attributes) 
			{
				float c0 = primes.get((int)(primes.size() * random.nextFloat()));
				float c1 = c1PrimeGenerator.getPrimes();
				float s = sPrimeGenerator.getPrimes();
				//float step = (int)(random.nextFloat() * 20);
				//step̫��Ӱ��������ԵĽ��,Ϊ3ʱ�Ͳ�����
				float step = (int)(random.nextFloat()+1);
				if(step == 0)	step++;
				if(attribute.getAttrType().equals("float") || attribute.getAttrType().equals("double") ||
						attribute.getAttrType().startsWith("decimal")) 
				{
					c0 += random.nextFloat();
					c1 += random.nextFloat();
					s += random.nextFloat();
					step += random.nextFloat();
				}
				AttrFunction attrFunction = new AttrFunction(c0, c1, s, step, table.getSize(), new ArrayList<EqualIndex>(),constraintsMap,table.getTableName()+'.'+attribute.getAttrName());
				attribute.setAttrFunction(attrFunction);
				
//				AttrFunction attrFunction = new AttrFunction(0, 0, 0, 0, table.getSize(), new ArrayList<EqualIndex>(),constraintsMap,table.getTableName()+'.'+attribute.getAttrName());
//				attribute.setAttrFunction(attrFunction);
			}
		}
		
//		tables.get(0).getAttributes().get(0).getAttrFunction().setC0(53);
//		tables.get(0).getAttributes().get(0).getAttrFunction().setC1(109);
//		tables.get(0).getAttributes().get(0).getAttrFunction().setS(659);
//		tables.get(0).getAttributes().get(0).getAttrFunction().setStep(2);
//		tables.get(0).getAttributes().get(1).getAttrFunction().setC0(97);
//		tables.get(0).getAttributes().get(1).getAttrFunction().setC1(113);
//		tables.get(0).getAttributes().get(1).getAttrFunction().setS(661);
//		tables.get(0).getAttributes().get(1).getAttrFunction().setStep(11);
//		tables.get(0).getAttributes().get(2).getAttrFunction().setC0(97);
//		tables.get(0).getAttributes().get(2).getAttrFunction().setC1(127);
//		tables.get(0).getAttributes().get(2).getAttrFunction().setS(673);
//		tables.get(0).getAttributes().get(2).getAttrFunction().setStep(17);
//		
//		tables.get(1).getAttributes().get(0).getAttrFunction().setC0(83);
//		tables.get(1).getAttributes().get(0).getAttrFunction().setC1(131);
//		tables.get(1).getAttributes().get(0).getAttrFunction().setS(677);
//		tables.get(1).getAttributes().get(0).getAttrFunction().setStep(9);
//		tables.get(1).getAttributes().get(1).getAttrFunction().setC0(71);
//		tables.get(1).getAttributes().get(1).getAttrFunction().setC1(137);
//		tables.get(1).getAttributes().get(1).getAttrFunction().setS(683);
//		tables.get(1).getAttributes().get(1).getAttrFunction().setStep(14);
//		
//		tables.get(2).getAttributes().get(0).getAttrFunction().setC0(67);
//		tables.get(2).getAttributes().get(0).getAttrFunction().setC1(139);
//		tables.get(2).getAttributes().get(0).getAttrFunction().setS(691);
//		tables.get(2).getAttributes().get(0).getAttrFunction().setStep(17);
//		tables.get(2).getAttributes().get(1).getAttrFunction().setC0(19);
//		tables.get(2).getAttributes().get(1).getAttrFunction().setC1(149);
//		tables.get(2).getAttributes().get(1).getAttrFunction().setS(701);
//		tables.get(2).getAttributes().get(1).getAttrFunction().setStep(1);
	}
	
	/**
	 * ��Ӱ���ϵ
	 * @return key��Լ����ţ�value��key��Ӱ���Լ���ı�ż���
	 */
	public void influenceRelation()
	{
		influenceMap = new HashMap<Integer,HashSet<Integer>>();
		for(Map.Entry<Integer, Constraint> i:constraintsMap.entrySet())
		{
			//��Ӱ���Լ���ı�ż���
			HashSet<Integer> influenceValue = new HashSet<Integer>();
			if(notDoIdSet.contains(i.getKey()))
				continue;
			for(Map.Entry<Integer, Constraint> k:constraintsMap.entrySet())
			{
				if(k.getKey().equals(i.getKey()) || notDoIdSet.contains(k.getKey()))
					continue;
				if(isDependent(i.getKey(),k.getKey()) || isIntersected(i.getKey(),k.getKey()))
					influenceValue.add(k.getKey());
			}
			
			if(influenceValue.size()!=0)
				influenceMap.put(i.getKey(), influenceValue);
		}

		//return influenceMap;
	}
	
	/**
	 * ����:��Ӱ���ϵ
	 * �жϱ��i��Լ���ͱ��k��Լ�������Ƿ��н�������iΪ�Ⱥţ�kΪ���Ⱥ�
	 * @param i
	 * @param k
	 * @return �������������򷵻�true
	 */
	private boolean isIntersected(int i,int k)
	{
		Constraint iCon = constraintsMap.get(i);
		Constraint kCon = constraintsMap.get(k);
		if(kCon.getExpTypes()[0].equals("=") || !iCon.getExpTypes()[0].equals("="))
			return false;
		HashSet<String> intersection = new HashSet<String>(iCon.getInvolvedAttributes());
		intersection.retainAll(kCon.getInvolvedAttributes());
		if(intersection.size()==0)
			return false;
		else
			return true;
	}
	
	/**
	 * ����:��Ӱ���ϵ
	 * �жϱ��k��Լ���Ƿ��������i��Լ��
	 * @param i
	 * @param k
	 * @return k����i�򷵻�true
	 */
	private boolean isDependent(int i,int k)
	{
		List<Integer> dependBySet = dependencesMap.get(k);
		if(dependBySet == null)
			return false;
		for(int temp : dependBySet)
			if(temp == i)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	/**
	 * ����������
	 */
	public void handleEquality()
	{
		for(int layerCount=0;layerCount<layerInfo.size();layerCount++)
		{
			HashSet<Integer> curLayer = layerInfo.get(layerCount);
			nonEqualityInjection(curLayer);
			List<Integer> equalityID = equalityInjection(curLayer);
			HashSet<HashSet<String>> unqualifiedGroupInfo = calculateAccuracy(equalityID,layerCount);
			if(unqualifiedGroupInfo.size()==0)
				continue;
			
			//��¼ÿ�ε���������������ʱEqualIndex���������ж������ṩ�̻�Դ
			ArrayList<HashMap<String,List<EqualIndex>>> allSituation = new ArrayList<HashMap<String,List<EqualIndex>>>();
			int optimalTime = 0;
			long optimalDifferenceValue = Long.MAX_VALUE;
			boolean isSatisfied = true;
			for(int i=0;i<Macro.calculateTimes;i++)
			{
				equalityID = equalityInjection(unqualifiedSet);
				unqualifiedGroupInfo = calculateAccuracy(equalityID,layerCount);
				if(unqualifiedGroupInfo.size()==0)
					break;
				
				//����������ʱ�������allSituation,���
				HashMap<String,List<EqualIndex>> element = new HashMap<String,List<EqualIndex>>();
				for(String attr : haveTempIndexsAttrSet)
				{
					AttrFunction af = getAttrFuncBy(attr);
					List<EqualIndex> tempIndexs = new ArrayList<EqualIndex>(af.getTempIndexs());
					element.put(attr, tempIndexs);
				}
				allSituation.add(element);
				
				long temp = sumOfDifferenceValue();
				if(temp < optimalDifferenceValue)
				{
					optimalDifferenceValue = temp;
					optimalTime = i;
				}
				if(i+1==Macro.calculateTimes)
					isSatisfied = false;
			}
			//û���ҵ�����Ҫ���ֵ��������ֵ�̻�
			if(!isSatisfied)
			{
				HashMap<String,List<EqualIndex>> optimalResult = allSituation.get(optimalTime);
				for(Map.Entry<String, List<EqualIndex>> entry : optimalResult.entrySet())
				{
					AttrFunction af = getAttrFuncBy(entry.getKey());
					af.addTempToIndexs(entry.getValue());
					af.clearTempIndexs();
				}
			}
		}
	}
	
	
	
	
	
	
	/**
	 * �����Ⱥ���Ϣע�붨����
	 * @param curLayer ��ǰ��ε�Լ�����
	 */
	private void nonEqualityInjection(HashSet<Integer> curLayer)
	{
		for(int id : curLayer)
		{
			Constraint curConstraint = constraintsMap.get(id);
			if(curConstraint.getExpTypes()[0].equals("="))
				continue;
			HashSet<String> involvedAttributes = curConstraint.getInvolvedAttributes();
			ArrayList<String> attrName = new ArrayList<String>(involvedAttributes);
			ArrayList<AttrFunction> attrFunctions = new ArrayList<AttrFunction>();
			switch(curConstraint.getNonEqualityType())
			{
			//0����������ԣ�������������
			case 0:
				for(String oneAttr : attrName)
				{
					AttrFunction af = new AttrFunction(getAttrFuncBy(oneAttr));
					modifyDomain(af,id);
					attrFunctions.add(af);
				}
				SubProblem3 sp3 = new SubProblem3();
				SubProblemResult spr3 = sp3.solve(attrFunctions, attrName,
						curConstraint.getExpressions()[0], curConstraint.getExpTypes()[0],
						curConstraint.getSize());
				sp3.clear();
				int nonEqualIndex3 = (int)Math.floor(spr3.getcNo()+0.5);
//				for(AttrFunction af : attrFunctions)
//					af.addNonEquIndex(nonEqualIndex3);
				for(String oneAttr : involvedAttributes)
					getAttrFuncBy(oneAttr).addNonEquIndex(nonEqualIndex3);
				break;
				
			//1���������ԣ�����������һ
			case 1:
				for(String oneAttr : attrName)
				{
//					String[] temp = oneAttr.split("\\.");
//					Table table = getTable(temp[0]);
//					attrFunctions.add(new AttrFunction(table.indexOf(temp[1]).getAttrFunction()));
					attrFunctions.add(new AttrFunction(getAttrFuncBy(oneAttr)));
				}
				SubProblem1 sp1 = new SubProblem1();
				//ArrayList<String> attrName = new ArrayList<String>(involvedAttributes);
				SubProblemResult spr1 = sp1.solve(attrFunctions.get(0), attrName.get(0),
						curConstraint.getExpressions()[0],
						curConstraint.getExpTypes()[0], curConstraint.getSize());
				sp1.clear();
				int nonEqualIndex1 = (int)Math.floor(spr1.getcNo()+0.5);
				//attrFunctions.get(0).addNonEquIndex(nonEqualIndex1);
				
				if(curConstraint.getExpTypes()[0].startsWith("<"))
				{
					curConstraint.setNonEquIndex(nonEqualIndex1);
					getAttrFuncBy(attrName.get(0)).addNonEquIndex(nonEqualIndex1);
				}
				else
				{
					curConstraint.setNonEquIndex(attrFunctions.get(0).getNum() - nonEqualIndex1);
					getAttrFuncBy(attrName.get(0)).addNonEquIndex(attrFunctions.get(0).getNum() - nonEqualIndex1);
				}
				break;
				
			//2����������ԣ������������
			case 2:
				for(String oneAttr : attrName)
				{
//					String[] temp = oneAttr.split("\\.");
//					Table table = getTable(temp[0]);
//					attrFunctions.add(new AttrFunction(table.indexOf(temp[1]).getAttrFunction()));
					attrFunctions.add(new AttrFunction(getAttrFuncBy(oneAttr)));
				}
				SubProblem2 sp2 = new SubProblem2();
				SubProblemResult spr2 = sp2.solve(attrFunctions, attrName,
						curConstraint.getExpressions()[0], curConstraint.getExpTypes()[0],
						curConstraint.getSize());
				sp2.clear();
				int nonEqualIndex2 = (int)Math.floor(spr2.getcNo()+0.5);
//				for(AttrFunction af : attrFunctions)
//					af.addNonEquIndex(nonEqualIndex2);
				for(String oneAttr : involvedAttributes)
					if(curConstraint.getExpTypes()[0].startsWith("<"))
					{
						curConstraint.setNonEquIndex(nonEqualIndex2);
						getAttrFuncBy(oneAttr).addNonEquIndex(nonEqualIndex2);
					}
					else
					{
						curConstraint.setNonEquIndex(attrFunctions.get(0).getNum() - nonEqualIndex2);
						getAttrFuncBy(oneAttr).addNonEquIndex(attrFunctions.get(0).getNum() - nonEqualIndex2);
					}
					
				break;
			
			default:
			}
		}
	}
	
	/**
	 * ���ף����㲻��ֵԼ����index�㡢���㾫�ȡ������������
	 * �޸�ÿ�����ԵĶ�����Ϊ��Ч������
	 * @param af ���Ժ���
	 * @param constraintID ��Լ���ı��
	 */
	private void modifyDomain(AttrFunction af,int constraintID)
	{
		List<Integer> temp = dependencesMap.get(constraintID);
		if(temp == null)
			return;
		class NonEquIndexType
		{
			NonEquIndexType(int nonEquIndex,String type){
				this.nonEquIndex = nonEquIndex;
				this.type = type;
			}
			int nonEquIndex;
			String type;
			int getNonEquIndex(){
				return nonEquIndex;
			}
			String getType(){
				return type;
			}
		}
		ArrayList<NonEquIndexType> points = new ArrayList<NonEquIndexType>(2);
		for(int a : temp)
		{
			//ֻ���ǵ�����Լ����ȡ��Լ���ı���
			String tableName = null;
			Constraint con = constraintsMap.get(a);
			for(Iterator<String> it = con.getInvolvedAttributes().iterator();it.hasNext();)
				tableName = it.next().split("\\.")[0];
			if(af.getAttributeFullName().startsWith(tableName))
				points.add(new NonEquIndexType(con.getNonEquIndex(),con.getExpTypes()[0]));
		}
		if(points.size()==0)
			return;
		if(points.size()==2)
		{
			int a = points.get(0).getNonEquIndex();
			int b = points.get(1).getNonEquIndex();
			af.setNum(Math.abs(a-b));
			if(a<b)
				af.setS(af.getS() + a*af.getStep());
			else
				af.setS(af.getS() + b*af.getStep());
			
		}else
		{
			if(points.get(0).getType().startsWith("<"))
				af.setNum(points.get(0).getNonEquIndex());
			else
			{
				af.setNum(af.getNum()-points.get(0).getNonEquIndex());
				af.setS(af.getS() + points.get(0).getNonEquIndex()*af.getStep());
			}
		}
	}
	
	/**
	 * ���ݱ���������ӦTable
	 * @param tableName
	 * @return
	 */
	private Table getTable(String tableName)
	{
		for(Table t : tables)
			if(tableName.equals(t.getTableName()))
				return t;
		return null;
	}
	
	
	
	
	
	
	/**
	 * ���Ⱥ���Ϣע�붨����
	 * @param curLayer ��ǰ��ε�Լ����ż���
	 * @return ��ǰ��εĵȺ�Լ����ż���
	 */
	List<Integer> equalityInjection(HashSet<Integer> curLayer)
	{
		haveTempIndexsAttrSet = new HashSet<String>();
		//���Ⱥ�Լ��ȡ��������
		ArrayList<Integer> equConID = new ArrayList<Integer>();
		for(int a : curLayer)
		{
			if(constraintsMap.get(a).getExpTypes()[0].equals("="))
				equConID.add(a);
		}
		EqualityComparator ec = new EqualityComparator(constraintsMap);
		Collections.sort(equConID, ec);
		
		//��յ�ǰ��εȺ�Լ�������Լ���������ʱ�ظ�����ϢList<EqualIndex>
		clearAllTempIndexs(equConID);
		
		for(int a : equConID)
		{
			Constraint con = constraintsMap.get(a);
			HashMap<String,Integer> attrLengthMap = solveEqualLength(con);
			switch(con.getEqualityType())
			{
			//��������
			case 0:
				ArrayList<String> involvedAttributes = new ArrayList<String>(con.getInvolvedAttributes());
				Section range0 = null;
				for(String attributeName : involvedAttributes)
				{
					//��ͻ��������ʱ��Ӧ��EqualIndex
					EqualIndex optimalIndex = new EqualIndex();
					//���ų�ͻ����
					int conflictLength = Integer.MAX_VALUE;
					
					//���ʱ���Ժ�������Ч������Χ
					range0 = getValidRange(attributeName,a);
					con.putRange(attributeName, range0);
					
					for(int i=0;i<Macro.randomTimes;i++)
					{
						int num = attrLengthMap.get(attributeName);
						int index = randomIndex(attributeName,num,range0);
						List<Integer> conflictList = getConflictList(attributeName,index,num);
						if(conflictList.size() == 0)
						{
							getAttrFuncBy(attributeName).addTempIndexs(new EqualIndex(index,num,a));
							haveTempIndexsAttrSet.add(attributeName);
							break;
						}
						
						//�ж��Ƿ����ţ�����������Ŷ�Ӧ�ĳ�ͻ���Ⱥ�EqualIndex
						int result = optimalConflictLength(attributeName,index,num,conflictLength);
						if(result >= 0)
						{
							conflictLength = result;
							optimalIndex.setIndex(index);
							optimalIndex.setNum(attrLengthMap.get(attributeName));
						}
						
						//�����ﵽ���ޣ�������ֵ������ʱList
						if(i+1==Macro.randomTimes)
						{
							getAttrFuncBy(attributeName).addTempIndexs(optimalIndex);
							haveTempIndexsAttrSet.add(attributeName);
						}
					}
				}
				break;
			
			//��������
			case 1:
				ArrayList<String> involvedAttributes1 = new ArrayList<String>(con.getInvolvedAttributes());
				//��ͻ��������ʱ��Ӧ��EqualIndex
				EqualIndex optimalIndex1 = new EqualIndex();
				//���ų�ͻ����
				int conflictLength1 = Integer.MAX_VALUE;
				String attributeName = involvedAttributes1.get(0);
				
				Section range1 = null;
				//�������������
				if(dependencesMap.get(a) != null)
				{
					List<Integer> temp = dependencesMap.get(a);
					if(temp.size()==1)
					{
						if(constraintsMap.get(temp.get(0)).getExpTypes()[0].startsWith(">"))
							range1 = new Section(constraintsMap.get(temp.get(0)).getNonEquIndex(),getAttrFuncBy(attributeName).getNum());
						else
							range1 = new Section(0, constraintsMap.get(temp.get(0)).getNonEquIndex());
					}
					else
					{
						if(constraintsMap.get(temp.get(0)).getNonEquIndex() > constraintsMap.get(temp.get(1)).getNonEquIndex())
							range1 = new Section(constraintsMap.get(temp.get(1)).getNonEquIndex(),constraintsMap.get(temp.get(0)).getNonEquIndex());
						else
							range1 = new Section(constraintsMap.get(temp.get(0)).getNonEquIndex(),constraintsMap.get(temp.get(1)).getNonEquIndex());
					}
				}
				con.putRange(attributeName, range1);
				
				for(int i=0;i<Macro.randomTimes;i++)
				{
					int num = con.getSize();
					int index = randomIndex(attributeName,num,range1);
					
					List<Integer> conflictList = getConflictList(attributeName,index,num);
					if(conflictList.size() == 0)
					{
						//��ȫû�г�ͻ�����������index��͵�ֵ�γ��Ȳ�����ʱList
						AttrFunction af = getAttrFuncBy(attributeName);
						af.addTempIndexs(new EqualIndex(index,num,a));
						haveTempIndexsAttrSet.add(attributeName);
						con.setNonEquIndex(index);
						//��˵Ⱥ�Լ���ĵ�������
//						double adjustParameter = af.getC0()*(af.getS()+(index-1)*af.getStep())+af.getC1();
//						con.setAdjustParameter(adjustParameter);
						break;
					}
					//�ж��Ƿ����ţ�����������Ŷ�Ӧ�ĳ�ͻ���Ⱥ�EqualIndex
					int result = optimalConflictLength(attributeName,index,num,conflictLength1);
					if(result >= 0)
					{
						conflictLength1 = result;
						optimalIndex1.setIndex(index);
						optimalIndex1.setNum(attrLengthMap.get(attributeName));
					}
					
					//�����ﵽ���ޣ�������ֵ������ʱList
					if(i+1==Macro.randomTimes)
					{
						AttrFunction af = getAttrFuncBy(attributeName);
						af.addTempIndexs(optimalIndex1);
						haveTempIndexsAttrSet.add(attributeName);
						//��˵Ⱥ�Լ���ĵ�������
//						double adjustParameter = af.getC0()*(af.getS()+(index-1)*af.getStep())+af.getC1();
//						con.setAdjustParameter(adjustParameter);
					}
				}
				break;
				
			default:
				break;
			}
		}
		return equConID;
	}
	
	/**
	 * ���ʱ��������Լ�������Ժ�������Ч������Χ
	 * @param attributeName ������
	 * @param constraintID ��������Լ���ı��
	 * @return ���������򷵻�null
	 */
	private Section getValidRange(String attributeName,int constraintID)
	{
		List<Integer> temp = dependencesMap.get(constraintID);
		if(temp == null)
			return null;
		Section sec = null;
		class NonEquIndexType
		{
			NonEquIndexType(int nonEquIndex,String type){
				this.nonEquIndex = nonEquIndex;
				this.type = type;
			}
			int nonEquIndex;
			String type;
			int getNonEquIndex(){
				return nonEquIndex;
			}
			String getType(){
				return type;
			}
		}
		ArrayList<NonEquIndexType> points = new ArrayList<NonEquIndexType>(2);
		for(int a : temp)
		{
			Constraint con = constraintsMap.get(a);
			//Ĭ�϶������Ե�ֵԼ��ֻ��������������Լ��,�������
			String name = null;
			for(Iterator<String> it = con.getInvolvedAttributes().iterator();it.hasNext();)
			{
				name = it.next();
				name = name.split("\\.")[0];
			}
			if(attributeName.startsWith(name))
				points.add(new NonEquIndexType(con.getNonEquIndex(),con.getExpTypes()[0]));
		}
		if(points.size()==0)
			return null;
		if(points.size()==2)
		{
			if(points.get(0).getNonEquIndex() < points.get(1).getNonEquIndex())
				sec = new Section(points.get(0).getNonEquIndex(),points.get(1).getNonEquIndex());
			else
				sec = new Section(points.get(1).getNonEquIndex(),points.get(0).getNonEquIndex());
		}else
		{
			//ֻ��һ������
			if(points.get(0).getType().startsWith("<"))
				sec = new Section(0,points.get(0).getNonEquIndex());
			else if(points.get(0).getType().startsWith("="))
				sec = new Section(points.get(0).getNonEquIndex(),points.get(0).getNonEquIndex());
			else
				sec = new Section(points.get(0).getNonEquIndex(),getAttrFuncBy(attributeName).getNum());
		}
		
		return sec;
	}
	
	/**
	 * ����:���Ⱥ���Ϣע�붨����
	 * ��������ֵ
	 * @param attributeName ����ȫ��������������
	 * @param index
	 * @param equalLength
	 * @param conflictLength Ŀǰ���ų�ͻ�ܳ���
	 * @return ����ֵΪ������������
	 */
	private int optimalConflictLength(String attributeName,int index,int equalLength,int conflictLength)
	{
		int result = 0;
		Section sec = new Section(index,index+equalLength);
		AttrFunction af = getAttrFuncBy(attributeName);
		for(EqualIndex ei : af.getIndexs())
			result+=sec.intersectLength(ei.getIndex(), ei.getIndex()+ei.getNum());
		for(EqualIndex ei: af.getTempIndexs())
			result+=sec.intersectLength(ei.getIndex(), ei.getIndex()+ei.getNum());
		
		if(result < conflictLength)
			return result;
		else
			return -1;

	}
	
	/**
	 * ���ף����Ⱥ���Ϣע�붨����
	 * �ж��Ƿ���Ⱥ�Լ����ͻ������ͻ�򷵻صĶ���Ϊ��
	 * @param attributeName ����ȫ�� ��A.a1
	 * @param index	�������index��
	 * @param equalLength ���Եĵ�ֵ�γ���
	 * @return ������index��͵�ֵ�γ��ȳ�ͻ��EqualIndex����ԴԼ����Ŷ���
	 */
	private List<Integer> getConflictList(String attributeName,int index,int equalLength)
	{
		List<Integer> conflictList = new ArrayList<Integer>();
		//String[] fullAttr = attributeName.split("\\.");
		//AttrFunction af = getTable(fullAttr[0]).indexOf(fullAttr[1]).getAttrFunction();
		AttrFunction af = getAttrFuncBy(attributeName);
		Section sec = new Section(index,index+equalLength);
		//���ظ�����Ϣ�ж�
		for(EqualIndex ei : af.getIndexs())
			if(sec.isIntersected(ei.getIndex(), ei.getIndex()+ei.getNum()))
				conflictList.add(ei.getConstraintId());
		
		//����ʱ�ظ�����Ϣ�ж�
		for(EqualIndex ei : af.getTempIndexs())
			if(sec.isIntersected(ei.getIndex(), ei.getNum()+ei.getIndex()))
				conflictList.add(ei.getConstraintId());
		
		return conflictList;
	}
	
	/**
	 * ���ף����Ⱥ���Ϣע�붨����
	 * ��������������һ��index��
	 * @param attributeName ȫ������A.a1
	 * @param num ��index�ĵ�ֵ�γ���
	 * @return һ��index�㣬��1��ʼ����
	 */
	private int randomIndex(String attributeName,int num,Section range)
	{
		String[] fullAttr = attributeName.split("\\.");
		Attribute attribute = getTable(fullAttr[0]).indexOf(fullAttr[1]);
		Random rand = new Random();
		int size = attribute.getAttrFunction().getNum();
		HashSet<Integer> nonEquIndexSet = attribute.getAttrFunction().getNonEquIndexSet();
		int index = 0;
		int i =20;
		while(i>0)
		{
			boolean flag = false;
			i--;
			if(range == null)
				index = rand.nextInt(size)+1;
			else if(range.getStart() != range.getEnd())
			{
				//��range����ѡ
				System.out.println("end:"+range.getEnd());
				System.out.println("start:"+range.getStart());
				System.out.println("num:"+num);
				if(range.getEnd()-range.getStart()-num<=0)
					index = range.getStart();
				else
					index = rand.nextInt(range.getEnd()-range.getStart()-num)+range.getStart();
				System.out.println("index:"+index);
			}else
			{
				index = range.getStart();
				System.out.println("index:"+index);
				break;
			}
			Section sec = new Section(index,index+num);
			for(int a : nonEquIndexSet)
			{
				if(range != null)
				{
					if(sec.contains(range.getStart()) || sec.contains(range.getEnd()))
					{
						flag = true;
						break;
					}
				}else if(sec.contains(a))
				{
					flag = true;
					break;
				}
				
			}
			if(flag)
				continue;
			//index�㲻�ܺͲ��Ⱥŵ�index���غ��ҵ�ֵ���ڶ�����
			if(!nonEquIndexSet.contains(index) && index+num<=size)
				break;
		}
		return index;
	}
	
	/**
	 * ���ף����Ⱥ���Ϣע�붨����
	 * ��ԵȺ�������ֵ�γ���
	 * @param constraint ĳ���Ⱥ�Լ��
	 * @return keyΪ�˵Ⱥ�Լ�������ԣ�����������valueΪ�������ϵĵ�ֵ�γ���
	 */
	private HashMap<String,Integer> solveEqualLength(Constraint constraint)
	{
		HashMap<String,Integer> attrLengthMap = new HashMap<String,Integer>();
		switch(constraint.getEqualityType())
		{
		//��������
		case 0:
			//���л��������
			ArrayList<String> involvedAttributes = new ArrayList<String>(constraint.getInvolvedAttributes());
			//ÿ�����Զ�Ӧ��Ĵ�С����Ե�һ����ı���
			ArrayList<Integer> tableSizes = new ArrayList<Integer>();
			ArrayList<Double> tableRatio = new ArrayList<Double>();
			for(String oneAttr : involvedAttributes)
				tableSizes.add(getTable(oneAttr.split("\\.")[0]).getSize());
			
			//�����������跽�̲�����
			StringBuilder equations = new StringBuilder("N[FindInstance[{");
			for(int a : tableSizes)
			{
				double b = (double)a/tableSizes.get(0);
				tableRatio.add(b);
				equations.append(String.valueOf(b));
				equations.append("x*");
			}
			equations.deleteCharAt(equations.length()-1);
			equations.append("==");
			equations.append(constraint.getSize());
			equations.append("},{x}]]");
			Mathematica mm = new Mathematica();
			int xValue = (int)Double.parseDouble(mm.attrEqualLength(equations.toString()));
			mm.close();
			
			
			//��ɷ��صĽ��
			attrLengthMap.put(involvedAttributes.get(0), xValue);
			for(int i=1;i<involvedAttributes.size()-1;i++)
			{
				int temp = (int)(xValue*tableRatio.get(i));
				if(temp<1)
					temp =1;
				attrLengthMap.put(involvedAttributes.get(i), temp);
			}
			int divided = 1;
			for(Map.Entry<String, Integer> entry : attrLengthMap.entrySet())
				if(entry.getValue()!=0)
					divided*=entry.getValue();
			attrLengthMap.put(involvedAttributes.get(involvedAttributes.size()-1), (int)(constraint.getSize()/divided));
			break;
			
		//��������
		case 1:
			for(String fullAttr : constraint.getInvolvedAttributes())
				attrLengthMap.put(fullAttr, constraint.getSize());
			break;
		
		//��������Բ�����
		default:
			break;
		}
		return attrLengthMap;
	}
	
	/**
	 * ���ף����Ⱥ���Ϣע�붨����
	 * ��յ�ǰ��εȺ�Լ�������Լ���������ʱ�ظ�����ϢList<EqualIndex>
	 * @param equConID ��ǰ��εĵȺ�Լ����ż���
	 */
	private void clearAllTempIndexs(ArrayList<Integer> equConID)
	{
		for(int a : equConID)
		{
			Constraint con = constraintsMap.get(a);
			for(String temp : con.getInvolvedAttributes())
			{
				String[] fullAttr = temp.split("\\.");
				Table table = getTable(fullAttr[0]);
				table.indexOf(fullAttr[1]).getAttrFunction().clearTempIndexs();
			}
		}
	}
	
	/**
	 * �������������ȫ������A.a1��������Ӧ��AttrFunction��
	 * @param attributeName
	 * @return
	 */
	private AttrFunction getAttrFuncBy(String attributeName)
	{
		String[] fullAttr = attributeName.split("\\.");
		return getTable(fullAttr[0]).indexOf(fullAttr[1]).getAttrFunction();
	}
	
	
	
	
	
	/**
	 * ���㾫��
	 * @param curLayer ��ǰ��εȺ�Լ�����List
	 * @param layerCount ��ǰ�Ĳ�κţ���0��Ϊ0
	 * @return ������Ҫ�������������ڷ��飬�������д��б���
	 */
	HashSet<HashSet<String>> calculateAccuracy(List<Integer> curLayer,int layerCount)
	{
		HashSet<HashSet<String>> result = new HashSet<HashSet<String>>();
		unqualifiedSet = new HashSet<Integer>();
		for(int a : curLayer)
		{
			//ȡ���˵Ⱥ�Լ��Ӱ��Ĳ��Ⱥ�Լ��List
			ArrayList<Integer> nonEquID = new ArrayList<Integer>();
			if(influenceMap.get(a) == null)
				continue;
			for(int b : influenceMap.get(a))
				if(!constraintsMap.get(b).getExpTypes()[0].equals("=") && isLower(layerCount,b))
					nonEquID.add(b);
			
			//�жϵ�ֵ���Ƿ��ڶ�������
			
			
			//�жϾ����Ƿ�����
			for(int b : nonEquID)
			{
				//���ȴﲻ��Ҫ��
				if(!isQualified(b))
				{
					//���˵Ⱥ�Լ���������Է�����뷵��ֵ����
					Constraint con = constraintsMap.get(a);
					for(HashSet<String> oneGroup : groupInfo)
						if(oneGroup.containsAll(con.getInvolvedAttributes()))
							result.add(oneGroup);
					
					//����ǰ�Ⱥű�ż��벻�ϸ񼯺�
					unqualifiedSet.add(a);
					break;
				}
			}
		}
		//�Բ����ڲ��ϸ񼯺ϵ���ʱEqualIndex���й̻�����ʽд�붨����
		for(int a : curLayer)
		{
			if(unqualifiedSet.contains(a))
				continue;
			for(String attr : haveTempIndexsAttrSet)
			{
				getAttrFuncBy(attr).addTempToIndexs(a);
				//Constraint con = constraintsMap.get(a);
				
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * ���ף����㾫��
	 * �жϴ�Լ���Ƿ񲻸��ڵ�ǰ
	 * @param layerCount ��ǰ�Ⱥ�Լ�����ڲ�εı��
	 * @param id2 ��ǰ�Ⱥ�Լ����Ӱ���Լ���ı��
	 * @return id2������id1�򷵻�true
	 */
	private boolean isLower(int layerCount, int id2)
	{
		if(layerCount == layerInfo.size()-1)
			return true;
		
		for(int i=layerCount+1;i<layerInfo.size();i++)
		{
			if(layerInfo.get(i).contains(id2))
				return false;
		}
		return true;
	}
	
	
	/**
	 * ���ף����㾫��
	 * ���ĳһ�����Ⱥ�Լ���ж��Ƿ����㾫��Ҫ��
	 * @param nonEquConID ���Ⱥ�Լ���ı��
	 * @return
	 */
	private boolean isQualified(int nonEquConID)
	{
		Constraint con = constraintsMap.get(nonEquConID);
		int trueSize = con.getSize();
		SubProblemResult spr = null;
		switch(con.getNonEqualityType())
		{
		//��������
		case 0:
			SubProblem3 sp3 = new SubProblem3();
			ArrayList<String> attrNameList0 = new ArrayList<String>(con.getInvolvedAttributes());
			//�����������������AttrFunction
			ArrayList<AttrFunction> attrFunction0 = new ArrayList<AttrFunction>();
			for(String str : attrNameList0)
			{
				AttrFunction attrf = new AttrFunction(getAttrFuncBy(str));
				attrf.addTempToIndexs();
				modifyDomain(attrf,nonEquConID);
				attrFunction0.add(attrf);
			}
			
			spr = sp3.solve(attrFunction0, attrNameList0, con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp3.clear();
			break;
			
		//��������
		case 1:
			SubProblem1 sp1 = new SubProblem1();
			ArrayList<String> attrNameList1 = new ArrayList<String>(con.getInvolvedAttributes());
			AttrFunction attrFunction1 = new AttrFunction(getAttrFuncBy(attrNameList1.get(0)));
			attrFunction1.addTempToIndexs();
			spr = sp1.solve(attrFunction1, attrNameList1.get(0), con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp1.clear();
			break;
		
		//���������
		case 2:
			SubProblem2 sp2 = new SubProblem2();
			ArrayList<String> attrNameList2 = new ArrayList<String>(con.getInvolvedAttributes());
			//����������������AttrFunction
			ArrayList<AttrFunction> attrFunction2 = new ArrayList<AttrFunction>();
			for(String str : attrNameList2)
			{
				AttrFunction attrf = new AttrFunction(getAttrFuncBy(str));
				attrf.addTempToIndexs();
				attrFunction2.add(attrf);
			}
			
			spr = sp2.solve(attrFunction2, attrNameList2, con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp2.clear();
			break;
		
		default:
			break;
		}
		
		if((double)Math.abs(trueSize-spr.getcNo())/trueSize <= Macro.nonEquPrecision)
			return true;
		else
			return false;
	}
	
	
	
	
	/**
	 * ���㾫�Ȳ�ֵ֮��
	 * @param curLayer ��ǰ��β��ϸ�ĵȺ�Լ����ż���
	 * @return �˴ε��������㾫��Ҫ��Ĳ�ֵ֮��
	 */
	long sumOfDifferenceValue()
	{
		long result = 0;
		for(int equID : unqualifiedSet)
		{
			//ȡ���˵Ⱥ�Լ��Ӱ��Ĳ��Ⱥ�Լ��List
			ArrayList<Integer> nonEquIDList = new ArrayList<Integer>();
			if(influenceMap.get(equID) == null)
				continue;
			for(int b : influenceMap.get(equID))
				if(!constraintsMap.get(b).getExpTypes()[0].equals("="))
					nonEquIDList.add(b);
			
			for(int b : nonEquIDList)
			{
				result+=calculateDifferenceValue(b);
			}
		}
		return result;
	}
	
	/**
	 * ���ף����㾫�Ȳ�ֵ֮��
	 * @param nonEquConID ���Ⱥ�Լ���ı��
	 * @return �˲��Ⱥ�Լ���Ļ�����ʵ����֮��Ĳ�ֵ
	 */
	private int calculateDifferenceValue(int nonEquConID)
	{
		Constraint con = constraintsMap.get(nonEquConID);
		int trueSize = con.getSize();
		SubProblemResult spr = null;
		switch(con.getNonEqualityType())
		{
		//��������
		case 0:
			SubProblem3 sp3 = new SubProblem3();
			ArrayList<String> attrNameList0 = new ArrayList<String>(con.getInvolvedAttributes());
			//�����������������AttrFunction
			ArrayList<AttrFunction> attrFunction0 = new ArrayList<AttrFunction>();
			for(String str : attrNameList0)
			{
				AttrFunction attrf = new AttrFunction(getAttrFuncBy(str));
				attrf.addTempToIndexs();
				attrFunction0.add(attrf);
			}
			
			spr = sp3.solve(attrFunction0, attrNameList0, con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp3.clear();
			break;
			
		//��������
		case 1:
			SubProblem1 sp1 = new SubProblem1();
			ArrayList<String> attrNameList1 = new ArrayList<String>(con.getInvolvedAttributes());
			AttrFunction attrFunction1 = new AttrFunction(getAttrFuncBy(attrNameList1.get(0)));
			spr = sp1.solve(attrFunction1, attrNameList1.get(0), con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp1.clear();
			break;
		
		//���������
		case 2:
			SubProblem2 sp2 = new SubProblem2();
			ArrayList<String> attrNameList2 = new ArrayList<String>(con.getInvolvedAttributes());
			//����������������AttrFunction
			ArrayList<AttrFunction> attrFunction2 = new ArrayList<AttrFunction>();
			for(String str : attrNameList2)
			{
				AttrFunction attrf = new AttrFunction(getAttrFuncBy(str));
				attrf.addTempToIndexs();
				attrFunction2.add(attrf);
			}
			
			spr = sp2.solve(attrFunction2, attrNameList2, con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp2.clear();
			break;
		
		default:
			break;
		}
		
		return Math.abs(trueSize-(int)spr.getcNo());
	}
	
	public ArrayList<HashSet<String>> getGroupInfo()
	{
		return groupInfo;
	}
	
	
	
	
	/**
	 * ���㲻�Ⱥŵĵ�������
	 * 
	 */
	public void calNonEquAdjustParameter()
	{
		for(Map.Entry<Integer,Constraint> entry : constraintsMap.entrySet())
		{
			Constraint con = entry.getValue();
			if(con.getExpTypes()[0].equals("="))
				continue;
			
			SubProblemResult spr = null;
			double startTime = 0;
			switch (con.getNonEqualityType())
			{
			//��������
			case 0:
				ArrayList<AttrFunction> attrFunctions0 = new ArrayList<AttrFunction>();
				ArrayList<String> attrNames0 = new ArrayList<String>(con.getInvolvedAttributes());
				for (String attr : attrNames0)
				{
					AttrFunction af = new AttrFunction(getAttrFuncBy(attr)); 
					modifyDomain(af,con.getId());
					attrFunctions0.add(af);
				}
				SubProblem3 sp3 = new SubProblem3();
				startTime = System.currentTimeMillis();
				spr = sp3.solve(attrFunctions0, attrNames0, con.getExpressions()[0], con.getExpTypes()[0],
						con.getSize());
				sp3.clear();
				Macro.calculateTime+=System.currentTimeMillis()-startTime;
				break;
			
			//��������
			case 1:
				ArrayList<AttrFunction> attrFunctions1 = new ArrayList<AttrFunction>();
				ArrayList<String> attrNames1 = new ArrayList<String>(con.getInvolvedAttributes());
				for (String attr : attrNames1)
					attrFunctions1.add(new AttrFunction(getAttrFuncBy(attr)));
				SubProblem1 sp1 = new SubProblem1();
				startTime = System.currentTimeMillis();
				spr = sp1.solve(attrFunctions1.get(0), attrNames1.get(0), con.getExpressions()[0], con.getExpTypes()[0],
						con.getSize());
				sp1.clear();
				Macro.calculateTime+=System.currentTimeMillis()-startTime;
				break;
			
			//���������
			case 2:
				ArrayList<AttrFunction> attrFunctions2 = new ArrayList<AttrFunction>();
				ArrayList<String> attrNames2 = new ArrayList<String>(con.getInvolvedAttributes());
				for (String attr : attrNames2)
					attrFunctions2.add(new AttrFunction(getAttrFuncBy(attr)));
				SubProblem2 sp2 = new SubProblem2();
				startTime = System.currentTimeMillis();
				spr = sp2.solve(attrFunctions2, attrNames2, con.getExpressions()[0], con.getExpTypes()[0],
						con.getSize());
				sp2.clear();
				Macro.calculateTime+=System.currentTimeMillis()-startTime;
				break;

			default:
				break;
			}
			// �Դ�Լ�����õ�������
			con.setAdjustParameter(spr.getAdjustParameter());
			con.setCount((int)spr.getcNo());
		}
	}
	
	
	
	/**
	 * ��Լ���ı��ʽ�͵����������
	 */
	public void outputAdjustParameter()
	{
		Properties prop = new Properties();
		FileInputStream fis = null;
		StringBuilder path = null;
		PrintWriter pw = null;
		try
		{
			fis = new FileInputStream("./src/configure.properties");
			prop.load(fis);
			path = new StringBuilder(prop.getProperty("outputDataDirectory"));
			path.append("adjustParameter.txt");
			pw = new PrintWriter(new BufferedWriter(new FileWriter(path.toString())));
			
			for(Map.Entry<Integer, Constraint> entry : constraintsMap.entrySet())
			{
				Constraint con = entry.getValue();
				pw.print(con.getId());
				pw.print("\t\t");
				pw.print(con.getSize());
				pw.print("\t\t\t");
				pw.print(con.getExpressions()[0]);
				pw.print(con.getExpTypes()[0]);
				pw.print(con.getAdjustParameter());
				pw.print("\t\t\t");
				pw.println(con.getCount());
			}
			pw.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
				if(fis!=null)
					fis.close();
				if(pw!=null)
					pw.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args)
	{
		
	}

}


/**
 * ���ڶ������Ⱥ�Լ��������˳�򣺶�������->��������->���������
 * @author Gizing
 *
 */
class EqualityComparator implements Comparator<Integer>
{
	private HashMap<Integer,Constraint> constraintsMap = null;
	
	public EqualityComparator(HashMap<Integer,Constraint> constraintsMap)
	{
		this.constraintsMap = constraintsMap;
	}

	@Override
	public int compare(Integer arg0, Integer arg1)
	{
		Constraint con0 = constraintsMap.get(arg0);
		Constraint con1 = constraintsMap.get(arg1);
		//����constraint���ǵȺ�
		if(con0.getEqualityType() == con1.getEqualityType())
			return 0;
		if(con0.getEqualityType() > con1.getEqualityType())
			return 1;
		return -1;
	}
}

