package com.khm.upstairs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    com.khm.upstairs.MyCanvas canvas;
    Button btn_0;
    Button btn_1;
    Button btn_2;
    TextView tv_successCnt;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 방법1
//        canvas = new MyCanvas(MainActivity.this);
//        setContentView(canvas);

//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.activity_main, null);
//        addContentView(v, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        btn_0 = v.findViewById(R.id.btn_0);

        // 방법2
        setContentView(R.layout.activity_main);

        canvas = findViewById(R.id.canvas);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        tv_successCnt = findViewById(R.id.tv_successCnt);
        progressBar = findViewById(R.id.progressBar);

        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.startGame();
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.getPlayer().up(canvas);
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.getPlayer().turnUp(canvas);
            }
        });
    }

}