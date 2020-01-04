package com.example.application2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.tabs.TabLayout;
import com.mongodb.DBObject;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //For Contacts Tab
    public static List<String> names;
    public static List<PhoneBook> phoneBooks;

    //For Tab Layout
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] permission_list = { Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE };

    //For FaceBook Login
    public Context mContext = this;
    CallbackManager callBackManager;
    AccessToken accessToken;
    LoginButton loginButton;
    boolean isLogin;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        //Log.d("test","1234");
        new JSONTask().execute("http://192.249.19.254:7980/a");
        request();
        names = new ArrayList<>();
        phoneBooks = new ArrayList<>();
        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        do {
            PhoneBook pb = new PhoneBook();
            String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            pb.setName(name);
            pb.setTel(phoneNumber);
            names.add(name);
            phoneBooks.add(pb);
        } while (c.moveToNext());
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addTab(tabLayout.newTab().setText("Gallery"));
        tabLayout.addTab(tabLayout.newTab().setText("Games"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //For Facebook Login
        callBackManager = CallbackManager.Factory.create();
//        accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLogin = (accessToken != null) && !accessToken.isExpired();
        loginButton = (LoginButton) findViewById(R.id.login_btn);
        logoutButton = (Button) findViewById(R.id.fblogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Initializing the TabLayout
                loginButton.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.VISIBLE);
                isLogin = true;
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "You've cancelled Facebook Login", Toast.LENGTH_LONG);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mContext, "Facebook Login ERROR", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callBackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = this.checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(this.getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }
        }
    }
    EditText editTextID;
    EditText editTextTW;
    public void request(){
        final JSONObject testjson = new JSONObject();
        Log.d("test","1234");
        String url = "http://192.249.19.254:7980/a";
        new JSONTask().execute("http://192.249.19.254:7980/b");
//        try {
//            testjson.put("id", "asdfasdf");
//            testjson.put("password", "adfafasd");
//            String jsonString = testjson.toString();
//            Log.d("test","2222");
//            //이제 전송해볼까요?
//            final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, testjson, new Response.Listener<JSONObject>() {
//                //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        System.out.println("데이터전송 성공");
//                        Log.d("test","3333");
//                       //받은 json형식의 응답을 받아
//                        JSONObject jsonObject = new JSONObject(response.toString());
//                    }
//                    catch (Exception e) {
//                        Log.d("test","fuck you");
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//               @Override
//               public void onErrorResponse(VolleyError error) {
//                   error.printStackTrace();
//               }
//            });
//            Log.d("test","4444");
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonObjectRequest);
//            Log.d("test","5555");
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("user_id", "androidTest");
                jsonObject.accumulate("name", "yun");
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.connect();
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
        }
    }
}
