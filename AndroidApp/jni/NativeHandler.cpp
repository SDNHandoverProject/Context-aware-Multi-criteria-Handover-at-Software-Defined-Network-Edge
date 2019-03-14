
#include <jni.h>
#include <android/log.h>
//#include "customrtpsession.h"
#include "jrtplib3/rtpsession.h"
#include "jrtplib3/rtppacket.h"
#include "jrtplib3/rtpudpv4transmitter.h"
#include "jrtplib3/rtpipv4address.h"
#include "jrtplib3/rtpsessionparams.h"
#include "jrtplib3/rtperrors.h"
#include "jrtplib3/rtpsourcedata.h"
#include "Structs.h"

extern "C" //ʹ��ffmpeg��Դ�ļ���  *.cpp �������Ѿ�������ffmpeg����Ӧ��ͷ�ļ�����ʹ�� extern "C" �޶� �����ffmpeg��ͷ�ļ���
{
#include "libavcodec/avcodec.h"
#include "libavutil/mathematics.h"
#include "libswscale/swscale.h"
#include "libavformat/avformat.h"
}

#include "com_javamedia_NativeHandler.h"
#ifndef WIN32
	#include <netinet/in.h>
	#include <arpa/inet.h>
#else
	#include <winsock2.h>
#endif // WIN32
#include <sys/time.h>
#include <stdlib.h>
#include <pthread.h>
#include <stdio.h>
#include <sys/time.h>
#include <iostream>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <stdio.h>


using namespace jrtplib;

#define QUEUE_Threshold 100  //���������ֵΪ100��
#define H264FrameSize 115200
#define DES_FMT PIX_FMT_RGB565

unsigned char delimiter_h264[4]  = {0x00, 0x00, 0x00, 0x01};

struct RTPpackets_queue rtpqueue = {0,NULL,NULL};
struct NALU_queue naluqueue = {0,NULL,NULL};
struct Frame_queue framequeue = {0,NULL,NULL};

unsigned char* framebuf ;
unsigned char* FirstFramebuf ;

bool SEIFrameArrived   = false;
bool SPSFrameArrived   = false;
bool PPSFrameArrived   = false;
bool KeyFrameArrived   = false;
bool firstFrameArrived = false;
bool decoderIsInited = false;
bool FrameDecodedOk = false;
bool rtpSessionIsActive_CreateNalu2Queue = false;
bool isDecode2Queue = false;

unsigned int SPSFrameLen = 0;
unsigned int PPSFrameLen = 0;
unsigned int KeyFrameLen = 0;
unsigned int FramebufPos = 0;
unsigned int SEIFrameLen = 0;
unsigned int throughput_count = 0;
unsigned int throughput_count2 = 0;
unsigned int FrameBuffer_count = 0;
unsigned int streamedSegment_count = 0;

static AVCodec *AVcodec = NULL ;
static AVCodecContext *AVcodecCtx = NULL;
static AVPacket pkt ;
static AVFrame *src_Frame = NULL;

RTPSession sess;

unsigned int count = 0;
unsigned int nalu_count = 0;
unsigned int KEYframeCount = 0;
unsigned int frameCount = 0;
//====================================================================================================================================================
int *colortab=NULL;
int *u_b_tab=NULL;
int *u_g_tab=NULL;
int *v_g_tab=NULL;
int *v_r_tab=NULL;

unsigned int *rgb_2_pix=NULL;
unsigned int *r_2_pix=NULL;
unsigned int *g_2_pix=NULL;
unsigned int *b_2_pix=NULL;

int iWidth;
int iHeight;

void DeleteYUVTab()
{
	av_free(colortab);
	av_free(rgb_2_pix);
}

