package kr.gooroom.gpms.health.service;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface GPMSMoudleHealthService {
    /**
     * GPMS 모듈 서비스의 상태 확인
     * 
     * @return GPMSServiceHealthVO
     * @throws Exception
     */
    ResultVO getGPMSModuleStatus(String moduleType) throws Exception;

    /**
     * GPMS 모듈 서비스 전체 상태 확인
     * 
     * @return GPMSServiceHealthVO
     * @throws Exception
     */
    ResultVO getGPMSModuleStatus() throws Exception;

    /**
     * GPMS 모듈 서비스 조작
     * 
     * @return SubModuleStatusVO
     * @throws Exception
     */
    StatusVO turnGPMSModule(String moduleType, String moduleActionType) throws Exception;
}
