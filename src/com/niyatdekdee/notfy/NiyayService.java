package com.niyatdekdee.notfy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context; 
import android.content.Intent; 
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class NiyayService extends WakefulIntentService  
{ 
	//private static DatabaseAdapter db;
	public static Context context;
	private static int REQUEST_CODE;
	private DatabaseAdapter db;
	private Map<String, String> sessionId;
	private int floop;



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
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("waitcheck", false))			
			Log.e("zone", "a waitcheck true");
		else
			Log.e("zone", "a waitcheck false");
		
		if (!Setting.getSelectNotifySetting(context)) return;
		int itemSelect = Integer.parseInt(Setting.getSelectItemSetting(getApplicationContext()));
		if (itemSelect == 0) return;	
		
		if (!isOnline()) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("waitcheck", true);
			editor.commit();
			return ;
		}
		
		db = new DatabaseAdapter(this);  	
		//showAllBook();
		showAllBook();
		if (Setting.getNotifyFav(context)) {
			login();
			loadUpdate();
		}
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
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean("waitcheck", false);
		editor.commit();
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("waitcheck", false))
			Log.e("zone", "b waitcheck true");
		else
			Log.e("zone", "b waitcheck false");	
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
	private void login() {
		Log.v("zone", "login");
		//System.out.println(Setting.getUserName(context));
		//System.out.println(Setting.getPassWord(context));
		Connection.Response res;
		try {
			res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
					.data("username", Setting.getUserName(context))
					.data("password", Setting.getPassWord(context))
					.method(Method.POST).timeout(8000)
					.execute();
			sessionId = res.cookies();
		} catch (IOException e) {
			//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   		
	}
	
	private void loadUpdate() {
		Log.v("favfin", "favfin");
		Document doc = null;
		if (sessionId == null) return;
		try {
			doc = Jsoup.connect("http://www.dek-d.com/story_message2012.php")
					.cookies(sessionId).timeout(3000)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}    	
		Elements link1 = doc.select(".novel");
		if(link1 == null) return;
		for (Element link:link1) {
			final String stext = link.text();
/*			Log.v("stext", stext);
			String[] temp  = new String[5];
			temp[0] = "-2";
			temp[1] = stext.substring(0, stext.indexOf("ตอนที่"));
			temp[2] = link.select("a").attr("href");
			temp[3] = "-2";
			temp[4] = stext.substring(stext.indexOf("ตอนที่"));*/
			//MainActivity.ListViewContent.add(stext.replace("ตอนที่", "\nตอนที่"));	
/*			MainActivity.ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +temp[1]+"</font><br />" +
							"<font color=#cc0029>" +temp[4]+"</font></p>"); */
			displayNotification(Integer.toString(floop++),stext.substring(0, stext.indexOf("ตอนที่")),stext.substring(stext.indexOf("ตอนที่")),link.select("a").attr("href"));
		}


		Log.v("listView", "listView");
		/*		for (String i : ListViewContent)
			Log.v("ListViewContent", i);*/
	}
	
	private static void displayNotification(String id,String name,String detail,String url)
	{
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.noti, name + "  ตอนใหม่", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		// The PendingIntent will launch activity if the user selects this notification
		Intent browserIntent = null;
		if (Setting.getArrowSelectSetting(context).equals("0")) {
			browserIntent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(url+"#story_body");
			browserIntent.setData(data);
		}
		else {
			browserIntent = new Intent(context, DekdeeBrowserActivity.class);
			browserIntent.putExtra("id",id);
			browserIntent.putExtra("url",url);
			browserIntent.putExtra("title",name);
		}
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, REQUEST_CODE,browserIntent, 0);
		notification.contentIntent = contentIntent;
		//notification.contentView = contentView;
		notification.setLatestEventInfo(context, name,detail, contentIntent);
		manager.notify(Integer.parseInt(id), notification);
	}
	
	private static void displayNotification(final String id,final String name,final String detail,String title,final String url)
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
		Intent browserIntent = null;
		if (Setting.getArrowSelectSetting(context).equals("0")) {
			browserIntent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(url+"#story_body");
			browserIntent.setData(data);
		}
		else {
			browserIntent = new Intent(context, DekdeeBrowserActivity.class);
			browserIntent.putExtra("id",id);
			browserIntent.putExtra("url",url);
			browserIntent.putExtra("title",name);
		}
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, REQUEST_CODE,browserIntent, 0);
		notification.contentIntent = contentIntent;
		//notification.contentView = contentView;
		if (title.contains(":")) {
			title = ":"+title;
		}
		if (title.indexOf(":")+2 < title.length()) {			
			notification.setLatestEventInfo(context, name,title.substring(title.indexOf(":")+2)+" ("+detail+")", contentIntent);
		}
		else if (title.indexOf(":")+1 < title.length()) {			
			notification.setLatestEventInfo(context, name,title.substring(title.indexOf(":")+1)+" ("+detail+")", contentIntent);
		}
		else if (title.indexOf(":") < title.length()) {			
			notification.setLatestEventInfo(context, name,title.substring(title.indexOf(":"))+" ("+detail+")", contentIntent);
		}
		else {
			notification.setLatestEventInfo(context, name,title+" ("+detail+")", contentIntent);
		}
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
			floop = c.getCount();
			Log.e("floop", Integer.toString(floop));
			if (floop == 0) {
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
			//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
		}
		
	
		public void displayBook(Cursor c) {
	
			int status = 0;
			String title = c.getString(4);
			final String url = c.getString(2); 
			final String chapter = c.getString(3);
			String text1 = "";
			if (title == null)
				title = "non";				
			if (title.contains(">")) title = title.substring(title.indexOf(">"));
	
			HttpClient httpclient = new DefaultHttpClient();
			try {
				HttpGet httpget = new HttpGet(new URI(url+chapter));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				text1 = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();
			} catch (URISyntaxException e) {
				 //TODO Auto-generated catch block
				//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.e("Error" , e.getMessage());
				e.printStackTrace();		
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
	
			if (text1.contains("<title>")) {
				final String start = text1.substring(text1.indexOf("<title>")+7);
				final int fst = start.indexOf(">");				final int snd = start.indexOf("</title>");				if (snd - fst > 2)					text1 = Jsoup.parse((start.substring(fst+2, snd))).text();				else					text1 = Jsoup.parse((start.substring(fst, snd))).text();
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
