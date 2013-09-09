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

package com.noisepages.nettoyeur.patchfield.csound;

import android.app.Notification;

import com.noisepages.nettoyeur.patchfield.AudioModule;

import csnd6.Csound;
import csnd6.SWIGTYPE_p_CSOUND_;

public class CsoundModule extends AudioModule {

  static {
    System.loadLibrary("pfcsound");
  }

  private final Csound csound;
  private long ptr = 0;

  // Slightly awkward hack to get underlying C pointer.
  private static class CsoundHelper extends SWIGTYPE_p_CSOUND_ {
    private static long getPointer(Csound csound) {
      return SWIGTYPE_p_CSOUND_.getCPtr(csound.GetCsound());
    }
  }

  public CsoundModule(Csound csound, Notification notification) {
    super(notification);
    this.csound = csound;
  }

  @Override
  protected boolean configure(String name, long handle, int sampleRate, int bufferSize) {
    if (csound.GetSr() != sampleRate) {
      throw new IllegalStateException("Csound sample rate differs from Patchfield sample rate.");
    }
    if (ptr != 0) {
      throw new IllegalStateException("Already configured.");
    }
    ptr = configureNativeComponents(handle, CsoundHelper.getPointer(csound), bufferSize);
    return ptr != 0;
  }

  @Override
  protected void release() {
    if (ptr != 0) {
      release(ptr);
      ptr = 0;
    }
  }

  @Override
  public int getInputChannels() {
    return csound.GetNchnlsInput();
  }

  @Override
  public int getOutputChannels() {
    return csound.GetNchnls();
  }

  private native long configureNativeComponents(long handle, long cPtr, int bufferSize);

  private native void release(long ptr);
}
