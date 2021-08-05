package com.khm.upstairs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

public class MyCanvas extends View {
    MainActivity mainActivity;
    MyHandler handler;
    Paint p;
    Stairs stairs;
    Player player;
    Background background;

    // 방법1
//    public MyCanvas(Context context) {
//        super(context);
//        this.mainActivity = (MainActivity) context;
//        p = new Paint();
//        stairs = new Stairs();
//
//        handler = new MyHandler();
//        handler.sendEmptyMessageDelayed(100, 200);
//    }

    // 방법2
    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mainActivity = (MainActivity) context;
        p = new Paint();
        stairs = new Stairs();
        backgroundBit = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        handler = new MyHandler();
        handler.sendEmptyMessageDelayed(100, 200);
    }

    public Player getPlayer() {
        return player;
    }

    int runTime = 0;
    int successCnt = 0;
    boolean isDead = false;

    // "시작 버튼"을 클릭하면 호출되는 메서드
    public void startGame() {
        playerState = "initialize";
        isDead = false;
        player.isPlayerLookLeft = true;
        mainActivity.progressBar.setProgress(50);

        if (runTime != 0) {
            // 계단 초기화
            Stairs.stairsArr.clear();
            finalStairY = originY - stairs.stairHeight;
            isFirstStair = true;
            // Player 초기화
            player.posX = originX;
            player.posY = originY;
            player.width = stairs.stairWidth;
            player.height = stairs.stairHeight * 5;
            // 배경화면 초기화
            background.leftUpX = backgroundBit.getWidth() / 100 * 30;
            background.leftUpY = backgroundBit.getHeight() / 100 * 80;
            background.rightDownX = backgroundBit.getWidth() / 100 * 70;
            background.rightDownY = backgroundBit.getHeight();
        }
        mainActivity.btn_0.setVisibility(View.INVISIBLE);
        mainActivity.btn_1.setVisibility(View.VISIBLE);
        mainActivity.btn_2.setVisibility(View.VISIBLE);

        runTime++;
        successCnt = 0;
        mainActivity.tv_successCnt.setText("" + successCnt);
    }

    // up()메서드가 호출되면 자동으로 호출되는 메서드(성공/실패 판정)
    public void chkSuccess() {
        if (successCnt == 0) {
            // 최초로 올라가기 버튼 클릭하는 순간 Timer 시작
            handler.sendEmptyMessage(123);
        }
        // Player가 계단 오르기 성공/실패를 확인하는 코드
        for (int i = 0; i < Stairs.stairsArr.size(); i++) {
            Stairs stairs = Stairs.stairsArr.get(i);
            if (stairs.stairX == 540 && stairs.stairY == (originY - stairs.stairHeight + stairs.stairYGap)) {
//                Log.d("result", "성공!");
                successCnt++;
                mainActivity.tv_successCnt.setText("" + successCnt);
                if (mainActivity.progressBar.getProgress() != 0) {
                    mainActivity.progressBar.incrementProgressBy(10);
                }
                return;
            }
            if (stairs.stairX != 540 && stairs.stairY == (originY - stairs.stairHeight + stairs.stairYGap)) {
//                Log.d("result", "실패!");
                isDead = true;
                handler.removeMessages(123);
                //질문!!!!!! PlayerThread "dead" 처리가 끝난 후에 ViSIBLE 하고 싶다.
                mainActivity.btn_0.setVisibility(View.INVISIBLE);
                mainActivity.btn_1.setVisibility(View.INVISIBLE);
                mainActivity.btn_2.setVisibility(View.INVISIBLE);
                return;
            }
            // 화면 아래로 사라진 계단 제거
            if (Stairs.stairsArr.get(0).stairY > getHeight() + stairs.stairYGap) {
                Stairs.stairsArr.remove(0);
            }
        }
    }

    int originX;
    int originY;
    int block;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                // Canvas가 올라오기를 기다리는 코드
                if (getWidth() > 0) {
                    block = getWidth() / 6;
                    originX = block * 3;
                    originY = getHeight() / 100 * 70;
                    Log.d("test", "originX : " + originX);
                    Log.d("test", "originY : " + originY);
                    player = new Player(originX, originY, stairs.stairWidth, stairs.stairHeight * 5, true);
                    background = new Background(backgroundBit.getWidth() / 100 * 30, backgroundBit.getHeight() / 100 * 80, backgroundBit.getWidth() / 100 * 70, backgroundBit.getHeight());
                    new StairsThread().start();
                    new PlayerThread().start();
                } else {
                    handler.sendEmptyMessageDelayed(100, 20);
                }
            } else if (msg.what == 123) {
                // ProgressBar가 줄어드는 처리를 하는 코드
                mainActivity.progressBar.incrementProgressBy(-10);
                if (mainActivity.progressBar.getProgress() == 0) {
                    // ProgressBar가 0이 된 경우
                    playerState = "stop";
                    isDead = true;
                    //질문!!!!!! PlayerThread "dead" 처리가 끝난 후에 ViSIBLE 하고 싶다.
                    mainActivity.btn_0.setVisibility(View.VISIBLE);
                    mainActivity.btn_1.setVisibility(View.INVISIBLE);
                    mainActivity.btn_2.setVisibility(View.INVISIBLE);
                    removeMessages(123);
                } else {
                    // ProgressBar가 0이 아닌경우 성공횟수에따라 delay(줄어드는 속도) 조정
                    if (successCnt > 100) {
                        sendEmptyMessageDelayed(123, 250);
                    } else if (successCnt > 200) {
                        sendEmptyMessageDelayed(123, 200);
                    } else if (successCnt > 300) {
                        sendEmptyMessageDelayed(123, 150);
                    } else {
                        sendEmptyMessageDelayed(123, 400);
                    }
                }
            } else if (msg.what == 222) {
                // playerThread에서 dead처리 완료 후 재시작을 위한 시작버튼을 보여주기 위해
                mainActivity.btn_0.setVisibility(View.VISIBLE);
                removeMessages(222);
            } else {
                invalidate();
            }
        }
    }

    int finalStairX;
    int finalStairY;
    Random ran = new Random();
    boolean isRunning = true;
    boolean isStairCreatedLeft;
    boolean isFirstStair;
    int continuedLeftCnt;
    int continuedRightCnt;

    // 계단 생성 쓰레드
    class StairsThread extends Thread {
        @Override
        public void run() {
            super.run();
            isFirstStair = true;

            while (isRunning) {
                // y좌표가 -100 이상인 상태에서만 화면에 보여지는 공간내에 계단 생성
                while (finalStairY > -100) {
                    Log.d("check", "Left : " + continuedLeftCnt);
                    Log.d("check", "Right : " + continuedRightCnt);

                    if (isFirstStair) {
                        // 첫번째 계단 생성
                        finalStairX = originX - stairs.stairXGap;
                        finalStairY = originY - stairs.stairHeight;
                        Stairs.stairsArr.add(new Stairs(finalStairX, finalStairY));
                        continuedLeftCnt = 1;
                        continuedRightCnt = 0;
                        isStairCreatedLeft = true;
                        isFirstStair = false;

                    } else {
                        // 두번째 ~ 무한의 계단 생성
                        finalStairY -= stairs.stairYGap;
                        int ranNum = ran.nextInt(5);
                        if (isStairCreatedLeft) { // 왼쪽방향계단이 왼쪽으로 이어질 확률을 더 높임
                            if (ranNum > 0) {
                                if (continuedLeftCnt >= 8) {  // 한방향으로 연속으로 생길수 있는 계단수 8개로 제한
                                    finalStairX += stairs.stairXGap;
                                    continuedLeftCnt = 0;
                                    continuedRightCnt = 1;
                                    isStairCreatedLeft = false;
                                } else {
                                    finalStairX += -stairs.stairXGap;   // 80%확률
                                    continuedLeftCnt++;
                                    isStairCreatedLeft = true;
                                }
                            } else {
                                finalStairX += stairs.stairXGap;    // 20%확률
                                continuedLeftCnt = 0;
                                continuedRightCnt = 1;
                                isStairCreatedLeft = false;           // 오른쪽방향 계단으로 전환
                            }
                        } else {  // 오른쪽방향계단이 오른쪽으로 이어질 확률을 더 높임
                            if (ranNum > 0) {
                                if (continuedRightCnt >= 8) {
                                    finalStairX += -stairs.stairXGap;
                                    continuedRightCnt = 0;
                                    continuedLeftCnt = 1;
                                    isStairCreatedLeft = true;
                                } else {
                                    finalStairX += stairs.stairXGap;
                                    continuedRightCnt++;
                                    isStairCreatedLeft = false;
                                }
                            } else {
                                finalStairX += -stairs.stairXGap;
                                continuedRightCnt = 0;
                                continuedLeftCnt = 1;
                                isStairCreatedLeft = true;             // 왼쪽방향 계단으로 전환
                            }
                        }
                        Stairs.stairsArr.add(new Stairs(finalStairX, finalStairY));
                    }
                    Log.d("test", "stairArrSize : " + Stairs.stairsArr.size());
                }
                try {
                    StairsThread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
    }

    String playerState = "initialize";
    int bitArrIdx = 0;
    Bitmap[] bitArr = {BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand1),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand2),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand3),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand4),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand5),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand6),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand7),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand8),
            BitmapFactory.decodeResource(getResources(), R.drawable.chracterstand9)
    };

    // Player 상태에 따른 이미지 처리 쓰레드
    class PlayerThread extends Thread {
        @Override
        public void run() {
            super.run();
            bitArrIdx = 0;
            while (isRunning) {
                if (playerState.equals("initialize")) {
                    bitArrIdx++;
                    if (bitArrIdx > 2) {
                        bitArrIdx = 0;
                    }
                    try {
                        PlayerThread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (playerState.equals("run")) {
                    if (player.isPlayerLookLeft) {
                    } else {
                    }
                    try {
                        PlayerThread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playerState = "stop";

                } else if (playerState.equals("stop")) {
                    if (isDead) {
                        playerState = "dead";
                    }
                    try {
                        PlayerThread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (playerState.equals("dead")) {
                    int i = 0;
                    while (player.posY < getHeight() + 500) {
                        if (i == 0) {
                            SystemClock.sleep(800); // 순간 깜놀
                            handler.sendEmptyMessageDelayed(222, 200);
                        }
                        player.posY += 30;
                        i++;
                        try {
                            PlayerThread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                try {
//                    PlayerThread.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                handler.sendEmptyMessage(0);
            }
        }
    }

    Bitmap bit;
    Bitmap backgroundBit;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.LTGRAY);

        if (background != null) {
            canvas.drawBitmap(backgroundBit,
                    new Rect(background.leftUpX, background.leftUpY, background.rightDownX, background.rightDownY),
                    new Rect(0, 0, getWidth(), getHeight()),
                    p);
        }

        // 계단 그리기
        bit = BitmapFactory.decodeResource(getResources(), R.drawable.stairs);
        for (int i = 0; i < Stairs.stairsArr.size(); i++) {
            Stairs stairs = Stairs.stairsArr.get(i);
//            canvas.drawRect(stairs.stairX, stairs.stairY, stairs.stairX + stairs.stairWidth, stairs.stairY + stairs.stairHeight, p);
            canvas.drawBitmap(bit,
                    new Rect(0, 0, bit.getWidth(), bit.getHeight()),
                    new Rect(stairs.stairX, stairs.stairY, stairs.stairX + stairs.stairWidth, stairs.stairY + stairs.stairHeight),
                    p);
        }

        // Player 그리기
        if (player != null) {        // 질문!!! : if문 지우면 Error 발생
            if (playerState.equals("initialize")) {
                bit = bitArr[bitArrIdx];
            } else if (playerState.equals("run")) {
                if (player.isPlayerLookLeft) {
                    bit = bitArr[6];
                } else {
                    bit = bitArr[5];
                }
            } else if (playerState.equals("stop")) {
                if (player.isPlayerLookLeft) {
                    bit = bitArr[3];
                } else {
                    bit = bitArr[4];
                }
            } else if (playerState.equals("dead")) {
                if (player.isPlayerLookLeft) {
                    bit = bitArr[7];
                } else {
                    bit = bitArr[8];
                }
            }
            canvas.drawBitmap(bit,
                    new Rect(0, 0, bit.getWidth(), bit.getHeight()),
                    new Rect(player.posX, player.posY - player.height, player.posX + player.width, player.posY),
                    p);
        }
    }

}
