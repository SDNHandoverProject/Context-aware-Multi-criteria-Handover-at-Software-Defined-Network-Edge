/*

  This file is a part of JRTPLIB
  Copyright (c) 1999-2007 Jori Liesenborgs

  Contact: jori.liesenborgs@gmail.com

  This library was developed at the "Expertisecentrum Digitale Media"
  (http://www.edm.uhasselt.be), a research center of the Hasselt University
  (http://www.uhasselt.be). The library is based upon work done for 
  my thesis at the School for Knowledge Technology (Belgium/The Netherlands).

  Permission is hereby granted, free of charge, to any person obtaining a
  copy of this software and associated documentation files (the "Software"),
  to deal in the Software without restriction, including without limitation
  the rights to use, copy, modify, merge, publish, distribute, sublicense,
  and/or sell copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included
  in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
  IN THE SOFTWARE.

*/

/**
 * \file rtpsession.h
 */

#ifndef RTPSESSION_H

#define RTPSESSION_H

//#include "rtpconfig.h"
#include "rtplibraryversion.h"
#include "rtppacketbuilder.h"
#include "rtpsessionsources.h"
#include "rtptransmitter.h"
#include "rtpcollisionlist.h"
#include "rtcpscheduler.h"
#include "rtcppacketbuilder.h"
#include "rtptimeutilities.h"
#include "rtcpcompoundpacketbuilder.h"
#include "rtpmemoryobject.h"
#include <list>

#ifdef RTP_SUPPORT_THREAD
	#include "jmutex.h"	
#endif // RTP_SUPPORT_THREAD

class RTPTransmitter;
class RTPSessionParams;
class RTPTransmissionParams;
class RTPAddress;
class RTPSourceData;
class RTPPacket;
class RTPPollThread;
class RTPTransmissionInfo;
class RTCPCompoundPacket;
class RTCPPacket;
class RTCPAPPPacket;

/** High level class for using RTP.
 *  For most RTP based applications, the RTPSession class will probably be the one to use. It handles 
 *  the RTCP part completely internally, so the user can focus on sending and receiving the actual data.
 *  \note The RTPSession class is not meant to be thread safe. The user should use some kind of locking 
 *        mechanism to prevent different threads from using the same RTPSession instance.
 */
class RTPSession : public RTPMemoryObject
{
public:
	/** Constructs an RTPSession instance, optionally installing a memory manager. */
	RTPSession(RTPMemoryManager *mgr = 0);
	virtual ~RTPSession();
	
	/** Creates an RTP session.
	 *  This function creates an RTP session with parameters \c sessparams, which will use a transmitter 
	 *  corresponding to \c proto. Parameters for this transmitter can be specified as well. If \c
	 *  proto is of type RTPTransmitter::UserDefinedProto, the NewUserDefinedTransmitter function must
	 *  be implemented.
	 */
	int Create(const RTPSessionParams &sessparams,const RTPTransmissionParams *transparams = 0, RTPTransmitter::TransmissionProtocol proto = RTPTransmitter::IPv4UDPProto);

	/** Creates an RTP session using \c transmitter as transmission component.
	 *  This function creates an RTP session with parameters \c sessparams, which will use the
	 *  transmission component \c transmitter. Initialization and destruction of the transmitter
	 *  will not be done by the RTPSession class if this Create function is used. This function
	 *  can be useful if you which to reuse the transmission component in another RTPSession
	 *  instance, once the original RTPSession isn't using the transmitter anymore.
	 */
	int Create(const RTPSessionParams &sessparams,RTPTransmitter *transmitter);

	/** Leaves the session without sending a BYE packet. */
	void Destroy();

	/** Sends a BYE packet and leaves the session. 
	 *  Sends a BYE packet and leaves the session. At most a time \c maxwaittime will be waited to 
	 *  send the BYE packet. If this time expires, the session will be left without sending a BYE packet. 
	 *  The BYE packet will contain as reason for leaving \c reason with length \c reasonlength.
	 */
	void BYEDestroy(const RTPTime &maxwaittime,const void *reason,size_t reasonlength);

	/** Returns whether the session has been created or not. */
	bool IsActive();
	
	/** Returns our own SSRC. */
	uint32_t GetLocalSSRC();
	
	/** Adds \c addr to the list of destinations. */
	int AddDestination(const RTPAddress &addr);

	/** Deletes \c addr from the list of destinations. */
	int DeleteDestination(const RTPAddress &addr);

	/** Clears the list of destinations. */
	void ClearDestinations();

