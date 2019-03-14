/**
 * ������һЩ��Ҫ�õ��Ľṹ��
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <memory.h>
#include <assert.h>
#include <stddef.h>
//#include <conio.h>


typedef struct RTPpacket_Node  //RTP���ݰ��ڵ�
{
	uint32_t timestamp;   //RTP���ݰ���ʱ���
	uint16_t suqenceNum;    //RTP���ݰ������к�
	unsigned char * payloadData;    //RTP���ݰ��ĸ���
	size_t payloadlength;
	bool mark;
	struct RTPpacket_Node * next;   //ָ����һ��RTP���ݰ��Ľڵ�
	struct RTPpacket_Node * front;   //ָ��ǰһ��RTP���ݰ��ڵ�
};

typedef struct RTPpackets_queue  //rtp���ݰ��ڵ�
{
	unsigned int NodeCount;   //�����е��ܵĽڵ����
	struct RTPpacket_Node * FirstNode;   //���׽ڵ�
	struct RTPpacket_Node * EndNode;   //��β�ڵ�
};

typedef struct NALU_node  //nalu�ڵ�
{
	unsigned int nalu_length;     //nalu��Ԫ��ȫ������
	unsigned char * nalu_node;    //ָ��nalu�ڴ�
	struct NALU_node* next_nalu;   //ָ����һ��nalu
};

typedef struct NALU_queue  //NALU�ڵ����
{
	unsigned int NodeCount;   //�����е��ܵĽڵ����
	struct NALU_node * FirstNode;   //���׽ڵ�
	struct NALU_node * EndNode;   //��β�ڵ�
};

typedef struct Frame_node   //֡�ڵ�
{
	unsigned int frame_length;
	unsigned char * frame_data;  //ָ��֡����
	struct Frame_node * next;
};

typedef struct Frame_queue   //֡����
{
	unsigned int NodeCount;
	struct Frame_node * FirstNode;
	struct Frame_node * EndNode;
};







