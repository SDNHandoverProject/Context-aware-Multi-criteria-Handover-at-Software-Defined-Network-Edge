package javamedia;

import java.io.*;  
import java.awt.*;  
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import javax.swing.*;
import javax.media.*;
import javax.media.rtp.*;
import javax.media.format.*;
import javax.media.control.*;   
import javax.media.protocol.*;  
import javax.media.rtp.event.*;  
import javax.media.rtp.rtcp.SourceDescription;

// 用RTP协议传输数据的类  
public class RTPTransmitThread implements Runnable
{  
	private Socket socket;
	private MediaLocator locator;     // 媒体定位，可以是一个本机文件，也可以是一个网络文件或采集设备得到的数据源  
  	private String ipAddress;         // 发送目的地（接收端）的IP地址  
  	private int portBase;             // 传输端口号
	private JTextArea displayArea;
	private OrderReceThread ORT;
	
	private ObjectOutputStream out = null;
	private InputStream ins = null;
  
  	private Processor processor = null;          // 处理器  
  	private RTPManager rtpMgrs[];                // RTP管理器  
  	private DataSource dataOutput = null;        // 输出的数据源  
  	private SendStream sendStream[];
  	private int count = 1;
  	private ArrayList<VideoInfo> VideoInfoArray;
  	
  	CStateListener CSta_listener = new CStateListener();
  	StreamListener Str_listener = new StreamListener();
  	// 构造函数  
  	public RTPTransmitThread(Socket socket, String ipAddress,int pb,int localport,JTextArea displayArea)                   
  	{  
		this.socket = socket;
		this.ipAddress = ipAddress;    
		this.portBase = pb;
		this.displayArea = displayArea;	
	}  
  
 // 开始传输  
   	// 如果一切正常，就返回 null，否则返回出错原因  
   	public void run(){
   		try {
			ExchangMessage(this.socket);
		} catch (Exception e){
			e.printStackTrace();
		}
   	}
  	
