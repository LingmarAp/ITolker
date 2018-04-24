package cn.lingmar.itolker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lingmar.common.app.Activity;
import cn.lingmar.common.widget.PortraitView;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.assist.PermissionsFragment;
import cn.lingmar.itolker.frags.main.AcitveFragment;
import cn.lingmar.itolker.frags.main.ContactFragment;
import cn.lingmar.itolker.frags.main.GroupFragment;
import cn.lingmar.itolker.helper.NavHelper;

public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.tv_title)
    TextView mTextView;

    @BindView(R.id.lay_container)
    FrameLayout mLayContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;


    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        // 判断用户信息是否完全
        if (Account.isComplete()) {
            // 正常流程
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 初始化底部工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper
                .add(R.id.action_home, new NavHelper.Tab<>(AcitveFragment.class, R.string.action_home))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.action_contact))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.action_group));

        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()   // 居中剪切
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });

        PermissionsFragment.haveAll(this, getSupportFragmentManager());
    }

    @Override
    protected void initData() {
        super.initData();

        // 程序开始时模拟点击home那个Menu
        Menu menu = mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_home, 0);
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        // 在群的界面时，点击顶部的搜索就进入群搜索界面
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void OnActionClick() {
        // 判断当前界面是群还是联系人界面
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            // TODO 打开群创建界面
        } else {
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return mNavHelper.performClickMenu(item.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTextView.setText(newTab.extra);

        // 浮动按钮动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.action_home)) {
            // 主界面隐藏
            transY = Ui.dipToPx(getResources(), 74);
        } else {
            if (Objects.equals(newTab.extra, R.string.action_group)) {
                // 群
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                // 联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        // 开始动画
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateInterpolator(1))
                .setDuration(480)
                .start();

    }
}
