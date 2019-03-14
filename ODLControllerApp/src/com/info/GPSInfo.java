package com.info;


public class GPSInfo
{
	private double Longitude;     //����
	private double Latitude;     //γ��
	private int ChargeLevel;     //������õȼ�
	private double Net_Score;   //�����ֽ��

	public GPSInfo(double longitude, double latitude,int chargeLevel){
		super();
		Longitude = longitude;
		Latitude = latitude;
		ChargeLevel = chargeLevel;
	}
	
	public double getLongitude() {return Longitude;}
	public void setLongitude(double longitude) {Longitude = longitude;}
	public double getLatitude() {return Latitude;}
	public void setLatitude(double latitude) {Latitude = latitude;}
	public int getChargeLevel() {return ChargeLevel;}
	public void setChargeLevel(int chargeLevel){this.ChargeLevel = chargeLevel;}
	public double getNet_Score() {return Net_Score;}
	public void setNet_Score(double net_Score) {Net_Score = net_Score;}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	private int Coverage_Area;    //������������ڲ���ʱָ��һ��wifi�����ĸ��Ƿ�Χ��������ɺ�ɾ��
	public int getCoverage_Area() {return Coverage_Area;}
	public void setCoverage_Area(int coverage_Area) {Coverage_Area = coverage_Area;}
	//////////////////////////////////////////////////////////////////////////////////////////////
	
}