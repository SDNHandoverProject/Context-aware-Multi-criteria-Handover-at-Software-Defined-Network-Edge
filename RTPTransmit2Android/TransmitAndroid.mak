# Microsoft Developer Studio Generated NMAKE File, Based on TransmitAndroid.dsp
!IF $(CFG)" == "
CFG=TransmitAndroid - Win32 Debug
!MESSAGE No configuration specified. Defaulting to TransmitAndroid - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "TransmitAndroid - Win32 Release" && "$(CFG)" != "TransmitAndroid - Win32 Debug"
!MESSAGE 指定的配置 "$(CFG)" 无效.
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
!ERROR An invalid configuration is specified.
!ENDIF 

!IF $(OS)" == "Windows_NT
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "TransmitAndroid - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# 开始自定义宏
OutDir=.\Release
# 结束自定义宏

ALL : "$(OUTDIR)\TransmitAndroid.dll"


CLEAN :
	-@erase "$(INTDIR)\rtcpapppacket.obj"
	-@erase "$(INTDIR)\rtcpbyepacket.obj"
	-@erase "$(INTDIR)\rtcpcompoundpacket.obj"
	-@erase "$(INTDIR)\rtcpcompoundpacketbuilder.obj"
	-@erase "$(INTDIR)\rtcppacket.obj"
	-@erase "$(INTDIR)\rtcppacketbuilder.obj"
	-@erase "$(INTDIR)\rtcprrpacket.obj"
	-@erase "$(INTDIR)\rtcpscheduler.obj"
	-@erase "$(INTDIR)\rtcpsdesinfo.obj"
	-@erase "$(INTDIR)\rtcpsdespacket.obj"
	-@erase "$(INTDIR)\rtcpsrpacket.obj"
	-@erase "$(INTDIR)\rtpcollisionlist.obj"
	-@erase "$(INTDIR)\rtpdebug.obj"
	-@erase "$(INTDIR)\rtperrors.obj"
	-@erase "$(INTDIR)\rtpinternalsourcedata.obj"
	-@erase "$(INTDIR)\rtpipv4address.obj"
	-@erase "$(INTDIR)\rtpipv6address.obj"
	-@erase "$(INTDIR)\rtplibraryversion.obj"
	-@erase "$(INTDIR)\rtppacket.obj"
	-@erase "$(INTDIR)\rtppacketbuilder.obj"
	-@erase "$(INTDIR)\rtppollthread.obj"
	-@erase "$(INTDIR)\rtprandom.obj"
	-@erase "$(INTDIR)\rtpsession.obj"
	-@erase "$(INTDIR)\rtpsessionparams.obj"
	-@erase "$(INTDIR)\rtpsessionsources.obj"
	-@erase "$(INTDIR)\rtpsourcedata.obj"
	-@erase "$(INTDIR)\rtpsources.obj"
	-@erase "$(INTDIR)\rtptimeutilities.obj"
	-@erase "$(INTDIR)\rtpudpv4transmitter.obj"
	-@erase "$(INTDIR)\rtpudpv6transmitter.obj"
	-@erase "$(INTDIR)\Sender.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\TransmitAndroid.dll"
	-@erase "$(OUTDIR)\TransmitAndroid.exp"
	-@erase "$(OUTDIR)\TransmitAndroid.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /Fp"$(INTDIR)\TransmitAndroid.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\TransmitAndroid.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /incremental:no /pdb:"$(OUTDIR)\TransmitAndroid.pdb" /machine:I386 /out:"$(OUTDIR)\TransmitAndroid.dll" /implib:"$(OUTDIR)\TransmitAndroid.lib" 
LINK32_OBJS= \
	"$(INTDIR)\rtcpapppacket.obj" \
	"$(INTDIR)\rtcpbyepacket.obj" \
	"$(INTDIR)\rtcpcompoundpacket.obj" \
	"$(INTDIR)\rtcpcompoundpacketbuilder.obj" \
	"$(INTDIR)\rtcppacket.obj" \
	"$(INTDIR)\rtcppacketbuilder.obj" \
	"$(INTDIR)\rtcprrpacket.obj" \
	"$(INTDIR)\rtcpscheduler.obj" \
	"$(INTDIR)\rtcpsdesinfo.obj" \
	"$(INTDIR)\rtcpsdespacket.obj" \
	"$(INTDIR)\rtcpsrpacket.obj" \
	"$(INTDIR)\rtpcollisionlist.obj" \
	"$(INTDIR)\rtpdebug.obj" \
	"$(INTDIR)\rtperrors.obj" \
	"$(INTDIR)\rtpinternalsourcedata.obj" \
	"$(INTDIR)\rtpipv4address.obj" \
	"$(INTDIR)\rtpipv6address.obj" \
	"$(INTDIR)\rtplibraryversion.obj" \
	"$(INTDIR)\rtppacket.obj" \
	"$(INTDIR)\rtppacketbuilder.obj" \
	"$(INTDIR)\rtppollthread.obj" \
	"$(INTDIR)\rtprandom.obj" \
	"$(INTDIR)\rtpsession.obj" \
	"$(INTDIR)\rtpsessionparams.obj" \
	"$(INTDIR)\rtpsessionsources.obj" \
	"$(INTDIR)\rtpsourcedata.obj" \
	"$(INTDIR)\rtpsources.obj" \
	"$(INTDIR)\rtptimeutilities.obj" \
	"$(INTDIR)\rtpudpv4transmitter.obj" \
	"$(INTDIR)\rtpudpv6transmitter.obj" \
	"$(INTDIR)\Sender.obj" \
	".\jrtplib.lib" \
	".\jthread.lib"

