package com.example.heartmeter.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.presisco.shared.service.BaseMonitorService;

/**
 * Created by presisco on 2017/5/9.
 */

public class MonitorService extends BaseMonitorService {
    private boolean use_vibrator;
    private boolean use_ringtone;
    private Uri uri_ringtone;
    private String emergency_number;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        use_vibrator = preferences.getBoolean("preference_use_vibration", true);
        use_ringtone = preferences.getBoolean("preference_use_sound", true);
        if (use_ringtone) {
            uri_ringtone = Uri.parse(preferences.getString("preference_ringtone",
                    RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
                            .toString()));
        }
        emergency_number = preferences.getString("preference_emergency_number", "120");
        getLocalBroadcastManager().registerReceiver(new SignsReceiver(), new IntentFilter(HubService.ACTION_HEART_RATE_REDUCED));
    }

    private class SignsReceiver extends BroadcastReceiver {
        private static final int EMERGENCY_THRESHOLD = 5;
        private int emergency_counter = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            int heart_rate = intent.getIntExtra(HubService.KEY_DATA, 60);
            if (heart_rate < 40) {
                emergency_counter++;
            }
            if (emergency_counter > EMERGENCY_THRESHOLD) {
                if (use_vibrator) {
                    vibrate(-1, 750, 500, 750, 500);
                }
                if (use_ringtone) {
                    scream(uri_ringtone);
                }
                Intent call_intent = new Intent(Intent.ACTION_CALL);
                call_intent.setData(Uri.parse("tel:" + emergency_number));
                try {
                    startActivity(call_intent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                emergency_counter = 0;
            }
        }
    }
}
