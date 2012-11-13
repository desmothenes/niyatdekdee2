package com.niyatdekdee.notfy;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;

public class MainLayout extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_layout);

        ImageView contentList = (ImageView) findViewById(R.id.imageView1);
        contentList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				while (MainActivity.LoadPage)
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				Intent i = new Intent(getBaseContext(),MainActivity.class);	
				startActivity(i);
			}        	
        });
        ImageView addIMG = (ImageView) findViewById(R.id.imageView2);
        addIMG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),add_web.class);
				startActivity(i);
			}        	
        });        
        ImageView search = (ImageView) findViewById(R.id.imageView3);
        search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),Fav_add.class);
				startActivity(i);
			}        	
        });           
        ImageView setting = (ImageView) findViewById(R.id.imageView4);
        setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getBaseContext(),Setting.class);
				startActivity(i);
			}        	
        });   
        
/*        new Thread(new Runnable() {
            public void run() {
            	MainActivity.LoadPage = true;
            	MainActivity.context = getBaseContext();
            	if (MainActivity.isOnline())
                MainActivity.showAllBook();
            	else MainActivity.showAllBookOffline();    
            	MainActivity.LoadPage = false;
            }
        }).start();*/
    }
}
