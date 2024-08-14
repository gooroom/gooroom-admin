package kr.gooroom.gpms.notice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticeService;
import kr.gooroom.gpms.notice.service.NoticeVO;
import kr.gooroom.gpms.notice.service.TargetNoticeVO;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

	private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

	@Resource(name = "noticeDAO")
	private NoticeDAO noticeDAO;

	@Override
	public StatusVO createNoticeData(NoticeVO noticeVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			noticeVO.setModUserId(LoginInfoHelper.getUserId());
			noticeVO.setRegUserId(LoginInfoHelper.getUserId());

			// create notice master
			long resultCnt = noticeDAO.createNoticeMaster(noticeVO);

			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("notice.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("notice.result.noinsert"));
			}
		} catch (Exception ex) {
			logger.error("error in createNoticeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	@Override
	public StatusVO updateNoticeData(NoticeVO noticeVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			noticeVO.setModUserId(LoginInfoHelper.getUserId());

			// update notice master
			long resultCnt = noticeDAO.updateNoticeMaster(noticeVO);

			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("notice.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("notice.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in updateNoticeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete notice master
	 * 
	 * @param noticeId String notice id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO deleteNoticeMaster(String noticeId) throws Exception {

		StatusVO statusVO = new StatusVO();

		NoticeVO noticeVO = new NoticeVO();
		noticeVO.setModUserId(LoginInfoHelper.getUserId());
		noticeVO.setNoticeId(noticeId);
		noticeVO.setStatusCd(GPMSConstants.STS_DELETE_NOTICE);

		try {
			long reCnt = noticeDAO.deleteNoticeMaster(noticeVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("notice.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("notice.result.nodelete"));
			}
		} catch (Exception ex) {
			logger.error("error in deleteUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	@Override
	public ResultPagingVO getNoticeList(Map<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			// add userId if not super
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("userId", LoginInfoHelper.getUserId());
			}
			
			List<NoticeVO> re = noticeDAO.selectNoticeList(options);
			long totalCount = noticeDAO.selectNoticeListTotalCount(options);
			long filteredCount = noticeDAO.selectNoticeListFilteredCount(options);

			if (re != null && re.size() > 0) {
				NoticeVO[] row = re.stream().toArray(NoticeVO[]::new);
				resultVO.setData(row);
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
			logger.error("error in getNoticeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	@Override
	public Page<TargetNoticeVO> getNoticesByTarget(Pageable pageable, String userId, String clientId) throws Exception {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("start", pageable.getPageNumber() * pageable.getPageSize());
		options.put("size", pageable.getPageSize());
		pageable.getSort().forEach(sort -> {
			options.put("sortD", sort.getDirection().name());

			if ("openDt".equalsIgnoreCase(sort.getProperty())) {
				options.put("sortP", "OPEN_DT");
			} else {
				options.put("sortP", "OPEN_DT");
			}
		});
		if (userId != null) {
			options.put("userId", userId);
		}
		options.put("clientId", clientId);

		List<TargetNoticeVO> targetNotices = noticeDAO.findAllByTarget(options);
		long totalCount = noticeDAO.getTotalCountByTarget(options);

		return new PageImpl<TargetNoticeVO>(targetNotices, pageable, totalCount);
	}

	@Override
	public Optional<TargetNoticeVO> getNoticeByTarget(String userId, String clientId, String noticePublishId)
			throws Exception {
		Map<String, Object> options = new HashMap<String, Object>();

		if (userId != null) {
			options.put("userId", userId);
		}
		options.put("clientId", clientId);
		options.put("noticePublishId", noticePublishId);

		return Optional.ofNullable(noticeDAO.findOneByTarget(options));
	}
}
