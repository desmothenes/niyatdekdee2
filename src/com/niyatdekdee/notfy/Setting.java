package com.niyatdekdee.notfy;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_setting);
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
/*        Preference myPref = (Preference) findPreference("myKey");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
             			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
						<PreferenceScreen android:title="website">
						    <intent
						        android:action="android.intent.action.VIEW"
						        android:data="http://www.example.com"
						        />
						</PreferenceScreen>;
						return false;
                         //open browser or intent here
                     }
                 });*/
    }
    
    public static boolean getCheckSetting(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("keyCheck", true);
    }
    public static boolean getAutoAdd(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoAdd", false);
    }
    public static String getSelectItemSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("keySelectItem", "0");
    }
    public static boolean getSelectImageSetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("image", true);
    }
    public static boolean getSelectNotifySetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notify", false);
    }
    public static long getSelectNotifyTimeSetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getLong("alarm_time", System.currentTimeMillis());
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		System.out.println(key+" change");
		if (key.equals("notify")) {
			System.out.println("notify change");
			if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notify", false)) {
				new AutoStart().CancelAlarm(getApplicationContext());
				new AutoStart().SetAlarm(getApplicationContext());
			}
			else {
				new Alarm().CancelAlarm(getApplicationContext());
				new AutoStart().CancelAlarm(getApplicationContext());
			}
		}
		else if (key.equals("keySelectItem")) {
			System.out.println("keySelectItem change");
				new AutoStart().CancelAlarm(getApplicationContext());
				new AutoStart().SetAlarm(getApplicationContext());
		}
		else if (key.equals("alarm_time")) {
			System.out.println("alarm_time change");
				new AutoStart().CancelAlarm(getApplicationContext());
				new AutoStart().SetAlarm(getApplicationContext());
		}
		
        System.out.println(Setting.getSelectNotifySetting(getApplicationContext()));
        System.out.println(Setting.getSelectItemSetting(getApplicationContext()));
	}

}
