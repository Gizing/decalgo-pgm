package com.fileparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.datastructure.Constraint;
import com.datastructure.Table;

/**
 * 针对基数约束输入信息的解析器
 * 
 * @author Gizing
 */
public class FileParser
{

	/** 数据表schema信息列表 */
	private List<Table> tables = null;

	/** 基数约束id到基数约束的映射 */
	private HashMap<Integer, Constraint> constraintsMap = null;

	/** 基数约束id到其依赖的基数约束id List的映射 */
	private HashMap<Integer, List<Integer>> dependencesMap = null;
	
	//不参与处理的基数的编号
	private HashSet<Integer> notDoIdSet = null;

	public FileParser()
	{
		super();
		tables = new ArrayList<Table>();
		constraintsMap = new HashMap<Integer, Constraint>();
		dependencesMap = new HashMap<Integer, List<Integer>>();
		notDoIdSet = new HashSet<Integer>();
	}

	/**
	 * 对输入信息做语法检测 要求输入文件的格式为：T信息；换行；C信息；换行；R信息
	 * 
	 * @param inputFile
	 *            输入文件，包含数据表Schema信息、基数约束信息和基数约束依赖关系
	 * @param encode
	 *            输入文件编码格式
	 * @return boolean 输入信息语法是否正确
	 */
	private boolean syntaxCheck(File inputFile, String encode)
	{
		// 针对 “T[table_name, size, attr_name attr_type, ...]” 的正则表达式
		Pattern tPattern = Pattern.compile("[ ]*T[ ]*" + "\\[" + "[ ]*[a-zA-Z_$]+[0-9a-zA-Z_$]*[ ]*," + "[ ]*[0-9]+[ ]*"
				+ "(,[ ]*[a-zA-Z_$]+[0-9a-zA-Z_$]*[ ]+(int||long||float||double||decimal"
				+ "\\([ ]*[0-9]+[ ]*,[ ]*[0-9]+[ ]*\\))[ ]*)+" + "\\]");
		// 针对 “C[id, exp; ..., type; ..., num]” 的正则表达式
		Pattern cPattern = Pattern.compile("[ ]*C[ ]*" + "\\[" + "[ ]*[0-9]+[ ]*,"
				+ "[ ]*[([a-zA-Z_$]+[0-9a-zA-Z_$]*\\.[a-zA-Z_$]+[0-9a-zA-Z_$])\\+\\-\\*\\/\\^0-9 ]+[ ]*"
				+ "(;[ ]*[([a-zA-Z_$]+[0-9a-zA-Z_$]*\\.[a-zA-Z_$]+[0-9a-zA-Z_$])\\+\\-\\*\\/\\^0-9 ]+[ ]*)*"
				+ ",[ ]*(>=||>||<=||<||=)[ ]*" + "(;[ ]*(>=||>||<=||<||=)[ ]*)*" + ",[ ]*[0-9]+[ ]*" + "\\]");
		// 针对 “R[id1, id2]” 的正则表达式
		Pattern rPattern = Pattern.compile("[ ]*R[ ]*" + "\\[" + "[ ]*[0-9]+[ ]*," + "[ ]*[0-9]+[ ]*" + "\\]");
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encode));
			String inputLine = null;
			// 表示当前输入信息的类型：0:T; 1:C; 2:R
			int flag = 0;
			while ((inputLine = br.readLine()) != null)
			{
				if (inputLine.matches("[ \t]*"))
				{
					flag++;
					continue;
				}
				switch (flag)
				{
				case 0:
					if (!tPattern.matcher(inputLine).matches())
						return false;
					break;
				case 1:
					if (!cPattern.matcher(inputLine).matches())
						return false;
					break;
				case 2:
					if (!rPattern.matcher(inputLine).matches())
						return false;
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (br != null)
					br.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	/** @see parse(File inputFile, String encode) */
	public byte parse()
	{
		// 默认文件编码格式为"utf-8"
		return parse("utf-8");
	}

	/**
	 * 将输入信息提取到相关数据结构中
	 * 
	 * @param inputFile
	 *            输入文件，包含数据表Schema信息、基数约束信息和基数约束依赖关系
	 * @param encode
	 *            输入文件编码格式
	 * @return byte -1:语法错误；-2：C中id有重复；-3：R中id不存在；1：解析成功
	 */
	public byte parse(String encode)
	{
		Properties prop = new Properties();
		FileInputStream fis = null;
		File inputFile = null;
		try
		{
			//Properties prop = new Properties();
			fis = new FileInputStream("./src/configure.properties");
			prop.load(fis);
			inputFile = new File(prop.getProperty("inputFilePath"));
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally
		{
			try
			{
				if(fis!=null)
					fis.close();
				prop.clear();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!syntaxCheck(inputFile, encode))
			return -1;
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encode));
			String inputLine = null;
			String[] arr = null;
			// 表示当前输入信息的类型：0:T; 1:C; 2:R
			int flag = 0;
			while ((inputLine = br.readLine()) != null)
			{
				if (inputLine.matches("[ \t]*"))
				{
					flag++;
					continue;
				}
				switch (flag)
				{
				case 0:
					inputLine = inputLine.substring(inputLine.indexOf('[') + 1, inputLine.indexOf(']'));
					arr = inputLine.split(",");
					tables.add(new Table(arr[0].trim(), Integer.parseInt(arr[1].trim()),
							Arrays.copyOfRange(arr, 2, arr.length)));
					break;
				case 1:
					inputLine = inputLine.substring(inputLine.indexOf('[') + 1, inputLine.indexOf(']'));
					arr = inputLine.split(",");
					int id = Integer.parseInt(arr[0].trim());
					String[] expressions = arr[1].replaceAll(" ", "").split(";");
					String[] expTypes = arr[2].replaceAll(" ", "").split(";");
					int size = Integer.parseInt(arr[3].trim());
					if (expressions.length != expTypes.length)
						return -1;
					if (constraintsMap.containsKey(id))
						return -2;
					constraintsMap.put(id, new Constraint(id, expressions, expTypes, size));
					break;
				case 2:
					inputLine = inputLine.substring(inputLine.indexOf('[') + 1, inputLine.indexOf(']'));
					arr = inputLine.split(",");
					int id1 = Integer.parseInt(arr[0].trim());
					int id2 = Integer.parseInt(arr[1].trim());
					if (!constraintsMap.containsKey(id1) || !constraintsMap.containsKey(id2))
						return -3;
					if (dependencesMap.containsKey(id2))
						dependencesMap.get(id2).add(id1);
					else
						dependencesMap.put(id2, new ArrayList<Integer>(Arrays.asList(id1)));
					break;
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (br != null)
					br.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		//读取不参与处理的信息
		File notDoFile = null;
		try
		{
			fis = new FileInputStream("./src/configure.properties");
			prop.load(fis);
			notDoFile = new File(prop.getProperty("notDoFilePath"));
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(notDoFile),encode));
			String inputLine = null;
			while((inputLine = br.readLine())!=null)
			{
				inputLine = inputLine.trim();
				if(inputLine.startsWith("#"))
					continue;
				notDoIdSet.add(Integer.parseInt(inputLine));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
				if(br!=null)
					br.close();
				if(fis!=null)
					fis.close();
				prop.clear();
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return 1;
	}

	public List<Table> getTables()
	{
		return tables;
	}

	public HashMap<Integer, Constraint> getConstraintsMap()
	{
		return constraintsMap;
	}

	public HashMap<Integer, List<Integer>> getDepandencesMap()
	{
		return dependencesMap;
	}
	
	public HashSet<Integer> getNotDoIdSet()
	{
		return notDoIdSet;
	}

	public static void main(String[] args)
	{
		FileParser parser = new FileParser();
		byte res = parser.parse();
		switch (res)
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
			System.out.println(parser.getConstraintsMap());
			System.out.println(parser.getDepandencesMap());
			System.out.println(parser.getNotDoIdSet());
			break;
		}
	}
}
