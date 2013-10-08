package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by E425 on 28/9/2556.
 */
public class FBlogin extends Activity {
    private WebView webView;
    private WebView mWebviewPop;
    private Context mContext;
    private FrameLayout mContainer;
    private boolean result = false;

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

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mContainer = new FrameLayout(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        String html = "<a href=\"#\" onclick=\"fbLogin( true , '' ); return false;\" id=\"fb-sprite\" class=\"badge-large\"></a>\n";
        webView.setWebViewClient(new UriWebViewClient());
        webView.setWebChromeClient(new UriChromeClient());
        webView.loadUrl("https://www.facebook.com/dialog/oauth?client_id=193207127471363&redirect_uri=http://my.dek-d.com/dekdee/control/writer_favorite.php");
        //webView.loadDataWithBaseURL("http://my.dek-d.com/dekdee/my.id_station/login.php?refer=/dekdee/control/writer.php", html, "text/html", "utf-8", null);
        final Activity activity = this;
        mContext = this.getApplicationContext();

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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            //Log.d("shouldOverrideUrlLoading", url);

            if (url.contains("view.php?id=")) {

                final String unum = MyAppClass.findnum(url, "id=", getBaseContext());
                if (unum == null || unum.equals("")) {
                    Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
                    return true;
                }
                url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=" + unum + "&chapter=";
                Intent i = new Intent(getBaseContext(), InsertForm.class);
                i.putExtra("fromMsearch", true);
                i.putExtra("url", url);
                startActivityForResult(i, 0);
                return false;
            }
            if (host.equals("my.dek-d.com")) {
                // This is my web site, so do not override; let my WebView load
                // the page
                if (mWebviewPop != null) {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop = null;
                }
                return false;
            }

            if (host.equals("m.facebook.com")) {
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }

        public void onPageFinished(WebView view, String url) {
            setProgressBarIndeterminateVisibility(false);
            setProgressBarVisibility(false);
            //https://m.facebook.com/login.php?skip_api_login=1&api_key=193207127471363&signed_next=1&next=https%3A%2F%2Fm.facebook.com%2Fdialog%2Foauth%3Fredirect_uri%3Dhttp%253A%252F%252Fstatic.ak.facebook.com%252Fconnect%252Fxd_arbiter.php%253Fversion%253D27%2523cb%253Df10ffb398c%2526domain%253Dmy.dek-d.com%2526origin%253Dhttp%25253A%25252F%25252Fmy.dek-d.com%25252Ffc5ba7c28%2526relation%253Dopener%2526frame%253Df38f0eb594%26display%3Dtouch%26scope%3Demail%252C%2Buser_about_me%252C%2Buser_birthday%252C%2Bpublish_actions%252C%2Bpublish_stream%252C%2Buser_likes%26response_type%3Dtoken%252Csigned_request%26domain%3Dmy.dek-d.com%26client_id%3D193207127471363%26ret%3Dlogin%26sdk%3Djoey&cancel_uri=http%3A%2F%2Fstatic.ak.facebook.com%2Fconnect%2Fxd_arbiter.php%3Fversion%3D27%23cb%3Df10ffb398c%26domain%3Dmy.dek-d.com%26origin%3Dhttp%253A%252F%252Fmy.dek-d.com%252Ffc5ba7c28%26relation%3Dopener%26frame%3Df38f0eb594%26error%3Daccess_denied%26error_code%3D200%26error_description%3DPermissions%2Berror%26error_reason%3Duser_denied%26e2e%3D%257B%257D&display=touch&locale2=en_US&_rdr
                /*if(url.startsWith("https://m.facebook.com/login.php?skip_api_login")){
                    String redirectUrl = "http://my.dek-d.com/dekdee/control/writer_favorite.php";
                    view.loadUrl(redirectUrl);
                    return;
                }*/
            super.onPageFinished(view, url);


            webView.loadUrl("javascript: var con = document.getElementsByTagName('table')[2];" +
                    "con.innerHTML = \"<a href=\\\"#\\\" onclick=\\\"fbLogin( true , '' ); return false;\\\" id=\\\"fb-sprite\\\" class=\\\"badge-large\\\"></a>\";");

        }
    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }
    }
}