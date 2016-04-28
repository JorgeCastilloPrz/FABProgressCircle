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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import com.github.jorgecastilloprz.completefab.CompleteFABListener;
import com.github.jorgecastilloprz.completefab.CompleteFABView;
import com.github.jorgecastilloprz.library.R;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.github.jorgecastilloprz.progressarc.ArcListener;
import com.github.jorgecastilloprz.progressarc.ProgressArcView;
import com.github.jorgecastilloprz.utils.LibraryUtils;

/**
 * This ViewGroup wraps your FAB, so it will insert a new child on top to draw the progress
 * arc around it.
 *
 * @author Jorge Castillo Pérez
 */
public class FABProgressCircle extends FrameLayout implements ArcListener, CompleteFABListener {

  private final int SIZE_NORMAL = 1;
  private final int SIZE_MINI = 2;

  @ColorInt
  private int arcColor;
  private int arcWidth;
  private int circleSize;
  private boolean roundedStroke;
  private boolean reusable;

  private CompleteFABView completeFABView;
  private Drawable completeIconDrawable;

  private boolean viewsAdded;
  private ProgressArcView progressArc;
  private FABProgressListener listener;

  public FABProgressCircle(Context context) {
    super(context);
    init(null);
  }

  public FABProgressCircle(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public FABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public FABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    setupInitialAttributes(attrs);
  }

  private void setupInitialAttributes(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray attrArray = getAttributes(attrs);
      try {
        arcColor = attrArray.getColor(R.styleable.FABProgressCircle_arcColor,
            getResources().getColor(R.color.fab_orange_dark));
        arcWidth = attrArray.getDimensionPixelSize(R.styleable.FABProgressCircle_arcWidth,
            getResources().getDimensionPixelSize(R.dimen.progress_arc_stroke_width));
        completeIconDrawable = attrArray.getDrawable(R.styleable.FABProgressCircle_finalIcon);
        circleSize = attrArray.getInt(R.styleable.FABProgressCircle_circleSize, 1);
        roundedStroke = attrArray.getBoolean(R.styleable.FABProgressCircle_roundedStroke, false);
        reusable = attrArray.getBoolean(R.styleable.FABProgressCircle_reusable, false);
      } finally {
        attrArray.recycle();
      }
    }
  }

  private TypedArray getAttributes(AttributeSet attrs) {
    return getContext().obtainStyledAttributes(attrs, R.styleable.FABProgressCircle, 0, 0);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    checkChildCount();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (!viewsAdded) {
      addArcView();
      setupFab();
      viewsAdded = true;
    }
  }

  /**
   * We need to draw a new view with the arc over the FAB, to be able to hide the fab shadow
   * (if it exists).
   */
  private void addArcView() {
    setClipChildren(false);
    progressArc = new ProgressArcView(getContext(), arcColor, arcWidth, roundedStroke);
    progressArc.setInternalListener(this);
    addView(progressArc,
        new FrameLayout.LayoutParams(getFabDimension() + arcWidth, getFabDimension() + arcWidth,
            Gravity.CENTER));
  }

  private void setupFab() {
    FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) getChildAt(0).getLayoutParams();
    fabParams.gravity = Gravity.CENTER;
    if (LibraryUtils.isAFutureSimpleFAB(getChildAt(0))) {
      fabParams.topMargin =
          getResources().getDimensionPixelSize(R.dimen.futuresimple_fab_shadow_offset);
    }
  }

  /**
   * FABProgressCircle will get its dimensions depending on its child dimensions. It will be easier
   * to force proper graphic standards for the button if we can get sure that only one child is
   * present. Every FAB library around has a single root layout, so it should not be an issue.
   */
  private void checkChildCount() {
    if (getChildCount() != 1) {
      throw new IllegalStateException(getResources().getString(R.string.child_count_error));
    }
  }

  public void attachListener(FABProgressListener listener) {
    this.listener = listener;
  }

  public void show() {
    progressArc.show();
  }

  /**
   * Method exposed to allow the user to hide the animation if something went wrong (like an error
   * in the async task running.
   */
  public void hide() {
    progressArc.stop();
  }

  public void beginFinalAnimation() {
    progressArc.requestCompleteAnimation();
  }

  @Override public void onArcAnimationComplete() {
    displayColorTransformAnimation();
  }

  /**
   * If the code is being executed in api >= 21 the fab could have elevation, so the
   * completeFabView should have at least the same elevation plus 1, to be able to
   * get displayed on top
   *
   * If we are in pre lollipop, there is no real elevation, so we just need to add the view
   * normally, as any possible elevation present would be fake (shadow tricks with backgrounds,
   * etc)
   *
   * We can use ViewCompat methods to set / get elevation, as they do not do anything when you
   * are in a pre lollipop device.
   */
  private void displayColorTransformAnimation() {
    addCompleteFabView();
    ViewCompat.setElevation(completeFABView, ViewCompat.getElevation(getChildAt(0)) + 1);
    completeFABView.animate(progressArc.getScaleDownAnimator());
  }

  private void addCompleteFabView() {
    completeFABView = new CompleteFABView(getContext(), completeIconDrawable, arcColor);
    completeFABView.attachListener(this);
    addView(completeFABView,
        new FrameLayout.LayoutParams(getFabDimension(), getFabDimension(), Gravity.CENTER));
  }

  @Override public void onCompleteFABAnimationEnd() {
    doReusableReset();
    if (listener != null) {
      listener.onFABProgressAnimationEnd();
    }
  }

  public FABProgressCircle withArcColor(@ColorInt int arcColor) {
    this.arcColor = arcColor;
    if (progressArc != null) {
        progressArc.setArcColor(arcColor);
    }
    return this;
  }

  private void doReusableReset() {
    if (isReusable()) {
      progressArc.reset();
      completeFABView.reset();
    }
  }

  private boolean isReusable() {
    return reusable;
  }

  private int getFabDimension() {
    if (circleSize == SIZE_NORMAL) {
      return getResources().getDimensionPixelSize(R.dimen.fab_size_normal);
    } else {
      return getResources().getDimensionPixelSize(R.dimen.fab_size_mini);
    }
  }
}