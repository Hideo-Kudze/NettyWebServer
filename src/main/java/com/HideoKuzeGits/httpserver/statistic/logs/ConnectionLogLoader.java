package com.HideoKuzeGits.httpserver.statistic.logs;

import com.HideoKuzeGits.httpserver.Server;

import java.io.File;
import java.util.*;

/**
 * Load connection logs.
 */

//Single instance per server.
public class ConnectionLogLoader {


    /**
     *  Load connection logs from files in log directory.
     * @return connections logs from log directory.
     */
    public SortedSet<ConnectionLog> load() {

        TreeSet<ConnectionLog> connectionLogs = new TreeSet<ConnectionLog>();

        File[] files = Server.logDir.listFiles();

        for (File file : files) {

            Scanner in = null;
            try {
                in = new Scanner(file, "UTF-8");

                //Skip header line.
                in.nextLine();

                while (in.hasNext()) {
                    String line = in.nextLine();

                    try {
                        ConnectionLog connectionLog = parse(line);
                        if (connectionLog != null)
                            connectionLogs.add(connectionLog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    in.close();
            }
        }

        return connectionLogs;
    }

    /**
     *
     * Parse connection log from string.
     *
     * @param line string representation of connection log.
     */
    private ConnectionLog parse(String line) {

        ConnectionLog connectionLog;
        String[] fields = line.split("     ");


        if (fields.length < 6)
            return null;

        connectionLog = new ConnectionLog();
        connectionLog.setTimeStamp(fields[0]);
        connectionLog.setIp(fields[1]);
        connectionLog.setUrl(fields[2]);
        connectionLog.setReceivedBytes(Integer.valueOf(fields[3]));
        connectionLog.setSentBytes(Integer.valueOf(fields[4]));
        connectionLog.setSpeed(Integer.valueOf(fields[5]));


        if (fields.length == 7)
            connectionLog.setRedirectUrl(fields[6]);

        return connectionLog;
    }
}
