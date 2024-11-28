package kr.gooroom.gpms.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.health.service.GPMSHealthCheckVO;
import kr.gooroom.gpms.health.service.HealthCheckService;

@RequestMapping("/health")
@Controller
public class GPMSHealthCheckController {

    private static final Logger logger = LoggerFactory.getLogger(GPMSHealthCheckController.class);

    @Resource(name = "healthCheckService")
	private HealthCheckService healthCheckService;

    //등록 서버 조회
    @PostMapping(value = "/getServerHealthList")
    public @ResponseBody ResultVO getServerHealthList() {
        ResultVO resultVO = new ResultVO();
        try{
            resultVO = healthCheckService.getServerHealthList();
        } catch (Exception ex) {
			logger.error("error in selectListUpdateServerHealth : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
        return resultVO;
    }

    //레포서버 url체크
    @PostMapping(value = "/checkRepoServerUrl")
    public @ResponseBody ResultVO checkRepoServerUrl(@RequestParam(value = "url")String url, @RequestParam(value = "dist")String dist) throws Exception{
        return healthCheckService.checkRepoServerUrl(url, dist);
    }

    //레포서버 등록
    @PostMapping(value = "/registerRepoServer")
    public @ResponseBody ResultVO registerRepoServer(@ModelAttribute("paramVO") GPMSHealthCheckVO paramVO) throws Exception{
        return healthCheckService.createHealthCheckServerList(paramVO);
    }

    //레포서버 등록 삭제
    @PostMapping(value = "/deleteRegisterRepoServer")
    public @ResponseBody ResultVO deleteRegisterRepoServer(@ModelAttribute("paramVO") GPMSHealthCheckVO paramVO) throws Exception{
        return healthCheckService.deleteHealthCheckServerList(paramVO);
    }

    //DB서버 등록
    @PostMapping(value = "/registerDbServer")
    public @ResponseBody ResultVO registerDbServer(@RequestBody GPMSHealthCheckVO paramVO) throws Exception{
        paramVO.decodePassword();

        return healthCheckService.createHealthCheckServerList(paramVO);
    }

    //db서버 url체크
    @PostMapping(value = "/checkDbServerUrl")
    public @ResponseBody ResultVO checkDbServerUrl(@RequestBody GPMSHealthCheckVO paramVO) throws Exception{
        ResultVO resultVO = new ResultVO();

        paramVO.decodePassword();

        if(!healthCheckService.checkDbServerUrlDup(paramVO.getUrl())){
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, "duplication url", "이미 등록된 서버입니다.")); 
            return resultVO;
        }

        if(healthCheckService.checkDbServerUrl(paramVO)){
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, "available url", "등록가능한 URL입니다."));
        }else{
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, "connection fail", "등록한 db정보로 연결할 수 없습니다."));
        }
       
        return resultVO;
    }

    //repo 서버 헬스체크
    // @PostMapping(value = "/repoHealthCheck")
    // public @ResponseBody void repoHealthCheck(@RequestParam(value = "url")String url) throws Exception{
    //     healthCheckService.repoHealthCheck(url);
    // }

    //db 서버 헬스체크
    @PostMapping(value = "/dbHealthCheck")
    public @ResponseBody void dbHealthCheck(@RequestParam(value="url") String url) throws Exception{
        healthCheckService.dbHealthCheck(url);
    }

}
