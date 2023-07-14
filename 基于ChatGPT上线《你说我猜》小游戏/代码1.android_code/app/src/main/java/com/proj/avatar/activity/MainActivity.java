package com.proj.avatar.activity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.proj.avatar.R;
import com.proj.avatar.entity.User;
import com.proj.avatar.utils.CB;
import com.proj.avatar.utils.ChatGPT;
import com.proj.avatar.zego.RTCMngr;
import com.proj.avatar.zego.runonserver.RunOnServer;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private EditText nickET;
    private boolean isCreator = false;
    private RTCMngr rtcMngr = null;
    private String userId;
//    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nickET = findViewById(R.id.nick);
        findViewById(R.id.enterBtn).setOnClickListener(this);
        findViewById(R.id.createBtn).setOnClickListener(this);
        rtcMngr = RTCMngr.getInstance(getApplication());
        userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        user = new User(userId, userId);

    }
//
//    private void login() {
//        showLoading("正在登录房间...");
////        user.roomId = getInpRoomID();
//        User user = new User(getInpNameID(), getInpRoomID());
//
//        rtcMngr.loginRoom(user, new CB() {
//            @Override
//            public void onResult(boolean succ, String msg) {
//                hideLoading();
//                if (!succ) {
//                    toast(msg);
//                }
//            }
//        });
//    }


    private String getInpRoomID() {
        String roomId = ((EditText) findViewById(R.id.roomId)).getText().toString();
        roomId = roomId.trim();
        if (roomId.length() > 0) return roomId;
        else return null;
    }

    private String getInpNameID() {
        String nick = ((EditText) findViewById(R.id.nick)).getText().toString();
        nick = nick.trim();
        if (nick.length() > 0) return nick;
        else return null;
    }

    private void openRoomActivity() {
        if (!checkPermission()) {
            requestPermission();
            return;
        }

        String nick = nickET.getText().toString();

        Intent intent = new Intent(MainActivity.this, RoomActivity.class);
        intent.putExtra("roomId", getInpRoomID());//加入前缀
        intent.putExtra("isCreator", isCreator);
        intent.putExtra("nick", nick);
        startActivity(intent);


    }


    @Override
    protected void onGrantedAllPermission() {
        openRoomActivity();
    }

    private void loginRoom() {
        showLoading("正在进入房间...");
        User user = new User(getInpNameID(), userId);
        user.roomId = getInpRoomID();
        user.isCreator = isCreator;

        rtcMngr.loginRoom(user, new CB() {
            @Override
            public void onResult(boolean succ, String msg) {
                hideLoading();
                if (succ) {
                    if (isCreator) {
                        rtcMngr.setRoomCreator(getInpRoomID(), userId, new CB() {
                            @Override
                            public void onResult(boolean succ, String msg) {
                                openRoomActivity();
                            }
                        });
                    } else {
                        isCreator = userId.equals(msg);
                        Log.e(TAG, "isCreator:" + isCreator + "," + msg);
                        user.isCreator = isCreator;
                        openRoomActivity();
                    }
                } else {
                    toast(msg);
                }
            }
        });

    }

    private void enterRoom(String roomId) {
        showLoading("正在验证...");
        RunOnServer.checkRoomId(roomId, new CB() {
            @Override
            public void onResult(boolean succ, String msg) {
                hideLoading();
                if (succ) {
                    int num = Integer.parseInt(msg);
                    if (num > 0) {
                        loginRoom();
                    } else {
                        toast("房间不存在！");
                    }
                } else {
                    toast(msg);
                }
            }
        });

    }

    private void createRoom(String roomId) {
        showLoading("正在创建房间");
        RunOnServer.checkRoomId(roomId, new CB() {
            @Override
            public void onResult(boolean succ, String msg) {
                hideLoading();
                if (succ) {
                    int num = Integer.parseInt(msg);
                    if (num > 0) {
                        toast("房间已存在！");
                    } else {
                        loginRoom();
                    }
                } else {
                    toast(msg);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        String roomId = getInpRoomID();
        String nick = getInpNameID();
        if (roomId == null) {
            toast("请输入房间ID!");
            return;
        }
        if (nick == null) {
            toast("请输入昵称!");
            return;
        }
        if (view.getId() == R.id.enterBtn) {
            isCreator = false;
            enterRoom(roomId);
        } else if (view.getId() == R.id.createBtn) {
            isCreator = true;
            createRoom(roomId);
        }
    }
}