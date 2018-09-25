package com.github.jumpt57.kijiji.engine.executor;

import com.github.jumpt57.kijiji.engine.worker.Worker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class SyncExecutor<T> {

    public T start(Worker<T> worker) {
        Callable<T> task = worker::load;
        try {
            return CachedThreadPool.INSTANCE.getExecutorService().submit(() -> task).get().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<T> start(Iterable<Worker<T>> workers) {
        Collection<Callable<T>> tasks = new HashSet<>();
        workers.forEach(worker -> {
            Callable<T> task = worker::load;
            tasks.add(task);
        });

        try {
            return CachedThreadPool.INSTANCE.getExecutorService().invokeAll(tasks)
                    .parallelStream()
                    .filter(Future::isDone)
                    .map(flatResult)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emptySet();
    }

    private final Function<Future<T>, Optional<T>> flatResult = future -> {
        try {
            return ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            return empty();
        }
    };

}
