package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EditForm extends Activity  {
	DatabaseAdapter db;
	Button saveButton;
	Button cancelButton;
	TextView txtName;
	TextView txtUrl;
	TextView txtChapter;
	TextView title;
	long rowId;
	@Override
	public void onBackPressed() {
		finish();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_form);
		db = new DatabaseAdapter(this);      
		saveButton = (Button) findViewById(R.id.button3);
		cancelButton = (Button) findViewById(R.id.button4);
		txtName = (TextView) findViewById(R.id.editText1);
		txtUrl = (TextView) findViewById(R.id.editText2);
		txtChapter = (TextView) findViewById(R.id.editText3);
		title = (TextView) findViewById(R.id.editText4);
		Intent intent = getIntent();  
		if (intent.getStringExtra("name") != null) {
			txtName.setText(intent.getStringExtra("name"));
			txtUrl.setText(intent.getStringExtra("url"));
			txtChapter.setText(intent.getStringExtra("chapter"));
			title.setText(intent.getStringExtra("title"));
			rowId = Long.parseLong(intent.getStringExtra("id"));
		}

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				db.open();
				boolean id = db.updateNiyay(rowId,
						txtName.getText().toString(),
						txtUrl.getText().toString(),
						Integer.parseInt(txtChapter.getText().toString()),
						title.getText().toString());
				if (id) {
					Toast.makeText(getBaseContext(), "Update Succeed.", Toast.LENGTH_SHORT).show();
					finish();
					//Intent i = new Intent(getBaseContext(),MainActivity.class);
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//startActivity(i);
				} else {
					Toast.makeText(getBaseContext(), "Update Failed.", Toast.LENGTH_SHORT).show();
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
	private void clearAll() {
		// TODO Auto-generated method stub
		txtName.setText("");
		txtUrl.setText("");
		txtChapter.setText("");
		title.setText("");
	}

}
