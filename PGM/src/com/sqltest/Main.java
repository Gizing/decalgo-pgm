package com.sqltest;

public class Main
{

	public static void main(String[] args)
	{
		SQLLink sqllink = new SQLLink();
		// 要执行的SQL语句
		String sql = "create table person(id int,id2 int,id3 int);";
		sqllink.executeUpdate(sql);
//		sql = "insert into test values(0,'李捷荧')";
//		if(sqllink.executeUpdate(sql)!=0)
//		{
//			System.out.println("ok");
//		}
		sql = "load data local infile \"g:/data.txt\" into table person(id,id2,id3);";
		sqllink.execute(sql);
		sqllink.close();
		
	}

}
