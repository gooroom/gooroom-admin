package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.service.PortableCertService;
import kr.gooroom.gpms.ptgr.service.PortableCertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortableCertServiceImpl implements PortableCertService {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertServiceImpl.class);

    @Override
    public StatusVO createCertData(PortableCertVO portableCertVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO readCertDataByCertId(String certId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deleteCertDataByCertId(String certId) throws Exception {
        return null;
    }
}
