package com.inovationware.toolkit.bistable.verb;

import static com.inovationware.toolkit.global.domain.Strings.bistable;
import static com.inovationware.toolkit.global.domain.Strings.netTimerMobileServiceIsRunning;
import static com.inovationware.toolkit.global.domain.Strings.ttsServiceProvider;

import android.content.Context;

import com.inovationware.toolkit.bistable.service.BistableManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BistableCommand {
    private final BistableNotifier regular;
    private final BistableNotifier reverse;
    public boolean repeat;

    private ScheduledExecutorService regularService;
    public ScheduledExecutorService reverseService;

    public BistableCommand(BistableNotifier regular, BistableNotifier reverse, boolean repeat) {
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
        regularService.scheduleWithFixedDelay(regular,regular.getInterval(), regular.getInterval(), regular.getTimeUnit());
    }

    public void startReverse(){
        this.reverseService = Executors.newSingleThreadScheduledExecutor();
        reverseService.scheduleWithFixedDelay(reverse,reverse.getInterval(), reverse.getInterval() , reverse.getTimeUnit());
    }

    public void cancel() {
        this.repeat = false;
        cancelRegular();
        cancelReverse();
        netTimerMobileServiceIsRunning = false;
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
