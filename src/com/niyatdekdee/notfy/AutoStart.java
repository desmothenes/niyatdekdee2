package com.niyatdekdee.notfy;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver; 
import android.content.Context; 
import android.content.Intent; 
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
 
public class AutoStart extends BroadcastReceiver 
{    
	private static final int PERIOD=3600000; 
	
    @Override 
    public void onReceive(Context context, Intent intent) 
    {    
    	Log.e("zone", "AutoStart onReceive");
    	if (!Setting.getSelectNotifySetting(context)) {
    		CancelAlarm(context);
    		return;
    	}
    	if (Integer.parseInt(Setting.getSelectItemSetting(context)) == 0) return;	
    	WakefulIntentService.acquireStaticLock(context);	
    	PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG"); 
		wl.acquire(); 
    	AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(context, NiyayService.class);
        PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
	    long time=Setting.getSelectNotifyTimeSetting(context);
		//Calendar timeOff9 = Calendar.getInstance();
		//timeOff9.setTimeInMillis(time);
	    System.out.print(time);
	    System.out.print(SystemClock.elapsedRealtime());
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        		time+PERIOD * Integer.parseInt(Setting.getSelectItemSetting(context)) /*SystemClock.elapsedRealtime()+60000*/,
                PERIOD * Integer.parseInt(Setting.getSelectItemSetting(context)) ,
                pi);
        wl.release(); 
        Log.e("zone", "ed AutoStart onReceive");
    } 

	public void SetAlarm(Context context) 
	{ 
		Log.e("zone", "set Alarm");
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0); 
	    long time=Setting.getSelectNotifyTimeSetting(context);
		Calendar timeOff9 = Calendar.getInstance();
		timeOff9.setTimeInMillis(time);
		am.setRepeating(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), 1000 * 60 * Integer.parseInt(Setting.getSelectItemSetting(context)), pi); // Millisec * Second * Minute 
	} 

	public void CancelAlarm(Context context) 
	{ 
		Log.e("zone", "cancel Alarm");
		Intent intent = new Intent(context, Alarm.class); 
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0); 
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		alarmManager.cancel(sender); 
	} 
} 

