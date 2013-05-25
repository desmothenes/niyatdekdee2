package com.niyatdekdee.notfy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver; 
import android.content.Context; 
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class Alarm extends BroadcastReceiver  
{     
	@Override 
	public void onReceive(Context context, Intent intent)  
	{    		
		Log.e("zone", "Alarm onReceive");
		if (!Setting.getSelectNotifySetting(context)) {
			 CancelAlarm(context) ;
			return;
		}

		WakefulIntentService.acquireStaticLock(context);	    
		context.startService(new Intent(context, NiyayService.class));	
                                          Log.e("zone", "ed in1 onReceive");  
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG"); 
		wl.acquire(); 

		// Put here YOUR code. 
		//Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example 
        Log.e("zone", "ed in2 onReceive");  
		wl.release(); 
		Log.e("zone", "ed Alarm onReceive");
	} 

	public void SetAlarm(Context context) 
	{ 
		Log.e("zone", "set Alarm");
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 33, i, 0); 
	    long time=Setting.getSelectNotifyTimeSetting(context);
		final long period = 1000 * 60 * 60 * Integer.parseInt(Setting.getSelectItemSetting(context));
		am.setRepeating (AlarmManager.RTC_WAKEUP, time+period, period, pi); // Millisec * Second * Minute 
	} 

	public void CancelAlarm(Context context) 
	{ 
		Log.e("zone", "cancel Alarm");
		Intent intent = new Intent(context, Alarm.class); 
		PendingIntent sender = PendingIntent.getBroadcast(context, 33, intent, 0); 
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		alarmManager.cancel(sender); 
		sender = PendingIntent.getBroadcast(context, 0, intent, 0); 
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		alarmManager.cancel(sender); 
	} 
} 
