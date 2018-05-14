package cn.lingmar.factory.presenter.contact;

import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.presenter.BaseContract;

public interface PersonalContract {
    interface Presenter extends BaseContract.Presenter {
        // 获取用户信息
        User getUserPersonal();
    }

    interface View extends BaseContract.View<PersonalContract.Presenter> {
        String getUserId();

        // 数据加载完成
        void onLoadDone(User user);

        // 是否发起聊天
        void allowSayHello(boolean isAllow);

        // 设置关注状态
        void setFollowStatus(boolean isFollow);
    }
}
