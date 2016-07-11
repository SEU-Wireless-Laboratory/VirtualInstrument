////
//// Created by Chengzhi_Huang on 2016/3/30.
////
//
//#include "DetectionBasedTracker_jni.h"
////库资源
//#include<opencv/highgui.h>
//#include<opencv/cv.h>
//#include <opencv2/core/core.hpp>
//#include <opencv2/highgui/highgui.hpp>
//#include <opencv2/imgproc/imgproc.hpp>
//
//#include <android/log.h>
//#define LOG_TAG "virtualinstrument/DetectionBasedTracker"
//#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,__VA_ARGS__))
//
//#define FINGER_AREA 50    ///识别是手指按下的最小的面积
//#define CLICK_HEIGHT 320     ///具体指尖落下的位置，与y轴参数进行比较
//
//enum Instrument { PIANO = 200, DRUM, CYMBAL };
//
//int max_three(int a, int b, int c)
//{
//    int m;
//    if (a>b) m = a;
//    else m = b;
//    if (c>m) m = c;
//    return m;
//}
//int min_three(int a, int b, int c) {
//    int m;
//    if (a<b) m = a;
//    else m = b;
//    if (c<m) m = c;
//    return m;
//}
//
//int h_region = 30;//30
//int s_region = 102;
//int v_region = 100;
/////HSV颜色模型的三个要素
//int hue = 144;  ///色调
//int saturation = 208;///色饱和度
//int val = 150;///明度
//
//bool play = false;
//bool last_state = false;
//bool find_finger = false;
//
//int counter = 0; // 記算停留時間，決定是否更換樂器
//
//Instrument insType = PIANO;
//
//int h_min, h_max, s_min, s_max, v_min, v_max;
//
//CvPoint line_leftPoint = cvPoint(0, 350);///坐标为整数的二维点，线的一个端点
//CvPoint line_rightPoint = cvPoint(800, 350);///线的另一端点
//CvPoint touchDetect_point = cvPoint(600, 400);
//
//CvScalar white_point = cvScalar(255, 0, 0);///BGR通道(数组)白色
//CvScalar gray_point = cvScalar(128, 0, 0);///灰色
//
//void HSV_ColorFilter(IplImage * src, IplImage * dst) {///函数的功能就是过滤颜色，剩下紫色区域
//    LOGD("Enter HSV_ColorFilter function");
//    for (int i = 0; i < src->height; i++)
//        for (int j = 0; j < src->width; j++) {
//            if (((uchar)src->imageData[i * src->widthStep + j * 3] >= h_min && (uchar)src->imageData[i * src->widthStep + j * 3] <= h_max) &&
//                ((uchar)src->imageData[i * src->widthStep + j * 3 + 1] >= s_min && (uchar)src->imageData[i * src->widthStep + j * 3 + 1] <= s_max) &&
//                ((uchar)src->imageData[i * src->widthStep + j * 3 + 2] >= v_min && (uchar)src->imageData[i * src->widthStep + j * 3 + 2] <= v_max))
//                dst->imageData[i * src->width + j] = 255;///255对应紫色
//            else
//                dst->imageData[i * src->width + j] = 0;
//
//        } // for
//    LOGD("Finish HSV_ColorFilter function");
//} // HSV_ColorFilter
//
//void Init() {
//    LOGD("Enter Init function");
//    h_min = hue - h_region;
//    h_max = hue + h_region;
//    s_min = saturation - s_region;
//    s_max = saturation + s_region;
//    v_min = val - v_region;
//    v_max = val + v_region;
//
//    if (h_min < 0) h_min = 0;
//    if (h_max > 180) h_max = 180;
//    if (s_min < 0) s_min = 0;
//    if (s_max > 255) s_max = 255;
//    if (v_min < 0) v_min = 0;
//    if (v_max > 255) v_max = 255;
//    LOGD("Finish Init function");
//} // Init()
//
//
//JNIEXPORT jintArray JNICALL Java_com_example_administrator_virtualinstrument_DetectionBasedTracker_nativeReturnXYCoordibate
//        (JNIEnv * jenv, jclass thiz, jlong imageGray) {
//    Init();
//    if(!(cv::Mat*)imageGray){
//        exit(0);
//        LOGD("123");
//    }
//    cv::Mat input_image=*((cv::Mat*)imageGray);//因为using namespace cv和windows.h中ACCESS_MASK定义冲突
//    IplImage * hsvFiltered;
//    IplImage frame(input_image);
//    hsvFiltered = cvCreateImage(cvGetSize(&frame), (&frame)->depth, 1);    ///创建首地址并分配存储空间(宽和高，图像元素的位深度，每个元素（像素）的通道数)
//    //并且是灰度图的空间
//
//    CvMemStorage * storage = cvCreateMemStorage();   ///用来创建一个内存存储器，来统一管理各种动态对象的内存。
//    CvSeq * contour;/// 定义了一个结构体
//    CvRect aRect = CvRect(0, 0, 10, 10) ;    ///通过定义矩形左上角坐标和矩形的宽和高来确定一个矩形
//    //CvPoint fingerTip = cvPoint(0, 0);
//    CvContourScanner scanner;    ///初始化轮廓扫描器
//
//    HSV_ColorFilter(&frame, hsvFiltered); // 過濾出指定顏色
//
//    cvErode(hsvFiltered, hsvFiltered, 0, 1); //cvErode(輸入圖像,輸出圖像,element,腐蝕的次數);element：用于腐蚀的结构元素。若为 NULL, 则使用 3×3 长方形的结构元素
//    //原3次
//
//    LOGD("After cvErode");
//    find_finger = false;
//    scanner = cvStartFindContours(hsvFiltered, storage);
//    int fingerAll[20];//[0,2*index-1]是具体的坐标
//    int index=0;//表示fingerAll里面有多少个点，
//    //取得影像的一帧后，过滤出指尖的颜色，然后由cvErode腐蚀找到颜色的位置，多次侵蚀找到准确的形状。
//    while ((contour = cvFindNextContour(scanner)) != NULL   /*&& find_finger*/ ) {   ///确定和提取图像的下一个轮廓，并且返回它的指针
//
//        LOGD("After FindNextContour");
//        aRect = cvBoundingRect(contour,
//                               0);///计算点集的最外面（up-right）矩形边界.一是侦测背部紫色区域的高和宽，侦测时需要立起来；二是侦测手指紫色区域（通过轮廓面积自动区分）
//        if (aRect.width * aRect.height >= FINGER_AREA) { // 可以用來去除雜訊，确认是手指出现
//            fingerAll[index*2] = aRect.x + (aRect.width / 2);   ///确定指尖的位置，以左下为基点
//            //fingerAll[index*2+1] = aRect.y + (aRect.height * 7) / 8;
//            fingerAll[index*2+1] = aRect.y;//改为取最下方的点，而不是正中心
//            index++;
//
////            if (aRect.width * aRect.height > 20000) { // 要更換樂器
////                if (counter < 60) counter++;
////                else {
////                    if (aRect.width * aRect.height > 280000) insType = PIANO;//每张纸片下面的紫色区域大小不同
////
////                    else if (aRect.width * aRect.height > 150000) insType = DRUM;
////                    else if (aRect.width * aRect.height > 20000) insType = CYMBAL;
////
////                    counter = -60;
////                } // else
////
////
////            } // if
////            else counter = 0;
//
////            cvCircle(hsvFiltered, fingerTip, 20, gray_point, -1, CV_AA, 0);     ///功能是寻找手指头；绘制一个圆形区域的灰阶（图像，圆心坐标，圆形半径，线条颜色，，，）
//            //find_finger = true;
//        } // if
//
//    } // while
//    LOGD("Before cvEndFindContours");
//    contour = cvEndFindContours(&scanner);   ///结束扫描过程
//    LOGD("Begin returning");
//
//    jintArray temp=jenv->NewIntArray(2*index);
//    jenv->SetIntArrayRegion(temp,0,2*index,fingerAll);
//    return temp;
//}
//
//JNIEXPORT jboolean JNICALL Java_com_example_administrator_virtualinstrument_DetectionBasedTracker_nativeChangeColor
//(JNIEnv * jenv, jclass thiz, jint _color) {
//    //color == 0 ,purple;color == 1,blue;color == 2,red
//    int color=(int)_color;
//    if(color == 1){
//        LOGD("Change color to Blue");
//        h_region =  5;
//        s_region = 102;
//        v_region = 80;
//        hue =88 ;
//        saturation = 188;///色饱和度
//        val = 150;///明度
//    }else if(color == 2){
//        LOGD("Change color to Red");
//        h_region =  10;
//        s_region = 102;
//        v_region = 80;
//        hue = 134 ;  //调
//        saturation = 188;///色饱和度
//        val = 150;///明度
//    }else if(color == 0){
//        LOGD("Change color to Purple");
//        h_region = 15;//30
//        s_region = 102;
//        v_region = 80;
//        hue = 154;  ///色调
//        saturation = 188;///色饱和度
//        val = 180;///明度
//    }
//    return (jboolean)true;
//}
//
