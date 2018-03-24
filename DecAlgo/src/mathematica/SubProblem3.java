package mathematica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;
import com.datastructure.SubProblemResult;

/**
 * 子问题3求解器
 * 
 * @author Gizing
 */
public class SubProblem3 extends SubProblem
{

	/**
	 * 子问题3的求解方法 当达到精度要求或者最大迭代次数时，函数退出
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表，这些属性不属于同一个数据表
	 * @param attrNames
	 *            属性名称列表，与属性生成函数列表一一对应
	 * @param expression
	 *            关于上述属性的表达式
	 * @param expType
	 *            表达式类型（>, >=, <, <=）
	 * @param k
	 *            要求比返回值小的元素的个数
	 * @return res 调整参数
	 */
	public SubProblemResult solve(List<AttrFunction> attrFunctions, List<String> attrNames, String expression,
			String expType, int k)
	{
		if (Macro.debug)
			System.out.println("SubProblem3:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: " + attrNames
					+ "\nexpression: " + expression + "\nexpType: " + expType + "\nk: " + k + "\n");
		// 对属性名和表达式作相关处理
		List<String> attrNamesTmp = new ArrayList<String>(attrNames);
		for (int i = 0; i < attrNames.size(); i++)
		{
			attrNames.set(i, attrNames.get(i).replaceAll("\\.", ""));
		}
		for (int i = 0; i < attrNamesTmp.size(); i++)
			expression = expression.replaceAll(attrNamesTmp.get(i), attrNames.get(i));
		// 修正定义域
		List<DefiDomain> defiDomains = new ArrayList<DefiDomain>();
		// m为定义域空间中点的个数
		int m = 1;
		for (int i = 0; i < attrFunctions.size(); i++)
		{
			m *= attrFunctions.get(i).getNum();
			// count记录各属性等值段上额外定义域index点的个数
			int count = 0;
			List<EqualIndex> indexs = attrFunctions.get(i).getIndexs();
			for (int j = 0; j < indexs.size(); j++)
			{
				count += indexs.get(j).getNum() - 1;
			}
			float start = attrFunctions.get(i).getS() - attrFunctions.get(i).getStep()/2;
			float end = attrFunctions.get(i).getS()+(attrFunctions.get(i).getNum()-count-1+0.5f)*attrFunctions.get(i).getStep();
			defiDomains.add(new DefiDomain(start, end));
		}
		if (Macro.debug)
			System.out.println("defiDomains: " + defiDomains + "\nm: " + m + "\n");

		// 求最值
		double max, min;
		max = mathematica.mulitTableExpExtremeValue(attrFunctions, attrNames, defiDomains, expression, "Max");
		min = mathematica.mulitTableExpExtremeValue(attrFunctions, attrNames, defiDomains, expression, "Min");
		if (Macro.debug)
			System.out.println("max: " + max + "\nmin: " + min + "\n");

		// 给出初始预测值
		double c = ((max - min) / m) * k + min;
		// space为定义域空间大小
		double space = 0;
		space = mathematica.integrate1(attrFunctions, attrNames, expression, ">=", min, defiDomains);
		space = mathematica.integrate2(attrFunctions, attrNames, expression, ">=", min, defiDomains)
				- space * (attrFunctions.size() - 1);
		space += mathematica.integrate3(attrFunctions, attrNames, expression, ">=", min, defiDomains);
		
		if (Macro.debug)
			System.out.println("space: " + space + "\n");

		// 迭代逼近目标解
		double res = -1, resCount = -1;// 记录最优解
		for (int i = 0; i < Macro.iter; i++)
		{
			if (Macro.debug)
				System.out.println("iter: " + i + "\nc: " + c + "\n");
			// 第一步计算结果
			double count = mathematica.integrate1(attrFunctions, attrNames, expression, expType, c, defiDomains);
			// 第一步和第二步计算结果之和
			count = mathematica.integrate2(attrFunctions, attrNames, expression, expType, c, defiDomains)
					- count * (attrFunctions.size() - 1);
			// 三步计算之和
			count += mathematica.integrate3(attrFunctions, attrNames, expression, expType, c, defiDomains);
			// 当前count为满足条件的点的个数
			count = count / space * m;
			if (Macro.debug)
			{
				System.out.println("count: " + count);
				System.out.println("adjustParameter: " + c);
			}
			// 判断count是否达到解的精度要求
			if (Math.abs(count - k) / k <= Macro.precision)
			{
				SubProblemResult spr = new SubProblemResult(c, count);
				return spr;
				// return c;
			}
			
			if (Math.abs(count - k) / k < Math.abs(resCount - k) / k)
			{
				//存最优值
				res = c;
				resCount = count;
			}
			if (Macro.debug)
				System.out.println("curprecision: " + (Math.abs(count - k) / k) + "\nadjParameter: " + res + "\noptiprecision: "
						+ (Math.abs(resCount - k) / k) + "\n");
			// 若解的精度未达到精度要求
			if (expType.equals(">") || expType.equals(">="))
				if (count > k)
				{
					min = c;
					c = (c + max) / 2;
				} else
				{
					max = c;
					c = (c + min) / 2;
				}
			else 
				if (count > k)
				{
					max = c;
					c = (c + min) / 2;
				} else
				{
					min = c;
					c = (c + max) / 2;
				}
		}
		//SubProblemResult spr = new SubProblemResult(c, resCount);
		SubProblemResult spr = new SubProblemResult(res, resCount);
		return spr;
		// return c;
	}

