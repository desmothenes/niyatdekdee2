package com.niyatdekdee.notfy;

//import com.bugsense.trace.BugSenseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class add_web extends Activity  {

	//private WebViewClient client;
	private WebView webView;
	private Button addButton;
	final Activity activity = this;
	private boolean tipCheck;
	private ProgressBar spiner;
	private boolean loading;
	static boolean addclick;
	String htmltext;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//BugSenseHandler.initAndStartSession(add_web.this, "7942beee");
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.web_add);  
		getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		if (customTitleSupported) {

			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);
			TextView title = (TextView) findViewById(R.id.textViewOk);
			title.setText(" เลือกตอนนิยาย");
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.okbar);
			spiner = new ProgressBar(this);
			RelativeLayout.LayoutParams lspin =  new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lspin.addRule(RelativeLayout.LEFT_OF,R.id.imageButton1);
			lspin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			spiner.setLayoutParams(lspin);
			barLayout.addView(spiner);
			ImageButton btnOk = (ImageButton)findViewById(R.id.imageButton1);

			switch (MainActivity.titleColor) {
			case 0:
				btnOk.setBackgroundResource(R.drawable.bg_titlebar);
				barLayout.setBackgroundResource(R.drawable.bg_titlebar);
				spiner.setBackgroundResource(R.drawable.bg_titlebar);
				break;
			case 1:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				btnOk.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				spiner.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				break;
			case 2:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
				btnOk.setBackgroundResource(R.drawable.bg_titlebar_green);
				spiner.setBackgroundResource(R.drawable.bg_titlebar_green);
				break;
			case 3:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
				btnOk.setBackgroundResource(R.drawable.bg_titlebar_pink);	
				spiner.setBackgroundResource(R.drawable.bg_titlebar_pink);	
			}



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
					new doback(MainActivity.context).execute();
					finish();
				}
			});
		}
		/*
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);*/

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.requestFocus(View.FOCUS_DOWN);
		if (!Setting.getSelectImageSetting(this))
			webView.getSettings().setLoadsImagesAutomatically(false);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress)            { 
				activity.setTitle("Loading..."); 
				activity.setProgress(progress * 100);
				if(progress == 100) {                  
					activity.setTitle(R.string.app_name);
					spiner.setVisibility(View.GONE);
				} else if (loading && progress > 70) {                  
					loading = false;
					if (tipCheck) {
						if (webView.getUrl().contains("view.php") && addclick)
							Toast.makeText(getBaseContext(), "คุณสามารถเพิ่มนิยายเรื่องนี้จากหน้านี้ได้ โดนการกด เพิ่ม ตอนล่าสุดจะเป็นตอนสุดท้ายที่มี แต่แนะนำให้เพิ่มโดยการเข้าไปเลือกกดเพิ่มจากตอนที่จ้องการจะดีกว่า", Toast.LENGTH_LONG).show();
						else if (webView.getUrl().contains("viewlongc.php") && addclick) {
							final Toast tag = Toast.makeText(getBaseContext(), "คุณสามารถเพิ่มนิยายเรื่องนี้จากหน้านี้ได้ โดนการกด เพิ่ม ตอนนี้จะเป็นตอนล่าสุด", Toast.LENGTH_SHORT);
							tag.show();
							new CountDownTimer(6000, 1000)
							{
								public void onTick(long millisUntilFinished) {tag.show();}
								public void onFinish() {tag.show();}

							}.start();
						}
					} 
				}
			}        
		});

		webView.setWebViewClient(new WebViewClient() {   
			@Override  
			public void onPageFinished(WebView view, String url2) {
				super.onPageFinished(webView, url2);
				spiner.setVisibility(View.GONE);
			}  

			@Override   
			public boolean shouldOverrideUrlLoading(WebView view, String url)   
			{   

				loading = true;
				spiner.setVisibility(View.VISIBLE);
				super.shouldOverrideUrlLoading(view, url);
				//view.loadUrl(url);
				return false; 
			}   
		});

		webView.loadUrl("http://www.dek-d.com/writer/frame.php");
		if (tipCheck) {
			final Toast tag = Toast.makeText(this, "เข้าไปหน้านิยายที่ต้องการแล้วกดเพิ่ม สามารถเลือกจากหน้าหลักหรือจากตอนที่ต้องการ แต่แนะนำให้เลือกจากตอน", Toast.LENGTH_SHORT);
			tag.show();
			new CountDownTimer(6000, 1000)
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
		while (url == null) {
			new Thread() {
				public void run() {
					while (webView.getUrl() == null) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			url = webView.getUrl();
		}
		//Log.v("url", url);
		Intent i = new Intent(getBaseContext(),InsertForm.class);				
		String title = webView.getTitle();
		//Log.v("title", title);
		while (title == null) {
			new Thread() {
				public void run() {
					while (webView.getTitle() == null) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			title = webView.getTitle();
		}

		if (title.contains(">"))
			title = title.substring(6, title.indexOf(">"));
		else
			title = title.substring(6);
		//Log.v("title", title);
		
		i.putExtra("name",title);
		
		if (url.contains("chapter")) {
			i.putExtra("chapter",url.substring(url.indexOf("chapter=")+8));
			i.putExtra("title",webView.getTitle());
			//in this fomat http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=580483&chapter=   378
			url = url.substring(0,url.indexOf("chapter=")+8);
			//Log.v("chapter", url.substring(url.indexOf("chapter=")+8));
		} else {
			//in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
			/*			final String stext = "id=";
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
				//Log.v("Character", Character.toString(url.charAt(i3)));
			}
			Log.v("len", Integer.toString(len));
			final String unum = url.substring(start,start+len);	*/
			final String unum = MyAppClass.findnum(url, "id=", getBaseContext());
			if (unum == null || unum.equals("")) {
				Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
				return;
			}
				
			//Log.v("unum", unum);
			url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter=";					
		}
		//Log.v("url", url);
		i.putExtra("url",url);
		//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//finish();
		addclick = false;
		webView.stopLoading();
		startActivityForResult(i,0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		tipCheck = Setting.getCheckSetting(getApplicationContext());
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (webView.canGoBack()) {
				if (tipCheck) {
				final Toast tag = Toast.makeText(this, "การย้อนใช้เวลาสักครู่ อาจะกลับไปที่ ด้านบทสุดก่อน แล้วจึงย้อน โปรดรอ\n\nถ้าต้องการออก กรุณากดลูกศรด้านบน", Toast.LENGTH_SHORT);
				tag.show();
				new CountDownTimer(4000, 1000)
				{
					public void onTick(long millisUntilFinished) {tag.show();}
					public void onFinish() {tag.show();}

				}.start();
				}
				//Toast.makeText(getBaseContext(), "การย้อนใช้เวลาสักครู่ อาจะกลับไปที่ ด้านบทสุดก่อน แล้วจึงย้อน โปรดรอ\n\nถ้าต้องการออก กรุณากดลูกศรด้านบน", Toast.LENGTH_LONG).show();
				webView.goBack();
			}			
			else {
				Toast.makeText(getBaseContext(), "ถ้าต้องการออก กรุณากดลูกศรด้านบน", Toast.LENGTH_LONG).show();
				//finish();
			}		
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);		
		addclick = true;
		webView.reload();
	}
	class MyJavaScriptInterface 
	{
		public void processHTML(String html)
		{
			htmltext = html;
		}
	}
	/*	public boolean onKeyUp(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||	keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			return true;
		else 
			return super.onKeyDown(keyCode, event);
	}*/

}