void CreateYUVTab_16()
{
	int i;
	int u, v;

//	tmp_pic = (short*)av_malloc(iWidth*iHeight*2); // ���� iWidth * iHeight * 16bits

	colortab = (int *)av_malloc(4*256*sizeof(int));
	u_b_tab = &colortab[0*256];
	u_g_tab = &colortab[1*256];
	v_g_tab = &colortab[2*256];
	v_r_tab = &colortab[3*256];

	for (i=0; i<256; i++)
	{
		u = v = (i-128);

		u_b_tab[i] = (int) ( 1.772 * u);
		u_g_tab[i] = (int) ( 0.34414 * u);
		v_g_tab[i] = (int) ( 0.71414 * v);
		v_r_tab[i] = (int) ( 1.402 * v);
	}

	rgb_2_pix = (unsigned int *)av_malloc(3*768*sizeof(unsigned int));

	r_2_pix = &rgb_2_pix[0*768];
	g_2_pix = &rgb_2_pix[1*768];
	b_2_pix = &rgb_2_pix[2*768];

	for(i=0; i<256; i++)
	{
		r_2_pix[i] = 0;
		g_2_pix[i] = 0;
		b_2_pix[i] = 0;
	}

	for(i=0; i<256; i++)
	{
		r_2_pix[i+256] = (i & 0xF8) << 8;
		g_2_pix[i+256] = (i & 0xFC) << 3;
		b_2_pix[i+256] = (i ) >> 3;
	}

	for(i=0; i<256; i++)
	{
		r_2_pix[i+512] = 0xF8 << 8;
		g_2_pix[i+512] = 0xFC << 3;
		b_2_pix[i+512] = 0x1F;
	}

	r_2_pix += 256;
	g_2_pix += 256;
	b_2_pix += 256;
}

void DisplayYUV_16(unsigned int *pdst1, unsigned char *y, unsigned char *u, unsigned char *v, int width, int height, int src_ystride, int src_uvstride, int dst_ystride)
{		//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "1");
	int i, j;
	int r, g, b, rgb;

	int yy, ub, ug, vg, vr;

	unsigned char* yoff;
	unsigned char* uoff;
	unsigned char* voff;

	unsigned int* pdst=pdst1;
			//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "2");
	int width2 = width/2;
	int height2 = height/2;
	//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "3");
	if(width2>iWidth/2)
	{    //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "4");
		width2=iWidth/2;

		y+=(width-iWidth)/4*2;
		u+=(width-iWidth)/4;
		v+=(width-iWidth)/4;
	}

	if(height2>iHeight)
		height2=iHeight;

	for(j=0; j<height2; j++) // һ��2x2���ĸ�����
	{   //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "5");
		yoff = y + j * 2 * src_ystride;
		uoff = u + j * src_uvstride;
		voff = v + j * src_uvstride;

		for(i=0; i<width2; i++)
		{    //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "6");
			yy  = *(yoff+(i<<1));
			ub = u_b_tab[*(uoff+i)];
			ug = u_g_tab[*(uoff+i)];
			vg = v_g_tab[*(voff+i)];
			vr = v_r_tab[*(voff+i)];

			b = yy + ub;
			g = yy - ug - vg;
			r = yy + vr;

			rgb = r_2_pix[r] + g_2_pix[g] + b_2_pix[b];

			yy = *(yoff+(i<<1)+1);
			b = yy + ub;
			g = yy - ug - vg;
			r = yy + vr;
			//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "7");
			pdst[(j*dst_ystride+i)] = (rgb)+((r_2_pix[r] + g_2_pix[g] + b_2_pix[b])<<16);

			yy = *(yoff+(i<<1)+src_ystride);
			b = yy + ub;
			g = yy - ug - vg;
			r = yy + vr;

			rgb = r_2_pix[r] + g_2_pix[g] + b_2_pix[b];
			//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "8");
			yy = *(yoff+(i<<1)+src_ystride+1);
			b = yy + ub;
			g = yy - ug - vg;
			r = yy + vr;

			pdst[((2*j+1)*dst_ystride+i*2)>>1] = (rgb)+((r_2_pix[r] + g_2_pix[g] + b_2_pix[b])<<16);
		}
	}
}


//FILE * throughput_fp = fopen("/storage/emulated/0/MPlayer/throughput_fp.txt", "ab+");

void wirte_stat2FILE(FILE *fp , char * framebuf, int FramebufPos )
{
	FILE *ff = fp ;

	if(ff != NULL){
		fwrite(framebuf , sizeof(char) , FramebufPos ,ff);
		fprintf(ff,"\n");
		fflush(ff);
	}
	else
		__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "open file error ----------------------------------------------------------");

}

void checkerror(int rtperr)
{
	if (rtperr < 0){
		__android_log_print(ANDROID_LOG_INFO, "ERROR: ", RTPGetErrorString(rtperr).c_str());
		exit(-1);
	}
}


/**
 * ��ʼ���ײ�� RTP �Ự
 */
