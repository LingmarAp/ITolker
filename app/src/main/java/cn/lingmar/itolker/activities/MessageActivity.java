package cn.lingmar.itolker.activities;

import android.content.Context;
import android.content.Intent;

import cn.lingmar.common.app.Activity;
import cn.lingmar.factory.model.Author;
import cn.lingmar.itolker.R;

public class MessageActivity extends Activity {

    /**
     * 显示人的聊天界面
     * @param context 上下文
     * @param author 人的信息
     */
    public static void show(Context context, Author author) {
        context.startActivity(new Intent(context, MessageActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }
}
