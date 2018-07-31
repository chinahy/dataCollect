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
 * ������յ�����Ϣ
 * 
 * @author hengyang
 *
 */
public class DataHandler extends SimpleChannelInboundHandler<String> {
	public static Map<String,String> gpsMap = new HashMap<String,String>();
	private static Logger logger = Logger.getLogger(DataHandler.class); 
	//handler���ܷź�ʱ�Ƚϴ��ҵ���߼�������ᵼ��netty�����ֳ�����
	public static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("women +"+msg);
		// ��д����
		ChannelFuture future = ctx.channel().writeAndFlush("ok\r\n");
		//future.addListener(ChannelFutureListener.CLOSE);
		cachedThreadPool.execute(new Runnable() {
			public void run() {
				if (null != msg) {
					// �ų����еĲ���
					//#;868575021912904;460043801100000
					String[] splitmsg = msg.split(";");
					if(splitmsg.length==3){
						String imei = splitmsg[1];
						String msisdn = splitmsg[2];
						System.out.println(imei+"���� "+msisdn);
						RunThread.judgeImeiAndInsertMC(imei, msisdn);
						System.out.println("��moudle_cards�жϲ�ѯ�ɹ�");
						return;
					}
					String str = msg.replaceAll("\r|\n", "");
					// �ָ��������
					String[] strArray = str.trim().split("##;");
					for (int i = 1; i < strArray.length; i++) {
						// ȥ��[] ����
						String[] twoArray = strArray[i].split("\\[");
						String[] noGpsArray = twoArray[0].split(";");
						String gps = twoArray[1].replaceAll("]", "");
						String[] GpsArray = new String[noGpsArray.length + 1];
						//��GPS����һ������
						for (int j = 0; j < noGpsArray.length + 1; j++) {
							if (j < noGpsArray.length) {
								GpsArray[j] = noGpsArray[j];
							} else {
								GpsArray[j] = gps;
								//System.out.println(gps);
							}
						}
						logger.info(msg);
						//��������뵽�����ĳ���߳���
						RunThread.dbConn(GpsArray,gpsMap);
						System.out.println("���ݴ������ݿ�");
					}
				}
			}
		});
	}

	/**
	 * �¿ͻ��˽���
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("------------------ �½�����----------------------------------");
	}

	/**
	 * �ͻ��˶Ͽ�
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
	}

	/**
	 * �쳣
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Channel incoming = ctx.channel();
		if (!incoming.isActive())
			System.out.println("SimpleClient:" + incoming.remoteAddress() + "�쳣");
		ctx.close();
		// cause.printStackTrace();
	}

	public static void main(String[] args) {
		Map<String,String > map = new HashMap<String,String>();
		//868575021164191  8
		//868575021162658;1;1;86;82;4074   7
		String msg="##;868575021164191;1;1;86;82;4074;[104.0659883,30.5503666;104.0666033,30.5503083;104.0666033,30.5503083;104.0665266,30.5504166;104.0665266,30.5504166;104.0664733,30.5500383;104.0664733,30.5500383;104.0664783,30.5500500;104.0664783,30.5500500;104.0664783,30.5500500]";
		if (null != msg) {
			// �ų����еĲ���
			String str = msg.replaceAll("\r|\n", "");
			// �ָ��������
			String[] strArray = str.trim().split("##;");
			for (int i = 1; i < strArray.length; i++) {
				// ȥ��[] ����
				String[] twoArray = strArray[i].split("\\[");
				String[] noGpsArray = twoArray[0].split(";");
				String gps = twoArray[1].replaceAll("]", "");
				String[] GpsArray = new String[noGpsArray.length + 1];
				//��GPS����һ������
				for (int j = 0; j < noGpsArray.length + 1; j++) {
					if (j < noGpsArray.length) {
						GpsArray[j] = noGpsArray[j];
					} else {
						GpsArray[j] = gps;
						System.out.println(gps);
					}
				}
				//��������뵽�����ĳ���߳���
				RunThread.dbConn(GpsArray,map);
				System.out.println("���ݴ������ݿ�");
			}
		}
		
	}
	
}
