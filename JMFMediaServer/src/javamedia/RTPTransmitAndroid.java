package javamedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.media.Format;
import javax.swing.JTextArea;
import javax.media.MediaLocator;
import javax.swing.SwingUtilities;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class RTPTransmitAndroid implements Runnable{
	
	Socket socket;
	private String ipAddress;
	private int port;
	private String file;
	private JTextArea displayArea;
	private OrderReceThread ORT;
	private ObjectOutputStream out = null;
	private InputStream ins = null;
	
	ArrayList<VideoInfo> VideoInfoArray;
	
	public RTPTransmitAndroid(Socket socket,String ipAddress,int pb,JTextArea displayArea)
	{  
		this.socket = socket;
		this.ipAddress = ipAddress;    
		this.port = pb;  
		this.displayArea = displayArea;
	}

	public native int init_RTP(String ipAddress,int port);//初始化本地rtp会话
	
	public native void Sender(String file);
	
	static{
		System.loadLibrary("RTPTransmit2Android");
	}

	@Override
	public void run(){
		
		int ret = init_RTP(ipAddress,port);
		if(ret != 1){
			SwingUtilities.invokeLater(new Runnable(){
	      		public void run(){
	      			displayArea.append("初始化本地RTP会话失败！！！！");
	      		}
			});
		}
		
		//开启接收码率切换线程
		ORT = new OrderReceThread( port+2 );  //port+1可能是被对应的RTCP端口占用
		new Thread(ORT).start();
		
		try{
			ExchangMessage(this.socket);
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public void ShowInfo(final String message){	
		//打印出信息
		SwingUtilities.invokeLater(new Runnable(){
      		public void run(){
      			displayArea.append(message+"");
      		}
		});
	}
	
	public int getNextPieceLevel(){
		int NextPieceLevel = ORT.get_askNextPieceLevel();
		return NextPieceLevel;
	}
	
	private void ExchangMessage(Socket socket) throws Exception 
	{
		String videoPath = "F:\\ServerVideo\\MobileVideo";
		File filePath = new File(videoPath);
		out = new ObjectOutputStream(socket.getOutputStream());
		VideoInfoArray = new ArrayList<VideoInfo>(); 
		
		if(filePath.isDirectory())
		{
			String[] fs = filePath.list();
			for(String item : fs)
			{
				String PropFile = videoPath+"\\"+item+"\\"+item+".properties";//进入具体子文件夹读取配置文件
				BufferedReader BR = new BufferedReader(new FileReader(PropFile));
				VideoInfo Vinfo = new VideoInfo();
				
				String line = "";
				while((line = BR.readLine()) != null)
				{
					System.out.println("------------"+line);
					int index = line.indexOf("：");   //：第一次出现的索引
					String key = line.substring(0, index);
					switch (key)
					{
					case "名称":
						Vinfo.setVideoName(line.substring(index+1));
						break;
					case "类型":
						Vinfo.setStyle(line.substring(index+1));
						break;
					case "格式":
						Vinfo.setFormat(line.substring(index+1));
						break;
					case "时长":
						Vinfo.setVideoTime(line.substring(index+1));
						break;
					case "创建日期":
						Vinfo.setCreateDate(line.substring(index+1));
						break;
					case "作者":
						Vinfo.setAuthor(line.substring(index+1));
						break;
						
					default:
						break;
					}	
				}	
				System.out.println("------------------------");
				out.writeObject(Vinfo);   //传输Java对象
				VideoInfoArray.add(Vinfo);  //加入到队列中
				BR.close(); //关闭
			}
			out.writeObject(null); //传输一个null可以让接收端知道对象发送结束
		}
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		ins = socket.getInputStream();
    	byte[] buf = new byte[1024];
    	int len = 0;
//    	while((len = ins.read(buf)) != 0)
//    	{
    		len = ins.read(buf);
    		String req_Video = new String(buf,0,len);
    		
    		//System.out.println("req_Video====="+req_Video);
    		
    		String FeilName = req_Video.substring(0, req_Video.lastIndexOf('.'));
    		String Video = "F:\\ServerVideo\\MobileVideo\\"+FeilName+"\\"+FeilName+"_IP_1.264";
    		   //System.out.println(Video);
    		
    		Sender(Video);//开始发送数据
//    	}
    	socket.close();
	}
	
}
