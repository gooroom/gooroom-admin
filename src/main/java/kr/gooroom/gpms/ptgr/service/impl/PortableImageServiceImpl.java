package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import kr.gooroom.gpms.ptgr.service.PortableImageViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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
            logger.error("error in createImageData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public ResultPagingVO readImageData(HashMap<String, Object> options) throws Exception {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<PortableImageViewVO> imageVO = portableImageDAO.selectPortableImageList(options);

            if (imageVO == null || imageVO.size() == 0) {
                Object[] o = new Object[0];
                resultVO.setData(o);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                long totalCount = portableImageDAO.selectPortableImageTotalCount(options);
                long filteredCount = portableImageDAO.selectPortableImageFilteredCount(options);
                resultVO.setData(imageVO.toArray());
                resultVO.setRecordsTotal(String.valueOf(totalCount));
                resultVO.setRecordsFiltered(String.valueOf(filteredCount));
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readImageData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return resultVO;
    }

    @Override
    public ResultVO readImageDataById(int imageId) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            PortableImageVO  imageVO = portableImageDAO.selectPortableImageByImageId(imageId);
            if (imageVO == null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(new Object[] {imageVO});
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readImageDataById: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
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
            logger.error("error in updateImageData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
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
            logger.error("error in updateImageStatus: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
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
            logger.error("error in removeImageDataByImageId: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
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
            logger.error("error in removeImageDataByImageIds: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
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
            logger.error("error in removeAllImageData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public StatusVO deleteImageDataByImageId(int id) throws Exception {
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
            logger.error("error in deleteImageDataByImageId: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public int readNextImageDataIndex() throws Exception {
        return portableImageDAO.selectNextPortableImageNumber();
    }

    @Override
    public long readImageDataCount() throws Exception {
        return portableImageDAO.selectPortableImageTotalCount(null);
    }
}
