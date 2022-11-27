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
        executorService = new ScheduledThreadPoolExecutor(100);
        running = new ArrayList<>();
        String ffKey = System.getenv("FF_KEY");

    }

    public void startAll()  {
        log.info("[Metric Automatic Behavior] Starting");
        running.add(executorService.scheduleAtFixedRate(
                new MetricsGenerator( ), 0, 1,
                TimeUnit.MINUTES));
        log.info("[Metric Automatic Behavior] Started");
    }





}
