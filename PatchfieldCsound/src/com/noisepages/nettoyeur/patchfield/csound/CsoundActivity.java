package com.noisepages.nettoyeur.patchfield.csound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;

import com.noisepages.nettoyeur.dafx.stereo.R;
import com.noisepages.nettoyeur.patchfield.IPatchfieldService;

import csnd6.Csound;

public class CsoundActivity extends Activity {

  private static final String TAG = "CsoundTest";

  private IPatchfieldService patchfield = null;

  private Csound csound;
  private CsoundModule module = null;

  private final String moduleLabel = "csound";

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
      patchfield = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG, "Service connected.");
      patchfield = IPatchfieldService.Stub.asInterface(service);
      PendingIntent pi =
          PendingIntent.getActivity(CsoundActivity.this, 0, new Intent(CsoundActivity.this,
              CsoundActivity.class), 0);
      Notification notification =
          new Notification.Builder(CsoundActivity.this).setSmallIcon(R.drawable.emo_im_happy)
              .setContentTitle("StereoModule").setContentIntent(pi).build();
      try {
        Log.i(TAG, "Creating module.");
        module = new CsoundModule(csound, notification);
        module.configure(patchfield, moduleLabel);
        patchfield.activateModule(moduleLabel);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initCsound();
    bindService(new Intent("IPatchfieldService"), connection, Context.BIND_AUTO_CREATE);
  }

  private void initCsound() {
    csound = new Csound();
    String csd = getResourceFileAsString(R.raw.trapped);
    File f = createTempFile(csd);
    int res = csound.Compile(f.getAbsolutePath());
    Log.i("initCsound", "result: " + res);
  }

  private String getResourceFileAsString(int resId) {
    StringBuilder str = new StringBuilder();
    InputStream is = getResources().openRawResource(resId);
    BufferedReader r = new BufferedReader(new InputStreamReader(is));
    String line;
    try {
      while ((line = r.readLine()) != null) {
        str.append(line).append("\n");
      }
    } catch (IOException ios) {

    }
    return str.toString();
  }

  private File createTempFile(String csd) {
    File f = null;
    try {
      f = File.createTempFile("temp", ".csd", this.getCacheDir());
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(csd.getBytes());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return f;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (patchfield != null) {
      try {
        module.release(patchfield);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    unbindService(connection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
