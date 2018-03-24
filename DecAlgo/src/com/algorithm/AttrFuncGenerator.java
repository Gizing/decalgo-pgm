package com.algorithm;

/*











此类暂时没用






















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
 * 属性数据生成函数生成器
 * @author LiYuming
 */
public class AttrFuncGenerator 
{

	/** 数据表schema信息列表 */
	private List<Table> tables = null;

	/** 构造函数，需传入表信息列表 */
	public AttrFuncGenerator(List<Table> tables) 
	{
		super();
		this.tables = tables;
	}

	/**
	 * 为数据表的每个属性生成一个生成函数
	 * 属性生成函数形式为：y = c0 * x + c1, s, step
	 * 其中所有属性生成函数的c1和s都是不相同的质数，step为[-10, 10]之间的非0整数，c0是100以内的质数（可重复）
	 * 针对float、double和decimal数据类型属性的相关参数各随机加上一个小于1的小数
	 * 
	 * @return Map<String, AttrFunction> 其中key为：表名.属性名；value为属性生成函数
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
			System.out.println("语法错误");
			break;
		case -2:
			System.out.println("C中id有重复");
			break;
		case -3:
			System.out.println("R中id不存在");
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
