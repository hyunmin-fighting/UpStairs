package com.khm.upstairs;

import java.util.ArrayList;

public class Stairs {
    int stairX;
    int stairY;

    final int stairWidth = 150;
    final int stairHeight = 80;
    final int stairXGap = 160;
    final int stairYGap = 90;

    public static ArrayList<Stairs> stairsArr = new ArrayList<>();

    public Stairs(){}
    public Stairs(int stairX, int stairY){
        this.stairX = stairX;
        this.stairY = stairY;

    }

}
