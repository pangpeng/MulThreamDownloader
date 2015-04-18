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
	//子线程更新主线程UI 自定义消息处理UiHandler
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
    		//1.文件保存sdcard 根目录
    		File saveDir = Environment.getExternalStorageDirectory();
    		//2.保存文件
    		download(path,saveDir);
    		downBut.setEnabled(false);//按钮状态
    		stopButton.setEnabled(true);
    	}else{
    		Toast.makeText(this, "SDcard Error!", 1);
    	}
    }
    //stop button listener
    public void stopButlistener(View v){
    	task.exitDown(); // 停止下载
    	downBut.setEnabled(true);//按钮状态
		stopButton.setEnabled(false);
    }
    /** 
     * @param path  网络下载路径
     * @param saveDir	保存本地的路径	
     * ThreadCount 开启的线程数量
     */
    private void download(String path, File saveDir) {//运行在主线程-导致主线程卡死
        task = new DownloadTask(path,saveDir);
		new Thread(task).start();
	}
    
    //启动workThread完成下载任务,子线程处理主线程UI方法 Handler
    private final class DownloadTask implements Runnable{
    	private int ThreadCount = 3;
    	private String path;
    	private File saveDir;
    	private FileDownloader loader;
    	
		public DownloadTask(String path, File saveDir) {
			this.path = path;
			this.saveDir = saveDir;
		}
		//exitDown()点击停止下载-退出下载
		public void exitDown(){
			if(loader != null) loader.exitDown();
		}
		
		public void run() {
	    	try {
	    		loader = new FileDownloader(getApplicationContext(), path,saveDir, ThreadCount);
	    		int max = loader.getFileSize();//得到要下载文件总大小
	    		progressBar.setMax(max);//设置进度条最大刻度
	    		loader.download(new DownloadProgressListener(){

					public void onDownloadSize(int size) {
						//更新主线程UI
						Message msg = new Message();
						msg.what = 1;//消息标识
						msg.getData().putInt("size", size);
						handler.sendMessage(msg);
						
					}});
	    	} catch (Exception e) {
	    			e.printStackTrace();
	    			//下载失败发送消息
	    			handler.sendMessage(handler.obtainMessage(-1));
	    	}
		}
    }
    //主线程创建handler
  	//-子线程发送消息handler.sendMessage()
  	//主线程重载handleMessage()处理消息
  	private final class UiHandler extends Handler{
  		public void handleMessage(Message msg) {
  			switch (msg.what) {
  			case 1:
  				int size = msg.getData().getInt("size");
  				progressBar.setProgress(size);//设置进度条进度
  			    //				20				     /			200		= 0.1		
  				float num = progressBar.getProgress()/progressBar.getMax();
  				int result = (int) (num*100);//百分比值
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
