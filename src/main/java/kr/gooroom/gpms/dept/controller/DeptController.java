package kr.gooroom.gpms.dept.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.gooroom.gpms.common.service.*;
import kr.gooroom.gpms.common.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.FileUploadService;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.dept.service.DeptService;
import kr.gooroom.gpms.dept.service.DeptVO;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;

@Controller
public class DeptController {

	private static final Logger logger = LoggerFactory.getLogger(DeptController.class);

	@Resource(name = "deptService")
	private DeptService deptService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "fileUploadService")
	private FileUploadService fileUploadService;

	@Resource(name = "excelCommonService")
	private ExcelCommonService excelCommonService;

	@Inject
	private CustomJobMaker jobMaker;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * 조직리스트(트리용 Json) 조회
	 */
	@PostMapping(value = "/readChildrenDeptList")
	public @ResponseBody Object[] readChildrenDeptListForPost(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		String deptCd = req.getParameter("deptCd");
		String hasWithRoot = req.getParameter("hasWithRoot");
		Object[] hm = null;
		try {
			ResultVO resultVO = deptService.getChildrenDeptList(deptCd, hasWithRoot);
			if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
				DeptVO rootInfo = null;
				if (GPMSConstants.GUBUN_YES.equalsIgnoreCase(hasWithRoot)) {
					rootInfo = (DeptVO) resultVO.getExtend()[0];
				}

				Object[] objs = resultVO.getData();
				hm = new Object[objs.length];
				for (int i = 0; i < objs.length; i++) {
					DeptVO vo = (DeptVO) objs[i];
					HashMap<String, Object> vm = new HashMap<String, Object>();
					vm.put("title", vo.getDeptNm());
					vm.put("key", vo.getDeptCd());
					vm.put("hasChildren", vo.getHasChildren());
					vm.put("modDt", vo.getModDate());
					vm.put("regDt", vo.getRegDate());
					vm.put("whleDeptCd", vo.getWhleDeptCd());
					vm.put("level", vo.getDeptLevel());
					vm.put("itemCount", vo.getItemCount());
					vm.put("itemTotalCount", vo.getItemTotalCount());
					vm.put("optYn", vo.getOptYn());
					vm.put("sortOrder", vo.getSortOrder());
					vm.put("expireDt", vo.getExpireDate());
					vm.put("isExpired", vo.getIsExpired());
					vm.put("isUseExpire", vo.getIsUseExpire());
					vm.put("parentExpireDt", vo.getUprExpireDate());
					if (rootInfo != null) {
						vm.put("rootItemCount", rootInfo.getItemCount());
						vm.put("rootItemTotalCount", rootInfo.getItemTotalCount());
					}
					hm[i] = vm;
				}
			}
		} catch (Exception e) {
			hm = new Object[0];
		}
		return hm;
	}

	/**
	 * 조직 정보 수정
	 * 
	 * @param paramVO DeptVO
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/updateDeptInfo")
	// ROLLBACK trasaction form inherit
	public @ResponseBody ResultVO updateDeptInfo(@ModelAttribute("paramVO") DeptVO paramVO, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = deptService.updateDeptData(paramVO);
			resultVO.setStatus(status);

			ArrayList<String> userIdList = new ArrayList<String>();

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {

				// set expire date to children, if it is date is bigger than this.
				deptService.updateChildrenDeptExpireData(paramVO);

				// create job
				ResultVO users = userService.getUserListInDept(paramVO.getDeptCd());
				if (users != null && users.getData() != null && users.getData().length > 0) {
					for (int c = 0; c < users.getData().length; c++) {
						userIdList.add(((UserVO) users.getData()[c]).getUserId());
					}
				}

				// get children dept list
				ResultVO allDeptResultVo = deptService.getAllChildrenDeptList(paramVO.getDeptCd());
				if (allDeptResultVo != null && allDeptResultVo.getData() != null
						&& allDeptResultVo.getData().length > 0) {
					DeptVO[] depts = (DeptVO[]) allDeptResultVo.getData();
					String[] deptCds = new String[depts.length];
					for (int i = 0; i < depts.length; i++) {
						deptCds[i] = depts[i].getDeptCd();
						ResultVO deptUsers = userService.getUserListInDept(depts[i].getDeptCd());
						if (deptUsers != null && deptUsers.getData() != null && deptUsers.getData().length > 0) {
							for (int c = 0; c < deptUsers.getData().length; c++) {
								userIdList.add(((UserVO) deptUsers.getData()[c]).getUserId());
							}
						}
					}

					//하위 조직 정책 변경
					StatusVO inheritDeptStatus = deptService.updateRuleInfoToMultiDept(deptCds,
							paramVO.getBrowserRuleId(), paramVO.getMediaRuleId(), paramVO.getSecurityRuleId(),
							paramVO.getFilteredSoftwareRuleId(), paramVO.getCtrlCenterItemRuleId(),
							paramVO.getPolicyKitRuleId(), paramVO.getDesktopConfId());
					resultVO.setStatus(inheritDeptStatus);

					//하위 사용자 정책 변경
					StatusVO inheritUserStatus = userService.updateRuleInfoToMultiUser(userIdList, paramVO.getBrowserRuleId(), paramVO.getMediaRuleId(), paramVO.getSecurityRuleId(),
							paramVO.getFilteredSoftwareRuleId(), paramVO.getCtrlCenterItemRuleId(),
							paramVO.getPolicyKitRuleId(), paramVO.getDesktopConfId());
					resultVO.setStatus(inheritUserStatus);
				}
			}

			if (userIdList != null && userIdList.size() > 0) {
				String[] userArray = new String[userIdList.size()];
				jobMaker.createJobForUseRuleByChangeDept(userIdList.toArray(userArray));
			}

		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * 여러조직의 권한정보 일괄 수정
	 * @param deptCds
	 * @param browserRuleId
	 * @param mediaRuleId
	 * @param securityRuleId
	 * @param filteredSoftwareRuleId
	 * @param ctrlCenterItemRuleId
	 * @param policyKitRuleId
	 * @param desktopConfId
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/updateRuleForDepts")
	public @ResponseBody ResultVO updateRuleForDepts(@RequestParam(value = "deptCds", required = true) String deptCds,
			@RequestParam(value = "browserRuleId") String browserRuleId,
			@RequestParam(value = "mediaRuleId") String mediaRuleId,
			@RequestParam(value = "securityRuleId") String securityRuleId,
			@RequestParam(value = "filteredSoftwareRuleId") String filteredSoftwareRuleId,
			@RequestParam(value = "ctrlCenterItemRuleId") String ctrlCenterItemRuleId,
			@RequestParam(value = "policyKitRuleId") String policyKitRuleId,
			@RequestParam(value = "desktopConfId") String desktopConfId, ModelMap model) {

		String[] deptCdArray = deptCds.split(",");
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = deptService.updateRuleInfoToMultiDept(deptCdArray, browserRuleId, mediaRuleId,
					securityRuleId, filteredSoftwareRuleId, ctrlCenterItemRuleId, policyKitRuleId, desktopConfId);
			resultVO.setStatus(status);

			// [JOBCREATE]
			ArrayList<String> userIdList = new ArrayList<String>();
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				for (int i = 0; i < deptCdArray.length; i++) {
					ResultVO users = userService.getUserListInDept(deptCdArray[i]);
					if (users != null && users.getData() != null && users.getData().length > 0) {
						for (int c = 0; c < users.getData().length; c++) {
							userIdList.add(((UserVO) users.getData()[c]).getUserId());
						}
					}
				}

				// create job
				if (userIdList != null && userIdList.size() > 0) {
					String[] userArray = new String[userIdList.size()];
					jobMaker.createJobForUseRuleByChangeDept(userIdList.toArray(userArray));
				}
			}
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * 조직 설정 수정 - 하위조직 포함
	 * @param parentDeptCd
	 * @param objId
	 * @param confType
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/updateDeptConfInherit")
	public @ResponseBody ResultVO updateDeptConfInherit(@RequestParam(value = "deptCd") String parentDeptCd,
			@RequestParam(value = "objId") String objId, @RequestParam(value = "confType") String confType,
			ModelMap model) {
		ResultVO resultVO = new ResultVO();
		try {
			// get children dept list
			ResultVO allDeptResultVo = deptService.getAllChildrenDeptList(parentDeptCd);
			if (allDeptResultVo != null && allDeptResultVo.getData() != null && allDeptResultVo.getData().length > 0) {
				DeptVO[] depts = (DeptVO[]) allDeptResultVo.getData();
				for (DeptVO vo : depts) {
					deptService.updateDeptConf(vo.getDeptCd(), objId, confType);
				}
			}

			StatusVO status = new StatusVO();
			status.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");

			resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * 조직 정보 등록
	 * @param paramVO
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/createDeptInfo")
	public @ResponseBody ResultVO createDeptInfo(@ModelAttribute("paramVO") DeptVO paramVO, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		try {
			// 중복 검사 (아이디)
			StatusVO dupCdStatus = deptService.isExistDeptCd(paramVO.getDeptCd());
			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupCdStatus.getResult())) {
				// 조직 이름 중복 검사
				StatusVO dupNameStatus = deptService.isExistDeptNameByParentCd(paramVO.getUprDeptCd(),
						paramVO.getDeptNm());
				if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupNameStatus.getResult())) {
					StatusVO status = deptService.createDeptData(paramVO);
					resultVO.setStatus(status);
				} else {
					resultVO.setStatus(dupNameStatus);
				}
			} else {
				resultVO.setStatus(dupCdStatus);
			}
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 삭제
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/deleteDeptData")
	public @ResponseBody ResultVO deleteDeptData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String deptCd = req.getParameter("deptCd");
		String deleteGubun = req.getParameter("deleteGubun");

		ResultVO resultVO = new ResultVO();

		try {

			if (GPMSConstants.GUBUN_DEPT_DELETE_ALL.equalsIgnoreCase(deleteGubun)) {

				StatusVO status = deptService.deleteDeptWithChildren(deptCd);
				resultVO.setStatus(status);
			} else if (GPMSConstants.GUBUN_DEPT_UNUSED_ALL.equalsIgnoreCase(deleteGubun)) {

				StatusVO status = deptService.updateDeptUnusedWithChildren(deptCd);
				resultVO.setStatus(status);
			}

		} catch (Exception ex) {

			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 삭제 - array
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/deleteDeptList")
	public @ResponseBody ResultVO deleteDeptList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String[] deptCds = req.getParameterValues("deptCds[]");
		String isDeleteUser = req.getParameter("isDeleteUser");

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = deptService.deleteDeptList(deptCds, isDeleteUser);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate dept information by selected dept cd.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readDeptData")
	public @ResponseBody ResultVO readDeptData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String deptCd = StringUtils.defaultString(req.getParameter("deptCd"));
		if ("".equals(deptCd)) {
			deptCd = GPMSConstants.DEFAULT_DEPTCD;
		}

		try {
			resultVO = deptService.getDeptData(deptCd);
		} catch (Exception ex) {
			logger.error("error in readClientGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate dept information by selected dept cd list.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readDeptNodeList")
	public @ResponseBody ResultVO readDeptNodeList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();
		String[] deptCds = req.getParameterValues("deptCds[]");

		try {
			resultVO = deptService.readDeptNodeList(deptCds);
		} catch (Exception ex) {
			logger.error("error in readDeptNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get dept list data for paging.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO
	 * 
	 */
	@PostMapping(value = "/readDeptListPaged")
	public @ResponseBody ResultVO readDeptListPaged(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_")
				: "");
		options.put("searchKey", searchKey);

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chDeptNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DEPT_NM");
		} else if ("chUserCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "USER_CNT");
		} else {
			options.put("paramOrderColumn", "DEPT_NM");
		}
		options.put("paramOrderDir", paramOrderDir);

		try {

			resultVO = deptService.getDeptListPaged(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readDeptListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create(insert) new dept data by file
	 * <p>
	 * use file uploader.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createDeptDataFromFile")
	public @ResponseBody ResultVO createDeptDataFromFile(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) req;
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

		if (iterator.hasNext()) {

			String fileName = (String) iterator.next();
			MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

			List<DeptVO> deptList = new ArrayList<DeptVO>();
			List<List<String>> dataList = null;
			try {

				// read data to List<List<String>> from file
				dataList = excelCommonService.read(multipartFile, GPMSConstants.RULE_GRADE_DEPT);
				if(dataList == null) {
					if (resultVO != null) {
						resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
								"읽을 수 없는 데이터가 존재합니다."));
					}
					return resultVO;
				}

				resultVO = deptService.isCanUpdateDeptDataFromFile(dataList);
				if(resultVO.getStatus().getResult().equals(GPMSConstants.MSG_FAIL)) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							resultVO.getStatus().getMessage()));
					return resultVO;
				}
				// set deptList from resultVO
				Object[] objs = resultVO.getData();
				for(int i = 0; i < objs.length; i++) {
					DeptVO vo = (DeptVO) objs[i];
					deptList.add(vo);
				}

				if (deptList.size() > 0) {
					// insert into database
					StatusVO status = deptService.updateDeptDataFromFile(deptList);
					resultVO.setStatus(status);
				}

			} catch (Exception ex) {
				logger.error("error in createDeptDataFromFile : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
				}
			}

		} else {
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR, "저장할 파일이 없음"));
			}
		}

		return resultVO;
	}

	/**
	 * create(insert) new dept list file by data
	 * <p>
	 * use file downloader.
	 *
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
    @PostMapping(value = "/createDeptFileFromData")
    public @ResponseBody void createDeptFileFromData(HttpServletRequest req, HttpServletResponse res) {
		//set file name
		String fileName = "DEPT_gooroom_" + CommonUtils.convertDataToString(new Date());

		res.reset();
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/vnd.ms-excel");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        try{
			XSSFWorkbook workbook = deptService.createDeptFileFromData();

            workbook.write(res.getOutputStream());
            workbook.close();

            res.flushBuffer();

        } catch (Exception ex) {

			logger.error("error in createDeptFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }
    }

	/**
	 * create(insert) new dept sample file for upload
	 * <p>
	 * use file downloader.
	 *
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createDeptSampleFileFromData")
	public @ResponseBody void createDeptSampleFileFromData(HttpServletRequest req, HttpServletResponse res) {
		//set file name
		String fileName = "DEPT_SAMPLE_gooroom_" + CommonUtils.convertDataToString(new Date());

		res.reset();
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/vnd.ms-excel");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

		try{
			XSSFWorkbook workbook = deptService.createDeptSampleFileFromData();

			workbook.write(res.getOutputStream());
			workbook.close();

			res.flushBuffer();

		} catch (Exception ex) {

			logger.error("error in createDeptSampleFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
	}
}
