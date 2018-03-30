package cn.lingmar.itolker.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * Created by Lingmar on 2018/3/26.
 */

public class NavTest<T> {
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();

    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private final OnTabChangedListener<T> listener;

    // 当前选中的Tab
    private Tab<T> currentTab;

    public NavTest(Context context, int containerId,
                   FragmentManager fragmentManager,
                   OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    public NavTest<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);

        return this;
    }

    public Tab<T> getCurrentTab() {
        return currentTab;
    }

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
                // 则进行刷新
                notifyTabReselect(tab);
            }
        }

        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    private void doTabChanged(Tab<T> currentTab, Tab<T> oldTab) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (oldTab != null && oldTab.fragment != null) {
            // 单从界面移除，但仍在缓存中
            ft.detach(oldTab.fragment);
        }

        if (currentTab != null && currentTab.fragment == null) {
            Fragment fragment = Fragment.instantiate(context, currentTab.clx.getName());
            currentTab.fragment = fragment;
            ft.add(containerId, currentTab.fragment, currentTab.clx.getName());
        } else {
            ft.attach(currentTab.fragment);
        }

        ft.commit();
        // 通知回调
        notifyTabSelect(currentTab, oldTab);
    }

    private void notifyTabSelect(Tab<T> currentTab, Tab<T> oldTab) {
        if(listener != null) {
            listener.onTabChanged(currentTab, oldTab);
        }
    }

    private void notifyTabReSelect(Tab<T> tab) {
        // TODO 二次点击Tab后的回调
    }

    private void notifyTabReselect(Tab<T> tab) {
        // TODO 二次点击Tab所进行的操作
    }

    public static class Tab<T> {
        public Class<? extends Fragment> clx;
        public T extra;

        public Tab(Class<? extends Fragment> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        private Fragment fragment;
    }

    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
