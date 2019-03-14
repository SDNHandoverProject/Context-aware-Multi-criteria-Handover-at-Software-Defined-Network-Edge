package com.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import android.net.wifi.ScanResult;





import com.info.GPSInfo;
import com.info.TopoInfo;


/**
 * 模拟android手机产生手机移动中所要发送给控制应用的数据
 * 所需要的额数据包括 
 * 					 1、private static double AndroidLongitude;     //手机位置经度
 *					 2、	private static double AndroidLatitude;     //手机位置纬度;
 *                   3、private static double AndroidSpeed;     //手机移动速度;
 *	                 4、private static double AndroidBearing;     //手机移动反向与正北方的角度;
 *	 				 5、private static int UserPref;      //用户偏好
 *	    			 6、private static int UserBuf;              //用户移动播放端的缓存量	
 *		
 */
public class AndroidInfoTest implements Runnable{
	
	private static double Init_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;    //随机在指定的区域里面选定一个初始的位置，作为android手机移动线路的初始点
	private static double Init_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
	private static double End_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;      //随机在指定的区域里面选定一个结束的位置，作为android手机移动线路的结束点
	private static double End_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
	private static double Last_Android_Long = Init_Android_Long;
	private static double Last_Android_Lat = Init_Android_Lat;
	private static double Next_Android_Long;
	private static double Next_Android_Lat;

	private static int UserPref = 65;      //用户偏好(这个值是设置好后，在整个播放期间是不变化的额)
	private static int UserBuf;              //用户移动播放端的缓存量	
	private static double AndroidSpeed;     //手机移动速度;
	private static double AndroidBearing;     //手机移动反向与正北方的角度;
	
	private static int TimeSlot = 5;
	private static Map<String,MyWifiInfoItem> CoverlistwifiS = new HashMap<String,MyWifiInfoItem>(); 
	private static String Connected_SSID = "";
	//private static double Based_ssid = 2400;
//	private static int previousBitRateLevel = 4;   //初始时用最高等级 的码率来发送
//	private static int requestLevel = previousBitRateLevel ;
//	private static double[] B_result ;
//	private static Map<Integer,Integer> BitrateLevels ; 
//	static{
//		BitrateLevels = new HashMap<Integer,Integer>();
//		BitrateLevels.put(1, 1000);    // 视频码率等级，以及对应码率
//		BitrateLevels.put(2, 1200);
//		BitrateLevels.put(3, 1400);
//		BitrateLevels.put(4, 1600);
//		
//		B_result = new double[BitrateLevels.size()];
//		for(int i=0 ; i<BitrateLevels.size() ;i++)  //初始化
//			B_result[i] = 0;
//		
//	}
	
	private DatagramSocket ds;
	private int ControllerAppPort = 22222;

