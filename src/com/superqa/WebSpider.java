package com.superqa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;


/**
 * QA�������洢�ṹ
 * �û����һ������ MainASK-->�ҵ����ͬ������Questions-->ÿ�������Ӧ����ش���QAData-->ÿ������ش��Ӧ�������Answer
 * @author ybs
 *
 */

// ��¼ĳһ���ش���
class Answer implements Serializable{
	public boolean bBestAnswer;
	public boolean bTopAnswer;
	public String strAuthorLevel;
	public int nOkPoints;
	String strAnswer;
	String strContent;
	public String strSaveDatetime;
		
	public String toString(){
		return 	"bBestAnswer:"+bBestAnswer+"|"+
				"bTopAnswer:"+bTopAnswer+"|"+
				"strAnswer:"+strAnswer+"|"+
				"strAuthorLevel:"+strAuthorLevel+"|"+
				"nOkPoints:"+nOkPoints+"\n"+
				"strContent:"+strContent;
	}
}

// ���⼰��Ӧ�Ķ���ش���
class QAData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String strSource;
	public String strQuestion;
	public String strQuestionDetail;
	public String strIntr;
	public String strDetailUrl;
	public String strDatetime;
	public int nAnswerNum;
	public Vector<Answer> answers = new Vector<Answer>();
	
}

// ���⼰�������
class Questions implements Serializable{
	public String strMainQuestion;
	public Vector<QAData> qaData = new Vector<QAData>();
	public int nQuestionNum;
	
//	//*****DEBUG
//	public String html;
//	public String url;
//	//*****DEBUG
	
	/**
	 * ��ʼ����
	 * @param strMainQ
	 * @return
	 */
	public int startAsk(String strMainQ){
		strMainQuestion = strMainQ;
		qaData.removeAllElements();
		new ParserBaiduZhidao().startAsk(strMainQ, this);
		nQuestionNum = qaData.size();
		return nQuestionNum;
	}
	
	// ����������
	public boolean addQuestion(String strSource, String strQ, String strIntr, String strDetailUrl, String time, int nANum){
		QAData qa = new QAData();
		qa.strSource = strSource;
		qa.strQuestion = strQ;
		qa.strDetailUrl = strDetailUrl;
		qa.strIntr = strIntr;
		qa.strDatetime = time;
		qaData.add(qa);
		return true;
	}
	
	// װ��ĳһ����Ĳο��ش���
	public int loadAnswers(int index){
		
		if(qaData == null || index < 0 || index > qaData.size())
			return 0;
		if(qaData.get(index).answers.size() > 0)
			return qaData.get(index).answers.size();
		
		//
		String strUrl = qaData.get(index).strDetailUrl;
		new ParserBaiduZhidao().loadAnswers(strUrl, qaData.get(index));
		
		qaData.get(index).nAnswerNum = qaData.get(index).answers.size();
		
		return qaData.get(index).nAnswerNum;
	}
	
	public int getCount(){
		return qaData.size();
	}
	
}

class QAFileInfo{
	public int id;
	public String strFilename;
	public String strTitle;
	public String strSaveDatetime;
}

// �Ա��ر����������ļ����й���
// ��ȡ�����ļ��б� ��base64���������ƣ�.qa
//
class QAFilesManage{
	
	static public Vector<QAFileInfo> qaFileList = new Vector<QAFileInfo>();
	
	// ʹ�ñ�����������ǰ��Ҫ���ȵ��ø÷���
	static public Vector<QAFileInfo> getQAFileList(){
		qaFileList.removeAllElements();
		File[] files = new File(SuperQAToolkit.strAppPath).listFiles();
		for(int i = 0; i < files.length; i++){
			QAFileInfo qaf = new QAFileInfo();
			qaf.id = i;
			qaf.strFilename = Base64.unescape(files[i].getName());
			////SuperQAToolkit.showTip(qaf.strFilename);
			qaf.strTitle = qaf.strFilename; 
			qaf.strSaveDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(files[i].lastModified()));
			qaFileList.add(qaf);
		}
		
