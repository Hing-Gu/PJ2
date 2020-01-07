package com.example.application2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.application2.R;

public class DiaryDownloadActivity extends FragmentActivity {
    TextView day;
    TextView text;
    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_download);
//        close = findViewById(R.id.buttonClose);
        day = findViewById(R.id.diaryday);
        text = findViewById(R.id.diarytext);
        Intent receivedIntent = getIntent();
        String str_day = (String)receivedIntent.getExtras().get("day");
        String str_text = (String)receivedIntent.getExtras().get("text");
        day.setText(str_day);
        text.setText(str_text);

//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }
}
