package com.niyatdekdee.notfy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

class doback extends AsyncTask<Context, String, Long>
{
	static Map<String, Long> sessionTime = new HashMap<String, Long>();
	static Map<String, String> sessionStatus = new HashMap<String, String>();
	DatabaseAdapter db;
	Context context;
	private static boolean tried = false;
	private static int floop;
	static Map<String, String> sessionId = new HashMap<String, String>();
	ArrayList<String> Listtemp = new ArrayList<String> ();
	static List<Cookie> cookies;
	public doback(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		MainActivity.dialog.show();
		MainActivity.ListViewContent.clear();
		MainActivity.niyayTable.clear();
	}

	@Override
	protected Long doInBackground(Context... arg0) {
		// TODO Auto-generated method stub
		try
		{
			//showAllBookOffline() ;
			if (MainActivity.isOnline())	{

				if (Setting.getisLogin(context)) {
					Log.e("login","login");
					Time now = new Time();

					SharedPreferences pref  = PreferenceManager.getDefaultSharedPreferences(context);
					now.set(pref.getLong("timecookies", 0));
					//System.out.println(now.format3339(false));
					now.setToNow();
					long time;

					if (sessionTime.get("fav") != null ) time = sessionTime.get("fav");
					else time = 0;
					/*if (now.toMillis(true) < pref.getLong("timecookies", 0)) Log.e("sessionTime","true");
				else Log.e("sessionTime","false");

				if ((now.toMillis(true) < pref.getLong("timecookies", 0)) && (pref.getString("usercookie","").equals(Setting.getUserName(context)))) {
					sessionId.put(pref.getString("keycookies1", ""),pref.getString("valuecookies1", ""));
					sessionId.put(pref.getString("keycookies2", ""),pref.getString("valuecookies2", ""));
					sessionId.put(pref.getString("keycookies3", ""),pref.getString("valuecookies3", ""));
				}
				else {
					login();
				}*/
					if (Setting.getdisplayResult(context)) {


						if (now.toMillis(true) - time  > 3600000 ) {
							login();				
							sessionTime.put("fav", now.toMillis(true));
							//Log.e("login","time login");
						}
						Log.e("login","end login");
						if (sessionId.size() != 0) {
/*							for(String key: doback.sessionId.keySet()){
								//Log.v(key, doback.sessionId.get(key));
								//cookieString.append(key + "=" +doback.sessionId.get(key)+ "; ");
							}*/
							loadUpdate();
						}
					}

					if (Setting.getonlyFavorite(context)) return null;
				}			

				showAllBook();
				//showAllBookOffline() ;

			}
			else {
				showAllBookOffline() ;
			}
		}
		catch(Exception e)
		{

		}
		return null;
	}

