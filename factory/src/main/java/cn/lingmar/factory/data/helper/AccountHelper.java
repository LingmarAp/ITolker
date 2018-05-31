package cn.lingmar.factory.data.helper;

import android.text.TextUtils;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.api.account.LoginModel;
import cn.lingmar.factory.model.api.account.RegisterModel;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.net.RemoteService;
import cn.lingmar.factory.persistence.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.lingmar.factory.net.Network.remote;

public class AccountHelper {

    /**
     * 注册的接口，异步调用
     *
     * @param model    传递一个注册的Model进来
     * @param callback 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        // 调用Retrofit对网络请求接口做代理
        RemoteService service = remote();
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登录的调用
     * @param model LoginModel
     * @param callback  成功与失败的接口回送
     */
    public static void login(LoginModel model, final DataSource.Callback<User> callback) {
        // 调用Retrofit对网络请求接口做代理
        RemoteService service = remote();
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备Id进行绑定操作
     *
     * @param callback callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        // 检查是否为NULL
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId)) {
            return;
        }

        // 调用Retrofit对网络请求接口做代理
        RemoteService service = remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));

    }

    /**
     * 请求的回调部分封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {

        final DataSource.Callback<User> callback;

        public AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            // 请求成功返回
            // 从返回中得到我们的全局Model，内不是使用的Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            // 判断绑定状态，是否绑定设备
            if (rspModel.success()) {
                // 拿到实体
                AccountRspModel accountRspModel = rspModel.getResult();
                User user = accountRspModel.getUser();
                // 进行的是数据库写入和缓存绑定
                DbHelper.save(User.class, user);
                // 同步到XML持久化中
                Account.login(accountRspModel);

                if (accountRspModel.isBind()) {
                    // 设置绑定状态为True
                    Account.setBind(true);
                    // 直接返回
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {
                    // 进行绑定
                    bindPush(callback);
                }
            } else {
                // 错误解析
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            // 网络请求失败
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
