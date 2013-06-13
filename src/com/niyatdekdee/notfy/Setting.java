package com.niyatdekdee.notfy;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Map.Entry;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.layout.activity_setting);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTitle("ตั่งค่า");

        Preference button = (Preference) findPreference("BackUp");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                //code for what you want it to do
                saveSharedPreferencesToFile();
                return true;
            }
        });
        Preference button2 = (Preference) findPreference("Restore");
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                //code for what you want it to do
                loadSharedPreferencesFromFile();
                return true;
            }
        });
    }

    private boolean saveSharedPreferencesToFile() {
        try {
            DatabaseAdapter db = new DatabaseAdapter(getBaseContext());
            db.close();
            if (MainActivity.db != null) MainActivity.db.close();
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.niyatdekdee.notfy" + "//databases//" + "NiyayDekD";
                String backupDBPath = "NiyayDekD";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "เก็บไว้ในไฟล์ชื่อ NiyayDekD, niyay_saveSharedPreferences", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            Log.d(getLocalClassName(), e.toString());
        }

        boolean res = false;
        File dst = new File(Environment.getExternalStorageDirectory(), "niyay_saveSharedPreferences");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref = getSharedPreferences(this.getPreferenceManager().getSharedPreferencesName(), MODE_PRIVATE);
            output.writeObject(pref.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    @SuppressWarnings({"unchecked"})
    private boolean loadSharedPreferencesFromFile() {
        try {
            DatabaseAdapter db = new DatabaseAdapter(getBaseContext());
            db.close();
            if (MainActivity.db != null) MainActivity.db.close();
            //getApplicationContext().deleteDatabase(getApplicationContext().databaseList()[0]);
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            Log.d(getLocalClassName(), getApplicationContext().getFilesDir().getAbsolutePath());
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.niyatdekdee.notfy" + "//databases//" + "NiyayDekD";
                String backupDBPath = "NiyayDekD";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                FileChannel src = new FileInputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "ดึงข้อมูลกลับมาแล้ว เพื่อความสมบูรณ์ปิดแล้วเปิดใหม่", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            Log.d(getLocalClassName(), e.toString());
        }

        File src = new File(Environment.getExternalStorageDirectory(), "niyay_saveSharedPreferences");
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            Editor prefEdit = getSharedPreferences(this.getPreferenceManager().getSharedPreferencesName(), MODE_PRIVATE).edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static boolean getisLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isLogin", false);
    }

    public static boolean getCheckSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("keyCheck", true);
    }

    public static boolean getAutoAdd(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoAdd", false);
    }

    public static boolean getNotifyFav(Context context) {
        return getisLogin(context) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifyFov", true);
    }

    public static boolean getdisplayResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("displayResult", true);
    }

    public static boolean getonlyFavorite(Context context) {
        return getisLogin(context) && getdisplayResult(context) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("onlyFavorite", false);
    }

    public static String getSelectItemSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("keySelectItem", "24");
    }

    public static String getArrowSelectSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("arrowSelect", "2");
    }

    public static String getScreenSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("sreenRol", "0");
    }

    public static String getColorSelectSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("colorSelect", "1");
    }

    public static String getBgColorSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("bgColor", "0");
    }

    public static String getTextColorSetting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("textColor", "0");
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
        //System.out.println(PreferenceManager.getDefaultSharedPreferences(context).getString("UserName", "-1"));
        return UserPreference.decrypt(PreferenceManager.getDefaultSharedPreferences(context).getString("UserName", "-1"), context).replace("จนฟหสดฟ", "");
    }

    public static String getPassWord(Context context) {
        //System.out.println(PreferenceManager.getDefaultSharedPreferences(context).getString("PassWord", "-1"));
        return passPreference.decrypt(PreferenceManager.getDefaultSharedPreferences(context).getString("PassWord", "-1"), context).replace("¢", "");
    }

    public static boolean getTTSToast(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("showTTSToast", false);
    }

    public static boolean getdownScroll(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("downScroll", true);
    }

    public static boolean getsinglecolum(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("singlecolum", false);
    }

    public static boolean getswipe(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("swipe", false);
    }

    public static boolean getTTStip(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("TTStip", true);
    }

    public static boolean get_add_after_notify(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("add_after_notify", false);
    }

    public static boolean get_FullCheck(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("FullCheck", true);
    }

    public static String getCommentSort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("commentSortSelect", "0");
    }

    public boolean isNiyayServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NiyayService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO Auto-generated method stub
        System.out.println(key + " change");
        if (key.equals("notify")) {
            //System.out.println("notify change");
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notify", false)) {
                new Alarm().CancelAlarm(getApplicationContext());
                new Alarm().SetAlarm(getApplicationContext());
            } else {
                new Alarm().CancelAlarm(getApplicationContext());
                new Alarm().CancelAlarm(getApplicationContext());
            }
        } else if (key.equals("keySelectItem")) {
            System.out.println("keySelectItem change");
            new Alarm().CancelAlarm(getApplicationContext());
            new Alarm().SetAlarm(getApplicationContext());
        } else if (key.equals("alarm_time")) {
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
