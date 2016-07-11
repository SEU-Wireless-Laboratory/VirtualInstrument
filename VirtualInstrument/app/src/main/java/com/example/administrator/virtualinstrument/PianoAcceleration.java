package com.example.administrator.virtualinstrument;

/**
 * Created by Chengzhi_Huang on 2016/7/2.
 */
public class PianoAcceleration {
    protected class Section{
        //对于每一块区域作为一个对象，坐标存入相应的区域
        public Section(){
            //初始化y数组
            //注意这里不能够使用foreach,foreach是只读的
            //边界储存在父类中了，作为int
            for(int i=0;i<5;i++){
                y[i]=0;
            }
        }
        //从外接收y值并存入数组中
        public boolean InputY(int i){
            y[index]=i;
            index++;
            if(index == 5){
                index=0;
            }
            return true;
        }
        public int Calculate(){
            int i=index-1;
            if(i<0){
                i=i+5;
            }
            int j=index-2;
            if(j<0){
                j=j+5;
            }
            int k=index-3;
            if(k<0){
                k=k+5;
            }
            if(y[i]-y[k]>highBorder){
                return 15;//大声
            }else if(y[i]-y[k]<lowBoder){
                return -1;//没有声
            }else{
                return 8;//小声
            }
        }
        private int[] y=new int[5];//用来存储系列坐标
        private int index=0;//最后存入的是index-1
    }
    public int[] setXY(int _x,int _y){
        //参数1若没有按键按下返回-1
        //参数2若有声音则返回8或15，若无声音则返回0
        int[] temp=new int[2];
        for(int i=0;i<key.length;++i) {
            if (_x >= (key[i] + 3) && _x < (key[i + 1]) - 3) {
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
    public PianoAcceleration(int[] boundary,int highBorder,int lowBoder,int criticaLine){
        //假设传过来的数组即为边界是从小到大排列的
        key=boundary;
        keys=new Section[boundary.length-1];
        for (int i=0;i<boundary.length-1;i++) {
            keys[i]=new Section();//
        }
        this.highBorder=highBorder;
        this.lowBoder=lowBoder;
        this.criticaLine=criticaLine;
    }
    protected Section[] keys;
    protected int[] key;
    protected int criticaLine;
    protected int highBorder,lowBoder;//鉴别发出声音的,速度差
}
