package com.example.application2;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends ArrayAdapter {
    List<Diary> diarys;
    Context mcontext;
    public DiaryAdapter(Context context, int resource, List<Diary> list) {
        super(context, resource, list);
        this.mcontext = context;
        diarys = new ArrayList<Diary>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.diaryview, parent, false);
        }
        TextView dayView = (TextView) convertView.findViewById(R.id.daytext);
//        final TextView textView = (TextView) convertView.findViewById(R.id.diarytext);
//        final ImageButton closebtn = (ImageButton) convertView.findViewById(R.id.closebtn);
        final String day = diarys.get(pos).getDay();
        final String text = diarys.get(pos).getText();
        dayView.setText(day);

//        textView.setText(text);

        return convertView;
    }

    public AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mcontext, DiaryDownloadActivity.class);
            Diary tmp = diarys.get(position);
            intent.putExtra("day", tmp.getDay());
            intent.putExtra("text", tmp.getText());
            intent.putParcelableArrayListExtra("diary List", (ArrayList<? extends Parcelable>) diarys);
            getContext().startActivity(intent);
        }
    };

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public Object getItem(int position) {
        return diarys.get(position);
    }

    @Override
    public int getCount() {
        return diarys.size();
    }
}
