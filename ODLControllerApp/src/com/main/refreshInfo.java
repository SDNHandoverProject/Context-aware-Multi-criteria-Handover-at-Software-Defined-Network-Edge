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
	* 获取当前网络的所有交换机的相关信息
	* http://127.0.0.1:8181/restconf/operational/opendaylight-inventory:nodes 获取当前网络的所有交换机的相关信息
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
			this.contrApp.setText2Area2(TopoInfo.CurrentTopoInfoMap);       //将CurrentTopoInfoMap中的当前网络中所有的switchs节点显示在textArea上面
			
			
			 
			
			
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //等待秒，每*秒执行一次
		}
	}
	
	//内部类：通过rest API 获取拓扑网络中of交换机节点信息
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
	        HttpRequest.sendGet(url,"");    //调用获取数据
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
