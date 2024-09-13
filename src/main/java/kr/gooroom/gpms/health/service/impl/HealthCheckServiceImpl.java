package kr.gooroom.gpms.health.service.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.health.service.GPMSHealthCheckVO;
import kr.gooroom.gpms.health.service.HealthCheckScheduler;
import kr.gooroom.gpms.health.service.HealthCheckService;
import kr.gooroom.gpms.health.util.DBConnectionValidator;
import kr.gooroom.gpms.health.util.HealthCheckQueryMap;

@Service("healthCheckService")
public class HealthCheckServiceImpl implements HealthCheckService{
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckServiceImpl.class);

    private DBConnectionValidator connectionValidator = new DBConnectionValidator();

    @Resource(name = "healthCheckDAO")
    private HealthCheckDAO healthCheckDAO;

    @Autowired
    private HealthCheckScheduler scheduler;

    @PostConstruct
    public void initialize() {
        if (!scheduler.isEmpty()) {
            return;
        }

        try {
            List<GPMSHealthCheckVO> checkList = healthCheckDAO.selectHealthCheckServerList(null);

            if (checkList != null && !checkList.isEmpty()) {
                for (GPMSHealthCheckVO entry : checkList) {
                    switch (entry.getTarget()) {
                        case "repo":
                            scheduleRepoCheck(entry.getUrl(), entry.getDist(), entry.getSchedule());
                            break;
                        case "db":
                            scheduleDBCheck(entry.getUrl(), entry.getSchedule());
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("error to fetch health check server list: {}", e.getMessage());
        }
    }

    /**
	 * 레포/db 서버 헬스 체크 결과 조회
	 * 
     * @param
	 * @return StatusVO
	 * @throws Exception
	 */
    @Override
    public ResultVO getServerHealthList() {
        ResultVO resultVO = new ResultVO();
        try{
            List<GPMSHealthCheckVO> re = healthCheckDAO.selectHealthCheckServerList(null);
            if (re != null && re.size() > 0) {
				GPMSHealthCheckVO[] row = re.stream().toArray(GPMSHealthCheckVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
        }catch (Exception ex) {
			logger.error("error in selectListRepoServerHealth : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
        return resultVO;
    }

    //레포/db 서버 등록
    @Override
    public ResultVO createHealthCheckServerList(GPMSHealthCheckVO paramVO) throws Exception {
        ResultVO resultVO = new ResultVO();
        
        try {
            healthCheckDAO.insertHealthServer(paramVO);

            if(paramVO.getTarget().equals("repo")){
                // repo 헬스체크 
                //repoHealthCheck(paramVO.getUrl());
                String url = makeRepoUrl(paramVO.getUrl(), paramVO.getDist());

                scheduleRepoCheck(paramVO.getUrl(), paramVO.getDist(), paramVO.getSchedule());
            }else if (paramVO.getTarget().equals("db")){
                // 리스트의 각 항목을 삽입
                for (GPMSHealthCheckVO.DbCheckItemsVO item : paramVO.getDbCheckItems()) {
                    item.setParentId(paramVO.getId());
                    item.setCheckQuery(HealthCheckQueryMap.getQueryByCheckItem(item.getItemName()));
                    healthCheckDAO.insertDbCheckItem(item);
                }
                scheduleDBCheck(paramVO.getUrl(), paramVO.getSchedule());
            }
            
            // TODO : error message
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERTERROR,
                    "insert health check server success."));
            
        }  catch (SQLException sqlEx) {
			logger.error("error in insertHealthServer : {}, {}, {}", GPMSConstants.CODE_INSERTERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                    "insert health check server is failed."));
		}

        return resultVO;

    }

    //레포/db 서버 삭제
    @Override
    public ResultVO deleteHealthCheckServerList(GPMSHealthCheckVO paramVO) throws Exception {
        ResultVO resultVO = new ResultVO();
        try {
            healthCheckDAO.deleteHealthServer(paramVO);
            scheduler.unschedule(paramVO.getUrl());
            // TODO : error message
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
                    "delete health check server success."));
            
            //http status check
        }  catch (Exception sqlEx) {
			logger.error("error in insertHealthServer : {}, {}, {}", GPMSConstants.CODE_DELETEERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
                    "delete health check server is failed."));
		}

        return resultVO;

    }

    //repo 서버 헬스체크
    @Override
    public void repoHealthCheck(String url, String dist) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        // for(GPMSHealthCheckVO vo : checkList){

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            int httpStatus = responseEntity.getStatusCode().value(); // Get status code as int

            GPMSHealthCheckVO vo = healthCheckDAO.selectHealthCheckServerByURL(url, dist);

            if(httpStatus == 200){
                vo.setStatus("NORMAL");
            }else{
                vo.setStatus("ERROR");
            }
            
            vo.setHttpStatus(String.valueOf(httpStatus));

            healthCheckDAO.updateServerHealth(vo);

        // }

    }

    private String makeRepoUrl(String url, String dist){
        String checkUrl;

        if (url.endsWith("/")) {
            checkUrl = url + "dists/" + dist + "/Release";
        } else {
            checkUrl = url + "/dists/" + dist + "/Release";
        }
        return checkUrl;
    }

    //repo url 체크
    @Override
    public ResultVO checkRepoServerUrl(String url, String dist) throws Exception {
        ResultVO resultVO = new ResultVO();

        // 1. RestTemplate으로 URL에 HTTP 요청 보내기
        try {
            RestTemplate restTemplate = new RestTemplate();

            String checkUrl = makeRepoUrl(url,dist);

            ResponseEntity<String> response = restTemplate.getForEntity(checkUrl, String.class);

            // 응답 상태 확인
            if (response.getStatusCode() != HttpStatus.OK) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, "unreachable url", "서버에서 응답이 없습니다."));
                return resultVO;
            }
        } catch (Exception e) {
            // 예외 발생 시 처리
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, "invalid url", "유효하지 않은 URL입니다."));
            return resultVO;
        }

        //2. 등록된 서버인지 중복 체크
        GPMSHealthCheckVO dup = healthCheckDAO.selectHealthCheckServerByURL(url, dist);

        if(dup != null && dup.getDist().equals(dist)){
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, "duplicate url", "이미 등록된 URL입니다."));
            return resultVO;
        }
        
        resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, "available url", "등록가능한 URL입니다."));

        return resultVO;
    }

    // db 헬스체크
    // MySQL: jdbc:mysql://<host>:<port>/<db>
    @Override
    public void dbHealthCheck(String url) throws Exception {

        GPMSHealthCheckVO vo = healthCheckDAO.selectHealthCheckServerByURL(url, null);

        // JDBC URL 생성
        String jdbcUrl = connectionValidator.createJdbcUrl(vo.getDbType(), vo.getUrl(), vo.getPort(), vo.getSid());

        runHealthCheck(vo, jdbcUrl, vo.getUsername(), vo.getPassword());
    }

    private String executeHealthCheckQuery(Connection connection, GPMSHealthCheckVO.DbCheckItemsVO item) {
        String resultValue = null;

        try (PreparedStatement statement = connection.prepareStatement(item.getCheckQuery());
             ResultSet resultSet = statement.executeQuery()) {

            // 쿼리 결과 처리
            // System.out.println("Health Check for item: " + item.getItemName());
            while (resultSet.next()) {
                resultValue = resultSet.getString(1);
                // System.out.println("Result for " + item.getItemName() + ": " + resultValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println("Failed to execute health check query for: " + item.getItemName());
        }
        return resultValue;
    }


    //db 서버 등록시 유효성 체크
    @Override
    public boolean checkDbServerUrl(GPMSHealthCheckVO vo) throws Exception{
        // JDBC URL 생성
        String jdbcUrl = connectionValidator.createJdbcUrl(vo.getDbType(), vo.getUrl(), vo.getPort(), vo.getSid());

        try (Connection connection = connectionValidator.createConnection(vo.getDbType(), jdbcUrl, vo.getUsername(), vo.getPassword())) {
            return connectionValidator.validateConnection(connection);
        } catch (Exception e) {
            System.out.println("Error validating DB connection: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkDbServerUrlDup(String url) throws Exception{
        // 등록된 서버인지 중복 체크
        GPMSHealthCheckVO dup = healthCheckDAO.selectHealthCheckServerByURL(url, null);

        if(dup != null){
           return false;
        }
        return true;
    }


    // DB 헬스체크
    private void runHealthCheck(GPMSHealthCheckVO vo, String jdbcUrl, String username, String password) throws Exception {
        // 커넥션 생성 및 검증
        try (Connection connection = createAndValidateConnection(vo.getDbType(), jdbcUrl, vo.getUsername(), vo.getPassword(), vo)) {

            // 항목별로 쿼리 실행
            for (GPMSHealthCheckVO.DbCheckItemsVO item : vo.getDbCheckItems()) {
                String chkResult = executeHealthCheckQuery(connection, item);
                item.setResult(chkResult);
                healthCheckDAO.updateDbItemResult(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DB 헬스체크 실패: " + e.getMessage());
        }
    }

    // 커넥션 생성 및 유효성 검증
    private Connection createAndValidateConnection(String dbType, String jdbcUrl, String username, String password, GPMSHealthCheckVO vo) throws Exception {
        Connection connection = connectionValidator.createConnection(dbType, jdbcUrl, username, password);
        if (connectionValidator.validateConnection(connection)) {
            if (vo != null) {
                vo.setConnection("SUCCESS");
                healthCheckDAO.updateServerHealth(vo);
            }
        } else {
            if (vo != null) {
                vo.setConnection("FAIL");
                healthCheckDAO.updateServerHealth(vo);
            }
            throw new Exception("DB 헬스체크 실패: 유효하지 않은 연결");
        }
        return connection;
    }

    private void scheduleRepoCheck(String url, String dist, long min) {
        scheduler.schedule(url, min, () -> {
            try {
                repoHealthCheck(url, dist);
            } catch (Exception e) {
                logger.error("error in repoHealthCheck: {}", e.getMessage());
            }
        });
    }

    private void scheduleDBCheck(String url, long min) {
        scheduler.schedule(url, min, () -> {
            try {
                dbHealthCheck(url);
            } catch (Exception e) {
                logger.error("error in dbHealthCheck: {}", e.getMessage());
            }
        });
    }
}
