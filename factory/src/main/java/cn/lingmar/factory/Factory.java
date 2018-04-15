package cn.lingmar.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.lingmar.common.app.Application;

public class Factory {
    // 单例模式
    private static final Factory instance;
    // 全局的线程池
    private final Executor executor;
    // 全局的Gson
    private final Gson gson;

    static {
        instance = new Factory();
    }

    private Factory() {
        // 创建一个4线程的线程池
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // TODO 设置一个过滤器，数据库级别的Model不进行Json转换
                // .setExclusionStrategies()
                .create();
    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步运行的方法
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable) {
        // 拿到单例-->拿到线程池-->异步执行
        instance.executor.execute(runnable);
    }

    public static Gson getGson() {
        return instance.gson;
    }
}
