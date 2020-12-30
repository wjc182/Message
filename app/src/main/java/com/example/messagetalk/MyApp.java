package com.example.messagetalk;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.util.EMLog;

import java.util.Iterator;
import java.util.List;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //添加好友
        friends();
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
    }

    private void friends() {
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e("TAG", "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

//初始化
        EMClient.getInstance().init(this, options);

//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.e("TAG","收到好友请求");
                NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String id="11";
                String name="苑";
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
                    service.createNotificationChannel(channel);
                }
                Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
                intent.putExtra("name",username);
                intent.putExtra("reason",reason);
                PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 10, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification notification = new NotificationCompat.Builder(MyApp.this, id)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("通知")
                        .setAutoCancel(true)
                        .setContentTitle("你收到好友请求")
                        .setContentIntent(activity)
                        .build();
                service.notify(1,notification);
            }

            @Override
            public void onFriendRequestAccepted(String username) {
                //好友请求被同意
                Log.e("TAG","好友请求被同意");

            }

            @Override
            public void onFriendRequestDeclined(String username) {
                //好友请求被拒绝
                Log.e("TAG","好友请求拒绝");
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法

            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.e("TAG","增加联系人"+username);
            }
        });
    }



    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.e("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    //实现ConnectionListener接口
    /**
     * 在聊天过程中难免会遇到网络问题，在此 SDK 为您提供了网络监听接口，实时监听
     */
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(int error) {
            EMLog.e("TAG1", "onDisconnect" + error);
            if (error == EMError.USER_REMOVED) {
                onUserException("Constant.ACCOUNT_REMOVED");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                onUserException("Constant.ACCOUNT_CONFLICT");
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                onUserException("Constant.ACCOUNT_FORBIDDEN");
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                onUserException("Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD");
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                onUserException("Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE");
            }
        }
    }


    /**
     * user met some exception: conflict, removed or forbidden， goto login activity
     */
    protected void onUserException(String exception){
        EMLog.e("TAG", "onUserException: " + exception);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(exception, true);
        this.startActivity(intent);
        showToast(exception);
    }
    //出现异常方法
    private void showToast(String exception) {
        Log.e("TAG2","异常："+exception);
    }


}
