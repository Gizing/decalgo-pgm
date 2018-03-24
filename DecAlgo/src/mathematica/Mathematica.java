package mathematica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

/**
 * Mathematica计算器
 * 
 * @author Gizing
 */
public class Mathematica
{

	/** Mathematica与Java交互的接口（JLink） */
	KernelLink ml = null;

	/** 构造函数，初始化KernelLink */
	public Mathematica()
	{
		super();
		try
		{
			ml = MathLinkFactory.createKernelLink(
					"-linkmode launch -linkname " + "'G:\\Wolfram Research\\Mathematica\\10.0\\mathkernel.exe'");
			// 清空计算环境
			ml.discardAnswer();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * 服务于：子问题1 针对单属性相关表达式值的计算 注意：属性名中不能含有"."，所以在传参数前应做相关处理，后面不再累述
	 * 
	 * @param c0
	 *            c1 属性生成函数"attrName = c0 * x + c1"的参数
	 * @param attrName
	 *            属性名
	 * @param x
	 *            自变量x的数值
	 * @param expression
	 *            关于attrName的表达式
	 * @return res expression的计算值
	 */
	public double singleAttrExpCalcu(float c0, float c1, String attrName, float x, String expression)
	{
		if (Macro.debug)
			System.out.println("SingleAttrExpCalcu:\n" + "c0: " + c0 + "\nc1: " + c1 + "\nattrName: " + attrName
					+ "\nx: " + x + "\nexpression: " + expression);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		sb.append("Clear[x]\n");
		sb.append("x = " + (new BigDecimal(x).toPlainString()) + "\n");
		sb.append(attrName + " = " + (new BigDecimal(c0).toPlainString()) + " * x + "
				+ (new BigDecimal(c1).toPlainString()) + "\n");
		sb.append(expression);
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题2 单表多属性情形 当各属性都为等值段时，此时关于各属性的表达式的值为一常数 注意values与attrNames是一一对应的
	 * 
	 * @param values
	 *            各属性的值
	 * @param attrNames
	 *            各属性的名称
	 * @param expression
	 *            关于各属性的表达式
	 * @return res expression的计算值
	 */
	public double singleTableExpCalcu(float[] values, List<String> attrNames, String expression)
	{
		if (Macro.debug)
			System.out.println("SingleTableExpCalcu:\n" + "values: " + Arrays.toString(values) + "\nattrNames: "
					+ attrNames + "\nexpression: " + expression);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++)
			sb.append(attrNames.get(i) + " = " + (new BigDecimal(values[i]).toPlainString()) + "\n");
		sb.append(expression);
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题3中第二步计算
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表
	 * @param attrNames
	 *            各个属性的名称
	 * @param k
	 *            第k个属性的x值是已知的
	 * @param x
	 *            第k个属性的x值
	 * @param expression
	 *            关于attrNames的表达式
	 * @return String 含有变量的一个表达式
	 */
	public String multiTableExpCalcu(List<AttrFunction> attrFunctions, List<String> attrNames, int k, float x,
			String expression)
	{
		if (Macro.debug)
			System.out.println("MultiTableExpCalcu:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: " + attrNames
					+ "\nk: " + k + "\nx: " + x + "\nexpression: " + expression);
		String res = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrFunctions.size(); i++)
			if (i != k)
				sb.append("Clear[x" + i + "];");
		for (int i = 0; i < attrFunctions.size(); i++)
			if (i != k)
				sb.append(attrNames.get(i) + " = " + (new BigDecimal(attrFunctions.get(i).getC0()).toPlainString())
						+ " * x" + i + " + " + (new BigDecimal(attrFunctions.get(i).getC1()).toPlainString()) + ";");
			else
				sb.append(attrNames.get(i) + " = "
						+ (new BigDecimal(attrFunctions.get(i).getC0() * x + attrFunctions.get(i).getC1())
								.toPlainString())
						+ ";");
		sb.append("-(" + expression + ")");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getExpr().toString();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题1 针对单属性相关表达式最值的计算
	 * 
	 * @param c0
	 *            c1 属性生成函数"attrName = c0 * x + c1"的参数
	 * @param attrName
	 *            属性名
	 * @param start
	 *            定义域的起点
	 * @param end
	 *            定义域的终点
	 * @param expression
	 *            关于attrName的表达式
	 * @param type
	 *            最值类型
	 * @return res expression的最值
	 */
	public double singleAttrExpExtremeValue(float c0, float c1, String attrName, float start, float end,
			String expression, String type)
	{
		if (Macro.debug)
			System.out.println("SingleAttrExpExtremeValue:\n" + "c0: " + c0 + "\nc1: " + c1 + "\nattrName: " + attrName
					+ "\nstart: " + start + "\nend: " + end + "\nexpression: " + expression + "\ntype: " + type);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		sb.append("Clear[x];");
		sb.append(attrName + " = " + (new BigDecimal(c0).toPlainString()) + " * x + "
				+ (new BigDecimal(c1).toPlainString()) + ";");
		sb.append("Find" + type + "Value[{" + expression + ", " + (new BigDecimal(start).toPlainString()) + " <= x <= "
				+ (new BigDecimal(end).toPlainString()) + "}, x]");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题2 针对单表多属性的最值计算
	 * 
	 * @param functions
	 *            各个属性的系数
	 * @param attrNames
	 *            各个属性的名称
	 * @param start
	 *            定义域的起点
	 * @param end
	 *            定义域的终点
	 * @param expression
	 *            关于attrName的表达式
	 * @param type
	 *            最值类型
	 * @return res expression的最值
	 */
	public double singleTableExpExtremeValue(List<Function> functions, List<String> attrNames, float start, float end,
			String expression, String type)
	{
		if (Macro.debug)
			System.out.println("SingleTableExpExtremeValue:\n" + "functions: " + functions + "\nattrNames: " + attrNames
					+ "\nstart: " + start + "\nend: " + end + "\nexpression: " + expression + "\ntype: " + type);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		sb.append("Clear[x];");
		for (int i = 0; i < functions.size(); i++)
			sb.append(attrNames.get(i) + " = " + (new BigDecimal(functions.get(i).c0).toPlainString()) + " * x + "
					+ (new BigDecimal(functions.get(i).c1).toPlainString()) + ";");
		sb.append("Find" + type + "Value[{" + expression + ", " + (new BigDecimal(start).toPlainString()) + " <= x <= "
				+ (new BigDecimal(end).toPlainString()) + "}, x]");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题3 针对多表多属性的最值计算
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表
	 * @param attrNames
	 *            各个属性的名称
	 * @param defiDomains
	 *            各个属性的定义域
	 * @param expression
	 *            关于attrName的表达式
	 * @param type
	 *            最值类型
	 * @return res expression的最值
	 */
	public double mulitTableExpExtremeValue(List<AttrFunction> attrFunctions, List<String> attrNames,
			List<DefiDomain> defiDomains, String expression, String type)
	{
		if (Macro.debug)
			System.out.println("MulitTableExpExtremeValue:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: "
					+ attrNames + "\ndefiDomains: " + defiDomains + "\nexpression: " + expression + "\ntype: " + type);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrFunctions.size(); i++)
			sb.append("Clear[x" + i + "];");
		for (int i = 0; i < attrFunctions.size(); i++)
			sb.append(attrNames.get(i) + " = " + (new BigDecimal(attrFunctions.get(i).getC0()).toPlainString()) + " * x"
					+ i + " + " + (new BigDecimal(attrFunctions.get(i).getC1()).toPlainString()) + ";");
//		sb.append("Find" + type + "Value[{" + expression);
		sb.append(type + "Value[{" + expression);
		for (int i = 0; i < defiDomains.size(); i++)
			sb.append(", " + (new BigDecimal(defiDomains.get(i).start).toPlainString()) + " <= x" + i + " <= "
					+ (new BigDecimal(defiDomains.get(i).end).toPlainString()));
		sb.append("}");
//		for (int i = 0; i < defiDomains.size(); i++)
//			sb.append(", x" + i);
//		sb.append("]");
		sb.append(",{");
		for (int i = 0; i < defiDomains.size(); i++)
			sb.append("x" + i + ",");
		sb.deleteCharAt(sb.length()-1);
		sb.append("}]");
		
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题1 针对单属性相关表达式，计算c在其表示的值域上的序 这里的序表示为满足条件的定义域占总定义域的比例
	 * 
	 * @param c0
	 *            c1 属性生成函数"attrName = c0 * x + c1"的参数
	 * @param attrName
	 *            属性名
	 * @param expression
	 *            关于attrName的表达式
	 * @param expType
	 *            表达式类型
	 * @param c
	 *            预测值
	 * @param start
	 *            定义域的起点
	 * @param end
	 *            定义域的终点
	 * @return res c的序，是一个百分比（满足条件的定义域占总定义域的比例）
	 */
	public double singleAttrRatioCalcu(float c0, float c1, String attrName, String expression, String expType, double c,
			float start, float end)
	{
		if (Macro.debug)
			System.out.println("SingleAttrRatioCalcu:\n" + "c0: " + c0 + "\nc1: " + c1 + "\nattrName: " + attrName
					+ "\nexpression: " + expression + "\nexpType: " + expType + "\nc: " + c + "\nstart: " + start
					+ "\nend: " + end);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		sb.append("Clear[x];");
		sb.append(attrName + " = " + (new BigDecimal(c0).toPlainString()) + " * x + "
				+ (new BigDecimal(c1).toPlainString()) + ";");
		sb.append("NIntegrate[Boole[" + expression + expType + (new BigDecimal(c).toPlainString()) + "], {" + "x, "
				+ (new BigDecimal(start).toPlainString()) + ", " + (new BigDecimal(end).toPlainString()) + "}]");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble() / (end - start);
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于：子问题2 针对单表多属性相关表达式，计算c在其表示的值域上的序 这里的序表示为满足条件的定义域占总定义域的比例
	 * 
	 * @param functions
	 *            各个属性的系数
	 * @param attrNames
	 *            各个属性的名称
	 * @param expression
	 *            关于attrNames的表达式
	 * @param expType
	 *            表达式类型
	 * @param c
	 *            预测值
	 * @param start
	 *            定义域的起点
	 * @param end
	 *            定义域的终点
	 * @return res c的序 是一个百分比（满足条件的定义域占总定义域的比例）
	 */
	public double singleTableRatioCalcu(List<Function> functions, List<String> attrNames, String expression,
			String expType, double c, float start, float end)
	{
		if (Macro.debug)
			System.out.println("SingleTableRatioCalcu:\n" + "functions: " + functions + "\nattrNames: " + attrNames
					+ "\nexpression: " + expression + "\nexpType: " + expType + "\nc: " + c + "\nstart: " + start
					+ "\nend: " + end);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		sb.append("Clear[x];");
		for (int i = 0; i < functions.size(); i++)
			sb.append(attrNames.get(i) + " = " + (new BigDecimal(functions.get(i).c0).toPlainString()) + " * x + "
					+ (new BigDecimal(functions.get(i).c1).toPlainString()) + ";");
		sb.append("NIntegrate[Boole[" + expression + expType + (new BigDecimal(c).toPlainString()) + "], {" + "x, "
				+ (new BigDecimal(start).toPlainString()) + ", " + (new BigDecimal(end).toPlainString()) + "}]");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble() / (end - start);
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);

		}
		return res;
	}

	/**
	 * 服务于子问题3的第一步计算
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表
	 * @param attrNames
	 *            各个属性的名称
	 * @param expression
	 *            关于attrNames的表达式
	 * @param expType
	 *            表达式类型
	 * @param c
	 *            预测值
	 * @param defiDomains
	 *            各属性的定义域
	 * @return res 满足条件的定义域空间大小
	 */
	public double integrate1(List<AttrFunction> attrFunctions, List<String> attrNames, String expression,
			String expType, double c, List<DefiDomain> defiDomains)
	{
		if (Macro.debug)
			System.out.println(
					"Integrate1:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: " + attrNames + "\nexpression: "
							+ expression + "\nexpType: " + expType + "\nc: " + c + "\ndefiDomains: " + defiDomains);
		double res = -1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attrFunctions.size(); i++)
			sb.append("Clear[x" + i + "];");
		for (int i = 0; i < attrFunctions.size(); i++)
			sb.append(attrNames.get(i) + " = " + (new BigDecimal(attrFunctions.get(i).getC0()).toPlainString()) + " * x"
					+ i + " + " + (new BigDecimal(attrFunctions.get(i).getC1()).toPlainString()) + ";");
		sb.append("NIntegrate[Boole[" + expression + expType + (new BigDecimal(c).toPlainString()) + "]");
		for (int i = 0; i < defiDomains.size(); i++)
			sb.append(", {x" + i + ", " + (new BigDecimal(defiDomains.get(i).start).toPlainString()) + ", "
					+ (new BigDecimal(defiDomains.get(i).end).toPlainString()) + "}");
		sb.append("]");
		if (Macro.debug)
			System.out.println("Mathematica:\n" + sb.toString() + "\n");
		try
		{
			ml.evaluate(sb.toString());
			ml.waitForAnswer();
			res = ml.getDouble();
		} catch (MathLinkException e)
		{
			e.printStackTrace();
			if (Macro.exceptionExit)
				System.exit(0);
		}
		return res;
	}

	/**
	 * 服务于子问题3的第二步计算 依次完成每个属性的第二步计算并求和
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表
	 * @param attrNames
	 *            各个属性的名称
	 * @param expression
	 *            关于attrNames的表达式
	 * @param expType
	 *            表达式类型
	 * @param c
	 *            预测值
	 * @param defiDomains
	 *            各属性的定义域
	 * @return res 满足条件的定义域空间大小
	 */
	public double integrate2(List<AttrFunction> attrFunctions, List<String> attrNames, String expression,
			String expType, double c, List<DefiDomain> defiDomains)
	{
		if (Macro.debug)
			System.out.println("Integrate2:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: " + attrNames
					+ "\nexpression: " + expression + "\nexpType: " + expType + "\nc: " + c + "\ndefiDomains: "
					+ defiDomains + "\n");
		double res = 0;
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < attrFunctions.size(); i++)
			// sb1.append("Clear[x" + i + "]\n");
			sb1.append("Clear[x" + i + "];");
		for (int i = 0; i < attrFunctions.size(); i++)
			sb1.append(attrNames.get(i) + " = " + (new BigDecimal(attrFunctions.get(i).getC0()).toPlainString())
					+ " * x" + i + " + " + (new BigDecimal(attrFunctions.get(i).getC1()).toPlainString()) + ";");
		// 针对每个属性完成第二步计算
		for (int i = 0; i < attrFunctions.size(); i++)
		{
			sb2.append("NIntegrate[Boole[" + expression + expType + (new BigDecimal(c).toPlainString()));
			List<EqualIndex> indexs = attrFunctions.get(i).getIndexs();
			// count记录该属性上的额外等值段长度
			int count = 0;
			// 将该属性每个等值段转化成mathematica语法输入
			for (int j = 0; j < indexs.size(); j++)
			{
				count += indexs.get(j).getNum() - 1;
				float x = attrFunctions.get(i).getS() + (indexs.get(j).getIndex() - 1) * attrFunctions.get(i).getStep();
				sb2.append(" /. x" + i + " -> x" + i + " - " + (indexs.get(j).getNum() - 1) + "UnitStep[x" + i + " - "
						+ x + "]UnitStep[" + (new BigDecimal(c).toPlainString()) + " + ");
				sb2.append(multiTableExpCalcu(attrFunctions, attrNames, i, x, expression));
				sb2.append("]");
			}
			sb2.append("]");
			// 对定义域进行扩展
			for (int j = 0; j < defiDomains.size(); j++)
			{
				sb2.append(", {");
				if (j == i)
					sb2.append("x" + j + ", " + (new BigDecimal(defiDomains.get(j).start).toPlainString()) + ", "
							+ (new BigDecimal(defiDomains.get(j).end + count * attrFunctions.get(j).getStep()).toPlainString())+ "}");
				else
					sb2.append("x" + j + ", " + (new BigDecimal(defiDomains.get(j).start).toPlainString()) + ", "
							+ (new BigDecimal(defiDomains.get(j).end).toPlainString()) + "}");
			}
			sb2.append("]");
			if (Macro.debug)
				System.out.println("Mathematica:\n" + sb1.toString() + sb2.toString() + "\n");
			try
			{
				ml.evaluate(sb1.toString() + sb2.toString());
				ml.waitForAnswer();
				res += ml.getDouble();
			} catch (MathLinkException e)
			{
				e.printStackTrace();
				if (Macro.exceptionExit)
					System.exit(0);
			}

			// String temp =
			// ml.evaluateToInputForm(sb1.toString()+sb2.toString(),sb1.length()+sb2.length());
			// temp = temp.substring(2, temp.length()-2);
			// System.out.println(temp);
			// res += Double.parseDouble(temp);

			sb2.delete(0, sb2.length());
		}
		return res;
	}

	/**
	 * 服务于子问题3的第三步计算 完成每个组合的第三步计算并求和
	 * 
	 * @param attrFunctions
	 *            属性的生成函数列表
	 * @param attrNames
	 *            各个属性的名称
	 * @param expression
	 *            关于attrNames的表达式
	 * @param expType
	 *            表达式类型
	 * @param c
	 *            预测值
	 * @param defiDomains
	 *            各属性的定义域
	 * @return res 满足条件的定义域空间大小
	 */
	public double integrate3(List<AttrFunction> attrFunctions, List<String> attrNames, String expression,
			String expType, double c, List<DefiDomain> defiDomains)
	{
		if (Macro.debug)
			System.out.println("Integrate3:\n" + "attrFunctions: " + attrFunctions + "\nattrNames: " + attrNames
					+ "\nexpression: " + expression + "\nexpType: " + expType + "\nc: " + c + "\ndefiDomains: "
					+ defiDomains + "\n");
		double res = 0;
		StringBuilder sb = new StringBuilder();
		// 针对各个属性进行两两组合
		for (int i = 0; i < attrFunctions.size(); i++)
			for (int j = i + 1; j < attrFunctions.size(); j++)
			{
				List<EqualIndex> indexs1 = attrFunctions.get(i).getIndexs();
				List<EqualIndex> indexs2 = attrFunctions.get(j).getIndexs();
				// 针对该两个属性的所有等值段进行两两组合
				for (int k = 0; k < indexs1.size(); k++)
					for (int l = 0; l < indexs2.size(); l++)
					{
						// 构建mathematica语法输入
						for (int m = 0; m < attrFunctions.size(); m++)
							if (m != i && m != j)
								sb.append("Clear[x" + i + "];");
						for (int m = 0; m < attrFunctions.size(); m++)
						{
							if (m != i && m != j)
							{
								sb.append(attrNames.get(m) + " = "
										+ (new BigDecimal(attrFunctions.get(m).getC0()).toPlainString()) + " * x" + m
										+ " + " + (new BigDecimal(attrFunctions.get(m).getC1()).toPlainString()) + ";");
							} else if (m == i)
							{
								float x = attrFunctions.get(m).getS()
										+ (indexs1.get(k).getIndex() - 1) * attrFunctions.get(m).getStep();
								sb.append(attrNames.get(m) + " = "
										+ (new BigDecimal(
												attrFunctions.get(m).getC0() * x + attrFunctions.get(m).getC1())
														.toPlainString())
										+ ";");
							} else if (m == j)
							{
								float x = attrFunctions.get(m).getS()
										+ (indexs2.get(l).getIndex() - 1) * attrFunctions.get(m).getStep();
								sb.append(attrNames.get(m) + " = "
										+ (new BigDecimal(
												attrFunctions.get(m).getC0() * x + attrFunctions.get(m).getC1())
														.toPlainString())
										+ ";");
							}
						}
						sb.append("NIntegrate[Boole[" + expression + expType + (new BigDecimal(c).toPlainString()) + "]");
						boolean flag = false;
						for (int m = 0; m < defiDomains.size(); m++)
							if (m != i && m != j)
							{
								sb.append(", {x" + m + ", " + (new BigDecimal(defiDomains.get(m).start).toPlainString())
										+ ", " + (new BigDecimal(defiDomains.get(m).end).toPlainString()) + "}");
								flag = true;
							}
						if(!flag)
						{
							//
							continue;
//							sb.delete(0, sb.length());
//							sb.append((indexs1.get(k).getNum() - 1) + " * " + (indexs2.get(l).getNum() - 1));
						}
						else
							sb.append("] * " + (indexs1.get(k).getNum() - 1) + " * " + (indexs2.get(l).getNum() - 1));
						if (Macro.debug)
							System.out.println("Mathematica:\n" + sb.toString() + "\n");
						try
						{
							ml.evaluate(sb.toString());
							ml.waitForAnswer();
							res += ml.getDouble();
						} catch (MathLinkException e)
						{
							e.printStackTrace();
							if (Macro.exceptionExit)
								System.exit(0);
						}
						sb.delete(0, sb.length());
					}
			}
		return res;
	}

	/** 释放Mathematica计算资源 */
	public void close()
	{
		if (ml != null)
			ml.close();
	}

	/**
	 * 计算属性等值段长度
	 * 
	 * @param equations
	 * @return 返回x的字符串结果
	 */
	public String attrEqualLength(String equations)
	{
		StringBuilder result = new StringBuilder(ml.evaluateToInputForm(equations, 100));
		result.delete(0, 2);
		result.delete(result.length() - 2, result.length());

		return result.toString().split("->")[1].trim();
	}

	/**
	 * 用于计算等号约束的调整参数
	 * 
	 * @param input
	 *            形如：x+3/.x->3
	 * @return 仅有数值，不需处理
	 */
	public String getEquAdjustParameter(String input)
	{
		return ml.evaluateToInputForm(input, 50);
	}

	public double evaluate(String input)
	{
		double result = 0;
		try
		{
			ml.evaluate(input);
			ml.waitForAnswer();
			result = ml.getDouble();
		} catch (MathLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} finally
		{
			if (ml != null)
				ml.close();
		}
		return result;
	}

	public static void main(String[] args)
	{
		Mathematica mathematica = new Mathematica();

		System.out.println(mathematica.singleAttrExpCalcu(2, 4, "Aa1", 5.5f, "3*Aa1 + Aa1^2") + "\n");

		float[] values =
		{ 23, 34, 56 };
		System.out.println(
				mathematica.singleTableExpCalcu(values, Arrays.asList("Aa2", "Aa3", "Aa4"), "Aa3 - Aa2 + Aa3 * Aa4")
						+ "\n");

		AttrFunction attrFunction1 = new AttrFunction(3, 5, 3, 2, 1000000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(16, 10, 1), new EqualIndex(50, 20, 2))), null,
				null);
		AttrFunction attrFunction2 = new AttrFunction(1, 6, -8, 3, 2000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(20, 10, 1), new EqualIndex(45, 10, 2))), null,
				null);
		AttrFunction attrFunction3 = new AttrFunction(2, -6, 12, 4, 50000000,
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(42, 13, 1), new EqualIndex(62, 10, 2))), null,
				null);

		System.out.println(mathematica.multiTableExpCalcu(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), 2, 35, "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2")
				+ "\n");

		System.out.println(mathematica.singleAttrExpExtremeValue(2, 4, "Aa1", 3, 15, "3*Aa1 + Aa1^2", "Max") + "\n");
		System.out.println(mathematica.singleAttrExpExtremeValue(2, 4, "Aa1", 3, 15, "3*Aa1 + Aa1^2", "Min") + "\n");

		Function function1 = new Function(2, 5);
		Function function2 = new Function(-4, 12);
		Function function3 = new Function(4, 1);
		System.out.println(mathematica.singleTableExpExtremeValue(
				new ArrayList<Function>(Arrays.asList(function1, function2, function3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), 15, 203, "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2",
				"Max") + "\n");
		System.out.println(mathematica.singleTableExpExtremeValue(
				new ArrayList<Function>(Arrays.asList(function1, function2, function3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), 15, 203, "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2",
				"Min") + "\n");

		DefiDomain defiDomain1 = new DefiDomain(12, 30);
		DefiDomain defiDomain2 = new DefiDomain(4, 46);
		DefiDomain defiDomain3 = new DefiDomain(-9, 16);
		System.out.println(mathematica.mulitTableExpExtremeValue(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")),
				new ArrayList<DefiDomain>(Arrays.asList(defiDomain1, defiDomain2, defiDomain3)),
				"3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", "Max") + "\n");
		System.out.println(mathematica.mulitTableExpExtremeValue(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")),
				new ArrayList<DefiDomain>(Arrays.asList(defiDomain1, defiDomain2, defiDomain3)),
				"3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", "Min") + "\n");

		System.out.println(mathematica.singleAttrRatioCalcu(2, 4, "Aa1", "3*Aa1 + Aa1^2", ">=", 250, 3, 15) + "\n");

		System.out.println(mathematica.singleTableRatioCalcu(
				new ArrayList<Function>(Arrays.asList(function1, function2, function3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", ">", 962, 0,
				15) + "\n");

		DefiDomain defiDomain4 = new DefiDomain(2, 1999946);
		DefiDomain defiDomain5 = new DefiDomain(-9.5f, 5936.5f);
		DefiDomain defiDomain6 = new DefiDomain(10.0f, 1.9999992E8f);
		System.out.println(mathematica.integrate1(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", "<",
				962000000, new ArrayList<DefiDomain>(Arrays.asList(defiDomain4, defiDomain5, defiDomain6))) + "\n");

		System.out.println(mathematica.integrate2(
				new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, attrFunction3)),
				new ArrayList<String>(Arrays.asList("Aa1", "Aa2", "Aa3")), "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", "<",
				962000000, new ArrayList<DefiDomain>(Arrays.asList(defiDomain4, defiDomain5, defiDomain6))) + "\n");

		// 多表多属性，当属性只有两个时
		// System.out.println(mathematica.integrate2(new
		// ArrayList<AttrFunction>(Arrays.asList(attrFunction1,
		// attrFunction2)), new ArrayList<String>(Arrays.asList("Aa1", "Aa2")),
		// "3Aa1 + 5* Aa2", "<", 962000000, new
		// ArrayList<DefiDomain>(Arrays.asList(defiDomain4,
		// defiDomain5))) + "\n");

		// System.out.println(mathematica.integrate3(new
		// ArrayList<AttrFunction>(Arrays.asList(attrFunction1,
		// attrFunction2, attrFunction3)), new
		// ArrayList<String>(Arrays.asList("Aa1", "Aa2","Aa3")),
		// "3Aa1 + 5* Aa2 + Aa1 * Aa3 ^2", "<", 962000000, new
		// ArrayList<DefiDomain>(Arrays.asList(defiDomain4,
		// defiDomain5, defiDomain6))) + "\n");

		// 多表多属性，当属性只有两个时出错
		System.out.println(
				mathematica.integrate3(new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2)),
						new ArrayList<String>(Arrays.asList("Aa1", "Aa2")), "3Aa1 + 5* Aa2", "<", 962000000,
						new ArrayList<DefiDomain>(Arrays.asList(defiDomain4, defiDomain5))) + "\n");

		mathematica.close();
	}
}
