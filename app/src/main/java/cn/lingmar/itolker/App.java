package cn.lingmar.itolker;

import com.igexin.sdk.PushManager;

import cn.lingmar.common.app.Application;
import cn.lingmar.factory.Factory;

/**
 * Created by Lingmar on 2017/11/13.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 调用Factory进行初始化
        Factory.setup();
        // 推送进行初始化
        PushManager.getInstance().initialize(this);
    }
}
