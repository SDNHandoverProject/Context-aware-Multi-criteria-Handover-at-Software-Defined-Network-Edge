package com.main;

import com.info.FISInfo;
import com.info.WifiInfoItem;

public class computeFIS {
	
	private static double Max_distance = 20;  //最大距离20米
	private static double Max_speed = 5;  //最大速度5米/每秒
	private static double Max_angle = 180;  //最大角度180度
	
	private static double Max_rssi = -10-(-70);  //取rssi的范围-10 ~ -70
	private static double Max_AB = 550.0;    //最大的网速550
	
	private static double Max_Buf = 300;  //最大缓存300
	private static double Max_User_per = 100;    //最大的用户偏好值
	private static double Max_WLAN_ChargeLevel = 10;  //最大的网络费用等级
	 
	private static double WLAN_Reject_Low = -1.0;          //WLAN拒绝接入系数  ,
	private static double WLAN_Reject_Medium_Low = -1.0;
	private static double WLAN_Reject_Medium = -1.0;
	private static double WLAN_Reject_Medium_High = -1.0;
	private static double WLAN_Reject_High = -1.0;
	
	private static double Throughtput_Factor_Low = -1.0;     //网络吞吐量系数    
	private static double Throughtput_Factor_Medium_Low = -1.0;
	private static double Throughtput_Factor_Medium = -1.0;
	private static double Throughtput_Factor_Medium_High = -1.0;
	private static double Throughtput_Factor_High = -1.0;
	
	private static double Net_Score_Not_Acceptable = -1.0;     //网络得分 隶属度函数，模糊值    
	private static double Net_Score_Probably_not_Acceptable = -1.0;
	private static double Net_Score_Probably_Acceptable = -1.0;
	private static double Net_Score_Acceptable = -1.0;

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 与接入点AP之间的空间距离（单位：米）的隶属度函数，距离范围取值为[0——20]米，取得实际的距离后划归取得 0.0-1.0之间的一个数值。
	 * 所以暂时将模糊分割为[0-0.4] [0.3-0.7] [0.6-1.0]
	 * Distance(m)--D  距离
	 */
	public static double MemberShip_Distance_Low(double distance){return D_S_A_Low(distance);}
	public static double MemberShip_Distance_Medium(double distance){return D_S_A_Medium(distance);}
	public static double MemberShip_Distance_High(double distance){return D_S_A_High(distance);}
	/**
	 * 与接入点AP之间的移动速度（单位：米/每秒）的隶属度函数，速度范围取值为[0——5]米/秒，取得实际的速度值后划归取得 0.0-1.0之间的一个数值。
	 * 所以暂时将模糊分割为[0-0.4] [0.3-0.7] [0.6-1.0]
	 * Speed(m/s)--S  移动速度
	 */
	public static double MemberShip_Speed_Low(double speed){return D_S_A_Low(speed);}
	public static double MemberShip_Speed_Medium(double speed){return D_S_A_Medium(speed);}
	public static double MemberShip_Speed_High(double speed){return D_S_A_High(speed);}
	/**
	 * 与接入点AP之间的接入角度（单位：度）的隶属度函数，范围取值为[0——180]度，取得实际的角度后后划归取得 0.0-1.0之间的一个数值。
	 * 所以暂时将模糊分割为[0-0.4] [0.3-0.7] [0.6-1.0]
	 * Angle(Degree)--A 角度
	 */
	public static double MemberShip_Angle_Low(double degree){return D_S_A_Low(degree);}
	public static double MemberShip_Angle_Medium(double degree){return D_S_A_Medium(degree);}
	public static double MemberShip_Angle_High(double degree){return D_S_A_High(degree);}
	
	/**[0-0.4]*/
	public static double D_S_A_Low(double value)
	{
		if(value>=0 && value<=0.3)
			return 1.0;
		else if(value>=0.3 && value<=0.4)
			return -10.0 * value + 4.0 ;
		else if(value>=0.4)
			return 0.0;
	
		return -1;	
	}
	
	/**[0.3-0.7]*/
	public static double D_S_A_Medium(double value)
	{
		if(value>=0 && value<=0.3)
			return 0.0;
		else if(value>=0.3 && value<=0.5)
			return 5 * value - 1.5;
		else if(value>=0.5 && value<=0.7)
			return -5 * value + 3.5;
		else if(value>=0.7)
			return 0.0;
	
		return -1;	
	}
	
