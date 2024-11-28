package kr.gooroom.gpms.ptgr.service.impl;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableJobService;
import kr.gooroom.gpms.ptgr.service.PortableJobVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("portableJobService")
public class PortableJobServiceImpl implements PortableJobService {

    private static final Logger logger = LoggerFactory.getLogger(PortableJobServiceImpl.class);

    @Resource(name = "portableJobDAO")
    private PortableJobDAO portableJobDAO;

    @Override
    public StatusVO createJobData(PortableJobVO portableJobVO) {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableJobDAO.createPortableJob(portableJobVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.noinsertjob"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.insertjob"));
            }
        }
        catch (Exception e) {
            logger.error("error in createLogData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public StatusVO deleteJobDataByImageId(int imageId) {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableJobDAO.deletePortableJobByImageId(imageId);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeletejob"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deletejob"));
            }
        }
        catch (Exception e) {
            logger.error("error in deleteAllJobData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return statusVO;
    }

    @Override
    public StatusVO deleteAllJobData() {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableJobDAO.deleteAllPortableJob();
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeletejob"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deletejob"));
            }
        }
        catch (Exception e) {
            logger.error("error in deleteAllJobData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return statusVO;
    }

    @Override
    public ResultVO readJobDataByImageId(int imageId) {

        ResultVO resultVO = new ResultVO();

        try {
            PortableJobVO jobVO= portableJobDAO.selectPortableJobByImageId(imageId);
            if (jobVO == null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(new Object[] {jobVO});
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
    public StatusVO updateJobData(PortableJobVO portableJobVO) {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableJobDAO.updatePortableJob(portableJobVO);
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
}