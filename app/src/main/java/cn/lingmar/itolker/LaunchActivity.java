package cn.lingmar.itolker;

import cn.lingmar.common.app.Activity;
import cn.lingmar.itolker.activities.MainActivity;
import cn.lingmar.itolker.frags.assist.PermissionsFragment;

public class LaunchActivity extends Activity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            MainActivity.show(this);
            finish();
        }
    }
}
