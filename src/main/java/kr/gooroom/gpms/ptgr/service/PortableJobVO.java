package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;

public class PortableJobVO implements Serializable {

    int jobId;
    int imageId;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
