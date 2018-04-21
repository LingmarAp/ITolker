package cn.lingmar.factory.data.helper;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.user.UserUpdateModel;
import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.lingmar.factory.net.Network.remote;

public class UserHelper {
    /**
     * 更新用户信息操作--异步
     * @param model UserUpdateModel
     * @param callback DataSource.Callback<UserCard>
     */
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        // 调用Retrofit对网络请求接口做代理
        RemoteService service = remote();
        // 得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);

        // 网络请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 保存用户信息至数据库
                    User user = userCard.build();
                    user.save();

                    // 返回成功
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                // 网络请求失败
                if (callback != null)
                    callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
}
