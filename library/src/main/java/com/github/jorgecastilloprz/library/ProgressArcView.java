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

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

/**
 * @author Jorge Castillo Pérez
 */
public class ProgressArcView extends ProgressBar {

  private final int SHOW_SCALE_ANIM_DURATION = 150;

  private int frontColor;
  private int arcWidth;

  public ProgressArcView(Context context, int frontColor, int arcWidth) {
    super(context);
    this.frontColor = frontColor;
    this.arcWidth = arcWidth;
    init();
  }

  private void init() {
    setupInitialAlpha();

    if (isInEditMode()) {
      setIndeterminateDrawable(new ProgressArcDrawable.Builder(getContext(), true).build());
      return;
    }

    Resources res = getResources();

    Drawable indeterminateDrawable;
    ProgressArcDrawable.Builder builder =
        new ProgressArcDrawable.Builder(getContext()).color(frontColor)
            .sweepSpeed(Float.parseFloat(res.getString(R.string.cpb_default_sweep_speed)))
            .rotationSpeed(Float.parseFloat(res.getString(R.string.cpb_default_rotation_speed)))
            .strokeWidth(arcWidth)
            .minSweepAngle(res.getInteger(R.integer.cpb_default_min_sweep_angle))
            .maxSweepAngle(res.getInteger(R.integer.cpb_default_max_sweep_angle));

    indeterminateDrawable = builder.build();
    setIndeterminateDrawable(indeterminateDrawable);
  }

  private void setupInitialAlpha() {
    setAlpha(0);
  }

  void fadeIn() {
    ValueAnimator fadeInAnim = ObjectAnimator.ofFloat(this, "alpha", 1);
    fadeInAnim.setDuration(SHOW_SCALE_ANIM_DURATION);
    fadeInAnim.setStartDelay(SHOW_SCALE_ANIM_DURATION);
    fadeInAnim.start();
  }

  private ProgressArcDrawable checkIndeterminateDrawable() {
    Drawable ret = getIndeterminateDrawable();
    if (ret == null || !(ret instanceof ProgressArcDrawable)) {
      throw new RuntimeException("The drawable is not a CircularProgressDrawable");
    }
    return (ProgressArcDrawable) ret;
  }

  public void progressiveStop() {
    checkIndeterminateDrawable().progressiveStop();
  }

  public void progressiveStop(ProgressArcDrawable.OnEndListener listener) {
    checkIndeterminateDrawable().progressiveStop(listener);
  }
}
