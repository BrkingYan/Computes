package com.example.yy.computes;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.sql.Time;
import java.util.Arrays;
import java.util.List;


public class CalculateTask implements Runnable {

    private MainActivity.AlgorithmType mAlgorithmType;

    private int[] intData;
    private float[] floatData;
    private int N;

    private Handler mHandler;

    private static double fftTime;
    private static double mixTime;
    private static float fftCallTime;
    private static float mixCallTime;
    //private static int counter = 0;

    private static final int TYPE_FFT = 1;
    private static final int TYPE_MIX = 2;
    private static final int CALL_FFT = 3;
    private static final int CALL_MIX = 4;

    private static final int CALLTIME = 20;

    CalculateTask(MainActivity.AlgorithmType algorithmType,Handler handler,int[] data){
        this.mAlgorithmType = algorithmType;
        intData = Arrays.copyOf(data,data.length);
        N = data.length;
        floatData = null;
        mHandler = handler;
    }

    CalculateTask(MainActivity.AlgorithmType algorithmType,Handler handler,float[] data){
        this.mAlgorithmType = algorithmType;
        floatData = Arrays.copyOf(data,data.length);
        N = data.length;
        intData = null;
        mHandler = handler;
    }

    @Override
    public void run() {
        Log.d("task","into run");
        int count = 0;

        switch (mAlgorithmType){
            case JNI:
                if (intData!=null){
                    testFFT_Int();
                    testMix_Int();
                    testIntArrayCall();
                }else if (floatData != null){
                    testFFT_Float();
                    testMix_Float();
                    testFloatArrayCall();
                }
                break;
            case Neon:
                break;
        }


        Message fftMsg = Message.obtain(mHandler);
        fftMsg.what = TYPE_FFT;// 1
        fftMsg.obj = fftTime;
        mHandler.sendMessage(fftMsg);


        Message fftCallMsg = Message.obtain(mHandler);
        fftCallMsg.what = CALL_FFT;// 3
        fftCallMsg.obj = fftCallTime;
        mHandler.sendMessage(fftCallMsg);


        Message mixMsg = Message.obtain(mHandler);
        mixMsg.what = TYPE_MIX;// 2
        mixMsg.obj = mixTime;
        mHandler.sendMessage(mixMsg);


        Message mixCallMsg = Message.obtain(mHandler);
        mixCallMsg.what = CALL_MIX;// 4
        mixCallMsg.obj = mixCallTime;
        boolean sent = mHandler.sendMessage(mixCallMsg);
        Log.d("cal",sent + "");
    }

    static{
        System.loadLibrary("JNITest");
    }

    private void testFFT_Float(){

        float[] real = Arrays.copyOf(floatData,N);
        float[] img = new float[N];

        int c = 0;
        float sum = 0;
        while (c < CALLTIME){
            long start = System.nanoTime();
            fftTime = Algorithm.FFT_float_JNI(N,real,img);
            long end = System.nanoTime();
            sum += (float) ((end - start)/1000 - fftTime)/1000;
            c++;
        }
        Log.d("sum",sum + "");
        fftCallTime = sum / CALLTIME;
    }



    private void testFFT_Int(){
        int[] real = Arrays.copyOf(intData,N);
        int[] img = new int[N];

        int c = 0;
        float sum = 0;
        Algorithm.FFT_int_JNI(N,real,img);
        while (c < CALLTIME){
            long start = System.nanoTime();
            fftTime = Algorithm.FFT_int_JNI(N,real,img);
            long end = System.nanoTime();
            //Log.d("fft","fft : start:" + start + " end:" + end);
            sum += (float) ((end - start)/1000 - fftTime)/1000;
            c++;
        }
        fftCallTime = sum / CALLTIME;
    }

    private void testMix_Float(){
        float[] data = Arrays.copyOf(floatData,N);

        int c = 0;
        float sum = 0;
        Algorithm.mix_float_JNI(data);
        while (c < CALLTIME){
            long start = System.nanoTime();
            mixTime = Algorithm.mix_float_JNI(data);
            long end = System.nanoTime();
            sum += (float)((end - start)/1000 - mixTime)/1000;
            c++;
        }
        mixCallTime = sum / CALLTIME;
    }

    private void testMix_Int(){
        int[] data = Arrays.copyOf(intData,N);

        int c = 0;
        float sum = 0;
        while (c < CALLTIME){
            long start = System.nanoTime();
            mixTime = Algorithm.mix_int_JNI(data);
            long end = System.nanoTime();
            //Log.d("fft","mix : start:" + start + " end:" + end);
            sum += (float)((end - start)/1000 - mixTime)/1000;
            c++;
        }
        mixCallTime = sum / CALLTIME;
    }

    private void testFloatArrayCall(){
        float[] real = Arrays.copyOf(floatData,N);
        long start = System.nanoTime();
        Algorithm.doubleArrayCall(real);
        long end = System.nanoTime();
        long time = (end - start);
        Log.d("caltest","float time : " + time);
    }

    private void testIntArrayCall(){
        int[] real = Arrays.copyOf(intData,N);
        long start = System.nanoTime();
        Algorithm.intArrayCall(real);
        long end = System.nanoTime();
        long time = (end - start);
        Log.d("caltest","int time : " + time);
    }
}
