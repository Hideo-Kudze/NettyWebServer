package com.HideoKuzeGits.httpserver.status.logs;

import com.HideoKuzeGits.httpserver.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConnectionLogLoader {

    public List<ConnectionLog> load() {

        List<ConnectionLog> connectionLogs = new ArrayList<ConnectionLog>();

        File[] files = Main.logDir.listFiles();

        for (File file : files) {

            Scanner in = null;
            try {
                in = new Scanner(file);
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

    private ConnectionLog parse(String line) {

        ConnectionLog connectionLog;
        String[] fields = line.split("     ");


        if (fields.length < 7)
            return null;

        connectionLog = new ConnectionLog();
        connectionLog.setTimeStamp(fields[0]);
        connectionLog.setIp(fields[1]);
        connectionLog.setUrl(fields[2]);
        connectionLog.setReceivedBytes(Integer.valueOf(fields[3]));
        connectionLog.setSentBytes(Integer.valueOf(fields[4]));
        connectionLog.setDownloadSpeed(Double.valueOf(fields[5]));
        connectionLog.setUploadSpeed(Double.valueOf(fields[6]));


        if (fields.length == 8)
            connectionLog.setRedirectUrl(fields[7]);

        return connectionLog;
    }
}
