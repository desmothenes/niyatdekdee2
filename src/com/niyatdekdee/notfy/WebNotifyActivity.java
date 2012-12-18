package com.niyatdekdee.notfy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class WebNotifyActivity extends ListActivity {

	private final ArrayList<String> ListViewContent = new ArrayList<String> ();
	Button loginbtt;
	EditText username;
	EditText password;
	protected ProgressDialog dialog;
	private final ArrayList<String> linktable = new ArrayList<String> ();
	//private DiskLruCache mDiskCache;
	//private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	//private static final String DISK_CACHE_SUBDIR = "thumbnails";
	private ArrayAdapter<String> adapter;
	Map<String, String> sessionId = new HashMap<String, String>();
	List<Cookie> cookies;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_web_notify);
		if (customTitleSupported) {
			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);
			//เชื่อม btnSearch btnDirection เข้ากับ View
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.nonbar);
			MainActivity.titleColor = Integer.parseInt(Setting.getColorSelectSetting(WebNotifyActivity.this));
			switch (MainActivity.titleColor) {
			case 0:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar);
				break;
			case 1:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				break;
			case 2:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
				break;
			case 3:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
			}

			TextView title = (TextView) findViewById(R.id.textViewBar);
			title.setText(" เข้าสู่ระบบ");
			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListViewContent);
		adapter.notifyDataSetChanged();
		final ListView listView = getListView();
		username = (EditText)findViewById(R.id.editText1);	
		password = (EditText)findViewById(R.id.editText2);
		loginbtt = (Button)findViewById(R.id.button1);		
		loginbtt.setOnClickListener(new OnClickListener() {         
			@Override
			public void onClick(View v) {				
				Log.v("login", "login");
				dialog = ProgressDialog.show(WebNotifyActivity.this,"Loading", "Please Wait...",true);
				doback dob=new doback();
				dob.execute();
				//ListViewContent.add("ถ้าตอนดังกล่าวมีการเพิ่มเติมภายหลังโดยมีอัพเดตชื่อตอนกรุณากดว่าอ่านแล้วเพิ่มให้แจ้งเตือนในครั้งหน้าว่ามีการอัพเดต แต่ถ้าจบตอนแล้วกรุณากดเพิ่มเพื่อรอตอนใหม่ถ้าตอนดังกล่าวมีการเพิ่มเติมภายหลังโดยมีอัพเดตชื่อตอนกรุณากดว่าอ่านแล้วเพิ่มให้แจ้งเตือนในครั้งหน้าว่ามีการอัพเดต แต่ถ้าจบตอนแล้วกรุณากดเพิ่มเพื่อรอตอนใหม่");
				Log.v("listView", "listView");
				for (String i : ListViewContent)
					Log.v("ListViewContent", i);
				listView.setVisibility(View.VISIBLE);
				username.setVisibility(View.INVISIBLE);
				password.setVisibility(View.INVISIBLE);
				loginbtt.setVisibility(View.INVISIBLE);
				//setListAdapter(adapter);

			}
		});

		ListView list = getListView();
		list.setFastScrollEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String url = "http://www.dek-d.com/"+linktable.get(arg2);
				Log.v("url", url);		
				

	/*			final String stext = "id=";
				//หาหลักของตอน
				final int start = url.lastIndexOf(stext)+stext.length();
				if (start - stext.length() == -1) {
					Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.v("url.length()", Integer.toString(url.length()));
				Log.v("start", Integer.toString(start));
				Log.v("stext.length()", Integer.toString(stext.length()));
				int len=0;
				Log.v("Character", Character.toString(url.charAt(start)));
				for (int i3 = start;i3 < url.length() && Character.isDigit(url.charAt(i3));i3++) {						
					len++;
					Log.v("Character", Character.toString(url.charAt(i3)));
				}
				Log.v("len", Integer.toString(len));
				final String unum = url.substring(start,start+len);	
				Log.v("unum", unum);*/
				final String unum = MyAppClass.findnum(url, "story_id=", getBaseContext());
				final String chapter = MyAppClass.findnum(ListViewContent.get(arg2), "ตอนที่ ", getBaseContext());
				String link = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter;
				
