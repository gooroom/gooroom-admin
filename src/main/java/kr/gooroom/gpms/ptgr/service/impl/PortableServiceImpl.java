package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.gkm.utils.FileUtils;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("portableService")
public class PortableServiceImpl implements PortableService {

    private static final Logger logger = LoggerFactory.getLogger(PortableServiceImpl.class);

    @Resource(name = "portableDAO")
    private PortableDAO portableDAO;

    @Resource(name = "portableImageDAO")
    private PortableImageDAO portableImageDAO;

    @Resource(name = "portableCertDAO")
    private PortableCertDAO portableCertDAO;

    @Resource(name = "portableLogDAO")
    private PortableLogDAO logDAO;

    private void createLog (PortableVO portableVO, String code, String message) {
        PortableLogVO logVO = new PortableLogVO();
        logVO.setLogId(logDAO.selectNextPortableLogNumber());
        logVO.setPtgrId(portableVO.getPtgrId());
        logVO.setAdminId(portableVO.getAdminId());
        logVO.setUserId(portableVO.getUserId());
        logVO.setErrorStatus(code);
        logVO.setLogLevel(PortableConstants.LOG_WARN);
        logVO.setLogValue(MessageSourceHelper.getMessage(message));
        logDAO.createPortableLog(logVO);
        logger.debug("create log : \n {}", logVO);
    }

    private boolean deletePortableDataAndRelationData(List<PortableVO> ptgrList) {
        for (PortableVO vo : ptgrList) {
            long ret = portableDAO.deletePortableDataById(vo.getPtgrId());
            if (ret == 0) {
                createLog(vo, GPMSConstants.CODE_DELETE, "portable.result.nodelete");
                return false;
            }
            //Insert History
            ret = portableDAO.createPortableDataHist(vo);
            if (ret == 0) {
                createLog(vo, GPMSConstants.CODE_CREATE, "portable.result.noinserthist");
            }
            // ImageTable
            PortableImageVO imageVO = portableImageDAO.selectPortableImageByImageId(vo.getImageId());
            if (imageVO == null ) {
                createLog(vo, GPMSConstants.CODE_SELECT, "portable.result.noselectimage");
            }
            else {
                ret = portableImageDAO.createPortableImageHist(imageVO);
                if (ret == 0) {
                    createLog(vo, GPMSConstants.CODE_CREATE, "portable.result.noinsertimagehist");
                }
                ret = portableImageDAO.deletePortableImage(vo.getImageId());
                if (ret == 0) {
                    createLog(vo, GPMSConstants.CODE_DELETE, "portable.result.nodeleteimage");
                }
            }
            // CertTable
            PortableCertVO certVO =  portableCertDAO.selectPortableCert(Integer.toString(vo.getCertId()));
            if (certVO == null) {
                createLog(vo, GPMSConstants.CODE_SELECT, "portable.result.noselectcert");
            }
            else {
                ret = portableCertDAO.deletePortableCert(vo.getCertId());
                if (ret == 0) {
                    createLog(vo, GPMSConstants.CODE_DELETE, "portable.result.nodeletecert");
                }
                //File remove
                String certPath = certVO.getCertPath();
                if (certPath != null && !certPath.isEmpty()) {
                    Path path = Paths.get(certPath);
                    FileUtils.delete(new File(path.getParent().toString()));
                }
            }
        }
        return true;
    }

