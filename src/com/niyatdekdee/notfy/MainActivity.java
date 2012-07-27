package com.niyatdekdee.notfy;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
        setListAdapter(listAdap);
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
				showAllBook();
			}
        	
        });
        showAllBook();
    }
    
    private static void showAllBook() {
		// TODO Auto-generated method stub
    	ListViewContent.clear();
    	ListID.clear();
    	db = new DatabaseAdapter(context);
    	db.open();
    	
    	Cursor c = db.getAllNiyay();
    	c.requery();
    	int i = 0;
    	if(c.moveToFirst()) {
    		Log.v("loop", Integer.toString(i++));
    		displayBook(c);
    	}while(c.moveToNext());
    	
    	db.close();
    	Toast.makeText(context, "Show Data", Toast.LENGTH_SHORT).show();
	}

	public static void displayBook(Cursor c) {
		ListViewContent.add(
    			"<font color=#33B6EA>เรื่อง :" +c.getString(1)+"</font></br>" +
    			"ตอนที่ : " +c.getString(3)+"</br>"+
    			"<font color=#cc0029>ตอน : " +c.getString(4)+"</font>");
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
    
    private static class ListViewAdapter extends BaseAdapter {

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
			ListContent holder;
			if (arg1 == null) {
				arg1 = mInflater.inflate(R.layout.list_item, null);
				holder = new ListContent();
				holder.text = (TextView) arg1.findViewById(R.id.textView1);
				holder.delete =  (Button) arg1.findViewById(R.id.button1);
				holder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.v("delete", "delete id"+ListID.get(arg0));
						db.open();
						boolean flag = db.deleteNiyay(ListID.get(arg0));
						if (flag) {
							Toast.makeText(context, "delete succeed", Toast.LENGTH_SHORT).show();
						}else {
							Toast.makeText(context, "delete failed", Toast.LENGTH_SHORT).show();
						}
						
						//Intent i = new Intent(context,MainActivity.class);
						db.close();
						showAllBook();
					}
					
				});
				arg1.setTag(holder);
			}else {
				holder = (ListContent) arg1.getTag();
			}
			holder.text.setText(Html.fromHtml(ListViewContent.get(arg0)));
			return arg1;
		}
		
		static class ListContent {
			TextView text;
			Button delete;
		}
    	
    }
    
}
