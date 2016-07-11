package com.example.administrator.virtualinstrument;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;

/**
 * Created by Chengzhi_Huang on 2016/7/2.
 */
public class DrumPlay extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    protected static final String TAG="DrumPlay";
    private ImageView iv;
    private Mat mRgba;
    private Mat                    mGray;
    protected int color;//用来记录用户选择的是哪一个颜色
    private Point fingerTip;
    private PianoAcceleration Piano;
    private boolean play = false ;
    private static final Scalar FINGER_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    //因为鼓的位置有自定义，所以这里只是创建了变量
    private static int DRUMKEY[]={0,44,74,118,148,192};
    private DrumAcceleration Drum;

    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "opencv loaded successfully");
                    System.loadLibrary("ndk_jni_utils");
                    mOpenCvCameraView.setMaxFrameSize(200,200);
                    mOpenCvCameraView.enableView();
                    DetectionBasedTracker.ChangeColor(color);
                    break;
                default:
                    super.onManagerConnected(status);
                    break;

            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.play_interface);
        iv = (ImageView) findViewById(R.id.instrument);
        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        //这里的接收应该要改,就先用固定的了
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        color=bundle.getInt("color",0);
        BeforePlay();//为了播放音乐之前的准备
        Drum=new DrumAcceleration(DRUMKEY,25,12,70);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray=new Mat();
        mRgba=new Mat();
        initPlay();
    }
    private void initPlay(){
        Log.i(TAG, "playing drum");
        //iv.setImageResource(R.drawable.drum);
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mOpenCvCameraView!=null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,this,mLoaderCallback);
        }else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGRA2RGB);//从RGBA转到RGB
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_RGB2HSV);//从RGB转到HSV
        Core.flip(mRgba,mRgba,1);//最后的1表示水平翻转，0表示垂直翻转
        //DetectionBasedTracker.ChangeColor(color);
        int pointXY[]=DetectionBasedTracker.returnXYCoordinate(mRgba);
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_HSV2BGR);
        for(int i=0;i<pointXY.length;i=i+2) {
            Point topLeft = new Point(pointXY[i], pointXY[i + 1]);
            fingerTip = new Point();
            ////////////////////////////////////////
            //特别注意这里直接变成了从手机倒放的左上角的坐标
            fingerTip.x = topLeft.x;
            fingerTip.y = topLeft.y;
            //////////////////////////////////////////
            Imgproc.circle(mRgba, topLeft, 10, FINGER_RECT_COLOR);
            Log.i(TAG, fingerTip.x + "," + (fingerTip.y));
            int temp[]=Drum.setXY((int)fingerTip.x,(int)fingerTip.y);
            if(temp[0] <0){
                play=false;
            }
            else{
                play=true;
            }
            if (play) playDrum(temp[0],temp[1]);
        }
        return mRgba;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////////////////////////////////////////
        //在退出时释放所有资源
        soundPool.release();
        ////////////////////////////////////////
        mOpenCvCameraView.disableView();
    }
    private void playDrum(int key,int tone){
        key=key+8;
        playMusic(key,tone);
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent1=new Intent();
            intent1.setClass(DrumPlay.this, ChoiceInterface.class);
            DrumPlay.this.startActivity(intent1);
            DrumPlay.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //以下为混音器demo
    private SoundPool soundPool;
    private AudioAttributes attributes;
    private HashMap<Integer, Integer> soundPoolMap;
    private AudioManager mgr;

    private float streamVolumeMax, volume;

    //SuppressWarnings为了在编译API<=21的时候直接创建SoundPool时使用
    //这个函数要在播放音乐之前调用
    //所以选择在oncreate中调用一次，现在已经加进去了
    @SuppressWarnings("deprecation")
    public void BeforePlay(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //API>=21的时候用
            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(5)
                    .build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,50);
        }
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(8, soundPool.load(this, R.raw.cymbal, 1));
        soundPoolMap.put(9, soundPool.load(this, R.raw.drumside, 1));
        soundPoolMap.put(10, soundPool.load(this, R.raw.drum, 1));
        soundPoolMap.put(11, soundPool.load(this, R.raw.cymbal, 1));
        soundPoolMap.put(12, soundPool.load(this, R.raw.drum2, 1));
        mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//15
    }

    //volumnToPlay分7档音量
    public void playMusic(int tone,int volumnToPlay){
        volume = (volumnToPlay/streamVolumeMax);
        soundPool.play(soundPoolMap.get(tone), (float)volume, (float)volume, 1, 0, 1f);
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ,
        //参数：soundID：资源ID;leftVolume：左频道声音;rightVolume：右频道声音
        //loop：-1代表循环，0代表不循环;rate：值0.5-2.0设置1为正常,表示播放快慢
    }
    //混音器Demo结束
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
}
