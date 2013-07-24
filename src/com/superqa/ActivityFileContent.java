package com.superqa;

import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.NullCipher;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityFileContent extends Activity {

	public ListView list;
	QAData qaData;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题 
        setContentView(R.layout.activity_filecontent);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//自定义布局赋值

        setTitle("查看保存的问题");
        
        showAnswerList();

        // 点击【删除】按钮
        TextView btnDel = (TextView)findViewById(R.id.btnDelFile);
        btnDel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(SuperQAToolkit.confirmDialog(ActivityFileContent.this, "确认删除该收藏内容？"))
				{
					QAFilesManage.removeQAFile(ActivityFileList.curFileIndex);
					ActivityFileContent.this.finish();
				}
			}
        });

        // 点击【返回】按钮
        TextView btnBack = (TextView)findViewById(R.id.btnBackFile);
        btnBack.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
        });

    }
	
	public void setTitle(String strTitle){
		TextView tw = (TextView)getWindow().findViewById(R.id.titleInfo);
		tw.setText(Html.fromHtml(strTitle));
	}
	    
    // 处理手机上的返回键
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		finish();
    	}
		return false;
    }
    

    public int showAnswerList(){
    	qaData = QAFilesManage.loadQAFile(ActivityFileList.curFileIndex);
        if(qaData == null)
        	return 0;
    	// 结果显示
        TextView answerInfo = (TextView)findViewById(R.id.askInfo);
        int nAnswers = qaData.nAnswerNum;
        String strQ = qaData.strQuestion;
        answerInfo.setText(Html.fromHtml("<font color='#ffffff'><b>　问:</b>"+strQ+"</font><font color='#e0e0e0'>("+Integer.valueOf(nAnswers)+")</font>"));
    	
    	//绑定Layout里面的ListView
        list = (ListView) findViewById(R.id.listFileContent);
    	//生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qaData.nAnswerNum;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	////map.put("aItemImage", R.drawable.ic_action_search);//图像资源的ID
        	String strTitle = "<font color='#606060'>回答"+(i+1)+
        			": </font><font color='#0000BB'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAnswer)+"</font> | "+
        			"<font color='#a0a0a0'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAuthorLevel)+"</font>";
        	map.put("fItemTitle", Html.fromHtml(strTitle));
        	map.put("fItemText", Html.fromHtml(qaData.answers.get(i).strContent+""));
        	map.put("fItemInfo",
        			"支持"+
        			qaData.answers.get(i).nOkPoints + " | "+
        			qaData.strDatetime);
        	listItem.add(map);
        }
        
        
    	//生成适配器的Item和动态数组对应的元素，这里用SimpleAdapter作为ListView的数据源
        //如果条目布局比较复杂，可以继承BaseAdapter来定义自己的数据源。
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//数据源 
            R.layout.filecontentview_item,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"fItemImage","fItemTitle", "fItemText", "fItemInfo"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.fItemImage,R.id.fItemTitle,R.id.fItemText, R.id.fItemInfo}
        );
       
        //添加并且显示
        list.setAdapter(listItemAdapter);
    	return nAnswers;
    }
    
}
