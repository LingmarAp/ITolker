package cn.lingmar.factory.net;

import java.util.List;

import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.api.account.LoginModel;
import cn.lingmar.factory.model.api.account.RegisterModel;
import cn.lingmar.factory.model.api.group.GroupCreateModel;
import cn.lingmar.factory.model.api.message.MsgCreateModel;
import cn.lingmar.factory.model.api.user.UserUpdateModel;
import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.model.card.MessageCard;
import cn.lingmar.factory.model.card.UserCard;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {

    /**
     * 网络请求的一个注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     *
     * @param model LoginModel
     * @return RspModel<AccountRspModel>
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备ID
     *
     * @param pushId 设备ID
     * @return RspModel<AccountRspModel>
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    /**
     * 用户更新的接口
     *
     * @param model UserUpdateModel
     * @return RspModel<UserCard>
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索的接口
     *
     * @param name 用户名
     * @return List<UserCard>
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);


    // 用户关注接口
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    // 获取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    // 获取用户信息，根据用户Id
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    // 获取用户信息，根据用户Id
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);

    // 创建群
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    // 查找群
    @POST("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);
}
