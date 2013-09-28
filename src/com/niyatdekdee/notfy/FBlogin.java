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
        String html = "<html>\n" +
                "<head>\n" +
                "\n" +
                "<link rel=\"stylesheet\" href=\"http://my.dek-d.com/a/style/newmyid.css\" type=\"text/css\">\n" +
                "<link href=\"http://my.dek-d.com/a/control/controlpanel.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "<script id=\"facebook-jssdk\" async=\"\" src=\"http://my.dek-d.com//connect.facebook.net/en_US/all.js\"></script><script type=\"text/javascript\" src=\"http://tool.dek-d.com/js/ToolbarApi.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://my.dek-d.com/a/js/jquery-1.4.4.min.js\"></script><style type=\"text/css\"></style>\n" +
                "<script type=\"text/javascript\" src=\"http://my.dek-d.com/a/js/fb.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "function MM_swapImgRestore() { //v3.0\n" +
                "  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;\n" +
                "}\n" +
                "function MM_preloadImages() { //v3.0\n" +
                "  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();\n" +
                "    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)\n" +
                "    if (a[i].indexOf(\"#\")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}\n" +
                "}\n" +
                "\n" +
                "function MM_findObj(n, d) { //v4.01\n" +
                "  var p,i,x;  if(!d) d=document; if((p=n.indexOf(\"?\"))>0&&parent.frames.length) {\n" +
                "    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}\n" +
                "  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];\n" +
                "  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);\n" +
                "  if(!x && d.getElementById) x=d.getElementById(n); return x;\n" +
                "}\n" +
                "\n" +
                "function MM_swapImage() { //v3.0\n" +
                "  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)\n" +
                "   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}\n" +
                "}\n" +
                "\n" +
                "document.onkeydown = function(event){\n" +
                "\t\tvar event = event || window.event;\n" +
                "\t\tvar key = event.keyCode || event.which;\n" +
                "\t\tif(key == 13) form1.submit();\n" +
                "}\n" +
                "\n" +
                "function fb_callback( username )\n" +
                "{\n" +
                "\tvar url = '/petri/control/writer.php';\n" +
                "\tif( url == '' )\n" +
                "\t\twindow.location.href = '/' + username + '/control/';\n" +
                "\telse\n" +
                "\t\twindow.location.href = url;\n" +
                "}\n" +
                "</script>\n" +
                "<style type=\"text/css\">.fb_hidden{position:absolute;top:-10000px;z-index:10001}\n" +
                ".fb_invisible{display:none}\n" +
                ".fb_reset{background:none;border:0;border-spacing:0;color:#000;cursor:auto;direction:ltr;font-family:\"lucida grande\", tahoma, verdana, arial, sans-serif;font-size:11px;font-style:normal;font-variant:normal;font-weight:normal;letter-spacing:normal;line-height:1;margin:0;overflow:visible;padding:0;text-align:left;text-decoration:none;text-indent:0;text-shadow:none;text-transform:none;visibility:visible;white-space:normal;word-spacing:normal}\n" +
                ".fb_reset > div{overflow:hidden}\n" +
                ".fb_link img{border:none}\n" +
                ".fb_dialog{background:rgba(82, 82, 82, .7);position:absolute;top:-10000px;z-index:10001}\n" +
                ".fb_dialog_advanced{padding:10px;-moz-border-radius:8px;-webkit-border-radius:8px;border-radius:8px}\n" +
                ".fb_dialog_content{background:#fff;color:#333}\n" +
                ".fb_dialog_close_icon{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat scroll 0 0 transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif);cursor:pointer;display:block;height:15px;position:absolute;right:18px;top:17px;width:15px;top:8px\\9;right:7px\\9}\n" +
                ".fb_dialog_mobile .fb_dialog_close_icon{top:5px;left:5px;right:auto}\n" +
                ".fb_dialog_padding{background-color:transparent;position:absolute;width:1px;z-index:-1}\n" +
                ".fb_dialog_close_icon:hover{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat scroll 0 -15px transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif)}\n" +
                ".fb_dialog_close_icon:active{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat scroll 0 -30px transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif)}\n" +
                ".fb_dialog_loader{background-color:#f2f2f2;border:1px solid #606060;font-size:24px;padding:20px}\n" +
                ".fb_dialog_top_left,\n" +
                ".fb_dialog_top_right,\n" +
                ".fb_dialog_bottom_left,\n" +
                ".fb_dialog_bottom_right{height:10px;width:10px;overflow:hidden;position:absolute}\n" +
                ".fb_dialog_top_left{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0 0;left:-10px;top:-10px}\n" +
                ".fb_dialog_top_right{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0 -10px;right:-10px;top:-10px}\n" +
                ".fb_dialog_bottom_left{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0 -20px;bottom:-10px;left:-10px}\n" +
                ".fb_dialog_bottom_right{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0 -30px;right:-10px;bottom:-10px}\n" +
                ".fb_dialog_vert_left,\n" +
                ".fb_dialog_vert_right,\n" +
                ".fb_dialog_horiz_top,\n" +
                ".fb_dialog_horiz_bottom{position:absolute;background:#525252;filter:alpha(opacity=70);opacity:.7}\n" +
                ".fb_dialog_vert_left,\n" +
                ".fb_dialog_vert_right{width:10px;height:100%}\n" +
                ".fb_dialog_vert_left{margin-left:-10px}\n" +
                ".fb_dialog_vert_right{right:0;margin-right:-10px}\n" +
                ".fb_dialog_horiz_top,\n" +
                ".fb_dialog_horiz_bottom{width:100%;height:10px}\n" +
                ".fb_dialog_horiz_top{margin-top:-10px}\n" +
                ".fb_dialog_horiz_bottom{bottom:0;margin-bottom:-10px}\n" +
                ".fb_dialog_iframe{line-height:0}\n" +
                ".fb_dialog_content .dialog_title{background:#6d84b4;border:1px solid #3b5998;color:#fff;font-size:14px;font-weight:bold;margin:0}\n" +
                ".fb_dialog_content .dialog_title > span{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yd/r/Cou7n-nqK52.gif)\n" +
                "no-repeat 5px 50%;float:left;padding:5px 0 7px 26px}\n" +
                "body.fb_hidden{-webkit-transform:none;height:100%;margin:0;left:-10000px;overflow:visible;position:absolute;top:-10000px;width:100%\n" +
                "}\n" +
                ".fb_dialog.fb_dialog_mobile.loading{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ya/r/3rhSv5V8j3o.gif)\n" +
                "white no-repeat 50% 50%;min-height:100%;min-width:100%;overflow:hidden;position:absolute;top:0;z-index:10001}\n" +
                ".fb_dialog.fb_dialog_mobile.loading.centered{max-height:590px;min-height:590px;max-width:500px;min-width:500px}\n" +
                "#fb-root #fb_dialog_ipad_overlay{background:rgba(0, 0, 0, .45);position:absolute;left:0;top:0;width:100%;min-height:100%;z-index:10000}\n" +
                "#fb-root #fb_dialog_ipad_overlay.hidden{display:none}\n" +
                ".fb_dialog.fb_dialog_mobile.loading iframe{visibility:hidden}\n" +
                ".fb_dialog_content .dialog_header{-webkit-box-shadow:white 0 1px 1px -1px inset;background:-webkit-gradient(linear, 0 0, 0 100%, from(#738ABA), to(#2C4987));border-bottom:1px solid;border-color:#1d4088;color:#fff;font:14px Helvetica, sans-serif;font-weight:bold;text-overflow:ellipsis;text-shadow:rgba(0, 30, 84, .296875) 0 -1px 0;vertical-align:middle;white-space:nowrap}\n" +
                ".fb_dialog_content .dialog_header table{-webkit-font-smoothing:subpixel-antialiased;height:43px;width:100%\n" +
                "}\n" +
                ".fb_dialog_content .dialog_header td.header_left{font-size:12px;padding-left:5px;vertical-align:middle;width:60px\n" +
                "}\n" +
                ".fb_dialog_content .dialog_header td.header_right{font-size:12px;padding-right:5px;vertical-align:middle;width:60px\n" +
                "}\n" +
                ".fb_dialog_content .touchable_button{background:-webkit-gradient(linear, 0 0, 0 100%, from(#4966A6),\n" +
                "color-stop(0.5, #355492), to(#2A4887));border:1px solid #29447e;-webkit-background-clip:padding-box;-webkit-border-radius:3px;-webkit-box-shadow:rgba(0, 0, 0, .117188) 0 1px 1px inset,\n" +
                "rgba(255, 255, 255, .167969) 0 1px 0;display:inline-block;margin-top:3px;max-width:85px;line-height:18px;padding:4px 12px;position:relative}\n" +
                ".fb_dialog_content .dialog_header .touchable_button input{border:none;background:none;color:#fff;font:12px Helvetica, sans-serif;font-weight:bold;margin:2px -12px;padding:2px 6px 3px 6px;text-shadow:rgba(0, 30, 84, .296875) 0 -1px 0}\n" +
                ".fb_dialog_content .dialog_header .header_center{color:#fff;font-size:16px;font-weight:bold;line-height:18px;text-align:center;vertical-align:middle}\n" +
                ".fb_dialog_content .dialog_content{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/y9/r/jKEcVPZFk-2.gif) no-repeat 50% 50%;border:1px solid #555;border-bottom:0;border-top:0;height:150px}\n" +
                ".fb_dialog_content .dialog_footer{background:#f2f2f2;border:1px solid #555;border-top-color:#ccc;height:40px}\n" +
                "#fb_dialog_loader_close{float:left}\n" +
                ".fb_dialog.fb_dialog_mobile .fb_dialog_close_button{text-shadow:rgba(0, 30, 84, .296875) 0 -1px 0}\n" +
                ".fb_dialog.fb_dialog_mobile .fb_dialog_close_icon{visibility:hidden}\n" +
                ".fb_iframe_widget{display:inline-block;position:relative}\n" +
                ".fb_iframe_widget span{display:inline-block;position:relative;text-align:justify}\n" +
                ".fb_iframe_widget iframe{position:absolute}\n" +
                ".fb_iframe_widget_lift{z-index:1}\n" +
                ".fb_hide_iframes iframe{position:relative;left:-10000px}\n" +
                ".fb_iframe_widget_loader{position:relative;display:inline-block}\n" +
                ".fb_iframe_widget_fluid{display:inline}\n" +
                ".fb_iframe_widget_fluid span{width:100%}\n" +
                ".fb_iframe_widget_loader iframe{min-height:32px;z-index:2;zoom:1}\n" +
                ".fb_iframe_widget_loader .FB_Loader{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/y9/r/jKEcVPZFk-2.gif) no-repeat;height:32px;width:32px;margin-left:-16px;position:absolute;left:50%;z-index:4}\n" +
                ".fb_connect_bar_container div,\n" +
                ".fb_connect_bar_container span,\n" +
                ".fb_connect_bar_container a,\n" +
                ".fb_connect_bar_container img,\n" +
                ".fb_connect_bar_container strong{background:none;border-spacing:0;border:0;direction:ltr;font-style:normal;font-variant:normal;letter-spacing:normal;line-height:1;margin:0;overflow:visible;padding:0;text-align:left;text-decoration:none;text-indent:0;text-shadow:none;text-transform:none;visibility:visible;white-space:normal;word-spacing:normal;vertical-align:baseline}\n" +
                ".fb_connect_bar_container{position:fixed;left:0 !important;right:0 !important;height:42px !important;padding:0 25px !important;margin:0 !important;vertical-align:middle !important;border-bottom:1px solid #333 !important;background:#3b5998 !important;z-index:99999999 !important;overflow:hidden !important}\n" +
                ".fb_connect_bar_container_ie6{position:absolute;top:expression(document.compatMode==\"CSS1Compat\"? document.documentElement.scrollTop+\"px\":body.scrollTop+\"px\")}\n" +
                ".fb_connect_bar{position:relative;margin:auto;height:100%;width:100%;padding:6px 0 0 0 !important;background:none;color:#fff !important;font-family:\"lucida grande\", tahoma, verdana, arial, sans-serif !important;font-size:13px !important;font-style:normal !important;font-variant:normal !important;font-weight:normal !important;letter-spacing:normal !important;line-height:1 !important;text-decoration:none !important;text-indent:0 !important;text-shadow:none !important;text-transform:none !important;white-space:normal !important;word-spacing:normal !important}\n" +
                ".fb_connect_bar a:hover{color:#fff}\n" +
                ".fb_connect_bar .fb_profile img{height:30px;width:30px;vertical-align:middle;margin:0 6px 5px 0}\n" +
                ".fb_connect_bar div a,\n" +
                ".fb_connect_bar span,\n" +
                ".fb_connect_bar span a{color:#bac6da;font-size:11px;text-decoration:none}\n" +
                ".fb_connect_bar .fb_buttons{float:right;margin-top:7px}\n" +
                ".fb_edge_widget_with_comment{position:relative;*z-index:1000}\n" +
                ".fb_edge_widget_with_comment span.fb_edge_comment_widget{position:absolute}\n" +
                ".fb_edge_widget_with_comment span.fb_send_button_form_widget{z-index:1}\n" +
                ".fb_edge_widget_with_comment span.fb_send_button_form_widget .FB_Loader{left:0;top:1px;margin-top:6px;margin-left:0;background-position:50% 50%;background-color:#fff;height:150px;width:394px;border:1px #666 solid;border-bottom:2px solid #283e6c;z-index:1}\n" +
                ".fb_edge_widget_with_comment span.fb_send_button_form_widget.dark .FB_Loader{background-color:#000;border-bottom:2px solid #ccc}\n" +
                ".fb_edge_widget_with_comment span.fb_send_button_form_widget.siderender\n" +
                ".FB_Loader{margin-top:0}\n" +
                ".fbpluginrecommendationsbarleft,\n" +
                ".fbpluginrecommendationsbarright{position:fixed !important;bottom:0;z-index:999}\n" +
                ".fbpluginrecommendationsbarleft{left:10px}\n" +
                ".fbpluginrecommendationsbarright{right:10px}</style></head>\n" +
                "<body>\n" +
                "<center>\n" +
                "<a href=\"#\" onclick=\"fbLogin( true , '' ); return false;\" id=\"fb-sprite\" class=\"badge-large\"></a>\n" +
                "\n" +
                "<div id=\"fb-root\" class=\" fb_reset\"><div style=\"position: absolute; top: -10000px; height: 0px; width: 0px;\"><div><iframe name=\"fb_xdm_frame_http\" frameborder=\"0\" allowtransparency=\"true\" scrolling=\"no\" id=\"fb_xdm_frame_http\" aria-hidden=\"true\" title=\"Facebook Cross Domain Communication Frame\" tab-index=\"-1\" src=\"http://static.ak.facebook.com/connect/xd_arbiter.php?version=27#channel=f1c1a338bc&amp;channel_path=%2Fdekdee%2Fmy.id_station%2Flogin.php%3Frefer%3D%2Fpetri%2Fcontrol%2Fwriter.php%26fb_xd_fragment%23xd_sig%3Df1604b9ce%26&amp;origin=http%3A%2F%2Fmy.dek-d.com\" style=\"border: none;\"></iframe><iframe name=\"fb_xdm_frame_https\" frameborder=\"0\" allowtransparency=\"true\" scrolling=\"no\" id=\"fb_xdm_frame_https\" aria-hidden=\"true\" title=\"Facebook Cross Domain Communication Frame\" tab-index=\"-1\" src=\"https://s-static.ak.facebook.com/connect/xd_arbiter.php?version=27#channel=f1c1a338bc&amp;channel_path=%2Fdekdee%2Fmy.id_station%2Flogin.php%3Frefer%3D%2Fpetri%2Fcontrol%2Fwriter.php%26fb_xd_fragment%23xd_sig%3Df1604b9ce%26&amp;origin=http%3A%2F%2Fmy.dek-d.com\" style=\"border: none;\"></iframe></div></div><div style=\"position: absolute; top: -10000px; height: 0px; width: 0px;\"><div></div></div></div>\n" +
                "<script>\n" +
                "\t\twindow.fbAsyncInit = function() {\n" +
                "\t\t\tFB.init({\n" +
                "\t\t\t\tappId: \"193207127471363\",\n" +
                "\t\t\t\tcookie: true,\n" +
                "\t\t\t\txfbml: true,\n" +
                "\t\t\t\toauth: true,\n" +
                "\t\t\t\tstatus: true\n" +
                "\t\t\t});\n" +
                "\t\t\t\n" +
                "\t\t\tif( typeof fbExec != 'undefined' )\n" +
                "\t\t\t\tfbExec.init();\n" +
                "\t\t};\n" +
                "\t\t(function(d){\n" +
                "\t\t     var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];\n" +
                "\t\t     if (d.getElementById(id)) {return;}\n" +
                "\t\t     js = d.createElement('script'); js.id = id; js.async = true;\n" +
                "\t\t     js.src = \"//connect.facebook.net/en_US/all.js\";\n" +
                "\t\t     ref.parentNode.insertBefore(js, ref);\n" +
                "\t\t}(document));\n" +
                "\t\t</script>\n" +
                "</center>\n" +
                "<body>\n" +
                "<html>";
        //webView.loadUrl("http://my.dek-d.com/dekdee/control/writer_favorite.php");
        webView.loadDataWithBaseURL("http://my.dek-d.com/dekdee/my.id_station/login.php?refer=/dekdee/control/writer.php", html, "text/html", "utf-8", null);
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
                        view.loadUrl(url);
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