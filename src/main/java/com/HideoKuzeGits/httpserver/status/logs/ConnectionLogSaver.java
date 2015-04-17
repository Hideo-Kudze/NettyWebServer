package com.HideoKuzeGits.httpserver.status.logs;

import com.HideoKuzeGits.httpserver.Main;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionLogSaver extends Thread {

    public static BlockingQueue<ConnectionLog> blockingQueue = new LinkedBlockingDeque<ConnectionLog>(1000);
    public static String date;
    private static PrintWriter printWriter;
    private static ConnectionLogSaver connectionLogSaver;
    private AtomicBoolean newDay = new AtomicBoolean(false);

    private ConnectionLogSaver() {

        changeFile();
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {

                newDay.set(!getDate().equals(date));
            }
        }, 10 * 1000, 10 * 60 * 1000);
        start();
    }


    public static synchronized ConnectionLogSaver getInstance() {

        if (connectionLogSaver == null)
            connectionLogSaver = new ConnectionLogSaver();
        return connectionLogSaver;
    }


    public void save(ConnectionLog connectionLog) {

        try {
            blockingQueue.put(connectionLog);
        } catch (InterruptedException e) {
            e.printStackTrace();
            interrupt();
        }
    }

    @Override
    public void run() {

        while (!isInterrupted()) {

            ConnectionLog connectionLog = null;
            try {
                connectionLog = blockingQueue.poll(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupt();
            }

            String connectionLogString = connectionLog.toString();
            printWriter.println(connectionLogString);


            if (newDay.get())
                changeFile();

        }
    }

    private void changeFile() {

        try {
            newDay.set(false);
            date = getDate();
            String fileName = "access_log." + date + ".txt";
            File logFile = new File(Main.logDir, fileName);
            boolean fileCreated = logFile.createNewFile();

            if (printWriter != null)
                printWriter.close();


            FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
            printWriter = new PrintWriter(new BufferedOutputStream(fileOutputStream), true);
            if (fileCreated) {
                String header = "Time Stamp     Ip     URL     Received bytes     SentBytes     Download speed (b/s)     Upload speed (b/s)     Redirect url";
                printWriter.println(header);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    @Override
    protected void finalize() throws Throwable {
        if (printWriter != null) {
            printWriter.close();
            printWriter.print("]");
        }
    }
}
