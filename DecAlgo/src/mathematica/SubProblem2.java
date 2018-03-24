package mathematica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;
import com.datastructure.SubProblemResult;

/**
 * 子问题2求解器
 * @author Gizing
 */
public class SubProblem2 extends SubProblem{

	/**
	 * 子问题2的求解方法
	 * 当达到精度要求或者最大迭代次数时，函数退出
	 * 
	 * @param attrFunctions 属性的生成函数列表，这些属性同属于一个数据表
	 * @param attrNames 属性名称列表，与属性生成函数列表一一对应
	 * @param expression 关于上述属性的表达式
	 * @param expType 表达式类型（>, >=, <, <=）
	 * @param k 要求比返回值小的元素的个数
	 * @return res 调整参数
	 */
	public SubProblemResult solve(List<AttrFunction> attrFunctions, List<String> attrNames, String expression, 
			String expType, int k) {
		if(Macro.debug) System.out.println("SubProblem2:\n" +"attrFunctions: " + attrFunctions + 
				"\nattrNames: " + attrNames + "\nexpression: " + expression + "\nexpType: " + 
				expType + "\nk: " + k + "\n");
		//对属性名和表达式作相关处理（因为mathematica中"."是保护字符）
		List<String> attrNamesTmp = new ArrayList<String>(attrNames);
		for(int i = 0; i < attrNames.size(); i++)
			attrNames.set(i, attrNames.get(i).replaceAll("\\.", ""));
		for(int i = 0; i < attrNamesTmp.size(); i++)
			expression = expression.replaceAll(attrNamesTmp.get(i), attrNames.get(i));
		//获取所有属性生成函数上的等值段信息
		List<Breakpoint> breakpoints = new ArrayList<Breakpoint>();
		for(int i = 0; i < attrFunctions.size(); i++) {
			List<EqualIndex> indexs = attrFunctions.get(i).getIndexs();
			for(int j = 0; j < indexs.size(); j++) {
				breakpoints.add(new Breakpoint(indexs.get(j).getIndex(), false, i, indexs.get(j).getNum()));
				breakpoints.add(new Breakpoint(indexs.get(j).getIndex() + indexs.get(j).getNum() - 1, true, 
						i, indexs.get(j).getNum()));
			}
		}
		Collections.sort(breakpoints);
		if(Macro.debug)	System.out.println(breakpoints + "\n");

		//分段函数列表（将多属性的生成函数转化成一个函数。由于等值段的原因，这个函数是分段的）
		List<PiecFunction> piecFunctions = new ArrayList<PiecFunction>();
		//当前定义域段上的等值属性列表
		List<Breakpoint> equalAttrbutes = new ArrayList<Breakpoint>();
		//上一个断点index值（从1计数）
		int lastIndex = 1;
		for(int i = 0; i < breakpoints.size(); i++) {
			//相同位置有多断点的处理
			if(i > 0 && breakpoints.get(i).getIndex() == breakpoints.get(i - 1).getIndex()) {
				if(!breakpoints.get(i).isType())	equalAttrbutes.add(breakpoints.get(i));
				else
					//删除已达终点的等值段
					for(int j = 0; j < equalAttrbutes.size(); j++)
						if(equalAttrbutes.get(j).getAttrFunctionid() == breakpoints.get(i).getAttrFunctionid()) {
							equalAttrbutes.remove(j); break;
						}
				continue;
			}
			if(!breakpoints.get(i).isType()) {
				piecFunctions.add(new PiecFunction(lastIndex, breakpoints.get(i).getIndex() - 1, 
						getAttrFunctionids(equalAttrbutes)));
				lastIndex = breakpoints.get(i).getIndex();
				equalAttrbutes.add(breakpoints.get(i));
			} else {
				piecFunctions.add(new PiecFunction(lastIndex, breakpoints.get(i).getIndex(), 
						getAttrFunctionids(equalAttrbutes)));
				lastIndex = breakpoints.get(i).getIndex() + 1;
				for(int j = 0; j < equalAttrbutes.size(); j++)
					if(equalAttrbutes.get(j).getAttrFunctionid() == breakpoints.get(i).getAttrFunctionid()) {
						equalAttrbutes.remove(j); break;
					}
			}
		}
		//m为数据表大小（默认attrFunctions的size大于0）
		int m = attrFunctions.get(0).getNum();
		if(lastIndex < m)	piecFunctions.add(new PiecFunction(lastIndex, m, getAttrFunctionids(equalAttrbutes)));
		if(Macro.debug)	System.out.println(piecFunctions + "\n");

		//计算各分段函数上每个属性生成函数index的偏移量
		for(int i = 0; i < piecFunctions.size(); i++) {
			int[] offsets = new int[attrFunctions.size()];
			for(int j = 0; j < attrFunctions.size(); j++) {
				List<EqualIndex> indexs = attrFunctions.get(j).getIndexs();
				for(int l = 0; l < indexs.size(); l++)
					if(indexs.get(l).getIndex() + indexs.get(l).getNum() - 1 < piecFunctions.get(i).getStart())
						offsets[j] += indexs.get(l).getNum() - 1;
					else if(indexs.get(l).getIndex() < piecFunctions.get(i).getStart())
						offsets[j] += piecFunctions.get(i).getStart() - indexs.get(l).getIndex();
			}
			piecFunctions.get(i).setOffsets(offsets);
		}
		if(Macro.debug)	System.out.println(piecFunctions + "\n");

		//将piecFunctions相关信息转化成：函数列表和常数列表
		//第一个list对应分段函数，第二个list对应各属性系数
		List<ArrayList<Function>> functionsList = new ArrayList<ArrayList<Function>>();
		//functions的定义域
		List<DefiDomain> defiDomains = new ArrayList<DefiDomain>();
		//functions定义中index点个数
		List<Integer> sizes = new ArrayList<Integer>();
		List<Constant> constants = new ArrayList<Constant>();
		for(int i = 0; i < piecFunctions.size(); i++) {
			//该分段函数为常数
			if(piecFunctions.get(i).getEqualAttrbutes().length == attrFunctions.size()) {
				float[] values = new float[attrFunctions.size()];
				for(int j = 0; j < attrFunctions.size(); j++) {
					float x = attrFunctions.get(j).getS() + (piecFunctions.get(i).getStart() - 
							piecFunctions.get(i).getOffsets()[j] - 1) * attrFunctions.get(j).getStep();
					values[j] = attrFunctions.get(j).getC0() * x + attrFunctions.get(j).getC1();
				}
				constants.add(new Constant(mathematica.singleTableExpCalcu(values, attrNames, expression), 
						piecFunctions.get(i).getEnd() - piecFunctions.get(i).getStart() + 1));
				continue;
			}
			//该分段函数不为常数，需要将各个属性生成函数的变量转化成由一个变量表示
			//首先选择一个最小的step（选择范围：非等值属性）
			float step = Float.MAX_VALUE;
			loop : for(int j = 0; j < attrFunctions.size(); j++) {
				//确保选择范围为非等值属性
				for(int l = 0; l < piecFunctions.get(i).getEqualAttrbutes().length; l++)
					if(piecFunctions.get(i).getEqualAttrbutes()[l] == j)	continue loop;
				if(attrFunctions.get(j).getStep() < step)	step = attrFunctions.get(j).getStep();
			}
			//为每个属性分别计算c0和c1
			ArrayList<Function> functionsItem = new ArrayList<Function>();
			loop : for(int j = 0; j < attrFunctions.size(); j++) {
				float c0 = 0, c1 = 0;
				float x = attrFunctions.get(j).getS() + (piecFunctions.get(i).getStart() - 
						piecFunctions.get(i).getOffsets()[j] - 1) * attrFunctions.get(j).getStep();
				//计算等值属性的值
				for(int l = 0; l < piecFunctions.get(i).getEqualAttrbutes().length; l++)
					if(piecFunctions.get(i).getEqualAttrbutes()[l] == j) {
						c1 = attrFunctions.get(j).getC0() * x + attrFunctions.get(j).getC1();
						functionsItem.add(new Function(c0, c1));
						continue loop;
					}
				//非等值属性
				c0 = attrFunctions.get(j).getC0() * (attrFunctions.get(j).getStep() / step);
				c1 = attrFunctions.get(j).getC0() * x + attrFunctions.get(j).getC1();
				functionsItem.add(new Function(c0, c1));
			}
			functionsList.add(functionsItem);
			//修正后的定义域
			int size = piecFunctions.get(i).getEnd() - piecFunctions.get(i).getStart() + 1;
			sizes.add(size);
			defiDomains.add(new DefiDomain(0 - step / 2, 0 + (size - 1 + 0.5f) * step));
		}
		if(Macro.debug) {
			System.out.println("functions & defiDomains:");
			for(int i = 0; i < functionsList.size(); i++) {
				System.out.println(functionsList.get(i) + "  " + defiDomains.get(i) + "  " + sizes.get(i));
			}
			System.out.println("constants:");
			for(int i = 0; i < constants.size(); i++)
				System.out.println(constants.get(i) + "\n");
		}

		//所有函数在该定义域上的综合最值
		double max = -Double.MAX_VALUE, min = Double.MAX_VALUE, tmp;
		for(int i = 0; i < functionsList.size(); i++) {
			tmp = mathematica.singleTableExpExtremeValue(functionsList.get(i), attrNames, 
					defiDomains.get(i).start, defiDomains.get(i).end, expression, "Max");
			if(tmp > max)	max = tmp;
			tmp = mathematica.singleTableExpExtremeValue(functionsList.get(i), attrNames, 
					defiDomains.get(i).start, defiDomains.get(i).end, expression, "Min");
			if(tmp < min)	min = tmp;
		}
		if(Macro.debug)	System.out.println("max: " + max + "\nmin: " + min + "\n");
		//给出初始预测值
		double c = (max - min) / m * k + min;

		//迭代逼近目标解
		double res = -1, resCount = -1;//记录最优解
		for(int i = 0; i < Macro.iter; i++) {
			if(Macro.debug)	System.out.println("iter: " + i + "\nc: " + c + "\n");
			double count = 0;
			if(expType.equals(">")) {
				for(int j = 0; j < constants.size(); j++)
					if(constants.get(j).value > c)	count += constants.get(j).num;
			} else if(expType.equals(">=")) {
				for(int j = 0; j < constants.size(); j++)
					if(constants.get(j).value >= c)	count += constants.get(j).num;
			} else if(expType.equals("<")) {
				for(int j = 0; j < constants.size(); j++)
					if(constants.get(j).value < c)	count += constants.get(j).num;
			} else if(expType.equals("<=")) {
				for(int j = 0; j < constants.size(); j++)
					if(constants.get(j).value <= c)	count += constants.get(j).num;
			}
			for(int j = 0; j < functionsList.size(); j++) {
				count += mathematica.singleTableRatioCalcu(functionsList.get(j), attrNames, expression, 
						expType, c, defiDomains.get(j).start, defiDomains.get(j).end) * sizes.get(j);
			}
			if(Macro.debug)	System.out.println("count: " + count + "\n");

			//判断count是否达到解的精度要求
			if(Math.abs(count - k) / k <= Macro.precision)
			{
				SubProblemResult spr = new SubProblemResult(c,count);
				return spr;
				//return c;
			}
			if(Math.abs(count - k) / k < Math.abs(resCount - k) / k) {
				res = c;
				resCount = count;
			}
			if(Macro.debug)	System.out.println("precision: " + (Math.abs(count - k) / k) + 
					"\nres: " + res + "\nprecision: " + (Math.abs(resCount - k) / k) + "\n");

			//若解的精度未达到精度要求
			if(expType.equals(">") || expType.equals(">="))
				if(count > k) { min = c; c = (c + max) / 2; } 
				else { max = c; c = (c + min) / 2; }
			else
				if(count > k) { max = c; c = (c + min) / 2; } 
				else { min = c; c = (c + max) / 2; }
		}
		SubProblemResult spr = new SubProblemResult(res,resCount);
		return spr;
		//return res;
	}

