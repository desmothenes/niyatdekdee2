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

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Bitmap;

public class DekdeeBrowserActivity extends Activity implements
TextToSpeech.OnInitListener, OnUtteranceCompletedListener  {
	private TextToSpeech tts;
	private WebView webView;
	private String url;
	private ImageButton btnOk;
	private Document doc;
	private File temp;
	private boolean isSpeak;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_dekdee_browser);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);
			TextView title = (TextView) findViewById(R.id.textViewOk);
			title.setTextSize(15);
			title.setText(String.format(" %s",intent.getStringExtra("title")));


			btnOk = (ImageButton)findViewById(R.id.imageButton1);
			btnOk.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
			//btnOk.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					tts.setOnUtteranceCompletedListener(DekdeeBrowserActivity.this);
					if (isSpeak) 
						tts.stop();
					else 
						speak(TTS(totext()));
				}				
			});

			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					editor.putInt(String.format("scroll %s",url), webView.getScrollY());
					Log.e(String.format("scroll %s",url), Integer.toString(webView.getScrollY()));
					editor.commit();
					finish();
				}
			});
		}

		tts = new TextToSpeech(this, this);



		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

		webView.setWebViewClient(new WebViewClient() {
			ProgressDialog prDialog;
			@Override
			public void onPageStarted(WebView view, String url,Bitmap favicon) {
				prDialog = ProgressDialog.show(DekdeeBrowserActivity.this, null, "loading, please wait...");
				super.onPageStarted(view, url, favicon);
			}
			@Override  
			public void onPageFinished(WebView view, String url2) {
				prDialog.dismiss();
				super.onPageFinished(webView, url2);
				SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
				webView.scrollTo(0,prefs.getInt(String.format("scroll %s",url), 0));	
				Log.e(String.format("scroll %s",url), Integer.toString(prefs.getInt(String.format("scroll %s",url), 0)));
			}  
			/*
		    @Override
		    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
		    }*/
		});

		ContextWrapper cw = new ContextWrapper(this);
		temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),intent.getStringExtra("id")+".html");
		/*		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
		bw.write(doc.html().replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
		bw.flush();
		bw.close();*/
		System.out.println(temp.getAbsolutePath());
		webView.setFocusableInTouchMode(false);
		webView.setFocusable(false);
		//webView.loadUrl("file:///" + temp.getAbsolutePath());
		webView.loadUrl(url);

	}

	@Override
	public void onBackPressed() {
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt(String.format("scroll %s",url), webView.getScrollY());
		editor.commit();
		System.out.println(webView.getScrollY());
		finish();
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_dekdee_browser, menu);
		return true;
	}

	String totext() {
		dialog = ProgressDialog.show(DekdeeBrowserActivity.this,"Loading", "Please Wait...",true);

		try {
			doc = Jsoup.parse(temp, "tis620");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
		tts.setLanguage (new Locale( "tha", "TH"));
		isSpeak = true;
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

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(new Locale( "tha", "TH"));

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				btnOk.setEnabled(true);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	@Override
	public void onUtteranceCompleted(String arg0) {
		// TODO Auto-generated method stub
		isSpeak = false;

		dialog.dismiss();
	}
}
