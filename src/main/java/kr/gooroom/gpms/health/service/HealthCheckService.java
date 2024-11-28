package kr.gooroom.gpms.health.service;

import java.sql.SQLException;

import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface HealthCheckService {

    ResultVO getServerHealthList();

    ResultVO checkRepoServerUrl(String url, String dist) throws Exception;

    ResultVO createHealthCheckServerList(GPMSHealthCheckVO paramVO) throws Exception;

    ResultVO deleteHealthCheckServerList(GPMSHealthCheckVO paramVO) throws Exception;

    void repoHealthCheck(String url, String dist) throws Exception;

    void dbHealthCheck(String url) throws Exception;

    boolean checkDbServerUrl(GPMSHealthCheckVO paramVO) throws Exception;

    boolean checkDbServerUrlDup(String url) throws Exception;

}
