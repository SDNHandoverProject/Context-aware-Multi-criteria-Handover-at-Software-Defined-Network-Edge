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
 * ģ��android�ֻ������ֻ��ƶ�����Ҫ���͸�����Ӧ�õ�����
 * ����Ҫ�Ķ����ݰ��� 
 * 					 1��private static double AndroidLongitude;     //�ֻ�λ�þ���
 *					 2��	private static double AndroidLatitude;     //�ֻ�λ��γ��;
 *                   3��private static double AndroidSpeed;     //�ֻ��ƶ��ٶ�;
 *	                 4��private static double AndroidBearing;     //�ֻ��ƶ��������������ĽǶ�;
 *	 				 5��private static int UserPref;      //�û�ƫ��
 *	    			 6��private static int UserBuf;              //�û��ƶ����Ŷ˵Ļ�����	
 *		
 */
public class AndroidInfoTest implements Runnable{
	
	private static double Init_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;    //�����ָ������������ѡ��һ����ʼ��λ�ã���Ϊandroid�ֻ��ƶ���·�ĳ�ʼ��
	private static double Init_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
	private static double End_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;      //�����ָ������������ѡ��һ��������λ�ã���Ϊandroid�ֻ��ƶ���·�Ľ�����
	private static double End_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
	private static double Last_Android_Long = Init_Android_Long;
	private static double Last_Android_Lat = Init_Android_Lat;
	private static double Next_Android_Long;
	private static double Next_Android_Lat;

	private static int UserPref = 65;      //�û�ƫ��(���ֵ�����úú������������ڼ��ǲ��仯�Ķ�)
	private static int UserBuf;              //�û��ƶ����Ŷ˵Ļ�����	
	private static double AndroidSpeed;     //�ֻ��ƶ��ٶ�;
	private static double AndroidBearing;     //�ֻ��ƶ��������������ĽǶ�;
	
	private static int TimeSlot = 5;
	private static Map<String,MyWifiInfoItem> CoverlistwifiS = new HashMap<String,MyWifiInfoItem>(); 
	private static String Connected_SSID = "";
	//private static double Based_ssid = 2400;
//	private static int previousBitRateLevel = 4;   //��ʼʱ����ߵȼ� ������������
//	private static int requestLevel = previousBitRateLevel ;
//	private static double[] B_result ;
//	private static Map<Integer,Integer> BitrateLevels ; 
//	static{
//		BitrateLevels = new HashMap<Integer,Integer>();
//		BitrateLevels.put(1, 1000);    // ��Ƶ���ʵȼ����Լ���Ӧ����
//		BitrateLevels.put(2, 1200);
//		BitrateLevels.put(3, 1400);
//		BitrateLevels.put(4, 1600);
//		
//		B_result = new double[BitrateLevels.size()];
//		for(int i=0 ; i<BitrateLevels.size() ;i++)  //��ʼ��
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
		CoverlistwifiS.get(ssid).setConnected(true);   //��ʼʱ��ʹ��Rssi����ȷ����ǰ���������
		CoverlistwifiS.get(ssid).setAB( (Math.random()*200)+200 );   //200-400 kb/s
		Connected_SSID = ssid;   //���õ�ǰ��������ĸ�����
		
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
				
