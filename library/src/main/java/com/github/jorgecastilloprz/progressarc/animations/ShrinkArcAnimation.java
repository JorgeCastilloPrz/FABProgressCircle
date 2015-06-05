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
package com.github.jorgecastilloprz.progressarc.animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

/**
 * @author Jorge Castillo Pérez
 */
public class ShrinkArcAnimation implements ArcAnimation {

  private ValueAnimator shrinkAnim;

  ShrinkArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
      Animator.AnimatorListener listener) {

    shrinkAnim = ValueAnimator.ofFloat(ArcAnimationFactory.MAXIMUM_SWEEP_ANGLE,
        ArcAnimationFactory.MINIMUM_SWEEP_ANGLE);
    shrinkAnim.setInterpolator(new DecelerateInterpolator());
    shrinkAnim.setDuration(ArcAnimationFactory.sweepAnimDuration);
    shrinkAnim.addUpdateListener(updateListener);
    shrinkAnim.addListener(listener);
  }

  @Override public ValueAnimator getAnimator() {
    return shrinkAnim;
  }
}
