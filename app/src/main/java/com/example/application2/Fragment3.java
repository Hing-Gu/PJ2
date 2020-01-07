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
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class Fragment3 extends Fragment {
    List<Diary> DIARY;
    DiaryAdapter adapter;
    private List<Map<String, Object>> tmp;
    public Fragment3(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        View v = inflater.inflate(R.layout.fragment_fragment3, null);
        Point point = new Point();
        display.getSize(point);
        DIARY = new ArrayList<Diary>();
        final ListView listview = (ListView) v.findViewById(R.id.listview3);
        adapter = new DiaryAdapter(getActivity(), R.layout.diaryview, DIARY);
        Button down_button = v.findViewById(R.id.download1);
        down_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download("http://192.249.19.254:7980/diary");
                adapter.diarys = DIARY;
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton add_button = v.findViewById(R.id.add_btn2);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryAddActivity.class);
                startActivity(intent);
            }
        });

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(adapter.mItemClickListener);
        return v;
    }

    public void download(String url) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    tmp = gson.fromJson(String.valueOf(response), new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
                    DIARY = jsontoList(tmp);
                    adapter.diarys = DIARY;
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(arrayRequest);
    }

    public List<Diary> jsontoList(List<Map<String, Object>> ori_list) {
        List<Diary> ret = new ArrayList<Diary>();
        for (int i=0; i<ori_list.size(); i++) {
            Diary d = new Diary("", "");
            d.setDay((String) ori_list.get(i).get("day"));
            d.setText((String) ori_list.get(i).get("text"));
            Log.d("d day", (String) ori_list.get(i).get("day"));
            Log.d("d text", (String) (String) ori_list.get(i).get("text"));
            ret.add(d);
        }
        return ret;
    }
}
