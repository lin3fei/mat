package com.cmcciot.mat.pushapi.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cmcciot.mat.pushapi.sms.util.Util;

public class SQLiteExecute
{
	private static String url = "jdbc:sqlite:";
	private static Connection conn = null;
	private static Statement statement = null;

	static
	{
		try
		{
			//连接SQLite的JDBC 
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 取得数据库连接
	 * 
	 * @return
	 */
	static synchronized Connection createConn()
	{
		try
		{
			//建立一个数据库名sms.db的连接 
			conn = DriverManager.getConnection(url + Util.p.getProperty("sqlitedb_path"));
			//conn = DriverManager.getConnection(url + "E://sqlite/db_weather.db");
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		return conn;
	}

	/**
	 * 插入操作
	 * 
	 * @param sql
	 */
	public static synchronized void executeInsert(String sql)
	{

		try
		{
			createConn();
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			statement.execute(sql);
			conn.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(conn, statement, null);
		}
	}

	/**
	 * 更新操作
	 * 
	 * @param sql
	 */
	public static synchronized void executeUpdate(String sql)
	{
		try
		{
			createConn();
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			statement.executeUpdate(sql);
			conn.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(conn, statement, null);
		}
	}

	/**
	 * 查询操作
	 * 
	 * @param sql
	 */
	public static synchronized List<Object[]> executeQuery(String sql)
	{
		ResultSet rs = null;
		List<Object[]> list = new ArrayList<Object[]>();
		try
		{
			createConn();
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			while (rs.next())
			{
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Object[] obj = new Object[columnCount];
				for (int i = 0; i < columnCount; i++)
				{
					obj[i] = rs.getObject(i + 1);
				}
				list.add(obj);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(conn, statement, rs);
		}
		return list;
	}

	/**
	 * 关闭连接
	 * 
	 * @param conn
	 * @param st
	 * @param rs
	 */
	public static void close(Connection conn, Statement st, ResultSet rs)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		if (st != null)
		{
			try
			{
				st.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

//	private static Session session = HibernateSessionFactory.getSession();

	public static void main(String[] args)
	{
//		int count=0;
//		List<String>  sqlList=new ArrayList<String>();
//		List<Object[]> list = executeQuery("select name,city_num  from citys");
//		@SuppressWarnings("unchecked")
//		List<City>  cityList=session.createQuery(" from City city where city.cityId not in(select c.cityId from CityForWeather c)").list();
//		
//		for(Object[] obj:list){
//			boolean bool=false;
//			String name=Util.convertToString(obj[0]);
//			int index=name.indexOf(".");
//			if(index>0){
//				name=name.substring(index+1);
//			}
//			String sql="insert into City_For_Weather(city_num,city_name,city_id)values('"+obj[1]+"','"+obj[0]+"',";
//		
//			for(City city:cityList){
//				if(city.getCityName().indexOf(name)>=0){
//					sql+="'"+city.getCityId()+"'";
//					bool=true;
//					count++;
//					break;
//				}
//			}
//			
//			if(!bool){
//				sql+="''";
//			}
//			sql+=")";
//			if(bool){
//			sqlList.add(sql);
//			}
//		}
//		
//		
//		try{
//		     BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c:\\Result.txt")));
//		     
//		     for(String str:sqlList){
//		     writer.write(str+"\r\n");
//		     }
//		     writer.close();
//
//		}catch(Exception e){
//
//		     }
//		System.out.println(count);
	}

}