	private int[] getAttrFunctionids(List<Breakpoint> equalAttrbutes) {
		int[] res = new int[equalAttrbutes.size()];
		for(int i = 0; i < equalAttrbutes.size(); i++) {
			res[i] = equalAttrbutes.get(i).getAttrFunctionid();
		}
		return res;
	}

	public static void main(String[] args) {
		AttrFunction attrFunction1 = new AttrFunction(3, 5, 3, 2, 100, 
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(16, 10,1), new EqualIndex(50, 20,4))),null,null);
		AttrFunction attrFunction2 = new AttrFunction(1, 6, -8, 3, 100, 
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(20, 10,2), new EqualIndex(45, 10,5))),null,null);
		AttrFunction attrFunction3 = new AttrFunction(2, -6, 12, 4, 100, 
				new ArrayList<EqualIndex>(Arrays.asList(new EqualIndex(42, 13,3), new EqualIndex(62, 10,6))),null,null);
		SubProblem2 subProblem2 = new SubProblem2();
		SubProblemResult result = subProblem2.solve(new ArrayList<AttrFunction>(Arrays.asList(attrFunction1, attrFunction2, 
				attrFunction3)), new ArrayList<String>(Arrays.asList("A.a1", "A.a2", "A.a3")), 
				"1*A.a2 + 2A.a1*A.a3 ^ 2+ 5", ">", 56);
		System.out.println(result);
		subProblem2.clear();
	}
}

