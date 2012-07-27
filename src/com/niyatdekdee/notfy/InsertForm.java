package com.niyatdekdee.notfy;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
					startActivity(i);
					clearAll();
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
		// TODO Auto-generated method stub
		String title = null;
		try {
			Document doc = Jsoup.connect(url).get();
			String html = doc.html();
			String stext = url.substring(0, url.indexOf("view"));
			//หาหลักของตอน
			int start = html.lastIndexOf(stext)+stext.length();
			int len=0;
			for (int i = start;Character.isDigit(html.charAt(i));i++)
				len++;
			title = html.substring(html.lastIndexOf(stext)+stext.length(),html.lastIndexOf(stext)+stext.length()+len);
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
