package kr.gooroom.gpms.metrics.service;

public class NetResourceVO extends ResourceMetricsVO {
    private Float recv;
    private Float sent;
    private String netInterface;

    public Float getRecv() {
        return recv;
    }

    public Float getSent() {
        return sent;
    }

    public String getNetInterface() {
        return netInterface;
    }
}