		return qaFileList;
	}
	
	// װ��ָ����ŵ��ļ�����
	static public QAData loadQAFile(String strTitle){
		for(int i = 0 ; i<= qaFileList.size(); i++){
			if(qaFileList.get(i).strTitle.compareTo(strTitle) == 0){
				qaFileList.remove(i);
				break;
			}
		}
		return SuperQAToolkit.loadQADataFromFile(Base64.toUtf8String(strTitle));
	}
	
	static public QAData loadQAFile(int index){
		return loadQAFile(qaFileList.get(index).strTitle);
	}

	// ɾ��ָ�������ļ�
	static public boolean removeQAFile(String strTitle){
		for(int i = 0 ; i<= qaFileList.size(); i++){
			if(qaFileList.get(i).strTitle.compareTo(strTitle) == 0){
				qaFileList.remove(i);
				break;
			}
		}
		File file = new File(SuperQAToolkit.strAppPath+Base64.toUtf8String(strTitle));
		return file.delete();
	}
	// ɾ��ָ�������ļ�
	static public boolean removeQAFile(int index){
		return removeQAFile(qaFileList.get(index).strTitle);
	}
	
	// ����������𰸣�ͬ�������ļ���
	static public boolean saveQAFile(QAData qad){
		qad.strQuestion = qad.strQuestion.replaceAll("<em>", "");
		qad.strQuestion = qad.strQuestion.replaceAll("</em>", "");
		return SuperQAToolkit.saveQADataToFile(Base64.toUtf8String(qad.strQuestion), qad);
	}		
}

/**
 * ������������������
 * @author ybs
 *
 */
class SuperQAToolkit{
	
	static final int MAX_HTML_BUFSIZE = 1024*50; 
	static public ActivityMain handle;
	static public String strAppPath = Environment.getExternalStorageDirectory().getPath()+"/superQA/";
	
	public static boolean isNetworkAvailable() {
		Activity mActivity = handle;
		Context context = mActivity.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * ��������Ƿ����
	 */
	public static boolean httpTest() {
		if (!isNetworkAvailable()) {
			showInfo("��ʾ","���粻�ܷ���...","ȷ��");
			return false;
		}
		return true;
	}
	
	public static String makeNull(String str){
		return str==null?"-":str;
	}
	
	public static void showInfo(String strTitle, String strText, String strButton) {
		new AlertDialog.Builder(SuperQAToolkit.handle)
				.setTitle(strTitle)
				.setMessage(strText)
				.setPositiveButton(strButton,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();

	}
	
	public static void showTip(String str){
		Toast.makeText(handle, str, Toast.LENGTH_SHORT).show();
	}
	
	static public boolean saveToFile(String strFile, String strText){
		String status = Environment.getExternalStorageState();
		String strPath = strAppPath;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				new File(strPath+strFile).createNewFile();
				BufferedWriter file  = new BufferedWriter(new FileWriter(strPath+strFile));
				file.write(strText);
				file.close();
			} catch (IOException e) {
				Toast.makeText(SuperQAToolkit.handle, "Save Logfile Error:"+strPath+strFile, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}	
			return true;
		} else {
			return false;
		}
	}
	
	static public boolean saveQADataToFile(String strFile, QAData qad){
		String status = Environment.getExternalStorageState();
		String strPath = strAppPath;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				new File(strPath).mkdir();
				ObjectOutputStream ow = new ObjectOutputStream(new FileOutputStream(strPath+strFile));
				ow.writeObject(qad);
				ow.close();
			} catch (IOException e) {
				Toast.makeText(SuperQAToolkit.handle, "Save file Error:"+strPath+strFile, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}	
			return true;
		} else {
			return false;
		}
	}
	
	static public QAData loadQADataFromFile(String strFile){
		String status = Environment.getExternalStorageState();
		String strPath = strAppPath;
		QAData qad = null;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				ObjectInputStream or = new ObjectInputStream(new FileInputStream(strPath+strFile));
				qad = (QAData)(or.readObject());
				or.close();
			} catch (IOException e) {
				Toast.makeText(SuperQAToolkit.handle, "Load file Error:"+strPath+strFile, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
			return qad;
		} else {
			return null;
		}
	}
    	
