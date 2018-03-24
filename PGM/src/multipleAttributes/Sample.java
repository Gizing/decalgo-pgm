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
 * �������ݿ�ʵ������
 * @author Gizing
 *
 */
public class Sample
{
	private VertexNode[] vn = null;
	private ArrayList<HashSet<Integer>> maxCliques = null;
	//private boolean[] maxCliquesVisited = null;//ÿ�������Ŷ�Ӧ�ķ���״̬��Ĭ��Ϊfalseδ���ʹ�
	private HashMap<String,Double> equationsResult = null;
	private int tableSize;
	private String tableName = null;
	private HashSet<Integer> sampleDoneAtt = null;//����ȡ��ֵ�����Եı��
	
	private HashMap<String,String[]> cliqueProbabilityArrayMap = null;//һ�鼫���Ŷ�Ӧ��ȡֵ���飬���ȡֵ00�ĸ���Ϊ1/2��������һ����������ݴ����00
	
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
		
		
		//���ݼ����Ŵ�С�Լ����ŵ�ArrayList���н�������
		HashSetSizeComparator hsc = new HashSetSizeComparator();
		Collections.sort(maxCliques, hsc);
		generateCliProArrMap();
		
		for(int i=0;i<this.tableSize;i++)
		{
			sampleOnce();
		}
		
		//�ر��ļ�
		try
		{
			bw.close();
			fw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//���ļ��е����ݹ̻������ݿ���
		importToMySQL();
	}
	
	/**
	 * ����ÿ��ȡֵ�ĸ��ʴ�С�����ݱ���������Ӧ����������Ԫ�أ����ݶ������Ӧ��ȡֵ��
	 */
	void generateCliProArrMap()
	{
		for(HashSet<Integer> temp:maxCliques)
		{
			//�齨ÿһ�������ŵķ���ǰ׺
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
	 * sampleһ��Ԫ�飬�������ļ���
	 * ���㣺ǰ������ͼ��ÿ���㶼��������������֮��һ���н�����������(1,2,3) (1,4) (5)�����ļ����ż���
	 */
	void sampleOnce()
	{
		//��һ��������ȡ��
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
				// ����Ҫȡ��
				continue;
			} else if (intersect.size() == 0)
			{
				// ����ȡֵ
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
				// ������ȡ��
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
					
					//�ж��Ƿ����������������ȡ��������������ȡ��
					if(isFulfillCondition(intersect,curCliqueStr.toString(),value))
					{
						setVertexNodeValue(curCliqueStr.toString(),value);
						break;
					}
				}
				
			}

		}
			
		
		//��һ��sample��ȡֵ�����ļ�
		writeInFile();
		
	}
	
	
	/**
	 * ��һ��sample��ȡֵ�����ļ�
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
	 * �ж�ĳ������ȡ���Ƿ���������
	 * @param intersect �Ѿ�ȡ����������
	 * @param value	  ����ȡ����ֵ
	 * @return ������������true
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
	 * Ϊ������Ը�ֵ����0����1������(VertexNode[0])����1����2������
	 * @param cliqueExample ��p12
	 * @param value ��01
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
	 * �ж������������Ƿ��ཻ
	 * @param input1
	 * @param input2
	 * @return ��������ŵĽ�����û���ཻ�򽻼���СΪ0
	 */
	HashSet<Integer> isIntersected(HashSet<Integer> input1,HashSet<Integer> input2)
	{
		HashSet<Integer> result = new HashSet<Integer>();
		result.addAll(input1);
		result.retainAll(input2);
		
		return result;
	}
	
	//��������txt�ļ��е��������ݵ������ݿ�
	void importToMySQL()
	{
		SQLLink sqlLink = new SQLLink();
		
		//����
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
		
		//��txt�ļ����ݵ������ݿ�
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
		
		//ɾ��data.txt�ļ�
		File dataFile = new File("g:/data.txt");
		if(dataFile.exists())
		{
			dataFile.delete();
		}
	}
}



/**
 * ���ݼ����Ŵ�С�����ڸ�����������
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
