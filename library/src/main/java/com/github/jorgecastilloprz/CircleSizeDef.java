package com.github.jorgecastilloprz;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.github.jorgecastilloprz.CircleSizeType.CIRCLE_MINI;
import static com.github.jorgecastilloprz.CircleSizeType.CIRCLE_NORMAL;

/**
 * CircleSizeDef
 */
@IntDef(value = {
        CIRCLE_NORMAL,
    CIRCLE_MINI})
@Retention(RetentionPolicy.SOURCE)
public @interface CircleSizeDef {
}
