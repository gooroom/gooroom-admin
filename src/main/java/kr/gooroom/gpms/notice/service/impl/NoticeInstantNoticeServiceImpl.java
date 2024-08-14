package kr.gooroom.gpms.notice.service.impl;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticeInstantNoticeService;
import kr.gooroom.gpms.notice.service.NoticeInstantNoticeVO;

@Service("noticeInstantNoticeService")
public class NoticeInstantNoticeServiceImpl implements NoticeInstantNoticeService {

	private static final Logger logger = LoggerFactory.getLogger(NoticeInstantNoticeServiceImpl.class);

	@Resource(name = "noticeInstantNoticeDAO")
	private NoticeInstantNoticeDAO noticeInstantNoticeDAO;

	@Override
	public StatusVO createNoticeInstantNotice(NoticeInstantNoticeVO noticeInstantNoticeVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			noticeInstantNoticeVO.setRegUserId(LoginInfoHelper.getUserId());

			// create notice instant notice
			long resultCnt = noticeInstantNoticeDAO.createNoticeInstantNotice(noticeInstantNoticeVO);

			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("noticeinstantalarm.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("noticeinstantalarm.result.noinsert"));
			}
		} catch (Exception ex) {
			logger.error("error in createNoticeInstantNotice : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	@Override
	public ResultPagingVO getNoticeInstantNoticeList(Map<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			List<NoticeInstantNoticeVO> re = noticeInstantNoticeDAO.selectNoticeInstantNoticeList(options);
			long totalCount = noticeInstantNoticeDAO.selectNoticeInstantNoticeListTotalCount(options);
			long filteredCount = noticeInstantNoticeDAO.selectNoticeInstantNoticeListFilteredCount(options);

			if (re != null && re.size() > 0) {
				resultVO.setData(re.stream().toArray(NoticeInstantNoticeVO[]::new));
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
			logger.error("error in getNoticeInstantNoticeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
}
