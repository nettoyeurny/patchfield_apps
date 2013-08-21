package com.noisepages.nettoyeur.pdeffectsrack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.noisepages.nettoyeur.patchfield.IPatchfieldService;
import com.noisepages.nettoyeur.patchfield.pd.PdModule;

public class MainActivity extends Activity {

  private static final String TAG = "PdEffectsRack";

  private IPatchfieldService patchfield = null;

  private final String label = "PdEffectsRack";
  private PdModule module = null;
  private int patch = -1;

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
      patchfield = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG, "Service connected.");
      patchfield = IPatchfieldService.Stub.asInterface(service);
      int inputChannels = 2;
      int outputChannels = 2;
      PendingIntent pi =
          PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this,
              MainActivity.class), 0);
      Notification notification = new Notification.Builder(MainActivity.this)
          .setSmallIcon(R.drawable.pd_icon)
          .setContentTitle("PdEffectsRack")
          .setContentIntent(pi)
          .build();
      try {
        // Create PdModule instance before invoking any methods on PdBase.
        module =
            PdModule.getInstance(patchfield.getSampleRate(), inputChannels, outputChannels, notification);
        PdBase.setReceiver(new PdDispatcher() {
          @Override
          public void print(String s) {
            Log.i(TAG, s);
          }
        });
        File pdFile = IoUtils.find(getCacheDir(), "effects.pd").get(0);
        Log.i(TAG, "pdFile: " + pdFile);
        patch = PdBase.openPatch(pdFile);
        module.configure(patchfield, label);
        patchfield.activateModule(label);
      } catch (RemoteException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SeekBar durationBar = (SeekBar) findViewById(R.id.durationBar);
    durationBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override public void onStopTrackingTouch(SeekBar seekBar) {}
      @Override public void onStartTrackingTouch(SeekBar seekBar) {}
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PdBase.sendMessage("params", "basedur", 10 * progress);
      }
    });
    SeekBar feedbackBar = (SeekBar) findViewById(R.id.feedbackBar);
    feedbackBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override public void onStopTrackingTouch(SeekBar seekBar) {}
      @Override public void onStartTrackingTouch(SeekBar seekBar) {}
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PdBase.sendMessage("params", "feedback", 0.0098 * progress);
      }
    });
    SeekBar bpqBar = (SeekBar) findViewById(R.id.bpqBar);
    bpqBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override public void onStopTrackingTouch(SeekBar seekBar) {}
      @Override public void onStartTrackingTouch(SeekBar seekBar) {}
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PdBase.sendMessage("params", "bq", 0.2 * progress);
      }
    });
    SeekBar bpfBar = (SeekBar) findViewById(R.id.bpfBar);
    bpfBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override public void onStopTrackingTouch(SeekBar seekBar) {}
      @Override public void onStartTrackingTouch(SeekBar seekBar) {}
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PdBase.sendMessage("params", "bpitch", 1.27 * progress);
      }
    });
    SeekBar dryBar = (SeekBar) findViewById(R.id.dryBar);
    dryBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override public void onStopTrackingTouch(SeekBar seekBar) {}
      @Override public void onStartTrackingTouch(SeekBar seekBar) {}
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        PdBase.sendMessage("params", "dry", 0.01 * progress);
      }
    });
    bindService(new Intent("IPatchFieldService"), connection, Context.BIND_AUTO_CREATE);
    InputStream in = getResources().openRawResource(R.raw.effects);
    try {
      IoUtils.extractZipResource(in, getCacheDir());
    } catch (IOException e) {
      e.printStackTrace();
      finish();
    }
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
    if (patch >= 0) {
      PdBase.closePatch(patch);
    }
    PdBase.release();
    unbindService(connection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

}
