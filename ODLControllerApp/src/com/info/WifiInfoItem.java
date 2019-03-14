package com.info;

public class WifiInfoItem {

	private String SSID;    //wifi的SSID
	private int Rssi;      //wifi当前手机感知到的信号强度
	private boolean isConnect;  //是否与当前这个wifi信号接入
	private double AB = 0.0;     //如果接入 的wifi信号，此时网络带宽是多少
	private int ChargeLevel;    //这个wifi网络的费用等级
	private double DistaFromAndroid;  //这个接入点与android手机的距离
	private double Longitude;     //经度
	private double Latitude;     //纬度
	private double Angle;  //角度 ，移动端与wifi接入点移动接入角度
	private double NetScore;   //网络的打分结果

	public double getAngle() {return Angle;}
	public void setAngle(double angle) {Angle = angle;}
	public double getDistaFromAndroid(){return DistaFromAndroid;}
	public void setDistaFromAndroid(double distaFromAndroid){DistaFromAndroid = distaFromAndroid;}
	public double getLongitude(){return Longitude;}
	public void setLongitude(double longitude){Longitude = longitude;}
	public double getLatitude(){return Latitude;}
	public void setLatitude(double latitude){Latitude = latitude;}
	public String getSSID(){return SSID;}
	public void setSSID(String sSID){SSID = sSID;}
	public int getRssi(){return Rssi;}
	public void setRssi(int rssi){Rssi = rssi;}
	public boolean isConnect(){return isConnect;}
	public void setConnect(boolean isConnect){this.isConnect = isConnect;}
	public double getAB(){return AB;}
	public void setAB(double aB){AB = aB;}
	public int getChargeLevel(){return ChargeLevel;}
	public void setChargeLevel(int chargeLevel){ChargeLevel = chargeLevel;}
	public double getNetScore(){return NetScore;}
	public void setNetScore(double netScore) {NetScore = netScore;}
	
}