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
package com.github.jorgecastilloprz.fabprogresscircle.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.github.jorgecastilloprz.fabprogresscircle.R;

/**
 * @author Jorge Castillo Pérez
 */
public class RobotoTextView extends TextView {

  private String basePath = "typeface/";

  public RobotoTextView(Context context) {
    super(context);
    init(null);
  }

  public RobotoTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    int typeFaceNumber = 0;
    if (attrs != null) {
      TypedArray a =
          getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RobotoTextView, 0, 0);

      try {
        typeFaceNumber = a.getInteger(R.styleable.RobotoTextView_typeface, 0);
      } finally {
        a.recycle();
      }
    }

    setTypeface(typeFaceNumber);
  }

  public void setType(int typefaceNumber) {
    setTypeface(typefaceNumber);
  }

  private void setTypeface(int typeFaceNumber) {
    Typeface typeface =
        Typeface.createFromAsset(getResources().getAssets(), getPathForTypeface(typeFaceNumber));
    setTypeface(typeface);
  }

  private String getPathForTypeface(int typeFaceNumber) {
    switch (typeFaceNumber) {
      case 0:
        return basePath + "Roboto-Regular.ttf";
      default:
        return basePath + "Roboto-Medium.ttf";
    }
  }
}
