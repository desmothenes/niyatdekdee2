package com.niyatdekdee.notfy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.bugsense.trace.BugSenseHandler;

public class MainActivity extends ListActivity  {
	static DatabaseAdapter db;
	static Context context;
	final static ArrayList<String> ListViewContent = new ArrayList<String> ();
	final static ArrayList<String[]> niyayTable = new ArrayList<String[]> ();
	static ListViewAdapter listAdap;
	//private static ListView myList;
	//private boolean resumeHasRun = false;
	static ProgressDialog dialog;
	private ImageButton btnDirection;
	static boolean isTTS = true;
	static ListView myList;
	//private static final int REQUEST_CODE=1;
	static boolean LoadPage=false;
	//static Map<String, String> sessionStatus = new HashMap<String, String>();

	static int titleColor = -1;	
	static int floop;
	//static doback dob;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//BugSenseHandler.initAndStartSession(MainActivity.this, "7942beee");
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		if (customTitleSupported) {

			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_main);

			//RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.mainbar);

			//titleColor = Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this));			

			ImageButton btnRefresh = (ImageButton)findViewById(R.id.imageButton1);
			ImageButton btnAdd = (ImageButton)findViewById(R.id.imageButton2);
			ImageButton btnSetting = (ImageButton)findViewById(R.id.btnSetting);


			TextView title = (TextView) findViewById(R.id.textViewTitle);
			title.setText(" รายการนิยาย");

