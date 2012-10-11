package com.niyatdekdee.notfy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

public class Fav_add extends ListActivity {
	private InteractiveArrayAdapter adapter;
	private static final ArrayList<String> ListViewContent = new ArrayList<String> ();
	private static final ArrayList<String> linktable = new ArrayList<String> ();
	private static final ArrayList<String> imagetable = new ArrayList<String> ();
	private static final ArrayList<String> detailtable = new ArrayList<String> ();
	Button loginbtt;
	EditText username;
	EditText password;
	protected ProgressDialog dialog ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav_add);
		adapter = new InteractiveArrayAdapter(this, ListViewContent);
		adapter.notifyDataSetChanged();
		final ListView listView = getListView();
		username = (EditText)findViewById(R.id.editText1);	
		password = (EditText)findViewById(R.id.editText2);
		loginbtt = (Button)findViewById(R.id.button1);		
		loginbtt.setOnClickListener(new OnClickListener() {         
			@Override
			public void onClick(View v) {				
				Log.v("login", "login");
				dialog = ProgressDialog.show(Fav_add.this,"Loading", "Please Wait...",true);
		        doback dob=new doback();
		        dob.execute();
				Log.v("listView", "listView");
				for (String i : ListViewContent)
					Log.v("ListViewContent", i);
				for (String i : linktable)
					Log.v("linktable", i);
				for (String i : imagetable)
					Log.v("imagetable", i);
				for (String i : detailtable)
					Log.v("detailtable", i);
				listView.setVisibility(View.VISIBLE);
				username.setVisibility(View.INVISIBLE);
				password.setVisibility(View.INVISIBLE);
				loginbtt.setVisibility(View.INVISIBLE);
			}
		});

		ListView list = getListView();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String url = linktable.get(arg2);
				Log.v("url", url);
				Intent i = new Intent(getBaseContext(),InsertForm.class);				
				String title = ListViewContent.get(arg2);
				Log.v("title", title);
				if (title.contains(">"))
					title = title.substring(2+title.indexOf(">"));
				Log.v("title", title);
				i.putExtra("name",title);
				//in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
				String stext = "id=";
				//หาหลักของตอน
				final int start = url.lastIndexOf(stext)+stext.length();
				if (start - stext.length() == -1) {
					Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
					return;
				}

				Log.v("url.length()", Integer.toString(url.length()));
				Log.v("start", Integer.toString(start));
				Log.v("stext.length()", Integer.toString(stext.length()));
				int len=0;
				Log.v("Character", Character.toString(url.charAt(start)));
				for (int i3 = start;i3 < url.length() && Character.isDigit(url.charAt(i3));i3++) {						
					len++;
					Log.v("Character", Character.toString(url.charAt(i3)));
				}
				Log.v("len", Integer.toString(len));
				String unum = url.substring(start,start+len);	
				Log.v("unum", unum);
				url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter=";					

				Log.v("url", url);
				i.putExtra("url",url);
				startActivity(i);
			}
		});        
	}
	private Map<String, String> login() {
		Connection.Response res;
		try {
			res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
					.data("username", username.getText().toString())
					.data("password", password.getText().toString())
					.method(Method.POST)
					.execute();
			return res.cookies();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;    		
	}    

	private void loadFav(Map<String, String> sessionId) {
		Document doc = null;
		try {
			doc = Jsoup.connect("http://my.dek-d.com/desmos/control/writer_favorite.php")
					.cookies(sessionId)
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
		Elements link1 = doc.select(".link1[href~=/dek-d/writer/view.php]");
		if(link1 == null) return;
		for (Element link:link1) {
			String stext = link.text();
			linktable.add(link.attr("href"));
			ListViewContent.add(stext);	
		}
    	Elements link2 = doc.select("table[width*=100]");
    	//for (int t=0;t<100;t++) {
    	int i = 0;
    	int j = 0;
    	for (Element link:link2) { 
    		if (i++<12) continue;
    		int s = link.text().indexOf("Date");
    		if (s != -1)
    			detailtable.add(link.text().substring(0,s).replace(ListViewContent.get(j++), "\b"));
    		i++;
    	}
		for (Element link:doc.select("img.mainborder")) {
			imagetable.add(link.attr("src"));
		}
		Log.v("favfin", "favfin");
	}

	class doback extends AsyncTask<URL, Integer, Long>
    {

        @Override
        protected Long doInBackground(URL... arg0) 
        {
            try
            {
            	loadFav(login());
            }
            catch(Exception e)
            {

            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) 
        {

        }
        protected void onPostExecute(Long result) 
        {
            try
            {
				setListAdapter(adapter);
                dialog.dismiss();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }
	private class InteractiveArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private ArrayList<String> names = new ArrayList<String>();

		public InteractiveArrayAdapter(Activity context, ArrayList<String> names) {
			super(context, R.layout.list_item, names);
			this.context = context;
			this.names = names;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.fav_item, null);}
			TextView text = (TextView) rowView.findViewById(R.id.textView1);
			TextView detail = (TextView) rowView.findViewById(R.id.artist);
			ImageView thumb_image = (ImageView) rowView.findViewById(R.id.list_image);


			System.out.println(names.get(position));
			System.out.println(detailtable.get(position));
			System.out.println(imagetable.get(position));
			text.setText((names.get(position)!= null)? names.get(position) : "1");
			detail.setText((detailtable.get(position)!= null)? detailtable.get(position) : "2");	
			//thumb_image.setImageResource(R.drawable.rihanna);
			try {
				URL newurl = new URL(imagetable.get(position));
				Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream()); 
				thumb_image.setImageBitmap(mIcon_val);
				thumb_image.setScaleType(ScaleType.CENTER_CROP);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//thumb_image.setImageBitmap(imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image));
			//imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image);
			return rowView;
		}

	}
}
