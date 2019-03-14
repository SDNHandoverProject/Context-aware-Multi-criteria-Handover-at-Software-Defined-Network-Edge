package com.javamedia;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenActivity extends Activity implements SurfaceHolder.Callback 
{
	private final static String TAG = "ScreenActivity";
	SurfaceHolder holder;             /** surfaceView�Ĵ���ӿ� */
	private SurfaceView surfaceView;    
	
	private boolean running = true;     //����MyThread��run����
	private MyPlayThread t;
	
	private boolean playing = true ;     
	private boolean pausing = false ;      
	private boolean faster = false ;       
	private boolean stop = false ;     
	
	private int mFrameWidth = 352;
	private int mFrameHeight = 288;
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	private int step_size = 10;      //���ʱ�����Ĳ���

	WakeLock wl;
	private ProgersssDialog progersssDialog;         //ѭ���ȴ���ת����

	ByteBuffer buf = ByteBuffer.allocateDirect(1024);       //����һ��1024�ֽڵ�buffer����
	
	private String ServerIpAddress;
	private int ServerPort;
	private int LocalPort;
	
	private ImageButton play_image_button; 
	private ImageButton stop_image_button; 
	private ImageButton faster_image_button; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		//Log.i(TAG, "Create new activity!!!!!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen);
		
		getWindow().setBackgroundDrawableResource(R.drawable.lightgray);   /** ���õ�ǰwindow�ı���ɫ */
		
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		this.holder = surfaceView.getHolder();
		this.holder.addCallback(this);		
		
		//ȡ�ô���һ��Activity���д��ݹ�����Intent����  
        Intent intent = getIntent();  
        //��Intent���и���keyȡ��value  
        ServerIpAddress = intent.getStringExtra("ServerIpAddress"); 
        ServerPort = intent.getIntExtra("ServerPort", 60000);
        LocalPort = intent.getIntExtra("LocalPort", -1);
        
		NativeHandler.nativeReceiver_init(ServerIpAddress, ServerPort, LocalPort);    /** JNI: ���õײ�C�������ڳ�ʼ��Jrtplib */
        
    	new Thread(new NativeThread()).start();  //��ʼ�ײ�����ݴ����߳�
    	
    	try{	
    		//��ʼ�������л��㷨���߳�
			new Thread(new BitrateSwitchThread(InetAddress.getByName(ServerIpAddress) , LocalPort+2)).start();   //���������л��㷨
			//new Thread(new CollectFISDataThread(ScreenActivity.this)).start();    //����ģ���߼�ϵͳ�������ݲɼ����߳�
			
		}catch(UnknownHostException e){e.printStackTrace();} 
   	
    	t = new MyPlayThread();		//���������̣߳����ǲ���ʼ
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "Start new activity!!!!!");
		super.onStart();
	}
		
	@Override
	protected void onResume() {
		Log.i(TAG, "Resume new activity!!!!!");
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		Log.i(TAG, "onPause new activity!!!!!");
		super.onPause();
	}
			
	@Override
	protected void onDestroy() { Log.i(TAG, "onDestroy new activity&&&&&&&&&&&");
		super.onDestroy();
		NativeHandler.Decoder_release();    //ֹͣ������
		NativeHandler.nativeReceiver_stop();    //ֹͣrtp
	}
		
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3){
		Log.i(TAG, "surfaceChanged....");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0){
		Log.i(TAG, "surfaceCreated....");
		
		//��ȡ��Ļ(SurfaceView)���		
		mScreenWidth = holder.getSurfaceFrame().width();
		mScreenHeight = holder.getSurfaceFrame().height();
		int Buttons_topLine = (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2 + 10;   //���ǰ�ť�������� ����һ����ֵ��Ϊ�˺Ͳ��������뿪һ�����
		
		//�ȴ�תȦ
		progersssDialog = new ProgersssDialog(this);
		progersssDialog.setMsg("Loading");
		//progersssDialog.show();
		
		Canvas canvas = holder.lockCanvas(null);//��ȡ����   
        Paint mPaint = new Paint();   
        mPaint.setColor(Color.LTGRAY);   
        canvas.drawRect(new RectF(0,(mScreenHeight-(mFrameHeight*mScreenWidth)/mFrameWidth)/2,mScreenWidth , (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2) ,mPaint);   
        holder.unlockCanvasAndPost(canvas);//�����������ύ���õ�ͼ��   
        
        //���ð�ť
		play_image_button = (ImageButton)findViewById(R.id.button_play);
		play_image_button.setY(Buttons_topLine);
		play_image_button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				if(playing){
					play_image_button.setImageResource(R.drawable.pausing);
					playing = false ; pausing = true ;
					stop    = false ; faster  = false ;
				}
				else if(pausing){
					play_image_button.setImageResource(R.drawable.playing);
					playing = true ;  pausing = false ;
					stop    = false ; faster  = false ;
				}
		}});
		
		stop_image_button = (ImageButton)findViewById(R.id.button_stop);
		stop_image_button.setY(Buttons_topLine);
		stop_image_button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				playing = false ;  pausing = false;
				stop    = true ;   faster  = false ; 
		}});
		
		faster_image_button = (ImageButton)findViewById(R.id.button_faster);
		faster_image_button.setY(Buttons_topLine);
		faster_image_button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				playing = false ;  pausing = false;
				stop    = false ;  faster  = true ;
		}});
        
		t.start(); //��ʼ�����߳�
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0){
		Log.i(TAG, "surfaceDestroyed.....");
		if (t.isAlive()){
			running = false;
		}
	}

	
	private class NativeThread implements Runnable
	{
		public void run() {
			//��ʼ�ײ�������ݵ��߳�
			NativeHandler.nativeReceiver_start();
		}
	}
	
	/**
	 * ���Ƹ���MyView�Ļ�ͼ�߳�
	 */
	private class MyPlayThread extends Thread {
		Canvas can = null;
		public void run() {
			
			mFrameWidth = NativeHandler.get_W(); 
			mFrameHeight = NativeHandler.get_H();
			while(mFrameWidth == -1 || mFrameHeight == -1 ){
				try{sleep(200);} catch (InterruptedException e) {e.printStackTrace();}	
				mFrameWidth = NativeHandler.get_W();
				mFrameHeight = NativeHandler.get_H();
			} //��ȡȥ����֡�Ŀ��
			
			byte[] mPixel = new byte[mFrameWidth * mFrameHeight * 2];
			int i = mPixel.length;  //��ʼ���������
	        for(i=0; i<mPixel.length; i++){
	        	mPixel[i]=(byte)0x00;
	        }
	        
			ByteBuffer buffer = ByteBuffer.wrap(mPixel);
			Bitmap videoBit = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Config.RGB_565);
			
			while(NativeHandler.get_FrameQueue_C() < 100){ //���nalu����С��50���͵ȴ�
				try{Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			progersssDialog.dismiss();	
			
			while (running)
			{	//System.out.println(mFrameWidth+" X "+mFrameHeight);
				if(playing)  //���ݰ�ť״̬
				{   
					//System.out.println("draw a Bitmap............................");
					//��ȡ����������
					int byteNum = NativeHandler.get_a_DecodedFrame(mPixel);
					if(byteNum == -1){//����-1��ʾ�����ܻ�ȡһ֡
						try {Thread.sleep(100);} catch (InterruptedException e){e.printStackTrace();}
						continue;
					}
						
					buffer.mark();
					videoBit.copyPixelsFromBuffer(buffer);
					buffer.reset();
					
					can = holder.lockCanvas();
					if (can != null){
						can.save();
						can.drawBitmap(videoBit,new Rect(0, 0, mFrameWidth , mFrameHeight),
												new Rect(0, (mScreenHeight-(mFrameHeight*mScreenWidth)/mFrameWidth)/2, mScreenWidth , (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2 ), null);
						can.restore();
					}
					holder.unlockCanvasAndPost(can);
					
				}
				else if(pausing)
				{
					//��ʱ����ֹͣ�ڵ�ǰ��֡��������ˢ�£��ٴε���Լ���ִ�У��������ˢ�²���
				}
				else if(faster)
				{
					playing = true ;  pausing = false;
					stop    = false ;   faster  = false ; 
					
					int byteNum = NativeHandler.get_a_Decoded_jumped_Frame(mPixel, step_size);   //����step_size��֡����ʾ
					if(byteNum == -1){//����-1��ʾ�����ܻ�ȡһ֡
						try {Thread.sleep(100);} catch (InterruptedException e){e.printStackTrace();}
						continue;
					}
						
					buffer.mark();
					videoBit.copyPixelsFromBuffer(buffer);
					buffer.reset();
					
					can = holder.lockCanvas();
					if (can != null){
						can.save();
						can.drawBitmap(videoBit,new Rect(0, 0, mFrameWidth , mFrameHeight),
												new Rect(0, (mScreenHeight-(mFrameHeight*mScreenWidth)/mFrameWidth)/2, mScreenWidth , (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2 ), null);
						can.restore();
					}
					holder.unlockCanvasAndPost(can);
				}
				else if(stop)  //�˳�����
				{
					finish();//ֻ��activity��ջ��������Դ��û�м�ʱ�ͷ�
				}
				
				try {Thread.sleep(35);} catch (InterruptedException e){e.printStackTrace();} //�ȴ�
			}
		}
	}
	
}
