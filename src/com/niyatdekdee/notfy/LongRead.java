package com.niyatdekdee.notfy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@SuppressLint("SetJavaScriptEnabled")
public class LongRead extends Activity {

    private WebView webView;
    private Document doc;
    private String url;
    protected ProgressDialog prodialog;
    protected AsyncTask<Context, String, Long> work;
    private GoogleAnalytics mGaInstance;
    private Tracker mGaTracker;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Setting.get_FullCheck(getApplicationContext())) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mGaInstance = GoogleAnalytics.getInstance(this);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker("UA-37746897-1");
        webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus(View.FOCUS_DOWN);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_long_read);
        dialog.setTitle("เลือกช่วงตอน");
        dialog.setCancelable(false);
        Button dialogButton = (Button) dialog.findViewById(R.id.longbutton1);
        final EditText start = (EditText) dialog.findViewById(R.id.longeditText1);
        final EditText end = (EditText) dialog.findViewById(R.id.longeditText2);
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    prodialog = new ProgressDialog(LongRead.this);
                    prodialog.setMessage("Please Wait...\nถ้ารู้สึกช้า เกินกวา 30 วินาทีออกแล้วเข้าใหม่");
                    prodialog.setCancelable(true);

                    if (end.length() == 0) {
                        work = new Long_doback(Integer.parseInt(start.getText().toString()), Integer.parseInt(start.getText().toString()));
                        if (mGaTracker != null)
                            mGaTracker.sendTiming("resources", 1000, "longread3", null);

                    } else {
                        work = new Long_doback(Integer.parseInt(start.getText().toString()), Integer.parseInt(end.getText().toString()));
                        if (mGaTracker != null)
                            mGaTracker.sendTiming("resources", 1000 * (Integer.parseInt(end.getText().toString()) - Integer.parseInt(start.getText().toString())), "longread4", null);
                    }


                    work.execute(getBaseContext());
                } catch (NumberFormatException e) {
                    Toast.makeText(getBaseContext(), "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        Button candialogButton1 = (Button) dialog.findViewById(R.id.longbutton2);

        candialogButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
        webView.setWebViewClient(new WebViewClient() {
            // Override URL
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(webView, url2);

                if (android.os.Build.VERSION.SDK_INT < 11) {
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "images = story.getElementsByTagName('img');" +
                            "for (i=0;i<images.length;i++) {images[i].outerHTML = \"<div id='touchscroll_imagediv\"+i.toString()+\"' style='border: 1px solid black; overflow: auto;  height: auto; width: 100%;'>\"+images[i].outerHTML+\"</div>\";" +
                            "touchScroll('touchscroll_imagediv'+i.toString());}\n");
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('table');" +
                            "for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div id='touchscroll_tablediv\"+i.toString()+\"' style='border: 1px solid black; overflow: auto;  height: auto; width: 100%;'>\"+tables[i].outerHTML+\"</div>\";" +
                            "touchScroll('touchscroll_tablediv'+i.toString());}\n");
                } else {
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "images = story.getElementsByTagName('img');" +
                            "for (i=0;i<images.length;i++) {images[i].outerHTML = \"<div style='overflow: scroll;'>\"+images[i].outerHTML+\"</div>\";}");

                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('table');" +
                            "for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div style='overflow: scroll;'>\"+tables[i].outerHTML+\"</div>\";}\n");
                }

            }
        });
