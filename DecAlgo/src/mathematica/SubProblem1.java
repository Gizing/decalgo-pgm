package mathematica;

import com.datastructure.SubProblemResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;

/**
 * 子问题1求解器
 * @author Gizing
 */
public class SubProblem1 extends SubProblem{

	/**
	 * 子问题1的求解方法
	 * 当达到精度要求或者最大迭代次数时，函数退出
	 * 
	 * @param attrFunction 属性的生成函数
	 * @param attrName 属性名称
	 * @param expression 关于该属性的表达式
	 * @param expType 表达式类型（>, >=, <, <=）
	 * @param k 要求满足条件的元素的个数
	 * @return res 调整参数
	 */
	public SubProblemResult solve(AttrFunction attrFunction, String attrName, String expression, String expType, int k) {
		if(Macro.debug) System.out.println("SubProblem1:\n" + "attrFunction: " + attrFunction.toString() + 
				"attrName: " + attrName + "\nexpression: " + expression + "\nexpType: " + expType + 
				"\nk: " + k + "\n");
		//对属性名和表达式作相关处理（因为mathematica中"."是保护字符）
		String attrNameTmp = attrName;
		attrName = attrName.replaceAll("\\.", "");
		expression = expression.replaceAll(attrNameTmp, attrName);

		//等值段信息
		List<EqualIndex> indexs = attrFunction.getIndexs();
		//count1为等值段的"额外"长度之和
		int count1 = 0;
		//数组y为各等值段上函数的值
		double[] y = new double[indexs.size()];
		for(int i = 0; i < indexs.size(); i++) {
			count1 += indexs.get(i).getNum() - 1;
			float x = attrFunction.getS() + (indexs.get(i).getIndex() - 1) * attrFunction.getStep();
			y[i] = mathematica.singleAttrExpCalcu(attrFunction.getC0(), attrFunction.getC1(), attrName, x, expression);
		}
		if(Macro.debug)	System.out.println("count1: " + count1 + "\ny: " + Arrays.toString(y) + "\n");

		//修正定义域
		float start = attrFunction.getS() - attrFunction.getStep() / 2;
		float end = attrFunction.getS() + (attrFunction.getNum() - count1 - 1 + 0.5f) * attrFunction.getStep();
		if(Macro.debug)	System.out.println("start: " + start + "\nend: " + end + "\n");

		//函数在该定义域上的最值
		double max = mathematica.singleAttrExpExtremeValue(attrFunction.getC0(), attrFunction.getC1(), 
				attrName, start, end, expression, "Max");
		double min = mathematica.singleAttrExpExtremeValue(attrFunction.getC0(), attrFunction.getC1(), 
				attrName, start, end, expression, "Min");
		if(Macro.debug)	System.out.println("max: " + max + "\nmin: " + min + "\n");
		//给出初始预测值
		double c = (max - min) / attrFunction.getNum() * k + min;

		//迭代逼近目标解
		double res = -1, resCount2 = -1;//记录最优解
		for(int i = 0; i < Macro.iter; i++) {
			if(Macro.debug)	System.out.println("iter: " + i + "\nc: " + c);
			double count2 = 0;
			//这段代码实在有点low
			if(expType.equals(">")) {
				for(int j = 0; j < indexs.size(); j++)
					if(y[j] > c)	count2 += indexs.get(j).getNum() - 1;
			} else if(expType.equals(">=")) {
				for(int j = 0; j < indexs.size(); j++)
					if(y[j] >= c)	count2 += indexs.get(j).getNum() - 1;
			} else if(expType.equals("<")) {
				for(int j = 0; j < indexs.size(); j++)
					if(y[j] < c)	count2 += indexs.get(j).getNum() - 1;
			} else if(expType.equals("<=")) {
				for(int j = 0; j < indexs.size(); j++)
					if(y[j] <= c)	count2 += indexs.get(j).getNum() - 1;
			}
			if(Macro.debug)	System.out.println("count2: " + count2 + "\n");

			//计算非等值段上满足预测值的index点个数
			double ratio = mathematica.singleAttrRatioCalcu(attrFunction.getC0(), attrFunction.getC1(),
					attrName, expression, expType, c, start, end);
			//当前count2是整个定义域上所有满足预测值的index点个数
			count2 += ratio * (attrFunction.getNum() - count1);
			if(Macro.debug)	System.out.println("count2: " + count2 + "\n");

			//判断count2是否达到解的精度要求
			if(Math.abs(count2 - k) / k <= Macro.precision)	
			{
				SubProblemResult spr = new SubProblemResult(c,count2);
				return spr;
				//return c;
			}
			if(Math.abs(count2 - k) / k < Math.abs(resCount2 - k) / k) {
				res = c;
				resCount2 = count2;
			}
			if(Macro.debug)	System.out.println("curprecision: " + (Math.abs(count2 - k) / k) + "\nadjParameter: " + 
					res + "\noptiprecision: " + (Math.abs(resCount2 - k) / k) + "\n");

			//若解的精度未达到精度要求
			if(expType.equals(">") || expType.equals(">="))
				if(count2 > k) { min = c; c = (c + max) / 2; } 
				else { max = c; c = (c + min) / 2; }
			else
				if(count2 > k) { max = c; c = (c + min) / 2; } 
				else { min = c; c = (c + max) / 2; }
		}
		SubProblemResult spr = new SubProblemResult(res,resCount2);
		return spr;
		//return res;
	}

	public static void main(String[] args) {
		AttrFunction attrFunction = new AttrFunction(3, 5, 3, 2, 100, 
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(2, 10,1))),null,null);
		SubProblem1 subProblem1 = new SubProblem1();
		System.out.println(subProblem1.solve(attrFunction, "A.a1", "4*A.a1+5+A.a1^2", "<=", 10));
		subProblem1.clear();
	}
}
