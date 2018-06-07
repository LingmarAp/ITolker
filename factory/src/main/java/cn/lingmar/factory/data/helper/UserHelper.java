package cn.lingmar.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import cn.lingmar.factory.Factory;
import cn.lingmar.factory.R;
import cn.lingmar.factory.data.DataSource;
import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.user.UserUpdateModel;
import cn.lingmar.factory.model.card.UserCard;
import cn.lingmar.factory.model.db.User;
import cn.lingmar.factory.model.db.User_Table;
import cn.lingmar.factory.net.Network;
import cn.lingmar.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cn.lingmar.factory.net.Network.remote;

public class UserHelper {
    /**
     * 更新用户信息操作--异步
     *
     * @param model    UserUpdateModel
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
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 持久化到本地数据库并通知联系人刷新
                    Factory.getUserCenter().dispatch(userCard);

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

    /**
     * 搜索方法
     *
     * @param name     用户名
     * @param callback List<UserCard>
     */
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        RemoteService service = remote();
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);

        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        return call;
    }


    public static void follow(String id, DataSource.Callback<UserCard> callback) {
        RemoteService service = remote();
        Call<RspModel<UserCard>> call = service.userFollow(id);

        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 持久化到本地数据库并通知联系人刷新
                    Factory.getUserCenter().dispatch(userCard);

                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    // 刷新联系人的操作，不需要Callback，直接存储到数据库
    // 并通过数据库观察者进行通知界面更新
    // 界面更新的时候进行对比，然后差异更新
    public static void refreshContacts() {
        RemoteService service = remote();

        service.userContacts()
                .enqueue(new Callback<RspModel<List<UserCard>>>() {
                    @Override
                    public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                        RspModel<List<UserCard>> rspModel = response.body();
                        if (rspModel.success()) {
                            List<UserCard> cards = rspModel.getResult();
                            if (cards == null || cards.size() == 0)
                                return ;

                            Factory.getUserCenter().dispatch(cards.toArray(new UserCard[0]));
                        } else {
                            Factory.decodeRspCode(rspModel, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                        // nothing
                    }
                });
    }

    // 从本地查询一个用户信息
    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    public static User findFromNet(String id) {
        RemoteService remoteService = Network.remote();
        try {
            Response<RspModel<UserCard>> response = remoteService.userFind(id).execute();
            UserCard card = response.body().getResult();
            if (card != null) {
                User user = card.build();
                // 进行数据库存储并进行通知
                Factory.getUserCenter().dispatch(card);

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 搜索一个用户，优先本地缓存
     * 本地没有再从网络拉取
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            user = findFromNet(id);
        }

        return user;
    }

    /**
     * 搜索一个用户，优先网络
     * 本地没有再从本地拉取
     */
    public static User searchFirstOnNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            user = findFromLocal(id);
        }

        return user;
    }

}
