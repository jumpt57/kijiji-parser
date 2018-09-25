package com.github.jumpt57.kijiji.engine.executor;

import com.github.jumpt57.kijiji.engine.worker.Worker;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class AsyncExecutor<T> {

    public void start(Worker<T> worker, Consumer<T> action) {
        try {
            CompletableFuture.supplyAsync(worker::load, CachedThreadPool.INSTANCE.getExecutorService())
                    .thenAcceptAsync(action)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void start(Collection<Worker<T>> workers, Consumer<T> action) {
        try {
            CompletableFuture.allOf(
                    workers.stream()
                            .map(worker -> CompletableFuture
                                    .supplyAsync(worker::load, CachedThreadPool.INSTANCE.getExecutorService())
                                    .thenAcceptAsync(action))
                            .toArray(CompletableFuture[]::new)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
