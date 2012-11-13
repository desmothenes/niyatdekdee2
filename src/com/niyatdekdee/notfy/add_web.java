package com.niyatdekdee.notfy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class add_web extends Activity  {

	//private WebViewClient client;
	private WebView webView;
	private Button addButton;
	final Activity activity = this;
	private boolean tipCheck;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.web_add);       
		if (customTitleSupported) {

			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);
			TextView title = (TextView) findViewById(R.id.textViewOk);
			title.setText(" เลือกตอนนิยาย");

			ImageButton btnOk = (ImageButton)findViewById(R.id.imageButton1);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					add();
				}				
			});
			//เชื่อม btnSearch btnDirection เข้ากับ View
			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);

			btnDirection.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		if (!Setting.getSelectImageSetting(this))
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
				if (tipCheck) {
					if (url.contains("view.php"))
						Toast.makeText(getBaseContext(), "คุณสามารถเพิ่มนิยายเรื่องนี้จากหน้านี้ได้ โดนการกด เพิ่ม ตอนล่าสุดจะเป็นตอนสุดท้ายที่มี แต่แนะนำให้เพิ่มโดยการเข้าไปเลือกกดเพิ่มจากตอนที่จ้องการจะดีกว่า", Toast.LENGTH_LONG).show();
					else if (url.contains("viewlongc.php"))
						Toast.makeText(getBaseContext(), "คุณสามารถเพิ่มนิยายเรื่องนี้จากหน้านี้ได้ โดนการกด เพิ่ม ตอนนี้จะเป็นตอนล่าสุด", Toast.LENGTH_LONG).show();
				} 
				view.loadUrl(url);
				return true; 
			}   
		});

		webView.loadUrl("http://www.dek-d.com/writer/frame.php");
		if (tipCheck) {
			final Toast tag = Toast.makeText(this, "เข้าไปหน้านิยายที่ต้องการแล้วกดเพิ่ม สามารถเลือกจากหน้าหลักหรือจากตอนที่ต้องการ แต่แนะนำให้เลือกจากตอน", Toast.LENGTH_SHORT);
			tag.show();
			new CountDownTimer(9000, 1000)
			{

				public void onTick(long millisUntilFinished) {tag.show();}
				public void onFinish() {tag.show();}

			}.start();
		}
		//webView.loadUrl("http://writer.dek-d.com/nanakosos/story/view.php?id=559528");
		addButton = (Button) findViewById(R.id.button1);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add();
			}        	
		});
	}

	private void add() {
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
			final String stext = "id=";
			//หาหลักของตอน
			final int start = url.lastIndexOf(stext)+stext.length();
			if (start - stext.length() == -1) {
				Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
				return;
			}

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

	@Override
	protected void onResume() {
		super.onResume();
		tipCheck = Setting.getCheckSetting(getApplicationContext());
	}
	@Override
	public void onBackPressed() {
		finish();
	}
}
