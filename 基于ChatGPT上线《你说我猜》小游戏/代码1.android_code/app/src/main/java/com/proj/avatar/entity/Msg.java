package com.proj.avatar.entity;

import android.util.Log;

import java.util.Date;

public class Msg {
    public String msg;
    public String decMsg;
    public String toUID;
    public String fromUID;
    public MsgProto proto;
    public String fromNick;
    public int intExt;


    public enum MsgProto {
        Msg,
        OnlineUser,
        Round

    }

    public Msg(MsgProto proto, String msg, String fromUID, String toUID, String fromNick) {
        this.msg = msg;
        this.fromUID = fromUID;
        this.toUID = toUID;
        this.proto = proto;
        this.fromNick = fromNick;
    }


    private Msg() {
    }


    public static Msg newRoomMsg(String roomId, String text, String fromUID, String fromNick) {
        Msg msg = new Msg(MsgProto.Msg, text, fromUID, roomId, fromNick);
        msg.decMsg = "msg://" + text;
        return msg;
    }

    public static Msg newOnlineMsg(String roomId, String fromUID, String fromNick) {
        Msg msg = new Msg(MsgProto.OnlineUser, fromUID + "," + fromNick, fromUID, roomId, fromNick);
        msg.decMsg = "online://" + fromUID + "," + fromNick;
        return msg;
    }

    public static Msg newRoundMsg(String roomId, String text, String fromUID) {
        Msg msg = new Msg(MsgProto.Round, text, fromUID, roomId, "system");
        msg.decMsg = "round://" + text;
        return msg;
    }

    public static Msg parseMsg(String txt, String fromUID, String fromNick, String toUID) {

        Msg msg = new Msg();
        msg.fromUID = fromUID;
        msg.toUID = toUID;
        msg.fromNick = fromNick;
        msg.decMsg = txt;
//        Log.e("Msg", "ori txt->" + txt);
        if (txt.startsWith("msg://")) {
            msg.msg = txt.substring("msg://".length());
            msg.proto = MsgProto.Msg;

        } else if (txt.startsWith("online://")) {
//            Log.e("Msg", "收到上线消息：" + txt);
            msg.msg = txt.substring("online://".length());
            String[] ss = msg.msg.split(",");
            msg.fromUID = ss[0];
            msg.fromNick = ss[1];
            msg.proto = MsgProto.OnlineUser;
        } else if (txt.startsWith("round://")) {
            msg.msg = txt.substring("round://".length());
            msg.proto = MsgProto.Round;
        } else {
            return null;
        }
        return msg;
    }

}
