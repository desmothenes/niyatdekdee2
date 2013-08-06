package com.niyatdekdee.notfy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class InsertForm extends Activity {
    private DatabaseAdapter db;
    private Button saveButton;
    private TextView txtName;
    private TextView txtChapter;
    private String url;
    private String name;
    private String title;
    private String chapter;
    private Intent intent;
    private boolean Isgetchapter;
    private boolean Isgetname = false;
    private ProgressDialog dialog;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.inset_form);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (customTitleSupported) {

            //ตั้งค่า custom titlebar จาก custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);

            //เชื่อม btnSearch btnDirection เข้ากับ View
            TextView titleView = (TextView) findViewById(R.id.textViewOk);
            titleView.setText(" เพิ่มนิยาย");
            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.okbar);
            ImageButton btnOk = (ImageButton) findViewById(R.id.imageButton1);
            switch (Integer.parseInt(Setting.getColorSelectSetting(getApplicationContext()))) {
                case 0:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar);
                    break;
                case 1:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    break;
                case 2:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
                    break;
                case 3:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    break;
                case 4:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    break;
                case 5:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
                    break;
                case 6:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_siver);
                    break;
                case 7:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_glay);
                    break;
                case 8:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_orange);
                    break;
            }

            btnOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    add();
                }
            });

            ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        db = new DatabaseAdapter(getApplicationContext());
        saveButton = (Button) findViewById(R.id.button3);
        txtName = (TextView) findViewById(R.id.LongReadText);
        txtChapter = (TextView) findViewById(R.id.editText3);
        intent = getIntent();
        if (intent.getStringExtra("name") != null) {
            txtName.setText(intent.getStringExtra("name"));
            url = (intent.getStringExtra("url"));
            if (intent.getStringExtra("chapter") != null) {
                chapter = intent.getStringExtra("chapter");
                txtChapter.setText(chapter);
                title = intent.getStringExtra("title");
                Isgetchapter = false;
                Isgetname = false;
                dialog = new ProgressDialog(InsertForm.this);
                dialog.setMessage("Loading Review\nถ้าไม่ต้องการ กด back แล้วเพิ่มได้เลย");
                //dialog.setMax(100);
                Insert_doback dob = new Insert_doback();
                dob.execute();
            } else {
                Log.v("ma url", intent.getStringExtra("url"));
                Isgetchapter = true;
                title = "non";
                dialog = new ProgressDialog(InsertForm.this);
                dialog.setMessage("Loading\nถ้าค้างนานกว่า 20 วินาที ลองกดออกแล้วเพิ่มใหม่");
                dialog.setMax(100);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                Insert_doback dob = new Insert_doback();
                dob.execute();
            }
        } else if (intent.getBooleanExtra("fromMsearch", false)) {
            url = (intent.getStringExtra("url"));
            Log.v("only url", intent.getStringExtra("url"));
            Isgetchapter = true;
            Isgetname = true;
            title = "non";
            dialog = new ProgressDialog(InsertForm.this);
            dialog.setMessage("Loading\nถ้าค้างนานกว่า 20 วินาที ลองกดออกแล้วเพิ่มใหม่");
            dialog.setMax(100);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            Insert_doback dob = new Insert_doback();
            dob.execute();
        }

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                add();
            }
        });

    }

    private void add() {
        // TODO Auto-generated method stub
        try {
            Integer.parseInt(txtChapter.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "ตอนที่ ไม่ได้อยู่ในรูปแบบของตัวเลข", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title == null) {
            title = "non";
        } else if (title.equals("")) {
            title = "non";
        }

        db.open();
        long id = db.insertNiyay(
                txtName.getText().toString(),
                url,
                Integer.parseInt(txtChapter.getText().toString()),
                title);
        db.close();
        if (id > 0) {
            Toast.makeText(getBaseContext(), "Insert Succeed.", Toast.LENGTH_SHORT).show();
            //Intent i = new Intent(getBaseContext(),MainActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            //startActivity(i);
        } else {
            Toast.makeText(getBaseContext(), "Insert Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

    private class Insert_doback extends AsyncTask<URL, String, String> {

        private Document doc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(URL... arg0) {
            Log.v("title", Isgetchapter ? "true" : "false");
            if (Isgetchapter)
                return review(getchapter());
            return review();
        }

        /*        protected void onProgressUpdate(Integer... progress) {
                    if (progress[0] == -1) {
                        Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                        return;
                    }
                    super.onProgressUpdate(progress);
                    dialog.setProgress(progress[0]);
                }*/
        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("-1")) {
                dialog.setMessage("การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
                Toast.makeText(getApplicationContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_SHORT).show();
                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            } else if (progress[0].equals("-2")) {
                dialog.setMessage("ไม่สามารถค้นหาตอนล่าสุดได้ โปรดใส่ด้วยตัวเอง");
                //Toast.makeText(getApplicationContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_SHORT).show();
                Log.e("onProgressUpdate", "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่");
            } else {
                try {
                    int temp = Integer.parseInt(progress[0]);
                    dialog.setProgress(temp);
                } catch (NumberFormatException e) {
                    dialog.setMessage(progress[0]);
                }
            }
        }

        protected void onPostExecute(String result) {
            try {
                txtChapter.setText(chapter);
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                if (Isgetname) {
                    txtName.setText(name);
                }
                TextView textreview = (TextView) findViewById(R.id.textreview);
                //textreview.setMovementMethod(new ScrollingMovementMethod());
                textreview.setText(Html.fromHtml(result));
            } catch (Exception e) {
                // nothing
            }
        }

        private boolean getchapter() {
            Log.v("ti url", url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id="));
            // TODO Auto-generated method stub
            //String title = null;
            publishProgress("10");
            doc = null;
            try {
                doc = Jsoup.connect(url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id=")).userAgent("Mozilla").timeout(15000).post();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            publishProgress("30");
            //String doc = Jsoup.parse(is, "UTF-8", url);
            //DefaultHttpClient httpclient = null;
            if (doc == null) {
                publishProgress("-2");
                Log.v("txtChapter", "can't find chapter please fill by yourself");
                chapter = "หาตอนล่าสุดไม่ได้โปรดใส่ด้วยตัวเอง";
                return false;
            }

            if (Isgetname) {
                title = doc.title();


                if (title.contains(">") && title.indexOf(">") > 7)
                    title = title.substring(6, title.indexOf(">"));
                else if (title.length() > 7)
                    title = title.substring(6);
                name = title;
            }

            //final String html = doc.html();
            /*			try {
                URL link = new URL(url.replace("&chapter=", "").replace("http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=", "http://writer.dek-d.com/story/writer/view.php?id="));
				URLConnection connection = link.openConnection();
				connection.connect();
				//connection.setConnectTimeout(8000);
				//connection.setReadTimeout(16000);
				float fileLength = 163622;
				//Log.v("fileLength", Float.toString(fileLength));
				InputStream input = new BufferedInputStream(link.openStream());
				//java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
				//html = s.hasNext() ? s.next() : "";
				byte[] buffer = new byte[1024];
				StringBuilder out = new StringBuilder();
				try {
					int  count;
					int total = 0;
					while ((count = input.read(buffer)) != -1) {
						total += count;
						//Log.v("count", Integer.toString(count));
						//Log.v("total", Integer.toString(total));
						publishProgress((int) (total * 50.0 / fileLength *0.6));
						//Log.v("inner", Float.toString(50 + (int)(total * 50.0 / fileLength)));
						out.append(new String(buffer, "tis-620"));
					}
				}
				finally {
					input.close();
				}
				html = out.toString();
				//			HttpGet httpget = new HttpGet(new URI(url));
				//HttpParams params = new BasicHttpParams();
				//HttpConnectionParams.setConnectionTimeout(params, 8000);
				//HttpConnectionParams.setSoTimeout(params, 8000);
				//httpclient = new DefaultHttpClient(params);
				//ResponseHandler<String> responseHandler = new BasicResponseHandler();
				//html = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} finally {
				//httpclient.getConnectionManager().shutdown();
			}*/
            //publishProgress(40);
            /*			try {
                ContextWrapper cw = new ContextWrapper(InsertForm.this);
				File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),"insert.html");
				BufferedWriter bw;
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp),"tis620"));
				bw.write(html);
				bw.flush();
				bw.close();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
            publishProgress("50");
/*

            final int start = html.lastIndexOf("<tr>\n          <td align=\"middle\">");
            final int end = html.lastIndexOf("</td>\n          <td><a target=\"_blank\"");
            //Log.v("html", html);

            Log.v("start", Integer.toString(start));
            Log.v("end", Integer.toString(end));


            if (start == -1 || end == -1) {
                final int op = html.lastIndexOf("</td></tr><tr><td align=middle>");
                final int ed = html.indexOf("</td>", op + "</td></tr><tr><td align=middle>".length());
                Log.v("op", Integer.toString(op));
                Log.v("ed", Integer.toString(ed));
                if (op == -1 || ed == -1) {
                    Log.v("txtChapter", "can't find chapter please fill by yourself");
                    chapter =  "หาตอนล่าสุดไม่ได้โปรดใส่ด้วยตัวเอง";
                    return false;
                } else {
                    chapter = html.substring(op + "</td></tr><tr><td align=middle>".length(), ed);
                }
            } else {
                chapter = html.substring(start + "</td>\n</tr><tr><td align=\"middle\">".length(), end);
            }
*/

            Elements cplist = doc.select("td[align=middle]");
            Collections.reverse(cplist);
            for (Element i : cplist) {
                try {
                    int cp = Integer.parseInt(i.text());
                    chapter = Integer.toString(cp);
                    publishProgress("70");
                    Log.v("url", url + chapter);
                    return true;
                } catch (NumberFormatException ee) {
                    //continue;
                }
            }

            publishProgress("70");
            Log.v("txtChapter", "can't find chapter please fill by yourself");
            chapter = "หาตอนล่าสุดไม่ได้โปรดใส่ด้วยตัวเอง";
            return false;
            //HttpClient httpclient1 = new DefaultHttpClient();
            /*
            try {
				//HttpGet httpget = new HttpGet(new URI(url+chapter));
				//ResponseHandler<String> responseHandler = new BasicResponseHandler();
				//html = httpclient1.execute(httpget, responseHandler);
				URL link = new URL(url+chapter);
				URLConnection connection = link.openConnection();
				connection.connect();
				//connection.setConnectTimeout(8000);
				//connection.setReadTimeout(16000);
				float fileLength = 147797;
				Log.v("fileLength", Float.toString(fileLength));
				InputStream input = new BufferedInputStream(link.openStream());
				//java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
				//html = s.hasNext() ? s.next() : "";
				byte[] buffer = new byte[1024];
				StringBuilder out = new StringBuilder();
				try {
					int  count;
					int total = 50;
					while ((count = input.read(buffer)) != -1) {
						total += count;
						//Log.v("count", Integer.toString(count));
						//Log.v("total", Integer.toString(total));
						publishProgress(40 + (int)(total * 50.0 / fileLength*0.6));
						//Log.v("inner", Float.toString(50 + (int)(total * 50.0 / fileLength)));
						out.append(new String(buffer, "tis-620"));
					}
				}
				finally {
					input.close();
				}
				html = out.toString();
			} catch (ClientProtocolException e) {
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} finally {
				//httpclient1.getConnectionManager().shutdown();
			}

			final int tstart;
			if ((tstart = html.indexOf("<title>")) != -1) {
				title = html.substring(tstart+7, html.indexOf("</title>"));
				if (title.indexOf(">") != -1)
					title = Jsoup.parse((title.substring(title.indexOf(">")+2))).text();}
			if (title == null)
				title = "null";
			Log.v("title", "end");
			return title;*/
        }

        private String review() {
            publishProgress("80");
            Document doc = null;
            System.out.println(Isgetchapter);

            try {
                doc = Jsoup.connect("http://writer.dek-d.com/dek-d/writer/view.php?id" + url.substring(url.indexOf("="), url.indexOf("&"))).timeout(5000).get();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                publishProgress("-1");
                //Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            publishProgress("92");
            ArrayList<String> detail = new ArrayList<String>();
            ArrayList<String> header = new ArrayList<String>();
            ArrayList<String> star = new ArrayList<String>();
            if (doc != null) {
                Elements link1 = doc.select(".f-s-grd");
                if (link1 == null) return "";
                for (Element link : link1) {
                    //if (++c != 2) continue;
                    String stext = link.text();
                    int index;
                    if ((index = stext.indexOf("<<")) != -1)
                        detail.add(stext.substring(0, index));
                }
                publishProgress("94");
                link1 = doc.select("td[width=314]");
                for (Element link : link1) {
                    //if (++c != 2) continue;
                    String stext = link.text();
                    //if (stext.contains("<<"))
                    header.add(stext);
                }
                publishProgress("96");
                link1 = doc.select(".curr");
                for (Element link : link1) {
                    //if (++c != 2) continue;
                    String stext = link.attr("title");
                    //if (stext.contains("<<"))
                    star.add(stext);
                }
                StringBuilder review = new StringBuilder("<h3>Top 3 Review</h3>");
                publishProgress("98");
                for (int i = 0; i < detail.size(); i++) {
                    review.append(String.format("<br/><p><font color=#33B6EA>%s</font><br /><font color=#cc0029>%s</font><br /><font color=#339900>ให้ %s ดาว</font></p>", header.get(i), detail.get(i), star.get(i)));
                }
                return review.toString();
            } else return "";
        }

        private String review(boolean input) {
            publishProgress("81");
            //System.out.println(Isgetchapter);
            //ContextWrapper cw = new ContextWrapper(InsertForm.this);
            //File temp =  new File(cw.getDir("temp", Context.MODE_PRIVATE),"insert.html");
            //doc = Jsoup.parse(temp, "tis620");
            if (!input) return "";
            publishProgress("91");
            ArrayList<String> detail = new ArrayList<String>();
            ArrayList<String> header = new ArrayList<String>();
            ArrayList<String> star = new ArrayList<String>();
            Elements link1 = doc.select(".f-s-grd");
            for (Element link : link1) {
                //if (++c != 2) continue;
                String stext = link.text();
                int index;
                if ((index = stext.indexOf("<<")) != -1)
                    detail.add(stext.substring(0, index));
            }
            publishProgress("94");
            link1 = doc.select("td[width=314]");
            for (Element link : link1) {
                //if (++c != 2) continue;
                String stext = link.text();
                //if (stext.contains("<<"))
                header.add(stext);
            }
            publishProgress("96");
            link1 = doc.select(".curr");
            for (Element link : link1) {
                //if (++c != 2) continue;
                String stext = link.attr("title");
                //if (stext.contains("<<"))
                star.add(stext);
            }
            StringBuilder review = new StringBuilder("<h3>Top 3 Review</h3>");
            publishProgress("98");
            for (int i = 0; i < detail.size(); i++) {
                review.append(String.format("<br/><p><font color=#33B6EA>%s</font><br /><font color=#cc0029>%s</font><br /><font color=#339900>ให้ %s ดาว</font></p>", header.get(i), detail.get(i), star.get(i)));
            }
            return review.toString();
        }
    }
}
