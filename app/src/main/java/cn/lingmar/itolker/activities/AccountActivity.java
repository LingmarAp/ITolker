package cn.lingmar.itolker.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.lingmar.common.app.Activity;
import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.account.UpdateInfoFragment;

public class AccountActivity extends Activity {

    private Fragment mCurFragment;

    /**
     * 跳转到这个Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
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
