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

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

/**
 * @author Jorge Castillo Pérez
 */
final class RotateArcAnimation implements ArcAnimation {

  private ValueAnimator rotateAnim;

  RotateArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener) {
    rotateAnim = ValueAnimator.ofFloat(0f, 360f);
    rotateAnim.setInterpolator(new LinearInterpolator());
    rotateAnim.setDuration(ArcAnimationFactory.ROTATE_ANIMATOR_DURATION);
    rotateAnim.addUpdateListener(updateListener);
    rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
    rotateAnim.setRepeatMode(ValueAnimator.RESTART);
  }

  @Override public ValueAnimator getAnimator() {
    return rotateAnim;
  }
}
