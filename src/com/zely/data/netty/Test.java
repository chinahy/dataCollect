package com.zely.data.netty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.zely.data.utils.StringUtils;

public class Test {
	public static final String DBDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
    
    public static final String DBURL = "jdbc:sqlserver://localhost:1433;DatabaseName=nongji";  
      
    public static final String DBUSER = "sa";  
      
    public static final String DBPASS = "Hy123";  
      
    public static void main(String[] args) throws Exception{  
          
        /*Connection conn = null;//表示数据库连接的对象   
          
        Statement stmt = null;//表示数据库更新操作  
          
        ResultSet result = null;//表示接受数据库查询到的结果  
          
        Class.forName(DBDRIVER);//使用class类加载驱动程序  
          
        conn = DriverManager.getConnection(DBURL, DBUSER, DBPASS);//连接数据库  
          
        stmt = conn.createStatement();//tatement接口需要通过connection接口进行实例化操作  
        long a = System.currentTimeMillis();
        result = stmt.executeQuery("select * from db_8");//执行sql语句，结果集放在result中  
        long b=System.currentTimeMillis();
        System.out.println(a-b);
        int i=0;
        while(result.next()){
        	i++;
        }  
        System.out.println(i+"条数"+(System.currentTimeMillis()-a));
        result.close();  
        stmt.close();  
        conn.close();  */
    	System.out.println(StringUtils.date());
          
    }  
  
}
