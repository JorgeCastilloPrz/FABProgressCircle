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

import android.animation.ValueAnimator;

/**
 * @author Jorge Castillo Pérez
 */
class Utils {

  static final int SHOW_SCALE_ANIM_DELAY = 150;
  static final int ROTATION_ANIMATOR_DURATION = 2000;
  static final int SWEEP_ANIMATOR_DURATION = 600;
  static final int COMPLETE_ANIM_DURATION = 6000;

  static float getAnimatedFraction(ValueAnimator animator) {
    float fraction = ((float) animator.getCurrentPlayTime()) / animator.getDuration();
    fraction = Math.min(fraction, 1f);
    fraction = animator.getInterpolator().getInterpolation(fraction);
    return fraction;
  }
}
