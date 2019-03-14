package com.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.Test.TopoTest;
import com.info.TopoInfo;
//import org.json.*;  

public class HttpRequest {

    private static String basicAuth = null;

    public static void setBasicAuth(String str){
        HttpRequest.basicAuth = str;
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url  发送请求的URL
     * @param param   请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static void sendGet(String url, String param) {
        sendGet(url, param, "utf-8");
    }


    /**
     * 向指定URL发送GET方法的请求
     * @param url  发送请求的URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param charSet  网页编码
     * @return URL 所代表远程资源的响应结果
     */
    public static void sendGet(String url, String param,String charSet) {
//        String result = ""; 
//        BufferedReader in = null;
//        try {
//            String urlNameString = url + "?" + param;
//            URL realUrl = new URL(urlNameString);
//            // 打开和URL之间的连接
//            URLConnection connection = realUrl.openConnection();
//            // 设置通用的请求属性
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent", "Mozilla/4.0(compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            if(basicAuth !=null){
//                connection.setRequestProperty("Authorization",basicAuth);
//            }
//            // 建立实际的连接
//            connection.connect();
//            
//            // 定义 BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charSet));
//            
//            String line;
//            while ((line = in.readLine()) != null) {
//            	//System.out.println("line:"+line);
//                result += line;
//            }
//        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
//            e.printStackTrace();
//        }
//        // 使用finally块来关闭输入流
//        finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
        
        GetTopoInfo(TopoTest.getRes_Str());      //这个方法用于从响应返回的字符串中获取到想要得到的数据
        //return result;
    }

    /**
     * 从响应返回的字符串中获取到想要得到的数据
     * @param respondStr 传入的响应获取到的字符串
     */
    private static void GetTopoInfo(String respondStr)
    {
    	TopoInfo.CurrentTopoInfoMap.clear();
    	String[] res = respondStr.split(","); 
    	for(String s:res){    //System.out.println("ssss_:"+s);
    		if(s.contains("\"flow-node-inventory:name\":\"ovs_")){
    			String str = s.substring(s.indexOf("ovs_"), s.length()-1);		//System.out.println(str);
    			if(TopoInfo.AllTopoInfoMap.containsKey(str)){
    				TopoInfo.CurrentTopoInfoMap.put(str, TopoInfo.AllTopoInfoMap.get(str));
    			}
    		}		
    	}
    
    }
    
}