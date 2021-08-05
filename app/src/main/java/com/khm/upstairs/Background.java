package com.khm.upstairs;

public class Background {
    int leftUpX;
    int leftUpY;
    int rightDownX;
    int rightDownY;

    public Background(int leftUpX, int leftUpY, int rightDownX, int rightDownY){
        this.leftUpX = leftUpX;
        this.leftUpY = leftUpY;
        this.rightDownX = rightDownX;
        this.rightDownY = rightDownY;
    }

    public void up(){
        leftUpY += -3;
        rightDownY += -3;
    }

    public void left(){
        leftUpX += -10;
        rightDownX += -10;
    }

    public void right(){
        leftUpX += 10;
        rightDownX += 10;
    }
}
