package com.HideoKuzeGits.httpserver.status.logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConnectionLog implements Comparable<ConnectionLog> {

    private String ip;
    private String url;
    private Long time;
    private Integer receivedBytes;
    private Integer sentBytes;

    //bytes per second
    private Double downloadSpeed;
    private Double uploadSpeed;
    private String redirectUrl;


    public ConnectionLog() {

    }

    public ConnectionLog(String ip, String url, Long time, Integer sentBytes, Integer receivedBytes) {
        this.ip = ip;
        this.url = url;
        this.time = time;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeStamp() {
        return new Date(time).toString();
    }

    public void setTimeStamp(String timeStamp) {
        try {
            this.time = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(timeStamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(Integer sentBytes) {
        this.sentBytes = sentBytes;
    }

    public Integer getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(Integer receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public Double getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(Double downloadSpeed) {

        this.downloadSpeed = (double) Math.round(downloadSpeed * 100) / 100;
    }

    public Double getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(Double uploadSpeed) {
        this.uploadSpeed = (double) Math.round(uploadSpeed * 100) / 100;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {
        String connectionLogString = getTimeStamp() + "     " + ip + "     " + url + "     " + receivedBytes + "     " +
                sentBytes + "     " + downloadSpeed + "     " + uploadSpeed;

        if (redirectUrl != null)
            connectionLogString += "     " + redirectUrl;

        return connectionLogString;
    }

    @Override
    public int compareTo(ConnectionLog c) {

        if (time > c.time)
            return -1;
        else if (time == c.time)
            return 0;
        else
            return -1;
    }
}
