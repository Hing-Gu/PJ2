package com.example.application2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment3 extends Fragment {
    MongoClient mongoClient;
    String Mongo_IP;
    int Mongo_Port;
    String DB_Name;
    MongoDatabase DB;

    public Fragment3(){
    }

    public String MongoAdress(String Mongo_IP, int Mongo_Port) {
        return "mongodb://" + Mongo_IP + ":" + Integer.toString(Mongo_Port);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        View v = inflater.inflate(R.layout.fragment_fragment3, container, false);
//        Mongo_IP = "192.249.19.254";
//        Mongo_Port = 7980;
//        DB_Name = "testDB";
//        mongoClient = MongoClients.create();//(MongoAdress(Mongo_IP, Mongo_Port));
//
//        MongoIterable<String> databases = mongoClient.listDatabaseNames();
//        System.out.println("=======Database List=======");
//        int num=1;
//        for (String dbName: databases) {
//            System.out.println(dbName);
//        }
        return v;
    }
}
