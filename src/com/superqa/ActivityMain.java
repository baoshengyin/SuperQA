package com.superqa;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ActivityMain extends Activity {

	public ListView list;
	
	static String strMainQ;
	static int curQuestionIndex;
	static Questions qa = new Questions();
	
	private Dialog mDialog;
	boolean bShow = false; 
	

	// 创建消息接收器
    MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();
    IntentFilter mfilter=new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题 
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
        
        setContentView(R.layout.activity_main);
        
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);//自定义布局赋值 
       
        
        SuperQAToolkit.handle = this;
        
        // 设置进度显示图标
        setProgressBarIndeterminateVisibility(false);
        mDialog = new AlertDialog.Builder(this).create();
	    

        // 关联问题输入控件 
        final EditText	editAsk = (EditText)findViewById(R.id.editAsk);
        final Button btnSearch = (Button)findViewById(R.id.btnSearch);
        
        // 注册消息接收器
        mfilter.addAction("ACT_DOWNLOADQUESTION_OK");
        registerReceiver(mReceiver, mfilter);
        
        // 点击【检索】按钮
        btnSearch.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				String strAsk = editAsk.getText().toString();
				////strAsk = "钱学森";
				if(strAsk.length() > 0){
					strMainQ = strAsk;
					
					// 彩蛋
					if(strMainQ.compareTo("作者")==0 || strMainQ.compareTo("author")==0){
						SuperQAToolkit.showInfo("作者简介", "程程: 这个软件是俺爹开发的！\n涵涵: 阿爹的邮箱 是BaoShengYin@yahoo.com.cn","谢谢程程&涵涵");
						editAsk.setText("");
						return;
					}
					
					// connect Internet?
					if(!SuperQAToolkit.httpTest())
						return;
					
					// 显示等待状态
					setProgressBarIndeterminateVisibility(true);
					showWaitingDlg(true);

					// 下载问题清单	
					Thread download = new DownloadProcess();
					download.start();
				}
			}
        	
        });
        
        //点击【问题】列表
        list = (ListView) findViewById(R.id.listQuestionView);
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				curQuestionIndex = arg2;
				arg1.setBackgroundDrawable(getResources().getDrawable(R.drawable.bk_item));
				
				showWaitingDlg(true);
				Thread download = new GetAnswerProcess();
				download.start();
			}
		});
        
        if(qa.nQuestionNum > 0)
        	showQuestionList();                
    }
    
    public int showQuestionList(){
    	//生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qa.qaData.size();i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_action_search);//图像资源的ID
        	
        	String strTitle =  "<font color='#606060'>Q"+(i+1)+".</font>"+
        			SuperQAToolkit.makeNull(qa.qaData.get(i).strQuestion);
        	strTitle = strTitle.replace("<em>", "<font color='#a00000'>");
        	strTitle = strTitle.replace("</em>", "</font>");
        	map.put("ItemTitle", Html.fromHtml(strTitle)); //
        	
        	String strText =  qa.qaData.get(i).strIntr+"";
        	strText = strText.replace("<em>", "<font color='#a00000'>");
        	strText = strText.replace("</em>", "</font>");
        	map.put("ItemText", Html.fromHtml(strText));
        	
        	map.put("ItemInfo", SuperQAToolkit.makeNull(qa.qaData.get(i).strDatetime));
        	listItem.add(map);
        }    
        
        if(listItem.size() == 0)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
	    	map.put("ItemTitle", "未检索到答案，或许网络速度太慢");
	    	listItem.add(map);
        }
        
    	//生成适配器的Item和动态数组对应的元素，这里用SimpleAdapter作为ListView的数据源
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//数据源 
            R.layout.mainview_item,//ListItem的XML实现
            //动态数组与ImageItem对应的子项        
            new String[] {"ItemImage","ItemTitle", "ItemText", "ItemInfo"}, 
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText, R.id.ItemInfo}
        );
       
        //添加并且显示
        list.setAdapter(listItemAdapter);
        
    	return 0;
    }
    
    // 接收界面更新消息
    public class MyBroadcastReceiver extends BroadcastReceiver {
    	public void onReceive(Context context, Intent intent){
    		mDialog.hide();
    		showQuestionList();
    		setProgressBarIndeterminateVisibility(false);
    		SuperQAToolkit.showTip("检索到相关问题"+qa.nQuestionNum+"个,\n请点击查看详细问题答案。");
    		unregisterReceiver(mReceiver);
    	}
    }
    

    // 下载具体问题及答案线程
    class GetAnswerProcess extends Thread{
    	public void run(){
    		qa.loadAnswers(curQuestionIndex);
    		
    		Intent intent = new Intent();
			intent.setClass(ActivityMain.this, ActivityAnswer.class);
			startActivity(intent);
    	}
    }

    // 下载相关问题线程
    class DownloadProcess extends Thread{
    	public void run(){
    		qa.startAsk(strMainQ);
    		
    		Intent it = new Intent("ACT_DOWNLOADQUESTION_OK");
    		sendBroadcast(it);
    	}
    }
    
     
    public void showWaitingDlg(boolean bShow){
	    if(bShow){
	    	mDialog.show();
	    	//注意此处要放在show之后 否则会报异常
	    	mDialog.setContentView(R.layout.loading_process_dialog_color);
	    }else{
	    	mDialog.hide();
	    }
	    	
    }
    
//    protected void onDraw(Canvas canvas) {
//    	Paint paint = new Paint();
//    	paint.setColor(Color.rgb(130, 0, 0));
//        
//    	canvas.drawText("你不知道的我知道", 10, 100, paint);
//        //super.onDraw(canvas);
//    }

//public static void main(String[] args) throws Exception {
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
//	//
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
//}	
}
