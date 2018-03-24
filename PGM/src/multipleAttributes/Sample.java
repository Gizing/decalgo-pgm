package multipleAttributes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.sqltest.SQLLink;

/**
 * 生成数据库实例的类
 * @author Gizing
 *
 */
public class Sample
{
	private VertexNode[] vn = null;
	private ArrayList<HashSet<Integer>> maxCliques = null;
	//private boolean[] maxCliquesVisited = null;//每个极大团对应的访问状态，默认为false未访问过
	private HashMap<String,Double> equationsResult = null;
	private int tableSize;
	private String tableName = null;
	private HashSet<Integer> sampleDoneAtt = null;//已有取样值的属性的编号
	
	private HashMap<String,String[]> cliqueProbabilityArrayMap = null;//一组极大团对应的取值数组，如果取值00的概率为1/2则数组有一半个数的内容存的是00
	
	private FileWriter fw = null;
	private BufferedWriter bw = null;
	
	public Sample(VertexNode[] vn,ArrayList<HashSet<Integer>> maxCliques,HashMap<String,Double> equationsResult,int tableSize,String tableName)
	{
		this.vn = vn;
		this.maxCliques = maxCliques;
		//this.maxCliquesVisited = new boolean[maxCliques.size()];
		this.equationsResult = equationsResult;
		this.tableSize = tableSize;
		this.tableName = tableName;
		sampleDoneAtt = new HashSet<Integer>();
		cliqueProbabilityArrayMap = new HashMap<String,String[]>();
		
		
	}
	
	
	void doSample()
	{
		try
		{
			fw = new FileWriter("g:/data.txt",true);
			bw = new BufferedWriter(fw);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//根据极大团大小对极大团的ArrayList进行降序排序
		HashSetSizeComparator hsc = new HashSetSizeComparator();
		Collections.sort(maxCliques, hsc);
		generateCliProArrMap();
		
		for(int i=0;i<this.tableSize;i++)
		{
			sampleOnce();
		}
		
		//关闭文件
		try
		{
			bw.close();
			fw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//将文件中的数据固化到数据库中
		importToMySQL();
	}
	
	/**
	 * 根据每个取值的概率大小，根据比例生成相应数量的数组元素（内容都存放相应的取值）
	 */
	void generateCliProArrMap()
	{
		for(HashSet<Integer> temp:maxCliques)
		{
			//组建每一个极大团的符号前缀
			StringBuilder cliqueExample = new StringBuilder("p");
			Iterator<Integer> it = temp.iterator();
			while(it.hasNext())
			{
				cliqueExample.append(it.next());
			}
			
			int domain = (int)Math.pow(10,temp.size());
			String[] probabilityArray = new String[domain];
			int index =0;
			for(Entry<String,Double> entry:equationsResult.entrySet())
			{
				if(entry.getKey().startsWith(cliqueExample.toString()))
				{
					int sum = (int)(entry.getValue()*domain);
					if(sum<1)
					{
						continue;
					}
					String value = entry.getKey();
					value = value.substring((value.length()+1)/2);
					for(;sum>0;sum--,index++)
					{
						probabilityArray[index] = value;
					}
				}
			}
			cliqueProbabilityArrayMap.put(cliqueExample.toString(), probabilityArray);
		}
	}
	
	
	/**
	 * sample一个元组，并存入文件，
	 * 不足：前提是弦图的每个点都不孤立，极大团之间一定有交集，不存在(1,2,3) (1,4) (5)这样的极大团集合
	 */
	void sampleOnce()
	{
		//第一个极大团取样
		HashSet<Integer> curClique = maxCliques.get(0);
		StringBuilder curCliqueStr = new StringBuilder("p");
		Iterator<Integer> it = curClique.iterator();
		while(it.hasNext())
		{
			curCliqueStr.append(it.next());
		}
		int temp = (int)(cliqueProbabilityArrayMap.get(curCliqueStr.toString()).length*Math.random());
		String value = cliqueProbabilityArrayMap.get(curCliqueStr.toString())[temp];
		setVertexNodeValue(curCliqueStr.toString(),value);

		for (int i = 1, length = maxCliques.size(); i < length; i++)
		{
			curClique = maxCliques.get(i);
			HashSet<Integer> intersect = isIntersected(sampleDoneAtt, curClique);
			if (intersect.size() == curClique.size())
			{
				// 不需要取样
				continue;
			} else if (intersect.size() == 0)
			{
				// 独立取值
				curCliqueStr = new StringBuilder("p");
				it = curClique.iterator();
				while(it.hasNext())
				{
					curCliqueStr.append(it.next());
				}
				temp = (int)(cliqueProbabilityArrayMap.get(curCliqueStr.toString()).length*Math.random());
				value = cliqueProbabilityArrayMap.get(curCliqueStr.toString())[temp];
				setVertexNodeValue(curCliqueStr.toString(),value);

			} else
			{
				// 有条件取样
				curCliqueStr = new StringBuilder("p");
				it = curClique.iterator();
				while(it.hasNext())
				{
					curCliqueStr.append(it.next());
				}
				while(true)
				{
					temp = (int)(cliqueProbabilityArrayMap.get(curCliqueStr.toString()).length*Math.random());
					value = cliqueProbabilityArrayMap.get(curCliqueStr.toString())[temp];
					
					//判断是否符合条件，符合则取样，不符合重新取样
					if(isFulfillCondition(intersect,curCliqueStr.toString(),value))
					{
						setVertexNodeValue(curCliqueStr.toString(),value);
						break;
					}
				}
				
			}

		}
			
		
		//将一次sample的取值存入文件
		writeInFile();
		
	}
	
	
	/**
	 * 将一次sample的取值存入文件
	 */
	void writeInFile()
	{
		StringBuilder input = new StringBuilder();
		for(int i=0;i<vn.length;i++)
		{
			input.append(vn[i].getValue());
			input.append('\t');
		}
		input.deleteCharAt(input.length()-1);
		try
		{
			bw.write(input.toString());
			bw.newLine();
			bw.flush();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断某次条件取样是否满足条件
	 * @param intersect 已经取样过的属性
	 * @param value	  本次取样的值
	 * @return 满足条件返回true
	 */
	boolean isFulfillCondition(HashSet<Integer> intersect,String curCliqueStr,String value)
	{
		Iterator<Integer> it = intersect.iterator();
		while(it.hasNext())
		{
			int attNo = it.next();
			int index = curCliqueStr.indexOf(attNo+48);
			if((value.charAt(index-1)-48)!=vn[attNo-1].getValue())
			{
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * 为相关属性赋值，将0赋给1号属性(VertexNode[0])，将1赋给2号属性
	 * @param cliqueExample 如p12
	 * @param value 如01
	 */
	void setVertexNodeValue(String cliqueExample,String value)
	{
		for(int i=1,length=cliqueExample.length();i<length;i++)
		{
			vn[cliqueExample.charAt(i)-48-1].setValue(value.charAt(i-1)-48);
			sampleDoneAtt.add(cliqueExample.charAt(i)-48);
		}
	}
	
	
	/**
	 * 判断两个极大团是否相交
	 * @param input1
	 * @param input2
	 * @return 两个最大团的交集，没有相交则交集大小为0
	 */
	HashSet<Integer> isIntersected(HashSet<Integer> input1,HashSet<Integer> input2)
	{
		HashSet<Integer> result = new HashSet<Integer>();
		result.addAll(input1);
		result.retainAll(input2);
		
		return result;
	}
	
	//将保存在txt文件中的生成数据导入数据库
	void importToMySQL()
	{
		SQLLink sqlLink = new SQLLink();
		
		//建表
		StringBuilder sql = new StringBuilder("create table ");
		sql.append(tableName);
		sql.append('(');
		for(int i=0;i<vn.length;i++)
		{
			sql.append(vn[i].getAttributeName());
			sql.append(" int,");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		sqlLink.executeUpdate(sql.toString());
		
		//将txt文件数据导入数据库
		sql = new StringBuilder();
		sql.append("load data local infile \"g:/data.txt\" into table ");
		sql.append(tableName);
		sql.append('(');
		for(int i=0;i<vn.length;i++)
		{
			sql.append(vn[i].getAttributeName());
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		sqlLink.execute(sql.toString());
		
		sqlLink.close();
		
		//删除data.txt文件
		File dataFile = new File("g:/data.txt");
		if(dataFile.exists())
		{
			dataFile.delete();
		}
	}
}



/**
 * 根据极大团大小，用于给极大团排序
 * @author Gizing
 *
 */
class HashSetSizeComparator implements Comparator<HashSet<Integer>>
{

	@Override
	public int compare(HashSet<Integer> arg0, HashSet<Integer> arg1)
	{
		// TODO Auto-generated method stub
		if(arg0.size()>arg1.size())
		{
			return -1;
		}
		else if(arg0.size()==arg1.size())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
}
