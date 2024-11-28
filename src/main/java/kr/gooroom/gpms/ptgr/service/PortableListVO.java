package kr.gooroom.gpms.ptgr.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PortableListVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5912047508661988542L;
    List<PortableVO> portableListVO;

    public List<PortableVO> getPortableListVO() {
        return portableListVO;
    }

    public void setPortableListVO(List<PortableVO> portableListVO) {
        this.portableListVO = portableListVO;
    }
}
