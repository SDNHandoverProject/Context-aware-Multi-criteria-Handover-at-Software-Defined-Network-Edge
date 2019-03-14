package com.javamedia;

public class NativeHandler {
	
	/**
	 * JNI编译的处理底层C的java接口类。定义接口函数用于java代码和底层C代码的交互。
	 * 
	 * 初始话底层jrtplib的机制
	 * @param ServerIP 利用底层的JRTP的包接受数据时，要先初始化Jrtplib接收数据机制的，数据发送端的IP地址设置。
	 * @param ServerPort 数据发送端的Jrtplib的端口号。
	 * @param LocalPort Jrtplib本地接受数据的端口号
	 */
	public static native void nativeReceiver_init(String ServerIP,int ServerPort,int LocalPort);
	
	/**
	 * 开始Jrtplib底层数据接收
	 */
	public static native void nativeReceiver_start();
	  
	/**
	*  停止Jrtplib底层数据接收
	*/
	public static native void nativeReceiver_stop();
	
	/**
	 * 获取网络吞吐量
	 */
	public static native int Get_throughput();
	public static native int Get_throughput2();
	
	/**
	 * 释放数据帧解码器的资源。
	 */
	public static native void Decoder_release();
	
	/**
	 * 获取数据帧的数据： 宽度W
	 */
	public static native int get_W();
	
	/**
	 * 获取数据帧的数据： 高度H
	 */
	public static native int get_H();
	
	/**
	 * 获取数据帧队列的长度。
	 */
	public static native int get_FrameQueue_C();
	
	/**
	 * //闁兼儳鍢茶ぐ鍥р柦閳ヨ櫕鎲伴悷娆欑悼閻栨粓宕ュ绐U闂傚啰鍠庨崹顏堝极娴兼潙娅�
	 */
	public static native int Get_streamedSegment(); 
	
	public static native int Get_streamedSegment_tatol();
	
	/**
	 *从底层获取一个解码后的视频数据帧，数据放入byte数组
	 */
	public static native int get_a_DecodedFrame(byte[] FrameBuf);
	
	/**
	 * 该函数用于获取一个视频数据帧，但是是从当前位置向后跳step_size个帧再获取。其实就是快进一次，跳过setp_size个帧后播放
	 */
	public static native int get_a_Decoded_jumped_Frame(byte[] FrameBuf , int step_size);
	
	/**
	 * 用于加载./jni/Android.mk文件中最后编译的那个RTPJNI包
	 */
	static{
		System.loadLibrary("RTPJNI");
	}
	
}
