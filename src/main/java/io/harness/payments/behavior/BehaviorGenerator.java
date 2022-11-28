package io.harness.payments.behavior;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;




import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BehaviorGenerator {
    private ScheduledExecutorService executorService;
    private List<ScheduledFuture> running;



    public void init()  {
        log.debug("[Metric Automatic Behavior] Init");
        executorService = new ScheduledThreadPoolExecutor(100);
        running = new ArrayList<>();

        startAll();

        //String ffKey = System.getenv("FF_KEY");

    }

    public void startAll()  {
        log.debug("[Metric Automatic Behavior] Starting");
        try {
            running.add(executorService.scheduleAtFixedRate(
                    new MetricsGenerator( ), 0, 1,
                    TimeUnit.MINUTES));
        }catch (Exception e){
            log.error("Metric Generator Error - Unknown");
        }

        log.debug("[Metric Automatic Behavior] Started");
    }

    public void stopAll() {
        running.forEach(future -> future.cancel(true));
        running.clear();
    }




}
