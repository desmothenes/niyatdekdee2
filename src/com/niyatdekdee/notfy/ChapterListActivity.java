package com.niyatdekdee.notfy;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ChapterListActivity extends ListActivity {

    private final static ArrayList<String> Linktable = new ArrayList<String>();
    private static ArrayList<String> ListViewContent = new ArrayList<String>();
    private static ProgressDialog dialog;
    private static InteractiveArrayAdapter adapter;
    private static ListView list;
    private static TextView title;
    private static int cp;
    private static ProgressBar spiner;
    //private static Context context;
    private cp_doback dob = null;
    private GoogleAnalytics mGaInstance;
    private Tracker mGaTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_chapter_list);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mGaInstance = GoogleAnalytics.getInstance(this);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker("UA-37746897-1");
        Intent intent = getIntent();
        if (customTitleSupported) {
            //ตั้งค่า custom titlebar จาก custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);

            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.nonbar);
            spiner = new ProgressBar(this);
            RelativeLayout.LayoutParams lspin = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lspin.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lspin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            spiner.setLayoutParams(lspin);
            barLayout.addView(spiner);
            spiner.setVisibility(View.GONE);
            spiner.setBackgroundResource(Color.parseColor("#00000000"));
            switch (Integer.parseInt(Setting.getColorSelectSetting(getApplicationContext()))) {
                case 0:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar);
                    break;
                case 1:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    break;
                case 2:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_green);
                    break;
                case 3:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    break;
                case 4:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    break;
                case 5:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
                    break;
                case 6:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_siver);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_siver);
                    break;
                case 7:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_glay);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_glay);
                    break;
                case 8:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_orange);
                    spiner.setBackgroundResource(R.drawable.bg_titlebar_orange);
                    break;
            }
            //เชื่อม btnSearch btnDirection เข้ากับ View
            title = (TextView) findViewById(R.id.textViewBar);
            title.setTextSize(15);
            title.setText(String.format("%s", intent.getStringExtra("title") == null ? "" : intent.getStringExtra("title").replace("นิยาย ", "")));

            ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        Toast.makeText(getBaseContext(), "สามารถกดค้างเพื่อเลือกวิธีการเปิดได้", Toast.LENGTH_LONG).show();
        //context = getBaseContext();
        dialog = new ProgressDialog(ChapterListActivity.this);
        //dialog.setTitle("Loading");
        dialog.setMessage("Please Wait...\n\nถ้าค้างนานกว่า 40 วินาที ลองกดออกแล้วเพิ่มใหม่");
        //dialog.setCancelable(true);
        dialog.show();
        //dialog = new ProgressDialog(ChapterListActivity.this);
        //dialog.setMessage("Please Wait...\n\nถ้าค้างนานกว่า 40 วินาที ลองกดออกแล้วเพิ่มใหม่");
        final String origin = intent.getStringExtra("url");
        cp = intent.getIntExtra("cp", 1) - 1;
        //from this fomat http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=580483&chapter=
        //to this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
        adapter = new InteractiveArrayAdapter(this, ListViewContent);
        adapter.notifyDataSetChanged();
        dob = (cp_doback) getLastNonConfigurationInstance();
        if (dob == null) {
            dob = new cp_doback(this);
            dob.execute(origin);
        } else {

        }
        list = getListView();
        list.setFastScrollEnabled(true);
        list.setLongClickable(true);
        list.getDrawingCache(false);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final String url = Linktable.get(arg2);
                Log.v("url", url);
                final String title = Jsoup.parse(ListViewContent.get(arg2)).text();
                Log.v("title", title);
                /*				Intent browser = new Intent(Intent.ACTION_VIEW);
                 Uri data = Uri.parse(url);
				browser.setData(data);
				startActivity(browser);		*/
                mGaTracker.sendEvent("ui_action", "button_press", "cp_default_open", Long.parseLong(Setting.getArrowSelectSetting(ChapterListActivity.this)));
                if (Setting.getArrowSelectSetting(ChapterListActivity.this).equals("0")) {
                    if (Setting.getCheckSetting(getApplicationContext())) {
                        Toast.makeText(getBaseContext(), "ถ้าต้องการเปิดโดยใช้ App ไปที่ \nตั่งค่า -> ตั่งค่าการการเลือกรายการ", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "หรือเลือกเองโดยการกดค้าง", Toast.LENGTH_LONG).show();
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else if (Setting.getArrowSelectSetting(getApplicationContext()).equals("2")) {
                    Intent TextReadActivity = new Intent(getBaseContext(), TextReadActivity.class);
                    TextReadActivity.putExtra("url", url);
                    TextReadActivity.putExtra("fromindex", true);
                    TextReadActivity.putExtra("from", "cp");

                    if (Setting.getCheckSetting(getApplicationContext())) {
                        Toast.makeText(getBaseContext(), "ถ้าต้องการเปิดโดยใช้ Browser ไปที่ \nตั่งค่า -> ตั่งค่าการการเลือกรายการ", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "หรือเลือกเองโดยการกดค้าง", Toast.LENGTH_LONG).show();
                    }
                    startActivity(TextReadActivity);
                } else {
                    if (Setting.getCheckSetting(getApplicationContext())) {
                        Toast.makeText(getBaseContext(), "ถ้าต้องการเปิดโดยใช้ Browser ไปที่ \nตั่งค่า -> ตั่งค่าการการเลือกรายการ", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "หรือเลือกเองโดยการกดค้าง", Toast.LENGTH_LONG).show();
                    }
                    Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
                    browserIntent.putExtra("url", url);
                    browserIntent.putExtra("title", title);
                    startActivity(browserIntent);
                }

            }
        });
        /*		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
	        public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	            return super.onItemLongClick(v,pos,id);
	        }
	    });*/
        registerForContextMenu(list);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // menu code here
        getMenuInflater().inflate(R.menu.cp_menu_data, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // menu habdling code here
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listItemName = (int) info.id;
        final String url = Linktable.get(listItemName);
        Log.v("url", url);
        final String title = Jsoup.parse(ListViewContent.get(listItemName)).text();
        Log.v("title", title);
        switch (item.getItemId()) {

            case R.id.open:
                Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
                browserIntent.putExtra("url", url);
                browserIntent.putExtra("title", title);
                if (MainActivity.sessionId != null) {
                    StringBuilder cookieString = new StringBuilder();
                    for (String key : MainActivity.sessionId.keySet()) {
                        Log.v(key, MainActivity.sessionId.get(key));
                        cookieString.append(key).append("=").append(MainActivity.sessionId.get(key)).append(";");
                    }
                    browserIntent.putExtra("cookieString", cookieString.toString());
                }
                startActivity(browserIntent);
                return true;

            case R.id.openweb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;

            case R.id.opentext:
                Intent TextReadActivity = new Intent(getBaseContext(), TextReadActivity.class);
                TextReadActivity.putExtra("url", url);
                TextReadActivity.putExtra("fromindex", true);
                TextReadActivity.putExtra("from", "cp");

                startActivity(TextReadActivity);
                return true;
            case R.id.openfast:
                Intent FastReadActivity = new Intent(getBaseContext(), LongRead2.class);
                //final String unum = MyAppClass.findnum(url, "story_id=", getBaseContext());
                FastReadActivity.putExtra("url", url.substring(0, url.indexOf("chapter=") + 8));
                FastReadActivity.putExtra("cp", url.substring(url.indexOf("chapter=") + 8));
                FastReadActivity.putExtra("from", "cp");

                startActivity(FastReadActivity);
                return true;
            case R.id.cptts:
                if (DekTTSActivity.tts != null && DekTTSActivity.tts.isSpeaking()) DekTTSActivity.tts.stop();
                DekTTSActivity.isSpeak = false;
                Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                DekTTSActivity.type = 1;
                DekTTSActivity.text = url;
                startService(intent);
        }

        mGaTracker.sendEvent("ui_action", "button_press", "cp_select_open", (long) item.getItemId());
        return super.onContextItemSelected(item);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        dob.detach();

        return (dob);
    }
    /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_chapter_list, menu);
		return true;
	}*/

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

    @Override
    public void onBackPressed() {
        if (dialog.isShowing()) {
            dialog.dismiss();
            return;
        }
        if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
            if (DekTTSActivity.tts.isSpeaking()) DekTTSActivity.tts.stop();
            DekTTSActivity.stop = true;
            DekTTSActivity.isSpeak = false;
            Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    static private class cp_doback extends AsyncTask<String, String, Long> {
        String doc_title;
        ChapterListActivity activity = null;

        cp_doback(ChapterListActivity activity) {
            attach(activity);
        }

        void detach() {
            activity = null;
        }

        private void attach(ChapterListActivity act) {
            // TODO Auto-generated method stub
            this.activity = act;
        }

        @Override
        protected void onPreExecute() {        //dialog.show();
            super.onPreExecute();
            ListViewContent.clear();
            Linktable.clear();
            spiner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Long doInBackground(String... origin0) {

            final String origin = origin0[0].contains("=") ? origin0[0].substring(0, origin0[0].lastIndexOf("=") + 1) : origin0[0];
                System.out.println("ori: " + origin0[0]);
                //System.out.println(origin);
            Document doc = null;
            int j = 0;
            while (true) {
                try {
                    final String idnum = Integer.toString(Integer.parseInt(MyAppClass.findnum(origin, "id=", activity)));
                    doc = Jsoup.connect("http://writer.dek-d.com/dek-d/writer/view.php?id=" + idnum).timeout(15000).get();
                } catch (NumberFormatException e) {
                    try {
                        doc = Jsoup.connect("http://writer.dek-d.com/dek-d/writer/view.php?id" + origin.substring(origin.indexOf("="), origin.indexOf("&"))).timeout(15000).get();
                    } catch (IOException e1) {
                        j++;
                        if (j > 3) {
                            publishProgress("-1");
                            return null;
                        }
                    }
                } catch (IOException ex) {
                    j++;
                    if (j > 3) {
                        publishProgress("-1");
                        return null;
                    }
                } finally {
                    if (doc == null) {
                        publishProgress("-1");
                        return null;
                    }
                    break;
                }
            }
            if (!doc.title().isEmpty()) doc_title = doc.title();
                int c = 1;
                Elements link1 = doc.select("table.tableblack[border=1]");//[href~=/dek-d/writer/view.php]");
                for (Element link : link1) {
                    if (++c != 2) continue;
                    Elements link0 = link.select("td");
                    int index = 0;
                    StringBuilder sum = new StringBuilder();
                    for (Element link2 : link0) {
                        if (++c < 8) continue;
                        index++;
                        String stext = link2.text();
                        if (index == 1) {
                            sum.append(String.format(" %s ", stext));
                            Linktable.add(String.format("%s%s", origin, stext));
                        } else if (index == 2) {
                            sum.append(String.format("  %s ", stext));
                        } else {
                            sum.append(String.format("  %s", stext));
                            ListViewContent.add(sum.toString().trim());
                            //print(ListViewContent.get(ListViewContent.size()-1));
                            index = 0;
                            sum = new StringBuilder();
                        }
                    }
                }
                for (int i = 0; i < 4; i++)
                    if (ListViewContent.size() != 0) ListViewContent.remove(ListViewContent.size() - 1);
                    else publishProgress("ไม่พบผลการค้นหา");

            return null;
        }

        protected void onPostExecute(Long result) {
            if (dialog.isShowing()) dialog.dismiss();
            list.setAdapter(adapter);
            list.setSelection(cp);
            title.setText(doc_title);
            spiner.setVisibility(View.GONE);
        }

        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("-1")) {
                dialog.setMessage("การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            } else {
                dialog.setMessage(progress[0]);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class InteractiveArrayAdapter extends ArrayAdapter<String> {
        private final Activity context;
        private ArrayList<String> names = new ArrayList<String>();

        public InteractiveArrayAdapter(Activity context, ArrayList<String> names) {
            super(context, R.layout.list_item2, names);
            this.context = context;
            this.names = names;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_item2, null);
            }
            TextView text = (TextView) rowView.findViewById(R.id.textView1);
            //System.out.println(names.get(position));
            String htmltext = names.get(position);//+"</font>";
            text.setText((names.get(position) != null) ? htmltext.replace("\n", "") : "null");
            return rowView;
        }

    }

}
