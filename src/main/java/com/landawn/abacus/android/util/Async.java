/*
 * Copyright (c) 2015, Haiyang Li.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.landawn.abacus.android.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.landawn.abacus.util.MoreExecutors;
import com.landawn.abacus.util.Retry;
import com.landawn.abacus.util.Throwables;
import com.landawn.abacus.util.function.BiPredicate;
import com.landawn.abacus.util.function.Predicate;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

// TODO: Auto-generated Javadoc
/**
 *
 * @author Haiyang Li
 * @since 0.8
 */
public class Async {

    /** The Constant SCHEDULED_EXECUTOR. */
    static final ScheduledExecutorService SCHEDULED_EXECUTOR;
    static {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8);
        executor.setKeepAliveTime(180, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        executor.setRemoveOnCancelPolicy(true);
        SCHEDULED_EXECUTOR = MoreExecutors.getExitingScheduledExecutorService(executor);
    }

    /** The Constant _UI_EXECUTOR. */
    static final _UIExecutor _UI_EXECUTOR = new _UIExecutor();

    /** The Constant SERIAL_EXECUTOR. */
    public static final Executor SERIAL_EXECUTOR = AsyncTask.SERIAL_EXECUTOR;

    /** The Constant TP_EXECUTOR. */
    public static final Executor TP_EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;

    /** The Constant UI_EXECUTOR. */
    public static final Executor UI_EXECUTOR = _UI_EXECUTOR;

    /**
     * Instantiates a new async.
     */
    private Async() {
        // Singleton
    }

