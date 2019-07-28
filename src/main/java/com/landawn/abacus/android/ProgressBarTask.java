/*
 * Copyright (C) 2016 HaiYang Li
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

package com.landawn.abacus.android;

import java.util.concurrent.Callable;

import com.landawn.abacus.android.util.Async.UIExecutor;
import com.landawn.abacus.android.util.ContinuableFuture;
import com.landawn.abacus.util.Multiset;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

// TODO: Auto-generated Javadoc
/**
 * Designed to show progress bar easily for network or other heavy operation:
 * <pre>
 * <code>
 *         final ProgressBarTask progressBarTask = ProgressBarTask.display(this, 1000, 0xFFE0E0E0);
 *         final LoginRequest request = ...;
 * 
 *         AsyncExecutor.executeInParallel(() -> accountService.login(request))
 *             .callbackOnUiThread((e, resp) -> {
 *                 displayProgressBarTask.finish();
 * 
 *                 if (resp != null && resp.getRespCode() == ResponseCode.OK) {
 *                     // TODO ...
 *                 } else {// TODO ...
 *                 }
 *             });
 * </code>
 * </pre>
 *
 * @author haiyangl
 * @since 0.8
 */
public class ProgressBarTask {

    /** The Constant activeProgressBarSet. */
    private static final Multiset<ViewGroup> activeProgressBarSet = new Multiset<>();

    /** The max progress bar task. */
    private static volatile int maxProgressBarTask = Integer.MAX_VALUE;

    /** The max progress bar task per view. */
    private static volatile int maxProgressBarTaskPerView = Integer.MAX_VALUE;

    /** The future. */
    protected final ContinuableFuture<ProgressBar> future;

    /** The progress bar. */
    protected ProgressBar progressBar;

    /**
     * Instantiates a new progress bar task.
     *
     * @param root the root
     * @param delay the delay
     * @param circleColor the circle color
     */
    public ProgressBarTask(final ViewGroup root, final long delay, final int circleColor) {
        future = UIExecutor.execute(new Callable<ProgressBar>() {
            @Override
            public ProgressBar call() {
                synchronized (ProgressBarTask.this) {
                    return ProgressBarTask.this.progressBar = (future.isCancelled() || activeProgressBarTaskCount() >= maxProgressBarTask
                            || activeProgressBarTaskCount(root) >= maxProgressBarTaskPerView) ? null : createProgressBar(root, circleColor);
                }
            }
        }, delay);
    }

    /**
     * Sets the max progress bar task.
     *
     * @param maxProgressBarTask the new max progress bar task
     */
    public static void setMaxProgressBarTask(int maxProgressBarTask) {
        ProgressBarTask.maxProgressBarTask = maxProgressBarTask;
    }

    /**
     * Sets the max progress bar task per view.
     *
     * @param maxProgressBarTaskPerView the new max progress bar task per view
     */
    public static void setMaxProgressBarTaskPerView(int maxProgressBarTaskPerView) {
        ProgressBarTask.maxProgressBarTaskPerView = maxProgressBarTaskPerView;
    }

    /**
     * Display.
     *
     * @param activity the activity
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Activity activity) {
        return display(activity, 0);
    }

    /**
     * Display.
     *
     * @param activity the activity
     * @param delay the delay
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Activity activity, final long delay) {
        return display(activity, delay, Integer.MIN_VALUE);
    }

    /**
     * Display.
     *
     * @param activity the activity
     * @param delay the delay
     * @param circleColor the circle color
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Activity activity, final long delay, final int circleColor) {
        return display((ViewGroup) activity.getWindow().getDecorView(), delay, circleColor);
    }

    /**
     * Display.
     *
     * @param dialog the dialog
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Dialog dialog) {
        return display(dialog, 0);
    }

    /**
     * Display.
     *
     * @param dialog the dialog
     * @param delay the delay
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Dialog dialog, final long delay) {
        return display(dialog, delay, Integer.MIN_VALUE);
    }

    /**
     * Display.
     *
     * @param dialog the dialog
     * @param delay the delay
     * @param circleColor the circle color
     * @return the progress bar task
     */
    public static ProgressBarTask display(final Dialog dialog, final long delay, final int circleColor) {
        return display((ViewGroup) dialog.getWindow().getDecorView(), delay, circleColor);
    }

    /**
     * Display.
     *
     * @param root the root
     * @return the progress bar task
     */
    public static ProgressBarTask display(final ViewGroup root) {
        return display(root, 0);
    }

    /**
     * Display.
     *
     * @param root the root
     * @param delay the delay
     * @return the progress bar task
     */
    public static ProgressBarTask display(final ViewGroup root, final long delay) {
        return display(root, delay, Integer.MIN_VALUE);
    }

    /**
     * Display.
     *
     * @param root the root
     * @param delay the delay
     * @param circleColor the circle color
     * @return the progress bar task
     */
    public static ProgressBarTask display(final ViewGroup root, final long delay, final int circleColor) {
        return new ProgressBarTask(root, delay, circleColor);
    }

    /**
     * Active progress bar task count.
     *
     * @return the int
     */
    static int activeProgressBarTaskCount() {
        synchronized (activeProgressBarSet) {
            return (int) activeProgressBarSet.sumOfOccurrences();
        }
    }

    /**
     * Active progress bar task count.
     *
     * @param activity the activity
     * @return the int
     */
    static int activeProgressBarTaskCount(Activity activity) {
        synchronized (activeProgressBarSet) {
            return activeProgressBarSet.get(activity.getWindow().getDecorView());
        }
    }

    /**
     * Active progress bar task count.
     *
     * @param dialog the dialog
     * @return the int
     */
    static int activeProgressBarTaskCount(Dialog dialog) {
        synchronized (activeProgressBarSet) {
            return activeProgressBarSet.get(dialog.getWindow().getDecorView());
        }
    }

    /**
     * Active progress bar task count.
     *
     * @param root the root
     * @return the int
     */
    static int activeProgressBarTaskCount(ViewGroup root) {
        synchronized (activeProgressBarSet) {
            return activeProgressBarSet.get(root);
        }
    }

    /**
     * Finish.
     */
    public void finish() {
        synchronized (this) {
            try {
                future.cancel(true);
            } catch (Exception e) {
                // ignore.
            } finally {
                if (progressBar != null) {
                    ViewParent parent = progressBar.getParent();

                    if (parent instanceof ViewGroup) {
                        final ViewGroup root = (ViewGroup) parent;
                        root.removeView(progressBar);

                        synchronized (activeProgressBarSet) {
                            activeProgressBarSet.remove(root);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the progress bar.
     *
     * @param root the root
     * @param circleColor the circle color
     * @return the progress bar
     */
    protected ProgressBar createProgressBar(final ViewGroup root, final int circleColor) {
        // Create layout params expecting to be added to a frame layout.
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        lp.width = lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        // Create a progress bar to be added to the window.
        final ProgressBar progressBar = new ProgressBar(root.getContext());
        progressBar.setIndeterminate(true);
        progressBar.setLayoutParams(lp);

        if (circleColor != Integer.MIN_VALUE) {
            progressBar.getIndeterminateDrawable().setColorFilter(circleColor, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        root.addView(progressBar);

        synchronized (activeProgressBarSet) {
            activeProgressBarSet.add(root);
        }

        return progressBar;
    }
}