JNIEXPORT void JNICALL Java_com_javamedia_NativeHandler_nativeReceiver_1init
(JNIEnv * env, jclass jclazz, jstring ServerIpAddress, jint ServerPort, jint LocalPort)
{

	__android_log_print(ANDROID_LOG_INFO, "Init_RTP : ", "inition start!!!!!");
	#ifdef WIN32
		WSADATA dat;
		WSAStartup(MAKEWORD(2,2),&dat);
	#endif //WIN32

	u_int16_t baseport = (u_int16_t)LocalPort;  //ֱ�Ӹ�ֵ������������
	u_int16_t destport = (u_int16_t)ServerPort;
	int status;

	jboolean flag = false;
	char buf[50] ;
	jchar const * ipchar = NULL;
	int lenth;
	ipchar = env->GetStringChars(ServerIpAddress, &flag);
	lenth = env->GetStringLength(ServerIpAddress);
	memset(buf,0,sizeof(buf));
	for(int i =0; i < lenth; i++)
	{
	     buf[i] = (char)ipchar[i];
	}
	std::string ipstr = std::string((char *)buf);
	uint32_t destip = inet_addr(ipstr.c_str());
	destip = ntohl(destip);   //��ȡ��Ŀ��ip��ַ

	RTPUDPv4TransmissionParams transparams;
	RTPSessionParams sessparams;
	sessparams.SetOwnTimestampUnit(1.0/30.0);
	sessparams.SetAcceptOwnPackets(true);
	transparams.SetPortbase(baseport);
	status = sess.Create(sessparams,&transparams);
	checkerror(status);

	sess.SetDefaultPayloadType(96);
	sess.SetDefaultTimestampIncrement(5);
	sess.SetSessionBandwidth(1000000);

	RTPIPv4Address addr(destip,destport);  //��Ϊ���ն˿��Բ��ó�ʼ����Щ�𣿣�������
	status = sess.AddDestination(addr); //����ͨ�ŵ�ַ
	checkerror(status);

	rtpSessionIsActive_CreateNalu2Queue = sess.IsActive();   //

	__android_log_print(ANDROID_LOG_INFO, "Init_RTP: ", "inition over!!!!!");
	#ifdef WIN32
		WSACleanup();
	#endif // WIN32
}


void Decoder_init(unsigned char * initData ,int len)  //�÷������ڳ�ʼ��������
{
	//avcodec_init();//���function�Ѿ�������Ҫ�ˣ��������avcodec_register()���� avcodec_register_all()ʱ��ffmpeg���Զ������������Է��Ĵ󵨵��Ƴ����Ϳ����ˡ�
	if(!decoderIsInited)
	{
		avcodec_register_all();//*register all the codecs

		AVcodec = avcodec_find_decoder(CODEC_ID_H264);//*find the h264 video decoder
		if (!AVcodec)
		   __android_log_print(ANDROID_LOG_INFO, "ERROR: ", "AVcodec not found???????????????????\n");

		AVcodecCtx = avcodec_alloc_context3(AVcodec);

		if (avcodec_open2(AVcodecCtx, AVcodec,NULL) < 0)
			__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "could not open AVcodec??????????????????\n");

		AVFrame *frame = av_frame_alloc();
		AVPacket packet;
		av_new_packet(&packet,len);
		memcpy(packet.data,initData,len);
		int ret,got_picture;
		ret = avcodec_decode_video2(AVcodecCtx, frame, &got_picture, &packet);

		char str[10];
		sprintf(str,"%d",ret);
		__android_log_print(ANDROID_LOG_INFO, "ret: ", str);
		char str1[10];
		sprintf(str1,"%d",got_picture);
		__android_log_print(ANDROID_LOG_INFO, "got_picture: ", str1);

		if (ret > 0){
			if(got_picture){
				decoderIsInited = true ; //��SPS,PPS,IDR֡������������ʼ��
				__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "frist DecoderIsInited = true ??????????????????\n");
			}
		}

		iWidth = AVcodecCtx->width;
		iHeight = AVcodecCtx->height;
		src_Frame = av_frame_alloc();
		av_init_packet(&pkt);
		CreateYUVTab_16();
	}
	__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "INIT DECODEER +++++++++++++++++++++++++++++++++++++++++++++++++");

}

