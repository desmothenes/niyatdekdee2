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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        webView.loadUrl("http://my.dek-d.com/dekdee/control/writer_favorite.php");
        //webView.loadDataWithBaseURL("http://my.dek-d.com/dekdee/my.id_station/login.php?refer=/dekdee/control/writer.php", html, "text/html", "utf-8", null);
        final Activity activity = this;
        mContext = this.getApplicationContext();

        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                activity.setProgress(progress * 100);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                mWebviewPop = new WebView(mContext);
                mWebviewPop.setVerticalScrollBarEnabled(false);
                mWebviewPop.setHorizontalScrollBarEnabled(false);
                mWebviewPop.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        setProgressBarIndeterminateVisibility(false);
                        setProgressBarVisibility(false);
                /*if(url.startsWith("https://m.facebook.com/dialog/oauth")){
                    String redirectUrl = "http://my.dek-d.com/dekdee/control/writer_favorite.php";
                    view.loadUrl(redirectUrl);
                    return;
                }*/
                        super.onPageFinished(view, url);


                /*webView.loadUrl("javascript: var item = document.getElementsByClassName('ui-footer');" +
                        "item[0].style.display = 'none';");*/

                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                                   SslError error) {
                        Log.d("onReceivedSslError", "onReceivedSslError");
                        //super.onReceivedSslError(view, handler, error);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        String host = Uri.parse(url).getHost();
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
                        return true;

                    }
                });
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
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                setProgressBarIndeterminateVisibility(false);
                setProgressBarVisibility(false);
                /*if(url.startsWith("https://m.facebook.com/dialog/oauth")){
                    String redirectUrl = "http://my.dek-d.com/dekdee/control/writer_favorite.php";
                    view.loadUrl(redirectUrl);
                    return;
                }*/
                super.onPageFinished(view, url);


                /*webView.loadUrl("javascript: var con = document.getElementsByTagName('table')[2];" +
                        "con.innerHTML = \"<a href=\\\"#\\\" onclick=\\\"fbLogin( true , '' ); return false;\\\" id=\\\"fb-sprite\\\" class=\\\"badge-large\\\"></a>\";");*/

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                Log.d("onReceivedSslError", "onReceivedSslError");
                //super.onReceivedSslError(view, handler, error);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String host = Uri.parse(url).getHost();
                if (host.equals("my.dek-d.com")) {
                    // This is my web site, so do not override; let my WebView load
                    // the page
                    if (mWebviewPop != null) {
                        mWebviewPop.setVisibility(View.GONE);
                        mContainer.removeView(mWebviewPop);
                        mWebviewPop = null;
                    }
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
                    }
                    return false;
                }
                if (host.equals("m.facebook.com")) {
                    return false;
                }
                view.loadUrl(url);
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }


}