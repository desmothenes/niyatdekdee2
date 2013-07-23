package com.niyatdekdee.notfy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView {
    //private boolean flinged = false;;

    private static final int SWIPE_MIN_DISTANCE = 100;
    // private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gd = new GestureDetector(context, sogl);
    }

    GestureDetector gd;
    protected boolean click;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println("onTouchEvent");
        // TODO Auto-generated method stub
        if (!Setting.getsinglecolum(getContext())) return super.onTouchEvent(event);
        if (Setting.getswipe(getContext()))
            gd.onTouchEvent(event);
        else {
            //System.out.println("non getswipe onTouchEvent");
            return super.onTouchEvent(event);
        }
/*		if (flinged) {
            flinged = false;
		}
		if(!click) {
			click = false;
			return super.onTouchEvent(event);
		}*/
        event.setLocation(0, event.getY());
        return true;
        /*		} else {

			return super.onTouchEvent(event);
		}*/
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
        // your fling code here
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            //System.out.println(event2.getX() - event1.getX());
            //System.out.println(Math.abs(velocityX));
            if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i("Swiped", "swipe left");
                pageDown(false);
                //click = false;
                return true;
            } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                pageUp(false);
                Log.i("Swiped", "swipe right");
                //click = false;
                return true;
            } /*else if (event2.getX() - event1.getX() < 10 && Math.abs(velocityX) < 10) {
				click = true;
				return true;
			}*/
            //click = false;
            return false;
        }
    };
}