package kr.gooroom.gpms.health.service.impl;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import kr.gooroom.gpms.health.service.HealthCheckScheduler;

@Component
public class HealthCheckSchedulerImpl implements HealthCheckScheduler {

    private ThreadPoolTaskScheduler scheduler;
    private static Map<String, ScheduledFuture<?>> map;

    public HealthCheckSchedulerImpl() {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }

        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
    }

    @Override
    public void schedule(String id, long min, Runnable runnable) {
        ScheduledFuture<?> handle = scheduler.schedule(
            runnable,
            new PeriodicTrigger(Duration.ofMinutes(min))
        );

        map.put(id, handle);
    }

    @Override
    public void unschedule(String id) {
        if (map.containsKey(id)) {
            map.get(id).cancel(false);
            map.remove(id);
        }
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
}