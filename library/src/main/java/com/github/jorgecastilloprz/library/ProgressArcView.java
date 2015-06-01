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
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

/**
 * This view contains the animated arc and depends totally on {@link ProgressArcDrawable} to get
 * its corresponding graphic aspect.
 *
 * @author Jorge Castillo Pérez
 */
final class ProgressArcView extends ProgressBar {

  private final int SHOW_SCALE_ANIM_DURATION = 40;
  private final int SHOW_SCALE_ANIM_DELAY = 150;

  private int frontColor;
  private int arcWidth;
  private Drawable progressDrawable;

  ProgressArcView(Context context, int frontColor, int arcWidth) {
    super(context);
    this.frontColor = frontColor;
    this.arcWidth = arcWidth;
    init();
  }

  private void init() {
    setupInitialAlpha();

    ProgressArcDrawable.Builder builder =
        new ProgressArcDrawable.Builder(getContext()).color(frontColor).strokeWidth(arcWidth);
    progressDrawable = builder.build();
    setIndeterminateDrawable(progressDrawable);
  }

  private void setupInitialAlpha() {
    setAlpha(0);
  }

  void fadeIn() {
    ValueAnimator fadeInAnim = ObjectAnimator.ofFloat(this, "alpha", 1);
    fadeInAnim.setDuration(SHOW_SCALE_ANIM_DURATION);
    fadeInAnim.setStartDelay(SHOW_SCALE_ANIM_DELAY);
    fadeInAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
        getDrawable().reset();
      }

      @Override public void onAnimationEnd(Animator animator) {
      }

      @Override public void onAnimationCancel(Animator animator) {
      }

      @Override public void onAnimationRepeat(Animator animator) {
      }
    });
    fadeInAnim.start();
  }

  void progressiveStop() {
    getDrawable().progressiveStop();
  }

  void progressiveStop(FABProgressListener listener) {
    getDrawable().progressiveStop(listener);
  }

  private ProgressArcDrawable getDrawable() {
    Drawable ret = getIndeterminateDrawable();
    return (ProgressArcDrawable) ret;
  }
}
