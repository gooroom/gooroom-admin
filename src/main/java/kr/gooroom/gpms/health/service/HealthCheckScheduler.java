package kr.gooroom.gpms.health.service;

public interface HealthCheckScheduler {
    public void schedule(String id, long min, Runnable runnable);
    public void unschedule(String id);
    public void shutdown();
    public boolean isEmpty();
}