package kr.gooroom.gpms.metrics.service;

public class NetResourceVO extends ResourceMetricsVO {
    private Float recv;
    private Float sent;
    private String netInterface;
    private Float bytes_recv;
    private Float bytes_sent;

    public Float getRecv() {
        return recv;
    }

    public Float getSent() {
        return sent;
    }

    public Float getBytesRecv() {
        return bytes_recv;
    }

    public Float getBytesSent() {
        return bytes_sent;
    }

    public String getNetInterface() {
        return netInterface;
    }
}
