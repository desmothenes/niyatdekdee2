package com.niyatdekdee.notfy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.google.analytics.tracking.android.EasyTracker;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Find extends ListActivity {
    private ProgressDialog dialog;
    private InteractiveArrayAdapter adapter;
    private final ArrayList<String> ListViewContent = new ArrayList<String>();
    private final ArrayList<String> linktable = new ArrayList<String>();
    private final ArrayList<String> authortable = new ArrayList<String>();
    private final ArrayList<String> detailtable = new ArrayList<String>();
    private final ArrayList<String> chaptertable = new ArrayList<String>();
    private final ArrayList<String> viewtable = new ArrayList<String>();
    private final ArrayList<String> Content = new ArrayList<String>();
    private int page = 1;
    private String story_type;
    private String main;
    private String sub;
    private String isend;
    private String sort;
    private String from;
    private String title;
    private String writer;
    private String abstract_w;
    private String ntitle;
    private String nwriter;
    private String nabstract_w;
    private ListView list;
    private ProgressBar spiner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_find);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (customTitleSupported) {
            //ตั้งค่า custom titlebar จาก custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);
            //เชื่อม btnSearch btnDirection เข้ากับ View
            TextView title = (TextView) findViewById(R.id.textViewBar);
            title.setText(" ผลการค้นหา");
            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.nonbar);
            spiner = new ProgressBar(this);
            RelativeLayout.LayoutParams lspin = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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

            ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("finish", "finish");
                    finish();
                }
            });
        }

        Intent intent = getIntent();
        if ((from = intent.getStringExtra("from")) != null) {
            if (from.equals("gp")) {
                story_type = intent.getStringExtra("story_type");
                main = intent.getStringExtra("main");
                sub = intent.getStringExtra("sub");
                isend = intent.getStringExtra("isend");
                sort = intent.getStringExtra("sort");
            } else {
                story_type = intent.getStringExtra("story_type");
                main = intent.getStringExtra("main");
                sub = intent.getStringExtra("sub");
                isend = intent.getStringExtra("isend");
                title = intent.getStringExtra("title");
                writer = intent.getStringExtra("writer");
                abstract_w = intent.getStringExtra("abstract_w");
                ntitle = intent.getStringExtra("ntitle");
                nwriter = intent.getStringExtra("nwriter");
                nabstract_w = intent.getStringExtra("nabstract_w");
            }
            Log.v("from", from);

        }
        adapter = new InteractiveArrayAdapter(this, Content);
        adapter.notifyDataSetChanged();
        dialog = ProgressDialog.show(Find.this, "Loading", "Please Wait...\n\nถ้ารอนานเกินไปกด back 2 ครั้งเพื่อออก", true);
        dialog.setCancelable(true);
        Find_doback dob = new Find_doback();
        dob.execute();
        list = getListView();
        list.setFastScrollEnabled(true);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (linktable.size() == 0) return;
                String url = linktable.get(arg2);
                Log.v("url", url);
                Intent i = new Intent(getBaseContext(), InsertForm.class);
                final String title = ListViewContent.get(arg2);
                Log.v("title", title);
                i.putExtra("name", title);
                //in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
                final String stext = "id=";
                //หาหลักของตอน
                final int start = url.lastIndexOf(stext) + stext.length();
                if (start - stext.length() == -1) {
                    Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Log.v("url.length()", Integer.toString(url.length()));
                //	Log.v("start", Integer.toString(start));
                //Log.v("stext.length()", Integer.toString(stext.length()));
                int len = 0;
                //	Log.v("Character", Character.toString(url.charAt(start)));
                for (int i3 = start; i3 < url.length() && Character.isDigit(url.charAt(i3)); i3++) {
                    len++;
                    //Log.v("Character", Character.toString(url.charAt(i3)));
                }
                //	Log.v("len", Integer.toString(len));
                final String unum = url.substring(start, start + len);
                Log.v("unum", unum);
                url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=";

                Log.v("url", url);
                i.putExtra("url", url);
                final String chapter = chaptertable.get(arg2).replace(" ตอน", "");
                Log.v("chapter", chapter);
                i.putExtra("chapter", chapter);
                startActivity(i);
            }
        });
        list.setOnScrollListener(new LoadMoreListView());
    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) Find.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_find, menu);
        return true;
    }
     */
    private class Find_doback extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spiner.setVisibility(View.VISIBLE);
        }

        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == -1)
                Toast.makeText(Find.this, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Long doInBackground(URL... arg0) {
            try {
                Log.v("doback", "in");
                if (isOnline()) {
                    Log.v("doback", "on");
                    showAllFind();
                    //showAllBookOffline() ;
                } else {
                    Log.v("doback", "off");
                    AlertDialog alertDialog = new AlertDialog.Builder(Find.this).create();
                    alertDialog.setTitle("Error.");
                    alertDialog.setMessage("ไม่ได้เชื่อมต่ออินเตอร์เน็ต\nโปรดตรวจสอบการเชื่อมต่ออินเตอร์เน็ต");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // here you can add functions
                        }
                    });
                    alertDialog.show();
                }
            } catch (Exception e) {

            }
            return null;
        }

        /*		protected void onProgressUpdate(Integer... progress)
        {
        }*/
        protected void onPostExecute(Long result) {
            try {
                if (Content.size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Find.this).create();
                    alertDialog.setTitle("ไม่พบข้อมูล");
                    alertDialog.setMessage("ถ้ามีรายการอยู่ ลองตรวจสอบการเชื่อมต่ออินเตอร์เน็ต แล้วลองใหม่");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // here you can add functions
                        }
                    });
                    alertDialog.show();
                }
                spiner.setVisibility(View.GONE);
                //Find.this.setProgressBarIndeterminateVisibility(false);
                Parcelable state = list.onSaveInstanceState();
                setListAdapter(adapter);
                list.onRestoreInstanceState(state);
                /*				Log.v("listView", "listView");
                for (String i : ListViewContent)
					Log.v("ListViewContent", i);
				for (String i : linktable)
					Log.v("linktable", i);
				for (String i : authortable)
					Log.v("authortable", i);
				for (String i : detailtable)
					Log.v("detailtable", i);
				for (String i : linktable)
					Log.v("viewtable", i);
				for (String i : viewtable)
					Log.v("viewtable", i);*/
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            } catch (Exception e) {
                try {
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                } catch (Exception e1) {
                }
                //e.printStackTrace();
            }
        }
        /*
		public void execute(int i) {
			// TODO Auto-generated method stub

		}*/

        private String convert(String in) throws UnsupportedEncodingException {
            StringBuilder sum = new StringBuilder();
            byte bin[] = in.getBytes("tis620");
            for (int i = 0; i < in.length(); i++) {
                if (bin[i] < 0)
                    sum.append(String.format("%%%02X", bin[i]));
                else
                    sum.append(in.charAt(i));
            }
            return sum.toString();
        }

        private String fixEncoding(String latin1) {
            try {
                byte[] bytes = latin1.getBytes("tis620");
                if (!validUTF8(bytes))
                    return latin1;
                String temp = new String(latin1.getBytes("tis620"), "UTF-8");
                System.out.println(temp);
                return temp;
            } catch (UnsupportedEncodingException e) {
                // Impossible, throw unchecked
                throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
            }
        }

        private boolean validUTF8(byte[] input) {
            int i = 0;
            // Check for BOM
            if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
                    && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
                i = 3;
            }

            int end;
            for (int j = input.length; i < j; ++i) {
                int octet = input[i];
                if ((octet & 0x80) == 0) {
                    continue; // ASCII
                }

                // Check for UTF-8 leading byte
                if ((octet & 0xE0) == 0xC0) {
                    end = i + 1;
                } else if ((octet & 0xF0) == 0xE0) {
                    end = i + 2;
                } else if ((octet & 0xF8) == 0xF0) {
                    end = i + 3;
                } else {
                    // Java only supports BMP so 3 is max
                    return false;
                }

                while (i < end) {
                    i++;
                    octet = input[i];
                    if ((octet & 0xC0) != 0x80) {
                        // Not a valid trailing byte
                        return false;
                    }
                }
            }
            return true;
        }

        private void showAllFind() {
            Log.v("showAllFind", "showAllFind");
            // TODO Auto-generated method stub
			/*		String story_type = "2";
			String main = "1";
			String sub = "17";
			String isend = "1";
			String sort = "1";*/

            //http://www.dek-d.com/writer/frame.php?page=2&main=1&sub=17&isend=1&story_type=2&sort=1
            String url;
            if (from.equals("gp")) {
                url = String.format("http://www.dek-d.com/writer/frame.php?isend=%s&main=%s&sub=%s&story_type=%s&sort=%s&ajax=1&page=%s"
                        , isend, main, sub, story_type, sort, Integer.toString(page));
                //System.out.println(main+sub+isend+story_type+sort);
            } else if (main.equals("0") || main.equals("")) {
                try {
                    title = convert(title);
                    ntitle = convert(ntitle);
                    writer = convert(writer);
                    nwriter = convert(nwriter);
                    abstract_w = convert(abstract_w);
                    nabstract_w = convert(nabstract_w);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url = String.format("http://www.dek-d.com/writer/frame.php?page=%s&is_end=%s&story_groups=&title=%s&n_title=%s&type=%s&writer=%s&n_writer=%s&abstract_w=%s&n_abstract_w=%s"
                        , Integer.toString(page), isend, title, ntitle, story_type, writer, nwriter, abstract_w, nabstract_w);
            } else {
                try {
                    title = convert(title);
                    ntitle = convert(ntitle);
                    writer = convert(writer);
                    nwriter = convert(nwriter);
                    abstract_w = convert(abstract_w);
                    nabstract_w = convert(nabstract_w);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url = String.format("http://www.dek-d.com/writer/frame.php?page=%s&is_end=%s&story_groups=%s,%s&title=%s&n_title=%s&type=%s&writer=%s&n_writer=%s&abstract_w=%s&n_abstract_w=%s"
                        , Integer.toString(page), isend, main, sub, title, ntitle, story_type, writer, nwriter, abstract_w, nabstract_w);
            }
            System.out.println(url);
            Document doc = null;
            try {

                if (from.equals("gp")) {

                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
                    connection.setRequestProperty("Content-Type", "text/html; charset=tis-620");
                    connection.addRequestProperty("REFERER", "http://www.dek-d.com/writer/frame.php");
                    InputStreamReader in = new InputStreamReader(connection.getInputStream(), Charset.forName("utf-8"));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int next = in.read();
                    while (next > -1) {
                        bos.write(next);
                        next = in.read();
                    }
                    bos.flush();
                    byte[] result = bos.toByteArray();

                    connection.disconnect();
/*                    String line = "";
                   StringWriter writer = new StringWriter();
                    while((line=reader.readLine())!=null){
                        writer.write(line);
                    }
                    reader.close();
                    writer.close();
                    //System.out.println(connection.getContentEncoding());
                    doc = Jsoup.connect(url)
                            .referrer("http://www.dek-d.com/writer/frame.php")
                            .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3")
                            .get();

                    */
                    String line = StringEscapeUtils.unescapeJava(new String(result));
                    //String line2 = StringEscapeUtils.unescapeJava(new String(result, "TIS-620"));
                    // line3 = StringEscapeUtils.unescapeJava(new String(result, "UTF-8"));
                    //System.out.println(line);
                    if (line.contains("เธเธ")) {
                        line = new String(line.getBytes("US-ASCII"), "tis620");
                        /*in = new InputStreamReader(connection.getInputStream(),"UTF-8");
                        bos = new ByteArrayOutputStream();
                        next = in.read();
                        while (next > -1) {
                            bos.write(next);
                            next = in.read();
                        }
                        bos.flush();
                        result = bos.toByteArray();
                        connection.disconnect();
                        line = StringEscapeUtils.unescapeJava(new String(result));*/
                    }
                    //String template = line.substring(line.indexOf( ",\"template\":\"" )+",\"template\":\"".length(),line.length()-3);
                    doc = Jsoup.parse(line);
                    doc.outputSettings().escapeMode(EscapeMode.xhtml);
                    doc.outputSettings().charset("TIS-620");
                } else {
                    doc = Jsoup.connect(url)
                            .referrer("http://www.dek-d.com/writer/frame.php")
                            .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3")
                            .get();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                publishProgress(-1);
                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (!doc.select(".fr-book").isEmpty() && doc.select(".fr-book").first().text().equals("ไม่พบข้อมูล")) {
                if (Content.size() == 0)
                    Content.add("ไม่พบข้อมูล");
                return;
            }
            //System.out.println(doc.html());
            Elements link1 = doc.select(".book-text6");
            int startsize = ListViewContent.size();
            Log.v("startsize", Integer.toString(startsize));
            if (link1 == null) {
                if (Content.size() == 0)
                    Content.add("ไม่พบข้อมูล");
                return;
            }
            for (Element link : link1) {
                String stext = fixEncoding(link.text());
                if (stext.contains("by")) {
                    authortable.add(stext);
                    continue;
                }
                linktable.add(link.select("a").attr("href"));
                ListViewContent.add(stext);
            }
            Log.v("startsize", Integer.toString(startsize));
            if (startsize == ListViewContent.size()) {
                page = -1;
                return;
            }
            link1 = doc.select(".book-text1");
            if (link1 == null) return;
            for (Element link : link1) {
                String stext = link.text();
                detailtable.add(stext);
            }
            link1 = doc.select(".view");
            if (link1 == null) return;
            for (Element link : link1) {
                String stext = link.text();
                if (stext.contains("/"))
                    viewtable.add(stext);
            }
            link1 = doc.select("p.chapter");
            if (link1 == null) return;
            for (Element link : link1) {
                String stext = link.text();
                chaptertable.add(stext);
            }
            for (int position = startsize; position < ListViewContent.size(); position++)
                Content.add("<br/><p><font color=#33B6EA>เรื่อง :" + ListViewContent.get(position) + " " + authortable.get(position) +
                        " </font><br /><br />" +
                        "<font color=#cc0029> " + detailtable.get(position) + "</font><br/><br />" +
                        "<font color=#339900> ผู้เข้าชม เดือนนี้ " + viewtable.get(position).substring(0, viewtable.get(position).indexOf("/")) + " / ทั้งหมด " + viewtable.get(position).substring(viewtable.get(position).indexOf("/") + 1, viewtable.get(position).length()) +
                        " มี " + chaptertable.get(position) +
                        " </font></p>");
        }
    }

    private class InteractiveArrayAdapter extends ArrayAdapter<String> {
        private final Activity context;
        private ArrayList<String> names = new ArrayList<String>();

        public InteractiveArrayAdapter(Activity context, ArrayList<String> names) {
            super(context, R.layout.list_item3, names);
            this.context = context;
            this.names = names;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_item3, null);
            }
            TextView text = (TextView) rowView.findViewById(R.id.textView1);
			/*
			System.out.println(names.get(position));
			System.out.println(detailtable.get(position));
			System.out.println(authortable.get(position));
			String htmltext = "<br/><p><font color=#33B6EA>เรื่อง :" +names.get(position)+" " +authortable.get(position)+
					" </font><br /><br />" +
					"<font color=#cc0029> " +detailtable.get(position)+"</font><br/><br />" +
					"<font color=#339900> ผู้เข้าชม เดือนนี้ " +viewtable.get(position).substring(0, viewtable.get(position).indexOf("/"))+" / ทั้งหมด " +viewtable.get(position).substring(viewtable.get(position).indexOf("/")+1,viewtable.get(position).length())+
							" มี " +chaptertable.get(position)+
					" </font></p>";
			 */
            text.setText((names.get(position) != null) ? Html.fromHtml(names.get(position)) : "1");
            //thumb_image.setImageResource(R.drawable.rihanna);
            //thumb_image.setImageBitmap(imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image));
            //imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image);
            return rowView;
        }

    }

    private class LoadMoreListView implements OnScrollListener {
        //private int visibleThreshold = 5;
        private int previousTotal = 0;
        private boolean loading = true;

        public LoadMoreListView() {
        }

        /*		public LoadMoreListView(int visibleThreshold) {
            //this.visibleThreshold = visibleThreshold;
        }
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //Log.v("firstVisibleItem", Integer.toString(firstVisibleItem));
            //Log.v("visibleItemCount", Integer.toString(visibleItemCount));
            //Log.v("totalItemCount", Integer.toString(totalItemCount));
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    page++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleItemCount)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                new Find_doback().execute();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }
}
