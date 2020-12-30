package com.example.messagetalk;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adapter.RecAdapter;
import com.example.bean.UpdataFileBean;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TalkActivity extends AppCompatActivity {

    private EditText edMessage;
    private Button sendMessage;
    private RecyclerView rec;
    private Toolbar toolbar;
    private RecAdapter recAdapter;
    private String name;
    private List<EMMessage> emMessages;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        initView();
        //聊天记录
        initData();
    }


    private void initView() {
        edMessage = (EditText) findViewById(R.id.ed_message);
        sendMessage = (Button) findViewById(R.id.send_message);
        rec = (RecyclerView) findViewById(R.id.te_mess);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        emMessages = new ArrayList<>();

        rec.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        //传来姓名
        name = intent.getStringExtra("name");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edMessage.getText().toString().isEmpty()){

                    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                    EMMessage message = EMMessage.createTxtSendMessage(edMessage.getText().toString(), "1");
                    //发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);

                    emMessages.add(message);

                    edMessage.setText("");
                    recAdapter = new RecAdapter(TalkActivity.this, emMessages,"1");
                    rec.setAdapter(recAdapter);
                    recAdapter.notifyDataSetChanged();
                    Log.e("TAG", message.getFrom());
                }else {
                    //弹窗
                    View inflate = LayoutInflater.from(TalkActivity.this).inflate(R.layout.phone_popu_item, null);
                    Button phone = inflate.findViewById(R.id.popu_image);
                    Button camera = inflate.findViewById(R.id.popu_camera);
                    PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new ColorDrawable());
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(inflate, Gravity.BOTTOM,0,0);
                    phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //打开相册
                            Intent intent1 = new Intent(Intent.ACTION_PICK);
                            intent1.setType("image/*");
                            startActivityForResult(intent1, 1);
                            //imageUri为图片本地资源标志符，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
                            Toast.makeText(TalkActivity.this, "23456789", Toast.LENGTH_SHORT).show();

                            if(EMMessage.ChatType.Chat.equals(EMMessage.Type.IMAGE)){

                                EMMessage imageSendMessage = EMMessage.createImageSendMessage(imageUri.toString(), false, "1");
                                //发送消息
                                EMClient.getInstance().chatManager().sendMessage(imageSendMessage);
                                emMessages.add(imageSendMessage);
                                recAdapter = new RecAdapter(TalkActivity.this, emMessages,"1");
                                rec.setAdapter(recAdapter);
                                recAdapter.notifyDataSetChanged();
                                Log.e("TAG", imageSendMessage.getFrom());
                            }
                            popupWindow.dismiss();
                        }
                    });
                }
            //打印
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //相册
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
            }
        }
    }

//聊天记录
    private void initData() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(name);
        if (conversation != null) {
            //加载所有消息
            List<EMMessage> messages1 = conversation.getAllMessages();
            //获取回话前的消息
            List<EMMessage> emMessages2 = conversation.loadMoreMsgFromDB(messages1.get(messages1.size() - 1).getMsgId(), 5);
            emMessages.addAll(emMessages2);
            recAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(TalkActivity.this, "无历史消息", Toast.LENGTH_SHORT).show();
        }

    }

    //接收消息监听事件
    EMMessageListener msgListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            emMessages.addAll(messages);
            recAdapter.notifyDataSetChanged();
            for (EMMessage mm : messages) {
                EMImageMessageBody imgBody = (EMImageMessageBody) mm.getBody();
//获取图片文件在服务器的路径
                String imgRemoteUrl = imgBody.getRemoteUrl();
//获取图片缩略图在服务器的路径
                String thumbnailUrl = imgBody.getThumbnailUrl();
//本地图片文件的资源路径
                Uri imgLocalUri = imgBody.getLocalUri();
//本地图片缩略图资源路径
                Uri thumbnailLocalUri = imgBody.thumbnailLocalUri();

            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };


    //注册
    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}