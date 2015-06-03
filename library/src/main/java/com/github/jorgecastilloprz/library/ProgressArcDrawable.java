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
package com.github.jorgecastilloprz.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import static com.github.jorgecastilloprz.library.Utils.COMPLETE_ANIM_DURATION;
import static com.github.jorgecastilloprz.library.Utils.ROTATION_ANIMATOR_DURATION;
import static com.github.jorgecastilloprz.library.Utils.SWEEP_ANIMATOR_DURATION;
import static com.github.jorgecastilloprz.library.Utils.getAnimatedFraction;

/**
 * This view is used to draw the progress circle animated arc
 * Canvas and angles will be our best friends here.
 *
 * @author Jorge Castillo Pérez
 */
final class ProgressArcDrawable extends Drawable implements Animatable {

  private final RectF arcBounds = new RectF();

  private float currentSweepAngle;
  private float currentRotationAngleOffset;
  private float currentRotationAngle;

  private ValueAnimator rotateAnim;
  private ValueAnimator growAnim;
  private ValueAnimator shrinkAnim;
  private ValueAnimator completeAnim;

  private boolean animationPlaying;
  private boolean mModeAppearing;
  private boolean completeAnimOnNextCycle;

  private Paint mPaint;

  private float mStrokeWidth;
  private int mArcColor;
  private int mMinSweepAngle;
  private int mMaxSweepAngle;

  private Interpolator sweepInterpolator;
  private Resources res;

  private InternalListener internalListener;

  ProgressArcDrawable(Resources res, float strokeWidth, int arcColor) {
    this.res = res;
    mStrokeWidth = strokeWidth;
    mArcColor = arcColor;
    initPaint();
    setupAnimations();
  }

  private void initPaint() {
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(mStrokeWidth);
    mPaint.setStrokeCap(Paint.Cap.BUTT);
    mPaint.setColor(mArcColor);
  }

  private void setupAnimations() {
    mMinSweepAngle = res.getInteger(R.integer.min_sweep_angle);
    mMaxSweepAngle = res.getInteger(R.integer.max_sweep_angle);

    sweepInterpolator = new DecelerateInterpolator();
    setupRotateAnimation();
    setupGrowAnimation();
    setupShrinkAnimation();
    setupCompleteAnimation();
  }

  private void setupRotateAnimation() {
    rotateAnim = ValueAnimator.ofFloat(0f, 360f);
    rotateAnim.setInterpolator(new LinearInterpolator());
    rotateAnim.setDuration(ROTATION_ANIMATOR_DURATION);
    rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float angle = getAnimatedFraction(animation) * 360f;
        updateCurrentRotationAngle(angle);
      }
    });
    rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
    rotateAnim.setRepeatMode(ValueAnimator.RESTART);
  }

  private void setupGrowAnimation() {
    growAnim = ValueAnimator.ofFloat(mMinSweepAngle, mMaxSweepAngle);
    growAnim.setInterpolator(sweepInterpolator);
    growAnim.setDuration(SWEEP_ANIMATOR_DURATION);
    growAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = getAnimatedFraction(animation);
        float angle = mMinSweepAngle + animatedFraction * (mMaxSweepAngle - mMinSweepAngle);
        updateCurrentSweepAngle(angle);
      }
    });
    growAnim.addListener(new Animator.AnimatorListener() {
      boolean cancelled = false;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
        mModeAppearing = true;
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          setDisappearing();
          shrinkAnim.start();
        }
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });
  }

  private void setupShrinkAnimation() {
    shrinkAnim = ValueAnimator.ofFloat(mMaxSweepAngle, mMinSweepAngle);
    shrinkAnim.setInterpolator(sweepInterpolator);
    shrinkAnim.setDuration(SWEEP_ANIMATOR_DURATION);

    shrinkAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = getAnimatedFraction(animation);
        updateCurrentSweepAngle(
            mMaxSweepAngle - animatedFraction * (mMaxSweepAngle - mMinSweepAngle));
      }
    });

    shrinkAnim.addListener(new Animator.AnimatorListener() {
      boolean cancelled;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          setAppearing();
          if (completeAnimOnNextCycle) {
            completeAnimOnNextCycle = false;
            completeAnim.start();
          } else {
            growAnim.start();
          }
        }
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });
  }

  private void setupCompleteAnimation() {
    completeAnim = ValueAnimator.ofFloat(currentSweepAngle, 360f);
    completeAnim.setInterpolator(sweepInterpolator);
    completeAnim.setDuration(COMPLETE_ANIM_DURATION);
    completeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = getAnimatedFraction(animation);
        float angle = mMinSweepAngle + animatedFraction * 360;
        updateCurrentSweepAngle(angle);
      }
    });
    completeAnim.addListener(new Animator.AnimatorListener() {
      boolean cancelled = false;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
        mModeAppearing = true;
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          stop();
        }

        completeAnim.removeListener(this);
        internalListener.onArcAnimationComplete();
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });
  }

  public void reset() {
    stop();
    resetProperties();
    start();
  }

  private void resetProperties() {
    currentSweepAngle = 0;
    currentRotationAngle = 0;
    currentRotationAngleOffset = 0;
  }

  @Override public void draw(Canvas canvas) {
    float startAngle = currentRotationAngle - currentRotationAngleOffset;
    float sweepAngle = currentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
    }
    startAngle %= 360;
    canvas.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint);
  }

  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    arcBounds.left = bounds.left + mStrokeWidth / 2f + .5f;
    arcBounds.right = bounds.right - mStrokeWidth / 2f - .5f;
    arcBounds.top = bounds.top + mStrokeWidth / 2f + .5f;
    arcBounds.bottom = bounds.bottom - mStrokeWidth / 2f - .5f;
  }

  private void setAppearing() {
    mModeAppearing = true;
    currentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    currentRotationAngleOffset = currentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  @Override public void start() {
    animationPlaying = true;
    resetProperties();
    rotateAnim.start();
    growAnim.start();
    invalidateSelf();
  }

  @Override public void stop() {
    animationPlaying = false;
    stopAnimators();
    invalidateSelf();
  }

  private void stopAnimators() {
    rotateAnim.cancel();
    growAnim.cancel();
    shrinkAnim.cancel();
    completeAnim.cancel();
  }

  void requestCompleteAnimation(final InternalListener internalListener) {
    if (!isRunning() || completeAnim.isRunning()) {
      return;
    }

    this.internalListener = internalListener;
    startCompleteAnimationOnNextCycle();
  }

  private void startCompleteAnimationOnNextCycle() {
    completeAnimOnNextCycle = true;
  }

  void updateCurrentRotationAngle(float currentRotationAngle) {
    this.currentRotationAngle = currentRotationAngle;
    invalidateSelf();
  }

  void updateCurrentSweepAngle(float currentSweepAngle) {
    this.currentSweepAngle = currentSweepAngle;
    invalidateSelf();
  }

  @Override public boolean isRunning() {
    return animationPlaying;
  }

  @Override public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override public void setColorFilter(ColorFilter colorFilter) {
    mPaint.setColorFilter(colorFilter);
  }

  @Override public int getOpacity() {
    return PixelFormat.RGB_565;
  }
}
