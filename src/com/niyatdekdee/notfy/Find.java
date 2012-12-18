package com.niyatdekdee.notfy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class Find extends ListActivity {
	private ProgressDialog dialog;
	private InteractiveArrayAdapter adapter;
	private final ArrayList<String> ListViewContent = new ArrayList<String> ();
	private final ArrayList<String> linktable = new ArrayList<String> ();
	private final ArrayList<String> authortable = new ArrayList<String> ();
	private final ArrayList<String> detailtable = new ArrayList<String> ();
	private final ArrayList<String> chaptertable = new ArrayList<String> ();
	private final ArrayList<String> viewtable = new ArrayList<String> ();
	private final ArrayList<String> Content = new ArrayList<String> ();
	private int page = 1;
	private String story_type;
	private String main;
	private String sub;
	private String isend;
	private String sort;
	private String from;
	private String title;
	private String writer;
	private String abstract_w;
	private String ntitle;
	private String nwriter;
	private String nabstract_w;
	private ListView list;
	private ProgressBar spiner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 

		setContentView(R.layout.activity_find);
		if (customTitleSupported) {
			//��駤�� custom titlebar �ҡ custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);
			//����� btnSearch btnDirection ��ҡѺ View
			TextView title = (TextView) findViewById(R.id.textViewBar);
			title.setText(" �š�ä���");
			RelativeLayout barLayout =  (RelativeLayout) findViewById(R.id.nonbar);
			spiner = new ProgressBar(this);
			RelativeLayout.LayoutParams lspin =  new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lspin.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lspin.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			spiner.setLayoutParams(lspin);
			barLayout.addView(spiner);
			spiner.setVisibility(View.GONE);					
			
			switch (MainActivity.titleColor) {
			case 0:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar);
				spiner.setBackgroundResource(R.drawable.bg_titlebar);
				break;
			case 1:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				spiner.setBackgroundResource(R.drawable.bg_titlebar_yellow);
				break;
			case 2:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
				spiner.setBackgroundResource(R.drawable.bg_titlebar_green);
				break;
			case 3:
				barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
				spiner.setBackgroundResource(R.drawable.bg_titlebar_pink);	
			}
			
			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		Intent intent = getIntent(); 
		if ((from = intent.getStringExtra("from")).equals("gp")) {
			story_type = intent.getStringExtra("story_type");
			main = intent.getStringExtra("main");
			sub = intent.getStringExtra("sub");
			isend = intent.getStringExtra("isend");
			sort = intent.getStringExtra("sort");
		}
		else {
			story_type = intent.getStringExtra("story_type");
			main = intent.getStringExtra("main");
			sub = intent.getStringExtra("sub");
			isend = intent.getStringExtra("isend");
			title = intent.getStringExtra("title");
			writer = intent.getStringExtra("writer");
			abstract_w = intent.getStringExtra("abstract_w");
			ntitle = intent.getStringExtra("ntitle");
			nwriter = intent.getStringExtra("nwriter");
			nabstract_w = intent.getStringExtra("nabstract_w");
		}
		
		Log.v("from", from);
		
		adapter = new InteractiveArrayAdapter(this, Content);
		adapter.notifyDataSetChanged();
		dialog = ProgressDialog.show(Find.this,"Loading", "Please Wait...",true);
		doback dob=new doback();
		dob.execute();
		list = getListView();
		list.setFastScrollEnabled(true);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (linktable.size() == 0) return;
				String url = linktable.get(arg2);
				Log.v("url", url);
				Intent i = new Intent(getBaseContext(),InsertForm.class);				
				final String title = ListViewContent.get(arg2);
				Log.v("title", title);
				i.putExtra("name",title);
				//in this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
				final String stext = "id=";
				//����ѡ�ͧ�͹
				final int start = url.lastIndexOf(stext)+stext.length();
				if (start - stext.length() == -1) {
					Toast.makeText(getBaseContext(), "Error not correct niyay page", Toast.LENGTH_SHORT).show();
					return;
				}

				//Log.v("url.length()", Integer.toString(url.length()));
			//	Log.v("start", Integer.toString(start));
				//Log.v("stext.length()", Integer.toString(stext.length()));
				int len=0;
			//	Log.v("Character", Character.toString(url.charAt(start)));
				for (int i3 = start;i3 < url.length() && Character.isDigit(url.charAt(i3));i3++) {						
					len++;
					//Log.v("Character", Character.toString(url.charAt(i3)));
				}
			//	Log.v("len", Integer.toString(len));
				final String unum = url.substring(start,start+len);	
				Log.v("unum", unum);
				url = "http://writer.dek-d.com/dek-d/writer/viewlongc.php?id="+unum+"&chapter=";					

				Log.v("url", url);
				i.putExtra("url",url);
				final String chapter = chaptertable.get(arg2).replace(" �͹", "");
				Log.v("chapter", chapter);
				i.putExtra("chapter",chapter);
				startActivity(i);
			}
		});  
		list.setOnScrollListener(new LoadMoreListView());
	}
	
	

	boolean isOnline() { 
		ConnectivityManager cm = 
				(ConnectivityManager) Find.this.getSystemService(Context.CONNECTIVITY_SERVICE); 

		return cm.getActiveNetworkInfo() != null &&  
				cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
	} 

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_find, menu);
		return true;
	}
