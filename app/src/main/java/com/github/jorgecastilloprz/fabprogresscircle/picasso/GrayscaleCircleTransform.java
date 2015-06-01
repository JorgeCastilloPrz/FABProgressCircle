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
package com.github.jorgecastilloprz.fabprogresscircle.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import com.squareup.picasso.Transformation;

/**
 * @author Jorge Castillo Pérez
 */
public class GrayscaleCircleTransform implements Transformation {
  @Override public Bitmap transform(Bitmap source) {
    int size = Math.min(source.getWidth(), source.getHeight());

    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;

    Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
    if (squaredBitmap != source) {
      source.recycle();
    }

    Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

    ColorMatrix colorMatrix = new ColorMatrix();
    colorMatrix.setSaturation(0);
    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    paint.setColorFilter(filter);
    BitmapShader shader =
        new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
    paint.setShader(shader);
    paint.setAntiAlias(true);

    float r = size / 2f;
    canvas.drawCircle(r, r, r, paint);

    squaredBitmap.recycle();
    return bitmap;
  }

  @Override public String key() {
    return "circle";
  }
}
