package kr.gooroom.gpms.notice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.common.errors.InternalServerErrorException;
import kr.gooroom.gpms.common.utils.PaginationUtil;
import kr.gooroom.gpms.notice.controller.model.response.TargetNoticeResponseModel;
import kr.gooroom.gpms.notice.service.NoticeService;
import kr.gooroom.gpms.notice.service.TargetNoticeVO;
import kr.gooroom.gpms.notice.service.UserOpenedNoticeService;
import kr.gooroom.gpms.user.service.UserTokenService;

@Controller
public class TargetNoticeController {

	private static final Logger logger = LoggerFactory.getLogger(TargetNoticeController.class);

	@Resource(name = "noticeService")
	private NoticeService noticeService;

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "userTokenService")
	private UserTokenService userTokenService;

	@Resource(name = "userOpenedNoticeService")
	private UserOpenedNoticeService userOpenedNoticeService;

	@GetMapping(value = "/notice")
	public ModelAndView notice() {
		return new ModelAndView("main/notice");
	}

	@GetMapping(value = "/apis/notices")
	@ResponseBody
	public ResponseEntity<List<TargetNoticeResponseModel>> getNoticesByTarget(
			@RequestAttribute(value = "CLIENT_ID") String clientId,
			@RequestAttribute(value = "USER_ID", required = false) String userId,
			@PageableDefault(page = 0, size = 10, sort = "openDt", direction = Direction.DESC) Pageable pageable) {

		logger.debug("REST request to get Notices : {}, {}", clientId, userId);

		Page<TargetNoticeResponseModel> page = null;
		try {
			page = noticeService.getNoticesByTarget(pageable, userId, clientId)
					.map(tnVO -> new TargetNoticeResponseModel(tnVO, false));
		} catch (Exception ex) {
			throw new InternalServerErrorException("error in getNoticesByTarget");
		}
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/apis/notices");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping(value = "/apis/notices/{noticePublishId}")
	@ResponseBody
	public ResponseEntity<TargetNoticeResponseModel> getNoticeByTarget(
			@RequestAttribute(value = "CLIENT_ID") String clientId,
			@RequestAttribute(value = "USER_ID", required = false) String userId,
			@PathVariable String noticePublishId) {

		logger.debug("REST request to get Notice : {}, {}, {}", clientId, userId, noticePublishId);

		Optional<TargetNoticeResponseModel> notice = null;
		try {
			notice = noticeService.getNoticeByTarget(userId, clientId, noticePublishId)
					.map(tnVO -> new TargetNoticeResponseModel(tnVO, true));
			if (userId != null && notice.get().getOpenedDt() == null) {
				userOpenedNoticeService.createUserOpenedNotice(userId, noticePublishId);
			}
		} catch (Exception ex) {
			throw new InternalServerErrorException("error in getNoticeByTarget");
		}

		return notice.map(response -> ResponseEntity.ok(response)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping(value = "/apis/notices/{noticePublishId}/pagenumber")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> getPageNumberOfNoticeByTarget(
			@RequestAttribute(value = "CLIENT_ID") String clientId,
			@RequestAttribute(value = "USER_ID", required = false) String userId, @PathVariable String noticePublishId,
			@PageableDefault(page = 0, size = 10, sort = "openDt", direction = Direction.DESC) Pageable pageable) {

		logger.debug("REST request to get PageNumber of notice : {}, {}, {}", clientId, userId, noticePublishId);

		int pageNumber = 0;
		try {
			while (true) {
				Pageable newPageable = new PageRequest(pageNumber, pageable.getPageSize(), pageable.getSort());
				Page<TargetNoticeVO> page = noticeService.getNoticesByTarget(newPageable, userId, clientId);
				if (page.getNumberOfElements() <= 0) {
					break;
				}
				if (page.getContent().stream().anyMatch(tnVO -> tnVO.getNoticePublishId().equals(noticePublishId))) {
					break;
				}
				pageNumber++;
			}
		} catch (Exception ex) {
			throw new InternalServerErrorException("error in getPageNumberOfNoticeByTarget");
		}

		Map<String, Integer> responseMap = new HashMap<>();
		responseMap.put("pageNumber", pageNumber + 1);

		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

}
