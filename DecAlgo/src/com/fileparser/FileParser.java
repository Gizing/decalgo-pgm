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
 * ��Ի���Լ��������Ϣ�Ľ�����
 * 
 * @author Gizing
 */
public class FileParser
{

	/** ���ݱ�schema��Ϣ�б� */
	private List<Table> tables = null;

	/** ����Լ��id������Լ����ӳ�� */
	private HashMap<Integer, Constraint> constraintsMap = null;

	/** ����Լ��id���������Ļ���Լ��id List��ӳ�� */
	private HashMap<Integer, List<Integer>> dependencesMap = null;
	
	//�����봦��Ļ����ı��
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
	 * ��������Ϣ���﷨��� Ҫ�������ļ��ĸ�ʽΪ��T��Ϣ�����У�C��Ϣ�����У�R��Ϣ
	 * 
	 * @param inputFile
	 *            �����ļ����������ݱ�Schema��Ϣ������Լ����Ϣ�ͻ���Լ��������ϵ
	 * @param encode
	 *            �����ļ������ʽ
	 * @return boolean ������Ϣ�﷨�Ƿ���ȷ
	 */
	private boolean syntaxCheck(File inputFile, String encode)
	{
		// ��� ��T[table_name, size, attr_name attr_type, ...]�� ��������ʽ
		Pattern tPattern = Pattern.compile("[ ]*T[ ]*" + "\\[" + "[ ]*[a-zA-Z_$]+[0-9a-zA-Z_$]*[ ]*," + "[ ]*[0-9]+[ ]*"
				+ "(,[ ]*[a-zA-Z_$]+[0-9a-zA-Z_$]*[ ]+(int||long||float||double||decimal"
				+ "\\([ ]*[0-9]+[ ]*,[ ]*[0-9]+[ ]*\\))[ ]*)+" + "\\]");
		// ��� ��C[id, exp; ..., type; ..., num]�� ��������ʽ
		Pattern cPattern = Pattern.compile("[ ]*C[ ]*" + "\\[" + "[ ]*[0-9]+[ ]*,"
				+ "[ ]*[([a-zA-Z_$]+[0-9a-zA-Z_$]*\\.[a-zA-Z_$]+[0-9a-zA-Z_$])\\+\\-\\*\\/\\^0-9 ]+[ ]*"
				+ "(;[ ]*[([a-zA-Z_$]+[0-9a-zA-Z_$]*\\.[a-zA-Z_$]+[0-9a-zA-Z_$])\\+\\-\\*\\/\\^0-9 ]+[ ]*)*"
				+ ",[ ]*(>=||>||<=||<||=)[ ]*" + "(;[ ]*(>=||>||<=||<||=)[ ]*)*" + ",[ ]*[0-9]+[ ]*" + "\\]");
		// ��� ��R[id1, id2]�� ��������ʽ
		Pattern rPattern = Pattern.compile("[ ]*R[ ]*" + "\\[" + "[ ]*[0-9]+[ ]*," + "[ ]*[0-9]+[ ]*" + "\\]");
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encode));
			String inputLine = null;
			// ��ʾ��ǰ������Ϣ�����ͣ�0:T; 1:C; 2:R
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
		// Ĭ���ļ������ʽΪ"utf-8"
		return parse("utf-8");
	}

	/**
	 * ��������Ϣ��ȡ��������ݽṹ��
	 * 
	 * @param inputFile
	 *            �����ļ����������ݱ�Schema��Ϣ������Լ����Ϣ�ͻ���Լ��������ϵ
	 * @param encode
	 *            �����ļ������ʽ
	 * @return byte -1:�﷨����-2��C��id���ظ���-3��R��id�����ڣ�1�������ɹ�
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
			// ��ʾ��ǰ������Ϣ�����ͣ�0:T; 1:C; 2:R
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
		
		//��ȡ�����봦�����Ϣ
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
			System.out.println(parser.getConstraintsMap());
			System.out.println(parser.getDepandencesMap());
			System.out.println(parser.getNotDoIdSet());
			break;
		}
	}
}
