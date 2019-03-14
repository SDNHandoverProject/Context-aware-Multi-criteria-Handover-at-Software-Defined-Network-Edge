package com.info;


public class GPSInfo
{
	private double Longitude;     //经度
	private double Latitude;     //纬度
	private int ChargeLevel;     //网络费用等级
	private double Net_Score;   //网络打分结果

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
	private int Coverage_Area;    //这个变量是用于测试时指定一个wifi接入点的覆盖范围，测试完成后删除
	public int getCoverage_Area() {return Coverage_Area;}
	public void setCoverage_Area(int coverage_Area) {Coverage_Area = coverage_Area;}
	//////////////////////////////////////////////////////////////////////////////////////////////
	
}