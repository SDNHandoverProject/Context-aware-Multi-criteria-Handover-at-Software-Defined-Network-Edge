package com.main;

import java.util.*;

import com.Test.AndroidInfoTest;
import com.info.GPSInfo;
import com.info.TopoInfo;

public class refreshInfo implements Runnable{
	
	private static int TimeSlot = 20; 
	private controllerApp contrApp;
	private String ControllerHostIp ;
	private int ControllerPort;
	private String Request = "/restconf/operational/opendaylight-inventory:nodes";       
				
	/**
	* ��ȡ��ǰ��������н������������Ϣ
	* http://127.0.0.1:8181/restconf/operational/opendaylight-inventory:nodes ��ȡ��ǰ��������н������������Ϣ
	*/
	
	public refreshInfo(controllerApp contrApp , String controllerHostIp, int port){
		super();
		this.ControllerHostIp = controllerHostIp;
		this.ControllerPort = port;
		this.contrApp = contrApp;
	    //System.out.println(ControllerHostIp+"---------------"+port);
	}
	
	
	@Override
	public void run(){
		
		OdlUtil odlUtil = new OdlUtil(ControllerHostIp,ControllerPort,Request);
        
		while(true)
		{
			odlUtil.getTopology();
			this.contrApp.setText2Area2(TopoInfo.CurrentTopoInfoMap);       //��CurrentTopoInfoMap�еĵ�ǰ���������е�switchs�ڵ���ʾ��textArea����
			
			
			 
			
			
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȴ��룬ÿ*��ִ��һ��
		}
	}
	
	//�ڲ��ࣺͨ��rest API ��ȡ����������of�������ڵ���Ϣ
	class OdlUtil{
	    private String url = "";

	    public OdlUtil(String host,int port,String Request){
	        this.url = "http://" + host + ":" + port + Request;
	    }

	    public String getTopology(){
	        return getTopology("node/OF");
	    }

	    public String getTopology(String containerName, String username, String password) {
	        HttpRequest.setBasicAuth(getBasicAuthStr(username,password));
	        HttpRequest.sendGet(url,"");    //���û�ȡ����
	        //String str = HttpRequest.sendGet(url,"");
	        //System.out.println(str);
	        return null;
	    }

	    public String getTopology(String containerName){
	        getTopology(containerName, "admin","admin");
	        return null;
	    }

	    private String getBasicAuthStr(String name,String password){
	        return "Basic " + Base64.getEncoder().encodeToString((name + ":" + password).getBytes());
	    }

	}

}
