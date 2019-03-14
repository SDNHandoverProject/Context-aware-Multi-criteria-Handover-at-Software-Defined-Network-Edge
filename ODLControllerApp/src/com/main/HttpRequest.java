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
     * ��ָ��URL����GET����������
     * @param url  ���������URL
     * @param param   ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @return URL ������Զ����Դ����Ӧ���
     */
    public static void sendGet(String url, String param) {
        sendGet(url, param, "utf-8");
    }


    /**
     * ��ָ��URL����GET����������
     * @param url  ���������URL
     * @param param  ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
     * @param charSet  ��ҳ����
     * @return URL ������Զ����Դ����Ӧ���
     */
    public static void sendGet(String url, String param,String charSet) {
//        String result = ""; 
//        BufferedReader in = null;
//        try {
//            String urlNameString = url + "?" + param;
//            URL realUrl = new URL(urlNameString);
//            // �򿪺�URL֮�������
//            URLConnection connection = realUrl.openConnection();
//            // ����ͨ�õ���������
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent", "Mozilla/4.0(compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            if(basicAuth !=null){
//                connection.setRequestProperty("Authorization",basicAuth);
//            }
//            // ����ʵ�ʵ�����
//            connection.connect();
//            
//            // ���� BufferedReader����������ȡURL����Ӧ
//            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),charSet));
//            
//            String line;
//            while ((line = in.readLine()) != null) {
//            	//System.out.println("line:"+line);
//                result += line;
//            }
//        } catch (Exception e) {
//            System.out.println("����GET��������쳣��" + e);
//            e.printStackTrace();
//        }
//        // ʹ��finally�����ر�������
//        finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
        
        GetTopoInfo(TopoTest.getRes_Str());      //����������ڴ���Ӧ���ص��ַ����л�ȡ����Ҫ�õ�������
        //return result;
    }

    /**
     * ����Ӧ���ص��ַ����л�ȡ����Ҫ�õ�������
     * @param respondStr �������Ӧ��ȡ�����ַ���
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