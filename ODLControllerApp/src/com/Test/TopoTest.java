package com.Test;

public class TopoTest { // "flow-node-inventory:name\":\"ovs_

	private static String Res_Str = "" ;
	private static int Node_Numbers = 30;

	public static String getRes_Str() {return Random_Topo_String();}

	public static void setRes_Str(String res_Str) {Res_Str = res_Str;}
	
	public static String Random_Topo_String()
	{
		for(int i=1;i<=Node_Numbers; i++)  //一直存在30个switch节点
			Res_Str = Res_Str + "\"flow-node-inventory:name\":\"ovs_"+i+"\",";
		//System.out.println(Res_Str);
		return Res_Str;
	}
	
}
