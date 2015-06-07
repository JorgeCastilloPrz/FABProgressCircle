FABProgressCircle
=================
[![Build Status](https://travis-ci.org/JorgeCastilloPrz/FABProgressCircle.svg?branch=master)](https://travis-ci.org/JorgeCastilloPrz/FABProgressCircle) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FABProgressCircle-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1942)
Android library to provide a material progress circle around your FloatingActionButton. This component is **compatible with any existent FAB**.

![Demo Screenshot][1]

![Demo Screenshot2][2]

[Youtube FABProgressCircle video](http://www.youtube.com/watch?v=sP-Ng7e8k6g)
 
`FABProgressCircle` follows material guidelines at 100%. Links to Google samples of this behavior:

* [Sample video from Google][material-sample-2]

How to use
----------
You can use the `FABProgressCircle` to wrap **any existent FAB**. Here you have an example wrapping the **Google** FloatingActionButton from the brand
new [Design Support Library][google-design-support].
```xml
<com.github.jorgecastilloprz.FABProgressCircle
    android:id="@+id/fabProgressCircle"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    >

  <!-- You can change it with the fab from the library you want to use -->
  <android.support.design.widget.FloatingActionButton
      android:id="@+id/fab"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:src="@drawable/ic_upload"
      app:rippleColor="@color/fab_ripple"
      app:elevation="@dimen/fab_elevation"
      app:borderWidth="0dp"
      />

</com.github.jorgecastilloprz.FABProgressCircle>
```
To show the progress circle, call the `show()` method into the normal click/touch listener of your fab:
```java
fabView.setOnClickListener(new View.OnClickListener() {
  @Override public void onClick(View view) {
    fabProgressCircle.show();
    startYourAsynchronousJob();
  }
});
```
The progress animation will be indeterminate at the beginning, as you can't really know how long is the asynchronous call going to take to complete.

**IMPORTANT:** The animation will start playing everytime the user clicks on the button. Developers should control the potential spam click in their fab buttons,
to not allow the users to click on it if the asynchronous task and the progress animation are already running / playing at that very moment. I rather leaving that behavior
to every dev using this library.

To play the completion animation, you will need to call:
```java
fabProgressCircle.beginFinalAnimation();
```
When the completion animation is displayed, the fab gets transformed to its final appearance, and **it becomes not clickable** anymore. This behavior is intentional.
If something happens to the asynchronous task running (some kind of error), you can always stop the animation by doing:
```java
fabProgressCircle.hide();
```
For listening to the completion animation, your class needs to implement the `FABProgressListener` interface:
```java
fabProgressCircle.attachListener(this);
```
If you do that, the following method call will be dispatched at the right time. The Snackbar creation is just an example:
```java
@Override public void onFABProgressAnimationEnd() {
    Snackbar.make(fabProgressCircle, R.string.cloud_upload_complete, Snackbar.LENGTH_LONG)
        .setAction("Action", null)
        .show();
}
```

Custom Attributes
-----------------
Even if i want the component to respect material guidelines, you can customize some parameters to adapt it a little bit for your application UI/UX.
Here they are:

* `app:arcColor="@color/progressArcColor"`: Sets the color for the arc, and for the final aspect of the FAB (after the transform animation).
* `app:arcWidth="@dimen/arcStrokeWidth"`: Stroke width for the progress circle.
* `app:finalIcon="@drawable/ic_done"`: By default, this library uses the typical `ic_done` icon at the end of the animation. Normally i would
rather not to change it, but feel free to do it if you need to.

Of course, anyone of the custom attrs can be used with resource references (`@dimen`, `@color`, `@integer` ...) or just literal values.
Dont forget to add the namespace declaration to your xml file. You can change the namespace name from `app` to anything you want.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```
Code sample
```xml
<com.github.jorgecastilloprz.FABProgressCircle
    android:id="@+id/fabProgressCircle"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:arcColor="#00ffff"
    app:arcWidth="7dp"
    app:finalIcon="@drawable/ic_bookmark_complete"
    >

  <android.support.design.widget.FloatingActionButton
      ...
      />

</com.github.jorgecastilloprz.FABProgressCircle>
```
Remember that the FAB customization / configuration depends on the FAB library, not on this one. This is just a component used to wrap it visually.
I created it by this way to make it totally compatible with any fab library you might be using into your app.

Mini Size
---------
Mini size is totally supported, so feel free to use the `app:fabSize="mini"` custom attribute on the Google FAB, or the corresponding `mini` custom
attribute of the fab library you are using.

![Demo Screenshot3][3]

Supported most used FAB libraries
---------------------------------
* [FutureSimple android-floating-action-button][4]
* [Makovkastar android-floating-action-button][5]

Known issues
------------
* Still not compatible with complex fab modes like the `FloatingActionsMenu` from the [futuresimple library][4]. I will work on that as soon as possible.
* Not tested for [makovkastar library][5] hidding / showing mode when attached to some `ListView` / `RecyclerView`. But it will pretty much fail (i think).

Pull requests to solve the above stated issues will be very welcome.

Contributions
-------------
Feel free to send `Pull Requests` to this repository if you feel that it lacks some functionality. I will be pleased to accept or discuss about them.
However, **Material Design guidelines will be required**.

Add it to your project
----------------------
If you are working with gradle, add the dependency to your `build.gradle` file:
```groovy
dependencies{
    compile 'com.github.jorgecastilloprz:fabprogresscircle:1.0@aar'
}
```
if you are working with maven, do it into your `pom.xml`
```xml
<dependency>
    <groupId>com.github.jorgecastilloprz</groupId>
    <artifactId>fabprogresscircle</artifactId>
    <version>1.0</version>
    <type>aar</type>
</dependency>
```

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://www.linkedin.com/in/jorgecastilloprz">
  <img alt="Add me to Linkedin" src="https://github.com/JorgeCastilloPrz/EasyMVP/blob/master/art/linkedin.png" />
</a>

License
-------

    Copyright 2015 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[material-sample-1]: http://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B0NGgBg38lWWbTlSaHdQZEMtalk/components-progressactivity-typesofindicators-061101_Circular_Aspirational_xhdpi_002.webm
[material-sample-2]: http://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsa0QwVnd6NVlvWkk/components-progressactivity-typesofindicators-Circular_wFab_xhdpi_003.webm
[google-design-support]: http://developer.android.com/tools/support-library/features.html#design
[1]: ./art/fabprogresscircle.gif
[2]: ./art/fabprogresscircle2.gif
[3]: ./art/fabprogressmini.gif
[4]: https://github.com/futuresimple/android-floating-action-button
[5]: https://github.com/makovkastar/FloatingActionButton