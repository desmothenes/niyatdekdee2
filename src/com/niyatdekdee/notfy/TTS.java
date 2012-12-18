package com.niyatdekdee.notfy;
/*
import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;
*/
class TTS {// implements  OnUtteranceCompletedListener {
/*
	static TextToSpeech tts;
	static boolean isSpeak = false;	
	

	static void totext(final File temp) {	

		if (temp == null) return;

		Log.e("zone", "end text file");
		Document doc = null;
		try {
			doc = Jsoup.parse(temp, "tis620");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	static void totext(final String url) {		
		new Thread() {
			public void run() {
				try {
					doc = Jsoup.connect(url).timeout(8000).get();
				} catch (IOException e) {
					Toast.makeText(MainActivity.context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}.start();	
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

	static void strtotext(final String url) {		
		doc = Jsoup.parse(url);
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
		//System.out.println(input.substring(input.length()-50));
		BreakIterator boundary = BreakIterator.getSentenceInstance(thaiLocale);
		boundary.setText(input);		 
		final ArrayList<String> cuttext = printEachForward(boundary, input);
		return cuttext;		
	}

	static void speak(final ArrayList<String> cuttext) {
		tts.setSpeechRate((float)(Setting.getspeechspeed(MainActivity.context)/100.0));
		tts.setPitch((float) (Setting.getspeechpitch(MainActivity.context)/100.0));
		//tts.setLanguage (new Locale( "tha", "TH"));
		isSpeak = true;
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
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(new Locale( "tha", "TH"));

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				//btnOk.setEnabled(true);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	@Override
	public void onUtteranceCompleted(final String arg0) {
		// TODO Auto-generated method stub
		isSpeak = false;
		//dialog.dismiss();
	}*/

}
