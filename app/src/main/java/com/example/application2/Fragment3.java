package com.example.application2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.volley.Request.Method.POST;

public class Fragment3 extends Fragment {

    public Fragment3(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        View view = inflater.inflate(R.layout.fragment_fragment3, null);
        Point point = new Point();
        display.getSize(point);
        ImageButton add_button =  view.findViewById(R.id.add_btn);

//        View v = inflater.inflate(R.layout.fragment_fragment3, container, false);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.diary_add, null);
                ad.setView(view);
                ad.setTitle("연락처 추가");       // 제목 설정
                ad.setMessage("이름과 전화번호를 입력해주세요");   // 내용 설정
                // EditText 삽입하기

                final Button submit =  view.findViewById(R.id.buttonSubmit2);
                final EditText day =  view.findViewById(R.id.edittext_day);
                final EditText text =  view.findViewById(R.id.edittext_text);
                day.setHint("날짜를 입력하세요");
                text.setHint("내용을 입력하세요");
                final AlertDialog dialog = ad.create();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Text 값 받아서 로그 남기기
                        String full_name =  day.getText().toString();
                        String phone_number = text.getText().toString();

                        request(full_name,phone_number);
                        dialog.dismiss();     //닫기
                    }

                });
                ad.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                dialog.show();
            }
        });
        return view;
//        return v;
    }
    public void request(String day, String text){
        String url = "http://192.249.19.254:7980/diary";
        JSONObject testjson = new JSONObject();
        try{
            testjson.put("day",day);
            testjson.put("text",text);
            final String jsonString = testjson.toString();
            Log.d("body",jsonString);

            final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
