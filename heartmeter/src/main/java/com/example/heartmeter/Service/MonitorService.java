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



public class MonitorService extends BaseMonitorService implements SharedPreferences.OnSharedPreferenceChangeListener {
    //震动变量
    private boolean use_vibrator;
    //铃声变量
    private boolean use_ringtone;
    //要播放的铃声
    private Uri uri_ringtone;
    //紧急联系人电话
    private String emergency_number;

    @Override
    public void onCreate() {
        super.onCreate();
        //读取检测设置
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        //震动设置
        use_vibrator = preferences.getBoolean("preference_use_vibration", true);
        //铃声设置
        use_ringtone = preferences.getBoolean("preference_use_sound", true);
        if (use_ringtone) {
            //读取铃声设置
            uri_ringtone = Uri.parse(preferences.getString("preference_ringtone",
                    RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
                            .toString()));
        }
        emergency_number = preferences.getString("preference_emergency_number", "120");
        getLocalBroadcastManager().registerReceiver(new SignsReceiver(), new IntentFilter(HubService.ACTION_HEART_RATE_REDUCED));
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *监听设置变动，如有更改就更新
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //只要点击就更新
        switch (key) {
            case "preference_use_vibration":
                use_vibrator = sharedPreferences.getBoolean(key, true);
                break;
            case "preference_use_sound":
                use_ringtone = sharedPreferences.getBoolean(key, true);
                break;
            case "preference_ringtone":
                uri_ringtone = Uri.parse(sharedPreferences.getString(key,
                        RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
                                .toString()));
            case "preference_emergency_number":
                emergency_number = sharedPreferences.getString("preference_emergency_number", "120");
                break;
        }
    }

    private class SignsReceiver extends BroadcastReceiver {
        //紧急处理下限变量
        private static final int EMERGENCY_THRESHOLD = 5;
        private int emergency_counter = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            int heart_rate = intent.getIntExtra(HubService.KEY_DATA, 60);
            //规定紧急情况的心率数值
            if (heart_rate < 40) {
                emergency_counter++;
            }
            //根据用户设定处理紧急情况处理办法
            if (emergency_counter > EMERGENCY_THRESHOLD) {
                if (use_vibrator) {
                    vibrate(-1, 750, 500, 750, 500);
                }
                if (use_ringtone) {
                    scream(uri_ringtone);
                }
                call(emergency_number);
                emergency_counter = 0;
            }
        }
    }
}
