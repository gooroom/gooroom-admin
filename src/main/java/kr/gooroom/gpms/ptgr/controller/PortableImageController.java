package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.PortableImageService;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/portable")
public class PortableImageController {

    private static final Logger logger = LoggerFactory.getLogger(PortableImageController.class);

    @Resource(name = "portableImageService")
    private PortableImageService portableImageService;

    @GetMapping(value="/admin/images")
    @ResponseBody
    public ResultVO getImageList() {
        ResultVO resultVO = new ResultVO();
        try
        {
            resultVO = portableImageService.readImageData();
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }
        return resultVO;
    }

    @PostMapping(value="/admin/images")
    @ResponseBody
    public StatusVO updateImages (@RequestBody PortableImageVO imageVO)  {

        StatusVO statusVO = new StatusVO();
        if (imageVO == null) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, "");
            return statusVO;
        }
        try
        {
            statusVO = portableImageService.updateImageData(imageVO);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }

        return statusVO;
    }

    @PostMapping(value="/admin/images/{imageId}")
    @ResponseBody
    public StatusVO updateImageStatusByImageId (
            @PathVariable String imageId,
            @RequestParam(value= "status") String status)  {

        StatusVO statusVO = new StatusVO();
        if (imageId == null || imageId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, "");
            return statusVO;
        }
        try
        {
            HashMap<String, Object> options =  new HashMap<String, Object>();
            options.put("imageId", imageId);
            options.put("status", status);
            statusVO = portableImageService.updateImageStatus(options);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }

        return statusVO;
    }
    @DeleteMapping (value="/admin/images")
    @ResponseBody
    public StatusVO deleteImages (@RequestBody List<String> ids)  {

        StatusVO statusVO = new StatusVO();
        try
        {
            if (ids.size() == 0) {
                statusVO = portableImageService.deleteAllImageData();
            }
            else {
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("imageIds", ids);
                statusVO = portableImageService.deleteImageDataByImageId(options);
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }

        return statusVO;
    }


}
