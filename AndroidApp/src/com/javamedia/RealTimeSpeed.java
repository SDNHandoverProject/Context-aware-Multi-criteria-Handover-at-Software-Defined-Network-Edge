package com.javamedia;

public class RealTimeSpeed implements Runnable {

	private int TimeSlot = 2;
	private double NetSpeed;
	
	public double getNetSpeed() {
		return NetSpeed;
	}

	@Override
	public void run() {
		// TODO �Զ����ɵķ������
		while(true)
		{
			try {Thread.sleep(TimeSlot * 1000);}catch(InterruptedException e){e.printStackTrace();}   //�ȴ��룬ÿTimeSlot��ִ��һ��
			NetSpeed = (NativeHandler.Get_throughput2()/1024*1.0)/TimeSlot;
		}
		
	}

}
