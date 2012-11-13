package com.niyatdekdee.notfy;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchNameActivity extends Activity {
	private ArrayList<String> mainList = new ArrayList<String>();
	private String type = "";
    private String is_end = "";
	protected String main = "";
	protected String sub = "";
	private Spinner subGP;


	@Override
	protected void onResume() {
		super.onResume();
		ScrollView mainScrollView = (ScrollView)findViewById(R.id.scrollView1);
		mainScrollView.smoothScrollTo(0,0);
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_search_name);
        if (customTitleSupported) {

			//��駤�� custom titlebar �ҡ custom_titlebar.xml
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);

			//����� btnSearch btnDirection ��ҡѺ View
			TextView title = (TextView) findViewById(R.id.textViewOk);
			title.setText(" ���ҹ����");
			
			ImageButton btnOk = (ImageButton)findViewById(R.id.imageButton1);
			btnOk.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_search));
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					add();
				}				
			});

			ImageButton btnDirection = (ImageButton)findViewById(R.id.btnDirection);
			btnDirection.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
        
        
        Spinner gpMenu = (Spinner) findViewById(R.id.spinner4);
		ArrayList<String> gpList = new ArrayList<String>();		
		gpList.add("������");
		gpList.add("����ͧ���");
		gpList.add("����ͧ���");
    	
		ArrayAdapter<String> gpadb = new ArrayAdapter<String>(SearchNameActivity.this,
				android.R.layout.simple_spinner_item, 
				gpList);
		gpadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		gpMenu.setAdapter(gpadb);
		gpMenu.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 != 0)
				type = Integer.toString(arg2);
				else type = "";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		Spinner stausMenu = (Spinner) findViewById(R.id.spinner5);
		ArrayList<String> statusList = new ArrayList<String>();		
		statusList.add("������");
		statusList.add("�ѧ��診");
		statusList.add("������");

		ArrayAdapter<String> statusadb = new ArrayAdapter<String>(SearchNameActivity.this,
				android.R.layout.simple_spinner_item, 
				statusList);
		statusadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		stausMenu.setAdapter(statusadb);
		stausMenu.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 != 0)
				is_end = Integer.toString(arg2);
				else is_end = "";
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Spinner mainGP = (Spinner) findViewById(R.id.spinner6);
		subGP = (Spinner) findViewById(R.id.spinner13);
		mainList.add("�ء��Ǵ");
		mainList.add("��Ǵ��ѡ�������");
		mainList.add("��Ǵ��ѡ������");
		mainList.add("��Ǵ��ѡ�ſ�����");
		ArrayAdapter<String> mainadb = new ArrayAdapter<String>(SearchNameActivity.this,
				android.R.layout.simple_spinner_item, 
				mainList);
		mainadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		mainGP.setAdapter(mainadb);
		subGP.setEnabled(false);
		mainGP.setSelection(0);
		mainGP.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				// TODO Auto-generated method stub
				ArrayList<String> subList = new ArrayList<String>();
				if (index == 0) {
					main = "";
					sub = "";
					subGP.setEnabled(false);
					return;
				}
				else if(index == 1) {
					main = "1";
					subList.add("������");
					subList.add("ʺ��� �������´");
					subList.add("�ѡ��ҹ����");
					subList.add("��駡Թ�");
					subList.add("�ѡ������");
					subList.add("�Էҹ");
					subList.add("������");
					subList.add("�׺�ǹ");
					subList.add("�з֡��ѭ");
					subList.add("ʧ����");
					subList.add("�š���ѹ");
					subList.add("��͹");
					subList.add("ʹյ �Ѩ�غѹ ͹Ҥ�");
					subList.add("�Ե�Է��");
					subList.add("�ѧ��");
					subList.add("�ѡ���");
					subList.add("Ό�ҫ�");
					subList.add("���ѧ����");
					subList.add("�Է����ʵ��");
					subList.add("Ό�Ԥ");
					subList.add("��ó�������Ǫ�");
					subList.add("����");
					
				}
				else if(index == 2) {
					main = "2";
					subList.add("������");
					subList.add("��������ͺ���");
					subList.add("����������ʹ��Թ���Ե");
					subList.add("��紻���ѵ���ʵ��");
					subList.add("�����������ͧ���¹");
					subList.add("��������͹��ҹ��");
					subList.add("����������� ෤�Ԥ");
					subList.add("��紷�ͧ�����");
				}
				else if(index == 3) {
					main = "3";
					subList.add("������");
					subList.add("�آ�Ҿ �������");
					subList.add("��觢ͧ intrend");
					subList.add("����Դ���ѧ");
					subList.add("����� �ŧ ˹ѧ");
					subList.add("��䫹� ��ҿԡ");
					subList.add("����ٹ ����");
					subList.add("�ͷ� ෤�����");
					subList.add("����");					
				}
				ArrayAdapter<String> subadb = new ArrayAdapter<String>(SearchNameActivity.this,
						android.R.layout.simple_spinner_item, 
						subList);
				subadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				subGP.setAdapter(subadb);
				subGP.setSelection(0);
				subGP.setEnabled(true);
			} 

		});
		
		subGP.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 == 0) {
					sub = "-1";
				}
				else if (mainList.size()-1 == arg2 && main != "2") {
					sub = "0";
				}
				else {
					sub = Integer.toString(arg2);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}			
		});
		
		Button searchbtt = (Button) findViewById(R.id.button1);
		searchbtt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				add();
			}			
		});
    }
	
	private void add() {
		TextView title = (TextView) findViewById(R.id.editText1);
		TextView writer = (TextView) findViewById(R.id.editText2);
		TextView abstract_w = (TextView) findViewById(R.id.editText3);	
		TextView ntitle = (TextView) findViewById(R.id.editText9);
		TextView nwriter = (TextView) findViewById(R.id.editText10);
		TextView nabstract_w = (TextView) findViewById(R.id.editText11);	
		Intent intent = new Intent(getBaseContext(), Find.class);				
		intent.putExtra("story_type",type);
		intent.putExtra("main",main);
		intent.putExtra("sub",sub);
		intent.putExtra("isend",is_end);
		intent.putExtra("title",title.getText().toString());
		intent.putExtra("writer",writer.getText().toString());
		intent.putExtra("abstract_w",abstract_w.getText().toString());
		intent.putExtra("ntitle",ntitle.getText().toString());
		intent.putExtra("nwriter",nwriter.getText().toString());
		intent.putExtra("nabstract_w",nabstract_w.getText().toString());
		intent.putExtra("from","na");
		startActivity(intent);
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_name, menu);
        return true;
    }
}
