package com.niyatdekdee.notfy;

import android.app.Service; 
import android.content.Context; 
import android.content.Intent; 
import android.os.IBinder; 

public class NiyayService extends Service 
{ 
	Alarm alarm = new Alarm(); 
	public void onCreate() 
	{ 
		super.onCreate();        
	} 

	public void onStart(Context context,Intent intent, int startId) 
	{ 
		alarm.SetAlarm(context); 
	} 

	@Override 
	public IBinder onBind(Intent intent)  
	{ 
		return null; 
	} 
} 
