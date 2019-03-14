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

	private int startValue = 0;   //��Ϊ��һ��ʱ϶�������ڿ�ʼ�ͽ�βʱ��ȥֵ��������ʱ϶��������
	private int endValue = 0;
	private int throughputInATimeSlot = 0;
	private double segmentCountInATimeSlot = 0;   //�൱��buffer  b(t)
	private double streamedSegmentInATimeSlot = 0 ;   //һ��ʱ��Ƭ��������ƵƬ�εĸ��� d(t)
	private int TimeSlot = 10;  //ÿһ��ʱ��ۣ���λ����ֵ
	private int segmentLen = 10;   //��λ����ֵ
	private int feedbackPort = 0;
	private double constant_K = 0.06 ;   //kֵ�ķ�Χ  ϣ��Խ��
	private double constant_V = 0; //   //vֵ�ķ�Χ ϣ��ԽС 
		/*
		 * k = 0.06 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.07 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.08 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.09 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 * k = 0.10 v={1,3,5,7,8,9,10,11,12,13,14,15,16,17,19,21,23,30}
		 */
	
	private double constant_Q = 0;  
	private int previousBitRateLevel = 4;   //��ʼʱ����ߵȼ� ������������
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
		BitrateLevels.put(1, 1000);    // ��Ƶ���ʵȼ����Լ���Ӧ����
		BitrateLevels.put(2, 1200);
		BitrateLevels.put(3, 1400);
		BitrateLevels.put(4, 1600);
		
		A_result = new double[BitrateLevels.size()];
		B_result = new double[BitrateLevels.size()];
		for(int i=0 ; i<BitrateLevels.size() ;i++)
		{  //��ʼ��
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
		requestLevel = previousBitRateLevel ; //Ĭ��ֵ
		
		while(true)   //������ѭ��
		{	
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȴ��룬ÿ����ִ��һ��
			
			throughputInATimeSlot = NativeHandler.Get_throughput();  //��ȡ��3��ʱ��Ƭ��������
			
			segmentCountInATimeSlot = NativeHandler.get_FrameQueue_C()/204.0; //Get_segmentCount();  //��ȡ��10����buffer�е������� ��ƵƬ�εĸ�����ʵ����֡�ĸ�����
			
			//segmentCountInATimeSlot = NativeHandler.Get_streamedSegment()/204.0 + NativeHandler.get_FrameQueue_C()/204.0;   //b(t)   ��ȡ��3���������� ��ƵƬ�εĸ�����ʵ����nalu�ĸ�����
			
			System.out.println("����������:"+throughputInATimeSlot+"-------buffer:"+(NativeHandler.Get_streamedSegment()/204.0 + NativeHandler.get_FrameQueue_C()/204.0));
	
			for(int i=0 ; i<BitrateLevels.size() ; i++ ){
				B_result[i] = computeRequestLevelNextSlot(segmentCountInATimeSlot , throughputInATimeSlot , i+1 );
				//System.out.println("---result["+i+"]="+B_result[i]);/////////////
			}
				
			//ѡ���������Сֵ
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
	
	public void wirte_stat2FILE(int throughputInATimeSlot2){		//��ͳ�ƽ��д���ļ�
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
		
		double Ut = Math.log( throughputInATimeSlot/(aBitrateLevel*1000/8) + 1) - constant_K * (Math.exp(Math.abs(i - previousBitRateLevel)) - 1) ;  //�û�����ֵ
		
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
			
			if(CollectFISDataThread.getWifiSwitchStat().equals("switching")){   //���wifi�����л��У�����ͣ���ͣ���Ϣ20�����룬���ͷ���
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
					System.err.println("UDP�����ˣ�����������������������������"); 
					e.printStackTrace();
				}
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
}
