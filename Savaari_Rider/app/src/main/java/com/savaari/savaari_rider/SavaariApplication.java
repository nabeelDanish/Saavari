package com.savaari.savaari_rider;

import android.app.Application;

import com.savaari.savaari_rider.services.location.LocationUpdateUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SavaariApplication extends Application {
    public ExecutorService executorService;
    public Repository repository;
    public ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(6);

    public SavaariApplication() {
        executorService = Executors.newFixedThreadPool(4);
        repository = new Repository(executorService);
        LocationUpdateUtil.setRepository(repository);
    }

    public Repository getRepository() { return repository; }
}
