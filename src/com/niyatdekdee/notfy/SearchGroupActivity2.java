package com.niyatdekdee.notfy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;

public class SearchGroupActivity2 extends Activity {
    ArrayList<String> mainList = new ArrayList<String>();
    private Spinner subGP;
    private Button searchbtt;
    private ScrollView point;
    private String story_type = "2";
    private String main = "0";
    private String sub = "0";
    private String isend = "0";
    private String sort = "1";
    private boolean result = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_search);
        if (Setting.getScreenSetting(getApplicationContext()).equals("1"))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (customTitleSupported) {

            //��駤�� custom titlebar �ҡ custom_titlebar.xml
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar_ok);

            //����� btnSearch btnDirection ��ҡѺ View
            TextView title = (TextView) findViewById(R.id.textViewOk);
            title.setText(" ���ҹ����");

            RelativeLayout barLayout = (RelativeLayout) findViewById(R.id.okbar);
            ImageButton btnOk = (ImageButton) findViewById(R.id.imageButton1);
            switch (Integer.parseInt(Setting.getColorSelectSetting(getApplicationContext()))) {
                case 0:
                    btnOk.setBackgroundResource(R.drawable.bg_titlebar);
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar);
                    break;
                case 1:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    btnOk.setBackgroundResource(R.drawable.bg_titlebar_yellow);
                    break;
                case 2:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_green);
                    btnOk.setBackgroundResource(R.drawable.bg_titlebar_green);
                    break;
                case 3:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    btnOk.setBackgroundResource(R.drawable.bg_titlebar_pink);
                    break;
                case 4:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    btnOk.setBackgroundResource(R.drawable.bg_titlebar_blue);
                    break;
                case 5:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_fuchsia);
                    break;
                case 6:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_siver);
                    break;
                case 7:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_glay);
                    break;
                case 8:
                    barLayout.setBackgroundResource(R.drawable.bg_titlebar_orange);
                    break;
            }

            btnOk.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_search));
            btnOk.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    add();
                }
            });

            ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        point = (ScrollView) findViewById(R.id.ScrollView01);
        Spinner mainGP = (Spinner) findViewById(R.id.Spinner02);
        subGP = (Spinner) findViewById(R.id.spinner2);
        mainList.add("�ء��Ǵ");
        mainList.add("��Ǵ��ѡ�������");
        mainList.add("��Ǵ��ѡ������");
        mainList.add("��Ǵ��ѡ�ſ�����");
        ArrayAdapter<String> mainadb = new ArrayAdapter<String>(SearchGroupActivity2.this,
                android.R.layout.simple_spinner_item,
                mainList);
        mainadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainGP.setAdapter(mainadb);
        subGP.setEnabled(false);
        mainGP.setSelection(1);
        mainGP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                } else if (index == 1) {
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

                } else if (index == 2) {
                    main = "2";
                    subList.add("������");
                    subList.add("��������ͺ���");
                    subList.add("����������ʹ��Թ���Ե");
                    subList.add("��紻���ѵ���ʵ��");
                    subList.add("�����������ͧ���¹");
                    subList.add("��������͹��ҹ��");
                    subList.add("����������� ෤�Ԥ");
                    subList.add("��紷�ͧ�����");
                } else if (index == 3) {
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
                ArrayAdapter<String> subadb = new ArrayAdapter<String>(SearchGroupActivity2.this,
                        android.R.layout.simple_spinner_item,
                        subList);
                subadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                final int x = point.getScrollX();
                final int y = point.getScrollY();
                subGP.setAdapter(subadb);
                subGP.setSelection(0);
                subGP.setEnabled(true);
                point.scrollTo(x, y);
            }

        });

        subGP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == 0) {
                    sub = "-1";
                } else if (mainList.size() - 1 == arg2 && !main.equals("2")) {
                    sub = "0";
                } else {
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
        ArrayAdapter<String> sortadb = new ArrayAdapter<String>(SearchGroupActivity2.this,
                android.R.layout.simple_spinner_item,
                sortList);
        sortadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortMenu.setAdapter(sortadb);
        sortMenu.setSelection(1);

        sortMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        statusList.add("�ѧ��診");
        statusList.add("������");

        ArrayAdapter<String> statusadb = new ArrayAdapter<String>(SearchGroupActivity2.this,
                android.R.layout.simple_spinner_item,
                statusList);
        statusadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stausMenu.setAdapter(statusadb);
        stausMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
        //gpList.add("������");
        //gpList.add("����ͧ���");
        gpList.add("����ͧ���");

        ArrayAdapter<String> gpadb = new ArrayAdapter<String>(SearchGroupActivity2.this,
                android.R.layout.simple_spinner_item,
                gpList);
        gpadb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gpMenu.setAdapter(statusadb);
        gpMenu.setSelection(0);
        gpMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                story_type = "2";//Integer.toString(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                story_type = "2";
            }

        });

        searchbtt = (Button) findViewById(R.id.searchbtt);
        searchbtt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                add();
            }
        });
    }

    protected void add() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getBaseContext(), Find.class);
        intent.putExtra("story_type", story_type);
        intent.putExtra("main", main);
        intent.putExtra("sub", sub);
        intent.putExtra("isend", isend);
        intent.putExtra("sort", sort);
        intent.putExtra("from", "gp");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            result = true;
        } else if (!result) {
            setResult(RESULT_CANCELED);
        }
    }

    /*	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_seaech, menu);
        return true;
    }*/
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }
}
