package com.HideoKuzeGits.httpserver.status;

import com.HideoKuzeGits.httpserver.status.logs.ConnectionLog;

import java.util.*;

public class ServerStatus {

    private Integer totalRequestCount = 0;
    private Integer uniqueRequestCount = 0;
    private CountingMap<String> connectionsFromIpCount = new CountingMap<String>();
    private Map<String, Long> ipLastConnectionTmeMap = new HashMap<String, Long>() {

        @Override
        public Long put(String ip, Long timeNew) {

            Long timeOld = get(ip);

            if (timeOld == null || timeOld < timeNew)
                return super.put(ip, timeNew);
            else
                return null;

        }
    };


    private CountingMap<String> redirectToUrlCount =new CountingMap<String>();
    private Integer connectionsCurrentlyOpened = 0;
    private ConnectionLog[] connectionLogsTable = new ConnectionLog[16];

    public ServerStatus() {
    }

    public ServerStatus(List<ConnectionLog> connectionLogs) {


        totalRequestCount = connectionLogs.size();

        for (ConnectionLog connectionLog : connectionLogs) {

            String ip = connectionLog.getIp();
            connectionsFromIpCount.add(ip);
            ipLastConnectionTmeMap.put(ip, connectionLog.getTime());

            String redirectUrl = connectionLog.getRedirectUrl();
            if (redirectUrl != null)
                redirectToUrlCount.add(redirectUrl);
        }

        uniqueRequestCount = connectionsFromIpCount.size();
        Collections.sort(connectionLogs);

        int toIndex = connectionLogs.size() < 16 ? connectionLogs.size() : 16;
        for (int i = 0; i < toIndex; i++)
            connectionLogsTable[i] = connectionLogs.get(i);

    }

    public static String millisToDateString(Long millis){

        return new Date(millis).toString();
    }

    public Integer getTotalRequestCount() {
        return totalRequestCount;
    }

    public Integer getUniqueRequestCount() {
        return uniqueRequestCount;
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
