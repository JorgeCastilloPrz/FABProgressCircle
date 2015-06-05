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
package com.github.jorgecastilloprz.completefab;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.github.jorgecastilloprz.library.R;

/**
 * This view represents the fake FAB that will be displayed at the end of the animation.
 *
 * @author Jorge Castillo Pérez
 */
public class CompleteFABView extends FrameLayout {

  private Drawable iconDrawable;
  private int arcColor;
  private CompleteFABListener listener;
  private boolean viewsAdded;

  public CompleteFABView(Context context, Drawable iconDrawable, int arcColor) {
    super(context);
    this.iconDrawable = iconDrawable;
    this.arcColor = arcColor;
    init();
  }

  public void attachListener(CompleteFABListener listener) {
    this.listener = listener;
  }

  private void init() {
    inflate(getContext(), R.layout.complete_fab, this);
  }

  private void tintCompleteFabWithArcColor() {
    Drawable background = getResources().getDrawable(R.drawable.oval_complete);
    background.setColorFilter(arcColor, PorterDuff.Mode.SRC_ATOP);
    findViewById(R.id.completeFabRoot).setBackgroundDrawable(background);
  }

  private void setIcon() {
    ImageView iconView = (ImageView) findViewById(R.id.completeFabIcon);
    iconView.setBackgroundDrawable(
        iconDrawable != null ? iconDrawable : getResources().getDrawable(R.drawable.ic_done));
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (!viewsAdded) {
      setupContentSize();
      tintCompleteFabWithArcColor();
      setIcon();
      viewsAdded = true;
    }
  }

  private void setupContentSize() {
    int contentSize = (int) getResources().getDimension(R.dimen.fab_content_size);
    int mContentPadding = (getChildAt(0).getMeasuredWidth() - contentSize) / 2;
    getChildAt(0).setPadding(mContentPadding, mContentPadding, mContentPadding, mContentPadding);
  }

  public void animate(AnimatorSet progressArcAnimator) {
    ValueAnimator completeFabAnim = ObjectAnimator.ofFloat(getChildAt(0), "alpha", 1);
    completeFabAnim.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());

    View icon = findViewById(R.id.completeFabIcon);

    ValueAnimator iconScaleAnimX = ObjectAnimator.ofFloat(icon, "scaleX", 0, 1);
    ValueAnimator iconScaleAnimY = ObjectAnimator.ofFloat(icon, "scaleY", 0, 1);

    Interpolator iconAnimInterpolator = new LinearInterpolator();
    iconScaleAnimX.setDuration(250).setInterpolator(iconAnimInterpolator);
    iconScaleAnimY.setDuration(250).setInterpolator(iconAnimInterpolator);

    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(completeFabAnim, progressArcAnimator, iconScaleAnimX, iconScaleAnimY);
    animatorSet.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
      }

      @Override public void onAnimationEnd(Animator animator) {
        if (listener != null) {
          listener.onCompleteFABAnimationEnd();
        }
      }

      @Override public void onAnimationCancel(Animator animator) {
      }

      @Override public void onAnimationRepeat(Animator animator) {
      }
    });
    animatorSet.start();
  }
}
