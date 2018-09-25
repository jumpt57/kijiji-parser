package com.github.jumpt57.kijiji.engine.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum CachedThreadPool {

    INSTANCE;

    private ExecutorService executorService;

    CachedThreadPool() {
        executorService = Executors.newCachedThreadPool();
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

}
