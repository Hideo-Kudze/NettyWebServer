package com.HideoKuzeGits.httpserver;

import com.HideoKuzeGits.httpserver.status.logs.ConnectionLogSaver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.net.URISyntaxException;

public class Main {

    private static final int port = 8080;
    public static File logDir;

    public static void main(String[] args) throws InterruptedException {

        createLogDir();

        /*List<ConnectionLog> load = new ConnectionLogLoader().load();
        System.out.println();*/

        ConnectionLogSaver.getInstance();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpTestServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private static void createLogDir() {
        try {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File dir = new File(path).getParentFile();
            logDir = new File(dir, "Logs");
            logDir.mkdirs();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
