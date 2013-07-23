package com.niyatdekdee.notfy;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Flow3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextReadActivity.getScRoll() == -1) setContentView(R.layout.activity_flow3l);
        else setContentView(R.layout.activity_flow3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || super.onKeyDown(keyCode, event);
    }

}
