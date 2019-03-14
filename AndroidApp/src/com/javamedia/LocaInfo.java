package com.javamedia;

public class LocaInfo {
	
	private double cGPSLatitude;    /**当前GPS位置中的：纬度*/
	private double cGPSLongitude;    /**当前GPS位置中的：经度*/
	private float cGPSBearing;    /**当前GPS位置中的：偏离正北方的度数*/
	private float cGPSSpeed;     /**当前GPS位置中的：速度*/
	
	private LocaInfo(){}     /**私有化构造函数*/
	private static LocaInfo locaInfo=null;  
    //静态工厂方法   
    public static LocaInfo getInstance() {           /**单例设计模式*/
         if (locaInfo == null) {    
        	 locaInfo = new LocaInfo();  
         }    
        return locaInfo;  
    }  
	
	public double getcGPSLatitude() {return cGPSLatitude;}
	public void setcGPSLatitude(double cGPSLatitude) 
	{
		this.cGPSLatitude = cGPSLatitude;
	}
	
	public double getcGPSLongitude(){return cGPSLongitude;}
	public void setcGPSLongitude(double cGPSLongitude) 
	{
		this.cGPSLongitude = cGPSLongitude;
	}
	
	public float getcGPSBearing() {return cGPSBearing;}
	public void setcGPSBearing(float cGPSBearing) 
	{
		this.cGPSBearing = cGPSBearing;
	}
	
	public float getcGPSSpeed(){return cGPSSpeed;}
	public void setcGPSSpeed(float cGPSSpeed) 
	{
		this.cGPSSpeed = cGPSSpeed;
	}
	
}
