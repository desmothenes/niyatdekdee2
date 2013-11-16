package com.niyatdekdee.notfy;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
    //final static Map<String, String> niyayTable = new HashMap<String, String>();
    static Tracker mGaTracker;
    private ImageButton btnDirection;
    //static doback dob;
    //private WakeLock wl;
    private GoogleAnalytics mGaInstance;
    private ContextWrapper cw;
    static ArrayList<String> ListViewStatus = new ArrayList<String>();
    private Time now;
    static List<Cookie> cookies;
    //private boolean isTemp;
    private boolean isErr = false;
    private boolean loginsuscess = false;
    static Map<String, Long> sessionTime = new HashMap<String, Long>();
    static Map<String, String> sessionStatus = new HashMap<String, String>();
    private static boolean tried = false;
    private static int floop;
    static Map<String, String> sessionId = new HashMap<String, String>();
    private ArrayList<String> Listtemp = new ArrayList<String>();
    private boolean falselogin;

    void update() {
        Log.e("doback at", "update static");
        //new doback(context).execute();
        do_back_3();
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

    //static  boolean isOnline() { return true;}
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
    private void draw_head() {

        //ตั้งค่า custom titlebar จาก custom_titlebar.xml
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_main);

        //RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.mainbar);

        //titleColor = Integer.parseInt(Setting.getColorSelectSetting(MainActivity.this));

        final ImageButton btnRefresh = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton btnAdd = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton btnSetting = (ImageButton) findViewById(R.id.btnSetting);


        TextView title = (TextView) findViewById(R.id.textViewTitle);
        title.setText(" รายการนิยาย");

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btnRefresh.setEnabled(false);
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("โปรดรอ...\nถ้ารู้สึกช้า โปรดออกแแล้วเข้าใหม่");
                dialog.setTitle("Loading");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                Log.e("doback at", "btnRefresh");
                //new doback(getApplicationContext()).execute();
                ////new Do_Back2(getApplicationContext()).execute();
                dialog.show();
                do_back_3();
                if (dialog.isShowing()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
                mGaTracker.sendEvent("ui_action", "button_press", "refresh", (long) 0);
                btnRefresh.setEnabled(true);
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
            draw_head();
        }
        context = getBaseContext();
        //db = new DatabaseAdapter(this);
        listAdap = new ListViewAdapter(this);


        myList = getListView();

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        //dob=;

        Log.e("doback at", "main");
        do_back_3();

        myList.setFastScrollEnabled(true);
        //myList.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myList.smoothScrollToPosition(0);
        myList.setItemsCanFocus(true);
        registerForContextMenu(myList);
        WebView obj = new WebView(this);
        obj.clearCache(true);
    }

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
                        //if (DekTTSActivity.tts != null) DekTTSActivity.tts.shutdown();
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
        if (ListViewContent.get(0).equals(getString(R.string.first_add_main))) {
            addmenu();
            return;
        }
        if (ListViewContent.get(0).equals(getString(R.string.no_fav_update))) {
            //addmenu();
            return;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        if (position < ListViewStatus.size() && ListViewStatus.get(position).equals("<font color=#cc0029>มีปัญหา โปรดลองใหม่</font>")) {
            reconnect(position);
            return;
        }

        getMenuInflater().inflate(R.menu.menu_data, menu);
    }

    private void reconnect(final int index) {
        context = MainActivity.this;
        CharSequence[] items = {"ลองเชื่อมต่อใหม่"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (index < niyayTable.size()) {
                            check_new_cp(index, niyayTable.get(index));
                        }
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //setContentView(R.layout.activity_main);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info != null) {
            final int listItemName = (int) info.id;
        /*		int i_index = -1;
        for(String[] i:niyayTable) {
			i_index++;
			Log.e("table "+Integer.toString(i_index), i[1]);
		}
		Log.e("get with", Integer.toString(listItemName));
		Log.e("niyayTable 0",niyayTable.get(listItemName)[0]);
		Log.e("niyayTable 1",niyayTable.get(listItemName)[1]);
		Log.e("niyayTable 2",niyayTable.get(listItemName)[2]);
		Log.e("niyayTable 3",niyayTable.get(listItemName)[3]);
		Log.e("niyayTable 4",niyayTable.get(listItemName)[4]);
		Log.e("get item", Integer.toString(item.getItemId()));*/

            cw = new ContextWrapper(this);

            switch (item.getItemId()) {
                case R.id.open:
                    return item_open(listItemName);
                case R.id.addcp:
                    return item_addcp(listItemName);
                case R.id.openweb:
                    return item_openweb(listItemName);
                case R.id.opentext:
                    return item_opentext(listItemName);
                case R.id.red:
                    return item_red(listItemName);
                case R.id.tts:
                    return item_tts(listItemName);
                case R.id.edit:
                    return item_edit(listItemName);
                case R.id.chapterlist:
                    return item_chapterlist(listItemName);
                case R.id.dec:
                    return item_dec(listItemName);
                case R.id.delete:
                    return item_delete(listItemName);
                case R.id.longread:
                    return item_longread(listItemName);
                case R.id.newlongread:
                    return item_newlongread(listItemName);
            /*case R.id.openfast:
                return item_openfast(listItemName);*/
                default:
                    return super.onContextItemSelected(item);
            }
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private boolean item_open(final int listItemName) {
        String url;
        if (niyayTable != null && niyayTable.size() < listItemName + 1) return true;
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
        if (sessionId != null) {
            StringBuilder cookieString = new StringBuilder();
            for (String key : sessionId.keySet()) {
                Log.e(key, sessionId.get(key));
                cookieString.append(key).append("=").append(sessionId.get(key)).append(";");
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
                        Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                    } catch (IOException e) {
                        //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                            Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                        } catch (IOException e) {
                            //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                            Log.e("onProgressUpdate", getString(R.string.connection_error));
                        } else {
                            showToast(progress[0]);
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
                            String line;
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                        doc = getString(R.string.wait_for_new);
                    }
                    db.open();

                    boolean flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])),
                            Integer.parseInt(niyayTable.get(listItemName)[3]),
                            "");
                    if (flag) {
                        Toast.makeText(context, "เพิ่มเรียบร้อย", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "เพิ่มไม่สำเร็จ", Toast.LENGTH_SHORT).show();
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
                    sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                    listAdap.notifyDataSetChanged();
                    if (dialog.isShowing()) dialog.dismiss();
                    mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                }


            }.execute();
            return true;
        }
    }

    private boolean item_addcp(final int listItemName) {
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
                        Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                    } catch (IOException e) {
                        //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
                    } /*else {
                        Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                    }*/
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
                        String line;
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
                    Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                    doc = getString(R.string.wait_for_new);
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
                /*if (flag) {
                    //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                }*/
                //Intent i = new Intent(context,MainActivity.class);
                db.close();
                ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                        "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                );
                //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                listAdap.notifyDataSetChanged();
                if (dialog.isShowing()) dialog.dismiss();
                mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
            }


        }.execute();
        return true;
    }

    private boolean item_opentext(final int listItemName) {
        String url;
        if (niyayTable.size() < listItemName + 1 || niyayTable.get(listItemName) == null || niyayTable.get(listItemName)[0] == null)
            return true;
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
					Log.e(key, doback.sessionId.get(key));
					cookieString.append(key + "=" +doback.sessionId.get(key)+ ";");
				}
				TextReadActivity.putExtra("cookieString",cookieString.toString());
			}*/
        //TextReadActivity.putExtra("title",niyayTable.get(listItemName)[4]);
            /*			if (doback.sessionId != null) {
                StringBuilder cookieString = new StringBuilder();
				for(String key: doback.sessionId.keySet()){
					Log.e(key, doback.sessionId.get(key));
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
                        Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                    } catch (IOException e) {
                        //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                            Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                        } catch (IOException e) {
                            //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                            Log.e("onProgressUpdate", getString(R.string.connection_error));
                        } /*else {
                            //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                        }*/
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
                            String line;
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                        doc = getString(R.string.wait_for_new);
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
                    /*if (flag) {
                        //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                    }*/
                    //Intent i = new Intent(context,MainActivity.class);
                    db.close();
                    ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                    );
                    //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                    sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                    listAdap.notifyDataSetChanged();
                    if (dialog.isShowing()) dialog.dismiss();
                    mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                }


            }.execute();
        }
        return true;
    }

    private boolean item_red(final int listItemName) {
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
                        Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
        sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
        listAdap.notifyDataSetChanged();
        return true;
    }

    private boolean item_tts(final int listItemName) {
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
    }

    private boolean item_edit(final int listItemName) {
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
    }

    private boolean item_chapterlist(final int listItemName) {
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
    }

    private boolean item_dec(final int listItemName) {
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
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
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
                        String line;
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
                    //Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                        doc = getString(R.string.wait_for_new);
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
                    /*if (flag) {
                        //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                    }*/
                    //Intent i = new Intent(context,MainActivity.class);
                    db.close();
                    ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                    );
                    listAdap.notifyDataSetChanged();
                }
                if (dialog.isShowing()) dialog.dismiss();
            }
        }.execute();
        //Toast.makeText(context, "dec", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean item_delete(final int listItemName) {
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
                        //new doback(getApplicationContext()).execute();
                        do_back_3();
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
    }

    private boolean item_longread(final int listItemName) {
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
    }

    private boolean item_newlongread(final int listItemName) {
        Intent longread1 = new Intent(context, LongRead2.class);
        if (niyayTable.size() < listItemName + 1 || niyayTable.get(listItemName) == null || niyayTable.get(listItemName)[0] == null)
            return true;
        if (niyayTable.get(listItemName)[0].equals("-2")) {
            final String unum = MyAppClass.findnum(niyayTable.get(listItemName)[2], "story_id=", getBaseContext());
            //final String chapter = MyAppClass.findnum(niyayTable.get(listItemName)[4], "ตอนที่ ", getBaseContext());
            longread1.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=");
            mGaTracker.sendEvent("ui_action", "button_press", "longread_fav", (long) 0);
        } else {
            longread1.putExtra("url", niyayTable.get(listItemName)[2]);
            mGaTracker.sendEvent("ui_action", "button_press", "longread", (long) 0);
        }
        longread1.putExtra("cp", niyayTable.get(listItemName)[3]);
        longread1.putExtra("from", "ncp");
        startActivity(longread1);
        return true;
    }
