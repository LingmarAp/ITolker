package cn.lingmar.common.app;

import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;

import java.io.File;

/**
 * Created by Lingmar on 2017/11/13.
 */

public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 外部获取单例
     *
     * @return 这个类的单例对象
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 获取缓存文件夹地址
     *
     * @return 缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getPortraitTmpFile() {
        // 头像目录的缓存地址
        File dir = new File(getCacheDirFile(), "portrait");
        dir.mkdirs();

        // 删除旧的缓存文件
        File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        // 返回当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址
     *
     * @param isTmp 是否是缓存文件 True-每次返回的地址都为../tmp.mp3，防止用户反复录音产生的大量音频文件
     * @return 声音文件的本地地址
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getAudioTmpFile(boolean isTmp) {
        File dir = new File(getCacheDirFile(), "audio");
        dir.mkdir();

        File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        // 返回当前时间戳的目录文件地址
        File path = new File(getCacheDirFile(), isTmp ? "tmp.mp3" : SystemClock.currentThreadTimeMillis() + ".mp3");

        return path.getAbsoluteFile();
    }

    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，需要进行线程转换
        Run.onUiAsync(() -> Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }

}
