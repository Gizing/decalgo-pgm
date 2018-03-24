package com.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ����������
 * 
 * @author Gizing
 */
public class PrimeGenerator
{

	/** ��¼��ǰ������������� */
	private int s;

	public PrimeGenerator(int s)
	{
		super();
		this.s = s;
	}

	/**
	 * ����n���ڵ���������
	 * 
	 * @param n
	 *            ��Χ
	 * @return List<Integer> n���ڵ���������
	 */
	public static List<Integer> getPrimes(int n)
	{
		List<Integer> primes = new ArrayList<Integer>();
		primes.add(2);
		for (int i = 3; i <= n; i += 2)
		{
			int tmp = (int) Math.sqrt(i) + 1;
			for (int j = 2; j <= tmp; j++)
			{
				if (i % j == 0)
					break;
				if (j == tmp)
					primes.add(i);
			}
		}
		return primes;
	}

	/**
	 * ÿ����һ�η���һ����������֤��ǰ���ص�������֮ǰ���ص��������ظ�
	 * 
	 * @return ����һ����֮ǰ���ظ�������
	 */
	public int getPrimes()
	{
		if (s == 2)
			return s++;
		else
			for (int i = s; i <= Integer.MAX_VALUE; i++)
			{
				int tmp = (int) Math.sqrt(i) + 1;
				for (int j = 2; j <= tmp; j++)
				{
					if (i % j == 0)
						break;
					if (j == tmp)
					{
						s = i + 1;
						return i;
					}
				}
			}
		return -1;
	}

	public static void main(String[] args)
	{
		System.out.println(PrimeGenerator.getPrimes(100));
		Random random = new Random();
		PrimeGenerator primeGenerator1 = new PrimeGenerator(random.nextInt(1000));
		for (int i = 0; i < 100; i++)
		{
			System.out.print(primeGenerator1.getPrimes() + ",");
		}
		System.out.println();
		PrimeGenerator primeGenerator2 = new PrimeGenerator(random.nextInt(1000));
		for (int i = 0; i < 100; i++)
		{
			System.out.print(primeGenerator2.getPrimes() + ",");
		}
	}
}
