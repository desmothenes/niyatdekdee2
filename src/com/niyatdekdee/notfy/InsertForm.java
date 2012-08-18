package com.niyatdekdee.notfy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InsertForm extends Activity  {
	DatabaseAdapter db;
	Button saveButton;
	Button cancelButton;
	TextView txtName;
	TextView txtUrl;
	TextView txtChapter;
	String title;
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
				// TODO Auto-generated method stub
				db.open();
				long id = db.insertNiyay(
						txtName.getText().toString(),
						txtUrl.getText().toString(),
						Integer.parseInt(txtChapter.getText().toString()),
						title);
				if (id >0) {
					Toast.makeText(getBaseContext(), "Insert Succeed.", Toast.LENGTH_SHORT).show();
					Intent i = new Intent(getBaseContext(),MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
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
		
		try {			
			//Document doc = Jsoup.connect(url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id=")).userAgent("Mozilla").timeout(30000).post();
			InputStream is = new URL(url).openStream(); 			
			Document doc = Jsoup.parse(is, "UTF-8", url);
			String html = doc.html();
//			FileWriter outFile = new FileWriter(Environment.getExternalStorageDirectory()+"/test.txt");
//			PrintWriter out = new PrintWriter(outFile);
//			out.println(html);
//			out.close();

			int start = html.lastIndexOf("<tr>\n          <td align=\"middle\">");
			int end = html.lastIndexOf("</td>\n          <td><a target=\"_blank\"");
			//Log.v("html", html);

			Log.v("start", Integer.toString(start));
			Log.v("end", Integer.toString(end));

			txtChapter.setText(html.substring(start+"</td>\n</tr><tr><td align=\"middle\">".length(), end));
			doc = Jsoup.connect(url+txtChapter.getText()).get();
			title = doc.title();
			Log.v("title", title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return title;
	}
	private void clearAll() {
		// TODO Auto-generated method stub
		txtName.setText("");
		txtUrl.setText("");
		txtChapter.setText("");
	}

}
