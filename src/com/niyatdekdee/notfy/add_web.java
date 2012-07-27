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

		addButton = (Button) findViewById(R.id.button1);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String url = webView.getUrl();
				Log.v("url", url);
				Intent i = new Intent(getBaseContext(),InsertForm.class);
				i.putExtra("url",url);
				String title = (String) getTitle();
				if (title.contains(">"))
					title = title.substring(5, title.indexOf(">"));
				else
					title = title.substring(5);
				Log.v("title", title);
				i.putExtra("name",title);
				if (url.contains("chapter")) {
					i.putExtra("",url.substring(url.indexOf("chapter=")+8));
					Log.v("chapter", url.substring(url.indexOf("chapter=")+8));
				}
				//startActivity(i);
			}        	
		});
	}
}
