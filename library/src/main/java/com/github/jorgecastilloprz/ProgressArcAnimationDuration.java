package com.github.jorgecastilloprz;

/**
 * Contains all anim duration
 */
class ProgressArcAnimationDuration {

    private int mRotationArcDuration;
    private int mShrinkArcDuration;
    private int mGrowArcDuration;
    private int mCompleteArcDuration;


    private ProgressArcAnimationDuration() {
    }

    private ProgressArcAnimationDuration(Builder builder) {
        mRotationArcDuration = builder.mRotationDuration;
        mShrinkArcDuration = builder.mShrinkDuration;
        mGrowArcDuration = builder.mGrowDuration;
        mCompleteArcDuration = builder.mCompleteDuration;
    }


    public static class Builder {
        private int mRotationDuration = ArcAnimationFactory.ROTATE_ANIM_DURATION;
        private int mShrinkDuration = ArcAnimationFactory.SWEEP_ANIM_DURATION;
        private int mGrowDuration = ArcAnimationFactory.SWEEP_ANIM_DURATION;
        private int mCompleteDuration = ArcAnimationFactory.COMPLETE_ANIM_DURATION;

        public Builder rotationArcDuration(int rotationDuration) {
            mRotationDuration = rotationDuration;
            return this;
        }

        public Builder shrinkArcDuration(int shrinkDuration) {
            mShrinkDuration = shrinkDuration;
            return this;
        }

        public Builder growArcDuration(int growDuration) {
            mGrowDuration = growDuration;
            return this;
        }

        public Builder completeArcDuration(int completeDuration) {
            mCompleteDuration = completeDuration;
            return this;
        }

        public ProgressArcAnimationDuration build() {
            return new ProgressArcAnimationDuration(this);
        }
    }

    public int getRotationArcDuration() {
        return mRotationArcDuration;
    }

    public int getShrinkArcDuration() {
        return mShrinkArcDuration;
    }

    public int getGrowArcDuration() {
        return mGrowArcDuration;
    }

    public int getCompleteArcDuration() {
        return mCompleteArcDuration;
    }
}