	protected void onProgressUpdate(String... progress) 
	{			
		if (progress[0].equals("-1")) {
			//Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			Log.e("onProgressUpdate","การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
			if (tried  == false) {
				tried = true;
				this.cancel(true);
				this.execute(context);
			}
		} else {
			MainActivity.dialog.setMessage(progress[0]);
		}

	}
	protected void onPostExecute(Long result) 
	{
		MainActivity.db = new DatabaseAdapter(context);
		MainActivity.db.close();
		Log.e("end back","end back");
		if (MainActivity.ListViewContent.size() == 0 ) {
			if (floop == 0)
				MainActivity.ListViewContent.add("<h2>Please add your first niyay. (Menu->Add open your main niyay page or chapter you want)</h2>");
			else 
				Log.e("onPostExecute","การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
		}

		MainActivity.myList.setAdapter(MainActivity.listAdap);
		MainActivity.dialog.dismiss();
		tried = false;
		/*		for (Map.Entry<String, String> entry : sessionStatus.entrySet()) 
			Log.e(entry.getKey(), entry.getValue());*/
	}

	boolean isTemp;
	void showAllBook() {
		// TODO Auto-generated method stub
		MainActivity.db = new DatabaseAdapter(context);
		MainActivity.db.close();
		MainActivity.db.open();
		Listtemp.clear();
		Cursor c = MainActivity.db.getAllNiyay();
		floop = c.getCount();
		Log.e("floop", Integer.toString(floop));
		if (floop == 0) {
			//Log.e("ck db", "not ok");
			MainActivity.db.close();
			return;
		}
		else {
			//Log.e("ok ?", "ok");
		}

		int i2 = 0;
		c.moveToFirst();		
		Time now = new Time();
		now.setToNow();
		ArrayList<String[]> tempTable = new ArrayList<String[]> ();
		final long curtime = now.toMillis(true); 
		do {		
			i2++;
			String[] temp  = new String[5];
			temp[0] = c.getString(0);
			temp[1] = c.getString(1);
			temp[2] = c.getString(2);
			temp[3] = c.getString(3);

			publishProgress(temp[1]);
			long time;
			if ((sessionTime.get(temp[2])) != null ) time = sessionTime.get(temp[2]);
			else time = 0;
			//Log.e("curtime",Long.toString(curtime));
			//Log.e("old "+temp[2],Long.toString(time));
			if ((curtime - time) > 600000) {
				Log.e("zone","download");
				temp[4] = displayBook(c);
			}
			else if (sessionStatus.get(temp[2]+temp[3]) != null) {
				Log.e("zone","load sessionStatus");
				temp[4] = c.getString(4);
				isTemp = false;
				MainActivity.ListViewContent.add(sessionStatus.get(temp[2]+temp[3]));
			}
			else {
				isTemp = true;
				Log.e("zone","load displayBookOffline");
				temp[4] = c.getString(4);
				displayBookOffline(c);
			}

			//Log.e("temp[4]", temp[4]);
			if (isTemp)
				tempTable.add(temp);
			else
				MainActivity.niyayTable.add(temp);
			/*
			Thread t = new Thread() {
				public void run() {
					listAdap.notifyDataSetChanged();
				}
			};
			t.start();
			 */
		}while(c.moveToNext());

		Log.e("loop end", Integer.toString(i2));
		MainActivity.db.close();
		for (String stemp:Listtemp) {
			//System.out.println("Listtemp: "+stemp);
			MainActivity.ListViewContent.add(stemp);
		}
		for (String[] stemp:tempTable)
			MainActivity.niyayTable.add(stemp);
	}

	private String displayBook(Cursor c) {
		//Log.e("displayBook","displayBook");

		int status = 0;
		String title = c.getString(4);
		final String url = c.getString(2); 
		final String chapter = c.getString(3);
		String text1 = "";
		//if (title.contains(">")) title = title.substring(title.indexOf(">"));

		HttpClient httpclient = null;
		try {
			HttpGet httpget = new HttpGet(new URI(url+chapter));
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 4000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			httpclient = new DefaultHttpClient(params);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			text1 = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException" , e.getMessage());
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();			
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("IOException" , e.getMessage());
		//	Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			Log.e("URISyntaxException" , e.getMessage());
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("ParseException" ,text1);
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e("IllegalStateException" ,text1);
			//Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		if (text1.contains("<title>")) {
			try {
				ContextWrapper cw = new ContextWrapper(context);
				File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),c.getString(0)+".html");
				//System.out.println(temp.getAbsolutePath());
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
				//bw.write(text1.replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
				bw.write(text1);
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		else if (!text1.trim().equals(title.trim())) {
			/*Log.e("title",title);
			Log.e("text1",text1);
			Log.e("compare",(text1.equals(title))? "same" : "not same");*/
			status = 1; //current chapter update	
			/*
		    try {
		        byte[] utf8Bytes = text1.getBytes("UTF8");
		        byte[] defaultBytes = text1.getBytes();

		        String roundTrip = new String(utf8Bytes, "UTF8");
		        Log.e("roundTrip",roundTrip);


		        printBytes(utf8Bytes, "utf8Bytes");
		        printBytes(defaultBytes, "defaultBytes");
		      } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		      }
		    try {
		        byte[] utf8Bytes = title.getBytes("UTF8");
		        byte[] defaultBytes = title.getBytes();

		        String roundTrip = new String(utf8Bytes, "UTF8");
		        Log.e("roundTrip",roundTrip);

		        printBytes(utf8Bytes, "utf8Bytes");
		        printBytes(defaultBytes, "defaultBytes");
		      } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		      }		  
			 */

		} 

		else if (!text1.contains("ยังไม่มีตอนปัจจุบัน")) {
			status = 2;
		}
		//Log.e("status", Integer.toString(status));


		if (status == 0 ) {
			isTemp = true;
			Listtemp.add(
					"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
		}
		else if (status == 2 ) {
			isTemp = true;
			Listtemp.add(
					"<br/><p><font color=#6E6E6E>ถ้าจบตอน กดปุ่มเพิ่มตอน เพื่อเข้าสู่สถานะรอตอนใหม่</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
			sessionStatus.put(url+chapter, Listtemp.get(Listtemp.size()-1));
		}
		else if (status == 1 || status == -1) {
			//displayNotification(c.getString(0),c.getString(1),chapter,text1,url+chapter); 
			isTemp = false;
			MainActivity.ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ตอน : " +text1+" ("+chapter+")</font></p>"); 
			sessionStatus.put(url+chapter, MainActivity.ListViewContent.get(MainActivity.ListViewContent.size()-1).replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน  ถ้าจบตอน กดปุ่มเพิ่มตอน เพื่อเข้าสู่สถานะรอตอนใหม่"));
		}

/*			Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4)+"\n"+
						"text1" +text1);*/
		Time now = new Time();
		now.setToNow();
		sessionTime.put(url, now.toMillis(true));
		//Log.e("set "+url,Long.toString(now.toMillis(true)));

		if  (status == 0  || status == 2) return title;
		else if  (status == 1 || status == -1) return text1;
		return "";
	}

	void showAllBookOffline() {
		// TODO Auto-generated method stub
		MainActivity.ListViewContent.clear();
		MainActivity.niyayTable.clear();
		MainActivity.db = new DatabaseAdapter(context);
		MainActivity.db.close();
		MainActivity.db.open();

		Cursor c = db.getAllNiyay();
		int i = c.getCount();
		Log.e("floop", Integer.toString(i++));
		if (c.requery() && i==0) {
			//Log.e("ck db", "not ok");
			db.close();
			return;
		}
		else {
			//Log.e("ok ?", "ok");
		}

		i = 0;
		c.moveToFirst();

		do {		
			i++;
			String[] temp  = new String[5];
			temp[0] = c.getString(0);
			temp[1] = c.getString(1);
			temp[2] = c.getString(2);
			temp[3] = c.getString(3);
			temp[4] = c.getString(4);
			MainActivity.niyayTable.add(temp);
			displayBookOffline(c);


		}while(c.moveToNext());
		Log.e("loop end", Integer.toString(i));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}


	private void displayBookOffline(Cursor c) {
		//Log.e("title0",(c.getString(4) != null) ? c.getString(4):"");
		String title = c.getString(4);	

		//if (title.contains(">")) title = title.substring(title.indexOf(">"));
		//Log.e("title",(title != null) ? title:" ");

		if (title != null) 
			if ( title.contains(">")) 
				title = title.substring(title.indexOf(">")+2);
		//dialog.setTitle(title);
		Listtemp.add(
				"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
						"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		/*Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));*/
	}

	private void loadUpdate() {
		Log.v("favfin", "favfin");
		publishProgress("favorite writer");

		Document doc = null;
		if (sessionId == null) return; 
		try {
			doc = Jsoup.connect("http://www.dek-d.com/story_message2012.php")
					.cookies(sessionId).timeout(3000)
					.get();
		} catch (IOException e) {
			publishProgress("-1");
			//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}    	
		//System.out.println(doc.html());
		Elements link1 = doc.select(".novel");
		if(link1 == null) return;
		for (Element link:link1) {
			final String stext = link.text();
			//Log.v("stext", stext);
			String[] temp  = new String[5];
			temp[0] = "-2";
			temp[1] = stext.substring(0, stext.indexOf("ตอนที่"));
			temp[2] = link.select("a").attr("href");
			temp[3] = "-2";
			temp[4] = stext.substring(stext.indexOf("ตอนที่"));;
			publishProgress(temp[1]);
			MainActivity.niyayTable.add(temp);
			//MainActivity.ListViewContent.add(stext.replace("ตอนที่", "\nตอนที่"));	
			MainActivity.ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +temp[1]+"</font><br />" +
							"<font color=#cc0029>" +temp[4]+"</font></p>"); 
			sessionStatus.put(temp[2], MainActivity.ListViewContent.get(MainActivity.ListViewContent.size()-1));
		}


		Log.v("listView", "listView");
		/*		for (String i : ListViewContent)
			Log.v("ListViewContent", i);*/
	}

	private void login() {
		Log.v("zone", "login");
		//System.out.println(Setting.getUserName(context));
		//System.out.println(Setting.getPassWord(context));
		/*		Connection.Response res;
		try {
			res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
					.data("username", Setting.getUserName(context))
					.data("password", Setting.getPassWord(context))
					.method(Method.POST).timeout(8000)
					.execute();
			sessionId = res.cookies();
		} catch (IOException e) {
			publishProgress(-1);
			//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
		publishProgress("log in");

		HttpParams httpParameters = new BasicHttpParams();
		final int timeoutConnection = 3000;		
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		final int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost("http://my.dek-d.com/dekdee/my.id_station/login.php");
		HttpResponse response = null;
		HttpEntity entity  = null;
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", Setting.getUserName(context)));
		nvps.add(new BasicNameValuePair("password", Setting.getPassWord(context)));
		System.out.println("connect");
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = httpclient.execute(httpost);
			entity = response.getEntity();

			//System.out.println("Login form get: " + response.getStatusLine());
			if (entity != null) {
				entity.consumeContent();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Post logon cookies:");
		cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				// System.out.println("- " + cookies.get(i).toString());
				final String temp = cookies.get(i).toString().substring(cookies.get(i).toString().indexOf("name: ")+6);
				//System.out.println(temp.substring(0, temp.indexOf("]")));
				final String temp2 = temp.substring(temp.indexOf("value: ")+7);
				//System.out.println(temp2.substring(0, temp2.indexOf("]")));
				if (temp.indexOf("][") != -1 && temp2.indexOf("]")  != -1)
					sessionId.put(temp.substring(0, temp.indexOf("][")), temp2.substring(0, temp2.indexOf("]")));
			}
		}
		Date fav ;
		if ((fav= cookies.get(2).getExpiryDate()) != null && cookies.size() > 1) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			int i = 1;
			editor.putString("usercookie",Setting.getUserName(context));
			for(String key: doback.sessionId.keySet()) {
				editor.putString("keycookies"+Integer.toString(i), key);
				editor.putString("valuecookies"+Integer.toString(i), sessionId.get(key));
				i++;
			}
			editor.putLong("timecookies", fav.getTime());
			editor.commit();
		}

		// When HttpClient instance is no longer needed, 
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();      
	}
}
