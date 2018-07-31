package com.zely.data.dbcp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class JdbcUtilDBCP {
	private static DataSource ds = null;
	// 在静态代码块中创建数据库连接池
	static {
		try {
			// 加载dbcp.properties配置文件
			//getClassLoader 是从classpath 下面去找 path默认是重该下面去找不用加'/'
			InputStream in = JdbcUtilDBCP.class.getClassLoader().getResourceAsStream("resources/dbcp.properties");
			Properties prop = new Properties();
			prop.load(in);
			// 创建数据源
			ds = BasicDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 从数据源中获取数据库连接
	public static Connection getConnection() throws SQLException {
		// 从数据源中获取数据库连接
		return ds.getConnection();
	}

	// 释放连接
	public static void release(Connection conn) {
		if (conn != null) {
			try {
				// 将Connection连接对象还给数据库连接池
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
