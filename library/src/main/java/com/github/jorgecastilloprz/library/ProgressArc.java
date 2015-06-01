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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * This view is used to draw the progress circle animated arc. Canvas and angles will be our best
 * friends here, so it will be done in the onDraw method.
 *
 * @author Jorge Castillo Pérez
 */
final class ProgressArc extends View {

  private Paint paint;

  ProgressArc(Context context, int frontColor, int arcStrokeWidth) {
    super(context);
    initFadedOut();
    initPaint(frontColor, arcStrokeWidth);
  }

  private void initFadedOut() {
    setAlpha(0);
  }

  private void initPaint(int color, int arcStrokeWidth) {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(color);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeCap(Paint.Cap.BUTT);
    paint.setStrokeWidth(arcStrokeWidth);
  }

  @Override protected void onDraw(Canvas canvas) {
    ovalRect = new RectF(midRingWidth,midRingWidth, size - midRingWidth, size- midRingWidth);
    float sweepAngle = actualProgress / maxProgress * 360;
    canvas.drawArc(bounds, -90f + indeterminateRotateOffset, 15f, false, paint);
  }
}
