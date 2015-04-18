package com.example.multhreamdownloader;

import java.io.File;
import net.download.DownloadProgressListener;
import net.download.FileDownloader;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button downBut,stopButton;
	private EditText editPath;
	private ProgressBar progressBar;
	private TextView textTitle;
	//���̸߳������߳�UI �Զ�����Ϣ����UiHandler
	private Handler handler = new UiHandler();
	private DownloadTask task;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downBut = (Button) findViewById(R.id.downBut);
        stopButton = (Button) findViewById(R.id.StopButton);
        editPath = (EditText) findViewById(R.id.editPath);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textTitle = (TextView) findViewById(R.id.textTitle);
        
    }
    //down button listener
    public void downButlistener(View v){
    	String path = editPath.getText().toString();
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
    		//1.�ļ�����sdcard ��Ŀ¼
    		File saveDir = Environment.getExternalStorageDirectory();
    		//2.�����ļ�
    		download(path,saveDir);
    		downBut.setEnabled(false);//��ť״̬
    		stopButton.setEnabled(true);
    	}else{
    		Toast.makeText(this, "SDcard Error!", 1);
    	}
    }
    //stop button listener
    public void stopButlistener(View v){
    	task.exitDown(); // ֹͣ����
    	downBut.setEnabled(true);//��ť״̬
		stopButton.setEnabled(false);
    }
    /** 
     * @param path  ��������·��
     * @param saveDir	���汾�ص�·��	
     * ThreadCount �������߳�����
     */
    private void download(String path, File saveDir) {//���������߳�-�������߳̿���
        task = new DownloadTask(path,saveDir);
		new Thread(task).start();
	}
    
    //����workThread�����������,���̴߳������߳�UI���� Handler
    private final class DownloadTask implements Runnable{
    	private int ThreadCount = 3;
    	private String path;
    	private File saveDir;
    	private FileDownloader loader;
    	
		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}
		//exitDown()���ֹͣ����-�˳�����
		public void exitDown(){
			if(loader != null) loader.exitDown();
		}
		
		public void run() {
	    	try {
	    		loader = new FileDownloader(getApplicationContext(), path,saveDir, ThreadCount);
	    		int max = loader.getFileSize();//�õ�Ҫ�����ļ��ܴ�С
	    		progressBar.setMax(max);//���ý��������̶�
	    		loader.download(new DownloadProgressListener(){

					public void onDownloadSize(int size) {
						//�������߳�UI
						Message msg = new Message();
						msg.what = 1;//��Ϣ��ʶ
						msg.getData().putInt("size", size);
						handler.sendMessage(msg);
						
					}});
	    	} catch (Exception e) {
	    			e.printStackTrace();
	    			//����ʧ�ܷ�����Ϣ
	    			handler.sendMessage(handler.obtainMessage(-1));
	    	}
		}
    }
    //���̴߳���handler
  	//-���̷߳�����Ϣhandler.sendMessage()
  	//���߳�����handleMessage()������Ϣ
  	private final class UiHandler extends Handler{
  		public void handleMessage(Message msg) {
  			switch (msg.what) {
  			case 1:
  				int size = msg.getData().getInt("size");
  				progressBar.setProgress(size);//���ý���������
  			    //				20				     /			200		= 0.1		
  				float num = progressBar.getProgress()/progressBar.getMax();
  				int result = (int) (num*100);//�ٷֱ�ֵ
  				textTitle.setText(result+"%");
  				if(progressBar.getProgress() == progressBar.getMax())
  				Toast.makeText(getApplicationContext(), "dwonload finish!", 1).show();
  				
  				break;
  			case -1:
  				Toast.makeText(getApplicationContext(), "dwonload fault!", 1).show();
  				
  				break;
  			default:
  				break;
  			}	
  		}
  	}
}
