package cn.lingmar.itolker.activities;

import android.content.Context;
import android.content.Intent;

import cn.lingmar.common.app.ToolbarActivity;
import cn.lingmar.itolker.R;

public class GroupCreateActivity extends ToolbarActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }
}
