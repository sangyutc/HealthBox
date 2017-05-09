package com.presisco.shared.service;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseMonitorService extends Service {
    private LocalBroadcastManager mBroadcastManager;
    private Vibrator mVibrator;
    private RingtoneManager mRingtoneManager;

    public BaseMonitorService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mRingtoneManager = new RingtoneManager(this);
    }

    protected LocalBroadcastManager getLocalBroadcastManager() {
        return mBroadcastManager;
    }

    protected void vibrate(int repeat, long... pattern) {
        mVibrator.vibrate(pattern, repeat);
    }

    protected void scream(Uri uri_ringtone) {
        int cursor_pos = mRingtoneManager.getRingtonePosition(uri_ringtone);
        Ringtone ringtone = mRingtoneManager.getRingtone(cursor_pos);
        ringtone.play();
    }

    @Override
    public void onDestroy() {

    }
}
