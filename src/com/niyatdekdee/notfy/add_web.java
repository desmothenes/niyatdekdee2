package com.niyatdekdee.notfy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class add_web extends Activity {
	WebView webView;
	Button addButton;
	final Activity activity = this;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.web_add);        
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadsImagesAutomatically(false);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress)            { 
				activity.setTitle("Loading..."); 
				activity.setProgress(progress * 100);
				if(progress == 100)                   
					activity.setTitle(R.string.app_name);
			}        
		});
		webView.setWebViewClient(new WebViewClient() {   
			@Override   
			public boolean shouldOverrideUrlLoading(WebView view, String url)   
			{   
				view.loadUrl(url); 
				return true; 

			}   
		});
		webView.loadUrl("http://www.dek-d.com/writer/frame.php");
		//webView.loadUrl("http://writer.dek-d.com/nanakosos/story/view.php?id=559528");
		addButton = (Button) findViewById(R.id.button1);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = webView.getUrl();
				Log.v("url", url);
				Intent i = new Intent(getBaseContext(),InsertForm.class);				
				String title = webView.getTitle();
				Log.v("title", title);
				if (title.contains(">"))
					title = title.substring(6, title.indexOf(">"));
				else
					title = title.substring(6);
				Log.v("title", title);
				i.putExtra("name",title);
				if (url.contains("chapter")) {
					i.putExtra("chapter",url.substring(url.indexOf("chapter=")+8));
					i.putExtra("title",webView.getTitle());
					//in this fomat http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=580483&chapter=   378
					url = url.substring(0,url.indexOf("chapter=")+8);
					Log.v("chapter", url.substring(url.indexOf("chapter=")+8));
				}
				else {
					//in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
					String stext = "id=";
					//หาหลักของตอน
					int start = url.lastIndexOf(stext)+stext.length();
					Log.v("url.length()", Integer.toString(url.length()));
					Log.v("start", Integer.toString(start));
					Log.v("stext.length()", Integer.toString(stext.length()));
					int len=0;
					Log.v("Character", Character.toString(url.charAt(start)));
					for (int i3 = start;i3 < url.length() && Character.isDigit(url.charAt(i3));i3++) {						
						len++;
						Log.v("Character", Character.toString(url.charAt(i3)));
					}
					Log.v("len", Integer.toString(len));
					String unum = url.substring(start,start+len);	
					Log.v("unum", unum);
					url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter=";					
				}
				Log.v("url", url);
				i.putExtra("url",url);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				startActivity(i);
			}        	
		});
	}
}
