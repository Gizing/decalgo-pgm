package multipleAttributes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import com.wolfram.jlink.*;

public class CalculateLink
{
	static KernelLink ml = null;
	private String mathematicaPath = null;
	//private StringBuilder equationsResult = null;
	//private HashMap<String,Double> equationsResult = null;

	public CalculateLink()
	{
		mathematicaPath = "-linkmode launch -linkname 'G:\\Wolfram Research\\Mathematica\\10.0\\MathKernel'";
		try
		{
			ml = MathLinkFactory.createKernelLink(mathematicaPath);
			ml.discardAnswer();
			
		} catch (MathLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String findCliques(String input)
	{
		//input.append(";FindClique[g,Infinity,All]");
		return ml.evaluateToInputForm(input,100);
	}
	
	//输入表达式，极大团属性值，和属性值一一对应的取值，如果此取值满足约束表达式则返回true，否则false
	public boolean isFulfill(String expr,String[] attributesName,int[] values)
	{
		StringBuilder rule2 = new StringBuilder("rule2=");
		StringBuilder expr2 = new StringBuilder("rule2/.{");
		
		//attributesName和values的大小一定是相等的
		rule2.append(expr);
		ml.evaluateToOutputForm(rule2.toString(),0);
		for(int i=0;i<attributesName.length;i++)
		{
			expr2.append(attributesName[i]);
			expr2.append("->");
			expr2.append(values[i]);
			if(i+1==attributesName.length)
			{
				expr2.append('}');
			}
			else
			{
				expr2.append(',');
			}
		}
		String temp = ml.evaluateToInputForm(expr2.toString(), 7);
		if(temp.startsWith("F"))
		{
			return false;
		}
		else
		{
			return true;
		}
		//String temp = ml.evaluateToOutputForm("fuck /. {x -> 1/2, y -> 1/2}", 30);
		
	}

	//输入方程组，输出各个变量的值(利用HashMap实现)
	public HashMap<String,Double> solveEquationsResult(String equations)
	{
//		try
//		{
//			ml.discardAnswer();
//		} catch (MathLinkException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//测试用，将equations写出到文件
		try
		{
			FileWriter fw = new FileWriter("g:/output.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(equations);
			bw.close();
			fw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(equations.length());
		
		HashMap<String,Double> equationsResult = new HashMap<String,Double>();
		StringBuilder evaluateAnswer = new StringBuilder(ml.evaluateToInputForm(equations, 3*equations.length()));
		evaluateAnswer.delete(0, 2);
		evaluateAnswer.delete(evaluateAnswer.length()-2, evaluateAnswer.length());
		
		//String evaluateAnswer = ml.evaluateToInputForm(equations, equations.length());
		
		String[] evaluateAnswerParts = evaluateAnswer.toString().split(", ");
		String[] tempResult = null;
		

		
		for(int i=0;i<evaluateAnswerParts.length;i++)
		{
			tempResult = evaluateAnswerParts[i].split(" -> ");
			
			//这一步有问题，解如果不是一个数值怎么办？
			equationsResult.put(tempResult[0], Double.parseDouble(tempResult[1]));
		}
		
		return equationsResult;
	}
	

	
	public void setMathematicaPath(String path)
	{
		this.mathematicaPath = path;
	}
	public String getMathematicaPath()
	{
		return mathematicaPath;
	}
	
	public void closeLink()
	{
		if(ml!=null)
		{
			ml.close();
		}
	}
}
