package kr.gooroom.gpms.notice.service.impl;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.impl.ClientGroupDAO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.dept.service.DeptVO;
import kr.gooroom.gpms.dept.service.impl.DeptDAO;
import kr.gooroom.gpms.notice.service.NoticePublishTargetService;
import kr.gooroom.gpms.notice.service.NoticePublishTargetVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("noticePublishTargetService")
public class NoticePublishTargetServiceImpl implements NoticePublishTargetService {

    private static final Logger logger = LoggerFactory.getLogger(NoticePublishTargetServiceImpl.class);
    
    @Resource(name = "noticePublishTargetDAO")
    private NoticePublishTargetDAO noticePublishTargetDAO;
    
    @Resource(name = "clientGroupDAO")
    private ClientGroupDAO clientGroupDao;
    
    @Resource(name = "deptDAO")
    private DeptDAO deptDao;

    @Override
    public StatusVO createNoticePublishTarget(String noticePublishId, List<String> grpInfos, List<String> clientIds, List<String> deptInfos, List<String> userIds) {
        
        List<NoticePublishTargetVO> noticePublishTargetVOs = new ArrayList<>();
       
        for (String grpInfo : grpInfos) {
            String[] tmpArr = grpInfo.split(":");

            noticePublishTargetVOs.add(getNoticePublishTargetVO(noticePublishId, "3", tmpArr[0], tmpArr[1], "1"));

            if (tmpArr[1].equals("1")) {
                List<ClientGroupVO> clientGroupVOs = clientGroupDao.selectAllChildrenGroupList(tmpArr[0]);
                if (clientGroupVOs != null && clientGroupVOs.size() > 0) {
                    List<NoticePublishTargetVO> noticePublishTargetVOsForGrp = clientGroupVOs.stream()
                            .filter(clientGroupVO -> !clientGroupVO.getGrpId().equals(tmpArr[0]))
                            .map(clientGroupVO -> getNoticePublishTargetVO(noticePublishId, "3", clientGroupVO.getGrpId(), null, "0")).toList();
                    noticePublishTargetVOs.addAll(noticePublishTargetVOsForGrp);
                }

            }
        }
        
        for (String clientId : clientIds) {
            noticePublishTargetVOs.add(getNoticePublishTargetVO(noticePublishId, "2", clientId, null, "1"));
        }
        
        for (String deptInfo : deptInfos) {
            String[] tmpArr = deptInfo.split(":");
            
            noticePublishTargetVOs.add(getNoticePublishTargetVO(noticePublishId, "1", tmpArr[0], tmpArr[1], "1"));
            
            List<DeptVO> deptVOs = deptDao.selectAllChildrenDeptList(tmpArr[0]);
            if (deptVOs != null && deptVOs.size() > 0) {
                List<NoticePublishTargetVO> noticePublishTargetVOsForDept = deptVOs.stream()
                        .filter(deptVO -> !deptVO.getDeptCd().equals(tmpArr[0]))
                        .map(deptVO -> getNoticePublishTargetVO(noticePublishId, "1", deptVO.getDeptCd(), null, "0")).toList();
                noticePublishTargetVOs.addAll(noticePublishTargetVOsForDept);
            }
        }
        
        for (String userId : userIds) {
            noticePublishTargetVOs.add(getNoticePublishTargetVO(noticePublishId, "0", userId, null, "1"));
        }

        noticePublishTargetDAO.createNoticePublishTargets(noticePublishTargetVOs);
        return null;
    }
    
    private NoticePublishTargetVO getNoticePublishTargetVO(String noticePublishId, String targetType, String targetId, String isChildCheck, String isShow) {
        NoticePublishTargetVO noticePublishTargetVO = new NoticePublishTargetVO();
        noticePublishTargetVO.setNoticePublishId(noticePublishId);
        noticePublishTargetVO.setTargetType(targetType);
        noticePublishTargetVO.setTargetId(targetId);
        noticePublishTargetVO.setIsChildCheck(isChildCheck);
        noticePublishTargetVO.setIsShow(isShow);
        return noticePublishTargetVO;
    }
    
    @Override
    public ResultVO getNoticePublishTargetList(Map<String, Object> options) {
        ResultVO resultVO = new ResultVO();
        try {
            List<NoticePublishTargetVO> re = noticePublishTargetDAO.selectNoticePublishTargetList(options);

            if (re != null && re.size() > 0) {
                resultVO.setData(re.toArray(NoticePublishTargetVO[]::new));
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));
            } else {
                resultVO.setData(new Object[0]);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            }
        } catch (Exception ex) {
            logger.error("error in getNoticePublishList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
        
        return resultVO;
    }

    @Override
    public ResultPagingVO getNoticePublishTargetListPaged(Map<String, Object> options) {
        ResultPagingVO resultVO = new ResultPagingVO();
        try {
            List<NoticePublishTargetVO> re = noticePublishTargetDAO.selectNoticePublishTargetListPaged(options);
            long totalCount = noticePublishTargetDAO.selectNoticePublishTargetListTotalCount(options);
            long filteredCount = noticePublishTargetDAO.selectNoticePublishTargetListFilteredCount(options);

            if (re != null && re.size() > 0) {
                resultVO.setData(re.toArray(NoticePublishTargetVO[]::new));
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                        MessageSourceHelper.getMessage("system.common.selectdata")));

                resultVO.setRecordsTotal(String.valueOf(totalCount));
                resultVO.setRecordsFiltered(String.valueOf(filteredCount));
            } else {
                resultVO.setData(new Object[0]);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            }
        } catch (Exception ex) {
            logger.error("error in getNoticePublishList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
        
        return resultVO;
    }
}