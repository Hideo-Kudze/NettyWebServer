package com.HideoKuzeGits.httpserver.statistic;

import com.HideoKuzeGits.httpserver.statistic.logs.ConnectionLog;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Gather and process server connection logs.
 */

//Single instance per server.
public class ServerStatistic {

    private Integer totalRequestCount = 0;
    private CountingMap<String> connectionsFromIpCount = new CountingMap<String>();

    /**
     * Map connections to its start time.
     */
    private Map<String, Long> ipLastConnectionTmeMap = new HashMap<String, Long>() {

        /**
         * If connections has occurred in the map it will be saved only if it was later.
         *
         * @param ip address from which the connection was initiated,
         * @param timeNew start time of connection.
         * @return time of latest connection,
         */
        @Override
        public Long put(String ip, Long timeNew) {

            Long timeOld = get(ip);

            if (timeOld == null || timeOld < timeNew)
                return super.put(ip, timeNew);
            else
                return timeOld;
        }
    };


    private CountingMap<String> redirectToUrlCount = new CountingMap<String>();
    private Integer connectionsCurrentlyOpened = 0;

    /**
     * Logs of the last 16 connections to the server.
     */
    private ConnectionLog[] connectionLogsTable = new ConnectionLog[16];


    /**
     * Buffer for connection logs that will be merged with statistic.
     */
    private BlockingQueue<ConnectionLog> connectionLogsQueue = new LinkedBlockingDeque<ConnectionLog>(10_000);



    public ServerStatistic(SortedSet<ConnectionLog> connectionLogs) {

        this();

        totalRequestCount = connectionLogs.size();

        for (ConnectionLog connectionLog : connectionLogs) {
            updateIpAndRedirectTableData(connectionLog);
        }

        int toIndex = connectionLogs.size() < 16 ? connectionLogs.size() : 16;
        Iterator<ConnectionLog> connectionLogIterator = connectionLogs.iterator();
        for (int i = 0; i < toIndex; i++)
            connectionLogsTable[i] = connectionLogIterator.next();
    }

    public ServerStatistic() {

        /*
          Many threads can fill the buffer but only one update statistic.
          It makes ServerStatistic thread safe and fast at the same time.
         */
        Thread thread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {
                        update(connectionLogsQueue.poll(10, TimeUnit.MINUTES));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.setDaemon(true);

        thread.start();
    }

    /**
     *
     * Add connection log to queue to updating server statistic in future.
     *
     * @param connectionLog
     */
    public void addData(ConnectionLog connectionLog){
        try {
            connectionLogsQueue.put(connectionLog);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add connection log to server statistic.
     */
    private void update(ConnectionLog connectionLog) {

        totalRequestCount++;
        connectionLogsTable[15] = connectionLog;
        Arrays.sort(connectionLogsTable, new Comparator<ConnectionLog>() {
            @Override
            public int compare(ConnectionLog c1, ConnectionLog c2) {

                //Move nulls to the end of array
                if (c1 == c2) return 0;
                if (c1 == null) return 1;
                if (c2 == null) return -1;

                return c1.compareTo(c2);

            }
        });
        updateIpAndRedirectTableData(connectionLog);
    }

    private void updateIpAndRedirectTableData(ConnectionLog connectionLog) {
        String ip = connectionLog.getIp();
        connectionsFromIpCount.add(ip);
        ipLastConnectionTmeMap.put(ip, connectionLog.getTime());

        String redirectUrl = connectionLog.getRedirectUrl();
        if (redirectUrl != null)
            redirectToUrlCount.add(redirectUrl);
    }

    public static String millisToDateString(Long millis) {
        return new Date(millis).toString();
    }

    public Integer getTotalRequestCount() {
        return totalRequestCount;
    }

    public Integer getUniqueRequestCount() {
        return connectionsFromIpCount.size();
    }

    public CountingMap<String> getConnectionsFromIpCount() {
        return connectionsFromIpCount;
    }

    public Map<String, Long> getIpLastConnectionTmeMap() {
        return ipLastConnectionTmeMap;
    }

    public CountingMap<String> getRedirectToUrlCount() {
        return redirectToUrlCount;
    }

    public Integer getConnectionsCurrentlyOpened() {
        return connectionsCurrentlyOpened;
    }

    public ConnectionLog[] getConnectionLogsTable() {
        return connectionLogsTable;
    }

    public void setConnectionsCurrentlyOpened(Integer conceptionsCurrentlyOpened) {
        this.connectionsCurrentlyOpened = conceptionsCurrentlyOpened;
    }

}
