package com.github.jorgecastilloprz.fabprogresscircle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.github.jorgecastilloprz.fabprogresscircle.picasso.GrayscaleCircleTransform;
import com.github.jorgecastilloprz.library.FABProgressCircle;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    loadAvatar();
    attachListeners();
  }

  private void loadAvatar() {
    ImageView avatarView = (ImageView) findViewById(R.id.avatar);
    Picasso.with(this)
        .load(R.drawable.avatar)
        .transform(new GrayscaleCircleTransform())
        .into(avatarView);
  }

  private void attachListeners() {
    final FABProgressCircle fabCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
    findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        fabCircle.show();
      }
    });
  }
}