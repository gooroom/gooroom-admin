package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortableImageServiceImpl implements PortableImageService {

    private static final Logger logger = LoggerFactory.getLogger(PortableImageServiceImpl.class);

    @Override
    public StatusVO createImageData(PortableImageVO portableImageVO) throws Exception {
        return null;
    }

    @Override
    public ResultVO readImageData() throws Exception {
        return null;
    }

    @Override
    public ResultVO readImageDataByUserId(String userId) throws Exception {
        return null;
    }

    @Override
    public ResultVO readImageDataByAdminId(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deleteImageDataByImageId(String imageId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deleteAllImageData() throws Exception {
        return null;
    }
}