	/**[0.6-1.0]*/
	public static double D_S_A_High(double value)
	{
		if(value>=0 && value<=0.6)
			return 0.0;
		else if(value>=0.6 && value<=0.7)
			return 10.0 * value - 6.0 ;
		else if(value>=0.7)
			return 1.0;
	
		return -1;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 拒绝接入网络系数WLAN-Reject的隶属度函数，取得实际的角度后后划归取得 0.0-1.0之间的一个数值。
	 * 所以暂时将模糊分割为[0-0.35] [0.2-0.5] [0.35-0.65] [0.5-0.8] [0.65-1.0]
	 */
	/**[0-0.35]*/
	public static double MemberShip_WLAN_Reject_Low(double value){return Five_Segmentation_Low(value);}
	
	/**[0.2-0.5]*/
	public static double MemberShip_WLAN_Reject_Medium_Low(double value){return Five_Segmentation_Medium_Low(value);}
	
	/**[0.35-0.65]*/
	public static double MemberShip_WLAN_Reject_Medium(double value){return Five_Segmentation_Medium(value);}
	
	/**[0.5-0.8]*/
	public static double MemberShip_WLAN_Reject_Medium_High(double value){return Five_Segmentation_Medium_High(value);}	
	
	/**[0.65-1.0]*/
	public static double MemberShip_WLAN_Reject_High(double value){return Five_Segmentation_High(value);}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//5个分割的模糊分割
	/**[0-0.35]*/
	public static double Five_Segmentation_Low(double value)
	{
		if(value>=0 && value<=0.2)
			return 1.0;
		else if(value>=0.2 && value<=0.35)
			return -20.0/3.0 * value + 7.0/3.0;
		else if(value>=0.35)
			return 0.0;
		
		return -1;
	}
	/**[0.2-0.5]*/
	public static double Five_Segmentation_Medium_Low(double value)
	{
		if(value>=0 && value<=0.2)
			return 0.0;
		else if(value>=0.2 && value<=0.35)
			return 20.0/3.0 * value - 4.0/3.0;
		else if(value>=0.35 && value<=0.5)
			return -20.0/3.0 * value + 10.0/3.0;
		else if(value>=0.5)
			return 0.0;
	
		return -1;
	}
	/**[0.35-0.65]*/
	public static double Five_Segmentation_Medium(double value)
	{
		if(value>=0 && value<=0.35)
			return 0.0;
		else if(value>=0.35 && value<=0.5)
			return 20.0/3.0 * value - 7.0/3.0;
		else if(value>=0.5 && value<=0.65)
			return -20.0/3.0 * value + 13.0/3.0;
		else if(value>=0.65)
			return 0.0;
		
		return -1;
	}
	/**[0.5-0.8]*/
	public static double Five_Segmentation_Medium_High(double value)
	{
		if(value>=0 && value<=0.5)
			return 0.0;
		else if(value>=0.5 && value<=0.65)
			return 20.0/3.0 * value - 10.0/3.0;
		else if(value>=0.65 && value<=0.8)
			return -20.0/3.0 * value + 16.0/3.0;
		else if(value>=0.8)
			return 0.0;
		
		return -1;
	}
	/**[0.65-1.0]*/
	public static double Five_Segmentation_High(double value)
	{
		if(value>=0 && value<=0.65)
			return 0.0;
		else if(value>=0.65 && value<=0.8)
			return 20.0/3.0 * value - 13.0/3.0;
		else if(value>=0.8)
			return 1.0;
		
		return -1;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * WIFI信号强度的RSSI的隶属度函数，取得实际值后划归取得 0.0-1.0之间的一个数值。
	 * 所以暂时将模糊分割为[0-0.4] [0.3-0.7][0.6-1.0]
	 */	 
	public static double MemberShip_RSSI_Low(double value){return D_S_A_Low(value);}

	public static double MemberShip_RSSI_Medium(double value){return D_S_A_Medium(value);}
	
	public static double MemberShip_RSSI_High(double value){return D_S_A_High(value);}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 网络实时带宽Bandwidth的隶属度函数，取得实际值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.4] [0.3-0.7] [0.6-1.0]
	*/	
	/**[0-0.4]*/
	public static double MemberShip_Bandwidth_Low(double value){return D_S_A_Low(value);}
	/**[0.3-0.7]*/
	public static double MemberShip_Bandwidth_Medium(double value){return D_S_A_Medium(value);}
	/**[0.6-1.0]*/
	public static double MemberShip_Bandwidth_High(double value){return D_S_A_High(value);}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 网络吞吐量系数Throughput-Factor的隶属度函数，取得实际值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.35] [0.2-0.5] [0.35-0.65] [0.5-0.8] [0.65-1.0]
	*/	 
	/**[0-0.35]*/
	public static double MemberShip_Throughput_Factor_Low(double value){return Five_Segmentation_Low(value);}
	/**[0.2-0.5]*/
	public static double MemberShip_Throughput_Factor_Medium_Low(double value){return Five_Segmentation_Medium_Low(value);}
	/**[0.35-0.65]*/
	public static double MemberShip_Throughput_Factor_Medium(double value){return Five_Segmentation_Medium(value);}
	/**[0.5-0.8]*/
	public static double MemberShip_Throughput_Factor_Medium_High(double value){return Five_Segmentation_Medium_High(value);}
	/**[0.65-1.0]*/
	public static double MemberShip_Throughput_Factor_High(double value){return Five_Segmentation_High(value);}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 播放器端缓存Buffer的隶属度函数，取值范围在[0,400]帧 ，取得实际值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.4] [0.3-0.7] [0.6-1.0]
	*/	 
	/**[0-0.4]*/
	public static double MemberShip_Buffer_Low(double value){return D_S_A_Low(value);}
	/**[0.3-0.7]*/
	public static double MemberShip_Buffer_Medium(double value){return D_S_A_Medium(value);}
	/**[0.6-1.0]*/
	public static double MemberShip_Buffer_High(double value){return D_S_A_High(value);}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 用户偏好User-Preference的隶属度函数，取值范围在[0,100]取得实际值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.5] [0.3-0.7] [0.5-0.10]
	*/	 
	/**Occasional [0-0.5]*/
	public static double MemberShip_User_Preference_Occasional(double value)
	{
		if(value>=0 && value<=0.2)
			return 1.0;
		else if(value>=0.2 && value<=0.5)
			return -10.0/3 * value + 5.0/3.0 ;
		else if(value>=0.5)
			return 0.0;
		
		return -1;
	}
	/**Standard [0.2-0.8]*/
	public static double MemberShip_User_Preference_Standard(double value)
	{
		if(value>=0 && value<=0.2)
			return 0.0;
		else if(value>=0.2 && value<=0.5)
			return 10.0/3 * value - 2.0/3.0 ;
		else if(value>=0.5 && value<=0.8)
			return -10.0/3 * value + 8.0/3.0 ;
		else if(value>=0.8)
			return 0.0;
		
		return -1;
	}
	/**Business [0.5-0.10]*/
	public static double MemberShip_User_Preference_Business(double value)
	{
		if(value>=0 && value<=0.5)
			return 0.0;
		else if(value>=0.5 && value<=0.8)
			return 10.0/3 * value - 5.0/3.0;
		else if(value>=0.8)
			return 1.0;
		
		return -1;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 网络费用Monetary-Cost的隶属度函数，取值范围在[1,4]取得实际值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.5][0.5-0.10]
	*/	 
	public static double MemberShip_Monetary_Cost_Low(double value){
		
		if(value>=0 && value<=0.4)
			return 1.0;
		else if(value>=0.4 && value<=0.6)
			return -5.0 * value + 3.0 ;
		else if(value>=0.6)
			return 0.0;
	
		return -1;	
	}
	
	public static double MemberShip_Monetary_Cost_High(double value){
		
		if(value>=0 && value<=0.4)
			return 0.0;
		else if(value>=0.4 && value<=0.6)
			return 5.0 * value - 2.0 ;
		else if(value>=0.6)
			return 1.0;
	
		return -1;	
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	* 网络打分的Net-Score隶属度函数，取得值后划归取得 0.0-1.0之间的一个数值。
	* 所以暂时将模糊分割为[0-0.35] [0.1-0.6] [0.35-0.85] [0.6-1.0] 
	*/
	/**Reject [0-0.35]*/
	public static double MemberShip_Net_Score_Not_Acceptable(double value)
	{
		if(value>=0 && value<=0.1)
			return 1.0;
		else if(value>=0.1 && value<=0.35)
			return -4 * value + 7.0/5.0 ;
		else if(value>=0.35)
			return 0.0;

		return -1;
	}

	/**Probably Reject [0.1-0.6]*/
	public static double MemberShip_Net_Score_Probably_Not_Acceptable(double value)
	{
		if(value>=0 && value<=0.1)
			return 0.0;
		else if(value>=0.1 && value<=0.35)
			return 4 * value - 2.0/5.0;
		else if(value>=0.35 && value<=0.6)
			return -4 * value + 12.0/5.0;
		else if(value>=0.6)
			return 0.0;
		
		return -1;
	}
	
	/**Probably Accept [0.35-0.85]*/
	public static double MemberShip_Net_Score_Probably_Acceptable(double value)
	{
		if(value>=0 && value<=0.35)
			return 0.0;
		else if(value>=0.35 && value<=0.6)
			return 4 * value - 7.0/5.0;
		else if(value>=0.6 && value<=0.85)
			return -4 * value + 17.0/5.0;
		else if(value>=0.85)
			return 0.0;
		
		return -1;
	}
	
	/**Accept [0.6-1.0]*/
	public static double MemberShip_Net_Score_Acceptable(double value)
	{
		if(value>=0 && value<=0.6)
			return 0.0;
		else if(value>=0.6 && value<=0.85)
			return 4 * value - 12.0/5.0 ;
		else if(value>=0.85)
			return 1.0;

		return -1;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 用于计算当前接入网络的满意程度 DegreeOfSatisfaction（Degree Of Satisfaction）
	 * 所用到的参数是记录当前手机位置信息，缓存信息，用户偏好以及所有扫描到的wifi信号的信息的FISInfo类
	 * return 为一个具体的评估的数值
	 */
	public static void CompleteNetWorkDegreeOfSatisfaction(WifiInfoItem CW)
	{
		//分层实现FIS：
		//			将移动终端与接入点之间的距离Distance、移动端移动速度、移动端接近角度三个参数作为第一层 得到一个WLAN网络拒绝接入系数（WLAN_Reject）
		//			将WLAN网络拒绝接入系数、wifi接收信号强度、wifi网络实时带宽作为第二层 得到一个网络吞吐量系数（Throughput_Factor）
		//			将网络吞吐量系数、用户缓存、用户偏好、网络费用作为第三层 得到一个网络打分结果（Net_Score）
		
		Comple_Net_Score(CW);
		
	}
	
	//第一层：移动终端与接入点之间的距离Distance、移动端移动速度、移动端接近角度三个参数作为第一层 得到一个WLAN网络拒绝接入系数
	public static void Comple_Net_Score(WifiInfoItem  CW) {
		
		get_Wlan_Reject(CW);  //计算得到WALN_Reject的五个值的模糊集合
		
		get_Throughtput_Factor(CW);   //计算网络吞吐量系数的模糊集合
		
		get_Net_Score(CW);  //计算网络打分结果的模糊集合
		
		//？？？？？？？？？？？？？？？？？？？？？？？？？
		//此处计算质心，逆模糊化cross point
		double NET_Score = CompletCOG();
		
		CW.setNetScore(NET_Score);
		
	}
	
	/**
	 * 计算重力的质心 COG (逆模糊化)
	 */
	private static double CompletCOG() {
		
		double First_Cross_Point_X = 9.0/40.0;
		double First_Cross_Point_Y = 1.0/2.0;
		double Second_Cross_Point_X = 19.0/40.0;
		double Second_Cross_Point_Y = 1.0/2.0;
		double Third_Cross_Point_X = 29.0/40.0;
		double Third_Cross_Point_Y = 1.0/2.0;
		
		double numerator_sum = 0.0;    //COG的计算分子
		double denominator_sum = 0.0;    //COG的计算分母
		
		//double Reject_Cross_Point = 
		if( Net_Score_Not_Acceptable != 0.0 && Net_Score_Not_Acceptable != -1.0)   //Net_Score_Not_Acceptable有数值的
			numerator_sum = numerator_sum +((Net_Score_Not_Acceptable - 7.0/5.0)*(-1.0/4.0) + 0.35 )*Net_Score_Not_Acceptable * 0.5;
		if( Net_Score_Probably_not_Acceptable != 0.0 && Net_Score_Probably_not_Acceptable != -1.0)   //Net_Score_Probably_not_Acceptable有数值的
			numerator_sum = numerator_sum +(((Net_Score_Probably_not_Acceptable-12.0/5.0)*(-1.0/4.0)-(Net_Score_Probably_not_Acceptable+2.0/5.0)*(1.0/4.0))+0.5)*Net_Score_Probably_not_Acceptable*0.5;
		if( Net_Score_Probably_Acceptable != 0.0 && Net_Score_Probably_Acceptable != -1.0)   //Net_Score_Probably_Acceptable有数值的
			numerator_sum = numerator_sum +(((Net_Score_Probably_Acceptable-17.0/5.0)*(-1.0/4.0)-(Net_Score_Probably_Acceptable+7.0/5.0)*(1.0/4.0))+0.5)*Net_Score_Probably_Acceptable*0.5;
		if( Net_Score_Acceptable != 0.0 && Net_Score_Acceptable != -1.0)   //Net_Score_Acceptable有数值的
			numerator_sum = numerator_sum + ((1.0-(Net_Score_Acceptable + 12.0/5.0)*(1.0/4.0)) + 0.4 )*Net_Score_Acceptable * 0.5;
		
		if(First_Cross_Point_Y < Net_Score_Not_Acceptable && First_Cross_Point_Y < Net_Score_Probably_not_Acceptable)    //Net_Score_Not_Acceptable和Net_Score_Probably_not_Acceptable都大于第一个交汇点
		{
			numerator_sum = numerator_sum - 0.5*0.25*0.5; 
			double temp_x_1 = (Net_Score_Not_Acceptable - 7.0/5.0)*(-1.0/4.0);
			denominator_sum = denominator_sum + temp_x_1 * Net_Score_Not_Acceptable + ((-2*Math.pow(First_Cross_Point_X,2)+7.0/5.0*First_Cross_Point_X)-(-2*Math.pow(temp_x_1,2)+7.0/5.0*temp_x_1));
			
			double temp_x_2 = (Net_Score_Probably_not_Acceptable+2.0/5.0)*(1.0/4.0);
			denominator_sum = denominator_sum + (2*Math.pow(temp_x_2, 2)-2.0/5.0*temp_x_2)-(2*Math.pow(First_Cross_Point_X, 2)-2.0/5.0*First_Cross_Point_X)+Net_Score_Probably_not_Acceptable*(0.35-temp_x_2);
		}
		else{ //否则 判断Net_Score_Not_Acceptable 和  Net_Score_Probably_not_Acceptable哪个大哪个小
			if(Net_Score_Not_Acceptable > Net_Score_Probably_not_Acceptable){ //Net_Score_Not_Acceptable大于Net_Score_Probably_not_Acceptable
				double temp_x = (Net_Score_Not_Acceptable - 7.0/5.0)*(-1.0/4.0);
				double temp_x_1 = (Net_Score_Probably_not_Acceptable + 2.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Probably_not_Acceptable - 7.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - ((temp_x_2 - temp_x_1) + 0.25)*Net_Score_Probably_not_Acceptable*0.5;
				denominator_sum = denominator_sum + temp_x*Net_Score_Not_Acceptable + ((-2*Math.pow(temp_x_2,2)+7.0/5.0*temp_x_2)-(-2*Math.pow(temp_x,2)+7.0/5.0*temp_x)) + Net_Score_Probably_not_Acceptable*(0.35-temp_x_2); 
			}
			else{ //Net_Score_Not_Acceptable小于Net_Score_Probably_not_Acceptable
				double temp_x = (Net_Score_Probably_not_Acceptable + 2.0/5.0)*(1.0/4.0);
				double temp_x_1 = (Net_Score_Not_Acceptable + 2.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Not_Acceptable - 7.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - (temp_x_2-temp_x_1+0.25)*Net_Score_Not_Acceptable*0.5;
				denominator_sum = denominator_sum + temp_x_1*Net_Score_Not_Acceptable+ Net_Score_Probably_not_Acceptable*(0.35-temp_x)+(2*Math.pow(temp_x, 2)-2.0/5.0*temp_x)-(2*Math.pow(temp_x_1, 2)-2.0/5.0*temp_x_1);
			}
		}
		
		if(Second_Cross_Point_Y < Net_Score_Probably_not_Acceptable && Second_Cross_Point_Y < Net_Score_Probably_Acceptable)  ////Net_Score_Probably_not_Acceptable和Net_Score_Probably_Acceptable都大于第二个交汇点   
		{
			numerator_sum = numerator_sum - 0.5*0.25*0.5;
			double temp_x_1 = (Net_Score_Probably_not_Acceptable - 12.0/5.0)*(-1.0/4.0);
			denominator_sum = denominator_sum + (temp_x_1-0.35)*Net_Score_Probably_not_Acceptable+((-2*Math.pow(Second_Cross_Point_X,2)+7.0/5.0*Second_Cross_Point_X)-(-2*Math.pow(temp_x_1,2)+7.0/5.0*temp_x_1));
			
			double temp_x_2 = (Net_Score_Probably_Acceptable+7.0/5.0)*(1.0/4.0);
			denominator_sum = denominator_sum + ((2*Math.pow(temp_x_2,2)-7.0/5.0*temp_x_2)-(2*Math.pow(Second_Cross_Point_X,2)-7.0/5.0*Second_Cross_Point_X)) + (0.6-temp_x_2)*Net_Score_Probably_Acceptable;
		}
		else{  //否则 判断Net_Score_Probably_not_Acceptable 和  Net_Score_Probably_Acceptable哪个大哪个小
			if(Net_Score_Probably_not_Acceptable > Net_Score_Probably_Acceptable){ //Net_Score_Probably_not_Acceptable大于Net_Score_Probably_Acceptable
				double temp_x = (Net_Score_Probably_not_Acceptable - 12.0/5.0)*(-1.0/4.0);
				double temp_x_1 = (Net_Score_Probably_Acceptable + 7.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Probably_Acceptable - 12.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - ((temp_x_2 - temp_x_1) + 0.25)*Net_Score_Probably_Acceptable*0.5;
				denominator_sum = denominator_sum + (temp_x-0.35)*Net_Score_Probably_not_Acceptable+((-2*Math.pow(temp_x_2,2)+12.0/5.0*temp_x_2)-(-2*Math.pow(temp_x,2)+12.0/5.0*temp_x)) + (0.6-temp_x_2)*Net_Score_Probably_Acceptable;
			}
			else
			{
				double temp_x = (Net_Score_Probably_Acceptable + 7.0/5.0)*(1.0/4.0);
				double temp_x_1 = (Net_Score_Probably_not_Acceptable + 7.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Probably_not_Acceptable - 12.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - (temp_x_2-temp_x_1+0.25)*Net_Score_Probably_not_Acceptable*0.5;
				denominator_sum = denominator_sum + (temp_x_1-0.35)*Net_Score_Probably_not_Acceptable+(0.6-temp_x)*Net_Score_Probably_Acceptable+((2*Math.pow(temp_x,2)-7.0/5.0*temp_x)-(2*Math.pow(temp_x_1,2)-7.0/5.0*temp_x_1));
			}
		}
		
		if(Third_Cross_Point_Y < Net_Score_Probably_Acceptable && Third_Cross_Point_Y < Net_Score_Acceptable)  //Net_Score_Probably_Acceptable和Net_Score_Acceptable都大于第三个交汇点      
		{
			numerator_sum = numerator_sum - 0.5*0.25*0.5;
			double temp_x_1 = (Net_Score_Probably_Acceptable - 17.0/5.0)*(-1.0/4.0);
			denominator_sum = denominator_sum + (temp_x_1-0.6)*Net_Score_Probably_Acceptable + ((-2*Math.pow(Third_Cross_Point_X,2)+17.0/5.0*Third_Cross_Point_X)-(-2*Math.pow(temp_x_1,2)+17.0/5.0*temp_x_1));
					
			double temp_x_2 = (Net_Score_Acceptable+12.0/5.0)*(1.0/4.0);
			denominator_sum = denominator_sum + ((2*Math.pow(temp_x_2,2)-12.0/5.0*temp_x_2)-(2*Math.pow(Third_Cross_Point_X,2)-12.0/5.0*Third_Cross_Point_X));	
		}
		else{  //否则 判断Net_Score_Probably_Acceptable 和  Net_Score_Acceptable哪个大哪个小
			if(Net_Score_Probably_Acceptable > Net_Score_Acceptable){ //Net_Score_Probably_Acceptable大于Net_Score_Acceptable
				double temp_x = (Net_Score_Probably_Acceptable - 17.0/5.0)*(-1.0/4.0);
				double temp_x_1 = (Net_Score_Acceptable + 12.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Acceptable - 17.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - ((temp_x_2 - temp_x_1) + 0.25)*Net_Score_Acceptable*0.5;
				denominator_sum = denominator_sum + (temp_x-0.6)*Net_Score_Probably_Acceptable+ ((-2*Math.pow(temp_x_2,2)+17.0/5.0*temp_x_2)-(-2*Math.pow(temp_x,2)+17.0/5.0*temp_x)) + (1.0-temp_x_2)*Net_Score_Acceptable;
			}
			else
			{
				double temp_x = (Net_Score_Acceptable + 12.0/5.0)*(1.0/4.0);
				double temp_x_1 = (Net_Score_Probably_Acceptable + 12.0/5.0)*(1.0/4.0);
				double temp_x_2 = (Net_Score_Probably_Acceptable - 17.0/5.0)*(-1.0/4.0);
				numerator_sum = numerator_sum - (temp_x_2-temp_x_1+0.25)*Net_Score_Probably_Acceptable*0.5;
				denominator_sum = denominator_sum + (temp_x_1-0.6)*Net_Score_Probably_Acceptable+(1.0-temp_x)*Net_Score_Acceptable+((2*Math.pow(temp_x,2)-12.0/5.0*temp_x)-(2*Math.pow(temp_x_1,2)-12.0/5.0*temp_x_1));
			}
		}

		return numerator_sum/denominator_sum;
	}
	
	
	private static void get_Net_Score(WifiInfoItem CW) {
		//
		Net_Score_Not_Acceptable = -1.0;     //网络得分   初始化 
		Net_Score_Probably_not_Acceptable = -1.0;
		Net_Score_Probably_Acceptable = -1.0;
		Net_Score_Acceptable = -1.0; 
		
		double Nmubership_Buffer = FISInfo.getUserBuf()/Max_Buf;      //将实际的缓存数值划归为0-1.0之间的数值
		double Nmubership_User_per = FISInfo.getUserPref()/Max_User_per;    // 将实际的用户偏好数值划归为0-1.0之间的数值
		double Nmubership_ChargeLevel = CW.getChargeLevel()/Max_WLAN_ChargeLevel; 
		
		
		double temp_buffer_low = MemberShip_Buffer_Low(Nmubership_Buffer);
		double temp_buffer_medium = MemberShip_Buffer_Medium(Nmubership_Buffer);
		double temp_buffer_high = MemberShip_Buffer_High(Nmubership_Buffer);
		
		double temp_User_Pref_low = MemberShip_User_Preference_Occasional(Nmubership_User_per);
		double temp_User_Pref_medium = MemberShip_User_Preference_Standard(Nmubership_User_per);
		double temp_User_Pref_high = MemberShip_User_Preference_Business(Nmubership_User_per);
				
		double temp_monetary_cost_low = MemberShip_Monetary_Cost_Low(Nmubership_ChargeLevel);
		double temp_monetary_cost_high = MemberShip_Monetary_Cost_High(Nmubership_ChargeLevel);
		
	
		/**rules 1: Low    ALL    ALL    ALL ---->Not_Acceptable */
		if(Throughtput_Factor_Low != 0.0)
			set_Net_Score(1,Throughtput_Factor_Low);
	
		if(Throughtput_Factor_Medium_Low != 0.0 && Throughtput_Factor_Medium_Low != -1.0)  
		{
			if(temp_buffer_low != 0.0) 
			{
				if(temp_User_Pref_low != 0.0)
				{
					if(temp_monetary_cost_high != 0.0)
					{	/**rules 2: Medium_Low    Low    Low    High ---->Probably_Not_Acceptable */
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
				if(temp_User_Pref_medium != 0.0)
				{   /**rules 5: Medium_Low    Low    Medium    ALL ---->Probably_Not_Acceptable */
					double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_low,temp_User_Pref_medium));
					set_Net_Score(2 ,temp);
				}
				if(temp_User_Pref_high != 0.0)
				{   /**rules 7: Medium_Low    Low    High    ALL ---->Not_Acceptable */
					double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_low,temp_User_Pref_high));
					set_Net_Score(1 ,temp);
				}
			}
			/**rules 6: Medium_Low    Not_Low    Medium    Low ---->Probably_Not_Acceptable */
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_low != 0.0)
					{
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_medium, temp_monetary_cost_low)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_low != 0.0)
					{
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_high, Math.min(temp_User_Pref_medium, temp_monetary_cost_low)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			
			/**rules 8: Medium_Low    Not_Low    High    ALL ---->Probably_Not_Acceptable */
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_high != 0.0){
					double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_medium, temp_User_Pref_high));  //最小值
					set_Net_Score(2 ,temp);
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_high != 0.0){
					double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_high, temp_User_Pref_high));  //最小值
					set_Net_Score(2 ,temp);
				}
			}
			
			/**rules 9: Medium_Low    Not_Low    Not_High    High ---->Not_Acceptable */
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_high, Math.min(temp_User_Pref_medium, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_high, Math.min(temp_User_Pref_medium, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
			}
			
			/** rules 3 : Medium_Low    High    Low    Low --->Probably-Acceptable*/
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_high, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(3 ,temp);
					}
				}
			}
			
			/** rules 4 : Medium_Low    Not_High    Low    Low --->Probably-not-Acceptable*/
			if(temp_buffer_low != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_Low, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}	
		}
		
		if(Throughtput_Factor_Medium != 0.0 && Throughtput_Factor_Medium != -1.0)
		{
			if(temp_User_Pref_high != 0.0){
				double temp = Math.min(Throughtput_Factor_Medium, temp_User_Pref_high);  //最小值
				set_Net_Score(2 ,temp);
			}
			
			if(temp_buffer_low != 0.0)
			{
				if(temp_User_Pref_low != 0.0)
				{   /** rules 10 : Medium    Low    Low    Low --->Probably-Acceptable */
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(3 ,temp);
					}
					/** rules 11 : Medium    Low    Low    High --->Probably-not-Acceptable */
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
				/** rules 14 : Medium    Low    Medium    ALL --->Probably-not-Acceptable */
				if(temp_User_Pref_medium != 0.0){
					double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_low, temp_User_Pref_medium));  //最小值
					set_Net_Score(2 ,temp);
				}
			}	

			/** rules 12 : Medium	 Not_Low    Low       Low  --->Acceptable*/
			/** rules 13 : Medium	 Not_Low    Low       High --->Not_Acceptable*/
			/** rules 15 : Medium	 Not_Low    Medium    Low  --->Probably-Acceptable*/
			/** rules 16 : Medium	 Not_Low    Medium    High --->Probably-not-Acceptable*/
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(4 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_medium, temp_monetary_cost_low)));  //最小值
						set_Net_Score(3 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_medium, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_high, Math.min(temp_User_Pref_low, temp_monetary_cost_low)));  //最小值
						set_Net_Score(4 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_high, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(1 ,temp);
					}
				}
				if(temp_User_Pref_medium != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_high, Math.min(temp_User_Pref_medium, temp_monetary_cost_low)));  //最小值
						set_Net_Score(3 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium, Math.min(temp_buffer_high, Math.min(temp_User_Pref_medium, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			
		}
		
		if(Throughtput_Factor_Medium_High != 0.0 && Throughtput_Factor_Medium_High != -1.0)
		{
			if(temp_User_Pref_low != 0.0){
				if(temp_monetary_cost_low != 0.0){    /** rules 20 : Medium_high	ALL    Low    Low  --->Acceptable*/
					double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_User_Pref_low, temp_monetary_cost_low));  //最小值
					set_Net_Score(4 ,temp);
				}
			}
			if(temp_User_Pref_medium != 0.0){
				if(temp_monetary_cost_low != 0.0){    /** rules 21 : Medium_high	ALL    Medium    Low  --->Acceptable*/
					double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_User_Pref_medium, temp_monetary_cost_low));  //最小值
					set_Net_Score(4 ,temp);
				}
				if(temp_monetary_cost_high != 0.0){   /** rules 22 : Medium_high	ALL    Medium    High  --->Probably-Acceptable*/
					double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_User_Pref_medium, temp_monetary_cost_high));  //最小值
					set_Net_Score(3 ,temp);
				}
			}
			
			
			/** rules 19 : Medium_High	 Not_Low    Low       High  --->Probably-not-Acceptable*/
			/** rules 24 : Medium_High	 Not_Low    High      Low   --->Acceptable*/
			/** rules 25 : Medium_High	 Not_Low    High      High  --->Probably-Acceptable*/
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
				if(temp_User_Pref_high != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_high, temp_monetary_cost_low)));  //最小值
						set_Net_Score(4 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_high, temp_monetary_cost_high)));  //最小值
						set_Net_Score(3 ,temp);
					}
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_high, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
				if(temp_User_Pref_high != 0.0){
					if(temp_monetary_cost_low != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_high, Math.min(temp_User_Pref_high, temp_monetary_cost_low)));  //最小值
						set_Net_Score(4 ,temp);
					}
					if(temp_monetary_cost_high != 0.0){
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_high, Math.min(temp_User_Pref_high, temp_monetary_cost_high)));  //最小值
						set_Net_Score(3 ,temp);
					}
				}
			}
			
			if(temp_buffer_low != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){   /** rules 18 : Medium_high	Low    Low    High  --->Probably_Acceptable*/
						double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(3 ,temp);
					}
				}
				if(temp_User_Pref_high != 0.0){   /** rules 23 : Medium_high	Low    High    ALL  --->Probably_Acceptable*/
					double temp = Math.min(Throughtput_Factor_Medium_High, Math.min(temp_buffer_low, temp_User_Pref_high));  //最小值
					set_Net_Score(3 ,temp);
				}
			}
		
		}
		
		if(Throughtput_Factor_High != 0.0 && Throughtput_Factor_High != -1.0)
		{
			if(temp_User_Pref_low != 0.0){
				if(temp_monetary_cost_low != 0.0){     /** rules 28 : High    ALL    Low        Low  --->Probably_Acceptable*/
					double temp = Math.min(Throughtput_Factor_High, Math.min(temp_User_Pref_low,temp_monetary_cost_low));  //最小值
					set_Net_Score(4 ,temp);
				}
			}
			
			/** rules 29 : High    ALL    Not_Low    Low  --->Probably_Acceptable*/
			if(temp_User_Pref_medium != 0.0){
				double temp = Math.min(Throughtput_Factor_High,temp_User_Pref_medium);  //最小值
				set_Net_Score(4 ,temp);
			}
			if(temp_User_Pref_high != 0.0){
				double temp = Math.min(Throughtput_Factor_High,temp_User_Pref_high);  //最小值
				set_Net_Score(4 ,temp);
			}
			
			if(temp_buffer_low != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){    /** rules 26 : High    Low    Low    H  --->Probably_Acceptable*/
						double temp = Math.min(Throughtput_Factor_High, Math.min(temp_buffer_low, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(3 ,temp);
					}
				}
			}
			
			/** rules 27 : High    Not_Low    Low    High  --->Probably_Not_Acceptable*/
			if(temp_buffer_medium != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){    
						double temp = Math.min(Throughtput_Factor_High, Math.min(temp_buffer_medium, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
			if(temp_buffer_high != 0.0){
				if(temp_User_Pref_low != 0.0){
					if(temp_monetary_cost_high != 0.0){    
						double temp = Math.min(Throughtput_Factor_High, Math.min(temp_buffer_high, Math.min(temp_User_Pref_low, temp_monetary_cost_high)));  //最小值
						set_Net_Score(2 ,temp);
					}
				}
			}
		}
	}
	
	/**
	 * 1 代表 ：Net_Score_Not_Acceptable = -1.0;     //网络得分    
	 * 2 代表 ：Net_Score_Probably_not_Acceptable = -1.0;
	 * 3 代表 ：Net_Score_Probably_Acceptable = -1.0;
	 * 4 代表 ：Net_Score_Acceptable = -1.0; 
	 */
	private static void set_Net_Score(int order,double v){
		double temp = 0.0 ;
		switch(order)
		{
		case 1:temp = Net_Score_Not_Acceptable; break;
		case 2:temp = Net_Score_Probably_not_Acceptable; break;
		case 3:temp = Net_Score_Probably_Acceptable; break;
		case 4:temp = Net_Score_Acceptable; break;
		}
		
		if(temp == -1.0) //如果没有赋过值，就直接赋值
			temp = v;   
		else  //已经赋值过，比较,保留较小值
		{   
			if(temp >= v)
				temp = v;
		}
		
		switch(order)
		{
		case 1:Net_Score_Not_Acceptable = temp; break;
		case 2:Net_Score_Probably_not_Acceptable = temp; break;
		case 3:Net_Score_Probably_Acceptable = temp; break;
		case 4:Net_Score_Acceptable = temp; break;
		}
	}
	
	private static void get_Throughtput_Factor(WifiInfoItem CW) {
		
		Throughtput_Factor_Low = -1.0;     //网络吞吐量系数    初始化
		Throughtput_Factor_Medium_Low = -1.0;
		Throughtput_Factor_Medium = -1.0;
		Throughtput_Factor_Medium_High = -1.0;
		Throughtput_Factor_High = -1.0;
		
		double Numbership_Rssi = CW.getRssi()-(-70)/Max_rssi;     //将实际的接收信号强度数值划归为0-1.0之间的数值
		double Numbership_AB = CW.getAB()/Max_AB;    //  FISInfo.getAndroidSpeed()/Max_speed;    //将实际的速度数值划归为0-1.0之间的数值，FISInfo.getAndroidSpeed()手机的实时速度
	
		double temp_rssi_low = MemberShip_RSSI_Low(Numbership_Rssi);
		double temp_rssi_medium = MemberShip_RSSI_Medium(Numbership_Rssi);
		double temp_rssi_high = MemberShip_RSSI_High(Numbership_Rssi);
		
		double temp_AB_low = MemberShip_Bandwidth_Low(Numbership_AB);
		double temp_AB_medium = MemberShip_Bandwidth_Medium(Numbership_AB);
		double temp_AB_high = MemberShip_Bandwidth_High(Numbership_AB);
		
		/**rule：1 (ALL   Low   ALL ---> Low)*/
		if(temp_rssi_low != 0.0)   //Rssi等于Low
			set_Throughtput_Factor( 1 ,temp_rssi_low);
		
		/**rule：24 (High   All   ALL ---> Low)*/
		if(WLAN_Reject_High != 0.0  && WLAN_Reject_High != -1.0 )   //WLAN_Reject_High
			set_Throughtput_Factor( 1 ,WLAN_Reject_High);
		
		if(WLAN_Reject_Low != 0.0 && WLAN_Reject_Low != -1.0 )    //WLAN_Reject等于WLAN_Reject_Low
		{  
			if(temp_rssi_medium != 0.0)  //Rssi等于Medium
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：2 (Low   Medium   Low ---> Medium_Low)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_medium, temp_AB_low));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：3 (Low   Medium   Medium ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_medium, temp_AB_medium));  //最小值
					set_Throughtput_Factor(3,temp);
				}
				if(temp_AB_high != 0.0 )  //AB等于High
				{   /**rule：4 (Low   Medium   High ---> Medium_High)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_medium, temp_AB_high));  //最小值
					set_Throughtput_Factor(4,temp);
				}
			}
			if(temp_rssi_high != 0.0 )  //Rssi等于High
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：5 (Low   High   Low ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_high, temp_AB_low));  //最小值
					set_Throughtput_Factor(3,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：6 (Low   High   Medium ---> Medium_High)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_high, temp_AB_medium));  //最小值
					set_Throughtput_Factor(4,temp);
				}
				if(temp_AB_high != 0.0)        //AB等于High
				{   /**rule：7 (Low   High   High ---> High)*/
					double temp = Math.min(WLAN_Reject_Low, Math.min(temp_rssi_high, temp_AB_high));  //最小值
					set_Throughtput_Factor(5,temp);
				}
			}
		}
		if(WLAN_Reject_Medium_Low != 0.0 && WLAN_Reject_Medium_Low != -1.0 )    //WLAN_Reject等于WLAN_Reject_Medium_Low
		{
			if(temp_rssi_medium != 0.0)  //Rssi等于Medium
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：8 (Medium_Low   Medium   Low ---> Medium_Low)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_medium, temp_AB_low));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：9 (Medium_Low   Medium   Medium ---> Medium_Low)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_medium, temp_AB_medium));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_high != 0.0)  //AB等于High
				{   /**rule：10 (Medium_Low   Medium   High ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_medium, temp_AB_high));  //最小值
					set_Throughtput_Factor(3,temp);
				}
			}
			if(temp_rssi_high != 0.0)  //Rssi等于High
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：11 (Medium_Low   High   Low ---> Medium_low)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_high, temp_AB_low));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：12 (Medium_Low   High   Medium ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_high, temp_AB_medium));  //最小值
					set_Throughtput_Factor(3,temp);
				}
				if(temp_AB_high != 0.0)        //AB等于High
				{   /**rule：13 (Medium_Low   High   High ---> Medium_High)*/
					double temp = Math.min(WLAN_Reject_Medium_Low, Math.min(temp_rssi_high, temp_AB_high));  //最小值
					set_Throughtput_Factor(4,temp);
				}
			}
		}
		if(WLAN_Reject_Medium != 0.0 && WLAN_Reject_Medium != -1.0 )    //WLAN_Reject等于WLAN_Reject_Medium
		{
			if(temp_rssi_medium != 0.0)  //Rssi等于Medium
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：14 (Medium   Medium   Low ---> Low)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_medium, temp_AB_low));  //最小值
					set_Throughtput_Factor(1,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：15 (Medium   Medium   Medium ---> Medium_Low)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_medium, temp_AB_medium));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_high != 0.0)  //AB等于High
				{   /**rule：16 (Medium   Medium   High ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_medium, temp_AB_high));  //最小值
					set_Throughtput_Factor(3,temp);
				}
			}
			if(temp_rssi_high != 0.0)  //Rssi等于High
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：17 (Medium   High   Low ---> Medium_low)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_high, temp_AB_low));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：18 (Medium   High   Medium ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_high, temp_AB_medium));  //最小值
					set_Throughtput_Factor(3,temp);
				}
				if(temp_AB_high != 0.0)        //AB等于High
				{   /**rule：19 (Medium   High   High ---> Medium_High)*/
					double temp = Math.min(WLAN_Reject_Medium, Math.min(temp_rssi_high, temp_AB_high));  //最小值
					set_Throughtput_Factor(4,temp);
				}
			}
		}
		if(WLAN_Reject_Medium_High != 0.0 && WLAN_Reject_Medium_High != -1.0 )    //WLAN_Reject等于WLAN_Reject_Medium_High
		{
			if(temp_rssi_high != 0.0)  //Rssi等于High
			{
				if(temp_AB_low != 0.0)  //AB等于Low
				{   /**rule：21 (Medium_High   High   Low ---> low)*/
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_high, temp_AB_low));  //最小值
					set_Throughtput_Factor(1,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于Medium
				{   /**rule：22 (Medium_High   High   Medium ---> Medium)*/
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_high, temp_AB_medium));  //最小值
					set_Throughtput_Factor(3,temp);
				}
				if(temp_AB_high != 0.0)        //AB等于High
				{   /**rule：23 (Medium_High   High   High ---> High)*/
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_high, temp_AB_high));  //最小值
					set_Throughtput_Factor(5,temp);
				}
			}
			if(temp_rssi_medium != 0.0)  //Rssi等于Medium
			{
				if(temp_AB_high != 0.0)  //AB等于High
				{   /**rule：20 (Medium_High   Medium   High ---> Medium_low)*/
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_medium, temp_AB_high));  //最小值
					set_Throughtput_Factor(2,temp);
				}
				
				/**rule：25 (Medium_High   Medium   Not_High ---> low)*/
				if(temp_AB_low != 0.0)  //AB等于low 
				{
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_medium, temp_AB_low));  //最小值
					set_Throughtput_Factor(1,temp);
				}
				if(temp_AB_medium != 0.0)  //AB等于medium
				{
					double temp = Math.min(WLAN_Reject_Medium_High, Math.min(temp_rssi_medium, temp_AB_medium));  //最小值
					set_Throughtput_Factor(1,temp);
				}	
			}
		}		
	}
	
	/**
	 * 1 代表 ：Throughtput_Factor_Low ;        
	 * 2 代表 ：Throughtput_Factor_Medium_Low 
	 * 3 代表 ：Throughtput_Factor_Medium ;
	 * 4 代表 ：Throughtput_Factor_Medium_High ;
	 * 5 代表 ：Throughtput_Factor_High
	 */
	private static void set_Throughtput_Factor(int order,double v)
	{
		double temp = 0.0 ;
		switch(order)
		{
		case 1:temp = Throughtput_Factor_Low; break;
		case 2:temp = Throughtput_Factor_Medium_Low; break;
		case 3:temp = Throughtput_Factor_Medium; break;
		case 4:temp = Throughtput_Factor_Medium_High; break;
		case 5:temp = Throughtput_Factor_High; break;
		}
		
		if(temp == -1.0) //如果没有赋过值，就直接赋值
			temp = v;   
		else  //已经赋值过，比较,保留较小值
		{   
			if(temp >= v)
				temp = v;
		}
		
		switch(order)
		{
		case 1:Throughtput_Factor_Low = temp; break;
		case 2:Throughtput_Factor_Medium_Low = temp; break;
		case 3:Throughtput_Factor_Medium = temp; break;
		case 4:Throughtput_Factor_Medium_High = temp; break;
		case 5:Throughtput_Factor_High = temp; break;
		}
	}

	
	private static void get_Wlan_Reject(WifiInfoItem  CW) {
		
		WLAN_Reject_Low = -1.0;        
		WLAN_Reject_Medium_Low = -1.0;
		WLAN_Reject_Medium = -1.0;
		WLAN_Reject_Medium_High = -1.0;
		WLAN_Reject_High = -1.0;
		
		double Numbership_Distance = CW.getDistaFromAndroid()/Max_distance;    //将实际的距离数值划归为0-1.0之间的数值
		double Numbership_Speed = FISInfo.getAndroidSpeed()/Max_speed;    //将实际的速度数值划归为0-1.0之间的数值，FISInfo.getAndroidSpeed()手机的实时速度
		double Numbership_Angle = CW.getDistaFromAndroid()/Max_angle;    //将实际的距离数值划归为0-1.0之间的数值
		
		double temp_distance_low = MemberShip_Distance_Low(Numbership_Distance);    
		double temp_distance_medium = MemberShip_Distance_Medium(Numbership_Distance);
		double temp_distance_high = MemberShip_Distance_High(Numbership_Distance);
		
		double temp_speed_low = MemberShip_Speed_Low(Numbership_Speed);
		double temp_speed_medium = MemberShip_Speed_Medium(Numbership_Speed);
		double temp_speed_high =  MemberShip_Speed_High(Numbership_Speed);
		
		double temp_angle_low = MemberShip_Angle_Low(Numbership_Angle);
		double temp_angle_medium = MemberShip_Angle_Medium(Numbership_Angle);
		double temp_angle_high = MemberShip_Angle_High(Numbership_Angle);
		
		/**rule：14 (ALL   High   ALL ---> High)*/
		if(temp_speed_high != 0.0){
			set_WLAN_Reject(5,temp_speed_high);
		}
		
		if(temp_distance_low != 0.0) //distance等于Low
		{ 
			if(temp_speed_low != 0.0)//speed等于Low
			{
				if(temp_angle_high != 0.0)  //angle等于High   
				{	/**rule：2 (Low   Low   High ---> Medium_Low)*/
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_low, temp_angle_high));  //最小值
					set_WLAN_Reject(2,temp);
				}
				    /**rule：1 (Low   Low   Not_High ---> Low)*/
				if(temp_angle_low != 0.0)  //angle等于low
				{
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_low, temp_angle_low));  //最小值
					set_WLAN_Reject(1,temp);
				}
				if(temp_angle_medium != 0.0)  //angle等于medium
				{
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_low, temp_angle_medium));  //最小值
					set_WLAN_Reject(1,temp);
				} 
			}	
			if(temp_speed_medium != 0.0)//speed等于Medium
			{
				if(temp_angle_low != 0.0) //angle等于low
				{   /**rule：3 (Low   Medium   Low ---> Medium_low)*/
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_medium, temp_angle_low));  //最小值
					set_WLAN_Reject(2,temp);
				}
				if(temp_angle_medium != 0.0)  //angle等于medium
				{	/**rule：4 (Low   Medium   Medium ---> Medium)*/
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_medium, temp_angle_medium));  //最小值
					set_WLAN_Reject(3,temp);
				}
				if(temp_angle_high != 0.0)  //angle等于high
				{	/**rule：5 (Low   Medium   High ---> Medium_High)*/
					double temp = Math.min(temp_distance_low, Math.min(temp_speed_medium, temp_angle_high));  //最小值
					set_WLAN_Reject(4,temp);
				}
			}
		}
		
		if(temp_distance_medium != 0.0) //distance等于Medium
		{
			if( temp_speed_low != 0.0)//speed等于Low
			{
				if(temp_angle_high != 0.0)  //angle等于High   
				{	/**rule：7 (Medium   Low   High ---> Medium)*/
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_low, temp_angle_high));  //最小值
					set_WLAN_Reject(3,temp);
				}
				/**rule：6 (Medium   Low   Not_High ---> Medium_Low)*/
				if(temp_angle_low != 0.0)  //angle等于low
				{
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_low, temp_angle_low));  //最小值
					set_WLAN_Reject(2,temp);
				}
				if(temp_angle_medium != 0.0)  //angle等于medium
				{
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_low, temp_angle_medium));  //最小值
					set_WLAN_Reject(2,temp);
				} 
			}	
			if(temp_speed_medium != 0.0)//speed等于Medium(8,9,10)
			{
				if(temp_angle_low != 0.0) //angle等于low
				{   /**rule：8 (Medium   Medium   Low ---> Medium)*/
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_medium, temp_angle_low));  //最小值
					set_WLAN_Reject(3,temp);
				}
				if(temp_angle_medium != 0.0)  //angle等于medium
				{	/**rule：9 (Medium   Medium   Medium ---> Medium_High)*/
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_medium, temp_angle_medium));  //最小值
					set_WLAN_Reject(4,temp);
				}
				if(temp_angle_high != 0.0)  //angle等于high
				{	/**rule：10 (Medium   Medium   High ---> High)*/
					double temp = Math.min(temp_distance_medium, Math.min(temp_speed_medium, temp_angle_high));  //最小值
					set_WLAN_Reject(5,temp);
				}
			}
		}
		
		if(temp_distance_high != 0.0) //distance等于High
		{ 
			/**rule：13 (High   ALL   Not_Low ---> High)*/	
			if(temp_angle_medium != 0.0)//angle等于Medium
			{
				double temp = Math.min(temp_distance_high, temp_angle_medium);  //最小值
				set_WLAN_Reject(5,temp);
			}
			if(temp_angle_high != 0.0)//angle等于High
			{
				double temp = Math.min(temp_distance_high, temp_angle_high);  //最小值
				set_WLAN_Reject(5,temp);
			}
			
			if(temp_speed_low != 0.0 && temp_angle_low != 0.0)  //speed等于low，angle等于low
			{   /**rule：11 (High   Low   Low ---> Medium)*/	
				double temp = Math.min(temp_distance_high, Math.min(temp_speed_low, temp_angle_low));  //最小值
				set_WLAN_Reject(3,temp);
			}
			
			if(temp_speed_medium != 0.0 && temp_angle_low != 0.0 )    //speed等于medium，angle等于low
			{   /**rule：12 (High   Medium   Low ---> Medium_high)*/	
				double temp = Math.min(temp_distance_high, Math.min(temp_speed_medium, temp_angle_low));  //最小值
				set_WLAN_Reject(4,temp);
			}	
		}
	}
	
	/**
	 * 1 代表 ：WLAN_Reject_Low ;        
	 * 2 代表 ：WLAN_Reject_Medium_Low 
	 * 3 代表 ：WLAN_Reject_Medium ;
	 * 4 代表 ：WLAN_Reject_Medium_High ;
	 * 5 代表 ：WLAN_Reject_High
	 */
	private static void set_WLAN_Reject(int order,double v)
	{
		double temp = 0.0 ;
		switch(order)
		{
		case 1:temp = WLAN_Reject_Low; break;
		case 2:temp = WLAN_Reject_Medium_Low; break;
		case 3:temp = WLAN_Reject_Medium; break;
		case 4:temp = WLAN_Reject_Medium_High; break;
		case 5:temp = WLAN_Reject_High; break;
		}
		
		if(temp == -1.0) //如果没有赋过值，就直接赋值
			temp = v;   
		else  //已经赋值过，比较,保留较小值
		{   
			if(temp >= v)
				temp = v;
		}
		
		switch(order)
		{
		case 1:WLAN_Reject_Low = temp; break;
		case 2:WLAN_Reject_Medium_Low = temp; break;
		case 3:WLAN_Reject_Medium = temp; break;
		case 4:WLAN_Reject_Medium_High = temp; break;
		case 5:WLAN_Reject_High = temp; break;
		}
	}

	
}
