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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.layout.activity_setting);
        setTitle("\tตั่งค่า");


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
    
    public static boolean getisLogin(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isLogin", false);
    }
    public static boolean getCheckSetting(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("keyCheck", true);
    }
    public static boolean getAutoAdd(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoAdd", false);
    }
    public static boolean getNotifyFav(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifyFov", false);
    }
    public static boolean getdisplayResult(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("displayResult", false);
    }
    public static boolean getonlyFavorite(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("onlyFavorite", false);
    }
    public static String getSelectItemSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("keySelectItem", "24");
    }
    public static String getArrowSelectSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("arrowSelect", "0");
    }
    
    public static String getColorSelectSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("colorSelect", "1");
    }
    public static String getPosttext(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("postText", "นิรนาม");
    }
    public static boolean getSelectImageSetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("image", true);
    }
    public static boolean getSelectNotifySetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notify", true);
    }
    public static long getSelectNotifyTimeSetting(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getLong("alarm_time", System.currentTimeMillis());
    }    
    
    public static int getspeechspeed(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getInt("speechRate", 70);
    }    
    public static int getspeechpitch(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getInt("speechPitch", 100);
    }
    
    public static String getUserName(Context context) {
    	System.out.println(PreferenceManager.getDefaultSharedPreferences(context).getString("UserName", "-1"));
    	return UserPreference.decrypt(PreferenceManager.getDefaultSharedPreferences(context).getString("UserName", "-1"),context).replace("จนฟหสดฟ", "");
    }
    public static String getPassWord(Context context) {
    	System.out.println(PreferenceManager.getDefaultSharedPreferences(context).getString("PassWord", "-1"));
    	return passPreference.decrypt(PreferenceManager.getDefaultSharedPreferences(context).getString("PassWord", "-1"),context).replace("¢", "");
    }
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		//System.out.println(key+" change");
		if (key.equals("notify")) {
			//System.out.println("notify change");
			if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notify", false)) {
				new Alarm().CancelAlarm(getApplicationContext());
				new Alarm().SetAlarm(getApplicationContext());
			}
			else {
				new Alarm().CancelAlarm(getApplicationContext());
				new Alarm().CancelAlarm(getApplicationContext());
			}
		}
		else if (key.equals("keySelectItem")) {
			System.out.println("keySelectItem change");
				new Alarm().CancelAlarm(getApplicationContext());
				new Alarm().SetAlarm(getApplicationContext());
		}
		else if (key.equals("alarm_time")) {
			//System.out.println("alarm_time change");
				new Alarm().CancelAlarm(getApplicationContext());
				new Alarm().SetAlarm(getApplicationContext());
		}
		
		/*else if (key.equals("UserName")) {
			byte[] UserName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("UserName", "0").getBytes();
			byte[] keyStart = "this is a key".getBytes();
			SecureRandom sr = null;
			KeyGenerator kgen = null;
			try {
				kgen = KeyGenerator.getInstance("AES");
				sr = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sr.setSeed(keyStart);
			kgen.init(128, sr);
			SecretKey skey = kgen.generateKey();
			byte[] key1 = skey.getEncoded();   
			// encrypt
			try {
				byte[] encryptedData = encrypt(key1,UserName);
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				editor.putString("UserName", encryptedData.toString());
				editor.commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (key.equals("PassWord")) {
			byte[] UserName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PassWord", "0").getBytes();
			byte[] keyStart = "this is a key".getBytes();
			SecureRandom sr = null;
			KeyGenerator kgen = null;
			try {
				kgen = KeyGenerator.getInstance("AES");
				sr = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sr.setSeed(keyStart);
			kgen.init(128, sr);
			SecretKey skey = kgen.generateKey();
			byte[] key1 = skey.getEncoded();   
			// encrypt
			try {
				byte[] encryptedData = encrypt(key1,UserName);
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				editor.putString("PassWord", encryptedData.toString());
				editor.commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
        //System.out.println(Setting.getSelectNotifySetting(getApplicationContext()));
        //System.out.println(Setting.getSelectItemSetting(getApplicationContext()));
	}
	

}
