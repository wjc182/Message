package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagetalk.R;

import java.util.List;


public class RequestAdafel extends RecyclerView.Adapter {
    private Context context;
    private List<String> list;


    public RequestAdafel(Context context, List<String> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_friends_item, parent, false);
        return new MyViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String s = list.get(position);
        MyViewHolder viewHolder= (MyViewHolder) holder;
        viewHolder.name.setText(s);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView reason;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.request_name);
            reason=itemView.findViewById(R.id.request_reason);
        }
    }
}
