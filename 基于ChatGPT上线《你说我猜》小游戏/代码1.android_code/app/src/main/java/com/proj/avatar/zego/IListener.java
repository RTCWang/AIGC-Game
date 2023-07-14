package com.proj.avatar.zego;


import com.proj.avatar.entity.Msg;

import java.util.Map;

public interface IListener {

    void onRcvMsg(Msg msg);

    void onUserCntUpdate(int cnt);

    void onUserUpdate(boolean isAdd, String userId, String userName);

    void notifyUpdateRoomInfo();
}