  	private void ExchangMessage(Socket socket) throws Exception {
		
		String videoPath = "F:\\ServerVideo\\PCVideo";
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
				System.out.println("------------");
				out.writeObject(Vinfo);   //传输Java对象
				VideoInfoArray.add(Vinfo);  //加入到队列中
				BR.close(); //关闭
			}
			out.writeObject(null); //传输一个null可以让接收端知道对象发送结束
		}
		
		ins = socket.getInputStream();
    	byte[] buf = new byte[1024];
    	int len = 0;
    	while((len = ins.read(buf)) != 0)
    	{
    		String req_Video = new String(buf,0,len);
    		
    		System.out.println("77777777777777"+req_Video);
    		
    		String FeilName = req_Video.substring(0, req_Video.lastIndexOf('.'));
    		String video = "file:/F:/ServerVideo/PCVideo/"+FeilName+"/"+FeilName+"_IP_1.avi";
    		
    		locator = new MediaLocator(video);
    		
    		System.out.println(video);
    		
    		SendVideo();   //开始发送数据
    	}	
		
  	}	
  	
  	public void SendVideo()
  	{
  		System.out.println("Unrealized="+Processor.Unrealized+"/Configuring="+Processor.Configuring+"/Configured="+Processor.Configured+"/Realizing="+Processor.Realizing+"/ Realized="+Processor.Realized+"/ Prefetching="+Processor.Prefetching+"/ Prefetched="+Processor.Prefetched);
    	String result = null;  
    	System.out.println("--------------------------------------------------------------------------------------"); 
    	
    	result = createProcessor(locator);           // 产生一个处理器，正常的话返回null  
    	
    	System.out.println("resultp:"+result); 
    	System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////");  
    	
    	result = createTransmitter();        // 产生RTP会话，将处理器输出的数据传给指定的IP地址的指定的端口号  
    	
    	System.out.println("resultt:"+result); 
    	System.out.println("----------------processor state is "+ processor.getState()); 
		
    	processor.start();    // 让处理器开始传输 
		
    	System.out.println("----------------processor state is "+ processor.getState());
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		ORT = new OrderReceThread(portBase+5,processor,sendStream);
		new Thread(ORT).start();
  	}  
  
  	// 为指定的媒体定位器产生一个处理器  
  	private String createProcessor(MediaLocator locator) 
	{  
  		//MediaLocator locator
    	if (locator == null)  
     		return "Locator is null";  
  
    	DataSource ds;  
  
    	try {   
      		ds = javax.media.Manager.createDataSource(locator);           // 为定义的MediaLocator定位并实例化一个适当的数据源。 
      		System.out.println("ds:"+ds); 
    	}  
    	catch (Exception e) {  
      		return "Couldn't create DataSource";  
    	}  
  	
    	try {  
    		processor = javax.media.Manager.createProcessor(ds);          // 通过数据源来产生一个处理器 
    		System.out.println("processor:"+processor);
    	}  
    	catch (NoProcessorException npe) {  
      		return "Couldn't create processor";  
    	}  
    	catch (IOException ioe){  
    		return "IOException creating processor";  
    	}  

    	boolean result = waitForState(processor, Processor.Configured);         // 等待处理器配置好  
    	System.out.println("---"+processor.getState());
    	if (result == false)  
      		return "Couldn't configure processor";  
  
    	TrackControl[] tracks = processor.getTrackControls();        // 为媒体流中的每一个磁道得到一个控制器  
    	System.out.println("tracks =" + tracks.length);
  
    	if (tracks == null || tracks.length < 1)              // 确保至少有一个可用的磁道  
      		return "Couldn't find tracks in processor";  
  
    	ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);  
    	System.out.println("cd:"+cd);
    	processor.setContentDescriptor(cd);                // 设置输出的内容描述为RAW_RTP  
                                                       // 从而限定每个磁道支持的格式仅为合法的RTP格式，即它影响后面的 Track.getSupportedFormats()  
    	Format supported[];  
    	Format chosen = null;  
    	boolean atLeastOneTrack = false;  
    	for (int i = 0; i < tracks.length; i++)  // 对每一个磁道，选择一种RTP支持的传输格式  
		{         
      		Format format = tracks[i].getFormat();  //取得该磁道被设置的格式
      		if (tracks[i].isEnabled())              // 如果该磁道可用,isEnabled()返回磁道状态
			{                       
				supported = tracks[i].getSupportedFormats();  //列出该磁道所有可能支持的输入格式
  				System.out.println("supported:"+supported);
        		if (supported.length > 0) 
				{  
          			if (supported[0] instanceof VideoFormat) {  
						chosen = checkForVideoSizes(format,supported[0]);       // 检查视频格式的尺寸，以确保正常工作 
						System.out.println(chosen.toString()); 
					}  
         			else {	
         				 chosen = supported[0];       // 前面已经设置了输出内容描述为RIP，这里支持的格式都可以与RTP配合工作  
  					}
          			tracks[i].setFormat(chosen);      //设置磁道控制器的输入格式                 
          			displayArea.append("              |(Detail)——>Track " + i + " is set to transmit as:"+"  " + chosen+"\n");	      
          			atLeastOneTrack = true;            //至少一个磁道
        		}  
        		else  
          			tracks[i].setEnabled(false);    //如果该磁道没有可能的输入格式，则将该Track设置为不可用.
			}  
      		else  
        		tracks[i].setEnabled(false);
      	}  
   
    	if (!atLeastOneTrack)  
      		return "Couldn't set any of the tracks to a valid RTP format"; 
                                                                                                              
    	result = waitForState(processor, Controller.Realized);        // 等待处理器实现 
		System.out.println(processor.getState());
    	if (result == false)  
      		return "Couldn't realize processor";  
                    		                                                                              
    	dataOutput = processor.getDataOutput();   // 从处理器得到输出的数据源  
		System.out.println("dataOpuput:"+dataOutput);
    	return null;      //全部执行完毕之后会返回null
  	}  
  	
  	//为处理器的每一个媒体磁道产生一个RTP会话  
 	private String createTransmitter() 
	{  
    	PushBufferDataSource pbds = (PushBufferDataSource)dataOutput;     // 将数据源转化为“Push”（推）数据源  
		System.out.println("pbds:"+pbds);
    	PushBufferStream pbss[] = pbds.getStreams();     // 从数据源得到“Push”数据流集合  
  		System.out.println("pbss:"+pbss);
    	rtpMgrs = new RTPManager[pbss.length];        // 为每个磁道产生一个RTP会话管理器  
    	sendStream = new SendStream[pbss.length];
    	
    	for (int i = 0; i < pbss.length; i++) 
		{  
      		try 
			{  
        		rtpMgrs[i] = RTPManager.newInstance();
        		rtpMgrs[i].addSendStreamListener(Str_listener);
  				System.out.println("rtpMgrs[i]:" + rtpMgrs[i]);
        		int port = portBase + 2 * i;                         // 每增加一个磁道，端口号加2  
        		InetAddress ipAddr = InetAddress.getByName(ipAddress);     // 得到发送目的地的IP地址 
				InetAddress loAddr = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
  				System.out.println("ipAddr:" + ipAddr);
				System.out.println("localAddr:" + loAddr);
        		SessionAddress localAddr = new SessionAddress(loAddr,port+1);    // 得到本机的会话地址  
				System.out.println("-------localAddr:" + localAddr);            // 这里传输端使用和接收目的端相同的端口号（实际上也可以不同）
        		SessionAddress destAddr = new SessionAddress( ipAddr,port+1);          // 得到目的机器（接收端）的会话地址  
				System.out.println("-------destAddr:" + destAddr);  
        		rtpMgrs[i].initialize( localAddr);                  // 将本机会话地址传给RTP管理器  
        		rtpMgrs[i].addTarget( destAddr);                    // 加入目的会话地址  
  				System.out.println("rtpMgrs[i]="+ rtpMgrs[i]);
 
				sendStream[i] = rtpMgrs[i].createSendStream(dataOutput,i);           // 产生数据源的RTP传输流  
				displayArea.append("              |(Detail)——>Created RTP session: " + ipAddress + "/" + (port+1) + "\n");
      		}  
      		catch (Exception e) 
      		{  
       			return e.getMessage();  
      		}
      		
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");  
    	}
    
  		try {   //开启发送流
			sendStream[0].start();
			sendStream[1].start();
		} 
  		catch (IOException e) 
  		{
			e.printStackTrace();
		}
  		
    	return null;  
  	}  
  
  	// 由于JPEG和H.263编码标准，只支持一些特定的图像大小，所以这里进行必要的检查，以确保其可以正确编码  
  	Format checkForVideoSizes(Format original, Format supported) 
	{  
    	int width, height;  
    	Dimension size = ((VideoFormat)original).getSize();         // 得到视频图像的尺寸  
    	Format jpegFmt = new Format(VideoFormat.JPEG_RTP);  
    	Format h263Fmt = new Format(VideoFormat.H263_RTP);  
		//System.out.println("checkForVideoSizes...............1,.................");  		

    	if (supported.matches(jpegFmt)) 
		{
			System.out.println("checkForVideoSizes...............supported.matches(jpegFmt)..................................");  
			// 对JPEG格式，视频图像的宽和高必须是8像素的整数倍  
      		width = (size.width % 8 == 0 ? size.width : (int)(size.width / 8) * 8);  
      		height = (size.height % 8 == 0 ? size.height : (int)(size.height / 8) * 8);  
    	}  
    	else if (supported.matches(h263Fmt)) 
		{   
			System.out.println("checkForVideoSizes...............supported.matches(h263Fmt),.................");  
			// H.263格式仅支持三种特定的图像尺寸  
     	 	if (size.width <= 128){	
     	 		//System.out.println("checkForVideoSizes...............supported.matches(h263Fmt)111111111111111111111111111111111,.................");  
          		width = 128;  
          		height = 96;  
      		}  
      		else if (size.width <= 176) 
			{  	//System.out.println("checkForVideoSizes...............supported.matches(h263Fmt)222222222222222222222222222222222,.................");  
          		width = 176;  
          		height = 144;  
      		}  
      		else 
			{	//System.out.println("checkForVideoSizes...............supported.matches(h263Fmt)333333333333333333333333333333333,.................");  
          		width = 352;  
          		height = 288;  
      		}  
    	}  
    	else {       
			//System.out.println("checkForVideoSizes...............rather,.................");  
			// 对其他格式不予处理  
     		return supported;  
    	}  
  
		//System.out.println("checkForVideoSizes...............ok.................");  
    	return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,  
                            null,Format.NOT_SPECIFIED)).intersects(supported);                // 返回经过处理后的视频格式  
 	}  
  
  	// 停止传输  
  	public void stop() 
	{  
    	synchronized (this) 
		{  
      		if (processor != null) 
			{  
        		processor.stop();  
        		processor.close();                          // 停止处理器  
       			processor = null;                           // 关闭处理器  
        		for (int i = 0; i < rtpMgrs.length; i++) 
				{  	// 删除所有RTP管理器  
          			rtpMgrs[i].removeTargets( "Session ended.");  
          			rtpMgrs[i].dispose();  
       			}  
			}  
    	}  
  	}  
  
  	// 以下两个变量为对处理器状态改变的处理服务  
  	private Integer stateLock = new Integer(0);        // 状态锁变量  
  	private boolean failed = false;                    // 是否失败的状态标志  
  
  	// 得到状态锁  
  	Integer getStateLock()
  	{  
    	return stateLock;  
  	}  
  
  	// 设置失败标志  
  	void setFailed() 
	{  
    	failed = true;  
  	}  
  
  	// 等待处理器达到相应的状态  
  	private synchronized boolean waitForState(Processor p, int state) 
	{  
    	p.addControllerListener(CSta_listener);          // 为处理器加上状态监听  
    	failed = false;  
  
    	if (state == Processor.Configured) 
		{                                 // 配置处理器  
      		p.configure();  
      		System.out.println("processor call （配置）configure()"+ Processor.Configured);
    	}  
    	else if (state == Processor.Realized) {                          
    		//实现处理器  
      		p.realize();   
      		System.out.println("processor call （实现）realize()"+ Processor.Realized);
    	}  
  
    	// 一直等待，直到成功达到所需状态，或失败  
    	while (p.getState() < state && !failed) 
		{  
      		synchronized (getStateLock()) 
			{  
				try{  
          			getStateLock().wait();
          			System.out.println("-------------等待--------------"); 
        		}  
        		catch (InterruptedException ie) {  
          			return false;  
        		}  
      		}  
    	}  
		System.out.println("----------------processor state is "+ p.getState()); 
    	if (failed)  
      		return false;  
    	else  
      		return true;  
  	}  
  
  	// 内部类：处理器的状态监听器  
  	class CStateListener implements ControllerListener
	{  
  		//实现ControllerListener接口中的方法
    	public void controllerUpdate(ControllerEvent ce) 
		{  
    		synchronized (getStateLock()) 
			{	System.out.println("----------控制器事件"+processor.getState()+"，唤醒线程------------");
      			getStateLock().notifyAll();  
    		} 
    		
      		// 如果在处理器配置或实现过程中出现错误，它将关闭  
      		if (ce instanceof ControllerClosedEvent)   //控制器关闭  
      		{	System.out.println("<<<<<<<<<<<<<<<<控制器关闭<<<<<<<<<<<<<<<<<<<<<<");	
        		setFailed(); 
        		if (ce instanceof ControllerErrorEvent) {  // 控制器错误       
    		 	 	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器错误<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");	
    			}
    			else if (ce instanceof DataLostErrorEvent ){             
    		    	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<数据丢失错误<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");          
    			}
      		}
      		// 对于所有的控制器事件，通知在waitForState方法中等待的线程  
      		else if (ce instanceof DurationUpdateEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<周期更新事件<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof CachingControlEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<缓存控制事件<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof FormatChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<格式改变事件<<<<<<<<<<<<<<<<<<<<<<");	
      			if (ce instanceof SizeChangeEvent) 
    			{  
      				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<尺寸改变事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");	
    			}
      		}
      		else if (ce instanceof MediaTimeSetEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<设置媒体时间事件<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof RateChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<Rate改变事件<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof StopTimeChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<停止时间改变事件<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof TransitionEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<传输事件<<<<<<<<<<<<<<<<<<<<<<");		
      			
      			if (ce instanceof ConfigureCompleteEvent){
    		   		 System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器配置完成事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");            
    			}
    			else if (ce instanceof PrefetchCompleteEvent){              
    		  	  	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器预取完成事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof RealizeCompleteEvent){              
    		 		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器实现完成事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof StartEvent){              
    		   		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器开始事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof StopEvent)
    			{ 
    				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<控制器停止事件<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
 
    				processor.close();      // 停止处理器
           			processor = null; 
           			dataOutput = null;      //输出数据源
           			
           			String piecePath = choiceNextPiece();    //返回一个视频文件片段的path字符串
           			
           			try{
           				MediaLocator Nloca = new MediaLocator(piecePath);
           				
           				sendNextpiece(Nloca);    //发送下一个视频片段 
           			}
           			catch (Exception e) {System.out.println("77777777777777777777777777777777");
           				e.printStackTrace();
           			}    
 
    			}
      		}
		}
		
  	}
  	
  	//选择下一个视频片段
  	private String choiceNextPiece() {
  		String path = null;
  		try {
			path = locator.getURL().toString();     System.out.println(path);
			
		} catch (MalformedURLException e) {e.printStackTrace();}
  		
  		
  		String[] subPath = path.split("/");   // for(String s:subPath) System.out.println(s+"==");
		String[] info = subPath[subPath.length-1].split("_"); // for(String s:info)System.out.println("-------"+s);
																//System.out.println("info[2]::"+info[2]);
		String ch = String.valueOf(++count);         //System.out.println("ch-========"+ch);
		info[2] = info[2].replaceAll("(\\d+)(.avi)", ch+"$2");    //正则表达式替换      //System.out.println("info[2]::"+info[2]);
		
		int order = (int)(Math.random() * 4) + 1;//ORT.get_askNextPieceLevel(); //该条语句由获得的指令取代
  		switch(order)
  		{ 
  			case 1: info[1] = "IP";
  					break;
  			case 2: info[1] = "CR";
  					break;
  			case 3: info[1] = "TC";
  					break;
  			case 4: info[1] = "TS";
  					break;
  		}
  		String nextURL = new StringBuilder().append(path.substring(0,path.lastIndexOf("/"))).append("/")
  				.append(info[0]).append("_").append(info[1]).append("_").append(info[2]).toString();
  		System.out.println("nextURL::"+nextURL);
  		return nextURL;
  	}
  		
  	//发送下一个视频片段
  	private void sendNextpiece(MediaLocator locator) throws Exception{
			
			DataSource ds = null;  
			try {   
				ds = javax.media.Manager.createDataSource(locator);           // 为定义的MediaLocator定位并实例化一个适当的数据源。 
			}  
			catch (Exception e) 
			{
				for(int i=0;i<sendStream.length;i++){  //关闭流
					sendStream[0].close();
					sendStream[1].close();
				}
				
				Thread.currentThread().interrupt();
			}  
			try {  
				processor = javax.media.Manager.createProcessor(ds);          // 通过数据源来产生一个处理器 
			}  
			catch (NoProcessorException npe) {Thread.currentThread().interrupt();}  
			catch (IOException ioe){Thread.currentThread().interrupt();}  

			boolean result = waitForState(processor, Processor.Configured);         // 等待处理器配置好  
			if (result == false)  
				throw new Exception("Couldn't configure processor");  
 		  
			TrackControl[] tracks = processor.getTrackControls();        // 为媒体流中的每一个磁道得到一个控制器  
  
			if (tracks == null || tracks.length < 1)              // 确保至少有一个可用的磁道  
				throw new Exception("Couldn't find tracks in processor");  
 		  
			ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);  
			System.out.println("cd:"+cd);
			processor.setContentDescriptor(cd);                // 设置输出的内容描述为RAW_RTP  
			                                               // 从而限定每个磁道支持的格式仅为合法的RTP格式，即它影响后面的 Track.getSupportedFormats()  
			Format supported[];  
			Format chosen = null;  
			boolean atLeastOneTrack = false;  
			for (int i = 0; i < tracks.length; i++)  // 对每一个磁道，选择一种RTP支持的传输格式  
			{         
				Format format = tracks[i].getFormat();  //取得该磁道被设置的格式
				if (tracks[i].isEnabled())              // 如果该磁道可用,isEnabled()返回磁道状态
				{                       
					supported = tracks[i].getSupportedFormats();  //列出该磁道所有可能支持的输入格式
					System.out.println("supported:"+supported);
					if (supported.length > 0) 
					{  
			  			if (supported[0] instanceof VideoFormat) {  
							chosen = checkForVideoSizes(format,supported[0]);       // 检查视频格式的尺寸，以确保正常工作 
							System.out.println(chosen.toString()); 
						}  
			 			else {	
			 				 chosen = supported[0];       // 前面已经设置了输出内容描述为RIP，这里支持的格式都可以与RTP配合工作  
						}
			  			tracks[i].setFormat(chosen);      //设置磁道控制器的输入格式                    
			  			atLeastOneTrack = true;            //至少一个磁道
					}  
					else  
			  			tracks[i].setEnabled(false);    //如果该磁道没有可能的输入格式，则将该Track设置为不可用.
				}  
				else  
					tracks[i].setEnabled(false);
			}  
 		   
			if (!atLeastOneTrack)  
				throw new Exception("Couldn't set any of the tracks to a valid RTP format"); 
			                                                                                                      
			result = waitForState(processor, Controller.Realized);        // 等待处理器实现 
			System.out.println(processor.getState());
			if (result == false)  
				throw new Exception("Couldn't realize processor");  
			            		                                                                              
			dataOutput = processor.getDataOutput();   // 从处理器得到输出的数据源  
			
			for (int i = 0; i < 2; i++) 
			{  
				try 
				{ 	
					sendStream[i] = rtpMgrs[i].createSendStream(dataOutput,i);           // 产生数据源的RTP传输流  
					sendStream[i].start();// 开始RTP数据流发送  
				}  
				catch (Exception e) 
				{  
					e.printStackTrace();  
				}
			}  
			processor.start();
		}
  	
  	//流监听器
  	class StreamListener implements SendStreamListener
	{
		//实现SendStreamListener接口中的方法
		public void update(SendStreamEvent SE) {
			// 
			System.out.println("<<<<<<<<<<<<<<<<流事件<<<<<<<<<<<<<<<<<<<<<<");
			if (SE instanceof ActiveSendStreamEvent) 
			{
				System.out.println("<<<<<<<<<<<<<<<<数据达到流事件<<<<<<<<<<<<<<<<<<<<<<");
			}
			else if (SE instanceof InactiveSendStreamEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<数据停止到达该Sendstraem事件<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof LocalPayloadChangeEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<负载改变事件<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof NewSendStreamEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<新发送流事件<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof StreamClosedEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<流关闭事件<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
				
			}
		}  
	 
	}
}  


