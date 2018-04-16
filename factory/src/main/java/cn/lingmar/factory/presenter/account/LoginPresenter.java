package cn.lingmar.factory.presenter.account;

import cn.lingmar.factory.presenter.BasePresenter;

/**
 * 登录的逻辑实现
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter {
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {

    }
}
