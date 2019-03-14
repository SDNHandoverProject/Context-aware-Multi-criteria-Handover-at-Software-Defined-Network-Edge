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
		System.out.println("OrderReceThread ��ʼ������");
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
		//ѭ������order//1.����UDP socket����
		try {ds = new DatagramSocket(this.port);} catch (SocketException e1) {e1.printStackTrace();}
		
		while(true){
			receOrder();//����ʽ��ѭ����������
		}
	}

	private void receOrder(){
		try{
			//2.�������ݰ�
			byte[] buf = new byte[10];
			DatagramPacket dp = new DatagramPacket(buf,buf.length);

			//3.ʹ�ý��շ��������ݴ洢�����ݰ���
			ds.receive(dp);		//����ʽ��
			System.out.println("ds.receive(dp);	");
			
			//4.ʹ�����ݰ�����ķ������������е����ݣ�����˿ڣ���ַ�����ݵ�
			//String ip = dp.getAddress().getHostAddress();
			//int port = dp.getPort();
			String Text = new String(dp.getData(),0,dp.getLength());
			
			askNextPieceLevel = Integer.parseInt(Text);
			//System.out.println("askNextPieceLevel = "+askNextPieceLevel);
		}
		catch(Exception e){e.printStackTrace();}
	} 
}
	
	

