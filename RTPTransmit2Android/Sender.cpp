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
#define FileTimeLen 10    //��Ƶ�ļ���ʱ������λ����

#define BUFFER_SIZE  1024 * 1024 * 4    //����һ��4M�Ŀռ���Ϊ����
#define piece_During 10     //��Ƶʱ��
#define piece_Number 111     //��ƵƬ�θ���
//#define BUFFER_SIZE  1024 * 1024     //����һ��1M�Ŀռ���Ϊ����

RTPSession rtpsess;    //rtp�Ự
RTPSessionParams sessionparams; //
RTPUDPv4TransmissionParams transparams;   //���԰�RTPUDPv4TransmissionParams�࿴��������ӿ��࣬����Ҫ�����¹��ܣ�
										   //����/��ȡ�󶨵�IP��ַ
										   //����/��ȡ�󶨵Ķ˿ں�
										   //����/��ȡ�㲥TTL
										   //��ȡ����IP��ַ��
										   //����/��ȡRTP/RTCP����/���ջ������Ĵ�С

char * filepath;
FILE *fp; 


float Tatal_File_Count = 0; //�ļ�����ͳ�ơ�
unsigned int Every_File_Count = 0; //�ļ�����ͳ�ơ�
SYSTEMTIME sys;
unsigned int Switch_count = 0;  //�����д�����ͳ��


int FindStartCode2 (unsigned char *Buf)  //���ҿ�ʼ��
{
	if(Buf[0]!=0 || Buf[1]!=0 || Buf[2] !=1) return 0; //�ж��Ƿ�Ϊ0x000001,����Ƿ���1
	else return 1;
}

int FindStartCode3 (unsigned char *Buf)
{
	if(Buf[0]!=0 || Buf[1]!=0 || Buf[2] !=0 || Buf[3] !=1) return 0;//�ж��Ƿ�Ϊ0x00000001,����Ƿ���1
	else return 1;
}

//ΪNALU_t�ṹ������ڴ�ռ�
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


//�ͷ�
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
	//��ȡ�����
	jclass cls = (*env).GetObjectClass(jobj);
	//��ȡ����ID
	jmethodID showinfoID = (*env).GetMethodID(cls, "ShowInfo","(Ljava/lang/String;)V" );   // ����java���showinfoID������ʾ��Ϣ
	//���÷���
	jstring param = (*env).NewStringUTF(message);
	(*env).CallVoidMethod(jobj,showinfoID,param);

	//-------------------------------------------------------------------------------------//
}

//������Ƶ����
void complete_BitRate(JNIEnv * env,jobject jobj,int file_size)
{
	int bitrate = file_size*8/(piece_During*1000);

		char str2[30];
		sprintf(str2,"--------BitRate=%d kbps \n",bitrate);
		Showinfo2Java( env,jobj,str2);

}

