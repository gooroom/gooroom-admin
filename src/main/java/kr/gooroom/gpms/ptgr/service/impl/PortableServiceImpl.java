package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.service.PortableService;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortableServiceImpl implements PortableService {

    private static final Logger logger = LoggerFactory.getLogger(PortableServiceImpl.class);

    @Override
    public StatusVO createPortableData(PortableVO portableVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO readPortableData() throws Exception {
        return null;
    }

    @Override
    public ResultVO readPortableDataById(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO updatePortableDataForApprove(String ptgrId) throws Exception {
        return null;
    }

    @Override
    public StatusVO updateAllPortableDataForApprove(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deletePortableData(String ptgrId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deleteAllPortableData() throws Exception {
        return null;
    }
}