	/**
	 * 
	 * @param pageUrl
	 * @return
	 */
	static public String downloadPageEx(String strURL, String strCharset) {
		try {
			
			System.out.println("dowloading:"+strURL);
			
			URL pageUrl = new URL(strURL);
			
			// Open connection to URL for reading.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					pageUrl.openStream(), strCharset));

			// Read page into buffer.
			String line;
			int size = 0;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line+"\n");
				size += line.length();
				if(size > MAX_HTML_BUFSIZE)
					break;
			}
			return pageBuffer.toString();
						
		} catch (Exception e) {
			System.out.println("Connect Internet Error:"+e);
			Toast.makeText(SuperQAToolkit.handle, "Connect Internet Error:"+e, Toast.LENGTH_SHORT).show();
		}
		return "";
	}
	
	
	public static String downloadPage(String urlStr, String charset){
		URL url = null;
		HttpURLConnection httpConn = null;
		InputStream in = null;
		BufferedReader reader = null;
		try{
			url = new URL(urlStr);
			httpConn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			in = httpConn.getInputStream();

			// Open connection to URL for reading.
			reader = new BufferedReader(new InputStreamReader(in, charset));

			// Read page into buffer.
			String line;
			int size = 0;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line+"\n");
				size += line.length();
				if(size > MAX_HTML_BUFSIZE)
					break;
			}
			return pageBuffer.toString();
		}
		catch (Exception e){
			System.out.println("Connect Internet Error:"+e);
		}finally{
			try{
				httpConn.disconnect();
				reader.close();
				in.close();
			}catch(Exception e){
				
			}
		}	 
		return "";
	}

	/**
	 * ͬʱ���������ִ������ҵ��߷���
	 * @param str
	 * @param s1
	 * @param s2
	 * @param pos
	 * @return
	 */
	static int indexOfFirstPos(String str, String s1, String s2, int pos){
		int ifrom = -1;
		// Other Answers 
		int ifrom1 = str.indexOf(s1, pos);
		int ifrom2 = str.indexOf(s2, pos);
		if(ifrom1 >= 0)
			ifrom = ifrom1;
		if(ifrom2 < ifrom1 && ifrom2 >= 0)
			ifrom = ifrom2;
		return ifrom;
	}
	
	/**
	 * ȡ��������ʶ���м������(������ʶ�ɺ���)
	 * @param str
	 * @param strMarkFrom
	 * @param strMarkTo
	 * @return
	 */
	static String getContent(String str, String strMarkFrom, String strMarkTo){
		
			int ifrom = str.indexOf(strMarkFrom, 0);
			int ito = str.indexOf(strMarkTo, ifrom+strMarkFrom.length());
			if(ifrom >= 0 && ito < 0)
				return str.substring(ifrom+strMarkFrom.length());
			if(ifrom >= 0 && ito > ifrom)
				return str.substring(ifrom+strMarkFrom.length(), ito);
			
			return null;
	}
	
	/**
	 * ȡ��������ʶ���м������(���Ӷ�����ʶ��λ���ܣ�������ʶ�ɺ���)
	 * @param str
	 * @param strMarkFrom1
	 * @param strMarkFrom2
	 * @param strMarkTo
	 * @return
	 */
	static String getContentEx(String str, String strMarkFrom1, String strMarkFrom2, String strMarkTo){
		
		int ifrom1 = str.indexOf(strMarkFrom1, 0);
		if(ifrom1 < 0)
			return null;
		int ifrom2 = str.indexOf(strMarkFrom2, ifrom1+strMarkFrom1.length());
		int ito = str.indexOf(strMarkTo, ifrom2+strMarkFrom2.length());
		if(ifrom2 >= 0 && ito < 0)
			return str.substring(ifrom2+strMarkFrom2.length());
		if(ifrom2 >= 0 && ito > ifrom2)
			return str.substring(ifrom2+strMarkFrom2.length(), ito);
		
		return null;
	}
	
	static boolean _bConfirmDlg = false;
	static public boolean confirmDialog(Context context, String strTitle) {
		new AlertDialog.Builder(context).setTitle(strTitle) 
		     .setIcon(android.R.drawable.ic_dialog_info) 
		     .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() { 
		         public void onClick(DialogInterface dialog, int which) { 
		        	 _bConfirmDlg = true;
		         } 
		     }) 
		     .setNegativeButton("����", new DialogInterface.OnClickListener() { 
		         public void onClick(DialogInterface dialog, int which) {
		        	 _bConfirmDlg = false;
		         } 
		     }).show();
		 return _bConfirmDlg;
	 }
}

/**
 * �ٶ�֪�������⼰�𰸲ɼ���
 * @author ybs
 *
 */
class ParserBaiduZhidao{
	
	public static final String _urlBaiduZhidao = "http://zhidao.baidu.com/search?pn=0&ie=utf-8&rn=10&lm=0&fr=search&word=";
	
	
	Questions 	q;
	QAData 		qd;
	
