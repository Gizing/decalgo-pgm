package mathematica;

import com.datastructure.SubProblemResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datastructure.AttrFunction;
import com.datastructure.EqualIndex;

/**
 * ������1�����
 * @author Gizing
 */
public class SubProblem1 extends SubProblem{

	/**
	 * ������1����ⷽ��
	 * ���ﵽ����Ҫ���������������ʱ�������˳�
	 * 
	 * @param attrFunction ���Ե����ɺ���
	 * @param attrName ��������
	 * @param expression ���ڸ����Եı��ʽ
	 * @param expType ���ʽ���ͣ�>, >=, <, <=��
	 * @param k Ҫ������������Ԫ�صĸ���
	 * @return res ��������
	 */
	public SubProblemResult solve(AttrFunction attrFunction, String attrName, String expression, String expType, int k) {
		if(Macro.debug) System.out.println("SubProblem1:\n" + "attrFunction: " + attrFunction.toString() + 
				"attrName: " + attrName + "\nexpression: " + expression + "\nexpType: " + expType + 
				"\nk: " + k + "\n");
		//���������ͱ��ʽ����ش�����Ϊmathematica��"."�Ǳ����ַ���
		String attrNameTmp = attrName;
		attrName = attrName.replaceAll("\\.", "");
		expression = expression.replaceAll(attrNameTmp, attrName);

		//��ֵ����Ϣ
		List<EqualIndex> indexs = attrFunction.getIndexs();
		//count1Ϊ��ֵ�ε�"����"����֮��
		int count1 = 0;
		//����yΪ����ֵ���Ϻ�����ֵ
		double[] y = new double[indexs.size()];
		for(int i = 0; i < indexs.size(); i++) {
			count1 += indexs.get(i).getNum() - 1;
			float x = attrFunction.getS() + (indexs.get(i).getIndex() - 1) * attrFunction.getStep();
			y[i] = mathematica.singleAttrExpCalcu(attrFunction.getC0(), attrFunction.getC1(), attrName, x, expression);
		}
		if(Macro.debug)	System.out.println("count1: " + count1 + "\ny: " + Arrays.toString(y) + "\n");

		//����������
		float start = attrFunction.getS() - attrFunction.getStep() / 2;
		float end = attrFunction.getS() + (attrFunction.getNum() - count1 - 1 + 0.5f) * attrFunction.getStep();
		if(Macro.debug)	System.out.println("start: " + start + "\nend: " + end + "\n");

		//�����ڸö������ϵ���ֵ
		double max = mathematica.singleAttrExpExtremeValue(attrFunction.getC0(), attrFunction.getC1(), 
				attrName, start, end, expression, "Max");
		double min = mathematica.singleAttrExpExtremeValue(attrFunction.getC0(), attrFunction.getC1(), 
				attrName, start, end, expression, "Min");
		if(Macro.debug)	System.out.println("max: " + max + "\nmin: " + min + "\n");
		//������ʼԤ��ֵ
		double c = (max - min) / attrFunction.getNum() * k + min;

		//�����ƽ�Ŀ���
		double res = -1, resCount2 = -1;//��¼���Ž�
		for(int i = 0; i < Macro.iter; i++) {
			if(Macro.debug)	System.out.println("iter: " + i + "\nc: " + c);
			double count2 = 0;
			//��δ���ʵ���е�low
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

			//����ǵ�ֵ��������Ԥ��ֵ��index�����
			double ratio = mathematica.singleAttrRatioCalcu(attrFunction.getC0(), attrFunction.getC1(),
					attrName, expression, expType, c, start, end);
			//��ǰcount2����������������������Ԥ��ֵ��index�����
			count2 += ratio * (attrFunction.getNum() - count1);
			if(Macro.debug)	System.out.println("count2: " + count2 + "\n");

			//�ж�count2�Ƿ�ﵽ��ľ���Ҫ��
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

			//����ľ���δ�ﵽ����Ҫ��
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
