package com.niyatdekdee.notfy;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChapterListActivity extends ListActivity {

	private ArrayList<String> ListViewContent = new ArrayList<String> ();
	private final ArrayList<String> Linktable = new ArrayList<String> ();
	private InteractiveArrayAdapter adapter;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_chapter_list);
		Intent intent = getIntent();
		if (customTitleSupported) {
			//��駤�� custom titlebar �ҡ custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_nonmain);
			
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
			//����� btnSearch btnDirection ��ҡѺ View
			TextView title = (TextView) findViewById(R.id.textViewBar);
			title.setTextSize(15);
			title.setText(String.format(" %s",intent.getStringExtra("title")));

			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		 
		final String origin = intent.getStringExtra("url");
		//from this fomat http://writer.dek-d.com/dek-d/writer/viewlongc.php?id=580483&chapter=
		//to this fomat http://writer.dek-d.com/dek-d/writer/view.php?id=580483
		adapter = new InteractiveArrayAdapter(this, ListViewContent);
		adapter.notifyDataSetChanged();

		doback dob=new doback();
		dob.execute(origin);
		ListView list = getListView();
		list.setFastScrollEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				final String url = Linktable.get(arg2);
				Log.v("url", url);				
				final String title = Jsoup.parse(ListViewContent.get(arg2)).text();
				Log.v("title", title);
/*				Intent browser = new Intent(Intent.ACTION_VIEW);
 				Uri data = Uri.parse(url);
				browser.setData(data);
				startActivity(browser);		*/
				
				if (Setting.getArrowSelectSetting(ChapterListActivity.this).equals("0")) {
					Intent browser = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
					startActivity(browser);
				}
				else {
					Intent browserIntent = new Intent(getBaseContext(), DekdeeBrowserActivity.class);
					browserIntent.putExtra("url",url);
					browserIntent.putExtra("title",title);
					startActivity(browserIntent);
				}

			}
		});  
		setListAdapter(adapter);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_chapter_list, menu);
		return true;
	}*/

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
				rowView = inflater.inflate(R.layout.list_item2, null);
			}
			TextView text = (TextView) rowView.findViewById(R.id.textView1);			
			String htmltext = "<br/><p><h4> " +names.get(position)+	"</font></h4></p>";			
			text.setText((names.get(position)!= null)? Html.fromHtml(htmltext) : "1");
			return rowView;
		}

	}

	class doback extends AsyncTask<String, Integer, Long>
	{

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ChapterListActivity.this,"Loading", "Please Wait...\n\n��Ҥ�ҧ�ҹ���� 40 �Թҷ� �ͧ���͡������������",true);
		}
		@Override
		protected Long doInBackground(String... origin0) 
		{
			try {
				final String origin = origin0[0].contains("=") ? origin0[0].substring(0, origin0[0].lastIndexOf("=")+1) : origin0[0];
				System.out.println(origin0[0]);
				System.out.println(origin);
				Document doc = Jsoup.connect( "http://writer.dek-d.com/dek-d/writer/view.php?id"+origin.substring(origin.indexOf("="),origin.indexOf("&"))).timeout(15000).get();
				int c = 1;
				Elements link1 = doc.select("table.tableblack[border=1]");//[href~=/dek-d/writer/view.php]");
				for (Element link: link1) {
					if (++c != 2) continue;
					Elements link0 = link.select("td");
					int index = 0;
					StringBuilder sum = new StringBuilder();
					for (Element link2: link0) {
						if (++c < 8) continue;
						index++;
						String stext = link2.text();
						if (index == 1) {
							sum.append(String.format(" %s ",stext));
							Linktable.add(String.format("%s%s",origin,stext));
						}
						else if (index == 2) {
							sum.append(String.format("<font color=#33B6EA>  %s ",stext));
						}
						else { 
							sum.append(String.format("</font><font color=#cc0029>  %s",stext));
							ListViewContent.add(sum.toString());
							//print(ListViewContent.get(ListViewContent.size()-1));
							index = 0;
							sum  = new StringBuilder();
						}
					}
				}
				for (int i = 0;i<4;i++)
					ListViewContent.remove(ListViewContent.size()-1);
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), "������������ջѭ�� ��سһ�Ѻ��ا����������� �����ͧ����", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Long result) 
		{
			dialog.dismiss();
			setListAdapter(adapter);			
		}


	}
}