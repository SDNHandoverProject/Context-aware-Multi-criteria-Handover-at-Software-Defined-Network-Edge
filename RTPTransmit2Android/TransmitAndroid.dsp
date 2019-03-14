# Microsoft Developer Studio Project File - Name="TransmitAndroid" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=TransmitAndroid - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "TransmitAndroid.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "TransmitAndroid.mak" CFG="TransmitAndroid - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "TransmitAndroid - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "TransmitAndroid - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "TransmitAndroid - Win32 Release"

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
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /YX /FD /c
# ADD CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /YX /FD /c
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

!ELSEIF  "$(CFG)" == "TransmitAndroid - Win32 Debug"

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
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /YX /FD /GZ /c
# ADD CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /YX /FD /GZ /c
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

# Name "TransmitAndroid - Win32 Release"
# Name "TransmitAndroid - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Source File

SOURCE=.\header\rtcpapppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpbyepacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpcompoundpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpcompoundpacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcppacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcprrpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpscheduler.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsdesinfo.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsdespacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsrpacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpcollisionlist.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpdebug.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtperrors.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpinternalsourcedata.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv4address.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv6address.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtplibraryversion.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtppacket.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtppacketbuilder.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtppollthread.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtprandom.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpsession.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpsessionparams.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpsessionsources.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpsourcedata.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpsources.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtptimeutilities.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpudpv4transmitter.cpp
# End Source File
# Begin Source File

SOURCE=.\header\rtpudpv6transmitter.cpp
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

SOURCE=..\RTPTransmit2Android\h264.h
# End Source File
# Begin Source File

SOURCE=.\javamediaserver_RTPTransmitAndroid.h
# End Source File
# Begin Source File

SOURCE=.\header\jmutex.h
# End Source File
# Begin Source File

SOURCE=.\header\jthread.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpapppacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpbyepacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpcompoundpacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpcompoundpacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcppacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcppacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcprrpacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpscheduler.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsdesinfo.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsdespacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpsrpacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtcpunknownpacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpaddress.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpcollisionlist.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpconfig.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpconfig_win.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpdebug.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpdefines.h
# End Source File
# Begin Source File

SOURCE=.\header\rtperrors.h
# End Source File
# Begin Source File

SOURCE=.\header\rtphashtable.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpinternalsourcedata.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv4address.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv4destination.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv6address.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpipv6destination.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpkeyhashtable.h
# End Source File
# Begin Source File

SOURCE=.\header\rtplibraryversion.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpmemorymanager.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpmemoryobject.h
# End Source File
# Begin Source File

SOURCE=.\header\rtppacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtppacketbuilder.h
# End Source File
# Begin Source File

SOURCE=.\header\rtppollthread.h
# End Source File
# Begin Source File

SOURCE=.\header\rtprandom.h
# End Source File
# Begin Source File

SOURCE=.\header\rtprawpacket.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpsession.h
# End Source File
# Begin Source File

SOURCE=.\rtpsession.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpsessionparams.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpsessionsources.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpsourcedata.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpsources.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpstructs.h
# End Source File
# Begin Source File

SOURCE=.\header\rtptimeutilities.h
# End Source File
# Begin Source File

SOURCE=.\header\rtptransmitter.h
# End Source File
# Begin Source File

SOURCE=.\header\rtptypes.h
# End Source File
# Begin Source File

SOURCE=.\header\rtptypes_win.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpudpv4transmitter.h
# End Source File
# Begin Source File

SOURCE=.\header\rtpudpv6transmitter.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# Begin Source File

SOURCE=.\header\rtpconfig_unix.h.in
# End Source File
# Begin Source File

SOURCE=.\jrtplib.lib
# End Source File
# Begin Source File

SOURCE=.\jthread.lib
# End Source File
# End Target
# End Project
