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

package com.landawn.abacus.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewBase.
 *
 * @author Haiyang Li
 * @since 0.8
 */
public abstract class ViewBase extends View {

    /**
     * Instantiates a new view base.
     *
     * @param context
     */
    public ViewBase(Context context) {
        super(context);
    }

    /**
     * Instantiates a new view base.
     *
     * @param context
     * @param attrs
     */
    public ViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new view base.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Instantiates a new view base.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public ViewBase(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
