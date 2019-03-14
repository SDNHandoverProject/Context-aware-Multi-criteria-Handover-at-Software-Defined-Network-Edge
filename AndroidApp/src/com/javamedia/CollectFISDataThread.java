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
	
	private String ControllerAppHostIP = "192.168.2.99";    //������host��IP��ַ
	private int ControllerAppPort = 22222; //    ������Ӧ�õ�Port
	
	private static final int TimeSlot = 6;
	
	private int UserBuf;
	private int UserPref;
	private int Switchs = 0;
	private String getSSID = "";

	private WifiManager wifiManager;    //wifi����������
	private List<ScanResult> listb;     //list�б��д洢����һ��ɨ������е�û�о������˵�Wifi���
	private List<ScanResult> listk = new ArrayList<ScanResult>();  //ScanResult�б����ڴ洢���˺��Openwrt��Wifi�ź�
	private List<WifiConfiguration> mWifiConfiguration; 
	private static String wifiSwitchStat = "switched";   //����ַ������ڼ�¼wifi�л���״̬����Ϊwifi�л��ļ�϶��Լ10���룬�ڼ������udp����Ҫ���ͣ���ʱ��������״̬�� ���л���:switching���л����:switched
	
	private List<MyInfoItem> listItem = new ArrayList<MyInfoItem>();    //�洢Openwrt��wifi���ź���Ϣ
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CollectFISDataThread(Context cont)    //����Context����
	{
		this.context = cont;          //System.out.println("---------Co��ʼ��---------------");
	}
	
	@Override
	public void run() {
		
		//��ȡAndroid��Wifi��������Ȩ��
		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);   //System.out.println("-----------��ȡwifi����------------");
																						//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");//�������ڸ�ʽ
		UserPref = MainActivity.getUserPreference();     //��ȡ�û�ƫ��
		locaInfo = LocaInfo.getInstance();//��ȡһ��������LocaInfo����

		//��ʼʵʱ���ٲ����߳�
		RealTimeSpeed RS = new RealTimeSpeed();
		new Thread(RS).start();
		
		try {
			ds = new DatagramSocket(ControllerAppPort);  //�˴�����port�˿ںţ���������������������������
			InetAddress address=InetAddress.getByName(this.ControllerAppHostIP);
			
			while(true)//������ѭ��
			{	
				try {Thread.sleep(TimeSlot /2 * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȵȴ����룬ÿTimeSlot��ִ��һ����Ϣ�ϴ�����������
				listItem.clear();
				listk.clear(); //���һ��
				
				
				
				UserBuf = NativeHandler.get_FrameQueue_C();     //��ȡʵʱBuf����
			
				//��ȡ����Wifi�������Ϣ
				//ɨ��һ��WIFI�ź�
				listb = wifiManager.getScanResults();   
				mWifiConfiguration = wifiManager.getConfiguredNetworks(); 
				if(listb!=null){ 				
					for(int i=0;i<listb.size();i++){  
						ScanResult scanResult = listb.get(i);
						if(scanResult.SSID.matches("^OpenWrt_1[1-9]_.*")){     //�ڴ˴���������ʽ ��һ�ι��ˣ�ֻ��ʶ��Openwrt��wifi�ź�
							listk.add(scanResult);
						}  
					} 
				}
				//��ȡ��ǰ�����ӵ�Wifi����Ϣ
				for(int i=0;i<listk.size();i++){
					MyInfoItem WItem = new MyInfoItem();

					ScanResult SRi = listk.get(i);
					WItem.setSSID(SRi.SSID);       //����Wifi��SSID����
					if(wifiManager.getConnectionInfo().getSSID().equals("\""+WItem.getSSID()+"\"")){   //�ǵ�ǰ����wifi����
						WItem.setConnected(true);       
						WItem.setAB(RS.getNetSpeed());    //�����������Wifi�Ŀ��ô�����λ��kb/s������ֻ���������wifi���Ի�ȡ��ʵʱ�������
					}
					WItem.setRss(SRi.level); 
					//����Wifi�Ľ����ź�ǿ��
				
					listItem.add(WItem);
				}
			
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//��ӡ��֤    
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
							
				//���ռ��������ݴ����server��������
				String str = S;
				byte[] buf = str.getBytes();
				DatagramPacket dp = new DatagramPacket(buf , buf.length , address, this.ControllerAppPort);     System.out.println("ds.send(dp)===Send collected Data to controllerApp"); 
				try{    //ʹ��UDP��������
					ds.send(dp);
				}
				catch(Exception e)
				{
					System.err.println("Send collected Data UDP �����ˣ�����������������������������"); 
					e.printStackTrace();
					try {Thread.sleep(100);}catch(InterruptedException e2){e2.printStackTrace();}   //�ȴ�����
					continue;   //��η���ʧ�ܾ�����
				}   
				
				
				//��������Server��ָ��
				byte[] buf1= new byte[1024];
        		DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length);  System.out.println("dp1.toString():"+dp1.toString());
        		ds.receive(dp1);	
        		
        		getSSID = new String(dp1.getData(),0,dp1.getLength());
        		System.out.println("ds.receive(dp1):" + getSSID);
        		
        		
				
				
				//ִ��wifi�л�����(����ط�Ҫ��д)
				if(wifiManager.getConnectionInfo().getSSID().equals(getSSID)){
					 //���յ���ssid�źŻ���ԭ�����Ǹ�
				}
				else 
				{  
					for (WifiConfiguration existingConfig : mWifiConfiguration)    
			        {   
			           if (existingConfig.SSID.equals(getSSID))   
			           {   
			               wifiSwitchStat = "switching";	 //����״̬Ϊ�������л��С�	
			               //wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());     //�ȶϵ���ǰwifi
			        	   wifiManager.enableNetwork(existingConfig.networkId, true);
			        	   wifiSwitchStat = "switched";		 //����״̬Ϊ  ���л�����ɡ�
			        	   ++Switchs;
			        	   break;
			           }   
			        } 
				}
		
				
			
				
				listb.clear();//����б�	
				try {Thread.sleep(TimeSlot /2 * 1000);}catch(InterruptedException e){e.printStackTrace();}     //�˴��ٵȴ�����
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{   //�ر��׽�����Դ
			try {ds.close();}catch(Exception e){e.printStackTrace();}	
		}

	}
	
	public static String getWifiSwitchStat(){
		return wifiSwitchStat;
	}
	
	/**
	 * ִ��wifi�ź��л��ķ���
	 * @param wifiSSID wifi��ssid��Ϊ���� 
	 */
	private void exeSwitchWIFI(String wifiSSID)
	{
		//ִ��wifi�л�����
		if(wifiManager.getConnectionInfo().getSSID().equals("\""+wifiSSID+"\""))
		{    //������ǰ�Ľ����wifi���ǽ��յ��ĲΣ���ʲôҲ������ִ���л�
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

	class MyInfoItem   //ÿһ��wifi�źŵ�һ����¼��
	{
		private String SSID = "";    //wifi��SSID
		private boolean isConnected = false ;   //�Ƿ��ǵ�ǰ���������
		private int Rss = 0;   //�����źŵ�ǿ��
		private double AB = 0;   //��ǰwifi���ô���,��λKB/S
		
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
