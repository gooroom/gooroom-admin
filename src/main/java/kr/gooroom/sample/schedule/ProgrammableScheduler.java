package kr.gooroom.sample.schedule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.sample.schedule.service.MigSampleService;

@Component
public class ProgrammableScheduler {
	
	@Resource(name = "migSampleService")
	private MigSampleService migSampleService;

	
    private ThreadPoolTaskScheduler scheduler;
 
    public void stopScheduler() {
    	if(scheduler != null) {
            scheduler.shutdown();    		
    	}
    }
 
    public void startScheduler(int period) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        // 스케쥴러가 시작되는 부분 
        scheduler.schedule(getRunnable(), getTrigger(period));
    }
 
    private Runnable getRunnable(){
        return () -> {
            // do something 
        	System.out.println("########### TRIGGER ###########");
            System.out.println(new Date());
            
            try {
				StatusVO status = migSampleService.createMigProcess("0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        };
    }
 
    private Trigger getTrigger(int period) {
        // 작업 주기 설정 
        return new PeriodicTrigger(period, TimeUnit.SECONDS);
    }
}
