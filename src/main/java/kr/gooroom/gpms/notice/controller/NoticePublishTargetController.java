package kr.gooroom.gpms.notice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticePublishTargetService;

@Controller
public class NoticePublishTargetController {

	private static final Logger logger = LoggerFactory.getLogger(NoticePublishTargetController.class);
	
	@Resource(name = "noticePublishTargetService")
	private NoticePublishTargetService noticePublishTargetService;

	@PostMapping(value = "/createNoticePublishTarget")
	@ResponseBody
	public ResultVO createNoticePublishTarget(
	        @RequestParam(value = "noticePublishId") String noticePublishId,
	        @RequestParam(value = "grpInfos[]", defaultValue="") List<String> grpInfos,
            @RequestParam(value = "clientIds[]", defaultValue="") List<String> clientIds,
            @RequestParam(value = "deptInfos[]", defaultValue="") List<String> deptInfos,
            @RequestParam(value = "userIds[]", defaultValue="") List<String> userIds
    ) {
	    ResultVO resultVO = new ResultVO();
	    try {
	        StatusVO statusVO = noticePublishTargetService.createNoticePublishTarget(noticePublishId, grpInfos, clientIds, deptInfos, userIds);
	        resultVO.setStatus(statusVO);
	    } catch (Exception e) {
            logger.error("error in createNoticePublishTarget : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

	    return resultVO;
	}

	@PostMapping(value = "/readNoticePublishTargetListPaged")
	@ResponseBody
	public ResultVO readNoticePublishTargetListPaged(HttpServletRequest req) {
	    ResultPagingVO resultVO = new ResultPagingVO();
        Map<String, Object> options = new HashMap<>();
        
        // << options >>
        options.put("noticePublishId", req.getParameter("noticePublishId"));
        
        // << paging >>
        String paramOrderColumn = req.getParameter("orderColumn");
        String paramOrderDir = req.getParameter("orderDir");
        String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
        String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");

        options.put("paramOrderColumn", "TARGET_TYPE");
        if ("DESC".equalsIgnoreCase(paramOrderDir)) {
            options.put("paramOrderDir", "DESC");
        } else {
            options.put("paramOrderDir", "ASC");
        }
        options.put("paramStart", Integer.parseInt(paramStart));
        options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = noticePublishTargetService.getNoticePublishTargetListPaged(options);
            resultVO.setDraw(String.valueOf(req.getParameter("page")));
            resultVO.setOrderColumn(paramOrderColumn);
            resultVO.setOrderDir(paramOrderDir);
            resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
		    logger.error("error in readNoticePublishTargetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
		    		MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		    if (resultVO != null) {
		    	resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
		    			MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
	    	}
	    }
		
		return resultVO;
	}

	@PostMapping(value = "/readNoticePublishTargetList")
    @ResponseBody
    public ResultVO readNoticePublishTargetList(HttpServletRequest req) {
       ResultVO resultVO = new ResultPagingVO();
        Map<String, Object> options = new HashMap<>();
        
        // << options >>
        options.put("noticePublishId", req.getParameter("noticePublishId"));

        try {
            resultVO = noticePublishTargetService.getNoticePublishTargetList(options);
        } catch (Exception ex) {
            logger.error("error in readNoticePublishTargetList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (resultVO != null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
        }
        
        return resultVO;
    }
}