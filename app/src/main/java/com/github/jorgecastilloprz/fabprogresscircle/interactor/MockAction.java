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
package com.github.jorgecastilloprz.fabprogresscircle.interactor;

import com.github.jorgecastilloprz.fabprogresscircle.executor.Interactor;
import com.github.jorgecastilloprz.fabprogresscircle.executor.MainThread;
import com.github.jorgecastilloprz.fabprogresscircle.executor.MainThreadImpl;

/**
 * @author Jorge Castillo Pérez
 */
public class MockAction implements Interactor {

  private MockActionCallback callback;
  private MainThread mainThread;

  public MockAction(MockActionCallback callback) {
    this.callback = callback;
    this.mainThread = new MainThreadImpl();
  }

  @Override public void run() {
    mockLoadingTime();
    notifyActionComplete();
  }

  private void mockLoadingTime() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      //Empty
    }
  }

  private void notifyActionComplete() {
    mainThread.post(new Runnable() {
      @Override public void run() {
        callback.onMockActionComplete();
      }
    });
  }
}
