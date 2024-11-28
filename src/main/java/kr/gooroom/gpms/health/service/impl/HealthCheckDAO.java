package kr.gooroom.gpms.health.service.impl;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.health.service.GPMSHealthCheckVO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository("healthCheckDAO")
public class HealthCheckDAO extends SqlSessionMetaDAO{

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckDAO.class);

    public List<GPMSHealthCheckVO> selectHealthCheckServerList(String target) throws SQLException {
        return sqlSessionMeta.selectList("selectHealthCheckServerList", target);
    }

    public void insertHealthServer(GPMSHealthCheckVO paramVO) throws SQLException{
        if (paramVO != null) {
            paramVO.encryptPassword();
        }
        sqlSessionMeta.insert("insertHealthServer",paramVO);
    }

    public void deleteHealthServer(GPMSHealthCheckVO paramVO) {
        sqlSessionMeta.delete("deleteHealthServer", paramVO);
    }

    public GPMSHealthCheckVO selectHealthCheckServerByURL(String url, String dist) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        params.put("dist", dist);

        GPMSHealthCheckVO gpmsHealthCheckVO = sqlSessionMeta.selectOne("selectHealthCheckServerByURL", params);
        if (gpmsHealthCheckVO != null) {
            gpmsHealthCheckVO.decryptPassword();
        }

        return gpmsHealthCheckVO;
    }

    public void updateServerHealth(GPMSHealthCheckVO vo) throws SQLException {
        sqlSessionMeta.update("updateServerHealth", vo);
    }

    public void insertDbCheckItem(GPMSHealthCheckVO.DbCheckItemsVO paramVO) throws SQLException{
        sqlSessionMeta.insert("insertDbCheckItem",paramVO);
    }

    public void updateDbItemResult(GPMSHealthCheckVO.DbCheckItemsVO vo) throws SQLException {
        sqlSessionMeta.update("updateDbItemResult", vo);
    }

}
