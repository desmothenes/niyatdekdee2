package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;

public class EditForm extends Activity {
    private DatabaseAdapter db;
    private Button saveButton;
    private TextView txtName;
    private TextView txtUrl;
    private TextView txtChapter;
    private TextView title;
    private long rowId;
    private int listItemName;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        finish();
        setResult(RESULT_CANCELED);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.edit_form);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (customTitleSupported) {

            //ตั้งค่า custom titlebar จาก custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);


            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.nonbar);
            TextView title = (TextView) findViewById(R.id.textViewBar);
            title.setText(" แก้ไข");
            switch (Integer.parseInt(Setting.getColorSelectSetting(getApplicationContext()))) {
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
            //เชื่อม btnSearch btnDirection เข้ากับ View
            ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);

            btnDirection.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        db = new DatabaseAdapter(getApplicationContext());
        saveButton = (Button) findViewById(R.id.button3);
        txtName = (TextView) findViewById(R.id.LongReadText);
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
            listItemName = intent.getIntExtra("listItemName", -1);
        }

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                db.open();
                boolean id;
                try {
                    id = db.updateNiyay(rowId,
                            txtName.getText().toString(),
                            txtUrl.getText().toString(),
                            Integer.parseInt(txtChapter.getText().toString()),
                            title.getText().toString());
                    MainActivity.niyayTable.get(listItemName)[1] = txtName.getText().toString();
                    MainActivity.niyayTable.get(listItemName)[2] = txtUrl.getText().toString();
                    MainActivity.niyayTable.get(listItemName)[3] = txtChapter.getText().toString();
                    MainActivity.niyayTable.get(listItemName)[4] = title.getText().toString();
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getBaseContext(), "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข", Toast.LENGTH_SHORT).show();
                    return;
                }
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
