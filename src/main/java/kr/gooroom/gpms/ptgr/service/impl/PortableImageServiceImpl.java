package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("portableImageService")
public class PortableImageServiceImpl implements PortableImageService {

    private static final Logger logger = LoggerFactory.getLogger(PortableImageServiceImpl.class);

    @Resource(name = "portableImageDAO")
    private PortableImageDAO portableImageDAO;

    @Override
    public StatusVO createImageData(PortableImageVO portableImageVO) throws Exception {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.createPortableImage(portableImageVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT, "");
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT, "");
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT, "");
        }

        return statusVO;
    }

    @Override
    public ResultVO readImageData() throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableImageVO> certVO = portableImageDAO.selectPortableImageList();
            if (certVO == null || certVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(certVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }
        return resultVO;
    }

    @Override
    public ResultVO readImageDataById(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableImageVO> certVO = portableImageDAO.selectPortableImageList(options);
            if (certVO == null || certVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(certVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }
        return resultVO;
    }

    @Override
    public ResultVO readImageDataByAdminId(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deleteImageDataByImageId(String imageId) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.deletePortableImage(imageId);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE, "");
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE, "");
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE, "");
        }

        return statusVO;
    }

    @Override
    public StatusVO deleteAllImageData() throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.deleteAllPortableImage();
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE, "");
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE, "");
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE, "");
        }

        return statusVO;
    }

    @Override
    public int readNextImageDataIndex() throws Exception {
        return portableImageDAO.selectNextPortableImageNumber();
    }

    @Override
    public int readImageDataCount() throws Exception {
        return portableImageDAO.selectPortableImageCount();
    }
}