	/** Returns \c true if multicasting is supported. */
	bool SupportsMulticasting();

	/** Joins the multicast group specified by \c addr. */
	int JoinMulticastGroup(const RTPAddress &addr);

	/** Leaves the multicast group specified by \c addr. */
	int LeaveMulticastGroup(const RTPAddress &addr);

	/** Leaves all multicast groups. */
	void LeaveAllMulticastGroups();

	/** Sends the RTP packet with payload \c data which has length \c len.
	 *  Sends the RTP packet with payload \c data which has length \c len.
	 *  The used payload type, marker and timestamp increment will be those that have been set 
	 *  using the \c SetDefault member functions.
	 */
	int SendPacket(const void *data,size_t len);

	/** Sends the RTP packet with payload \c data which has length \c len.
	 *  It will use payload type \c pt, marker \c mark and after the packet has been built, the 
	 *  timestamp will be incremented by \c timestampinc.
	 */
	int SendPacket(const void *data,size_t len,
	                uint8_t pt,bool mark,uint32_t timestampinc);

	/** Sends the RTP packet with payload \c data which has length \c len.
	 *  The packet will contain a header extension with identifier \c hdrextID and containing data 
	 *  \c hdrextdata. The length of this data is given by \c numhdrextwords and is specified in a 
	 *  number of 32-bit words. The used payload type, marker and timestamp increment will be those that
	 *  have been set using the \c SetDefault member functions.
	 */
	int SendPacketEx(const void *data,size_t len,
	                  uint16_t hdrextID,const void *hdrextdata,size_t numhdrextwords);

	/** Sends the RTP packet with payload \c data which has length \c len.
	 *  It will use payload type \c pt, marker \c mark and after the packet has been built, the 
	 *  timestamp will be incremented by \c timestampinc. The packet will contain a header 
	 *  extension with identifier \c hdrextID and containing data \c hdrextdata. The length 
	 *  of this data is given by \c numhdrextwords and is specified in a number of 32-bit words.
	 */
	int SendPacketEx(const void *data,size_t len,
	                  uint8_t pt,bool mark,uint32_t timestampinc,
	                  uint16_t hdrextID,const void *hdrextdata,size_t numhdrextwords);
#ifdef RTP_SUPPORT_SENDAPP
	/** If sending of RTCP APP packets was enabled at compile time, this function creates a compound packet 
	 *  containing an RTCP APP packet and sends it immediately. 
	 *  If sending of RTCP APP packets was enabled at compile time, this function creates a compound packet 
	 *  containing an RTCP APP packet and sends it immediately. If successful, the function returns the number 
	 *  of bytes in the RTCP compound packet. Note that this immediate sending is not compliant with the RTP 
	 *  specification, so use with care. 
	 */
	int SendRTCPAPPPacket(uint8_t subtype, const uint8_t name[4], const void *appdata, size_t appdatalen);
#endif // RTP_SUPPORT_SENDAPP

	/** Sets the default payload type for RTP packets to \c pt. */
	int SetDefaultPayloadType(uint8_t pt);

	/** Sets the default marker for RTP packets to \c m. */
	int SetDefaultMark(bool m);

	/** Sets the default value to increment the timestamp with to \c timestampinc. */                         
	int SetDefaultTimestampIncrement(uint32_t timestampinc);

	/** This function increments the timestamp with the amount given by \c inc.
	 *  This function increments the timestamp with the amount given by \c inc. This can be useful 
	 *  if, for example, a packet was not sent because it contained only silence. Then, this function 
	 *  should be called to increment the timestamp with the appropriate amount so that the next packets 
	 *  will still be played at the correct time at other hosts.
	 */
	int IncrementTimestamp(uint32_t inc);

	/** This function increments the timestamp with the amount given set by the SetDefaultTimestampIncrement
	 *  member function. 
	 *  This function increments the timestamp with the amount given set by the SetDefaultTimestampIncrement
	 *  member function. This can be useful if, for example, a packet was not sent because it contained only silence.
	 *  Then, this function should be called to increment the timestamp with the appropriate amount so that the next
	 *  packets will still be played at the correct time at other hosts.	
	 */
	int IncrementTimestampDefault();

	/** This function allows you to inform the library about the delay between sampling the first 
	 *  sample of a packet and sending the packet.
	 *  This function allows you to inform the library about the delay between sampling the first
	 *  sample of a packet and sending the packet. This delay is taken into account when calculating the 
	 *  relation between RTP timestamp and wallclock time, used for inter-media synchronization.
	 */
	int SetPreTransmissionDelay(const RTPTime &delay);
	
