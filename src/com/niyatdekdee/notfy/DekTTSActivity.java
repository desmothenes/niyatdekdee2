package com.niyatdekdee.notfy;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.IBinder;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;

public class DekTTSActivity extends Service implements  OnUtteranceCompletedListener,OnInitListener {

	static TextToSpeech tts;
	static boolean isSpeak = false;
	//private Intent intent;
	private ProgressDialog dialog;
	static int type = -1;
	static String text = "";
	static File temp;

	@Override
	public void onCreate() {
		Log.e("zone", "create");
		tts = new TextToSpeech(getApplicationContext(),this);
		
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
	}

	@Override
	public void onStart(Intent intent, int startId) {

	    Log.v("TTS", "onstart_service");
		tts = new TextToSpeech(getApplicationContext(),this);
		tts.speak("โปรดรอ", TextToSpeech.QUEUE_ADD, null); 
		//final int type = intent.getIntExtra("type", -1);
	    super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	void totext(final File temp) {	
		
		if (temp == null) Log.e("temp", "null file");

		if (temp == null) return;

		Log.e("zone", "end text file");
		Document doc = null;
		try {
			doc = Jsoup.parse(temp, "tis620");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} 
		Log.e("zone", "parse html");

		StringBuilder sum = new StringBuilder();
		if (doc == null) return;
		Elements link1 = doc.select("table[id*=story_body]").select("p");
		if(link1 == null) return;
		for (Element link : link1) {
			sum.append(link.text()+"/n");
			//System.out.println(link.text());
		}
		speak(SubText(sum.substring(0, sum.length() - 55).toString()));
	}

	protected static Document doc;

	void totext(final String url) {		
		new Thread() {
			public void run() {
				try {
					doc = Jsoup.connect(url).timeout(8000).get();
					if (doc == null) return;
					StringBuilder sum = new StringBuilder();
					Elements link1 = doc.select("table[id*=story_body]");
					for (Element link : link1) {
						sum.append(link.text());
						//System.out.println(link.text());
					}
					doc = null;
					if (sum.length() - 55 > 0)
						speak(SubText(sum.substring(0, sum.length() - 55).toString()));
				} catch (IOException e) {
					//Toast.makeText(MainActivity.context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
					Log.e("totext", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
					dialog.dismiss();
					Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}.start();			
	}	

	void strtotext(final String str) {		
		doc = Jsoup.parse(str);
		if (doc == null) return;
		StringBuilder sum = new StringBuilder();
		Elements link1 = doc.select("table[id*=story_body]");
		for (Element link : link1) {
			sum.append(link.text());
			//System.out.println(link.text());
		}
		doc = null;
		speak(SubText(sum.substring(0, sum.length() - 55).toString()));
	}

	static ArrayList<String> SubText(final String input) {
		Locale thaiLocale = new Locale("th");
		System.out.println("subtext");
		BreakIterator boundary = BreakIterator.getSentenceInstance(thaiLocale);
		boundary.setText(input);		 
		final ArrayList<String> cuttext = printEachForward(boundary, input);
		return cuttext;		
	}

	void speak(final ArrayList<String> cuttext) {
		tts.setSpeechRate((float)(Setting.getspeechspeed(getApplicationContext())/100.0));
		tts.setPitch((float) (Setting.getspeechpitch(getApplicationContext())/100.0));
		//tts.setLanguage (new Locale( "tha", "TH"));
		isSpeak = true;
		/*		final int from = intent.getIntExtra("from", -1);


		if (from == 1) {
			startActivity(new Intent(this, MainActivity.class));
		} else if (from == 2) {
			startActivity(new Intent(this, DekdeeBrowserActivity.class));
		}*/

		tts.speak(" ", TextToSpeech.QUEUE_FLUSH, null);
		for (String text : cuttext)
			tts.speak(text, TextToSpeech.QUEUE_ADD, null);
	}

	static ArrayList<String> printEachForward(final BreakIterator boundary,final String source) {
		ArrayList<String> arrayList=new ArrayList<String>();
		int start = boundary.first();
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {		 
			arrayList.add(source.substring(start, end));
		}		 
		return arrayList;
	}

	@Override
	public void onUtteranceCompleted(final String arg0) {
		// TODO Auto-generated method stub
		if (arg0.indexOf("ok")!=-1)
			return;
		isSpeak = false;
		dialog.dismiss();
	}
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(new Locale( "tha", "TH"));

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(getApplicationContext(), "TTS engine ของคุณไม่รองรับภาษาไทย", Toast.LENGTH_SHORT).show();
				Log.e("TTS", "Thai language is not supported on your TTS");
				MainActivity.isTTS = false;
			}	else {

				tts.speak("โปรดรอ", TextToSpeech.QUEUE_ADD, null); 
				//final int type = intent.getIntExtra("type", -1);
				if (type != -1) {
					dialog = new ProgressDialog(DekTTSActivity.this);
					dialog.setCancelable(true);
					dialog.setMessage("Loading");
					//dialog.show();
				}

				if (type == 1 && !text.equals(text)) {
					totext(text);
				} else if (type == 2 && temp != null) {
					totext(temp);
				} else if (type == 3 && !text.equals(text)) {
					strtotext(text);
				} 
			}
		} else {
			Toast.makeText(getApplicationContext(), "TTS Initilization Failed!", Toast.LENGTH_SHORT).show();
			Log.e("TTS", "Initilization Failed!");
			MainActivity.isTTS = false;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.w(" ibinder ","");
		return null;
	}
}