	String strFromMark = "\"result-title\"";
	String strToMark = "</dd></dl>";
	
	/**
	 * �ڰٶ�֪���������������
	 * @param strMainQ
	 * @param question
	 * @return 	=0 �����ȡʧ�� 
	 * 			>0 ʵ�ʻ�ȡ�������������
	 */
	public int startAsk(String strMainQ, Questions question){
		q = question;
		// first page
		String strResult = SuperQAToolkit.downloadPage(_urlBaiduZhidao+Base64.toUtf8String(strMainQ), "GBK");
		
//		////******DEBUG
//		q.url = _urlBaiduZhidao+strMainQ;
//		q.html = strResult;
//		////******DEBUG
		
		//System.out.print(strResultBaiduZhidao);
		parserQuestionHtml(strResult);
		return q.getCount();
	}
	
	/**
	 * ����������������ҳ���ݲ���ȡ�������
	 * @param strHtml
	 * @return
	 */	
	private int parserQuestionHtml(String strHtml){
		if(strHtml == null || strHtml.length() <= 0 || q == null)
			return 0;
		int nRet = 0;
		int ifrom = strHtml.indexOf(strFromMark, 0);
		int ito = ifrom;
		while(ifrom >= 0){
			ito = strHtml.indexOf(strToMark, ifrom);
			if(ito > ifrom){
				String strQ = strHtml.substring(ifrom+strFromMark.length(), ito);
				parserQuestion(strQ);
				nRet++;
			}
			else
				break;
			ifrom = strHtml.indexOf(strFromMark, ito);
		}
		return nRet;
	}
	
	/**
	 * ��ȡÿ���������ϸ��Ϣ
	 * @param strQ
	 */
	private void parserQuestion(String strQ){
		if(strQ == null || strQ.length() <= 0 || q == null)
			return;
		
		q.addQuestion("Baidu", 
				SuperQAToolkit.getContentEx(strQ, "\">", ">", "</a>"), // title
				SuperQAToolkit.getContent(strQ, "\"result-info\">", "</dd>"), //strIntr, 
				SuperQAToolkit.getContent(strQ, "<a href=\"", "\""), //strDetaikUrl
				SuperQAToolkit.getContent(strQ, "</a>  - ", "  "), //time, 
				0);
	}
	
	/**
	 * ��ȡÿ�������ȫ���ش�
	 * @param strUrl
	 * @param qd
	 * @return
	 */
	public int loadAnswers(String strUrl, QAData qdata){
		int nRet = 0;
		qd = qdata;
				
		String strAnswerHtml = SuperQAToolkit.downloadPage(strUrl, "GBK");
		
		// baidu baike
		if(strUrl.indexOf("baike.baidu") >= 0){
			qd.nAnswerNum += parserBaikeAnswersHtml(strAnswerHtml);
		}
		else
			qd.nAnswerNum += parserAnswersHtml(strAnswerHtml);
				
		nRet = qd.nAnswerNum;
		return nRet;
	}
	