	/** This function returns an instance of a subclass of RTPTransmissionInfo which will give some 
	 *  additional information about the transmitter (a list of local IP addresses for example).
	 *  This function returns an instance of a subclass of RTPTransmissionInfo which will give some 
	 *  additional information about the transmitter (a list of local IP addresses for example). The user
	 *  has to free the returned instance when it is no longer needed, preferably using the DeleteTransmissionInfo
	 *  function.
	 */
	RTPTransmissionInfo *GetTransmissionInfo();

	/** Frees the memory used by the transmission information \c inf. */
	void DeleteTransmissionInfo(RTPTransmissionInfo *inf);

	/** If you're not using the poll thread, this function must be called regularly to process incoming data
	 *  and to send RTCP data when necessary.
	 */
	int Poll();

	/** Waits at most a time \c delay until incoming data has been detected. 
	 *  Waits at most a time \c delay until incoming data has been detected. Only works when you're not 
	 *  using the poll thread. If \c dataavailable is not \c NULL, it should be set to \c true if data 
	 *  was actually read and to \c false otherwise.
	 */
	int WaitForIncomingData(const RTPTime &delay,bool *dataavailable = 0);

	/** If the previous function has been called, this one aborts the waiting (only works when you're not 
	 *  using the poll thread).
	 */
	int AbortWait();

	/** Returns the time interval after which an RTCP compound packet may have to be sent (only works when 
	 *  you're not using the poll thread.
	 */
	RTPTime GetRTCPDelay();

	/** The following member functions (till EndDataAccess}) need to be accessed between a call 
	 *  to BeginDataAccess and EndDataAccess. 
	 *  The BeginDataAccess function makes sure that the poll thread won't access the source table
	 *  at the same time that you're using it. When the EndDataAccess is called, the lock on the 
	 *  source table is freed again.
	 */
	int BeginDataAccess();

	/** Starts the iteration over the participants by going to the first member in the table. 
	 *  Starts the iteration over the participants by going to the first member in the table.
	 *  If a member was found, the function returns \c true, otherwise it returns \c false.
	 */
	bool GotoFirstSource();

	/** Sets the current source to be the next source in the table. 
	 *  Sets the current source to be the next source in the table. If we're already at the last 
	 *  source, the function returns \c false, otherwise it returns \c true.
	 */
	bool GotoNextSource();

	/** Sets the current source to be the previous source in the table.
	 *  Sets the current source to be the previous source in the table. If we're at the first source, 
	 *  the function returns \c false, otherwise it returns \c true.
	 */
	bool GotoPreviousSource();

	/** Sets the current source to be the first source in the table which has RTPPacket instances 
	 *  that we haven't extracted yet. 
	 *  Sets the current source to be the first source in the table which has RTPPacket instances 
	 *  that we haven't extracted yet. If no such member was found, the function returns \c false,
	 *  otherwise it returns \c true.
	 */
	bool GotoFirstSourceWithData();

	/** Sets the current source to be the next source in the table which has RTPPacket instances 
	 *  that we haven't extracted yet. 
	 *  Sets the current source to be the next source in the table which has RTPPacket instances 
	 *  that we haven't extracted yet. If no such member was found, the function returns \c false, 
	 *  otherwise it returns \c true.
	 */
	bool GotoNextSourceWithData();

	/** Sets the current source to be the previous source in the table which has RTPPacket 
	 *  instances that we haven't extracted yet. 
	 *  Sets the current source to be the previous source in the table which has RTPPacket 
	 *  instances that we haven't extracted yet. If no such member was found, the function returns \c false,
	 *  otherwise it returns \c true.
	 */
	bool GotoPreviousSourceWithData();

	/** Returns the \c RTPSourceData instance for the currently selected participant. */
	RTPSourceData *GetCurrentSourceInfo();

	/** Returns the \c RTPSourceData instance for the participant identified by \c ssrc, 
	 *  or NULL if no such entry exists.
	 */
	RTPSourceData *GetSourceInfo(uint32_t ssrc);

	/** Extracts the next packet from the received packets queue of the current participant,
	 *  or NULL if no more packets are available.
	 *  Extracts the next packet from the received packets queue of the current participant,
	 *  or NULL if no more packets are available. When the packet is no longer needed, its
	 *  memory should be freed using the DeletePacket member function.
	 */
	RTPPacket *GetNextPacket();

	/** Frees the memory used by \c p. */
	void DeletePacket(RTPPacket *p);

