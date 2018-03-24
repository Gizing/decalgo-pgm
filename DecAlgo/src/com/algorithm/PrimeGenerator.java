package com.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 质数生成器
 * 
 * @author Gizing
 */
public class PrimeGenerator
{

	/** 记录当前生成质数的起点 */
	private int s;

	public PrimeGenerator(int s)
	{
		super();
		this.s = s;
	}

	/**
	 * 生成n以内的所有质数
	 * 
	 * @param n
	 *            范围
	 * @return List<Integer> n以内的所有质数
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
	 * 每调用一次返回一个质数，保证当前返回的质数与之前返回的质数不重复
	 * 
	 * @return 返回一个与之前不重复的质数
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
