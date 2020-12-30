package com.example.messagetalk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddActivity extends AppCompatActivity {

    private EditText addFriends;
    private Button buAddFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView() {
        addFriends = (EditText) findViewById(R.id.add_friends);
        buAddFriends = (Button) findViewById(R.id.bu_add_friends);

        //添加好友
        buAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(addFriends.getText().toString(), "回忆");

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}