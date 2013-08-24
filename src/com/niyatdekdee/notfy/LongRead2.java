package com.niyatdekdee.notfy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LongRead2 extends Activity {
    public static EditText webView;
    public Dialog dialog;
    public static String url;
    public static ProgressDialog prodialog;
    private Long_doback work;
    private GoogleAnalytics mGaInstance;
    private Tracker mGaTracker;
    private int scRoll;
    private int sclight;
    private Handler mHandler = new Handler();
    private boolean roll_flag = false;
    private int currentPositon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Setting.get_FullCheck(getApplicationContext())) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        if (!roll_flag && Setting.getScreenSetting(getApplicationContext()).equals("1")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            scRoll = -1;
        }
        mGaInstance = GoogleAnalytics.getInstance(this);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker("UA-37746897-1");
        if (sclight > 0) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = sclight / 100.0f;
            getWindow().setAttributes(lp);
        }

        webView = new EditText(this);
        final int theme = Integer.parseInt(Setting.getBgColorSetting(getApplicationContext()));
        if (theme == 2) {
            webView.setBackgroundColor(0xFFFFFFFF);
        } else if (theme == 3) {
            webView.setBackgroundColor(0xFF808080);
        } else if (theme == 4) {
            webView.setBackgroundColor(0xFFFFFFD8);
        } else {
            webView.setBackgroundColor(0);
        }

        setContentView(webView);
        webView.setKeyListener(null);
        webView.setLongClickable(false);
        webView.setGravity(Gravity.TOP);
        webView.setLineSpacing(1.5f, 1.75f);

        webView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
