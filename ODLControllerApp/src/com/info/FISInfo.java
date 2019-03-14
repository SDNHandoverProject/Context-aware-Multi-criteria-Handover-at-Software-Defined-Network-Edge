package com.info;

import java.util.*;

public class FISInfo {
	
	private static double AndroidLongitude;     //手机位置经度
	private static double AndroidLatitude;     //手机位置纬度;
	private static double AndroidSpeed;     //手机移动速度;
	private static double AndroidBearing;     //手机移动反向与正北方的角度;
	private static int UserPref;      //用户偏好
	private static int UserBuf;              //用户移动播放端的缓存量
	
	private static double ReferLongitude;     //参考位置经度
	private static double ReferLatitude;     //参考位置纬度;
	
	public static List<WifiInfoItem> wifiInfo = new ArrayList<WifiInfoItem>();
	
	public static double getReferLongitude(){return ReferLongitude;}
	public static void setReferLongitude(double referLongitude) {ReferLongitude = referLongitude;}
	
	public static double getReferLatitude(){return ReferLatitude;}
	public static void setReferLatitude(double referLatitude) {ReferLatitude = referLatitude;}
	
	public static double getAndroidLongitude(){return AndroidLongitude;}
	public static void setAndroidLongitude(double androidLongitude) {AndroidLongitude = androidLongitude;}
	
	public static double getAndroidLatitude(){return AndroidLatitude;}
	public static void setAndroidLatitude(double androidLatitude) {AndroidLatitude = androidLatitude;}
	
	public static double getAndroidSpeed(){return AndroidSpeed;}
	public static void setAndroidSpeed(double androidSpeed) {AndroidSpeed = androidSpeed;}
	
	public static double getAndroidBearing(){return AndroidBearing;}
	public static void setAndroidBearing(double androidBearing) {AndroidBearing = androidBearing;}
	
	public static int getUserPref(){return UserPref;}
	public static void setUserPref(int userPref) {UserPref = userPref;}
	
	public static int getUserBuf(){return UserBuf;}
	public static void setUserBuf(int userBuf) {UserBuf = userBuf;}
	
//	public double getAndroidLongitude(){return AndroidLongitude;}
//	public void setAndroidLongitude(double androidLongitude){AndroidLongitude = androidLongitude;}
//	public double getAndroidLatitude(){return AndroidLatitude;}
//	public void setAndroidLatitude(double androidLatitude){AndroidLatitude = androidLatitude;}
//	public double getAndroidSpeed(){return AndroidSpeed;}
//	public void setAndroidSpeed(double androidSpeed){AndroidSpeed = androidSpeed;}
//	public double getAndroidBearing(){return AndroidBearing;}
//	public void setAndroidBearing(double androidBearing){AndroidBearing = androidBearing;}
//	public String getUserPref(){return UserPref;}
//	public void setUserPref(String userPref){UserPref = userPref;}
//	public int getUserBuf(){return UserBuf;}
//	public void setUserBuf(int userBuf){UserBuf = userBuf;}
	
}
