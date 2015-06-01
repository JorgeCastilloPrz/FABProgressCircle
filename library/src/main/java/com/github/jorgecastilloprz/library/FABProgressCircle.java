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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

/**
 * This ViewGroup wraps your FAB, so it will insert a new child on top to draw the progress
 * arc around it.
 *
 * @author Jorge Castillo Pérez
 */
public class FABProgressCircle extends FrameLayout {

  private int arcColor;
  private int arcWidth;
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
      addArcViewAtFront();
      setFabGravity();
      viewsAdded = true;
    }
  }

  private void setFabGravity() {
    FrameLayout.LayoutParams fabParams = (FrameLayout.LayoutParams) getChildAt(0).getLayoutParams();
    fabParams.gravity = Gravity.CENTER;
  }

  /**
   * We need to draw a new view with the circle at front, to be able to hide the fab button shadow
   * (if it exists). The progress circle will have his own shadow effect.
   */
  private void addArcViewAtFront() {
    progressArc = new ProgressArcView(getContext(), arcColor, arcWidth);
    addView(progressArc,
        new FrameLayout.LayoutParams(getMeasuredWidth() + arcWidth, getMeasuredHeight() + arcWidth,
            Gravity.CENTER));
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

  public void beginStopAnimation() {
    if (listener != null) {
      progressArc.progressiveStop(listener);
    } else {
      progressArc.progressiveStop();
    }
  }
}
