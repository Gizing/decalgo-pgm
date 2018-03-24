package com.main;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.fileparser.*;
import com.preprocess.*;

import mathematica.Macro;

import com.algorithm.*;
import com.datastructure.*;

/**
 * ������������������ں�������
 * @author Gizing
 *
 */
public class Main
{

	public static void main(String[] args)
	{
		double startTime = System.currentTimeMillis();
		
		//�ļ���ȡģ��
		FileParser fileParser = new FileParser();
		fileParser.parse();
		
		//Ԥ����ģ��
		PreProcess preProcess = new PreProcess(fileParser.getConstraintsMap(), 
				fileParser.getDepandencesMap(), fileParser.getNotDoIdSet());
		ArrayList<HashSet<String>> groupInfo = preProcess.groupAttributes();
		ArrayList<HashSet<Integer>> layerInfo = preProcess.layerConstraints();
		
		//�Ⱥ�ģ��
		EqualityModule equalityModule = new EqualityModule(fileParser.getTables(),fileParser.getNotDoIdSet(),
				fileParser.getConstraintsMap(),fileParser.getDepandencesMap(),layerInfo,groupInfo);
		equalityModule.generateFunction();
		equalityModule.influenceRelation();
		equalityModule.handleEquality();
		equalityModule.calNonEquAdjustParameter();
		
		double geneStartTime = System.currentTimeMillis();
		if(Macro.multiThreadGeneration)
		{
			//��������,���̲߳���
			CountDownLatch cdl = new CountDownLatch(fileParser.getTables().size());
			Thread thread = null;
			for (Table table : fileParser.getTables())
			{
				thread = new Thread(new GenerateData(table, cdl));
				thread.start();
			}
			try
			{
				cdl.await();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			//we use single thread to generate data
			for(Table table : fileParser.getTables())
			{
				GenerateData gd = new GenerateData(table);
				gd.runSingleThread();
			}
		}
		System.out.println("Generate Time:"+(System.currentTimeMillis()-geneStartTime)/1000+'s');
		
		if(Macro.ouputEquaAdjParameter)
		{
			// ����Ⱥ�Լ���ĵ�������
			for (Map.Entry<Integer, Constraint> entry : fileParser.getConstraintsMap().entrySet())
				if (entry.getValue().getExpTypes()[0].equals("="))
					entry.getValue().calEquAdjustParameter();
		}
		
		//��Լ���ı��ʽ�͵�������������ļ�
		equalityModule.outputAdjustParameter();
		
		System.out.println("Calculate Time:" + Macro.calculateTime/1000 + 's');
		System.out.println("Total Time:"+(System.currentTimeMillis()-startTime)/1000+'s');
		System.out.println("compelete");
	}

}
