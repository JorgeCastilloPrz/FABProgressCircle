package com.github.jorgecastilloprz.fabprogresscircle;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.fabprogresscircle.executor.ThreadExecutor;
import com.github.jorgecastilloprz.fabprogresscircle.interactor.MockAction;
import com.github.jorgecastilloprz.fabprogresscircle.interactor.MockActionCallback;
import com.github.jorgecastilloprz.fabprogresscircle.picasso.GrayscaleCircleTransform;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity implements MockActionCallback, FABProgressListener {

  private FABProgressCircle fabProgressCircle;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initViews();
    loadAvatar();
    attachListeners();
  }

  private void initViews() {
    fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
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

    findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        fabProgressCircle.show();
        runMockInteractor();
      }
    });
  }

  private void runMockInteractor() {
    ThreadExecutor executor = new ThreadExecutor();
    executor.run(new MockAction(this));
  }

  @Override public void onMockActionComplete() {
    //fabProgressCircle.beginFinalAnimation();
    fabProgressCircle.hide();
  }

  @Override public void onFABProgressAnimationEnd() {
    Snackbar.make(fabProgressCircle, R.string.cloud_upload_complete, Snackbar.LENGTH_LONG)
        .setAction("Action", null)
        .show();
  }
}