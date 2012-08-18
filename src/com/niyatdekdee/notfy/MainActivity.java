package com.niyatdekdee.notfy;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	static DatabaseAdapter db;
	public static Context context;

	private static final ArrayList<String> ListViewContent = new ArrayList<String> ();
	private static final ArrayList<Integer> ListID = new ArrayList<Integer> ();
	ListViewAdapter listAdap;
	Button addButton;
	Button refreshButton;
	TextView txtview1;

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setMessage("Are you sure you want to exit?") 
		.setCancelable(false) 
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				finish();
				System.exit(0);
			} 
		}) 
		.setNegativeButton("No", new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) { 
				dialog.cancel(); 
			} 
		}); 
		AlertDialog alert = builder.create(); 
		alert.show(); 
	}


	public boolean isOnline() { 
		ConnectivityManager cm = 
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 

		return cm.getActiveNetworkInfo() != null &&  
				cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
	} 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addButton = (Button) findViewById(R.id.button1);
		refreshButton = (Button) findViewById(R.id.button2);
		txtview1 = (TextView) findViewById(R.id.textView1);
		context = getBaseContext();
		db = new DatabaseAdapter(this);        
		listAdap = new ListViewAdapter(this);
		listAdap.notifyDataSetChanged();
		ListView myList=(ListView)findViewById(android.R.id.list); 		 
		myList.setAdapter(listAdap);
		myList.setItemsCanFocus(true);
		myList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "touched1", Toast.LENGTH_SHORT).show();
			}

		});



		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getBaseContext(),add_web.class);
				startActivity(i);
			}        	
		});
		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAllBookOffline();				
			}

		});

		//		ListView lv = getListView(); 
		//
		//		// listening to single list item on click 
		//		lv.setOnItemClickListener(new OnItemClickListener() { 
		//			public void onItemClick(AdapterView<?> parent, View view,int position, long id) { 
		//	             Intent i = new Intent(getApplicationContext(), InsertForm.class); 
		//	              // sending data to new activity 
		//	              startActivity(i); 
		//			} 
		//		}); 
		if (isOnline())	{showAllBookOffline();}
		else {Toast.makeText(context, "Not connect to internet.\nPlease check your internet connection", Toast.LENGTH_LONG).show();
		showAllBookOffline() ;
		}
	}

	public void reload() {
		Intent intent = getIntent(); //new Intent(getBaseContext(),InsertForm.class);	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish(); 
		startActivity(intent); 
	}


	private static void showAllBook() {
		// TODO Auto-generated method stub
		ListViewContent.clear();
		ListID.clear();
		db = new DatabaseAdapter(context);
		db.open();

		Cursor c = db.getAllNiyay();
		c.requery();
		int i = c.getCount();
		Log.v("floop", Integer.toString(i++));
		i = 0;
		c.moveToFirst();
		do {		
			i++;
			displayBook(c);
		}while(c.moveToNext());
		Log.v("loop end", Integer.toString(i));
		db.close();
		Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}
	private static void showAllBookOffline() {
		// TODO Auto-generated method stub
		ListViewContent.clear();
		ListID.clear();
		db = new DatabaseAdapter(context);
		db.open();

		Cursor c = db.getAllNiyay();
		c.requery();
		int i = c.getCount();
		Log.v("floop", Integer.toString(i++));
		i = 0;
		c.moveToFirst();
		do {		
			i++;
			displayBookOffline(c);
		}while(c.moveToNext());
		Log.v("loop end", Integer.toString(i));
		db.close();
		Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}
	public static void displayBook(Cursor c) {

		int status = 0;
		String title = c.getString(4);
		String url = c.getString(2); //url
		String text2 = null;

		//if (title.contains(">")) title = title.substring(title.indexOf(">"));

		try {
			Document doc = Jsoup.connect(url+c.getString(3)).get();
			Element link1 = doc.select("title").first();
			String text1 = link1.text();
			Log.v("title", title);
			//if (text1 != "" && text1.contains(">"))	text1 = text1.substring(text1.indexOf(">"));
			Log.v("text1", text1);
			doc = Jsoup.connect(url+Integer.toString((Integer.parseInt(c.getString(3)+1)))).get();

			text2 = doc.body().text();
			if (text1.compareTo(title) != 0) {
				status = 1; //current chapter update
			} 
			else if (/*(text2.compareTo("ไม่พบตอนที่คุณค้นหา") != 0) ||*/ (!doc.select("title").isEmpty()) ) {
				status = 2; //new chapter update
				text2 = doc.select("title").first().text();
				Log.v("text2", text2);
			}
			Log.v("status", Integer.toString(status));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "update cheaker error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}


		if (title.contains(">")) title = title.substring(title.indexOf(">")+2);

		if (text2.contains(">")) text2 = text2.substring(text2.indexOf(">")+2);	

		if (status == 0)
			ListViewContent.add(
					"<p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		else if (status == 1)
			ListViewContent.add(
					"<p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		else if (status == 2)
			ListViewContent.add(
					"<p><font color=#339900>มีการอัพเดตตอนใหม่</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029>ปัจจุบัน ตอน : " +title+" ("+c.getString(3)+")</font><br />"+
							"<font color=#cc0029>ล่าสุด ตอน : " +text2+" ("+Integer.toString(Integer.parseInt(c.getString(3))+1)+")</font></p>");	

		Log.v("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));
		ListID.add(Integer.parseInt(c.getString(0)));
	}


	public static void displayBookOffline(Cursor c) {

		String title = c.getString(4);

		//if (title.contains(">")) title = title.substring(title.indexOf(">"));

		if (title.contains(">")) title = title.substring(title.indexOf(">")+2);



		ListViewContent.add(
				"<p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
						"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		Log.v("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));
		ListID.add(Integer.parseInt(c.getString(0)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private class ListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public ListViewAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ListViewContent.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub.
			ListContent holder = new ListContent();
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.list_item, null);
				arg1.setClickable(true); 
				arg1.setFocusable(true); 
				arg1.setBackgroundResource(android.R.drawable.menuitem_background); 
				arg1.setOnClickListener(new OnClickListener() { 
					@Override 
					public void onClick(View v) { 
						Toast.makeText(context, "touched2", Toast.LENGTH_SHORT).show();
					} 

				}); 
				
				holder.text = (TextView) arg1.findViewById(R.id.textView1);
				holder.delete =  (Button) arg1.findViewById(R.id.button1);
				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
						builder.setMessage("Are you sure you want to delete?") 
						.setCancelable(false) 
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
							public void onClick(DialogInterface dialog, int id) { 
								Log.v("delete", "delete id"+ListID.get(arg0));
								db.open();
								boolean flag = db.deleteNiyay(ListID.get(arg0));
								if (flag) {
									Toast.makeText(context, "delete succeed", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "delete failed", Toast.LENGTH_SHORT).show();
								}
								//Intent i = new Intent(context,MainActivity.class);
								db.close();
								reload();
							} 
						}) 
						.setNegativeButton("No", new DialogInterface.OnClickListener() { 
							public void onClick(DialogInterface dialog, int id) { 
								dialog.cancel(); 
							} 
						}); 
						AlertDialog alert = builder.create(); 
						alert.show(); 	
					}
				});
				arg1.setTag(holder);
			}else {
				holder = (ListContent) arg1.getTag();
			}	
			holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
			return arg1;
		}
		class ListContent {
			TextView text;
			Button delete;
		}

	}

}