"$(OUTDIR)\TransmitAndroid.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "TransmitAndroid - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# 开始自定义宏
OutDir=.\Debug
# 结束自定义宏

ALL : "$(OUTDIR)\TransmitAndroid.dll"


CLEAN :
	-@erase "$(INTDIR)\rtcpapppacket.obj"
	-@erase "$(INTDIR)\rtcpbyepacket.obj"
	-@erase "$(INTDIR)\rtcpcompoundpacket.obj"
	-@erase "$(INTDIR)\rtcpcompoundpacketbuilder.obj"
	-@erase "$(INTDIR)\rtcppacket.obj"
	-@erase "$(INTDIR)\rtcppacketbuilder.obj"
	-@erase "$(INTDIR)\rtcprrpacket.obj"
	-@erase "$(INTDIR)\rtcpscheduler.obj"
	-@erase "$(INTDIR)\rtcpsdesinfo.obj"
	-@erase "$(INTDIR)\rtcpsdespacket.obj"
	-@erase "$(INTDIR)\rtcpsrpacket.obj"
	-@erase "$(INTDIR)\rtpcollisionlist.obj"
	-@erase "$(INTDIR)\rtpdebug.obj"
	-@erase "$(INTDIR)\rtperrors.obj"
	-@erase "$(INTDIR)\rtpinternalsourcedata.obj"
	-@erase "$(INTDIR)\rtpipv4address.obj"
	-@erase "$(INTDIR)\rtpipv6address.obj"
	-@erase "$(INTDIR)\rtplibraryversion.obj"
	-@erase "$(INTDIR)\rtppacket.obj"
	-@erase "$(INTDIR)\rtppacketbuilder.obj"
	-@erase "$(INTDIR)\rtppollthread.obj"
	-@erase "$(INTDIR)\rtprandom.obj"
	-@erase "$(INTDIR)\rtpsession.obj"
	-@erase "$(INTDIR)\rtpsessionparams.obj"
	-@erase "$(INTDIR)\rtpsessionsources.obj"
	-@erase "$(INTDIR)\rtpsourcedata.obj"
	-@erase "$(INTDIR)\rtpsources.obj"
	-@erase "$(INTDIR)\rtptimeutilities.obj"
	-@erase "$(INTDIR)\rtpudpv4transmitter.obj"
	-@erase "$(INTDIR)\rtpudpv6transmitter.obj"
	-@erase "$(INTDIR)\Sender.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\TransmitAndroid.dll"
	-@erase "$(OUTDIR)\TransmitAndroid.exp"
	-@erase "$(OUTDIR)\TransmitAndroid.ilk"
	-@erase "$(OUTDIR)\TransmitAndroid.lib"
	-@erase "$(OUTDIR)\TransmitAndroid.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "TRANSMITANDROID_EXPORTS" /Fp"$(INTDIR)\TransmitAndroid.pch" /YX /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC=rc.exe
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\TransmitAndroid.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib jrtplib.lib jthread.lib ws2_32.lib /nologo /dll /incremental:yes /pdb:"$(OUTDIR)\TransmitAndroid.pdb" /debug /machine:I386 /out:"$(OUTDIR)\TransmitAndroid.dll" /implib:"$(OUTDIR)\TransmitAndroid.lib" /pdbtype:sept 
LINK32_OBJS= \
	"$(INTDIR)\rtcpapppacket.obj" \
	"$(INTDIR)\rtcpbyepacket.obj" \
	"$(INTDIR)\rtcpcompoundpacket.obj" \
	"$(INTDIR)\rtcpcompoundpacketbuilder.obj" \
	"$(INTDIR)\rtcppacket.obj" \
	"$(INTDIR)\rtcppacketbuilder.obj" \
	"$(INTDIR)\rtcprrpacket.obj" \
	"$(INTDIR)\rtcpscheduler.obj" \
	"$(INTDIR)\rtcpsdesinfo.obj" \
	"$(INTDIR)\rtcpsdespacket.obj" \
	"$(INTDIR)\rtcpsrpacket.obj" \
	"$(INTDIR)\rtpcollisionlist.obj" \
	"$(INTDIR)\rtpdebug.obj" \
	"$(INTDIR)\rtperrors.obj" \
	"$(INTDIR)\rtpinternalsourcedata.obj" \
	"$(INTDIR)\rtpipv4address.obj" \
	"$(INTDIR)\rtpipv6address.obj" \
	"$(INTDIR)\rtplibraryversion.obj" \
	"$(INTDIR)\rtppacket.obj" \
	"$(INTDIR)\rtppacketbuilder.obj" \
	"$(INTDIR)\rtppollthread.obj" \
	"$(INTDIR)\rtprandom.obj" \
	"$(INTDIR)\rtpsession.obj" \
	"$(INTDIR)\rtpsessionparams.obj" \
	"$(INTDIR)\rtpsessionsources.obj" \
	"$(INTDIR)\rtpsourcedata.obj" \
	"$(INTDIR)\rtpsources.obj" \
	"$(INTDIR)\rtptimeutilities.obj" \
	"$(INTDIR)\rtpudpv4transmitter.obj" \
	"$(INTDIR)\rtpudpv6transmitter.obj" \
	"$(INTDIR)\Sender.obj" \
	".\jrtplib.lib" \
	".\jthread.lib"