/*			        Toast toast = Toast.makeText(
                    getApplicationContext(),
		            "View touched", 
		            Toast.LENGTH_LONG
		        );
		        toast.show();
		        System.out.println(event.getX());
		        System.out.println(event.getY());
		        System.out.println(event.getDownTime()-SystemClock.uptimeMillis());
		        System.out.println(event.getEventTime()-SystemClock.uptimeMillis());*/
                return false;
            }
        });
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_long_read);
        dialog.setTitle("เลือกช่วงตอน");
        dialog.setCancelable(false);
        Button dialogButton = (Button) dialog.findViewById(R.id.longbutton1);
        final EditText start = (EditText) dialog.findViewById(R.id.longeditText1);
        final EditText end = (EditText) dialog.findViewById(R.id.longeditText2);
        end.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (work != null && work.getStatus() == AsyncTask.Status.RUNNING) return false;

                    try {
                        prodialog = new ProgressDialog(LongRead2.this);
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

                        if (dialog.isShowing()) dialog.dismiss();
                        work.execute(getBaseContext());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getBaseContext(), "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข", Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                    return true;
                }
                return false;
            }
        });

        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (work != null && work.getStatus() == AsyncTask.Status.RUNNING) return;
                if (dialog.isShowing()) dialog.dismiss();
                try {
                    prodialog = new ProgressDialog(LongRead2.this);
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

        if (intent.getStringExtra("from") != null) {
            if (intent.getStringExtra("from").equals("cp")) {
                try {
                    prodialog = new ProgressDialog(LongRead2.this);
                    prodialog.setMessage("Please Wait...\nถ้ารู้สึกช้า เกินกวา 30 วินาทีออกแล้วเข้าใหม่");
                    prodialog.setCancelable(true);
                    final int cp = Integer.parseInt(intent.getStringExtra("cp"));
                    work = new Long_doback(cp, cp);
                    work.execute(getBaseContext());
                    start.setText(Integer.toString(cp));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            } else if (intent.getStringExtra("from").equals("ncp")) {
                if (!roll_flag) {
                    int cp = Integer.parseInt(intent.getStringExtra("cp"));
                    start.setText(Integer.toString(cp));
                    dialog.show();
                }
            }
        } else {
            if (!roll_flag)
                dialog.show();
        }
/*		webView.setOnTouchListener(new View.OnTouchListener() { 

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int cursorPosition = webView.getSelectionStart();
				CharSequence enteredText = webView.getText().toString();
				CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());

				return false;
			}
		});*/
    }

    /*	private int getCurrentCursorLine()
        {
            int selectionStart = Selection.getSelectionStart(webView.getText());
            Layout layout = webView.getLayout();

            if (!(selectionStart == -1)) {
                return layout.getLineForOffset(selectionStart);
            }

            return -1;
        }*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(webView);
        webView.setSelection(currentPositon);
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            dialog.show();
        }
    };

    private void touchSimu() {
        /*			// Obtain MotionEvent object
		long downTime = ;
		long eventTime = ;
		float x = ;
		float y = ;
		// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
		int metaState = 0;*/
        final MotionEvent motionEvent = MotionEvent.obtain(
                SystemClock.uptimeMillis() + 20,
                SystemClock.uptimeMillis() + 20,
                MotionEvent.ACTION_DOWN,
                10.0f,
                10.0f,
                0
        );
        webView.dispatchTouchEvent(motionEvent);
        final MotionEvent motionEvent2 = MotionEvent.obtain(
                SystemClock.uptimeMillis() + 20,
                SystemClock.uptimeMillis() + 20,
                MotionEvent.ACTION_UP,
                10.0f,
                10.0f,
                0
        );

        webView.dispatchTouchEvent(motionEvent2);
        motionEvent.recycle();
        motionEvent2.recycle();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //webView.scrollBy(webView.getScrollX(), webView.getScrollY()+webView.getHeight());
/*			System.out.println("getSelectionStart "+Integer.toString(webView.getSelectionStart()));
			System.out.println("getSelectionEnd "+Integer.toString(webView.getSelectionEnd()));
			if (webView.getSelectionEnd()+700 < webView.length())
				webView.setSelection(webView.getSelectionEnd()+700);
			else
				webView.setSelection(webView.length());*/
            if (webView.getScrollY() + webView.getHeight() - 40 < webView.getLayout().getHeight())
                webView.scrollTo(0, webView.getScrollY() + webView.getHeight() - 40);
            else
                webView.scrollTo(0, webView.getLayout().getHeight() - 1);
            touchSimu();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (webView.getScrollY() - webView.getHeight() + 20 > 0)
                webView.scrollTo(0, webView.getScrollY() - webView.getHeight() + 20);
            else
                webView.scrollTo(0, 0);
            touchSimu();
            return true;
        } /*else if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            Toast.makeText(getBaseContext(), "Pause TTS", Toast.LENGTH_LONG).show();
            return true;*/ else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                DekTTSActivity.tts.stop();
                DekTTSActivity.stop = true;
                DekTTSActivity.isSpeak = false;
                DekTTSActivity.tts.stop();
                DekTTSActivity.stop = true;
                DekTTSActivity.isSpeak = false;
                Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
                return true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(LongRead2.this);
            builder.setMessage("คุณต้องการที่จะ ?")
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
                            if (work != null)
                                work.cancel(true);
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

            return true;
        } /*else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            currentPositon = webView.getSelectionEnd();
            webView.setSelection(currentPositon,webView.getText().toString().indexOf(10,currentPositon));
            currentPositon = webView.getText().toString().indexOf(10,currentPositon);
            return true;
        } */ else {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(LongRead2.this);
                    builder.setMessage("ยังโหลดไม่คบครบทุกตอน ต้องการฟังแค่ที่มีหรือไม่")
                            .setCancelable(false)
                            .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                                        DekTTSActivity.tts.stop();
                                        DekTTSActivity.stop = true;
                                        DekTTSActivity.isSpeak = false;
                                    }
                                    Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                                    intent.putExtra("from", "longread2");
                                    DekTTSActivity.type = 4;
                                    DekTTSActivity.text = (String) webView.getText().toString().subSequence(webView.getSelectionStart(), webView.length());
                                    startService(intent);
                                }
                            })
                            .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak) {
                        DekTTSActivity.tts.stop();
                        DekTTSActivity.stop = true;
                        DekTTSActivity.isSpeak = false;
                    }
                    Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
                    intent.putExtra("from", "longread2");
                    DekTTSActivity.type = 4;
                    DekTTSActivity.text = (String) webView.getText().toString().subSequence(webView.getSelectionStart(), webView.length());
                    startService(intent);
                }
                return true;
            case R.id.reselect:
                dialog.setCancelable(true);
                dialog.show();
                return true;
            case R.id.light:
                ShowDialog();
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
                currentPositon = webView.getSelectionStart();
                scRoll = -scRoll;
                roll_flag = true;
                return true;
            case R.id.inctext:
                //เพิ่มขนาด font
                Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
                webView.setTextSize(TypedValue.COMPLEX_UNIT_PX, webView.getTextSize() + 1);
                return true;
            case R.id.dectext:
                //ลดขนาด font
                Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
                webView.setTextSize(TypedValue.COMPLEX_UNIT_PX, webView.getTextSize() - 1);
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


    private void ShowDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(LongRead2.this);
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


    private class Long_doback extends AsyncTask<Context, String, Long> {


        private int start;
        private int end;
        private StringBuilder HTMLdata = new StringBuilder();
        private int index;
        private StringBuilder HTMLdata2;
        private Document doc;

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
                publishProgress("2");
                return;
            }
            if (doc.title() != null) HTMLdata2.append("\r\n\r\n").append(doc.title()).append("\r\n\r\n");

            {
                Elements story = doc.select("#story_body").select("td");
                if (story != null && story.first() != null) {


                    if (story.select("div") != null) if (story.select("div").last() != null)
                        story.select("div").last().remove();
                    try {
                        //for (Element i : story.first().children()) {
                        //HTMLdata2.append(i.html().replace("\"", "\\\"").replace("\r\n", " ").replace('\n', ' ').replace('\r', ' ').replaceAll("(<img src=\"[a-zA-Z0-9:/.-]+\" />)", "<div style=\"overflow: scroll;\">$1</div>").replace("<table", "<div style=\"overflow: scroll;\"><table").replace("</table>", "</table></div>"));
                        HTMLdata2.append(Jsoup.parse(story.first().html().replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n"));
                        publishProgress("1", HTMLdata2.toString());
                        HTMLdata2 = new StringBuilder();
                        //}
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
                publishProgress("2");
                return;
            }

            if (doc.title() != null) HTMLdata.append("\r\n\r\n").append(doc.title()).append("\r\n\r\n");

            {
                Elements story = doc.select("#story_body").select("td");
                if (story != null && story.first() != null) {

                    if (story.select("div") != null) if (story.select("div").last() != null)
                        story.select("div").last().remove();
                    //HTMLdata.append(Jsoup.parse(story.first().html().replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n"));
                    final int thcc = Integer.parseInt(Setting.getTextColorSetting(getApplicationContext()));
                    if (thcc == 2 || thcc == 3) {
                        for (Element span : story.select("span")) {
                            if (thcc == 2)
                                try {
                                    span.wrap("<font color=\"#000000\"></font>");
                                } catch (IndexOutOfBoundsException ex) {

                                }
                            else if (thcc == 3)
                                try {
                                    span.wrap("<font color=\"#0000FF\"></font>");
                                } catch (IndexOutOfBoundsException ex) {

                                }
                        }
                    }


                    Elements color = story.select("span[style^=color");
                    for (Element span : color) {
                        if (!span.attr("style").isEmpty() && span.attr("style").length() > 0)
                            span.attr("style", span.attr("style").replace(":", "=\"").replace(";", "\""));
                        if (span.attr("style").length() > 0) {
                            try {
                                span.wrap("<font " + span.attr("style") + "></font>");
                            } catch (IndexOutOfBoundsException ex) {

                            }
                        }
                        //span.append("</font>");
                    }
                    Elements els = story.select("style");
                    for (Element e : els) {
                        e.remove();
                    }
                    els = story.select("script");
                    for (Element e : els) {
                        e.remove();
                    }
                    //removeComments(doc);
                    HTMLdata.append(story.first().html());
                }
            }


            doc = null;
            publishProgress("0");
        }

        /*        private void removeComments(Node node) {
                    for (int i = 0; i < node.childNodes().size();) {
                        Node child = node.childNode(i);
                        if (child.nodeName().equals("#comment"))
                            child.remove();
                        else {
                            removeComments(child);
                            i++;
                        }
                    }
                }*/
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

                int temp = webView.getSelectionStart();
                int temp2 = webView.getScrollY();
                System.out.println(temp);
                webView.append(progress[1]);
                if (temp != 0)
                    webView.setSelection(temp);
                else
                    webView.scrollTo(0, temp2);
            } else if (progress[0].equals("0")) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                if (prefs.getBoolean(url, true)) {
                    try {
                        URLImageParser p = new URLImageParser(webView, getBaseContext(), url);
                        webView.setText(Html.fromHtml(HTMLdata.toString(), p, null));
                        if (prodialog != null && prodialog.isShowing()) prodialog.dismiss();
                    } catch (Exception e1) {
                        webView.setText(Html.fromHtml(HTMLdata.toString()));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LongRead2.this);
                    builder.setMessage("ตอนที่จะเปิดเคยมีประวัติการใช้หน่วยความจำเกินกว่าเครื่องจะรับได้")
                            .setCancelable(false)
                            .setPositiveButton("ไม่แสดงภาพ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    webView.setText(Html.fromHtml(HTMLdata.toString()));
                                }
                            })
                            .setNegativeButton("แสดงภาพ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    URLImageParser p = new URLImageParser(webView, getBaseContext(), url);
                                    webView.setText(Html.fromHtml(HTMLdata.toString(), p, null));
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                try {
                    if (prodialog != null && prodialog.isShowing()) prodialog.dismiss();
                } catch (Exception e1) {
                }

            } else if (progress[0].equals("2")) {
                prodialog.setMessage("การเชื่อมต่อมีปัญหา");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                prodialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(LongRead2.this);
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
                                if (work != null)
                                    work.cancel(true);
                                finish();
                            }
                        })
                        .setNegativeButton("ลองใหม่", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (work != null)
                                    work.cancel(true);
                                mHandler.postDelayed(runnable, 1);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (prodialog != null) prodialog.show();
        }
    }
}