/*		webView.setWebChromeClient(new WebChromeClient() {
            @Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				// TODO Auto-generated method stub
				Log.v("ChromeClient", "invoked: onConsoleMessage() - " + sourceID + ":"
						+ lineNumber + " - " + message);
				super.onConsoleMessage(message, lineNumber, sourceID);
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.v("ChromeClient", cm.message() + " -- From line "
						+ cm.lineNumber() + " of "
						+ cm.sourceId() );
				return true;
			}
		});*/
        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

    }

    class MyJavaScriptInterface {
        public void processHTML(String html) {
            Log.e("zone", "end html");
            //TTS.strtotext(html);
            //ContextWrapper cw = new ContextWrapper(getBaseContext());
            //File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),"ll.html");
            //System.out.println(temp.getAbsolutePath());
            //bw.write(text1.replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
/*			try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
				bw.write(html);
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
            Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
            intent.putExtra("from", "main");
            DekTTSActivity.type = 5;
            DekTTSActivity.text = html;
            startService(intent);
        }
    }

    class Long_doback extends AsyncTask<Context, String, Long> {

        private int start;
        private int end;
        private StringBuilder HTMLdata = new StringBuilder();
        private int index;
        private StringBuilder HTMLdata2;

        public Long_doback(int s, int e) {
            // TODO Auto-generated constructor stub
            start = s;
            end = e;
        }

        private void HTMLAdd(int index) {
            // TODO Auto-generated method stub
            HTMLdata2 = new StringBuilder();
            try {
                doc = Jsoup.connect(url + Integer.toString(index)).get();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (doc == null) {
                System.out.println("doc null");
                return;
            }
            if (doc.title() != null) HTMLdata2.append("<br><h2>").append(doc.title()).append("</h2><br><br><br>");

            {
                Elements story = doc.select("#story_body").select("td");
                if (story != null && story.first() != null) {


                    if (story.select("div") != null) if (story.select("div").last() != null)
                        story.select("div").last().remove();
                    try {
                        for (Element i : story.first().children()) {
                            HTMLdata2.append(i.html().replace("\"", "\\\"").replace("\r\n", " ").replace('\n', ' ').replace('\r', ' ').replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>"));
                            publishProgress("1", HTMLdata2.toString());
                            HTMLdata2 = new StringBuilder();
                        }
                    } catch (OutOfMemoryError e) {
                        //TODO do something intelligent
                        HTMLdata2 = null;
                    }
                }
            }
            //publishProgress("1",HTMLdata.toString());
        }

        private void HTMLMake(int start) {
            try {
                doc = Jsoup.connect(url + start).get();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (doc == null) {
                System.out.println("doc null");
                return;
            }
            HTMLdata.append("<html>");
            HTMLdata.append("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=tis-620\">");
            HTMLdata.append("<style type=\"text/css\">" +
                    "textarea {" +
                    "border: none;" +
                    "width: 100%;" +
                    "-webkit-box-sizing: border-box;" +
                    "box-sizing: border-box;" +
                    "}" +
                    "</style>" +
                    "<script type=\"text/javascript\">" +
                    "\r\n" +
                    "function isTouchDevice(){\r\n" +
                    "	/* Added Android 3.0 honeycomb detection because touchscroll.js breaks\r\n" +
                    "		the built in div scrolling of android 3.0 mobile safari browser */\r\n" +
                    "	if((navigator.userAgent.match(/android 3/i)) ||\r\n" +
                    "		(navigator.userAgent.match(/honeycomb/i)))\r\n" +
                    "		return false;\r\n" +
                    "	try{\r\n" +
                    "		document.createEvent(\"TouchEvent\");\r\n" +
                    "		return true;\r\n" +
                    "	}catch(e){\r\n" +
                    "		return false;\r\n" +
                    "	}\r\n" +
                    "}\r\n" +
                    "\r\n" +
                    "function touchScroll(id){\r\n" +
                    "	if(isTouchDevice()){ //if touch events exist...\r\n" +
                    "		var el=document.getElementById(id);\r\n" +
                    "		var scrollStartPosY=0;\r\n" +
                    "		var scrollStartPosX=0;\r\n" +
                    "\r\n" +
                    "		document.getElementById(id).addEventListener(\"touchstart\", function(event) {\r\n" +
                    "			scrollStartPosY=this.scrollTop+event.touches[0].pageY;\r\n" +
                    "			scrollStartPosX=this.scrollLeft+event.touches[0].pageX;\r\n" +
                    "			//event.preventDefault(); // Keep this remarked so you can click on buttons and links in the div\r\n" +
                    "		},false);\r\n" +
                    "\r\n" +
                    "		document.getElementById(id).addEventListener(\"touchmove\", function(event) {\r\n" +
                    "			// These if statements allow the full page to scroll (not just the div) if they are\r\n" +
                    "			// at the top of the div scroll or the bottom of the div scroll\r\n" +
                    "			// The -5 and +5 below are in case they are trying to scroll the page sideways\r\n" +
                    "			// but their finger moves a few pixels down or up.  The event.preventDefault() function\r\n" +
                    "			// will not be called in that case so that the whole page can scroll.\r\n" +
                    "			if ((this.scrollTop < this.scrollHeight-this.offsetHeight &&\r\n" +
                    "				this.scrollTop+event.touches[0].pageY < scrollStartPosY-5) ||\r\n" +
                    "				(this.scrollTop != 0 && this.scrollTop+event.touches[0].pageY > scrollStartPosY+5))\r\n" +
                    "					event.preventDefault();	\r\n" +
                    "			if ((this.scrollLeft < this.scrollWidth-this.offsetWidth &&\r\n" +
                    "				this.scrollLeft+event.touches[0].pageX < scrollStartPosX-5) ||\r\n" +
                    "				(this.scrollLeft != 0 && this.scrollLeft+event.touches[0].pageX > scrollStartPosX+5))\r\n" +
                    "					event.preventDefault();	\r\n" +
                    "			this.scrollTop=scrollStartPosY-event.touches[0].pageY;\r\n" +
                    "			this.scrollLeft=scrollStartPosX-event.touches[0].pageX;\r\n" +
                    "		},false);\r\n" +
                    "	}\r\n" +
                    "}\r\n" +
                    "</script>" +
                    "<script language=\"JavaScript\" type=\"text/JavaScript\">" +
                    "function playSound(){}" +
                    "function showPoll(){}" +
                    "</script>");
			/*
				HTMLdata.append("<style type=\"text/css\">" +
						"p{" +
						"font-family:'Cordia New',sans-serif;"+
						"}" +
						"</style>");
			 */
            HTMLdata.append("</head>");

            HTMLdata.append("<body>");

            if (doc.title() != null) HTMLdata.append("\n<br><h2>").append(doc.title()).append("</h2><br><br><br>\n");
            HTMLdata.append("<div id='story_body'><div>");

            {
                Elements story = doc.select("#story_body").select("td");
                if (story != null && story.first() != null) {

                    if (story.select("div") != null) if (story.select("div").last() != null)
                        story.select("div").last().remove();
                    HTMLdata.append(story.first().html()/*.replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>")*/);
                }
            }
            HTMLdata.append("</div></div>");

            HTMLdata.append("<br><br><div id='detail'><h4>ข้อตกลง &amp; เงื่อนไขการใช้งาน</h4><br><ul><li><p>กรณีที่ผลงานชิ้นนี้เป็นผลงานที่แต่งโดยผู้ลงผลงานเอง ลิขสิทธิ์ของผลงานนี้จะ<br>เป็นของผู้ลงผลงานโดยตรง ห้ามมิให้คัดลอก ทำซ้ำ เผยแพร่ ก่อนได้รับอนุญาต<br>" +
                    "จากผู้ลงผลงาน</p></li><li><p>กรณีที่ผลงานชิ้นนี้กระทำการคัดลอก ทำซ้ำ มาจากผลงานของบุคคลอื่นๆ ผู้ลง<br>ผลงานจะต้องทำการอ้างอิงอย่างเหมาะสม และต้องรับผิดชอบเรื่องการจัดการ<br>ลิขสิทธิ์แต่เพียงผู้เดียว</p></li><li><p>ข้อความและรูปภาพที่ปรากฏอยู่ในผลงานที่ท่านเห็นอยู่นี้ เกิดจากการส่งเข้าระบบ<br>" +
                    "โดยอัตโนมัติจากบุคคลทั่วไป ซึ่งเด็กดีดอทคอมมิได้มีส่วนร่วมรู้เห็น ตรวจสอบ <br>หรือพิสูจน์ข้อเท็จจริงใดๆ ทั้งสิ้น ผู้ใดพบเห็นการลงผลงานละเมิดลิขสิทธิ์ หรือ<br>ไม่เหมาะสมโปรดแจ้งผู้ดูแลระบบเพื่อดำเนินการทันที<br>" +
                    "Email: <span>contact@dek-d.com</span> ( ทุกวัน 24 ชม ) หรือ<br>Tel: <span>0-2860-1142</span> ( จ-ศ 0900-1800 )</p></li></ul>" +
                    "</div></body></html>");

            doc = null;
            publishProgress("0");
        }

        @Override
        protected Long doInBackground(Context... arg0) {
            // TODO Auto-generated method stub
            HTMLMake(start);
            for (index = start + 1; index <= end; index++)
                HTMLAdd(index);
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("1")) {
                int temp = webView.getScrollY();
                progress[1] = progress[1].replace("//-->", "-->");

                webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                        "var newdiv = document.createElement('div');" +
                        "newdiv.innerHTML =\"" + progress[1].trim() + "<br><br>\";" +
                        "story.appendChild(newdiv);");

                webView.scrollTo(0, temp);
            } else if (progress[0].equals("0")) {
                webView.loadDataWithBaseURL(url + start, HTMLdata.toString().replace("//-->", "-->"), "text/html", "utf-8", null);
                try {
                    if (prodialog != null && prodialog.isShowing()) prodialog.dismiss();
                } catch (Exception e1) {
                }

            } else if (progress[0].equals("2")) {

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prodialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            webView.pageDown(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            webView.pageUp(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                DekTTSActivity.tts.stop();
                DekTTSActivity.stop = true;
                DekTTSActivity.isSpeak = false;
                Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
                return true;
            }
            if (work != null) work.cancel(true);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tts:
                if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                    DekTTSActivity.tts.stop();
                    DekTTSActivity.stop = true;
                    DekTTSActivity.isSpeak = false;
                    //Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
                }
                if (work.getStatus() == AsyncTask.Status.RUNNING) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LongRead.this);
                    builder.setMessage("ยังโหลดไม่คบครบทุกตอน ต้องการฟังแค่ที่มีหรือไม่")
                            .setCancelable(false)
                            .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    webView.loadUrl("javascript: HTMLOUT.processHTML(document.getElementById('story_body').outerHTML);");
                                }
                            })
                            .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else
                    webView.loadUrl("javascript: HTMLOUT.processHTML(document.getElementById('story_body').outerHTML);");
                return true;
            case R.id.reselect:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_long_read, menu);
        return true;
    }
}