void addNALU2queue(unsigned char * framebuf, unsigned int FramebufPos)  //���齨��NALU��Ԫ��ӵ�������
{
	struct NALU_node* nalunode = (NALU_node *)malloc(sizeof(struct NALU_node));
	nalunode->nalu_node = (unsigned char *)(new char[FramebufPos + 1]);    //����һ���ڴ�
	memcpy(nalunode->nalu_node , framebuf , FramebufPos);
	nalunode->nalu_node[FramebufPos] = 0;
	nalunode->nalu_length = FramebufPos;   //NALU��Ԫ����
	nalunode->next_nalu = NULL;

	//��ʼ���nalu��nalu��Ԫ�Ķ�����
	if(naluqueue.NodeCount == 0)
	{
		naluqueue.FirstNode = nalunode;
		naluqueue.EndNode = nalunode;
		naluqueue.NodeCount++;
	}
	else//��������β
	{
		naluqueue.EndNode->next_nalu = nalunode ;
		naluqueue.EndNode = nalunode ;
		naluqueue.NodeCount++;
	}
	streamedSegment_count++;

//	//----------------------//��鿴rtp�����а�����
//	char str3[20];
//	sprintf(str3,"nalu_count=%d",naluqueue.NodeCount);
//	__android_log_print(ANDROID_LOG_INFO, "queue_Count_Nalu: ", str3);
//	//----------------------//

}