/*				try {
					Jsoup.connect(url).cookies(sessionId).timeout(3000).get();
				} catch (IOException e) {
					Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				*/
				if (Setting.getArrowSelectSetting(WebNotifyActivity.this).equals("0")) {

					Intent browser = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
					/*Bundle bundle = browser.getBundleExtra(Browser.EXTRA_HEADERS);
						if (bundle == null)
						bundle = new Bundle();
					Log.v("zone", "sessionId");
					if(sessionId != null){
						for(String key: sessionId.keySet()){
							Log.v(key, sessionId.get(key));
							bundle.putString(key, sessionId.get(key));
						}
						browser.putExtra(Browser.EXTRA_HEADERS, bundle);
					}	*/
					startActivity(browser);
				}
				else {
					Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
					//browserIntent.putExtra("id",niyayTable.get(arg0)[0]);
					StringBuilder cookieString = new StringBuilder();
					Cookie cookie = null;
					for (int i = 0; i < cookies.size(); i++) {
					    cookie = cookies.get(i);
					}
					if(sessionId != null){
						for(String key: sessionId.keySet()){
							Log.v(key, sessionId.get(key));
							cookieString.append(key + "=" +sessionId.get(key)+ "; ");
						}
					}
					browserIntent.putExtra("cookieString",cookieString +/* "fbm_193207127471363=base_*/"domain=" + cookie.getDomain());
					//browserIntent.putExtra("url","http://www.dek-d.com/story_message2012.php");
					browserIntent.putExtra("url",link);
					browserIntent.putExtra("title",ListViewContent.get(arg2).substring(ListViewContent.get(arg2).indexOf("ตอนที่")));
					startActivity(browserIntent);
				}
			}
		});  
	}

	class doback extends AsyncTask<URL, Integer, Long>
	{

		@Override
		protected Long doInBackground(URL... arg0) 
		{
			try
			{
				login();
				loadUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(Integer... publishProgress) 
		{
			if (publishProgress[0] == -1) {
				Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			}


		}
		protected void onPostExecute(Long result) 
		{
			try
			{
				if (ListViewContent.size() == 0) {
					AlertDialog alertDialog = new AlertDialog.Builder(WebNotifyActivity.this).create();
					alertDialog.setTitle("ไม่พบรายการ");
					alertDialog.setMessage("ถ้ามีรายการอยู่ ลองตรวจสอบการเชื่อมต่ออินเตอร์เน็ต แล้วลองใหม่");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// here you can add functions
						}
					});
					alertDialog.show();
				}
				setListAdapter(adapter);
				TextView title = (TextView) findViewById(R.id.textViewBar);
				title.setText(" เลือกรายการที่อ่าน");
				dialog.dismiss();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				dialog.dismiss();
			}
		}
		private void loadUpdate() {
			Document doc = null;
			if (sessionId == null) return; 
			try {
				doc = Jsoup.connect("http://www.dek-d.com/story_message2012.php")
						.cookies(sessionId).timeout(3000)
						.get();
			} catch (IOException e) {
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
			Elements link1 = doc.select(".novel");
			if(link1 == null) return;
			for (Element link:link1) {
				String stext = link.text();
				linktable.add(link.select("a").attr("href"));
				ListViewContent.add(stext.replace("ตอนที่", "\nตอนที่"));	
			}
			Log.v("favfin", "favfin");
			Log.v("listView", "listView");
			for (String i : ListViewContent)
				Log.v("ListViewContent", i);
		}
		private void login() {
/*			Connection.Response res;
			try {
				res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
						.data("username", username.getText().toString())
						.data("password", password.getText().toString())
						.method(Method.POST).timeout(8000)
						.execute();
				sessionId = res.cookies();
			} catch (IOException e) {
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  */ 	

/*	        HttpGet httpget = new HttpGet("http://my.dek-d.com/dekdee/my.id_station/login.php");

	       
			try {
				response = httpclient.execute(httpget);
		        entity = response.getEntity();

		        System.out.println("Login form get: " + response.getStatusLine());
		        if (entity != null) {
		            entity.consumeContent();
		        }
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	        System.out.println("Initial set of cookies:");
	        cookies = httpclient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	            System.out.println("None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	                System.out.println("- " + cookies.get(i).toString());
	            }
	        }
*/
			DefaultHttpClient httpclient = new DefaultHttpClient();
	        HttpPost httpost = new HttpPost("http://my.dek-d.com/dekdee/my.id_station/login.php");
	        HttpResponse response = null;
	        HttpEntity entity  = null;
	        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	        nvps.add(new BasicNameValuePair("username", username.getText().toString()));
	        nvps.add(new BasicNameValuePair("password", password.getText().toString()));

	        try {
				httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				response = httpclient.execute(httpost);
		        entity = response.getEntity();

		        System.out.println("Login form get: " + response.getStatusLine());
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

	        


	        System.out.println("Post logon cookies:");
	        cookies = httpclient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	            System.out.println("None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	                //System.out.println("- " + cookies.get(i).toString());
	        		final String temp = cookies.get(i).toString().substring(cookies.get(i).toString().indexOf("name: ")+6);
	        		//System.out.println(temp.substring(0, temp.indexOf("]")));
	        		final String temp2 = temp.substring(temp.indexOf("value: ")+7);
	        		//System.out.println(temp2.substring(0, temp2.indexOf("]")));
	        		sessionId.put(temp.substring(0, temp.indexOf("][")), temp2.substring(0, temp2.indexOf("]")));
	            }
	        }

	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();        
		}
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_web_notify, menu);
		return true;
	}*/
}