/*

    private boolean item_openfast(final int listItemName) {
        Intent FastReadActivity = new Intent(getBaseContext(), LongRead2.class);
        if (niyayTable.size() < listItemName + 1 || niyayTable.get(listItemName) == null || niyayTable.get(listItemName)[0] == null)  return true;
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
    }
*/

    private boolean item_openweb(final int listItemName) {
        String url;
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
                        Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                    } catch (IOException e) {
                        //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                            Jsoup.connect("http://www.dek-d.com/" + niyayTable.get(listItemName)[2]).cookies(sessionId).timeout(3000).get();
                        } catch (IOException e) {
                            //Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                            Log.e("onProgressUpdate", getString(R.string.connection_error));
                        } /*else {
                            //Toast.makeText(context, progress[0] , Toast.LENGTH_SHORT).show();
                        }*/
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
                            String line;
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
                    if (progress == null || progress[0] == null || progress[0].isEmpty()) return;
                    else if (progress[0].equals("-1")) {
                        Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e("onProgressUpdate", getString(R.string.connection_error));
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
                        doc = getString(R.string.wait_for_new);
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
                    /*if (flag) {
                        //Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
                    }*/
                    //Intent i = new Intent(context,MainActivity.class);
                    db.close();
                    ListViewContent.set(listItemName, "<br /><p><font color=#33B6EA>เรื่อง :" + niyayTable.get(listItemName)[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + doc + " (" + niyayTable.get(listItemName)[3] + ")</font></p>"
                    );
                    //doback.sessionStatus.put(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3], ListViewContent.get(listItemName));
                    sessionStatus.remove(niyayTable.get(listItemName)[2] + niyayTable.get(listItemName)[3]);
                    listAdap.notifyDataSetChanged();
                    if (dialog.isShowing()) dialog.dismiss();
                    mGaTracker.sendEvent("ui_action", "button_press", "add_cp", (long) 0);
                }


            }.execute();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        settingmenu();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("doback at", "onActivityResult");
        //new doback(getApplicationContext()).execute();
        if (resultCode == RESULT_OK) do_back_3();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
        /*switch (item.getItemId()) {
            case R.id.add:
                mGaTracker.sendEvent("ui_action", "button_press", "menu_add", (long) 0);
                addmenu();
                return true;
            case R.id.show:
                mGaTracker.sendEvent("ui_action", "button_press", "menu_show", (long) 0);
                Log.e("doback at", "Options");
                //new doback(getApplicationContext()).execute();
                do_back_3();
                return true;
            case R.id.sort:
*//*                Collections.sort(ListViewContent,new Comparator<String>()
                {
                    public int compare(String s1,String s2)
                    {
                        if (s1.contains("มีการอัพเดตตอนปัจจุบัน"))
                            return -1;
                        else if (s2.contains("มีการอัพเดตตอนปัจจุบัน"))
                            return 1;
                        else if (s1.contains("ถ้าจบตอน"))
                            return -1;
                        else if (s2.contains("ถ้าจบตอน"))
                            return 1;
                        return s1.length() - s2.length();
                    }
                });*//*
                sortby1st(ListViewContent, ListViewStatus, niyayTable);
                listAdap.notifyDataSetChanged();
                return true;
            case R.id.menu_settings:
                mGaTracker.sendEvent("ui_action", "button_press", "menu_settings", (long) 0);
                startActivityForResult(new Intent(getBaseContext(), Setting.class), 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
    }

    private void sortby1st(List... lists) {
        assert lists.length > 0;

        Object[][] objects = new Object[lists[0].size()][lists.length];

        for (int i = 0; i < lists.length; i++) {
            int j = 0;
            for (Object object : lists[i]) {
                objects[j++][i] = object;
            }
        }
        try {
            Arrays.sort(objects, new Comparator<Object[]>() {
                public int compare(Object[] o1, Object[] o2) {
                    if (((String) o1[0]).contains("มีการอัพเดตตอนปัจจุบัน")) {
                        if (((String) o2[0]).contains("มีการอัพเดตตอนปัจจุบัน")) {
                            return ((String) o2[0]).compareTo((String) o1[0]);
                        }
                        return -1;
                    } else if (((String) o2[0]).contains("มีการอัพเดตตอนปัจจุบัน"))
                        return 1;
                    else if (((String) o1[0]).contains("[fav]")) {
                        if (((String) o2[0]).contains("[fav]")) {
                            return ((String) o2[0]).compareTo((String) o1[0]);
                        }
                        return -1;
                    } else if (((String) o1[0]).contains("[fav]"))
                        return 1;
                    else if (((String) o1[0]).contains("ถ้าจบตอน")) {
                        if (((String) o2[0]).contains("ถ้าจบตอน")) {
                            return ((String) o2[0]).compareTo((String) o1[0]);
                        }
                        return -1;
                    } else if (((String) o2[0]).contains("ถ้าจบตอน"))
                        return 1;
                    return ((String) ((String[]) o2[2])[0]).compareTo((String) ((String[]) o1[2])[0]);
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < lists.length; i++) {
            lists[i].clear();
            for (Object[] tuple : objects) {
                lists[i].add(tuple[i]);
            }
        }
    }

    @Override
    public void onDestroy() {
		/*		if (TTS.tts != null) {
			TTS.tts.stop();
			TTS.tts.shutdown();
		}*/
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), DekTTSActivity.class));
        if (DekTTSActivity.tts != null) DekTTSActivity.tts.shutdown();
        //BugSenseHandler.closeSession(MainActivity.this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void addmenu() {
        context = MainActivity.this;

        if (Setting.getAdvance(getApplicationContext())) {
            CharSequence[] items = {"ค้นหาจากหน้า Web แบบใหม่", "ค้นหาแบ่งตามหมวด (แบบใหม่)", "ค้นหาจากข้อมูล ", "จาก Favorite Writer", "ค้นหาจากหน้า Web", "ค้นหาแบ่งตามหมวด (ปรับปรุง)", "ค้นหาแบ่งตามหมวด (แบบเก่า)", "FaceBook"};

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true)
                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (id == 5) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_web_add", (long) 0);
                                Intent i = new Intent(getApplicationContext(), add_web.class);
                                startActivityForResult(i, 0);
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
                            } else if (id == 6) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchNewWeb2", (long) 0);
                                Intent i = new Intent(getApplicationContext(), SearchGroupActivity.class);
                                startActivityForResult(i, 0);
                            } else if (id == 7) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_SearchGroupActivity", (long) 0);
                                Intent i = new Intent(getApplicationContext(), SearchGroupActivity2.class);
                                startActivityForResult(i, 0);
                            } else if (id == 4) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_FBGroupActivity", (long) 0);
                                Intent i = new Intent(getApplication(), FBlogin.class);
                                startActivityForResult(i, 0);
                            }
                        }

                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            CharSequence[] items = {"ค้นหาจากหน้า Web แบบใหม่", "ค้นหาแบ่งตามหมวด (แบบใหม่)", "ค้นหาจากข้อมูล ", "จาก Favorite Writer", "FaceBook"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true)
                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (id == 3) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_Fav_add", (long) 0);
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
                            } else if (id == 4) {
                                mGaTracker.sendEvent("ui_action", "dialog_press", "add_FBGroupActivity", (long) 0);
                                Intent i = new Intent(getApplication(), FBlogin.class);
                                startActivityForResult(i, 0);
                            }
                        }

                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    private void settingmenu() {
        context = MainActivity.this;
        CharSequence[] items = {"ตั่งค่า", "อัพเดตจาก Favorite Writer แบบประหยัด", "เรียง"};
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
                        } else if (id == 2) {
                            sortby1st(ListViewContent, ListViewStatus, niyayTable);
                            listAdap.notifyDataSetChanged();
                        }
                    }

                });
        AlertDialog alert = builder.create();
        alert.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
        alert.getWindow().getAttributes().y = 30
        ;
        alert.getWindow().setLayout(100, 200);
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
			text1 =getString(R.string.wait_for_new);
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
    //			text1 =getString(R.string.wait_for_new);
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
                viewHolder.status = (TextView) arg1.findViewById(R.id.textView2);
                viewHolder.arrow = (Button) arg1.findViewById(R.id.arrow1);
                arg1.setTag(viewHolder);
            }
            arg1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v != null) v.showContextMenu();
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
                        if (niyayTable.size() - 1 < arg0 && ListViewContent.size() > arg0) return;
                        if (arg0 < ListViewStatus.size() && ListViewStatus.get(arg0).equals("<font color=#cc0029>มีปัญหา โปรดลองใหม่</font>")) {
                            reconnect(arg0);
                            return;
                        }
                        if (niyayTable.size() != 0) {
                            String url;

                            if (niyayTable.get(arg0)[0].equals("-2")) {
                                final String unum = MyAppClass.findnum(niyayTable.get(arg0)[2], "story_id=", getBaseContext());
                                final String chapter = MyAppClass.findnum(niyayTable.get(arg0)[4], "ตอนที่ ", getBaseContext());
                                url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=" + chapter;
                            } else {
                                url = niyayTable.get(arg0)[2] + niyayTable.get(arg0)[3];
                            }

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
                if (ListViewStatus.size() > arg0)
                    holder.status.setText(Html.fromHtml(ListViewStatus.get(arg0)));
            }
            return arg1;
        }

        @SuppressWarnings("unused")
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // empty implementation
        }

        private class ViewHolder {
            TextView text;
            TextView status;
            Button arrow;
        }
    }

    void do_back_3() {
        onPre();
        onBack();
        //onPost();
    }

    private void onPost() {
        if (MainActivity.ListViewContent.size() == 0) {/*
            if (loginsuscess && Setting.getisLogin(context) && Setting.getdisplayResult(context))
                Toast.makeText(context, "ไม่พบตอนใหม่ใน Favorite Writer", Toast.LENGTH_LONG).show();
            else if (Setting.getisLogin(context) && Setting.getdisplayResult(context))
                Toast.makeText(context, "ไม่มีตอนใหม่ หรือ เข้าสู่ระบบไม่ได้", Toast.LENGTH_LONG).show();
*/
            if (loginsuscess || falselogin) {
                ListViewContent.add(getString(R.string.no_fav_update));
            } else if ((floop == 0 && ListViewContent.size() == 0)) {
                MainActivity.ListViewContent.add(getString(R.string.first_add_main));
                addmenu();
            } else {
                if (Setting.getisLogin(context) && Setting.getdisplayResult(context)) return;
                Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                Log.e("onPostExecute", getString(R.string.connection_error));
            }

            //if (MainActivity.niyayTable.size() == 0) MainActivity.niyayTable.add(new String[4]);
        }

        if (isErr) {
            if (Setting.getisLogin(context) && Setting.getdisplayResult(context)) return;
            Log.e("onPostExecute", "isErr");
            Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        }/* else {

        }*/
    }

    private void onBack() {
        int temp = 0;
        if (MainActivity.isOnline()) {

            if (!Setting.getisLogin(context) || !Setting.getdisplayResult(context) || !Setting.getonlyFavorite(context)) {
                Thread[] threads = new Thread[niyayTable.size()];
                cThreadsize = niyayTable.size();
                cThreadFin = 0;
                for (final String data[] : MainActivity.niyayTable) {
                    final int index = temp;
                    threads[index] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = check_new_cp(index, data);
                            if (!result.equals("err")) {
                                if (index < niyayTable.size()) MainActivity.niyayTable.get(index)[4] = result;
                            }
                            mHandler.postDelayed(threadCount, 1);
                        }
                    });
                    threads[index].start();
                    temp++;
                }

               /* for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
            }
            if (Setting.getisLogin(context)) {
                Log.e("login", "login");


                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                //now.set(pref.getLong("timecookies", 0));
                //System.out.println(now.format3339(false));
                //now.setToNow();
                final long time;
                now = new Time();
                now.setToNow();
                if (sessionTime.get("fav") != null) time = sessionTime.get("fav");
                else time = 0;
                //new Thread(new Runnable() {
                //    @Override
                //    public void run() {
                if ((falselogin) || (now.toMillis(true) - time > 330000) || (!pref.getString("usercookie", " ").equals(Setting.getUserName(context)))) {
                    if (!pref.getString("usercookie", " ").equals(Setting.getUserName(context)))
                        sessionId.clear();
                    login();
                    sessionTime.put("fav", now.toMillis(true));
                    //Log.e("login","time login");
                }
                Log.e("login", "end login");

                if (Setting.getdisplayResult(context)) {
                    if (sessionId.size() != 0) {
                        loadUpdate();
                    }
                }
                onPost();
                //   }
                //}).start();
            } else {
                onPost();
            }
        } else {
            Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG);
            for (int i = 0; i < ListViewStatus.size(); i++) {
                ListViewStatus.set(i, "<font color=#cc0029>การเชื่อมต่อมีปัญหา</font>");
            }
        }
    }

    private void publishProgress(String s) {
        if (s.equals("-1")) {
            showToast(getString(R.string.connection_error));
            Log.e("onProgressUpdate", getString(R.string.connection_error));
            if (!tried) {
                tried = true;
                //this.cancel(true);
                //this.execute(context);
            }
        } else {
            showToast(s);
        }
    }

    private Handler mHandler = new Handler();

    private void publishProgress(String s, String s1, String temp) {
        int index = Integer.parseInt(s1);
        if (!(index < ListViewStatus.size()) || !(index < ListViewContent.size()))
            return;
        else if (s.equals("-1")) {
            //Toast.makeText(context, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            Log.e("onProgressUpdate", getString(R.string.connection_error));
            if (!tried) {
                tried = true;
                //this.cancel(true);
                //this.execute(context);
            }
        } else if (s.equals("-97")) {
            if (index < ListViewStatus.size())
                ListViewStatus.set(index, "<font color=#cc0029>มีปัญหา โปรดลองใหม่</font>");
            mHandler.postDelayed(runnable, 1);
        } else if (s.equals("-99")) {
            ListViewContent.set(index, temp);
            ListViewStatus.set(index, "ตรวจสอบเสร็จสิ้น");
            mHandler.postDelayed(runnable, 1);
            //listAdap.notifyDataSetChanged();
        } else if (s.equals("-98")) {
            ListViewContent.set(index, temp);
            ListViewStatus.set(index, "แฟนพันธ์แท้");
            mHandler.postDelayed(runnable, 1);
            //listAdap.notifyDataSetChanged();
        } else {
            MainActivity.dialog.setMessage(s);
        }
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            listAdap.notifyDataSetChanged();
        }
    };
    int cThreadFin;
    int cThreadsize;
    private Runnable threadCount = new Runnable() {
        public void run() {
            cThreadFin++;
            Log.e("cfin", Integer.toString(cThreadFin));
            if (cThreadFin == niyayTable.size()) {
                sortby1st(ListViewContent, ListViewStatus, niyayTable);
                listAdap.notifyDataSetChanged();
            }
        }
    };

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onPre() {
        MainActivity.ListViewStatus.clear();
        MainActivity.ListViewContent.clear();
        MainActivity.niyayTable.clear();
        if (!Setting.getisLogin(context) || !Setting.getdisplayResult(context) || !Setting.getonlyFavorite(context))
            showAllBookOffline();

        if (MainActivity.db != null)
            MainActivity.db.close();
/*        if (MainActivity.dialog.isShowing())
            MainActivity.dialog.dismiss();*/

        MainActivity.myList.setAdapter(MainActivity.listAdap);
        //MainActivity.dialog.dismiss();
        tried = false;
    }

    private String check_new_cp(final int index, final String[] data) {

        int status = 0;
        //Log.e("data[4]",data[4]);
        String title = data[4].equals("non") ? getString(R.string.wait_for_new) : data[4];
        //Log.e("data[4]",data[4]);
        final String id = data[0];
        final String url = data[2];
        final String chapter = data[3];
        String text1 = "";

        if (url.contains("inlove-book.com")) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements allCP = doc.select(".style1[target=_self]");
                text1 = allCP.last().text().trim();
                if (allCP.size() > Integer.parseInt(chapter)) {
                    String temp =
                            "<br /><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
                                    "<font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                                    "<font color=#cc0029> ตอน : " + text1 + " (" + chapter + ")</font></p>";
                    publishProgress("-99", Integer.toString(index), temp);
                    sessionStatus.put(url + chapter, temp.replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน\nถ้าจบตอน กดปุ่มเพิ่มตอน\nเพื่อเข้าสู่สถานะรอตอนใหม่"));
                } else {
                    String temp =
                            "<br /><p><font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                                    "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + chapter + ")</font></p>";
                    publishProgress("-99", Integer.toString(index), temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");
            }
            return "";
        }
        //if (title.contains(">")) title = title.substring(title.indexOf(">"));

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(new URI(url + chapter));
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 8000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            if (cookies != null && !cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    httpclient.getCookieStore().addCookie(cookie);
                }
            }
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            text1 = httpclient.execute(httpget, responseHandler);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.getMessage());
            HttpGet method = new HttpGet(url + chapter);
            BufferedReader in = null;
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url + chapter));
                //request.setHeader("Range", "bytes=0-1023");
                HttpResponse response = client.execute(method);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                StringBuffer sb = new StringBuffer("");
                String line;
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line).append(NL);
                }
                in.close();
                text1 = sb.toString();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");//	Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return "err";
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");//	Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return "err";
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();

                    }
                }
            }

        } catch (IOException e) {
            publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");
            e.printStackTrace();
            return "err";

        } catch (URISyntaxException e) {
            publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");
            e.printStackTrace();
            return "err";

        } catch (IllegalStateException e) {
            publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");
            e.printStackTrace();
            return "err";

        } catch (Exception e) {
            publishProgress("-97", Integer.toString(index), "ผิดพลาด โปรดลองใหม่");
            e.printStackTrace();
            return "err";

        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        if (text1.contains("<title>")) {
            final String text2 = text1;
            new Thread() {
                public void run() {
                    try {
                        ContextWrapper cw = new ContextWrapper(context);
                        File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), id + ".html");
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                        bw.write(text2);
                        bw.flush();
                        bw.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
            final String start = text1.substring(text1.indexOf("<title>") + 7);
            try {
                text1 = Jsoup.parse((start.substring(start.indexOf(">") + 2, start.indexOf("</title>")))).text();
            } catch (IndexOutOfBoundsException ex) {
                text1 = "error";
            }
        } else {
            text1 = getString(R.string.wait_for_new);
        }

		/*		Log.e("title",(title == null) ? "null" : title);
        Log.e("text1",text1);
		Log.e("compare",Integer.toString(text1.compareTo(title)));		*/

        if (title == null) title = "";
        else if (title.contains(">") && (title.indexOf(">") + 2 < title.length()))
            title = title.substring(title.indexOf(">") + 2);
        if (text1.contains(">") && (text1.indexOf(">") + 2 < text1.length()))
            text1 = text1.substring(text1.indexOf(">") + 2);
        if (title.isEmpty()) {
            Log.e(id, "title isEmpty()");
            title = text1;
            status = -1;
        } else if (!text1.trim().contains(title.trim())) {
            status = 1; //current chapter update
        } else if (!text1.contains("ยังไม่มีตอนปัจจุบัน รอตอนใหม่") && !text1.contains("non")) {
            status = 2;
        }
        //Log.e("status", Integer.toString(status));


        if (status == 0) {
            String temp =
                    "<br /><p><font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
        } else if (status == 2) {
            String temp =
                    "<br /><p><font color=#6E6E6E>ถ้าจบตอน กดปุ่มเพิ่มตอน เพื่อเข้าสู่สถานะรอตอนใหม่</font><br />" +
                            "<font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
            sessionStatus.put(url + chapter, temp);
        } else if (status == 1 || status == -1) {
            String temp =
                    "<br /><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
                            "<font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ตอน : " + text1 + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
            sessionStatus.put(url + chapter, temp.replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน\nถ้าจบตอน กดปุ่มเพิ่มตอน\nเพื่อเข้าสู่สถานะรอตอนใหม่"));
        }

/*				Log.e("content",
				"id: " +data[0]+"\n"+
						"name: " +data[1]+"\n" +
						"url: " +data[2]+"\n"+
						"chapter: " +data[3]+"\n"+
						"status: " +data[4]+"\n"+
						"title: " +Integer.toString(status)+"\n"+
						"text1: " +text1);*/
        Time now = new Time();
        now.setToNow();
        sessionTime.put(url, now.toMillis(true));
        //Log.e("set "+url,Long.toString(now.toMillis(true)));

        if (status == 0 || status == 2) return title;
        else if (status == 1 || status == -1) return text1;
        return "";
    }


    private void showAllBookOffline() {
        // TODO Auto-generated method stub
        Log.e("in", "showAllBookOffline");

		/*		ListViewContent.clear();
		MainActivity.niyayTable.clear();*/
        MainActivity.db = new DatabaseAdapter(context);
        MainActivity.db.close();
        MainActivity.db.open();
        Listtemp.clear();

        Cursor c = MainActivity.db.getAllNiyay();
        floop = c.getCount();
        Log.e("floop", Integer.toString(floop));
        if (c.isClosed() && floop == 0) {
            //Log.e("ck db", "not ok");
            //MainActivity.db.close();
            return;
        } /*else {
            //Log.e("ok ?", "ok");
        }*/

        if (!c.moveToFirst()) return;
        int i = 0;
        do {
            i++;
            String[] temp = new String[5];
            temp[0] = c.getString(0);
            temp[1] = c.getString(1);
            temp[2] = c.getString(2);
            temp[3] = c.getString(3);
            temp[4] = c.getString(4);
            /*Log.e("content",
					"id: " +c.getString(0)+"\n"+
							"name:" +c.getString(1)+"\n" +
							"url: " +c.getString(2)+"\n"+
							"chapter: " +c.getString(3)+"\n"+
							"title: " +c.getString(4));*/
            MainActivity.niyayTable.add(temp);
        } while (c.moveToNext());
        Log.e("loop end", Integer.toString(i));
        for (String[] stemp : MainActivity.niyayTable) {
            displayBookOffline(stemp);
        }
        for (String stemp : Listtemp) {
            //System.out.println("Listtemp: "+ stemp);
            ListViewContent.add(stemp);
            ListViewStatus.add("กำลังตรวจสอบตอนใหม่");
        }
        MainActivity.db.close();
    }


    private void displayBookOffline(String c[]) {
        Log.e("in", "displayBookOffline");

        //Log.e("title0",(data[4] != null) ? data[4]:"");
        String title = c[4];

        //if (title.contains(">")) title = title.substring(title.indexOf(">"));
        //Log.e("title",(title != null) ? title:" ");

        if (title != null) {
            if (title.contains(">") && (title.length() - 1 > title.indexOf(">") + 2))
                title = title.substring(title.indexOf(">") + 2);
            //dialog.setTitle(title);
            Listtemp.add(
                    "<br /><br /><br /><br /><p><font color=#33B6EA>เรื่อง :" + c[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + c[3] + ")</font></p><br /><br />");
        }
        /*Log.e("content",
				"id: " +data[0]+"\n"+
						"name:" +data[1]+"\n" +
						"url: " +data[2]+"\n"+
						"chapter: " +data[3]+"\n"+
						"title: " +data[4]);*/
    }

    private void loadUpdate() {
        Log.e("favfin", "favfin");
        dialog.setMessage("favorite writer");

        Document doc = null;
        if (sessionId == null || !loginsuscess) return;
        try {
            doc = Jsoup.connect("http://www.dek-d.com/story_message2012.php")
                    .cookies(sessionId).timeout(3000)
                    .get();
        } catch (IOException e) {
            publishProgress("-1");
            //mHandler.postDelayed(runnable2, 1);
            e.printStackTrace();
        }
        if (doc == null) return;//System.out.println(doc.html());
        Elements link1 = doc.select(".novel");
        if (link1 == null) return;
        for (Element link : link1) {
            final String stext = link.text();
            //Log.e("stext", stext);
            String[] temp = new String[5];
            temp[0] = "-2";
            temp[1] = stext.substring(0, stext.indexOf("ตอนที่"));
            temp[2] = link.select("a").attr("href");
            temp[3] = "-2";
            temp[4] = stext.substring(stext.indexOf("ตอนที่"));

            dialog.setMessage(temp[1]);
            MainActivity.niyayTable.add(0,temp);

            if (sessionStatus.get(temp[2]) != null) {
                ListViewContent.add(0,sessionStatus.get(temp[2]));
                ListViewStatus.add(0, "แฟนพันธ์แท้");
            } else {
                //MainActivity.ListViewContent.add(stext.replace("ตอนที่", "\nตอนที่"));
                ListViewContent.add(0,
                        "<br /><p><font color=#339900>[fav]มีการอัพเดตตอนปัจจุบัน</font><br />" +
                                "<font color=#33B6EA>เรื่อง :" + temp[1] + "</font><br />" +
                                "<font color=#cc0029>" + temp[4] + "</font></p>");
                ListViewStatus.add(0, "แฟนพันธ์แท้");
                sessionStatus.put(temp[2], ListViewContent.get(ListViewContent.size() - 1).replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน\nถ้าจบตอน กดปุ่มเพิ่มตอน\nเพื่อเข้าสู่สถานะรอตอนใหม่"));
            }
        }

        Log.e("listView", "listView");
        mHandler.postDelayed(runnable, 1);
        /*		for (String i : ListViewContent)
			Log.e("ListViewContent", i);*/
    }

    private void login() {
        Log.e("zone", "login");
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
			//Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}  */
        dialog.setMessage("log in");
        falselogin = true;
        HttpParams httpParameters = new BasicHttpParams();
        final int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        final int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httpost = new HttpPost("http://my.dek-d.com/dekdee/my.id_station/login.php");
        HttpResponse response;
        HttpEntity entity;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
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
            e.printStackTrace();
            showToast("ไม่สามารถเชื่อมต่อเพื่อเข้าสู่ระบบได้");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            showToast("ไม่สามารถเชื่อมต่อเพื่อเข้าสู่ระบบได้");
        } catch (IOException e) {
            e.printStackTrace();
            //mHandler.postDelayed(runnable3, 1);
            showToast("ไม่สามารถเชื่อมต่อเพื่อเข้าสู่ระบบได้");
        }

        //System.out.println("Post logon cookies:");
        cookies = httpclient.getCookieStore().getCookies();
        httpclient.getConnectionManager().shutdown();

        if (cookies.isEmpty()) {
            System.out.println("None");
            return;
        } else {
            System.out.print("size");
            System.out.println(cookies.size());
            falselogin = false;
            for (Cookie cooky : cookies) {
                System.out.println("- " + cooky.toString());
                final String temp = cooky.toString().substring(cooky.toString().indexOf("name: ") + 6);
                //System.out.println(temp.substring(0, temp.indexOf("]")));
                final String temp2 = temp.substring(temp.indexOf("value: ") + 7);
                //System.out.println(temp2.substring(0, temp2.indexOf("]")));
                if (temp.contains("][") && temp2.contains("]"))
                    //		System.out.println(temp.substring(0, temp.indexOf("][")));
                    sessionId.put(temp.substring(0, temp.indexOf("][")), temp2.substring(0, temp2.indexOf("]")));
            }
        }
        //Date fav = new Date();
        //System.out.println("aff date");
        if ((cookies.size() > 1) && ((cookies.get(2).getExpiryDate()) != null) && (sessionId.size() != 0)) {
            //System.out.println("putString");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            int i = 1;
            editor.putString("usercookie", Setting.getUserName(context));
            //System.out.println("putString for ");

            for (String key : sessionId.keySet()) {
                //System.out.println("key");
                editor.putString("keycookies" + Integer.toString(i), key);
                editor.putString("valuecookies" + Integer.toString(i), sessionId.get(key));
                //	System.out.println(i);
                i++;
            }
            editor.putLong("timecookies", cookies.get(2).getExpiryDate().getTime());
            editor.commit();
            loginsuscess = true;
        } else {
            System.out.println("Username หรือ Password ไม่ถูกต้อง");
            showToast("Username หรือ Password ไม่ถูกต้อง");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            loginsuscess = false;
        }
    }


}