//�����߳�����Ҫִ�еĴ���
void* rtprece_to_nalu_Thread(void* args){

	__android_log_print(ANDROID_LOG_INFO, "Info: ", "start receive rtp packet loop !!!!!!!!!!!!!!!!");


	unsigned char * framebuf = (unsigned char *)(new char[H264FrameSize]);
	if(framebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "framebuf malloc error\n");}

	unsigned char * FirstFramebuf = (unsigned char *)(new char[H264FrameSize]);    //�����һ����
	if(FirstFramebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "FirstFramebuf malloc error\n");}

	unsigned char * SPSFramebuf = (unsigned char *)(new char[400]);
	if(FirstFramebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "SPStFramebuf malloc error\n");}

	unsigned char * PPSFramebuf = (unsigned char *)(new char[400]);
	if(FirstFramebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "PPStFramebuf malloc error\n");}

	unsigned char * SEIFramebuf = (unsigned char *)(new char[400]);
	if(FirstFramebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "SEItFramebuf malloc error\n");}

	unsigned char * KeyFramebuf = (unsigned char *)(new char[H264FrameSize - 800]);
	if(FirstFramebuf == 0){__android_log_print(ANDROID_LOG_INFO, "ERROR: ", "KeytFramebuf malloc error\n");}



	RTPSourceData * sData;
	while(sess.IsActive())//�������ѭ���н�������
	{
	    sess.BeginDataAccess(); //��ʼ���ݽ���
		if(sess.GotoFirstSourceWithData())
		{
			do
			{
				RTPPacket *pack = NULL;
				RTCPPacket *rtcppack = NULL; //rtcp��

				sData = sess.GetCurrentSourceInfo() ;

				while((pack = sess.GetNextPacket()) != NULL)
				{   //__android_log_print(ANDROID_LOG_INFO, "Info: ", ">>>");
					rtpSessionIsActive_CreateNalu2Queue = true;
					struct RTPpacket_Node * cu_node = (RTPpacket_Node *)malloc(sizeof(RTPpacket_Node));
					cu_node->timestamp = pack->GetTimestamp();
					cu_node->suqenceNum = pack->GetSequenceNumber();
					cu_node->payloadlength = pack->GetPayloadLength();
					cu_node->payloadData = (unsigned char *)(new char[cu_node->payloadlength+1]);
					memcpy(cu_node->payloadData, (unsigned char *)pack->GetPayloadData(), cu_node->payloadlength);
					cu_node->payloadData[cu_node->payloadlength] = 0;

					cu_node->mark = pack->HasMarker();  //���λ
					cu_node->next = cu_node->front = NULL;

					throughput_count = throughput_count + cu_node->payloadlength;
					throughput_count2 = throughput_count2 + cu_node->payloadlength;

					int nalType = (0x1f) & cu_node->payloadData[0];    //ȡ��NALͷ�ĺ���һ���ֽڵĺ����λ ��ʾtype(nal����)

					if(cu_node->mark) //һ��rtp���ݰ���ֻ��һ��nalu��������NALU���һ��������
					{
						if(nalType == 28)  //��fu-a��������������һ����
						{
							nalType = (0x1f) & cu_node->payloadData[1];     //��nal_type����ΪFU header�е�type,ָ������������ʲô��������
							memcpy((framebuf+FramebufPos),cu_node->payloadData+2 , cu_node->payloadlength - 2);  //��Ϊfu-a���ݰ��������ֽڣ�fu_indicator��fu_header
							FramebufPos = FramebufPos + cu_node->payloadlength - 2; //��ǰָ��ǰ��
						}
						else if (nalType > 0 && nalType < 24)  //һ��rtp������ֻ��һ��h264�ĵ�ԪNALU
						{
							FramebufPos = 0 ;
							memset(framebuf,0,H264FrameSize);  //��ʼ��Ϊ0
							memcpy(framebuf, delimiter_h264, 4) ;   //��ӿ�ʼ����00 00 00 01�ĸ��ֽ�
							FramebufPos += 4 ;
							memcpy((framebuf+FramebufPos), cu_node->payloadData ,cu_node->payloadlength);   //Ȼ���ٽ����ݸ�����ӵ�֡������
							FramebufPos += cu_node->payloadlength ;
						}

						if(nalType == 0x07)    //SPS֡
						{
							//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "SPS");
							SPSFrameArrived = true;   //sps֡����
							SPSFrameLen = FramebufPos;
							memcpy(FirstFramebuf , framebuf , SPSFrameLen);  //��sps֡���롰��һ֡����buf��
							if(PPSFrameArrived){
								memcpy((FirstFramebuf + SPSFrameLen), PPSFramebuf, PPSFrameLen);
							}
							if(KeyFrameArrived){
								memcpy((FirstFramebuf + SPSFrameLen + PPSFrameLen), KeyFramebuf, KeyFrameLen);
							}
							if(KeyFrameArrived && PPSFrameArrived){
								firstFrameArrived = true;
							}
						}
						else if(nalType == 0x08)   //pps֡
						{
							//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "PPS");
							PPSFrameArrived = true;
							PPSFrameLen = FramebufPos;

							if(SPSFrameArrived)   //���SPS֡�Ѿ����
							{
								memcpy((FirstFramebuf + SPSFrameLen), framebuf, PPSFrameLen);
								if(KeyFrameArrived ){
									memcpy((FirstFramebuf + SPSFrameLen + PPSFrameLen), framebuf, KeyFrameLen);
									firstFrameArrived = true;
								}
							}
							else{   //���sps֡��û�н��յ����Ͱ�pps֡�ȷ���ppsframebuf��// put in PPS buffer
								memset(PPSFramebuf ,0, 400);
								memcpy(PPSFramebuf, framebuf, PPSFrameLen);
							}
						}
						else if(nalType == 0x05)  //�ؼ�֡
						{
							//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "KEY nalu");
							KeyFrameArrived = true ;
							KeyFrameLen = FramebufPos-1 ;
							if(SPSFrameArrived && PPSFrameArrived) //���sps֡��pps֡���Ѿ�����
							{
								memcpy((FirstFramebuf + SPSFrameLen + PPSFrameLen ), framebuf+1, KeyFrameLen);
								firstFrameArrived = true;
							}
							else
							{
								memcpy(KeyFramebuf, framebuf+1, KeyFrameLen);
							}
						}
						else{//�������
							rtpSessionIsActive_CreateNalu2Queue = true;
							addNALU2queue(framebuf,FramebufPos);//��buf�е�nalu��Ԫ��ӵ�NALU�Ķ�����ȥ//�������
						}

						//�ж��Ƿ��һ��֡(��sps+pps+key֡�����)����������ڳ�ʼ��������
						if(firstFrameArrived)  //����һ��֡�ﵽ�Ϳ��Գ�ʼ��������
						{
							if(!decoderIsInited){
								Decoder_init(FirstFramebuf , SPSFrameLen + PPSFrameLen + KeyFrameLen);  //��ʼ��������
							}
							else{//�������
								rtpSessionIsActive_CreateNalu2Queue = true;
								addNALU2queue(FirstFramebuf , SPSFrameLen + PPSFrameLen + KeyFrameLen);//��buf�е�nalu��Ԫ��ӵ�NALU�Ķ�����ȥ//�������
								//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "add NEW KEY nalu");

							}

							//��ʼ����ɺ󣬽������ֵΪfalse,��ֹһֱ��ʼ������������sps+pps+key֡���ڳ�ʼ��һ��
							firstFrameArrived = false;
							SPSFrameArrived   = false;
							PPSFrameArrived   = false;
							KeyFrameArrived   = false;
							//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "init over !!!!!!!!!!!!!!!!!!!!!!!!");
						}
					}
					else		//RTP packet's marker is false, NAl Unit is part.
					{
						if(nalType == 28)      //��FU-A�������ʽ
						{
							nalType = (0x1f) & cu_node->payloadData[1]; // reset the nal unit type ȡ�� FU header�ֽڵ�type��������ֽڣ�
																		// the first packet of FU-A  sλ �����ó�1,��ʼλָʾ��ƬNAL��Ԫ�Ŀ�ʼ  E���ó�1, ����λָʾ��ƬNAL��Ԫ�Ľ���
							if(cu_node->payloadData[1] & 0x80 ) //��һ������   FU header�Ķ�������10 00 00 00  sλʱ1��EλΪ0 ��R����Ϊ0��typeΪ0����ʾ��fu-a�����ʽ��NALU�ĵ�һ����
							{
								//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "First");
								FramebufPos = 0;  //��ʼ��λ��ָ��
								memset(framebuf,0,H264FrameSize);  //��ʼ��Ϊ0
								memcpy(framebuf,delimiter_h264 , 4);  //���뿪ʼ��//add Startcode
								FramebufPos += 4 ;
								framebuf[FramebufPos++] = (cu_node->payloadData[0] & 0xe0) | (cu_node->payloadData[1] & 0x1f);   //��ԭnalu�ĵ�һ���ֽ�
								memcpy((framebuf+FramebufPos),cu_node->payloadData+2 , cu_node->payloadlength-2);   //ȥ��fu-indicator �� fu-header�����ֽ�
								FramebufPos = FramebufPos + cu_node->payloadlength - 2;     //ָ��ǰ��
							}
							else// the middle packets of FU-A  �����NALU�м��ĳ��һ������Ƭ��
							{
								//__android_log_print(ANDROID_LOG_INFO, "Info_nalu: ", "mudel");
								memcpy((framebuf+FramebufPos) , cu_node->payloadData+2 , cu_node->payloadlength-2); //ȥ��fu-indicator �� fu-header�����ֽ�ֱ�Ӵ洢����
								FramebufPos = FramebufPos + cu_node->payloadlength - 2;  //ָ��ǰ��
							}
						}
					}
					sess.DeletePacket(pack);
				}
			}while (sess.GotoNextSourceWithData());

			if(sData->ReceivedBYE()){  //���յ��Ự������BYE��
				__android_log_print(ANDROID_LOG_INFO, "Info: ", "get BYE####################################################");
				sess.Destroy();  //�뿪�Ự
				break;
			}
		}
	    sess.EndDataAccess();

#ifndef RTP_SUPPORT_THREAD
		status = sess.Poll();
		checkerror(status);
#endif // RTP_SUPPORT_THREAD

		usleep(5000);
	}

	rtpSessionIsActive_CreateNalu2Queue = false; //rtp�ײ�����߳��˳�ѭ��
	__android_log_print(ANDROID_LOG_INFO, "INFO: ", "RTP packet thread loop is over !!!!!!!!!!!!!!!!");
}

