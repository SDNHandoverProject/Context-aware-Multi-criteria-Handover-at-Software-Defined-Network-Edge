package com.javamedia;

public class LocaInfo {
	
	private double cGPSLatitude;    /**��ǰGPSλ���еģ�γ��*/
	private double cGPSLongitude;    /**��ǰGPSλ���еģ�����*/
	private float cGPSBearing;    /**��ǰGPSλ���еģ�ƫ���������Ķ���*/
	private float cGPSSpeed;     /**��ǰGPSλ���еģ��ٶ�*/
	
	private LocaInfo(){}     /**˽�л����캯��*/
	private static LocaInfo locaInfo=null;  
    //��̬��������   
    public static LocaInfo getInstance() {           /**�������ģʽ*/
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
