package cn.lingmar.itolker.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * 解决Fragment调度与重用的问题
 * 优化Fragment的切换
 */
public class NavHelper<T> {
    // 所有的Tab
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();

    // 初始化参数
    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private final OnTabChangedListener<T> listener;

    // 当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId,
                     FragmentManager fragmentManager,
                     OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);

        return this;
    }

    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     *
     * @param menuId
     * @return 是否能够处理点击
     */
    public boolean performClickMenu(int menuId) {
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }

        return false;
    }

    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;

        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                // 如果当前Tab就是用户点击的Tab
                // 则进行信息刷新
                notifyTabReselect(tab);
                return;
            }
        }

        currentTab = tab;
        doTabChanged(tab, oldTab);
    }

    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction fgTrans = fragmentManager.beginTransaction();

        if (oldTab != null && oldTab.fragment != null) {
            // 单从界面移除，但仍在缓存中
            fgTrans.detach(oldTab.fragment);
        }

        if (newTab != null && newTab.fragment == null) {
            Fragment f = Fragment.instantiate(context, newTab.clx.getName());
            newTab.fragment = f;
            fgTrans.add(containerId, f, newTab.clx.getName());
        } else {
            fgTrans.attach(newTab.fragment);
        }

        fgTrans.commit();
        // 通知回调
        notifyTabSelect(newTab, oldTab);
    }

    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab) {

    }

    public static class Tab<T> {
        public Tab(Class<? extends Fragment> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        public Class<? extends Fragment> clx;
        // 用户设定需要使用的额外字段
        public T extra;

        // 内部缓存的Fragment
        private Fragment fragment;
    }

    /**
     * 定义事件处理完成的回调接口
     *
     * @param <T>
     */
    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
