package com.info;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TopoInfo {
	
	private static int Node_Numbers = 30;
	
	public static Map<String,GPSInfo>  AllTopoInfoMap;   //ȫ����switch�ڵ�gpsλ����Ϣ
	public static Map<String,GPSInfo>  CurrentTopoInfoMap;   //ȫ����switch�ڵ�gpsλ����Ϣ
	static{
		AllTopoInfoMap = new HashMap<String,GPSInfo>();    
		
//		AllTopoInfoMap.put("ovs_1", new GPSInfo(100.00001, 30.00099,1));    //ǰ���1-10�ţ��Լ�20-30�������ڽ����Դ����������
//		AllTopoInfoMap.put("ovs_2", new GPSInfo(100.00128, 30.00, 8));
//		AllTopoInfoMap.put("ovs_3", new GPSInfo(100.00, 30.00, 9));
//		AllTopoInfoMap.put("ovs_4", new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_5", new GPSInfo(100.00, 30.00, 4));
//		AllTopoInfoMap.put("ovs_6", new GPSInfo(100.00, 30.00, 1));
//		AllTopoInfoMap.put("ovs_7", new GPSInfo(100.00, 30.00, 5));
//		AllTopoInfoMap.put("ovs_8", new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_9", new GPSInfo(100.00, 30.00, 2));
//		AllTopoInfoMap.put("ovs_10",new GPSInfo(100.00, 30.00, 1));
//		
//		AllTopoInfoMap.put("ovs_20",new GPSInfo(100.00, 30.00, 1));    
//		AllTopoInfoMap.put("ovs_21",new GPSInfo(100.00, 30.00, 8));
//		AllTopoInfoMap.put("ovs_22",new GPSInfo(100.00, 30.00, 9));
//		AllTopoInfoMap.put("ovs_23",new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_24",new GPSInfo(100.00, 30.00, 4));
//		AllTopoInfoMap.put("ovs_25",new GPSInfo(100.00, 30.00, 1));
//		AllTopoInfoMap.put("ovs_26",new GPSInfo(100.00, 30.00, 5));
//		AllTopoInfoMap.put("ovs_27",new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_28",new GPSInfo(100.00, 30.00, 2));
//		AllTopoInfoMap.put("ovs_29",new GPSInfo(100.00, 30.00, 1));
//		AllTopoInfoMap.put("ovs_30",new GPSInfo(100.00, 30.00, 1));
//		
//		AllTopoInfoMap.put("ovs_11",new GPSInfo(100.00, 30.00, 1));    //��Щ��������ģ����Ҫ�ռ���ȷλ������
//		AllTopoInfoMap.put("ovs_12",new GPSInfo(100.00, 30.00, 8));
//		AllTopoInfoMap.put("ovs_13",new GPSInfo(100.00, 30.00, 9));
//		AllTopoInfoMap.put("ovs_14",new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_15",new GPSInfo(100.00, 30.00, 4));
//		AllTopoInfoMap.put("ovs_16",new GPSInfo(100.00, 30.00, 1));
//		AllTopoInfoMap.put("ovs_17",new GPSInfo(100.00, 30.00, 5));
//		AllTopoInfoMap.put("ovs_18",new GPSInfo(100.00, 30.00, 3));
//		AllTopoInfoMap.put("ovs_19",new GPSInfo(100.00, 30.00, 2));
		
		
		/**���Բ��ִ��룬��Ϊ�������λ��
		 * ģ����200*100�������ڲ���30�������ڵ㣬ÿ����������λ�����ݸ�������ͼ��ָ����
		 * ȡ������������Ͻǵ�GPSλ��Ϊ������100.00000�� γ��30.00000��
		 * ȡ��������������ǵ�GPSλ��Ϊ������100.00000�� γ��30.00100��
		 * ȡ��������Ķ��Ͻǵ�GPSλ��Ϊ������100.00200�� γ��30.00000��
		 * ȡ��������Ķ����ǵ�GPSλ��Ϊ������100.00200�� γ��30.00100��
		 * 
		 * ���н������ڵ��λ��������Ϣ��ȡֵ��Χ���������Χ
		 */
		Set<Double> longset=new HashSet<Double>();
		Set<Double> latset=new HashSet<Double>();
		int count=1;
		while(count<=Node_Numbers){
			double tmp=100.00000 + ((int)(Math.random()*200)+1)/100000.0;
			if(longset.add(tmp))
				count++;
		}
		count=1;
		while(count<=Node_Numbers){
			double tmp=30.00000 + ((int)(Math.random()*100)+1)/100000.0;
			if(latset.add(tmp))
				count++;
		}
		
		Iterator<Double> Longiter=longset.iterator();
		Iterator<Double> Latiter=latset.iterator();
		int i=1;
		while(Longiter.hasNext() && Latiter.hasNext()){
			double Long = Longiter.next();
			double Lat = Latiter.next();
			GPSInfo gpsInfo= new GPSInfo(Long, Lat , (int)(Math.random()*9+1) );
			gpsInfo.setCoverage_Area( 51-(int)(Math.random()*30+1) );   //�������(tset��ɺ�ɾ��)�����Ƿ�Χ�뾶r��20-50��
									//System.out.println("ovs_"+ i +"-long:"+Long+"-lat:"+Lat+"-fee:"+(int)(Math.random()*9+1)+"-Coverage:"+gpsInfo.getCoverage_Area());
			AllTopoInfoMap.put("ovs_" + i++ , gpsInfo);
		}
		 
		
		CurrentTopoInfoMap = new ConcurrentHashMap<String,GPSInfo>();      //��ΪҪ���ж��̶߳�д��˵��ʹ��ConcurrentHashMap����
	}
}