/**
 * 定义域上的断点，由等值段带来的
 */
class Breakpoint implements Comparable<Breakpoint>{

	/** 断点位置 */
	private int index;

	/** false: 起点; true: 终点 */
	private boolean type;

	/** 属性生成函数在attrFunctions中的index */
	private int attrFunctionid;

	/** 该断点所属等值段的长度 */
	private int num;

	public Breakpoint(int index, boolean type, int attrFunctionid, int num) {
		super();
		this.index = index;
		this.type = type;
		this.attrFunctionid = attrFunctionid;
		this.num = num;
	}

	public int getIndex() {
		return index;
	}

	public boolean isType() {
		return type;
	}

	public int getAttrFunctionid() {
		return attrFunctionid;
	}

	public int getNum() {
		return num;
	}

	public int compareTo(Breakpoint o) {
		if(this.index > o.index)	return 1;
		else	return -1;
	}

	public String toString() {
		return "BreakPoint [index=" + index + ", type=" + type
				+ ", attrFunctionid=" + attrFunctionid + ", num=" + num + "]\n";
	}
}

/**
 * 各分段函数
 */
class PiecFunction {

	/** 该分段函数的index起点 */
	private int start;

	/** 该分段函数的index终点 */
	private int end;

	/** 该分段函数的哪些属性是等值的 */
	private int[] equalAttrbutes = null;

