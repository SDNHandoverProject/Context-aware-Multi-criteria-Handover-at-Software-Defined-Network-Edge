package javamedia;
import java.awt.*;
import java.io.*;             
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
//import javax.media.*; 
//import javax.media.format.*;


class VideoServer extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7180616711074395954L;
	private ServerSocketChannel serverSocketChannel;   //Java NIO中的 ServerSocketChannel 是一个可以监听新进来的TCP连接的通道, 就像标准IO中的ServerSocket一样。ServerSocketChannel类在 java.nio.channels包中。
 	private Selector selector;                         //Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件.使用Selector能够处理多个通道
  	private JTextArea displayArea;                     //聊天文本区
  	private Socket socket = null;

  	private String req_Style = "";//= new StringBuilder();

//	private Vector sockets = new Vector();             //Vector类在java中可以实现自动增长的对象数组
//  	private int counter = 0;

  	String ipString ;						
  	int port ;   //这个port是server端自动分配的
  	int localport ;

  	public VideoServer()
 	{
		setTitle("Video_Server");
		setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/ServerIcon.png"));
    	setSize(610,400);
		displayArea = new JTextArea(20,54);
		displayArea.setEditable(false);
		JPanel showPanel = new JPanel();
		showPanel.add(new JScrollPane(displayArea));//获取内容面板，因为JFrame不能直接添加组件，需要用getContentPane()函数获取内容面板，再在内容面板上进行添加组件
		add(showPanel);
		setResizable(false);
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//获取系统屏幕尺寸
		Dimension frameSize = this.getSize();
		if(frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if(frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		this.setLocation((screenSize.width - frameSize.width) / 2,(screenSize.height - frameSize.height) / 2);
		
		setVisible(true);
   
		addWindowListener(new WindowAdapter()     //设置窗口监听器
		{    
        		public void windowClosing(WindowEvent windowEvent)  //窗口关闭事件
      			{
        			try
        			{
            			serverSocketChannel.close();         //关闭通道
           				selector.close();                    //关闭该Selector，且使注册到该Selector上的所有SelectionKey实例无效。通道本身并不会关闭
        			}
          			catch(IOException ioException)
        			{
            				ioException.printStackTrace();
         			}
          			finally
          			{
            				System.exit(0);                      //程序退出，finally代码块必须执行
          			}
        		}
     		 });
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				displayArea.append("Video server initializationis completed , starting.........\n");
			}
		});	
  	}

 	public void runServer()
  	{ 
		try
		{     
     		//开启服务器套接字通道
      		serverSocketChannel = ServerSocketChannel.open();
      		try{serverSocketChannel.socket().bind(new InetSocketAddress(60000));}catch(IOException e){System.out.println("端口已经被占用");}
      		serverSocketChannel.configureBlocking(false);           //设置阻塞模式为非阻塞模式
			
      		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					displayArea.append("Video server has been started , waiting for the connection request..........\n\n");
				}
			});

			//寻找正在进来的请求
    		selector = SelectorProvider.provider().openSelector();   //选择器通过专门的工厂SelectorProvider来创建Selector的实现，SelectorProvider屏蔽了不同操作系统及版本创建实现的差异性
    		serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT,null);//将此channel注册到选择器，注册为接收新连接  Accept
    		//进行进来的请求
			while(selector.select()>0)
			{
    			//一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，
			    //访问“已选择键集（selected key set）”中的就绪通道，这方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道
			  
      			Set readyKeys = selector.selectedKeys();
      			Iterator iterator = readyKeys.iterator();           //迭代器
	
      			//为每个准备好的channel进行请求
      			while(iterator.hasNext())
      			{
        			SelectionKey key = (SelectionKey)iterator.next();
        			iterator.remove();//每次迭代末尾的Iterator.remove()调用。Selector不会自己从已选择键集中移除SelectionKey实例。
        			//必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中

        			if(key.isAcceptable())                      //如果管道是注册accept(接受新进入的链接)的
        			{                           
                 		SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();  //通过 ServerSocketChannel.accept() 方法监听新进来的连接
                 		socket = socketChannel.socket();
          				if(socketChannel != null)
          				{ 
      						ipString = socket.getInetAddress().getHostAddress();				
      				    	port = socket.getPort();   //这个port是server端自动分配的
      				    	localport = socket.getLocalPort();

      				    	System.out.println(ipString+"---port:"+port + "  /localport:" + localport);
      						
      				    	InputStream ins = socket.getInputStream();
      				    	byte[] buf = new byte[1024];
      				    	int len = ins.read(buf);
      				    	req_Style = new String(buf,0,len);
      				    	
      				    	System.out.println("req_Style==="+req_Style);
      					
      						//判断是什么设备发来的请求
          					if(req_Style.toString().equals("X86_req"))
          					{    
          						SwingUtilities.invokeLater(new Runnable()
    							{
						      		public void run()
						     		{
						      			displayArea.append("\nPC Client-side:"+ipString+"  Port:"+port+"......connected\n");
						      		}
    							});
          					
    							RTPTransmitThread rtpTransmit = new RTPTransmitThread(socket,ipString,port,localport,displayArea);
    							new Thread(rtpTransmit).start();  
          					}
          					else if(req_Style.toString().equals("ARM_req"))
          					{  	
          						if((port % 2) != 0) 
          							port += 1;  //jrtplib不支持奇数端口，初始化时要保证是偶数端口，以此会话中的rtcp使用紧随其后的奇数端口
          						
          						SwingUtilities.invokeLater(new Runnable()
    							{
						      		public void run()
						     		{
						      			displayArea.append("\nAndroid Client-side:"+ipString+"  Port:"+port+"......connected\n");
						      		}
    							});
          					
          						RTPTransmitAndroid rtpAndroid = new RTPTransmitAndroid(socket,ipString,port,displayArea);
          						new Thread(rtpAndroid).start();
          					}
						}
          				req_Style = "";
					}	
				}
      			//socket.close();  System.out.println("-----socket.close();-----");
			}
		}
		catch(Exception Exception)
		{
  			Exception.printStackTrace();
		}
	}
 	
	public static void main(String args[])
	{
		VideoServer frame = new VideoServer();//创建服务器对象实例
		frame.runServer();                
 	}

}




