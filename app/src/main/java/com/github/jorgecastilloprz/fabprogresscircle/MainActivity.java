package com.github.jorgecastilloprz.fabprogresscircle;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.FABProgressListener;
import com.github.jorgecastilloprz.fabprogresscircle.executor.ThreadExecutor;
import com.github.jorgecastilloprz.fabprogresscircle.interactor.MockAction;
import com.github.jorgecastilloprz.fabprogresscircle.interactor.MockActionCallback;
import com.github.jorgecastilloprz.fabprogresscircle.picasso.GrayscaleCircleTransform;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity implements MockActionCallback, FABProgressListener {

    private FloatingActionButton mFloatingActionButton;
    private FABProgressCircle fabProgressCircle;
    private boolean taskRunning = false;
    private ThreadExecutor mExecutor;
    private boolean mIsFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadAvatar();
        attachListeners();
    }

    private void initViews() {
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void loadAvatar() {
        ImageView avatarView = (ImageView) findViewById(R.id.avatar);
        Picasso.with(this)
                .load(R.drawable.avatar)
                .transform(new GrayscaleCircleTransform())
                .into(avatarView);
    }

    private void attachListeners() {
        fabProgressCircle.attachListener(this);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskRunning) {
                    fabProgressCircle.show();
                    runMockInteractor();
                }
            }
        });
    }

    private void runMockInteractor() {
        mExecutor = new ThreadExecutor();
        mExecutor.run(new MockAction(this));
        taskRunning = true;
    }

    @Override
    public void onMockActionComplete() {
        taskRunning = false;
        fabProgressCircle.beginFinalAnimation();
        //fabProgressCircle.hide();
    }

    @Override
    public void onFABProgressAnimationEnd() {
        mIsFavorite = !mIsFavorite;
        int colorSelected = getResources().getColor(R.color.fab_orange_dark);
        int colorNotSelected = getResources().getColor(android.R.color.white);
        ValueAnimator colorAnimator;
        if (mIsFavorite) {
             colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    colorNotSelected, colorSelected);
        } else {
            colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                    colorSelected, colorNotSelected);
        }
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                Drawable d = mFloatingActionButton.getDrawable();
                d = DrawableCompat.wrap(d);
                d = d.mutate();
                DrawableCompat.setTint(d, color);
                mFloatingActionButton.setImageDrawable(d);
            }
        });
        colorAnimator.setDuration(250);
        colorAnimator.start();
        Snackbar.make(fabProgressCircle, R.string.cloud_upload_complete, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }
}