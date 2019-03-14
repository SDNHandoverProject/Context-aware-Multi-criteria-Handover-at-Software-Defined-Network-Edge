package com.javamedia;

public class RealTimeSpeed implements Runnable {

	private int TimeSlot = 2;
	private double NetSpeed;
	
	public double getNetSpeed() {
		return NetSpeed;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		while(true)
		{
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //等待秒，每TimeSlot秒执行一次
			NetSpeed = (NativeHandler.Get_throughput2()/1024*1.0)/TimeSlot;
		}
		
	}

}