//�����߳�
void* DecodeFrameThread(void* args){

	while(naluqueue.NodeCount < 100 && decoderIsInited){  //�ж�rtp�ڵ���������Ƿ���ڶ��ٸ�,�����Ѿ���������ʼ��,���С��200���͵ȴ�
			usleep(500000);   // usleep���ܰѽ��̹���һ��ʱ�䣬 ��λ��΢�루�����֮һ��)
		}

	__android_log_print(ANDROID_LOG_INFO, "INFO: ", "start decode function!!!!!!!!!!!!!!!!");

	int got_picture;
	int consumed_bytes;

	while(rtpSessionIsActive_CreateNalu2Queue || naluqueue.NodeCount > 0){//��CreateNALU���߳�û��ֹͣ������nalu������Ҳ�нڵ㡣��ִ��
		if(rtpSessionIsActive_CreateNalu2Queue && naluqueue.NodeCount < 5){
			usleep(100000);   //��Ϣ50���룬���Եȴ�Nalu���Ķ��м����µĽڵ�
		}
		else if(framequeue.NodeCount > 500 ){   //frame����̫�󣬻�ȫ��ռ���ֻ��ڴ棬���Ե�һ���ڿ�ʼ����
			usleep(100000);
			continue;
		}
		else
		{   isDecode2Queue = true;    //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "1---------------");
			//��NALU������ȡ��һ���ڵ�
			struct NALU_node* cu_nalunode = naluqueue.FirstNode;  //cu_nalunodeָ��ָ���һ���ڵ㣬Ȼ��ָ������ƶ�
			naluqueue.FirstNode = naluqueue.FirstNode->next_nalu;     //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "2");
			naluqueue.NodeCount--;
										//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "3");
			pkt.data = cu_nalunode->nalu_node;
			pkt.size = cu_nalunode->nalu_length;    //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "4");
			consumed_bytes = avcodec_decode_video2(AVcodecCtx, src_Frame, &got_picture, &pkt);  //��Ҫ�Ľ�����ٶ���������
													//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "5");
			if(consumed_bytes > 0 && got_picture > 0)
			{
				struct Frame_node* frameNode = (Frame_node*)malloc(sizeof(Frame_node));   //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "6");
				frameNode->frame_data = (unsigned char*)(new char[iWidth * iHeight * 2]);   //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "6-7");
				memset(frameNode->frame_data , 0 , iWidth * iHeight * 2 );        //__android_log_print(ANDROID_LOG_INFO, "INFO: ", "7");
				frameNode->frame_length = consumed_bytes;
																			//
				frameNode->next = NULL;

																				if(got_picture==0){char str3[30]; sprintf(str3,"got_picture=%d",got_picture);	__android_log_print(ANDROID_LOG_INFO, "INFO: ", str3);}  //����got_picture
																				//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "1-------------------------");
				DisplayYUV_16((unsigned int*)frameNode->frame_data, src_Frame->data[0], src_Frame->data[1], src_Frame->data[2], AVcodecCtx->width, AVcodecCtx->height, src_Frame->linesize[0], src_Frame->linesize[1], iWidth);
																				//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "9-------------------------");
				//����ѽ���֡��֡������ȥ
				if(framequeue.NodeCount == 0)
				{
					framequeue.EndNode = framequeue.FirstNode = frameNode ;
					framequeue.NodeCount++;
				}
				else//��������β
				{
					framequeue.EndNode->next = frameNode ;
					framequeue.EndNode = frameNode ;
					framequeue.NodeCount++ ;
				}
				FrameBuffer_count++ ;//	/
																//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "10");

//				//----------------------//��鿴rtp�����а�����
//					char str3[30];
//					sprintf(str3,"frame_count=%d",framequeue.NodeCount);
//					__android_log_print(ANDROID_LOG_DEBUG, "queue_Count_Nalu: ", str3);
//				//	//----------------------//
			}
			delete cu_nalunode->nalu_node;
			cu_nalunode->nalu_node = NULL;
			free(cu_nalunode);
		}
	}

	isDecode2Queue = false;
	__android_log_print(ANDROID_LOG_INFO, "Info: ", "Decode thread is over >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");

}

