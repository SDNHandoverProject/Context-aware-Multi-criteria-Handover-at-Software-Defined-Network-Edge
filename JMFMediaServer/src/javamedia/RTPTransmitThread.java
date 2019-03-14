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

// ��RTPЭ�鴫�����ݵ���  
public class RTPTransmitThread implements Runnable
{  
	private Socket socket;
	private MediaLocator locator;     // ý�嶨λ��������һ�������ļ���Ҳ������һ�������ļ���ɼ��豸�õ�������Դ  
  	private String ipAddress;         // ����Ŀ�ĵأ����նˣ���IP��ַ  
  	private int portBase;             // ����˿ں�
	private JTextArea displayArea;
	private OrderReceThread ORT;
	
	private ObjectOutputStream out = null;
	private InputStream ins = null;
  
  	private Processor processor = null;          // ������  
  	private RTPManager rtpMgrs[];                // RTP������  
  	private DataSource dataOutput = null;        // ���������Դ  
  	private SendStream sendStream[];
  	private int count = 1;
  	private ArrayList<VideoInfo> VideoInfoArray;
  	
  	CStateListener CSta_listener = new CStateListener();
  	StreamListener Str_listener = new StreamListener();
  	// ���캯��  
  	public RTPTransmitThread(Socket socket, String ipAddress,int pb,int localport,JTextArea displayArea)                   
  	{  
		this.socket = socket;
		this.ipAddress = ipAddress;    
		this.portBase = pb;
		this.displayArea = displayArea;	
	}  
  
 // ��ʼ����  
   	// ���һ���������ͷ��� null�����򷵻س���ԭ��  
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
				String PropFile = videoPath+"\\"+item+"\\"+item+".properties";//����������ļ��ж�ȡ�����ļ�
				BufferedReader BR = new BufferedReader(new FileReader(PropFile));
				VideoInfo Vinfo = new VideoInfo();
				
