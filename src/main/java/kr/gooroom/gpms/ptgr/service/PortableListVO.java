package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PortableListVO implements Serializable {

    List<PortableVO> portableListVO;

    public List<PortableVO> getPortableListVO() {
        return portableListVO;
    }

    public void setPortableListVO(List<PortableVO> portableListVO) {
        this.portableListVO = portableListVO;
    }
}
