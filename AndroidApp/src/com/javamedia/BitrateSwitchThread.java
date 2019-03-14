package com.javamedia;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
//import java.math.*;

public class BitrateSwitchThread implements Runnable {

	private int startValue = 0;   //因为是一个时隙，所以在开始和结尾时刻去值来获得这个时隙的吞吐量
	private int endValue = 0;
	private int throughputInATimeSlot = 0;
	private double segmentCountInATimeSlot = 0;   //相当于buffer  b(t)
	private double streamedSegmentInATimeSlot = 0 ;   //一个时间片流化的视频片段的个数 d(t)
	private int TimeSlot = 10;  //每一个时间槽，单位是秒值
	private int segmentLen = 10;   //单位是秒值
	private int feedbackPort = 0;
	private double constant_K = 0.06 ;   //k值的范围  希望越大
	private double constant_V = 0; //   //v值的范围 希望越小 
		/*
		 * k = 0.06 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.07 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.08 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.09 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.10 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 */
	
	private double constant_Q = 0;  
	private int previousBitRateLevel = 4;   //初始时用最高等级 的码率来发送
	private static int requestLevel;
	


	private InetAddress inetAdd;
	private DatagramSocket ds;
	private static Map<Integer,Integer> BitrateLevels ; 
	private double[] A_result ;
	private double[] B_result ;
	private RandomAccessFile out ;
	private FileOutputStream fos = null;
	
	public BitrateSwitchThread(InetAddress inetAdd,int feedbackPort){
		this.inetAdd = inetAdd;
		this.feedbackPort = feedbackPort;
		
		BitrateLevels = new HashMap<Integer,Integer>();
		BitrateLevels.put(1, 1000);    // 视频码率等级，以及对应码率
		BitrateLevels.put(2, 1200);
		BitrateLevels.put(3, 1400);
		BitrateLevels.put(4, 1600);
		
		A_result = new double[BitrateLevels.size()];
		B_result = new double[BitrateLevels.size()];
		for(int i=0 ; i<BitrateLevels.size() ;i++)
		{  //初始化
			A_result[i] = 0;
			B_result[i] = 0;
		}
		
		try{fos = new FileOutputStream(new File("/storage/emulated/0/MPlayer/throughput_stat_fp.dat"));}catch(IOException e){}
	}
	
	public static int getRequestLevel() {
		return BitrateLevels.get(requestLevel) ;  //requestLevel;
	}
	@Override
	public void run(){
		
		try {ds = new DatagramSocket(feedbackPort);} catch (SocketException e) {e.printStackTrace();}
		constant_Q = TimeSlot * 1.0 / segmentLen ;
		requestLevel = previousBitRateLevel ; //默认值
		
		while(true)   //无限死循环
		{	
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //等待秒，每三秒执行一次
			
			throughputInATimeSlot = NativeHandler.Get_throughput();  //获取此3秒时间片的吞吐量
			
			segmentCountInATimeSlot = NativeHandler.get_FrameQueue_C()/204.0; //Get_segmentCount();  //获取此10秒中buffer中的流化的 视频片段的个数（实际是帧的个数）
			
			//segmentCountInATimeSlot = NativeHandler.Get_streamedSegment()/204.0 + NativeHandler.get_FrameQueue_C()/204.0;   //b(t)   获取此3秒中流化的 视频片段的个数（实际是nalu的个数）
			
			System.out.println("——吞吐量:"+throughputInATimeSlot+"-------buffer:"+(NativeHandler.Get_streamedSegment()/204.0 + NativeHandler.get_FrameQueue_C()/204.0));
	
			for(int i=0 ; i<BitrateLevels.size() ; i++ ){
				B_result[i] = computeRequestLevelNextSlot(segmentCountInATimeSlot , throughputInATimeSlot , i+1 );
				//System.out.println("---result["+i+"]="+B_result[i]);/////////////
			}
				
			//选出计算的最小值
			double min = B_result[0];
			requestLevel = 1;
			for(int i=0;i<BitrateLevels.size();i++){
				if(min > B_result[i])
				{
					min = B_result[i];
					requestLevel = i+1;
				}
			}
		
			System.out.println("---------------------------------------------------requestLevel:"+requestLevel);
			previousBitRateLevel = requestLevel ;
			
			SendOrder(requestLevel+"");
	
		}
	}
	
	public void wirte_stat2FILE(int throughputInATimeSlot2){		//将统计结果写入文件
		try {
			fos.write((throughputInATimeSlot2 + "" + "|").getBytes());
			fos.flush();
		}catch(IOException e){e.printStackTrace();}
	}

	private double computeRequestLevelNextSlot(double segmentCountInATimeSlot,int throughputInATimeSlot, int i) 
	{
		
		int aBitrateLevel = BitrateLevels.get(i);
		
		streamedSegmentInATimeSlot = throughputInATimeSlot /(segmentLen * aBitrateLevel * 1000 / 8.0 );  
		
		double B = Math.pow(streamedSegmentInATimeSlot,2)/2 + Math.pow(constant_Q,2)/2 - constant_Q*streamedSegmentInATimeSlot ;
		
		double Ut = Math.log( throughputInATimeSlot/(aBitrateLevel*1000/8) + 1) - constant_K * (Math.exp(Math.abs(i - previousBitRateLevel)) - 1) ;  //用户体验值
		
		double Re = B + segmentCountInATimeSlot * (streamedSegmentInATimeSlot - constant_Q) -  constant_V * Ut ;
		
		return 	Re; 
		
	}
	

	private void SendOrder(String order)
	{
		try{
			String str = order;
			byte[] buf = str.getBytes();
			DatagramPacket dp = new DatagramPacket(buf , buf.length , this.inetAdd , this.feedbackPort);
			
			//System.out.println("sender......level..........");
			
			if(CollectFISDataThread.getWifiSwitchStat().equals("switching")){   //如果wifi正在切换中，就暂停发送，休息20个毫秒，再送发送
				System.out.println("sleep.......100......................sender");
				try {Thread.sleep(100);}catch(InterruptedException e){e.printStackTrace();}
			}
			else if(CollectFISDataThread.getWifiSwitchStat().equals("switched"))
			{
				try{
					ds.send(dp);
				}
				catch(Exception e)
				{
					System.err.println("UDP出错了！！！！！！！！！！！！！！！"); 
					e.printStackTrace();
				}
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
}
