package com.example.administrator.virtualinstrument;

/**
 * Created by Chengzhi_Huang on 2016/3/30.
 */
import org.opencv.core.Mat;
public class DetectionBasedTracker {
    public static int[] returnXYCoordinate(Mat mat){
        return nativeReturnXYCoordibate(mat.getNativeObjAddr());
    }
    private static native int[] nativeReturnXYCoordibate(long mat);

    //color == 0 ,purple;color == 1,blue;color == 2,red
    public static void ChangeColor(int color){
        nativeChangeColor(color);
    }
    private static native boolean nativeChangeColor(int color);
}
