package com.javamedia;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javamedia.VideoInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	
	
	private LocationManager locationManager;   /**定位管理*/
	private String provider;   /**位置提供者，GPS提供或者 NETWORK提供*/
	private LocaInfo locaInfo;

	SurfaceView surfaceView;       /**
									  1、定义:可以直接从内存或者DMA等硬件接口取得图像数据,是个非常重要的绘图容器。它的特性是：可以在主线程之外的线程中向屏幕绘图上。
	                                                                  这样可以避免画图任务繁重的时候造成主线程阻塞,从而提高了程序的反应速度。
									  2、实现:首先继承SurfaceView并实现SurfaceHolder.Callback接口.使用接口的原因：因为使用SurfaceView 有一个原则，
									  	 所有的绘图工作必须得在Surface 被创建之后才能开始(Surface—表面，这个概念在 图形编程中常常被提到。基本上我们可以把它当作显存的一个映射，
									  	 写入到Surface 的内容可以被直接复制到显存从而显示出来，这使得显示速度会非常快)，而在Surface 被销毁之前必须结束。
									  	 所以Callback 中的surfaceCreated 和surfaceDestroyed 就成了绘图处理代码的边界
                      			   */
	
	SurfaceHolder holder;       /** 可以通过SurfaceHolder接口访问这个surface，getHolder()方法可以得到这个接口*/
	
	ProgersssDialog progersssDialog = null;    
	
	private String ServerIpAddress = "192.168.0.103";    /** server端的主机ip地址 */
	private int ServerPort = 60004;
	private int LocalPort = -1;                        /** 先将本地端口初始化为-1*/
	private SocketChannel socketChannel=null; //         /**socket通信，用于？？？？？？？？？？？？？？？？？？？？？*/
	
	ArrayList<VideoInfo> VideoInfoArray = null;        /** ArrayList数据结构，用于存放接收到的视频数据相关信息，每个视频信息放在VideoInfo类中 */
	List<HashMap<String,String>> data ;
	
	static OutputStream Out = null;       /** 输出流 */
	ObjectInputStream Oin;         /** 序列化 对象输入流 */
	
	//private ImageView play ;
	private ListView listView;       /**列表控件*/
	
	private static Handler handler = null;    /**handler 主要接受子线程发送的数据， 并用此数据配合主线程更新UI。*/
	private SimpleAdapter adapter =null;     /** */
	
	private boolean LoadListOver = false;    /** */
	
	private static int UserPreference = 50 ;
	
	public static int getUserPreference() {    
		return UserPreference;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){	     /**onCreate() 消息响应函数，是用来“表示一个窗口正在生成”。*/
		System.out.println("onCreate.............");
	
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);            /** 加载 布局文件 */
	
		surfaceView = (SurfaceView) findViewById(R.id.loopsurfaceview);   /** SurfaceView 关联起来 */
		this.holder = surfaceView.getHolder();     /** SurfaceHolder是SurfaceView的控制接口 */
		this.holder.addCallback(this);             /** 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this */
		
		surfaceView.setZOrderOnTop(true);        /** 是否这个surfaceView 放置在window的最顶层*/
		this.holder.setFormat(PixelFormat.TRANSLUCENT);   /** 设置旋转等待环loop透明  */
		
		VideoInfoArray = new ArrayList<VideoInfo>();
		data = new ArrayList<HashMap<String,String>>();
		
		handler = new Handler(){
			public void handleMessage(Message msg) {
				switch(msg.what){ 
				case 0:		           System.out.println("case 0........................");
					adapter.notifyDataSetChanged();        /** 发送消息通知ListView更新  */
					listView.setAdapter(adapter);         /** 重新设置ListView的数据适配器 */
					progersssDialog.dismiss(); 
					break;
				case 1:		           System.out.println("case 1........................");
					progersssDialog.dismiss(); 
					break;
				default:
					break;
				}
			}
		};
		
		listView = (ListView)this.findViewById(R.id.listView);     /** 列表view的关联 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){       /** 设置列表项的单击监听事件 */
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView VName=(TextView)view.findViewById(R.id.VideoName);      /** 视频名称view相关联 */
			
				String videoN = (String)VName.getText();
				try {
					Out.write(videoN.substring(videoN.indexOf('-')+1).getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//if(LocalPort != -1){//当出现列表进行选择的时候，已经socket连接好了，所以不用验证
				Intent Switch2Screen_intent = new Intent(MainActivity.this,ScreenActivity.class);
				Switch2Screen_intent.putExtra("ServerIpAddress", ServerIpAddress);
				Switch2Screen_intent.putExtra("ServerPort", ServerPort);
				Switch2Screen_intent.putExtra("LocalPort", LocalPort);
				MainActivity.this.startActivity(Switch2Screen_intent);    /** 执行这个意图，打开播放视频的Activity */
				//}
		}});
		
		locaInfo = LocaInfo.getInstance();//获取一个单例的LocaInfo对象
		//获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //查找到服务信息    位置数据标准类   
        Criteria criteria = new Criteria();       
        //查询精度:高   
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    
        //是否查询海拔:否
        criteria.setAltitudeRequired(true);   
        //是否查询方位角:是   
        criteria.setBearingRequired(true);   
        //是否允许付费   
        criteria.setCostAllowed(false);       
        //电量要求:高
        criteria.setPowerRequirement(Criteria.POWER_HIGH);   
        //是否查询速度:是   
        criteria.setSpeedRequired(true);
        provider = locationManager.getBestProvider(criteria, true);   
        
        Location location = locationManager.getLastKnownLocation(provider);
        
        Log.i("Location","---location---:" + location );
        if (location != null) {   Log.i("Location", "location != null.................");
        	SetLocInfo(location);     /**设置location的位置信息*/
        }
        
        locationManager.requestLocationUpdates(provider , 10, 1 ,new MylocationListener());
        
	}
	
	@Override
	protected void onResume(){System.out.println("onResume.............");
		super.onResume();	
		
		ConnToServer CTS = new ConnToServer(this);   /** 开始连接Server端的线程  */
		new Thread(CTS).start();
		
		Timer timer = new Timer(5000);   /** 开启计时器线程 */
		new Thread(timer).start();
	}
	
	@Override
	protected void onPause(){System.out.println("onPause.............");
		super.onPause();
	}
	
	@Override
	protected void onDestroy(){System.out.println("onDestroy.............");
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0){
		System.out.println("surfaceCreated.............");
		//等待转圈
		progersssDialog = new ProgersssDialog(this);
		progersssDialog.setMsg("Loading list....");
		progersssDialog.show();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		System.out.println("surfaceChanged.............");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("surfaceDestroyed.............");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {     /** onCreateOptionsMenu()这个方法是用于创建菜单的 */
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
									System.out.println("1mian_menu_create.............");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {    /**每当有菜单项被点击时，android就会调用该方法，并传入被点击菜单项*/
		int id = item.getItemId();
		if (id == R.id.action_settings) {    System.out.println("USER_PERCE菜单选项被点击了.............");
			ShowDialog();   //弹出dialog来设置用户偏好值
			return true;
		}
		else if (id == R.id.Settings) {     System.out.println("Settings菜单选项被点击了++++++.............");
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	} 

	class ConnToServer implements Runnable
	{
		MainActivity MA = null;
		
		public ConnToServer(MainActivity mainactivity){
			this.MA = mainactivity;            
		}
		
		public void run(){
			//socketChannel连接
	        try{
	        	Looper.prepare(); 
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
				socketChannel = SocketChannel.open();
				socketChannel.connect(new InetSocketAddress(InetAddress.getByName(ServerIpAddress),ServerPort));
				
				while (!socketChannel.finishConnect()) {	//System.out.println("等待非阻塞连接建立....");
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
				}
				
				LocalPort = socketChannel.socket().getLocalPort();
				if(LocalPort % 2 != 0) 
					LocalPort += 1; 	 //jrtplib不支持奇数端口
				
				Socket socket = socketChannel.socket();
				Out = socket.getOutputStream();
				Out.write("ARM_req".getBytes());	//以示区别Android端的请求
		       
				Oin = new ObjectInputStream(socket.getInputStream());
				
				Object obj = null;  	 //序列化对象接收  
		        while((obj = Oin.readObject()) != null)
		        {     
		        	VideoInfo Vinfo = (VideoInfo)obj;     //把接收到的对象转化为VideoInfo
		        	
		        	HashMap<String,String> item = new HashMap<String,String>();    /** 用于存储视频信息的hash表  */
		        	item.put("VideoName", "<视频>-"+Vinfo.getVideoName());
		        	item.put("VideoCdata", "创建日期:"+Vinfo.getCreateDate()+"  ");
		        	item.put("VideoTime", " 时长:"+Vinfo.getVideoTime());
		        	data.add(item);
		        	VideoInfoArray.add(Vinfo); //加入队列中	
		
		        }
		        
		        adapter = new SimpleAdapter(MA , data, R.layout.item,                  /** 这个适配器用于显示视频列表中的信息 */
		        		  new String[]{"VideoName", "VideoTime", "VideoCdata"}, 
		        		  new int[]{ R.id.VideoName, R.id.TimeDuring, R.id.CreateData});
		        
		        handler.sendEmptyMessage(0);      /** 通知列表View进行 画面更新 */
		        
		        LoadListOver = true;     /** 列表View更新完毕  */     System.out.println("//////////////////////LoadListOver = true");
		        
		        Looper.loop(); 
			} 
			catch (Exception e){
				Log.e("SocketError", e.getMessage());
			} 
		}
	}
 
	//计时器线程
	class Timer implements Runnable
	{
		long waitingPeriod;
		
		public Timer(int timeout){      /** 设置超时时长 */
			this.waitingPeriod = timeout;      System.out.println("Timer...........................");
		}
		
		public void run(){
			long then = System.currentTimeMillis();   
			while(!LoadListOver && waitingPeriod>System.currentTimeMillis()-then){   /** 循环检查是否在规定时间里面列表更新完毕，超时会跳出while，
																						  或者列表更新完毕LoadListOver=true也会跳出while循环 */
				try {Thread.sleep(100);}
				catch(InterruptedException e)
				{e.printStackTrace();}
			}
			if(!LoadListOver){	      /** 检查如果列表更新没有完成，则关闭循环等待，同时显示提示信息 */
				Looper.prepare();
				handler.sendEmptyMessage(1);
				Toast.makeText(getApplicationContext(), "Load failed , check network connection", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}
	}
	
	
	private class MylocationListener implements LocationListener    
	 {
       @Override
       public void onStatusChanged(String arg0, int arg1, Bundle arg2){}

       @Override
       public void onProviderEnabled(String arg0){}

       @Override
       public void onProviderDisabled(String arg0){}

       //当位置发生变化时调用的方法 
       public void onLocationChanged(Location location){
    	   SetLocInfo(location);     /**设置location的位置信息*/
       }
	};
	
	private void SetLocInfo(Location location)
	{
//		//获取当前位置，这里只用到了经纬度
//        String Provider = location.getProvider();
//        String Latitude = "|纬度:"+location.getLatitude();
//        String Longitude = "|经度:"+location.getLongitude();
//        String Bearing = "|偏离正北方的度数 :"+location.getBearing();
//        String speed = "|速度:"+location.getSpeed();
//        String Accuracy = "|精度级别:"+location.getAccuracy();
//       
//        Date date = new Date(location.getTime());          //利用Date进行时间的转换    
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       //设置时间的显示格式 也可以设置为：yyyy/MM/dd HH:mm:ss  
//        String Time = "|时刻:"+df.format(date);
//        String LocInfo = "\n"+Provider+Latitude+Longitude+Bearing+speed+Accuracy+Time+"\n";
		
		locaInfo.setcGPSLatitude(location.getLatitude());     //设置纬度值
		locaInfo.setcGPSLongitude(location.getLongitude());    //设置经度值
		locaInfo.setcGPSSpeed(location.getSpeed());         //设置速度值
		locaInfo.setcGPSBearing(location.getBearing());    //设置方向
	}
	
	//显示alertDialog
	public void ShowDialog()
	{
		final SeekBar seekBar;
	    AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
	    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    View Viewlayout = inflater.inflate(R.layout.mydialog,(ViewGroup)findViewById(R.id.layout_dialog));       
	    
	    final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); 
	    final TextView item2 = (TextView)Viewlayout.findViewById(R.id.txtItem2); 
	    item2.setText("注：这个数值表示用户期望的用户体验的量化表示。\n        数值越大，表示用户对网络费用不敏感，而比较在乎视频观看的良好体验。\n        数值越小，表示用户对网络费用的变化比较敏感。");
		
		popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Set_User_Preference");
		popDialog.setView(Viewlayout);
		
		//seekBar1
		seekBar = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);  
		seekBar.setProgress(50);  //设置seekbar的初始值为50
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
		        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		        	item1.setText("Value：" + progress);
		        	UserPreference = progress;
		        }

				public void onStartTrackingTouch(SeekBar arg0) {}
				public void onStopTrackingTouch(SeekBar seekBar) {}
		    });
	
		//Button OK
		popDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					UserPreference = seekBar.getProgress();    //设置数值
					dialog.dismiss();
				}
			});
		popDialog.create().show();
	}

	
}


