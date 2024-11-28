package kr.gooroom.gpms.health.util;

import java.util.HashMap;
import java.util.Map;

// HealthCheck 쿼리를 관리하는 개별 클래스
public class HealthCheckQueryMap {

    private static final Map<String, String> healthCheckQueries = new HashMap<>();

    static {
        // memory used
        healthCheckQueries.put("Memory used", 
            "SELECT concat (truncate((@@innodb_buffer_pool_size + @@key_buffer_size + " +
            "@@innodb_log_buffer_size + @@max_connections * " +
            "(@@sort_buffer_size + @@read_buffer_size + @@binlog_cache_size + @@thread_stack)) / 1024 / 1024, 2), 'MB') AS total_memory_MB");

        // database total size
        healthCheckQueries.put("database total size", 
            "SELECT concat(ROUND(SUM(data_length+index_length)/1024/1024, 1),'MB') AS 'Used(MB)' " +
            "FROM information_schema.tables;");

        // Threads connected
        healthCheckQueries.put("Threads connected", 
            "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME = 'Threads_connected';");

        // Aborted connects
        healthCheckQueries.put("Aborted connects", 
            "SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME = 'Aborted_connects';");
    }

    public static Map<String, String> getHealthCheckQueries() {
        return healthCheckQueries;
    }

    // 개별 쿼리 조회 메서드
    public static String getQueryByCheckItem(String checkItem) {
        return healthCheckQueries.get(checkItem);
    }
    
}
