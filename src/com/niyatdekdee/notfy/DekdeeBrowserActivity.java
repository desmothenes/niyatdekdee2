package com.niyatdekdee.notfy;

import java.io.File;
import java.util.HashMap;

import com.google.analytics.tracking.android.EasyTracker;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Color;

public class DekdeeBrowserActivity extends Activity  {
	private static MyWebView webView;
	private String url;
	private ImageButton btnOk;
	private File temp;
	private ProgressDialog dialog;
	private static int useFullscreen = 0;
	private Button spiner;
	private TextView title;
	private HashMap<String,Boolean> history = new HashMap<String,Boolean>();
	private Intent intent;
	private int count = 0;
	protected boolean sixty = true;
	private static int scRoll = 0;
	private boolean customTitleSupported;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//BugSenseHandler.initAndStartSession(DekdeeBrowserActivity.this, "7942beee");
		customTitleSupported = false;
		if (useFullscreen != 1)
			customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		else  {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);  
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//useFullscreen = false;
		intent = getIntent();
		url = intent.getStringExtra("url");
		System.out.println("1 "+url);

		dialog = new ProgressDialog(DekdeeBrowserActivity.this);
		setContentView(R.layout.activity_dekdee_browser);
		if (scRoll == 0)
			if (Setting.getScreenSetting(getApplicationContext()).equals("1")) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				scRoll = -1;
			}
			else {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				scRoll = 1;
			}
		else if (scRoll == -1)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.okbar);
			spiner = new Button(this);
			RelativeLayout.LayoutParams lspin =  new RelativeLayout.LayoutParams(45,45);
			lspin.addRule(RelativeLayout.LEFT_OF,R.id.imageButton1);
			lspin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			spiner.setId(R.id.layout1);
			spiner.setLayoutParams(lspin);
			spiner.setText("F");			
			spiner.setTextSize(20);
			spiner.setGravity(Gravity.CENTER_HORIZONTAL);
			spiner.setTextColor(Color.WHITE);
			spiner.setBackgroundColor(Color.TRANSPARENT);			
			barLayout.addView(spiner);

			title = (TextView) findViewById(R.id.textViewOk);
			lspin = (LayoutParams) title.getLayoutParams();
			lspin.addRule(RelativeLayout.LEFT_OF,R.id.layout1);
			lspin.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);


			title.setLayoutParams(lspin);
			title.setEllipsize(TruncateAt.MARQUEE);
			title.setSingleLine();
			title.setHorizontallyScrolling(true);
			title.setFocusableInTouchMode(true);
			title.setSelected(true);

			MainActivity.titleColor = Integer.parseInt(Setting.getColorSelectSetting(DekdeeBrowserActivity.this));
			switch (MainActivity.titleColor) {
			case 0:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar);
				break;
			case 1:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				break;
			case 2:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
				break;
			case 3:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
				break;
			case 4:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_blue);
				break;
			case 5:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
				break;
			case 6:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_siver);
				break;
			case 7:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_glay);
				break;
			case 8:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_orange);
				break;
			}

			spiner.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*					if(useFullscreen)  
					{  
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);  
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);					    
					} 
					else
					{  */
					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					String temp = webView.getUrl() ;
					while (temp == null) temp = webView.getUrl() ;	
					editor.putInt(String.format("scroll %s",temp), webView.getScrollY());
					Log.e(String.format("scroll x %s",temp), Integer.toString(webView.getScrollX()));
					Log.e(String.format("scroll y %s",temp), Integer.toString(webView.getScrollY()));
					editor.commit();
					Intent intent = getIntent();
					webView.saveState(savedInstanceState);
					finish();
					startActivity(intent);
					/*						getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);  
						//requestWindowFeature(Window.FEATURE_NO_TITLE);
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
						//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);					    
					 */
					/*	} */ 	
					useFullscreen = 1;
				}

			});


			title.setTextSize(15);
			title.setText(String.format(" %s",intent.getStringExtra("title")));
			//btnOk.setMaxHeight(35);

			btnOk = (ImageButton)findViewById(R.id.imageButton1);
			btnOk.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
			btnOk.setScaleType(ScaleType.CENTER_INSIDE);
			//btnOk.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			btnOk.setOnClickListener(new View.OnClickListener() {


				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CharSequence[] items = {"อ่านออกเสียง","โหลดใหม่","หมุนหน้าจอ","เพิ่มขนาดตัวอักษร","ลดขนาดตัวอักษร", "แสดงความคิดเห็น", "เนื้อเรื่อง","ความคิดเห็น","รายชื่อตอนทั้งหมด"};
					AlertDialog.Builder builder = new AlertDialog.Builder(DekdeeBrowserActivity.this); 
					builder.setCancelable(true)
					.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int id) { 
							dialog.dismiss();
							if (id == 0) {
								Log.e("zone", "click tts");
								if (!MainActivity.isTTS) {
									Log.e("zone", "false tts");
									CharSequence[] items = {"ค้นหาจาก Play Store", "Vaja", "SVOX Thai Kanya Voice"};
									AlertDialog.Builder builder = new AlertDialog.Builder(DekdeeBrowserActivity.this); 
									builder.setCancelable(true).setTitle("ไม่รองรับ TTS ภาษาไทย เลือกรายการด้านล่างแทน")
									.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() { 
										public void onClick(DialogInterface dialog, int id) { 
											dialog.dismiss();
											if (id == 0) {
												try {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=tts+engine+thai")));
												} catch (android.content.ActivityNotFoundException anfe) {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q==tts+engine+thai")));
												}
											}
											else if(id == 1) {
												try {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spt.tts.vaja")));
												} catch (android.content.ActivityNotFoundException anfe) {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.spt.tts.vaja")));
												}
											}
											else if(id == 2) {		
												try {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
												} catch (android.content.ActivityNotFoundException anfe) {
													startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.svox.classic.langpack.tha_tha_fem_trial")));
												}
											}
										}
									}); 
								} else	if (DekTTSActivity.tts != null && DekTTSActivity.isSpeak ) {
									Log.e("zone", "stop tts");
									DekTTSActivity.tts.stop();									
									DekTTSActivity.isSpeak = false;
									DekTTSActivity.stop = true;
									Toast.makeText(getBaseContext(), "tts กำลังหยุดหลังประโยคสุดท้าย", Toast.LENGTH_LONG).show();
								} else {	
									Toast.makeText(getBaseContext(),"โปรดรอสักครู่ กำลังประมวลผล\nสามารถกด Back เพื่อหยุด TTS ได้", Toast.LENGTH_LONG).show();
									Log.e("zone", "totext tts");
									//tts = new TTS(DekdeeBrowserActivity.this.getBaseContext(),DekdeeBrowserActivity.this);
									if (webView.getUrl().contains(url) && temp != null) {
										Log.e("zone", "by file tts");
										//TTS.totext(temp);
										Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
										DekTTSActivity.type = 2;										//intent.putExtra("text", temp.getAbsolutePath());
										DekTTSActivity.temp = temp;
										startService(intent);
									} else {
										Log.e("zone", "by script tts");
										webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
										/*							while (htmlcache == null) {
												new Thread() {
													public void run() {
														while (htmlcache == null) {
															try {
																Thread.sleep(1000);
															} catch (InterruptedException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
														}
														TTS.strtotext(htmlcache);	
													}
												}.start();
											}*/
									}	
									
								}
							}			
							else if(id == 1) {
								webView.reload();
							}
							else if(id == 2) {
								//Roll screen
								if(scRoll == -1)  
								{  
									getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
									getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);  
									setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);					    
								} 
								else
								{  
									getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);  
									getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
									setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
									//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
									//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);					    

								} 
								scRoll = -scRoll;							
							}	
							else if(id == 3) {
								//เพิ่มขนาด font
								Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
								webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
										"for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)+5+'px'};");
							}
							else if(id == 4) {
								//ลดขนาด font
								Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
								webView.loadUrl("javascript: var arr = document.getElementById('story_body').getElementsByTagName('span');" +
										"for (var i = 0;i<arr.length;i++) { if (arr[i].fontSize != '') arr[i].style.fontSize=parseInt(arr[i].style.fontSize)-5+'px'};");
							}
							else if(id == 5) {
								//post comment
								Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
								//Toast.makeText(getApplicationContext(), "this function not enable in this version"/*items[id]*/, Toast.LENGTH_SHORT).show();
								webView.loadUrl("javascript: var elem = document.getElementsByName('t_msg')[0];" +
										"var x = 0; var y = 0;" +
										"    while (elem != null) {" +
										"		y += elem.offsetTop;" +
										"	elem = elem.offsetParent;" +
										"}" +					
										"window.scrollTo(x, y-10);");

							}
							else if(id == 6) {		
								//story body
								Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
								webView.loadUrl("javascript: var elem = document.getElementById('story_body');" +
										"var x = 0; var y = 0;" +
										"    while (elem != null) {" +
										"		y += elem.offsetTop;" +
										"	elem = elem.offsetParent;" +
										"}" +
										"window.scrollTo(x, y);");
							}
							else if(id == 7) {
								//comment
								Toast.makeText(getBaseContext(), "โปรดรอ", Toast.LENGTH_SHORT).show();
								webView.loadUrl("javascript: var elem = document.getElementById('ed_1');" +
										"var x = 0; var y = 0;" +
										"    while (elem != null) {" +
										"		y += elem.offsetTop;" +
										"	elem = elem.offsetParent;" +
										"}" +
										(Setting.getsinglecolum(getApplicationContext()) ? "window.scrollTo(x, y-45);":"window.scrollTo(x, y-15);"));
							}
							else if (id == 8) {
								//chapter list
								Intent chapterlist = new Intent(getBaseContext(), ChapterListActivity.class);
								String temp = webView.getUrl() ;
								while (temp == null) temp = webView.getUrl() ;									
								chapterlist.putExtra("url", temp);
								temp = webView.getTitle();								
								if (temp == null) temp = "";	
								final int cutpoint = temp.indexOf(">");
								if (cutpoint != -1) temp = temp.substring(0, cutpoint);
								chapterlist.putExtra("title", temp);
								startActivity(chapterlist);
								finish();
							}

						}

					}); 
					AlertDialog alert = builder.create(); 
					alert.show();


				}				
			});

			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					String temp = webView.getUrl() ;
					while (temp == null) temp = webView.getUrl() ;	
					editor.putInt(String.format("scroll %s",temp), webView.getScrollY());
					Log.e(String.format("scroll x %s",temp), Integer.toString(webView.getScrollX()));
					Log.e(String.format("scroll y %s",temp), Integer.toString(webView.getScrollY()));
					editor.commit();
					finish();
				}
			});
		}	



		webView = (MyWebView) findViewById(R.id.webView1);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress)            { 
				super.onProgressChanged(view, progress);
				//Log.v("%",Integer.toString(progress));

				if(sixty && (progress > 60)) {
					sixty = false;
					final String newtitle = webView.getTitle();
					if (newtitle != null)
						if (newtitle.contains("ตอนที่")) {

							if (getPreferences(MODE_PRIVATE).getInt(String.format("scroll %s",url), 0) == 0) {
								webView.loadUrl("javascript: var elem = document.getElementById('story_body');" +
										"var x = 0; var y = 0;" +
										"    while (elem != null) {" +
										"		y += elem.offsetTop;" +
										"	elem = elem.offsetParent;" +
										"}" +
										"window.scrollTo(x, y);");
							}					

							if (useFullscreen != 1)
								if (title != null) title.setText(String.format(" %s",newtitle.substring(newtitle.indexOf("ตอนที่"))));
							webView.loadUrl("javascript: var x = document.getElementsByTagName('table');"+
									"for( var i = 0; i < x.length ; i++ ) {x[i].align='left'; x[i].border='0'; }");

							webView.loadUrl("javascript: var story = document.getElementById('story_body');" +
									"story.insertBefore(document.createElement('br'),story.firstChild);" +
									"story.insertBefore(document.createElement('br'),story.firstChild);" +
									"var arr = story.getElementsByTagName('p');" +
									"document.getElementsByName('txtCode')[0].type = 'number';" +
									"for (var i = 0;i<arr.length;i++) {arr[i].style.textAlign = 'left'} ;" +
									"document.getElementsByName('t_mem')[1].checked  = true;" +
									(Setting.getsinglecolum(getApplicationContext()) ? "document.getElementsByClassName('accord-big')[0].innerHTML = '';" : "" )+
									"document.getElementsByName('t_name')[0].value='" +Setting.getPosttext(getApplicationContext())+ " (เขียนบน android)';" +
									"images = story.getElementsByTagName('img');" +
									"tables = story.getElementsByTagName('table');"+
									"for (i=0;i<tables.length;i++) {tables[i].outerHTML = \"<div style='overflow: scroll;'>\"+tables[i].outerHTML+\"</div>\";}"+
									"for (i=0;i<images.length;i++) {images[i].outerHTML = \"<div style='overflow: scroll;'>\"+images[i].outerHTML+\"</div>\";}");

							/* 
									"var canvas = document.createElement('canvas');" +
									"document.getElementsByName('txtCode')[0].appendChild(canvas);" +
									"var context = canvas.getContext('2d');" +
									"var imageObj = new Image();" +
									"imageObj.onload = function() {" +
									"context.drawImage(imageObj, 80, 20);" +
									"};" +
									"imageObj.src ='http://www.dek-d.com/myLib/authimg.php';"*/

							if (Setting.getisLogin(getApplicationContext())) {
								webView.loadUrl("javascript: document.getElementsByName('t_mem')[1].checked  = true");
								webView.loadUrl("javascript: document.getElementsByName('t_username')[0].value='" +Setting.getUserName(getApplicationContext())+"';");
								webView.loadUrl("javascript: document.getElementsByName('t_password')[0].value='" +Setting.getPassWord(getApplicationContext())+"';");
								//webView.loadUrl("javascript: document.getElementById('story_body').scrollIntoView()");
							}

							final int theme = Integer.parseInt(Setting.getBgColorSetting(getApplicationContext()));
							if (theme != 0) {
								webView.loadUrl("javascript: document.body.background = '';");
							}
							if (theme == 1) {
								webView.loadUrl("javascript: var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].bgColor='#0';" +
										"document.body.style.background  = 'black';");
							} else if (theme == 2) {
								webView.loadUrl("javascript:  var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].bgColor='#FFFFFF';" +
										"var x = document.getElementsByTagName('font');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].color='#000000'" +
										"document.body.style.background  = 'white';");
							} else if (theme == 3) {
								webView.loadUrl("javascript:  var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) {x[i].bgColor='#808080';  x[i].style.color='black' }" +
										"document.body.style.background  = 'gray';");
							} 

							final int thcc = Integer.parseInt(Setting.getTextColorSetting(getApplicationContext()));
							if (thcc == 1) {
								webView.loadUrl("javascript: var x = document.getElementById('story_body').getElementsByTagName('span');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='black';"+
										"var x = document.getElementById('story_body').getElementsByTagName('font');"+
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='black';" +
										"var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='black';");
							} else if (thcc == 2) {
								webView.loadUrl("javascript: var x = document.getElementById('story_body').getElementsByTagName('span');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='white';"+
										"var x = document.getElementById('story_body').getElementsByTagName('font');"+
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='white';"+
										"var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='white';");
							} else if (thcc == 3) {
								webView.loadUrl("javascript: var x = document.getElementById('story_body').getElementsByTagName('span');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='blue';"+
										"var x = document.getElementById('story_body').getElementsByTagName('font');"+
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='blue';"+
										"var x = document.getElementsByTagName('td');" +
										"for( var i = 0; i < x.length ; i++ ) x[i].style.color='blue';");
							} 
						} else 
							Log.e("title",newtitle);
				}
				dialog.setProgress(progress);
			}        

			/*			@Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				// TODO Auto-generated method stub
				Log.v("ChromeClient", "invoked: onConsoleMessage() - " + sourceID + ":"
						+ lineNumber + " - " + message);
				super.onConsoleMessage(message, lineNumber, sourceID);
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.v("ChromeClient", cm.message() + " -- From line "
						+ cm.lineNumber() + " of "
						+ cm.sourceId() );
				return true;
			}*/
		});

		webView.setWebViewClient(new WebViewClient() {			
			@Override
			public void onPageStarted(WebView view, String url,Bitmap favicon) {
				count++;
				if (count > 3) {
					webView.freeMemory();
					count = 0;
				}
				dialog.setTitle("Loading");
				final double rand = Math.random();
				if (rand < 0.2)
					dialog.setMessage("สามารถใช้ปุ่มเพิ่ม-ลดเสียง เพื่อเลื่อนหน้าจอได้");
				else if (rand < 0.4)
					dialog.setMessage("สามารถกด menu เพื่อเลื่อนไปยังส่วนต่าง ๆ ของหน้าได้ เช่น แสดงความคิดเห็น");				
				else if (rand < 0.6)
					dialog.setMessage("สามารถกด Back เพื่อย้อนกลับไปหน้าเดิมได้");
				else if (rand < 0.6)
					dialog.setMessage("สามารถกด F เพื่ออ่านในรูปแบบแนวนอนเต็มจอ");
				else
					dialog.setMessage("สามารถปิดกรอบนี้โดยใช้ปุ่ม Back ได้ แต่เมื่อโหลดเสร็จอาจมีการเลื่อนอัตโนมัติ");
				dialog.setMax(100);
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.setCancelable(true);
				if (dialog != null && dialog.isShowing()) {try {dialog.show();} catch (Exception e) {}}
				super.onPageStarted(view, url, favicon);
			}

			@Override 
			public void onPageFinished(WebView view, String url2) {
				super.onPageFinished(webView, url2);
				if (dialog != null && dialog.isShowing()) {try {dialog.dismiss();} catch (Exception e) {}}
				sixty = true;
				SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
				final int yposi = prefs.getInt(String.format("scroll %s",url), 0);
				//Log.e(String.format("scroll %s",url), Integer.toString(yposi));
				Log.e("url2",url2);		

				//if (Setting.getsinglecolum(getApplicationContext()))
				if (yposi != 0)	webView.scrollTo(0,yposi);
				else {
					//if (yposi != 0)	webView.scrollTo(11,yposi);
					//else webView.scrollTo(11,webView.getScrollY());
				}
			}  

			@Override   
			public boolean shouldOverrideUrlLoading(WebView view, String url)   
			{   
				//super.shouldOverrideUrlLoading(view, url);

				if (url.contains("#story_body")) {
					history.put(url, true);
					return false;
				} else if (history.get(url) == null) {
					view.loadUrl(url/*+"#story_body"*/);
					history.put(url, true);
					return true; 
				}
				return false;

			}  

			/*
		    @Override
		    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
		    }*/
		});
		getApplicationContext();
		String sessionCookie;  
		CookieManager cookieManager;  

		CookieSyncManager.createInstance(DekdeeBrowserActivity.this);  
		cookieManager = CookieManager.getInstance(); 
		sessionCookie = intent.getStringExtra("cookieString");
		if (sessionCookie != null) 
			cookieManager.removeSessionCookie();   
		SystemClock.sleep(1000);  

		cookieManager = CookieManager.getInstance();

		if (sessionCookie != null) {  
			//Log.e("cookieString",sessionCookie);
			cookieManager.setCookie("dek-d.com", sessionCookie);  
			CookieSyncManager.getInstance().sync();  
		}  
		webView.getSettings().setJavaScriptEnabled(true);
		webView.requestFocus(View.FOCUS_DOWN);
		//webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		if (!Setting.getSelectImageSetting(getApplicationContext()))
			webView.getSettings().setLoadsImagesAutomatically(false);
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		if (intent.getStringExtra("id") != null)
			if (!intent.getStringExtra("id").equals("-2")) {
				temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),intent.getStringExtra("id")+".html");
				/*Document doc = null;
				try {
					doc = Jsoup.parse(temp, "tis620");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} */
			}
		/*		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
		bw.write(doc.html().replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
		bw.flush();
		bw.close();*/
		//System.out.println(temp.getAbsolutePath());
		//webView.requestFocus(View.FOCUS_DOWN);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		//webView.setFocusableInTouchMode(false);
		//webView.setFocusable(false);
		webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");  
		CookieSyncManager.createInstance(webView.getContext());
		if (Setting.getsinglecolum(getApplicationContext())) {
			webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);			
		}		
		else {
			webView.getSettings().setBuiltInZoomControls(true);
		}
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollbarOverlay(true);

		if (!isOnline() && temp != null) webView.loadUrl( "file:///"+temp.getAbsolutePath());
		/*else if (yposi != 0)	webView.loadUrl(url);
		else*/ webView.loadUrl(url/*+"#story_body"*/);


		//if (savedInstanceState == null)
		//new WebViewTask().execute();  
		Button downButton = (Button) findViewById(R.id.button1);
		if (!Setting.getdownScroll(getApplicationContext())) {
			downButton.setVisibility(View.INVISIBLE);
			downButton.setEnabled(false);
		} else {
			downButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					webView.pageDown(false);
				}        	
			});
		}
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

	/*	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_dekdee_browser);
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			webView.pageDown(false);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			webView.pageUp(false);
			return true;
		} else  if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!customTitleSupported) {
				useFullscreen = -1;
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
			if (DekTTSActivity.tts != null  && DekTTSActivity.isSpeak) {
				DekTTSActivity.tts.stop();
				DekTTSActivity.stop = true;
				DekTTSActivity.isSpeak = false;
				Toast.makeText(getBaseContext(), "Stop TTS", Toast.LENGTH_LONG).show();
				return true;
			}	

			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			String temp = webView.getUrl() ;
			while (temp == null) temp = webView.getUrl() ;	
			editor.putInt(String.format("scroll %s",temp), webView.getScrollY());
			Log.e(String.format("scroll x %s",temp), Integer.toString(webView.getScrollX()));
			Log.e(String.format("scroll y %s",temp), Integer.toString(webView.getScrollY()));
			editor.commit();

			if (webView.getUrl() == null) {
				Toast.makeText(getBaseContext(), "การย้อนใช้เวลาสักครู่ โปรดรอ\n\nถ้าต้องการออก กรุณากดลูกศรด้านบน", Toast.LENGTH_LONG).show();
			}			
			else if (webView.getUrl().equals(url)) {
				Toast.makeText(getBaseContext(), "การย้อนใช้เวลาสักครู่ โปรดรอ\n\nถ้าต้องการออก กรุณากดลูกศรด้านบน", Toast.LENGTH_LONG).show();
				finish();
			} else if (!webView.canGoBack()) {
				if (intent.getStringExtra("from") == null) stopService( new Intent(getApplicationContext(), DekTTSActivity.class));
				finish();
			}			
			webView.goBack();
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
        return  keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||	keyCode == KeyEvent.KEYCODE_VOLUME_UP || super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {

	}

	public void onFormResubmission(WebView view, Message dontResend, Message resend)
	{
		resend.sendToTarget();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_dekdee_browser, menu);
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
			webView.loadUrl("javascript: var elem = document.getElementById('ed_1');" +
					"var x = 0; var y = 0;"+
					"    while (elem != null) {" +
					"		y += elem.offsetTop;" +
					"	elem = elem.offsetParent;" +
					"}" +
					(Setting.getsinglecolum(getApplicationContext()) ? "window.scrollTo(x, y-45);":"window.scrollTo(x, y-15);"));
			return true;
		case R.id.postcomment:
			webView.loadUrl("javascript: var elem = document.getElementsByName('t_msg')[0];" +
					"var x = 0; var y = 0;" +
					"    while (elem != null) {" +
					"		y += elem.offsetTop;" +
					"	elem = elem.offsetParent;" +
					"}" +
					"window.scrollTo(x, y-10);");
			return true;
		case R.id.Top:
			webView.pageUp(true);
			return true;
		case R.id.buttom:
			webView.pageDown(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/*
	String totext() {
		tts = new TextToSpeech(this, this);
		dialog = ProgressDialog.show(DekdeeBrowserActivity.this,"Loading", "Please Wait...",true);
		if (webView.getUrl().equals(url))
			try {
				doc = Jsoup.parse(temp, "tis620");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		else			
			try {
				doc = Jsoup.connect(url).timeout(8000).get();
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
	 */
	/*	@Override
	protected void onResume() {
		//dialog = ProgressDialog.show(MainActivity.this,"Loading", "Please Wait...",true);
		super.onResume();		
		if (TTS.tts != null) 
			TTS.tts.shutdown();
		TTS.tts = new TextToSpeech(this,this);
	}*/

	/*	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (TTS.tts != null) {
			TTS.tts.stop();
			TTS.isSpeak = false;

		}
		super.onDestroy();
	}*/

	/*
private void restoreProgress(Bundle savedInstanceState) {
        waiting=savedInstanceState.getBoolean("waiting");
        if (waiting) {
            AppClass app=(AppClass) getApplication();
            ProgressDialog refresher=(ProgressDialog) app.Dialog.get();
            refresher.dismiss();
            String logingon=getString(R.string.signon);
            app.Dialog=new WeakReference<ProgressDialog>(ProgressDialog.show(AddAccount.this, "", logingon, true));



        }

    }*/
	class MyJavaScriptInterface 
	{
		public void processHTML(String html)
		{
			/*		        new AlertDialog.Builder(DekdeeBrowserActivity.this)
	            .setTitle("HTML")
	            .setMessage(html)
	            .setPositiveButton(android.R.string.ok, null)
	        .setCancelable(false)
	        .create()
	        .show();
			ContextWrapper cw = new ContextWrapper(DekdeeBrowserActivity.this);
			ttscache =  new File(cw.getDir("temp", Context.MODE_PRIVATE),"ttscache");
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ttscache),"tis620"));
				bw.write(html);
				bw.flush();
				bw.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			Log.e("zone", "end html");
			//TTS.strtotext(html);	
			Intent intent = new Intent(getApplicationContext(), DekTTSActivity.class);
			intent.putExtra("from", "main");
			DekTTSActivity.type = 3;
			DekTTSActivity.text = html;
			startService(intent);
		}
	}

	/*	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {
			int result = TTS.tts.setLanguage(new Locale( "tha", "TH"));

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(getApplicationContext(), "This Language is not supported", Toast.LENGTH_SHORT).show();
				Log.e("TTS", "Thai language is not supported on your TTS");
				isTTS = false;
			}	
		} else {
			Toast.makeText(getApplicationContext(), "TTS Initilization Failed!", Toast.LENGTH_SHORT).show();
			Log.e("TTS", "Initilization Failed!");
			isTTS = false;
		}
	}*/
	private boolean isOnline() { 
		ConnectivityManager cm = 
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 

		return cm.getActiveNetworkInfo() != null &&  
				cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
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
}
