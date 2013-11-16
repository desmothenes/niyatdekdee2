package com.niyatdekdee.notfy;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DekTTSActivity extends Service implements OnInitListener {

    static TextToSpeech tts;
    static boolean isSpeak = false;
    //private Intent intent;
    private ProgressDialog dialog;
    private TelephonyManager mgr;
    private PhoneStateListener phoneStateListener;
    private static boolean oninit = false;
    static boolean stop = false;

    static int type = -1;
    static String text = "";
    static File temp;

    @Override
    public void onCreate() {
        Log.e("zone", "create");
        if (tts == null)
            tts = new TextToSpeech(this, this);
        /*
        Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
		startActivityForResult(checkIntent, RESULT_OK);
		tts.speak("โปรดรอ", TextToSpeech.QUEUE_ADD, null); 

		intent = getIntent();
		final int type = intent.getIntExtra("type", -1);
		if (type != -1) {
			dialog = new ProgressDialog(DekTTSActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage("Loading");
			//dialog.show();
		}
		if (type == 1) {
			totext(intent.getStringExtra("text"));
		} else if (type == 2) {
			totext((File) intent.getSerializableExtra("file"));
		} else if (type == 3) {
			strtotext(intent.getStringExtra("text"));
		} */

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    DekTTSActivity.tts.stop();
                    DekTTSActivity.stop = true;
                    DekTTSActivity.isSpeak = false;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };


        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(getBaseContext(), "โปรดรอสักครู่ กำลังประมวลผล อาจนานถึง 20 วินาทีในการเริ่มต้น\nสามารถกด Back เพื่อหยุด TTS ได้", Toast.LENGTH_LONG).show();
        super.onStart(intent, startId);
        Log.v("TTS", "onstart_service");
        if (oninit && MainActivity.isTTS) {
            Log.v("TTS", "non oninit");

            if (type != -1) {
                dialog = new ProgressDialog(DekTTSActivity.this);
                dialog.setCancelable(true);
                dialog.setMessage("Loading");
                //dialog.show();
            }

            if (type == 1) {
                totext(text);
            } else if (type == 2 && temp != null) {
                totext(temp);
            } else if (type == 3) {
                strtotext(text);
            } else if (type == 4) {
                tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, null);
                speak(SubText(text));
            } else if (type == 5) {
                lltotext(text);
            } else if (type == 99) {
                tts.speak("ยินดีต้อนรับ", TextToSpeech.QUEUE_FLUSH, null);
            } else {
                Log.e("zone", "error");
                System.out.println(type);
            }
        } else {
            System.out.println("oninit " + oninit);
            System.out.println("MainActivity.isTTS " + MainActivity.isTTS);
            Log.v("TTS", "non oninit error");
            if (!oninit) return;
            Intent intent1 = new Intent(getApplicationContext(), Flow2.class);
            intent1.putExtra("from", "main");
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
        //tts = new TextToSpeech(getApplicationContext(),this);
        //tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, null);
        //final int type = intent.getIntExtra("type", -1);
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    void totext(final File temp) {

        if (temp == null) Log.e("temp", "null file");

        if (temp == null) return;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");
        tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, params);
        Log.e("zone", "end text file");
        Document doc = null;
        try {
            doc = Jsoup.parse(temp, "tis620");
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        Log.e("zone", "parse html");

        StringBuilder sum = new StringBuilder();
        if (doc == null) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Elements link1 = doc.select("table[id*=story_body]").select("p");

        if (link1 == null) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            return;
        }/* else {
            System.out.println(doc.select("table[id*=story_body]").html());
		}*/
        for (Element link : link1) {
            sum.append(link.text()).append("\n");
            //System.out.println(link.text());
        }
        //System.out.println(sum.substring(sum.length() - 55, sum.length()).toString());
        if (sum.length() < 5) {
            sum.append(Jsoup.parse((doc.select("table[id*=story_body]").html()).replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n")).append("\n");
        }
        speak(SubText(sum.toString()));
    }

    protected static Document doc;

    void totext(final String url) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");
        tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, params);
        new Thread() {
            public void run() {
                try {
                    Log.e("zone", "end url");
                    doc = Jsoup.connect(url).timeout(8000).get();
                    if (doc == null) {
                        Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuilder sum = new StringBuilder();
                    Elements link1 = doc.select("table[id*=story_body]").select("p");
                    if (link1 == null) {
                        Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        for (Element link : link1) {
                            sum.append(link.text()).append("\n");
                            //			System.out.println(link.text());
                        }
                    }
                    if (sum.length() < 5) {
                        sum.append(Jsoup.parse((doc.select("table[id*=story_body]").html()).replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n")).append("\n");
                    }
                    doc = null;
                    speak(SubText(sum.toString()));
                } catch (IOException e) {
                    //Toast.makeText(MainActivity.context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                    Log.e("totext", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                    if (dialog.isShowing()) dialog.dismiss();
                    //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void strtotext(final String str) {
        Log.e("zone", "strtotext");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");
        tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, params);
        //Log.e("str", str);

        doc = Jsoup.parse(str);
        if (doc == null) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            return;
        }

		/*		for(Node n: doc.childNodes()){
            if(n instanceof Comment){
				n.remove();
			}
		}*/

        //removeComments(doc);
        //Log.e("str1", doc.html());
        StringBuilder sum = new StringBuilder();
        Elements link1 = doc.select("#story_body").select("p");
        if (link1 == null) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Element link : link1) {
            sum.append(link.text()).append("\n");
            System.out.println(link.text());
        }
        if (sum.length() < 5) {
            sum.append(Jsoup.parse((doc.select("#story_body").html()).replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n")).append("\n");
            //Log.e("sum", sum.toString());
        }
        doc = null;
        //Log.e("sum", sum.toString());
        //System.out.println(sum.substring(sum.length() - 55, sum.length()).toString());
        speak(SubText(sum.toString()));
    }


    void lltotext(final String str) {
        Log.e("zone", "lltotext");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");
        tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, params);
        //Log.e("str", str);

        doc = Jsoup.parse(str);
        if (doc == null) {
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Node n : doc.childNodes()) {
            if (n instanceof Comment) {
                n.remove();
            }
        }

        //removeComments(doc);
        Log.e("str1", doc.html());
        StringBuilder sum = new StringBuilder();
        /*Elements link1 = doc.select("#story_body");
        if(link1 == null)  {
			Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
			return;
		}*/
        try {
            sum.append(Jsoup.parse((doc.select("#story_body").html()).replace("</div>", "</div>br2n").replace("</p>", "</p>br2n").replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n\r\n")).append("\n");
        } catch (Exception anfe) {
            Log.e("error", "sum.append(Jsoup.parse((doc.select(\"table[id*=story_body]\")");
            Toast.makeText(getApplicationContext(), "IOException Failed!", Toast.LENGTH_SHORT).show();
        }
        //Log.e("sum", sum.toString());

        doc = null;
        Log.e("sum", sum.toString());
        //System.out.println(sum.substring(sum.length() - 55, sum.length()).toString());
        speak(SubText(sum.toString()));
    }

    static ArrayList<String> SubText(String input) {
        StringBuffer newnum = new StringBuffer();
        Pattern rexexp = Pattern.compile("\\.\\d+");
        Matcher matcher = rexexp.matcher(input);
        while (matcher.find()) {
            //System.out.println("in: "+matcher.group(0));
            matcher.appendReplacement(newnum, floatDigit(matcher.group(0).replace(",", "")));
        }
        matcher.appendTail(newnum);
        input = newnum.toString();
        rexexp = Pattern.compile("\\d+(\\d{0,2})(,\\d{3})*");
        newnum = new StringBuffer();
        matcher = rexexp.matcher(input);
        while (matcher.find()) {
            //System.out.println("in: "+matcher.group(0));
            matcher.appendReplacement(newnum, conto(matcher.group(0).replace(",", "")));
        }
        matcher.appendTail(newnum);
        input = newnum.toString();
        if (tts.getDefaultEngine().contains("vaja")) {
            while (input.contains(" ๆ")) {
                input = input.replace(" ๆ", "ๆ");
            }
            List<String> temp = new ArrayList<String>(Arrays.asList(input.split("\\s+")));
            temp.add("หมดข้อความที่จะอ่านออกเสียง");
            return new ArrayList<String>(temp);
        } else {
            Locale thaiLocale = new Locale("th");
            System.out.println("subtext");
            System.out.println("input");
            BreakIterator boundary = BreakIterator.getSentenceInstance(thaiLocale);
            input += "\n\nหมดข้อความที่จะอ่านออกเสียง";
            boundary.setText(input);
            return printEachForward(boundary, input);
        }
    }
/*	static ArrayList<String> SubText2(final String input) {

		System.out.println("subtext2");
		System.out.println("input");
		ArrayList<String> cuttext = new ArrayList<String>();
		int dot1 = 0;
		int dot2 = input.indexOf(" ",dot1 + 1);
		while (dot1 != -1 && dot2 != 1) {
			cuttext.add(input.substring(dot1, dot2));
			System.out.println(cuttext.get(cuttext.size()-1));
			dot1 = input.indexOf(" ",dot2+1);
			dot2 = input.indexOf(" ",dot1 + 1);
		}
		cuttext.add(input.substring(dot1));
		return cuttext;		
	}*/

    void speak(final ArrayList<String> cuttext) {
        Log.e("zone", "cuttext");
        while (DekTTSActivity.tts.isSpeaking()) {
            DekTTSActivity.tts.stop();
            tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
            try {
                Thread.sleep(1000);
                //System.out.println(isSpeak);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        DekTTSActivity.stop = false;
        tts.setSpeechRate((float) (Setting.getspeechspeed(getApplicationContext()) / 100.0));
        tts.setPitch((float) (Setting.getspeechpitch(getApplicationContext()) / 100.0));
        //for (String i:cuttext) Log.e( "cuttext",i);
        //tts.setLanguage (new Locale( "tha", "TH"));
        /*		final int from = intent.getIntExtra("from", -1);


		if (from == 1) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (from == 2) {
			startActivity(new Intent(this, DekdeeBrowserActivity.class));
		}*/

        //tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
        if (tts.getDefaultEngine().contains("vaja")) {
            AsyncTask<Void, String, Void> execute = new AsyncTask<Void, String, Void>() {

                private NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                private int i;

                @Override
                protected Void doInBackground(Void... arg0) {
                    i = 0;
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");
                    for (String text : cuttext) {
                        Log.e("cuttext", text);
                        while (text.contains("..")) {
                            text = text.replaceAll("\\.\\.", " ");
                        }
                        text = text.replaceAll("…", " ").trim();
                        i++;

                        text = text.trim();

                        while (isSpeak)
                            try {
                                Thread.sleep(1000);
                                //System.out.println(isSpeak);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        isSpeak = true;
                        //handler.sendMessage(handler.obtainMessage(0,Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)));
                        try {
                            tts.speak(text, TextToSpeech.QUEUE_ADD, params);
                        } catch (Exception anfe) {
                            //System.out.println(anfe.getMessage());
                        }
                        if (Setting.getTTSToast(getApplicationContext())) {
                            publishProgress(text);
                        }

                        if (stop) {
                            tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
                            isSpeak = false;
                            stop = false;
                            cuttext.clear();
                            return null;
                        }

                    }
                    isSpeak = false;
                    stop = false;
                    if (manager != null)
                        manager.cancel(1);
                    return null;
                }

                protected void onProgressUpdate(String... progress) {        //publishProgress
                    if (!stop) {
                        //Toast.makeText(getBaseContext(), progress[0] , Toast.LENGTH_LONG).show();
                        Notification notification;
                        if (i == 1)
                            notification = new Notification(R.drawable.noti, progress[0], System.currentTimeMillis());
                        else if (i == 2) {

                            return;
                        } else
                            notification = new Notification(R.drawable.noti, progress[0], System.currentTimeMillis());

                        //notification.defaults |= Notification.DEFAULT_SOUND;
                        // Hide the notification after its selected
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        // The PendingIntent will launch activity if the user selects this notification


                        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), 0);
                        notification.contentIntent = contentIntent;
                        ///notification.contentView = contentView;
                        notification.setLatestEventInfo(getBaseContext(), "TTS", "Speking", contentIntent);
                        manager.notify(1, notification);
                    }

                }

                protected void onPostExecute(Void result) {
                    isSpeak = false;
                    stop = false;
                    if (manager != null)
                        manager.cancel(1);
                }
            }.execute();
        } else {
            new AsyncTask<Void, String, Void>() {

                private NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                private CharSequence delay = "";
                private int i;

                @Override
                protected Void doInBackground(Void... arg0) {
                    i = 0;
                    HashMap<String, String> params = new HashMap<String, String>();
                    for (String text : cuttext) {
                        //Log.e( "cuttext",text);

                        while (text.contains("..")) {
                            text = text.replaceAll("\\.\\.", " ");
                        }
                        text = text.replace(" ๆ", "ๆ").replace("ปล.", "ปัจฉิมลิขิต").replace("ช.ม.", "ชั่วโมง").replace("อก.", "อก .").replace("มอก .", "มอก.").replace("[", "ก้ามปูเปิด").replace("]", "ก้ามปูปิด").replace("[", "ก้ามปูเปิด").replace(":", "ต่อ").replace("+", "บวก").trim();
                        final int size = text.length();
                        if (size < 2) continue;
                        i++;
                        if (i == 1) {
                            delay = text;
                        }
                        if (i == 2) {
                            if (size > 500) {
                                if (Setting.getTTSToast(getApplicationContext())) {
                                    publishProgress(text);
                                }
                                String[] subtext = text.split(" ");
                                for (String aSubtext : subtext) {
                                    tts.speak(aSubtext.trim(), TextToSpeech.QUEUE_ADD, params);
                                    // if (Setting.getTTSToast(getApplicationContext())) {
                                    //publishProgress(subtext[index]); }
                                }

                            } else {
                                tts.speak(text.trim(), TextToSpeech.QUEUE_ADD, params);
                                if (Setting.getTTSToast(getApplicationContext())) {
                                    publishProgress(text);
                                }
                            }
                        } else if (size > 500) {
                            for (String subtext : text.split(" ")) {
                                subtext = subtext.trim();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");

                                while (isSpeak)
                                    try {
                                        Thread.sleep(1000);
                                        //System.out.println(isSpeak);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                isSpeak = true;
                                //handler.sendMessage(handler.obtainMessage(0,Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)));
                                try {
                                    tts.speak(subtext, TextToSpeech.QUEUE_ADD, params);
                                } catch (Exception anfe) {
                                    //System.out.println(anfe.getMessage());
                                }
                                if (Setting.getTTSToast(getApplicationContext())) {
                                    publishProgress(subtext);
                                }

                                if (stop) {
                                    tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
                                    isSpeak = false;
                                    cuttext.clear();
                                    return null;
                                }
                            }
                        } else {
                            text = text.trim();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "complete");

                            while (isSpeak)
                                try {
                                    Thread.sleep(1000);
                                    //System.out.println(isSpeak);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            isSpeak = true;
                            //handler.sendMessage(handler.obtainMessage(0,Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)));
                            try {
                                tts.speak(text, TextToSpeech.QUEUE_ADD, params);
                            } catch (Exception anfe) {
                                //System.out.println(anfe.getMessage());
                            }
                            if (Setting.getTTSToast(getApplicationContext())) {
                                publishProgress(text);
                            }

                            if (stop) {
                                tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
                                isSpeak = false;
                                stop = false;
                                cuttext.clear();
                                return null;
                            }
                        }
                    }
                    isSpeak = false;
                    stop = false;
                    if (manager != null)
                        manager.cancel(1);
                    return null;
                }

                protected void onProgressUpdate(String... progress) {        //publishProgress
                    if (!stop) {
                        //Toast.makeText(getBaseContext(), progress[0] , Toast.LENGTH_LONG).show();
                        Notification notification;
                        if (i == 1)
                            notification = new Notification(R.drawable.noti, progress[0], System.currentTimeMillis());
                        else if (i == 2) {
                            delay = progress[0];
                            return;
                        } else
                            notification = new Notification(R.drawable.noti, delay, System.currentTimeMillis());

                        //notification.defaults |= Notification.DEFAULT_SOUND;
                        // Hide the notification after its selected
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;

                        // The PendingIntent will launch activity if the user selects this notification


                        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), 0);
                        notification.contentIntent = contentIntent;
                        ///notification.contentView = contentView;
                        notification.setLatestEventInfo(getBaseContext(), "TTS", "Speking", contentIntent);
                        manager.notify(1, notification);
                    }
                    delay = progress[0];
                }

                protected void onPostExecute(Void result) {
                    isSpeak = false;
                    stop = false;
                    if (manager != null)
                        manager.cancel(1);
                }
            }.execute();
        }
        //MainActivity.dialog.dismiss();
    }

    static ArrayList<String> printEachForward(final BreakIterator boundary, final String source) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            arrayList.add(source.substring(start, end));
        }
        return arrayList;
    }

    /*
    @Override
    public void onUtteranceCompleted(final String arg0) {
        Log.e("onUtteranceCompleted", arg0);

        if (arg0.indexOf("ok")!=-1)
            return;
        isSpeak = false;
        dialog.dismiss();
    }*/
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("tha", "TH"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "TTS engine ในเครื่องของคุณไม่รองรับภาษาไทย", Toast.LENGTH_SHORT).show();
                Log.e("TTS", "Thai language is not supported on your TTS");
                MainActivity.isTTS = false;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("ttsinstall", false);
                editor.commit();
            } else {
                oninit = true;
                //final int type = intent.getIntExtra("type", -1);
                if (type != -1) {
                    dialog = new ProgressDialog(DekTTSActivity.this);
                    dialog.setCancelable(true);
                    dialog.setMessage("Loading");
                    //dialog.show();
                }
                Log.e("TTS", "Initilization start! type " + Integer.toString(type));
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean("ttsinstall", true);
                editor.commit();
                if (type == 1) {
                    totext(text);
                } else if (type == 2 && temp != null) {
                    totext(temp);
                } else if (type == 3) {
                    strtotext(text);
                } else if (type == 4) {
                    tts.speak("โปรดรอ", TextToSpeech.QUEUE_FLUSH, null);
                    speak(SubText(text));
                } else if (type == 5) {
                    lltotext(text);
                } else if (type == 99) {
                    if (Setting.getTTStip(getApplicationContext())) {
                        tts.speak("ยินดีต้อนรับ", TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }

                tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                    @Override
                    public void onUtteranceCompleted(String utteranceId) {
                        isSpeak = false;
                        Log.e("onUtteranceCompleted", utteranceId);
                        //Toast.makeText(getBaseContext(), utteranceId, Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        } else {
            Toast.makeText(getApplicationContext(), "TTS Initilization Failed!", Toast.LENGTH_SHORT).show();
            Log.e("TTS", "Initilization Failed!");
            MainActivity.isTTS = false;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putBoolean("ttsinstall", false);
            editor.commit();
        }
        Intent intent = new Intent(this.getApplicationContext(), Flow2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.w(" ibinder ", "");
        return null;
    }

    final static String[] lek = {"ศูนย์", "หนึ่ง", "สอง", "สาม", "สี่", "ห้า", "หก", "เจ็ด", "แปด", "เก้า"};
    final static String[] luk = {"สิบ", "ร้อย", "พัน", "หมื่น", "แสน", "ล้าน"};

    static String floatDigit(String val) {
        StringBuilder text = new StringBuilder("จุด");
        val = val.replace(".", "");
        for (char i : val.toCharArray()) {
            text.append(lek[Character.getNumericValue(i)]);
        }

        //System.out.println("out: "+text);
        return text.toString();
    }

    static StringBuilder thainumtext(String val) {
        StringBuilder text = new StringBuilder();
        int i = 0, temp_int = 0;
        int n = val.length();
        while (n > 0) {
            temp_int = Integer.parseInt(Character.toString(val.charAt(i)));
            if (temp_int > 0) {
                if ((n == 1) && temp_int == 1)
                    text.append("เอ็ด");
                else if ((2 == n) && temp_int == 1)
                    ;
                else if ((2 == n) && temp_int == 2)
                    text.append("ยี่");
                else
                    text.append(lek[temp_int]);

                if (n > 1)
                    text.append(luk[n - 2]);
            }
            n--;
            i++;
        }
        return text;
    }

    static String conto(String val) {
        StringBuilder text = new StringBuilder();
        int n = val.length();
        if (n == 1) return lek[Integer.parseInt(val)];
        ArrayList<String> nlist = new ArrayList<String>();
        n = 6;
        StringBuilder temp = new StringBuilder();
        for (int index = val.length() - 1; index >= 0; index--) {
            temp.insert(0, val.charAt(index));
            n--;
            if (n == 0) {
                nlist.add(temp.toString());
                temp = new StringBuilder();
                n = 6;
            }
        }
        if (temp.length() != 0)
            nlist.add(temp.toString());

        n = 1;
        for (String i : nlist) {
            if (n == 1) {
                text.insert(0, thainumtext(i));
                n = 0;
            } else {
                text.insert(0, thainumtext(i) + "ล้าน");
            }
        }
        //System.out.println("out: "+text);
        return text.toString();
    }
}
