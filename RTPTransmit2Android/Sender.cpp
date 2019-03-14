/*
   Here's a small IPv4 example: it asks for a portbase and a destination and 
   starts sending packets to that destination.
*/
#pragma warning(disable : 4786)

#include "rtpsession.h"
#include "rtppacket.h"
#include "rtpudpv4transmitter.h"
#include "rtpipv4address.h"
#include "rtpsessionparams.h"
#include "rtperrors.h"
#include "h264.h"
#include "javamedia_RTPTransmitAndroid.h"
#ifndef WIN32
	#include <netinet/in.h>
	#include <arpa/inet.h>
#else
	#include <winsock2.h>
#endif // WIN32
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <time.h>

#pragma comment(lib,"jrtplib.lib")
#pragma comment(lib,"jthread.lib")
#pragma comment(lib,"WS2_32.lib")

//#define DEBUG

#define H264  96 
#define TimestampIn 5
#define FileTimeLen 10    //视频文件的时长，单位是秒

#define BUFFER_SIZE  1024 * 1024 * 4    //定义一个4M的空间作为缓存
#define piece_During 10     //视频时长
#define piece_Number 111     //视频片段个数
//#define BUFFER_SIZE  1024 * 1024     //定义一个1M的空间作为缓存

RTPSession rtpsess;    //rtp会话
RTPSessionParams sessionparams; //
RTPUDPv4TransmissionParams transparams;   //可以把RTPUDPv4TransmissionParams类看作是网络接口类，它主要有以下功能：
										   //设置/获取绑定的IP地址
										   //设置/获取绑定的端口号
										   //设置/获取广播TTL
										   //获取本地IP地址串
										   //设置/获取RTP/RTCP发送/接收缓冲区的大小

char * filepath;
FILE *fp; 


float Tatal_File_Count = 0; //文件字数统计。
unsigned int Every_File_Count = 0; //文件字数统计。
SYSTEMTIME sys;
unsigned int Switch_count = 0;  //码率切次数的统计


int FindStartCode2 (unsigned char *Buf)  //查找开始码
{
	if(Buf[0]!=0 || Buf[1]!=0 || Buf[2] !=1) return 0; //判断是否为0x000001,如果是返回1
	else return 1;
}

int FindStartCode3 (unsigned char *Buf)
{
	if(Buf[0]!=0 || Buf[1]!=0 || Buf[2] !=0 || Buf[3] !=1) return 0;//判断是否为0x00000001,如果是返回1
	else return 1;
}