	/** See BeginDataAccess. */
	int EndDataAccess();
	
	/** Sets the receive mode to \c m.
	 *  Sets the receive mode to \c m. Note that when the receive mode is changed, the list of
	 *  addresses to be ignored ot accepted will be cleared.
	 */
	int SetReceiveMode(RTPTransmitter::ReceiveMode m);

	/** Adds \c addr to the list of addresses to ignore. */
	int AddToIgnoreList(const RTPAddress &addr);

	/** Deletes \c addr from the list of addresses to ignore. */
	int DeleteFromIgnoreList(const RTPAddress &addr);

	/** Clears the list of addresses to ignore. */
	void ClearIgnoreList();

	/** Adds \c addr to the list of addresses to accept. */
	int AddToAcceptList(const RTPAddress &addr);

	/** Deletes \c addr from the list of addresses to accept. */
	int DeleteFromAcceptList(const RTPAddress &addr);

	/** Clears the list of addresses to accept. */
	void ClearAcceptList();
	
	/** Sets the maximum allowed packet size to \c s. */
	int SetMaximumPacketSize(size_t s);

	/** Sets the session bandwidth to \c bw, which is specified in bytes per second. */
	int SetSessionBandwidth(double bw);

	/** Sets the timestamp unit for our own data.
	 *  Sets the timestamp unit for our own data. The timestamp unit is defined as a time interval in 
	 *  seconds divided by the corresponding timestamp interval. For example, for 8000 Hz audio, the 
	 *  timestamp unit would typically be 1/8000. Since this value is initially set to an illegal value, 
	 *  the user must set this to an allowed value to be able to create a session.
	 */
	int SetTimestampUnit(double u);
	
	/** Sets the RTCP interval for the SDES name item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES name item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */
	void SetNameInterval(int count);

	/** Sets the RTCP interval for the SDES e-mail item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES e-mail item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */	
	void SetEMailInterval(int count);
	
	/** Sets the RTCP interval for the SDES location item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES location item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */		
	void SetLocationInterval(int count);

	/** Sets the RTCP interval for the SDES phone item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES phone item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */	
	void SetPhoneInterval(int count);

	/** Sets the RTCP interval for the SDES tool item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES tool item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */	
	void SetToolInterval(int count);

	/** Sets the RTCP interval for the SDES note item.
	 *  After all possible sources in the source table have been processed, the class will check if other 
	 *  SDES items need to be sent. If \c count is zero or negative, nothing will happen. If \c count 
	 *  is positive, an SDES note item will be added after the sources in the source table have been 
	 *  processed \c count times.
	 */	
	void SetNoteInterval(int count);
	
	/** Sets the SDES name item for the local participant to the value \c s with length \c len. */
	int SetLocalName(const void *s,size_t len);
	
	/** Sets the SDES e-mail item for the local participant to the value \c s with length \c len. */
	int SetLocalEMail(const void *s,size_t len);

	/** Sets the SDES location item for the local participant to the value \c s with length \c len. */
	int SetLocalLocation(const void *s,size_t len);

	/** Sets the SDES phone item for the local participant to the value \c s with length \c len. */
	int SetLocalPhone(const void *s,size_t len);

	/** Sets the SDES tool item for the local participant to the value \c s with length \c len. */
	int SetLocalTool(const void *s,size_t len);

	/** Sets the SDES note item for the local participant to the value \c s with length \c len. */
	int SetLocalNote(const void *s,size_t len);

#ifdef RTPDEBUG
	void DumpSources();
	void DumpTransmitter();
#endif // RTPDEBUG
protected:
	/** Allocate a user defined transmitter.
	 *  In case you specified in the Create function that you want to use a
	 *  user defined transmitter, you should override this function. The RTPTransmitter 
	 *  instance returned by this function will then be used to send and receive RTP and 
	 *  RTCP packets. Note that when the session is destroyed, this RTPTransmitter 
	 *  instance will be destroyed as well.
 	 */
	virtual RTPTransmitter *NewUserDefinedTransmitter()						{ return 0; }
	
	/** Is called when an incoming RTCP packet is about to be processed. */
	virtual void OnRTPPacket(RTPPacket *pack,const RTPTime &receivetime,
	                         const RTPAddress *senderaddress) 					{ }

	/** Is called when an incoming RTCP packet is about to be processed. */
	virtual void OnRTCPCompoundPacket(RTCPCompoundPacket *pack,const RTPTime &receivetime,
	                                  const RTPAddress *senderaddress) 				{ }

