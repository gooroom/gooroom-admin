package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import kr.gooroom.gpms.ptgr.service.PortableImageViewVO;
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
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.noinsertimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.insertimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        return statusVO;
    }

    @Override
    public ResultVO readImageData() throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableImageViewVO> certVO = portableImageDAO.selectPortableImageList();
            if (certVO == null || certVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(certVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
        return resultVO;
    }

    @Override
    public ResultVO readImageDataById(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableImageVO> certVO = portableImageDAO.selectPortableImageList(options);
            if (certVO == null || certVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(certVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
        return resultVO;
    }

    @Override
    public ResultVO readImageDataByAdminId(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO updateImageData(PortableImageVO imageVO) throws Exception {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.updatePortableImage(imageVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.noupdateimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.updateimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }
        return statusVO;
    }

    @Override
    public StatusVO updateImageStatus(HashMap<String, Object> options) throws Exception {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.updatePortableImageStatus(options);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.noupdateimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.updateimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }
        return statusVO;
    }

    @Override
    public StatusVO removeImageDataByImageId(int id) throws Exception {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.removePortableImage(id);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeleteimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deleteimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        return statusVO;
    }

    @Override
    public StatusVO removeImageDataByImageIds(HashMap<String, Object> ids) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.removePortableImages(ids);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeleteimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deleteimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        return statusVO;
    }

    @Override
    public StatusVO removeAllImageData() throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.removeAllPortableImage();
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeleteimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deleteimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        return statusVO;
    }

    @Override
    public StatusVO deleteImageDataByImageId(int id) {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableImageDAO.deletePortableImage(id);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeleteimage"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deleteimage"));
            }
        }
        catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
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
