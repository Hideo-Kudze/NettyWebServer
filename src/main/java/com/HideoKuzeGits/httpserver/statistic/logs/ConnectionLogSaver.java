package com.HideoKuzeGits.httpserver.statistic.logs;

import com.HideoKuzeGits.httpserver.Server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;


/**
 * Asynchronously saves ConnectionLogs. <br/>
 * Log file name format - access_log.yyyy-MM-dd.txt
 */

//Single instance per server.
public class ConnectionLogSaver extends Thread {

    /**
     * Connection logs to save.
     */
    private BlockingQueue<ConnectionLog> connectionLogsQueue = new LinkedBlockingDeque<ConnectionLog>(10_000);

    /**
     * Creation date of the log file.
     */
    public static String date;

    /**
     * Writer for logfile.
     */
    private static PrintWriter printWriter;

    /**
     * Add to a top of every new log file and describe record format.
     */
    private static final String LOG_FILE_HEADER = "Time Stamp     Ip     URL     Received bytes     SentBytes     Speed (b/s)     Redirect url";


    public ConnectionLogSaver() {
        changeFile();
        start();
    }


    /**
     * Add connection log to queue for save it in future.
     */
    public void save(ConnectionLog connectionLog) {

        try {
            connectionLogsQueue.put(connectionLog);
        } catch (InterruptedException e) {
            e.printStackTrace();
            interrupt();
        }
    }


    /**
     * Save connection logs to file in loop.<br/>
     * Merge server statistic with newly arrived conception log.
     */
    @Override
    public void run() {

        while (!isInterrupted()) {

            try {
                ConnectionLog  connectionLog = connectionLogsQueue.poll(10, TimeUnit.MINUTES);
                String connectionLogString = connectionLog.toString();
                printWriter.println(connectionLogString);

                //If a new day has come change log file.
                if (!getDate().equals(date))
                    changeFile();

            } catch (InterruptedException e) {
                e.printStackTrace();
                interrupt();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        printWriter.close();
    }

    /**
     * Create new log file and change printWriter.
     */
    private void changeFile() {

        try {
            date = getDate();
            String fileName = "access_log." + date + ".txt";
            File logFile = new File(Server.logDir, fileName);
            boolean fileCreated = logFile.createNewFile();

            if (printWriter != null)
                printWriter.close();

            FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
            printWriter = new PrintWriter(outputStreamWriter, true);

            if (fileCreated) {
                printWriter.println(LOG_FILE_HEADER);
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
        if (printWriter != null)
            printWriter.close();
        super.finalize();
    }

}
