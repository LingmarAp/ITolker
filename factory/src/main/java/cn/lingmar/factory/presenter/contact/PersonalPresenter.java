package cn.lingmar.factory.presenter.contact;

import net.qiujuer.genius.kit.handler.Run;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.data.helper.UserHelper;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.persistence.Account;
import cn.lingmar.factory.presenter.BasePresenter;

public class PersonalPresenter extends BasePresenter<PersonalContract.View>
        implements PersonalContract.Presenter {

    private User user;

    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();

        // 个人界面用户数据优先从网络拉取
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    String id = view.getUserId();
                    User user = UserHelper.searchFirstOnNet(id);
                    onLoaded(view, user);
                }
            }
        });
    }

    private void onLoaded(final PersonalContract.View view, final User user) {
        this.user = user;
        // 是否就是我自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        // 是否已经关注
        final boolean isFollow = isSelf || user.isFollow();
        // 已经关注同时不失自己才能聊天
        final boolean allowSayHello = isFollow && !isSelf;

        // 切换到UI线程
        Run.onUiAsync(() -> {
            view.onLoadDone(user);
            view.allowSayHello(allowSayHello);
            view.setFollowStatus(isFollow);
        });
    }

    @Override
    public User getUserPersonal() {
        if (user != null) {
            return user;
        }

        return null;
    }
}
