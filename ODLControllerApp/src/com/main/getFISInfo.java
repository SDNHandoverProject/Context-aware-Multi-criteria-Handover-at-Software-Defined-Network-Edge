package com.main;

import java.io.*;
import java.net.*;

import com.Test.AndroidInfoTest;
import com.info.FISInfo;
import com.info.TopoInfo;
import com.info.WifiInfoItem;

public class getFISInfo implements Runnable {
	
	private String ControllerAppHostIp;
	private int ControllerAppPort;
	private DatagramSocket ds = null;
	private controllerApp contrApp;
	private String Switch_str = "";
	private double DS_th_ = 0.3; //�������ֵ
	private double Value_�� = 0.05; //��Ѻ�ѡ����͵�ǰ������Ĳ�ֵ��
	
	public String getSwitch_str() {return Switch_str;}
	
	public getFISInfo(controllerApp contrApp,String ControllerHostIp ,int ControllerAppPort)
	{
		this.ControllerAppHostIp = ControllerHostIp;
		this.ControllerAppPort = ControllerAppPort;
		this.contrApp = contrApp;
	}

	@Override
	public void run(){

		//��������Android�����ռ��߳�
		//////////////////////////////////////////////////////////////////////////////////////////////
		try {Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}
		new Thread(new AndroidInfoTest()).start(); //���������߳�
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			ds = new DatagramSocket(ControllerAppPort);
			
        	while(true)
        	{
        		//������Ϣ
        		byte[] buf= new byte[1024];
        		DatagramPacket dp = new DatagramPacket(buf,buf.length);
        		ds.receive(dp);

        		//�����android�˽��յ�����Ϣ
        		String GotStr = new String(dp.getData(),0,dp.getLength());				System.out.println("GotStr: "+GotStr);	
        		
        		//String GotStr = "";
				dealWithDataFromAndroid(GotStr);   //���յ���
				this.contrApp.setText2Area1(); //����Ϣ��ʾ��frame��	
																		String Switch_str = "asegfweg";
																		String SS="";
																		for(int i=0;i<FISInfo.wifiInfo.size();++i){
																			 WifiInfoItem  W = FISInfo.wifiInfo.get(i);
																			 SS = SS+"SSID:"+W.getSSID()+"|IsCon:"+W.isConnect()+"|Rss:"+W.getRssi()+"|AB:"+W.getAB()+"|level:"+W.getChargeLevel()+"|Dsitan:"+W.getDistaFromAndroid()+"\n";
																		}
																		SS = SS+ "Lati:"+FISInfo.getAndroidLatitude()+"|Long:"+FISInfo.getAndroidLongitude()+"|Speed:"+FISInfo.getAndroidSpeed()+"|Beari:"+FISInfo.getAndroidBearing()+"|UPre:"+FISInfo.getUserPref()+"|UBuf:"+FISInfo.getUserBuf();
																					System.out.println(SS);
																		WifiInfoItem CW = null; 
																		int i=0;
																		for( ;i<FISInfo.wifiInfo.size();++i){
																			 CW = FISInfo.wifiInfo.get(i);
																			 if(CW.isConnect() == true )
																				 break;  //�Ӽ�¼����FISInfo���ҵ���ǰ�����wifi�����ź�
																		}
																		
																		if(i == FISInfo.wifiInfo.size())   //���i����Ϊѭ����϶���������ô��û��Connected����true��,�����ȡ���ֵ�����Ǹ��������
																		{
																			double best_score = 0.0;
																			String best_SSID = "";
																			for(int j=0;j<FISInfo.wifiInfo.size();++j){
																				WifiInfoItem cw = FISInfo.wifiInfo.get(j);
																				if(cw.isConnect() == false){   //��ѡ����
																					computeFIS.Comple_Net_Score(cw);
																					if(best_score < cw.getNetScore()){
																						best_score = cw.getNetScore();
																						best_SSID = cw.getSSID();
																					}
																				}
																			}
																			Switch_str = best_SSID; System.out.println("1111111111111111111111");	
																		}
																		else  //��Connected����true
																		{
																			computeFIS.CompleteNetWorkDegreeOfSatisfaction(CW);  //���㵱ǰ�����ֽ��
																			if(DS_th_ < CW.getNetScore()){   //��ǰ����ĵ÷ֽ��������ֵ
																				Switch_str = CW.getSSID();    System.out.println("22222222222222222222222");
																			}
																			else{ //��ǰ����ĵ÷ֽ��С����ֵ���ȼ������к�ѡ��������д�ֽ����ѡ�������ٶԱȵ�ǰ������
																				double best_score  = 0.0;
																				String best_SSID = "";
																				for(int r=0;r<FISInfo.wifiInfo.size();++r){
																					WifiInfoItem cw = FISInfo.wifiInfo.get(r);
																					if(cw.isConnect() == false){   //��ѡ����
																						computeFIS.Comple_Net_Score(cw);
																						if(best_score < cw.getNetScore()){
																							best_score = cw.getNetScore();
																							best_SSID = cw.getSSID(); 
																						}
																					}
																				}
																				
																				if(Math.abs(best_score-CW.getNetScore()) > Value_��)     //��Ѻ�ѡ����͵�ǰ������Ĳ����  ��  ���л����ú�ѡ������
																				{	Switch_str = best_SSID;    System.out.println(CW.getNetScore()+"---33333333333333333333333");}
																				else{
																					Switch_str = CW.getSSID();  System.out.println("444444444444444444444444"); 
																				}
																			}
																		}

				
//				/**
//				 * ��һ�������㵱ǰ��������������DS,������ȸ���Ԥ�ڵ���ֵDS(th),��ͣ����ԭ�����У����򣬽���ڶ���
//				 * �ڶ�����������ĺ�ѡ��������ۺϴ�֣��õ������ۺϴ����õ�����DS(best)�����������Ľ��DS(best)�뵱ǰ�����ֽ�����бȽϣ�
//				 * 		      ��DS(best)-DS<��������л�����ͣ���ڵ�ǰ�����С�
//				 * 		      ��DS(best)-DS>�����л����õ÷���õ�����
//				 */
//				WifiInfoItem CW = null;
//				for(int i=0;i<FISInfo.wifiInfo.size();++i){
//					 CW = FISInfo.wifiInfo.get(i);
//					 if(CW.isConnect() == true )
//						 break;  //�Ӽ�¼����FISInfo���ҵ���ǰ�����wifi�����ź�
//				}
//				computeFIS.CompleteNetWorkDegreeOfSatisfaction(CW);  //���㵱ǰ�����ֽ��
//				if(DS_th_ < CW.getNetScore()){   //��ǰ����ĵ÷ֽ��������ֵ
//					Switch_str = CW.getSSID();
//				}
//				else{ //��ǰ����ĵ÷ֽ��С����ֵ���ȼ������к�ѡ��������д�ֽ����ѡ�������ٶԱȵ�ǰ������
//					double best_score  = 0.0;
//					String best_SSID = "";
//					for(int i=0;i<FISInfo.wifiInfo.size();++i){
//						WifiInfoItem cw = FISInfo.wifiInfo.get(i);
//						if(cw.isConnect() == false){   //��ѡ����
//							computeFIS.Comple_Net_Score(cw);
//							if(best_score < cw.getNetScore()){
//								best_score = cw.getNetScore();
//								best_SSID = cw.getSSID();
//							}
//						}
//					}
//					
//					if(Math.abs(best_score-CW.getNetScore()) > Value_��){     //��Ѻ�ѡ����͵�ǰ������Ĳ����  ��  ���л����ú�ѡ������
//						Switch_str = best_SSID;
//					}else
//						Switch_str = CW.getSSID();
        		
        		
//				}
											
				//���ڽ������� �������ƶ��˶Ե��ֻ� 
				byte[] buf1 = Switch_str.getBytes();
				DatagramPacket dp1 = new DatagramPacket(buf1 , buf1.length , dp.getAddress(), dp.getPort());
				ds.send(dp1);   //ʹ��UDP��������												
				
				//break;
				//try {Thread.sleep(5 * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȴ�5�룬ÿ5���ϴ�һ�Σ�ִ��һ��(test��ɺ�ɾ��)
			}
		} 
        catch (IOException e) {
        	e.printStackTrace();
        }
        finally 
        {   //�ر���Դ
        	try {ds.close();}catch (Exception e){e.printStackTrace();}	
        }
	}

	/**
	 * ���������ƶ����Ŷ˵���Ϣ
	 * @param gotStr  ����һ���ַ�����ʽ�Ľ��
	 */
	private void dealWithDataFromAndroid(String gotStr) {
		
		FISInfo.wifiInfo.clear();
		
		String[] str = gotStr.split("&");
		
		String[] s = str[str.length-1].split("\\|");
		String[] subS = s[0].split(":"); FISInfo.setAndroidLatitude(Double.parseDouble(subS[1]));
		String[] subS1 = s[1].split(":"); FISInfo.setAndroidLongitude(Double.parseDouble(subS1[1]));
		String[] subS2 = s[2].split(":"); FISInfo.setAndroidSpeed(Double.parseDouble(subS2[1]));
		String[] subS3 = s[3].split(":"); FISInfo.setAndroidBearing(Double.parseDouble(subS3[1]));
		String[] subS4 = s[4].split(":"); FISInfo.setUserPref(Integer.parseInt(subS4[1]));
		String[] subS5 = s[5].split(":"); FISInfo.setUserBuf(Integer.parseInt(subS5[1]));

		for(int i=0;i<str.length-1;i++)
		{ 
			WifiInfoItem wifiInfo = new WifiInfoItem();
			String[] ss = str[i].split("\\|"); 
			String[] subs = ss[0].split(":"); wifiInfo.setSSID(subs[1]);   //����ssid
			String[] subs1 = ss[1].split(":"); 
			String[] subs2 = ss[2].split(":"); wifiInfo.setRssi(Integer.parseInt(subs2[1]));    //����rssi
			String[] subs3 = ss[3].split(":"); if(wifiInfo.isConnect()) wifiInfo.setAB(Double.parseDouble(subs3[1])); else wifiInfo.setAB( (Math.random()*200)+200 );   
			
			String[] S = subs[1].split("_");
			wifiInfo.setChargeLevel(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getChargeLevel());    //���ø�wifi���������
			wifiInfo.setLatitude(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getLatitude());        //���ø�wifi��γ��
			wifiInfo.setLongitude(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getLongitude());     // ���ø�wifi�ľ���ֵ
			wifiInfo.setNetScore(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getNet_Score());      //��ֽ��
			
			if(subs1[1].equals("false"))//���ý����Ƿ�
				wifiInfo.setConnect(false);
			else if(subs1[1].equals("true"))   //��ǰwifi���ֻ�����wifi
			{
				wifiInfo.setConnect(true);  
				FISInfo.setReferLongitude(FISInfo.getAndroidLongitude());    //�ο�λ�õľ���ֵΪ��ǰ�ֻ�λ�õľ�����ֵ
				FISInfo.setReferLatitude(wifiInfo.getLatitude());       //�ο�λ�õ�γ��ֵΪ��ǰ����� wifi�����λ�õ�γ��
			}
			//System.out.println(wifiInfo.getLongitude()+"--"+wifiInfo.getLatitude()+"--"+FISInfo.getAndroidLongitude()+"--"+FISInfo.getAndroidLatitude());
			wifiInfo.setDistaFromAndroid(getDistance(wifiInfo.getLongitude() ,wifiInfo.getLatitude() , FISInfo.getAndroidLongitude(), FISInfo.getAndroidLatitude()));       //getDistance�ĸ�����					 
			//getDistance3(108.9761222 ,34.2524816 , 108.9760222, 34.2523816)
			//getDistance3(wifiInfo.getLongitude() ,wifiInfo.getLatitude() , FISInfo.getAndroidLongitude(), FISInfo.getAndroidLatitude());	
			FISInfo.wifiInfo.add(wifiInfo);
		}
		
		for(int i=0;i<FISInfo.wifiInfo.size();i++)
		{ 
			WifiInfoItem wifiInfo = FISInfo.wifiInfo.get(i);  
			
			wifiInfo.setAngle(CompleteAngle(FISInfo.getAndroidBearing(), 
											FISInfo.getAndroidLongitude(), FISInfo.getAndroidLatitude(), 
											FISInfo.getReferLongitude(), FISInfo.getReferLatitude() ,
											wifiInfo.getLongitude(), wifiInfo.getLatitude(),
											wifiInfo.getDistaFromAndroid())); 
		}
	}
	
	/**
	 * ͨ���ֻ���ǰ�ƶ������뱱��ļнǣ��ֻ���ǰλ�õľ�γ�ȣ��ο�λ�õľ�γ�ȣ�wifi�����ľ�γ��
	 * ������AndroidAngle: android�ƶ����������ļн�
	 * 		AndLong,AndLat: ��ʱandroid�ֻ���λ�þ�γ��
	 * 		RefeLong,RefeLat: ��ʱ�ο�λ�õľ�γ��
	 * 		WifiLong ,WifiLat��wifi�����ľ�γ��
	 *		wifi_andr_len: android�ƶ��˺�wifi�����ľ���
	 * 
	 * ���أ� �ƶ���������֮����ƶ��ļн�
	 */
	private double CompleteAngle(double AndroidAngle,double AndLong,double AndLat,double RefeLong,double RefeLat,double WifiLong ,double WifiLat, double wifi_andr_len){

		double len1 = getDistance(AndLong, AndLat, RefeLong, RefeLat);   //�ƶ���λ�úͲο���ľ��루��λ���ף�
		double len2 = getDistance(WifiLong, WifiLat, RefeLong, RefeLat);   //wifi�����λ�úͲο���ľ��루��λ���ף�
		double len3 = wifi_andr_len;  //android�ƶ��˺�wifi�����ľ���
		
		double AngleWithN = getAngle(len2,len1,len3);    //�������ļн�
		
		if(AndLong > WifiLong){   //�ֻ��ľ��ߴ���wifi��ľ���
			if(AndLat<WifiLat){  
				if(AndroidAngle > 180){
					AndroidAngle = 360 - AndroidAngle;
					return Math.abs(AndroidAngle - AngleWithN);
				}
				else
				{
					double temp =  AndroidAngle + AngleWithN;
					if(temp > 180.0)
						return 360-temp;
					else
						return temp;
				}
			}
			else{
				AngleWithN = 180 - AngleWithN;
				if(AndroidAngle > 180){
					AndroidAngle = 360 - AndroidAngle;
					return Math.abs(AndroidAngle - AngleWithN);
				}
				else{
					double temp =  AndroidAngle + AngleWithN;
					if(temp > 180.0)
						return 360-temp;
					else
						return temp;
				}
			}
		}
		else{//�ֻ��ľ���С��wifi��ľ���
			if(AndLat<WifiLat){
				if(AndroidAngle > 180){
					AndroidAngle = 360 - AndroidAngle;
					double temp =  AndroidAngle + AngleWithN;
					if(temp > 180.0)
						return 360-temp;
					else
						return temp;
				}
				else
				{
					return Math.abs(AndroidAngle - AngleWithN);
				}
			}
			else{
				AngleWithN = 180 - AngleWithN;
				if(AndroidAngle > 180){
					AndroidAngle = 360 - AndroidAngle;
					double temp =  AndroidAngle + AngleWithN;
					if(temp > 180.0)
						return 360-temp;
					else
						return temp;
				}
				else
				{
					return Math.abs(AndroidAngle - AngleWithN);
				}
			}
		}
	}

	//���ؽǶȣ���Χ0-180�ȣ�
	private double getAngle(double n1, double n2, double n3) {	
		return (Math.acos((Math.pow(n2, 2) + Math.pow(n3, 2) - Math.pow(n1, 2)) / (2 * n2 * n3))) * (180/Math.PI);
	}

	/**
	 * ��������GPS��֮��ľ���
	 * @param longitude  ��һ��GPSλ�õľ���ֵ
	 * @param latitude  ��һ��GPSλ�õ� γ��ֵ
	 * @param androidLongitude   �ڶ���GPSλ�õľ���
	 * @param androidLatitude   �ڶ���GPSλ�õ� γ��
	 * @return
	 */
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

}
