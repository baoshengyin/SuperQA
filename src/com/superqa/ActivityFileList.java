package com.superqa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.superqa.ActivityMain.GetAnswerProcess;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityFileList extends Activity {

	static int curFileIndex = 0;
	public ListView list;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
     	//  设置正确的标题
        setContentView(R.layout.activity_savelist);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//�Զ��岼�ָ�ֵ
        
        list = (ListView) findViewById(R.id.FilelistView);
        
        setTitle("ѡ���ղص�����");
        
        showFileList();
        
        // ��������ء���ť
        TextView btnBack = (TextView)findViewById(R.id.btnBackList);
        btnBack.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
				//SuperQAToolkit.showTip("Backed!");				
			}
        });
        
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				curFileIndex = arg2;
		
				Intent intent = new Intent();
				intent.setClass(ActivityFileList.this, ActivityFileContent.class);
				startActivity(intent);			
				
			}
		});

    }
	
	public int showFileList(){
		// װ���ļ��б�
		Vector<QAFileInfo> flist = QAFilesManage.getQAFileList();
		
		SuperQAToolkit.showTip("��"+flist.size()+"��������");
		//���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<flist.size();i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_action_del);//ͼ����Դ��ID
        	
        	String strTitle = flist.get(i).strTitle;
        	map.put("ItemTitle", Html.fromHtml(strTitle)); //
        	map.put("ItemText", "");
        	map.put("ItemInfo", flist.get(i).strSaveDatetime);
        	listItem.add(map);
        }    
        
        if(listItem.size() == 0)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
	    	map.put("ItemTitle", "û�з��ֱ�����");
	    	listItem.add(map);
        }
        
    	//������������Item�Ͷ�̬�����Ӧ��Ԫ�أ�������SimpleAdapter��ΪListView������Դ
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//����Դ 
            R.layout.savelistview_item,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemImage","ItemTitle", "ItemText", "ItemInfo"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText, R.id.ItemInfo}
        );
       
        //���Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
        
    	return 0;
    }
	
	public void setTitle(String strTitle){
		TextView tw = (TextView)getWindow().findViewById(R.id.titleInfo);
		tw.setText(Html.fromHtml(strTitle));
		
	}
	
	protected void onResume() {
        super.onResume();
        showFileList();
	}

	    

}
