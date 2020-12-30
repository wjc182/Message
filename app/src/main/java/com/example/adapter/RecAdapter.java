package com.example.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messagetalk.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;

public class RecAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<EMMessage> list;
    private String name;

    public RecAdapter(Context context, List<EMMessage> list, String name) {
        this.context = context;
        this.list = list;
        this.name = name;
    }

    @Override
    public int getItemViewType(int position) {
        String from = list.get(position).getFrom();
        if(from.equals(name)){
            return 1;
        }else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0){
            View inflate = LayoutInflater.from(context).inflate(R.layout.left_item, parent, false);
            return  new MyViewHolder(inflate);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.right_item, parent, false);
            return  new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        EMMessage emMessage = list.get(position);
        MyViewHolder viewHolder= (MyViewHolder) holder;
        String message = ((EMTextMessageBody) emMessage.getBody()).getMessage();
        if(itemViewType==0){
            viewHolder.left_text.setText(message);
            Glide.with(context).load(message).into(viewHolder.left_image);
        }else {
            viewHolder.right_text.setText(message);
            Glide.with(context).load(message).into(viewHolder.right_image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class  MyViewHolder extends RecyclerView.ViewHolder{
        private TextView left_text;
        private TextView right_text;
        private ImageView left_image;
        private ImageView right_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            left_text=itemView.findViewById(R.id.left_text);
            left_image=itemView.findViewById(R.id.left_image);
            right_text=itemView.findViewById(R.id.right_text);
            right_image=itemView.findViewById(R.id.right_image);
        }
    }
}
