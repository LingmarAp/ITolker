package cn.lingmar.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 */
public interface DataSource {

    /**
     * 同时包括了成功与失败的回调接口
     * @param <T>
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback {

    }

    /**
     * 只关注成功的接口
     * @param <T>
     */
    interface SucceedCallback<T> {
        void onDataLoaded(T t);
    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback {
        void onDataNotAvailable(@StringRes int strRes);
    }
}
