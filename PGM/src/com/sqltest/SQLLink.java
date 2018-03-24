package com.sqltest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于数据连接的类
 * @author Gizing
 *
 */
public class SQLLink
{
	private String driver = null;// 驱动程序名
	private String url = null; //访问的数据库
	private String username = null;// MySQL配置时的用户名
	private String password = null;// MySQL配置时的密码
	private Connection conn = null;
	
	//private String sql = null;//sql语句
	private PreparedStatement preSta = null;
	private ResultSet rs = null;
	
	public SQLLink()
	{
		driver = "com.mysql.jdbc.Driver";
		url = "jdbc:mysql://127.0.0.1:3306/sqltest";
		username = "saojie";
		password = "kaiji";
		
		
		try
		{
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void execute(String sql)
	{
		try
		{
			preSta = conn.prepareStatement(sql);
			preSta.execute(sql);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public ResultSet executeQuery(String sql)
	{
		try
		{
			preSta = conn.prepareStatement(sql);
			rs = preSta.executeQuery();
			
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
		return rs;
	}
	
	public int executeUpdate(String sql)
	{
		int result = 0;
		try
		{
			preSta = conn.prepareStatement(sql);
			result = preSta.executeUpdate();
			
			
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void close()
	{
		try
		{
			conn.close();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
