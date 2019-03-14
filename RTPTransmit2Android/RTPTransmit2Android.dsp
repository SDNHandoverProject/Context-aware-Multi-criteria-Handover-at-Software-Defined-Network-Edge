# Microsoft Developer Studio Project File - Name="RTPTransmit2Android" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=RTPTransmit2Android - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "RTPTransmit2Android.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "RTPTransmit2Android.mak" CFG="RTPTransmit2Android - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "RTPTransmit2Android - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "RTPTransmit2Android - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "RTPTransmit2Android - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "RTPTRANSMIT2ANDROID_EXPORTS" /YX /FD /c
# ADD CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "RTPTRANSMIT2ANDROID_EXPORTS" /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x804 /d "NDEBUG"
# ADD RSC /l 0x804 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386

!ELSEIF  "$(CFG)" == "RTPTransmit2Android - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "RTPTRANSMIT2ANDROID_EXPORTS" /YX /FD /GZ /c
# ADD CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "RTPTRANSMIT2ANDROID_EXPORTS" /YX /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x804 /d "_DEBUG"
# ADD RSC /l 0x804 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "RTPTransmit2Android - Win32 Release"
# Name "RTPTransmit2Android - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\rtcpapppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpbyepacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpcompoundpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpcompoundpacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcppacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcprrpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpscheduler.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpsdesinfo.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpsdespacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtcpsrpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpcollisionlist.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpdebug.cpp
# End Source File
# Begin Source File

SOURCE=.\rtperrors.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpinternalsourcedata.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpipv4address.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpipv6address.cpp
# End Source File
# Begin Source File

SOURCE=.\rtplibraryversion.cpp
# End Source File
# Begin Source File

SOURCE=.\rtppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\rtppacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\rtppollthread.cpp
# End Source File
# Begin Source File

SOURCE=.\rtprandom.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpsession.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpsessionparams.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpsessionsources.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpsourcedata.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpsources.cpp
# End Source File
# Begin Source File

SOURCE=.\rtptimeutilities.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpudpv4transmitter.cpp
# End Source File
# Begin Source File

SOURCE=.\rtpudpv6transmitter.cpp
# End Source File
# Begin Source File

SOURCE=.\Sender.cpp
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=.\basetsd.h
# End Source File
# Begin Source File

SOURCE=.\h264.h
# End Source File
# Begin Source File

SOURCE=.\javamedia_RTPTransmitAndroid.h
# End Source File
# Begin Source File

SOURCE=.\jmutex.h
# End Source File
# Begin Source File

SOURCE=.\jthread.h
# End Source File
# Begin Source File

SOURCE=.\rtcpapppacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpbyepacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpcompoundpacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpcompoundpacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\rtcppacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcppacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\rtcprrpacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpscheduler.h
# End Source File
# Begin Source File

SOURCE=.\rtcpsdesinfo.h
# End Source File
# Begin Source File

SOURCE=.\rtcpsdespacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpsrpacket.h
# End Source File
# Begin Source File

SOURCE=.\rtcpunknownpacket.h
# End Source File
# Begin Source File

SOURCE=.\rtpaddress.h
# End Source File
# Begin Source File

SOURCE=.\rtpcollisionlist.h
# End Source File
# Begin Source File

SOURCE=.\rtpconfig.h
# End Source File
# Begin Source File

SOURCE=.\rtpconfig_win.h
# End Source File
# Begin Source File

SOURCE=.\rtpdebug.h
# End Source File
# Begin Source File

SOURCE=.\rtpdefines.h
# End Source File
# Begin Source File

SOURCE=.\rtperrors.h
# End Source File
# Begin Source File

SOURCE=.\rtphashtable.h
# End Source File
# Begin Source File

SOURCE=.\rtpinternalsourcedata.h
# End Source File
# Begin Source File

SOURCE=.\rtpipv4address.h
# End Source File
# Begin Source File

SOURCE=.\rtpipv4destination.h
# End Source File
# Begin Source File

SOURCE=.\rtpipv6address.h
# End Source File
# Begin Source File

SOURCE=.\rtpipv6destination.h
# End Source File
# Begin Source File

SOURCE=.\rtpkeyhashtable.h
# End Source File
# Begin Source File

SOURCE=.\rtplibraryversion.h
# End Source File
# Begin Source File

SOURCE=.\rtpmemorymanager.h
# End Source File
# Begin Source File

SOURCE=.\rtpmemoryobject.h
# End Source File
# Begin Source File

SOURCE=.\rtppacket.h
# End Source File
# Begin Source File

SOURCE=.\rtppacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\rtppollthread.h
# End Source File
# Begin Source File

SOURCE=.\rtprandom.h
# End Source File
# Begin Source File

SOURCE=.\rtprawpacket.h
# End Source File
# Begin Source File

SOURCE=.\rtpsession.h
# End Source File
# Begin Source File

SOURCE=.\rtpsessionparams.h
# End Source File
# Begin Source File

SOURCE=.\rtpsessionsources.h
# End Source File
# Begin Source File

SOURCE=.\rtpsourcedata.h
# End Source File
# Begin Source File

SOURCE=.\rtpsources.h
# End Source File
# Begin Source File

SOURCE=.\rtpstructs.h
# End Source File
# Begin Source File

SOURCE=.\rtptimeutilities.h
# End Source File
# Begin Source File

SOURCE=.\rtptransmitter.h
# End Source File
# Begin Source File

SOURCE=.\rtptypes.h
# End Source File
# Begin Source File

SOURCE=.\rtptypes_win.h
# End Source File
# Begin Source File

SOURCE=.\rtpudpv4transmitter.h
# End Source File
# Begin Source File

SOURCE=.\rtpudpv6transmitter.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# End Target
# End Project
