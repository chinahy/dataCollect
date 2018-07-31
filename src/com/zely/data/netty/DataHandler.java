package com.zely.data.netty;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理接收到的消息
 * 
 * @author hengyang
 *
 */
public class DataHandler extends SimpleChannelInboundHandler<String> {
	public static Map<String,String> gpsMap = new HashMap<String,String>();
	private static Logger logger = Logger.getLogger(DataHandler.class); 
	//handler不能放耗时比较大的业务逻辑，否则会导致netty工作现成阻塞
	public static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("women +"+msg);
		// 回写数据
		ChannelFuture future = ctx.channel().writeAndFlush("ok\r\n");
		//future.addListener(ChannelFutureListener.CLOSE);
		cachedThreadPool.execute(new Runnable() {
			public void run() {
				if (null != msg) {
					// 排除换行的操作
					//#;868575021912904;460043801100000
					String[] splitmsg = msg.split(";");
					if(splitmsg.length==3){
						String imei = splitmsg[1];
						String msisdn = splitmsg[2];
						System.out.println(imei+"测试 "+msisdn);
						RunThread.judgeImeiAndInsertMC(imei, msisdn);
						System.out.println("在moudle_cards判断查询成功");
						return;
					}
					String str = msg.replaceAll("\r|\n", "");
					// 分割多条数据
					String[] strArray = str.trim().split("##;");
					for (int i = 1; i < strArray.length; i++) {
						// 去掉[] 括号
						String[] twoArray = strArray[i].split("\\[");
						String[] noGpsArray = twoArray[0].split(";");
						String gps = twoArray[1].replaceAll("]", "");
						String[] GpsArray = new String[noGpsArray.length + 1];
						//将GPS存入一个数据
						for (int j = 0; j < noGpsArray.length + 1; j++) {
							if (j < noGpsArray.length) {
								GpsArray[j] = noGpsArray[j];
							} else {
								GpsArray[j] = gps;
								//System.out.println(gps);
							}
						}
						logger.info(msg);
						//将结果传入到具体的某个线程中
						RunThread.dbConn(GpsArray,gpsMap);
						System.out.println("数据存入数据库");
					}
				}
			}
		});
	}

	/**
	 * 新客户端接入
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("------------------ 新建连接----------------------------------");
	}

	/**
	 * 客户端断开
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
	}

	/**
	 * 异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Channel incoming = ctx.channel();
		if (!incoming.isActive())
			System.out.println("SimpleClient:" + incoming.remoteAddress() + "异常");
		ctx.close();
		// cause.printStackTrace();
	}

	public static void main(String[] args) {
		Map<String,String > map = new HashMap<String,String>();
		//868575021164191  8
		//868575021162658;1;1;86;82;4074   7
		String msg="##;868575021164191;1;1;86;82;4074;[104.0659883,30.5503666;104.0666033,30.5503083;104.0666033,30.5503083;104.0665266,30.5504166;104.0665266,30.5504166;104.0664733,30.5500383;104.0664733,30.5500383;104.0664783,30.5500500;104.0664783,30.5500500;104.0664783,30.5500500]";
		if (null != msg) {
			// 排除换行的操作
			String str = msg.replaceAll("\r|\n", "");
			// 分割多条数据
			String[] strArray = str.trim().split("##;");
			for (int i = 1; i < strArray.length; i++) {
				// 去掉[] 括号
				String[] twoArray = strArray[i].split("\\[");
				String[] noGpsArray = twoArray[0].split(";");
				String gps = twoArray[1].replaceAll("]", "");
				String[] GpsArray = new String[noGpsArray.length + 1];
				//将GPS存入一个数据
				for (int j = 0; j < noGpsArray.length + 1; j++) {
					if (j < noGpsArray.length) {
						GpsArray[j] = noGpsArray[j];
					} else {
						GpsArray[j] = gps;
						System.out.println(gps);
					}
				}
				//将结果传入到具体的某个线程中
				RunThread.dbConn(GpsArray,map);
				System.out.println("数据存入数据库");
			}
		}
		
	}
	
}
