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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������ 
        setContentView(R.layout.activity_filecontent);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_back);//�Զ��岼�ָ�ֵ

        setTitle("�鿴���������");
        
        showAnswerList();

        // �����ɾ������ť
        TextView btnDel = (TextView)findViewById(R.id.btnDelFile);
        btnDel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(SuperQAToolkit.confirmDialog(ActivityFileContent.this, "ȷ��ɾ�����ղ����ݣ�"))
				{
					QAFilesManage.removeQAFile(ActivityFileList.curFileIndex);
					ActivityFileContent.this.finish();
				}
			}
        });

        // ��������ء���ť
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
	    
    // �����ֻ��ϵķ��ؼ�
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
    	// �����ʾ
        TextView answerInfo = (TextView)findViewById(R.id.askInfo);
        int nAnswers = qaData.nAnswerNum;
        String strQ = qaData.strQuestion;
        answerInfo.setText(Html.fromHtml("<font color='#ffffff'><b>����:</b>"+strQ+"</font><font color='#e0e0e0'>("+Integer.valueOf(nAnswers)+")</font>"));
    	
    	//��Layout�����ListView
        list = (ListView) findViewById(R.id.listFileContent);
    	//���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qaData.nAnswerNum;i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	////map.put("aItemImage", R.drawable.ic_action_search);//ͼ����Դ��ID
        	String strTitle = "<font color='#606060'>�ش�"+(i+1)+
        			": </font><font color='#0000BB'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAnswer)+"</font> | "+
        			"<font color='#a0a0a0'>"+SuperQAToolkit.makeNull(qaData.answers.get(i).strAuthorLevel)+"</font>";
        	map.put("fItemTitle", Html.fromHtml(strTitle));
        	map.put("fItemText", Html.fromHtml(qaData.answers.get(i).strContent+""));
        	map.put("fItemInfo",
        			"֧��"+
        			qaData.answers.get(i).nOkPoints + " | "+
        			qaData.strDatetime);
        	listItem.add(map);
        }
        
        
    	//������������Item�Ͷ�̬�����Ӧ��Ԫ�أ�������SimpleAdapter��ΪListView������Դ
        //�����Ŀ���ֱȽϸ��ӣ����Լ̳�BaseAdapter�������Լ�������Դ��
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//����Դ 
            R.layout.filecontentview_item,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"fItemImage","fItemTitle", "fItemText", "fItemInfo"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.fItemImage,R.id.fItemTitle,R.id.fItemText, R.id.fItemInfo}
        );
       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
    	return nAnswers;
    }
    
}
