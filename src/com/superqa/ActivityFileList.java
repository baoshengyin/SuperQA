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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题 
        setContentView(R.layout.activity_savelist);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//自定义布局赋值
        
        list = (ListView) findViewById(R.id.FilelistView);
        
        setTitle("选择收藏的问题");
        
        showFileList();
        
        // 点击【返回】按钮
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
		// 装载文件列表
		Vector<QAFileInfo> flist = QAFilesManage.getQAFileList();
		
		SuperQAToolkit.showTip("共"+flist.size()+"个保存结果");
		//生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<flist.size();i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_action_del);//图像资源的ID
        	
        	String strTitle = flist.get(i).strTitle;
        	map.put("ItemTitle", Html.fromHtml(strTitle)); //
        	map.put("ItemText", "");
        	map.put("ItemInfo", flist.get(i).strSaveDatetime);
        	listItem.add(map);
        }    
        
        if(listItem.size() == 0)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
	    	map.put("ItemTitle", "没有发现保存结果");
	    	listItem.add(map);
        }
        
    	//生成适配器的Item和动态数组对应的元素，这里用SimpleAdapter作为ListView的数据源
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//数据源 
            R.layout.savelistview_item,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"ItemImage","ItemTitle", "ItemText", "ItemInfo"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText, R.id.ItemInfo}
        );
       
        //添加并且显示
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
