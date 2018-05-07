package cn.lingmar.factory.presenter.contact;

import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.presenter.BaseContract;

/**
 * 关注的接口定义
 */
public interface FollowContract {
    // 任务调度者
    interface Presenter extends BaseContract.Presenter {
        // 关注一个人
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter> {
        // 成功的情况下回调一个用户的信息
        void onFollowSucceed(UserCard userCard);
    }
}
