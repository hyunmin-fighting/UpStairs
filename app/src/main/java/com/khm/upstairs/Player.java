package com.khm.upstairs;

public class Player {
    com.khm.upstairs.MyCanvas canvas;
    int posX;
    int posY;
    int width;
    int height;
    boolean isPlayerLookLeft;

    public Player(int posX, int posY, int width, int height, boolean isPlayerLookLeft) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.isPlayerLookLeft = isPlayerLookLeft;
    }

    // "돌아서서 올라가기 버튼"을 클릭하면 호출되는 메서드
    public void turnUp(com.khm.upstairs.MyCanvas canvas) {
        this.canvas = canvas;
        isPlayerLookLeft = !isPlayerLookLeft;
        up(canvas);
    }

    // "올라가기 버튼"을 클릭하면 호출되는 메서드
    public void up(com.khm.upstairs.MyCanvas canvas) {
        this.canvas = canvas;
        // 모든 계단의 X,Y 좌표를 Player 방향으로 한 단계씩 이동시킨다.
        canvas.playerState = "run";
        for (int i = 0; i < Stairs.stairsArr.size(); i++) {
            Stairs stairs = Stairs.stairsArr.get(i);
            stairs.stairY += stairs.stairYGap;
            if (isPlayerLookLeft) {
                stairs.stairX += stairs.stairXGap;
            } else {
                stairs.stairX -= stairs.stairXGap;
            }
//            Log.d("test", "stairs.stairX : " + stairs.stairX);
        }
        canvas.finalStairY = Stairs.stairsArr.get(Stairs.stairsArr.size() - 1).stairY;
        canvas.finalStairX = Stairs.stairsArr.get(Stairs.stairsArr.size() - 1).stairX;

        canvas.background.up();
        if(isPlayerLookLeft){
            canvas.background.left();
        }else{
            canvas.background.right();
        }
        canvas.chkSuccess();
    }

}
