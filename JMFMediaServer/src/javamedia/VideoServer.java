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
	private ServerSocketChannel serverSocketChannel;   //Java NIO�е� ServerSocketChannel ��һ�����Լ����½�����TCP���ӵ�ͨ��, �����׼IO�е�ServerSocketһ����ServerSocketChannel���� java.nio.channels���С�
 	private Selector selector;                         //Selector��ѡ��������Java NIO���ܹ����һ�����NIOͨ�������ܹ�֪��ͨ���Ƿ�Ϊ�����д�¼�����׼�������.ʹ��Selector�ܹ�������ͨ��
  	private JTextArea displayArea;                     //�����ı���
  	private Socket socket = null;

  	private String req_Style = "";//= new StringBuilder();

//	private Vector sockets = new Vector();             //Vector����java�п���ʵ���Զ������Ķ�������
//  	private int counter = 0;

  	String ipString ;						
  	int port ;   //���port��server���Զ������
  	int localport ;

  	public VideoServer()
 	{
		setTitle("Video_Server");
		setIconImage(Toolkit.getDefaultToolkit().getImage("Resources/ServerIcon.png"));
    	setSize(610,400);
		displayArea = new JTextArea(20,54);
		displayArea.setEditable(false);
		JPanel showPanel = new JPanel();
		showPanel.add(new JScrollPane(displayArea));//��ȡ������壬��ΪJFrame����ֱ������������Ҫ��getContentPane()������ȡ������壬������������Ͻ���������
		add(showPanel);
		setResizable(false);
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//��ȡϵͳ��Ļ�ߴ�
		Dimension frameSize = this.getSize();
		if(frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if(frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		this.setLocation((screenSize.width - frameSize.width) / 2,(screenSize.height - frameSize.height) / 2);
		
		setVisible(true);
   
		addWindowListener(new WindowAdapter()     //���ô��ڼ�����
		{    
        		public void windowClosing(WindowEvent windowEvent)  //���ڹر��¼�
      			{
        			try
        			{
            			serverSocketChannel.close();         //�ر�ͨ��
           				selector.close();                    //�رո�Selector����ʹע�ᵽ��Selector�ϵ�����SelectionKeyʵ����Ч��ͨ����������ر�
        			}
          			catch(IOException ioException)
        			{
            				ioException.printStackTrace();
         			}
          			finally
          			{
            				System.exit(0);                      //�����˳���finally��������ִ��
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
     		//�����������׽���ͨ��
      		serverSocketChannel = ServerSocketChannel.open();
      		try{serverSocketChannel.socket().bind(new InetSocketAddress(60000));}catch(IOException e){System.out.println("�˿��Ѿ���ռ��");}
      		serverSocketChannel.configureBlocking(false);           //��������ģʽΪ������ģʽ
			
      		SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					displayArea.append("Video server has been started , waiting for the connection request..........\n\n");
				}
			});

			//Ѱ�����ڽ���������
    		selector = SelectorProvider.provider().openSelector();   //ѡ����ͨ��ר�ŵĹ���SelectorProvider������Selector��ʵ�֣�SelectorProvider�����˲�ͬ����ϵͳ���汾����ʵ�ֵĲ�����
    		serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT,null);//����channelע�ᵽѡ������ע��Ϊ����������  Accept
    		//���н���������
			while(selector.select()>0)
			{
    			//һ��������select()���������ҷ���ֵ������һ��������ͨ�������ˣ�Ȼ�����ͨ������selector��selectedKeys()������
			    //���ʡ���ѡ�������selected key set�����еľ���ͨ�����ⷽ��������������Ȥ���¼��������ӡ����ܡ�����д���Ѿ�׼����������Щͨ��
			  
      			Set readyKeys = selector.selectedKeys();
      			Iterator iterator = readyKeys.iterator();           //������
	
      			//Ϊÿ��׼���õ�channel��������
      			while(iterator.hasNext())
      			{
        			SelectionKey key = (SelectionKey)iterator.next();
        			iterator.remove();//ÿ�ε���ĩβ��Iterator.remove()���á�Selector�����Լ�����ѡ��������Ƴ�SelectionKeyʵ����
        			//�����ڴ�����ͨ��ʱ�Լ��Ƴ����´θ�ͨ����ɾ���ʱ��Selector���ٴν��������ѡ�������

        			if(key.isAcceptable())                      //����ܵ���ע��accept(�����½��������)��
        			{                           
                 		SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();  //ͨ�� ServerSocketChannel.accept() ���������½���������
                 		socket = socketChannel.socket();
          				if(socketChannel != null)
          				{ 
      						ipString = socket.getInetAddress().getHostAddress();				
      				    	port = socket.getPort();   //���port��server���Զ������
      				    	localport = socket.getLocalPort();

      				    	System.out.println(ipString+"---port:"+port + "  /localport:" + localport);
      						
      				    	InputStream ins = socket.getInputStream();
      				    	byte[] buf = new byte[1024];
      				    	int len = ins.read(buf);
      				    	req_Style = new String(buf,0,len);
      				    	
      				    	System.out.println("req_Style==="+req_Style);
      					
      						//�ж���ʲô�豸����������
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
          							port += 1;  //jrtplib��֧�������˿ڣ���ʼ��ʱҪ��֤��ż���˿ڣ��Դ˻Ự�е�rtcpʹ�ý������������˿�
          						
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
		VideoServer frame = new VideoServer();//��������������ʵ��
		frame.runServer();                
 	}

}




