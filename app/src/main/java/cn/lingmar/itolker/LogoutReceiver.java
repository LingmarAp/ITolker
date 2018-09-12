package cn.lingmar.itolker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lingmar.common.app.Activity;
import cn.lingmar.factory.Factory;
import cn.lingmar.itolker.activities.AccountActivity;

public class LogoutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null)
            return ;

        // TODO 退出账户并进入登录界面
        // TODO 清理当前数据库信息 && 清理当前登录用户信息 && 关闭对比界面

        Activity currentActivity = Activity.getCurrentActivity();
        AccountActivity.show(Factory.app().getApplicationContext());

        if(currentActivity != null)
            currentActivity.finish();
    }
}
