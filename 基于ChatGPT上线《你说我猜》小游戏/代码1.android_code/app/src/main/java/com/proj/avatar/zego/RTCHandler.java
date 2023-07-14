package com.proj.avatar.zego;

import android.util.Log;

import com.proj.avatar.entity.Msg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoBarrageMessageInfo;
import im.zego.zegoexpress.entity.ZegoRoomExtraInfo;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;

public class RTCHandler extends IZegoEventHandler {

    private static final String TAG = "RTCHandler";
    private IRTCEventListener listener;

    public interface IRTCEventListener {
        void onRoomTokenWillExpire(String roomID);

        void onAddStream(ArrayList<ZegoStream> streams);

        void onRcvBarrage(List<Msg> msg);

        void onUserCntUpdate(int cnt);

        void onUserUpdate(boolean isAdd, String userId, String userName);

        void onRcvRoomInfo(Map<String, String> info);
    }

    public RTCHandler(IRTCEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRoomExtraInfoUpdate(String roomID, ArrayList<ZegoRoomExtraInfo> roomExtraInfoList) {
        super.onRoomExtraInfoUpdate(roomID, roomExtraInfoList);
        if (listener == null) return;
        Map<String, String> map = new HashMap<>();
        for (ZegoRoomExtraInfo info : roomExtraInfoList) {
            map.put(info.key, info.value);
        }
        listener.onRcvRoomInfo(map);
    }


    @Override
    public void onRoomOnlineUserCountUpdate(String roomID, int count) {
        super.onRoomOnlineUserCountUpdate(roomID, count);
        if (listener == null) return;
        listener.onUserCntUpdate(count);
    }

    @Override
    public void onIMRecvBarrageMessage(String roomID, ArrayList<ZegoBarrageMessageInfo> messageList) {
        super.onIMRecvBarrageMessage(roomID, messageList);
        if (listener == null) return;
        List<Msg> msgs = new ArrayList<>();
        for (ZegoBarrageMessageInfo zbmi : messageList) {
            Msg msg = Msg.parseMsg(zbmi.message, zbmi.fromUser.userID, zbmi.fromUser.userName, roomID);
            msgs.add(msg);
        }
        listener.onRcvBarrage(msgs);
    }

    @Override
    public void onRoomUserUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoUser> userList) {
        super.onRoomUserUpdate(roomID, updateType, userList);

        if (listener == null) return;
        if (updateType == ZegoUpdateType.ADD) {
            for (ZegoUser user : userList) {
                listener.onUserUpdate(true, user.userID, user.userName);
            }
        } else if (updateType == ZegoUpdateType.DELETE) {
            for (ZegoUser user : userList) {
                listener.onUserUpdate(false, user.userID, user.userName);
            }
        }
    }

    @Override
    public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
        if (updateType == ZegoUpdateType.ADD) {
            if (listener != null) listener.onAddStream(streamList);
        }
    }

    @Override
    public void onRoomTokenWillExpire(String roomID, int remainTimeInSecond) {
        if (listener != null) listener.onRoomTokenWillExpire(roomID);
    }
}
