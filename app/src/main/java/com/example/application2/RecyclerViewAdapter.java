package com.example.application2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {
    private Context context;
    private List<Imagelist> mThumbIds;
    private List<Map<String, Object>> tmp;
    private int size;
    private int pad;

    public RecyclerViewAdapter(Context context, int displayWidth) {
        this.context = context;
        mThumbIds = new ArrayList<Imagelist>();
        mThumbIds.add(new Imagelist(R.drawable.f12, ""));
        mThumbIds.add(new Imagelist(R.drawable.f13, ""));
        mThumbIds.add(new Imagelist(R.drawable.f14, ""));
        mThumbIds.add(new Imagelist(R.drawable.f15, ""));
        mThumbIds.add(new Imagelist(R.drawable.f16, ""));
        mThumbIds.add(new Imagelist(R.drawable.f17, ""));
        size = displayWidth/3;
        pad = 8;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clickview, parent, false);
        Holder holder = new Holder(view);

        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            Imagelist tmp = mThumbIds.get(position);
            if (tmp.imageId != 0) {
                holder.imageView.setImageResource(tmp.imageId);
            } else {
                holder.imageView.setImageURI(Uri.parse(tmp.imageUri));
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        int pos = position;
        Imagelist tmp = mThumbIds.get(position);
        if (tmp.imageId != 0) {
            holder.imageView.setImageResource(tmp.imageId);
        }
        else {
            holder.imageView.setImageURI(Uri.parse(tmp.imageUri));
        }
        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.imageView.setPadding(pad, pad, pad, pad);
    }

    @Override
    public int getItemCount() {
        return mThumbIds.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public ImageView imageView;
        public Holder(final View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageviewrecycle);
            int pos = getAdapterPosition();
            imageView.setOnCreateContextMenuListener(this);
            if (pos != RecyclerView.NO_POSITION) {
                Imagelist il = mThumbIds.get(pos);
                if (il.imageId != 0) {
                    imageView.setImageResource(il.imageId);
                }
                else {
                    imageView.setImageURI(Uri.parse(il.imageUri));
                }
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Imagelist tmp = mThumbIds.get(position);
                        if (tmp.imageId != 0) {
                            imageView.setImageResource(tmp.imageId);
                        } else {
                            imageView.setImageURI(Uri.parse(tmp.imageUri));
                        }
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("image ID", tmp.imageId);
                        intent.putExtra("image URI", tmp.imageUri);
                        intent.putParcelableArrayListExtra("image List", (ArrayList<? extends Parcelable>) mThumbIds);
                        context.startActivity(intent);
                    }

                }
            });
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Delete = menu.add(Menu.NONE, 1001, 1, "Delete");
            Delete.setOnMenuItemClickListener(onEditMenu);
            MenuItem Upload = menu.add(Menu.NONE, 1002, 2, "Upload");
            Upload.setOnMenuItemClickListener(onUploadMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 1001) {
                    mThumbIds.remove(getAdapterPosition());

                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mThumbIds.size());
                }
                return true;
            }
        };
        private final MenuItem.OnMenuItemClickListener onUploadMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 1002) {
                    String url = "http://192.249.19.254:7980/imageup";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("imageid", Integer.toString(mThumbIds.get(getAdapterPosition()).imageId));
                        jsonObject.put("imageuri", mThumbIds.get(getAdapterPosition()).imageUri);
                        final String jsonString = jsonObject.toString();
                        Log.d("json", jsonString);

                        final RequestQueue requestQueue = Volley.newRequestQueue(context);
                        Log.d("requestqueue", "OK");
                        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Log.d("test", "이미지 데이터 성공");
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
                        Log.d("jsonobjectreq", "OK");

                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        Log.d("reqset", "OK");
                        requestQueue.add(jsonObjectRequest);
                        Log.d("reqQadd", "OK");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        };
    }
    public void download(String url) {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    tmp = gson.fromJson(String.valueOf(response), new TypeToken<List<Map<String, Object>>>() {}.getType());
                    mThumbIds = jsontoList(tmp);
                    notifyDataSetChanged();
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
    public void addList(String o) {
        mThumbIds.add(new Imagelist(0, o));
    }
    public List<Imagelist> jsontoList(List<Map<String, Object>> ori_list) {
        List<Imagelist> ret = new ArrayList<Imagelist>();
        for (int i=0; i<ori_list.size(); i++) {
            Imagelist imglist = new Imagelist(0, "");
            imglist.imageId = Integer.parseInt((String) ori_list.get(i).get("imageid"));
            imglist.imageUri = (String) ori_list.get(i).get("imageuri");
            Log.d("imglist id", String.valueOf(Integer.parseInt((String) ori_list.get(i).get("imageid"))));
            Log.d("imglist uri", (String) ori_list.get(i).get("imageuri"));
            ret.add(imglist);
        }
        return ret;
    }
}