/**
 * ��ʼ�ײ����rtp����
 */
JNIEXPORT void JNICALL Java_com_javamedia_NativeHandler_nativeReceiver_1start(JNIEnv * env, jclass jclazz)
{	__android_log_print(ANDROID_LOG_INFO, "Info: ", "START ALL Threads >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	//ʹ��һ��c�߳�������ݵĽ���
	pthread_t t1;
	pthread_t t2;
	pthread_create(&t1,NULL,rtprece_to_nalu_Thread,NULL); 	  //����RTP���ݰ�
	pthread_create(&t2,NULL,DecodeFrameThread,NULL); 	 //���Nalu��Ԫ
	pthread_join(t1,NULL);
	pthread_join(t2,NULL);

}


//�ͷ���Դ
JNIEXPORT void JNICALL Java_com_javamedia_NativeHandler_Decoder_1release(JNIEnv * env, jclass jclazz)
{	__android_log_print(ANDROID_LOG_INFO, "Info: ", "release all >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	avcodec_close(AVcodecCtx);
	av_free(AVcodecCtx);
	av_free(&pkt);
	av_free(src_Frame);
	DeleteYUVTab();
}

//ֹͣRTP
JNIEXPORT void JNICALL Java_com_javamedia_NativeHandler_nativeReceiver_1stop(JNIEnv * env, jclass jclazz)
{	__android_log_print(ANDROID_LOG_INFO, "Info: ", "stop >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	sess.Destroy();
}


JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_get_1W
(JNIEnv * env, jclass jclazz)
{	//__android_log_print(ANDROID_LOG_INFO, "Info: ", "get_w >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	if(decoderIsInited)
		return AVcodecCtx->width;
	else
		return -1;
}

JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_get_1H(JNIEnv * env, jclass jclazz)
{	//__android_log_print(ANDROID_LOG_INFO, "Info: ", "get_h >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	if(decoderIsInited)
		return AVcodecCtx->height;
	else
		return -1;
}

JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_get_1FrameQueue_1C(JNIEnv * env, jclass jclazz)
{	//__android_log_print(ANDROID_LOG_INFO, "Info: ", "get_1FrameQueue_1Count >>>>>>>>>>>>>>>>>>>>>>>>>>>>>.\n");
	return framequeue.NodeCount;
}

JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_Get_1streamedSegment(JNIEnv * env, jclass jclazz)
{
	double Segment_count = streamedSegment_count;   // / 205 ;   //ÿ����Ƶ���ڽ��ն���205��Nalu
	streamedSegment_count = 0 ;    //ÿ����ʱ���ȡֵһ�Σ�����ֵΪ0
	return Segment_count ;
}

JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_Get_1streamedSegment_1tatol(JNIEnv * env, jclass jclazz)
{
	return naluqueue.NodeCount;
}


int get_a_frame(JNIEnv * env, jbyteArray jPixel)
{
	//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "get a decode frame!!!!!!!!!!!!!!!");
	jbyte * Pixel= (jbyte*)(*env).GetByteArrayElements(jPixel, 0);

	struct Frame_node* cu_frameNode = framequeue.FirstNode;		//cu_frameNodeָ��ָ���һ���ڵ㣬Ȼ��ָ������ƶ�
	framequeue.FirstNode = framequeue.FirstNode->next;
	framequeue.NodeCount--;

	memcpy( Pixel , cu_frameNode->frame_data , iWidth * iHeight * 2 );
	(*env).ReleaseByteArrayElements(jPixel, Pixel, 0);
	int leng = cu_frameNode->frame_length;

	delete cu_frameNode->frame_data ;
	cu_frameNode->frame_data = NULL ;
	free(cu_frameNode);

	return leng;

}

//��ȡ������һ֡����
JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_get_1a_1DecodedFrame(JNIEnv * env, jclass jclazz, jbyteArray jPixel)
{
	//__android_log_print(ANDROID_LOG_INFO, "INFO: ", "get decode frame!!!!!!!!!!!!!!!");

//	//----------------------//��鿴frame�����а�����
//	char str1[10];
//	sprintf(str1,"%d",framequeue.NodeCount);
//	__android_log_print(ANDROID_LOG_INFO, "framequeue.NodeCount: ", str1);
//	//----------------------//

	if(framequeue.NodeCount > 1)
	{
		return get_a_frame(env,jPixel);
	}
	else
		return -1;

}

//��������֡��Ȼ���ȡһ֡
JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_get_1a_1Decoded_1jumped_1Frame(JNIEnv * env , jclass jclazz , jbyteArray jPixel , jint step_size)
{
	__android_log_print(ANDROID_LOG_INFO, "INFO: ", "get jumped decode frame!!!!!!!!!!!!!!!");
	if(framequeue.NodeCount > 0)
	{
		for(int i=0 ; i<step_size && framequeue.NodeCount>0 ;i++){
			framequeue.FirstNode = framequeue.FirstNode->next ;  //��һ��ָ�������
			framequeue.NodeCount-- ;
		}
		return get_a_frame(env,jPixel);
	}
	else
		return -1;
}

//��ȡ���ʱ��ʱ�̵�������
JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_Get_1throughput(JNIEnv * env , jclass jclazz)
{
	int throughput = throughput_count ;
	throughput_count = 0;   //���غ󣬸�ֵ0

	return throughput;
}

//��ȡ���ʱ��ʱ�̵�������2
JNIEXPORT jint JNICALL Java_com_javamedia_NativeHandler_Get_1throughput2 (JNIEnv * env, jclass jclazz)
{
	int throughput = throughput_count2 ;
	throughput_count2 = 0;   //���غ󣬸�ֵ0

	return throughput;
}



/*
//��ȡ���ʱ����е���ƵƬ�ε�����
JNIEXPORT jdouble JNICALL Java_com_example_mplayer_NativeHandler_Get_1segmentCount(JNIEnv * env, jclass jclazz)
{
	double segment_count = FrameBuffer_count;	// / 204 ;   //ÿ����Ƶ����204��֡�����ڲ��ţ���sps+pps+key֡����һ��֡��
	FrameBuffer_count = 0 ;      //ÿ����ʱ���ȡֵһ�Σ�����ֵΪ0
	//return segment_count ;
	return framequeue.NodeCount/204.0;    //֡���е�ȫ�����Ȼ����segment�ĸ���
}
*/

/*
//��ȡ���ʱ����е���������ƵƬ�ε�����
JNIEXPORT jdouble JNICALL Java_com_example_mplayer_NativeHandler_Get_1streamedSegment(JNIEnv * env, jclass jclazz)
{
	double Segment_count = streamedSegment_count/205.0;   // / 205 ;   //ÿ����Ƶ���ڽ��ն���205��Nalu
	streamedSegment_count = 0 ;    //ÿ����ʱ���ȡֵһ�Σ�����ֵΪ0
	return Segment_count ;

}
*/

