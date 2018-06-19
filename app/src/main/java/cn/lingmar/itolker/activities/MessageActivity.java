package cn.lingmar.itolker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import cn.lingmar.common.app.Activity;
import cn.lingmar.common.app.Fragment;
import cn.lingmar.factory.model.Author;
import cn.lingmar.factory.model.db.Group;
import cn.lingmar.factory.model.db.Message;
import cn.lingmar.factory.model.db.Session;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.message.ChatGroupFragment;
import cn.lingmar.itolker.frags.message.ChatUserFragment;

public class MessageActivity extends Activity {
    // 接受者Id
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    // 标志为群还是用户
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 显示人的聊天界面
     *
     * @param context 上下文
     * @param session 人的信息
     */
    public static void show(Context context, Session session) {
        if (context == null || session == null || TextUtils.isEmpty(session.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,
                session.getReceiverType() == Message.RECEIVER_TYPE_GROUP);

        context.startActivity(intent);
    }

    /**
     * 显示人的聊天界面
     *
     * @param context 上下文
     * @param author  人的信息
     */
    public static void show(Context context, Author author) {
        if (context == null || author == null || TextUtils.isEmpty(author.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);

        context.startActivity(intent);
    }

    public static void show(Context context, Group group) {
        if (context == null || group == null || TextUtils.isEmpty(group.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);

        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if (mIsGroup)
            fragment = new ChatGroupFragment();
        else
            fragment = new ChatUserFragment();

        // 从Activity传递参数到Fragment中去
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }
}
