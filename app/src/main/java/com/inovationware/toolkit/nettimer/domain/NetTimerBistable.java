package com.inovationware.toolkit.nettimer.domain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class NetTimerBistable {
    private final NetTimerNotifierObject regular;
    private final NetTimerNotifierObject reverse;
    public boolean repeat;

    private ScheduledExecutorService regularService;
    public ScheduledExecutorService reverseService;

    public NetTimerBistable(NetTimerNotifierObject regular, NetTimerNotifierObject reverse, boolean repeat) {
        this.regular = regular;
        this.reverse = reverse;
        this.repeat = repeat;

        this.regular.setRegular(true);
        this.reverse.setRegular(false);

        this.regular.setBistable(this);
        this.reverse.setBistable(this);

    }

    public void start(){
        startRegular();
    }

    public void startRegular(){
        this.regularService = Executors.newSingleThreadScheduledExecutor();
        regularService.scheduleAtFixedRate(regular,regular.getInterval(), regular.getInterval(), regular.getTimeUnit());
    }

    public void startReverse(){
        this.reverseService = Executors.newSingleThreadScheduledExecutor();
        reverseService.scheduleAtFixedRate(reverse,reverse.getInterval(), reverse.getInterval() , reverse.getTimeUnit());
    }

    public void cancel() {
        this.repeat = false;
        cancelRegular();
        cancelReverse();
    }

    public void cancelRegular() {
        regularService.shutdownNow();
        if (!regularService.isShutdown()) {
            regularService.shutdown();
            try {
                if (!regularService.awaitTermination(reverse.getInterval() / 2, TimeUnit.SECONDS)) {
                    regularService.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void cancelReverse() {
        reverseService.shutdownNow();
        if (!reverseService.isShutdown()) {
            reverseService.shutdown();
            try {
                if (!reverseService.awaitTermination(regular.getInterval() / 2, TimeUnit.SECONDS)) {
                    reverseService.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
