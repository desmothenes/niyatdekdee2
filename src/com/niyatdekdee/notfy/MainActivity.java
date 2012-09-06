package com.niyatdekdee.notfy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	static DatabaseAdapter db;
	public static Context context;
	private static final ArrayList<String> ListViewContent = new ArrayList<String> ();
	private static final ArrayList<String[]> niyayTable = new ArrayList<String[]> ();
	public static ListViewAdapter listAdap;
	static ListView myList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getBaseContext();
		db = new DatabaseAdapter(this);        
		listAdap = new ListViewAdapter(this);
		listAdap.notifyDataSetChanged();
		//myList=(ListView)findViewById(android.R.id.list); 	
		//myList.setScrollingCacheEnabled(false);
		//myList.setAdapter(listAdap);
		setListAdapter(listAdap);
		ListView myList = getListView();
		/*	    myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
			}
		});*/
		myList.setItemsCanFocus(true);
		registerForContextMenu(myList);
		/*		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String item = ((TextView)view).getText().toString();

				Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();

			}
		});		*/
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

	/*	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		String pen = o.toString();
		Toast.makeText(this, "You have chosen the pen: " + " " + pen, Toast.LENGTH_LONG).show();
	}*/

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
			Log.e("table "+Integer.toString(i_index), i[1]);			
		}
		Log.v("get with", Integer.toString(listItemName));
		Log.v("niyayTable 0",niyayTable.get(listItemName)[0]);
		Log.v("niyayTable 1",niyayTable.get(listItemName)[1]);
		Log.v("niyayTable 2",niyayTable.get(listItemName)[2]);
		Log.v("niyayTable 3",niyayTable.get(listItemName)[3]);
		Log.v("niyayTable 4",niyayTable.get(listItemName)[4]);
		Log.v("get item", Integer.toString(item.getItemId()));
		HttpClient httpclient = new DefaultHttpClient();
		switch(item.getItemId()) {
		case R.id.open:
			String url = niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3];			
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;
			Log.e("url", url);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
			return true;
		case R.id.red:
			Log.e("replace with", niyayTable.get(listItemName)[4]);
			db.open();
			if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return true;
			flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
					niyayTable.get(listItemName)[4]);			
			/*			if (flag) {
				Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
			}*/
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			//reload();	
			ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
					"<font color=#cc0029> ล่าสุด ตอน : " +niyayTable.get(listItemName)[4]+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
					);		
			listAdap.notifyDataSetChanged();
			return true;			
		case R.id.edit:
			Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(context, EditForm.class);
			intent.putExtra("id", niyayTable.get(listItemName)[0]);
			intent.putExtra("name", niyayTable.get(listItemName)[1]);
			intent.putExtra("url", niyayTable.get(listItemName)[2]);
			intent.putExtra("chapter", niyayTable.get(listItemName)[3]);
			intent.putExtra("title", niyayTable.get(listItemName)[4]);
			startActivity(intent);
			listAdap.notifyDataSetChanged();
			return true;	
		case R.id.addcp:
			niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])+1);
			String doc = "";
			try {
				HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				doc = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
			Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();
			final int start;
			if ((start=doc.indexOf("<title>")) != -1) {
				doc = doc.substring(start+7, doc.indexOf("</title>"));
				doc = Jsoup.parse((doc.substring(doc.indexOf(">")+2))).text();
			}
			else {
				doc = "non chapter";
			}
			db.open();
			flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
					Integer.parseInt(niyayTable.get(listItemName)[3]),
					"");			
			if (flag) {
				Toast.makeText(context, "inc succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "inc failed", Toast.LENGTH_SHORT).show();
				niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])-1);
			}
			niyayTable.get(listItemName)[4] = doc ;
			if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return true;
			flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
					niyayTable.get(listItemName)[4]);			
			if (flag) {
				Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
			}		
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
					"<font color=#cc0029> ล่าสุด ตอน : " +doc+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
					);		
			listAdap.notifyDataSetChanged();
			return true;
		case R.id.dec:
			niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])-1);
			doc = "";			
			try {
				HttpGet httpget = new HttpGet(new URI(niyayTable.get(listItemName)[2]+niyayTable.get(listItemName)[3]));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				doc = httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
			}

			Toast.makeText(context, "dec", Toast.LENGTH_SHORT).show();
			final int start2;
			if ((start2=doc.indexOf("<title>")) != -1) {
				doc = doc.substring(start2+7, doc.indexOf("</title>"));
				Log.e("url", doc);
				doc = Jsoup.parse((doc.substring(doc.indexOf(">")+2))).text();
			}
			else {
				doc = "non chapter";
			}
			db.open();
			flag = db.updateChapter((Long.parseLong(niyayTable.get(listItemName)[0])), 
					Integer.parseInt(niyayTable.get(listItemName)[3]),
					"");			
			if (flag) {
				Toast.makeText(context, "dec succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "dec failed", Toast.LENGTH_SHORT).show();
				niyayTable.get(listItemName)[3] = Integer.toString(Integer.parseInt(niyayTable.get(listItemName)[3])+1);
			}

			niyayTable.get(listItemName)[4] = doc;
			if (niyayTable.get(listItemName)[4] == null || niyayTable.get(listItemName)[4] == "") return true;
			flag = db.updateTitle(Long.parseLong(niyayTable.get(listItemName)[0]), 
					niyayTable.get(listItemName)[4]);			
			if (flag) {
				Toast.makeText(context, "rec succeed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "rec failed", Toast.LENGTH_SHORT).show();
			}			
			//Intent i = new Intent(context,MainActivity.class);
			db.close();
			ListViewContent.set(listItemName, "<br/><p><font color=#33B6EA>เรื่อง :" +niyayTable.get(listItemName)[1]+"</font><br />" +
					"<font color=#cc0029> ล่าสุด ตอน : " +doc+" ("+niyayTable.get(listItemName)[3]+")</font></p>"
					);		
			listAdap.notifyDataSetChanged();
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
			listAdap.notifyDataSetChanged();
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
			ListViewContent.add("<h2>Please add your first niyay. (Menu->Add open your main niyay page or chapter you want)</h2>");
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
			Thread t = new Thread() {
				public void run() {
					listAdap.notifyDataSetChanged();
				}
			};
			t.start();
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
			try {
				//Thread.currentThread();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}while(c.moveToNext());
		Log.e("loop end", Integer.toString(i));
		db.close();
		//Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}

	public static String displayBook(Cursor c) {

		int status = 0;
		String title = c.getString(4);
		final String url = c.getString(2); 
		final String chapter = c.getString(3);
		String text1 = "";
		//if (title.contains(">")) title = title.substring(title.indexOf(">"));

		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet(new URI(url+chapter));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			text1 = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("Error" , e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("Error" , e.getMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("Error" , e.getMessage());
			e.printStackTrace();		
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		if (text1.contains("<title>")) {
			final String start = text1.substring(text1.indexOf("<title>")+7);
			text1 = Jsoup.parse((start.substring(start.indexOf(">")+2, start.indexOf("</title>")))).text();
		}
		else {
			text1 = "รอตอนใหม่";
		}

		/*		Log.e("title",(title == null) ? "null" : title);
		Log.e("text1",text1);
		Log.e("compare",Integer.toString(text1.compareTo(title)));		*/	

		if (title == null ) title = "";			
		else if (title.contains(">")) title = title.substring(title.indexOf(">")+2);
		if (text1.contains(">"))	text1 = text1.substring(text1.indexOf(">")+2);
		if (title.isEmpty()) {
			title = text1;
			status = -1;
		}
		else if (!text1.equals(title) && !text1.equals("รอตอนใหม่")) {
			Log.e("title",title);
			Log.e("text1",text1);
			Log.e("compare",(text1.equals(title))? "same" : "not same");
			
		    try {
		        byte[] utf8Bytes = text1.getBytes("UTF8");
		        byte[] defaultBytes = text1.getBytes();

		        String roundTrip = new String(utf8Bytes, "UTF8");
		        Log.e("roundTrip",roundTrip);


		        printBytes(utf8Bytes, "utf8Bytes");
		        printBytes(defaultBytes, "defaultBytes");
		      } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		      }
		    try {
		        byte[] utf8Bytes = title.getBytes("UTF8");
		        byte[] defaultBytes = title.getBytes();

		        String roundTrip = new String(utf8Bytes, "UTF8");
		        Log.e("roundTrip",roundTrip);

		        printBytes(utf8Bytes, "utf8Bytes");
		        printBytes(defaultBytes, "defaultBytes");
		      } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		      }		  
			 
			status = 1; //current chapter update				
		} 
		Log.e("status", Integer.toString(status));


		if (status == 0) {
			ListViewContent.add(
					"<br/><p><font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ล่าสุด ตอน : " +title+" ("+chapter+")</font></p>"); 
		}
		else if (status == 1 || status == -1) {
			ListViewContent.add(
					"<br/><p><font color=#339900>มีการอัพเดตตอนปัจจุบัน</font><br />" +
							"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font><br />" +
							"<font color=#cc0029> ตอน : " +text1+" ("+chapter+")</font></p>"); 
		}

		/*		Log.e("content",
				"id: " +c.getString(0)+"\n"+
						"name:" +c.getString(1)+"\n" +
						"url: " +c.getString(2)+"\n"+
						"chapter: " +c.getString(3)+"\n"+
						"title: " +c.getString(4));*/

		if  (status == 0) return title;
		else if  (status == 1 || status == -1) return text1;
		return "";
	}

		static public String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	static public String charToHex(char c) {
		// Returns hex String representation of char c
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

	static void printBytes(byte[] array, String name) {
	    for (int k = 0; k < array.length; k++) {
	    	Log.e(name , "[" + k + "] = " + "0x"
	          + byteToHex(array[k]));
	    }
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
				arg1.setOnClickListener(new OnClickListener() { 
					@Override 
					public void onClick(View v) { 
						v.showContextMenu();
					} 
				}); 
				arg1.setOnCreateContextMenuListener(null);
				holder.text = (TextView) arg1.findViewById(R.id.textView1);
				/*				holder.delete =  (Button) arg1.findViewById(R.id.button1);

				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						v.showContextMenu();
					}
				});*/
				arg1.setTag(holder);
			}else {
				holder = (ListContent) arg1.getTag();
			}	
			holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
			return arg1;
		}

		@SuppressWarnings("unused")
		public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
			// empty implementation
		}

		class ListContent {
			TextView text;
			//Button delete;
		}
	}
}
