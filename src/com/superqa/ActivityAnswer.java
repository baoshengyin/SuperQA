package com.superqa;

import java.util.ArrayList;
import java.util.HashMap;
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

public class ActivityAnswer extends Activity {

	public ListView list;
	TextView answerInfo;
	QAData qaData;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题 
        setContentView(R.layout.activity_answer);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//自定义布局赋值
        
        setTitle("网上结果");
        
        qaData = ActivityMain.qa.qaData.get(ActivityMain.curQuestionIndex);
        showAnswerList(qaData);

        // 点击【保存】按钮
        TextView btnSave = (TextView)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				QAFilesManage.saveQAFile(qaData);
			}
        });

        // 点击【打开】按钮
        TextView btnLoad = (TextView)findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
	    		Intent intent = new Intent();
				intent.setClass(ActivityAnswer.this, ActivityFileList.class);
				startActivity(intent);
				
			}
        });

        // 点击【返回】按钮
        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//SuperQAToolkit.showTip("Backed!");
				SuperQAToolkit.handle.showWaitingDlg(false);
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
			
			SuperQAToolkit.handle.showWaitingDlg(false);
    	}
		return false;
    }
    

    public int showAnswerList(QAData qaData){
    	// 结果显示
        answerInfo = (TextView)findViewById(R.id.answerInfo);
        int nAnswers = qaData.nAnswerNum;
        String strQ = qaData.strQuestion;
        answerInfo.setText(Html.fromHtml("<font color='#ffffff'><b>　问:</b>"+strQ+"</font><font color='#e0e0e0'>("+Integer.valueOf(nAnswers)+")</font>"));
    	
    	//绑定Layout里面的ListView
        list = (ListView) findViewById(R.id.listAnswerView);
    	//生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qaData.nAnswerNum;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	////map.put("aItemImage", R.drawable.ic_action_search);//图像资源的ID
        	String strTitle = "<font color='#606060'>回答"+(i+1)+
        			": </font><font color='#0000BB'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAnswer)+"</font> | "+
        			"<font color='#a0a0a0'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAuthorLevel)+"</font>";
        	map.put("aItemTitle", Html.fromHtml(strTitle));
        	map.put("aItemText", Html.fromHtml(qaData.answers.get(i).strContent+""));
        	map.put("aItemInfo",
        			"支持"+
        			qaData.answers.get(i).nOkPoints + " | "+
        			qaData.strDatetime);
        	listItem.add(map);
        }
        
        
    	//生成适配器的Item和动态数组对应的元素，这里用SimpleAdapter作为ListView的数据源
        //如果条目布局比较复杂，可以继承BaseAdapter来定义自己的数据源。
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//数据源 
            R.layout.answerview_item,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"aItemImage","aItemTitle", "aItemText", "aItemInfo"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.aItemImage,R.id.aItemTitle,R.id.aItemText, R.id.aItemInfo}
        );
       
        //添加并且显示
        list.setAdapter(listItemAdapter);
    	return 0;
    }
    
    public void onConfigurationChanged(Configuration newConfig){
   	 
    	super.onConfigurationChanged(newConfig);
    	SuperQAToolkit.showTip("欢迎使用《知道》 V1.0");
    	
    }

        
}

//public static void main(String[] args) throws MalformedURLException {
//
//	String strMainQ = "最小公分数";
//	
//	System.out.println("*************************************");
//	System.out.println("ASK:"+strMainQ);
//	
//	Questions qa = new Questions();
//	qa.startAsk(strMainQ);
//	
//	System.out.println("====Total Questions"+qa.nQuestionNum+"====");
//	
//	for(int i = 0; i<qa.qaData.size(); i++){
//		System.out.println("[Question:"+i+"th]");
//		System.out.println(qa.qaData.get(i).strQuestion);
//		System.out.println(qa.qaData.get(i).strDetailUrl);
//		System.out.println(qa.qaData.get(i).strIntr);
//		System.out.println(qa.qaData.get(i).strDatetime);
//		//
//		qa.loadAnswers(i);
//		System.out.println("----Total Answers:"+qa.qaData.get(i).nAnswerNum+"----");
//		
//		for(int j=0; j<qa.qaData.get(i).nAnswerNum;j++){
//			System.out.println(qa.qaData.get(i).answers.get(j));
//		}
//	}
//	System.out.println("End");		
//	
//}
