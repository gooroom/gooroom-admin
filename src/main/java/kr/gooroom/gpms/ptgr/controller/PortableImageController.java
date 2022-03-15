package kr.gooroom.gpms.ptgr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import kr.gooroom.gpms.ptgr.service.PortableImageViewVO;
import kr.gooroom.gpms.ptgr.service.PortableJobService;
import kr.gooroom.gpms.ptgr.util.JenkinsJob;
import kr.gooroom.gpms.ptgr.util.JenkinsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/portable")
public class PortableImageController {

    private static final Logger logger = LoggerFactory.getLogger(PortableImageController.class);

    @Resource(name = "portableImageService")
    private PortableImageService portableImageService;

    @Resource(name = "portableJobService")
    private PortableJobService portableJobService;

    private final JenkinsUtils jenkinsUtils = new JenkinsUtils();

    /**
     * 휴대형 구름 이미지 정보
     *
     * @return ResultVO
     */
    @PostMapping(value="/readImageListEx")
    @ResponseBody
    public ResultVO getImageList() {
        ResultVO resultVO = new ResultVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            resultVO = portableImageService.readImageData(null);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }
        return resultVO;
    }

    /**
     * 휴대형 구름 이미지 정보
     *
     * @return ResultPagingVO
     */
    @PostMapping(value="/readImageList")
    @ResponseBody
    public ResultPagingVO getImageListPaged(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

        ResultPagingVO resultVO = new ResultPagingVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
            String toDate = StringUtils.defaultString(req.getParameter("toDate"));
            if ("".equals(fromDate) || "".equals(toDate)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                toDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.YEAR, -1);
                fromDate = dateFormat.format(cal.getTime());
            }
            options.put("fromDate", fromDate);
            options.put("toDate", toDate);

            options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
            options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

            String searchType = req.getParameter("searchType");
            if (searchType != null) {
                if (searchType.equalsIgnoreCase("chUserId")) {
                    options.put("searchType", "USER_ID");
                }
                else if (searchType.equalsIgnoreCase("chRegDate")) {
                    options.put("searchType", "REG_DT");
                }
                else if (searchType.equalsIgnoreCase("chCreateDate")) {
                    options.put("searchType", "CREATED_DT");
                }
                else {
                    options.put("searchType", "ALL");
                }
            }

            options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));

            String paramOrderColumn = req.getParameter("orderColumn");
            if (paramOrderColumn != null) {
                if (paramOrderColumn.equalsIgnoreCase("chRegDate")) {
                    options.put("paramOrderColumn", "REG_DT");
                }
                else if (paramOrderColumn.equalsIgnoreCase("chUserIdl")) {
                    options.put("paramOrderColumn", "USER_ID");
                }
                else {
                    options.put("paramOrderColumn", "CREATED_DT");
                }
            }
            String paramOrderDir = req.getParameter("orderDir");
            options.put("paramOrderDir", paramOrderDir);

            String paramLang = req.getParameter("lang");
            options.put("lang", paramLang);

            resultVO = portableImageService.readImageData(options);

            HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
            fromDateHm.put("name", "fromDate");
            fromDateHm.put("value", fromDate);
            HashMap<String, Object> toDateHm = new HashMap<String, Object>();
            toDateHm.put("name", "toDate");
            toDateHm.put("value", toDate);
            resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

            resultVO.setDraw(String.valueOf(req.getParameter("page")));
            resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
            resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
            resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));

            for ( Object o : resultVO.getData()) {
                PortableImageViewVO imgViewVO = (PortableImageViewVO) o;
                if (imgViewVO == null)
                    continue;

                logger.debug("JobID: [" + imgViewVO.getJobId() +"]");
                if (imgViewVO.getJobId() == 0)
                    continue;

                String json = jenkinsUtils.jenkinsGetJobDuration(imgViewVO.getJobId());
                logger.debug("Jenkins Data :\n" + json);
                if (json == null || json.isEmpty())
                    continue;

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JenkinsJob job = objectMapper.readValue(json, JenkinsJob.class);
                    if (job.isBuilding()) {
                        imgViewVO.setDurationTime(job.getEstimatedDuration());
                    }
                    if (job.getResult() == null)  {
                        logger.debug("Jenkins Result is null");
                        continue;
                    }

                    if (job.getResult().equalsIgnoreCase("aborted")) {
                        ResultVO rVO = portableImageService.readImageDataById(imgViewVO.getImageId());
                        if (rVO.getData() != null) {
                            PortableImageVO iVO = (PortableImageVO)rVO.getData()[0];
                            iVO.setStatus(PortableConstants.STATUS_IMAGE_ABORTED);
                            portableImageService.updateImageData(iVO);
                            if (paramLang.equals("ko")) {
                                imgViewVO.setStatus("중단");
                            }
                            else {
                                imgViewVO.setStatus("ABORTED");
                            }
                        }
                    }
                    else if (job.getResult().equalsIgnoreCase("failure")) {
                        ResultVO rVO = portableImageService.readImageDataById(imgViewVO.getImageId());
                        if (rVO.getData() != null) {
                            PortableImageVO iVO = (PortableImageVO)rVO.getData()[0];
                            iVO.setStatus(PortableConstants.STATUS_IMAGE_FAILED);
                            portableImageService.updateImageData(iVO);
                            if (paramLang.equals("ko")) {
                                imgViewVO.setStatus("실패");
                            }
                            else {
                                imgViewVO.setStatus("FAILED");
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    logger.debug(e.getMessage());
                }
            }
        } catch (Exception e) {
            Object[] o = new Object[0];
            resultVO.setData(o);
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return resultVO;
    }

    /**
     * 휴대형 구름 이미지 정보 업데이트
     *
     * @param imageVO
     * @return StatusVO
     */
    @PostMapping(value="/updateImageList")
    @ResponseBody
    public StatusVO updateImageList  (@ModelAttribute PortableImageVO imageVO)  {

        StatusVO statusVO = new StatusVO();
        if (imageVO == null) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam"));
            return statusVO;
        }
        try
        {
            statusVO = portableImageService.updateImageData(imageVO);
            if (imageVO.getStatus() >= 2) {
                portableJobService.deleteJobDataByImageId(imageVO.getImageId());
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                   MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }

        return statusVO;
    }

    /**
     * 휴대형 구름 이미지 정보 상태 업데이트
     *
     * @param imageId
     * @param status
     * @return StatusVO
     */
    @PostMapping(value="/updateImage")
    @ResponseBody
    public StatusVO updateImageStatusByImageId (@RequestParam(value= "imageId") String imageId,
                                                @RequestParam(value= "status") String status)  {

        StatusVO statusVO = new StatusVO();
        if (imageId == null || imageId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam"));
            return statusVO;
        }
        try
        {
            HashMap<String, Object> options =  new HashMap<String, Object>();
            options.put("imageId", imageId);
            options.put("status", status);
            statusVO = portableImageService.updateImageStatus(options);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }

        return statusVO;
    }

    /**
     * 휴대형 구름 이미지 삭제
     *
     * @return StatusVO
     */
    @PostMapping (value="deleteImageList")
    @ResponseBody
    public StatusVO deleteImageList (HttpServletRequest req, HttpServletResponse res)  {

        String id = req.getParameter("ids");
        String[] ids = id.split(",");
        StatusVO statusVO = new StatusVO();
        try
        {
            if (ids.length == 0 || ids[0].equals("")) {
                statusVO = portableImageService.removeAllImageData();
            }
            else {
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("imageIds", ids);
                statusVO = portableImageService.removeImageDataByImageIds(options);
            }
            jenkinsUtils.jenkinsRemoveFile(id);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }

        return statusVO;
    }
}
