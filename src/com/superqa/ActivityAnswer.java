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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������ 
        setContentView(R.layout.activity_answer);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//�Զ��岼�ָ�ֵ
        
        setTitle("���Ͻ��");
        
        qaData = ActivityMain.qa.qaData.get(ActivityMain.curQuestionIndex);
        showAnswerList(qaData);

        // ��������桿��ť
        TextView btnSave = (TextView)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				QAFilesManage.saveQAFile(qaData);
			}
        });

        // ������򿪡���ť
        TextView btnLoad = (TextView)findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
	    		Intent intent = new Intent();
				intent.setClass(ActivityAnswer.this, ActivityFileList.class);
				startActivity(intent);
				
			}
        });

        // ��������ء���ť
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
	    
    // �����ֻ��ϵķ��ؼ�
    public boolean onKeyDown(int keyCode, KeyEvent event){

    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		finish();
			
			SuperQAToolkit.handle.showWaitingDlg(false);
    	}
		return false;
    }
    

    public int showAnswerList(QAData qaData){
    	// �����ʾ
        answerInfo = (TextView)findViewById(R.id.answerInfo);
        int nAnswers = qaData.nAnswerNum;
        String strQ = qaData.strQuestion;
        answerInfo.setText(Html.fromHtml("<font color='#ffffff'><b>����:</b>"+strQ+"</font><font color='#e0e0e0'>("+Integer.valueOf(nAnswers)+")</font>"));
    	
    	//��Layout�����ListView
        list = (ListView) findViewById(R.id.listAnswerView);
    	//���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qaData.nAnswerNum;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	////map.put("aItemImage", R.drawable.ic_action_search);//ͼ����Դ��ID
        	String strTitle = "<font color='#606060'>�ش�"+(i+1)+
        			": </font><font color='#0000BB'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAnswer)+"</font> | "+
        			"<font color='#a0a0a0'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAuthorLevel)+"</font>";
        	map.put("aItemTitle", Html.fromHtml(strTitle));
        	map.put("aItemText", Html.fromHtml(qaData.answers.get(i).strContent+""));
        	map.put("aItemInfo",
        			"֧��"+
        			qaData.answers.get(i).nOkPoints + " | "+
        			qaData.strDatetime);
        	listItem.add(map);
        }
        
        
    	//������������Item�Ͷ�̬�����Ӧ��Ԫ�أ�������SimpleAdapter��ΪListView������Դ
        //�����Ŀ���ֱȽϸ��ӣ����Լ̳�BaseAdapter�������Լ�������Դ��
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//����Դ 
            R.layout.answerview_item,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"aItemImage","aItemTitle", "aItemText", "aItemInfo"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.aItemImage,R.id.aItemTitle,R.id.aItemText, R.id.aItemInfo}
        );
       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
    	return 0;
    }
    
    public void onConfigurationChanged(Configuration newConfig){
   	 
    	super.onConfigurationChanged(newConfig);
    	SuperQAToolkit.showTip("��ӭʹ�á�֪���� V1.0");
    	
    }

        
}

//public static void main(String[] args) throws MalformedURLException {
//
//	String strMainQ = "��С������";
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