//�����������Ϊһ��NAL�ṹ�壬��Ҫ����Ϊ�õ�һ��������NALU��������NALU_t��buf�У���ȡ���ĳ��ȣ����F,IDC,TYPEλ��
//���ҷ���������ʼ�ַ�֮�������ֽ�������������ǰ׺��NALU�ĳ���
int GetAnnexbNALU (JNIEnv * env,jobject jobj,NALU_t *nalu)
{
  int pos = 0;
  int StartCodeFound, rewind;
  unsigned char * Buf;
  unsigned int info2;
  unsigned int info3;

  if ((Buf = (unsigned char*)malloc(nalu->max_size * sizeof(char))) == NULL)   //����nalu->max_size������Ϊsizeof(char)�������ռ䣬����ÿһ���ֽڶ���ʼ��Ϊ0
	  (*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Could not allocate Buf memory !!!!!!!");
	memset(Buf,0,nalu->max_size);

  nalu->startcodeprefix_len=3; //��ʼ���������еĿ�ʼ�ַ�Ϊ3���ֽ�
  
   if (3 != fread (Buf, 1, 3, fp))//�������ж�3���ֽ�
	   {
		free(Buf);
		return 0;
	   }
   info2 = FindStartCode2 (Buf);//�ж��Ƿ�Ϊ0x00 00 01 
   if(info2 != 1) 
   {
	//������ǣ��ٶ�һ���ֽ�
    if(1 != fread(Buf+3, 1, 1, fp))//��һ���ֽ�
		{
		 free(Buf);
		 return 0;
		}
    info3 = FindStartCode3 (Buf);//�ж��Ƿ�Ϊ0x00 00 00 01
    if (info3 != 1)//������ǣ�����-1
		{ 
		 free(Buf);
		 return -1;
		}
    else 
		{
		//�����0x00000001,�õ���ʼǰ׺Ϊ4���ֽ�
		 pos = 4;
		 nalu->startcodeprefix_len = 4;
		}
   }
   else
   {
   //�����0x000001,�õ���ʼǰ׺Ϊ3���ֽ�
	nalu->startcodeprefix_len = 3;
	pos = 3;
   }

   //������һ����ʼ�ַ��ı�־λ
   StartCodeFound = 0;
   info2 = 0;
   info3 = 0;
  
	

	while (!StartCodeFound)
	{
		if (feof (fp))//�ж��Ƿ����ļ�β����������ļ�ĩβ
		{
			nalu->len = (pos-1)-nalu->startcodeprefix_len;
			memcpy (nalu->buf, &Buf[nalu->startcodeprefix_len], nalu->len);     
			nalu->forbidden_bit = nalu->buf[0] & 0x80; //1 bit
			nalu->nal_reference_idc = nalu->buf[0] & 0x60; // 2 bit
			nalu->nal_unit_type = (nalu->buf[0]) & 0x1f;// 5 bit
			free(Buf);
			return pos-1;
		}
		Buf[pos++] = fgetc (fp);//��һ���ֽڵ�BUF��
		info3 = FindStartCode3(&Buf[pos-4]);//�ж��Ƿ�Ϊ0x00000001
		if(info3 != 1)
			info2 = FindStartCode2(&Buf[pos-3]);//�ж��Ƿ�Ϊ0x000001
		StartCodeFound = (info2 == 1 || info3 == 1);
	}
 
  // Here, we have found another start code (and read length of startcode bytes more than we should
  // have.  Hence, go back in the file
  rewind = (info3 == 1)? -4 : -3;     //�����0x00000001�򵹻�4���ֽڣ����򵹻�3���ֽ�

  if (0 != fseek (fp, rewind, SEEK_CUR))//���ļ�ָ��ָ��ǰһ��NALU��ĩβ��SEEK_CUR �ӵ�ǰλ��
  {
    free(Buf);
	(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Cannot fseek in the bit stream file !!!!!!!");
  }

  // Here the Start code, the complete NALU, and the next start code is in the Buf.  
  // The size of Buf is pos, pos+rewind are the number of bytes excluding the next
  // start code, and (pos+rewind)-startcodeprefix_len is the size of the NALU excluding the start code

  nalu->len = (pos+rewind)-nalu->startcodeprefix_len;  //��ȥnalu��ʼ���ʵ�����ݳ���
  memcpy (nalu->buf, &Buf[nalu->startcodeprefix_len], nalu->len);//����һ������NALU����������ʼǰ׺0x000001��0x00000001
  nalu->forbidden_bit = nalu->buf[0] & 0x80; //1 bit
  nalu->nal_reference_idc = nalu->buf[0] & 0x60; // 2 bit
  nalu->nal_unit_type = (nalu->buf[0]) & 0x1f;// 5 bit
  free(Buf);
 
  return (pos+rewind);//����������ʼ�ַ�֮�������ֽ�������������ǰ׺��NALU�ĳ���
}

//��ȡ�ͻ��˷�������Ƶ������������
int getClientOrder(JNIEnv * env, jobject jobj)
{
	//srand((unsigned)time(NULL));        //����
	//	return (rand()%4)+1;

	//��ȡ�����
	jclass cls = (*env).GetObjectClass(jobj);
	//��ȡ����ID
	jmethodID getNextPieceLevelID = (*env).GetMethodID(cls, "getNextPieceLevel","()I");   // ����java���getNextPieceLevel������ʾ��Ϣ
	//���÷���
	//jstring param = (*env).NewStringUTF(message);
	int order = (*env).CallIntMethod(jobj,getNextPieceLevelID);

		char str2[30];
		sprintf(str2,"-----request_order=%d\n",order);
		Showinfo2Java( env,jobj,str2);

	return order;
}

//ѡ����һ����ƵƬ�εľ���·��
char* choiceNextPiece(JNIEnv * env, jobject jobj,char* filepath)
{
	char string[200] = {"0"};
	char* path = NULL;
	strcpy(string,filepath);//����·�����Ƹ�string[]

	char delims[] = "\\";
	char *result = NULL;     
	result = strtok(string,delims);
	while(result != NULL){
		path = result;   //�Ӵ�����path�С��õ����һ���ִ�����ƵƵ�ε����ƣ�
		result = strtok(NULL,delims);
	}

	//����path(����)
	char point[] = ".";
	char Aname[100] = {"0"};
	strcpy(Aname,path);
	char* Pro_name = strtok(Aname,point); //�õ�ǰ׺��,����4S_IP_1�����Ĳ�����׺���ֶ�

	//�ָ�ǰ׺����4S_IP_1������������
	char delow_line[] = "_";
	char P_name[100] = {"0"};
	strcpy(P_name,Pro_name);
	char* parts[3]; //�����ַ���ָ������
	parts[0] = strtok(P_name,delow_line);
	for(int i=1;i<3;i++){
		parts[i] = strtok(NULL,delow_line);
	}

	//���л�ǰ�����ʵȼ����룬�������Ƚ���ȷ���Ƿ��л����ʵȼ�
	char bitrate_Level_sub[5] = {"0"};
	strcpy(bitrate_Level_sub,parts[1]); 
	//Showinfo2Java( env,jobj,bitrate_Level_sub);

	int order = atoi(parts[2]) + 1;  //�¸���ƵƬ�ε���ż�1��
	sprintf(parts[2],"%d",order);

	int level = getClientOrder(env,jobj);  //��������ɻ�õ�ָ��ȡ��
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
	

	if(strcmp(bitrate_Level_sub,parts[1]) != 0 )  //�Ƚ������ַ���
		Switch_count++;

	//������ַ���
	strcpy(string,filepath);//����·�����Ƹ�string[]

	char* newPro = strtok(string,"_"); 
	newPro = strcat(strcat(strcat(strcat(strcat(newPro,"_"),parts[1]),"_"),parts[2]),".264"); 

	char* new_Pro = new char[200];
	memset(new_Pro,0,200);
	strcpy(new_Pro,newPro);    

	return new_Pro;
}

JNIEXPORT jint JNICALL Java_javamedia_RTPTransmitAndroid_init_1RTP(JNIEnv * env, jobject jobj, jstring ServerIP, jint Port)  //��jint����JNIΪ�н�ʹJAVA��int�����뱾�� ��int��ͨ��һ�����ͣ����ǿ����Ӷ��������͵���intʹ�á�
{
	#ifdef WIN32
		WSADATA dat;
		WSAStartup(MAKEWORD(2,2),&dat);
	#endif // WIN32

	//std::cout << "ERROR: " << "init_1RTP" << std::endl;
	//Showinfo2Java( env,jobj,"              |(Detail)---->Created RTP session: ");

	int status; //��¼ִ�н��״̬

	//�����Ự
	sessionparams.SetOwnTimestampUnit(1.0 / 30.0); //30 video frames per second
	//sessionparams.SetUsePollThread(1);
	sessionparams.SetAcceptOwnPackets(true);

	transparams.SetPortbase( (uint16_t)Port );     //��ӱ��ض˿� ,��֧�������˿�
	status = rtpsess.Create( sessionparams, &transparams );
	if( status < 0 )
	{
		std::cout << "ERROR: " << RTPGetErrorString(status) << std::endl;
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Create rtp seeion error !!!!!!!!!");
		return status;
	}	


	char * IPstr;
	IPstr=(char*)(*env).GetStringUTFChars(ServerIP,NULL);

	uint32_t destip;   //Ŀ��ip
	destip = inet_addr(IPstr);   /*����ַ�����Internet�ġ�.�������ʽ��ʾһ�����ֵ�Internet��ַ������ֵ������Internet��ַ������Internet��ַ�������ֽ�˳�򷵻�(�ֽڴ���������)��
								     *inet_addr()�Ĺ����ǽ�һ�����ʮ���Ƶ�IPת����һ������������
									 */

	destip = ntohl( destip );  // ��һ���޷��ų��������������ֽ�˳��ת��Ϊ�����ֽ�˳��
  
	//��Ŀ���ַ��ӵ����͵�ַ�б���
	RTPIPv4Address addr( destip, (uint16_t)Port );   //�˴�������port��Զ��portʹ��һ������ֵ
	status = rtpsess.AddDestination( addr );
	if( status < 0 )
	{
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not add Destination to rtp seesion !!!!!!!!");
		return status;
	}

	//����RTP����Ĭ�ϲ���
	rtpsess.SetDefaultPayloadType( H264 );
	rtpsess.SetDefaultMark( false );
	rtpsess.SetDefaultTimestampIncrement( TimestampIn );
	rtpsess.SetSessionBandwidth(1000000);

	//���һЩ��ʼ����Ϣ
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

	filepath = (char*)(*env).GetStringUTFChars(file,NULL);    //��Ƶ�ļ��ľ��Ե�ַ

	NALU_t *n;

	NALU_HEADER		*nalu_hdr;  //�ṹ�壬NALU��Ԫ��ͷ��
	FU_INDICATOR	*fu_ind;
	FU_HEADER		*fu_hdr;       
	char sendbuf[1500];  
	char* nalu_payload;
	unsigned long maxpacket;  //�����Եİ���С
	int status = 1;

	//��ȡ������������Ĵ�С
	maxpacket = sessionparams.GetMaximumPacketSize(); // Ĭ��1400�ֽ�
	maxpacket -= 20;  //ÿһ��RTP���ݱ�����ͷ����Header���͸��أ�Payload������������ɣ�����ͷ��ǰ 12 ���ֽڵĺ����ǹ̶��ģ����������������Ƶ������Ƶ���ݡ�
					//��ȥ20����Ϊ���һ��fu-a��Ƭ���ܼ���2���ֽڵ�nalu_indicate��nalu_header,�ټ���12�ֽڵ�rtpͷ�����ܻ����������С

	n = AllocNALU(env,8000000);//Ϊ�ṹ��nalu_t�����Աbuf����ռ䡣����ֵΪָ��nalu_t�洢�ռ��ָ��

	fp = fopen( filepath, "rb" );    //���ļ�
	if( NULL == fp )
	{
		(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Can not open file !!!!!!!!");
		return ;
	}

	Tatal_File_Count = 0; 

	while(fp != NULL)
	{

		//�Ӿ���·����ȡ���ļ�������
		char string[200] = {"0"};
		char* VideoName = NULL;   //·�����ļ�������
		strcpy(string,filepath); 

		char delims[] = "\\";
		char *result = NULL;     
		result = strtok(string,delims);
		while(result != NULL){
			VideoName = result;   //�Ӵ�����VideoName�С��õ����һ���ִ�����ƵƵ�ε����ƣ�
			result = strtok(NULL,delims);
		}

		//��ӡ�����Ϣ
		char buf3[100] ;
		sprintf(buf3,"              |(Detail)-+->Sending [ %s ] .... ",VideoName);
		Showinfo2Java( env,jobj,buf3);

		int nalu_c = 0;
		
		//��ʼ����
		while(!feof(fp))
		{

			int size = GetAnnexbNALU(env,jobj,n); //ÿִ��һ�Σ���nalu����װ��n ,�ļ���ָ��ָ�򱾴��ҵ���NALU��ĩβ����һ��λ�ü�Ϊ�¸�NALU����ʼ��0x000001, 

			nalu_c = nalu_c+size;
			//nalu_count++;  //ÿ���ļ���nalu����
		
		
			if(size<4){                   //���ҷ���������ʼ�ַ�֮�������ֽ�������������ǰ׺��NALU�ĳ���
				(*env).ThrowNew((*env).FindClass("java/lang/Exception"),"Get nalu error !!!!!!!!");
				return;
			}

			//��һ��NALUС��MAX_RTP_PKT_LENGTH�ֽڵ�ʱ�򣬲���һ����RTP������
			if(n->len <= maxpacket)
			{
				//Showinfo2Java( env,jobj,"-send single rtp packet!!\n");
			
				//����NALU HEADER,�������HEADER����sendbuf[12]
				nalu_hdr = (NALU_HEADER*)&sendbuf[0]; //��sendbuf[0]�ĵ�ַ����nalu_hdr��֮���nalu_hdr��д��ͽ�д��sendbuf��
				nalu_hdr->F = n->forbidden_bit;
				nalu_hdr->NRI = n->nal_reference_idc>>5;//��Ч������n->nal_reference_idc�ĵ�6��7λ����Ҫ����5λ���ܽ���ֵ����nalu_hdr->NRI��????????????????????????????????????????????
				nalu_hdr->TYPE = n->nal_unit_type;

				nalu_payload = &sendbuf[1];//ͬ��sendbuf[1]����nalu_payload
				memcpy(nalu_payload,n->buf+1,n->len-1);//ȥ��naluͷ��naluʣ������д��sendbuf[13]��ʼ���ַ�����
				//ts_current=ts_current+timestamp_increse;

				status = rtpsess.SendPacket((void *)sendbuf,n->len,H264,true,TimestampIn);  //�������ݰ� , mark���

				Tatal_File_Count = Tatal_File_Count+n->len;    //ȫ���ֽ���ͳ��
				Every_File_Count = Every_File_Count+n->len;    //ÿ����ƵƬ���ֽ�ͳ��
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
				//�õ���nalu��Ҫ�ö��ٳ���Ϊmaxpacket�ֽڵ�RTP��������
				int k=0,l=0;
				k=n->len / maxpacket;//��Ҫk��maxpacket�ֽڵ�RTP��
				l=n->len % maxpacket;//���һ��RTP������Ҫװ�ص��ֽ���
				int t=0;//����ָʾ��ǰ���͵��ǵڼ�����ƬRTP��

				while(t<=k)
				{
					if(!t)//����һ����Ҫ��Ƭ��NALU�ĵ�һ����Ƭ����ʱt����0������FU HEADER��Sλ
					{
						//Showinfo2Java( env,jobj,"-send frist fu-a packet!!\n");					
						memset(sendbuf,0,1500);
						//����FU INDICATOR,�������HEADER����sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //��sendbuf[12]�ĵ�ַ����fu_ind��֮���fu_ind��д��ͽ�д��sendbuf�У�
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//����FU HEADER,�������HEADER����sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						fu_hdr->E=0;
						fu_hdr->R=0;
						fu_hdr->S=1;
						fu_hdr->TYPE=n->nal_unit_type;


						nalu_payload=&sendbuf[2];//ͬ��sendbuf[14]����nalu_payload����ַ����ȥ���͵���ֱ�Ӳ�����
						memcpy(nalu_payload,n->buf+1,maxpacket);//ȥ��NALUͷ

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
					//����һ����Ҫ��Ƭ��NALU�ķǵ�һ����Ƭ������FU HEADER��Sλ������÷�Ƭ�Ǹ�NALU�����һ����Ƭ����FU HEADER��Eλ
					else if(k==t)//���͵������һ����Ƭ��ע�����һ����Ƭ�ĳ��ȿ��ܳ���MAX_RTP_PKT_LENGTH�ֽڣ���l>1386ʱ��(�˴���ȥ20������)��
					{
						//Showinfo2Java( env,jobj,"-send last fu-a packet!!\n");

						memset(sendbuf,0,1500);

						//����FU INDICATOR,�������HEADER����sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //��sendbuf[12]�ĵ�ַ����fu_ind��֮���fu_ind��д��ͽ�д��sendbuf�У�
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//����FU HEADER,�������HEADER����sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						fu_hdr->R=0;
						fu_hdr->S=0;
						fu_hdr->TYPE=n->nal_unit_type;
						fu_hdr->E=1;
						nalu_payload=&sendbuf[2];//ͬ��sendbuf[14]����nalu_payload
						memcpy(nalu_payload, n->buf+1+t*maxpacket ,l-1);//��nalu���ʣ���l-1(ȥ����һ���ֽڵ�NALUͷ)�ֽ�����д��sendbuf[14]��ʼ���ַ�����

						status = rtpsess.SendPacket((void *)sendbuf,l+1,H264,true,TimestampIn);  //ע��mark���λ
							
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
					else if(t<k&&0!=t)  //����nalu�м�Ĳ��ַ�Ƭ
					{
						//Showinfo2Java( env,jobj,"-send medial fu-a packet!!\n");
						
						memset(sendbuf,0,1500);

						//����FU INDICATOR,�������HEADER����sendbuf[12]
						fu_ind =(FU_INDICATOR*)&sendbuf[0]; //��sendbuf[12]�ĵ�ַ����fu_ind��֮���fu_ind��д��ͽ�д��sendbuf�У�
						fu_ind->F=n->forbidden_bit;
						fu_ind->NRI=n->nal_reference_idc>>5;
						fu_ind->TYPE=28;

						//����FU HEADER,�������HEADER����sendbuf[13]
						fu_hdr =(FU_HEADER*)&sendbuf[1];
						//fu_hdr->E=0;
						fu_hdr->R=0;
						fu_hdr->S=0;
						fu_hdr->E=0;
						fu_hdr->TYPE=n->nal_unit_type;

						nalu_payload=&sendbuf[2];//ͬ��sendbuf[14]�ĵ�ַ����nalu_payload
						memcpy(nalu_payload,n->buf+1+t*maxpacket,maxpacket);//ȥ����ʼǰ���naluʣ������д��sendbuf[14]��ʼ���ַ�����

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

			RTPTime::Wait(RTPTime(0,2000));  //1000΢��=1����

		}

		char str[30];
		sprintf(str," (File_size=%.2f kb",nalu_c/1024.0);
		Showinfo2Java( env,jobj,str);
		Showinfo2Java( env,jobj," ---complete!)\n");

		complete_BitRate(env,jobj,Every_File_Count);   //���㲢��ʾ����Ƶ����
			
		Every_File_Count = 0;  //

		fclose(fp);    //�ر��ļ�
		//wirte_stat2FILE(nalu_c);  //����Ĳ������Ѿ����͵���Ƶ�ļ��ĵ��ֽ���

		filepath = choiceNextPiece(env,jobj,filepath); //Showinfo2Java( env,jobj,filepath);

		fp = fopen( filepath, "rb" );    //���ļ�
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