package kr.gooroom.gpms.notice.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticePublishService;
import kr.gooroom.gpms.notice.service.NoticePublishVO;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;

@Controller
public class NoticePublishController {

	private static final Logger logger = LoggerFactory.getLogger(NoticePublishController.class);
	
	@Resource(name = "noticePublishService")
	private NoticePublishService noticePublishService;
	
	@PostMapping(value = "/createNoticePublish")
	@ResponseBody
	public ResultVO createNoticePublish(@ModelAttribute NoticePublishVO noticePublishVO) {
	    ResultVO resultVO = new ResultVO();
	    
	    try {
	        StatusVO status = noticePublishService.createNoticePublish(noticePublishVO);
	        resultVO.setData(Collections.singletonList(noticePublishVO).toArray());
            resultVO.setStatus(status);
	    } catch (Exception e) {
            logger.error("error in createNoticePublish : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
	        resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
	    return resultVO;
	}
	
	
	@PostMapping(value = "/updateNoticePublish")
	@ResponseBody
	public ResultVO updateNoticePublish(@ModelAttribute NoticePublishVO noticePublishVO) {
	    ResultVO resultVO = new ResultVO();
        
        try {
            StatusVO status = noticePublishService.updateNoticePublish(noticePublishVO);
            resultVO.setData(Collections.singletonList(noticePublishVO).toArray());
            resultVO.setStatus(status);
        } catch (Exception e) {
            logger.error("error in updateNoticeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            if (resultVO != null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
        }

        return resultVO;
	}

	@PostMapping(value = "/readNoticePublishListPaged")
	@ResponseBody
	public ResultVO readNoticePublishListPaged(HttpServletRequest req, HttpServletResponse res) {
		
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<>();
		
		// << options >>
		options.put("noticeId", req.getParameter("noticeId"));
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		
		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		
		if ("chNoticePublishId".equalsIgnoreCase(paramOrderColumn)) {
		    options.put("paramOrderColumn", "NOTICE_PUBLISH_ID");
		} else if ("chOpenDt".equalsIgnoreCase(paramOrderColumn)) {
		    options.put("paramOrderColumn", "OPEN_DT");
		} else if ("chCloseDt".equalsIgnoreCase(paramOrderColumn)) {
		    options.put("paramOrderColumn", "CLOSE_DT");
		} else if ("chViewType".equalsIgnoreCase(paramOrderColumn)) {
		    options.put("paramOrderColumn", "VIEW_TYPE");
		} else if ("chViewCnt".equalsIgnoreCase(paramOrderColumn)) {
            options.put("paramOrderColumn", "OPENED_USER_CNT");
        } else if ("chInstantNoticeCnt".equalsIgnoreCase(paramOrderColumn)) {
            options.put("paramOrderColumn", "INSTANT_ALARM_CNT");
        } else if ("chRegUserId".equalsIgnoreCase(paramOrderColumn)) {
            options.put("paramOrderColumn", "REG_USER_ID");
        } else if ("chStatusCd".equalsIgnoreCase(paramOrderColumn)) {
            options.put("paramOrderColumn", "STATUS_CD");
        } else {
		    options.put("paramOrderColumn", "NOTICE_PUBLISH_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = noticePublishService.getNoticePublishList(options);
		    resultVO.setDraw(String.valueOf(req.getParameter("page")));
		    resultVO.setOrderColumn(paramOrderColumn);
		    resultVO.setOrderDir(paramOrderDir);
		    resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readNoticePublishListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		
		return resultVO;
	}
}
