package com.example.application2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.POST;

public class DiaryAddActivity extends FragmentActivity {
    EditText day;
    EditText text;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_add);
        submit = findViewById(R.id.buttonSubmit2);
        day = findViewById(R.id.edittext_day);
        text = findViewById(R.id.edittext_text);
        day.setHint("날짜 입력(예:200106)");
        text.setHint("내용을 입력하세요");
        submit=findViewById(R.id.buttonSubmit2);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Text 값 받아서 로그 남기기
                String full_name =  day.getText().toString();
                String phone_number = text.getText().toString();

                request(full_name,phone_number);
                Toast.makeText(getApplicationContext(),"저장 완료",Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();

            }

        });


    }

    public void request(String day, String text){
        String url = "http://192.249.19.254:7980/diary";
        JSONObject testjson = new JSONObject();
        try{
            testjson.put("day",day);
            testjson.put("text",text);
            final String jsonString = testjson.toString();
            Log.d("body",jsonString);

            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(POST, url, testjson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("test","데이터전송성공");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }});

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}