    @Override
    public StatusVO createPortableData(PortableVO portableVO) {

        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.createPortableData(portableVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.noinsert"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                        MessageSourceHelper.getMessage("portable.result.insert"));
            }
        }
        catch (Exception e) {
            logger.error("error in createPortableData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public ResultVO readPortableView() {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableViewVO> portableVO = portableDAO.selectPortableViewList();
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readPortableView: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public ResultPagingVO readPortableViewPaged(HashMap<String, Object> options) {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<PortableViewVO> portableVO = portableDAO.selectPortableViewList(options);
            long totalCount = portableDAO.selectPortableTotalCount(options);
            long filteredCount = portableDAO.selectPortableFilteredCount(options);

            if (portableVO == null || portableVO.size() == 0) {
                Object[] o = new Object[0];
                resultVO.setData(o);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setRecordsTotal(String.valueOf(totalCount));
                resultVO.setRecordsFiltered(String.valueOf(filteredCount));
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readPortableViewPaged: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public ResultPagingVO readPortableViewById(HashMap<String, Object> options) {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<PortableViewVO> portableVO = portableDAO.selectPortableViewList(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readPortableViewById: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableDataById(HashMap<String, Object> options) {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableVO> portableVO = portableDAO.selectPortableDataList(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readPortableDataById: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableDataByAdminIdAndApprove(HashMap<String, Object> options) {

        ResultVO resultVO = new ResultVO();

        try {
            List<PortableVO> portableVO = portableDAO.selectPortableDataListByAdminIdAndApprove(options);
            if (portableVO == null || portableVO.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            } else {
                resultVO.setData(portableVO.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            }
        }
        catch (Exception e) {
            logger.error("error in readPortableDataByAdminIdAndApprove: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public ResultVO readPortableArroveState(HashMap<String, Object> options) {

        ResultVO resultVO = new ResultVO();

        try {
            long cnt = portableDAO.selectPortableReapproveCount (options);
            resultVO.setData(new Object[] {cnt});
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                    MessageSourceHelper.getMessage("system.common.selectdata")));
        }
        catch (Exception e) {
            logger.error("error in readPortableDataByAdminIdAndApprove: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return resultVO;
    }

    @Override
    public StatusVO updatePortableData(PortableVO portableVO) {
        StatusVO statusVO = new StatusVO();

        try {
            long resultCnt = portableDAO.updatePortableData(portableVO);
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodelete"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.delete"));
            }
        }
        catch (Exception e) {
            logger.error("error in updatePortableData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    @Override
    public StatusVO updateAllPortableDataForApprove(String adminId) {
        return null;
    }

    @Override
    public StatusVO deletePortableData(HashMap<String, Object> ids) {
        StatusVO result = null;

        StatusVO statusVO = new StatusVO();

        try {
            List<PortableVO> ptgrList = portableDAO.selectPortableDataList(ids);
            if (ptgrList.size() == 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                        MessageSourceHelper.getMessage("system.common.noselectdata"));
                result = statusVO;
            } else {
                for (PortableVO vo : ptgrList) {
                    PortableCertVO certVO = portableCertDAO.selectPortableCert(Integer.toString(vo.getCertId()));
                    if (certVO == null) {
                        createLog(vo, GPMSConstants.CODE_SELECT, "portable.result.noselectcert");
                    } else {
                        //File remove
                        String certPath = certVO.getCertPath();
                        if (certPath != null && !certPath.isEmpty()) {
                            Path path = Paths.get(certPath);
                            FileUtils.delete(new File(path.getParent().toString()));
                        }
                    }
                    long ret = portableDAO.deletePortableDataById(vo.getPtgrId());
                    if (ret == 0) {
                        createLog(vo, GPMSConstants.CODE_DELETE, "portable.result.nodelete");
                    }
                }
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.delete"));
            }

        } catch (Exception e) {
            logger.error("error in deletePortableData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }
        if (result == null) {
            result = statusVO;
        }

        return result;
    }

    @Override
    public StatusVO deleteAllPortableData() {

        StatusVO statusVO = new StatusVO();

        try {
            //인증서 삭제
            FileUtils.delete(new File(GPMSConstants.PORTABLE_CERTPATH));
            long resultCnt = portableDAO.deleteAllPortableData();
            if (0 == resultCnt) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.nodelete"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                        MessageSourceHelper.getMessage("portable.result.delete"));
            }
        }
        catch (Exception e) {
            logger.error("error in deleteAllPortableData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        }

        return statusVO;
    }

    /**
     * check duplicate user id list
     *
     * @param ids list of user id
     * @return ResultVO result data bean
     */
    @Override
    public ResultVO isNoExistInUserIdList(String[] ids) {

        ResultVO resultVO = new ResultVO();

        try {
            List<String> idList = new ArrayList<>();

            for (String s : ids) {
                String id = portableDAO.selectPortableUserListForDuplicateUserId(s);
                if (!id.equals("") && id != null)
                    idList.add(id);
            }

            if (idList.size() == 0) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
                        MessageSourceHelper.getMessage("user.result.noduplicate")));
            } else {
                resultVO.setData(idList.toArray());
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
                        MessageSourceHelper.getMessage("user.result.duplicate")));
            }
        } catch (Exception ex) {
            logger.error("error in duplicate id : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (resultVO != null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
        }

        return resultVO;
    }

    @Override
    public int readNextPortableDataIndex() {
        return portableDAO.selectNextPortableNumber();
    }

    @Override
    public long readPortableDataCount() {
        return portableDAO.selectPortableTotalCount(null);
    }
}
