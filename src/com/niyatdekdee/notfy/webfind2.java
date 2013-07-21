package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;

/**
 * Created by E425 on 12/6/2556.
 */
public class webfind2 extends Activity {
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        //webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.OFF);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(webView);

        Display display = getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.5);  // deprecated
        int height = display.getHeight();  // deprecated

        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);
/*        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        Intent intent = getIntent();
        Document doc = null;
        try {
            String from;
            if ((from = intent.getStringExtra("from")) != null) {
                if (from.equals("gp")) {
                    String story_type = intent.getStringExtra("story_type");
                    String main = intent.getStringExtra("main");
                    String sub = intent.getStringExtra("sub");
                    String isend = intent.getStringExtra("isend");
                    String sort = intent.getStringExtra("sort");
                    String url = String.format("http://www.dek-d.com/writer/frame.php?page=1&main=%S&sub=%S&isend=%s&story_type=%s&sort=%s",
                            main, sub, isend, story_type, sort);
                    doc = Jsoup.connect(url).get();
                } else {
                    finish();
                }
            } else {
                int count_loop = 0;
                while (doc == null && count_loop < 3) {
                    doc = Jsoup.connect("http://www.dek-d.com/writer/frame.php").timeout(12000).get();
                    count_loop++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc == null) {
            Toast.makeText(this, "การเชื่อมต่อมีปัญหา กรุณาตรวจสอบอินเตอร์เน็ต", Toast.LENGTH_LONG);
            return;
        }

        for (Element item : doc.select("#topline")) {
            item.remove();
        }//.attr("style","display:none");
        for (Element item : doc.select("#head_bar")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select("#top5")) {
            item.empty();
        }
        for (Element item : doc.select(".top5")) {
            item.empty();
        }
        for (Element item : doc.select(".menu-box")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select(".asmenu")) {
            item.attr("style", "display:none");
        }/*
        for (Element item : doc.select("body")) {
            item.attr("leftmargin","0");
            item.attr("topmargin","0");
        }*/
        //doc.select(".wrapper").attr("style","width: 100%;");
        //doc.select(".fr-box").append("<span></span>");
        for (Element item : doc.select(".boxgo")) {
            item.empty();
            item.append("<button onclick=\" return searchsort(1)\">ค้นหา</button>");
        }
        if (android.os.Build.VERSION.SDK_INT > 10) {
            for (Element item : doc.select(".wrapper")) {
                item.attr("style", "margin:0 auto; width: " + Integer.toString(width) + "px;");
            }
        } else {
            for (Element item : doc.select(".wrapper")) {
                item.attr("style", "margin:0 auto; width: " + Integer.toString(width) + "px; float: left;");
            }
        }

        for (Element item : doc.select(".top15")) {
            item.attr("style", "display:none;");
        }
        for (Element item : doc.select(".section1")) {
            item.attr("style", "display:block;");
        }
        for (Element item : doc.select("#frame-right")) {
            item.attr("style", "display:block;");
        }/*
        for (Element item : doc.select("body")) {
            item.attr("style", "margin: 0px auto; text-align: center; width: "+ Integer.toString(width) +"px;");
        }*/
        for (Element item : doc.select("#headmenu")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select("#footer")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select("#forslider")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select("#frame-left")) {
            item.attr("style", "display:none");
        }
        for (Element item : doc.select(".fr-box")) {
            item.attr("style", "text-align: center;");
        }
        for (Element item : doc.select(".paging")) {
            item.attr("style", "");//"margin: 0px auto; text-align: center; width: 100%;");
        }/*
        for (Element item : doc.select(".fr-book")) {
            item.attr("style","margin: 0px auto; text-align: center; width: "+ Integer.toString(width) +"px;");
        }*/
        for (Element item : doc.select(".fr-box-bottom")) {
            item.remove();
        }
        for (Element item : doc.select(".block")) {
            item.remove();
        }
        for (Element item : doc.select(".link1")) {
            item.remove();
        }
        for (Element item : doc.select(".app-left")) {
            item.remove();
        }
        for (Element item : doc.select("div[style^=width: 690px;]")) {
            item.remove();
        }/*
        for (Element img : doc.select("img")) {
            img.remove();
        }*/
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.setHorizontalScrollbarOverlay(false);
        webView.loadDataWithBaseURL("http://www.dek-d.com/writer/frame.php", doc.html().replace("$('#menu0'+(parseInt(mainsub[0])+1)).show();", "").replace("$('#meu-0'+mainsub[0]).show();", "").replace("$('div.menu-box').show();", "").replace("$('#menu01').show();", "").replace("$('#top5').empty().append(data.statKey);", "$('#top5').empty();").replace("$('.top5').empty().append(data.top5);", "$('.top5').empty();").replace("hideshowtop();", "").replace("$('.top5').empty().append('<li style=\"padding:52px 0px 52px 310px;\"><img src=\"/writer/img/bigrotation.gif\" width=32 height=32 /></li>');", "").replace("$('ul.fr-book').empty().append('<li style=\"padding-left:310px;\"><img src=\"/writer/img/bigrotation.gif\" width=32 height=32 /></li>');", "").replace("$('.top15').empty().append('Top 5');", "").replace("/writer/frame2012.css?ver=0.9", "").replace("</head>", "<style type=\"text/css\">\n" +
                "body  {\n" +
                "    margin: 0px auto; \n" +
                //"    width: " +Integer.toString(width)+ "px;\n" +
                "    padding: 0;\n" +
                "    text-align: center;\n" +
                "    overflow:hidden;\n" +
                "}\n" +/*
                ".wrapper {\n" +
                "width: 1000px;\n" +
                "padding: 0;\n" +
                "margin: auto;\n" +
                "}\n" +*/
                ".top5 \n" +
                "{\n" +
                "   display:block;" +
                "}" +
                ".top5 ul\n" +
                "{\n" +
                "   list-style-type:none;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   overflow:hidden;\n" +
                "   text-align:center;\n" +
                "}\n" +/*
                ".fr-number ul\n" +
                "{\n" +
                "float: center;\n" +
                "list-style-type:none;\n" +
                "margin:0;\n" +
                "padding:0;\n" +
                "width: 40%;\n" +
                "overflow:hidden;\n" +
                "}\n" +
                ".fr-book li \n" +
                "{\n" +
                "float:center;\n" +
                "margin: 0px auto;\n" +
                "width: 30%;\n" +
                "}\n" +*/
                "a {\n" +
                "display:block;\n" +
                "width:200px;\n" +
                "float:center;\n" +
                "margin: 0px auto;\n" +
                "background-color:#bbbbbb;\n" +
                "}\n" +
                ".nu2 li\n" +
                "{\n" +
                "display:block;\n" +
                "width:15px;\n" +
                "background-color:#dddddd;\n" +
                "}\n" +
                "li\n" +
                "{\n" +
                //"float:left;\n" +
                "margin: 0px auto;\n" +
                "}\n" +
                ".fr-book a\n" +
                "{\n" +
                "   display:block;\n" +
                "   width:240px;\n" +
                "   background-color:#dddddd;\n" +
                "}\n" +
                ".fr-book ul\n" +
                "{\n" +
                "   display:block;\n" +
                "   width:240px;\n" +
                "   background-color:#dddddd;\n" +
                "}\n" +
                ".fr-f-m-ora\n" +
                "{\n" +
                "   font-size:16px;\n" +
                "}\n" +
                "#top5 {\n" +
                "  margin-bottom:20px;\n" +
                "}\n" +
                ".paging {\n" +
                "   float:left;\n" +
                "   width:100%;\n" +
                "   overflow:hidden;\n" +
                "   position:relative;\n" +
                "   margin-bottom:20px;\n" +
                "   margin-top:20px\n" +
                "}\n" +
/*                "ul {\n" +
                "   clear:left;\n" +
                "   float:none;\n" +
                "   list-style:none;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                //
                "   left:50%;\n" +
                "   text-align:center;\n" +
                "}\n" +
                "ul li {\n" +
                "   display:block;\n" +
                "   list-style:none;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                "   left:-50%;\n" +
                "}\n" +*/
                ".paging ul {\n" +
                "   clear:left;\n" +
                "   float:left;\n" +
                "   list-style:none;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                //"   left:50%;\n" +
                "   text-align:center;\n" +
                "}\n" +
                ".paging ul li {\n" +
                "   display:block;\n" +
                "   list-style:none;\n" +
                "   float:left;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                //"   left:-50%;\n" +
                "}\n" +
                /*".fr-book ul {\n" +
                "   clear:left;\n" +
                "   float:left;\n" +
                "   list-style:none;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                //"   left:50%;\n" +
                "   text-align:center;\n" +
                "}\n" +
                ".fr-book ul li {\n" +
                "   display:block;\n" +
                "   list-style:none;\n" +
                "   float:left;\n" +
                "   margin:0;\n" +
                "   padding:0;\n" +
                "   position:relative;\n" +
                //"   right:50%;\n" +
                "}\n" +
                */".paging ul li a {\n" +
                "   display:block;\n" +
                "   margin:0 0 0 1px;\n" +
                "   padding:3px 10px;\n" +
                "   background:#ddd;\n" +
                "   color:#000;\n" +
                "   width:20%;\n" +
                "   text-decoration:none;\n" +
                "   line-height:1.3em;\n" +
                "}\n</style></head>").replace("hideshowtop();", ""), "text/html", "utf-8", null);

        //ContextWrapper cw = new ContextWrapper(getBaseContext());
        /*File temp = new File(*//*cw.getDir("temp", Context.MODE_PRIVATE)*//*Environment.getExternalStorageDirectory(), "niyay_temp.html");
        Toast.makeText(this,temp.getAbsolutePath(),Toast.LENGTH_LONG);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
            bw.write(doc.html().replace("$('#menu0'+(parseInt(mainsub[0])+1)).show();","").replace("$('#meu-0'+mainsub[0]).show();","").replace("$('div.menu-box').show();","").replace("$('#menu01').show();","").replace("$('#top5').empty().append(data.statKey);","$('#top5').empty();").replace("$('.top5').empty().append(data.top5);","$('.top5').empty();").replace("/writer/frame2012.css?ver=0.9","").replace("</head>","<style type=\"text/css\">\n" +
                    "body  {\n" +
                    "    margin: 0; \n" +
                    "    padding: 0;\n" +
                    "    text-align: center;\n" +
                    "}\n" +
                    ".top5 \n" +
                    "{\n" +
                    "display:block;" +
                    "}" +
                    ".top5 ul\n" +
                    "{\n" +
                    "float:none;\n" +
                    "list-style-type:none;\n" +
                    "margin:0;\n" +
                    "padding:0;\n" +
                    "overflow:hidden;\n" +
                    "   text-align:center;\n" +
                    "}\n" +*//*
                ".fr-number ul\n" +
                "{\n" +
                "float: center;\n" +
                "list-style-type:none;\n" +
                "margin:0;\n" +
                "padding:0;\n" +
                "width: 40%;\n" +
                "overflow:hidden;\n" +
                "}\n" +
                ".fr-book li \n" +
                "{\n" +
                "float:center;\n" +
                "margin: 0px auto;\n" +
                "width: 30%;\n" +
                "}\n" +*//*
                    "a {\n" +
                    "display:block;\n" +
                    "width:100%;\n" +
                    "float:center;\n" +
                    "margin: 0px auto;\n" +
                    "background-color:#bbbbbb;\n" +
                    "}\n" +
                    ".nu2 li\n" +
                    "{\n" +
                    "display:block;\n" +
                    "width:15px;\n" +
                    "background-color:#dddddd;\n" +
                    "}\n" +
                    "li\n" +
                    "{\n" +
                    "float:left;\n" +
                    "margin: 0px auto;\n" +
                    "}\n" +
                    ".fr-book a\n" +
                    "{\n" +
                    "display:block;\n" +
                    "width:300px;\n" +
                    "background-color:#dddddd;\n" +
                    "}\n" +
                    ".fr-f-m-ora\n" +
                    "{\n" +
                    "font-size:16px;\n" +
                    "}\n" +
                    "#top5 {\n" +
                    "  margin-bottom:20px;\n" +
                    "}\n"+
                    ".paging {\n" +
                    "   float:left;\n" +
                    "   width:70%;\n" +
                    //"   overflow:hidden;\n" +
                    "   position:relative;\n" +
                    "   margin-bottom:20px;\n" +
                    "   margin-top:20px\n" +
                    "}\n" +
                    "ul {\n" +
                    "   clear:left;\n" +
                    "   float:none;\n" +
                    "   list-style:none;\n" +
                    "   margin:0;\n" +
                    "   padding:0;\n" +
                    "   position:relative;\n" +
                    "   left:50%;\n" +
                    "   text-align:center;\n" +
                    "}\n" +
                    "ul li {\n" +
                    "   display:block;\n" +
                    "   list-style:none;\n" +
                    "   margin:0;\n" +
                    "   padding:0;\n" +
                    "   position:relative;\n" +
                    "   right:50%;\n" +
                    "}\n" +
                    ".paging ul {\n" +
                    "   clear:left;\n" +
                    "   float:left;\n" +
                    "   list-style:none;\n" +
                    "   margin:0;\n" +
                    "   padding:0;\n" +
                    "   position:relative;\n" +
                    "   left:50%;\n" +
                    "   text-align:center;\n" +
                    "}\n" +
                    ".paging ul li {\n" +
                    "   display:block;\n" +
                    "   list-style:none;\n" +
                    "   float:left;\n" +
                    "   margin:0;\n" +
                    "   padding:0;\n" +
                    "   position:relative;\n" +
                    "   right:20%;\n" +
                    "}\n" +
                    ".paging ul li a {\n" +
                    "   display:block;\n" +
                    "   margin:0 0 0 1px;\n" +
                    "   padding:3px 10px;\n" +
                    "   background:#ddd;\n" +
                    "   color:#000;\n" +
                    "   width:20%;\n" +
                    "   text-decoration:none;\n" +
                    "   line-height:1.3em;\n" +
                    "}\n</style></head>").replace("hideshowtop();",""));
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

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

                webView.loadUrl("javascript: var elem = document.getElementById('forslider');" +
                        "var x = 0; var y = 0;" +
                        "    while (elem != null) {" +
                        "		y += elem.offsetTop;" +
                        "	elem = elem.offsetParent;" +
                        "}" +
                        "window.scrollTo(x, y);");
            }
/*

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.contains("ajax")) {
                    Log.e("url", url);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            webView.loadUrl("javascript: document.getElementById(\"head_bar\").style.display = \"none\";\n" +
                                    "document.getElementById(\"headmenu\").style.display = \"none\";\n" +
                                    "document.getElementById(\"footer\").style.display = \"none\";\n" +
                                    "document.getElementById(\"frame-left\").style.display = \"none\";\n" +
                                    "document.getElementById(\"frame-right\").style.float = \"left\";\n" +
                                    "document.getElementById(\"frame-right\").style.width = \"100%\";\n" +
                                    "var item = document.getElementsByClassName(\"menu-box\");\n" +
                                    "for (i=0;i<item.length;i++) item[i].style.display = \"none\";\n" +
                                    "item = document.getElementsByClassName(\"asmenu\");\n" +
                                    "for (i=0;i<item.length;i++) item[i].style.display = \"none\";\n" +
                                    "item = document.getElementsByClassName(\"section1\");\n" +
                                    "for (i=0;i<item.length;i++) {\n" +
                                    "    item[i].style.display = \"block\";\n" +
                                    "    item[i].style.float = \"left\";\n" +
                                    "}\n" +
                                    "item = document.getElementsByClassName(\"wrapper\");\n" +
                                    "for (i=0;i<item.length;i++) {\n" +
                                    "    item[i].style.width = \"100%\";\n" +
                                    "    item[i].style.float = \"left\";\n" +
                                    "}\n" +
                                    "document.getElementById(\"topline\").remove();");
                        }
                    }, 1500);
                }
            }
*/

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
                    //if (!url.startsWith("http://") && !url.startsWith("https://"))
                    //url = "http://" + url;
                    //Log.v("url", url);
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;

            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    class MyJavaScriptInterface {
        public void processHTML(String html) {
            File temp = new File(Environment.getExternalStorageDirectory(), "niyay_temp.html");
            Toast.makeText(getApplicationContext(), temp.getAbsolutePath(), Toast.LENGTH_LONG);

            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                bw.write(html);
                bw.flush();
                bw.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
