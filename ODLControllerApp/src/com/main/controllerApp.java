package com.main;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import com.Test.AndroidInfoTest;
import com.info.FISInfo;
import com.info.GPSInfo;
import com.info.WifiInfoItem;

public class controllerApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8688528084215281988L;
	private JTabbedPane tabbedPane;  
    private JLabel label1,label2; //,label3;  
    private JPanel panel1,panel2; //,panel3; 
    
    JTextArea textAreaPanel1;
    JTextArea textAreaPanel2;
    private SimpleDateFormat sdf;
    
    private String ControllerHostIp = "202.117.49.162";
	private int ControllerPort = 8181;
	private int ControllerAppPort = 22222;             //移动端与该应用交换时使用的socket的端口号
  
    public controllerApp()  
    {
        super("ControllerApp"); 
        setSize(500,395); 
        setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/Controller.png"));
  
        //Container c = getContentPane();  
        tabbedPane=new JTabbedPane();   //创建选项卡面板对象  
        
        //创建标签  
        label1=new JLabel("",SwingConstants.CENTER);  
        label2=new JLabel("",SwingConstants.CENTER);  
        //label3=new JLabel("",SwingConstants.CENTER);  
        //创建面板  
        panel1=new JPanel();  
        panel2=new JPanel();  
        //panel3=new JPanel();  
  
        panel1.add(label1);  
        panel2.add(label2);  
        // panel3.add(label3);  
  
        //将标签面板加入到选项卡面板对象上  
        tabbedPane.addTab("Current Covered",null,panel1,"First panel");  
        tabbedPane.addTab("Current All APs",null,panel2,"Second panel");  
        //tabbedPane.addTab("标签3",null,panel3,"Third panel");  
        
        textAreaPanel1 = new JTextArea(18, 43);
        textAreaPanel1.setLineWrap(true);       //激活自动换行功能 
        textAreaPanel1.setWrapStyleWord(true);       //激活断行不断字功能
        textAreaPanel1.setEditable(false);
        //textAreaPanel1.set
        
        textAreaPanel2 = new JTextArea(18, 43);
        textAreaPanel2.setLineWrap(true);       //激活自动换行功能 
        textAreaPanel2.setWrapStyleWord(true);       //激活断行不断字功能
        textAreaPanel2.setEditable(false);
      
        panel1.add(new JScrollPane(textAreaPanel1));      //将textArea加入滚动条后放入面板中
        panel2.add(new JScrollPane(textAreaPanel2));      //将textArea加入滚动条后放入面板中
        
        textAreaPanel1.setText("");    //清除testArea中的文字
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information(from Android) update："+sdf.format(new Date())+"\n";    //获取系统当前时间
		textAreaPanel1.append(update);		
  
        add(tabbedPane);  
        setBackground(Color.white); 
        setResizable(false); 
  
        //设置位置在电脑屏幕的中心位置
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//获取系统屏幕尺寸
		Dimension frameSize = this.getSize();
		if(frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if(frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		this.setLocation((screenSize.width - frameSize.width) / 2,(screenSize.height - frameSize.height) / 2);
        setVisible(true);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        
        //启动获取topo信息  的线程
        new Thread(new refreshInfo(this,ControllerHostIp,ControllerPort)).start();
        
        //启动从移动端获取wifi信息   的线程
        new Thread(new getFISInfo(this,ControllerHostIp,ControllerAppPort)).start();     
       
        
        
        
    }  

    //主函数入口
	public static void main(String[] args) {
		
		controllerApp ContrApp = new controllerApp();  
	}

	//为面板panel1添加内容
	public void setText2Area1()
	{
		textAreaPanel1.setText("");    //清除testArea中的文字
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information(from Android) update："+sdf.format(new Date())+"\n";    //获取系统当前时间
		textAreaPanel1.append(update);
		for (int i=0;i<FISInfo.wifiInfo.size();++i){ 
			WifiInfoItem wifiInfoItem = FISInfo.wifiInfo.get(i);
			textAreaPanel1.append(" <OF_Switch>| 名称："+wifiInfoItem.getSSID()// + "     ("+update+")"
									+"\n                          | 节点经度："+wifiInfoItem.getLongitude()
									+"\n                          | 节点纬度："+wifiInfoItem.getLatitude()
									+"\n                          | 费用等级："+wifiInfoItem.getChargeLevel()
									+"\n                          | 信号强度："+wifiInfoItem.getRssi()
									+"\n                          | 是否接入："+wifiInfoItem.isConnect()
									+"\n                          | 实时带宽："+new DecimalFormat("#.00").format(wifiInfoItem.getAB()) +" (kb/s)"
									+"\n------------------------------------------------------------------------------------------------------\n"
									);	
		}
	}
	
	//为面板panel2添加内容
	public void setText2Area2(Map<String,GPSInfo>  CurrentTopoInfoMap)
	{
		textAreaPanel2.setText("");    //清除testArea中的文字
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information update："+sdf.format(new Date())+"\n";    //获取系统当前时间
		textAreaPanel2.append(update);
		for (Map.Entry<String,GPSInfo> entry : CurrentTopoInfoMap.entrySet()){ 
			textAreaPanel2.append(" <OF_Switch>| 名称："+entry.getKey()// + "     ("+update+")"
									+"\n                          | 经度："+entry.getValue().getLongitude()
									+"\n                          | 纬度："+entry.getValue().getLatitude()
									+"\n                          | 费用："+entry.getValue().getChargeLevel()
									+"\n------------------------------------------------------------------------------------------------------\n"
									);	
		}
		
	}


}
