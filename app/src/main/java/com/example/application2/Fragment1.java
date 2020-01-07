package com.example.application2;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.*;
import static com.android.volley.Request.Method.*;
import static com.example.application2.MainActivity.phoneBooks;

public class Fragment1 extends Fragment {
    final List<String> LIST_MENU = MainActivity.names;
    List<PhoneBook> REF_MENU = phoneBooks;
    ListViewAdapter adapter;
    String[] permission_list = { Manifest.permission.WRITE_CONTACTS };
    private List<Map<String, Object>> tmp;


    public Fragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment1, null);
        checkPermission();
        Button button2 = (Button) view.findViewById(R.id.refreshbtn1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<REF_MENU.size();i++){
                    request(String.valueOf( REF_MENU.get(i).getName()),String.valueOf(REF_MENU.get(i).getTel()));
                    Log.d("ref", String.valueOf(REF_MENU.get(i).getName())+","+ String.valueOf(REF_MENU.get(i).getTel()));
                }
            }
        });
//        for(int i=0; i<REF_MENU.size();i++){
//            request(String.valueOf( REF_MENU.get(i).getName()),String.valueOf(REF_MENU.get(i).getTel()));
//            Log.d("ref", String.valueOf(REF_MENU.get(i).getName())+","+ String.valueOf(REF_MENU.get(i).getTel()));
//        }
        adapter = new ListViewAdapter(getActivity(), R.layout.listview_btn_item, REF_MENU);
        final ListView listview = (ListView) view.findViewById(R.id.listview1);
        ImageButton add_button = (ImageButton) view.findViewById(R.id.add_btn);
//        final Button dbbtn = view.findViewById(R.id.getTel);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.phone_add, null);
                ad.setView(view);
                ad.setTitle("연락처 추가");       // 제목 설정
                ad.setMessage("이름과 전화번호를 입력해주세요");   // 내용 설정
                // EditText 삽입하기

                final Button submit = (Button) view.findViewById(R.id.buttonSubmit);
                final EditText name = (EditText) view.findViewById(R.id.edittext_name);
                final EditText phone_num = (EditText) view.findViewById(R.id.edittext_phone);
                name.setHint("이름을 입력하세요");
                phone_num.setHint("번호를 입력하세요");
                final AlertDialog dialog = ad.create();
//                dbbtn.setOnClickListener(new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v) {
////                        get();
//                    }
//                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PhoneBook add_phone = new PhoneBook();
                        // Text 값 받아서 로그 남기기
                        String full_name = name.getText().toString();
                        String phone_number = phone_num.getText().toString();

                        request(name.getText().toString(),phone_num.getText().toString());
                        add_phone.setName(full_name);
                        add_phone.setTel(phone_number);

                        contactAdd(full_name, phone_number);
                        adapter.phoneBooks.add(add_phone);
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

        Button button3 = (Button) view.findViewById(R.id.downbtn1);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download("http://192.249.19.254:7980/tels");
                adapter.phoneBooks = REF_MENU;
                adapter.notifyDataSetChanged();
            }
        });

        adapter.notifyDataSetChanged();
        listview.setAdapter(adapter);
        REF_MENU = adapter.phoneBooks;

        return view;
    }
    public void contactAdd(final String name, final String phone_num){
        new Thread(){
            @Override
            public void run() {
                ArrayList<ContentProviderOperation> list = new ArrayList<>();
                try{
                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                    .build()
                    );
                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)   //이름
                                    .build()
                    );
                    list.add(
                            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_num)           //전화번호
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE  , ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)   //번호타입(Type_Mobile : 모바일)
                                    .build()
                    );
                    checkPermission();
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, list);  //주소록추가
                    list.clear();   //리스트 초기화
                }catch(RemoteException e){
                    e.printStackTrace();
                }catch(OperationApplicationException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = getActivity().checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }

    public void request(String name, String tel){
        String url = "http://192.249.19.254:7980/tels";
        JSONObject testjson = new JSONObject();
        try{
            testjson.put("name",name);
            testjson.put("tel",tel);
            final String jsonString = testjson.toString();
            Log.d("body",jsonString);

            final  RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
            Log.d("test","first");
            requestQueue.add(jsonObjectRequest);
            Log.d("test", String.valueOf(requestQueue));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void download(String url) {
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    tmp = gson.fromJson(String.valueOf(response), new TypeToken<List<Map<String, Object>>>() {}.getType());
                    REF_MENU = jsontoList(tmp);
                    adapter.phoneBooks = REF_MENU;
                    Log.d("listsize", Integer.toString(REF_MENU.size()));
                    adapter.notifyDataSetChanged();
                    Log.d("test", String.valueOf(response));
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.d("reqset", "OK");
        requestQueue.add(stringRequest);
        Log.d("reqQadd", "OK");
    }

    public List<PhoneBook> jsontoList(List<Map<String, Object>> ori_list) {
        List<PhoneBook> ret = new ArrayList<PhoneBook>();
        for (int i=0; i<ori_list.size(); i++) {
            PhoneBook pb = new PhoneBook();
            pb.setName((String) ori_list.get(i).get("name"));
            pb.setTel((String) ori_list.get(i).get("tel"));
            Log.d("imglist id", (String) ori_list.get(i).get("name"));
            Log.d("imglist uri", (String) (String) ori_list.get(i).get("tel"));
            ret.add(pb);
        }
        return ret;
    }
}