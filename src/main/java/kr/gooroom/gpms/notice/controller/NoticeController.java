package kr.gooroom.gpms.notice.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticeService;
import kr.gooroom.gpms.notice.service.NoticeVO;

@Controller
public class NoticeController {

	private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@PostMapping(value = "/createNotice")
	@ResponseBody
	public ResultVO createNotice(@ModelAttribute NoticeVO noticeVO) {

		ResultVO resultVO = new ResultVO();

		try {
			StatusVO status = noticeService.createNoticeData(noticeVO);
			resultVO.setStatus(status);
		} catch (Exception e) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	@PostMapping(value = "/updateNotice")
	@ResponseBody
	public ResultVO updateNotice(@ModelAttribute NoticeVO noticeVO) {

		ResultVO resultVO = new ResultVO();

		try {
			StatusVO status = noticeService.updateNoticeData(noticeVO);
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

	@PostMapping(value = "/deleteNotice")
	@ResponseBody
	public ResultVO deleteNotice(@RequestParam(value = "noticeId", required = true) String noticeId) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = noticeService.deleteNoticeMaster(noticeId);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in deleteUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	@PostMapping(value = "/readNoticeListPaged")
	@ResponseBody
	public ResultVO readNoticeListPaged(HttpServletRequest req, HttpServletResponse res) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));

		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");

		if ("chNoticeId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "NOTICE_ID");
		} else if ("chTitle".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TITLE");
		} else if ("chRegUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_USER_ID");
		} else {
			options.put("paramOrderColumn", "NOTICE_ID");
		}

		options.put("paramOrderDir", paramOrderDir);
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = noticeService.getNoticeList(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readNoticeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}
}
