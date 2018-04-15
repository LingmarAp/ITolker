package cn.lingmar.factory.data.helper;

import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.api.account.RegisterModel;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.net.Network;
import cn.lingmar.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {

    /**
     * 注册的接口，异步调用
     *
     * @param model    传递一个注册的Model进来
     * @param callback 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        // 调用Retrofit对网络请求接口做代理
        RemoteService service = Network.getRetrofit().create(RemoteService.class);
        // 得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
                // 请求成功返回
                // 从返回中得到我们的全局Model，内不是使用的Gson进行解析
                RspModel<AccountRspModel> rspModel = response.body();
                // 判断绑定状态，是否绑定设备
                if (rspModel.success()) {
                    // 拿到实体
                    AccountRspModel accountRspModel = rspModel.getResult();
                    if (accountRspModel.isBind()) {
                        User user = accountRspModel.getUser();
                        // TODO 进行的是数据库写入和缓存绑定
                        // 然后返回
                        callback.onDataLoaded(user);
                    } else {
                        // 进行绑定
                        bindPush(callback);
                    }
                } else {
                    // TODO 对返回的RspModel中的失败的Code进行解析，即系到对应的String资源上
                    // callback.onDataNotAvailable(1);
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                // 网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 对设备Id进行绑定操作
     *
     * @param callback callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {

    }
}
