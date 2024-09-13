package kr.gooroom.gpms.metrics.service;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ResourceMetricsVO implements Serializable {
    private Date timeStamp;

    public Date getTimeStamp() {
        return timeStamp;
    }
}
