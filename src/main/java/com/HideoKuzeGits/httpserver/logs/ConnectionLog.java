package com.HideoKuzeGits.httpserver.logs;

public class ConnectionLog {

    private String ip;
    private String url;
    private Long time;
    private Integer sentBytes;
    private Integer receivedBytes;
    //bytes per second
    private Float downloadSpeed;
    private Float uploadSpeed;
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

    public Float getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(Float downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public Float getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(Float uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
