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

/**
 * This view is used to draw the progress circle animated arc. Canvas and angles will be our best
 * friends here, so it will be done in the onDraw method.
 *
 * @author Jorge Castillo Pérez
 */
final class ProgressArcDrawable extends Drawable implements Animatable {

  private static final int ROTATION_ANIMATOR_DURATION = 2000;
  private static final int SWEEP_ANIMATOR_DURATION = 600;
  private static final int END_ANIMATOR_DURATION = 200;

  private final RectF arcBounds = new RectF();

  private ValueAnimator mSweepAppearingAnimator;
  private ValueAnimator mSweepDisappearingAnimator;
  private ValueAnimator mRotationAnimator;
  private ValueAnimator mEndAnimator;
  private boolean mModeAppearing;
  private Paint mPaint;
  private boolean animationPlaying;
  private float mCurrentSweepAngle;
  private float mCurrentRotationAngleOffset = 0;
  private float mCurrentRotationAngle = 0;
  private float mCurrentEndRatio = 1f;

  private float mStrokeWidth;
  private float mSweepSpeed;
  private float mRotationSpeed;
  private int mMinSweepAngle;
  private int mMaxSweepAngle;
  private boolean mFirstSweepAnimation;

  ProgressArcDrawable(Resources res, float strokeWidth, int arcColor) {
    mStrokeWidth = strokeWidth;
    mSweepSpeed = Float.parseFloat(res.getString(R.string.cpb_default_sweep_speed));
    mRotationSpeed = Float.parseFloat(res.getString(R.string.cpb_default_rotation_speed));
    mMinSweepAngle = res.getInteger(R.integer.cpb_default_min_sweep_angle);
    mMaxSweepAngle = res.getInteger(R.integer.cpb_default_max_sweep_angle);

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(mStrokeWidth);
    mPaint.setStrokeCap(Paint.Cap.BUTT);
    mPaint.setColor(arcColor);

    setupAnimations();
  }

  public void reset() {
    if (isRunning()) {
      return;
    }
    stop();
    resetProperties();
    start();
  }

  private void resetProperties() {
    mFirstSweepAnimation = true;
    mCurrentEndRatio = 1f;
    mCurrentSweepAngle = 0;
    mCurrentRotationAngle = 0;
    mCurrentRotationAngleOffset = 0;
  }

  @Override public void draw(Canvas canvas) {
    float startAngle = mCurrentRotationAngle - mCurrentRotationAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
    }
    startAngle %= 360;
    if (mCurrentEndRatio < 1f) {
      float newSweepAngle = sweepAngle * mCurrentEndRatio;
      startAngle = (startAngle + (sweepAngle - newSweepAngle)) % 360;
      sweepAngle = newSweepAngle;
    }
    canvas.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint);
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

  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    arcBounds.left = bounds.left + mStrokeWidth / 2f + .5f;
    arcBounds.right = bounds.right - mStrokeWidth / 2f - .5f;
    arcBounds.top = bounds.top + mStrokeWidth / 2f + .5f;
    arcBounds.bottom = bounds.bottom - mStrokeWidth / 2f - .5f;
  }

  private void setAppearing() {
    mModeAppearing = true;
    mCurrentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentRotationAngleOffset = mCurrentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  private void setupAnimations() {
    Interpolator mSweepInterpolator = new DecelerateInterpolator();

    mRotationAnimator = ValueAnimator.ofFloat(0f, 360f);
    mRotationAnimator.setInterpolator(new LinearInterpolator());
    mRotationAnimator.setDuration((long) (ROTATION_ANIMATOR_DURATION / mRotationSpeed));
    mRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float angle = ProgressArcUtils.getAnimatedFraction(animation) * 360f;
        setCurrentRotationAngle(angle);
      }
    });
    mRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);

    mSweepAppearingAnimator = ValueAnimator.ofFloat(mMinSweepAngle, mMaxSweepAngle);
    mSweepAppearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepAppearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepAppearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = ProgressArcUtils.getAnimatedFraction(animation);
        float angle;
        if (mFirstSweepAnimation) {
          angle = animatedFraction * mMaxSweepAngle;
        } else {
          angle = mMinSweepAngle + animatedFraction * (mMaxSweepAngle - mMinSweepAngle);
        }
        setCurrentSweepAngle(angle);
      }
    });
    mSweepAppearingAnimator.addListener(new Animator.AnimatorListener() {
      boolean cancelled = false;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
        mModeAppearing = true;
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          mFirstSweepAnimation = false;
          setDisappearing();
          mSweepDisappearingAnimator.start();
        }
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });

    mSweepDisappearingAnimator = ValueAnimator.ofFloat(mMaxSweepAngle, mMinSweepAngle);
    mSweepDisappearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepDisappearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));

    mSweepDisappearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = ProgressArcUtils.getAnimatedFraction(animation);
        setCurrentSweepAngle(mMaxSweepAngle - animatedFraction * (mMaxSweepAngle - mMinSweepAngle));
      }
    });

    mSweepDisappearingAnimator.addListener(new Animator.AnimatorListener() {
      boolean cancelled;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
      }

      @Override public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          setAppearing();
          mSweepAppearingAnimator.start();
        }
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {
      }
    });

    mEndAnimator = ValueAnimator.ofFloat(1f, 0f);
    mEndAnimator.setInterpolator(new LinearInterpolator());
    mEndAnimator.setDuration(END_ANIMATOR_DURATION);
    mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        setEndRatio(1f - ProgressArcUtils.getAnimatedFraction(animation));
      }
    });
    mEndAnimator.addListener(new Animator.AnimatorListener() {
      private boolean cancelled;

      @Override public void onAnimationStart(Animator animation) {
        cancelled = false;
      }

      @Override public void onAnimationEnd(Animator animation) {
        setEndRatio(0f);
        if (!cancelled) stop();
      }

      @Override public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });
  }

  @Override public void start() {
    animationPlaying = true;
    resetProperties();
    mRotationAnimator.start();
    mSweepAppearingAnimator.start();
    invalidateSelf();
  }

  @Override public void stop() {
    animationPlaying = false;
    stopAnimators();
    invalidateSelf();
  }

  private void stopAnimators() {
    mRotationAnimator.cancel();
    mSweepAppearingAnimator.cancel();
    mSweepDisappearingAnimator.cancel();
    mEndAnimator.cancel();
  }

  void progressiveStop() {
    progressiveStop(null);
  }

  void progressiveStop(final FABProgressListener listener) {
    if (!isRunning() || mEndAnimator.isRunning()) {
      return;
    }

    mEndAnimator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        mEndAnimator.removeListener(this);
        if (listener != null) {
          listener.onFABProgressAnimationEnd();
        }
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });
    mEndAnimator.start();
  }

  @Override public boolean isRunning() {
    return animationPlaying;
  }

  void setCurrentRotationAngle(float currentRotationAngle) {
    mCurrentRotationAngle = currentRotationAngle;
    invalidateSelf();
  }

  void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    invalidateSelf();
  }

  private void setEndRatio(float ratio) {
    mCurrentEndRatio = ratio;
    invalidateSelf();
  }
}
