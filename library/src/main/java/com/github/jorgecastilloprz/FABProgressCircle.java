/*
 * Copyright (C) 2015 Jorge Castillo Pérez
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
package com.github.jorgecastilloprz;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;

import com.github.jorgecastilloprz.library.R;

/**
 * This ViewGroup wraps your FAB, so it will insert a new child on top to draw the progress
 * arc around it.
 *
 * @author Jorge Castillo Pérez
 */
public class FABProgressCircle extends AbstractFABProgressCircle {

    private final int SIZE_MINI = 2;

    private boolean mReusable;

    private Drawable mCompleteIconDrawable;
    private int mShowFinalIconDuration;


    private boolean viewsAdded = false;
    private FABProgressListener listener;
    private boolean mShowFinalIcon;


    public FABProgressCircle(Context context) {
        super(context);
        init(null, 0);
    }

    public FABProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray attrArray = getContext()
                    .obtainStyledAttributes(attrs, R.styleable.FABProgressCircle, defStyleAttr, 0);
            try {

                mReusable = attrArray.getBoolean(R.styleable.FABProgressCircle_reusable, false);
                mCompleteIconDrawable = attrArray
                        .getDrawable(R.styleable.FABProgressCircle_finalIcon);

                mShowFinalIconDuration = attrArray
                        .getInteger(R.styleable.FABProgressCircle_finalIconDuration,
                                getResources()
                                        .getInteger(R.integer.default_final_icon_show_duration));

                mShowFinalIcon = attrArray
                        .getBoolean(R.styleable.FABProgressCircle_showFinalIcon, true);
            } finally {
                attrArray.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkChildCount();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!viewsAdded) {
            addArcView();
            setupFab();
            viewsAdded = true;
        }
    }

    public void attachListener(FABProgressListener listener) {
        this.listener = listener;
    }

    public void show() {
        progressArc.show();
    }

    /**
     * Method exposed to allow the user to hide the animation if something went wrong (like an error
     * in the async task running.
     */
    public void hide() {
        progressArc.stop();
    }

    /**
     * Begin final animation
     */
    public void beginFinalAnimation() {
        super.beginFinalAnimation();
    }

    /**
     * Reset progress arc
     */
    private void doReusableResetProgressArc() {
        if (mReusable && progressArc != null) {
            progressArc.reset();
        }
    }

    /**
     * Reset {@link CompleteFABView} if exist
     */
    private void doReusableResetCompleteFABView() {
        if (mReusable && completeFABView != null) {
            completeFABView.reset();
        }
    }


    private final CompleteFABListener mCompleteFABListener = new CompleteFABListener() {
        @Override
        public void onCompleteFABAnimationEnd() {
            doReusableResetProgressArc();
            doReusableResetCompleteFABView();
            if (listener != null) {
                listener.onFABProgressAnimationEnd();
            }
        }
    };

    /**
     * If the code is being executed in api >= 21 the fab could have elevation, so the
     * completeFabView should have at least the same elevation plus 1, to be able to
     * get displayed on top
     * <p>
     * If we are in pre lollipop, there is no real elevation, so we just need to add the view
     * normally, as any possible elevation present would be fake (shadow tricks with backgrounds,
     * etc)
     * <p>
     * We can use ViewCompat methods to set / get elevation, as they do not do anything when you
     * are in a pre lollipop device.
     */
    @Override
    void handleArcAnimationComplete() {
        if (mShowFinalIcon) {
            completeFABView = new CompleteFABView(getContext(), mCompleteIconDrawable,
                    getArcColor(),
                    mShowFinalIconDuration);
            completeFABView.attachListener(mCompleteFABListener);
            addView(completeFABView,
                    new LayoutParams(getFabDimension(), getFabDimension(), Gravity.CENTER));
            ViewCompat.setElevation(completeFABView, ViewCompat.getElevation(getChildAt(0)) + 1);
            completeFABView.animate(progressArc.getScaleDownAnimator());
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(progressArc.getScaleDownAnimator());
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    doReusableResetProgressArc();
                    if (listener != null) {
                        listener.onFABProgressAnimationEnd();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animatorSet.start();
        }
    }

    /**
     * @return the duration for the final icon
     */
    public int getShowFinalIconDuration() {
        return mShowFinalIconDuration;
    }

    /**
     * Set the duration of the final icon
     *
     * @param duration the duration in milliseconds
     */
    public void setShowFinalIconDuration(int duration) {
        mShowFinalIconDuration = duration;
        invalidate();
    }

    /**
     * @return true if the final icon should be visible
     */
    public boolean isShowFinalIcon() {
        return mShowFinalIcon;
    }

    /**
     * Set the visibility of the final icon
     *
     * @param showFinalIcon true the final icon will be visible, hide otherwise
     */
    public void setShowFinalIcon(boolean showFinalIcon) {
        mShowFinalIcon = showFinalIcon;
        invalidate();
    }

    public int getArcColor() {
        return super.getArcColor();
    }

    public void setArcColor(@ColorInt int arcColor) {
        super.setArcColor(arcColor);
    }

    public int getArcWidth() {
        return super.getArcWidth();
    }

    /**
     * Set progress arc width
     *
     * @param arcWidth the width
     */
    public void setArcWidth(@DimenRes int arcWidth) {
        super.setArcWidth(arcWidth);
    }

    public int getCircleSize() {
        return super.getCircleSize();
    }

    /**
     * Set circle size of the Fab
     *
     * @param circleSize the cercleSize type between {@link CircleSizeType#CIRCLE_NORMAL}
     *                   or {@link CircleSizeType#CIRCLE_MINI}
     */
    public void setCircleSize(@CircleSizeDef int circleSize) {
        super.setCircleSize(circleSize);
    }

    public boolean isRoundedStroke() {
        return super.isRoundedStroke();
    }

    public void setRoundedStroke(boolean roundedStroke) {
        super.setRoundedStroke(roundedStroke);
    }

    /**
     * Set complete arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setCompleteArcDuration(int duration) {
        super.setCompleteArcDuration(duration);
    }

    /**
     * @return the arc complete duration in milliseconds
     */
    public int getCompleteArcDuration() {
        return super.getCompleteArcDuration();
    }

    /**
     * Set rotation arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setRotationArcDuration(int duration) {
        super.setRotationArcDuration(duration);
    }

    /**
     * @return the arc rotation duration in milliseconds
     */
    public int getRotationArcDuration() {
        return super.getRotationArcDuration();
    }

    /**
     * Set grow arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setGrowArcDuration(int duration) {
        super.setGrowArcDuration(duration);
    }

    /**
     * @return the arc grow duration in milliseconds
     */
    public int getGrowArcDuration() {
        return super.getGrowArcDuration();
    }

    /**
     * Set shrink arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setShrinkArcDuration(int duration) {
        super.setShrinkArcDuration(duration);
    }

    /**
     * @return the arc shrink duration in milliseconds
     */
    public int getShrinkArcDuration() {
        return super.getShrinkArcDuration();
    }
}