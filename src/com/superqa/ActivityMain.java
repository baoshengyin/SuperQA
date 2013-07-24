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
	

	// ������Ϣ������
    MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();
    IntentFilter mfilter=new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������ 
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
        
        setContentView(R.layout.activity_main);
        
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);//�Զ��岼�ָ�ֵ 
       
        
        SuperQAToolkit.handle = this;
        
        // ���ý�����ʾͼ��
        setProgressBarIndeterminateVisibility(false);
        mDialog = new AlertDialog.Builder(this).create();
	    

        // ������������ؼ� 
        final EditText	editAsk = (EditText)findViewById(R.id.editAsk);
        final Button btnSearch = (Button)findViewById(R.id.btnSearch);
        
        // ע����Ϣ������
        mfilter.addAction("ACT_DOWNLOADQUESTION_OK");
        registerReceiver(mReceiver, mfilter);
        
        // �������������ť
        btnSearch.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				String strAsk = editAsk.getText().toString();
				////strAsk = "Ǯѧɭ";
				if(strAsk.length() > 0){
					strMainQ = strAsk;
					
					// �ʵ�
					if(strMainQ.compareTo("����")==0 || strMainQ.compareTo("author")==0){
						SuperQAToolkit.showInfo("���߼��", "�̳�: �������ǰ��������ģ�\n����: ���������� ��BaoShengYin@yahoo.com.cn","лл�̳�&����");
						editAsk.setText("");
						return;
					}
					
					// connect Internet?
					if(!SuperQAToolkit.httpTest())
						return;
					
					// ��ʾ�ȴ�״̬
					setProgressBarIndeterminateVisibility(true);
					showWaitingDlg(true);

					// ���������嵥	
					Thread download = new DownloadProcess();
					download.start();
				}
			}
        	
        });
        
        //��������⡿�б�
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
    	//���ɶ�̬���飬��������
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<qa.qaData.size();i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("ItemImage", R.drawable.ic_action_search);//ͼ����Դ��ID
        	
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
	    	map.put("ItemTitle", "δ�������𰸣����������ٶ�̫��");
	    	listItem.add(map);
        }
        
    	//������������Item�Ͷ�̬�����Ӧ��Ԫ�أ�������SimpleAdapter��ΪListView������Դ
        SimpleAdapterEx listItemAdapter = new SimpleAdapterEx(this,listItem,//����Դ 
            R.layout.mainview_item,//ListItem��XMLʵ��
            //��̬������ImageItem��Ӧ������        
            new String[] {"ItemImage","ItemTitle", "ItemText", "ItemInfo"}, 
            //ImageItem��XML�ļ������һ��ImageView,����TextView ID
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText, R.id.ItemInfo}
        );
       
        //��Ӳ�����ʾ
        list.setAdapter(listItemAdapter);
        
    	return 0;
    }
    
    // ���ս��������Ϣ
    public class MyBroadcastReceiver extends BroadcastReceiver {
    	public void onReceive(Context context, Intent intent){
    		mDialog.hide();
    		showQuestionList();
    		setProgressBarIndeterminateVisibility(false);
    		SuperQAToolkit.showTip("�������������"+qa.nQuestionNum+"��,\n�����鿴��ϸ����𰸡�");
    		unregisterReceiver(mReceiver);
    	}
    }
    

    // ���ؾ������⼰���߳�
    class GetAnswerProcess extends Thread{
    	public void run(){
    		qa.loadAnswers(curQuestionIndex);
    		
    		Intent intent = new Intent();
			intent.setClass(ActivityMain.this, ActivityAnswer.class);
			startActivity(intent);
    	}
    }

    // ������������߳�
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
	    	//ע��˴�Ҫ����show֮�� ����ᱨ�쳣
	    	mDialog.setContentView(R.layout.loading_process_dialog_color);
	    }else{
	    	mDialog.hide();
	    }
	    	
    }
    
//    protected void onDraw(Canvas canvas) {
//    	Paint paint = new Paint();
//    	paint.setColor(Color.rgb(130, 0, 0));
//        
//    	canvas.drawText("�㲻֪������֪��", 10, 100, paint);
//        //super.onDraw(canvas);
//    }

//public static void main(String[] args) throws Exception {
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
