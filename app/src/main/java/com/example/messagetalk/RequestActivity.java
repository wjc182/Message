package com.example.messagetalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.RequestAdafel;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView rewRequest;
    private Button sharedRequest;
    private Button findRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        initView();
    }

    private void initView() {
        rewRequest = (RecyclerView) findViewById(R.id.rew_request);
        sharedRequest = (Button) findViewById(R.id.shared_request);
        findRequest = (Button) findViewById(R.id.find_request);
        rewRequest.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String reason = intent.getStringExtra("reason");
        ArrayList<String> strings = new ArrayList<>();
        strings.add(name+":请求添加你为好友,"+"请求理由:"+reason);

        RequestAdafel requestAdafel = new RequestAdafel(this, strings);
        rewRequest.setAdapter(requestAdafel);
        sharedRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(name);
                    Intent intent1 = new Intent(RequestActivity.this, SharedActivity.class);
                    intent1.putExtra("list",name);
                    startActivity(intent1);
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
        findRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝
                try {
                    EMClient.getInstance().contactManager().declineInvitation(name);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}