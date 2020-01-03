package com.example.application2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Holder> {
    private Context context;
    private List<Imagelist> mThumbIds;
    private int size;
    private int pad;

    public RecyclerViewAdapter(Context context, int displayWidth) {
        this.context = context;
        mThumbIds = new ArrayList<Imagelist>();
        mThumbIds.add(new Imagelist(R.drawable.f1, ""));
        mThumbIds.add(new Imagelist(R.drawable.f2, ""));
        mThumbIds.add(new Imagelist(R.drawable.f3, ""));
        mThumbIds.add(new Imagelist(R.drawable.f12, ""));
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
    }
    public void addList(String o) {
        mThumbIds.add(new Imagelist(0, o));
    }
}
