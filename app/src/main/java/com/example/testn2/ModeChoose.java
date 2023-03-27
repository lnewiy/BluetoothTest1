package com.example.testn2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ModeChoose extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divide);

        Button touchDraw = findViewById(R.id.dbutton1);
        Button selectPic = findViewById(R.id.dbutton2);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle1 = intent.getExtras();

        touchDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeChoose.this,BTRW.class);
                Bundle bundle = new Bundle();
                bundle.putString("deviceaddr",bundle1.getString("deviceaddr"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeChoose.this,PicSelect.class);
                Bundle bundle = new Bundle();
                bundle.putString("deviceaddr",bundle1.getString("deviceaddr"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
