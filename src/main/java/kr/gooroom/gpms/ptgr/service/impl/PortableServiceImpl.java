package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.service.PortableService;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import kr.gooroom.gpms.ptgr.service.PortableViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("portableService")
public class PortableServiceImpl implements PortableService {

    private static final Logger logger = LoggerFactory.getLogger(PortableServiceImpl.class);

    @Resource(name = "portableDAO")
    private PortableDAO portableDAO;

    @Override
    public ResultVO checkId(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<String> portableVO = portableDAO.checkCertID(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }

        return resultVO;
    }

    @Override
    public StatusVO createPortableData(PortableVO portableVO) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.createPortableData(portableVO);
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
    public ResultVO readPortableView() throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableViewVO> portableVO = portableDAO.selectPortableViewList();
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableViewById(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableViewVO> portableVO = portableDAO.selectPortableViewList(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableDataById(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableVO> portableVO = portableDAO.selectPortableDataList(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableDataByAdminIdAndApprove(HashMap<String, Object> options) throws Exception {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableVO> portableVO = portableDAO.selectPortableDataListByAdminIdAndApprove(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT, ""));
            }
        }
        catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT, ""));
        }

        return resultVO;
    }

    @Override
    public StatusVO updatePortableData(PortableVO portableVO) throws Exception {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.updatePortableData(portableVO);
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
    public StatusVO updateAllPortableDataForApprove(String adminId) throws Exception {
        return null;
    }

    @Override
    public StatusVO deletePortableData(HashMap<String, Object> ids) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.deletePortableData(ids);
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
    public StatusVO deleteAllPortableData() throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.deleteAllPortableData();
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
    public int readNextPortableDataIndex() throws Exception {
        return portableDAO.selectNextPortableNumber();
    }

    @Override
    public int readPortableDataCount() throws Exception {
        return portableDAO.selectPortableCount();
    }
}
