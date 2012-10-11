package com.niyatdekdee.notfy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Find extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_find, menu);
		return true;
	}
}
