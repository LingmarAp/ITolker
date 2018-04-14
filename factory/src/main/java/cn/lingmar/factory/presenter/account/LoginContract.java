package cn.lingmar.factory.presenter.account;

import cn.lingmar.factory.presenter.BaseContract;

public interface LoginContract {

    interface View extends BaseContract.View<Presenter> {
        // 登录成功
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        // 发起一个登录
        void login(String phone, String name, String password);

        // 公用的开始方法
        void start();

        // 公用的销毁触发
        void destroy();
    }

}
