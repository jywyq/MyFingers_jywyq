package com.example.dell.myfingers_jywyq;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;


/**
 * Created by dell on 2016/11/30.
 */

public class Counter {

    private Bitmap changeBitmap(Mat rgba) {
        Bitmap dst = Bitmap.createBitmap(rgba.width(), rgba.height(), Bitmap.Config.ARGB_8888);
        //Log.i(TAG,"width: "+rgba.width()+" height: "+rgba.height());
        //现在只差dst的初始化
        Utils.matToBitmap(rgba,dst);
        return dst;
    }

    public int CountNum(Mat rgba){
        Bitmap dst = changeBitmap(rgba);
        Bitmap bitmap = changeBitmapContrastBrightness(dst, (float) .4, 200);
        int numFingers = numFingers(bitmap);
        return numFingers;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }
    public int numFingers(Bitmap img)
    {
        int x1p = 710, x2p = 1450, y1p = 1600, y2p = 2100;
        int x1t = 1610, x2t = 1870, y1t = 1340, y2t = 1410;
        int x1f = 1350, x2f = 1500, y1f = 1180, y2f = 700;
        int x1m = 1090, x2m = 1260, y1m = 650, y2m = 1090;
        int x1r = 890, x2r = 1020, y1r = 770, y2r = 1200;
        int x1pi = 630, x2pi = 750, y1pi = 1100, y2pi = 1260;

        int numMatches = 0;
        double xxx = 0.5;
        double yyy = 0.3;
        int palm   = majorColor(img, (int)(x1p*xxx) , (int)(x2p*xxx) , (int)(y1p*yyy) , (int)(y2p*yyy)) ;
        int thumb  = majorColor(img, (int)(x1t*xxx) , (int)(x2t*xxx) , (int)(y1t*yyy) , (int)(y2t*yyy)) ;
        int fore   = majorColor(img, (int)(x1f*xxx) , (int)(x2f*xxx) , (int)(y1f*yyy) , (int)(y2f*yyy)) ;
        int middle = majorColor(img, (int)(x1m*xxx) , (int)(x2m*xxx) , (int)(y1m*yyy) , (int)(y2m*yyy)) ;
        int ring   = majorColor(img, (int)(x1r*xxx) , (int)(x2r*xxx) , (int)(y1r*yyy) , (int)(y2r*yyy)) ;
        int pinkie = majorColor(img, (int)(x1pi*xxx), (int)(x2pi*xxx), (int)(y1pi*yyy), (int)(y2pi*yyy));
		/*
		System.out.println("Major color: "+Integer.toHexString(palm));
		System.out.println("Major color: "+Integer.toHexString(thumb));
		System.out.println("Major color: "+Integer.toHexString(fore));
		System.out.println("Major color: "+Integer.toHexString(middle));
		System.out.println("Major color: "+Integer.toHexString(ring));
		System.out.println("Major color: "+Integer.toHexString(pinkie));
		*/
        if(isWithinRGBRange(palm, thumb))
        {
            numMatches++;
        }
        if(isWithinRGBRange(palm, fore))
        {
            numMatches++;
        }
        if(isWithinRGBRange(palm, middle))
        {
            numMatches++;
        }
        if(isWithinRGBRange(palm, ring))
        {
            numMatches++;
        }
        if(isWithinRGBRange(palm, pinkie))
        {
            numMatches++;
        }

        return numMatches;
    }
    public int majorColor(Bitmap img, int x1, int x2, int y1, int y2)
    {
        int rgb;
        int red;
        int green;
        int blue;

        int numberOfColors = 1;
        int[] colors = new int[1000];
        int[] numColors = new int[1000];
        for(int i = 0; i < 255; i++)
        {
            numColors[i] = 0;
        }
        colors[0] = img.getPixel(0,0);
        //System.out.println("first color: "+Integer.toHexString(colors[0]));
        numColors[0] = 1;

        for (int h = y1; h<y2; h++)
        {
            for (int w = x1; w<x2; w++)
            {
                rgb = img.getPixel(w, h);
                if(isNewColor(rgb, colors, numColors))
                {
                    numberOfColors ++;
                    if(numberOfColors <=1000)
                    {
                        colors[numberOfColors-1] = rgb;
                    }
                }
            }
        }
        return mostCommonColor(numColors, colors);
    }
    public static boolean isNewColor(int color, int[] colors, int[] numColors)
    {
        for(int i = 0; i<255; i++)
        {
            if(color == colors[i])
            {
                numColors[i]++;
                return false;
            }
        }
        //.out.println("New Color: "+Integer.toHexString(color));
        return true;
    }
    public int mostCommonColor(int[] numColors, int[] colors)
    {
        int most = 0;
        int mostIndex = - 99;
        for(int i = 0 ; i < 1000; i++)
        {
            if(numColors[i] > most)
            {
                most = numColors[i];
                mostIndex = i;
            }
        }
        int rgb = colors[mostIndex];
        int red = (rgb >> 16 ) & 0x000000FF;
        int green = (rgb >> 8 ) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;

        System.out.println("red: "+red+" green: "+green+" blue: "+green);
        return colors[mostIndex];
    }
    boolean isWithinRGBRange(int rgb1, int rgb2)
    {
        int tolerance = 42;

        int red1 = (rgb1 >> 16 ) & 0x000000FF;
        int green1 = (rgb1 >> 8 ) & 0x000000FF;
        int blue1 = (rgb1) & 0x000000FF;


        int red2 = (rgb2 >> 16 ) & 0x000000FF;
        int green2 = (rgb2 >> 8 ) & 0x000000FF;
        int blue2 = (rgb2) & 0x000000FF;

        int difR = Math.abs(red1-red2);
        int difG = Math.abs(green1-green2);
        int difB = Math.abs(blue1-blue2);

        if((difR < tolerance)&&(difG < tolerance)&&(difB < tolerance))
        {
            return true;
        }
        else
        {
            return false;
        }

    }
}
