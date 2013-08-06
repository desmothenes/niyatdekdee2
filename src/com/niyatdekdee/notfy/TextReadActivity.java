package com.niyatdekdee.notfy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;

@SuppressLint("SetJavaScriptEnabled")
public class TextReadActivity extends Activity {
    private static int scRoll;
    private static int sclight = 0;
    static Intent intent;
    private WebView webView;
    private String ttstext;
    private String oriurl;
    private ProgressDialog dialog;
    private String urlid;
    private int cp;
    //private boolean isFile;
    private File temp;
    private Text_Doback text_doback;
    private static int font_size = 0;

    static int getScRoll() {
        return scRoll;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        if (Setting.get_FullCheck(getApplicationContext())) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        if (sclight > 0) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = sclight / 100.0f;
            getWindow().setAttributes(lp);
        }
        if (scRoll == 0)
            if (Setting.getScreenSetting(getApplicationContext()).equals("1"))//(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                scRoll = -1;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                scRoll = 1;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        else if (scRoll == -1)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean("text_first_run", true)) {
            startActivity(new Intent(getBaseContext(), Flow3.class));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("text_first_run", false);
            editor.commit();
        }
        intent = null;
        webView = new WebView(this);
        webView.getSettings().setDefaultTextEncodingName("TIS-620");

        //System.out.println(test.substring(test.indexOf("<title>")+7));
        intent = getIntent();
        System.out.println(intent.getStringExtra("id") != null ? intent.getStringExtra("id") : "null");
        dialog = new ProgressDialog(TextReadActivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("โปรดรอ...\nถ้ารู้สึกช้า ออกแล้วเข้าใหม่");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        if (intent.getStringExtra("id") != null && !intent.getStringExtra("id").equals("-2")) {
            //isFile = true;
            text_doback = new Text_Doback();
            text_doback.execute(false);
        } else {
            oriurl = intent.getStringExtra("url");
            //dialog = ProgressDialog.show(TextReadActivity.this,"Loading", "Please Wait...\nถ้ารู้สึกช้า ออกแล้วเข้าใหม่",true);
            text_doback = new Text_Doback();
            text_doback.execute(true);
        }

        //webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        setContentView(webView);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 100);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(webView, url2);

                try {
                    if (dialog != null)
                        if (dialog.isShowing())
                            dialog.dismiss();
                } catch (Exception e1) {
                }

                webView.loadUrl("javascript: document.getElementsByName('txtCode')[0].type = 'number';");
                webView.loadUrl("javascript: document.getElementsByName('t_name')[0].value='" + Setting.getPosttext(getApplicationContext()) + " (เขียนบน android)';");
                if (android.os.Build.VERSION.SDK_INT < 11) {
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "images = story.getElementsByTagName('img');" +
                            "for (i=0;i<images.length;i++) {images[i].outerHTML = \"<div id='touchscroll_imagediv\"+i.toString()+\"' style='border: 1px solid black; overflow: auto;  height: auto; width: 100%;'>\"+images[i].outerHTML+\"</div>\";" +
                            "touchScroll('touchscroll_imagediv'+i.toString());}\n");
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('iframe');" +
                            "for (i=0;i<tables.length;i++) {tables[i].width=\"100\"; tables[i].outerHTML = \"<div id='touchscroll_tablediv\"+i.toString()+\"' style='border: 1px solid black; overflow: auto;  height: auto; width: 100%;'>\"+tables[i].outerHTML+\"</div>\";" +
                            "touchScroll('touchscroll_tablediv'+i.toString());}\n");
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('table');" +
                            "for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div id='touchscroll_tablediv\"+i.toString()+\"' style='border: 1px solid black; overflow: auto;  height: auto; width: 100%;'>\"+tables[i].outerHTML+\"</div>\";" +
                            "touchScroll('touchscroll_tablediv'+i.toString());}\n");
                } else {
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "images = story.getElementsByTagName('img');" +
                            "for (i=0;i<images.length;i++) {images[i].outerHTML = \"<div style='overflow: scroll;'>\"+images[i].outerHTML+\"</div>\";}");

                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('iframe');" +
                            "for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div style='overflow: scroll;'>\"+tables[i].outerHTML+\"</div>\";}\n");
                    webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
                            "tables = story.getElementsByTagName('table');" +
                            "for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div style='overflow: scroll;'>\"+tables[i].outerHTML+\"</div>\";}\n");
                }

                if (Setting.getisLogin(getApplicationContext())) {
                    webView.loadUrl("javascript: document.getElementsByName('t_mem')[1].checked  = true;");
                    webView.loadUrl("javascript: document.getElementsByName('t_username')[0].value='" + Setting.getUserName(getApplicationContext()) + "';");
                    webView.loadUrl("javascript: document.getElementsByName('t_password')[0].value='" + Setting.getPassWord(getApplicationContext()) + "';");
                } else {
                    webView.loadUrl("javascript: document.getElementsByName('t_mem')[1].checked  = false;");
                }

                if (font_size > 0) {
                    for (int i = 0; i < font_size; i++) {
                        webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
                                "for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)+5+'px'};");
                    }
                } else if (font_size < 0) {
                    webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
                            "for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)-5+'px'};");
                }


                SharedPreferences prefs = getPreferences(MODE_PRIVATE);

                final int yposi = prefs.getInt(String.format("scroll %s", oriurl), 0);
                if (yposi != 0 && url2.equals(oriurl))
                    webView.scrollTo(0, yposi);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("showCommentc")) {
                    dialog.show();
                    view.loadUrl(url);
                } else if (url.endsWith("view.php?id=" + urlid)) {
                    if (intent.getBooleanExtra("fromindex", false)) {
                        finish();
                    } else {
                        Intent chapterlist = new Intent(getBaseContext(), ChapterListActivity.class);
                        chapterlist.putExtra("url", "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + urlid + "&chapter=");
                        String temp = webView.getTitle();
                        if (temp == null) temp = "";
                        final int cutpoint = temp.indexOf(">");
                        if (cutpoint != -1) temp = temp.substring(0, cutpoint);
                        chapterlist.putExtra("title", temp);
                        chapterlist.putExtra("cp", cp);
                        startActivity(chapterlist);
                        finish();
                    }
                } else if (url.contains("viewlongc.php?id=" + urlid + "&chapter=")) {
                    Intent newtext = new Intent(getBaseContext(), TextReadActivity.class);
                    newtext.putExtra("url", url);
                    newtext.putExtra("from", "text");
                    newtext.putExtra("fromindex", intent.getBooleanExtra("fromindex", false));
                    startActivity(newtext);
                    finish();
                } else {
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;

            }
        });

        webView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        webView.setLongClickable(false);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        /*		if (!Setting.getSelectImageSetting(this))
            webView.getSettings().setLoadsImagesAutomatically(false);*/
        webView.requestFocus(View.FOCUS_DOWN);
        /*		CookieSyncManager.createInstance(TextReadActivity.this);
		CookieManager cookieManager = CookieManager.getInstance();
		String sessionCookie = intent.getStringExtra("cookieString");
		if (sessionCookie != null)
			cookieManager.removeSessionCookie();
		SystemClock.sleep(1000);

		cookieManager = CookieManager.getInstance();

		if (sessionCookie != null) {
			//Log.e("cookieString",sessionCookie);
			cookieManager.setCookie("dek-d.com", sessionCookie);
			CookieSyncManager.getInstance().sync();
		}

		CookieSyncManager.createInstance(webView.getContext());*/
        webView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!arg0.hasFocus()) {
                            arg0.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
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
                if (DekTTSActivity.tts.isSpeaking()) DekTTSActivity.tts.stop();
                DekTTSActivity.stop = true;
                DekTTSActivity.isSpeak = false;
                Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
                return true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(TextReadActivity.this);
            builder.setMessage("คุณต้องการที่จะ ?")
                    .setCancelable(true)
                    .setPositiveButton("ออกจากหน้านี้", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                            String temp = oriurl;
                            editor.putInt(String.format("scroll %s", temp), webView.getScrollY());
                            //Log.e(String.format("scroll x %s",temp), Integer.toString(webView.getScrollX()));
                            Log.e(String.format("scroll y %s", temp), Integer.toString(webView.getScrollY()));
                            editor.commit();
                            if (intent.getStringExtra("from") == null)
                                stopService(new Intent(getApplicationContext(), DekTTSActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("ย้อนกลับ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {
                                Toast.makeText(getBaseContext(), "กรุณาเลื่อนไปกดปุ่ม ตอนก่อนหน้า แทนครับ\nปุ่มย้อนกลับใช้สำหรับย้อนจากหน้าความคิดเห็นเท่านั้น", Toast.LENGTH_LONG).show();
                                Toast.makeText(getBaseContext(), "กรุณาเลื่อนไปกดปุ่ม ตอนก่อนหน้า แทนครับ\nปุ่มย้อนกลับใช้สำหรับย้อนจากหน้าความคิดเห็นเท่านั้น", Toast.LENGTH_LONG).show();
                            }
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_text_read, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.story:
                webView.loadUrl("javascript: var elem = document.getElementById('story_body');" +
                        "var x = 0; var y = 0;" +
                        "    while (elem != null) {" +
                        "		y += elem.offsetTop;" +
                        "	elem = elem.offsetParent;" +
                        "}" +
                        "window.scrollTo(x, y);");
                return true;
            case R.id.comment:
                webView.loadUrl("javascript: var elem = document.getElementById('comment33');" +
                        "var x = 0; var y = 0;" +
                        "    while (elem != null) {" +
                        "		y += elem.offsetTop;" +
                        "	elem = elem.offsetParent;" +
                        "}" +
                        "window.scrollTo(x, y-33);");
                return true;
            case R.id.postcomment:
                webView.loadUrl("javascript:  var elem = document.getElementsByName('t_msg')[0];" +
                        "var x = 0; var y = 0;" +
                        "    while (elem != null) {" +
                        "		y += elem.offsetTop;" +
                        "	elem = elem.offsetParent;" +
                        "}" +
                        "window.scrollTo(x, y-90);");
                return true;
            case R.id.Top:
                webView.pageUp(true);
                return true;
            case R.id.buttom:
                webView.pageDown(true);
                return true;
            case R.id.tts:
                Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                intent.putExtra("from", "text");
			/*		if (isFile && temp.canRead() && !intent.getBooleanExtra("fromindex", false)) {
				Log.e("zone", "by file tts");
				DekTTSActivity.type = 2;
				DekTTSActivity.temp = temp;
			}else {*/
                Log.e("zone text", "by text tts");
                DekTTSActivity.type = 3;
                //System.out.println(ttstext);
                if (ttstext != null) DekTTSActivity.text = ttstext;
                else {
                    Toast.makeText(getBaseContext(), "ผิดพลาด โปรดออกแล้วเข้าใหม่", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //}
                //System.out.println(ttstext);
                getApplicationContext().startService(intent);
                return true;
            case R.id.light:
                ShowDialog();
			/*Dialog yourDialog = new Dialog(this);
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.seek, (ViewGroup)findViewById(R.id.root));
			yourDialog.setContentView(layout);
			//Button yourDialogButton = (Button)layout.findViewById(R.id.button);
			SeekBar yourDialogSeekBar = (SeekBar) layout.findViewById(R.id.seekbar);
			yourDialogSeekBar.setProgress(50);
			OnSeekBarChangeListener yourSeekBarListener = new OnSeekBarChangeListener() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					//add code here
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					//add code here
					//seekBar.setProgress(100);
				}

				@Override
				public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
					//add code here
					lp.screenBrightness = progress/ 100.0f;
					getWindow().setAttributes(lp);
					sclight = progress;
				}
			};
			yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);
			layout.setOnTouchListener(new OnTouchListener() {
			    @Override
			    public boolean onTouch(View v, MotionEvent event) {
			    	 return true;
			    }
			});
			yourDialog.show();*/
                return true;
            case R.id.roll:
                if (scRoll == -1) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
                scRoll = -scRoll;
                return true;
            case R.id.inctext:
                //เพิ่มขนาด font
                font_size += 1;
                Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
                webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
                        "for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)+5+'px'};");
                return true;
            case R.id.dectext:
                //ลดขนาด font
                font_size -= 1;
                Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
                webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
                        "for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)-5+'px'};");
                return true;
            case R.id.next_cp:
                String url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + urlid + "&chapter=" + Integer.toString(cp + 1);
                Intent newtext = new Intent(getBaseContext(), TextReadActivity.class);
                newtext.putExtra("url", url);
                newtext.putExtra("from", "text");
                if (TextReadActivity.intent != null)
                    newtext.putExtra("fromindex", TextReadActivity.intent.getBooleanExtra("fromindex", false));
                startActivity(newtext);
                finish();
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Save the state of Webview
        webView.saveState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of webview
        webView.restoreState(savedInstanceState);
    }

    private void ShowDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(TextReadActivity.this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(100);
        //seek.setBackgroundDrawable(new ColorDrawable(0));
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("ระดับแสงสว่าง");
        popDialog.setView(seek);

        if (sclight != 0)
            seek.setProgress(sclight);
        else
            seek.setProgress(50);

        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        System.out.println(lp.screenBrightness);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something here with new value
                if (progress == 0) progress++;
                lp.screenBrightness = progress / 100.0f;
                getWindow().setAttributes(lp);
                sclight = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });

        AlertDialog a = popDialog.create();
        a.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        a.show();
    }

 /*   private class MyAlertDialog extends AlertDialog {
        private MyAlertDialog(Context context) {
            super(context);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }*/

    private class Text_Doback extends AsyncTask<Boolean, String, Void> {

        private StringBuilder HTMLdata;
        private Document doc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HTMLdata = new StringBuilder();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Boolean... arg0) {
            // TODO Auto-generated method stub
            if (arg0[0]) {
                try {
                    final String tempurl = intent.getStringExtra("url");
                    if (tempurl == null) return null;
                    if (MainActivity.sessionId == null)
                        doc = Jsoup.connect(tempurl).cookies(MainActivity.sessionId).timeout(16000).get();
                    else
                        doc = Jsoup.connect(tempurl).timeout(16000).get();

                } catch (IOException e) {
                    publishProgress("-3");
                    e.printStackTrace();
                    return null;
                    //finish();
                }

            } else {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), intent.getStringExtra("id") + ".html");
                try {
                    oriurl = intent.getStringExtra("url");
                    doc = Jsoup.parse(temp, "tis620");
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        final String tempurl = intent.getStringExtra("url");
                        if (tempurl == null) return null;
                        if (MainActivity.sessionId == null)
                            doc = Jsoup.connect(tempurl).cookies(MainActivity.sessionId).timeout(16000).get();
                        else
                            doc = Jsoup.connect(tempurl).timeout(16000).get();

                    } catch (IOException e2) {
                        publishProgress("-1");
                        e2.printStackTrace();
                        finish();
                    }
                }
            }
            HTMLMake();
            return null;
        }

        protected void onPostExecute(Void result) {
            ttstext = HTMLdata.toString();
            if (temp != null)
                webView.loadDataWithBaseURL(oriurl, ttstext, "text/html", "utf-8", "file://" + temp.getAbsolutePath());
            else
                webView.loadDataWithBaseURL(oriurl, ttstext, "text/html", "utf-8", null);
        }

        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("-1")) {
                dialog.setMessage("การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                Toast.makeText(getApplicationContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_SHORT).show();
                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            } else if (progress[0].equals("-2")) {
                dialog.setMessage("ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข");
                Toast.makeText(getApplicationContext(), "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข", Toast.LENGTH_SHORT).show();
                //Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            } else if (progress[0].equals("-3")) {
                dialog.setMessage("การเชื่อมต่อมีปัญหา");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(TextReadActivity.this);
                builder.setMessage("การเชื่อมต่อมีปัญหา คุณต้องการที่จะ ?")
                        .setCancelable(true)
                        .setPositiveButton("ออกจากหน้านี้", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                                    DekTTSActivity.tts.stop();
                                    DekTTSActivity.stop = true;
                                    DekTTSActivity.isSpeak = false;
                                    DekTTSActivity.tts.stop();
                                    DekTTSActivity.stop = true;
                                    DekTTSActivity.isSpeak = false;
                                    Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
                                }
                                if (text_doback != null)
                                    text_doback.cancel(true);
                                finish();
                            }
                        })
                        .setNegativeButton("ลองใหม่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (text_doback != null)
                                    text_doback.cancel(true);
                                if (intent.getStringExtra("id") != null && !intent.getStringExtra("id").equals("-2")) {
                                    text_doback = new Text_Doback();
                                    text_doback.execute(false);
                                } else {
                                    text_doback = new Text_Doback();
                                    text_doback.execute(true);
                                }
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                dialog.setMessage(progress[0]);
            }
        }

        private void HTMLMake() {
            if (doc == null) {
                System.out.println("doc null");
                return;
            }
            publishProgress("25 %");
            final int theme = Integer.parseInt(Setting.getBgColorSetting(getApplicationContext()));
            final int thcc = Integer.parseInt(Setting.getTextColorSetting(getApplicationContext()));
            //final int widthsc = getBaseContext().getResources().getDisplayMetrics().widthPixels<getBaseContext().getResources().getDisplayMetrics().heightPixels ? getBaseContext().getResources().getDisplayMetrics().widthPixels+60 :getBaseContext().getResources().getDisplayMetrics().heightPixels ;
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
            if (thcc == 1)
                HTMLdata.append("<style type=\"text/css\">" +
                        "*{" +
                        "color:black;" +
                        "}" +
                        "</style>");
            else if (thcc == 2)
                HTMLdata.append("<style type=\"text/css\">" +
                        "*{" +
                        "color:white;" +
                        "}" +
                        "input, select, textarea{" +
                        "color: #0000ff;" +
                        "}" +
                        "textarea:focus, input:focus {" +
                        "color: #0000ff;" +
                        "}" +
                        "</style>");
            else if (thcc == 3)
                HTMLdata.append("<style type=\"text/css\">" +
                        "*{" +
                        "color:blue;" +
                        "}" +
                        "</style>");
            HTMLdata.append("</head>");
            if (theme == 0) {
                HTMLdata.append("<body>");
            }
            if (theme == 1) {
                HTMLdata.append("<body bgColor='#000000'>");
            } else if (theme == 2) {
                HTMLdata.append("<body bgColor='#FFFFFF'>");
            } else if (theme == 3) {
                HTMLdata.append("<body bgColor='#808080'>");
            }

            urlid = MyAppClass.findnum(oriurl, "id=", getBaseContext());
            System.out.println(oriurl);
            //System.out.println(urlid);
            try {
                cp = Integer.parseInt(MyAppClass.findnum(oriurl, "chapter=", getBaseContext()));
            } catch (NumberFormatException e) {
                publishProgress("-2");
                Log.e("error", oriurl);
                Log.e("error", "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข");
                finish();
            }
            final String nav_code = "<div  align='center' style='display: table; margin: 0 auto;'>" + "<table cellpadding='10' cellspacing='0' border='0'>" +
                    "<tbody><tr><td align='center'><font style='font-family:Tahoma;font-size:14px'>" +
                    "<a href='viewlongc.php?id=" + urlid + "&amp;chapter=" + Integer.toString(cp - 1) + "'> <img src='/a/writer/pic/ba.gif' width='16' height='16' align='absbottom' border='0'> ตอนก่อนหน้า</a> |" +
                    "<a href='view.php?id=" + urlid + "'><img src='/a/writer/pic/ho.gif' width='16' height='16' align='absbottom' border='0'><b> สารบัญ</b> </a> | " +
                    "<a href='viewlongc.php?id=" + urlid + "&amp;chapter=" + Integer.toString(cp + 1) + "'>ตอนถัดไป <b><img src='/a/writer/pic/ne.gif' width='16' height='16' align='absbottom' border='0'></b> </a>" +
                    " </font></td></tr></tbody></table></div>";

            if (doc.title() != null)
                HTMLdata.append("<br><h2 align='center'>").append(doc.title()).append("</h2><br><br><br>");
            //System.out.print("width: ");System.out.println(widthsc);
            {
                Elements story = doc.select("#story_body").select("td");
                HTMLdata.append("<div id='story_body' style='display: block; margin: 0 auto; width: 98%; word-wrap: break-word;'>");
                if (story != null && story.first() != null) {
                    if (story.select("div") != null) if (story.select("div").last() != null)
                        story.select("div").last().remove();
                    if (thcc == 1 || thcc == 0)
                        HTMLdata.append(story.first().html().replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>"));
                    else if (thcc == 2)
                        HTMLdata.append(story.first().html().replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>").replace("color: black", "color: white").replace("color: rgb(0, 0, 0)", "color: rgb(255, 255, 255)"));
                    else if (thcc == 3)
                        HTMLdata.append(story.first().html().replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>").replace("color: black", "color: blue").replace("color: rgb(0, 0, 0)", "color: rgb(0, 0, 255)"));
                    //ttstext = story.text();

                } else {
                    HTMLdata.append("<br><h2> ไม่พบตอนนี้  </h2><br><br><br>");
                }
                publishProgress("50 %");

                HTMLdata.append("</div>");

                //System.out.println(MyAppClass.findnum(oriurl, "chapter=", getBaseContext()));

                HTMLdata.append(nav_code);


                {

                    Elements ment = doc.select("table[style=BORDER-COLLAPSE: collapse][border=2]");
                    if (ment != null) {
                        HTMLdata.append("<span></span><div id='comment33' style='float: center; display: inline; margin: auto; width: 90%; word-break: break-all;'>");
                        ment.select("img").remove();
                        ment.select("a[onclick]").remove();
                        for (Element per : ment) {
                            if (Setting.getCommentSort(getApplicationContext()).equals("0")) {
                                ArrayList<String> comment = new ArrayList<String>();
                                if (thcc == 0)
                                    comment.add(0, "<br><br>" + per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("#ffffff", "#0").replace("Email / Msn: ", "").replace("(แอท)", "@") + "<br><br>");
                                else if (thcc == 1)
                                    comment.add(0, "<br><br>" + per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replaceAll("#[0-9A-F]{6}", "#000000") + "<br><br>");
                                else if (thcc == 2)
                                    comment.add(0, "<br><br>" + per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replace("color: rgb(0, 0, 0)", "color: rgb(255, 255, 255)").replaceAll("#[0-9A-F]{6}", "#FFFFFF") + "<br><br>");
                                else if (thcc == 3)
                                    comment.add(0, "<br><br>" + per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replaceAll("#[0-9A-F]{6}", "#0000FF").replace("color: rgb(0, 0, 0)", "color: rgb(0, 0, 255)") + "<br><br>");

                                for (String temp : comment) {
                                    HTMLdata.append(temp);
                                    //System.out.println(temp);
                                }

                            } else {
                                if (thcc == 0)
                                    HTMLdata.append("<br><br>").append(per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("#ffffff", "#0").replace("Email / Msn: ", "").replace("(แอท)", "@")).append("<br><br>");
                                else if (thcc == 1)
                                    HTMLdata.append("<br><br>").append(per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replaceAll("#[0-9A-F]{6}", "#000000")).append("<br><br>");
                                else if (thcc == 2)
                                    HTMLdata.append("<br><br>").append(per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replace("color: rgb(0, 0, 0)", "color: rgb(255, 255, 255)").replaceAll("#[0-9A-F]{6}", "#FFFFFF")).append("<br><br>");
                                else if (thcc == 3)
                                    HTMLdata.append("<br><br>").append(per.html().replace("width=\"75\"", "").replace("width=\"700\"", "").replace("Email / Msn: ", "").replace("(แอท)", "@").replaceAll("#[0-9A-F]{6}", "#0000FF").replace("color: rgb(0, 0, 0)", "color: rgb(0, 0, 255)")).append("<br><br>");
                            }
                        }


                        //page's comment choice
                        Elements page = doc.select("table[width=700]");
                        if (page != null && page.first() != null) {
                            HTMLdata.append("<br><br><div  align='center' style='display: block; margin: 0 auto;'>").append(page.first().html()).append("</div>");
                            //System.out.println(page.first().html());
                        }
                        HTMLdata.append("</div><br><br><span></span>");
                    }

                }
                {
                    HTMLdata.append("<span></span><span></span><br>").append(nav_code);
                    HTMLdata.append("<br><span></span><span></span><span></span><div id='postcomment' style='margin: 0 auto; " + "width: 80%; text-align:center;' >");
                    HTMLdata.append(
                            "<script language='javascript' type='text/javascript'> function stripHTML(txt){\n" +
                                    "var re= /<\\S[^><]*>/g;\n	return txt.replace(re, '');\n}\n" +
                                    "	function submitFormNo(){\n		var err = '';\n		var  msg = stripHTML(document.mainForm.t_msg.value);\n" +
                                    "		var len_msg = msg.length;\n		if (len_msg < 6 || len_msg > 10000) {\n" +
                                    "		err+='   - ข้อความที่โพสจะต้องไม่น้อยกว่า 6 ตัวอักษรและไม่เกิน 10,000 ตัวอักษร';		}\n" +
                                    "		if (document.mainForm.t_mem[0].checked==1 && (document.mainForm.t_username.value=='' || document.mainForm.t_password.value=='')) {\n" +
                                    "			err+='   - Login name/Password';\n		}\n" +
                                    "		if (document.mainForm.t_mem[1].checked==1 && document.mainForm.t_name.value=='') {			err+='\\n   - กรอกชื่อด้วยนะ';		}\n" +
                                    "		if (err != '') {\n			err ='_____________________________\\n' +			'กรอกข้อมูลในช่องต่อไปนี้ไม่ครบ\\nหรือข้อมูลผิดพลาดครับ :\\n' +" +
                                    "			err + '\\n_____________________________' +			'\\nช่วยกรอกอีกครั้งนะครับ';\n			alert(err);\n			return false;\n" +
                                    "		} else {\n			return true;\n		}\n	}\n" +
                                    "  </script>");
                    Elements page = doc.select("form[name=mainForm]");
                    if (page != null && page.first() != null) {
                        Elements page3 = page.first().select("p");
                        if (page3 != null) page3.remove();
                        HTMLdata.append("<br><br>").append(page.first().outerHtml().replace("width=\"97%\"", "width=\"50%\"").replace("&nbsp;", "").replace("size=\"-1\"", "size=\"3\"").replace("<form", "<form style='margin: 0 auto;'accept-charset=\"tis-620\" style='margin: 0 auto; " + "width: 100%;'").replace("ชื่อ* ", "<br>ชื่อ* ").replace("รูปตัวแทน ", "<br>รูปตัวแทน ").replace("email <input", "<br>email <input").replace("Password", "<br>Password").replace("Login name", "<br>Login name").replace("ROWS=\"10\" COLS=\"100\"", "ROWS=\"20\" COLS=\"30\"").replace("700", "100%").replace("550", "95%").replace("width:670px;", "")).append("<br><br>");
                    }
                }
            }
            Log.d("scvalue", Integer.toString(scRoll));
            HTMLdata.append("</div><span></span><h4>ข้อตกลง &amp; เงื่อนไขการใช้งาน</h4><br><ul><li><p>กรณีที่ผลงานชิ้นนี้เป็นผลงานที่แต่งโดยผู้ลงผลงานเอง ลิขสิทธิ์ของผลงานนี้จะ<br>เป็นของผู้ลงผลงานโดยตรง ห้ามมิให้คัดลอก ทำซ้ำ เผยแพร่ ก่อนได้รับอนุญาต<br>" +
                    "จากผู้ลงผลงาน</p></li><li><p>กรณีที่ผลงานชิ้นนี้กระทำการคัดลอก ทำซ้ำ มาจากผลงานของบุคคลอื่นๆ ผู้ลง<br>ผลงานจะต้องทำการอ้างอิงอย่างเหมาะสม และต้องรับผิดชอบเรื่องการจัดการ<br>ลิขสิทธิ์แต่เพียงผู้เดียว</p></li><li><p>ข้อความและรูปภาพที่ปรากฏอยู่ในผลงานที่ท่านเห็นอยู่นี้ เกิดจากการส่งเข้าระบบ<br>" +
                    "โดยอัตโนมัติจากบุคคลทั่วไป ซึ่งเด็กดีดอทคอมมิได้มีส่วนร่วมรู้เห็น ตรวจสอบ <br>หรือพิสูจน์ข้อเท็จจริงใดๆ ทั้งสิ้น ผู้ใดพบเห็นการลงผลงานละเมิดลิขสิทธิ์ หรือ<br>ไม่เหมาะสมโปรดแจ้งผู้ดูแลระบบเพื่อดำเนินการทันที<br>" +
                    "Email: <span>contact@dek-d.com</span> ( ทุกวัน 24 ชม ) หรือ<br>Tel: <span>0-2860-1142</span> ( จ-ศ 0900-1800 )</p></li></ul>" +
                    "</div></body></html>");

            publishProgress("80 %");
            doc = null;
            try {
                //ContextWrapper cw = new ContextWrapper(getBaseContext());
                temp = new File(/*cw.getDir("temp", Context.MODE_PRIVATE)*/Environment.getExternalStorageDirectory(), "niyay_temp");
                //System.out.println(temp.getAbsolutePath());

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                //bw.write(text1.replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
                bw.write(HTMLdata.toString());
                bw.flush();
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
}
