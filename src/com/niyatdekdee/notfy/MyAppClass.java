package com.niyatdekdee.notfy;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = "http://niyay.jolthai.com/logcat.php", formKey = "",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)

public class MyAppClass extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
        //PreferenceManager.setDefaultValues(this, R.layout.activity_setting, false);
        // TODO Put your application initialization code here.
    }

    static String findnum(String text, String stext, Context context) {
        final int start = text.lastIndexOf(stext) + stext.length();
        if (start - stext.length() == -1) {
            Toast.makeText(context, "Error not correct niyay page", Toast.LENGTH_SHORT).show();
            return null;
        }
        //Log.v("url.length()", Integer.toString(text.length()));
        //	Log.v("start", Integer.toString(start));
        //	Log.v("stext.length()", Integer.toString(stext.length()));
        int len = 0;
        //Log.v("Character", Character.toString(text.charAt(start)));
        for (int i3 = start; i3 < text.length() && Character.isDigit(text.charAt(i3)); i3++) {
            len++;
            //Log.v("Character", Character.toString(text.charAt(i3)));
        }
        //Log.v("len", Integer.toString(len));
        return text.substring(start, start + len);
    }

    static String findnum(String text, String stext) {
        final int start = text.lastIndexOf(stext) + stext.length();
        if (start - stext.length() == -1) {
            //Toast.makeText(context, "Error not correct niyay page", Toast.LENGTH_SHORT).show();
            return null;
        }
        //Log.v("url.length()", Integer.toString(text.length()));
        //	Log.v("start", Integer.toString(start));
        //	Log.v("stext.length()", Integer.toString(stext.length()));
        int len = 0;
        //Log.v("Character", Character.toString(text.charAt(start)));
        for (int i3 = start; i3 < text.length() && Character.isDigit(text.charAt(i3)); i3++) {
            len++;
            //Log.v("Character", Character.toString(text.charAt(i3)));
        }
        //Log.v("len", Integer.toString(len));
        return text.substring(start, start + len);
    }
}//dHFkeDMxeVZyMUxRbnluSzJ6RlNELVE6MQ