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
	SurfaceHolder holder;             /** surfaceView的处理接口 */
	private SurfaceView surfaceView;    
	
	private boolean running = true;     //控制MyThread的run方法
	private MyPlayThread t;
	
	private boolean playing = true ;     
	private boolean pausing = false ;      
	private boolean faster = false ;       
	private boolean stop = false ;     
	
	private int mFrameWidth = 352;
	private int mFrameHeight = 288;
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	private int step_size = 10;      //快进时跳过的步长

	WakeLock wl;
	private ProgersssDialog progersssDialog;         //循环等待的转动环

	ByteBuffer buf = ByteBuffer.allocateDirect(1024);       //申请一个1024字节的buffer缓存
	
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
		
		getWindow().setBackgroundDrawableResource(R.drawable.lightgray);   /** 设置当前window的背景色 */
		
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		this.holder = surfaceView.getHolder();
		this.holder.addCallback(this);		
		
		//取得从上一个Activity当中传递过来的Intent对象  
        Intent intent = getIntent();  
        //从Intent当中根据key取得value  
        ServerIpAddress = intent.getStringExtra("ServerIpAddress"); 
        ServerPort = intent.getIntExtra("ServerPort", 60000);
        LocalPort = intent.getIntExtra("LocalPort", -1);
        
		NativeHandler.nativeReceiver_init(ServerIpAddress, ServerPort, LocalPort);    /** JNI: 调用底层C函数用于初始化Jrtplib */
        
    	new Thread(new NativeThread()).start();  //开始底层的数据处理线程
    	
    	try{	
    		//开始比特率切换算法的线程
			new Thread(new BitrateSwitchThread(InetAddress.getByName(ServerIpAddress) , LocalPort+2)).start();   //启动码率切换算法
			//new Thread(new CollectFISDataThread(ScreenActivity.this)).start();    //启动模糊逻辑系统所需数据采集的线程
			
		}catch(UnknownHostException e){e.printStackTrace();} 
   	
    	t = new MyPlayThread();		//创建播放线程，但是不开始
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
		NativeHandler.Decoder_release();    //停止解码器
		NativeHandler.nativeReceiver_stop();    //停止rtp
	}
		
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3){
		Log.i(TAG, "surfaceChanged....");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0){
		Log.i(TAG, "surfaceCreated....");
		
		//获取屏幕(SurfaceView)宽高		
		mScreenWidth = holder.getSurfaceFrame().width();
		mScreenHeight = holder.getSurfaceFrame().height();
		int Buttons_topLine = (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2 + 10;   //这是按钮顶部基线 ，加一个数值是为了和播放区域离开一点距离
		
		//等待转圈
		progersssDialog = new ProgersssDialog(this);
		progersssDialog.setMsg("Loading");
		//progersssDialog.show();
		
		Canvas canvas = holder.lockCanvas(null);//获取画布   
        Paint mPaint = new Paint();   
        mPaint.setColor(Color.LTGRAY);   
        canvas.drawRect(new RectF(0,(mScreenHeight-(mFrameHeight*mScreenWidth)/mFrameWidth)/2,mScreenWidth , (mScreenHeight+(mFrameHeight*mScreenWidth)/mFrameWidth)/2) ,mPaint);   
        holder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像   
        
        //设置按钮
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
        
		t.start(); //开始播放线程
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
			//开始底层接收数据的线程
			NativeHandler.nativeReceiver_start();
		}
	}
	
	/**
	 * 控制更新MyView的画图线程
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
			} //获取去数据帧的宽高
			
			byte[] mPixel = new byte[mFrameWidth * mFrameHeight * 2];
			int i = mPixel.length;  //初始化这个数组
	        for(i=0; i<mPixel.length; i++){
	        	mPixel[i]=(byte)0x00;
	        }
	        
			ByteBuffer buffer = ByteBuffer.wrap(mPixel);
			Bitmap videoBit = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Config.RGB_565);
			
			while(NativeHandler.get_FrameQueue_C() < 100){ //如果nalu数量小于50个就等待
				try{Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
			progersssDialog.dismiss();	
			
			while (running)
			{	//System.out.println(mFrameWidth+" X "+mFrameHeight);
				if(playing)  //根据按钮状态
				{   
					//System.out.println("draw a Bitmap............................");
					//获取解码后的数据
					int byteNum = NativeHandler.get_a_DecodedFrame(mPixel);
					if(byteNum == -1){//返回-1表示，不能获取一帧
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
					//此时画面停止在当前的帧，不进行刷新，再次点击对继续执行，画面进行刷新播放
				}
				else if(faster)
				{
					playing = true ;  pausing = false;
					stop    = false ;   faster  = false ; 
					
					int byteNum = NativeHandler.get_a_Decoded_jumped_Frame(mPixel, step_size);   //跳过step_size个帧再显示
					if(byteNum == -1){//返回-1表示，不能获取一帧
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
				else if(stop)  //退出程序
				{
					finish();//只是activity出栈，但是资源还没有及时释放
				}
				
				try {Thread.sleep(35);} catch (InterruptedException e){e.printStackTrace();} //等待
			}
		}
	}
	
}
