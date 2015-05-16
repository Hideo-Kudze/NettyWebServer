package com.HideoKuzeGits.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Main server class.
 */

public class Server {

    /**
     * Port that server will use.
     */
    private static final int port = 8080;


    /**
     * The folder in which logs will be written.
     */
    public static File logDir;


    /**
     * Main server channel.
     */
    private static Channel serverChanel;


    public static void main(String[] args) throws InterruptedException {

        //Stop server
        if (args.length > 0 && args[0].equals("stop")) {
            stopServerInstance();
            return;
        }

        createLogDir();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


            ServerBootstrap b = new ServerBootstrap();
            final HttpTestServerInitializer serverInitializer = new HttpTestServerInitializer();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(serverInitializer);
            serverChanel = b.bind(port).channel();

            //Stop server when SIGINT arrived.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    Server.stopCurrentServer();
                    serverInitializer.stop();
                }
            });

            serverChanel.closeFuture().sync();



        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }


    public static void stopCurrentServer() {
        serverChanel.close();
    }

    /**
     * Stops server instance running on this computer.
     */
    private static void stopServerInstance() {

        try {
            String urlString = "http://localhost:8080/stopServer";
            URL url = new URL(urlString);
            ((HttpURLConnection)url.openConnection()).getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates directory for logs if it is not exist.
     */
    private static void createLogDir() {
        try {
            String path = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File dir = new File(path).getParentFile();
            logDir = new File(dir, "Logs");
            logDir.mkdirs();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
