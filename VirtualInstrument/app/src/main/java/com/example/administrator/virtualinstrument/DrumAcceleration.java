package com.example.administrator.virtualinstrument;

/**
 * Created by Chengzhi_Huang on 2016/7/2.
 */
public class DrumAcceleration extends PianoAcceleration{
    public DrumAcceleration(int[] boundary,int highBorder,int lowBoder,int criticaLine){
        super(boundary, highBorder, lowBoder, criticaLine);
    }
    public int[] setXY(int _x,int _y){
        //参数1若没有按键按下返回-1
        //参数2若有声音则返回8或15，若无声音则返回0
        int[] temp=new int[2];
        for(int i=0;i<key.length;++i) {
            if (_x >= (key[i] + 5) && _x < (key[i + 1]) - 5) {
                keys[i].InputY(_y);
                if (_y > criticaLine) {
                    if ((temp[1] = keys[i].Calculate()) < 0) {
                        //Calculate():返回-1没有声音，返回8是小声，返回15是大声
                        //无声音
                        temp[0] = -1;
                        temp[1] = 0;
                    } else {
                        //有声音
                        temp[0] = i;//temp已经在if判断的时候赋值过了
                    }
                    return temp;
                }
                break;
            }
        }
        //运行到这里就说明已经判断不应该有声音发出
        temp[0]=-1;
        temp[1]=0;
        return temp;
    }

}
