package com.zely.data.netty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Soundbank;

import com.zely.data.gps.GpsUtils;
import com.zely.data.utils.ConstantsUtil;
import com.zely.data.utils.StringUtils;

/**
 * 
 * @author hengyang
 *
 */
public class CURD {
	
	public static final Integer MAXCOUNT=0;
	/**
	 * 查询油量的最大值和最小值
	 * @param conn
	 * @param tableName
	 * @return
	 */
	public static Map<String,String>  findOilMaxAndMin(Connection conn,String tableName){
		Map<String,String> map = new HashMap<String,String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select  MIN(a.v_oil_amount) oil_min,MAX(a.v_oil_amount) oil_max FROM "+ tableName+" a"; 
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx111111111111111111111");
				map.put(ConstantsUtil.FRAM_OILMAX, rs.getString(ConstantsUtil.FRAM_OILMAX));
				map.put(ConstantsUtil.FRAM_OILMIN, rs.getString(ConstantsUtil.FRAM_OILMIN));
				break;
			}
		} catch (Exception e) {
			System.out.println("查询总条数错误");
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 更新油量的做最大值和最小值
	 * @param conn
	 * @param map
	 */
	public static  void updateOilMaxOrMin(Connection conn,Map<String,String> map,String imei){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if( null!= map.get(ConstantsUtil.FRAM_OILMAX)  && null!= map.get(ConstantsUtil.FRAM_OILMIN) ){
				System.out.println("xxxxxxx进入根性xxxxxxxxxxxxxxxxx");
				String sql = "update farm_machine SET oil_max= ?, oil_min=?  WHERE device_imei= ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, map.get(ConstantsUtil.FRAM_OILMAX) );
				pstmt.setString(2, map.get(ConstantsUtil.FRAM_OILMIN) );
				pstmt.setString(3, imei );
				pstmt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println("更新数据农机状态");
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static Integer   countByDb(Connection conn,String tableName){
		int  a=0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(1) mm from "+tableName;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				int  count = Integer.valueOf(rs.getString("mm"));
				if(count >MAXCOUNT){
					a=1;
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("查询总条数错误");
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return a;
	}
	
	
	/**
	 * 查询告警列表
	 * 
	 * @param conn
	 * @param imei
	 * @param a
	 * @return
	 * @throws Exception
	 */
	private static List<Map<String, String>> queryAlarmList(Connection conn, String imei, Integer a) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select a.machine_id,a.alarm_type,a.user_id from alarm_set_infor a,farm_machine b "
				+ "WHERE a.machine_id =b.id and b.device_imei='" + imei + "' and a.alarm_type=" + a + " and a.status="
				+ 1;
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			map = new HashMap<String, String>();
			map.put(ConstantsUtil.ALARM_TYPE, rs.getString(ConstantsUtil.ALARM_TYPE));
			map.put(ConstantsUtil.USER_ID, rs.getString(ConstantsUtil.USER_ID));
			map.put(ConstantsUtil.MACHINE_ID, rs.getString(ConstantsUtil.MACHINE_ID));
			list.add(map);
		}
		pstmt.close();
		return list;
	}

	/**
	 * 更改农机表,农机的状态
	 * 
	 * @param conn
	 * @param strArray
	 */
	public static void updateWorkStatus(Connection conn, String[] strArray) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "update farm_machine SET work_status=" + strArray[1] + " WHERE device_imei='" + strArray[0]
					+ "'";
			stmt=conn.createStatement();
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("更新数据农机状态");
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查询是否绑定imei
	 * 
	 * @param conn
	 * @param imei
	 * @return
	 */
	public static Map<String, String> checkImei(Connection conn, String imei) {
		Map<String, String> map = new HashMap<String, String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select top 1 * FROM farm_machine a WHERE a.device_imei=" + imei + "order by create_time desc";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			// 判断是否查询出结果
			if (rs.next()) {
				// 只需要拿去冻库id
				String str = rs.getString("id");
				imei = "db_" + str;
				// 如果绑定了imei 就需要将这些参数拿出来,进行换算
				map.put(ConstantsUtil.FRAM_OILPOOL, rs.getString("oil_pool"));
				map.put(ConstantsUtil.FRAM_OILMAX, rs.getString("oil_max"));
				map.put(ConstantsUtil.FRAM_OILMIN, rs.getString("oil_min"));
				map.put(ConstantsUtil.FRAM_OILALARM, rs.getString("oil_alarm"));
				map.put(ConstantsUtil.FARM_BATTERYELEALARM, rs.getString("battery_ele_alarm"));
				map.put(ConstantsUtil.FRAM_MODELEALARM, rs.getString("mod_ele_alarm"));
			}
			crateTable(conn, pstmt, imei);
			map.put(ConstantsUtil.FRAM_TABLENAME, imei);
			return map;
		} catch (Exception e) {
			System.out.println("查询冻库是否存在出错");
			e.printStackTrace();
			return null;
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 没有录入的时候
	 * 
	 * @param list
	 * @param conn
	 * @param strArray
	 * @return
	 */
	public String[] reckonData(Map<String, String> map, String tableName, Connection conn, String[] strArray,
			Map<String, String> gpsMap) {
		int a = strArray.length;
		String[] retArray = new String[5 + a];
		System.arraycopy(strArray, 0, retArray, 0, a);
		String[] gpsArray = null;
		// 求出上一条记录的里程
		double distance = getTopOne(tableName, conn);
		// 距离增加量
		double addDistance = 0;
		// 平均速度
		double avg_speed = 0;
		// 油箱油量
		double oil_mount = 0;
		// 电瓶电压
		double battery_ele = 0;
		// 模块电压
		double mod_ele = 0;

		/**
		 * +"battery_ele float  NULL," +"mod_ele float  NULL,"
		 */
		// 判断acc状态
		try {
			// 防拆报警
			if (Integer.valueOf(strArray[2]) == 1) {
				List<Map<String, String>> alarmList = queryAlarmList(conn, strArray[0], 4);
				insertAlarm(conn, alarmList, null);
			}
			if (Integer.valueOf(strArray[1]) == 1) {
				// gps是否为空
				if (strArray[a - 1] != null && !strArray[a - 1].equals("")) {
					// System.out.println(strArray[a-1]+"aaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbb");
					gpsArray = strArray[a - 1].split(";");
					if (gpsArray.length == 1) {
						String str = map.get(strArray[0]);
						System.out.println(str + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx第一次进来的数据");
						if (null == str) {
							// key 是imei, values 是gps
							gpsMap.put(strArray[0], gpsArray[0]);
						} else {
							addDistance = addDistance
									+ Math.abs(GpsUtils.getDistance(gpsMap.get(strArray[0]), gpsArray[0]));
							System.out.println(addDistance + "一个点的时候 讲改定put j~~=1");
							avg_speed = 0;
						}
					}
					// gps个数
					if (gpsArray.length != 1) {
						// 计算距离增加量
						for(int i=1;i<gpsArray.length;i++){
							 addDistance =
							 addDistance+Math.abs(GpsUtils.getDistance(gpsArray[i-1],gpsArray[i]));
							 System.out.println(addDistance+"获取没有加距离的第一个长都和");
						}
						// 计算平局数据km/h
						int length = gpsArray.length - 1;
						// 上传的时间间隔需要进一步确定
						avg_speed = (addDistance / (gpsArray.length - 1)) * 1.8;
						
						if (gpsMap.get(strArray[0]) != null) {
							// 上一条记录的gps点,与这一条记录的第一个点
							addDistance = addDistance
									+ Math.abs(GpsUtils.getDistance(gpsMap.get(strArray[0]), gpsArray[0]));
							// System.out.println(addDistance+"距离长都大于1的时候，计算两点之间的定位"+gpsMap.get(strArray[0])+"~~~"+gpsArray[0]);
						}
						gpsMap.put(strArray[0], gpsArray[gpsArray.length - 1]);
					}
					// 最大值不为空表示该去计算油量
					if (map.get(ConstantsUtil.FRAM_OILMAX) != null && map.get(ConstantsUtil.FRAM_OILMAX) != "") {
						Map<String, String> parMap = checkMap(map);

						// 最大值和最小值的余数
						int minus = Integer.valueOf(parMap.get(ConstantsUtil.FRAM_OILMAX))
								- Integer.valueOf(map.get(ConstantsUtil.FRAM_OILMIN));
						oil_mount = (Double.parseDouble(parMap.get(ConstantsUtil.FRAM_OILPOOL)) / minus);
						// 油量告警值
						int alarm_mount = Integer
								.valueOf((int) (minus * Double.parseDouble(parMap.get(ConstantsUtil.FRAM_OILALARM))
										+ Integer.valueOf(map.get(ConstantsUtil.FRAM_OILMIN))));
						// 告警油量值
						double oilAlarm = (Double.parseDouble(parMap.get(ConstantsUtil.FRAM_OILPOOL)) / alarm_mount);
						System.out.println(oilAlarm);
						// 计算当前油量( 油量电压-最低电压)
						oil_mount = oil_mount
								* (Integer.valueOf(strArray[3]) - Integer.valueOf(map.get(ConstantsUtil.FRAM_OILMIN)));
						System.out.println(oilAlarm+" 前面是告警值 xxxxxxxxxx 后面是实时值"+oil_mount);
						// 电池电压和模块电压占时未处理
						battery_ele = Double.parseDouble(strArray[4]);
						mod_ele = Double.parseDouble(strArray[5]);
						if (oil_mount < oilAlarm) {
							// 1,代表用量告警
							List<Map<String, String>> alarmList = queryAlarmList(conn, strArray[0], 1);
							insertAlarm(conn, alarmList, oil_mount);
						}
						if (battery_ele < Integer.valueOf(map.get(ConstantsUtil.FARM_BATTERYELEALARM))) {
							System.out.println(battery_ele+" 前面是電池值 xxxxxxxxxx 后面是实时值"+ Integer.valueOf(map.get(ConstantsUtil.FARM_BATTERYELEALARM)));
							List<Map<String, String>> alarmList = queryAlarmList(conn, strArray[0], 5);
							insertAlarm(conn, alarmList, battery_ele);
						}
					
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		distance = distance + addDistance;
		// System.out.println(addDistance+""+(gpsArray.length-1));

		retArray[a] = StringUtils.toDecimal(avg_speed);
		retArray[a + 1] = StringUtils.toDecimal(distance);
		retArray[a + 2] = StringUtils.toDecimal(oil_mount);
		retArray[a + 3] = StringUtils.toDecimal(battery_ele);
		retArray[a + 4] = StringUtils.toDecimal(mod_ele);
		for (String str : gpsMap.keySet()) {
			System.out.println(str + "++++++++++++++~" + gpsMap.get(str));
		}
		return retArray;
	}

	// 插入告警信息
	private void insertAlarm(Connection conn, List<Map<String, String>> alarmList, Object obj) {
		try {
			PreparedStatement pst = conn.prepareStatement(
					"insert into message_record(user_id,machine_id,message_type,value,read_status,create_time) values(?,?,?,?,?,?)");
			for (int i = 0; i < alarmList.size(); i++) {
				pst.setString(1, alarmList.get(i).get(ConstantsUtil.USER_ID));
				pst.setString(2, alarmList.get(i).get(ConstantsUtil.MACHINE_ID));
				pst.setString(3, alarmList.get(i).get(ConstantsUtil.ALARM_TYPE));
				pst.setObject(4, obj);
				pst.setString(5, "1");
				pst.setString(6, StringUtils.date());
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 检查要用的参数是否为空,如果为空就给一个默认值
	private Map<String, String> checkMap(Map<String, String> map) {
		// 最小值
		if (map.get(ConstantsUtil.FRAM_OILMIN) == null || map.get(ConstantsUtil.FRAM_OILMIN) == "") {
			map.put(ConstantsUtil.FRAM_OILMIN, 0 + "");
		}
		// 油箱大小
		if (map.get(ConstantsUtil.FRAM_OILPOOL) == null || map.get(ConstantsUtil.FRAM_OILPOOL) == "") {
			map.put(ConstantsUtil.FRAM_OILPOOL, 50 + "");
		}
		// 油箱告警系数
		if (map.get(ConstantsUtil.FRAM_OILALARM) == null || map.get(ConstantsUtil.FRAM_OILALARM) == "") {
			map.put(ConstantsUtil.FRAM_OILALARM, 0.25 + "");
		}
		// 电池电压
		if (map.get(ConstantsUtil.FARM_BATTERYELEALARM) == null || map.get(ConstantsUtil.FARM_BATTERYELEALARM) == "") {
			map.put(ConstantsUtil.FARM_BATTERYELEALARM, 0 + "");
		}
		// 模块电压
		if (map.get(ConstantsUtil.FRAM_MODELEALARM) == null || map.get(ConstantsUtil.FRAM_MODELEALARM) == "") {
			map.put(ConstantsUtil.FRAM_MODELEALARM, 0 + "");
		}
		return map;
	}

	// 求出上一条记录的里程
	private static Double getTopOne(String tableName, Connection conn) {
		double distance = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT TOP 1 * FROM [" + tableName + "] ORDER BY create_time DESC";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			// 判断是否查询出结果
			if (rs.next()) {
				// 获取上一条的记录 ,时间倒叙
				distance = Double.parseDouble(rs.getString(ConstantsUtil.IMEI_DISTANCE));
			}
		} catch (Exception e) {
			System.out.println("查询上一条记录距离出错");
			e.printStackTrace();

		}
		return distance;
	}

	/**
	 * 去创建表有就不用创建�?
	 * 
	 * @param conn
	 * @param ps
	 * @param dbName
	 */
	public static void crateTable(Connection conn, PreparedStatement ps, String dbName) {
		System.out.println(dbName);
		String sql = "if(NOT Exists(select * from sys.sysobjects where id=OBJECT_ID('[" + dbName + "]')))"
				+ " create table [" + dbName + "] (id  int identity(1,1) primary key ," + "	imei varchar(15) NOT NULL,"
				+ "acc int NOT NULL," + "alarm int  NOT NULL," + "v_oil_amount int NOT NULL,"
				+ "v_battery_ele int NOT NULL," + "v_mod_ele int NOT NULL," + "gps varchar(512) NULL,"
				+ "avg_speed float  NULL," + "distance float NOT NULL," + "oil_mount float  NULL,"
				+ "battery_ele float  NULL," + "mod_ele float  NULL," + "status int NOT NULL,"
				+ "create_time datetime NOT NULL);";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.out.println("执行创表语句失败");
			e.printStackTrace();
		}
	}

	/**
	 * 插入数据
	 * 
	 * @param conn
	 * @param str
	 * @return
	 */
	@SuppressWarnings("finally")
	public int insert(String dbName, Connection conn, String[] str) {
		int result = -1;
		PreparedStatement pstmt = null;
		try {
			String sql = "INSERT INTO [" + dbName + "](imei,acc,alarm,v_oil_amount,v_battery_ele,v_mod_ele,"
					+ "gps,avg_speed ,distance,oil_mount,battery_ele ,mod_ele,status,create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = (PreparedStatement) conn.prepareStatement(sql);

			pstmt.setString(1, str[0]);
			pstmt.setString(2, str[1]);
			pstmt.setString(3, str[2]);
			pstmt.setString(4, str[3]);
			pstmt.setString(5, str[4]);
			pstmt.setString(6, str[5]);
			pstmt.setString(7, str[6]);
			/**
			 * 换算结果
			 */
			pstmt.setString(8, str[7]);
			pstmt.setString(9, str[8]);
			pstmt.setString(10, str[9]);
			pstmt.setString(11, str[10]);
			pstmt.setString(12, str[11]);
			pstmt.setString(13, "1");
			pstmt.setString(14, StringUtils.date());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			// 负数代表错误�?
			result = -1;
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return result;
		}
	}

	public static int insertModuleCards(Connection conn, String imei,String msisdn) {
		PreparedStatement pstmt = null;
		int result = -1;
		ResultSet rs = null;
		try {
			 String date = StringUtils.date();
			String sql = "insert module_cards (imei,msisdn,create_time,status) values('"+imei+"','"+msisdn+"','"+date+"',1)";
			System.out.println(sql);
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	public static int checkImeiForModuleCards(Connection conn, String imei) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		try {
			String sql = "select imei as imei FROM module_cards WHERE imei='" + imei + "'";
			System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
//				result = rs.getInt(1);
				imei = rs.getString("imei");
				if(imei!=null&&!imei.equals("")){
					result = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	
}
