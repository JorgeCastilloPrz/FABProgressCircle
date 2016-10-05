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
import android.animation.ValueAnimator;

/**
 * @author Jorge Castillo Pérez
 */
class ArcAnimationFactory {

    static final int MINIMUM_SWEEP_ANGLE = 20;
    static final int MAXIMUM_SWEEP_ANGLE = 300;

    static final int ROTATE_ANIM_DURATION = 2000;
    static final int SWEEP_ANIM_DURATION = 1000;
    static final int COMPLETE_ANIM_DURATION = SWEEP_ANIM_DURATION * 2;


    static ValueAnimator getCompleteAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
                                                     Animator.AnimatorListener animatorListener,
                                                     int duration) {
        ArcAnimation arcAnimation =
                new CompleteArcAnimation(updateListener, animatorListener, duration);
        return arcAnimation.getAnimator();
    }

    static ValueAnimator getRotateArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
                                                      int duration) {
        ArcAnimation arcAnimation =
                new RotateArcAnimation(updateListener, duration);
        return arcAnimation.getAnimator();
    }

    static ValueAnimator getGrowArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
                                                    Animator.AnimatorListener animatorListener,
                                                    int duration) {
        ArcAnimation arcAnimation =
                new GrowArcAnimation(updateListener, animatorListener, duration);
        return arcAnimation.getAnimator();
    }

    static ValueAnimator getShrinkArcAnimation(ValueAnimator.AnimatorUpdateListener updateListener,
                                                      Animator.AnimatorListener animatorListener,
                                                      int duration) {
        ArcAnimation arcAnimation =
                new ShrinkArcAnimation(updateListener, animatorListener, duration);
        return arcAnimation.getAnimator();
    }
}