*/
	class doback extends AsyncTask<URL, Integer, Long>
	{
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			spiner.setVisibility(View.VISIBLE);
		}
		
		protected void onProgressUpdate(Integer... progress) 
		{			
			if (progress[0] == -1) 
			Toast.makeText(Find.this, "������������ջѭ�� ��سһ�Ѻ��ا����������� �����ͧ����", Toast.LENGTH_LONG).show();
		}
		
		@Override
		protected Long doInBackground(URL... arg0) 
		{
			try
			{
				Log.v("doback", "in");
				if (isOnline())	{
					Log.v("doback", "on");
					showAllFind();
					//showAllBookOffline() ;
				}
				else {
					Log.v("doback", "off");
					AlertDialog alertDialog = new AlertDialog.Builder(Find.this).create();
					alertDialog.setTitle("Error.");
					alertDialog.setMessage("Not connect to internet.\nPlease check your internet connection");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// here you can add functions
						}
					});
					alertDialog.show();
				}
			}
			catch(Exception e)
			{

			}
			return null;
		}

/*		protected void onProgressUpdate(Integer... progress) 
		{
		}*/
		protected void onPostExecute(Long result) 
		{
			try
			{
				if (Content.size() == 0) {
					AlertDialog alertDialog = new AlertDialog.Builder(Find.this).create();
					alertDialog.setTitle("��辺������");
					alertDialog.setMessage("�������¡������ �ͧ��Ǩ�ͺ������������Թ������ �����ͧ����");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// here you can add functions
						}
					});
					alertDialog.show();
				}
				spiner.setVisibility(View.GONE);
				//Find.this.setProgressBarIndeterminateVisibility(false);
				Parcelable state = list.onSaveInstanceState();
				setListAdapter(adapter);
				list.onRestoreInstanceState(state);
/*				Log.v("listView", "listView");
				for (String i : ListViewContent)
					Log.v("ListViewContent", i);
				for (String i : linktable)
					Log.v("linktable", i);
				for (String i : authortable)
					Log.v("authortable", i);
				for (String i : detailtable)
					Log.v("detailtable", i);
				for (String i : linktable)
					Log.v("viewtable", i);
				for (String i : viewtable)
					Log.v("viewtable", i);*/
				dialog.dismiss();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				dialog.dismiss();
			}
		}
