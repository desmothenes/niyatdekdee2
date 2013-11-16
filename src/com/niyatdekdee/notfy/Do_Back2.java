package com.niyatdekdee.notfy;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E425 on 17/7/2556.
 */
class Do_Back2 extends AsyncTask<Context, String, Long> {
    static Map<String, Long> sessionTime = new HashMap<String, Long>();
    static Map<String, String> sessionStatus = new HashMap<String, String>();
    static Map<String, String> sessionStatusTitle = new HashMap<String, String>();
    //DatabaseAdapter db;
    Context context;
    private static boolean tried = false;
    private static int floop;
    static Map<String, String> sessionId = new HashMap<String, String>();
    private ArrayList<String> Listtemp = new ArrayList<String>();
    final static ArrayList<String> ListViewContent = new ArrayList<String>();
    final static ArrayList<String> ListViewStatus = new ArrayList<String>();
    private Time now;
    static List<Cookie> cookies;
    private boolean isTemp;
    private boolean isErr = false;
    private boolean loginsuscess = false;

    public Do_Back2(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //MainActivity.dialog.show();
        MainActivity.ListViewStatus.clear();
        MainActivity.ListViewContent.clear();
        MainActivity.niyayTable.clear();
        showAllBookOffline();


        if (MainActivity.db != null)
            MainActivity.db.close();
        if (MainActivity.dialog.isShowing())
            MainActivity.dialog.dismiss();


        MainActivity.ListViewContent = ListViewContent;
        MainActivity.ListViewStatus = ListViewStatus;
        if (MainActivity.ListViewContent.size() == 0) {
            if (loginsuscess && Setting.getisLogin(context) && Setting.getdisplayResult(context))
                Toast.makeText(context, "ไม่พบตอนใหม่ใน Favorite Writer", Toast.LENGTH_LONG).show();
            else if (Setting.getisLogin(context) && Setting.getdisplayResult(context))
                Toast.makeText(context, "ไม่มีตอนใหม่ หรือ เข้าสู่ระบบไม่ได้", Toast.LENGTH_LONG).show();

            if (floop == 0 || loginsuscess)
                MainActivity.ListViewContent.add("<big><big>โปรดเพิ่มนิยายเรื่องแรก\n(กดเครื่องหมายบวกสีเขียว\nเพื่อเลือกวิธีการเพิ่มนิยาย)</big></big>");
            else {
                Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                Log.e("onPreExecute", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            }

            //if (MainActivity.niyayTable.size() == 0) MainActivity.niyayTable.add(new String[4]);
        }

        if (isErr) {
            Log.e("onPreExecute", "isErr");
            Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
        }

        MainActivity.myList.setAdapter(MainActivity.listAdap);
        //MainActivity.dialog.dismiss();
        tried = false;
    }

    protected void onProgressUpdate(String... progress) {
        if (progress[0].equals("-1")) {
            //Toast.makeText(context, "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
            Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            if (!tried) {
                tried = true;
                this.cancel(true);
                //this.execute(context);
            }
        } else if (progress[0].equals("-99")) {
            int index = Integer.parseInt(progress[1]);
            ListViewContent.set(index, progress[2]);
            ListViewStatus.set(index, "ตรวจสอบเสร็จสิ้น");
            MainActivity.myList.setAdapter(MainActivity.listAdap);
        } else {
            MainActivity.dialog.setMessage(progress[0]);
        }

    }

    @Override
    protected Long doInBackground(Context... contexts) {
        int index = 0;
        for (String data[] : MainActivity.niyayTable) {
            String result = check_new_cp(index, data);
            if (result.equals("err")) {

            } else {
                MainActivity.niyayTable.get(index)[4] = result;
            }
            index++;
        }
        return null;
    }


    protected void onPostExecute(Long result) {
        Log.e("end back", "end back");
        //MainActivity.db = new DatabaseAdapter(context);
/*        Time post = new Time();
        post.setToNow();
        if (MainActivity.mGaTracker != null)
            MainActivity.mGaTracker.sendTiming("resources", post.toMillis(true) - now.toMillis(true), "doback", null);*/

		/*		for (Map.Entry<String, String> entry : sessionStatus.entrySet())
            Log.e(entry.getKey(), entry.getValue());*/
    }

    private String check_new_cp(final int index, final String[] data) {

        int status = 0;
        //Log.e("data[4]",data[4]);
        String title = data[4].equals("non") ? "ยังไม่มีตอนปัจจุบัน รอตอนใหม่" : data[4];
        //Log.e("data[4]",data[4]);
        final String id = data[0];
        final String url = data[2];
        final String chapter = data[3];
        String text1 = "";

        //if (title.contains(">")) title = title.substring(title.indexOf(">"));

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(new URI(url + chapter));
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 4000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            if (cookies != null && !cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    httpclient.getCookieStore().addCookie(cookie);
                }
            }
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            text1 = httpclient.execute(httpget, responseHandler);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.getMessage());
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
            //return "err";
            HttpGet method = new HttpGet(url + chapter);
            BufferedReader in = null;
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(url + chapter));
                request.setHeader("Range", "bytes=0-1023");
                HttpResponse response = client.execute(method);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "TIS620"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line).append(NL);
                }
                in.close();
                text1 = sb.toString();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            //	Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "err";

        } catch (URISyntaxException e) {
            Log.e("URISyntaxException", e.getMessage());
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "err";

        } catch (IllegalStateException e) {
            Log.e("IllegalStateException", text1);
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "err";

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return "err";

        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        if (text1.contains("<title>")) {
            final String text2 = text1;
            new Thread() {
                public void run() {
                    try {
                        ContextWrapper cw = new ContextWrapper(context);
                        File temp = new File(cw.getDir("temp", Context.MODE_PRIVATE), id + ".html");
                        //System.out.println(temp.getAbsolutePath());
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "tis620"));
                        //bw.write(text1.replace("href=\"/", String.format("href=\"%s/",url.substring(0, url.lastIndexOf("/")))).replace("href=\"view", String.format("href=\"%s/view",url.substring(0, url.lastIndexOf("/")))));
                        bw.write(text2);
                        bw.flush();
                        bw.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
            final String start = text1.substring(text1.indexOf("<title>") + 7);
            text1 = Jsoup.parse((start.substring(start.indexOf(">") + 2, start.indexOf("</title>")))).text();
        } else {
            text1 = "ยังไม่มีตอนปัจจุบัน รอตอนใหม่";
        }

		/*		Log.e("title",(title == null) ? "null" : title);
        Log.e("text1",text1);
		Log.e("compare",Integer.toString(text1.compareTo(title)));		*/

        if (title == null) title = "";
        else if (title.contains(">")) title = title.substring(title.indexOf(">") + 2);
        if (text1.contains(">")) text1 = text1.substring(text1.indexOf(">") + 2);
        if (title.isEmpty()) {
            Log.e(id, "title isEmpty()");
            title = text1;
            status = -1;
        } else if (!text1.trim().contains(title.trim())) {
            /*Log.e("title",title);
            Log.e("text1",text1);
			Log.e("compare",(text1.equals(title))? "same" : "not same");*/
			/*			System.out.println("text1");
			System.out.println(text1);
			for (int i=0; i < text1.trim().length();i++) {
				System.out.print(text1.trim().charAt(i));System.out.println((int)text1.trim().charAt(i));
			}
			System.out.println("title");
			System.out.println(title);
			for (int i=0; i < title.trim().length();i++) {
				System.out.print(title.trim().charAt(i));System.out.println((int)title.trim().charAt(i));
			}*/
            status = 1; //current chapter update
        } else if (!text1.contains("ยังไม่มีตอนปัจจุบัน รอตอนใหม่") && !text1.contains("non")) {
            status = 2;
        }
        //Log.e("status", Integer.toString(status));


        if (status == 0) {
            String temp =
                    "<br /><p><font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
        } else if (status == 2) {
            String temp =
                    "<br /><p><font color=#6E6E6E>ถ้าจบตอน กดปุ่มเพิ่มตอน เพื่อเข้าสู่สถานะรอตอนใหม่</font><br />" +
                            "<font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
            sessionStatus.put(url + chapter, temp);
        } else if (status == 1 || status == -1) {
            String temp =
                    "<br /><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
                            "<font color=#33B6EA>เรื่อง :" + data[1] + "</font><br />" +
                            "<font color=#cc0029> ตอน : " + text1 + " (" + chapter + ")</font></p>";
            publishProgress("-99", Integer.toString(index), temp);
            sessionStatus.put(url + chapter, temp.replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน\nถ้าจบตอน กดปุ่มเพิ่มตอน\nเพื่อเข้าสู่สถานะรอตอนใหม่"));
        }

		/*		Log.e("content",
				"id: " +data[0]+"\n"+
						"name: " +data[1]+"\n" +
						"url: " +data[2]+"\n"+
						"chapter: " +data[3]+"\n"+
						"status: " +data[4]+"\n"+
						"title: " +Integer.toString(status)+"\n"+
						"text1: " +text1);*/
        Time now = new Time();
        now.setToNow();
        sessionTime.put(url, now.toMillis(true));
        //Log.e("set "+url,Long.toString(now.toMillis(true)));

        if (status == 0 || status == 2) return title;
        else if (status == 1 || status == -1) return text1;
        return "";
    }


    private void showAllBookOffline() {
        // TODO Auto-generated method stub
        Log.e("in", "showAllBookOffline");

		/*		ListViewContent.clear();
		MainActivity.niyayTable.clear();*/
        MainActivity.db = new DatabaseAdapter(context);
        MainActivity.db.close();
        MainActivity.db.open();
        Listtemp.clear();

        Cursor c = MainActivity.db.getAllNiyay();
        floop = c.getCount();
        Log.e("floop", Integer.toString(floop));
        if (c.isClosed() && floop == 0) {
            //Log.e("ck db", "not ok");
            //MainActivity.db.close();
            return;
        } else {
            //Log.e("ok ?", "ok");
        }

        c.moveToFirst();
        int i = 0;
        do {
            i++;
            String[] temp = new String[5];
            temp[0] = c.getString(0);
            temp[1] = c.getString(1);
            temp[2] = c.getString(2);
            temp[3] = c.getString(3);
            temp[4] = c.getString(4);
            /*Log.e("content",
					"id: " +c.getString(0)+"\n"+
							"name:" +c.getString(1)+"\n" +
							"url: " +c.getString(2)+"\n"+
							"chapter: " +c.getString(3)+"\n"+
							"title: " +c.getString(4));*/
            MainActivity.niyayTable.add(temp);
        } while (c.moveToNext());
        Log.e("loop end", Integer.toString(i));
        for (String[] stemp : MainActivity.niyayTable) {
            displayBookOffline(stemp);
        }
        for (String stemp : Listtemp) {
            //System.out.println("Listtemp: "+ stemp);
            ListViewContent.add(stemp);
            ListViewStatus.add("กำลังตรวจสอบตอนใหม่");
        }
        MainActivity.db.close();
    }


    private void displayBookOffline(String c[]) {
        Log.e("in", "displayBookOffline");

        //Log.e("title0",(data[4] != null) ? data[4]:"");
        String title = c[4];

        //if (title.contains(">")) title = title.substring(title.indexOf(">"));
        //Log.e("title",(title != null) ? title:" ");

        if (title != null) {
            if (title.contains(">"))
                title = title.substring(title.indexOf(">") + 2);
            //dialog.setTitle(title);
            Listtemp.add(
                    "<br /><p><font color=#33B6EA>เรื่อง :" + c[1] + "</font><br />" +
                            "<font color=#cc0029> ล่าสุด ตอน : " + title + " (" + c[3] + ")</font></p>");
        }
        /*Log.e("content",
				"id: " +data[0]+"\n"+
						"name:" +data[1]+"\n" +
						"url: " +data[2]+"\n"+
						"chapter: " +data[3]+"\n"+
						"title: " +data[4]);*/
    }

    private void loadUpdate() {
        Log.v("favfin", "favfin");
        publishProgress("favorite writer");

        Document doc = null;
        if (sessionId == null || !loginsuscess) return;
        try {
            doc = Jsoup.connect("http://www.dek-d.com/story_message2012.php")
                    .cookies(sessionId).timeout(3000)
                    .get();
        } catch (IOException e) {
            publishProgress("-1");
            //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //System.out.println(doc.html());
        Elements link1 = doc.select(".novel");
        if (link1 == null) return;
        for (Element link : link1) {
            final String stext = link.text();
            //Log.v("stext", stext);
            String[] temp = new String[5];
            temp[0] = "-2";
            temp[1] = stext.substring(0, stext.indexOf("ตอนที่"));
            temp[2] = link.select("a").attr("href");
            temp[3] = "-2";
            temp[4] = stext.substring(stext.indexOf("ตอนที่"));
            ;
            publishProgress(temp[1]);
            MainActivity.niyayTable.add(temp);

            if (sessionStatus.get(temp[2]) != null) {
                ListViewContent.add(sessionStatus.get(temp[2]));
            } else {
                //MainActivity.ListViewContent.add(stext.replace("ตอนที่", "\nตอนที่"));
                ListViewContent.add(
                        "<br /><p><font color=#339900>[fav]มีการอัพเดตตอนปัจจุบัน</font><br />" +
                                "<font color=#33B6EA>เรื่อง :" + temp[1] + "</font><br />" +
                                "<font color=#cc0029>" + temp[4] + "</font></p>");
                sessionStatus.put(temp[2], ListViewContent.get(ListViewContent.size() - 1).replace("มีการอัพเดตตอนปัจจุบัน", "มีการอัพเดตตอนปัจจุบัน\nถ้าจบตอน กดปุ่มเพิ่มตอน\nเพื่อเข้าสู่สถานะรอตอนใหม่"));
            }
        }

        Log.v("listView", "listView");
        /*		for (String i : ListViewContent)
			Log.v("ListViewContent", i);*/
    }

    private void login() {
        Log.v("zone", "login");
        //System.out.println(Setting.getUserName(context));
        //System.out.println(Setting.getPassWord(context));
        /*		Connection.Response res;
		try {
			res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
					.data("username", Setting.getUserName(context))
					.data("password", Setting.getPassWord(context))
					.method(Method.POST).timeout(8000)
					.execute();
			sessionId = res.cookies();
		} catch (IOException e) {
			publishProgress(-1);
			//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
        publishProgress("log in");

        HttpParams httpParameters = new BasicHttpParams();
        final int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        final int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httpost = new HttpPost("http://my.dek-d.com/dekdee/my.id_station/login.php");
        HttpResponse response = null;
        HttpEntity entity = null;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", Setting.getUserName(context)));
        nvps.add(new BasicNameValuePair("password", Setting.getPassWord(context)));
        System.out.println("connect");
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpclient.execute(httpost);
            entity = response.getEntity();

            //System.out.println("Login form get: " + response.getStatusLine());
            if (entity != null) {
                entity.consumeContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //System.out.println("Post logon cookies:");
        cookies = httpclient.getCookieStore().getCookies();
        httpclient.getConnectionManager().shutdown();

        if (cookies.isEmpty()) {
            System.out.println("None");
            return;
        } else {
            //System.out.print("size");
            //System.out.println(cookies.size());

            for (Cookie cooky : cookies) {
                //System.out.println("- " + cookies.get(i).toString());
                final String temp = cooky.toString().substring(cooky.toString().indexOf("name: ") + 6);
                //System.out.println(temp.substring(0, temp.indexOf("]")));
                final String temp2 = temp.substring(temp.indexOf("value: ") + 7);
                //System.out.println(temp2.substring(0, temp2.indexOf("]")));
                if (temp.contains("][") && temp2.contains("]"))
                    //		System.out.println(temp.substring(0, temp.indexOf("][")));
                    sessionId.put(temp.substring(0, temp.indexOf("][")), temp2.substring(0, temp2.indexOf("]")));
            }
        }
        //Date fav = new Date();
        //System.out.println("aff date");
        if ((cookies.size() > 1) && ((cookies.get(2).getExpiryDate()) != null) && (sessionId.size() != 0)) {
            //System.out.println("putString");
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            int i = 1;
            editor.putString("usercookie", Setting.getUserName(context));
            //System.out.println("putString for ");

            for (String key : sessionId.keySet()) {
                //System.out.println("key");
                editor.putString("keycookies" + Integer.toString(i), key);
                editor.putString("valuecookies" + Integer.toString(i), sessionId.get(key));
                //	System.out.println(i);
                i++;
            }
            editor.putLong("timecookies", cookies.get(2).getExpiryDate().getTime());
            editor.commit();
            loginsuscess = true;
        } else {
            System.out.println("Username หรือ Password ไม่ถูกต้อง");
            publishProgress("Username หรือ Password ไม่ถูกต้อง");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            loginsuscess = false;
        }
    }
}
