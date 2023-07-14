package com.proj.avatar.zego;

import android.app.Application;
import android.util.Log;

import com.proj.avatar.entity.Msg;
import com.proj.avatar.entity.User;
import com.proj.avatar.utils.CB;
import com.proj.avatar.zego.runonserver.KeyCenter;
import com.proj.avatar.zego.runonserver.RunOnServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendBarrageMessageCallback;
import im.zego.zegoexpress.callback.IZegoRoomLogoutCallback;
import im.zego.zegoexpress.callback.IZegoRoomSetRoomExtraInfoCallback;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegoexpress.entity.ZegoVideoConfig;


public class RTCMngr implements RTCHandler.IRTCEventListener {

    private static final String TAG = "RTCMngr";
    private ZegoExpressEngine mRTCEngine;
    private static RTCMngr mInstance;
    private RTCHandler mRTCHandler = new RTCHandler(this);
    private ZegoVideoConfig videoCfg = null;
    private IListener msgListener;
    private static Map<String, String> roomInfo = new HashMap<>();

    private RTCMngr(Application app) {
        mRTCEngine = createRTCEngine(app, mRTCHandler);
    }

    public static RTCMngr getInstance(Application app) {
        if (null == mInstance) {
            synchronized (RTCMngr.class) {
                if (null == mInstance) {
                    mInstance = new RTCMngr(app);
                }
            }
        }
        return mInstance;
    }

    private ZegoExpressEngine createRTCEngine(Application app, IZegoEventHandler handler) {
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = KeyCenter.APP_ID;
        profile.scenario = ZegoScenario.GENERAL;  // 通用场景接入
        profile.application = app;
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, handler);
        return engine;
    }

    public void setMsgListener(IListener msgListener) {
        this.msgListener = msgListener;
    }

    public void pushStream(String streamId) {
        Log.e(TAG, "push streamID" + streamId);
        mRTCEngine.startPublishingStream(streamId);
        mRTCEngine.startPreview(null);

    }


    public void loginRoom(User user, CB cb) {

        ZegoUser zuser = new ZegoUser(user.userId, user.userName);
        ZegoRoomConfig config = new ZegoRoomConfig();
        config.token = RunOnServer.getToken(user.userId, user.roomId); // 请求开发者服务端获取
        config.isUserStatusNotify = true;
        mRTCEngine.loginRoom(user.roomId, zuser, config, (int error, JSONObject extendedData) -> {
            if (error == 0) {
                Log.e(TAG, "登录房间：" + user.roomId);
                String hostId = null;
                try {
                    hostId = extendedData.getString("hostId");
                } catch (JSONException e) {
                    hostId = "";
                }
                cb._complete(error == 0, hostId);
            } else {
                Log.e(TAG, "Login Error, errorCode=" + error);
                cb._complete(error == 0, "login room error code:" + error);
            }
        });

    }


    public void setRoomCreator(String roomId, String creatorId, CB cb) {
        mRTCEngine.setRoomExtraInfo(roomId, "hostId", creatorId, new IZegoRoomSetRoomExtraInfoCallback() {
            @Override
            public void onRoomSetRoomExtraInfoResult(int errorCode) {
                cb._complete(errorCode == 0, "");
            }
        });
    }


    public void leaveRoom(String roomId, CB cb) {
        mRTCEngine.stopPreview();
        mRTCEngine.stopPublishingStream();
        mRTCEngine.logoutRoom(roomId, new IZegoRoomLogoutCallback() {
            @Override
            public void onRoomLogoutResult(int errorCode, JSONObject extendedData) {
                cb._complete(errorCode == 0, "");
            }
        });
    }

    /**
     * 发送弹幕消息
     */
    public void sendMsg(Msg msg, CB cb) {
//        Log.e(TAG, "send:" + msg.decMsg);
        mRTCEngine.sendBarrageMessage(msg.toUID,
                msg.decMsg, new IZegoIMSendBarrageMessageCallback() {
                    @Override
                    public void onIMSendBarrageMessageResult(int errorCode, String messageID) {
                        cb._complete(errorCode == 0, messageID);
                    }
                });

    }

    @Override
    public void onRoomTokenWillExpire(String roomID) {
        mRTCEngine.renewToken(roomID, RunOnServer.reToken());
    }

    @Override
    public void onAddStream(ArrayList<ZegoStream> streams) {
        Log.e(TAG, "on Add Stream");
        String streamId = streams.get(0).streamID;
        mRTCEngine.startPlayingStream(streamId);

    }

    @Override
    public void onRcvBarrage(List<Msg> msgs) {
        if (msgListener == null) return;
        for (Msg msg : msgs) {
            msgListener.onRcvMsg(msg);
        }
    }

    @Override
    public void onUserCntUpdate(int cnt) {
        if (msgListener == null) return;
        msgListener.onUserCntUpdate(cnt);
    }

    @Override
    public void onUserUpdate(boolean isAdd, String userId, String userName) {
        if (msgListener == null) return;
        msgListener.onUserUpdate(isAdd, userId, userName);
    }

    @Override
    public void onRcvRoomInfo(Map<String, String> info) {
        roomInfo = info;
        if (msgListener == null) return;
        msgListener.notifyUpdateRoomInfo();
    }


    public String getHostId() {
        return roomInfo.get("hostId");
    }
}
