package com.example.application2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import static android.view.View.VISIBLE;

public class ListViewAdapter extends ArrayAdapter {
    int resourceId;
    public List<PhoneBook> phoneBooks;
    private boolean numOpened;
    public boolean checkable;
    private boolean nowVisible;
    public boolean isOpened() {
        return numOpened;
    }

    public void setOpened() {
        if (this.numOpened) {
            this.numOpened = false;
        }
        else {
            this.numOpened = true;
        }
    }

    public ListViewAdapter(Context context, int resource, List<PhoneBook> list) {
        super(context, resource, list);
        this.resourceId = resource;
        this.phoneBooks = list;
        this.numOpened = false;
        this.checkable = false;
        this.nowVisible = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        int longClicked = pos;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_btn_item, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        final TextView nameTextView = (TextView) convertView.findViewById(R.id.textView1);
        final String name = phoneBooks.get(pos).getName();
        final String tel = phoneBooks.get(pos).getTel();
        nameTextView.setText(name);
        final String str_true = name + "\n" + tel;
        final String str_false = name;

        final ImageButton button1 = (ImageButton) convertView.findViewById(R.id.numbtn);
        button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setOpened();
                if (isOpened()) {
                    button1.setBackgroundResource(R.drawable.unexpand);
                    nameTextView.setText(str_true);
                }
                else {
                    button1.setBackgroundResource(R.drawable.expand);
                    nameTextView.setText(str_false);
                }
            }
        });

        ImageButton button2 = (ImageButton) convertView.findViewById(R.id.callbtn);
        button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tel));
                context.startActivity(intent);
            }
        });

        final ImageButton button3 = (ImageButton) convertView.findViewById(R.id.delbtn);
        button3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                phoneBooks.remove(pos);
                notifyDataSetChanged();
                button3.setVisibility(View.GONE);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (nowVisible) {
                    button3.setVisibility(View.GONE);
                    nowVisible = false;
                }
                else {
                    button3.setVisibility(VISIBLE);
                    nowVisible = true;
                }
                return true;
            }
        });
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return phoneBooks.get(position);
    }

    @Override
    public int getCount() {
        return phoneBooks.size();
    }
}
