package kr.gooroom.sample.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgrammableSchedulerRunner {
    @Autowired
    ProgrammableScheduler scheduler;
 
    public void runSchedule(int period){
        // called by somewhere 
        scheduler.startScheduler(period);
    }
    
    public void stopSchedule(){
        // called by somewhere 
        scheduler.stopScheduler();
    }
}

