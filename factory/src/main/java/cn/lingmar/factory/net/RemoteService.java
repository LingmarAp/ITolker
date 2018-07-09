package cn.lingmar.factory.net;

import java.util.List;

import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.api.account.LoginModel;
import cn.lingmar.factory.model.api.account.RegisterModel;
import cn.lingmar.factory.model.api.group.GroupCreateModel;
import cn.lingmar.factory.model.api.group.GroupJoinModel;
import cn.lingmar.factory.model.api.group.GroupMemberAddModel;
import cn.lingmar.factory.model.api.group.GroupMemberUpdateModel;
import cn.lingmar.factory.model.api.message.MsgCreateModel;
import cn.lingmar.factory.model.api.user.UserUpdateModel;
import cn.lingmar.factory.model.card.ApplyCard;
import cn.lingmar.factory.model.card.GroupCard;
import cn.lingmar.factory.model.card.GroupMemberCard;
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

    // 搜索群
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name", encoded = true) String name);

    // 我的群列表
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date", encoded = true) String date);

    // 拉取一个群的所有成员
    @GET("group/{groupId}/members")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path("groupId") String id);

    // 给群添加成员（管理员权限）
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path("groupId") String groupId,
                                             GroupMemberAddModel model);

    // 给群添加成员（管理员权限）
    @PUT("group/modify/{memberId}")
    Call<RspModel<GroupMemberCard>> groupMemberModify(@Path("memberId") String memberId,
                                                      GroupMemberUpdateModel model);

    // 给群添加成员（管理员权限）
    @POST("group/applyJoin/{groupId}")
    Call<RspModel<ApplyCard>> groupMemberJoin(@Path("groupId") String groupId,
                                              GroupJoinModel model);
}
