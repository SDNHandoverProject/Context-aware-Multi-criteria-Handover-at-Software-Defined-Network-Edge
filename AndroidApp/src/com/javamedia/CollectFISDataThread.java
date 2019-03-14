package com.javamedia;

import java.io.*;
import java.net.*;
import java.util.*;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class CollectFISDataThread implements Runnable {
	
	private Context context;
	private LocaInfo locaInfo;
	
	private DatagramSocket ds;
	
	private String ControllerAppHostIP = "192.168.2.99";    //控制器host的IP地址
	private int ControllerAppPort = 22222; //    控制器应用的Port
	
	private static final int TimeSlot = 6;
	
	private int UserBuf;
	private int UserPref;
	private int Switchs = 0;
	private String getSSID = "";

	private WifiManager wifiManager;    //wifi管理器对象
	private List<ScanResult> listb;     //list列表中存储的是一次扫描的所有的没有经过过滤的Wifi结果
	private List<ScanResult> listk = new ArrayList<ScanResult>();  //ScanResult列表用于存储过滤后的Openwrt的Wifi信号
	private List<WifiConfiguration> mWifiConfiguration; 
	private static String wifiSwitchStat = "switched";   //这个字符串用于记录wifi切换的状态，因为wifi切换的间隙大约10毫秒，期间可能有udp数据要发送，暂时设置两种状态即 ：切换中:switching，切换完成:switched
	
	private List<MyInfoItem> listItem = new ArrayList<MyInfoItem>();    //存储Openwrt的wifi的信号信息
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CollectFISDataThread(Context cont)    //传递Context参数
	{
		this.context = cont;          //System.out.println("---------Co初始化---------------");
	}
	
	@Override
	public void run() {
		
		//获取Android的Wifi管理服务的权限
		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);   //System.out.println("-----------获取wifi管理------------");
																						//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");//设置日期格式
		UserPref = MainActivity.getUserPreference();     //获取用户偏好
		locaInfo = LocaInfo.getInstance();//获取一个单例的LocaInfo对象

		//开始实时网速测量线程
		RealTimeSpeed RS = new RealTimeSpeed();
		new Thread(RS).start();
		
		try {
			ds = new DatagramSocket(ControllerAppPort);  //此处不用port端口号？？？？？？？？？？？？？？
			InetAddress address=InetAddress.getByName(this.ControllerAppHostIP);
			
			while(true)//无限死循环
			{	
				try {Thread.sleep(TimeSlot /2 * 1000);}catch(InterruptedException e){e.printStackTrace();}   //先等待三秒，每TimeSlot秒执行一次信息上传至控制器端
				listItem.clear();
				listk.clear(); //清空一次
				
				
				
				UserBuf = NativeHandler.get_FrameQueue_C();     //获取实时Buf队列
			
				//获取关于Wifi的相关信息
				//扫描一次WIFI信号
				listb = wifiManager.getScanResults();   
				mWifiConfiguration = wifiManager.getConfiguredNetworks(); 
				if(listb!=null){ 				
					for(int i=0;i<listb.size();i++){  
						ScanResult scanResult = listb.get(i);
						if(scanResult.SSID.matches("^OpenWrt_1[1-9]_.*")){     //在此处用正则表达式 做一次过滤，只用识别Openwrt的wifi信号
							listk.add(scanResult);
						}  
					} 
				}
				//获取当前所连接的Wifi的信息
				for(int i=0;i<listk.size();i++){
					MyInfoItem WItem = new MyInfoItem();

					ScanResult SRi = listk.get(i);
					WItem.setSSID(SRi.SSID);       //设置Wifi的SSID属性
					if(wifiManager.getConnectionInfo().getSSID().equals("\""+WItem.getSSID()+"\"")){   //是当前接入wifi网络
						WItem.setConnected(true);       
						WItem.setAB(RS.getNetSpeed());    //设置这个接入Wifi的可用带宽，单位是kb/s（但是只有连接入的wifi可以获取到实时网络带宽）
					}
					WItem.setRss(SRi.level); 
					//设置Wifi的接收信号强度
				
					listItem.add(WItem);
				}
			
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//打印验证    
				String S="";////										
				for(int i=0;i<listItem.size();i++){																							
					MyInfoItem WIt = listItem.get(i);																						
					S = S+
						"SSID:"+WIt.getSSID()+																			
						"|IsCon:"+WIt.isConnected()+																		
						"|Rss:"+WIt.getRss()+																				
						"|AB:"+WIt.getAB()+																																																
						"&";    	
																												
				}	
				
				S = S+
					"Lati:"+locaInfo.getcGPSLatitude()+
					"|Long:"+locaInfo.getcGPSLongitude()+
					"|Speed:"+locaInfo.getcGPSSpeed()+
					"|Beari:"+locaInfo.getcGPSBearing()+
					"|UPre:"+UserPref+																					
					"|UBuf:"+UserBuf+
					"|Switch:"+Switchs+
					"|Throughput:"+RS.getNetSpeed()*TimeSlot*1024*1.0+  //(NativeHandler.Get_throughput2()/1024*1.0)/TimeSlot;
					"|bitLevel: "+BitrateSwitchThread.getRequestLevel(); 
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
							
				//将收集到的数据传输给server来做决策
				String str = S;
				byte[] buf = str.getBytes();
				DatagramPacket dp = new DatagramPacket(buf , buf.length , address, this.ControllerAppPort);     System.out.println("ds.send(dp)===Send collected Data to controllerApp"); 
				try{    //使用UDP发送数据
					ds.send(dp);
				}
				catch(Exception e)
				{
					System.err.println("Send collected Data UDP 出错了！！！！！！！！！！！！！！！"); 
					e.printStackTrace();
					try {Thread.sleep(100);}catch(InterruptedException e2){e2.printStackTrace();}   //等待毫秒
					continue;   //这次发送失败就跳过
				}   
				
				
				//接收来自Server的指令
				byte[] buf1= new byte[1024];
        		DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length);  System.out.println("dp1.toString():"+dp1.toString());
        		ds.receive(dp1);	
        		
        		getSSID = new String(dp1.getData(),0,dp1.getLength());
        		System.out.println("ds.receive(dp1):" + getSSID);
        		
        		
				
				
				//执行wifi切换操作(这个地方要重写)
				if(wifiManager.getConnectionInfo().getSSID().equals(getSSID)){
					 //接收到的ssid信号还是原来的那个
				}
				else 
				{  
					for (WifiConfiguration existingConfig : mWifiConfiguration)    
			        {   
			           if (existingConfig.SSID.equals(getSSID))   
			           {   
			               wifiSwitchStat = "switching";	 //设置状态为“正在切换中”	
			               //wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());     //先断掉当前wifi
			        	   wifiManager.enableNetwork(existingConfig.networkId, true);
			        	   wifiSwitchStat = "switched";		 //设置状态为  “切换已完成”
			        	   ++Switchs;
			        	   break;
			           }   
			        } 
				}
		
				
			
				
				listb.clear();//清空列表	
				try {Thread.sleep(TimeSlot /2 * 1000);}catch(InterruptedException e){e.printStackTrace();}     //此处再等待三秒
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{   //关闭套接字资源
			try {ds.close();}catch(Exception e){e.printStackTrace();}	
		}

	}
	
	public static String getWifiSwitchStat(){
		return wifiSwitchStat;
	}
	
	/**
	 * 执行wifi信号切换的方法
	 * @param wifiSSID wifi的ssid作为参数 
	 */
	private void exeSwitchWIFI(String wifiSSID)
	{
		//执行wifi切换操作
		if(wifiManager.getConnectionInfo().getSSID().equals("\""+wifiSSID+"\""))
		{    //若果当前的接入的wifi就是接收到的参，则什么也不做不执行切换
		}
		else{
			for (WifiConfiguration existingConfig : mWifiConfiguration)    
	        {
				if (existingConfig.SSID.equals("\""+wifiSSID+"\""))
				{   							
					wifiSwitchStat = "switching";
					wifiManager.enableNetwork(existingConfig.networkId, true);
					wifiSwitchStat = "switched";							
					break;
				} 
	        }
		}
	}

	class MyInfoItem   //每一个wifi信号的一个记录行
	{
		private String SSID = "";    //wifi的SSID
		private boolean isConnected = false ;   //是否是当前接入的网络
		private int Rss = 0;   //接收信号的强度
		private double AB = 0;   //当前wifi可用带宽,单位KB/S
		
		public String getSSID() {return SSID;}
		public void setSSID(String sSID) {SSID = sSID;}
		
		public boolean isConnected() {return isConnected;}
		public void setConnected(boolean isConnected) {this.isConnected = isConnected;}
		
		public int getRss() {return Rss;}
		public void setRss(int rss) {Rss = rss;}
		
		public double getAB() {return AB;}
		public void setAB(double aB) {AB = aB;}
		
	}

}
