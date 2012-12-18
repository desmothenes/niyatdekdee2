package com.niyatdekdee.notfy;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
	//private DiskLruCache mDiskCache;
	//private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	//private static final String DISK_CACHE_SUBDIR = "thumbnails";
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_fav_add);
		if (customTitleSupported) {
			//ตั้งค่า custom titlebar จาก custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);
			//เชื่อม btnSearch btnDirection เข้ากับ View
			TextView title = (TextView) findViewById(R.id.textViewBar);
			title.setText(" เข้าสูระบบ");
			
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.nonbar);
			switch (MainActivity.titleColor) {
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
			}
			
			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}

		adapter = new InteractiveArrayAdapter(this, ListViewContent);
		adapter.notifyDataSetChanged();
		 listView = getListView();
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

				listView.setVisibility(View.VISIBLE);
				username.setVisibility(View.INVISIBLE);
				password.setVisibility(View.INVISIBLE);
				loginbtt.setVisibility(View.INVISIBLE);
			}
		});

		ListView list = getListView();
		list.setFastScrollEnabled(true);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String url = linktable.get(arg2);
				//Log.v("url", url);
				Intent i = new Intent(getBaseContext(),InsertForm.class);				
				String title = ListViewContent.get(arg2);
				//Log.v("title", title);
				if (title.contains(">"))
					title = title.substring(2+title.indexOf(">"));
				//Log.v("title", title);
				i.putExtra("name",title);
				//in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
				String stext = "id=";
				//หาหลักของตอน
				final int start = url.lastIndexOf(stext)+stext.length();
				if (start - stext.length() == -1) {
					Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
					return;
				}

				//Log.v("url.length()", Integer.toString(url.length()));
				//Log.v("start", Integer.toString(start));
				//Log.v("stext.length()", Integer.toString(stext.length()));
				int len=0;
				//Log.v("Character", Character.toString(url.charAt(start)));
				for (int i3 = start;i3 < url.length() && Character.isDigit(url.charAt(i3));i3++) {						
					len++;
					//Log.v("Character", Character.toString(url.charAt(i3)));
				}
				//Log.v("len", Integer.toString(len));
				String unum = url.substring(start,start+len);	
				//Log.v("unum", unum);
				url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter=";					

				//Log.v("url", url);
				i.putExtra("url",url);
				startActivity(i);
			}
		});        
	}

	private Map<String, String> login() {
		Log.v("zone","login");
		Connection.Response res;
		try {
			res = Jsoup.connect("http://my.dek-d.com/dekdee/my.id_station/login.php")
					.data("username", username.getText().toString())
					.data("password", password.getText().toString())
					.method(Method.POST).timeout(8000)
					.execute();
			return res.cookies();
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;    		
	}    

	private void loadFav(Map<String, String> sessionId) {
		Log.v("zone","loadFav");
		Document doc = null;
		try {
			doc = Jsoup.connect("http://my.dek-d.com/desmos/control/writer_favorite.php")
					.cookies(sessionId).timeout(3000)
					.get();
		} catch (IOException e) {
			Toast.makeText(getBaseContext(), "การเชื่อมต่อมีปัญหา กรุณาปรับปรุงการเชื่อมต่อ แล้วลองใหม่", Toast.LENGTH_LONG).show();

			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
		Elements link1 = doc.select(".link1[href~=/dek-d/writer/view.php]");
		if(link1 == null) return;
		for (Element link:link1) {
			String stext = link.text();
			linktable.add(link.attr("href"));
			ListViewContent.add(stext);	
			//Log.v("stext", stext);
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
				if (ListViewContent.size() == 0) {
					AlertDialog alertDialog = new AlertDialog.Builder(Fav_add.this).create();
					alertDialog.setTitle("ไม่พบข้อมูล");
					alertDialog.setMessage("ถ้ามีรายการอยู่ ลองตรวจสอบการเชื่อมต่ออินเตอร์เน็ต แล้วลองใหม่");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							listView.setVisibility(View.INVISIBLE);
							username.setVisibility(View.VISIBLE);
							password.setVisibility(View.VISIBLE);
							loginbtt.setVisibility(View.VISIBLE);
						}
					});
					alertDialog.show();
				}
				setListAdapter(adapter);
				TextView title = (TextView) findViewById(R.id.textViewBar);
				title.setText(" เลือกรายการที่จะเพิ่ม");
				dialog.dismiss();
				for (String i : ListViewContent)
					Log.v("ListViewContent", i);
				for (String i : linktable)
					Log.v("linktable", i);
				for (String i : imagetable)
					Log.v("imagetable", i);
				for (String i : detailtable)
					Log.v("detailtable", i);
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

		private LruCache<String, Bitmap> mMemoryCache;

		public InteractiveArrayAdapter(Activity context, ArrayList<String> names) {
			super(context, R.layout.list_item, names);
			this.context = context;
			this.names = names;
			final int memClass = ((ActivityManager) context.getSystemService(
					Context.ACTIVITY_SERVICE)).getMemoryClass();

			// Use 1/8th of the available memory for this memory cache.
			final int cacheSize = 1024 * 1024 * memClass / 8;
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in bytes rather than number of items.
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};    
/*
			try {
				for (String imgurl : imagetable) {
					URL newurl = new URL(imgurl);
					URLConnection connection;
					connection = newurl.openConnection();
					connection.setUseCaches(true);
					Bitmap mIcon_val = BitmapFactory.decodeStream(connection.getInputStream()); 
					addBitmapToMemoryCache(imgurl,mIcon_val);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
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

			//System.out.println(names.get(position));
			//System.out.println(detailtable.get(position));
			//System.out.println(imagetable.get(position));
			text.setText((names.get(position)!= null)? names.get(position) : "1");
			detail.setText((detailtable.get(position)!= null)? detailtable.get(position) : "2");	
			//thumb_image.setImageResource(R.drawable.rihanna);
			final Bitmap bitmap = getBitmapFromMemCache(imagetable.get(position));
			if (bitmap != null) {
				thumb_image.setImageBitmap(bitmap);
				thumb_image.setScaleType(ScaleType.CENTER_CROP);
			} else {
				BitmapWorkerTask task = new BitmapWorkerTask(thumb_image);
				task.execute(imagetable.get(position));
			}

			//thumb_image.setImageBitmap(imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image));
			//imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image);
			return rowView;
		}

		public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
			if (getBitmapFromMemCache(key) == null) {
				mMemoryCache.put(key, bitmap);
			}
		}

		public Bitmap getBitmapFromMemCache(String key) {
			return mMemoryCache.get(key);
		}

		class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>  {

			private WeakReference<ImageView> imageViewReference;

			public BitmapWorkerTask(ImageView thumb_image) {
				// TODO Auto-generated constructor stub
				imageViewReference = new WeakReference<ImageView>(thumb_image);
			}

			@Override
			protected Bitmap doInBackground(String... arg0) {
				try {
					URL newurl = new URL(arg0[0]);
					URLConnection connection = newurl.openConnection();
					connection.setUseCaches(true);
					Bitmap mIcon_val = BitmapFactory.decodeStream(connection.getInputStream()); 
					addBitmapToMemoryCache(arg0[0],mIcon_val);
					return mIcon_val;
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Bitmap result) 
			{
				try
				{
					imageViewReference.get().setImageBitmap(result);
					imageViewReference.get().setScaleType(ScaleType.CENTER_CROP);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					dialog.dismiss();
				}
			}

		}
	}

	/*
	class BitmapWorkerTask extends AsyncTask {
		@Override
		protected Object doInBackground(Object... params) {
			final String imageKey = String.valueOf(params[0]);

			// Check disk cache in background thread
			Bitmap bitmap = getBitmapFromDiskCache(imageKey);

			if (bitmap == null) { // Not found in disk cache
				// Process as normal
				final Bitmap bitmap = decodeSampledBitmapFromResource(
						getResources(), params[0], 100, 100));
			}

			// Add final bitmap to caches
			addBitmapToCache(String.valueOf(imageKey, bitmap);

			return bitmap;
		}
	}

	public void addBitmapToCache(String key, Bitmap bitmap) {
		// Add to memory cache as before
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}

		// Also add to disk cache
		if (!mDiskCache.containsKey(key)) {
			mDiskCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromDiskCache(String key) {
		return mDiskCache.get(key);
	}

	// Creates a unique subdirectory of the designated app cache directory. Tries to use external
	// but if not mounted, falls back on internal storage.
	public static File getCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
				|| !Environment.isExternalStorageRemovable() ?
						context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

						return new File(cachePath + File.separator + uniqueName);
	}*/
}
