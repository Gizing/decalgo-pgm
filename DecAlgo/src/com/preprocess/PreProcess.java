package com.preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.datastructure.Constraint;
import com.fileparser.FileParser;

/**
 * Ԥ������
 * ���Է���
 * ��λ�
 * @author Gizing
 * 
 */
public class PreProcess
{
	private HashMap<Integer,Constraint> constraintsMap = null;
	private HashMap<Integer,List<Integer>> dependencesMap = null;
	private HashSet<Integer> notDoIdSet = null;
	
	public PreProcess(HashMap<Integer,Constraint> constraintsMap,HashMap<Integer,List<Integer>> dependencesMap,HashSet<Integer> notDoIdSet)
	{
		this.constraintsMap = constraintsMap;
		this.dependencesMap = dependencesMap;
		this.notDoIdSet = notDoIdSet;
	}
	
	/**
	 * ���Է��� 
	 * @author LiYuming
	 * @return ���Է���Ľ����ArrayList��ÿ��Ԫ����һ�����Է��飬ÿ�����Զ�������
	 */
	public ArrayList<HashSet<String>> groupAttributes()
	{
		ArrayList<HashSet<String>> attributesGroup = new ArrayList<HashSet<String>>();
		// key: ��������; value: �������������id
		HashMap<String, Integer> attributesGroupMap = new HashMap<String, Integer>();
		Iterator<Entry<Integer, Constraint>> constraintsMapIter = constraintsMap.entrySet().iterator();
		while (constraintsMapIter.hasNext())
		{
			Entry<Integer, Constraint> constraintsMapEntry = constraintsMapIter.next();
			if (constraintsMapEntry.getValue().getExpressions().length == 1
					&& constraintsMapEntry.getValue().getExpTypes()[0].equals("="))
			{
				String[] arr = constraintsMapEntry.getValue().getExpressions()[0].split("[-\\+\\*/]");
				List<String> arrTmp = new ArrayList<String>();
				for (String item : arr)
					if (!item.trim().matches("[0-9]+") && !item.trim().equals(""))
						arrTmp.add(item);
				// ȷ��arr��ֻ����������
				arr = arrTmp.toArray(new String[0]);
				int i = 0;
				for (; i < arr.length; i++)
					if (attributesGroupMap.containsKey(arr[i]))
						break;
				// ��������֮ǰ��δ����ĳһ��
				if (i == arr.length)
				{
					int value = attributesGroupMap.size();
					for (int j = 0; j < arr.length; j++)
						attributesGroupMap.put(arr[j], value);
				} else
				{
					// list�д�ŵ�����Ҫ�ı������������
					List<String> list = new ArrayList<String>();
					for (int j = 0; j < arr.length; j++)
						if (attributesGroupMap.containsKey(arr[j]) && j != i)
						{
							int value = attributesGroupMap.get(arr[j]);
							if (value == attributesGroupMap.get(arr[i]))
								continue;
							Iterator<Entry<String, Integer>> attrGroupMapIter = attributesGroupMap.entrySet()
									.iterator();
							while (attrGroupMapIter.hasNext())
							{
								Entry<String, Integer> attrGroupMapEntry = attrGroupMapIter.next();
								if (attrGroupMapEntry.getValue() == value)
									list.add(attrGroupMapEntry.getKey());
							}
						}
					for (int j = 0; j < list.size(); j++)
						attributesGroupMap.put(list.get(j), attributesGroupMap.get(arr[i]));
					for (int j = 0; j < arr.length; j++)
						attributesGroupMap.put(arr[j], attributesGroupMap.get(arr[i]));
				}
			}
		}
		HashSet<Integer> set = new HashSet<Integer>(attributesGroupMap.values());
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Integer item : set)
		{
			map.put(item, map.size());
			attributesGroup.add(new HashSet<String>());
		}
		Iterator<Entry<String, Integer>> attributesGroupMapIter = attributesGroupMap.entrySet().iterator();
		while (attributesGroupMapIter.hasNext())
		{
			Entry<String, Integer> attributesGroupMapEntry = attributesGroupMapIter.next();
			attributesGroup.get(map.get(attributesGroupMapEntry.getValue())).add(attributesGroupMapEntry.getKey());
		}
		return attributesGroup;
	}
	
	/**
	 * ��λ�
	 * @return ��λ��Ľ��
	 */
	public ArrayList<HashSet<Integer>> layerConstraints()
	{
		ArrayList<HashSet<Integer>> constraintsLayer = new ArrayList<HashSet<Integer>>();
		// set ��¼�Ѿ�ȷ����εĻ���Լ��
		HashSet<Integer> set = new HashSet<Integer>();
		Iterator<Entry<Integer, Constraint>> constraintsMapIter = constraintsMap.entrySet().iterator();
		HashSet<Integer> list = new HashSet<Integer>();
		// ���ҵ���0��Ļ���Լ��
		while (constraintsMapIter.hasNext())
		{
			Entry<Integer, Constraint> entry = constraintsMapIter.next();
			if (!notDoIdSet.contains(entry.getKey()) && !dependencesMap.containsKey(entry.getKey()))
			{
				list.add(entry.getKey());
				set.add(entry.getKey());
			}
		}
		constraintsLayer.add(list);
		
		HashMap<Integer, List<Integer>> dependencesMapTmp = new HashMap<Integer, List<Integer>>(dependencesMap);
		Iterator<Entry<Integer, List<Integer>>> dependencesMapTmpIter = dependencesMapTmp.entrySet().iterator();
		while (true)
		{
			HashSet<Integer> layer = new HashSet<Integer>();
			while (dependencesMapTmpIter.hasNext())
			{
				Entry<Integer, List<Integer>> entry = dependencesMapTmpIter.next();
				if (set.containsAll(entry.getValue()))
					layer.add(entry.getKey());
			}
			constraintsLayer.add(layer);
			for (Integer item : layer)
				dependencesMapTmp.remove(item);
			if (dependencesMapTmp.size() == 0)
				break;
		}
		return constraintsLayer;
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
			PreProcess preprocessor = new PreProcess(parser.getConstraintsMap(), parser.getDepandencesMap(),parser.getNotDoIdSet());
			System.out.println("\nԤ�����");
			//System.out.println(parser.getConstraintsMap());
			//System.out.println(parser.getDepandencesMap());
			System.out.println(preprocessor.groupAttributes());
			System.out.println(preprocessor.layerConstraints());
			break;
		}
	}
}
