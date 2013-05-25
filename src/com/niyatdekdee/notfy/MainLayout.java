package com.niyatdekdee.notfy;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ViewFlipper;
import android.app.Activity;

public class MainLayout extends Activity {
	private ViewFlipper vf ;
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_layout);
        vf  = (ViewFlipper) findViewById(R.id.viewFlipper1);

        vf.setInAnimation(this,android.R.anim.fade_in);
        vf.setOutAnimation(this, android.R.anim.fade_out);
        TimePicker timepicker = (TimePicker) vf.findViewById(R.id.timePicker1);
        timepicker.setIs24HourView(true);
        Button back = (Button) findViewById(R.id.wi_back);
        back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vf.showPrevious();
			}
        	
        });
        Button next = (Button) findViewById(R.id.wi_next);
        next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vf.showNext();
			}
        	
        });
    }
}
