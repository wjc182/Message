package com.example.messagetalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.SharedAdafel;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class SharedActivity extends AppCompatActivity {

    private EditText sharedFriends;
    private Button findFriends;

    private RecyclerView rewShared;
    private Button jumpTalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        initView();
    }

    private void initView() {
        sharedFriends = (EditText) findViewById(R.id.shared_friends);
        findFriends = (Button) findViewById(R.id.find_friends);
        rewShared = (RecyclerView) findViewById(R.id.rew_shared);

        rewShared.setLayoutManager(new LinearLayoutManager(this));

        //集合
        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加好友
                startActivity(new Intent(SharedActivity.this, AddActivity.class));
            }
        });
        jumpTalk = (Button) findViewById(R.id.jump_talk);

        jumpTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SharedActivity.this,TalkActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            Intent intent = getIntent();
            //得到搜索添加好友的名字
            String name = intent.getStringExtra("name");

            Log.e("TT",name);
            //存入集合
            usernames.add(name);

            SharedAdafel sharedAdafel = new SharedAdafel(this, usernames);
            //设置适配器
            rewShared.setAdapter(sharedAdafel);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //刷新适配器
                    sharedAdafel.notifyDataSetChanged();
                }
            });
            //监听跳转对话页面
            sharedAdafel.setItemListener(new SharedAdafel.ItemListener() {
                @Override
                public void itemClick(int pos) {
                    //点击对应好友聊天
                    String s = usernames.get(pos);
                    //传值
                    Intent intent1 = new Intent(SharedActivity.this, TalkActivity.class);
                    intent1.putExtra("name", s);
                    startActivity(intent1);
                }
            });
        } catch (HyphenateException e) {
            e.printStackTrace();
            Log.e("TTT",e.getMessage());
        }
    }
}