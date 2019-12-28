/*
 * Copyright (C) 2017 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.landawn.abacus.android.util;

import com.landawn.abacus.android.util.Async.UIExecutor;
import com.landawn.abacus.android.util.Observer.ViewObserver;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.Throwables;
import com.landawn.abacus.util.Tuple;
import com.landawn.abacus.util.Tuple.Tuple5;
import com.landawn.abacus.util.function.Consumer;

import android.view.View;
import android.view.View.OnScrollChangeListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ObserverX.
 *
 * @author Haiyang Li
 * @param <T>
 * @since 0.9
 */
public abstract class ObserverX<T> {

    /**
     * Instantiates a new observer X.
     */
    ObserverX() {

    }

    /**
     *
     * @param <T>
     * @param <O>
     * @param view
     * @return
     */
    public static <T extends View, O extends ViewObserverX<T, O>> ViewObserverX<T, O> of(final T view) {
        return new ViewObserverX<>(view);
    }

    /**
     * The Class ViewObserverX.
     *
     * @param <T>
     * @param <O>
     */
    public static class ViewObserverX<T extends View, O extends ViewObserverX<T, O>> extends ViewObserver<T, ViewObserverX<T, O>> {

        /**
         * Instantiates a new view observer X.
         *
         * @param view
         */
        ViewObserverX(T view) {
            super(view);
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @return
         */
        public Disposable onScrollChange(final OnScrollChangeListener onNext) {
            return onScrollChange(onNext, CommonUtils.ON_ERROR_MISSING);
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @param onError
         * @return
         */
        public Disposable onScrollChange(final OnScrollChangeListener onNext, final Consumer<? super Exception> onError) {
            return onScrollChange(onNext, onError, CommonUtils.EMPTY_ACTION);
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @param onError
         * @param onComplete
         * @return
         */
        public Disposable onScrollChange(final OnScrollChangeListener onNext, final Consumer<? super Exception> onError, final Runnable onComplete) {
            N.checkArgNotNull(onNext, "onNext");
            N.checkArgNotNull(onError, "onError");
            N.checkArgNotNull(onComplete, "onComplete");

            dispatcher.append(new DispatcherBase<Object>(onError, onComplete) {
                @Override
                public void onNext(Object param) {
                    final Tuple5<View, Integer, Integer, Integer, Integer> tmp = (Tuple5<View, Integer, Integer, Integer, Integer>) param;

                    if (CommonUtils.isUiThread()) {
                        onNext.onScrollChange(tmp._1, tmp._2, tmp._3, tmp._4, tmp._5);
                    } else {
                        UIExecutor.execute(new Throwables.Runnable<RuntimeException>() {
                            @Override
                            public void run() {
                                onNext.onScrollChange(tmp._1, tmp._2, tmp._3, tmp._4, tmp._5);
                            }
                        });
                    }
                }
            });

            _view.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    dispatcher.onNext(Tuple.of(v, scrollX, scrollY, oldScrollX, oldScrollY));
                }
            });

            disposeActions.add(new Runnable() {
                @Override
                public void run() {
                    _view.setOnScrollChangeListener(null);
                }
            });

            return this;
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @return
         */
        public Disposable onScrollChange(final Consumer<? super Tuple5<View, Integer, Integer, Integer, Integer>> onNext) {
            return onScrollChange(onNext, CommonUtils.ON_ERROR_MISSING);
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @param onError
         * @return
         */
        public Disposable onScrollChange(final Consumer<? super Tuple5<View, Integer, Integer, Integer, Integer>> onNext,
                final Consumer<? super Exception> onError) {
            return onScrollChange(onNext, onError, CommonUtils.EMPTY_ACTION);
        }

        /**
         * On scroll change.
         *
         * @param onNext
         * @param onError
         * @param onComplete
         * @return
         */
        public Disposable onScrollChange(final Consumer<? super Tuple5<View, Integer, Integer, Integer, Integer>> onNext,
                final Consumer<? super Exception> onError, final Runnable onComplete) {
            N.checkArgNotNull(onNext, "onNext");
            N.checkArgNotNull(onError, "onError");
            N.checkArgNotNull(onComplete, "onComplete");

            dispatcher.append(new DispatcherBase<Object>(onError, onComplete) {
                @Override
                public void onNext(Object param) {
                    final Tuple5<View, Integer, Integer, Integer, Integer> tmp = (Tuple5<View, Integer, Integer, Integer, Integer>) param;

                    if (CommonUtils.isUiThread()) {
                        onNext.accept(tmp);
                    } else {
                        UIExecutor.execute(new Throwables.Runnable<RuntimeException>() {
                            @Override
                            public void run() {
                                onNext.accept(tmp);
                            }
                        });
                    }
                }
            });

            _view.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    dispatcher.onNext(Tuple.of(v, scrollX, scrollY, oldScrollX, oldScrollY));
                }
            });

            disposeActions.add(new Runnable() {
                @Override
                public void run() {
                    _view.setOnScrollChangeListener(null);
                }
            });

            return this;
        }
    }
}
