package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;
import java.util.List;

public class PortableListVO implements Serializable {

    List<PortableVO> portableVOList;

    public List<PortableVO> getPortableVOList() {
        return portableVOList;
    }

    public void setPortableVOList(List<PortableVO> portableVOList) {
        this.portableVOList = portableVOList;
    }
}
