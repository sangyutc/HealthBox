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
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri_ringtone);
        ringtone.play();
    }

    protected void call(String number) {
        Intent call_intent = new Intent(Intent.ACTION_CALL);
        call_intent.setData(Uri.parse("tel:" + number));
        try {
            startActivity(call_intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }
}
