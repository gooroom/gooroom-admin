package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.service.PortableCertService;
import kr.gooroom.gpms.ptgr.service.PortableCertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("portableCertService")
public class PortableCertServiceImpl implements PortableCertService {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertServiceImpl.class);

    @Resource(name = "portableCertDAO")
    private PortableCertDAO portableCertDAO;

    @Override
    public StatusVO createCertData(PortableCertVO portableCertVO) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableCertDAO.createPortableCert(portableCertVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.noinsertcert"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.insertcert"));
            }
        }
        catch (Exception e) {
            logger.error("error in createCertData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public ResultVO readCertDataByCertId(String certId) throws Exception {

        ResultVO resultVO = new ResultVO();
        try {
            PortableCertVO certVO = portableCertDAO.selectPortableCert(certId);
            if (certVO == null ) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(new Object[] {certVO});
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readCertDataByCertId: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public StatusVO updateCertData(PortableCertVO portableCertVO) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableCertDAO.updatePortableCert(portableCertVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.noupdatecert"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.updatecert"));
            }
        }
        catch (Exception e) {
            logger.error("error in updateCertData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public StatusVO deleteCertDataByCertId(int certId) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableCertDAO.deletePortableCert(certId);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodeletecert"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.deletecert"));
            }
        }
        catch (Exception e) {
            logger.error("error in deleteCertDataByCertId: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public int readNextCertDataIndex() throws Exception {
        return portableCertDAO.selectNextPortableCertNumber();
    }

    @Override
    public int readCertDataCount() throws Exception {
        return portableCertDAO.selectPortableCertCount();
    }
}