				if(Next_Android_Long == End_Android_Long && Next_Android_Lat == End_Android_Lat)	//�����ʱ�õ�һ��GPSλ�õ���ָ���Ľ���λ�� ��������ָ���ĳ�ʼ����ʼ��ͽ�����
				{   
					Init_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;    //�����ָ������������ѡ��һ����ʼ��λ�ã���Ϊandroid�ֻ��ƶ���·�ĳ�ʼ��
					Init_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
					End_Android_Long = 100.00000 + ((int)(Math.random()*200)+1)/100000.0 ;      //�����ָ������������ѡ��һ��������λ�ã���Ϊandroid�ֻ��ƶ���·�Ľ�����
					End_Android_Lat = 30.00000 + ((int)(Math.random()*100)+1)/100000.0 ;
					Last_Android_Long = Init_Android_Long;   //��ʼλ��
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
					CoverlistwifiS.get(Ssid).setConnected(true);   //��ʼʱ��ʹ��Rssi����ȷ����ǰ���������
					CoverlistwifiS.get(Ssid).setAB( (Math.random()*200)+200 );   //200-400 kb/s
					Connected_SSID = Ssid;   //���õ�ǰ��������ĸ�����
				}
				else  //�����ж����λ���Ƿ�����ָ����GPS��Χ�������������ôֻ������ָ����һ��λ��
				{
					if(!(Next_Android_Long>=100.00000 && Next_Android_Long <= 100.00200) || !(Next_Android_Lat>=30.00000 &&Next_Android_Lat<=30.00100 ))   //���ڷ�Χ��
						continue;  //���������break��䣬��ͷ��ʼ����
					else{ //next��λ��û�г�����Χ����ʱ���Լ���һЩ����ֵ
						double distance = getDistance(Last_Android_Long, Last_Android_Lat, Next_Android_Long, Next_Android_Lat);
						UserBuf = get_Buf(CoverlistwifiS.get(Connected_SSID).getAB()); //ȡ�ô�����Ϊ������������  (int)(Math.random()*300)+1;  //��������û�ƫ��
						AndroidSpeed = distance / TimeSlot;   //���ݾ�������ٶ�
						AndroidBearing = getAngle(Last_Android_Long, Last_Android_Lat, Next_Android_Long, Next_Android_Lat);   //��ȡ��ʱ�˿̵� �ƶ��Ƕ�ֵ
						
						set_CoverlistwifiS( Next_Android_Long, Next_Android_Lat);    //���������ǵ�����
						if(CoverlistwifiS.containsKey(Connected_SSID)){  //������һ��ɨ������������磬����������
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
		
						String str = S;   //�ռ��������ϴ���FIS
						byte[] buf = str.getBytes();
						DatagramPacket dp = new DatagramPacket(buf , buf.length , address, ControllerAppPort);     //System.out.println("ds.send(dp)===Send collected Data to controllerApp"); 
						try{    //ʹ��UDP��������
							ds.send(dp);
						}catch(Exception e){
							e.printStackTrace();
						}
						
						//��������Server��ָ��
						byte[] buf1= new byte[1024];
		        		DatagramPacket dp1 = new DatagramPacket(buf1,buf1.length); 
		        		ds.receive(dp1);	                              System.out.println("ds.receive(dp1): "+new String(dp1.getData(),0,dp1.getLength()));
		        		//Connected_SSID = new String(dp1.getData(),0,dp1.getLength());

		        		try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȴ��룬ÿ*��ִ��һ��
					}
				}
			}
		} 
		catch(IOException e){
			e.printStackTrace();
		}
		finally{   //�ر��׽�����Դ
			try{ds.close();}catch(Exception e){e.printStackTrace();}	
		}
	}
	
	/**
	 * ����һ��buf
	 * @param throughput ��������ʵʱ�仯
	 * @return
	 */
	private int get_Buf(double throughput) {
		// TODO �Զ����ɵķ������
		return 0;
	}

	private static double getAngle(double last_Android_Long,double last_Android_Lat, double next_Android_Long,double next_Android_Lat) {
		if(last_Android_Long < next_Android_Long){  //�µ� �� �õ���Ҳ�
			if(last_Android_Lat < next_Android_Lat){  //�µ� �� �õ���ϲ�(��һ����)
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return (Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
			else //(��������)
			{
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return 180-(Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
		}
		else{  //�µ� �� �õ�����
			if(last_Android_Lat < next_Android_Lat){  //�µ� �� �õ���ϲࣨ�ڶ����ޣ�
				double len1 = getDistance(last_Android_Long, next_Android_Lat, next_Android_Long , next_Android_Lat);
				double len2 = getDistance(last_Android_Long, next_Android_Lat, last_Android_Long, last_Android_Lat);
				double len3 = getDistance(last_Android_Long, last_Android_Lat, next_Android_Long , next_Android_Lat);
				return 360-(Math.acos((Math.pow(len2, 2) + Math.pow(len3, 2) - Math.pow(len1, 2)) / (2 * len2 * len3))) * (180/Math.PI);
			}
			else{    //��������
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
        R = 6378137; //����뾶
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
			if(dist < gpsInfo.getCoverage_Area())  //����ռ���ǰλ�úͽ����ľ�����wifi�źŵĸ��ǰ뾶���ڣ�����Ϊ�ֻ����յ�wifi�ź�
			{        										//System.out.print(SSID_Str);
				 MyWifiInfoItem item = new MyWifiInfoItem();
				 item.setSSID(SSID_Str); 	 //����SSID����
				 item.setConnected(false);  	//������û������
				 item.setAB(0.0);
				 int rssi = (int)(-Math.pow(10.0/gpsInfo.getCoverage_Area(),2.0) * Math.pow(dist,2.0) - 10);                  System.out.println(SSID_Str+"--dist:"+dist+"--Coverage:"+gpsInfo.getCoverage_Area()+"--rssi:"+rssi);
				 item.setRss( rssi);   //gpsInfo.getCoverage_Area()��ΧԽ��(20-50),������Ϊ�����ķ��书��Խ��
				 CoverlistwifiS.put(SSID_Str,item);
			}
		}
	}
	
	
}

class MyWifiInfoItem   //ÿһ��wifi�źŵ�һ����¼��
{
	private String SSID = "";    //wifi��SSID
	private boolean isConnected = false ;   //�Ƿ��ǵ�ǰ���������
	private int Rss = 0;   //�����źŵ�ǿ��
	private double AB = 0.0;   //��ǰwifi���ô���,��λKB/S
	
	public String getSSID() {return SSID;}
	public void setSSID(String sSID) {SSID = sSID;}
	
	public boolean isConnected() {return isConnected;}
	public void setConnected(boolean isConnected) {this.isConnected = isConnected;}
	
	public int getRss() {return Rss;}
	public void setRss(int rss) {Rss = rss;}
	
	public double getAB() {return AB;}
	public void setAB(double aB) {AB = aB;}
	
}