			btnRefresh.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub					
					dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...\nถ้ารู้สึกช้า ออกแล้วเข้าใหม่",true);
					Log.e("doback at", "btnRefresh");
					new doback(getApplicationContext()).execute();
				}				
			});

			btnAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					addmenu();
				}				
			});

			btnSetting.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					settingmenu();
				}				
			});

			btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
					builder.setMessage("ต้องการที่จะออก?\n\nช่วยแนะนำติชมเพิ่มเติมผ่านทางช่องทางต่าง ๆ เช่น e-mail fanpage review เพื่อนำมาปรับปรุงต่อไปด้วยครับ") 
					.setCancelable(false) 
					.setPositiveButton("ออก", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) { 
							dialog.cancel();
							finish();
						} 
					}) 
					.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) { 
							dialog.cancel(); 
						} 
					}); 
					AlertDialog alert = builder.create(); 
					alert.show();
				}
			});
		}

		//		ArrayList<String> allLaunchers = new ArrayList<String>();
		//
		//		Intent allApps = new Intent(Intent.ACTION_MAIN);
		//		List<ResolveInfo> allAppList = getPackageManager().queryIntentActivities(allApps, 0);
		//		for(int i =0;i<allAppList.size();i++) allLaunchers.add(allAppList.get(i).activityInfo.packageName);
		//
		//		Intent myApps = new Intent(Intent.ACTION_VIEW);
		//		       myApps.setData(Uri.parse("http://www.google.es"));
		//		List<ResolveInfo> myAppList = getPackageManager().queryIntentActivities(myApps, 0);
		//		for(int i =0;i<myAppList.size();i++){
		//		    if(allLaunchers.contains(myAppList.get(i).activityInfo.packageName)){
		//		        Log.e("match",myAppList.get(i).activityInfo.packageName+"");
		//		    }
		//		}
		//		
		context = getBaseContext();
		//db = new DatabaseAdapter(this);
		listAdap = new ListViewAdapter(this);


		myList = getListView();

		dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...\nถ้ารู้สึกช้า ออกแล้วเข้าใหม่",true);
		//dob=;
		Log.e("doback at", "main");
		new doback(getApplicationContext()).execute();
		//Intent i = new Intent(getBaseContext(), FlowActivity.class);
		//startActivity(i);
		//myList=(ListView)findViewById(android.R.id.list); 	
		//myList.setScrollingCacheEnabled(false);
		//myList.setAdapter(listAdap);

		/*	    myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
			}
		});*/

		myList.setFastScrollEnabled(true);
		myList.smoothScrollToPosition(0);
		myList.setItemsCanFocus(true);
		registerForContextMenu(myList);
		//myList.setOnItemClickListener(new OnItemClickListener() {});
		/*		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String item = ((TextView)view).getText().toString();

				Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

			}
		});		*/
		/*		if (isOnline())	{
			showAllBook();
			//showAllBookOffline() ;
		}
		else {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
			alertDialog.setTitle("Error.");
			alertDialog.setMessage("Not connect to internet.\nPlease check your internet connection");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// here you can add functions
				}
			});
			alertDialog.show();

			showAllBookOffline() ;
		}*/

		WakefulIntentService.acquireStaticLock(context);
		//context.startService(new Intent(context, NiyayService.class));
		//context.startService(new Intent(context, Alarm.class));
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getBoolean("first_run", true)) {			
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			editor.putLong("alarm_time", System.currentTimeMillis());
			editor.putString("keySelectItem", "6");
			editor.putBoolean("first_run", false);
			editor.commit();
			new Alarm().SetAlarm(getApplicationContext());
		}
		//context.startService(new Intent(context, AutoStart.class));
		/*		while (LoadPage)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//myList.setAdapter(listAdap);
		//setListAdapter(listAdap);
		listAdap.notifyDataSetChanged();*/
	}


	static void update() {
		Log.e("doback at", "update static");
		new doback(context).execute();
		//listAdap.notifyDataSetChanged();
	}


	/*	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = TTS.tts.setLanguage(new Locale( "tha", "TH"));

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(getApplicationContext(), "This Language is not supported", Toast.LENGTH_SHORT).show();
				Log.e("TTS", "Your TTS not supported on Thai language");
				isTTS = false;
			}	
		} else {
			Toast.makeText(getApplicationContext(), "TTS Initilization Failed!", Toast.LENGTH_SHORT).show();
			Log.e("TTS", "Initilization Failed!");
			isTTS = false;
		}
	}
	 */

	@Override
	protected void onResume() {
		//dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
		super.onResume();		
		if (titleColor != Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this))) {
			titleColor = Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this));
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.mainbar);
			switch (titleColor) {
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
		}	
		if (DekTTSActivity.tts != null) {
			try {
				DekTTSActivity.tts.shutdown();
			} catch (IllegalArgumentException e) {
				getApplicationContext().unbindService((ServiceConnection) DekTTSActivity.tts);
			}
		}
		//DekTTSActivity.tts = new TextToSpeech(this,this);
		/*		if (!resumeHasRun) {
			resumeHasRun = true;
			return;
		}
		else {
			//new doback().execute();
		}*/
		listAdap.notifyDataSetChanged();
		//dialog.dismiss();


		/*else {
			dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
			doback dob=new doback();
			dob.execute();
		}*/

		/*		if (Setting.getCheckSetting(getApplicationContext()) tipCheck) {
			final Toast tag  = Toast.makeText(getBaseContext(), "ถ้าตอนดังกล่าวมีการเพิ่มเติมภายหลังโดยมีอัพเดตชื่อตอนกรุณากดว่าอ่านแล้วเพิ่มให้แจ้งเตือนในครั้งหน้าว่ามีการอัพเดต แต่ถ้าจบตอนแล้วกรุณากดเพิ่มเพื่อรอตอนใหม่", Toast.LENGTH_LONG);
			tag.show();	    
			new CountDownTimer(7000, 1000)
			{

				public void onTick(long millisUntilFinished) {tag.show();}
				public void onFinish() {tag.show();}

			}.start();
		}*/
		//context = MainActivity.this;
	}

	/*	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		String pen = o.toString();
		Toast.makeText(this, "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();
	}*/
	/*
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        StatusListAdapter adapter = (StatusListAdapter) ((ListView) adapterView).getAdapter();
        Status status = (Status) adapter.getItem(position);
        Intent intent = new Intent(getContext(), CustomActivity.class);
        intent.putExtra(C.extra_keys.status, status);
        getContext().startActivity(intent);
     }*/




	@Override
	public void onBackPressed() {
		if (DekTTSActivity.tts != null) {
			DekTTSActivity.tts.stop();
			DekTTSActivity.isSpeak = false;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
		builder.setMessage("ต้องการที่จะออก?\n\nช่วยแนะนำติชมเพิ่มเติมผ่านทางช่องทางต่าง ๆ เช่น e-mail fanpage review เพื่อนำมาปรับปรุงต่อไปด้วยครับ") 
		.setCancelable(false) 
		.setPositiveButton("ออก", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				finish();
			} 
		}) 
		.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				dialog.cancel(); 
			} 
		}); 
		AlertDialog alert = builder.create(); 
		alert.show(); 
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{		
		super.onCreateContextMenu(menu, v, menuInfo);
		if (ListViewContent.get(0).equals("<h2>Please add your first niyay. (Menu->Add open your main niyay page or chapter you want)</h2>")) {
			addmenu();
			return ;
		}
		getMenuInflater().inflate(R.menu.menu_data, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//setContentView(R.layout.activity_main);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final int listItemName = (int)info.id;	
		/*		int i_index = -1;		
		for(String[] i:niyayTable) {
			i_index++;
			Log.e("table "+Integer.toString(i_index), i[1]);			
		}
		Log.v("get with", Integer.toString(listItemName));
		Log.v("niyayTable 0",niyayTable.get(listItemName)[0]);
		Log.v("niyayTable 1",niyayTable.get(listItemName)[1]);
		Log.v("niyayTable 2",niyayTable.get(listItemName)[2]);
		Log.v("niyayTable 3",niyayTable.get(listItemName)[3]);
		Log.v("niyayTable 4",niyayTable.get(listItemName)[4]);
		Log.v("get item", Integer.toString(item.getItemId()));*/
		final ContextWrapper cw = new ContextWrapper(this);

		switch(item.getItemId()) {
		case R.id.open:
			String url;
			if (niyayTable.get(listItemName)[0].equals("-2")) {
				final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
				final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
				url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter;
			} else {
				url = niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3];
			}			
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;
			//Log.e("url", url);
			//			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
			//			Uri data = Uri.parse(url);
			//			browserIntent.setData(data);
			//			startActivity(browserIntent);
			Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
			browserIntent.putExtra("id",niyayTable.get(listItemName)[0]);
			browserIntent.putExtra("url",url);
			browserIntent.putExtra("title",niyayTable.get(listItemName)[4]);
			if (doback.sessionId != null) {
				StringBuilder cookieString = new StringBuilder();
				for(String key: doback.sessionId.keySet()){
					Log.v(key, doback.sessionId.get(key));
					cookieString.append(key + "=" +doback.sessionId.get(key)+ ";");
				}
				browserIntent.putExtra("cookieString",cookieString.toString());
			}
			startActivity(browserIntent);
			if (!Setting.getAutoAdd(getApplicationContext()) || niyayTable.get(listItemName)[4].equals("ยังไม่มีตอนปัจจุบัน รอตอนใหม่"))
				return true;
			else if (niyayTable.get(listItemName)[0].equals("-2")) {
				new AsyncTask<Integer, Void, Void>() {

					protected void onPreExecute() {
						Log.d("ASYNCTASK", "Pre execute for task : ");
						dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
					}
					@Override
					protected Void doInBackground(Integer... params) {
						// TODO Auto-generated method stub
						try {
							Jsoup.connect("http://www.dek-d.com/"+niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
						} catch (IOException e) {
							Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  finally {
							niyayTable.remove(listItemName);
							ListViewContent.remove(listItemName);							
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						dialog.dismiss();
						listAdap.notifyDataSetChanged();
					}

				}.execute();		
				return true;
			}

		case R.id.addcp:
			if (niyayTable.get(listItemName)[0].equals("-2"))  {
				new AsyncTask<Integer, Void, Void>() {

					protected void onPreExecute() {
						Log.d("ASYNCTASK", "Pre execute for task : ");
						dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
					}
					@Override
					protected Void doInBackground(Integer... params) {
						// TODO Auto-generated method stub
						try {
							Jsoup.connect("http://www.dek-d.com/"+niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
						} catch (IOException e) {
							Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  finally {
							niyayTable.remove(listItemName);
							ListViewContent.remove(listItemName);							
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						dialog.dismiss();
						listAdap.notifyDataSetChanged();
					}

				}.execute();
				return true;
			}
			niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])+1);

			new AsyncTask<Integer, Void, Void>() {
				String doc = "";

				protected void onPreExecute() {
					Log.d("ASYNCTASK", "Pre execute for task : ");
					dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
				}

				@Override
				protected Void doInBackground(Integer... args) {
					HttpClient httpclient = new DefaultHttpClient();
					try {         				
						HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]));
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						doc = httpclient.execute(httpget, responseHandler);
					} catch (ClientProtocolException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (URISyntaxException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} finally {
						httpclient.getConnectionManager().shutdown();
					}
					return null;
				}

				protected void onPostExecute(Void result) {
					Log.d("ASYNCTASK", "Post execute for task : " );
					//Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
					final int start;
					if ((start=doc.indexOf("<title>")) != -1) {
						try {
							File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html");
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
							bw.write(doc);
							bw.flush();
							bw.close();
							//System.out.println(temp.getAbsolutePath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						doc = doc.substring(start+7, doc.indexOf("</title>"));
						doc = Jsoup.parse((doc.substring(doc.indexOf(">")+2))).text();
					}
					else {
						doc = "ยังไม่มีตอนปัจจุบัน รอตอนใหม่";
					}
					db.open();

					boolean flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
							Integer.parseInt(niyayTable.get(listItemName)[3]),
							"");			
					if (flag) {
						Toast.makeText(context, "inc succeed", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "inc failed", Toast.LENGTH_SHORT).show();
						niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])-1);
					}
					niyayTable.get(listItemName)[4] = doc ;
					if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return ;
					flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
							niyayTable.get(listItemName)[4]);			
					if (flag) {
						//Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
					} else {
						//Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
					}		
					//Intent i = new Intent(context,MainActivity.class);
					db.close();
					ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +doc+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
							);		
					//doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
					doback.sessionStatus.remove(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]);
					listAdap.notifyDataSetChanged();
					dialog.dismiss();
				};
			}.execute();
			return true;
		case R.id.openweb:
			if  (niyayTable.get(listItemName)[0].equals("-2"))  {
				final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
				final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
				url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter+"#story_body";
			} else {
				url = niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]+"#story_body";	
			}		
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;
			//Log.e("url", url2);
			/*			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			//final  pairs = i.getBundleExtra(Browser.EXTRA_HEADERS);

			StringBuilder cookieString = new StringBuilder();
			Bundle bundle = new Bundle();
			if(doback.sessionId != null){
				for(String key: doback.sessionId.keySet()){
					//Log.v(key, doback.sessionId.get(key));
					cookieString.append(key + "=" +doback.sessionId.get(key)+ "; ");
				}
			}
			if(doback.cookies != null){
				//for (Cookie cookie : doback.cookies)
					// for(String key: doback.cookies.get(index).keySet()){
					bundle.putString("Cookie",cookieString.toString()+"domain=" + doback.cookies.get(0).getDomain());
				// }
			}
			i.putExtra(Browser.EXTRA_HEADERS, bundle);
			startActivity(i);*/
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
			return true;
		case R.id.red:			
			//Log.e("replace with", niyayTable.get(listItemName)[4]);
			if (niyayTable.get(listItemName)[0].equals("-2"))  {
				new AsyncTask<Integer, Void, Void>() {

					protected void onPreExecute() {
						Log.d("ASYNCTASK", "Pre execute for task : ");
						dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
					}
					@Override
					protected Void doInBackground(Integer... params) {
						// TODO Auto-generated method stub
						try {
							Jsoup.connect("http://www.dek-d.com/"+niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
						} catch (IOException e) {
							Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  finally {
							niyayTable.remove(listItemName);
							ListViewContent.remove(listItemName);		
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						dialog.dismiss();
						listAdap.notifyDataSetChanged();						
					}
				}.execute();
				return true;
			}
			db.open();
			if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return true;
			/*boolean flag = */
			db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), niyayTable.get(listItemName)[4]);			
			/*			if (flag) {
					Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
				}*/
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			//reload();	
			ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
					"<font color=#cc0029> ล่าสุด ตอน : " +niyayTable.get(listItemName)[4]+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
					);		
			//doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
			doback.sessionStatus.remove(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]);
			listAdap.notifyDataSetChanged();
			return true;		
		case R.id.tts:
			if (!isTTS) {
				CharSequence[] items = {"ค้นหาจาก Play Store", "Vaja", "SVOX Thai Kanya Voice"};
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
				builder.setCancelable(true).setTitle("ไม่รองรับ TTS ภาษาไทย เลือกรายการด้านล่างแทน")
				.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int id) { 
						dialog.dismiss();
						if (id == 0) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=tts+engine+thai")));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q==tts+engine+thai")));
							}
						}
						else if(id == 1) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spt.tts.vaja")));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.spt.tts.vaja")));
							}
						}
						else if(id == 2) {		
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
							}
						}
					}
				}); 
				AlertDialog alert = builder.create(); 
				alert.show();
				return true;
			}

			if (DekTTSActivity.tts != null) {
				DekTTSActivity.tts.stop();
				DekTTSActivity.isSpeak = false;
			}

			if  (niyayTable.get(listItemName)[0].equals("-2"))  {
				final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
				final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
				//TTS.totext("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter);
				Intent intent = new Intent(context, DekTTSActivity.class);
				DekTTSActivity.type = 1;
				DekTTSActivity.text = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter;
				context.startService(intent);
				final Toast tag = Toast.makeText(getBaseContext(),"โปรดรอสักครู่ กำลังประมวลผล\nสามารถกด Back เพื่อหยุด TTS ได้", Toast.LENGTH_SHORT);
				tag.show();
				new CountDownTimer(6000, 1000)
				{
					public void onTick(long millisUntilFinished) {tag.show();}
					public void onFinish() {tag.show();}

				}.start();
			}
			else {
				Intent intent = new Intent(context, DekTTSActivity.class);
				File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html");
				if (temp.canRead()) {
					DekTTSActivity.type = 2;					//intent.putExtra("text", temp.getAbsolutePath());
					DekTTSActivity.temp = temp;
					context.startService(intent);
					final Toast tag = Toast.makeText(getBaseContext(),"โปรดรอสักครู่ กำลังประมวลผล\nสามารถกด Back เพื่อหยุด TTS ได้", Toast.LENGTH_SHORT);
					tag.show();
					new CountDownTimer(6000, 1000)
					{
						public void onTick(long millisUntilFinished) {tag.show();}
						public void onFinish() {tag.show();}

					}.start();
				} else {
					DekTTSActivity.type = 1;/*
					System.out.println(niyayTable.get(listItemName)[2]);
					System.out.println(niyayTable.get(listItemName)[4]);*/

					final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "id=", getBaseContext());
					//	final String chapter = MyAppClass.findnum(, "ตอนที่ ", getBaseContext());
					DekTTSActivity.text = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+niyayTable.get(listItemName)[3];
					context.startService(intent);
					final Toast tag = Toast.makeText(getBaseContext(),"โปรดรอสักครู่ กำลังประมวลผล\nสามารถกด Back เพื่อหยุด TTS ได้", Toast.LENGTH_SHORT);
					tag.show();
					new CountDownTimer(6000, 1000)
					{
						public void onTick(long millisUntilFinished) {tag.show();}
						public void onFinish() {tag.show();}

					}.start();
				}
				if (!isTTS) {
					CharSequence[] items = {"ค้นหาจาก Play Store", "Vaja", "SVOX Thai Kanya Voice"};
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
					builder.setCancelable(true).setTitle("ไม่รองรับ TTS ภาษาไทย เลือกรายการด้านล่างแทน")
					.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) { 
							dialog.dismiss();
							if (id == 0) {
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=tts+engine+thai")));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q==tts+engine+thai")));
								}
							}
							else if(id == 1) {
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spt.tts.vaja")));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.spt.tts.vaja")));
								}
							}
							else if(id == 2) {		
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
								}
							}
						}
					}); 
					AlertDialog alert = builder.create(); 
					alert.show();
					return true;
				}
			}
			//TTS.totext(new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html"));
			return true;
		case R.id.edit:
			if  (niyayTable.get(listItemName)[0].equals("-2"))  {
				Toast.makeText(context, "ไม่รองรับกับ favorite writer", Toast.LENGTH_LONG).show();
				return true;
			}
			/*
			while (!resumeHasRun)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			db.open();
			Cursor c = db.getNiyay(Long.parseLong(niyayTable.get(listItemName)[0]));
			c.moveToFirst();
			niyayTable.get(listItemName)[0]= c.getString(0);
			niyayTable.get(listItemName)[1] = c.getString(1);
			niyayTable.get(listItemName)[2] = c.getString(2);
			niyayTable.get(listItemName)[3] = c.getString(3);
			niyayTable.get(listItemName)[4] = displayBookforedit(listItemName,c);
			db.close();
			listAdap.notifyDataSetChanged();*/
			Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(context, EditForm.class);
			intent.putExtra("id", niyayTable.get(listItemName)[0]);
			intent.putExtra("name", niyayTable.get(listItemName)[1]);
			intent.putExtra("url", niyayTable.get(listItemName)[2]);
			intent.putExtra("chapter", niyayTable.get(listItemName)[3]);
			intent.putExtra("title", niyayTable.get(listItemName)[4]);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//resumeHasRun = true;
			startActivityForResult(intent, 0);
			//Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.chapterlist:
			Intent chapterlist = new Intent(context, ChapterListActivity.class);
			if  (niyayTable.get(listItemName)[0].equals("-2"))  {
				final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
				//final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
				chapterlist.putExtra("url","id="+unum+"&=");
			} else {
				chapterlist.putExtra("url", niyayTable.get(listItemName)[2]);
			}			
			chapterlist.putExtra("title", niyayTable.get(listItemName)[1]);
			startActivity(chapterlist);
			return true;
		case R.id.dec:
			if  (niyayTable.get(listItemName)[0].equals("-2"))  {
				Toast.makeText(context, "ไม่รองรับกับ favorite writer", Toast.LENGTH_LONG).show();
				return true;
			}
			new AsyncTask<Integer, Void, Void>() {
				String doc = "";

				protected void onPreExecute() {
					Log.d("ASYNCTASK", "Pre execute for task : ");
					dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
				};

				@Override
				protected Void doInBackground(Integer... args) {
					HttpClient httpclient = new DefaultHttpClient();
					niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])-1);

					try {        				
						HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]));    
						ResponseHandler<String> responseHandler = new BasicResponseHandler();
						doc = httpclient.execute(httpget, responseHandler);
					} catch (ClientProtocolException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (URISyntaxException e) {
						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} finally {
						httpclient.getConnectionManager().shutdown();
					}
					return null;
				}

				protected void onPostExecute(Void result) {					
					Log.d("ASYNCTASK", "Post execute for task : " );
					final int start2;
					if ((start2=doc.indexOf("<title>")) != -1) {
						try {
							File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html");
							BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
							bw.write(doc);
							bw.flush();
							bw.close();
							//System.out.println(temp.getAbsolutePath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						doc = doc.substring(start2+7, doc.indexOf("</title>"));
						//Log.e("url", doc);
						doc = Jsoup.parse((doc.substring(doc.indexOf(">")+2))).text();
					}
					else {
						doc = "ยังไม่มีตอนปัจจุบัน รอตอนใหม่";
					}
					db.open();
					boolean flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
							Integer.parseInt(niyayTable.get(listItemName)[3]),
							"");			
					if (flag) {
						Toast.makeText(context, "dec succeed", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "dec failed", Toast.LENGTH_SHORT).show();
						niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])+1);
					}

					niyayTable.get(listItemName)[4] = doc;
					if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return;
					flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
							niyayTable.get(listItemName)[4]);			
					if (flag) {
						Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
					}			
					//Intent i = new Intent(context,MainActivity.class);
					db.close();
					ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +doc+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
							);		
					listAdap.notifyDataSetChanged();
					dialog.dismiss();
				};
			}.execute();		
			//Toast.makeText(context, "dec", Toast.LENGTH_SHORT).show();	
			return true;	
		case R.id.delete:
			if (niyayTable.get(listItemName)[0].equals("-2")) {
				Toast.makeText(context, "ไม่รองรับกับ favorite writer", Toast.LENGTH_LONG).show();
				return true;
			}
			Toast.makeText(context, "del ", Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
			builder.setIcon(R.drawable.delete);
			builder.setMessage("คุณต้องการที่จะลบเรื่อง "+niyayTable.get(listItemName)[1]+" ?") 
			.setCancelable(false) 						 
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int id) { 
					db.open();
					boolean flag;
					flag = db.deleteNiyay(Long.parseLong(niyayTable.get(listItemName)[0]));		
					if (flag) {
						Toast.makeText(context, "delete succeed", Toast.LENGTH_SHORT).show();
						ContextWrapper cw = new ContextWrapper(context);
						File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html");
						temp.delete();
						//System.out.println(temp.getAbsolutePath());
					} else {
						Toast.makeText(context, "delete failed", Toast.LENGTH_SHORT).show();
					}
					//Intent i = new Intent(context,MainActivity.class);
					db.close();
					Log.e("doback at", "del");
					new doback(getApplicationContext()).execute();
				} 
			}) 
			.setNegativeButton("No", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int id) { 
					dialog.cancel(); 
				} 
			}); 
			AlertDialog alert = builder.create(); 
			alert.show(); 	
			listAdap.notifyDataSetChanged();
			return true;	
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);	
		Log.e("doback at", "onActivityResult");
		new doback(getApplicationContext()).execute();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			addmenu();
			return true;
		case R.id.show:
			Log.e("doback at", "Options");
			new doback(getApplicationContext()).execute();
			return true;
		case R.id.menu_settings:
			startActivityForResult(new Intent(getBaseContext(),Setting.class),0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroy() {
		/*		if (TTS.tts != null) {
			TTS.tts.stop();
			TTS.tts.shutdown();
		}*/
		super.onDestroy();
		//BugSenseHandler.closeSession(MainActivity.this);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void addmenu() {
		context = MainActivity.this;
		CharSequence[] items = {"ค้นหาแบ่งตามหมวด", "ค้นหาจากข้อมูล", "จาก Favorite Writer","ค้นหาจากหน้า Web"};
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
		builder.setCancelable(true)
		.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				dialog.dismiss();
				if (id == 3) {
					Intent i = new Intent(getApplicationContext(),add_web.class);
					startActivity(i);
				}
				else if(id == 2) {
					//Toast.makeText(getApplicationContext(), "this function not enable in this version"/*items[id]*/, Toast.LENGTH_SHORT).show();
					Intent i = new Intent(getApplicationContext(),Fav_add.class);
					startActivityForResult(i,0);
				}
				else if(id == 0) {		
					Intent i = new Intent(getApplicationContext(),SearchGroupActivity.class);
					startActivityForResult(i,0);
				}
				else if(id == 1) {
					Intent i = new Intent(getApplicationContext(),SearchNameActivity.class);
					startActivityForResult(i,0);
				}

			}

		}); 
		AlertDialog alert = builder.create(); 
		alert.show();
	}

	private void settingmenu() {
		context = MainActivity.this;
		CharSequence[] items = {"ตั่งค่า", "อัพเดตจาก Favorite Writer แบบประหยัด"};
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
		builder.setCancelable(true)
		.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				dialog.dismiss();
				if (id == 0) {
					startActivityForResult(new Intent(getBaseContext(),Setting.class),0);
				}
				else if(id == 1) {
					//Toast.makeText(getApplicationContext(), "this function not enable in this version"/*items[id]*/, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(getApplicationContext(),WebNotifyActivity.class));
				}
			}

		}); 
		AlertDialog alert = builder.create(); 
		alert.show();
	}
	/*
	String formatDateTime(String timeToFormat) {

		String finalDateTime = "";          

		SimpleDateFormat iso8601Format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		Date date = null;
		if (timeToFormat != null) {
			try {
				date = iso8601Format.parse(timeToFormat);
			} catch (ParseException e) {
				date = null;
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				date = null;
				e.printStackTrace();
			}

			if (date != null) {
				long when = date.getTime();
				int flags = 0;
				flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
				flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
				flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
				flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

				finalDateTime = android.text.format.DateUtils.formatDateTime(context,
						when + TimeZone.getDefault().getOffset(when), flags);               
			}
		}
		return finalDateTime;
	}
	 */
	static boolean isOnline() { 
		ConnectivityManager cm = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 

		return cm.getActiveNetworkInfo() != null &&  
				cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
	}

	/*	private void reload() {
		Intent intent = getIntent(); //new Intent(getBaseContext(),InsertForm.class);	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish(); 
		showAllBook();
		startActivity(intent); 
	}*/

	/*
	void showAllBook() {
		// TODO Auto-generated method stub
		db = new DatabaseAdapter(context);
		db.open();

		Cursor c = db.getAllNiyay();
		final int i = c.getCount();
		Log.e("floop", Integer.toString(i));
		if (i == 0) {
			//Log.e("ck db", "not ok");
			db.close();
			ListViewContent.add("<h2>Please add your first niyay. (Menu->Add open your main niyay page or chapter you want)</h2>");
			return;
		}
		else {
			//Log.e("ok ?", "ok");
		}

		int i2 = 0;
		c.moveToFirst();		
		Time now = new Time();
		now.setToNow();
		final long curtime = now.toMillis(true); 
		do {		
			i2++;
			String[] temp  = new String[5];
			temp[0] = c.getString(0);
			temp[1] = c.getString(1);
			temp[2] = c.getString(2);
			temp[3] = c.getString(3);

			long time;
			if ((sessionTime.get(temp[2])) != null ) time = sessionTime.get(temp[2]);
			else time = 0;
			//Log.e("curtime",Long.toString(curtime));
			//Log.e("old "+temp[0],Long.toString(time));
			if ((curtime - time) > 600000) {
				temp[4] = displayBook(c);
			}
			else if (sessionStatus.get(temp[2]+temp[3]) != null) {
				temp[4] = c.getString(4);
				ListViewContent.add(sessionStatus.get(temp[2]+temp[3]));
			}
			else {
				temp[4] = c.getString(4);
				displayBookOffline(c);
			}
			//Log.e("temp[4]", temp[4]);
			niyayTable.add(temp);

			Thread t = new Thread() {
				public void run() {
					listAdap.notifyDataSetChanged();
				}
			};
			t.start();

		}while(c.moveToNext());

		Log.e("loop end", Integer.toString(i2));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}*/
	/*
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
			HttpConnectionParams.setConnectionTimeout(params, 8000);
			HttpConnectionParams.setSoTimeout(params, 8000);
			httpclient = new DefaultHttpClient(params);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			text1 = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("ClientProtocolException" , e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("IOException" , e.getMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("URISyntaxException" , e.getMessage());
			e.printStackTrace();	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("ParseException" ,text1);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("IllegalStateException" ,text1);
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		if (text1.contains("<title>")) {
			try {
				ContextWrapper cw = new ContextWrapper(this);
				File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),c.getString(0)+".html");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
				bw.write(text1.replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
				bw.flush();
				bw.close();
				System.out.println(temp.getAbsolutePath());
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

				Log.e("title",(title == null) ? "null" : title);
		Log.e("text1",text1);
		Log.e("compare",Integer.toString(text1.compareTo(title)));			

		if (title == null ) title = "";			
		else if (title.contains(">")) title = title.substring(title.indexOf(">")+2);
		if (text1.contains(">"))	text1 = text1.substring(text1.indexOf(">")+2);
		if (title.isEmpty()) {
			title = text1;
			status = -1;
		}
		else if (!text1.trim().equals(title.trim())) {
			Log.e("title",title);
			Log.e("text1",text1);
			Log.e("compare",(text1.equals(title))? "same" : "not same");
			status = 1; //current chapter update	

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


		} 

		else if (!text1.contains("ยังไม่มีตอนปัจจุบัน")) {
			status = 2;
		}
		//Log.e("status", Integer.toString(status));


		if (status == 0 ) {
			ListViewContent.add(
					"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
		}
		else if (status == 2 ) {
			ListViewContent.add(
					"<br/><p><font color=#6E6E6E>ถ้าจบตอน กดปุ่มเพิ่มตอนเพื่อเข้าสู่สถานะรอตอนใหม่</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
			sessionStatus.put(url+chapter, ListViewContent.get(ListViewContent.size()-1));
		}
		else if (status == 1 || status == -1) {
			//displayNotification(c.getString(0),c.getString(1),chapter,text1,url+chapter); 
			ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ตอน : " +text1+" ("+chapter+")</font></p>"); 
			sessionStatus.put(url+chapter, ListViewContent.get(ListViewContent.size()-1));
		}

			Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4)+"\n"+
						"text1" +text1);
		Time now = new Time();
		now.setToNow();
		sessionTime.put(url, now.toMillis(true));
		//Log.e("set "+url,Long.toString(now.toMillis(true)));

		if  (status == 0) return title;
		else if  (status == 1 || status == -1) return text1;
		return "";
	}

	void showAllBookOffline() {
		// TODO Auto-generated method stub
		ListViewContent.clear();
		niyayTable.clear();
		db = new DatabaseAdapter(context);
		db.open();

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
			niyayTable.add(temp);
			displayBookOffline(c);


		}while(c.moveToNext());
		Log.e("loop end", Integer.toString(i));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}


	 */
	//	private static String displayBookforedit(int index,Cursor c) {
	//
	//		int status = 0;
	//		String title = c.getString(4);
	//		final String url = c.getString(2); 
	//		final String chapter = c.getString(3);
	//		String text1 = "";
	//
	//		HttpClient httpclient = new DefaultHttpClient();
	//		try {
	//			HttpGet httpget = new HttpGet(new URI(url+chapter));
	//			ResponseHandler<String> responseHandler = new BasicResponseHandler();
	//			text1 = httpclient.execute(httpget, responseHandler);
	//		} catch (ClientProtocolException e) {
	//			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	//			Log.e("Error" , e.getMessage());
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	//			Log.e("Error" , e.getMessage());
	//			e.printStackTrace();
	//		} catch (URISyntaxException e) {
	//			// TODO Auto-generated catch block
	//			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	//			Log.e("Error" , e.getMessage());
	//			e.printStackTrace();		
	//		} finally {
	//			httpclient.getConnectionManager().shutdown();
	//		}
	//
	//		if (text1.contains("<title>")) {
	//			final String start = text1.substring(text1.indexOf("<title>")+7);
	//			text1 = Jsoup.parse((start.substring(start.indexOf(">")+2, start.indexOf("</title>")))).text();
	//		}
	//		else {
	//			text1 = "ยังไม่มีตอนปัจจุบัน รอตอนใหม่";
	//		}
	//
	//		if (title == null ) title = "";			
	//		else if (title.contains(">")) title = title.substring(title.indexOf(">")+2);
	//		if (text1.contains(">"))	text1 = text1.substring(text1.indexOf(">")+2);
	//		if (title.isEmpty()) {
	//			title = text1;
	//			status = -1;
	//		}
	//		else if (!text1.trim().equals(title.trim())) {
	//			Log.e("title",title);
	//			Log.e("text1",text1);
	//			Log.e("compare",(text1.equals(title))? "same" : "not same");
	//
	//		} 
	//
	//		else if (!text1.contains("ยังไม่มีตอนปัจจุบัน")) {
	//			status = 2;
	//		}
	//		Log.e("status", Integer.toString(status));
	//
	//
	//		if (status == 0 ) {
	//			ListViewContent.set(index,
	//					"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
	//							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
	//		}
	//		else if (status == 2 ) {
	//			ListViewContent.set(index,
	//					"<br/><p><font color=#6E6E6E>อ่านจบแล้วกรุณากดเพิ่มเพื่อรอตอนใหม่ด้วย</font><br />" +
	//							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
	//							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
	//		}
	//		else if (status == 1 || status == -1) {
	//			ListViewContent.set(index,
	//					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
	//							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
	//							"<font color=#cc0029> ตอน : " +text1+" ("+chapter+")</font></p>"); 
	//		}
	//
	//		Log.e("content",
	//				"id: " +c.getString(0)+"\n"+
	//						"name:" +c.getString(1)+"\n" +
	//						"url: " +c.getString(2)+"\n"+
	//						"chapter: " +c.getString(3)+"\n"+
	//						"title: " +c.getString(4)+"\n"+
	//						"text1" +text1);
	//
	//		if  (status == 0) return title;
	//		else if  (status == 1 || status == -1) return text1;
	//		return "";
	//	}
	/*
		static public String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	static public String charToHex(char c) {
		// Returns hex String representation of char c
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

	static void printBytes(byte[] array, String name) {
	    for (int k = 0; k < array.length; k++) {
	    	Log.e(name , "[" + k + "] = " + "0x"
	          + byteToHex(array[k]));
	    }
	}
	 */
	/*
	private void displayBookOffline(Cursor c) {
		//Log.e("title0",(c.getString(4) != null) ? c.getString(4):"");
		String title = c.getString(4);	

		//if (title.contains(">")) title = title.substring(title.indexOf(">"));
		//Log.e("title",(title != null) ? title:" ");

		if (title != null) 
			if ( title.contains(">")) 
				title = title.substring(title.indexOf(">")+2);
		//dialog.setTitle(title);
		ListViewContent.add(
				"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
						"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));
	}*/
	/*
	String totext(File temp) {
		ProgressDialog dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
		Document doc = null;
		try {
			doc = Jsoup.parse(temp, "tis620");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		dialog.dismiss();
		StringBuilder sum = new StringBuilder();
		Elements link1 = doc.select("table[id*=story_body]");
		for (Element link : link1) {
			sum.append(link.text());
			//System.out.println(link.text());
		}
		return sum.substring(0, sum.length() - 55).toString();
	}

	ArrayList<String> TTS(String input) {
		Locale thaiLocale = new Locale("th");
		System.out.println(input.substring(input.length()-50));
		BreakIterator boundary = BreakIterator.getSentenceInstance(thaiLocale);
		boundary.setText(input);		 
		final ArrayList<String> cuttext = printEachForward(boundary, input);
		return cuttext;		
	}

	void speak(ArrayList<String> cuttext) {
		tts.setSpeechRate((float) 0.7);
		//tts.setLanguage (new Locale( "tha", "TH"));
		for (String text : cuttext)
			tts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}

	ArrayList<String> printEachForward(BreakIterator boundary, String source) {
		ArrayList<String> arrayList=new ArrayList<String>();
		int start = boundary.first();
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {		 
			arrayList.add(source.substring(start, end));
		}		 
		return arrayList;
	}
	 */
	private class ListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		class ViewHolder {
			public TextView text;
			public ImageButton arrow;
		}

		public ListViewAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ListViewContent.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub.
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.list_item, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) arg1.findViewById(R.id.textView1);
				viewHolder.arrow = (ImageButton) arg1.findViewById(R.id.arrow1);
				arg1.setTag(viewHolder);
			}
			arg1.setOnClickListener(new OnClickListener() { 
				@Override 
				public void onClick(View v) { 
					v.showContextMenu();
				} 
			}); 

			arg1.setClickable(true); 
			arg1.setFocusable(true); 	
			//arg1.setBackgroundResource(R.drawable.list_selector); 			
			arg1.setOnCreateContextMenuListener(null);
			ViewHolder holder = (ViewHolder) arg1.getTag();			
			//TextView holdertext = (TextView) arg1.findViewById(R.id.textView1);
			//ImageButton arrow = (ImageButton) arg1.findViewById(R.id.arrow1);
			if (ListViewContent.size() > 0) {
				holder.arrow.setEnabled(true);
				holder.arrow.setVisibility(View.VISIBLE);
				holder.arrow.setFocusable(true);
				holder.arrow.setClickable(true);
				holder.arrow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg3) {
						// TODO Auto-generated method stub
						String url = niyayTable.get(arg0)[2]+niyayTable.get(arg0)[3];			
						if (!url.startsWith("http://") && !url.startsWith("https://"))
							url = "http://" + url;

						if (Setting.getArrowSelectSetting(MainActivity.this).equals("0")) {
							Intent browser = new Intent(Intent.ACTION_VIEW);
							Uri data = Uri.parse(url);
							browser.setData(data);
							startActivity(browser);
						}
						else {
							Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
							browserIntent.putExtra("id",niyayTable.get(arg0)[0]);
							browserIntent.putExtra("url",url);
							browserIntent.putExtra("title",niyayTable.get(arg0)[4]);
							startActivity(browserIntent);
						}
					}

				});
				if (ListViewContent.size() > 0)
					holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
			}
			return arg1;
		}

		@SuppressWarnings("unused")
		public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
			// empty implementation
		}
	}
	/*	private class doback extends AsyncTask<URL, Integer, Long>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			ListViewContent.clear();
			niyayTable.clear();
		}

		@Override
		protected Long doInBackground(URL... arg0) 
		{
			try
			{
				//showAllBookOffline() ;
				if (isOnline())	{
					//Log.e("on","online");
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
		protected void onProgressUpdate(Integer... progress) 
		{

		}
		protected void onPostExecute(Long result) 
		{
			Log.e("end back","end back");
			if (ListViewContent.size() == 0) {
				AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
				alertDialog.setTitle("Connection Error");
				alertDialog.setMessage("Please check your internet connection and try again.\nตรวจสอบการเชื่อมต่ออินเตอร์เน็ต แล้วลองใหม่");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// here you can add functions
					}
				});
				alertDialog.show();
			}

			setListAdapter(listAdap);
			dialog.dismiss();
			for (Map.Entry<String, String> entry : sessionStatus.entrySet()) 
				Log.e(entry.getKey(), entry.getValue());
		}
	}*/
}