	@Override
	public void run() {
		System.out.println("-------------init start--------------");
		set_CoverlistwifiS(Last_Android_Long, Last_Android_Lat);
		int maxRSSI = -1000;
		String ssid = "";
		for (Map.Entry<String,MyWifiInfoItem> entry : CoverlistwifiS.entrySet()){
			if(entry.getValue().getRss()>maxRSSI){
				maxRSSI = entry.getValue().getRss();
				ssid = entry.getKey();
			}
		}
		CoverlistwifiS.get(ssid).setConnected(true);   //初始时刻使用Rssi的来确定当前接入的网络
		CoverlistwifiS.get(ssid).setAB( (Math.random()*200)+200 );   //200-400 kb/s
		Connected_SSID = ssid;   //设置当前接入的是哪个网络
		
												String Ss = "";
												for (Map.Entry<String,MyWifiInfoItem> entry : CoverlistwifiS.entrySet()){
													MyWifiInfoItem item = entry.getValue();
													Ss = Ss+
															"SSID:"+item.getSSID()+																			
															"|IsCon:"+item.isConnected()+																		
															"|Rss:"+item.getRss()+																				
															"|AB:"+item.getAB()+																																																
															"-----";
												}
												Ss = Ss+
														"Lati:"+Next_Android_Lat+
														"|Long:"+Next_Android_Long+
														"|Speed:"+AndroidSpeed+
														"|Beari:"+AndroidBearing+
														"|UPre:"+UserPref+																					
														"|UBuf:"+UserBuf;
												System.out.println("Ss: "+ Ss);
												System.out.println("-------------init over--------------");

		try
		{
			ds = new DatagramSocket();
			InetAddress address = InetAddress.getByName("127.0.0.1");
		
			while(true)
			{System.out.println("\nwhile-----------------------------");
				Next_Android_Long = Last_Android_Long + ((int)(Math.random()*17)+1)/100000.0;
				Next_Android_Lat = Last_Android_Lat + ((int)(Math.random()*17)+1)/100000.0;
				
				if(Next_Android_Long == End_Android_Long && Next_Android_Lat == End_Android_Lat)	//如果此时得到一个GPS位置点是指定的结束位置 ，则重新指定的初始化开始点和结束点
				{   
					Init_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;    //随机在指定的区域里面选定一个初始的位置，作为android手机移动线路的初始点
					Init_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
					End_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;      //随机在指定的区域里面选定一个结束的位置，作为android手机移动线路的结束点
					End_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
					Last_Android_Long = Init_Android_Long;   //初始位置
					Last_Android_Lat = Init_Android_Lat;
					set_CoverlistwifiS(Last_Android_Long, Last_Android_Lat);
					int MaxRSSI = -1000;
					String Ssid = "";
					for (Map.Entry<String,MyWifiInfoItem> entry : CoverlistwifiS.entrySet()){
						if(entry.getValue().getRss()>maxRSSI){
							MaxRSSI = entry.getValue().getRss();
							Ssid = entry.getKey();
						}
					}
					CoverlistwifiS.get(Ssid).setConnected(true);   //初始时刻使用Rssi的来确定当前接入的网络
					CoverlistwifiS.get(Ssid).setAB( (Math.random()*200)+200 );   //200-400 kb/s
					Connected_SSID = Ssid;   //设置当前接入的是哪个网络
				}
				else  //否则，判断这个位置是否还属于指定的GPS范围，如果超出，那么只能重新指定下一步位置
				{
					if(!(Next_Android_Long>=100.00000 && Next_Android_Long <= 100.00200) || !(Next_Android_Lat>=30.00000 &&Next_Android_Lat<=30.00100 ))   //不在范围内
						continue;  //跳过下面的break语句，从头开始重新
					else{ //next的位置没有超出范围，此时可以计算一些参数值
						double distance = getDistance(Last_Android_Long, Last_Android_Lat, Next_Android_Long, Next_Android_Lat);
						UserBuf = get_Buf(CoverlistwifiS.get(Connected_SSID).getAB()); //取得带宽作为，吞吐量参数  (int)(Math.random()*300)+1;  //随机产生用户偏好
						AndroidSpeed = distance / TimeSlot;   //根据距离计算速度
						AndroidBearing = getAngle(Last_Android_Long, Last_Android_Lat, Next_Android_Long, Next_Android_Lat);   //获取此时此刻的 移动角度值
						
						set_CoverlistwifiS( Next_Android_Long, Next_Android_Lat);    //计算所覆盖的网络
						if(CoverlistwifiS.containsKey(Connected_SSID)){  //包含上一次扫描所接入的网络，则设置连接
							CoverlistwifiS.get(Connected_SSID).setConnected(true);
							CoverlistwifiS.get(Connected_SSID).setAB( (Math.random()*200)+200 ) ;   //200-400
						}
						
						Last_Android_Long = Next_Android_Long ;
		        		Last_Android_Lat = Next_Android_Lat ;
		        		
						String S = "";
						for (Map.Entry<String,MyWifiInfoItem> entry : CoverlistwifiS.entrySet()){
							MyWifiInfoItem item = entry.getValue();
							S = S+
									"SSID:"+item.getSSID()+																			
									"|IsCon:"+item.isConnected()+																		
									"|Rss:"+item.getRss()+																				
									"|AB:"+item.getAB()+																																																
									"&";
						}
						S = S+
								"Lati:"+Next_Android_Lat+
								"|Long:"+Next_Android_Long+
								"|Speed:"+AndroidSpeed+
								"|Beari:"+AndroidBearing+
								"|UPre:"+UserPref+																					
								"|UBuf:"+UserBuf;
		
						String str = S;   //收集的数据上传给FIS
						byte[] buf = str.getBytes();
						DatagramPacket dp = new DatagramPacket(buf , buf.length , address, ControllerAppPort);     //System.out.println("ds.send(dp)===Send collected Data to controllerApp"); 
						try{    //使用UDP发送数据
							ds.send(dp);
						}catch(Exception e){
							e.printStackTrace();
						}
						
						//接收来自Server的指令
						byte[] buf1= new byte[1024];
		        		DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length); 
		        		ds.receive(dp1);	                              System.out.println("ds.receive(dp1): "+new String(dp1.getData(),0,dp1.getLength()));
		        		//Connected_SSID = new String(dp1.getData(),0,dp1.getLength());

		        		try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //等待秒，每*秒执行一次
					}
				}
			}
		} 
		catch(IOException e){
			e.printStackTrace();
		}
		finally{   //关闭套接字资源
			try{ds.close();}catch(Exception e){e.printStackTrace();}	
		}
	}
	
	/**
	 * 计算一次buf
	 * @param throughput 吞吐量，实时变化
	 * @return
	 */
	private int get_Buf(double throughput) {
		// TODO 自动生成的方法存根
		return 0;
	}

	private static double getAngle(double last_Android_Long,double last_Android_Lat, double next_Android_Long,double next_Android_Lat) {
		if(last_Android_Long < next_Android_Long){  //新点 在 久点的右侧
			if(last_Android_Lat < next_Android_Lat){  //新点 在 久点的上侧(第一象限)
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return (Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
			else //(第四象限)
			{
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return 180-(Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
		}
		else{  //新点 在 久点的左侧
			if(last_Android_Lat < next_Android_Lat){  //新点 在 久点的上侧（第二象限）
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return 360-(Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
			else{    //第三象限
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return 180+(Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
		}
	}

	private static double getDistance(double long1, double lat1, double long2, double lat2)
    {
        double a, b, R;
        R = 6378137; //地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));   //System.out.println("--"+d);
        return d;
    }

	private static void set_CoverlistwifiS(double Long, double Lat)
	{
		CoverlistwifiS.clear();
		for (Map.Entry<String,GPSInfo> entry : TopoInfo.CurrentTopoInfoMap.entrySet())
		{
			String SSID_Str = entry.getKey();  //System.out.print(SSID_Str);
			GPSInfo gpsInfo = entry.getValue();
			double dist = getDistance(gpsInfo.getLongitude(), gpsInfo.getLatitude(), Long, Lat);   
			if(dist < gpsInfo.getCoverage_Area())  //如果收集当前位置和接入点的距离在wifi信号的覆盖半径以内，则认为手机接收到wifi信号
			{        										//System.out.print(SSID_Str);
				 MyWifiInfoItem item = new MyWifiInfoItem();
				 item.setSSID(SSID_Str); 	 //设置SSID属性
				 item.setConnected(false);  	//先设置没有连接
				 item.setAB(0.0);
				 int rssi = (int)(-Math.pow(10.0/gpsInfo.getCoverage_Area(),2.0) * Math.pow(dist,2.0) - 10);                  System.out.println(SSID_Str+"--dist:"+dist+"--Coverage:"+gpsInfo.getCoverage_Area()+"--rssi:"+rssi);
				 item.setRss( rssi);   //gpsInfo.getCoverage_Area()范围越大(20-50),可以视为基本的发射功率越大
				 CoverlistwifiS.put(SSID_Str,item);
			}
		}
	}
	
	
}

class MyWifiInfoItem   //每一个wifi信号的一个记录行
{
	private String SSID = "";    //wifi的SSID
	private boolean isConnected = false ;   //是否是当前接入的网络
	private int Rss = 0;   //接收信号的强度
	private double AB = 0.0;   //当前wifi可用带宽,单位KB/S
	
	public String getSSID() {return SSID;}
	public void setSSID(String sSID) {SSID = sSID;}
	
	public boolean isConnected() {return isConnected;}
	public void setConnected(boolean isConnected) {this.isConnected = isConnected;}
	
	public int getRss() {return Rss;}
	public void setRss(int rss) {Rss = rss;}
	
	public double getAB() {return AB;}
	public void setAB(double aB) {AB = aB;}
	
}