	public static void main(String[] args)
	{
		AttrFunction attrFunction1 = new AttrFunction(3, 5, 3, 2, 1000000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(16, 10, 1), new EqualIndex(50, 20, 4))), null,
				null);
		AttrFunction attrFunction2 = new AttrFunction(1, 6, -8, 3, 2000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(20, 10, 2), new EqualIndex(45, 10, 5))), null,
				null);
		AttrFunction attrFunction3 = new AttrFunction(2, -6, 12, 4, 50000000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(42, 13, 3), new EqualIndex(62, 10, 6))), null,
				null);
		SubProblem3 subProblem3 = new SubProblem3();
		long a = System.currentTimeMillis();
		SubProblemResult result = subProblem3.solve(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("A.a1", "A.a2", "A.a3")), "12 *A.a2 + 2 A.a1 + 7 * A.a3 + 5", ">=",
				5000000);
		System.out.println("time: " + (System.currentTimeMillis() - a) / 1000 + 's');
		System.out.println(result);
		subProblem3.clear();

		//
		// AttrFunction attrFunction1 = new AttrFunction(3, 5, 3, 2, 10000,
		// new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(16, 10, 1),
		// new EqualIndex(50, 20, 4))),null,null);
		// AttrFunction attrFunction2 = new AttrFunction(1, 6, -8, 3, 200,
		// new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(20, 10, 2),
		// new EqualIndex(45, 10, 5))),null,null);
		//
		// SubProblem3 subProblem3 = new SubProblem3();
		// long a = System.currentTimeMillis();
		// SubProblemResult result = subProblem3.solve(
		// new ArrayList<AttrFunction>(Arrays.asList(attrFunction1,
		// attrFunction2)),
		// new ArrayList<String>(Arrays.asList("B.b2", "C.c2")), "B.b2+3*C.c2",
		// ">",
		// 5000);
		// System.out.println("time: " + (System.currentTimeMillis() - a) / 1000
		// + 's');
		// System.out.println(result);
		// subProblem3.clear();

		//
		// AttrFunction attrFunction1 = new AttrFunction(3, 5, 3, 2, 1000000,
		// new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(16, 10,1), new
		// EqualIndex(50, 20,4))),null,null);
		// AttrFunction attrFunction2 = new AttrFunction(1, 6, -8, 3, 2000,
		// new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(20, 10,2), new
		// EqualIndex(45, 10,5))),null,null);
		// AttrFunction attrFunction3 = new AttrFunction(2, -6, 12, 4, 50000000,
		// new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(42, 13,3), new
		// EqualIndex(62, 10,6))),null,null);
		// SubProblem3 subProblem3 = new SubProblem3();
		// long a = System.currentTimeMillis();
		// SubProblemResult result = subProblem3.solve(new
		// ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2)),
		// new ArrayList<String>(Arrays.asList("A.a1", "A.a2")),
		// "12 *A.a2 + 2 A.a1", ">=", 5000000);
		// System.out.println("time: " + (System.currentTimeMillis() - a)/1000
		// +'s');
		// System.out.println(result);
		// subProblem3.clear();
	}
}
