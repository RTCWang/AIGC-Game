package com.proj.avatar.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proj.avatar.R;
import com.proj.avatar.dm.DanmuAdapter;
import com.proj.avatar.dm.DanmuContainerView;
import com.proj.avatar.entity.Msg;
import com.proj.avatar.entity.User;
import com.proj.avatar.utils.CB;
import com.proj.avatar.utils.ChatGPT;
import com.proj.avatar.view.ChatAdapter;
import com.proj.avatar.view.KeyboardLayout;
import com.proj.avatar.view.ShowUtils;
import com.proj.avatar.zego.IListener;
import com.proj.avatar.zego.RTCMngr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RoomActivity extends BaseActivity implements KeyboardLayout.KeyboardLayoutListener, View.OnClickListener, IListener {
    private final static String TAG = "RoomActivity";
    private User mUser;
    private LinearLayout bottomBar;
    private DanmuContainerView danmuView;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText sendET;
    private Button sendBtn;
    private ChatGPT chatGPT;
    private TextView tipsTV;
    private ImageView refreshTV;
    private ImageView iv1, iv2, iv3, iv4;
    private TextView tv1, tv2, tv3, tv4;
    private String wordAns = "";
    private Set<String> onlineIds = new HashSet<>();
    private RTCMngr rtcMngr = RTCMngr.getInstance(getApplication());
    private int[] headers = {R.drawable.wm01, R.drawable.wm02, R.drawable.wm03, R.drawable.wm04, R.drawable.wm05, R.drawable.m01, R.drawable.m02, R.drawable.m03, R.drawable.m04, R.drawable.m05};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        bottomBar = findViewById(R.id.bottomBar);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        danmuView = findViewById(R.id.danmu);
        danmuView.setAdapter(new DanmuAdapter(this));
        initData();
        initView();

    }


    private void initView() {


        adapter = new ChatAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rclView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        sendBtn = findViewById(R.id.sendBtn);
        sendET = findViewById(R.id.sendEt);
        tipsTV = findViewById(R.id.tips);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv4 = findViewById(R.id.iv4);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        sendBtn.setOnClickListener(this);
        refreshTV = findViewById(R.id.refresh);
        refreshTV.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqExit(0);
            }
        });

    }

    private void initData() {

        Intent intent = getIntent();
        String roomId = intent.getStringExtra("roomId");
        String nick = intent.getStringExtra("nick");
        boolean isCreator = intent.getBooleanExtra("isCreator", false);


        chatGPT = ChatGPT.getInstance();
        ((KeyboardLayout) findViewById(R.id.keyboard_layout)).setKeyboardListener(this);
        String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mUser = new User(userId, userId);
        mUser.isCreator = isCreator;
        mUser.roomId = roomId;
        mUser.streamId = userId;
        mUser.userName = nick;

        rtcMngr.setMsgListener(this);

    }

    @Override
    public void notifyUpdateRoomInfo() {
        Log.e(TAG, "update room Info");
        String hostId = rtcMngr.getHostId();
        if (!mUser.isCreator)
            mUser.isCreator = (hostId != null) && (hostId.equals(mUser.userId));
        if (mUser.isCreator) {
            sendET.setHint("请输入猜词");
            tipsTV.setText("您是房主，请输入猜题词");
            tipsTV.setTextColor(Color.RED);
            rtcMngr.pushStream(mUser.streamId);
            refreshTV.setVisibility(View.VISIBLE);
            sendBtn.setText("设词");
        } else {
            sendET.setHint("请输入答案");
            tipsTV.setText("等待房主设置题目");
            sendBtn.setText("猜词");
            refreshTV.setVisibility(View.GONE);
        }

        Log.e(TAG, "isHost:" + mUser.isCreator);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notifyUpdateRoomInfo();
    }

    //添加实时消息
    private void addBarrageItem(Msg msg) {
        adapter.addItem(msg);
        adapter.scrollToBottom(recyclerView);
    }

    @Override
    public void onUserUpdate(boolean isAdd, String userId, String userName) {
        Log.e(TAG, isAdd + "<>" + userName);
        if (isAdd) {
            addBarrageItem(Msg.newRoomMsg(mUser.roomId, "来了", mUser.userId, userName));
        } else {
            if (userId.equals(rtcMngr.getHostId())) {
                reqExit(4);
            } else {
                addBarrageItem(Msg.newRoomMsg(mUser.roomId, "离开房间", mUser.userId, userName));
            }
        }
    }

    private void reqGPT() {
        if (wordAns.length() <= 0) return;
        chatGPT.ask("请描述" + wordAns + ",10个字以内，不要出现" + wordAns + wordAns.length() + "个字", new CB() {
            @Override
            public void onResult(boolean succ, String res) {
                if (!succ) {
                    toast(res);
                } else {
                    Msg msg = Msg.newRoundMsg(mUser.roomId, res, mUser.userId);
                    rtcMngr.sendMsg(msg, new CB() {
                        @Override
                        public void onResult(boolean succ, String err) {
                            onlineIds.clear();
                            onNewRound(msg);
                        }
                    });

                }
            }
        });
    }

    /**
     * 重新开一局，需要传入猜词
     */
    private void newRound(String target) {
        wordAns = target;
        reqGPT();
    }

    /**
     * 点击发送按钮
     */
    public void onClkSendMsgBtn(View view) {
        String txt = sendET.getText().toString().trim();
        if (txt.length() <= 0) return;
        Msg msg = Msg.newRoomMsg(mUser.roomId, txt, mUser.userId, mUser.userName);
        if (mUser.isCreator) {//房主重新设置猜测词
            newRound(txt);
        } else {
            rtcMngr.sendMsg(msg, new CB() {
                @Override
                public void onResult(boolean succ, String err) {
                    addBarrageItem(msg);
                }
            });
        }
        sendET.setText("");
    }


    /**
     * 只有房主才能验证答案
     */
    private void checkAns(String userId, String userName, String ans) {
        if (onlineIds.size() >= 4 || onlineIds.contains(userId)) return;

        if (wordAns.equals(ans)) {
            onlineIds.add(userId);
            Msg msg = Msg.newOnlineMsg(mUser.roomId, userId, userName);
            rtcMngr.sendMsg(msg, new CB() {
                @Override
                public void onResult(boolean succ, String msg) {
                    onNewOnline(userId, userName);
                }
            });
        }
    }

    private void onNewRound(Msg msg) {
        tipsTV.setText(msg.msg);
        ImageView[] ivs = {iv1, iv2, iv3, iv4};
        TextView[] tvs = {tv1, tv2, tv3, tv4};
        for (ImageView iv : ivs) {
            iv.setImageResource(R.drawable.none_user);
            iv.setTag(false);
        }
        for (TextView iv : tvs) {
            iv.setText("无");
        }
    }

    private void onNewOnline(String userId, String userName) {
        //确定头像，实际项目中应该从服务器读取
        int code = userId.hashCode();
        int idx = code % headers.length;
        int head = headers[idx];
        ImageView[] ivs = {iv1, iv2, iv3, iv4};
        TextView[] tvs = {tv1, tv2, tv3, tv4};
        for (int i = 0; i < 4; ++i) {
            ImageView iv = ivs[i];
            boolean tag = (boolean) iv.getTag();
            if (!tag) {
                iv.setImageResource(head);
                iv.setTag(true);
                tvs[i].setText(userName);
                break;
            }
        }

    }

    //接收到文字消息
    @Override
    public void onRcvMsg(Msg msg) {
        Log.e(TAG, "rct" + msg.proto + "," + msg.decMsg + "," + msg.fromNick);
        switch (msg.proto) {
            case Msg: {
                if (mUser.isCreator) {
                    checkAns(msg.fromUID, msg.fromNick, msg.msg);
                }
                addBarrageItem(msg);
                break;

            }
            case OnlineUser: {
                onNewOnline(msg.fromUID, msg.fromNick);
                break;
            }
            case Round: {
                onNewRound(msg);
                break;
            }
        }

    }

    @Override
    public void onUserCntUpdate(int cnt) {
        //
        setTitle("你说我猜(" + cnt + ")");
    }


    public int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        FrameLayout.LayoutParams rcycParams = (FrameLayout.LayoutParams) recyclerView.getLayoutParams();

        if (isActive) {
            params.bottomMargin = keyboardHeight + dp2px(40);
            rcycParams.bottomMargin = keyboardHeight + dp2px(90);
        } else {
            params.bottomMargin = dp2px(10);
            rcycParams.bottomMargin = dp2px(70);
        }
        bottomBar.setLayoutParams(params);
        recyclerView.setLayoutParams(rcycParams);
    }


    @Override
    public void onClick(View view) { //目前只有点击返回箭头才有回调，如果增加其他点击事件注意要添加if/else判断
        switch (view.getId()) {
            case R.id.sendBtn: {
                onClkSendMsgBtn(view);
                break;
            }
            case R.id.refresh: {
                reqGPT();
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        reqExit(0);
    }


    private void reqExit(int code) {
        switch (code) {
            case 0: {//press back
                ShowUtils.comfirm(this, "提示", "确定离开直播间？", "确定", new ShowUtils.OnClickOkCancelListener() {
                    @Override
                    public void onOk() {
                        reqExit(1);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            }
            case 1: {//exit direct
                rtcMngr.leaveRoom(mUser.roomId, new CB() {
                    @Override
                    public void onResult(boolean succ, String msg) {

                        if (succ) {
                            RoomActivity.super.onBackPressed();
                        } else {
                            toast("退出失败！");
                        }
                    }
                });
                break;
            }
            case 4: {//被迫退出
                rtcMngr.leaveRoom(mUser.roomId, new CB() {
                    @Override
                    public void onResult(boolean succ, String msg) {
                        ShowUtils.alert(RoomActivity.this, "提示", "房间已解散！", new ShowUtils.OnClickOkListener() {
                            @Override
                            public void onOk() {
                                RoomActivity.super.onBackPressed();
                            }
                        });
                    }
                });
                break;
            }
        }

    }
}