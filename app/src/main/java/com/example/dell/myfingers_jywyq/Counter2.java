package com.example.dell.myfingers_jywyq;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/12/1.
 */

public class Counter2 {
    private int num=0;
    private int lastshownum=0;
    private Mat mFlipRgba;
    //private Mat mTransposeRgba;
    private Handler handler;
    public int CountNum(Mat mRgba){
        Log.i("TAG","Channels : "+ mRgba.channels());
        if(mFlipRgba==null)mFlipRgba = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1, new Scalar(4));
        //if(mTransposeRgba==null)mTransposeRgba = new Mat(mRgba.height(), mRgba.width(), CvType.CV_8UC1, new Scalar(4));
        if(handler==null)handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.d("test.....cont.....",num+"");
//                Toast.makeText(OpenCameraActivity.this,lastshownum,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        ArrayList<MatOfPoint> contours=new ArrayList<MatOfPoint>();
        //Imgproc.resize(mTransposeRgba,mFlipRgba,mFlipRgba.size() , 0.0D, 0.0D, 0);
        Imgproc.resize(mRgba,mFlipRgba,mFlipRgba.size() , 0.0D, 0.0D, 0);
        Imgproc.cvtColor(mFlipRgba, mRgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(mRgba, mRgba, 100, 255, Imgproc.THRESH_BINARY);
        Imgproc.cvtColor(mRgba, mFlipRgba, Imgproc.COLOR_GRAY2RGBA, 4);
        Mat hierarchy = new Mat();
        //转换矩阵的数据类型
        hierarchy.convertTo(hierarchy, CvType.CV_32SC1);
        //定义轮廓抽取模式
        //zhichaozhaowaiweilunkuo
        int mode = Imgproc.RETR_EXTERNAL;
        //定义轮廓识别方法,边缘近似方法
        //将所有连码点转换成点
        int method = Imgproc.CHAIN_APPROX_NONE;
        //Log.d("test.....","here");
        Imgproc.findContours(mRgba, contours,hierarchy,mode,method);
        //Log.d("test.....","here2");

        num=contours.size();
        double x=0;
        double y=0;
        for (int k=0; k < contours.size(); k++){
            Point[] ap=contours.get(k).toArray();
            x=x+ap[0].x;
            y=y+ap[0].y;
        }
        //Point cenPoint=new Point(x/contours.size(),y/contours.size());
        ArrayList<MatOfInt> hull=new ArrayList<MatOfInt>();
        ArrayList<MatOfInt4> dis=new ArrayList<MatOfInt4>();
        int shownum=0;
        int max=0;
        lastshownum=0;
        for (int k=0; k < contours.size(); k++){
            MatOfInt matint=new MatOfInt();
            //凸包
            Imgproc.convexHull(contours.get(k), matint,true);
            hull.add(matint);

        }
        for (int k=0; k < contours.size(); k++){
            try {
                MatOfInt4 matint4=new MatOfInt4();
                //凸缺陷
                Imgproc.convexityDefects(contours.get(k), hull.get(k),matint4);
                List<Integer> cdList = matint4.toList();

                shownum=0;
                for (int i=0;i<cdList.size();i++){
                    if (i%4==3&&cdList.get(i)>40500){
//                        Point[] a1=contours.get(cdList.get(i-3)).toArray();
//                        Double x1=a1[0].x;
//                        Double y1=a1[0].y;
//                        Point[] a2=contours.get(cdList.get(i-2)).toArray();
//                        Double x2=a2[0].x;
//                        Double y2=a2[0].y;
//                        Point[] a3=contours.get(cdList.get(i-1)).toArray();
//                        Double x3=a3[0].x;
//                        Double y3=a3[0].y;
//                        if ((x1-x3)*(x2-x3)+(y1-y3)*(y2-y3)>0){
//                            shownum++;
//                        }
//                        找到最大值
//                        if (cdList.get(i)>max){
//                            max=cdList.get(i);
//                        }

                        shownum++;
                    }
                }
//                for (int i=0;i<cdList.size();i++){
////                    防止max为0的时候
//                    if (max==0){
//                        break;
//                    }
//                    if (i%4==3&&max*1.0/cdList.get(i)<2){
//                        shownum++;
//                    }
//                }
//                //        手指比凹槽+1
//                shownum++;


                if (shownum>lastshownum){
                    lastshownum=shownum;
                }

            }catch (Exception e){

            }

        }
        return lastshownum;
    }
}