	private int parserBaikeAnswersHtml(String strHtml){
		if(strHtml == null || strHtml.length() <= 0 || qd == null)
			return 0;
				
		int nRet = 0;

		String strLexTitle = SuperQAToolkit.getContentEx(strHtml, "<h1 class=\"title", ">", "<");
		String strLexContent = SuperQAToolkit.getContentEx(strHtml, "<h1 class=\"title", "</h1>", "<dl class=\"lemma-ext-bottom nslog-area");
		
		if(strLexTitle == null || strLexContent == null)
			return 0;
		// ȥ���ٿ��е�<script>
		int 	s1 = strLexContent.indexOf("<script", 0), 
				s2 = strLexContent.indexOf("script>", s1+7);
		while(s1>=0 && s2>s1){
			if(s1 == 0)
				strLexContent = strLexContent.substring(s2+7);
			else
				strLexContent = strLexContent.substring(0, s1)+	strLexContent.substring(s2+7);
			s1 = strLexContent.indexOf("<script", 0); 
			s2 = strLexContent.indexOf("script>", s1+7);
		};

		int len = strLexContent.length();
		for (int i = 0; i < len / 2048; i++) {
			Answer ba = new Answer();
			if (i == 0)
				ba.strAnswer = strLexTitle;
			ba.strContent = strLexContent.substring(i * 2048, (i + 1) * 2048);
			// �����ضϵ�HTML��ʶ
			int m1 = ba.strContent.indexOf(">");
			int m2 = ba.strContent.indexOf("<");
			if(m1 >=0 && (m2<0 || m2>m1))
				ba.strContent = ba.strContent.substring(m1+1);
			qd.answers.add(ba);
			nRet++;
		}
		
		return nRet;
	}
	/**
	 * ���������ҳ
	 * @param strHtml
	 * @return
	 */
	private int parserAnswersHtml(String strHtml){
		if(strHtml == null || strHtml.length() <= 0 || qd == null)
			return 0;
				
		int nRet = 0;

		// ��ȡ��������
		qd.strQuestionDetail = SuperQAToolkit.getContentEx(strHtml,"question-content",">","<"); 				
		
		// Best Answer
		String strBestA = SuperQAToolkit.getContentEx(strHtml, "best-answer-content", "\"aContent\">", "</pre>");
		if(strBestA != null){
			Answer ba = new Answer();
			ba.strAnswer = SuperQAToolkit.getContentEx(strHtml, "bestreplyer.userhead.click", "user-name\">", "<");
			ba.strAuthorLevel = SuperQAToolkit.getContentEx(strHtml, "bestreplyer.icon.grade", ">", "<");
			ba.strContent = strBestA;
			String strOk = SuperQAToolkit.getContentEx(strHtml, ">��ͬ</div>", "fixed\">", "<");
			ba.nOkPoints = Integer.parseInt(strOk==null?"0":strOk);
			ba.bBestAnswer = true;
			ba.bTopAnswer = false;
			
			qd.answers.add(ba);
			nRet ++;
		}
		
		// Other Answers 
		String strOtherAMark1 = "\"replyer.username.click";
		String strOtherAMark2 = "��������";
		
		int ifrom = SuperQAToolkit.indexOfFirstPos(strHtml, strOtherAMark1, strOtherAMark2, 0);
		int ito = ifrom;
		while(ifrom >= 0){
			ito = strHtml.indexOf(">����<", ifrom);
			if(ito > ifrom){
				String strA = strHtml.substring(ifrom, ito);
				parserAnswer(strA);
				nRet++;
			}
			else
				break;
			ifrom = SuperQAToolkit.indexOfFirstPos(strHtml, strOtherAMark1, strOtherAMark2, ito);
		}
		return nRet;
	}
	
	/**
	 * ��ȡÿ���ش����ϸ��Ϣ
	 * @param strA
	 */
	private void parserAnswer(String strA){
		if(strA == null || strA.length() == 0)
			return;
		
		Answer an = new Answer();
		an.strAnswer = SuperQAToolkit.getContent(strA, "user-name\">", "<");
		an.strAuthorLevel = SuperQAToolkit.getContent(strA, "</span>\n", "\n");
		an.strContent = SuperQAToolkit.getContent(strA, "aContent\">", " </pre>");
		an.nOkPoints = Integer.parseInt(SuperQAToolkit.getContentEx(strA, ">��ͬ</div>", "fixed\">", "<"));
		an.bBestAnswer = false;
		an.bTopAnswer = false;
		qd.answers.add(an);
	}
	
}

// ����Web������
public class WebSpider{

	// ������
	public static void main(String[] args) throws MalformedURLException {

		String strMainQ = "��С������";
		
		System.out.println("*************************************");
		System.out.println("ASK:"+strMainQ);
		
		Questions qa = new Questions();
		qa.startAsk(strMainQ);
		
		System.out.println("====Total Questions"+qa.nQuestionNum+"====");
		//
		for(int i = 0; i<qa.qaData.size(); i++){
			System.out.println("[Question:"+i+"th]");
			System.out.println(qa.qaData.get(i).strQuestion);
			System.out.println(qa.qaData.get(i).strDetailUrl);
			System.out.println(qa.qaData.get(i).strIntr);
			System.out.println(qa.qaData.get(i).strDatetime);
			//
			qa.loadAnswers(i);
			System.out.println("----Total Answers:"+qa.qaData.get(i).nAnswerNum+"----");
			
			for(int j=0; j<qa.qaData.get(i).nAnswerNum;j++){
				System.out.println(qa.qaData.get(i).answers.get(j));
			}
		}
		System.out.println("End");		
		
	}
}
