package kr.gooroom.gpms.metrics.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.metrics.service.ResourceMetricsService;

@Controller
public class ResourceMetricsController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceMetricsController.class);

    @Resource(name = "resourceMetricsService")
    private ResourceMetricsService resourceMetricsService;

    /**
     * initialize binder for date format
     * <p>
     * ex) date format : 2017-10-04
     *
     * @param binder WebDataBinder
     * @return void
     *
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * generate client group list data.
     *
     * @param req   HttpServletRequest
     * @param res   HttpServletResponse
     * @param model ModelMap
     * @return ResultVO
     *
     */
    @PostMapping(value = "/readResourceMetrics")
    public @ResponseBody ResultVO readResourceMetrics(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

        String resourceType = req.getParameter("resourceType");

        ResultVO resultVO = new ResultVO();

        try {
            resultVO = resourceMetricsService.readResourceMetrics(resourceType);

        } catch (Exception ex) {
//            logger.error("error in readResourceMetrics : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
//                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (resultVO != null) {
//                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
//                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
        }
        return resultVO;
    }
}
