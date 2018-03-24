package com.algorithm;

/*











������ʱû��






















*/

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.datastructure.AttrFunction;
import com.datastructure.Attribute;
import com.datastructure.EqualIndex;
import com.datastructure.Table;
import com.fileparser.FileParser;

/**
 * �����������ɺ���������
 * @author LiYuming
 */
public class AttrFuncGenerator 
{

	/** ���ݱ�schema��Ϣ�б� */
	private List<Table> tables = null;

	/** ���캯�����贫�����Ϣ�б� */
	public AttrFuncGenerator(List<Table> tables) 
	{
		super();
		this.tables = tables;
	}

	/**
	 * Ϊ���ݱ��ÿ����������һ�����ɺ���
	 * �������ɺ�����ʽΪ��y = c0 * x + c1, s, step
	 * ���������������ɺ�����c1��s���ǲ���ͬ��������stepΪ[-10, 10]֮��ķ�0������c0��100���ڵ����������ظ���
	 * ���float��double��decimal�����������Ե���ز������������һ��С��1��С��
	 * 
	 * @return Map<String, AttrFunction> ����keyΪ������.��������valueΪ�������ɺ���
	 */
	public Map<String, AttrFunction> getAttrFunctions() 
	{
		Map<String, AttrFunction> attrFunctionMap = new HashMap<String, AttrFunction>();
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
				float step = (int)(random.nextFloat() * 20) - 10;
				if(step == 0)	step++;
				if(attribute.getAttrType().equals("float") || attribute.getAttrType().equals("double") ||
						attribute.getAttrType().startsWith("decimal")) 
				{
					c0 += random.nextFloat();
					c1 += random.nextFloat();
					s += random.nextFloat();
					step += random.nextFloat();
				}
				AttrFunction attrFunction = new AttrFunction(c0, c1, s, step, table.getSize(), Collections.
						<EqualIndex>emptyList(),null,null);
				attribute.setAttrFunction(attrFunction);
				attrFunctionMap.put(table.getTableName() + "." + attribute.getAttrName(), attrFunction);
			}
		}
		return attrFunctionMap;
	}

	public static void main(String[] args) 
	{
		FileParser parser = new FileParser();
		byte res = parser.parse();
		switch(res) 
		{
		case -1:
			System.out.println("�﷨����");
			break;
		case -2:
			System.out.println("C��id���ظ�");
			break;
		case -3:
			System.out.println("R��id������");
			break;
		case 1:
			System.out.println(parser.getTables());
			AttrFuncGenerator attrFuncGenerator = new AttrFuncGenerator(parser.getTables());
			Map<String, AttrFunction> attrFunctionMap = attrFuncGenerator.getAttrFunctions();
			System.out.println(parser.getTables());
			System.out.println(attrFunctionMap);
			break;
		}
	}
}
