package com.algorithm;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastructure.*;

import mathematica.Macro;

/**
 * ��������ģ��
 * @author Gizing
 *
 */
public class GenerateData implements Runnable
{
	private Table table = null;
	private CountDownLatch cdl = null;
	
	/**
	 * ���캯��
	 * @param tables ����Ϣ������Լ����Ϣ��ȫ��ע��
	 */
	public GenerateData(Table table, CountDownLatch cdl)
	{
		this.table = table;
		this.cdl = cdl;
	}
	
	public GenerateData(Table table)
	{
		this.table = table;
	}
	
	
	/**
	 * ������������
	 * ���һ��������һ��tableName.txt������,�����ڴ������������ٵ����ļ�
	 */
	private void generateDataColumn()
	{
		//��������,һ������һ��
		int row = table.getSize();
		int column = table.getAttributes().size();
		Object[][] geneData = new Object[row][column];
		for(int j=0;j < column;j++)
		{
			AttrFunction af = table.getAttributes().get(j).getAttrFunction();
			af.sort();
			for(int i=0;i < row;i++)
				geneData[i][j] = af.getGeneData(i);
		}
		
		//�����ļ�
		Properties prop = new Properties();
		FileInputStream fis = null;
		PrintWriter pw = null;
		try
		{
			fis = new FileInputStream("./src/configure.properties");
			prop.load(fis);
			StringBuilder outputPath = new StringBuilder(prop.getProperty("outputDataDirectory"));
			outputPath.append(table.getTableName());
			outputPath.append(".txt");
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputPath.toString())));
			
			for(int i=0;i<row;i++)
			{
				for(int j=0;j<column;j++)
				{
					pw.print(geneData[i][j]);
					if(j+1==column)
						pw.println();
					else
						pw.print('\t');
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}finally
		{
			pw.close();
			try
			{
				fis.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * �����������ݣ�����һ��д��һ��
	 */
	private void generateDataRow()
	{
		int row = table.getSize();
		int column = table.getAttributes().size();
		Properties prop = new Properties();
		FileInputStream fis = null;
		PrintWriter pw = null;
		try
		{
			fis = new FileInputStream("./src/configure.properties");
			prop.load(fis);
			StringBuilder outputPath = new StringBuilder(prop.getProperty("outputDataDirectory"));
			outputPath.append(table.getTableName());
			outputPath.append(".txt");
			pw = new PrintWriter(new BufferedWriter(new FileWriter(outputPath.toString())));
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<column;j++)
			{
				AttrFunction af = table.getAttributes().get(j).getAttrFunction();
				af.sort();
				if(!Macro.verifyGeneration)
					pw.print(af.getGeneData(i));
				else
					pw.print(af.verifyGeneration(i));
				
				if(j+1==column)
					pw.println();
				else
					pw.print('\t');
			}
		}
		
		pw.close();
		try
		{
			if (fis != null)
				fis.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void run()
	{
		if(Macro.row)
			generateDataRow();
		else
			generateDataColumn();
		cdl.countDown();
	}
	
	//when we don't use multiple thread, we use this function
	public void runSingleThread()
	{
		if(Macro.row)
			generateDataRow();
		else
			generateDataColumn();
	}
	
	public Table getTable()
	{
		return table;
	}
	
	public static void main(String[] args)
	{
		
	}

}
