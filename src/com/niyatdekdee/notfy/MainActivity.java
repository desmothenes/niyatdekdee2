package com.niyatdekdee.notfy;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
//import com.bugsense.trace.BugSenseHandler;

public class MainActivity extends ListActivity {
    final static ArrayList<String[]> niyayTable = new ArrayList<String[]>();
    static DatabaseAdapter db;
    static Context context;
    static ArrayList<String> ListViewContent = new ArrayList<String>();
    static ListViewAdapter listAdap;
    //private static ListView myList;
    //private boolean resumeHasRun = false;
    static ProgressDialog dialog;
    static boolean isTTS = true;
    static ListView myList;
    //private static final int REQUEST_CODE=1;
    //static boolean LoadPage = false;
    static int titleColor = -1;
    //static int floop;
    //static Map<String, String> sessionStatus = new HashMap<String, String>();
    static Tracker mGaTracker;
    private ImageButton btnDirection;
    //static doback dob;
    //private WakeLock wl;
    private GoogleAnalytics mGaInstance;

    static void update() {
        Log.e("doback at", "update static");
        new doback(context).execute();
        //listAdap.notifyDataSetChanged();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BugSenseHandler.initAndStartSession(MainActivity.this, "7942beee");
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean("first_run", true)) {
            Log.e("doback at", "first_run");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putLong("alarm_time", System.currentTimeMillis());
            editor.putString("keySelectItem", "6");
            editor.putBoolean("first_run", false);

            System.out.print("sc ");
            System.out.println(getResources().getConfiguration().orientation);//Display getOrient = getWindowManager().getDefaultDisplay();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                System.out.print("sc ");
                System.out.println(getResources().getConfiguration().orientation);// orientation = Configuration.ORIENTATION_LANDSCAPE;
                editor.putString("sreenRol", "1");
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            editor.commit();
            new Alarm().SetAlarm(getApplicationContext());
        } else if (Setting.getScreenSetting(getApplicationContext()).equals("1")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        mGaInstance = GoogleAnalytics.getInstance(this);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker("UA-37746897-1");
        if (customTitleSupported) {

            //ตั้งค่า custom titlebar จาก custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_main);

            //RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.mainbar);

            //titleColor = Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this));

            ImageButton btnRefresh = (ImageButton) findViewById(R.id.imageButton1);
            ImageButton btnAdd = (ImageButton) findViewById(R.id.imageButton2);
            ImageButton btnSetting = (ImageButton) findViewById(R.id.btnSetting);


            TextView title = (TextView) findViewById(R.id.textViewTitle);
            title.setText(" รายการนิยาย");

            btnRefresh.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dialog = ProgressDialog.show(MainActivity.this, "Loading", "โปรดรอ...\nถ้ารู้สึกช้า โปรดออกแแล้วเข้าใหม่", true);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    Log.e("doback at", "btnRefresh");
                    new doback(getApplicationContext()).execute();
                    mGaTracker.sendEvent("ui_action", "button_press", "refresh", (long) 0);
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    addmenu();
                    mGaTracker.sendEvent("ui_action", "button_press", "title_add", (long) 0);
                }
            });

            btnSetting.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    settingmenu();
                }
            });

            btnDirection = (ImageButton) findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (DekTTSActivity.tts != null) {
                        if (DekTTSActivity.isSpeak)
                            Toast.makeText(getBaseContext(), "tts กำลังหยุดปรธโยคสุดท้าย", Toast.LENGTH_SHORT).show();
                        DekTTSActivity.stop = true;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("ต้องการที่จะออก?\n\nช่วยแนะนำติชมเพิ่มเติมผ่านทางช่องทางต่างๆ เช่น e-mail fanpage review เพื่อนำมาปรับปรุงต่อไปครับ")
                            .setCancelable(false)
                            .setPositiveButton("ออก", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    if (DekTTSActivity.tts != null) DekTTSActivity.tts.shutdown();
                                    //if (wl != null) wl.release();
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

        dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...\nถ้ารู้สึกช้า ออกแล้วเข้าใหม่", true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
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
        WebView obj = new WebView(this);
        obj.clearCache(true);
		/*		if (prefs.getBoolean("ttsinstall", true)) {
			Intent intent = new Intent(getBaseContext(), DekTTSActivity.class);
			DekTTSActivity.type = 99;
			getBaseContext().startService(intent);
		}*/
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

        //WakefulIntentService.acquireStaticLock(context);
        //context.startService(new Intent(context, NiyayService.class));
        //context.startService(new Intent(context, Alarm.class));
		/*		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getBoolean("first_run", true)) {
			Log.e("doback at", "first_run");
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			editor.putLong("alarm_time", System.currentTimeMillis());
			editor.putString("keySelectItem", "6");
			editor.putBoolean("first_run", false);
			editor.commit();
			Toast.makeText(context, "สามารถเปลี่ยนแนวหน้าจอได้จากตั่งค่า เท่านั้น", Toast.LENGTH_LONG).show();
			//Display getOrient = getWindowManager().getDefaultDisplay();
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				System.out.print("sc ");System.out.println(getResources().getConfiguration().orientation );// orientation = Configuration.ORIENTATION_LANDSCAPE;
				editor.putString("sreenRol", "1");
			}
			new Alarm().SetAlarm(getApplicationContext());
			Toast.makeText(context, "สามารถเปลี่ยนแนวหน้าจอได้จากตั่งค่า เท่านั้น", Toast.LENGTH_LONG).show();
		}*/
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
    protected void onResume() {
        //dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
        super.onResume();
        if (Setting.getScreenSetting(getApplicationContext()).equals("1")) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (titleColor != Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this))) {
            titleColor = Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this));
            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.mainbar);
            switch (titleColor) {
                case 0:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar);
                    mGaTracker.sendEvent("ui_layout", "color", "cyal", (long) 0);
                    break;
                case 1:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    mGaTracker.sendEvent("ui_layout", "color", "yellow", (long) 0);
                    break;
                case 2:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
                    mGaTracker.sendEvent("ui_layout", "color", "green", (long) 0);
                    break;
                case 3:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    mGaTracker.sendEvent("ui_layout", "color", "pink", (long) 0);
                    break;
                case 4:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    mGaTracker.sendEvent("ui_layout", "color", "blue", (long) 0);
                    break;
                case 5:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
                    mGaTracker.sendEvent("ui_layout", "color", "fuchsia", (long) 0);
                    break;
                case 6:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_siver);
                    mGaTracker.sendEvent("ui_layout", "color", "siver", (long) 0);
                    break;
                case 7:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_glay);
                    mGaTracker.sendEvent("ui_layout", "color", "glay", (long) 0);
                    break;
                case 8:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_orange);
                    mGaTracker.sendEvent("ui_layout", "color", "orange", (long) 0);
                    break;
            }
        }
		/*
		if (DekTTSActivity.tts != null) {
			try {
				DekTTSActivity.tts.stop();
			} catch (IllegalArgumentException e) {
				getApplicationContext().unbindService((ServiceConnection) DekTTSActivity.tts);
			}
		}*/
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
        Log.e("zone", "onresume");

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

    @Override
    public void onBackPressed() {
        if (DekTTSActivity.tts != null) {
            if (DekTTSActivity.isSpeak)
                Toast.makeText(getBaseContext(), "tts กำลังหยุดหลังประโยคสุดท้าย", Toast.LENGTH_SHORT).show();
            DekTTSActivity.isSpeak = false;
            DekTTSActivity.stop = true;
        }
        if (dialog.isShowing()) {
            Toast.makeText(getBaseContext(), "การดำเนินการยังดำเนินอยู่", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("ต้องการที่จะออก?\n\nช่วยแนะนำติชมเพิ่มเติมผ่านทางช่องทางต่าง ๆ เช่น e-mail fanpage review เพื่อนำมาปรับปรุงต่อไปด้วยครับ")
                .setCancelable(false)
                .setPositiveButton("ออก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (DekTTSActivity.tts != null) DekTTSActivity.tts.shutdown();
                        //if (wl != null) wl.release();
                        Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                        stopService(intent);
                        File temp = new File(Environment.getExternalStorageDirectory(), "niyay_temp");
                        if (temp.exists())
                            temp.delete();
                        ContextWrapper cw = new ContextWrapper(getBaseContext());
                        File temp2 = new File(cw.getDir("temp", Context.MODE_PRIVATE), "tt.html");
                        if (temp2.exists())
                            temp2.delete();
                        File temp3 = new File(Environment.getExternalStorageDirectory(), "tt.html");
                        if (temp3.exists())
                            temp3.delete();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (ListViewContent == null) {
            addmenu();
            return;
        }
        if (ListViewContent.size() == 0) {
            addmenu();
            return;
        }
        if (ListViewContent.get(0).equals("<h2>Please add your first niyay. (Menu->Add open your main niyay page or chapter you want)</h2>")) {
            addmenu();
            return;
        }
        getMenuInflater().inflate(R.menu.menu_data, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //setContentView(R.layout.activity_main);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listItemName = (int) info.id;
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

        switch (item.getItemId()) {
            case R.id.open:
                String url;
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + chapter;
                    mGaTracker.sendEvent("ui_action", "button_press", "open", (long) 0);
                } else {
                    url = niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3];
                    mGaTracker.sendEvent("ui_action", "button_press", "open_fav", (long) 0);
                }
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                //Log.e("url", url);
                //			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                //			Uri data = Uri.parse(url);
                //			browserIntent.setData(data);
                //			startActivity(browserIntent);
                Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
                browserIntent.putExtra("id", niyayTable.get(listItemName)[0]);
                browserIntent.putExtra("url", url);
                browserIntent.putExtra("title", niyayTable.get(listItemName)[4]);
                if (doback.sessionId != null) {
                    StringBuilder cookieString = new StringBuilder();
                    for (String key : doback.sessionId.keySet()) {
                        Log.v(key, doback.sessionId.get(key));
                        cookieString.append(key).append("=").append(doback.sessionId.get(key)).append(";");
                    }
                    browserIntent.putExtra("cookieString", cookieString.toString());
                }
                startActivity(browserIntent);
                if (!Setting.getAutoAdd(getApplicationContext()) || niyayTable.get(listItemName)[4].equals("ยังไม่มีตอนปัจจุบัน รอตอนใหม่"))
                    return true;
                else if (niyayTable.get(listItemName)[0].equals("-2")) {
                    //open
                    new AsyncTask<Integer, String, Void>() {

                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            // TODO Auto-generated method stub
                            try {
                                Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                            } catch (IOException e) {
                                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                // TODO Auto-generated catch block
                                publishProgress("-1");
                                e.printStackTrace();
                            } finally {
                                niyayTable.remove(listItemName);
                                ListViewContent.remove(listItemName);
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            //dialog.dismiss();
                            listAdap.notifyDataSetChanged();
                        }

                    }.execute();
                    return true;
                } else {
                    if (niyayTable.get(listItemName)[0].equals("-2")) {
                        new AsyncTask<Integer, String, Void>() {
                            //addcp
                            protected void onPreExecute() {
                                Log.d("ASYNCTASK", "Pre execute for task : ");
                                //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();
                                dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            }

                            @Override
                            protected Void doInBackground(Integer... params) {
                                // TODO Auto-generated method stub
                                try {
                                    Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                                } catch (IOException e) {
                                    //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    // TODO Auto-generated catch block
                                    publishProgress("-1");
                                    e.printStackTrace();
                                } finally {
                                    niyayTable.remove(listItemName);
                                    ListViewContent.remove(listItemName);
                                    if (ListViewContent.size() == 0) {
                                        update();
                                    }
                                }
                                return null;
                            }

                            protected void onProgressUpdate(String... progress) {        //publishProgress
                                if (progress[0].equals("-1")) {
                                    Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                                } else {
                                    //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                                }
                            }

                            protected void onPostExecute(Void result) {
                                if (dialog.isShowing()) dialog.dismiss();
                                listAdap.notifyDataSetChanged();
                                mGaTracker.sendEvent("ui_action", "button_press", "add_cp_fav", (long) 0);
                            }

                        }.execute();
                        return true;
                    }
                    niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) + 1);
                    new AsyncTask<Integer, String, Void>() {
                        String doc = "";

                        //addcp
                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();

                            dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            ////dialog.setCancelable(true);
                        }

                        @Override
                        protected Void doInBackground(Integer... args) {
                            HttpClient httpclient = new DefaultHttpClient();
                            try {
                                HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                doc = httpclient.execute(httpget, responseHandler);
                            } catch (ClientProtocolException e) {
                                publishProgress(e.getMessage());
                                HttpGet method = new HttpGet(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                                BufferedReader in = null;
                                try {
                                    DefaultHttpClient client = new DefaultHttpClient();
                                    HttpGet request = new HttpGet();
                                    request.setURI(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                    request.setHeader("Range", "bytes=0-1023");
                                    HttpResponse response = client.execute(method);
                                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                                    StringBuffer sb = new StringBuffer("");
                                    String line = "";
                                    String NL = System.getProperty("line.separator");
                                    while ((line = in.readLine()) != null) {
                                        sb.append(line).append(NL);
                                    }
                                    in.close();
                                    doc = sb.toString();
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } catch (URISyntaxException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } finally {
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                                //e.printStackTrace();
                            } catch (IOException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } finally {
                                httpclient.getConnectionManager().shutdown();
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0] == null) return;
                            else if (progress[0].isEmpty()) return;
                            else if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            Log.d("ASYNCTASK", "Post execute for task : ");
                            //Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
                            final int start;
                            if ((start = doc.indexOf("<title>")) != -1) {
                                try {
                                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                                    bw.write(doc);
                                    bw.flush();
                                    bw.close();
                                    //System.out.println(temp.getAbsolutePath());
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                doc = doc.substring(start + 7, doc.indexOf("</title>"));
                                doc = Jsoup.parse((doc.substring(doc.indexOf(">") + 2))).text();
                            } else {
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
                                niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) - 1);
                            }
                            niyayTable.get(listItemName)[4] = doc;
                            if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals(""))
                                return;
                            flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]),
                                    niyayTable.get(listItemName)[4]);
                            if (flag) {
                                //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                            }
                            //Intent i = new Intent(context,MainActivity.class);
                            db.close();
                            ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                                    "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                            );
                            //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                            doback.sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                            listAdap.notifyDataSetChanged();
                            if (dialog.isShowing()) dialog.dismiss();
                            mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                        }


                    }.execute();
                    return true;
                }
            case R.id.addcp:
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    new AsyncTask<Integer, String, Void>() {
                        //addcp
                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();
                            dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            // TODO Auto-generated method stub
                            try {
                                Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                            } catch (IOException e) {
                                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                // TODO Auto-generated catch block
                                publishProgress("-1");
                                e.printStackTrace();
                            } finally {
                                niyayTable.remove(listItemName);
                                ListViewContent.remove(listItemName);
                                if (ListViewContent.size() == 0) {
                                    update();
                                }
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            if (dialog.isShowing()) dialog.dismiss();
                            listAdap.notifyDataSetChanged();
                            mGaTracker.sendEvent("ui_action", "button_press", "add_cp_fav", (long) 0);
                        }

                    }.execute();
                    return true;
                }
                niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) + 1);
                new AsyncTask<Integer, String, Void>() {
                    String doc = "";

                    //addcp
                    protected void onPreExecute() {
                        Log.d("ASYNCTASK", "Pre execute for task : ");
                        //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();

                        dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                        ////dialog.setCancelable(true);
                    }

                    @Override
                    protected Void doInBackground(Integer... args) {
                        HttpClient httpclient = new DefaultHttpClient();
                        try {
                            HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            doc = httpclient.execute(httpget, responseHandler);
                        } catch (ClientProtocolException e) {
                            publishProgress(e.getMessage());
                            HttpGet method = new HttpGet(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                            BufferedReader in = null;
                            try {
                                DefaultHttpClient client = new DefaultHttpClient();
                                HttpGet request = new HttpGet();
                                request.setURI(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                request.setHeader("Range", "bytes=0-1023");
                                HttpResponse response = client.execute(method);
                                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                                StringBuffer sb = new StringBuffer("");
                                String line = "";
                                String NL = System.getProperty("line.separator");
                                while ((line = in.readLine()) != null) {
                                    sb.append(line).append(NL);
                                }
                                in.close();
                                doc = sb.toString();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (URISyntaxException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } finally {
                                if (in != null) {
                                    try {
                                        in.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            //e.printStackTrace();
                        } catch (IOException e) {
                            publishProgress(e.getMessage());
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            publishProgress(e.getMessage());
                            e.printStackTrace();
                        } finally {
                            httpclient.getConnectionManager().shutdown();
                        }
                        return null;
                    }

                    protected void onProgressUpdate(String... progress) {        //publishProgress
                        if (progress[0] == null) return;
                        else if (progress[0].isEmpty()) return;
                        else if (progress[0].equals("-1")) {
                            Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                            Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                        } else {
                            Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                        }
                    }

                    protected void onPostExecute(Void result) {
                        Log.d("ASYNCTASK", "Post execute for task : ");
                        //Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
                        final int start;
                        if ((start = doc.indexOf("<title>")) != -1) {
                            try {
                                File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                                bw.write(doc);
                                bw.flush();
                                bw.close();
                                //System.out.println(temp.getAbsolutePath());
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            doc = doc.substring(start + 7, doc.indexOf("</title>"));
                            doc = Jsoup.parse((doc.substring(doc.indexOf(">") + 2))).text();
                        } else {
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
                            niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) - 1);
                        }
                        niyayTable.get(listItemName)[4] = doc;
                        if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals(""))
                            return;
                        flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]),
                                niyayTable.get(listItemName)[4]);
                        if (flag) {
                            //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                        }
                        //Intent i = new Intent(context,MainActivity.class);
                        db.close();
                        ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                                "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                        );
                        //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                        doback.sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                        listAdap.notifyDataSetChanged();
                        if (dialog.isShowing()) dialog.dismiss();
                        mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                    }


                }.execute();
                return true;
            case R.id.openweb:
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + chapter + "#story_body";
                    mGaTracker.sendEvent("ui_action", "button_press", "web_fav", (long) 0);
                } else {
                    url = niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3] + "#story_body";
                    mGaTracker.sendEvent("ui_action", "button_press", "web", (long) 0);
                }
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                if (!Setting.getAutoAdd(getApplicationContext()) || niyayTable.get(listItemName)[4].equals("ยังไม่มีตอนปัจจุบัน รอตอนใหม่"))
                    return true;
                else if (niyayTable.get(listItemName)[0].equals("-2")) {
                    //open
                    new AsyncTask<Integer, String, Void>() {

                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            // TODO Auto-generated method stub
                            try {
                                Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                            } catch (IOException e) {
                                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                // TODO Auto-generated catch block
                                publishProgress("-1");
                                e.printStackTrace();
                            } finally {
                                niyayTable.remove(listItemName);
                                ListViewContent.remove(listItemName);
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            //dialog.dismiss();
                            listAdap.notifyDataSetChanged();
                        }

                    }.execute();
                    return true;
                } else {
                    if (niyayTable.get(listItemName)[0].equals("-2")) {
                        new AsyncTask<Integer, String, Void>() {
                            //addcp
                            protected void onPreExecute() {
                                Log.d("ASYNCTASK", "Pre execute for task : ");
                                //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();
                                dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            }

                            @Override
                            protected Void doInBackground(Integer... params) {
                                // TODO Auto-generated method stub
                                try {
                                    Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                                } catch (IOException e) {
                                    //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    // TODO Auto-generated catch block
                                    publishProgress("-1");
                                    e.printStackTrace();
                                } finally {
                                    niyayTable.remove(listItemName);
                                    ListViewContent.remove(listItemName);
                                    if (ListViewContent.size() == 0) {
                                        update();
                                    }
                                }
                                return null;
                            }

                            protected void onProgressUpdate(String... progress) {        //publishProgress
                                if (progress[0].equals("-1")) {
                                    Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                                } else {
                                    //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                                }
                            }

                            protected void onPostExecute(Void result) {
                                if (dialog.isShowing()) dialog.dismiss();
                                listAdap.notifyDataSetChanged();
                                mGaTracker.sendEvent("ui_action", "button_press", "add_cp_fav", (long) 0);
                            }

                        }.execute();
                        return true;
                    }
                    niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) + 1);
                    new AsyncTask<Integer, String, Void>() {
                        String doc = "";

                        //addcp
                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();

                            dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            ////dialog.setCancelable(true);
                        }

                        @Override
                        protected Void doInBackground(Integer... args) {
                            HttpClient httpclient = new DefaultHttpClient();
                            try {
                                HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                doc = httpclient.execute(httpget, responseHandler);
                            } catch (ClientProtocolException e) {
                                publishProgress(e.getMessage());
                                HttpGet method = new HttpGet(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                                BufferedReader in = null;
                                try {
                                    DefaultHttpClient client = new DefaultHttpClient();
                                    HttpGet request = new HttpGet();
                                    request.setURI(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                    request.setHeader("Range", "bytes=0-1023");
                                    HttpResponse response = client.execute(method);
                                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                                    StringBuffer sb = new StringBuffer("");
                                    String line = "";
                                    String NL = System.getProperty("line.separator");
                                    while ((line = in.readLine()) != null) {
                                        sb.append(line).append(NL);
                                    }
                                    in.close();
                                    doc = sb.toString();
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } catch (URISyntaxException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } finally {
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                                //e.printStackTrace();
                            } catch (IOException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } finally {
                                httpclient.getConnectionManager().shutdown();
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].isEmpty()) return;
                            else if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            Log.d("ASYNCTASK", "Post execute for task : ");
                            //Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
                            final int start;
                            if ((start = doc.indexOf("<title>")) != -1) {
                                try {
                                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                                    bw.write(doc);
                                    bw.flush();
                                    bw.close();
                                    //System.out.println(temp.getAbsolutePath());
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                doc = doc.substring(start + 7, doc.indexOf("</title>"));
                                if (doc.length() > doc.indexOf(">") + 2)
                                    doc = Jsoup.parse((doc.substring(doc.indexOf(">") + 2))).text();
                                else
                                    doc = Jsoup.parse((doc)).text();
                            } else {
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
                                niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) - 1);
                            }
                            niyayTable.get(listItemName)[4] = doc;
                            if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals(""))
                                return;
                            flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]),
                                    niyayTable.get(listItemName)[4]);
                            if (flag) {
                                //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                            }
                            //Intent i = new Intent(context,MainActivity.class);
                            db.close();
                            ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                                    "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                            );
                            //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                            doback.sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                            listAdap.notifyDataSetChanged();
                            if (dialog.isShowing()) dialog.dismiss();
                            mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                        }

                        ;
                    }.execute();
                }
                return true;
            case R.id.opentext:
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + chapter;
                    mGaTracker.sendEvent("ui_action", "button_press", "open_text_fav", (long) 0);
                    mGaTracker.sendEvent("url", "story", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=", (long) 0);
                } else {
                    url = niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3];
                    mGaTracker.sendEvent("url", "story", niyayTable.get(listItemName)[2], (long) 0);
                    mGaTracker.sendEvent("ui_action", "button_press", "open_text", (long) 0);
                }
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                //Log.e("url", url);
                //			Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                //			Uri data = Uri.parse(url);
                //			browserIntent.setData(data);
                //			startActivity(browserIntent);
                Intent TextReadActivity = new Intent(getBaseContext(), TextReadActivity.class);
                TextReadActivity.putExtra("id", niyayTable.get(listItemName)[0]);
                TextReadActivity.putExtra("url", url);
                TextReadActivity.putExtra("from", "main");
			/*			if (doback.sessionId != null) {
				StringBuilder cookieString = new StringBuilder();
				for(String key: doback.sessionId.keySet()){
					Log.v(key, doback.sessionId.get(key));
					cookieString.append(key + "=" +doback.sessionId.get(key)+ ";");
				}
				TextReadActivity.putExtra("cookieString",cookieString.toString());
			}*/
                //TextReadActivity.putExtra("title",niyayTable.get(listItemName)[4]);
			/*			if (doback.sessionId != null) {
				StringBuilder cookieString = new StringBuilder();
				for(String key: doback.sessionId.keySet()){
					Log.v(key, doback.sessionId.get(key));
					cookieString.append(key + "=" +doback.sessionId.get(key)+ ";");
				}
				TextReadActivity.putExtra("cookieString",cookieString.toString());
			}*/
                startActivity(TextReadActivity);
                if (!Setting.getAutoAdd(getApplicationContext()) || niyayTable.get(listItemName)[4].equals("ยังไม่มีตอนปัจจุบัน รอตอนใหม่"))
                    return true;
                else if (niyayTable.get(listItemName)[0].equals("-2")) {
                    //open
                    new AsyncTask<Integer, String, Void>() {

                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            // TODO Auto-generated method stub
                            try {
                                Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                            } catch (IOException e) {
                                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                // TODO Auto-generated catch block
                                publishProgress("-1");
                                e.printStackTrace();
                            } finally {
                                niyayTable.remove(listItemName);
                                ListViewContent.remove(listItemName);
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            //dialog.dismiss();
                            listAdap.notifyDataSetChanged();
                        }

                    }.execute();
                    return true;
                } else {
                    if (niyayTable.get(listItemName)[0].equals("-2")) {
                        new AsyncTask<Integer, String, Void>() {
                            //addcp
                            protected void onPreExecute() {
                                Log.d("ASYNCTASK", "Pre execute for task : ");
                                //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();
                                dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            }

                            @Override
                            protected Void doInBackground(Integer... params) {
                                // TODO Auto-generated method stub
                                try {
                                    Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                                } catch (IOException e) {
                                    //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    // TODO Auto-generated catch block
                                    publishProgress("-1");
                                    e.printStackTrace();
                                } finally {
                                    niyayTable.remove(listItemName);
                                    ListViewContent.remove(listItemName);
                                    if (ListViewContent.size() == 0) {
                                        update();
                                    }
                                }
                                return null;
                            }

                            protected void onProgressUpdate(String... progress) {        //publishProgress
                                if (progress[0].equals("-1")) {
                                    Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                    Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                                } else {
                                    //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                                }
                            }

                            protected void onPostExecute(Void result) {
                                if (dialog.isShowing()) dialog.dismiss();
                                listAdap.notifyDataSetChanged();
                                mGaTracker.sendEvent("ui_action", "button_press", "add_cp_fav", (long) 0);
                            }

                        }.execute();
                        return true;
                    }
                    niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) + 1);
                    new AsyncTask<Integer, String, Void>() {
                        String doc = "";

                        //addcp
                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();

                            dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                            ////dialog.setCancelable(true);
                        }

                        @Override
                        protected Void doInBackground(Integer... args) {
                            HttpClient httpclient = new DefaultHttpClient();
                            try {
                                HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                doc = httpclient.execute(httpget, responseHandler);
                            } catch (ClientProtocolException e) {
                                publishProgress(e.getMessage());
                                HttpGet method = new HttpGet(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                                BufferedReader in = null;
                                try {
                                    DefaultHttpClient client = new DefaultHttpClient();
                                    HttpGet request = new HttpGet();
                                    request.setURI(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                    request.setHeader("Range", "bytes=0-1023");
                                    HttpResponse response = client.execute(method);
                                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                                    StringBuffer sb = new StringBuffer("");
                                    String line = "";
                                    String NL = System.getProperty("line.separator");
                                    while ((line = in.readLine()) != null) {
                                        sb.append(line).append(NL);
                                    }
                                    in.close();
                                    doc = sb.toString();
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } catch (URISyntaxException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                } finally {
                                    if (in != null) {
                                        try {
                                            in.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                                //e.printStackTrace();
                            } catch (IOException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                publishProgress(e.getMessage());
                                e.printStackTrace();
                            } finally {
                                httpclient.getConnectionManager().shutdown();
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0] == null) ;
                            else if (progress[0].isEmpty()) ;
                            else if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            } else {
                                Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                            }
                        }

                        protected void onPostExecute(Void result) {
                            Log.d("ASYNCTASK", "Post execute for task : ");
                            //Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
                            final int start;
                            if ((start = doc.indexOf("<title>")) != -1) {
                                try {
                                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                                    bw.write(doc);
                                    bw.flush();
                                    bw.close();
                                    //System.out.println(temp.getAbsolutePath());
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                doc = doc.substring(start + 7, doc.indexOf("</title>"));
                                doc = Jsoup.parse((doc.substring(doc.indexOf(">") + 2))).text();
                            } else {
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
                                niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) - 1);
                            }
                            niyayTable.get(listItemName)[4] = doc;
                            if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals(""))
                                return;
                            flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]),
                                    niyayTable.get(listItemName)[4]);
                            if (flag) {
                                //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                            }
                            //Intent i = new Intent(context,MainActivity.class);
                            db.close();
                            ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                                    "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                            );
                            //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                            doback.sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                            listAdap.notifyDataSetChanged();
                            if (dialog.isShowing()) dialog.dismiss();
                            mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                        }


                    }.execute();
                }
                return true;
            case R.id.red:
                //Log.e("replace with", niyayTable.get(listItemName)[4]);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    new AsyncTask<Integer, String, Void>() {
                        //red
                        protected void onPreExecute() {
                            Log.d("ASYNCTASK", "Pre execute for task : ");
                            //Toast.makeText(getApplicationContext(), "รอสักครู่ กำลังทำงานอยู่เบื้องหลัง", Toast.LENGTH_LONG).show();
                            dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                        }

                        @Override
                        protected Void doInBackground(Integer... params) {
                            // TODO Auto-generated method stub
                            try {
                                Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(doback.sessionId).timeout(3000).get();
                            } catch (IOException e) {
                                publishProgress("-1");
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } finally {
                                niyayTable.remove(listItemName);
                                ListViewContent.remove(listItemName);
                            }
                            return null;
                        }

                        protected void onProgressUpdate(String... progress) {        //publishProgress
                            if (progress[0].equals("-1")) {
                                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                            }
                        }

                        protected void onPostExecute(Void result) {
                            if (dialog.isShowing()) dialog.dismiss();
                            listAdap.notifyDataSetChanged();
                            mGaTracker.sendEvent("ui_action", "button_press", "red", (long) 0);
                        }
                    }.execute();
                    return true;
                }
                db.open();
                if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals("")) return true;
                else Log.e("niyayTable.get(listItemName)[4]", niyayTable.get(listItemName)[4]);
                boolean flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), niyayTable.get(listItemName)[4]);
                if (flag) {
                    Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                }
                //Intent i = new Intent(context,MainActivity.class);
                db.close();
                //reload();
                ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                        "<font color=#cc0029> ล่าสุด ตอน : " + niyayTable.get(listItemName)[4] + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                );
                //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                doback.sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                listAdap.notifyDataSetChanged();
                return true;
            case R.id.tts:
                if (niyayTable.size() < listItemName + 1) return true;
                mGaTracker.sendEvent("ui_action", "button_press", "main_tts", (long) 0);
                if (!isTTS) {
                    Intent intent = new Intent(getApplicationContext(), Flow2.class);
                    intent.putExtra("from", "main");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

                if (DekTTSActivity.tts != null) {
                    DekTTSActivity.tts.stop();
                    DekTTSActivity.stop = true;
                    //Toast.makeText(getBaseContext(), "tts กำลังหยุดหลังประโยคสุดท้าย", Toast.LENGTH_LONG).show();
                }

                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    //TTS.totext("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter="+chapter);
                    Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                    intent.putExtra("from", "main");
                    DekTTSActivity.type = 1;
                    DekTTSActivity.text = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + chapter;
                    startService(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                    intent.putExtra("from", "main");
                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                    if (temp.canRead()) {
                        DekTTSActivity.type = 2;                    //intent.putExtra("text", temp.getAbsolutePath());
                        DekTTSActivity.temp = temp;
                        startService(intent);

                    } else {
                        DekTTSActivity.type = 1;/*
					System.out.println(niyayTable.get(listItemName)[2]);
					System.out.println(niyayTable.get(listItemName)[4]);*/

                        final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "id=", getBaseContext());
                        //	final String chapter = MyAppClass.findnum(, "ตอนที่ ", getBaseContext());
                        DekTTSActivity.text = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + niyayTable.get(listItemName)[3];
                        startService(intent);

                    }
                }
                //TTS.totext(new File(cw.getDir("temp", Context.MODE_PRIVATE),niyayTable.get(listItemName)[0]+".html"));
                return true;
            case R.id.edit:
                mGaTracker.sendEvent("ui_action", "button_press", "edit", (long) 0);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
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
                intent.putExtra("listItemName", listItemName);
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
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    //final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    chapterlist.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=");
                    mGaTracker.sendEvent("ui_action", "button_press", "cplist_fav", (long) 0);
                } else {
                    chapterlist.putExtra("url", niyayTable.get(listItemName)[2]);
                    mGaTracker.sendEvent("ui_action", "button_press", "cplist", (long) 0);
                }
                chapterlist.putExtra("title", niyayTable.get(listItemName)[1]);
                startActivity(chapterlist);
                return true;
            case R.id.dec:
                mGaTracker.sendEvent("ui_action", "button_press", "dec", (long) 0);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    Toast.makeText(context, "ไม่รองรับกับ favorite writer", Toast.LENGTH_LONG).show();
                    return true;
                }
                new AsyncTask<Integer, String, Boolean>() {
                    String doc = "";
                    //dec

                    protected void onPreExecute() {
                        Log.d("ASYNCTASK", "Pre execute for task : ");
                        dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait...", true);
                        ////dialog.setCancelable(true);
                    }


                    @Override
                    protected Boolean doInBackground(Integer... args) {
                        DefaultHttpClient httpclient = new DefaultHttpClient();
                        if (Integer.parseInt(niyayTable.get(listItemName)[3]) - 1 > 0) {
                            niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) - 1);
                        } else {
                            publishProgress("ไม่สามารถลดต่ำกว่า 1 ได้");
                            return false;
                        }


                        try {
                            if (doback.cookies != null) {
                                for (Cookie cookie : doback.cookies) {
                                    httpclient.getCookieStore().addCookie(cookie);
                                }
                            }
                            HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            doc = httpclient.execute(httpget, responseHandler);
                        } catch (ClientProtocolException e) {
                            publishProgress(e.getMessage());
                            HttpGet method = new HttpGet(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                            BufferedReader in = null;
                            try {
                                DefaultHttpClient client = new DefaultHttpClient();
                                HttpGet request = new HttpGet();
                                request.setURI(new URI(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]));
                                request.setHeader("Range", "bytes=0-1023");
                                HttpResponse response = client.execute(method);
                                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                                StringBuffer sb = new StringBuffer("");
                                String line = "";
                                String NL = System.getProperty("line.separator");
                                while ((line = in.readLine()) != null) {
                                    sb.append(line).append(NL);
                                }
                                in.close();
                                doc = sb.toString();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                                return false;
                            } catch (URISyntaxException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                                return false;
                            } finally {
                                if (in != null) {
                                    try {
                                        in.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                        return false;
                                    }
                                }
                            }
                            //e.printStackTrace();
                        } catch (IOException e) {
                            publishProgress(e.getMessage());
                            e.printStackTrace();
                            return false;
                        } catch (URISyntaxException e) {
                            publishProgress(e.getMessage());
                            e.printStackTrace();
                            return false;
                        } finally {
                            httpclient.getConnectionManager().shutdown();
                        }
                        return true;
                    }

                    protected void onProgressUpdate(String... progress) {
                        if (progress[0] != null) if (!progress[0].isEmpty()) if (progress[0].equals("-1")) {
                            //Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                            Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                        } else {
                            Toast.makeText(context, progress[0], Toast.LENGTH_SHORT).show();
                        }

                    }

                    protected void onPostExecute(Boolean result) {
                        Log.d("ASYNCTASK", "Post execute for task : ");
                        if (result) {
                            final int start2;
                            if ((start2 = doc.indexOf("<title>")) != -1) {
                                try {
                                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                                    bw.write(doc);
                                    bw.flush();
                                    bw.close();
                                    //System.out.println(temp.getAbsolutePath());
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                doc = doc.substring(start2 + 7, doc.indexOf("</title>"));
                                //Log.e("url", doc);
                                doc = Jsoup.parse((doc.substring(doc.indexOf(">") + 2))).text();
                            } else {
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
                                niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3]) + 1);
                            }

                            niyayTable.get(listItemName)[4] = doc;
                            if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4].equals(""))
                                return;
                            flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]),
                                    niyayTable.get(listItemName)[4]);
                            if (flag) {
                                //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                            }
                            //Intent i = new Intent(context,MainActivity.class);
                            db.close();
                            ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                                    "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                            );
                            listAdap.notifyDataSetChanged();
                        }
                        if (dialog.isShowing()) dialog.dismiss();
                    }

                    ;
                }.execute();
                //Toast.makeText(context, "dec", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                mGaTracker.sendEvent("ui_action", "button_press", "delete", (long) 0);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    Toast.makeText(context, "ไม่รองรับกับ favorite writer", Toast.LENGTH_LONG).show();
                    return true;
                }
                //Toast.makeText(context, "del ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.delete);
                builder.setMessage("คุณต้องการที่จะลบเรื่อง " + niyayTable.get(listItemName)[1] + " ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.open();
                                boolean flag;
                                flag = db.deleteNiyay(Long.parseLong(niyayTable.get(listItemName)[0]));
                                if (flag) {
                                    Toast.makeText(context, "delete succeed", Toast.LENGTH_SHORT).show();
                                    ContextWrapper cw = new ContextWrapper(context);
                                    File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), niyayTable.get(listItemName)[0] + ".html");
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
            case R.id.longread:
                Intent longread = new Intent(context, LongRead.class);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    //final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    longread.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=");
                    mGaTracker.sendEvent("ui_action", "button_press", "longread_fav", (long) 0);
                } else {
                    longread.putExtra("url", niyayTable.get(listItemName)[2]);
                    mGaTracker.sendEvent("ui_action", "button_press", "longread", (long) 0);
                }
                startActivity(longread);
                return true;
            case R.id.newlongread:
                Intent longread1 = new Intent(context, LongRead2.class);
                if (niyayTable.size() < listItemName + 1) return true;
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    //final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
                    longread1.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=");
                    mGaTracker.sendEvent("ui_action", "button_press", "longread_fav", (long) 0);
                } else {
                    longread1.putExtra("url", niyayTable.get(listItemName)[2]);
                    mGaTracker.sendEvent("ui_action", "button_press", "longread", (long) 0);
                }
                startActivity(longread1);
                return true;
            case R.id.openfast:
                Intent FastReadActivity = new Intent(getBaseContext(), LongRead2.class);
                if (niyayTable.get(listItemName)[0].equals("-2")) {
                    final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
                    FastReadActivity.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=");
                } else {
                    FastReadActivity.putExtra("url", niyayTable.get(listItemName)[2]);
                }
                FastReadActivity.putExtra("cp", niyayTable.get(listItemName)[3]);
                FastReadActivity.putExtra("from", "cp");

                startActivity(FastReadActivity);
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
                mGaTracker.sendEvent("ui_action", "button_press", "menu_add", (long) 0);
                addmenu();
                return true;
            case R.id.show:
                mGaTracker.sendEvent("ui_action", "button_press", "menu_show", (long) 0);
                Log.e("doback at", "Options");
                new doback(getApplicationContext()).execute();
                return true;
            case R.id.menu_settings:
                mGaTracker.sendEvent("ui_action", "button_press", "menu_settings", (long) 0);
                startActivityForResult(new Intent(getBaseContext(), Setting.class), 0);
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
        CharSequence[] items = {"ค้นหาจากหน้า Web แบบใหม่", "ค้นหาแบ่งตามหมวด (แบบใหม่)", "ค้นหาจากข้อมูล ", "จาก Favorite Writer", "ค้นหาจากหน้า Web", "ค้นหาแบ่งตามหมวด (ปรับปรุง)", "ค้นหาแบ่งตามหมวด (แบบเก่า)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (id == 4) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_web_add", (long) 0);
                            Intent i = new Intent(getApplicationContext(), add_web.class);
                            startActivity(i);
                        } else if (id == 3) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_Fav_add", (long) 0);
                            //Toast.makeText(getApplicationContext(), "this function not enable in this version"/*items[id]*/, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), Fav_add.class);
                            startActivityForResult(i, 0);
                        } else if (id == 1) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchGroupActivity2", (long) 0);
                            Intent i = new Intent(getApplicationContext(), webfind2.class);
                            startActivityForResult(i, 0);
                        } else if (id == 2) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchNameActivity", (long) 0);
                            Intent i = new Intent(getApplicationContext(), SearchNameActivity.class);
                            startActivityForResult(i, 0);
                        } else if (id == 0) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchNewWeb", (long) 0);
                            Intent i = new Intent(getApplicationContext(), webfind.class);
                            startActivityForResult(i, 0);
                        } else if (id == 5) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchNewWeb2", (long) 0);
                            Intent i = new Intent(getApplicationContext(), SearchGroupActivity.class);
                            startActivityForResult(i, 0);
                        } else if (id == 6) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchGroupActivity", (long) 0);
                            Intent i = new Intent(getApplicationContext(), SearchGroupActivity2.class);
                            startActivityForResult(i, 0);
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
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (id == 0) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "settingmenu_menu", (long) 0);
                            startActivityForResult(new Intent(getBaseContext(), Setting.class), 0);
                        } else if (id == 1) {
                            mGaTracker.sendEvent("ui_action", "dialog_press", "settingmenu_webcheck", (long) 0);
                            //Toast.makeText(getApplicationContext(), "this function not enable in this version"/*items[id]*/, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), WebNotifyActivity.class));
                        } /*else if(id == 2) {
					startActivity(new Intent(getApplicationContext(),MainLayout.class));
				} */
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

	/*	private void reload() {
		Intent intent = getIntent(); //new Intent(getBaseContext(),InsertForm.class);	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish(); 
		showAllBook();
		startActivity(intent); 
	}*/

    public void onStart() {
        super.onStart();
        //Debug.startMethodTracing("calc");
        //mGaTracker.sendView("/MainActivity");
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        //Debug.stopMethodTracing();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

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
					"<br /><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>");
		}
		else if (status == 2 ) {
			ListViewContent.add(
					"<br /><p><font color=#6E6E6E>ถ้าจบตอน กดปุ่มเพิ่มตอนเพื่อเข้าสู่สถานะรอตอนใหม่</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>");
			sessionStatus.put(url+chapter, ListViewContent.get(ListViewContent.size()-1));
		}
		else if (status == 1 || status == -1) {
			//displayNotification(c.getString(0),c.getString(1),chapter,text1,url+chapter);
			ListViewContent.add(
					"<br /><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
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
    //					"<br /><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
    //							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>");
    //		}
    //		else if (status == 2 ) {
    //			ListViewContent.set(index,
    //					"<br /><p><font color=#6E6E6E>อ่านจบแล้วกรุณากดเพิ่มเพื่อรอตอนใหม่ด้วย</font><br />" +
    //							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
    //							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>");
    //		}
    //		else if (status == 1 || status == -1) {
    //			ListViewContent.set(index,
    //					"<br /><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
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
				"<br /><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
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
            //Log.e("zone", "getview");
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
                //holder.arrow.setEnabled(true);
                //holder.arrow.setVisibility(View.VISIBLE);
                //holder.arrow.setFocusable(true);
                //holder.arrow.setClickable(true);
                holder.arrow.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg3) {
                        // TODO Auto-generated method stub
                        if (niyayTable.size() < arg0 - 1 && ListViewContent.size() > arg0) return;
                        if (niyayTable.size() != 0) {
                            String url = niyayTable.get(arg0)[2] + niyayTable.get(arg0)[3];
                            if (!url.startsWith("http://") && !url.startsWith("https://"))
                                url = "http://" + url;

                            if (Setting.getArrowSelectSetting(getApplicationContext()).equals("0")) {
                                if (Setting.getCheckSetting(getApplicationContext()))
                                    Toast.makeText(getBaseContext(), "ถ้าต้องการเปิดโดยใช้ App ไปที่ \nตั่งค่า -> ตั่งค่าการการเลือกรายการ", Toast.LENGTH_LONG).show();
                                mGaTracker.sendEvent("ui_action", "button_press", "arrow_web", (long) 0);
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            } else if (Setting.getArrowSelectSetting(getApplicationContext()).equals("2")) {
                                Intent TextReadActivity = new Intent(getBaseContext(), TextReadActivity.class);
                                TextReadActivity.putExtra("url", url);
                                TextReadActivity.putExtra("from", "main");
                                startActivity(TextReadActivity);
                                mGaTracker.sendEvent("ui_action", "button_press", "arrow_text", (long) 0);
                            } else {
                                if (Setting.getCheckSetting(getApplicationContext()))
                                    Toast.makeText(getBaseContext(), "ถ้าต้องการเปิดโดยใช้ Browser ไปที่ \nตั่งค่า -> ตั่งค่าการการเลือกรายการ", Toast.LENGTH_LONG).show();
                                Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
                                browserIntent.putExtra("id", niyayTable.get(arg0)[0]);
                                browserIntent.putExtra("url", url);
                                browserIntent.putExtra("title", niyayTable.get(arg0)[4]);
                                startActivity(browserIntent);
                                mGaTracker.sendEvent("ui_action", "button_press", "arrow_old", (long) 0);
                            }
                        } else {
                            addmenu();
                        }
                    }

                });
                if (ListViewContent.size() > arg0)
                    holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
            }
            return arg1;
        }

        @SuppressWarnings("unused")
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // empty implementation
        }

        class ViewHolder {
            public TextView text;
            public ImageButton arrow;
        }
    }
}
