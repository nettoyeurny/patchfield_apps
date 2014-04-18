/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.noisepages.nettoyeur.patchfield.java;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

import com.noisepages.nettoyeur.patchfield.PatchfieldActivity;

public class JavaModuleActivity extends PatchfieldActivity {

  private JavaModule module = null;
  private final String moduleLabel = "java";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onDestroy() {
    if (patchfield != null) {
      try {
        module.release(patchfield);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    super.onDestroy();
  }

  @Override
  protected void onPatchfieldConnected() {
    try {
      PendingIntent pi = PendingIntent.getActivity(
          JavaModuleActivity.this, 0, new Intent(
              JavaModuleActivity.this,
              JavaModuleActivity.class), 0);
      Notification notification = new Notification.Builder(JavaModuleActivity.this)
          .setSmallIcon(R.drawable.emo_im_happy)
          .setContentTitle("JavaModule").setContentIntent(pi).build();
      module = new JavaModule(64, 2, 2, notification) {
        @Override
        protected void process(int sampleRate, int bufferSize, int inputChannels,
            float[] inputBuffer, int outputChannels, float[] outputBuffer) {
          // Switch channels, just to show that we're doing something here.
          System.arraycopy(inputBuffer, 0, outputBuffer, bufferSize, bufferSize);
          System.arraycopy(inputBuffer, bufferSize, outputBuffer, 0, bufferSize);
        }
      };
      module.configure(patchfield, moduleLabel);
      patchfield.activateModule(moduleLabel);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
  }

  @Override
  protected void onPatchfieldDisconnected() {
    // Do nothing.
  }
}
