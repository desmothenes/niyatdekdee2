package com.niyatdekdee.notfy;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
	private static final ArrayList<String[]> niyayTable = new ArrayList<String[]> ();
	static ListViewAdapter listAdap;
	static ListView myList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getBaseContext();
		db = new DatabaseAdapter(this);        
		listAdap = new ListViewAdapter(this);
		listAdap.notifyDataSetChanged();
		myList=(ListView)findViewById(android.R.id.list); 	
		myList.setScrollingCacheEnabled(false);
		myList.setAdapter(listAdap);
		myList.setItemsCanFocus(true);
		registerForContextMenu(myList);
		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String item = ((TextView)view).getText().toString();

				Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

			}
		});		
		if (isOnline())	{showAllBook();}
		else {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
			alertDialog.setTitle("Error.");
			alertDialog.setMessage("Not connect to internet.\nPlease check your internet connection");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// here you can add functions
				}
			});

			alertDialog.show();
			showAllBookOffline() ;
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		String pen = o.toString();
		Toast.makeText(this, "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();
	}

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
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final int listItemName = (int)info.id;	
		boolean flag = false;
		int i_index = -1;		
		for(String[] i:niyayTable) {
			i_index++;
			int j_index = -1;
			for (String j:i) {
				j_index++;
				Log.e("table "+Integer.toString(i_index)+","+Integer.toString(j_index), (j != null) ? j : "");
			}
		}

		switch(item.getItemId()) {
		case R.id.red:
			db.open();
			Log.e("replace with", niyayTable.get(listItemName)[4]);
			flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
					niyayTable.get(listItemName)[4]);			
			if (flag) {
				Toast.makeText(context, "inc succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "inc failed", Toast.LENGTH_SHORT).show();
			}
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			reload();		
			return true;			
		case R.id.edit:
			Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
			return true;	
		case R.id.addcp:
			Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
			db.open();
			flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
					Integer.parseInt(niyayTable.get(listItemName)[3])+1,
					"");			
			if (flag) {
				Toast.makeText(context, "inc succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "inc failed", Toast.LENGTH_SHORT).show();
			}
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			reload();		
			return true;
		case R.id.dec:
			Toast.makeText(context, "dec ", Toast.LENGTH_SHORT).show();
			db.open();
			flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
					Integer.parseInt(niyayTable.get(listItemName)[3])-1,
					"");			
			if (flag) {
				Toast.makeText(context, "dec succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "dec failed", Toast.LENGTH_SHORT).show();
			}
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			reload();	
			return true;	
		case R.id.delete:			
			Toast.makeText(context, "del ", Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
			builder.setIcon(R.drawable.delete);
			builder.setMessage("Are you sure you want to delete?") 
			.setCancelable(false) 						 
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int id) { 
					db.open();
					boolean flag;
					flag = db.deleteNiyay(Long.parseLong(niyayTable.get(listItemName)[0]));		
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
			return true;	
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_data, menu);
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
		db = new DatabaseAdapter(context);
		db.open();

		Cursor c = db.getAllNiyay();
		final int i = c.getCount();
		Log.e("floop", Integer.toString(i));
		if (i == 0) {
			Log.e("ck db", "not ok");
			db.close();
			return;
		}
		else {
			Log.e("ok ?", "ok");
		}
		int i2 = 0;
		c.moveToFirst();

		do {		
			i2++;
			String[] temp  = new String[5];
			temp[0] = c.getString(0);
			temp[1] = c.getString(1);
			temp[2] = c.getString(2);
			temp[3] = c.getString(3);
			temp[4] = displayBook(c);
			niyayTable.add(temp);
			//myList.
		}while(c.moveToNext());

		Log.e("loop end", Integer.toString(i2));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}

	private static void showAllBookOffline() {
		// TODO Auto-generated method stub
		ListViewContent.clear();
		db = new DatabaseAdapter(context);
		db.open();

		Cursor c = db.getAllNiyay();
		int i = c.getCount();
		Log.e("floop", Integer.toString(i++));
		if (c.requery() && i==0) {
			Log.e("ck db", "not ok");
			db.close();
			return;
		}
		else {
			Log.e("ok ?", "ok");
		}

		i = 0;
		c.moveToFirst();

		do {		
			i++;
			String[] temp  = new String[5];
			temp[0] = c.getString(0);
			temp[1] = c.getString(1);
			temp[2] = c.getString(2);
			temp[3] = c.getString(3);
			temp[4] = c.getString(4);
			niyayTable.add(temp);
			displayBookOffline(c);
		}while(c.moveToNext());
		Log.e("loop end", Integer.toString(i));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}

	public static String displayBook(Cursor c) {

		int status = 0;
		String title = c.getString(4);
		final String url = c.getString(2); //url
		String text2 = "";
		String text1 = "";
		String doc = "";
		//if (title.contains(">")) title = title.substring(title.indexOf(">"));

		try {
			text1 = Jsoup.connect(url+c.getString(3)).timeout(0).get().select("title").first().text();				
			doc = Jsoup.connect(url+Integer.toString((Integer.parseInt(c.getString(3))+1))).timeout(0).get().html();
			Log.e("doc url", url+Integer.toString((Integer.parseInt(c.getString(3)+1))));
			//temp code
			if (title == null ) title = "";
			else if (text1.contains(">"))	text1 = text1.substring(text1.indexOf(">")+2);
			if (title.isEmpty()) {
				title = text1;
			}
			else if (text1.compareTo(title) == -1) {
				status = 1; //current chapter update				
			} 
			else if (/*(text2.compareTo("ไม่พบตอนที่คุณค้นหา") != 0) ||*/ doc.contains("<title>"))  {
				status = 2; //new chapter update
				text2 = doc.substring(doc.indexOf("<title>")+7, doc.indexOf("</title>"));
				Log.e("text2", text2);

			}
			Log.e("status", Integer.toString(status));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(context, "update cheaker error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		if (title != null ) 
			if (title.contains(">")) title = title.substring(title.indexOf(">")+2);
		String text3 = "";
		if (status == 0) {			
			ListViewContent.add(
					"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>"); 
		}
		else if (status == 1) {
			ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ตอน : " +text1+" ("+c.getString(3)+")</font></p>"); 
		}
		else if (status == 2) {
			Log.e("text2 old",text2);
			text3 = Jsoup.parse(text2).text();
			if (text3.contains(">"))  text3 = text3.substring(text3.indexOf(">")+2);			
			//Log.e("text2 new",text2);
			Log.e("text3 new",text3);
			ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนใหม่</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029>ปัจจุบัน ตอน : " +title+" ("+c.getString(3)+")</font><br />"+
							"<font color=#555>ตอนใหม่ : " +text3+" ("+Integer.toString(Integer.parseInt(c.getString(3))+1)+")</font></p>");	
		}
		Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));
		if  (status == 0) return title;
		else if  (status == 1) return text1;
		else if  (status == 2) return text3;
		return "";
	}

	public static void displayBookOffline(Cursor c) {
		//Log.e("title0",(c.getString(4) != null) ? c.getString(4):"");
		String title = c.getString(4);	

		//if (title.contains(">")) title = title.substring(title.indexOf(">"));
		Log.e("title",(title != null) ? title:" ");

		if (title != null) 
			if ( title.contains(">")) 
				title = title.substring(title.indexOf(">")+2);

		ListViewContent.add(
				"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
						"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+c.getString(3)+")</font></p>");
		Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent i = new Intent(getBaseContext(),add_web.class);
			startActivity(i);
			return true;
		case R.id.show:
			reload();
			return true;
		case R.id.menu_settings:
			//Intent i = new Intent(getBaseContext(),setting.class);
			//startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
				/*			arg1.setOnClickListener(new OnClickListener() { 
					@Override 
					public void onClick(View v) { 
						int i_index = -1;
						int j_index = 0;
						//String index = "";
						//int loc = myList.getId();
						for(String[] i:niyayTable) {
							i_index++;
							//j_index = -1;
							Log.e("table "+i_index,i[1]);
							//for (String j:i) {
								//j_index++;
								//Log.e("table "+Integer.toString(i_index)+","+Integer.toString(j_index), j);
								//if (j_index == 4) if (index.contains(j)) loc = i_index;
							//}
						}
						//Log.e("view",index );
						Log.e("arg0", Integer.toString(arg0));
						//Log.e("table size",Integer.toString(i_index)+","+Integer.toString(j_index));

						String url = niyayTable.get(loc)[2]+niyayTable.get(loc)[3];
						Log.e("url", url);
						if (!url.startsWith("http://") && !url.startsWith("https://"))
							url = "http://" + url;
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(browserIntent);
					} 
				}); 
				arg1.setOnCreateContextMenuListener(null);
				holder.text = (TextView) arg1.findViewById(R.id.textView1);
				holder.delete =  (Button) arg1.findViewById(R.id.button1);
				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.showContextMenu();
					}
				});*/
				holder.text = (TextView) arg1.findViewById(R.id.textView1);
				holder.delete =  (Button) arg1.findViewById(R.id.button1);
				arg1.setTag(holder);
			}else {
				holder = (ListContent) arg1.getTag();
			}	
			holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
			return arg1;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			//_NotifyOnChange = true;
		}

		@SuppressWarnings("unused")
		public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
			// empty implementation
		}

		class ListContent {
			TextView text;
			Button delete;
		}
	}

}
