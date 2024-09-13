package kr.gooroom.gpms.health.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnectionValidator {

    /**
     * DB 유형에 맞는 JDBC URL 생성
     * @param dbType 데이터베이스 유형 (예: mysql, postgresql 등)
     * @param host 호스트 주소
     * @param port 포트 번호
     * @param sid 데이터베이스 이름 (또는 SID)
     * @return JDBC URL
     * @throws Exception 지원하지 않는 DB 유형일 경우 예외 발생
     */
    public String createJdbcUrl(String dbType, String host, String port, String sid) throws Exception {
        switch (dbType.toLowerCase()) {
            case "mysql":
                return "jdbc:mysql://" + host + ":" + port + "/" + sid;
            case "postgresql":
                return "jdbc:postgresql://" + host + ":" + port + "/" + sid;
            case "oracle":
                return "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
            case "sqlserver":
                return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + sid;
            default:
                throw new Exception("지원하지 않는 데이터베이스 유형입니다: " + dbType);
        }
    }

    /**
     * DB 유형에 맞는 JDBC 드라이버 클래스를 로드하고, 커넥션을 생성
     * @param dbType 데이터베이스 유형 (예: mysql, postgresql 등)
     * @param jdbcUrl JDBC URL (예: jdbc:mysql://localhost:3306/db_name)
     * @param username DB 사용자명
     * @param password DB 비밀번호
     * @return Connection DB 커넥션 객체
     * @throws Exception JDBC 드라이버 로딩 또는 커넥션 생성 실패 시 예외 발생
     */
    public Connection createConnection(String dbType, String jdbcUrl, String username, String password) throws Exception {
        String driverClass;

        switch (dbType.toLowerCase()) {
            case "mysql":
                driverClass = "com.mysql.cj.jdbc.Driver";
                break;
            case "postgresql":
                driverClass = "org.postgresql.Driver";
                break;
            case "oracle":
                driverClass = "oracle.jdbc.driver.OracleDriver";
                break;
            case "sqlserver":
                driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                break;
            default:
                throw new Exception("지원하지 않는 데이터베이스 유형입니다: " + dbType);
        }

        // JDBC 드라이버 로드
        Class.forName(driverClass);

        // DB 커넥션 생성
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        // Statement 타임아웃 설정 (초 단위)
        connection.createStatement().setQueryTimeout(5); // 5초 타임아웃

        return connection;
    }

    /**
     * DB 커넥션을 검증하기 위해 간단한 쿼리 (SELECT 1) 실행
     * @param connection DB 커넥션 객체
     * @return 검증 결과 (성공: true, 실패: false)
     * @throws Exception 쿼리 실행 실패 시 예외 발생
     */
    public boolean validateConnection(Connection connection) throws Exception {
        String validationQuery = "SELECT 1";

        try (PreparedStatement statement = connection.prepareStatement(validationQuery);
             ResultSet resultSet = statement.executeQuery()) {

            // 쿼리 결과 처리
            return resultSet.next() && resultSet.getInt(1) == 1;
        } catch (Exception e) {
            throw new Exception("DB 커넥션 유효성 검증 실패: " + e.getMessage());
        }
    }
}

