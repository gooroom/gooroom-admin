package kr.gooroom.gpms.ptgr.service;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PortableImageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1088845010832332565L;
    int imageId;
    long size;
    String name;
    String url;
    int status; //신청/생성/경로전송/완료/삭제
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date createdDt;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }
}
