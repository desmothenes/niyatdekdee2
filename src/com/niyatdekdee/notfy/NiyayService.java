package com.niyatdekdee.notfy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context; 
import android.content.Intent; 
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class NiyayService extends WakefulIntentService  
{ 
	//private static DatabaseAdapter db;
	public static Context context;
	private static int REQUEST_CODE;
	private DatabaseAdapter db;



	public NiyayService() {
		super("NiyayService");
		Log.e("NY", "create");
		//context = getApplicationContext();
		//if (!Setting.getSelectNotifySetting(context)) return;
		// TODO Auto-generated constructor stub
		//db = new DatabaseAdapter(this);  	
	}

	@Override
	protected void doWakefulWork(Intent intent) {		
		Log.e("zone", "doWakefulWork");
		context = getApplicationContext();
		
		if (!Setting.getSelectNotifySetting(context)) return;
		int itemSelect = Integer.parseInt(Setting.getSelectItemSetting(getApplicationContext()));
		if (itemSelect == 0 || !isOnline()) return;		
		
		db = new DatabaseAdapter(this);  	
		//showAllBook();
		showAllBook();
		File log=new File(Environment.getExternalStorageDirectory(),
				"AlarmLog.txt");

		try {
			BufferedWriter out=new BufferedWriter(
					new FileWriter(log.getAbsolutePath(),
							log.exists()));

			out.write(new Date().toString());
			out.write("\n");
			out.close();
		}
		catch (IOException e) {
			Log.e("AppService", "Exception appending to log file", e);
		}
		Log.e("zone", "ed doWakefulWork");

	}
/*
	private void displayNotification(String name,String detail,String title,String url)
	{
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.noti, name + "  ตอนใหม่", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		// The PendingIntent will launch activity if the user selects this notification
		Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse(url);
		browserIntent.setData(data);
		PendingIntent contentIntent = PendingIntent.getActivity(this, REQUEST_CODE,browserIntent, 0);

		notification.setLatestEventInfo(this, "เรื่อง"+ name + " ตอนที่ "+detail+" มีการเปลี่ยนแปลง",title+" ("+detail+")", contentIntent);
		manager.notify(NOTIFICATION_ID++, notification);

	}
*/
	
	private static void displayNotification(String id,String name,String detail,String title,String url)
	{
		//RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_noti);
		//contentView.setImageViewResource(R.id.image, R.drawable.notification_image);
		//contentView.setTextViewText(R.id.notiTitle, name);
		//contentView.setTextViewText(R.id.notiDetail1, title.substring(title.indexOf(":"))+" ("+detail+")");
		//contentView.setTextViewText(R.id.notiDetail2, title+" ("+detail+")");		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.noti, name + "  ตอนใหม่", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		// The PendingIntent will launch activity if the user selects this notification
		Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse(url);
		browserIntent.setData(data);
		PendingIntent contentIntent = PendingIntent.getActivity(context, REQUEST_CODE,browserIntent, 0);
		notification.contentIntent = contentIntent;
		//notification.contentView = contentView;
		notification.setLatestEventInfo(context, name,title.substring(title.indexOf(":")+2)+" ("+detail+")", contentIntent);
		manager.notify(Integer.parseInt(id), notification);
	}
	
	public boolean isOnline() { 
		ConnectivityManager cm = 
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 

		return cm.getActiveNetworkInfo() != null &&  
				cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
	} 

		private void showAllBook() {
			 //TODO Auto-generated method stub
			db = new DatabaseAdapter(context);
			db.open();
	
			Cursor c = db.getAllNiyay();
			final int i = c.getCount();
			Log.e("floop", Integer.toString(i));
			if (i == 0) {
				Log.e("ck db", "not ok");
				db.close();
				return;
			}
			else {
				Log.e("ok ?", "ok");
			}
			int i2 = 0;
			c.moveToFirst();
	
			do {		
				i2++;
				displayBook(c);
			}while(c.moveToNext());
	
			Log.e("loop end", Integer.toString(i2));
			db.close();
			Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
		}
		
	
		public void displayBook(Cursor c) {
	
			int status = 0;
			String title = c.getString(4);
			final String url = c.getString(2); 
			final String chapter = c.getString(3);
			String text1 = "";
			if (title.contains(">")) title = title.substring(title.indexOf(">"));
	
			HttpClient httpclient = new DefaultHttpClient();
			try {
				HttpGet httpget = new HttpGet(new URI(url+chapter));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				text1 = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();
			} catch (URISyntaxException e) {
				 //TODO Auto-generated catch block
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();		
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
	
			if (text1.contains("<title>")) {
				final String start = text1.substring(text1.indexOf("<title>")+7);
				text1 = Jsoup.parse((start.substring(start.indexOf(">")+2, start.indexOf("</title>")))).text();
			}
			else {
				text1 = "ยังไม่มีตอนปัจจุบัน รอตอนใหม่";
			}
	
			/*		Log.e("title",(title == null) ? "null" : title);
			Log.e("text1",text1);
			Log.e("compare",Integer.toString(text1.compareTo(title)));		*/	
	
			if (title == null ) title = "";			
			else if (title.contains(">")) title = title.substring(title.indexOf(">")+2);
			if (text1.contains(">"))	text1 = text1.substring(text1.indexOf(">")+2);
			if (title.isEmpty()) {
				title = text1;
				status = -1;
			}
			else if (text1.equals("ยังไม่มีตอนปัจจุบัน รอตอนใหม่")) {
				status = 2;
			}
			else if (!text1.trim().equals(title.trim())) {
				Log.e("title",title);
				Log.e("text1",text1);
				Log.e("compare",(text1.equals(title))? "same" : "not same");
				status = 1; //current chapter update							
			} 
			Log.e("status", Integer.toString(status));
	
	
			if (status == 1 || status == -1) {
				displayNotification(c.getString(0),c.getString(1),chapter,text1,url+chapter); 
				REQUEST_CODE++;
			}
	
			/*		Log.e("content",
					"id: " +c.getString(0)+"\n"+
							"name:" +c.getString(1)+"\n" +
							"url: " +c.getString(2)+"\n"+
							"chapter: " +c.getString(3)+"\n"+
							"title: " +c.getString(4));*/
	
		}

} 