/*
		public void execute(int i) {
			// TODO Auto-generated method stub

		}*/
		
		private String convert(String in) throws UnsupportedEncodingException {
			StringBuilder sum = new StringBuilder();
			byte bin[] = in.getBytes("tis620");
			for (int i=0;i<in.length();i++) {
				if (bin[i] < 0) 
					sum.append(String.format("%%%02X", bin[i]));
				else
					sum.append(in.charAt(i));
			}
			return sum.toString();			
		}

		private void showAllFind() {
			Log.v("showAllFind", "showAllFind");
			// TODO Auto-generated method stub
			/*		String story_type = "2";
			String main = "1";
			String sub = "17";
			String isend = "1";
			String sort = "1";*/

			//http://www.dek-d.com/writer/frame.php?page=2&main=1&sub=17&isend=1&story_type=2&sort=1
			String url;
			if (from.equals("gp")) {
				url = String.format("http://www.dek-d.com/writer/frame.php?page=%s&main=%s&sub=%s&isend=%s&story_type=%s&sort=%s"
						,Integer.toString(page),main,sub,isend,story_type,sort);
				System.out.println(main+sub+isend+story_type+sort);
			}
			else if (main.equals("0") || main.equals("")) {
				try {
					title = convert(title);
					ntitle = convert(ntitle);
					writer = convert(writer);
					nwriter = convert(nwriter);
					abstract_w= convert(abstract_w);
					nabstract_w = convert(nabstract_w);					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				url = String.format("http://www.dek-d.com/writer/frame.php?page=%s&is_end=%s&story_groups=&title=%s&n_title=%s&type=%s&writer=%s&n_writer=%s&abstract_w=%s&n_abstract_w=%s"
						,Integer.toString(page),isend,title,ntitle,story_type,writer,nwriter,abstract_w,nabstract_w);
			}
			else {
				try {
					title = convert(title);
					ntitle = convert(ntitle);
					writer = convert(writer);
					nwriter = convert(nwriter);
					abstract_w= convert(abstract_w);
					nabstract_w = convert(nabstract_w);					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				url = String.format("http://www.dek-d.com/writer/frame.php?page=%s&is_end=%s&story_groups=%s,%s&title=%s&n_title=%s&type=%s&writer=%s&n_writer=%s&abstract_w=%s&n_abstract_w=%s"
						,Integer.toString(page),isend,main,sub,title,ntitle,story_type,writer,nwriter,abstract_w,nabstract_w);
			}
			System.out.println(url);
			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(8000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				publishProgress(-1);
				//Toast.makeText(getBaseContext(), "������������ջѭ�� ��سһ�Ѻ��ا����������� �����ͧ����", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}    
			
			if (doc.select(".fr-book").first().text().equals("��辺������")) {
				if (Content.size() == 0)
					Content.add("��辺������");
				return;
			}
			//System.out.println(doc.html());
			Elements link1 = doc.select(".book-text6");
			int startsize = ListViewContent.size();
			Log.v("startsize", Integer.toString(startsize));
			if(link1 == null) {
				if (Content.size() == 0)
					Content.add("��辺������");
				return;
			}
			for (Element link:link1) {
				String stext = link.text();
				if (stext.contains("by")) {
					authortable.add(stext);
					continue;
				}
				linktable.add(link.select("a").attr("href"));		
				ListViewContent.add(stext);	
			}
			Log.v("startsize", Integer.toString(startsize));
			if (startsize == ListViewContent.size()) {
				page = -1;
				return;
			}
			link1 = doc.select(".book-text1");
			if(link1 == null) return;
			for (Element link:link1) {
				String stext = link.text();
				detailtable.add(stext);	
			}	
			link1 = doc.select(".view");
			if(link1 == null) return;
			for (Element link:link1) {
				String stext = link.text();
				if (stext.contains("/")) 
					viewtable.add(stext);	
			}	
			link1 = doc.select("p.chapter");
			if(link1 == null) return;
			for (Element link:link1) {
				String stext = link.text();	
				chaptertable.add(stext);
			}
			for (int position = startsize; position < ListViewContent.size() ;position++)
				Content.add("<br/><p><font color=#33B6EA>����ͧ :" +ListViewContent.get(position)+" " +authortable.get(position)+
						" </font><br /><br />" +
						"<font color=#cc0029> " +detailtable.get(position)+"</font><br/><br />" +
						"<font color=#339900> �����Ҫ� ��͹��� " +viewtable.get(position).substring(0, viewtable.get(position).indexOf("/"))+" / ������ " +viewtable.get(position).substring(viewtable.get(position).indexOf("/")+1,viewtable.get(position).length())+
								" �� " +chaptertable.get(position)+
						" </font></p>");
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
				rowView = inflater.inflate(R.layout.list_item, null);}
			TextView text = (TextView) rowView.findViewById(R.id.textView1);
/*
			System.out.println(names.get(position));
			System.out.println(detailtable.get(position));
			System.out.println(authortable.get(position));
			String htmltext = "<br/><p><font color=#33B6EA>����ͧ :" +names.get(position)+" " +authortable.get(position)+
					" </font><br /><br />" +
					"<font color=#cc0029> " +detailtable.get(position)+"</font><br/><br />" +
					"<font color=#339900> �����Ҫ� ��͹��� " +viewtable.get(position).substring(0, viewtable.get(position).indexOf("/"))+" / ������ " +viewtable.get(position).substring(viewtable.get(position).indexOf("/")+1,viewtable.get(position).length())+
							" �� " +chaptertable.get(position)+
					" </font></p>";
			*/
			text.setText((names.get(position)!= null)? Html.fromHtml(names.get(position)) : "1");
			//thumb_image.setImageResource(R.drawable.rihanna);
			//thumb_image.setImageBitmap(imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image));
			//imageLoader.DisplayImage((imagetable.get(position)!= null)? imagetable.get(position) : "3",thumb_image);
			return rowView;
		}

	}

	class LoadMoreListView implements OnScrollListener {
		//private int visibleThreshold = 5;
		private int previousTotal = 0;
		private boolean loading = true;

		public LoadMoreListView() {
		}
		public LoadMoreListView(int visibleThreshold) {
			//this.visibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			//Log.v("firstVisibleItem", Integer.toString(firstVisibleItem));
			//Log.v("visibleItemCount", Integer.toString(visibleItemCount));
			//Log.v("totalItemCount", Integer.toString(totalItemCount));
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					page++;
				}
			}
			if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleItemCount)) {
				// I load the next page of gigs using a background task,
				// but you can call any function here.
				new doback().execute();
				loading = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}
}