	/** 该分段函数上每个属性生成函数index的偏移量 */
	private int[] offsets = null;

	public PiecFunction(int start, int end, int[] equalAttrbutes) {
		super();
		this.start = start;
		this.end = end;
		this.equalAttrbutes = equalAttrbutes;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int[] getEqualAttrbutes() {
		return equalAttrbutes;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public String toString() {
		return "PiecFunction [start=" + start + ", end=" + end
				+ ", equalAttrbutes=" + Arrays.toString(equalAttrbutes)
				+ ", offsets=" + Arrays.toString(offsets) + "]\n";
	}
}

/**
 * 在某段定义域上，各个属性不都为等值段，从而为一个函数
 * 这里我们需要将不为等值段属性中的所有变量转化为一个变量
 */
class Function {

	/** 调整后的系数 */
	float c0;
	float c1;

	public Function(float c0, float c1) {
		super();
		this.c0 = c0;
		this.c1 = c1;
	}

	public String toString() {
		return "Function [c0=" + c0 + ", c1=" + c1 + "]";
	}
}


/**
 * 在某段定义域上，各个属性都为等值段，从而函数值为常数
 */
class Constant {

	/** 常数函数值 */
	double value;

	/** 定义域上index点个数 */
	int num;

	public Constant(double value, int num) {
		super();
		this.value = value;
		this.num = num;
	}

	public String toString() {
		return "Constant [value=" + value + ", num=" + num + "]";
	}
}

/**
 * 修正后的定义域，将原来的离散型定义域转化成连续性的
 */
class DefiDomain {

	float start;
	float end;

	public DefiDomain(float start, float end) {
		super();
		this.start = start;
		this.end = end;
	}

	public String toString() {
		return "DefiDomain [start=" + start + ", end=" + end + "]";
	}
}