	/** Is called when an SSRC collision was detected. 
	 *  Is called when an SSRC collision was detected. The instance \c srcdat is the one present in 
	 *  the table, the address \c senderaddress is the one that collided with one of the addresses 
	 *  and \c isrtp indicates against which address of \c srcdat the check failed.
	 */
	virtual void OnSSRCCollision(RTPSourceData *srcdat,const RTPAddress *senderaddress,bool isrtp)	{ }

	/** Is called when another CNAME was received than the one already present for source \c srcdat. */
	virtual void OnCNAMECollision(RTPSourceData *srcdat,const RTPAddress *senderaddress,
	                              const uint8_t *cname,size_t cnamelength)				{ }

	/** Is called when a new entry \c srcdat is added to the source table. */
	virtual void OnNewSource(RTPSourceData *srcdat)			 				{ }

	/** Is called when the entry \c srcdat is about to be deleted from the source table. */
	virtual void OnRemoveSource(RTPSourceData *srcdat)						{ }
	
	/** Is called when participant \c srcdat is timed out. */
	virtual void OnTimeout(RTPSourceData *srcdat)							{ }

	/** Is called when participant \c srcdat is timed after having sent a BYE packet. */
	virtual void OnBYETimeout(RTPSourceData *srcdat)						{ }

	/** Is called when an RTCP APP packet \c apppacket has been received at time \c receivetime 
	 *  from address \c senderaddress.
	 */
	virtual void OnAPPPacket(RTCPAPPPacket *apppacket,const RTPTime &receivetime,
	                         const RTPAddress *senderaddress)					{ }
	
	/** Is called when an unknown RTCP packet type was detected. */
	virtual void OnUnknownPacketType(RTCPPacket *rtcppack,const RTPTime &receivetime,
	                                 const RTPAddress *senderaddress)				{ }

	/** Is called when an unknown packet format for a known packet type was detected. */
	virtual void OnUnknownPacketFormat(RTCPPacket *rtcppack,const RTPTime &receivetime,
	                                   const RTPAddress *senderaddress)				{ }

	/** Is called when the SDES NOTE item for source \c srcdat has been timed out. */
	virtual void OnNoteTimeout(RTPSourceData *srcdat)						{ }

	/** Is called when a BYE packet has been processed for source \c srcdat. */
	virtual void OnBYEPacket(RTPSourceData *srcdat)							{ }

	/** Is called when an RTCP compound packet has just been sent (useful to inspect outgoing RTCP data). */
	virtual void OnSendRTCPCompoundPacket(RTCPCompoundPacket *pack)					{ }
#ifdef RTP_SUPPORT_THREAD
	/** Is called when error \c errcode was detected in the poll thread. */
	virtual void OnPollThreadError(int errcode)							{ }

	/** Is called each time the poll thread loops.
	 *  Is called each time the poll thread loops. This happens when incoming data was 
	 *  detected or when it's time to send an RTCP compound packet.
	 */
	virtual void OnPollThreadStep()									{ }
#endif // RTP_SUPPORT_THREAD
private:
	int InternalCreate(const RTPSessionParams &sessparams);
	int CreateCNAME(uint8_t *buffer,size_t *bufferlength,bool resolve);
	int ProcessPolledData();
	int ProcessRTCPCompoundPacket(RTCPCompoundPacket &rtcpcomppack,RTPRawPacket *pack);
	
	RTPTransmitter *rtptrans;
	bool created;
	bool deletetransmitter;
	bool usingpollthread;
	bool acceptownpackets;
	bool useSR_BYEifpossible;
	size_t maxpacksize;
	double sessionbandwidth;
	double controlfragment;
	double sendermultiplier;
	double byemultiplier;
	double membermultiplier;
	double collisionmultiplier;
	double notemultiplier;
	bool sentpackets;

	RTPSessionSources sources;
	RTPPacketBuilder packetbuilder;
	RTCPScheduler rtcpsched;
	RTCPPacketBuilder rtcpbuilder;
	RTPCollisionList collisionlist;

	std::list<RTCPCompoundPacket *> byepackets;
	
#ifdef RTP_SUPPORT_THREAD
	RTPPollThread *pollthread;
	JMutex sourcesmutex,buildermutex,schedmutex,packsentmutex;

	friend class RTPPollThread;
#endif // RTP_SUPPORT_THREAD
	friend class RTPSessionSources;
	friend class RTCPSessionPacketBuilder;
};

#endif // RTPSESSION_H

