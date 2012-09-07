package com.niyatdekdee.notfy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InsertForm extends Activity  {
	private DatabaseAdapter db;
	private Button saveButton;
	private Button cancelButton;
	private TextView txtName;
	private TextView txtUrl;
	private TextView txtChapter;
	private String title;
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inset_form);
		db = new DatabaseAdapter(this);      
		saveButton = (Button) findViewById(R.id.button3);
		cancelButton = (Button) findViewById(R.id.button4);
		txtName = (TextView) findViewById(R.id.editText1);
		txtUrl = (TextView) findViewById(R.id.editText2);
		txtChapter = (TextView) findViewById(R.id.editText3);
		Intent intent = getIntent();  
		if (intent.getStringExtra("name") != null) {
			txtName.setText(intent.getStringExtra("name"));
			txtUrl.setText(intent.getStringExtra("url"));
			if (intent.getStringExtra("chapter") != null) {
				txtChapter.setText(intent.getStringExtra("chapter"));
				title = intent.getStringExtra("title");
			}	
			else {
				Log.v("ma url", intent.getStringExtra("url"));
				title = gettitle(intent.getStringExtra("url"));				
			}
		}

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				db.open();
				long id = db.insertNiyay(
						txtName.getText().toString(),
						txtUrl.getText().toString(),
						Integer.parseInt(txtChapter.getText().toString()),
						title);
				if (id >0) {
					Toast.makeText(getBaseContext(), "Insert Succeed.", Toast.LENGTH_SHORT).show();
					//Intent i = new Intent(getBaseContext(),MainActivity.class);
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					//startActivity(i);
				} else {
					Toast.makeText(getBaseContext(), "Insert Failed.", Toast.LENGTH_SHORT).show();
				}
				db.close();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearAll();
			}
		});
	}
	
	private String gettitle(String url) {
		Log.v("ti url", url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id="));
		// TODO Auto-generated method stub
		String title = null;		
		
		//Document doc = Jsoup.connect(url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id=")).userAgent("Mozilla").timeout(30000).post();
		HttpClient httpclient = new DefaultHttpClient();			
		//String doc = Jsoup.parse(is, "UTF-8", url);
		String html = "";
		try {
			HttpGet httpget = new HttpGet(new URI(url));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			html = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
			FileWriter outFile;
			try {
				outFile = new FileWriter(Environment.getExternalStorageDirectory()+"/test.txt");
				PrintWriter out = new PrintWriter(outFile);
				out.println(html);
				out.close();
			} catch (IOException e1) {
				Toast.makeText(getBaseContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
				e1.printStackTrace();
			}

		final int start = html.lastIndexOf("<tr>\n          <td align=\"middle\">");
		final int end = html.lastIndexOf("</td>\n          <td><a target=\"_blank\"");
		//Log.v("html", html);

/*		Log.v("start", Integer.toString(start));
		Log.v("end", Integer.toString(end));*/
		
		
		
		
		if (start == -1 || end == -1) {
			final int op = html.lastIndexOf("</td></tr><tr><td align=middle>");
			final int ed = html.indexOf("</td>", op+"</td></tr><tr><td align=middle>".length());/*
			Log.v("op", Integer.toString(op));
			Log.v("ed", Integer.toString(ed));*/
			if (op == -1 || ed == -1) {
				txtChapter.setText("can't find chapter please fill by yourself");
				return "non";
			}
			else {
				txtChapter.setText(html.substring(op+"</td></tr><tr><td align=middle>".length(), ed));
			}
		}
		else {
			txtChapter.setText(html.substring(start+"</td>\n</tr><tr><td align=\"middle\">".length(), end));
		}
		
		Log.v("url", url+txtChapter.getText());
		HttpClient httpclient1 = new DefaultHttpClient();	
		try {
			HttpGet httpget = new HttpGet(new URI(url+txtChapter.getText()));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			html = httpclient1.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			httpclient1.getConnectionManager().shutdown();
		}
		
		final int tstart;
		if ((tstart = html.indexOf("<title>")) != -1) {
			title = html.substring(tstart+7, html.indexOf("</title>"));
			if (title.indexOf(">") != -1) 				
				title = Jsoup.parse((title.substring(title.indexOf(">")+2))).text();}
		if (title == null)
			title = "null";
		//Log.v("title", title);
		return title;
	}
	
	private void clearAll() {
		txtName.setText("");
		txtUrl.setText("");
		txtChapter.setText("");
	}
}
