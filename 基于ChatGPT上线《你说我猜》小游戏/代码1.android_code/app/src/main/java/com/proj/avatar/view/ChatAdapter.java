package com.proj.avatar.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.proj.avatar.R;
import com.proj.avatar.entity.Msg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemViewHolder> implements View.OnClickListener {
    private static final String TAG = "ChatAdapter";

    public static final String ChatGPT_ID = "chatgpt";
    private OnClickItemListener onClickItemListener;
    public List<Msg> mMsgList;
    private Context context;

    public ChatAdapter(Context context) {
        this.context = context;
        this.mMsgList = new ArrayList<>();

        // Scroll to bottom on new messages
//        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                mManager.smoothScrollToPosition(mMessages, null, mAdapter.getItemCount());
//            }
//        });
    }

    public void addItem(Msg msg) {
        mMsgList.add(msg);
        notifyDataSetChanged();
    }

    public void setData(List<Msg> msgList) {
        mMsgList = msgList;
        notifyDataSetChanged();
    }

    public void scrollToBottom(final RecyclerView recyclerView) {
        // scroll to last item to get the view of last item
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int lastItemPosition = adapter.getItemCount() - 1;

        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // then scroll to specific offset
                View target = layoutManager.findViewByPosition(lastItemPosition);
                if (target != null) {
                    int offset = recyclerView.getMeasuredHeight() - target.getMeasuredHeight();
                    layoutManager.scrollToPositionWithOffset(lastItemPosition, offset);
                }
            }
        });
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewHolder viewHolder;

        View inflate = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        viewHolder = new ItemViewHolder(inflate);

        inflate.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        holder.setRole(msg.fromNick);
        holder.msg.setText(msg.msg);
    }


    @Override
    public int getItemCount() {
        return mMsgList.size();
    }


    @Override
    public void onClick(View v) {
//        if (this.onClickItemListener == null) return;
//        int pos = (Integer) (v.findViewById(R.id.wxid).getTag());
//        this.onClickItemListener.onClickItem(pos);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        FrameLayout root;
        TextView nameTV;
        TextView msg;

        public ItemViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.chatItem);
            nameTV = itemView.findViewById(R.id.uname);
            msg = itemView.findViewById(R.id.msg);
        }

        public void setRole(String name) {
            nameTV.setText(name);
        }

        public int dp2px(float dpValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

    }


    public void setOnClickItemListener(OnClickItemListener onClickItemBtnListener) {
        this.onClickItemListener = onClickItemBtnListener;
    }

    public interface OnClickItemListener {
        void onClickItem(int position);
    }
}