				String line = "";
				while((line = BR.readLine()) != null)
				{
					int index = line.indexOf("��");   //����һ�γ��ֵ�����
					String key = line.substring(0, index);
					switch (key)
					{
					case "����":
						Vinfo.setVideoName(line.substring(index+1));
						break;
					case "����":
						Vinfo.setStyle(line.substring(index+1));
						break;
					case "��ʽ":
						Vinfo.setFormat(line.substring(index+1));
						break;
					case "ʱ��":
						Vinfo.setVideoTime(line.substring(index+1));
						break;
					case "��������":
						Vinfo.setCreateDate(line.substring(index+1));
						break;
					case "����":
						Vinfo.setAuthor(line.substring(index+1));
						break;
						
					default:
						break;
					}	
				}	
				System.out.println("------------");
				out.writeObject(Vinfo);   //����Java����
				VideoInfoArray.add(Vinfo);  //���뵽������
				BR.close(); //�ر�
			}
			out.writeObject(null); //����һ��null�����ý��ն�֪�������ͽ���
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
    		
    		SendVideo();   //��ʼ��������
    	}	
		
  	}	
  	
  	public void SendVideo()
  	{
  		System.out.println("Unrealized="+Processor.Unrealized+"/Configuring="+Processor.Configuring+"/Configured="+Processor.Configured+"/Realizing="+Processor.Realizing+"/ Realized="+Processor.Realized+"/ Prefetching="+Processor.Prefetching+"/ Prefetched="+Processor.Prefetched);
    	String result = null;  
    	System.out.println("--------------------------------------------------------------------------------------"); 
    	
    	result = createProcessor(locator);           // ����һ���������������Ļ�����null  
    	
    	System.out.println("resultp:"+result); 
    	System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////");  
    	
    	result = createTransmitter();        // ����RTP�Ự������������������ݴ���ָ����IP��ַ��ָ���Ķ˿ں�  
    	
    	System.out.println("resultt:"+result); 
    	System.out.println("----------------processor state is "+ processor.getState()); 
		
    	processor.start();    // �ô�������ʼ���� 
		
    	System.out.println("----------------processor state is "+ processor.getState());
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		ORT = new OrderReceThread(portBase+5,processor,sendStream);
		new Thread(ORT).start();
  	}  
  
  	// Ϊָ����ý�嶨λ������һ��������  
  	private String createProcessor(MediaLocator locator) 
	{  
  		//MediaLocator locator
    	if (locator == null)  
     		return "Locator is null";  
  
    	DataSource ds;  
  
    	try {   
      		ds = javax.media.Manager.createDataSource(locator);           // Ϊ�����MediaLocator��λ��ʵ����һ���ʵ�������Դ�� 
      		System.out.println("ds:"+ds); 
    	}  
    	catch (Exception e) {  
      		return "Couldn't create DataSource";  
    	}  
  	
    	try {  
    		processor = javax.media.Manager.createProcessor(ds);          // ͨ������Դ������һ�������� 
    		System.out.println("processor:"+processor);
    	}  
    	catch (NoProcessorException npe) {  
      		return "Couldn't create processor";  
    	}  
    	catch (IOException ioe){  
    		return "IOException creating processor";  
    	}  

    	boolean result = waitForState(processor, Processor.Configured);         // �ȴ����������ú�  
    	System.out.println("---"+processor.getState());
    	if (result == false)  
      		return "Couldn't configure processor";  
  
    	TrackControl[] tracks = processor.getTrackControls();        // Ϊý�����е�ÿһ���ŵ��õ�һ��������  
    	System.out.println("tracks =" + tracks.length);
  
    	if (tracks == null || tracks.length < 1)              // ȷ��������һ�����õĴŵ�  
      		return "Couldn't find tracks in processor";  
  
    	ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);  
    	System.out.println("cd:"+cd);
    	processor.setContentDescriptor(cd);                // �����������������ΪRAW_RTP  
                                                       // �Ӷ��޶�ÿ���ŵ�֧�ֵĸ�ʽ��Ϊ�Ϸ���RTP��ʽ������Ӱ������ Track.getSupportedFormats()  
    	Format supported[];  
    	Format chosen = null;  
    	boolean atLeastOneTrack = false;  
    	for (int i = 0; i < tracks.length; i++)  // ��ÿһ���ŵ���ѡ��һ��RTP֧�ֵĴ����ʽ  
		{         
      		Format format = tracks[i].getFormat();  //ȡ�øôŵ������õĸ�ʽ
      		if (tracks[i].isEnabled())              // ����ôŵ�����,isEnabled()���شŵ�״̬
			{                       
				supported = tracks[i].getSupportedFormats();  //�г��ôŵ����п���֧�ֵ������ʽ
  				System.out.println("supported:"+supported);
        		if (supported.length > 0) 
				{  
          			if (supported[0] instanceof VideoFormat) {  
						chosen = checkForVideoSizes(format,supported[0]);       // �����Ƶ��ʽ�ĳߴ磬��ȷ���������� 
						System.out.println(chosen.toString()); 
					}  
         			else {	
         				 chosen = supported[0];       // ǰ���Ѿ������������������ΪRIP������֧�ֵĸ�ʽ��������RTP��Ϲ���  
  					}
          			tracks[i].setFormat(chosen);      //���ôŵ��������������ʽ                 
          			displayArea.append("              |(Detail)����>Track " + i + " is set to transmit as:"+"  " + chosen+"\n");	      
          			atLeastOneTrack = true;            //����һ���ŵ�
        		}  
        		else  
          			tracks[i].setEnabled(false);    //����ôŵ�û�п��ܵ������ʽ���򽫸�Track����Ϊ������.
			}  
      		else  
        		tracks[i].setEnabled(false);
      	}  
   
    	if (!atLeastOneTrack)  
      		return "Couldn't set any of the tracks to a valid RTP format"; 
                                                                                                              
    	result = waitForState(processor, Controller.Realized);        // �ȴ�������ʵ�� 
		System.out.println(processor.getState());
    	if (result == false)  
      		return "Couldn't realize processor";  
                    		                                                                              
    	dataOutput = processor.getDataOutput();   // �Ӵ������õ����������Դ  
		System.out.println("dataOpuput:"+dataOutput);
    	return null;      //ȫ��ִ�����֮��᷵��null
  	}  
  	
  	//Ϊ��������ÿһ��ý��ŵ�����һ��RTP�Ự  
 	private String createTransmitter() 
	{  
    	PushBufferDataSource pbds = (PushBufferDataSource)dataOutput;     // ������Դת��Ϊ��Push�����ƣ�����Դ  
		System.out.println("pbds:"+pbds);
    	PushBufferStream pbss[] = pbds.getStreams();     // ������Դ�õ���Push������������  
  		System.out.println("pbss:"+pbss);
    	rtpMgrs = new RTPManager[pbss.length];        // Ϊÿ���ŵ�����һ��RTP�Ự������  
    	sendStream = new SendStream[pbss.length];
    	
    	for (int i = 0; i < pbss.length; i++) 
		{  
      		try 
			{  
        		rtpMgrs[i] = RTPManager.newInstance();
        		rtpMgrs[i].addSendStreamListener(Str_listener);
  				System.out.println("rtpMgrs[i]:" + rtpMgrs[i]);
        		int port = portBase + 2 * i;                         // ÿ����һ���ŵ����˿ںż�2  
        		InetAddress ipAddr = InetAddress.getByName(ipAddress);     // �õ�����Ŀ�ĵص�IP��ַ 
				InetAddress loAddr = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
  				System.out.println("ipAddr:" + ipAddr);
				System.out.println("localAddr:" + loAddr);
        		SessionAddress localAddr = new SessionAddress(loAddr,port+1);    // �õ������ĻỰ��ַ  
				System.out.println("-------localAddr:" + localAddr);            // ���ﴫ���ʹ�úͽ���Ŀ�Ķ���ͬ�Ķ˿ںţ�ʵ����Ҳ���Բ�ͬ��
        		SessionAddress destAddr = new SessionAddress( ipAddr,port+1);          // �õ�Ŀ�Ļ��������նˣ��ĻỰ��ַ  
				System.out.println("-------destAddr:" + destAddr);  
        		rtpMgrs[i].initialize( localAddr);                  // �������Ự��ַ����RTP������  
        		rtpMgrs[i].addTarget( destAddr);                    // ����Ŀ�ĻỰ��ַ  
  				System.out.println("rtpMgrs[i]="+ rtpMgrs[i]);
 
				sendStream[i] = rtpMgrs[i].createSendStream(dataOutput,i);           // ��������Դ��RTP������  
				displayArea.append("              |(Detail)����>Created RTP session: " + ipAddress + "/" + (port+1) + "\n");
      		}  
      		catch (Exception e) 
      		{  
       			return e.getMessage();  
      		}
      		
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");  
    	}
    
  		try {   //����������
			sendStream[0].start();
			sendStream[1].start();
		} 
  		catch (IOException e) 
  		{
			e.printStackTrace();
		}
  		
    	return null;  
  	}  
  
  	// ����JPEG��H.263�����׼��ֻ֧��һЩ�ض���ͼ���С������������б�Ҫ�ļ�飬��ȷ���������ȷ����  
  	Format checkForVideoSizes(Format original, Format supported) 
	{  
    	int width, height;  
    	Dimension size = ((VideoFormat)original).getSize();         // �õ���Ƶͼ��ĳߴ�  
    	Format jpegFmt = new Format(VideoFormat.JPEG_RTP);  
    	Format h263Fmt = new Format(VideoFormat.H263_RTP);  
		//System.out.println("checkForVideoSizes...............1,.................");  		

    	if (supported.matches(jpegFmt)) 
		{
			System.out.println("checkForVideoSizes...............supported.matches(jpegFmt)..................................");  
			// ��JPEG��ʽ����Ƶͼ��Ŀ�͸߱�����8���ص�������  
      		width = (size.width % 8 == 0 ? size.width : (int)(size.width / 8) * 8);  
      		height = (size.height % 8 == 0 ? size.height : (int)(size.height / 8) * 8);  
    	}  
    	else if (supported.matches(h263Fmt)) 
		{   
			System.out.println("checkForVideoSizes...............supported.matches(h263Fmt),.................");  
			// H.263��ʽ��֧�������ض���ͼ��ߴ�  
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
			// ��������ʽ���账��  
     		return supported;  
    	}  
  
		//System.out.println("checkForVideoSizes...............ok.................");  
    	return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,  
                            null,Format.NOT_SPECIFIED)).intersects(supported);                // ���ؾ�����������Ƶ��ʽ  
 	}  
  
  	// ֹͣ����  
  	public void stop() 
	{  
    	synchronized (this) 
		{  
      		if (processor != null) 
			{  
        		processor.stop();  
        		processor.close();                          // ֹͣ������  
       			processor = null;                           // �رմ�����  
        		for (int i = 0; i < rtpMgrs.length; i++) 
				{  	// ɾ������RTP������  
          			rtpMgrs[i].removeTargets( "Session ended.");  
          			rtpMgrs[i].dispose();  
       			}  
			}  
    	}  
  	}  
  
  	// ������������Ϊ�Դ�����״̬�ı�Ĵ������  
  	private Integer stateLock = new Integer(0);        // ״̬������  
  	private boolean failed = false;                    // �Ƿ�ʧ�ܵ�״̬��־  
  
  	// �õ�״̬��  
  	Integer getStateLock()
  	{  
    	return stateLock;  
  	}  
  
  	// ����ʧ�ܱ�־  
  	void setFailed() 
	{  
    	failed = true;  
  	}  
  
  	// �ȴ��������ﵽ��Ӧ��״̬  
  	private synchronized boolean waitForState(Processor p, int state) 
	{  
    	p.addControllerListener(CSta_listener);          // Ϊ����������״̬����  
    	failed = false;  
  
    	if (state == Processor.Configured) 
		{                                 // ���ô�����  
      		p.configure();  
      		System.out.println("processor call �����ã�configure()"+ Processor.Configured);
    	}  
    	else if (state == Processor.Realized) {                          
    		//ʵ�ִ�����  
      		p.realize();   
      		System.out.println("processor call ��ʵ�֣�realize()"+ Processor.Realized);
    	}  
  
    	// һֱ�ȴ���ֱ���ɹ��ﵽ����״̬����ʧ��  
    	while (p.getState() < state && !failed) 
		{  
      		synchronized (getStateLock()) 
			{  
				try{  
          			getStateLock().wait();
          			System.out.println("-------------�ȴ�--------------"); 
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
  
  	// �ڲ��ࣺ��������״̬������  
  	class CStateListener implements ControllerListener
	{  
  		//ʵ��ControllerListener�ӿ��еķ���
    	public void controllerUpdate(ControllerEvent ce) 
		{  
    		synchronized (getStateLock()) 
			{	System.out.println("----------�������¼�"+processor.getState()+"�������߳�------------");
      			getStateLock().notifyAll();  
    		} 
    		
      		// ����ڴ��������û�ʵ�ֹ����г��ִ��������ر�  
      		if (ce instanceof ControllerClosedEvent)   //�������ر�  
      		{	System.out.println("<<<<<<<<<<<<<<<<�������ر�<<<<<<<<<<<<<<<<<<<<<<");	
        		setFailed(); 
        		if (ce instanceof ControllerErrorEvent) {  // ����������       
    		 	 	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<����������<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");	
    			}
    			else if (ce instanceof DataLostErrorEvent ){             
    		    	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<���ݶ�ʧ����<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");          
    			}
      		}
      		// �������еĿ������¼���֪ͨ��waitForState�����еȴ����߳�  
      		else if (ce instanceof DurationUpdateEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<���ڸ����¼�<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof CachingControlEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<��������¼�<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof FormatChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<��ʽ�ı��¼�<<<<<<<<<<<<<<<<<<<<<<");	
      			if (ce instanceof SizeChangeEvent) 
    			{  
      				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<�ߴ�ı��¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");	
    			}
      		}
      		else if (ce instanceof MediaTimeSetEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<����ý��ʱ���¼�<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof RateChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<Rate�ı��¼�<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof StopTimeChangeEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<ֹͣʱ��ı��¼�<<<<<<<<<<<<<<<<<<<<<<");		
      		}
      		else if (ce instanceof TransitionEvent) 
			{  
      			System.out.println("<<<<<<<<<<<<<<<<�����¼�<<<<<<<<<<<<<<<<<<<<<<");		
      			
      			if (ce instanceof ConfigureCompleteEvent){
    		   		 System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<��������������¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");            
    			}
    			else if (ce instanceof PrefetchCompleteEvent){              
    		  	  	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<������Ԥȡ����¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof RealizeCompleteEvent){              
    		 		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<������ʵ������¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof StartEvent){              
    		   		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<��������ʼ�¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    			}
    			else if (ce instanceof StopEvent)
    			{ 
    				System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<������ֹͣ�¼�<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
 
    				processor.close();      // ֹͣ������
           			processor = null; 
           			dataOutput = null;      //�������Դ
           			
           			String piecePath = choiceNextPiece();    //����һ����Ƶ�ļ�Ƭ�ε�path�ַ���
           			
           			try{
           				MediaLocator Nloca = new MediaLocator(piecePath);
           				
           				sendNextpiece(Nloca);    //������һ����ƵƬ�� 
           			}
           			catch (Exception e) {System.out.println("77777777777777777777777777777777");
           				e.printStackTrace();
           			}    
 
    			}
      		}
		}
		
  	}
  	
  	//ѡ����һ����ƵƬ��
  	private String choiceNextPiece() {
  		String path = null;
  		try {
			path = locator.getURL().toString();     System.out.println(path);
			
		} catch (MalformedURLException e) {e.printStackTrace();}
  		
  		
  		String[] subPath = path.split("/");   // for(String s:subPath) System.out.println(s+"==");
		String[] info = subPath[subPath.length-1].split("_"); // for(String s:info)System.out.println("-------"+s);
																//System.out.println("info[2]::"+info[2]);
		String ch = String.valueOf(++count);         //System.out.println("ch-========"+ch);
		info[2] = info[2].replaceAll("(\\d+)(.avi)", ch+"$2");    //������ʽ�滻      //System.out.println("info[2]::"+info[2]);
		
		int order = (int)(Math.random() * 4) + 1;//ORT.get_askNextPieceLevel(); //��������ɻ�õ�ָ��ȡ��
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
  		
  	//������һ����ƵƬ��
  	private void sendNextpiece(MediaLocator locator) throws Exception{
			
			DataSource ds = null;  
			try {   
				ds = javax.media.Manager.createDataSource(locator);           // Ϊ�����MediaLocator��λ��ʵ����һ���ʵ�������Դ�� 
			}  
			catch (Exception e) 
			{
				for(int i=0;i<sendStream.length;i++){  //�ر���
					sendStream[0].close();
					sendStream[1].close();
				}
				
				Thread.currentThread().interrupt();
			}  
			try {  
				processor = javax.media.Manager.createProcessor(ds);          // ͨ������Դ������һ�������� 
			}  
			catch (NoProcessorException npe) {Thread.currentThread().interrupt();}  
			catch (IOException ioe){Thread.currentThread().interrupt();}  

			boolean result = waitForState(processor, Processor.Configured);         // �ȴ����������ú�  
			if (result == false)  
				throw new Exception("Couldn't configure processor");  
 		  
			TrackControl[] tracks = processor.getTrackControls();        // Ϊý�����е�ÿһ���ŵ��õ�һ��������  
  
			if (tracks == null || tracks.length < 1)              // ȷ��������һ�����õĴŵ�  
				throw new Exception("Couldn't find tracks in processor");  
 		  
			ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);  
			System.out.println("cd:"+cd);
			processor.setContentDescriptor(cd);                // �����������������ΪRAW_RTP  
			                                               // �Ӷ��޶�ÿ���ŵ�֧�ֵĸ�ʽ��Ϊ�Ϸ���RTP��ʽ������Ӱ������ Track.getSupportedFormats()  
			Format supported[];  
			Format chosen = null;  
			boolean atLeastOneTrack = false;  
			for (int i = 0; i < tracks.length; i++)  // ��ÿһ���ŵ���ѡ��һ��RTP֧�ֵĴ����ʽ  
			{         
				Format format = tracks[i].getFormat();  //ȡ�øôŵ������õĸ�ʽ
				if (tracks[i].isEnabled())              // ����ôŵ�����,isEnabled()���شŵ�״̬
				{                       
					supported = tracks[i].getSupportedFormats();  //�г��ôŵ����п���֧�ֵ������ʽ
					System.out.println("supported:"+supported);
					if (supported.length > 0) 
					{  
			  			if (supported[0] instanceof VideoFormat) {  
							chosen = checkForVideoSizes(format,supported[0]);       // �����Ƶ��ʽ�ĳߴ磬��ȷ���������� 
							System.out.println(chosen.toString()); 
						}  
			 			else {	
			 				 chosen = supported[0];       // ǰ���Ѿ������������������ΪRIP������֧�ֵĸ�ʽ��������RTP��Ϲ���  
						}
			  			tracks[i].setFormat(chosen);      //���ôŵ��������������ʽ                    
			  			atLeastOneTrack = true;            //����һ���ŵ�
					}  
					else  
			  			tracks[i].setEnabled(false);    //����ôŵ�û�п��ܵ������ʽ���򽫸�Track����Ϊ������.
				}  
				else  
					tracks[i].setEnabled(false);
			}  
 		   
			if (!atLeastOneTrack)  
				throw new Exception("Couldn't set any of the tracks to a valid RTP format"); 
			                                                                                                      
			result = waitForState(processor, Controller.Realized);        // �ȴ�������ʵ�� 
			System.out.println(processor.getState());
			if (result == false)  
				throw new Exception("Couldn't realize processor");  
			            		                                                                              
			dataOutput = processor.getDataOutput();   // �Ӵ������õ����������Դ  
			
			for (int i = 0; i < 2; i++) 
			{  
				try 
				{ 	
					sendStream[i] = rtpMgrs[i].createSendStream(dataOutput,i);           // ��������Դ��RTP������  
					sendStream[i].start();// ��ʼRTP����������  
				}  
				catch (Exception e) 
				{  
					e.printStackTrace();  
				}
			}  
			processor.start();
		}
  	
  	//��������
  	class StreamListener implements SendStreamListener
	{
		//ʵ��SendStreamListener�ӿ��еķ���
		public void update(SendStreamEvent SE) {
			// 
			System.out.println("<<<<<<<<<<<<<<<<���¼�<<<<<<<<<<<<<<<<<<<<<<");
			if (SE instanceof ActiveSendStreamEvent) 
			{
				System.out.println("<<<<<<<<<<<<<<<<���ݴﵽ���¼�<<<<<<<<<<<<<<<<<<<<<<");
			}
			else if (SE instanceof InactiveSendStreamEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<����ֹͣ�����Sendstraem�¼�<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof LocalPayloadChangeEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<���ظı��¼�<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof NewSendStreamEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<�·������¼�<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
			}
			else if (SE instanceof StreamClosedEvent)
			{
				System.out.println("<<<<<<<<<<<<<<<<���ر��¼�<<<<<<<<<<<<<<<<<<<<<<----------------------------------------------------------------");
				
			}
		}  
	 
	}
}  


