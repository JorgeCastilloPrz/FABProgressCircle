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
import android.content.Context;
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

  public enum Style {NORMAL, ROUNDED}

  static final Interpolator END_INTERPOLATOR = new LinearInterpolator();
  private static final Interpolator DEFAULT_ROTATION_INTERPOLATOR = new LinearInterpolator();
  private static final Interpolator DEFAULT_SWEEP_INTERPOLATOR = new DecelerateInterpolator();
  private static final int ROTATION_ANIMATOR_DURATION = 2000;
  private static final int SWEEP_ANIMATOR_DURATION = 600;
  private static final int END_ANIMATOR_DURATION = 200;

  private final RectF fBounds = new RectF();

  private ValueAnimator mSweepAppearingAnimator;
  private ValueAnimator mSweepDisappearingAnimator;
  private ValueAnimator mRotationAnimator;
  private ValueAnimator mEndAnimator;
  private boolean mModeAppearing;
  private Paint mPaint;
  private boolean mRunning;
  private int mCurrentColor;
  private float mCurrentSweepAngle;
  private float mCurrentRotationAngleOffset = 0;
  private float mCurrentRotationAngle = 0;
  private float mCurrentEndRatio = 1f;

  private Interpolator mAngleInterpolator;
  private Interpolator mSweepInterpolator;
  private float mStrokeWidth;
  private float mSweepSpeed;
  private float mRotationSpeed;
  private int mMinSweepAngle;
  private int mMaxSweepAngle;
  private boolean mFirstSweepAnimation;

  private ProgressArcDrawable(int frontColor, float strokeWidth, float sweepSpeed,
      float rotationSpeed, int minSweepAngle, int maxSweepAngle, Style style,
      Interpolator angleInterpolator, Interpolator sweepInterpolator) {

    mCurrentColor = frontColor;
    mSweepInterpolator = sweepInterpolator;
    mAngleInterpolator = angleInterpolator;
    mStrokeWidth = strokeWidth;
    mSweepSpeed = sweepSpeed;
    mRotationSpeed = rotationSpeed;
    mMinSweepAngle = minSweepAngle;
    mMaxSweepAngle = maxSweepAngle;

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(strokeWidth);
    mPaint.setStrokeCap(style == Style.ROUNDED ? Paint.Cap.ROUND : Paint.Cap.BUTT);
    mPaint.setColor(frontColor);

    setupAnimations();
  }

  private void reinitValues() {
    mFirstSweepAnimation = true;
    mCurrentEndRatio = 1f;
    mPaint.setColor(mCurrentColor);
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
    canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
  }

  @Override public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override public void setColorFilter(ColorFilter cf) {
    mPaint.setColorFilter(cf);
  }

  @Override public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    fBounds.left = bounds.left + mStrokeWidth / 2f + .5f;
    fBounds.right = bounds.right - mStrokeWidth / 2f - .5f;
    fBounds.top = bounds.top + mStrokeWidth / 2f + .5f;
    fBounds.bottom = bounds.bottom - mStrokeWidth / 2f - .5f;
  }

  private void setAppearing() {
    mModeAppearing = true;
    mCurrentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentRotationAngleOffset = mCurrentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  //////////////////////////////////////////////////////////////////////////////
  ////////////////            Animation

  private void setupAnimations() {
    mRotationAnimator = ValueAnimator.ofFloat(0f, 360f);
    mRotationAnimator.setInterpolator(mAngleInterpolator);
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
    mEndAnimator.setInterpolator(END_INTERPOLATOR);
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
    if (isRunning()) {
      return;
    }
    mRunning = true;
    reinitValues();
    mRotationAnimator.start();
    mSweepAppearingAnimator.start();
    invalidateSelf();
  }

  @Override public void stop() {
    if (!isRunning()) {
      return;
    }
    mRunning = false;
    stopAnimators();
    invalidateSelf();
  }

  private void stopAnimators() {
    mRotationAnimator.cancel();
    mSweepAppearingAnimator.cancel();
    mSweepDisappearingAnimator.cancel();
    mEndAnimator.cancel();
  }

  public void progressiveStop(final FABProgressListener listener) {
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

  void progressiveStop() {
    progressiveStop(null);
  }

  @Override public boolean isRunning() {
    return mRunning;
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

  static class Builder {
    private float mSweepSpeed;
    private float mRotationSpeed;
    private float mStrokeWidth;
    private int mMinSweepAngle;
    private int mMaxSweepAngle;
    private int frontColor;
    private Style mStyle;
    private Interpolator mSweepInterpolator = DEFAULT_SWEEP_INTERPOLATOR;
    private Interpolator mAngleInterpolator = DEFAULT_ROTATION_INTERPOLATOR;

    public Builder(Context context) {
      this(context, false);
    }

    public Builder(Context context, boolean editMode) {
      initValues(context, editMode);
    }

    private void initValues(Context context, boolean editMode) {
      mStrokeWidth = context.getResources().getDimension(R.dimen.progress_arc_stroke_width);
      mSweepSpeed = 1f;
      mRotationSpeed = 1f;
      if (editMode) {
        mMinSweepAngle = 20;
        mMaxSweepAngle = 300;
      } else {
        mMinSweepAngle = context.getResources().getInteger(R.integer.cpb_default_min_sweep_angle);
        mMaxSweepAngle = context.getResources().getInteger(R.integer.cpb_default_max_sweep_angle);
      }
      mStyle = Style.ROUNDED;
    }

    public Builder color(int frontColor) {
      this.frontColor = frontColor;
      return this;
    }

    public Builder sweepSpeed(float sweepSpeed) {
      mSweepSpeed = sweepSpeed;
      return this;
    }

    public Builder rotationSpeed(float rotationSpeed) {
      mRotationSpeed = rotationSpeed;
      return this;
    }

    public Builder minSweepAngle(int minSweepAngle) {
      mMinSweepAngle = minSweepAngle;
      return this;
    }

    public Builder maxSweepAngle(int maxSweepAngle) {
      mMaxSweepAngle = maxSweepAngle;
      return this;
    }

    public Builder strokeWidth(float strokeWidth) {
      mStrokeWidth = strokeWidth;
      return this;
    }

    public Builder style(Style style) {
      mStyle = style;
      return this;
    }

    public Builder sweepInterpolator(Interpolator interpolator) {
      mSweepInterpolator = interpolator;
      return this;
    }

    public Builder angleInterpolator(Interpolator interpolator) {
      mAngleInterpolator = interpolator;
      return this;
    }

    public ProgressArcDrawable build() {
      return new ProgressArcDrawable(frontColor, mStrokeWidth, mSweepSpeed, mRotationSpeed,
          mMinSweepAngle, mMaxSweepAngle, mStyle, mAngleInterpolator, mSweepInterpolator);
    }
  }
}
