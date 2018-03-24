package mathematica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;
import com.datastructure.SubProblemResult;

/**
 * ������2�����
 * @author Gizing
 */
public class SubProblem2 extends SubProblem{

	/**
	 * ������2����ⷽ��
	 * ���ﵽ����Ҫ���������������ʱ�������˳�
	 * 
	 * @param attrFunctions ���Ե����ɺ����б���Щ����ͬ����һ�����ݱ�
	 * @param attrNames ���������б����������ɺ����б�һһ��Ӧ
	 * @param expression �����������Եı��ʽ
	 * @param expType ���ʽ���ͣ�>, >=, <, <=��
	 * @param k Ҫ��ȷ���ֵС��Ԫ�صĸ���
	 * @return res ��������
	 */
	public SubProblemResult solve(List<AttrFunction> attrFunctions, List<String> attrNames, String expression, 
			String expType, int k) {
		if(Macro.debug) System.out.println("SubProblem2:\n" +"attrFunctions: " + attrFunctions + 
				"\nattrNames: " + attrNames + "\nexpression: " + expression + "\nexpType: " + 
				expType + "\nk: " + k + "\n");
		//���������ͱ��ʽ����ش�����Ϊmathematica��"."�Ǳ����ַ���
		List<String> attrNamesTmp = new ArrayList<String>(attrNames);
		for(int i = 0; i < attrNames.size(); i++)
			attrNames.set(i, attrNames.get(i).replaceAll("\\.", ""));
		for(int i = 0; i < attrNamesTmp.size(); i++)
			expression = expression.replaceAll(attrNamesTmp.get(i), attrNames.get(i));
		//��ȡ�����������ɺ����ϵĵ�ֵ����Ϣ
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

		//�ֶκ����б��������Ե����ɺ���ת����һ�����������ڵ�ֵ�ε�ԭ����������Ƿֶεģ�
		List<PiecFunction> piecFunctions = new ArrayList<PiecFunction>();
		//��ǰ��������ϵĵ�ֵ�����б�
		List<Breakpoint> equalAttrbutes = new ArrayList<Breakpoint>();
		//��һ���ϵ�indexֵ����1������
		int lastIndex = 1;
		for(int i = 0; i < breakpoints.size(); i++) {
			//��ͬλ���ж�ϵ�Ĵ���
			if(i > 0 && breakpoints.get(i).getIndex() == breakpoints.get(i - 1).getIndex()) {
				if(!breakpoints.get(i).isType())	equalAttrbutes.add(breakpoints.get(i));
				else
					//ɾ���Ѵ��յ�ĵ�ֵ��
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
		//mΪ���ݱ��С��Ĭ��attrFunctions��size����0��
		int m = attrFunctions.get(0).getNum();
		if(lastIndex < m)	piecFunctions.add(new PiecFunction(lastIndex, m, getAttrFunctionids(equalAttrbutes)));
		if(Macro.debug)	System.out.println(piecFunctions + "\n");

		//������ֶκ�����ÿ���������ɺ���index��ƫ����
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

		//��piecFunctions�����Ϣת���ɣ������б�ͳ����б�
		//��һ��list��Ӧ�ֶκ������ڶ���list��Ӧ������ϵ��
		List<ArrayList<Function>> functionsList = new ArrayList<ArrayList<Function>>();
		//functions�Ķ�����
		List<DefiDomain> defiDomains = new ArrayList<DefiDomain>();
		//functions������index�����
		List<Integer> sizes = new ArrayList<Integer>();
		List<Constant> constants = new ArrayList<Constant>();
		for(int i = 0; i < piecFunctions.size(); i++) {
			//�÷ֶκ���Ϊ����
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
			//�÷ֶκ�����Ϊ��������Ҫ�������������ɺ����ı���ת������һ��������ʾ
			//����ѡ��һ����С��step��ѡ��Χ���ǵ�ֵ���ԣ�
			float step = Float.MAX_VALUE;
			loop : for(int j = 0; j < attrFunctions.size(); j++) {
				//ȷ��ѡ��ΧΪ�ǵ�ֵ����
				for(int l = 0; l < piecFunctions.get(i).getEqualAttrbutes().length; l++)
					if(piecFunctions.get(i).getEqualAttrbutes()[l] == j)	continue loop;
				if(attrFunctions.get(j).getStep() < step)	step = attrFunctions.get(j).getStep();
			}
			//Ϊÿ�����Էֱ����c0��c1
			ArrayList<Function> functionsItem = new ArrayList<Function>();
			loop : for(int j = 0; j < attrFunctions.size(); j++) {
				float c0 = 0, c1 = 0;
				float x = attrFunctions.get(j).getS() + (piecFunctions.get(i).getStart() - 
						piecFunctions.get(i).getOffsets()[j] - 1) * attrFunctions.get(j).getStep();
				//�����ֵ���Ե�ֵ
				for(int l = 0; l < piecFunctions.get(i).getEqualAttrbutes().length; l++)
					if(piecFunctions.get(i).getEqualAttrbutes()[l] == j) {
						c1 = attrFunctions.get(j).getC0() * x + attrFunctions.get(j).getC1();
						functionsItem.add(new Function(c0, c1));
						continue loop;
					}
				//�ǵ�ֵ����
				c0 = attrFunctions.get(j).getC0() * (attrFunctions.get(j).getStep() / step);
				c1 = attrFunctions.get(j).getC0() * x + attrFunctions.get(j).getC1();
				functionsItem.add(new Function(c0, c1));
			}
			functionsList.add(functionsItem);
			//������Ķ�����
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

		//���к����ڸö������ϵ��ۺ���ֵ
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
		//������ʼԤ��ֵ
		double c = (max - min) / m * k + min;

		//�����ƽ�Ŀ���
		double res = -1, resCount = -1;//��¼���Ž�
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

			//�ж�count�Ƿ�ﵽ��ľ���Ҫ��
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

			//����ľ���δ�ﵽ����Ҫ��
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
 * �������ϵĶϵ㣬�ɵ�ֵ�δ�����
 */
class Breakpoint implements Comparable<Breakpoint>{

	/** �ϵ�λ�� */
	private int index;

	/** false: ���; true: �յ� */
	private boolean type;

	/** �������ɺ�����attrFunctions�е�index */
	private int attrFunctionid;

	/** �öϵ�������ֵ�εĳ��� */
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
 * ���ֶκ���
 */
class PiecFunction {

	/** �÷ֶκ�����index��� */
	private int start;

	/** �÷ֶκ�����index�յ� */
	private int end;

	/** �÷ֶκ�������Щ�����ǵ�ֵ�� */
	private int[] equalAttrbutes = null;

	/** �÷ֶκ�����ÿ���������ɺ���index��ƫ���� */
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
 * ��ĳ�ζ������ϣ��������Բ���Ϊ��ֵ�Σ��Ӷ�Ϊһ������
 * ����������Ҫ����Ϊ��ֵ�������е����б���ת��Ϊһ������
 */
class Function {

	/** �������ϵ�� */
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
 * ��ĳ�ζ������ϣ��������Զ�Ϊ��ֵ�Σ��Ӷ�����ֵΪ����
 */
class Constant {

	/** ��������ֵ */
	double value;

	/** ��������index����� */
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
 * ������Ķ����򣬽�ԭ������ɢ�Ͷ�����ת���������Ե�
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
