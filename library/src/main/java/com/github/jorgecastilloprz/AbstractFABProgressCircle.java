package com.github.jorgecastilloprz;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.github.jorgecastilloprz.library.R;

/**
 * Abstract of {@link FABProgressCircle} to manage fork
 */
abstract class AbstractFABProgressCircle extends FrameLayout implements ArcListener {
    private final int SIZE_NORMAL = 1;
    protected CompleteFABView completeFABView;
    protected ProgressArcView progressArc;
    private int mArcColor;
    private int mArcWidth;
    private int mCircleSize;
    private boolean mRoundedStroke;
    private ProgressArcAnimationDuration mProgressArcAnimationDuration;


    abstract void handleArcAnimationComplete();

    public AbstractFABProgressCircle(Context context) {
        super(context);
        init(null, 0);
    }

    public AbstractFABProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AbstractFABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractFABProgressCircle(Context context, AttributeSet attrs, int defStyleAttr,
                                     int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray attrArray = getContext()
                    .obtainStyledAttributes(attrs, R.styleable.FABProgressCircleGlobal,
                            defStyleAttr, 0);
            mArcColor = attrArray.getColor(R.styleable.FABProgressCircleGlobal_arcColor,
                    getResources().getColor(R.color.fab_orange_dark));
            mArcWidth = attrArray
                    .getDimensionPixelSize(R.styleable.FABProgressCircleGlobal_arcWidth,
                            getResources()
                                    .getDimensionPixelSize(R.dimen.progress_arc_stroke_width));

            mCircleSize = attrArray.getInt(R.styleable.FABProgressCircleGlobal_circleSize, 1);
            mRoundedStroke = attrArray.getBoolean(R.styleable.FABProgressCircleGlobal_roundedStroke,
                    false);

            int completeArcDuration = attrArray
                    .getInteger(R.styleable.FABProgressCircleGlobal_completeArcDuration,
                            getResources().getInteger(R.integer.default_arc_complete_duration));
            int rotationArcDuration = attrArray
                    .getInteger(R.styleable.FABProgressCircleGlobal_rotationArcDuration,
                            getResources().getInteger(R.integer.default_arc_rotation_duration));

            int growArcDuration = attrArray
                    .getInteger(R.styleable.FABProgressCircleGlobal_growArcDuration,
                            getResources().getInteger(R.integer.default_arc_grow_duration));

            int shrinkArcDuration = attrArray
                    .getInteger(R.styleable.FABProgressCircleGlobal_shrinkArcDuration,
                            getResources().getInteger(R.integer.default_arc_shrink_duration));

            mProgressArcAnimationDuration = new ProgressArcAnimationDuration.Builder()
                    .completeArcDuration(completeArcDuration)
                    .rotationArcDuration(rotationArcDuration)
                    .growArcDuration(growArcDuration)
                    .shrinkArcDuration(shrinkArcDuration)
                    .build();
            attrArray.recycle();
        }
    }


    /**
     * We need to draw a new view with the arc over the FAB, to be able to hide the fab shadow
     * (if it exists).
     */
    protected void addArcView() {
        setClipChildren(false);
        progressArc = new ProgressArcView(getContext(), mArcColor, mArcWidth, mRoundedStroke,
                mProgressArcAnimationDuration);
        progressArc.setInternalListener(this);
        addView(progressArc,
                new LayoutParams(getFabDimension() + mArcWidth, getFabDimension() + mArcWidth,
                        Gravity.CENTER));
    }

    protected void setupFab() {
        LayoutParams fabParams = (LayoutParams) getChildAt(0).getLayoutParams();
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
    protected void checkChildCount() {
        if (getChildCount() != 1) {
            throw new IllegalStateException(getResources().getString(R.string.child_count_error));
        }
    }


    public void beginFinalAnimation() {
        progressArc.requestCompleteAnimation();
    }

    protected int getFabDimension() {
        if (mCircleSize == SIZE_NORMAL) {
            return getResources().getDimensionPixelSize(R.dimen.fab_size_normal);
        } else {
            return getResources().getDimensionPixelSize(R.dimen.fab_size_mini);
        }
    }

    @Override
    public void onArcAnimationComplete() {
        handleArcAnimationComplete();
    }

    public int getArcColor() {
        return mArcColor;
    }

    public void setArcColor(@ColorInt int arcColor) {
        mArcColor = arcColor;
        invalidate();
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(@DimenRes int arcWidth) {
        mArcWidth = arcWidth;
        invalidate();
    }

    public int getCircleSize() {
        return mCircleSize;
    }

    /**
     * Set circle size of the Fab
     *
     * @param circleSize the cercleSize type between {@link CircleSizeType#CIRCLE_NORMAL}
     *                   or {@link CircleSizeType#CIRCLE_MINI}
     */
    public void setCircleSize(@CircleSizeDef int circleSize) {
        if (circleSize > CircleSizeType.CIRCLE_MINI
                || circleSize < CircleSizeType.CIRCLE_NORMAL) {
            throw new IllegalArgumentException("Invalid CircleSize. Must be "
                    + "CircleSizeType.CIRCLE_MINI or CircleSizeType.CIRCLE_NORMAL");
        }
        mCircleSize = circleSize;
        invalidate();
    }

    public boolean isRoundedStroke() {
        return mRoundedStroke;
    }

    public void setRoundedStroke(boolean roundedStroke) {
        mRoundedStroke = roundedStroke;
        invalidate();
    }

    /**
     * Set complete arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setCompleteArcDuration(int duration) {
        mProgressArcAnimationDuration = new ProgressArcAnimationDuration.Builder()
                .completeArcDuration(duration)
                .rotationArcDuration(mProgressArcAnimationDuration.getRotationArcDuration())
                .growArcDuration(mProgressArcAnimationDuration.getGrowArcDuration())
                .shrinkArcDuration(mProgressArcAnimationDuration.getShrinkArcDuration())
                .build();
        invalidate();
    }

    /**
     * @return the arc complete duration in milliseconds
     */
    public int getCompleteArcDuration() {
        return mProgressArcAnimationDuration.getCompleteArcDuration();
    }

    /**
     * Set rotation arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setRotationArcDuration(int duration) {
        mProgressArcAnimationDuration = new ProgressArcAnimationDuration.Builder()
                .rotationArcDuration(duration)
                .completeArcDuration(mProgressArcAnimationDuration.getCompleteArcDuration())
                .growArcDuration(mProgressArcAnimationDuration.getGrowArcDuration())
                .shrinkArcDuration(mProgressArcAnimationDuration.getShrinkArcDuration())
                .build();
        invalidate();
    }

    /**
     * @return the arc rotation duration in milliseconds
     */
    public int getRotationArcDuration() {
        return mProgressArcAnimationDuration.getRotationArcDuration();
    }

    /**
     * Set grow arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setGrowArcDuration(int duration) {
        mProgressArcAnimationDuration = new ProgressArcAnimationDuration.Builder()
                .growArcDuration(duration)
                .completeArcDuration(mProgressArcAnimationDuration.getCompleteArcDuration())
                .rotationArcDuration(mProgressArcAnimationDuration.getRotationArcDuration())
                .shrinkArcDuration(mProgressArcAnimationDuration.getShrinkArcDuration())
                .build();
        invalidate();
    }

    /**
     * @return the arc grow duration in milliseconds
     */
    public int getGrowArcDuration() {
        return mProgressArcAnimationDuration.getGrowArcDuration();
    }

    /**
     * Set shrink arc duration
     *
     * @param duration the duration in milliseconds
     */
    public void setShrinkArcDuration(int duration) {
        mProgressArcAnimationDuration = new ProgressArcAnimationDuration.Builder()
                .shrinkArcDuration(duration)
                .completeArcDuration(mProgressArcAnimationDuration.getCompleteArcDuration())
                .rotationArcDuration(mProgressArcAnimationDuration.getRotationArcDuration())
                .growArcDuration(mProgressArcAnimationDuration.getShrinkArcDuration())
                .build();
        invalidate();
    }

    /**
     * @return the arc shrink duration in milliseconds
     */
    public int getShrinkArcDuration() {
        return mProgressArcAnimationDuration.getShrinkArcDuration();
    }
}
