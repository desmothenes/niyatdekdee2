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
    private boolean result = false;

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
        String newhtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"th\">\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>Dek-D.com - Writer : คลังนิยายออนไลน์ ใหญ่ที่สุดในไทย กว่า 300,000 นิยายดีๆ สนุกๆ\n" +
                "    จากนักเขียนมีชื่อทั่วไทย</title>\n" +
                "<meta name=\"DESCRIPTION\"\n" +
                "      content=\"ศูนย์รวมนิยายคุณภาพ มีให้เลือกอ่านมากที่สุด นิยาย เรื่องสั้น เรื่องยาว นิยายรักหวานแหวว นิยายรักเศร้าๆ นิยายแฟนตาซี นิยายซึ้งกินใจ และอีกมากมาย อัพเดททุกวัน อ่านกันฟรีๆ\">\n" +
                "<meta name=\"KEYWORDS\" content=\"นิยาย, ค้นหา นิยาย, บทความ, เรื่องสั้น, เรื่องยาว, ฟิค, นิยายออนไลน์, อ่าน นิยาย\">\n" +
                "\n" +
                "<meta name=\"googlebot\" content=\"index,archive,follow\">\n" +
                "<meta name=\"msnbot\" content=\"all,index,follow\">\n" +
                "<meta name=\"robots\" content=\"all,index,follow\">\n" +
                "<base target=\"_blank\">\n" +
                "\n" +
                "<!-- link href=\"http://www.dek-d.com/studyabroad/css/reset200802.css\" rel=\"stylesheet\" type=\"text/css\" / -->\n" +
                "<link href=\"http://www.dek-d.com/08dekd.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.dek-d.com/resource/css/11dekdhome.min.css?version=2.0\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.dek-d.com/resource/css/2010content.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.dek-d.com/resource/css/2010pagetab.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.dek-d.com/2011menu/mainmenu.min.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.dek-d.com/10dekd.css?version=2.0\">\n" +
                "<link href=\"http://www.dek-d.com/writer/frame2012.css?ver=0.9a\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "<script id=\"facebook-jssdk\" async=\"\" src=\"http://www.dek-d.com//connect.facebook.net/en_US/all.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://www.dek-d.com/resource/js/jquery-latest.min.js\"></script>\n" +
                "<style type=\"text/css\"></style>\n" +
                "<script language=\"JavaScript\" type=\"text/javascript\" src=\"http://www.dek-d.com/resource/js/paging_utf8.min.js\"></script>\n" +
                "<link rel=\"stylesheet\" href=\"http://www0.dek-d.com/template/toolbar.css\">\n" +
                "<link rel=\"stylesheet\" href=\"http://www0.dek-d.com/template/menu.css\">\n" +
                "<link type=\"text/css\" rel=\"stylesheet\" href=\"http://www.dek-d.com/writer/wri12.css?ver=0.1\">\n" +
                "<script type=\"text/javascript\">\n" +
                "var dekdee_story_groups = new Array();\n" +
                "dekdee_story_groups[1] = new Array();\n" +
                "dekdee_story_groups[1][999] = 'หมวดนิยาย ฟรีสไตล์';\n" +
                "dekdee_story_groups[1][1] = 'สบายๆ คลายเครียด';\n" +
                "dekdee_story_groups[1][2] = 'นิยาย รักหวานแหวว';\n" +
                "dekdee_story_groups[1][3] = 'นิยาย ซึ้งกินใจ';\n" +
                "dekdee_story_groups[1][4] = 'นิยาย รักเศร้าๆ';\n" +
                "dekdee_story_groups[1][5] = 'นิทาน';\n" +
                "dekdee_story_groups[1][6] = 'นิยาย ผจญภัย';\n" +
                "dekdee_story_groups[1][7] = 'นิยาย สืบสวน';\n" +
                "dekdee_story_groups[1][8] = 'นิยาย ระทึกขวัญ';\n" +
                "dekdee_story_groups[1][9] = 'นิยาย สงคราม';\n" +
                "dekdee_story_groups[1][10] = 'ตลก-ขบขัน';\n" +
                "dekdee_story_groups[1][11] = 'กลอน';\n" +
                "dekdee_story_groups[1][12] = 'นิยาย อดีต ปัจจุบัน อนาคต';\n" +
                "dekdee_story_groups[1][13] = 'จิตวิทยา';\n" +
                "dekdee_story_groups[1][14] = 'สังคม';\n" +
                "dekdee_story_groups[1][15] = 'นิยาย หักมุม';\n" +
                "dekdee_story_groups[1][16] = 'นิยาย แฟนตาซี';\n" +
                "dekdee_story_groups[1][17] = 'นิยาย กำลังภายใน';\n" +
                "dekdee_story_groups[1][18] = 'นิยาย วิทยาศาสตร์';\n" +
                "dekdee_story_groups[1][19] = 'แฟนฟิค';\n" +
                "dekdee_story_groups[1][20] = 'วรรณกรรมเยาวชน';\n" +
                "dekdee_story_groups[1][0] = 'อื่น ๆ';\n" +
                "dekdee_story_groups[2] = new Array();\n" +
                "dekdee_story_groups[2][999] = 'หมวดมีสาระ';\n" +
                "dekdee_story_groups[2][1] = 'ความรู้รอบตัว';\n" +
                "dekdee_story_groups[2][2] = 'ความรู้เพื่อดำเนินชีวิต';\n" +
                "dekdee_story_groups[2][3] = 'เกร็ดประวัติศาสตร์';\n" +
                "dekdee_story_groups[2][4] = 'ความรู้เรื่องเรียน';\n" +
                "dekdee_story_groups[2][5] = 'ความรู้เอนทรานซ์';\n" +
                "dekdee_story_groups[2][6] = 'ความรู้กลเม็ด เทคนิค ฯลฯ';\n" +
                "dekdee_story_groups[2][7] = 'เกร็ดท่องเที่ยว';\n" +
                "dekdee_story_groups[3] = new Array();\n" +
                "dekdee_story_groups[3][999] = 'หมวดไลฟ์สไตล์';\n" +
                "dekdee_story_groups[3][1] = 'สุขภาพ ความงาม';\n" +
                "dekdee_story_groups[3][2] = 'สิ่งของ intrend';\n" +
                "dekdee_story_groups[3][3] = 'ตามติดคนดัง';\n" +
                "dekdee_story_groups[3][4] = 'ดนตรี เพลง หนัง';\n" +
                "dekdee_story_groups[3][5] = 'ดีไซน์ กราฟิก';\n" +
                "dekdee_story_groups[3][6] = 'การ์ตูน  เกมส์';\n" +
                "dekdee_story_groups[3][7] = 'ไอที เทคโนโลยี';\n" +
                "dekdee_story_groups[3][8] = 'ประสบการณ์ต่างแดน';\n" +
                "dekdee_story_groups[3][0] = 'อื่นๆ';\n" +
                "\t$(document).ready( function(){\n" +
                "\t\t$('#global-placeholder').css('margin-top','-22px');\n" +
                "\t\t\n" +
                "\t\tdoChangeCat( $('#hdd_maing').val(), $('#hdd_subg').val() );\n" +
                "\t\t\n" +
                "\t\t$(window).scroll(function () {\n" +
                "\t\t\t//if( !is_mobile() ){\n" +
                "\t\t\t\tfixBannerScroll( $(window) );\n" +
                "\t\t\t//}\n" +
                "\t\t});\n" +
                "\t});\n" +
                "\t\n" +
                "\tvar isie = is_ie();\n" +
                "\tvar headh = 374;\n" +
                "\tvar blockwidth = 7;\n" +
                "\tfunction scrollTo(a){\n" +
                "\t\tif(parseInt($('#'+a).length)==0){\n" +
                "\t\t\treturn true;\n" +
                "\t\t}\n" +
                "\tvar b=$('#'+a).offset().top;\n" +
                "\tvar c=$('#'+a).css('background-color');\n" +
                "\t$('html,body').animate({ scrollTop:b-80 },1000);\n" +
                "\tif(typeof scrollToTab=='function')\n" +
                "\t\tscrollToTab(a);\n" +
                "\treturn false;\n" +
                "\t}\n" +
                "\tfunction is_ie(){\n" +
                "\t\tif( $.browser.msie && parseInt($.browser.version) <= 7){ return true; }\n" +
                "\t\treturn false;\n" +
                "\t}\n" +
                "\tfunction is_mobile() {\t\n" +
                "\t\tvar agent = navigator.userAgent.toLowerCase();\n" +
                "\t\tif ( agent.match(/android/) || agent.match(/webos/) || agent.match(/iphone/) || agent.match(/ipad/)\n" +
                "\t\t|| agent.match(/ipod/) || agent.match(/blackberry/)\t)\n" +
                "\t\t{return true;}\n" +
                "\t\telse {return false;}\n" +
                "\t}\n" +
                "\tfunction fixBannerScroll( windowObj ){\n" +
                "\t\t\t//console.log(window.scrollTop() + '[window height' + window.height() + '] [html height' + $('html').height() + '] [footer' + $('.footer').height() + '] [bannerframe' + $('#bannerframe').height() +\"] body height\"+$('body').height() );\n" +
                "\t\t\twinscroll = windowObj.scrollTop();\n" +
                "\t\t\t//tempPadding = ( isie ) ? 68 : 34;\t\t// ie : other\n" +
                "\t\t\tst = $('body').height() - windowObj.height() - $('.footer').outerHeight(true);\t// - tempPadding;\t\t//padding & margin footer\n" +
                "\t\t\tbottomstop = ((winscroll - st)>=0)?true:false;\n" +
                "\t\t\tbannerpos = 391 + $('#frame-search').height(); \n" +
                "\t\t\ttoppos = winscroll - headh;\n" +
                "\t\t\t\n" +
                "\t\t\tif( winscroll >= bannerpos && !bottomstop){\n" +
                "\t\t\t\t$(\"#bannerframe\").css({\"position\": \"absolute\", \"top\": toppos+\"px\", \"bottom\": \"\"});\n" +
                "\t\t\t} else if( bottomstop ){\t\t\t//bottom\n" +
                "\t\t\t\t$(\"#bannerframe\").css({\"position\": \"absolute\", \"bottom\": \"0px\", \"top\": \"\"});\n" +
                "\t\t\t\tif( isie ){\n" +
                "\t\t\t\t\tbannerbottom = winscroll - st;\n" +
                "\t\t\t\t\t$(\"#bannerframe\").css({\"position\": \"fixed\", \"bottom\": bannerbottom+\"px\", \"top\": \"\"});\n" +
                "\t\t\t\t}\n" +
                "\t\t\t} else if ( winscroll < bannerpos ){\n" +
                "\t\t\t\t$(\"#bannerframe\").css({\"position\": \"absolute\", \"top\": (20 +$('#frame-search').height() ) + \"px\", \"bottom\": \"\"});\n" +
                "\t\t\t}\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction setpage(curPage){\n" +
                "\t$('.paging').paging({\n" +
                "\t\t\t\t\ttotal : $('#hdd_count').val(),\n" +
                "\t\t\t\t\titemperpage : 10,\n" +
                "\t\t\t\t\tshowpage : 1,\n" +
                "\t\t\t\t\tpage : curPage,\n" +
                "\t\t\t\t\tcache : false,\n" +
                "\t\t\t\t\thref : '/writer/frame.php',\n" +
                "\t\t\t\t\threfParams : {\n" +
                "\t\t\t\t\t\tmain : $('#hdd_maing').val(),\n" +
                "\t\t\t\t\t\tsub : $('#hdd_subg').val(),\n" +
                "\t\t\t\t\t\tisend : $('#hdd_isend').val(),\n" +
                "\t\t\t\t\t\tstory_type : $('#hdd_story_type').val(),\n" +
                "\t\t\t\t\t\tsort : $('#hdd_sort').val()\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\tajax : '/writer/list2013.php',\n" +
                "\t\t\t\t\tajaxParams : {\n" +
                "\t\t\t\t\t\tisend : $('#hdd_isend').val(),\n" +
                "\t\t\t\t\t\tmain : $('#hdd_maing').val(),\n" +
                "\t\t\t\t\t\tsub : $('#hdd_subg').val(),\n" +
                "\t\t\t\t\t\tstory_type : $('#hdd_story_type').val(),\n" +
                "\t\t\t\t\t\tsort : $('#hdd_sort').val(),\n" +
                "\t\t\t\t\t\tajax : 1\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\tajaxDatatype: 'json',\n" +
                "\t\t\t\t\tajaxType: 'GET',\n" +
                "\t\t\t\t\tajaxable :true,\n" +
                "\t\t\t\t\tajaxCallback : function( data ){\n" +
                "\t\t\t\t\t\tif( data != null ){\n" +
                "\t\t\t\t\t\t\tdoChangeCat( $('#hdd_maing').val(), $('#hdd_subg').val() );\n" +
                "\t\t\t\t\t\t\t$('ul.fr-book').empty().append(data.template);\n" +
                "\t\t\t\t\t\t\tscrollTo('anchor');\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\telse{\n" +
                "\t\t\t\t\t\t\t$('ul.fr-book').empty().append('<b style=\"margin-left:300px;\">ระบบขัดข้อง กรุณาลองใหม่อีกครั้ง</b>');\n" +
                "\t\t\t\t\t\t}\t\t\t\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\ttemplateWrap : '<ul class=\"fr-number paging-${group}\">${list}</ul>',\n" +
                "\t\t\t\t\ttemplateCurrent : '<li class=\"nu2\">${page}</li>',\n" +
                "\t\t\t\t\ttemplatePrev : '<li><a href=\"${href}\" ajax=\"${ajax}\" target=\"${target}\" title=\"${title}\" onclick=\"${onclick}\" page=\"${p}\">&laquo; Prev</a></li>',\n" +
                "\t\t\t\t\ttemplateNext : '<li><a href=\"${href}\" ajax=\"${ajax}\" target=\"${target}\" title=\"${title}\" onclick=\"${onclick}\" page=\"${p}\">Next &raquo;</a></li>'\n" +
                "\t\t\t\t});\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction setpage_wordsearch(curPage){ \n" +
                "\tvar group = ( $('#hdd_maing').val() == -1 )? '' : $('#hdd_mainsub').val();\n" +
                "\t$('.paging').paging({\n" +
                "\t\t\t\t\ttotal : $('#hdd_count').val(),\n" +
                "\t\t\t\t\titemperpage : 10,\n" +
                "\t\t\t\t\tshowpage : 1,\n" +
                "\t\t\t\t\tpage : curPage,\n" +
                "\t\t\t\t\thref : '/writer/frame.php',\n" +
                "\t\t\t\t\threfParams : {\n" +
                "\t\t\t\t\t\tis_end : $('#hdd_isend').val(),\n" +
                "\t\t\t\t\t\tstory_groups : group,\n" +
                "\t\t\t\t\t\ttitle : $('#hdd_title').val(),\n" +
                "\t\t\t\t\t\tn_title : $('#hdd_not_title').val(),\n" +
                "\t\t\t\t\t\ttype : $('#hdd_story_type').val(),\n" +
                "\t\t\t\t\t\twriter : $('#hdd_writer').val(),\n" +
                "\t\t\t\t\t\tn_writer : $('#hdd_not_writer').val(),\n" +
                "\t\t\t\t\t\tabstract_w : $('#hdd_abstract_w').val(),\n" +
                "\t\t\t\t\t\tn_abstract_w : $('#hdd_not_abstract_w').val()\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\tcache : false,\n" +
                "\t\t\t\t\tajax : '/writer/story_search_2012.php',\n" +
                "\t\t\t\t\tajaxParams : {\n" +
                "\t\t\t\t\t\tis_end : $('#hdd_isend').val(),\n" +
                "\t\t\t\t\t\tstory_groups : group,\n" +
                "\t\t\t\t\t\ttitle : $('#hdd_title').val(),\n" +
                "\t\t\t\t\t\tn_title : $('#hdd_not_title').val(),\n" +
                "\t\t\t\t\t\ttype : $('#hdd_story_type').val(),\n" +
                "\t\t\t\t\t\twriter : $('#hdd_writer').val(),\n" +
                "\t\t\t\t\t\tn_writer : $('#hdd_not_writer').val(),\n" +
                "\t\t\t\t\t\tabstract_w : $('#hdd_abstract_w').val(),\n" +
                "\t\t\t\t\t\tn_abstract_w : $('#hdd_not_abstract_w').val(),\n" +
                "\t\t\t\t\t\tajax : 1\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\tajaxDatatype: 'json',\n" +
                "\t\t\t\t\tajaxType: 'GET',\n" +
                "\t\t\t\t\tajaxable :true,\n" +
                "\t\t\t\t\tajaxCallback : function( data ){\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\tif( data != null ){\n" +
                "\t\t\t\t\t\t\t\tdoChangeCat( $('#hdd_maing').val(), $('#hdd_subg').val() );\n" +
                "\t\t\t\t\t\t\t\tvar iframe = document.getElementById( 'istat' );\n" +
                "\t\t\t\t\t\t\t\tvar newTHframesrc = 'statcode.php?page=writer_search';\n" +
                "\t\t\t\t\t\t\t\tiframe.src = newTHframesrc;\n" +
                "\t\t\t\t\t\t\t$('ul.fr-book').empty().append(data.template);\n" +
                "\t\t\t\t\t\t\tscrollTo('anchor');\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\telse{\n" +
                "\t\t\t\t\t\t\t$('ul.fr-book').empty().append('<b style=\"margin-left:300px;\">ระบบขัดข้อง กรุณาลองใหม่อีกครั้ง</b>');\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\ttemplateWrap : '<ul class=\"fr-number paging-${group}\">${list}</ul>',\n" +
                "\t\t\t\t\ttemplateCurrent : '<li class=\"nu2\">${page}</li>',\n" +
                "\t\t\t\t\ttemplatePrev : '<li><a href=\"${href}\" ajax=\"${ajax}\" target=\"${target}\" title=\"${title}\" onclick=\"${onclick}\" page=\"${p}\">&laquo; Prev</a></li>',\n" +
                "\t\t\t\t\ttemplateNext : '<li><a href=\"${href}\" ajax=\"${ajax}\" target=\"${target}\" title=\"${title}\" onclick=\"${onclick}\" page=\"${p}\">Next &raquo;</a></li>'\n" +
                "\t\t\t\t});\n" +
                "\t}\n" +
                "var xhr;\t\n" +
                "\tfunction loadjson(fncPage,curPage,url,seria){\n" +
                "\n" +
                "\t\t$('#topstatus').val(1);\n" +
                "\t\t// $('#forslider a').empty();\n" +
                "\t\t$('.top15').empty().append('Top 5');\n" +
                "\t\thideshowtop();\n" +
                "\t\t$('.top5').empty().append('<li style=\"padding:52px 0px 52px 310px;\"><img src=\"http://www.dek-d.com/writer/img/bigrotation.gif\" width=32 height=32 /></li>');\n" +
                "\t\t$('ul.fr-book').empty().append('<li style=\"padding-left:310px;\"><img src=\"http://www.dek-d.com/writer/img/bigrotation.gif\" width=32 height=32 /></li>');\n" +
                "\t\t$('.paging').empty();\n" +
                "\t\tif( typeof xhr != \"undefined\" )  xhr.abort();\n" +
                "\t\t xhr = $.ajax({\n" +
                "\t\t\turl : url,\n" +
                "\t\t\tdata : seria,\n" +
                "\t\t\tdataType : 'json',\n" +
                "\t\t\ttype : 'GET',\n" +
                "\t\t\tsuccess : function(data){\n" +
                "\t\t\t\tif( data != null ){\n" +
                "\t\t\t\t\t$('#hdd_count').val(0); \n" +
                "\t\t\t\t\t$('#count').empty().append(data.count);\n" +
                "\t\t\t\t\t$('#hdd_count').val(data.count_nocommas); \n" +
                "\t\t\t\t\t$('#top5').empty().append(data.statKey);\n" +
                "\t\t\t\t\t$('.top5').empty().append(data.top5);\n" +
                "\t\t\t\t\t$('ul.fr-book').empty().append(data.template);\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\tif(fncPage == 0 && data.count_nocommas != 0) \n" +
                "\t\t\t\t\t\tsetpage(curPage);\n" +
                "\t\t\t\t\tif(fncPage == 1 && data.count_nocommas != 0) \n" +
                "\t\t\t\t\t\tsetpage_wordsearch(curPage);\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\telse{\n" +
                "\t\t\t\t\t$('.top5').empty().append('<li style=\"padding:52px 0px 52px 260px;\"><b>ระบบขัดข้อง กรุณาลองใหม่อีกครั้ง</b></li>');\n" +
                "\t\t\t\t\t$('ul.fr-book').empty().append('<b style=\"margin-left:240px;\">ระบบขัดข้อง กรุณาลองใหม่อีกครั้ง</b>');\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t});\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction words(curPage){ // search ด้วย keyword\n" +
                "\t\tvar iframe = document.getElementById( 'istat' );\n" +
                "\t\tvar newTHframesrc = 'statcode.php?page=writer_search';\n" +
                "\t\tiframe.src = newTHframesrc;\n" +
                "\t\t\n" +
                "\t\tif($('#story_groups').val() != '' || $('#is_end').val() != '' || $('#type').val() != '' || $('#title').val() != '' || $('#writer').val() != '' || $('#abstract_w').val() != '' || $('#not_title').val() != '' || $('#not_writer').val() != '' || $('#not_abstract_w').val() != ''){\n" +
                "\t\t\tvar mainsub = $('#story_groups').val().split(',');\n" +
                "\t\t\tif( $('#story_groups').val() == '' ){\n" +
                "\t\t\t\tsetHidden(-1,-1,$('#is_end').val(),$('#type').val(),0,$('#story_groups').val(),$('#title').val(),$('#not_title').val(),$('#writer').val(),$('#not_writer').val(),$('#abstract_w').val(),$('#not_abstract_w').val());\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\tsetHidden( mainsub[0] , mainsub[1] , $('#is_end').val() , $('#type').val(),0,$('#story_groups').val(),$('#title').val(),$('#not_title').val(),$('#writer').val(),$('#not_writer').val(),$('#abstract_w').val(),$('#not_abstract_w').val());\n" +
                "\t\t\t}\t\n" +
                "\t\t\t//var url = '/writer/story_search_2012.php?story_groups='+$('#story_groups').val()+'&is_end='+$('#is_end').val()+'&type='+$('#type').val()+'&title='+$('#title').val()+'&writer='+$('#writer').val()+'&abstract_w='+$('#abstract_w').val()+'&n_title='+$('#not_title').val()+'&n_writer='+$('#not_writer').val()+'&n_abstract_w='+$('#not_abstract_w').val()+'&ajax=1';\n" +
                "\t\t\tloadjson( 1, 1, '/writer/story_search_2012.php', $('#words').serialize() );\n" +
                "\t\t\t$('.menucat').hide();\n" +
                "\t\t\t$('.menusub').hide();\n" +
                "\t\t\tif(mainsub[0] != '')\n" +
                "\t\t\t{\n" +
                "\t\t\t\t$('div.menu-box').show();\n" +
                "\t\t\t\t$('#menu0'+(parseInt(mainsub[0])+1)).show();\n" +
                "\t\t\t\t$('#meu-0'+mainsub[0]).show();\n" +
                "\t\t\t\tsetdropdown(mainsub[0]);\n" +
                "\t\t\t}else{\n" +
                "\t\t\t\t$('div.menu-box').hide();\n" +
                "\t\t\t\t$('#menu01').show();\n" +
                "\t\t\t\tsetdropdown(-1);\n" +
                "\t\t\t}\t\t\t\t\n" +
                "\t\t}\n" +
                "\t\t$('.menucat').hide();\n" +
                "\t\t$('div.menu-box').hide();\n" +
                "\t\t$('#toparea').hide();\n" +
                "\t\t$('.fr-box').hide();\n" +
                "\t\t$('#menu05').show();\n" +
                "\t\t$('.top15').empty().append('ผลการค้นหานิยายแบบป้อนคำ');\n" +
                "\t\t//scrollTo('anchor');\n" +
                "\t\treturn false;\n" +
                "\t}\n" +
                "\n" +
                "\tfunction searchsort(curPage){ // search & sort\n" +
                "\t\tvar mainsub = $('#mainsub').val().split(',');\n" +
                "\t\tif( $('#mainsub').val() == 0 ){\n" +
                "\t\t\tsetHidden(-1,-1,$('#isend').val(),$('#story_type').val(),$('#sort').val());\n" +
                "\t\t\tvar url = '/writer/list2013.php?story_type='+$('#story_type').val()+'&isend='+$('#isend').val()+'&sort='+$('#sort').val()+'&main=-1&sub=-1&ajax=1';\n" +
                "\t\t}else{\n" +
                "\t\t\tsetHidden(mainsub[0],mainsub[1],$('#isend').val(),$('#story_type').val(),$('#sort').val());\n" +
                "\t\t\tvar url = '/writer/list2013.php?story_type='+$('#story_type').val()+'&isend='+$('#isend').val()+'&sort='+$('#sort').val()+'&main='+mainsub[0]+'&sub='+mainsub[1]+'&ajax=1';\n" +
                "\t\t}\t\t\n" +
                "\t\tloadjson( 0, 1, url);\n" +
                "\t\t$('.menucat').hide();\n" +
                "\t\t$('.menusub').hide();\n" +
                "\t\tif(mainsub[0] != ''){\n" +
                "\t\t\t$('#menu0'+(parseInt(mainsub[0])+1)).show();\n" +
                "\t\t\t$('#meu-0'+mainsub[0]).show();\n" +
                "\t\t\t$('div.menu-box').show();\n" +
                "\t\t\t//setdropdown(mainsub[0]);\n" +
                "\t\t}else{\n" +
                "\t\t\t$('#menu01').show();\n" +
                "\t\t\t$('div.menu-box').hide();\n" +
                "\t\t\t//setdropdown(-1);\n" +
                "\t\t}\n" +
                "\t\treturn false;\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction setHidden(main,sub,isend,type,sort,mainsub,title,not_title,writer,not_writer,abstract_w,not_abstract_w){\n" +
                "\t\t$('#hdd_maing').val(main);\n" +
                "\t\t$('#hdd_subg').val(sub);\n" +
                "\t\t$('#hdd_isend').val(isend);\n" +
                "\t\t$('#hdd_story_type').val(type);\n" +
                "\t\t$('#hdd_sort').val(sort);\n" +
                "\t\t$('#hdd_mainsub').val(mainsub);\n" +
                "\t\t$('#hdd_title').val(title);\n" +
                "\t\t$('#hdd_not_title').val(not_title);\n" +
                "\t\t$('#hdd_writer').val(writer);\n" +
                "\t\t$('#hdd_not_writer').val(not_writer);\n" +
                "\t\t$('#hdd_abstract_w').val(abstract_w);\n" +
                "\t\t$('#hdd_not_abstract_w').val(not_abstract_w);\n" +
                "\t}\n" +
                "\n" +
                "\tfunction chgMenu(chosen){\t\n" +
                "\t\t$('.top15').empty().append('Top 5');\n" +
                "\t\t$('#toparea').show();\n" +
                "\t\t$('.fr-box').show();\n" +
                "\t\tdoChangeCat( chosen, 0 );\n" +
                "\t\t$('.menucat').hide();\n" +
                "\t\t$('.menusub').hide();\n" +
                "\t\t$('#menu0'+(chosen+1)).show();\t\t\t\t\n" +
                "\t\t\t\t\t\t\n" +
                "\t\tif( chosen != 0 ){\n" +
                "\t\t\t$('#meu-0'+chosen).show();\n" +
                "\t\t\t$('div.menu-box').show();\n" +
                "\t\t\tsetHidden(chosen,-1,0,0,0);\n" +
                "\t\t\tloadjson( 0, 1, '/writer/list2013.php?main='+chosen+'&sub=-1&ajax=1');\n" +
                "\t\t\tsetdropdown(chosen);\n" +
                "\t\t}else{\n" +
                "\t\t\t$('div.menu-box').hide();\n" +
                "\t\t\tsetHidden(-1,-1,0,0,0);\n" +
                "\t\t\tloadjson( 0, 1, '/writer/list2013.php?main=-1&sub=-1&ajax=1');\t\n" +
                "\t\t\tsetdropdown(-1);\n" +
                "\t\t}\n" +
                "\t\tscrollTo('headmenu');\n" +
                "\t\treturn false;\t\t\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction subselect(main,sub,jsonurl){\n" +
                "\t\tdoChangeCat( main, sub );\n" +
                "\t\tif( sub != $('#hdd_subg').val() ){\n" +
                "\t\t\tsetHidden(main,sub,0,0,0); \n" +
                "\t\t\tloadjson( 0, 1, jsonurl);\n" +
                "\t\t}\n" +
                "\t\treturn false;\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction setdropdown(main){\n" +
                "\t\t$('#mainsub').empty();\n" +
                "\t\tvar options ='';\n" +
                "\t\tif(main == -1) options +='<option value=\"0\">ทุกหมวด</option>';\n" +
                "\t\tif( main == 1 || main == -1){\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,-1\">--หมวดหลักฟรีสไตล์--</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,1\">สบายๆ คลายเครียด</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,2\">รักหวานแหวว</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,3\">ซึ้งกินใจ</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,4\">รักเศร้าๆ</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,5\">นิทาน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,6\">ผจญภัย</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,7\">สืบสวน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,8\">ระทึกขวัญ</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,9\">สงคราม</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,10\">ตลกขบขัน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,11\">กลอน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,12\">อดีต ปัจจุบัน อนาคต</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,13\">จิตวิทยา</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,14\">สังคม</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,15\">หักมุม</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,16\">แฟนตาซี</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,17\">กำลังภายใน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,18\">วิทยาศาสตร์</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,19\">แฟนฟิค</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,20\">วรรณกรรมเยาวชน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"1,0\">อื่นๆ</option>';\n" +
                "\t\t\t\t\t} \n" +
                "\t\tif( main == 2  || main == -1){\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,-1\">--หมวดหลักมีสาระ--</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,1\">ความรู้รอบตัว</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,2\">ความรู้เพื่อดำเนินชีวิต</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,3\">เกร็ดประวัติศาสตร์</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,4\">ความรู้เรื่องเรียน</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,5\">ความรู้เอนทรานซ์</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,6\">ความรู้กลเม็ด เทคนิค</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"2,7\">เกร็ดท่องเที่ยว</option>';\n" +
                "\t\t\t\t\t} \n" +
                "\t\tif( main == 3  || main == -1){\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,-1\">--หมวดหลักไลฟ์สไตล์--</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,1\">สุขภาพ ความงาม</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,2\">สิ่งของ intrend</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,3\">ตามติดคนดัง</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,4\">ดนตรี เพลง หนัง</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,5\">ดีไซน์ กราฟิก</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,6\">การ์ตูน เกมส์</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,7\">ไอที เทคโนโลยี</option>';\n" +
                "\t\t\t\t\t\t\toptions+='<option value=\"3,0\">อื่นๆ</option>';\n" +
                "\t\t\t\t\t}\n" +
                "\t\t$('#mainsub').append(options);\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction doChangeTH( mCat, sCat ){\n" +
                "\t\tvar iframe = document.getElementById( 'istat' );\n" +
                "\t\tvar newTHframesrc = 'statcode.php?page=writer_browse';\n" +
                "\t\tif( mCat > 0 ){\n" +
                "\t\t\tnewTHframesrc += '_' + dekdee_story_groups[ mCat ][999] ;\n" +
                "\t\t}\n" +
                "\t\tif( sCat > 0 ){\n" +
                "\t\t\tnewTHframesrc += ' > ' + dekdee_story_groups[ mCat ][ sCat ] ;\n" +
                "\t\t}\n" +
                "\t\tiframe.src = newTHframesrc+'&bgc=transparent';\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction doChangeCat( mCat , sCat ){\n" +
                "\t\tdoChangeTH( mCat, sCat );\n" +
                "\t\tvar iframe = document.getElementById( 'bannerframe' );\n" +
                "\t\tvar newbannerframesrc = 'ad/2012zone1.html';\n" +
                "\t\tif( mCat <= 0 || ( mCat == 1 && sCat == 0 ) ){\n" +
                "\t\t\tnewbannerframesrc = 'ad/2012zone1.html';\n" +
                "\t\t}else if( mCat == 1 && ( sCat == 2 || sCat == 3 || sCat == 4 ) ){\n" +
                "\t\t\tnewbannerframesrc = 'ad/2012zone2.html';\n" +
                "\t\t}else if( mCat == 1 && ( sCat == 6 || sCat == 7 || sCat == 8 || sCat == 9 || sCat == 12 || sCat == 15 || sCat == 16 || sCat == 17 || sCat == 18 ) ){\n" +
                "\t\t\tnewbannerframesrc = 'ad/2012zone3.html';\n" +
                "\t\t}else if( mCat == 1 && ( sCat == 0 || sCat == 1 || sCat == 5 || sCat == 10 || sCat == 11 || sCat == 13 || sCat == 14 || sCat == 19 || sCat == 20 ) ){\n" +
                "\t\t\tnewbannerframesrc = 'ad/2012zone4.html';\n" +
                "\t\t}else if( mCat == 2 || mCat == 3 ){\n" +
                "\t\t\tnewbannerframesrc = 'ad/2012zone5.html';\n" +
                "\t\t}\n" +
                "\t\tiframe.src = newbannerframesrc;\n" +
                "\t}\n" +
                "\t\n" +
                "\tfunction hideshowtop(){\n" +
                "\t\tvar status = $('#topstatus').val();\n" +
                "\t\tif(status == 0){\n" +
                "\t\t\t$('#topslider').css('height','268px');\n" +
                "\t\t\t$('.top15').empty().append('Top 10');\n" +
                "\t\t\t$('#topstatus').val(1);\n" +
                "\t\t\t$('.slidertxt').empty().append('ดู Top5');\n" +
                "\t\t\t$('#arrow').removeClass('arrowdown');\n" +
                "\t\t\t$('#arrow').addClass('arrowup');\n" +
                "\t\t}else if(status == 1){\n" +
                "\t\t\t$('#topslider').css('height','137px');\n" +
                "\t\t\t$('.top15').empty().append('Top 5');\n" +
                "\t\t\t$('#topstatus').val(0);\n" +
                "\t\t\t$('.slidertxt').empty().append('ดู Top10');\n" +
                "\t\t\t$('#arrow').removeClass('arrowup');\n" +
                "\t\t\t$('#arrow').addClass('arrowdown');\n" +
                "\t\t}\n" +
                "\t\treturn false;\n" +
                "\t}\n" +
                "\t\n" +
                "\t\n" +
                "\n" +
                "</script>\n" +
                "<style>\n" +
                "    .asmenu{ padding-right: 7px; padding-left: 7px; }\n" +
                "</style>\n" +
                "<style type=\"text/css\">\n" +
                "    .asmenu .box-list .box-item .paddingbox{ padding-top: 6px; }\n" +
                "    .asmenu .box-list .box-item .iconleft{ width: 41px; height: 41px; float: left; margin-left: 21px; }\n" +
                "    .asmenu .box-list .box-item .textright{ width: auto; float: left; padding-top: 4px; }\n" +
                "    .box-list .box-item .box h3 span {\n" +
                "    color: #8A8A8A;\n" +
                "    text-decoration: none;\n" +
                "    height: 17px;\n" +
                "    line-height: 14px;\n" +
                "    display: block;\n" +
                "    }\n" +
                "    #page0 .box-list .box-item .box h3 span.global-a-0 { color: white; }\n" +
                "    #page1 .box-list .box-item .box h3 span.global-a-1 { color: white; }\n" +
                "    #page2 .box-list .box-item .box h3 span.global-a-2 { color: white; }\n" +
                "    #page3 .box-list .box-item .box h3 span.global-a-3 { color: white; }\n" +
                "    #page4 .box-list .box-item .box h3 span.global-a-4 { color: white; }\n" +
                "    #page5 .box-list .box-item .box h3 span.global-a-5 { color: white; }\n" +
                "    .fb_hidden{position:absolute;top:-10000px;z-index:10001}\n" +
                "    .fb_invisible{display:none}\n" +
                "    .fb_reset{background:none;border:0;border-spacing:0;color:#000;cursor:auto;direction:ltr;font-family:\"lucida\n" +
                "    grande\", tahoma, verdana, arial,\n" +
                "    sans-serif;font-size:11px;font-style:normal;font-variant:normal;font-weight:normal;letter-spacing:normal;line-height:1;margin:0;overflow:visible;padding:0;text-align:left;text-decoration:none;text-indent:0;text-shadow:none;text-transform:none;visibility:visible;white-space:normal;word-spacing:normal}\n" +
                "    .fb_reset > div{overflow:hidden}\n" +
                "    .fb_link img{border:none}\n" +
                "    .fb_dialog{background:rgba(82, 82, 82, .7);position:absolute;top:-10000px;z-index:10001}\n" +
                "    .fb_dialog_advanced{padding:10px;-moz-border-radius:8px;-webkit-border-radius:8px;border-radius:8px}\n" +
                "    .fb_dialog_content{background:#fff;color:#333}\n" +
                "    .fb_dialog_close_icon{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat scroll 0\n" +
                "    0\n" +
                "    transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif);cursor:pointer;display:block;height:15px;position:absolute;right:18px;top:17px;width:15px;top:8px\\9;right:7px\\9}\n" +
                "    .fb_dialog_mobile .fb_dialog_close_icon{top:5px;left:5px;right:auto}\n" +
                "    .fb_dialog_padding{background-color:transparent;position:absolute;width:1px;z-index:-1}\n" +
                "    .fb_dialog_close_icon:hover{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat\n" +
                "    scroll 0 -15px transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif)}\n" +
                "    .fb_dialog_close_icon:active{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yq/r/IE9JII6Z1Ys.png) no-repeat\n" +
                "    scroll 0 -30px transparent;_background-image:url(http://static.ak.fbcdn.net/rsrc.php/v2/yL/r/s816eWC-2sl.gif)}\n" +
                "    .fb_dialog_loader{background-color:#f2f2f2;border:1px solid #606060;font-size:24px;padding:20px}\n" +
                "    .fb_dialog_top_left,\n" +
                "    .fb_dialog_top_right,\n" +
                "    .fb_dialog_bottom_left,\n" +
                "    .fb_dialog_bottom_right{height:10px;width:10px;overflow:hidden;position:absolute}\n" +
                "    .fb_dialog_top_left{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0\n" +
                "    0;left:-10px;top:-10px}\n" +
                "    .fb_dialog_top_right{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0\n" +
                "    -10px;right:-10px;top:-10px}\n" +
                "    .fb_dialog_bottom_left{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0\n" +
                "    -20px;bottom:-10px;left:-10px}\n" +
                "    .fb_dialog_bottom_right{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ye/r/8YeTNIlTZjm.png) no-repeat 0\n" +
                "    -30px;right:-10px;bottom:-10px}\n" +
                "    .fb_dialog_vert_left,\n" +
                "    .fb_dialog_vert_right,\n" +
                "    .fb_dialog_horiz_top,\n" +
                "    .fb_dialog_horiz_bottom{position:absolute;background:#525252;filter:alpha(opacity=70);opacity:.7}\n" +
                "    .fb_dialog_vert_left,\n" +
                "    .fb_dialog_vert_right{width:10px;height:100%}\n" +
                "    .fb_dialog_vert_left{margin-left:-10px}\n" +
                "    .fb_dialog_vert_right{right:0;margin-right:-10px}\n" +
                "    .fb_dialog_horiz_top,\n" +
                "    .fb_dialog_horiz_bottom{width:100%;height:10px}\n" +
                "    .fb_dialog_horiz_top{margin-top:-10px}\n" +
                "    .fb_dialog_horiz_bottom{bottom:0;margin-bottom:-10px}\n" +
                "    .fb_dialog_iframe{line-height:0}\n" +
                "    .fb_dialog_content .dialog_title{background:#6d84b4;border:1px solid\n" +
                "    #3b5998;color:#fff;font-size:14px;font-weight:bold;margin:0}\n" +
                "    .fb_dialog_content .dialog_title > span{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/yd/r/Cou7n-nqK52.gif)\n" +
                "    no-repeat 5px 50%;float:left;padding:5px 0 7px 26px}\n" +
                "    body.fb_hidden{-webkit-transform:none;height:100%;margin:0;left:-10000px;overflow:visible;position:absolute;top:-10000px;width:100%\n" +
                "    }\n" +
                "    .fb_dialog.fb_dialog_mobile.loading{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/ya/r/3rhSv5V8j3o.gif)\n" +
                "    white no-repeat 50% 50%;min-height:100%;min-width:100%;overflow:hidden;position:absolute;top:0;z-index:10001}\n" +
                "    .fb_dialog.fb_dialog_mobile.loading.centered{max-height:590px;min-height:590px;max-width:500px;min-width:500px}\n" +
                "    #fb-root #fb_dialog_ipad_overlay{background:rgba(0, 0, 0,\n" +
                "    .45);position:absolute;left:0;top:0;width:100%;min-height:100%;z-index:10000}\n" +
                "    #fb-root #fb_dialog_ipad_overlay.hidden{display:none}\n" +
                "    .fb_dialog.fb_dialog_mobile.loading iframe{visibility:hidden}\n" +
                "    .fb_dialog_content .dialog_header{-webkit-box-shadow:white 0 1px 1px -1px inset;background:-webkit-gradient(linear,\n" +
                "    0 0, 0 100%, from(#738ABA), to(#2C4987));border-bottom:1px solid;border-color:#1d4088;color:#fff;font:14px\n" +
                "    Helvetica, sans-serif;font-weight:bold;text-overflow:ellipsis;text-shadow:rgba(0, 30, 84, .296875) 0 -1px\n" +
                "    0;vertical-align:middle;white-space:nowrap}\n" +
                "    .fb_dialog_content .dialog_header table{-webkit-font-smoothing:subpixel-antialiased;height:43px;width:100%\n" +
                "    }\n" +
                "    .fb_dialog_content .dialog_header td.header_left{font-size:12px;padding-left:5px;vertical-align:middle;width:60px\n" +
                "    }\n" +
                "    .fb_dialog_content .dialog_header td.header_right{font-size:12px;padding-right:5px;vertical-align:middle;width:60px\n" +
                "    }\n" +
                "    .fb_dialog_content .touchable_button{background:-webkit-gradient(linear, 0 0, 0 100%, from(#4966A6),\n" +
                "    color-stop(0.5, #355492), to(#2A4887));border:1px solid\n" +
                "    #29447e;-webkit-background-clip:padding-box;-webkit-border-radius:3px;-webkit-box-shadow:rgba(0, 0, 0, .117188) 0\n" +
                "    1px 1px inset,\n" +
                "    rgba(255, 255, 255, .167969) 0 1px 0;display:inline-block;margin-top:3px;max-width:85px;line-height:18px;padding:4px\n" +
                "    12px;position:relative}\n" +
                "    .fb_dialog_content .dialog_header .touchable_button input{border:none;background:none;color:#fff;font:12px\n" +
                "    Helvetica, sans-serif;font-weight:bold;margin:2px -12px;padding:2px 6px 3px 6px;text-shadow:rgba(0, 30, 84, .296875)\n" +
                "    0 -1px 0}\n" +
                "    .fb_dialog_content .dialog_header\n" +
                "    .header_center{color:#fff;font-size:16px;font-weight:bold;line-height:18px;text-align:center;vertical-align:middle}\n" +
                "    .fb_dialog_content .dialog_content{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/y9/r/jKEcVPZFk-2.gif)\n" +
                "    no-repeat 50% 50%;border:1px solid #555;border-bottom:0;border-top:0;height:150px}\n" +
                "    .fb_dialog_content .dialog_footer{background:#f2f2f2;border:1px solid #555;border-top-color:#ccc;height:40px}\n" +
                "    #fb_dialog_loader_close{float:left}\n" +
                "    .fb_dialog.fb_dialog_mobile .fb_dialog_close_button{text-shadow:rgba(0, 30, 84, .296875) 0 -1px 0}\n" +
                "    .fb_dialog.fb_dialog_mobile .fb_dialog_close_icon{visibility:hidden}\n" +
                "    .fb_iframe_widget{display:inline-block;position:relative}\n" +
                "    .fb_iframe_widget span{display:inline-block;position:relative;text-align:justify}\n" +
                "    .fb_iframe_widget iframe{position:absolute}\n" +
                "    .fb_iframe_widget_lift{z-index:1}\n" +
                "    .fb_hide_iframes iframe{position:relative;left:-10000px}\n" +
                "    .fb_iframe_widget_loader{position:relative;display:inline-block}\n" +
                "    .fb_iframe_widget_fluid{display:inline}\n" +
                "    .fb_iframe_widget_fluid span{width:100%}\n" +
                "    .fb_iframe_widget_loader iframe{min-height:32px;z-index:2;zoom:1}\n" +
                "    .fb_iframe_widget_loader .FB_Loader{background:url(http://static.ak.fbcdn.net/rsrc.php/v2/y9/r/jKEcVPZFk-2.gif)\n" +
                "    no-repeat;height:32px;width:32px;margin-left:-16px;position:absolute;left:50%;z-index:4}\n" +
                "    .fb_connect_bar_container div,\n" +
                "    .fb_connect_bar_container span,\n" +
                "    .fb_connect_bar_container a,\n" +
                "    .fb_connect_bar_container img,\n" +
                "    .fb_connect_bar_container\n" +
                "    strong{background:none;border-spacing:0;border:0;direction:ltr;font-style:normal;font-variant:normal;letter-spacing:normal;line-height:1;margin:0;overflow:visible;padding:0;text-align:left;text-decoration:none;text-indent:0;text-shadow:none;text-transform:none;visibility:visible;white-space:normal;word-spacing:normal;vertical-align:baseline}\n" +
                "    .fb_connect_bar_container{position:fixed;left:0 !important;right:0 !important;height:42px !important;padding:0 25px\n" +
                "    !important;margin:0 !important;vertical-align:middle !important;border-bottom:1px solid #333\n" +
                "    !important;background:#3b5998 !important;z-index:99999999 !important;overflow:hidden !important}\n" +
                "    .fb_connect_bar_container_ie6{position:absolute;top:expression(document.compatMode==\"CSS1Compat\"?\n" +
                "    document.documentElement.scrollTop+\"px\":body.scrollTop+\"px\")}\n" +
                "    .fb_connect_bar{position:relative;margin:auto;height:100%;width:100%;padding:6px 0 0 0\n" +
                "    !important;background:none;color:#fff !important;font-family:\"lucida grande\", tahoma, verdana, arial, sans-serif\n" +
                "    !important;font-size:13px !important;font-style:normal !important;font-variant:normal !important;font-weight:normal\n" +
                "    !important;letter-spacing:normal !important;line-height:1 !important;text-decoration:none !important;text-indent:0\n" +
                "    !important;text-shadow:none !important;text-transform:none !important;white-space:normal\n" +
                "    !important;word-spacing:normal !important}\n" +
                "    .fb_connect_bar a:hover{color:#fff}\n" +
                "    .fb_connect_bar .fb_profile img{height:30px;width:30px;vertical-align:middle;margin:0 6px 5px 0}\n" +
                "    .fb_connect_bar div a,\n" +
                "    .fb_connect_bar span,\n" +
                "    .fb_connect_bar span a{color:#bac6da;font-size:11px;text-decoration:none}\n" +
                "    .fb_connect_bar .fb_buttons{float:right;margin-top:7px}\n" +
                "    .fb_edge_widget_with_comment{position:relative;*z-index:1000}\n" +
                "    .fb_edge_widget_with_comment span.fb_edge_comment_widget{position:absolute}\n" +
                "    .fb_edge_widget_with_comment span.fb_send_button_form_widget{z-index:1}\n" +
                "    .fb_edge_widget_with_comment span.fb_send_button_form_widget\n" +
                "    .FB_Loader{left:0;top:1px;margin-top:6px;margin-left:0;background-position:50%\n" +
                "    50%;background-color:#fff;height:150px;width:394px;border:1px #666 solid;border-bottom:2px solid #283e6c;z-index:1}\n" +
                "    .fb_edge_widget_with_comment span.fb_send_button_form_widget.dark .FB_Loader{background-color:#000;border-bottom:2px\n" +
                "    solid #ccc}\n" +
                "    .fb_edge_widget_with_comment span.fb_send_button_form_widget.siderender\n" +
                "    .FB_Loader{margin-top:0}\n" +
                "    .fbpluginrecommendationsbarleft,\n" +
                "    .fbpluginrecommendationsbarright{position:fixed !important;bottom:0;z-index:999}\n" +
                "    .fbpluginrecommendationsbarleft{left:10px}\n" +
                "    .fbpluginrecommendationsbarright{right:10px}\n" +
                "</style>\n" +
                "</head>\n" +
                "</head>\n";
        String newhtml2 = "\n" +
                "<body  id=\"page1\">\n" +
                "<input type=\"hidden\" id=\"hdd_isend\" value=\"0\">\n" +
                "<input type=\"hidden\" id=\"hdd_maing\" value=\"-1\">\n" +
                "<input type=\"hidden\" id=\"hdd_subg\" value=\"-1\">\n" +
                "<input type=\"hidden\" id=\"hdd_mainsub\" value=\"-1,-1\">\n" +
                "<input type=\"hidden\" id=\"hdd_story_type\" value=\"0\">\n" +
                "<input type=\"hidden\" id=\"hdd_sort\" value=\"0\">\n" +
                "<input type=\"hidden\" id=\"hdd_count\" value=\"360260\">\n" +
                "<input type=\"hidden\" id=\"hdd_title\" value=\"\">\n" +
                "<input type=\"hidden\" id=\"hdd_not_title\" value=\"\">\n" +
                "<input type=\"hidden\" id=\"hdd_writer\" value=\"\">\n" +
                "<input type=\"hidden\" id=\"hdd_not_writer\" value=\"\">\n" +
                "<input type=\"hidden\" id=\"hdd_abstract_w\" value=\"\">\n" +
                "<input type=\"hidden\" id=\"hdd_not_abstract_w\" value=\"\">\n" +
                "\n" +
                "\n" +
                "<div class=\"sun11\">\n" +
                "<h2 class=\"top15\">Top 5</h2>\n" +
                "\n" +
                "<div id=\"toparea\" style=\"display:block;\">\n" +
                "    <h3 id=\"top5\">ทุกหมวด</h3>\n" +
                "\n" +
                "    <div id=\"topslider\">\n" +
                "        <ul class=\"top5\">\n" +
                "            <li>\n" +
                "                <div class=\"number number-x00\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/lily-amber/story/view.php?id=972906\"\n" +
                "                                         target=\"_blank\" class=\"fr-l-s-grd\" title=\"วิวาห์ไร้ใจ\">วิวาห์ไร้ใจ</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/lily-amber/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"ปาริดา\"><strong>ปาริดา</strong></a></div>\n" +
                "                <div class=\"number3\">55 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.3M&nbsp;/&nbsp;&nbsp;0.5M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x01\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/nitatsr809/story/view.php?id=836791\"\n" +
                "                                         target=\"_blank\" class=\"fr-l-s-grd\" title=\"Magic World Online โลกออนไลน์ในฝัน\">Magic\n" +
                "                    World Online โลกออนไลน์ในฝัน</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/nitatsr809/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"Mr.Saka\"><strong>Mr.Saka</strong></a></div>\n" +
                "                <div class=\"number3\">156 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.2M&nbsp;/&nbsp;&nbsp;1.5M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x02\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/tunstang/story/view.php?id=937496\"\n" +
                "                                         target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                         title=\"ทวงรักสัญญามาเฟีย (นิยายชุด ดวงใจมาเฟีย)\">ทวงรักสัญญามาเฟีย (นิยายชุด\n" +
                "                    ดวงใจมาเฟีย)</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/tunstang/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"สิระสา\"><strong>สิระสา</strong></a></div>\n" +
                "                <div class=\"number3\">31 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.2M&nbsp;/&nbsp;&nbsp;0.4M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x03\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/padluang/story/view.php?id=981298\"\n" +
                "                                         target=\"_blank\" class=\"fr-l-s-grd\" title=\"ร้อยเรื่องรัก\">ร้อยเรื่องรัก</a>\n" +
                "                </div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/padluang/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"ละมุน\"><strong>ละมุน</strong></a></div>\n" +
                "                <div class=\"number3\">40 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.1M&nbsp;/&nbsp;&nbsp;0.2M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x04\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/jojoro/story/view.php?id=580483\" target=\"_blank\"\n" +
                "                                         class=\"fr-l-s-grd\" title=\"Pangea Online โลกใหม่\">Pangea Online โลกใหม่</a>\n" +
                "                </div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/jojoro/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"Great Polar Bear\"><strong>Great Polar Bear</strong></a></div>\n" +
                "                <div class=\"number3\">635 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.1M&nbsp;/&nbsp;&nbsp;4.0M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x05\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/Ladyann/story/view.php?id=954113\" target=\"_blank\"\n" +
                "                                         class=\"fr-l-s-grd\"\n" +
                "                                         title=\"วิวาห์ร้อนอ้อน(ให้)รัก...(ผ่านการพิจารณาจาก สนพ. อินเลิฟ)\">วิวาห์ร้อนอ้อน(ให้)รัก...(ผ่านการพิจารณาจาก\n" +
                "                    สนพ. อินเลิฟ)</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/Ladyann/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"ศศิร์นารา,จันทร์ร้อยดาว\"><strong>ศศิร์นารา,จันทร์ร้อยดาว</strong></a>\n" +
                "                </div>\n" +
                "                <div class=\"number3\">31 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.1M&nbsp;/&nbsp;&nbsp;0.3M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x06\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/i-thip/story/view.php?id=961874\" target=\"_blank\"\n" +
                "                                         class=\"fr-l-s-grd\" title=\"เพียงแสงส่องใจ (ตีพิมพ์กับสนพ.อิงค์)\">เพียงแสงส่องใจ\n" +
                "                    (ตีพิมพ์กับสนพ.อิงค์)</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/i-thip/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"ทิพย์ทิวา\"><strong>ทิพย์ทิวา</strong></a></div>\n" +
                "                <div class=\"number3\">112 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.1M&nbsp;/&nbsp;&nbsp;0.3M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x07\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/mu_mu_jung/story/view.php?id=971508\"\n" +
                "                                         target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                         title=\"สุภาพบุรุษอสูร -นิยายชุดสุภาพบุรุษลวงรัก-\">สุภาพบุรุษอสูร\n" +
                "                    -นิยายชุดสุภาพบุรุษลวงรัก-</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/mu_mu_jung/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"mu_mu_jung / มิรา / ม่านโมรี\"><strong>mu_mu_jung / มิรา /\n" +
                "                    ม่านโมรี</strong></a></div>\n" +
                "                <div class=\"number3\">11 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">&nbsp;0.1M&nbsp;/&nbsp;&nbsp;0.3M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x08\"></div>\n" +
                "                <div class=\"number1 \"><a href=\"http://writer.dek-d.com/sarnfun/story/view.php?id=796465\" target=\"_blank\"\n" +
                "                                         class=\"fr-l-s-grd\" title=\"บุพเพพรางใจ\">บุพเพพรางใจ</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/sarnfun/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"มหานที/แย้มยิ้ม/มโนวิจิตร\"><strong>มหานที/แย้มยิ้ม/มโนวิจิตร</strong></a>\n" +
                "                </div>\n" +
                "                <div class=\"number3\">25 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">94.2K&nbsp;/&nbsp;&nbsp;0.2M</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "                <div class=\"number number-x09\"></div>\n" +
                "                <div class=\"number1 for10\"><a href=\"http://writer.dek-d.com/pinkko911/story/view.php?id=1006758\"\n" +
                "                                              target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                              title=\"เจ็ดการ์ดราชันย์ครองพิภพ... Duel World Online\">เจ็ดการ์ดราชันย์ครองพิภพ...\n" +
                "                    Duel World Online</a></div>\n" +
                "                <div class=\"number2\"><a href=\"http://my.dek-d.com/pinkko911/\" target=\"_blank\" class=\"fr-l-s-grd\"\n" +
                "                                        title=\"Season Cloud\"><strong>Season Cloud</strong></a></div>\n" +
                "                <div class=\"number3\">67 ตอน</div>\n" +
                "                <div class=\"img-view\"></div>\n" +
                "                <div class=\"img-left-top5\"></div>\n" +
                "                <div class=\"view-top5\"><p class=\"fr-f-s-whi-top5\">81.4K&nbsp;/&nbsp;83.0K</p></div>\n" +
                "                <div class=\"img-right-top5\"></div>\n" +
                "            </li>\n" +
                "        </ul>\n" +
                "    </div>\n" +
                "    <div id=\"forslider\">\n" +
                "        <input type=\"hidden\" id=\"topstatus\" value=\"0\">\n" +
                "        <a href=\"#\" onclick=\"return hideshowtop()\">\n" +
                "            <div class=\"slidertxt\">ดู Top10</div>\n" +
                "            <div id=\"arrow\" class=\"arrowdown\"></div>\n" +
                "        </a>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"clear\"></div>\n" +
                "<div class=\"sto\"></div>\n" +
                "\n" +
                "<div class=\"fr-box\" style=\"display:block;\">\n" +
                "    <ul class=\"box1\">\n" +
                "        <li>\n" +
                "            <div class=\"box1-1\">\n" +
                "                <p class=\"ff-s-grd1\">เลือกชนิดบทความ</p>\n" +
                "                <select id=\"story_type\" name=\"select\" class=\"almenubox almenubox4\">\n" +
                "                    <option value=\"0\">ทั้งหมด</option>\n" +
                "                    <option value=\"1\">เรื่องสั้น</option>\n" +
                "                    <option value=\"2\">เรื่องยาว</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "            <div class=\"box1-2 fr-f-b-yel\">+</div>\n" +
                "        </li>\n" +
                "\n" +
                "        <li>\n" +
                "            <div class=\"box1-1\">\n" +
                "                <p class=\"ff-s-grd1\">แสดงผลเรียงตาม</p>\n" +
                "                <select id=\"sort\" class=\"almenubox almenubox4\">\n" +
                "                    <option value=\"0\">อัพเดทล่าสุด</option>\n" +
                "                    <option value=\"1\">มีผู้ชมมากสุด</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "            <div class=\"box1-2 fr-f-b-yel\">+</div>\n" +
                "        </li>\n" +
                "\n" +
                "        <li>\n" +
                "            <div class=\"box1-3\">\n" +
                "                <p class=\"ff-s-grd1\">สถานะบทความ</p>\n" +
                "                <select id=\"isend\" class=\"almenubox almenubox5\">\n" +
                "                    <option value=\"0\">ทั้งหมด</option>\n" +
                "                    <option value=\"2\">จบแล้ว</option>\n" +
                "                    <option value=\"1\">ยังไม่จบ</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "            <div class=\"box1-2 fr-f-b-yel\">+</div>\n" +
                "        </li>\n" +
                "\n" +
                "        <li>\n" +
                "            <div class=\"box1-4\">\n" +
                "                <p class=\"ff-s-grd1\">เลือกบทความจากหมวด</p>\n" +
                "                <select id=\"mainsub\" class=\"almenubox almenubox6\">\n" +
                "                    <option value=\"0\">ทุกหมวด</option>\n" +
                "                    <option value=\"1,-1\">--หมวดหลักฟรีสไตล์--</option>\n" +
                "                    <option value=\"1,1\">สบายๆ คลายเครียด</option>\n" +
                "                    <option value=\"1,2\">รักหวานแหวว</option>\n" +
                "                    <option value=\"1,3\">ซึ้งกินใจ</option>\n" +
                "                    <option value=\"1,4\">รักเศร้าๆ</option>\n" +
                "                    <option value=\"1,5\">นิทาน</option>\n" +
                "                    <option value=\"1,6\">ผจญภัย</option>\n" +
                "                    <option value=\"1,7\">สืบสวน</option>\n" +
                "                    <option value=\"1,8\">ระทึกขวัญ</option>\n" +
                "                    <option value=\"1,9\">สงคราม</option>\n" +
                "                    <option value=\"1,10\">ตลกขบขัน</option>\n" +
                "                    <option value=\"1,11\">กลอน</option>\n" +
                "                    <option value=\"1,12\">อดีต ปัจจุบัน อนาคต</option>\n" +
                "                    <option value=\"1,13\">จิตวิทยา</option>\n" +
                "                    <option value=\"1,14\">สังคม</option>\n" +
                "                    <option value=\"1,15\">หักมุม</option>\n" +
                "                    <option value=\"1,16\">แฟนตาซี</option>\n" +
                "                    <option value=\"1,17\">กำลังภายใน</option>\n" +
                "                    <option value=\"1,18\">วิทยาศาสตร์</option>\n" +
                "                    <option value=\"1,19\">แฟนฟิค</option>\n" +
                "                    <option value=\"1,20\">วรรณกรรมเยาวชน</option>\n" +
                "                    <option value=\"1,0\">อื่นๆ</option>\n" +
                "                    <option value=\"2,-1\">--หมวดหลักมีสาระ--</option>\n" +
                "                    <option value=\"2,1\">ความรู้รอบตัว</option>\n" +
                "                    <option value=\"2,2\">ความรู้เพื่อดำเนินชีวิต</option>\n" +
                "                    <option value=\"2,3\">เกร็ดประวัติศาสตร์</option>\n" +
                "                    <option value=\"2,4\">ความรู้เรื่องเรียน</option>\n" +
                "                    <option value=\"2,5\">ความรู้เอนทรานซ์</option>\n" +
                "                    <option value=\"2,6\">ความรู้กลเม็ด เทคนิค</option>\n" +
                "                    <option value=\"2,7\">เกร็ดท่องเที่ยว</option>\n" +
                "                    <option value=\"3,-1\">--หมวดหลักไลฟ์สไตล์--</option>\n" +
                "                    <option value=\"3,1\">สุขภาพ ความงาม</option>\n" +
                "                    <option value=\"3,2\">สิ่งของ intrend</option>\n" +
                "                    <option value=\"3,3\">ตามติดคนดัง</option>\n" +
                "                    <option value=\"3,4\">ดนตรี เพลง หนัง</option>\n" +
                "                    <option value=\"3,5\">ดีไซน์ กราฟิก</option>\n" +
                "                    <option value=\"3,6\">การ์ตูน เกมส์</option>\n" +
                "                    <option value=\"3,7\">ไอที เทคโนโลยี</option>\n" +
                "                    <option value=\"3,0\">อื่นๆ</option>\n" +
                "                </select>\n" +
                "            </div>\n" +
                "            <div class=\"box1-2 fr-f-b-yel\">=</div>\n" +
                "        </li>\n" +
                "\n" +
                "        <li>\n" +
                "            <div class=\"box1-5\"><p id=\"count\" class=\"fr-f-b-yel fr-color-yelbla\">360,260</p>\n" +
                "\n" +
                "                <p class=\"ff-s-yel textbox1\">เรื่อง</p></div>\n" +
                "        </li>\n" +
                "    </ul>\n" +
                "    <div class=\"clear\"></div>\n" +
                "    <div class=\"boxgo\"><a href=\"#\" target=\"_blank\" title=\"ค้นหา\" class=\"notext\"\n" +
                "                          onclick=\" return searchsort(1)\">ค้นหา</a></div>\n" +

                "</div>\n" +
                "<!--fr-box-->\n" +
                "\n" +

                "\n" +
                "<div style=\"text-align: center;\" class=\"paging\">\n" +
                "    <ul class=\"fr-number paging-default\">\n" +
                "        <li class=\"nu2\">1</li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=2&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 2\" page=\"2\">2</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=3&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 3\" page=\"3\">3</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=4&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 4\" page=\"4\">4</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=5&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 5\" page=\"5\">5</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=6&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 6\" page=\"6\">6</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=7&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 7\" page=\"7\">7</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=8&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 8\" page=\"8\">8</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=9&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 9\" page=\"9\">9</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=10&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 10\" page=\"10\">10</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=2&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 11\" onclick=\"\" page=\"11\">»</a></li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "<div class=\"clear\"></div>\n" +
                "<div id=\"anchor\" class=\"clear\"></div>\n" +
                "<ul class=\"fr-book\" id=\"anchor\">\n" +
                "\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/justbeamc/story/view.php?id=990428\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/26/2920744/t_113393578\" class=\"book-img1\"\n" +
                "             alt=\".:: Baby Hunnie ll Krishun ll ::. Yaoi\" width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/justbeamc/story/view.php?id=990428\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\".:: Baby Hunnie ll Krishun ll ::. Yaoi\">.:: Baby Hunnie ll Krishun ll ::. Yaoi\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/justbeamc\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"Justbeamc\">by <strong>Justbeamc</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/justbeamc/story/view.php?id=990428\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\" title=\"โตขึ้นฮุนนี่อยากเป็นอะไรครับ ^^  ฮุนนี่อยากเป็นเจ้าสาวของป่ะป๋า &gt;0\">\n" +
                "                โตขึ้นฮุนนี่อยากเป็นอะไรครับ ^^ ฮุนนี่อยากเป็นเจ้าสาวของป่ะป๋า &gt;0\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=19&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,19,'/writer/list2013.php?main=1&amp;sub=19&amp;ajax=1');\">แฟนฟิค</a><br>2013-09-27\n" +
                "        21:40:10</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>13 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">5,632 / 13,259</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment-over\">\n" +
                "            <p class=\"fr-f-s-whi\">1,013</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/vilandara/story/view.php?id=625595\" target=\"_blank\">\n" +
                "        <img src=\"http://myc.dek-d.com/dek-d/story/img/storythumb.gif\" class=\"book-img1\" alt=\"วิวาห์กากี\" width=\"70\"\n" +
                "             height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/vilandara/story/view.php?id=625595\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"วิวาห์กากี\">วิวาห์กากี\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/vilandara\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"วิลันดารา\">by <strong>วิลันดารา</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/vilandara/story/view.php?id=625595\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\" title=\"\" เธอแต่งงานกับคุณลุงผมไม่ว่า=\"\" มาแต่งใหม่กับพี่ผม=\"\"\n" +
                "            ผมก็พอทนอายชาวบ้านได้=\"\" แต่นี่เธอดันมาแต่งกับพ่ออีก=\"\" กากีขนาดนี้ผมรับไม่ได้!\"=\"\"\n" +
                "            \"รับไม่ได้มันก็เรื่องของแก=\"\" คืนเจ้าสาวมาให้พ่อเดี๋ยวนี้=\"\"\n" +
                "            ไม่งั้นแกกับพ่อขาดกัน!\"\"=\"\">\"เธอแต่งงานกับคุณลุงผมไม่ว่า มาแต่งใหม่กับพี่ผม ผมก็พอทนอายชาวบ้านได้\n" +
                "            แต่นี่เธอดันมาแต่งกับพ่ออีก กากีขนาดนี้ผมรับไม่ได้!\" \"รับไม่ได้มันก็เรื่องของแก คืนเจ้าสาวมาให้พ่อเดี๋ยวนี้\n" +
                "            ไม่งั้นแกกับพ่อขาดกัน!\"\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=4&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,4,'/writer/list2013.php?main=1&amp;sub=4&amp;ajax=1');\">นิยาย\n" +
                "            รักเศร้าๆ</a><br>2013-09-27 21:40:06</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>31 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">3,561 / 69,063</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment-over\">\n" +
                "            <p class=\"fr-f-s-whi\">2,494</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/LP_Me_Kapong/story/view.php?id=652827\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/24/648930/t_105768666\" class=\"book-img1\" alt=\"Healer Master (Online)\"\n" +
                "             width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/LP_Me_Kapong/story/view.php?id=652827\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"Healer Master (Online)\">Healer Master (Online)\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/LP_Me_Kapong\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"LPหมีกระป๋อง\">by\n" +
                "            <strong>LPหมีกระป๋อง</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/LP_Me_Kapong/story/view.php?id=652827\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"เพียร์ถูกฝ้าย ยัยเพื่อนตัวดีชวนให้มาเล่นเกมโดยที่เธอไม่ค่อยอยากเล่นเท่าไรเพราะมันเป็นเกมเสมือนจริงที่เธอไม่ชอบ เธอจึงตัดสินใจแล้วว่า เธอจะเล่นเกมโดยไม่ฆ่ามอนสเตอร์แม้แต่ตัวเดียว\">\n" +
                "                เพียร์ถูกฝ้าย\n" +
                "                ยัยเพื่อนตัวดีชวนให้มาเล่นเกมโดยที่เธอไม่ค่อยอยากเล่นเท่าไรเพราะมันเป็นเกมเสมือนจริงที่เธอไม่ชอบ\n" +
                "                เธอจึงตัดสินใจแล้วว่า เธอจะเล่นเกมโดยไม่ฆ่ามอนสเตอร์แม้แต่ตัวเดียว\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=16&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,16,'/writer/list2013.php?main=1&amp;sub=16&amp;ajax=1');\">นิยาย\n" +
                "            แฟนตาซี</a><br>2013-09-27 21:39:57</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>15 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">14,895 / 58,572</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment-over\">\n" +
                "            <p class=\"fr-f-s-whi\">1,733</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +

                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/beemlovely/story/view.php?id=1021121\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/27/0299/4909/t_113789652\" class=\"book-img1\"\n" +
                "             alt=\"รักฉบับร้ายกวนหัวใจแบดกายตัวแสบ\" width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/beemlovely/story/view.php?id=1021121\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"รักฉบับร้ายกวนหัวใจแบดกายตัวแสบ\">รักฉบับร้ายกวนหัวใจแบดกายตัวแสบ\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/beemlovely\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"Angel Dark\">by <strong>Angel\n" +
                "            Dark</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/beemlovely/story/view.php?id=1021121\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"เรื่องราวของแก๊ง SKY RACK กับ ANGEL DARK เมื่อทั้งสองกลุ่มมาเจอกันเรื่องวุ่นๆ จะเกิดขึ้นมากมายแค่ไหนกันนะ เริ่มด้วย คู่ ปูเป้ ที่พกความติ๊งต๋องสนใจ และบ้าไม่มีใครเกิน VS จีซัส หนุ่มหล่อ แบดบอย สุดกวน \">\n" +
                "                เรื่องราวของแก๊ง SKY RACK กับ ANGEL DARK เมื่อทั้งสองกลุ่มมาเจอกันเรื่องวุ่นๆ\n" +
                "                จะเกิดขึ้นมากมายแค่ไหนกันนะ เริ่มด้วย คู่ ปูเป้ ที่พกความติ๊งต๋องสนใจ และบ้าไม่มีใครเกิน VS จีซัส\n" +
                "                หนุ่มหล่อ แบดบอย สุดกวน\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=0&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,0,'/writer/list2013.php?main=1&amp;sub=0&amp;ajax=1');\">อื่น\n" +
                "            ๆ</a><br>2013-09-27 21:39:56</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>3 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">8 / 8</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">0</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/pimrose/story/view.php?id=940583\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/26/2763309/t_112605989\" class=\"book-img1\"\n" +
                "             alt=\"♔ ROOMMATE | EXO SNSD SHINEE APINK ♔\" width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/pimrose/story/view.php?id=940583\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"♔ ROOMMATE | EXO SNSD SHINEE APINK ♔\">♔ ROOMMATE | EXO SNSD SHINEE APINK ♔\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/pimrose\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"'อันดามัน\">by <strong>'อันดามัน</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/pimrose/story/view.php?id=940583\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"ห้าหนุ่มแห่ง 'โลลิเซ่' ยู ใครๆ ก็รู้ว่าพวกเขามันตัวอันตราย!   เตรียมพบกับรักหลากสไตล์ที่จะทำให้คุณ 'ตกหลุมรัก' ได้โดยไม่รู้ตัว ♥\">\n" +
                "                ห้าหนุ่มแห่ง 'โลลิเซ่' ยู ใครๆ ก็รู้ว่าพวกเขามันตัวอันตราย! เตรียมพบกับรักหลากสไตล์ที่จะทำให้คุณ\n" +
                "                'ตกหลุมรัก' ได้โดยไม่รู้ตัว ♥\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=19&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,19,'/writer/list2013.php?main=1&amp;sub=19&amp;ajax=1');\">แฟนฟิค</a><br>2013-09-27\n" +
                "        21:39:47</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>47 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">2,441 / 10,287</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">947</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/pim2001/story/view.php?id=1021427\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/27/0301/4102/t_113795066\" class=\"book-img1\"\n" +
                "             alt=\"LOVE ADDICT เสพติดรัก กับดักนายกวนประสาท\" width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/pim2001/story/view.php?id=1021427\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"LOVE ADDICT เสพติดรัก กับดักนายกวนประสาท\">LOVE ADDICT เสพติดรัก\n" +
                "                กับดักนายกวนประสาท\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/pim2001\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"Risa\">by\n" +
                "            <strong>Risa</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/pim2001/story/view.php?id=1021427\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"จะทำยังไงดี!! ทำไมฉันเห็นรอยยิ้มของหมอนั่นแล้วใจต้องสั่น หน้าร้อนอย่างไม่รู้ตัว นี่เค้าเรียกว่า...แอบชอบใช่มั้ย!? ไม่นะ! ฉันจะไม่ยอมชอบนายเด็ดขาด เราเป็นคู่กัด ไม่ใช่คู่รัก!!\">\n" +
                "                จะทำยังไงดี!! ทำไมฉันเห็นรอยยิ้มของหมอนั่นแล้วใจต้องสั่น หน้าร้อนอย่างไม่รู้ตัว\n" +
                "                นี่เค้าเรียกว่า...แอบชอบใช่มั้ย!? ไม่นะ! ฉันจะไม่ยอมชอบนายเด็ดขาด เราเป็นคู่กัด ไม่ใช่คู่รัก!!\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=2&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,2,'/writer/list2013.php?main=1&amp;sub=2&amp;ajax=1');\">นิยาย\n" +
                "            รักหวานแหวว</a><br>2013-09-27 21:39:35</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>1 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">6 / 6</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">0</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/louth-ormond/story/view.php?id=1020573\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/27/0319/2131/t_113782842\" class=\"book-img1\" alt=\"Louth-Ormond Square .. }\"\n" +
                "             width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/louth-ormond/story/view.php?id=1020573\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"Louth-Ormond Square .. }\">Louth-Ormond Square .. }\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/louth-ormond\" target=\"_blank\" class=\"fr-l-s-grd b1\"\n" +
                "           title=\"Principality of Louth-Ormond\">by <strong>Principality of Louth-Ormond</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/louth-ormond/story/view.php?id=1020573\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\" title=\"[ L.O.M ; Town ]\">[ L.O.M ; Town ]\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=0&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,0,'/writer/list2013.php?main=1&amp;sub=0&amp;ajax=1');\">อื่น\n" +
                "            ๆ</a><br>2013-09-27 21:39:33</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>7 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">6 / 6</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">0</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/bowviw/story/view.php?id=1020113\" target=\"_blank\">\n" +
                "        <img src=\"http://myc.dek-d.com/dek-d/story/img/storythumb.gif\" class=\"book-img1\" alt=\"เราแตกต่างกันเกินไป\"\n" +
                "             width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/bowviw/story/view.php?id=1020113\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"เราแตกต่างกันเกินไป\">เราแตกต่างกันเกินไป\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/bowviw\" target=\"_blank\" class=\"fr-l-s-grd b1\"\n" +
                "           title=\"นู๋แพรว ยัยเด็กซ่าส์ น่ารักอ่ะ\">by <strong>นู๋แพรว ยัยเด็กซ่าส์ น่ารักอ่ะ</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/bowviw/story/view.php?id=1020113\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\" title=\"เค๊าเป็นคนเดียวที่หล่อนรักและจะรักตลอดไป\">\n" +
                "                เค๊าเป็นคนเดียวที่หล่อนรักและจะรักตลอดไป\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=2&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,2,'/writer/list2013.php?main=1&amp;sub=2&amp;ajax=1');\">นิยาย\n" +
                "            รักหวานแหวว</a><br>2013-09-27 21:39:30</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>12 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">27 / 27</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">0</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/bowlinggogo/story/view.php?id=1021579\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/27/0291/5302/t_113797234\" class=\"book-img1\" alt=\"[EXO SNSD] May I Love U?\"\n" +
                "             width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/bowlinggogo/story/view.php?id=1021579\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"[EXO SNSD] May I Love U?\">[EXO SNSD] May I Love U?\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/bowlinggogo\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"Bowling\">by <strong>Bowling</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/bowlinggogo/story/view.php?id=1021579\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"Luhan : ทำไมผมจะต้องทำความรู้จักกับยัยนั่นด้วยนะ =_= เพราะพ่อแท้ๆเลย!!  Seohyun : ชาติที่แล้วฉันไปทำกรรมอะไรไว้นักหนานะ ชาตินี้ฉันถึงได้มาเจอกับเรื่องแบบนี้ ฮือๆๆ T^T\">\n" +
                "                Luhan : ทำไมผมจะต้องทำความรู้จักกับยัยนั่นด้วยนะ =_= เพราะพ่อแท้ๆเลย!! Seohyun :\n" +
                "                ชาติที่แล้วฉันไปทำกรรมอะไรไว้นักหนานะ ชาตินี้ฉันถึงได้มาเจอกับเรื่องแบบนี้ ฮือๆๆ T^T\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=2&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,2,'/writer/list2013.php?main=1&amp;sub=2&amp;ajax=1');\">นิยาย\n" +
                "            รักหวานแหวว</a><br>2013-09-27 21:39:24</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>2 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">10 / 10</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">1</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "<li>\n" +
                "    <a href=\"http://writer.dek-d.com/naruto-kitty/story/view.php?id=583894\" target=\"_blank\">\n" +
                "        <img src=\"http://image.dek-d.com/23/2095939/t_103509811\" class=\"book-img1\"\n" +
                "             alt=\"ห้องเก็บของส่วนตั๊วส่วนตัวจ้ะ =o=!\" width=\"70\" height=\"70\">\n" +
                "    </a>\n" +
                "\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://writer.dek-d.com/naruto-kitty/story/view.php?id=583894\" target=\"_blank\">\n" +
                "            <h6 class=\"fr-f-m-ora\" title=\"ห้องเก็บของส่วนตั๊วส่วนตัวจ้ะ =o=!\">ห้องเก็บของส่วนตั๊วส่วนตัวจ้ะ =o=!\n" +
                "            </h6></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text6\">\n" +
                "        <a href=\"http://my.dek-d.com/naruto-kitty\" target=\"_blank\" class=\"fr-l-s-grd b1\" title=\"K_cat\">by\n" +
                "            <strong>K_cat</strong></a>\n" +
                "    </div>\n" +
                "    <div class=\"book-text1\">\n" +
                "        <a href=\"http://writer.dek-d.com/naruto-kitty/story/view.php?id=583894\" target=\"_blank\">\n" +
                "            <p class=\"fr-f-s-gry1 b2\"\n" +
                "               title=\"อิสอีสอ่ะส่วนตัวเจ้าค่ะ -..-  ก็ไม่มีอะไรมากหรอกค่ะแค่เก็บธีมทำเองของทำเองเน่าๆค่ะ &gt;O\">\n" +
                "                อิสอีสอ่ะส่วนตัวเจ้าค่ะ -..- ก็ไม่มีอะไรมากหรอกค่ะแค่เก็บธีมทำเองของทำเองเน่าๆค่ะ &gt;O\n" +
                "            </p></a>\n" +
                "    </div>\n" +
                "    <p class=\"book-text2 book-text3 fr-f-s-gry1\"><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=-1&amp;page=1\"\n" +
                "                                                    target=\"_blank\" onclick=\"return chgMenu(1); \">นิยาย ฟรีสไตล์</a>\n" +
                "        &gt;<br><a href=\"http://www.dek-d.com/writer/frame.php?main=1&amp;sub=0&amp;page=1\" target=\"_blank\"\n" +
                "                   onclick=\" chgMenu(1); return subselect(1,0,'/writer/list2013.php?main=1&amp;sub=0&amp;ajax=1');\">อื่น\n" +
                "            ๆ</a><br>2013-09-27 21:39:08</p>\n" +
                "\n" +
                "    <p class=\"book-text2 fr-f-s-gry1 chapter\"><strong>59 ตอน</strong></p>\n" +
                "    <br>\n" +
                "\n" +
                "    <div class=\"book-bottom notext\" title=\"ยังไม่จบ\">ยังไม่จบ</div>\n" +
                "    <div class=\"book-text7 fr-f-s-gry1\">\n" +
                "        <div class=\"viewarea\">\n" +
                "            <div class=\"img-view\"></div>\n" +
                "            <div class=\"img-left\"></div>\n" +
                "            <div class=\"view\">\n" +
                "                <p class=\"fr-f-s-whi\">22 / 602</p>\n" +
                "            </div>\n" +
                "            <div class=\"img-right\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"img-comment\"></div>\n" +
                "        <div class=\"img-left\"></div>\n" +
                "        <div class=\"view comment\">\n" +
                "            <p class=\"fr-f-s-whi\">8</p>\n" +
                "        </div>\n" +
                "        <div class=\"img-right\"></div>\n" +
                "    </div>\n" +
                "</li>\n" +
                "</ul>\n" +
                "<div class=\"clear\"></div>\n" +

                "<br><br>\n" +
                "\n" +
                "<div style=\"text-align: center;\" class=\"paging clear\">\n" +
                "    <center><ul class=\"fr-number paging-default\">\n" +
                "        <li class=\"nu2\">1</li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=2&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 2\" page=\"2\">2</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=3&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 3\" page=\"3\">3</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=4&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 4\" page=\"4\">4</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=5&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 5\" page=\"5\">5</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=6&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 6\" page=\"6\">6</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=7&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 7\" page=\"7\">7</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=8&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 8\" page=\"8\">8</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=9&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 9\" page=\"9\">9</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=10&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 10\" page=\"10\">10</a></li>\n" +
                "        <li><a href=\"http://www.dek-d.com/writer/frame.php?page=2&amp;main=-1&amp;sub=-1&amp;isend=0&amp;story_type=0&amp;sort=0\"\n" +
                "               ajax=\"http://www.dek-d.com/writer/list2013.php\" target=\"_self\" title=\"ไปหน้า 11\" onclick=\"\" page=\"11\">»</a></li>\n" +
                "    </ul></center>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\">\n" +
                "\t\t\t<!--\n" +
                "\t\t\t\t$('.fr-number li a').live('click' , function(e){\n" +
                "\t\t\t\t\te.preventDefault();\n" +
                "\t\t\t\t\treturn false;\n" +
                "\t\t\t\t});\n" +
                "\t\t\t\t \n" +
                "\t\t\t\t\tsetpage(1);\n" +
                "\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\tsetdropdown(-1);\n" +
                "\t\t//-->\n" +
                "\n" +
                "</script>\n" +

                "<div class=\"clear\"></div>\n" +
                "\n" +
                "<div class=\"app\">\n" +
                "    <div class=\"app-right\">\n" +
                "        <h5>ข้อตกลง</h5>\n" +
                "        <ul class=\"app-r-u\">\n" +
                "            <li><p>Dek-D.com มิได้ให้บริการพื้นที่สำหรับการอัพโหลดไฟล์หรือจัดเก็บไฟล์<br>ประเภทใดๆยกเว้นรูปภาพเพื่อการประกอบบทความเท่านั้น\n" +
                "            </p></li>\n" +
                "            <li><p>ผู้ใช้งานโปรดงดการลงผลงานที่เสี่ยงต่อปัญหาลิขสิทธิ์เช่น การแจกเนื้อเพลง<br>\n" +
                "                แจกลิงค์ไปเว็บภายนอก ที่สามารถคลิกและดาวน์โหลดไฟล์เพลงได้ รวมทั้ง<br>\n" +
                "                ตัวการ์ตูนข้อความ รูปถ่าย วีดีโอคลิปต่างๆที่มีลิขสิทธิ์ ฯลฯ</p></li>\n" +
                "            <li><p>หากทีมงานตรวจพบ ขอทำการลบโดยไม่ต้องแจ้งให้ทราบล่วงหน้า ผู้ลง<br>\n" +
                "                บทความอาจต้องเป็นผู้รับผิดชอบ หากมีการฟ้องร้องด้านลิขสิทธิ์ </p></li>\n" +
                "            <li><p>ผู้ใดพบเจอบทความละเมิดลิขสิทธิ์โปรดแจ้งทีมงานในทันที <br>\n" +
                "                <span class=\"t-c-ora\">contact(at)dek-d.com</span> หรีอ โทร <span class=\"t-c-ora\">0-2860-1142</span> (\n" +
                "                จ-ศ 0900-1800 )<br>\n" +
                "                เพื่อ ดำเนินการถอดออกจากระบบ</p></li>\n" +
                "        </ul>\n" +
                "\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"clear\"></div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webView.setHorizontalScrollbarOverlay(false);
        webView.loadDataWithBaseURL("http://www.dek-d.com/writer/frame.php", newhtml.concat(newhtml2).replace("$('#menu0'+(parseInt(mainsub[0])+1)).show();", "").replace("$('#meu-0'+mainsub[0]).show();", "").replace("$('div.menu-box').show();", "").replace("$('#menu01').show();", "").replace("$('#top5').empty().append(data.statKey);", "$('#top5').empty();").replace("$('.top5').empty().append(data.top5);", "$('.top5').empty();").replace("hideshowtop();", "").replace("$('.top5').empty().append('<li style=\"padding:52px 0px 52px 310px;\"><img src=\"/writer/img/bigrotation.gif\" width=32 height=32 /></li>');", "").replace("$('ul.fr-book').empty().append('<li style=\"padding-left:310px;\"><img src=\"/writer/img/bigrotation.gif\" width=32 height=32 /></li>');", "").replace("$('.top15').empty().append('Top 5');", "").replace("/writer/frame2012.css?ver=0.9", "").replace("</head>", "<style type=\"text/css\">\n" +
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
