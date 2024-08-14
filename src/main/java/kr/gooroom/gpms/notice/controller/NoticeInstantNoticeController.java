package kr.gooroom.gpms.notice.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.notice.service.NoticeInstantNoticeService;
import kr.gooroom.gpms.notice.service.NoticeInstantNoticeVO;

@Controller
public class NoticeInstantNoticeController {

	private static final Logger logger = LoggerFactory.getLogger(NoticeInstantNoticeController.class);

	@Resource(name = "noticeInstantNoticeService")
	private NoticeInstantNoticeService noticeInstantNoticeService;

	@Inject
	private CustomJobMaker jobMaker;

	@PostMapping(value = "/createNoticeInstantNotice")
	@ResponseBody
	public ResultVO createNoticeInstantNotice(@ModelAttribute NoticeInstantNoticeVO noticeInstantNoticeVO) {
		ResultVO resultVO = new ResultVO();
		try {
			noticeInstantNoticeService.createNoticeInstantNotice(noticeInstantNoticeVO);
			jobMaker.createJobForNoticeInstantNotice(noticeInstantNoticeVO.getNoticePublishId());
		} catch (Exception ex) {
			logger.error("error in createResetViolatedClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	@PostMapping(value = "/readNoticeInstantNoticeListPaged")
	@ResponseBody
	public ResultVO readNoticeInstantNoticeListPaged(HttpServletRequest req) {
		ResultPagingVO resultVO = new ResultPagingVO();
		Map<String, Object> options = new HashMap<>();

		// << options >>
		options.put("noticePublishId", req.getParameter("noticePublishId"));

		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");

		options.put("paramOrderColumn", "NOTICE_INSTANT_ALARM_ID");
		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = noticeInstantNoticeService.getNoticeInstantNoticeList(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readNoticeInstantNoticeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	};
}
