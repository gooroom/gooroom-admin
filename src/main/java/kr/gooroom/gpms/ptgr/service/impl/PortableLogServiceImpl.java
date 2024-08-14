package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableLogService;
import kr.gooroom.gpms.ptgr.service.PortableLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("portableLogService")
public class PortableLogServiceImpl implements PortableLogService {

    private static final Logger logger = LoggerFactory.getLogger(PortableLogServiceImpl.class);

    @Resource(name = "portableLogDAO")
    private PortableLogDAO portableLogDAO;

    @Override
    public StatusVO createLogData(PortableLogVO portableLogVO) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableLogDAO.createPortableLog(portableLogVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.noinsertlog"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.insertlog"));
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
    public ResultVO readLogData() throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableLogVO> certVO = portableLogDAO.selectPortableLogList();
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
            logger.error("error in readLogData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return resultVO;
    }

    @Override
    public ResultVO readLogDataByOptions(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableLogVO> certVO = portableLogDAO.selectPortableLogList(options);
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
            logger.error("error in readLogDataByOptions: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return resultVO;
    }

    @Override
    public StatusVO deleteAllLogData() throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableLogDAO.deleteAllPortableLog();
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeletelog"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deletelog"));
            }
        }
        catch (Exception e) {
            logger.error("error in deleteAllLogData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        return statusVO;
    }

    @Override
    public int readNextLogDataIndex() throws Exception {
        return portableLogDAO.selectNextPortableLogNumber();
    }

    @Override
    public int readLogDataCount() throws Exception {
        return portableLogDAO.selectPortableLogCount();
    }
}