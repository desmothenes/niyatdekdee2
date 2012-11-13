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
import android.widget.Spinner;
import android.widget.TextView;

public class SearchGroupActivity extends Activity {
	ArrayList<String> mainList = new ArrayList<String>();
	private Spinner subGP;
	private Button searchbtt;
	private String story_type = "0";
	private String main = "0";
	private String sub = "0";
	private String isend = "0";
	private String sort = "1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_search);
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
		Spinner mainGP = (Spinner) findViewById(R.id.Spinner02);
		subGP = (Spinner) findViewById(R.id.spinner2);
		mainList.add("�ء��Ǵ");
		mainList.add("��Ǵ��ѡ�������");
		mainList.add("��Ǵ��ѡ������");
		mainList.add("��Ǵ��ѡ�ſ�����");
		ArrayAdapter<String> mainadb = new ArrayAdapter<String>(SearchGroupActivity.this,
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
					main = "0";
					sub = "0";
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
				ArrayAdapter<String> subadb = new ArrayAdapter<String>(SearchGroupActivity.this,
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

		Spinner sortMenu = (Spinner) findViewById(R.id.Spinner01);
		ArrayList<String> sortList = new ArrayList<String>();		
		sortList.add("�Ѿഷ����ش");
		sortList.add("�ռ����ҡ�ش");
		ArrayAdapter<String> sortadb = new ArrayAdapter<String>(SearchGroupActivity.this,
				android.R.layout.simple_spinner_item, 
				sortList);
		sortadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		sortMenu.setAdapter(sortadb);
		sortMenu.setSelection(1);

		sortMenu.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				sort = Integer.toString(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		}); 

		Spinner stausMenu = (Spinner) findViewById(R.id.spinner1);
		ArrayList<String> statusList = new ArrayList<String>();		
		statusList.add("������");
		statusList.add("������");
		statusList.add("�ѧ��診");

		ArrayAdapter<String> statusadb = new ArrayAdapter<String>(SearchGroupActivity.this,
				android.R.layout.simple_spinner_item, 
				statusList);
		statusadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		stausMenu.setAdapter(statusadb);
		stausMenu.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				isend = Integer.toString(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		Spinner gpMenu = (Spinner) findViewById(R.id.Spinner03);
		ArrayList<String> gpList = new ArrayList<String>();		
		gpList.add("������");
		gpList.add("����ͧ���");
		gpList.add("����ͧ���");

		ArrayAdapter<String> gpadb = new ArrayAdapter<String>(SearchGroupActivity.this,
				android.R.layout.simple_spinner_item, 
				gpList);
		gpadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		gpMenu.setAdapter(statusadb);
		gpMenu.setSelection(0);
		gpMenu.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				story_type = Integer.toString(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		searchbtt = (Button) findViewById(R.id.searchbtt);
		searchbtt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				add();
			}			
		});
	}

	protected void add() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getBaseContext(), Find.class);
		intent.putExtra("story_type",story_type);
		intent.putExtra("main",main);
		intent.putExtra("sub",sub);
		intent.putExtra("isend",isend);
		intent.putExtra("sort",sort);
		intent.putExtra("from","gp");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_seaech, menu);
		return true;
	}
}
