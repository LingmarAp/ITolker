package cn.lingmar.itolker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.helper.AccountHelper;
import cn.lingmar.factory.persistence.Account;

/**
 * 个推消息接收器
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();
        // 判断消息意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID: {
                Log.i(TAG, "GET_CLIENTID: " + bundle.toString());
                // 当Id初始化的时候
                // 获取设备Id
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA: {
                // 常规消息送达
                byte[] payloads = bundle.getByteArray("payload");
                if (payloads != null) {
                    String msg = new String(payloads);
                    Log.i(TAG, "GET_MSG_DATA: " + msg);
                    onMessageArrived(msg);
                }
                break;
            }
            default:
                Log.i(TAG, "OTHER: " + bundle.toString());
                break;
        }
    }

    /**
     * 当Id初始化的时候
     *
     * @param cid 设备Id
     */
    private void onClientInit(String cid) {
        // 设置设备Id
        Account.setPushId(cid);
        // 没有登录是不能绑定PushId的
        if(Account.isLogin()) {
            // 账户登录状态，进行一次PushId绑定
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息到达时
     * @param msg 新消息
     */
    private void onMessageArrived(String msg) {
        // 交由Factory处理
        Factory.dispatchPush(msg);
    }

}