"$(OUTDIR)\TransmitAndroid.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ENDIF 


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("TransmitAndroid.dep")
!INCLUDE "TransmitAndroid.dep"
!ELSE 
!MESSAGE Warning: cannot find "TransmitAndroid.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "TransmitAndroid - Win32 Release" || "$(CFG)" == "TransmitAndroid - Win32 Debug"
SOURCE=.\header\rtcpapppacket.cpp

"$(INTDIR)\rtcpapppacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpbyepacket.cpp

"$(INTDIR)\rtcpbyepacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpcompoundpacket.cpp

"$(INTDIR)\rtcpcompoundpacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpcompoundpacketbuilder.cpp

"$(INTDIR)\rtcpcompoundpacketbuilder.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcppacket.cpp

"$(INTDIR)\rtcppacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcppacketbuilder.cpp

"$(INTDIR)\rtcppacketbuilder.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcprrpacket.cpp

"$(INTDIR)\rtcprrpacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpscheduler.cpp

"$(INTDIR)\rtcpscheduler.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpsdesinfo.cpp

"$(INTDIR)\rtcpsdesinfo.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpsdespacket.cpp

"$(INTDIR)\rtcpsdespacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtcpsrpacket.cpp

"$(INTDIR)\rtcpsrpacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpcollisionlist.cpp

"$(INTDIR)\rtpcollisionlist.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpdebug.cpp

"$(INTDIR)\rtpdebug.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtperrors.cpp

"$(INTDIR)\rtperrors.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpinternalsourcedata.cpp

"$(INTDIR)\rtpinternalsourcedata.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpipv4address.cpp

"$(INTDIR)\rtpipv4address.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpipv6address.cpp

"$(INTDIR)\rtpipv6address.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtplibraryversion.cpp

"$(INTDIR)\rtplibraryversion.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtppacket.cpp

"$(INTDIR)\rtppacket.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtppacketbuilder.cpp

"$(INTDIR)\rtppacketbuilder.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtppollthread.cpp

"$(INTDIR)\rtppollthread.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtprandom.cpp

"$(INTDIR)\rtprandom.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpsession.cpp

"$(INTDIR)\rtpsession.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpsessionparams.cpp

"$(INTDIR)\rtpsessionparams.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpsessionsources.cpp

"$(INTDIR)\rtpsessionsources.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpsourcedata.cpp

"$(INTDIR)\rtpsourcedata.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpsources.cpp

"$(INTDIR)\rtpsources.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtptimeutilities.cpp

"$(INTDIR)\rtptimeutilities.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpudpv4transmitter.cpp

"$(INTDIR)\rtpudpv4transmitter.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\header\rtpudpv6transmitter.cpp

"$(INTDIR)\rtpudpv6transmitter.obj" : $(SOURCE) "$(INTDIR)"
	$(CPP) $(CPP_PROJ) $(SOURCE)


SOURCE=.\Sender.cpp

"$(INTDIR)\Sender.obj" : $(SOURCE) "$(INTDIR)"



!ENDIF 