//为NALU_t结构体分配内存空间
NALU_t *AllocNALU(JNIEnv * env,int buffersize)
{
  NALU_t *n;

  if ((n = (NALU_t*)calloc (1, sizeof (NALU_t))) == NULL)
  {
	  (*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not create NALU_t !!!!!!!");
	  exit(0);
  }

  n->max_size=buffersize;

  if ((n->buf = (char*)calloc (buffersize, sizeof (char))) == NULL)
  {
	free (n);
    (*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not create NALU_t's buf !!!!!!!");
	exit(0);
  }

  return n;
}


//释放
void FreeNALU(NALU_t *n)
{
  if (n)
  {
    if (n->buf)
    {
      free(n->buf);
      n->buf=NULL;
    }
    free (n);
  }
}


void Showinfo2Java(JNIEnv * env, jobject jobj, char* message)
{
	//-------------------------------------------------------------------------------------//
	//获取类对象
	jclass cls = (*env).GetObjectClass(jobj);
	//获取方法ID
	jmethodID showinfoID = (*env).GetMethodID(cls, "ShowInfo","(Ljava/lang/String;)V" );   // 调用java层的showinfoID方法显示信息
	//调用方法
	jstring param = (*env).NewStringUTF(message);
	(*env).CallVoidMethod(jobj,showinfoID,param);

	//-------------------------------------------------------------------------------------//
}

//计算视频码率
void complete_BitRate(JNIEnv * env,jobject jobj,int file_size)
{
	int bitrate = file_size*8/(piece_During*1000);

		char str2[30];
		sprintf(str2,"--------BitRate=%d kbps \n",bitrate);
		Showinfo2Java( env,jobj,str2);

}

//这个函数输入为一个NAL结构体，主要功能为得到一个完整的NALU并保存在NALU_t的buf中，获取他的长度，填充F,IDC,TYPE位。
//并且返回两个开始字符之间间隔的字节数，即包含有前缀的NALU的长度
int GetAnnexbNALU (JNIEnv * env,jobject jobj,NALU_t *nalu)
{
  int pos = 0;
  int StartCodeFound, rewind;
  unsigned char * Buf;
  unsigned int info2;
  unsigned int info3;

  if ((Buf = (unsigned char*)malloc(nalu->max_size * sizeof(char))) == NULL)   //分配nalu->max_size个长度为sizeof(char)的连续空间，并将每一个字节都初始化为0
	  (*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Could not allocate Buf memory !!!!!!!");
	memset(Buf,0,nalu->max_size);

  nalu->startcodeprefix_len=3; //初始化码流序列的开始字符为3个字节
  
   if (3 != fread (Buf, 1, 3, fp))//从码流中读3个字节
	   {
		free(Buf);
		return 0;
	   }
   info2 = FindStartCode2 (Buf);//判断是否为0x00 00 01 
   if(info2 != 1) 
   {
	//如果不是，再读一个字节
    if(1 != fread(Buf+3, 1, 1, fp))//读一个字节
		{
		 free(Buf);
		 return 0;
		}
    info3 = FindStartCode3 (Buf);//判断是否为0x00 00 00 01
    if (info3 != 1)//如果不是，返回-1
		{ 
		 free(Buf);
		 return -1;
		}
    else 
		{
		//如果是0x00000001,得到开始前缀为4个字节
		 pos = 4;
		 nalu->startcodeprefix_len = 4;
		}
   }
   else
   {
   //如果是0x000001,得到开始前缀为3个字节
	nalu->startcodeprefix_len = 3;
	pos = 3;
   }

   //查找下一个开始字符的标志位
   StartCodeFound = 0;
   info2 = 0;
   info3 = 0;
  
	

	while (!StartCodeFound)
	{
		if (feof (fp))//判断是否到了文件尾，如果到了文件末尾
		{
			nalu->len = (pos-1)-nalu->startcodeprefix_len;
			memcpy (nalu->buf, &Buf[nalu->startcodeprefix_len], nalu->len);     
			nalu->forbidden_bit = nalu->buf[0] & 0x80; //1 bit
			nalu->nal_reference_idc = nalu->buf[0] & 0x60; // 2 bit
			nalu->nal_unit_type = (nalu->buf[0]) & 0x1f;// 5 bit
			free(Buf);
			return pos-1;
		}
		Buf[pos++] = fgetc (fp);//读一个字节到BUF中
		info3 = FindStartCode3(&Buf[pos-4]);//判断是否为0x00000001
		if(info3 != 1)
			info2 = FindStartCode2(&Buf[pos-3]);//判断是否为0x000001
		StartCodeFound = (info2 == 1 || info3 == 1);
	}
 
  // Here, we have found another start code (and read length of startcode bytes more than we should
  // have.  Hence, go back in the file
  rewind = (info3 == 1)? -4 : -3;     //如果是0x00000001则倒回4个字节，否则倒回3个字节

  if (0 != fseek (fp, rewind, SEEK_CUR))//把文件指针指向前一个NALU的末尾，SEEK_CUR 从当前位置
  {
    free(Buf);
	(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Cannot fseek in the bit stream file !!!!!!!");
  }

  // Here the Start code, the complete NALU, and the next start code is in the Buf.  
  // The size of Buf is pos, pos+rewind are the number of bytes excluding the next
  // start code, and (pos+rewind)-startcodeprefix_len is the size of the NALU excluding the start code

  nalu->len = (pos+rewind)-nalu->startcodeprefix_len;  //除去nalu开始码的实际数据长度
  memcpy (nalu->buf, &Buf[nalu->startcodeprefix_len], nalu->len);//拷贝一个完整NALU，不拷贝起始前缀0x000001或0x00000001
  nalu->forbidden_bit = nalu->buf[0] & 0x80; //1 bit
  nalu->nal_reference_idc = nalu->buf[0] & 0x60; // 2 bit
  nalu->nal_unit_type = (nalu->buf[0]) & 0x1f;// 5 bit
  free(Buf);
 
  return (pos+rewind);//返回两个开始字符之间间隔的字节数，即包含有前缀的NALU的长度
}

//获取客户端发来的视频质量级别请求
int getClientOrder(JNIEnv * env, jobject jobj)
{
	//srand((unsigned)time(NULL));        //种子
	//	return (rand()%4)+1;

	//获取类对象
	jclass cls = (*env).GetObjectClass(jobj);
	//获取方法ID
	jmethodID getNextPieceLevelID = (*env).GetMethodID(cls, "getNextPieceLevel","()I");   // 调用java层的getNextPieceLevel方法显示信息
	//调用方法
	//jstring param = (*env).NewStringUTF(message);
	int order = (*env).CallIntMethod(jobj,getNextPieceLevelID);

		char str2[30];
		sprintf(str2,"-----request_order=%d\n",order);
		Showinfo2Java( env,jobj,str2);

	return order;
}

//选择下一个视频片段的绝对路径
char* choiceNextPiece(JNIEnv * env, jobject jobj,char* filepath)
{
	char string[200] = {"0"};
	char* path = NULL;
	strcpy(string,filepath);//绝对路劲复制给string[]

	char delims[] = "\\";
	char *result = NULL;     
	result = strtok(string,delims);
	while(result != NULL){
		path = result;   //子串放入path中。得到最后一个字串（视频频段的名称）
		result = strtok(NULL,delims);
	}

	//处理path(名称)
	char point[] = ".";
	char Aname[100] = {"0"};
	strcpy(Aname,path);
	char* Pro_name = strtok(Aname,point); //得到前缀名,类似4S_IP_1这样的不带后缀的字段

	//分割前缀名“4S_IP_1”这样的数据
	char delow_line[] = "_";
	char P_name[100] = {"0"};
	strcpy(P_name,Pro_name);
	char* parts[3]; //三个字符串指针数组
	parts[0] = strtok(P_name,delow_line);
	for(int i=1;i<3;i++){
		parts[i] = strtok(NULL,delow_line);
	}

	//吧切换前的码率等级存入，方便后面比较以确定是否切换码率等级
	char bitrate_Level_sub[5] = {"0"};
	strcpy(bitrate_Level_sub,parts[1]); 
	//Showinfo2Java( env,jobj,bitrate_Level_sub);

	int order = atoi(parts[2]) + 1;  //下个视频片段的序号加1；
	sprintf(parts[2],"%d",order);

	int level = getClientOrder(env,jobj);  //该条语句由获得的指令取代
  	switch(level)
  	{ 
  		case 4: parts[1] = "IP";
  				break;
  		case 3: parts[1] = "CR";
  				break;
  		case 2: parts[1] = "TC";
  				break;
  		case 1: parts[1] = "TS";
  				break;
  	}
	

	if(strcmp(bitrate_Level_sub,parts[1]) != 0 )  //比较两个字符串
		Switch_count++;

	//组合新字符串
	strcpy(string,filepath);//绝对路劲复制给string[]

	char* newPro = strtok(string,"_"); 
	newPro = strcat(strcat(strcat(strcat(strcat(newPro,"_"),parts[1]),"_"),parts[2]),".264"); 

	char* new_Pro = new char[200];
	memset(new_Pro,0,200);
	strcpy(new_Pro,newPro);    

	return new_Pro;
}

JNIEXPORT jint JNICALL Java_javamedia_RTPTransmitAndroid_init_1RTP(JNIEnv * env, jobject jobj, jstring ServerIP, jint Port)  //而jint是以JNI为中介使JAVA的int类型与本地 的int沟通的一种类型，我们可以视而不见，就当做int使用。
{
	#ifdef WIN32
		WSADATA dat;
		WSAStartup(MAKEWORD(2,2),&dat);
	#endif // WIN32

	//std::cout << "ERROR: " << "init_1RTP" << std::endl;
	//Showinfo2Java( env,jobj,"              |(Detail)---->Created RTP session: ");

	int status; //记录执行结果状态

	//创建会话
	sessionparams.SetOwnTimestampUnit(1.0 / 30.0); //30 video frames per second
	//sessionparams.SetUsePollThread(1);
	sessionparams.SetAcceptOwnPackets(true);

	transparams.SetPortbase( (uint16_t)Port );     //添加本地端口 ,不支持奇数端口
	status = rtpsess.Create( sessionparams, &transparams );
	if( status < 0 )
	{
		std::cout << "ERROR: " << RTPGetErrorString(status) << std::endl;
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Create rtp seeion error !!!!!!!!!");
		return status;
	}	


	char * IPstr;
	IPstr=(char*)(*env).GetStringUTFChars(ServerIP,NULL);

	uint32_t destip;   //目的ip
	destip = inet_addr(IPstr);   /*这个字符串用Internet的“.”间隔格式表示一个数字的Internet地址。返回值可用作Internet地址。所有Internet地址以网络字节顺序返回(字节从左到右排列)。
								     *inet_addr()的功能是将一个点分十进制的IP转换成一个长整数型数
									 */

	destip = ntohl( destip );  // 将一个无符号长整形数从网络字节顺序转换为主机字节顺序
  
	//将目标地址添加到发送地址列表中
	RTPIPv4Address addr( destip, (uint16_t)Port );   //此处，本地port和远端port使用一样的数值
	status = rtpsess.AddDestination( addr );
	if( status < 0 )
	{
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not add Destination to rtp seesion !!!!!!!!");
		return status;
	}

	//设置RTP包的默认参数
	rtpsess.SetDefaultPayloadType( H264 );
	rtpsess.SetDefaultMark( false );
	rtpsess.SetDefaultTimestampIncrement( TimestampIn );
	rtpsess.SetSessionBandwidth(1000000);

	//输出一些初始化信息
	char buf1[100] ;
	sprintf(buf1,"              |(Detail)--->Created RTP session: dest_IP(%s) - dest_Port(%d)...\n",IPstr,(uint16_t)Port);
	Showinfo2Java( env,jobj,buf1);

	char buf2[100] ;
	sprintf(buf2,"              |(Detail)--->Created RTP session: PayloadType(%s) - TimestampIn(%d)...\n","H264",TimestampIn);
	Showinfo2Java( env,jobj,buf2);

	(*env).ReleaseStringUTFChars(ServerIP, IPstr);

#ifdef WIN32
	WSACleanup();
#endif // WIN32

	return 1;

}

JNIEXPORT void JNICALL Java_javamedia_RTPTransmitAndroid_Sender(JNIEnv * env, jobject jobj, jstring file)
{
	#ifdef WIN32
		WSADATA dat;
		WSAStartup(MAKEWORD(2,2),&dat);
	#endif // WIN32

	filepath = (char*)(*env).GetStringUTFChars(file,NULL);    //视频文件的绝对地址

	NALU_t *n;

	NALU_HEADER		*nalu_hdr;  //结构体，NALU单元的头部
	FU_INDICATOR	*fu_ind;
	FU_HEADER		*fu_hdr;       
	char sendbuf[1500];  
	char* nalu_payload;
	unsigned long maxpacket;  //最大可以的包大小
	int status = 1;

	//获取所允许的最大包的大小
	maxpacket = sessionparams.GetMaximumPacketSize(); // 默认1400字节
	maxpacket -= 20;  //每一个RTP数据报都由头部（Header）和负载（Payload）两个部分组成，其中头部前 12 个字节的含义是固定的，而负载则可以是音频或者视频数据。
					//减去20是因为最后一个fu-a分片可能加上2个字节的nalu_indicate和nalu_header,再加上12字节的rtp头，可能会大于最大包大小

	n = AllocNALU(env,8000000);//为结构体nalu_t及其成员buf分配空间。返回值为指向nalu_t存储空间的指针

	fp = fopen( filepath, "rb" );    //打开文件
	if( NULL == fp )
	{
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not open file !!!!!!!!");
		return ;
	}

	Tatal_File_Count = 0; 

	while(fp != NULL)
	{

		//从绝对路径中取得文件的名称
		char string[200] = {"0"};
		char* VideoName = NULL;   //路径中文件的名称
		strcpy(string,filepath); 

		char delims[] = "\\";
		char *result = NULL;     
		result = strtok(string,delims);
		while(result != NULL){
			VideoName = result;   //子串放入VideoName中。得到最后一个字串（视频频段的名称）
			result = strtok(NULL,delims);
		}

		//打印输出信息
		char buf3[100] ;
		sprintf(buf3,"              |(Detail)-+->Sending [ %s ] .... ",VideoName);
		Showinfo2Java( env,jobj,buf3);

		int nalu_c = 0;
		
		//开始传输
		while(!feof(fp))
		{

			int size = GetAnnexbNALU(env,jobj,n); //每执行一次，将nalu数据装入n ,文件的指针指向本次找到的NALU的末尾，下一个位置即为下个NALU的起始码0x000001, 

			nalu_c = nalu_c+size;
			//nalu_count++;  //每个文件的nalu数量
		
		
			if(size<4){                   //并且返回两个开始字符之间间隔的字节数，即包含有前缀的NALU的长度
				(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Get nalu error !!!!!!!!");
				return;
			}

			//当一个NALU小于MAX_RTP_PKT_LENGTH字节的时候，采用一个单RTP包发送
			if(n->len <= maxpacket)
			{
				//Showinfo2Java( env,jobj,"-send single rtp packet!!\n");
			
				//设置NALU HEADER,并将这个HEADER填入sendbuf[12]
				nalu_hdr = (NALU_HEADER*)&sendbuf[0]; //将sendbuf[0]的地址赋给nalu_hdr，之后对nalu_hdr的写入就将写入sendbuf中
				nalu_hdr->F = n->forbidden_bit;
				nalu_hdr->NRI = n->nal_reference_idc>>5;//有效数据在n->nal_reference_idc的第6，7位，需要右移5位才能将其值赋给nalu_hdr->NRI。????????????????????????????????????????????
				nalu_hdr->TYPE = n->nal_unit_type;

				nalu_payload = &sendbuf[1];//同理将sendbuf[1]赋给nalu_payload
				memcpy(nalu_payload,n->buf+1,n->len-1);//去掉nalu头的nalu剩余内容写入sendbuf[13]开始的字符串。
				//ts_current=ts_current+timestamp_increse;

				status = rtpsess.SendPacket((void *)sendbuf,n->len,H264,true,TimestampIn);  //发送数据包 , mark标记

				Tatal_File_Count = Tatal_File_Count+n->len;    //全局字节数统计
				Every_File_Count = Every_File_Count+n->len;    //每个视频片段字节统计
					//nalu_c = nalu_c+n->len+4;
					//char str2[20];
					//sprintf(str2,"%d\n",n->len);
					//Showinfo2Java( env,jobj,str2);

				if (status < 0){
					(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Send rtp error !!!!!!!!");
					exit(-1);
				}
			}
			else if(n->len>maxpacket)
			{	
				//得到该nalu需要用多少长度为maxpacket字节的RTP包来发送
				int k=0,l=0;
				k=n->len / maxpacket;//需要k个maxpacket字节的RTP包
				l=n->len % maxpacket;//最后一个RTP包的需要装载的字节数
				int t=0;//用于指示当前发送的是第几个分片RTP包

				while(t<=k)
				{
					if(!t)//发送一个需要分片的NALU的第一个分片（此时t等于0），置FU HEADER的S位
					{
						//Showinfo2Java( env,jobj,"-send frist fu-a packet!!\n");					
						memset(sendbuf,0,1500);
						//设置FU INDICATOR,并将这个HEADER填入sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //将sendbuf[12]的地址赋给fu_ind，之后对fu_ind的写入就将写入sendbuf中；
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//设置FU HEADER,并将这个HEADER填入sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						fu_hdr->E=0;
						fu_hdr->R=0;
						fu_hdr->S=1;
						fu_hdr->TYPE=n->nal_unit_type;


						nalu_payload=&sendbuf[2];//同理将sendbuf[14]赋给nalu_payload，地址赋过去，就等于直接操作了
						memcpy(nalu_payload,n->buf+1,maxpacket);//去掉NALU头

						status = rtpsess.SendPacket((void *)sendbuf,maxpacket+2,H264,false,TimestampIn);
					
						Tatal_File_Count = Tatal_File_Count+maxpacket+2;
						Every_File_Count = Every_File_Count+maxpacket+2;		
							//nalu_c = nalu_c+maxpacket+1;
							//char str3[20];
							//sprintf(str3,"%d\n",maxpacket+2);
							//Showinfo2Java( env,jobj,str3);

						if (status < 0){
							(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Send rtp error !!!!!!!!");
							exit(-1);
						}
						t++;
					}
					//发送一个需要分片的NALU的非第一个分片，清零FU HEADER的S位，如果该分片是该NALU的最后一个分片，置FU HEADER的E位
					else if(k==t)//发送的是最后一个分片，注意最后一个分片的长度可能超过MAX_RTP_PKT_LENGTH字节（当l>1386时）(此处减去20做处理)。
					{
						//Showinfo2Java( env,jobj,"-send last fu-a packet!!\n");

						memset(sendbuf,0,1500);

						//设置FU INDICATOR,并将这个HEADER填入sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //将sendbuf[12]的地址赋给fu_ind，之后对fu_ind的写入就将写入sendbuf中；
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//设置FU HEADER,并将这个HEADER填入sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						fu_hdr->R=0;
						fu_hdr->S=0;
						fu_hdr->TYPE=n->nal_unit_type;
						fu_hdr->E=1;
						nalu_payload=&sendbuf[2];//同理将sendbuf[14]赋给nalu_payload
						memcpy(nalu_payload, n->buf+1+t*maxpacket ,l-1);//将nalu最后剩余的l-1(去掉了一个字节的NALU头)字节内容写入sendbuf[14]开始的字符串。

						status = rtpsess.SendPacket((void *)sendbuf,l+1,H264,true,TimestampIn);  //注意mark标记位
							
						Tatal_File_Count = Tatal_File_Count+l+1;   //
						Every_File_Count = Every_File_Count+l+1;
							//nalu_c = nalu_c+l-1;
							//char str4[20];
							//sprintf(str4,"%d\n",l+1);
							//Showinfo2Java( env,jobj,str4);


						if (status < 0){
							(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Send rtp error !!!!!!!!");
							exit(-1);
						}
						t++;
				
					}
					else if(t<k&&0!=t)  //发送nalu中间的部分分片
					{
						//Showinfo2Java( env,jobj,"-send medial fu-a packet!!\n");
						
						memset(sendbuf,0,1500);

						//设置FU INDICATOR,并将这个HEADER填入sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //将sendbuf[12]的地址赋给fu_ind，之后对fu_ind的写入就将写入sendbuf中；
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//设置FU HEADER,并将这个HEADER填入sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						//fu_hdr->E=0;
						fu_hdr->R=0;
						fu_hdr->S=0;
						fu_hdr->E=0;
						fu_hdr->TYPE=n->nal_unit_type;

						nalu_payload=&sendbuf[2];//同理将sendbuf[14]的地址赋给nalu_payload
						memcpy(nalu_payload,n->buf+1+t*maxpacket,maxpacket);//去掉起始前面的nalu剩余内容写入sendbuf[14]开始的字符串。

						status = rtpsess.SendPacket((void *)sendbuf,maxpacket+2,H264,false,TimestampIn);

						Tatal_File_Count = Tatal_File_Count+maxpacket+2;
						Every_File_Count = Every_File_Count+maxpacket+2;
							//nalu_c = nalu_c+maxpacket;
							//char str5[20];
							//sprintf(str5,"%d\n",maxpacket+2);
							//Showinfo2Java( env,jobj,str5);

						if (status < 0){
							(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Send rtp error !!!!!!!!");
							exit(-1);
						}
						t++;
					}
				}
			}

			RTPTime::Wait(RTPTime(0,2000));  //1000微妙=1毫秒

		}

		char str[30];
		sprintf(str," (File_size=%.2f kb",nalu_c/1024.0);
		Showinfo2Java( env,jobj,str);
		Showinfo2Java( env,jobj," ---complete!)\n");

		complete_BitRate(env,jobj,Every_File_Count);   //计算并显示，视频码率
			
		Every_File_Count = 0;  //

		fclose(fp);    //关闭文件
		//wirte_stat2FILE(nalu_c);  //传入的参数是已经发送的视频文件的的字节数

		filepath = choiceNextPiece(env,jobj,filepath); //Showinfo2Java( env,jobj,filepath);

		fp = fopen( filepath, "rb" );    //打开文件
		if(fp == NULL)
		{
			(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can't open file !!!!!!!!");
			break;
		}
	}

	char str1[30];
	sprintf(str1,"\nBitRate switch %d times\n",Switch_count);
	Showinfo2Java( env,jobj,str1);

	char str2[30];
	sprintf(str2,"Tatal_File_Count=%d\n",Tatal_File_Count);
	//Showinfo2Java( env,jobj,str2);

	int Average_BitRate = (Tatal_File_Count)/(piece_During * piece_Number * 125);  //
	char str3[30];
	sprintf(str3,"Average_BitRate=%d kbps\n",Average_BitRate);
	Showinfo2Java( env,jobj,str3);

	rtpsess.BYEDestroy(RTPTime(10,0),0,0);

	//free(Buf);
#ifdef WIN32
	WSACleanup();
#endif // WIN32

}