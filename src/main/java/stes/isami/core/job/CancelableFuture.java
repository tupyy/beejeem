package stes.isami.core.job;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * A wrapper class on the {@link CompletableFuture} to provides a functional cancel feature.
 * To be able to interrupt the underline thread when canceling a {@link CompletableFuture}, the user must use this class.
 */
class CancelableFuture<T> extends CompletableFuture<T> {
    private Future<?> inner;

    /**
     * Creates a new CancelableFuture which will be completed by calling the
     * given {@link Callable} via the provided {@link ExecutorService}.
     */
    public CancelableFuture(Supplier<T> task, ExecutorService executor) {
        this.inner = executor.submit(() -> complete(task));
    }

    /**
     * Completes this future by executing a {@link Callable}. If the call throws
     * an exception, the future will complete with that exception. Otherwise,
     * the future will complete with the value returned from the callable.
     */
    private void complete(Supplier<T> callable) {
        try {
            T result = callable.get();
            complete(result);
        } catch (Exception e) {
            completeExceptionally(e);
        }
    }

    @Override
    public boolean cancel(boolean mayInterrupt) {
        return inner.cancel(mayInterrupt) && super.cancel(true);
    }
}