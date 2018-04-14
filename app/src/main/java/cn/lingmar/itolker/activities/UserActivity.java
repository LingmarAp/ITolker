package cn.lingmar.itolker.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.lingmar.common.app.Activity;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.user.UpdateInfoFragment;

public class UserActivity extends Activity {

    private Fragment mCurFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mCurFragment =  new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }

}
