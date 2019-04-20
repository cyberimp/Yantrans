package com.kalinasoft.yantrans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by Andrey Kalikin on 17.04.2017.
 */

class HistoryListAdapter extends BaseAdapter {

    private HistoryArray content;
    private LayoutInflater mInflater;
    private boolean favsOnly;
    private Context context;


    HistoryListAdapter(Context context, HistoryArray array, boolean favsOnly){
        this.content = array;
        this.favsOnly = favsOnly;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return content.getCount(favsOnly);
    }

    @Override
    public Object getItem(int i) {
        return content.getItem(i,favsOnly);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.history_item, viewGroup, false);
            holder = new ViewHolder();
            holder.word = view.findViewById(R.id.word);
            holder.translation = view.findViewById(R.id.translation);
            holder.lang_to = view.findViewById(R.id.lang_from);
            holder.lang_from = view.findViewById(R.id.lang_to);
            holder.favored = view.findViewById(R.id.favorited);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.word.setText(content.getItem(i,favsOnly).getWord());
        holder.translation.setText(content.getItem(i,favsOnly).getTranslation());
        holder.lang_from.setText(content.getItem(i,favsOnly).getLang_from());
        holder.lang_to.setText(content.getItem(i,favsOnly).getLang_to());
        holder.favored.setChecked(content.getItem(i,favsOnly).isChecked());
        holder.favored.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                content.getItem(i,favsOnly).setChecked(isChecked);
                HistoryArray.getInstance().saveArray(context);
            }
        });
        return view;
    }

    static private class ViewHolder{
        TextView word, translation, lang_to, lang_from;
        CheckBox favored;
    }
}
