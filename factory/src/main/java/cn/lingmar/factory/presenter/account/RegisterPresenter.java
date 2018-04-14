package cn.lingmar.factory.presenter.account;

import android.text.TextUtils;

import java.util.regex.Pattern;

import cn.lingmar.common.Common;
import cn.lingmar.factory.data.helper.AccountHelper;
import cn.lingmar.factory.model.api.RegisterModel;
import cn.lingmar.factory.presenter.BasePresenter;

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {
    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        // 调用开始方法，在start中默认启动了loading
        start();

        if (checkMobile(phone)) {
            // 提示
        } else if (name.length() < 2) {
            // 姓名需要大于2位
        } else if (password.length() < 6) {
            // 密码需要大于6位
        } else {
            // 进行网络请求
            RegisterModel model = new RegisterModel(phone, password, name);
            AccountHelper.register(model);
        }
    }

    /**
     * 检查手机号是否合法
     *
     * @param phone 手机号码
     * @return true-合法
     */
    @Override
    public boolean checkMobile(String phone) {
        // 手机号不为空，并且满足格式
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }
}
