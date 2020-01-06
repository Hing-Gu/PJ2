package com.example.application2;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Fragment2 extends Fragment {
    final int PICTURE_REQUEST_CODE = 100;
    String[] permission_list = { Manifest.permission.READ_EXTERNAL_STORAGE };
    List<Uri> sendlistUri = new ArrayList<Uri>();
    RecyclerViewAdapter adapter;
    public Fragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();

        display.getSize(point);
        int displayWidth = point.x;

        checkPermission();
        View v = inflater.inflate(R.layout.fragment_fragment2, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview1);
        adapter = new RecyclerViewAdapter(getActivity(), displayWidth);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        Button button = (Button) v.findViewById(R.id.selectbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 갤러리 들어감
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
            }
        });

        Button button2 = (Button) v.findViewById(R.id.refreshbtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.download("http://192.249.19.254:7980/imagedown");
            }
        });
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = getActivity().checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }
    //
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
                    Toast.makeText(getActivity().getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }
        }
    }
    //
//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICTURE_REQUEST_CODE) {
            if(resultCode == getActivity().RESULT_OK){
                ClipData clipData = data.getClipData();
                List<Uri> imageListUri = new ArrayList<>();

                if(clipData == null){
                    Toast.makeText(getActivity(), "다중선택이 불가능한 기기입니다.", Toast.LENGTH_LONG).show();
                }

                else if(clipData != null){
                    for(int i = 0; i < clipData.getItemCount(); i++){
                        Log.i("3. single choice", String.valueOf(clipData.getItemAt(i).getUri()));
                        adapter.addList(clipData.getItemAt(i).getUri().toString());
                        imageListUri.add(clipData.getItemAt(i).getUri());
                    }

                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
