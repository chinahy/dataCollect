package com.zely.data.netty;
import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
/**
 * netty5
 * @author hengyang
 *
 */
public class Server {
	public static void main(String[] args) {
		
		//服务类
		ServerBootstrap bootstrap = new ServerBootstrap();
		//用来处理i/0多线程的循环器
		//boss用来接收连接
		EventLoopGroup boss = new NioEventLoopGroup();
		//用来处理连接
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			//设置线程池
			bootstrap.group(boss, worker);
			//设置socket工厂、
			bootstrap.channel(NioServerSocketChannel.class);
			//设置管道工厂
			bootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new StringEncoder());
					ch.pipeline().addLast(new DataHandler());
				}
			});
			
			//设置参数，TCP参数
			bootstrap.option(ChannelOption.SO_BACKLOG, 2048);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//socketchannel的设置,维持链接的活跃，清除死链接
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);//socketchannel的设置,关闭延迟发送
			//绑定端口
			ChannelFuture future = bootstrap.bind(6000);
			System.out.println("start");
			//等待服务端关闭
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//释放资源
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
}