    /**
     * The action will be asynchronously executed with {@code android.io.AsyncTask#SERIAL_EXECUTOR} in background.
     *
     * @param action
     * @return
     */
    static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action) {
        return execute(new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                action.run();
                return null;
            }
        }), SERIAL_EXECUTOR);
    }

    /**
     *
     * @param action
     * @param delayInMillis
     * @return
     */
    static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
        final Callable<ContinuableFuture<Void>> scheduledAction = new Callable<ContinuableFuture<Void>>() {
            @Override
            public ContinuableFuture<Void> call() throws Exception {
                return Async.execute(action);
            }
        };

        final ScheduledFuture<ContinuableFuture<Void>> scheduledFuture = SCHEDULED_EXECUTOR.schedule(scheduledAction, delayInMillis, TimeUnit.MILLISECONDS);

        return new ContinuableFuture<>(wrap(scheduledFuture), null, SERIAL_EXECUTOR);
    }

    /**
     *
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final int retryTimes, final long retryIntervalInMillis,
            final Predicate<? super Exception> retryCondition) {
        return execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Retry.of(retryTimes, retryIntervalInMillis, retryCondition).run(action);
                return null;
            }
        });
    }

    /**
     * The action will be asynchronously executed with {@code android.io.AsyncTask#SERIAL_EXECUTOR} in background.
     *
     * @param <T>
     * @param action
     * @return
     */
    static <T> ContinuableFuture<T> execute(final Callable<T> action) {
        return execute(new FutureTask<>(action), SERIAL_EXECUTOR);
    }

    /**
     *
     * @param <T>
     * @param action
     * @param delayInMillis
     * @return
     */
    static <T> ContinuableFuture<T> execute(final Callable<T> action, final long delayInMillis) {
        final Callable<ContinuableFuture<T>> scheduledAction = new Callable<ContinuableFuture<T>>() {
            @Override
            public ContinuableFuture<T> call() throws Exception {
                return Async.execute(action);
            }
        };

        final ScheduledFuture<ContinuableFuture<T>> scheduledFuture = SCHEDULED_EXECUTOR.schedule(scheduledAction, delayInMillis, TimeUnit.MILLISECONDS);

        return new ContinuableFuture<>(wrap(scheduledFuture), null, SERIAL_EXECUTOR);
    }

    /**
     *
     * @param <T>
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static <T> ContinuableFuture<T> execute(final Callable<T> action, final int retryTimes, final long retryIntervalInMillis,
            final BiPredicate<? super T, ? super Exception> retryCondition) {
        return execute(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final Retry<T> retry = Retry.of(retryTimes, retryIntervalInMillis, retryCondition);
                return retry.call(action);
            }
        });
    }

    /**
     * The action will be asynchronously executed with {@code android.io.AsyncTask#THREAD_POOL_EXECUTOR} in background.
     *
     * @param action
     * @return
     */
    static ContinuableFuture<Void> executeWithThreadPool(final Throwables.Runnable<? extends Exception> action) {
        return execute(new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                action.run();
                return null;
            }
        }), TP_EXECUTOR);
    }

    /**
     * Execute with thread pool.
     *
     * @param action
     * @param delayInMillis
     * @return
     */
    static ContinuableFuture<Void> executeWithThreadPool(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
        final Callable<ContinuableFuture<Void>> scheduledAction = new Callable<ContinuableFuture<Void>>() {
            @Override
            public ContinuableFuture<Void> call() throws Exception {
                return Async.executeWithThreadPool(action);
            }
        };

        final ScheduledFuture<ContinuableFuture<Void>> scheduledFuture = SCHEDULED_EXECUTOR.schedule(scheduledAction, delayInMillis, TimeUnit.MILLISECONDS);

        return new ContinuableFuture<>(wrap(scheduledFuture), null, TP_EXECUTOR);
    }

    /**
     * Execute with thread pool.
     *
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static ContinuableFuture<Void> executeWithThreadPool(final Throwables.Runnable<? extends Exception> action, final int retryTimes,
            final long retryIntervalInMillis, final Predicate<? super Exception> retryCondition) {
        return execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Retry.of(retryTimes, retryIntervalInMillis, retryCondition).run(action);
                return null;
            }
        });
    }

    /**
     * The action will be asynchronously executed with {@code android.io.AsyncTask#THREAD_POOL_EXECUTOR} in background.
     *
     * @param <T>
     * @param action
     * @return
     */
    static <T> ContinuableFuture<T> executeWithThreadPool(final Callable<T> action) {
        return execute(new FutureTask<>(action), TP_EXECUTOR);
    }

    /**
     * Execute with thread pool.
     *
     * @param <T>
     * @param action
     * @param delayInMillis
     * @return
     */
    static <T> ContinuableFuture<T> executeWithThreadPool(final Callable<T> action, final long delayInMillis) {
        final Callable<ContinuableFuture<T>> scheduledAction = new Callable<ContinuableFuture<T>>() {
            @Override
            public ContinuableFuture<T> call() throws Exception {
                return Async.executeWithThreadPool(action);
            }
        };

        final ScheduledFuture<ContinuableFuture<T>> scheduledFuture = SCHEDULED_EXECUTOR.schedule(scheduledAction, delayInMillis, TimeUnit.MILLISECONDS);

        return new ContinuableFuture<>(wrap(scheduledFuture), null, TP_EXECUTOR);
    }

    /**
     * Execute with thread pool.
     *
     * @param <T>
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static <T> ContinuableFuture<T> executeWithThreadPool(final Callable<T> action, final int retryTimes, final long retryIntervalInMillis,
            final BiPredicate<? super T, ? super Exception> retryCondition) {
        return executeWithThreadPool(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final Retry<T> retry = Retry.of(retryTimes, retryIntervalInMillis, retryCondition);
                return retry.call(action);
            }
        });
    }

    /**
     * The action will be asynchronously executed in UI thread.
     *
     * @param action
     * @return
     */
    static ContinuableFuture<Void> executeOnUiThread(final Throwables.Runnable<? extends Exception> action) {
        return executeOnUiThread(action, 0);
    }

    /**
     * The action will be asynchronously executed in UI thread.
     *
     * @param action
     * @param delayInMillis
     * @return
     */
    static ContinuableFuture<Void> executeOnUiThread(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
        return execute(new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                action.run();
                return null;
            }
        }), _UI_EXECUTOR, delayInMillis);
    }

    /**
     * Execute on ui thread.
     *
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static ContinuableFuture<Void> executeOnUiThread(final Throwables.Runnable<? extends Exception> action, final int retryTimes,
            final long retryIntervalInMillis, final Predicate<? super Exception> retryCondition) {
        return execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Retry.of(retryTimes, retryIntervalInMillis, retryCondition).run(action);
                return null;
            }
        });
    }

    /**
     * The action will be asynchronously executed in UI thread.
     *
     * @param <T>
     * @param action
     * @return
     */
    static <T> ContinuableFuture<T> executeOnUiThread(final Callable<T> action) {
        return executeOnUiThread(action, 0);
    }

    /**
     * The action will be asynchronously executed in UI thread.
     *
     * @param <T>
     * @param action
     * @param delayInMillis
     * @return
     */
    static <T> ContinuableFuture<T> executeOnUiThread(final Callable<T> action, final long delayInMillis) {
        return execute(new FutureTask<>(action), _UI_EXECUTOR, delayInMillis);
    }

    /**
     * Execute on ui thread.
     *
     * @param <T>
     * @param action
     * @param retryTimes
     * @param retryIntervalInMillis the retry interval
     * @param retryCondition
     * @return
     */
    static <T> ContinuableFuture<T> executeOnUiThread(final Callable<T> action, final int retryTimes, final long retryIntervalInMillis,
            final BiPredicate<? super T, ? super Exception> retryCondition) {
        return executeOnUiThread(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final Retry<T> retry = Retry.of(retryTimes, retryIntervalInMillis, retryCondition);
                return retry.call(action);
            }
        });
    }

    /**
     *
     * @param <T>
     * @param futureTask
     * @param executor
     * @return
     */
    private static <T> ContinuableFuture<T> execute(final FutureTask<T> futureTask, final Executor executor) {
        executor.execute(futureTask);

        return new ContinuableFuture<>(futureTask, null, executor);
    }

    /**
     *
     * @param <T>
     * @param futureTask
     * @param executor
     * @param delayInMillis
     * @return
     */
    private static <T> ContinuableFuture<T> execute(final FutureTask<T> futureTask, final _UIExecutor executor, final long delayInMillis) {
        executor.execute(futureTask, delayInMillis);

        return new ContinuableFuture<>(futureTask, null, executor);
    }

    /**
     *
     * @param <T>
     * @param scheduledFuture
     * @return
     */
    private static <T> Future<T> wrap(final ScheduledFuture<ContinuableFuture<T>> scheduledFuture) {
        return new Future<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (scheduledFuture.cancel(mayInterruptIfRunning) && scheduledFuture.isDone()) {
                    try {
                        final ContinuableFuture<T> resFuture = scheduledFuture.get();
                        return resFuture == null || resFuture.cancel(mayInterruptIfRunning);
                    } catch (Exception e) {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public boolean isCancelled() {
                if (scheduledFuture.isCancelled() && scheduledFuture.isDone()) {
                    try {
                        final ContinuableFuture<T> resFuture = scheduledFuture.get();
                        return resFuture == null || resFuture.isCancelled();
                    } catch (Exception e) {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public boolean isDone() {
                if (scheduledFuture.isDone()) {
                    try {
                        final ContinuableFuture<T> resFuture = scheduledFuture.get();
                        return resFuture == null || resFuture.isDone();
                    } catch (Exception e) {
                        return false;
                    }
                }

                return false;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                final ContinuableFuture<T> resFuture = scheduledFuture.get();
                return resFuture == null ? null : resFuture.get();
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                final long beginTime = System.currentTimeMillis();

                final ContinuableFuture<T> resFuture = scheduledFuture.get(timeout, unit);

                final long remainingTimeout = unit.toMillis(timeout) - (System.currentTimeMillis() - beginTime);

                return resFuture == null ? null : (remainingTimeout > 0 ? resFuture.get(remainingTimeout, TimeUnit.MILLISECONDS) : resFuture.get());
            }
        };
    }

    /**
     * The Class _UIExecutor.
     */
    static final class _UIExecutor implements Executor {

        /** The Constant HANDLER. */
        private static final Handler HANDLER = new Handler(Looper.getMainLooper());

        /**
         * Instantiates a new UI executor.
         */
        private _UIExecutor() {
            // Singleton.
        }

        /**
         *
         * @param command
         */
        @Override
        public void execute(Runnable command) {
            HANDLER.post(command);
        }

        /**
         *
         * @param command
         * @param delayInMillis
         */
        public void execute(Runnable command, final long delayInMillis) {
            if (delayInMillis > 0) {
                HANDLER.postDelayed(command, delayInMillis);
            } else {
                HANDLER.post(command);
            }
        }
    }

    /**
     * The Class SerialExecutor.
     */
    public static final class SerialExecutor {

        /**
         * Instantiates a new serial executor.
         */
        private SerialExecutor() {
            // singleton
        }

        /**
         * The action will be asynchronously executed with {@code android.io.AsyncTask#SERIAL_EXECUTOR} in background.
         *
         * @param action
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action) {
            return Async.execute(action);
        }

        /**
         *
         * @param action
         * @param delayInMillis
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
            return Async.execute(action, delayInMillis);
        }

        /**
         *
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final int retryTimes,
                final long retryIntervalInMillis, final Predicate<? super Exception> retryCondition) {
            return Async.execute(action, retryTimes, retryIntervalInMillis, retryCondition);

        }

        /**
         * The action will be asynchronously executed with {@code android.io.AsyncTask#SERIAL_EXECUTOR} in background.
         *
         * @param <T>
         * @param action
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action) {
            return Async.execute(action);
        }

        /**
         *
         * @param <T>
         * @param action
         * @param delayInMillis
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final long delayInMillis) {
            return Async.execute(action, delayInMillis);
        }

        /**
         *
         * @param <T>
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final int retryTimes, final long retryIntervalInMillis,
                final BiPredicate<? super T, ? super Exception> retryCondition) {
            return Async.execute(action, retryTimes, retryIntervalInMillis, retryCondition);
        }
    }

    /**
     * The Class TPExecutor.
     */
    public static final class TPExecutor {

        /**
         * Instantiates a new TP executor.
         */
        private TPExecutor() {
            // singleton
        }

        /**
         * The action will be asynchronously executed with {@code android.io.AsyncTask#THREAD_POOL_EXECUTOR} in background.
         *
         * @param action
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action) {
            return Async.executeWithThreadPool(action);
        }

        /**
         *
         * @param action
         * @param delayInMillis
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
            return Async.executeWithThreadPool(action, delayInMillis);
        }

        /**
         *
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final int retryTimes,
                final long retryIntervalInMillis, final Predicate<? super Exception> retryCondition) {
            return Async.executeWithThreadPool(action, retryTimes, retryIntervalInMillis, retryCondition);

        }

        /**
         * The action will be asynchronously executed with {@code android.io.AsyncTask#THREAD_POOL_EXECUTOR} in background.
         *
         * @param <T>
         * @param action
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action) {
            return Async.executeWithThreadPool(action);
        }

        /**
         *
         * @param <T>
         * @param action
         * @param delayInMillis
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final long delayInMillis) {
            return Async.executeWithThreadPool(action, delayInMillis);
        }

        /**
         *
         * @param <T>
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final int retryTimes, final long retryIntervalInMillis,
                final BiPredicate<? super T, ? super Exception> retryCondition) {
            return Async.executeWithThreadPool(action, retryTimes, retryIntervalInMillis, retryCondition);
        }
    }

    /**
     * The Class UIExecutor.
     */
    public static final class UIExecutor {

        /**
         * Instantiates a new UI executor.
         */
        private UIExecutor() {
            // singleton
        }

        /**
         * The action will be asynchronously executed in UI thread.
         *
         * @param action
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action) {
            return Async.executeOnUiThread(action);
        }

        /**
         * The action will be asynchronously executed in UI thread.
         *
         * @param action
         * @param delayInMillis unit is milliseconds
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final long delayInMillis) {
            return Async.executeOnUiThread(action, delayInMillis);
        }

        /**
         *
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static ContinuableFuture<Void> execute(final Throwables.Runnable<? extends Exception> action, final int retryTimes,
                final long retryIntervalInMillis, final Predicate<? super Exception> retryCondition) {
            return Async.executeOnUiThread(action, retryTimes, retryIntervalInMillis, retryCondition);
        }

        /**
         * The action will be asynchronously executed in UI thread.
         *
         * @param <T>
         * @param action
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action) {
            return Async.executeOnUiThread(action);
        }

        /**
         * The action will be asynchronously executed in UI thread.
         *
         * @param <T>
         * @param action
         * @param delayInMillis
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final long delayInMillis) {
            return Async.executeOnUiThread(action, delayInMillis);
        }

        /**
         *
         * @param <T>
         * @param action
         * @param retryTimes
         * @param retryIntervalInMillisInMillis the retry interval
         * @param retryCondition
         * @return
         */
        public static <T> ContinuableFuture<T> execute(final Callable<T> action, final int retryTimes, final long retryIntervalInMillisInMillis,
                final BiPredicate<? super T, ? super Exception> retryCondition) {
            return Async.executeOnUiThread(action, retryTimes, retryIntervalInMillisInMillis, retryCondition);
        }
    }

    //    /**
    //     * Short name for AsyncExecutor
    //     *
    //     * @deprecated replaced with SerialExecutor/TPExecutor/UIExecutor.
    //     */
    //    @Deprecated
    //    @Beta
    //    public static class Asyn extends AsyncExecutor {
    //        private Asyn() {
    //            // singleton
    //        }
    //    }
}
