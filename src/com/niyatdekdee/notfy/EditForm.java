package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditForm extends Activity  {
	DatabaseAdapter db;
	Button saveButton;
	TextView txtName;
	TextView txtUrl;
	TextView txtChapter;
	TextView title;
	long rowId;
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		finish();
		startActivity(intent);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.edit_form);
		if (customTitleSupported) {

			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);

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

		db = new DatabaseAdapter(this);      
		saveButton = (Button) findViewById(R.id.button3);
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
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK, resultIntent);
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
	}

}
