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
	private double DS_th_ = 0.3; //满意度阈值
	private double Value_δ = 0.05; //最佳候选网络和当前面网络的差值δ
	
	public String getSwitch_str() {return Switch_str;}
	
	public getFISInfo(controllerApp contrApp,String ControllerHostIp ,int ControllerAppPort)
	{
		this.ControllerAppHostIp = ControllerHostIp;
		this.ControllerAppPort = ControllerAppPort;
		this.contrApp = contrApp;
	}

	@Override
	public void run(){

		//开启虚拟Android数据收集线程
		//////////////////////////////////////////////////////////////////////////////////////////////
		try {Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}
		new Thread(new AndroidInfoTest()).start(); //开启测试线程
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		try {
			ds = new DatagramSocket(ControllerAppPort);
			
        	while(true)
        	{
        		//接收消息
        		byte[] buf= new byte[1024];
        		DatagramPacket dp = new DatagramPacket(buf,buf.length);
        		ds.receive(dp);

        		//处理从android端接收到的信息
        		String GotStr = new String(dp.getData(),0,dp.getLength());				System.out.println("GotStr: "+GotStr);	
        		
        		//String GotStr = "";
				dealWithDataFromAndroid(GotStr);   //将收到的
				this.contrApp.setText2Area1(); //将信息显示在frame中	
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
																				 break;  //从记录数据FISInfo中找到当前接入的wifi网络信号
																		}
																		
																		if(i == FISInfo.wifiInfo.size())   //如果i是因为循环完毕而跳出，那么是没有Connected等于true的,计算后取打分值最大的那个网络接入
																		{
																			double best_score = 0.0;
																			String best_SSID = "";
																			for(int j=0;j<FISInfo.wifiInfo.size();++j){
																				WifiInfoItem cw = FISInfo.wifiInfo.get(j);
																				if(cw.isConnect() == false){   //候选网络
																					computeFIS.Comple_Net_Score(cw);
																					if(best_score < cw.getNetScore()){
																						best_score = cw.getNetScore();
																						best_SSID = cw.getSSID();
																					}
																				}
																			}
																			Switch_str = best_SSID; System.out.println("1111111111111111111111");	
																		}
																		else  //有Connected等于true
																		{
																			computeFIS.CompleteNetWorkDegreeOfSatisfaction(CW);  //计算当前网络打分结果
																			if(DS_th_ < CW.getNetScore()){   //当前网络的得分结果大于阈值
																				Switch_str = CW.getSSID();    System.out.println("22222222222222222222222");
																			}
																			else{ //当前网络的得分结果小于阈值，先计算所有候选网络的所有打分结果，选出最优再对比当前网络结果
																				double best_score  = 0.0;
																				String best_SSID = "";
																				for(int r=0;r<FISInfo.wifiInfo.size();++r){
																					WifiInfoItem cw = FISInfo.wifiInfo.get(r);
																					if(cw.isConnect() == false){   //候选网络
																						computeFIS.Comple_Net_Score(cw);
																						if(best_score < cw.getNetScore()){
																							best_score = cw.getNetScore();
																							best_SSID = cw.getSSID(); 
																						}
																					}
																				}
																				
																				if(Math.abs(best_score-CW.getNetScore()) > Value_δ)     //最佳候选网络和当前面网络的差大于  δ  ，切换至该候选网络中
																				{	Switch_str = best_SSID;    System.out.println(CW.getNetScore()+"---33333333333333333333333");}
																				else{
																					Switch_str = CW.getSSID();  System.out.println("444444444444444444444444"); 
																				}
																			}
																		}

				
//				/**
//				 * 第一步：计算当前接入网络的满意度DS,若满意度高于预期的阈值DS(th),则停留在原网络中，否则，进入第二部
//				 * 第二步：对其余的候选网络进行综合打分，得到其中综合打分最好的网络DS(best)。将最佳网络的结果DS(best)与当前网络打分结果进行比较，
//				 * 		      若DS(best)-DS<δ则放弃切换继续停留在当前网络中。
//				 * 		      若DS(best)-DS>δ则切换到该得分最好的网络
//				 */
//				WifiInfoItem CW = null;
//				for(int i=0;i<FISInfo.wifiInfo.size();++i){
//					 CW = FISInfo.wifiInfo.get(i);
//					 if(CW.isConnect() == true )
//						 break;  //从记录数据FISInfo中找到当前接入的wifi网络信号
//				}
//				computeFIS.CompleteNetWorkDegreeOfSatisfaction(CW);  //计算当前网络打分结果
//				if(DS_th_ < CW.getNetScore()){   //当前网络的得分结果大于阈值
//					Switch_str = CW.getSSID();
//				}
//				else{ //当前网络的得分结果小于阈值，先计算所有候选网络的所有打分结果，选出最优再对比当前网络结果
//					double best_score  = 0.0;
//					String best_SSID = "";
//					for(int i=0;i<FISInfo.wifiInfo.size();++i){
//						WifiInfoItem cw = FISInfo.wifiInfo.get(i);
//						if(cw.isConnect() == false){   //候选网络
//							computeFIS.Comple_Net_Score(cw);
//							if(best_score < cw.getNetScore()){
//								best_score = cw.getNetScore();
//								best_SSID = cw.getSSID();
//							}
//						}
//					}
//					
//					if(Math.abs(best_score-CW.getNetScore()) > Value_δ){     //最佳候选网络和当前面网络的差大于  δ  ，切换至该候选网络中
//						Switch_str = best_SSID;
//					}else
//						Switch_str = CW.getSSID();
        		
        		
//				}
											
				//用于将计算结果 反馈给移动端对的手机 
				byte[] buf1 = Switch_str.getBytes();
				DatagramPacket dp1 = new DatagramPacket(buf1 , buf1.length , dp.getAddress(), dp.getPort());
				ds.send(dp1);   //使用UDP发送数据												
				
				//break;
				//try {Thread.sleep(5 * 1000);}catch(InterruptedException e){e.printStackTrace();}   //等待5秒，每5秒上传一次，执行一次(test完成后删除)
			}
		} 
        catch (IOException e) {
        	e.printStackTrace();
        }
        finally 
        {   //关闭资源
        	try {ds.close();}catch (Exception e){e.printStackTrace();}	
        }
	}

	/**
	 * 处理来自移动播放端的信息
	 * @param gotStr  接收一个字符串形式的结果
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
			String[] subs = ss[0].split(":"); wifiInfo.setSSID(subs[1]);   //设置ssid
			String[] subs1 = ss[1].split(":"); 
			String[] subs2 = ss[2].split(":"); wifiInfo.setRssi(Integer.parseInt(subs2[1]));    //设置rssi
			String[] subs3 = ss[3].split(":"); if(wifiInfo.isConnect()) wifiInfo.setAB(Double.parseDouble(subs3[1])); else wifiInfo.setAB( (Math.random()*200)+200 );   
			
			String[] S = subs[1].split("_");
			wifiInfo.setChargeLevel(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getChargeLevel());    //设置该wifi的网络费用
			wifiInfo.setLatitude(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getLatitude());        //设置该wifi的纬度
			wifiInfo.setLongitude(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getLongitude());     // 设置该wifi的经度值
			wifiInfo.setNetScore(TopoInfo.CurrentTopoInfoMap.get("ovs_"+S[1]).getNet_Score());      //打分结果
			
			if(subs1[1].equals("false"))//设置接入是否
				wifiInfo.setConnect(false);
			else if(subs1[1].equals("true"))   //当前wifi是手机接入wifi
			{
				wifiInfo.setConnect(true);  
				FISInfo.setReferLongitude(FISInfo.getAndroidLongitude());    //参考位置的经度值为当前手机位置的经度数值
				FISInfo.setReferLatitude(wifiInfo.getLatitude());       //参考位置的纬度值为当前接入的 wifi接入点位置的纬度
			}
			//System.out.println(wifiInfo.getLongitude()+"--"+wifiInfo.getLatitude()+"--"+FISInfo.getAndroidLongitude()+"--"+FISInfo.getAndroidLatitude());
			wifiInfo.setDistaFromAndroid(getDistance(wifiInfo.getLongitude() ,wifiInfo.getLatitude() , FISInfo.getAndroidLongitude(), FISInfo.getAndroidLatitude()));       //getDistance四个参数					 
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
	 * 通过手机当前移动方向与北向的夹角，手机当前位置的经纬度，参考位置的经纬度，wifi接入点的经纬度
	 * 参数：AndroidAngle: android移动端与正北的夹角
	 * 		AndLong,AndLat: 此时android手机的位置经纬度
	 * 		RefeLong,RefeLat: 此时参考位置的经纬度
	 * 		WifiLong ,WifiLat：wifi接入点的经纬度
	 *		wifi_andr_len: android移动端和wifi接入点的距离
	 * 
	 * 返回： 移动端与接入点之间的移动的夹角
	 */
	private double CompleteAngle(double AndroidAngle,double AndLong,double AndLat,double RefeLong,double RefeLat,double WifiLong ,double WifiLat, double wifi_andr_len){

		double len1 = getDistance(AndLong, AndLat, RefeLong, RefeLat);   //移动端位置和参考点的距离（单位：米）
		double len2 = getDistance(WifiLong, WifiLat, RefeLong, RefeLat);   //wifi接入点位置和参考点的距离（单位：米）
		double len3 = wifi_andr_len;  //android移动端和wifi接入点的距离
		
		double AngleWithN = getAngle(len2,len1,len3);    //与正北的夹角
		
		if(AndLong > WifiLong){   //手机的经线大于wifi点的经线
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
		else{//手机的经线小于wifi点的经线
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

	//返回角度（范围0-180度）
	private double getAngle(double n1, double n2, double n3) {	
		return (Math.acos((Math.pow(n2, 2) + Math.pow(n3, 2) - Math.pow(n1, 2)) / (2 * n2 * n3))) * (180/Math.PI);
	}

	/**
	 * 计算两个GPS点之间的距离
	 * @param longitude  第一个GPS位置的经度值
	 * @param latitude  第一个GPS位置的 纬度值
	 * @param androidLongitude   第二个GPS位置的经度
	 * @param androidLatitude   第二个GPS位置的 纬度
	 * @return
	 */
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

}
