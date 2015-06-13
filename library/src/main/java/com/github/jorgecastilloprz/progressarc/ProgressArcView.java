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
package com.github.jorgecastilloprz.progressarc;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import static com.github.jorgecastilloprz.utils.AnimationUtils.SHOW_SCALE_ANIM_DELAY;

/**
 * This view contains the animated arc and depends totally on {@link ProgressArcDrawable} to get
 * its corresponding graphic aspect.
 *
 * @author Jorge Castillo Pérez
 */
public final class ProgressArcView extends ProgressBar {

  private ArcListener internalListener;
  private int arcColor;
  private int arcWidth;
  private boolean roundedStroke;

  public ProgressArcView(Context context, int arcColor, int arcWidth, boolean roundedStroke) {
    super(context);
    this.arcColor = arcColor;
    this.arcWidth = arcWidth;
    this.roundedStroke = roundedStroke;
    init(arcColor, arcWidth, roundedStroke);
  }

  public void init(int arcColor, int arcWidth, boolean roundedStroke) {
    setupInitialAlpha();
    ProgressArcDrawable arcDrawable = new ProgressArcDrawable(arcWidth, arcColor, roundedStroke);
    setIndeterminateDrawable(arcDrawable);
  }

  private void setupInitialAlpha() {
    setAlpha(0);
  }

  public void setInternalListener(ArcListener internalListener) {
    this.internalListener = internalListener;
  }

  public void show() {
    postDelayed(new Runnable() {
      @Override public void run() {
        setAlpha(1);
        getDrawable().reset();
      }
    }, SHOW_SCALE_ANIM_DELAY);
  }

  public void stop() {
    getDrawable().stop();
    ValueAnimator fadeOutAnim = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
    fadeOutAnim.setDuration(100).start();
  }

  public void reset() {
    getDrawable().reset();

    ValueAnimator arcScaleX = ObjectAnimator.ofFloat(this, "scaleX", 1);
    ValueAnimator arcScaleY = ObjectAnimator.ofFloat(this, "scaleY", 1);

    AnimatorSet set = new AnimatorSet();
    set.setDuration(0).setInterpolator(new DecelerateInterpolator());
    set.playTogether(arcScaleX, arcScaleY);
    set.start();
  }

  public void requestCompleteAnimation() {
    getDrawable().requestCompleteAnimation(internalListener);
  }

  private ProgressArcDrawable getDrawable() {
    Drawable ret = getIndeterminateDrawable();
    return (ProgressArcDrawable) ret;
  }

  public AnimatorSet getScaleDownAnimator() {
    float scalePercent = (float) getWidth() / (getWidth() + arcWidth + 5);

    ValueAnimator arcScaleX = ObjectAnimator.ofFloat(this, "scaleX", scalePercent);
    ValueAnimator arcScaleY = ObjectAnimator.ofFloat(this, "scaleY", scalePercent);

    AnimatorSet set = new AnimatorSet();
    set.setDuration(150).setInterpolator(new DecelerateInterpolator());
    set.playTogether(arcScaleX, arcScaleY);
    set.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
      }

      @Override public void onAnimationEnd(Animator animator) {
        setupInitialAlpha();
      }

      @Override public void onAnimationCancel(Animator animator) {
      }

      @Override public void onAnimationRepeat(Animator animator) {
      }
    });

    return set;
  }
}
