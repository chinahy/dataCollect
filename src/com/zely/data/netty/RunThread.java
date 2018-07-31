package com.zely.data.netty;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.zely.data.dbcp.JdbcUtilDBCP;
import com.zely.data.utils.ConstantsUtil;

@SuppressWarnings("all")
public class RunThread {
	public static void judgeImeiAndInsertMC(String imei,String msisdn){
		System.out.println(imei+"测试 "+msisdn);
		//获取连接池
		Connection conn = null;
		// 创建运用类
		CURD curd = new CURD();
		try {
			conn = JdbcUtilDBCP.getConnection();
			int count = curd.checkImeiForModuleCards(conn,imei);
			if(count>=1){
				System.out.println("imei在moudle_cards中存在");
				return ;
			}else {
				int result = curd.insertModuleCards(conn, imei, msisdn);
			}
		} catch (SQLException e) {
			System.out.println("数据插入异常");
			e.printStackTrace();
		}finally {
			JdbcUtilDBCP.release(conn);
		}
	}
	public static void dbConn(String[] strArray,Map<String,String> gpsMap) {
		// 正删改查类
		CURD curd = new CURD();
		// 获取的对应的连接
		Connection conn =null;
		try {
			 conn = JdbcUtilDBCP.getConnection();
			// 根据传入的数据结构知道第一个为imei
			String imei = strArray[0];
			// 判断imei是否存在,同时创表
			Map<String,String> map = curd.checkImei(conn, imei);
		
			String tableName=map.get(ConstantsUtil.FRAM_TABLENAME);
			if(tableName.substring(0,2).equals(ConstantsUtil.INDEX_DB)){
				// 这样写的目的是帮李晓红 跟新
				curd.updateWorkStatus(conn, strArray);
				int i=curd.countByDb(conn, tableName);
				//說明該表的內容大於兩千條
				if(i==1){
					System.out.println("最新的数据1");
					// 油量的查询最大值和最小值
					Map<String,String> oilMap=	curd.findOilMaxAndMin(conn, tableName);
					System.out.println("最新的数据2");
					curd.updateOilMaxOrMin(conn,oilMap,imei);
					System.out.println("最新的数据3");
				}
			}
			String [] retArray=null;
			//包含db说明管理员已经绑定了imei
			retArray =curd.reckonData(map,tableName,conn,strArray,gpsMap);
			curd.insert(tableName, conn, retArray);
			//计算数据
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("插入数据异常");
		} finally {
			JdbcUtilDBCP.release(conn);
		}
	}

}
