package com.info;

public class WifiInfoItem {

	private String SSID;    //wifi��SSID
	private int Rssi;      //wifi��ǰ�ֻ���֪�����ź�ǿ��
	private boolean isConnect;  //�Ƿ��뵱ǰ���wifi�źŽ���
	private double AB = 0.0;     //������� ��wifi�źţ���ʱ��������Ƕ���
	private int ChargeLevel;    //���wifi����ķ��õȼ�
	private double DistaFromAndroid;  //����������android�ֻ��ľ���
	private double Longitude;     //����
	private double Latitude;     //γ��
	private double Angle;  //�Ƕ� ���ƶ�����wifi������ƶ�����Ƕ�
	private double NetScore;   //����Ĵ�ֽ��

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