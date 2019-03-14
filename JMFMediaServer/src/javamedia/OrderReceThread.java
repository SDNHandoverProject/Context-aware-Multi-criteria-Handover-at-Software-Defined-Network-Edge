package javamedia;

import java.net.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.*;

public class OrderReceThread implements Runnable{

	int port = 0;
	private int askNextPieceLevel = 4;
	Processor processor = null;
	DatagramSocket ds = null;
	
	public OrderReceThread(int port){
		System.out.println("OrderReceThread 初始化！！");
		this.port = port;
	}
	
	public OrderReceThread(int port,Processor processor,SendStream sendStream[]){
		this.port = port;
		this.processor = processor;
	}
	
	public int get_askNextPieceLevel()
	{
		return askNextPieceLevel;
	}
	
	public void run(){
		//循环接收order//1.建立UDP socket服务
		try {ds = new DatagramSocket(this.port);} catch (SocketException e1) {e1.printStackTrace();}
		
		while(true){
			receOrder();//阻塞式的循环接收命令
		}
	}

	private void receOrder(){
		try{
			//2.创建数据包
			byte[] buf = new byte[10];
			DatagramPacket dp = new DatagramPacket(buf,buf.length);

			//3.使用接收方法将数据存储到数据包中
			ds.receive(dp);		//阻塞式的
			System.out.println("ds.receive(dp);	");
			
			//4.使用数据包对象的方法，解析其中的数据，比如端口，地址，数据等
			//String ip = dp.getAddress().getHostAddress();
			//int port = dp.getPort();
			String Text = new String(dp.getData(),0,dp.getLength());
			
			askNextPieceLevel = Integer.parseInt(Text);
			//System.out.println("askNextPieceLevel = "+askNextPieceLevel);
		}
		catch(Exception e){e.printStackTrace();}
	} 
}
	
	

