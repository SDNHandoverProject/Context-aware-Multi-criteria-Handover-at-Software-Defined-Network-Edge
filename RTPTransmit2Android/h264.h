// MPEG2RTP.h
#include <stdio.h>
#include <stdlib.h>
#include <conio.h>
#include <string.h>

#include <winsock2.h>
#include <winsock2.h>


//#define PACKET_BUFFER_END            (unsigned int)0x00000000
//



/*typedef struct 
{
   
    unsigned char csrc_len:4;        // expect 0
    unsigned char extension:1;        // expect 1, see RTP_OP below 
    unsigned char padding:1;        // expect 0 
    unsigned char version:2;        // expect 2 
  
    unsigned char payload:7;        // RTP_PAYLOAD_RTSP 
    unsigned char marker:1;        //expect 1 
  
    unsigned short seq_no;            
    
    unsigned  long timestamp;       // bytes 4-7    
   
    unsigned long ssrc;            // bytes 8-11 stream number is used here.
} RTP_FIXED_HEADER;
*/

typedef struct
{
  int startcodeprefix_len;      //! 4 for parameter sets and first slice in picture, 3 for everything else (suggested)
  unsigned len;                 //! Length of the NAL unit (Excluding the start code, which does not belong to the NALU)
  unsigned max_size;            //! Nal Unit Buffer size
  int forbidden_bit;            //! should be always FALSE   
  int nal_reference_idc;        //! NALU_PRIORITY_xxxx
  int nal_unit_type;            //! NALU_TYPE_xxxx    
  char *buf;                    //! contains the first byte followed by the EBSP
  unsigned short lost_packets;  //! true, if packet loss is detected
} NALU_t;


typedef struct {
    //byte 0
	unsigned char TYPE:5;
    unsigned char NRI:2;
	unsigned char F:1;    
} NALU_HEADER; /**//* 1 BYTES */


typedef struct {
    //byte 0
    unsigned char TYPE:5;
	unsigned char NRI:2; 
	unsigned char F:1;    
} FU_INDICATOR; /**//* 1 BYTES */


typedef struct {
    //byte 0
    unsigned char TYPE:5;
	unsigned char R:1;
	unsigned char E:1;
	unsigned char S:1;    
} FU_HEADER; /**//* 1 BYTES */

