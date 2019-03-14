/**
 * 这里是一些需要用到的结构体
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <memory.h>
#include <assert.h>
#include <stddef.h>
//#include <conio.h>


typedef struct RTPpacket_Node  //RTP数据包节点
{
	uint32_t timestamp;   //RTP数据包的时间戳
	uint16_t suqenceNum;    //RTP数据包的序列号
	unsigned char * payloadData;    //RTP数据包的负载
	size_t payloadlength;
	bool mark;
	struct RTPpacket_Node * next;   //指向下一个RTP数据包的节点
	struct RTPpacket_Node * front;   //指向前一个RTP数据包节点
};

typedef struct RTPpackets_queue  //rtp数据包节点
{
	unsigned int NodeCount;   //队列中的总的节点计数
	struct RTPpacket_Node * FirstNode;   //队首节点
	struct RTPpacket_Node * EndNode;   //队尾节点
};

typedef struct NALU_node  //nalu节点
{
	unsigned int nalu_length;     //nalu单元的全部长度
	unsigned char * nalu_node;    //指向nalu内存
	struct NALU_node* next_nalu;   //指向下一个nalu
};

typedef struct NALU_queue  //NALU节点对列
{
	unsigned int NodeCount;   //队列中的总的节点计数
	struct NALU_node * FirstNode;   //队首节点
	struct NALU_node * EndNode;   //队尾节点
};

typedef struct Frame_node   //帧节点
{
	unsigned int frame_length;
	unsigned char * frame_data;  //指向帧数据
	struct Frame_node * next;
};

typedef struct Frame_queue   //帧队列
{
	unsigned int NodeCount;
	struct Frame_node * FirstNode;
	struct Frame_node * EndNode;
};







