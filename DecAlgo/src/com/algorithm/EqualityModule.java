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
 * 等号模块，包含：
 * 1.生成 函数及定义域
 * 2.求影响关系
 * 3.等号信息注入定义域
 * 4.计算调整参数
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
	
	//影响关系,key是约束编号，value是key所影响的约束的编号集合
	private HashMap<Integer,HashSet<Integer>> influenceMap = null;
	
	//一次等号步骤中插入临时重复点信息的属性全名（带表名）集合，用于计算精度步骤的固化
	private HashSet<String> haveTempIndexsAttrSet = null;
	
	//一次计算精度步骤给出的不合格等号集合
	private HashSet<Integer> unqualifiedSet = null;

	
	/**
	 * 构造函数
	 * @param tables 表信息
	 * @param notDoIdSet 不参与处理约束编号集
	 * @param constraintsMap 约束信息
	 * @param dependencesMap 依赖信息
	 * @param layerInfo 约束层次信息
	 * @param groupInfo 属性组信息
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
	 * 随机生成属性对应的函数和定义域，结果存入属性的AttrFunction属性
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
				//step太大影响多表多属性的结果,为3时就不行了
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
	 * 求影响关系
	 * @return key是约束编号，value是key所影响的约束的编号集合
	 */
	public void influenceRelation()
	{
		influenceMap = new HashMap<Integer,HashSet<Integer>>();
		for(Map.Entry<Integer, Constraint> i:constraintsMap.entrySet())
		{
			//受影响的约束的编号集合
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
	 * 配套:求影响关系
	 * 判断编号i的约束和编号k的约束属性是否有交集，且i为等号，k为不等号
	 * @param i
	 * @param k
	 * @return 符合两个条件则返回true
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
	 * 配套:求影响关系
	 * 判断编号k的约束是否依赖编号i的约束
	 * @param i
	 * @param k
	 * @return k依赖i则返回true
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
	 * 整个第六步
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
			
			//记录每次迭代产生的所有临时EqualIndex，给后面判断最优提供固化源
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
				
				//将产生的临时结果放入allSituation,深复制
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
			//没有找到符合要求的值，将最优值固化
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
	 * 将不等号信息注入定义域
	 * @param curLayer 当前层次的约束编号
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
			//0代表多表多属性，调用子问题三
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
				
			//1代表单表单属性，调用子问题一
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
				
			//2代表单表多属性，调用子问题二
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
	 * 配套：计算不等值约束的index点、计算精度、计算调整参数
	 * 修改每个属性的定义域为有效定义域
	 * @param af 属性函数
	 * @param constraintID 此约束的编号
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
			//只能是单表类约束，取出约束的表名
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
	 * 根据表名返回相应Table
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
	 * 将等号信息注入定义域
	 * @param curLayer 当前层次的约束编号集合
	 * @return 当前层次的等号约束编号集合
	 */
	List<Integer> equalityInjection(HashSet<Integer> curLayer)
	{
		haveTempIndexsAttrSet = new HashSet<String>();
		//将等号约束取出并排序
		ArrayList<Integer> equConID = new ArrayList<Integer>();
		for(int a : curLayer)
		{
			if(constraintsMap.get(a).getExpTypes()[0].equals("="))
				equConID.add(a);
		}
		EqualityComparator ec = new EqualityComparator(constraintsMap);
		Collections.sort(equConID, ec);
		
		//清空当前层次等号约束的属性集中所有临时重复点信息List<EqualIndex>
		clearAllTempIndexs(equConID);
		
		for(int a : equConID)
		{
			Constraint con = constraintsMap.get(a);
			HashMap<String,Integer> attrLengthMap = solveEqualLength(con);
			switch(con.getEqualityType())
			{
			//多表多属性
			case 0:
				ArrayList<String> involvedAttributes = new ArrayList<String>(con.getInvolvedAttributes());
				Section range0 = null;
				for(String attributeName : involvedAttributes)
				{
					//冲突长度最优时对应的EqualIndex
					EqualIndex optimalIndex = new EqualIndex();
					//最优冲突长度
					int conflictLength = Integer.MAX_VALUE;
					
					//求此时属性函数的有效定义域范围
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
						
						//判断是否最优，是则更新最优对应的冲突长度和EqualIndex
						int result = optimalConflictLength(attributeName,index,num,conflictLength);
						if(result >= 0)
						{
							conflictLength = result;
							optimalIndex.setIndex(index);
							optimalIndex.setNum(attrLengthMap.get(attributeName));
						}
						
						//次数达到上限，将最优值插入临时List
						if(i+1==Macro.randomTimes)
						{
							getAttrFuncBy(attributeName).addTempIndexs(optimalIndex);
							haveTempIndexsAttrSet.add(attributeName);
						}
					}
				}
				break;
			
			//单表单属性
			case 1:
				ArrayList<String> involvedAttributes1 = new ArrayList<String>(con.getInvolvedAttributes());
				//冲突长度最优时对应的EqualIndex
				EqualIndex optimalIndex1 = new EqualIndex();
				//最优冲突长度
				int conflictLength1 = Integer.MAX_VALUE;
				String attributeName = involvedAttributes1.get(0);
				
				Section range1 = null;
				//最多有两个依赖
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
						//完全没有冲突，将随机到的index点和等值段长度插入临时List
						AttrFunction af = getAttrFuncBy(attributeName);
						af.addTempIndexs(new EqualIndex(index,num,a));
						haveTempIndexsAttrSet.add(attributeName);
						con.setNonEquIndex(index);
						//求此等号约束的调整参数
//						double adjustParameter = af.getC0()*(af.getS()+(index-1)*af.getStep())+af.getC1();
//						con.setAdjustParameter(adjustParameter);
						break;
					}
					//判断是否最优，是则更新最优对应的冲突长度和EqualIndex
					int result = optimalConflictLength(attributeName,index,num,conflictLength1);
					if(result >= 0)
					{
						conflictLength1 = result;
						optimalIndex1.setIndex(index);
						optimalIndex1.setNum(attrLengthMap.get(attributeName));
					}
					
					//次数达到上限，将最优值插入临时List
					if(i+1==Macro.randomTimes)
					{
						AttrFunction af = getAttrFuncBy(attributeName);
						af.addTempIndexs(optimalIndex1);
						haveTempIndexsAttrSet.add(attributeName);
						//求此等号约束的调整参数
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
	 * 求此时多表多属性约束的属性函数的有效定义域范围
	 * @param attributeName 属性名
	 * @param constraintID 属性所在约束的编号
	 * @return 如无依赖则返回null
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
			//默认多表多属性等值约束只会依赖单表单属性约束,最多两个
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
			//只有一个依赖
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
	 * 配套:将等号信息注入定义域
	 * 计算最优值
	 * @param attributeName 属性全名（包含表名）
	 * @param index
	 * @param equalLength
	 * @param conflictLength 目前最优冲突总长度
	 * @return 返回值为负数则不是最优
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
	 * 配套：将等号信息注入定义域
	 * 判断是否与等号约束冲突，不冲突则返回的队列为空
	 * @param attributeName 属性全名 如A.a1
	 * @param index	随机到的index点
	 * @param equalLength 属性的等值段长度
	 * @return 与输入index点和等值段长度冲突的EqualIndex的来源约束编号队列
	 */
	private List<Integer> getConflictList(String attributeName,int index,int equalLength)
	{
		List<Integer> conflictList = new ArrayList<Integer>();
		//String[] fullAttr = attributeName.split("\\.");
		//AttrFunction af = getTable(fullAttr[0]).indexOf(fullAttr[1]).getAttrFunction();
		AttrFunction af = getAttrFuncBy(attributeName);
		Section sec = new Section(index,index+equalLength);
		//与重复点信息判断
		for(EqualIndex ei : af.getIndexs())
			if(sec.isIntersected(ei.getIndex(), ei.getIndex()+ei.getNum()))
				conflictList.add(ei.getConstraintId());
		
		//与临时重复点信息判断
		for(EqualIndex ei : af.getTempIndexs())
			if(sec.isIntersected(ei.getIndex(), ei.getNum()+ei.getIndex()))
				conflictList.add(ei.getConstraintId());
		
		return conflictList;
	}
	
	/**
	 * 配套：将等号信息注入定义域
	 * 对输入的属性随机一个index点
	 * @param attributeName 全属性如A.a1
	 * @param num 该index的等值段长度
	 * @return 一个index点，从1开始计数
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
				//从range里面选
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
			//index点不能和不等号的index点重合且等值段在定义域
			if(!nonEquIndexSet.contains(index) && index+num<=size)
				break;
		}
		return index;
	}
	
	/**
	 * 配套：将等号信息注入定义域
	 * 针对等号类别求等值段长度
	 * @param constraint 某个等号约束
	 * @return key为此等号约束的属性（带表名），value为此属性上的等值段长度
	 */
	private HashMap<String,Integer> solveEqualLength(Constraint constraint)
	{
		HashMap<String,Integer> attrLengthMap = new HashMap<String,Integer>();
		switch(constraint.getEqualityType())
		{
		//多表多属性
		case 0:
			//序列化相关属性
			ArrayList<String> involvedAttributes = new ArrayList<String>(constraint.getInvolvedAttributes());
			//每个属性对应表的大小和相对第一个表的比例
			ArrayList<Integer> tableSizes = new ArrayList<Integer>();
			ArrayList<Double> tableRatio = new ArrayList<Double>();
			for(String oneAttr : involvedAttributes)
				tableSizes.add(getTable(oneAttr.split("\\.")[0]).getSize());
			
			//构建计算所需方程并计算
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
			
			
			//完成返回的结果
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
			
		//单表单属性
		case 1:
			for(String fullAttr : constraint.getInvolvedAttributes())
				attrLengthMap.put(fullAttr, constraint.getSize());
			break;
		
		//单表多属性不处理
		default:
			break;
		}
		return attrLengthMap;
	}
	
	/**
	 * 配套：将等号信息注入定义域
	 * 清空当前层次等号约束的属性集中所有临时重复点信息List<EqualIndex>
	 * @param equConID 当前层次的等号约束编号集合
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
	 * 根据输入的属性全名（如A.a1）返回相应的AttrFunction类
	 * @param attributeName
	 * @return
	 */
	private AttrFunction getAttrFuncBy(String attributeName)
	{
		String[] fullAttr = attributeName.split("\\.");
		return getTable(fullAttr[0]).indexOf(fullAttr[1]).getAttrFunction();
	}
	
	
	
	
	
	/**
	 * 计算精度
	 * @param curLayer 当前层次等号约束编号List
	 * @param layerCount 当前的层次号，第0层为0
	 * @return 不符合要求的相关属性所在分组，属性名中带有表名
	 */
	HashSet<HashSet<String>> calculateAccuracy(List<Integer> curLayer,int layerCount)
	{
		HashSet<HashSet<String>> result = new HashSet<HashSet<String>>();
		unqualifiedSet = new HashSet<Integer>();
		for(int a : curLayer)
		{
			//取出此等号约束影响的不等号约束List
			ArrayList<Integer> nonEquID = new ArrayList<Integer>();
			if(influenceMap.get(a) == null)
				continue;
			for(int b : influenceMap.get(a))
				if(!constraintsMap.get(b).getExpTypes()[0].equals("=") && isLower(layerCount,b))
					nonEquID.add(b);
			
			//判断等值段是否在定义域内
			
			
			//判断精度是否满足
			for(int b : nonEquID)
			{
				//精度达不到要求
				if(!isQualified(b))
				{
					//将此等号约束所在属性分组加入返回值队列
					Constraint con = constraintsMap.get(a);
					for(HashSet<String> oneGroup : groupInfo)
						if(oneGroup.containsAll(con.getInvolvedAttributes()))
							result.add(oneGroup);
					
					//将当前等号编号记入不合格集合
					unqualifiedSet.add(a);
					break;
				}
			}
		}
		//对不属于不合格集合的临时EqualIndex进行固化（正式写入定义域）
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
	 * 配套：计算精度
	 * 判断此约束是否不高于当前
	 * @param layerCount 当前等号约束所在层次的编号
	 * @param id2 当前等号约束所影响的约束的编号
	 * @return id2不高于id1则返回true
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
	 * 配套：计算精度
	 * 针对某一个不等号约束判断是否满足精度要求
	 * @param nonEquConID 不等号约束的编号
	 * @return
	 */
	private boolean isQualified(int nonEquConID)
	{
		Constraint con = constraintsMap.get(nonEquConID);
		int trueSize = con.getSize();
		SubProblemResult spr = null;
		switch(con.getNonEqualityType())
		{
		//多表多属性
		case 0:
			SubProblem3 sp3 = new SubProblem3();
			ArrayList<String> attrNameList0 = new ArrayList<String>(con.getInvolvedAttributes());
			//构建子问题三所需的AttrFunction
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
			
		//单表单属性
		case 1:
			SubProblem1 sp1 = new SubProblem1();
			ArrayList<String> attrNameList1 = new ArrayList<String>(con.getInvolvedAttributes());
			AttrFunction attrFunction1 = new AttrFunction(getAttrFuncBy(attrNameList1.get(0)));
			attrFunction1.addTempToIndexs();
			spr = sp1.solve(attrFunction1, attrNameList1.get(0), con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp1.clear();
			break;
		
		//单表多属性
		case 2:
			SubProblem2 sp2 = new SubProblem2();
			ArrayList<String> attrNameList2 = new ArrayList<String>(con.getInvolvedAttributes());
			//构建子问题二所需的AttrFunction
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
	 * 计算精度差值之和
	 * @param curLayer 当前层次不合格的等号约束编号集合
	 * @return 此次迭代不满足精度要求的差值之和
	 */
	long sumOfDifferenceValue()
	{
		long result = 0;
		for(int equID : unqualifiedSet)
		{
			//取出此等号约束影响的不等号约束List
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
	 * 配套：计算精度差值之和
	 * @param nonEquConID 不等号约束的编号
	 * @return 此不等号约束的基数与实际序之间的差值
	 */
	private int calculateDifferenceValue(int nonEquConID)
	{
		Constraint con = constraintsMap.get(nonEquConID);
		int trueSize = con.getSize();
		SubProblemResult spr = null;
		switch(con.getNonEqualityType())
		{
		//多表多属性
		case 0:
			SubProblem3 sp3 = new SubProblem3();
			ArrayList<String> attrNameList0 = new ArrayList<String>(con.getInvolvedAttributes());
			//构建子问题三所需的AttrFunction
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
			
		//单表单属性
		case 1:
			SubProblem1 sp1 = new SubProblem1();
			ArrayList<String> attrNameList1 = new ArrayList<String>(con.getInvolvedAttributes());
			AttrFunction attrFunction1 = new AttrFunction(getAttrFuncBy(attrNameList1.get(0)));
			spr = sp1.solve(attrFunction1, attrNameList1.get(0), con.getExpressions()[0], con.getExpTypes()[0], con.getSize());
			sp1.clear();
			break;
		
		//单表多属性
		case 2:
			SubProblem2 sp2 = new SubProblem2();
			ArrayList<String> attrNameList2 = new ArrayList<String>(con.getInvolvedAttributes());
			//构建子问题二所需的AttrFunction
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
	 * 计算不等号的调整参数
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
			//多表多属性
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
			
			//单表单属性
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
			
			//单表多属性
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
			// 对此约束设置调整参数
			con.setAdjustParameter(spr.getAdjustParameter());
			con.setCount((int)spr.getcNo());
		}
	}
	
	
	
	/**
	 * 将约束的表达式和调整参数输出
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
 * 用于对两个等号约束的排序，顺序：多表多属性->单表单属性->单表多属性
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
		//两个constraint都是等号
		if(con0.getEqualityType() == con1.getEqualityType())
			return 0;
		if(con0.getEqualityType() > con1.getEqualityType())
			return 1;
		return -1;
	}
}

