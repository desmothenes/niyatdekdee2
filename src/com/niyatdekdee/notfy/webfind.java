package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by E425 on 12/6/2556.
 */
public class webfind extends Activity {
    private WebView webView;
    private boolean result;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        webView = new WebView(this);
        setContentView(webView);

        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);
        webView.getSettings().setJavaScriptEnabled(true);
 /*           Document doc = Jsoup.connect("http://www.dek-d.com/writer/frame.php").get();
            doc.select("#topline").remove();//.attr("style","display:none");
            doc.select("#head_bar").attr("style","display:none");
            doc.select(".asmenu").attr("style","display:none");
            doc.select(".wrapper").attr("style","width: 100%;");
            doc.select(".fr-box").append("<span></span>");
            doc.select(".fr-box-bottom").remove();
            doc.select(".section1").attr("style","display:block; float: left;");
            doc.select("#headmenu").attr("style","display:none");
            doc.select("#footer").attr("style","display:none");
            doc.select("#frame-left").attr("style","display:none");
            doc.select("div[style^=width: 690px;]").attr("style","display:none");*/
        //Elements sun11 = doc.select(".sun11");

        webView.loadUrl("http://m.dek-d.com/writertop.php");
        //ContextWrapper cw = new ContextWrapper(getBaseContext());
           /* File temp = new File(*//*cw.getDir("temp", Context.MODE_PRIVATE)*//*Environment.getExternalStorageDirectory(), "niyay_temp");
                //System.out.println(temp.getAbsolutePath());

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));

                bw.write(Jsoup.parse(doc.select(".sun11").html(),"http://www.dek-d.com/writer/frame.php").html());
                bw.flush();
                bw.close();*/
        final Activity activity = this;

        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                activity.setProgress(progress * 100);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(webView, url2);

                setProgressBarIndeterminateVisibility(false);
                setProgressBarVisibility(false);

                webView.loadUrl("javascript: var item = document.getElementsByClassName('ui-footer');" +
                        "item[0].style.display = 'none';");

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("writertop.php") || url.contains("writerindex.php")) {
                    view.loadUrl(url);
                } else if (url.contains("view.php?id=")) {

                    final String unum = MyAppClass.findnum(url, "id=", getBaseContext());
                    if (unum == null || unum.equals("")) {
                        Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    //Log.v("unum", unum);
                    url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=";
                    Intent i = new Intent(getBaseContext(), InsertForm.class);
                    //Log.v("url", url);
                    i.putExtra("fromMsearch", true);
                    i.putExtra("url", url);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //finish();
                    startActivityForResult(i, 0);
                } else {
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            result = true;
        } else if (!result) {
            setResult(RESULT_CANCELED);
        }
    }
}