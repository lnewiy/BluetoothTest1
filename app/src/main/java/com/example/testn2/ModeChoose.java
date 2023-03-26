package com.example.testn2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ModeChoose extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divide);

        Button touchDraw = findViewById(R.id.dbutton1);
        Button selectPic = findViewById(R.id.dbutton2);

        touchDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeChoose.this,BTRW.class);
                startActivity(intent);
            }
        });

        selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeChoose.this,PicSelect.class);
                startActivity(intent);
            }
        });

    }




}
