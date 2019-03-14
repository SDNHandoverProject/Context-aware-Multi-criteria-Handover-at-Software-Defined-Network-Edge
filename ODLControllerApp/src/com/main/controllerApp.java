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
	private int ControllerAppPort = 22222;             //�ƶ������Ӧ�ý���ʱʹ�õ�socket�Ķ˿ں�
  
    public controllerApp()  
    {
        super("ControllerApp"); 
        setSize(500,395); 
        setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/Controller.png"));
  
        //Container c = getContentPane();  
        tabbedPane=new JTabbedPane();   //����ѡ�������  
        
        //������ǩ  
        label1=new JLabel("",SwingConstants.CENTER);  
        label2=new JLabel("",SwingConstants.CENTER);  
        //label3=new JLabel("",SwingConstants.CENTER);  
        //�������  
        panel1=new JPanel();  
        panel2=new JPanel();  
        //panel3=new JPanel();  
  
        panel1.add(label1);  
        panel2.add(label2);  
        // panel3.add(label3);  
  
        //����ǩ�����뵽ѡ���������  
        tabbedPane.addTab("Current Covered",null,panel1,"First panel");  
        tabbedPane.addTab("Current All APs",null,panel2,"Second panel");  
        //tabbedPane.addTab("��ǩ3",null,panel3,"Third panel");  
        
        textAreaPanel1 = new JTextArea(18, 43);
        textAreaPanel1.setLineWrap(true);       //�����Զ����й��� 
        textAreaPanel1.setWrapStyleWord(true);       //������в����ֹ���
        textAreaPanel1.setEditable(false);
        //textAreaPanel1.set
        
        textAreaPanel2 = new JTextArea(18, 43);
        textAreaPanel2.setLineWrap(true);       //�����Զ����й��� 
        textAreaPanel2.setWrapStyleWord(true);       //������в����ֹ���
        textAreaPanel2.setEditable(false);
      
        panel1.add(new JScrollPane(textAreaPanel1));      //��textArea�������������������
        panel2.add(new JScrollPane(textAreaPanel2));      //��textArea�������������������
        
        textAreaPanel1.setText("");    //���testArea�е�����
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information(from Android) update��"+sdf.format(new Date())+"\n";    //��ȡϵͳ��ǰʱ��
		textAreaPanel1.append(update);		
  
        add(tabbedPane);  
        setBackground(Color.white); 
        setResizable(false); 
  
        //����λ���ڵ�����Ļ������λ��
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//��ȡϵͳ��Ļ�ߴ�
		Dimension frameSize = this.getSize();
		if(frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if(frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		this.setLocation((screenSize.width - frameSize.width) / 2,(screenSize.height - frameSize.height) / 2);
        setVisible(true);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        
        //������ȡtopo��Ϣ  ���߳�
        new Thread(new refreshInfo(this,ControllerHostIp,ControllerPort)).start();
        
        //�������ƶ��˻�ȡwifi��Ϣ   ���߳�
        new Thread(new getFISInfo(this,ControllerHostIp,ControllerAppPort)).start();     
       
        
        
        
    }  

    //���������
	public static void main(String[] args) {
		
		controllerApp ContrApp = new controllerApp();  
	}

	//Ϊ���panel1�������
	public void setText2Area1()
	{
		textAreaPanel1.setText("");    //���testArea�е�����
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information(from Android) update��"+sdf.format(new Date())+"\n";    //��ȡϵͳ��ǰʱ��
		textAreaPanel1.append(update);
		for (int i=0;i<FISInfo.wifiInfo.size();++i){ 
			WifiInfoItem wifiInfoItem = FISInfo.wifiInfo.get(i);
			textAreaPanel1.append(" <OF_Switch>| ���ƣ�"+wifiInfoItem.getSSID()// + "     ("+update+")"
									+"\n                          | �ڵ㾭�ȣ�"+wifiInfoItem.getLongitude()
									+"\n                          | �ڵ�γ�ȣ�"+wifiInfoItem.getLatitude()
									+"\n                          | ���õȼ���"+wifiInfoItem.getChargeLevel()
									+"\n                          | �ź�ǿ�ȣ�"+wifiInfoItem.getRssi()
									+"\n                          | �Ƿ���룺"+wifiInfoItem.isConnect()
									+"\n                          | ʵʱ����"+new DecimalFormat("#.00").format(wifiInfoItem.getAB()) +" (kb/s)"
									+"\n------------------------------------------------------------------------------------------------------\n"
									);	
		}
	}
	
	//Ϊ���panel2�������
	public void setText2Area2(Map<String,GPSInfo>  CurrentTopoInfoMap)
	{
		textAreaPanel2.setText("");    //���testArea�е�����
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update = " Information update��"+sdf.format(new Date())+"\n";    //��ȡϵͳ��ǰʱ��
		textAreaPanel2.append(update);
		for (Map.Entry<String,GPSInfo> entry : CurrentTopoInfoMap.entrySet()){ 
			textAreaPanel2.append(" <OF_Switch>| ���ƣ�"+entry.getKey()// + "     ("+update+")"
									+"\n                          | ���ȣ�"+entry.getValue().getLongitude()
									+"\n                          | γ�ȣ�"+entry.getValue().getLatitude()
									+"\n                          | ���ã�"+entry.getValue().getChargeLevel()
									+"\n------------------------------------------------------------------------------------------------------\n"
									);	
		}
		
	}


}
